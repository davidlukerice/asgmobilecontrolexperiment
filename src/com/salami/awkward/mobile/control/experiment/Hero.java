package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.hardware.SensorManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import com.badlogic.gdx.math.Vector2;

/*
 * Wrapper around body with a display image. Currently unusable garbage mostly copied from tiltlevel
 */
public class Hero extends AnimatedSprite {
	//static so we only have one of these if there are multiple heroes
	//TODO: Check if TiledTexture region does this for us (it probably does)
	static private TiledTextureRegion mHeroTextureRegion;
	static private BitmapTextureAtlas mBitmapTextureAtlas;
	private static final Vector2 JUMP_VECTOR = new Vector2(10,0);
	private static final float START_X_POSITION = 1;
	private static final float START_Y_POSITION = 1;
	
	private Body mBody;
	
	/**
	 * Creates a Hero 
	 * @param activity
	 * @param world
	 * @return
	 */
	public static Hero create_hero(BaseGameActivity activity, PhysicsWorld world) {
		if (mBitmapTextureAtlas == null) {
			mBitmapTextureAtlas = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		}
		
		if (mHeroTextureRegion == null) {
			mHeroTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, activity, "boxface_tiled.png", 0, 0, 2, 1); // 64x32
		}
		
		return new Hero(world);
	}
	
	/**
	 * Creates the hero. Called from create_hero.
	 * @param world
	 */
	private Hero(PhysicsWorld world){
		super(START_X_POSITION, START_Y_POSITION, mHeroTextureRegion);
		
		FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		mBody = PhysicsFactory.createBoxBody(world, this, BodyType.DynamicBody, objectFixtureDef);
		world.registerPhysicsConnector(new PhysicsConnector(this, mBody, true, true));
		
		this.animate(new long[]{200,200}, 0, 1, true);
		mBody.setUserData(this);
		//TODO: Are we going to use user data fields in animated sprites /Box2D bodies?	
	}
	
	public static void onLoadResources(Context context){
		if(mHeroTextureRegion==null){
			new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
			mHeroTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
					                null, context, "boxface_tiled.png", 0, 0, 2, 1); // 64x32
		}
	}
	
	public void jump() {
		mBody.setLinearVelocity(JUMP_VECTOR);
	}
	
	public void move( Vector2 direction) {
		//TODO: Unify the vector
		//TODO: Move character with the x direction
		mBody.setLinearVelocity(direction);
	}
}
