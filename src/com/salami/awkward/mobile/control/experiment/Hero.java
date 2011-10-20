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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.salami.awkward.mobile.control.experiment.tracking.StatisticsTracker;

/*
 * Wrapper around body with a display image.
 */
public class Hero extends AnimatedSprite implements Entity{
	//static so we only have one of these if there are multiple heroes
	//TODO: Check if TiledTexture region does this for us (it probably does)
	static private TiledTextureRegion mHeroTextureRegion;
	static private BitmapTextureAtlas mBitmapTextureAtlas;
	private static final Vector2 JUMP_VECTOR = new Vector2(0,-8);
	
	//Default starting positions
	private static final float START_X_POSITION = 1;
	private static final float START_Y_POSITION = 1;
	
	private Body mBody;
	private boolean isJumping;
	
	/**
	 * create_hero using default x and y positions
	 */
	public static Hero create_hero(BaseGameActivity activity, PhysicsWorld world) {
		return create_hero(activity,world,START_X_POSITION,START_Y_POSITION);
	}
	
	
	/**
	 * Creates a Hero 
	 * @param activity
	 * @param world
	 * @param xPosition
	 * @param yPosition
	 * @return
	 */
	public static Hero create_hero(BaseGameActivity activity, PhysicsWorld world,float xPosition, float yPosition) {
		//Make sure everything is loaded
		onLoadResources(activity);
		
		return new Hero(world,xPosition, yPosition);
	}
	
	/**
	 * Creates the hero. Called from create_hero.
	 * @param world
	 */
	private Hero(PhysicsWorld world,float xPosition, float yPosition){
		super(xPosition, yPosition, mHeroTextureRegion);
		
		FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		objectFixtureDef.restitution=0;
		mBody = PhysicsFactory.createBoxBody(world, this, BodyType.DynamicBody, objectFixtureDef);
		mBody.setFixedRotation(true);
		world.registerPhysicsConnector(new PhysicsConnector(this, mBody, true, true));

		this.animate(new long[]{200,200}, 0, 1, true);
		mBody.setUserData(this);
		//TODO: Are we going to use user data fields in animated sprites /Box2D bodies?	
		
		isJumping = false;
		
	}
	
	public static void onLoadResources(BaseGameActivity activity){
		if (mBitmapTextureAtlas == null) {
			mBitmapTextureAtlas = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		}
		
		if (mHeroTextureRegion == null) {
			mHeroTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, activity, "hero_tiled.png", 0, 0, 2, 1); // 64x32
		}
		
		activity.getEngine().getTextureManager().loadTexture(mBitmapTextureAtlas);
	}
	
	public void jump() {
		if (isOnGround()) {
			isJumping = true;
			Vector2 new_vect = new Vector2(mBody.getLinearVelocity().x, JUMP_VECTOR.y);
			mBody.setLinearVelocity(new_vect);
		}	
	}
	
	public boolean isOnGround(){
		return mBody.getLinearVelocity().y<0.01 && !isJumping;
	}
	
	/**
	 * This should be called when the hero dies
	 */
	public void onDeath() {
		StatisticsTracker.getTracker().incrementDeathCount();
	}
	
	/**
	 * Sets the bodies x and keeps the current y
	 * @param direction
	 */
	public void move(float speed) {
		//TODO: Unify the vector
		//TODO: Move character with the x direction
		//TODO: Pull a vector from pool?
		assert(Math.abs(speed)<=1);
		Vector2 new_vect = new Vector2(5*speed,mBody.getLinearVelocity().y);
		mBody.setLinearVelocity(new_vect);
	}
	
	/*
	 * Direct gets and sets to Body properties. I have a feeling we might accumulate a lot of these...
	 * 
	 */
	public Vector2 getLinearVelocity(){
		return mBody.getLinearVelocity();
	}
	
	@Override
	public void onManagedUpdate(float pSecondsElapsed){
		super.onManagedUpdate(pSecondsElapsed);
		
	}
	
	@Override
	public EntityType getEntityType() {
		return EntityType.HERO_ENTITY;
	}

	@Override
	public void onCollide(Fixture other, Contact contact)
	{	
		if(other.getBody().getUserData() == null || other.getBody().getUserData() instanceof Ground)
		{	
			Vector2 normal=contact.getWorldManifold().getNormal();
			if(normal.y != 0 ){
				isJumping=false;
			}
		}
	}

	@Override
	public void onSeparate(Fixture other, Contact contact) {

	}
	
}
