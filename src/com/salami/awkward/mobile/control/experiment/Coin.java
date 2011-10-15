package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/*
 * Wrapper around body with a display image.
 */
public class Coin extends AnimatedSprite implements Entity{
	
	//TODO: Check if TiledTexture region does this for us (it probably does)
	static private TiledTextureRegion mCoinTextureRegion;
	static private BitmapTextureAtlas mBitmapTextureAtlas;
	
	//Default starting positions
	private static final float START_X_POSITION = 1;
	private static final float START_Y_POSITION = 1;
	
	private Body mBody;
	
	
	/**
	 * create_Coin using default x and y positions
	 */
	public static Coin create_coin(BaseGameActivity activity, PhysicsWorld world) {
		return create_coin(activity,world,START_X_POSITION,START_Y_POSITION);
	}
	
	
	/**
	 * Creates a Coin 
	 * @param activity
	 * @param world
	 * @param xPosition
	 * @param yPosition
	 * @return
	 */
	public static Coin create_coin(BaseGameActivity activity, PhysicsWorld world,float xPosition, float yPosition) {
		//Make sure everything is loaded
		onLoadResources(activity);
		return new Coin(world,xPosition, yPosition);
	}
	
	/**
	 * Creates the Coin. Called from create_coin.
	 * @param world
	 */
	private Coin(PhysicsWorld world,float xPosition, float yPosition){
		super(xPosition, yPosition, mCoinTextureRegion);
		
//		FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
//		objectFixtureDef.restitution=0;
//		mBody = PhysicsFactory.createBoxBody(world, this, BodyType.StaticBody, objectFixtureDef);
//		mBody.setFixedRotation(true);
//		world.registerPhysicsConnector(new PhysicsConnector(this, mBody, true, true));
//		
//
//		
//		this.animate(new long[]{200,200}, 0, 1, true);
//		mBody.setUserData(this);		
	}
	
	public static void onLoadResources(BaseGameActivity activity){
		if (mBitmapTextureAtlas == null) {
			mBitmapTextureAtlas = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		}
		
		if (mCoinTextureRegion == null) {
			mCoinTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, activity, "boxface_tiled.png", 0, 0, 2, 1); // 64x32
		}
		
		activity.getEngine().getTextureManager().loadTexture(mBitmapTextureAtlas);
	}
	
	@Override
	public void onManagedUpdate(float pSecondsElapsed){
		super.onManagedUpdate(pSecondsElapsed);
		
	}
	
	@Override
	public EntityType getEntityType() {
		return EntityType.COIN_ENTITY;
	}

	@Override
	public void onCollide(Fixture other, Contact contact) {
		// TODO Auto-generated method stub
		//TODO collides with hero
		System.out.println("GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOAAAAAAAAAAAAAAAAAAAAAAAAAAAAL");
	}	

	@Override
	public void onSeparate(Fixture other, Contact contact) {
		// TODO Auto-generated method stub
		
	}

}
