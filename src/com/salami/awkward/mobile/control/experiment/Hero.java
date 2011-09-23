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
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
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
	private static final Vector2 JUMP_VECTOR = new Vector2(0,-10);
	private static final float START_X_POSITION = 1;
	private static final float START_Y_POSITION = 1;
	
	private Body mBody;
	private boolean isJumping;
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
		
		//mBody.
		
		this.animate(new long[]{200,200}, 0, 1, true);
		mBody.setUserData(this);
		//TODO: Are we going to use user data fields in animated sprites /Box2D bodies?	
		
		isJumping = false;
		
		ContactFilter filter = new ContactFilter() {
		
			@Override
			public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
				//TODO: Add checking for only colliding with group elements
				if (fixtureA == mBody.getFixtureList().get(0) ||
					fixtureB == mBody.getFixtureList().get(0) ) {
					isJumping = false;
				}
				return true;
			}
		};
		
		world.setContactFilter(filter);
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
		//TODO: Better method
		if (mBody.linVelLoc.y == 0 && !isJumping) {
			isJumping = true;
			Vector2 new_vect = new Vector2(mBody.getLinearVelocity().x, JUMP_VECTOR.y);
			mBody.setLinearVelocity(new_vect);
		}
			
	}
	
	/**
	 * Sets the bodies x and keeps the current y
	 * @param direction
	 */
	public void move( Vector2 direction) {
		//TODO: Unify the vector
		//TODO: Move character with the x direction
		//TODO: Pull a vector from pool?
		Vector2 new_vect = new Vector2(direction.x,mBody.getLinearVelocity().y);
		mBody.setLinearVelocity(new_vect);
	}
}
