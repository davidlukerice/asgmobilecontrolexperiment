package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/*
 * Wrapper around body with a display image. Currently unusable garbage mostly copied from tiltlevel
 */
public class Hero {
	private Body mBody;
	
	//static so we only have one of these if there are multiple heroes
	//TODO: Check if TiledTexture region does this for us (it probably does)
	static TiledTextureRegion mBoxFaceTextureRegion;
	
	public Hero(PhysicsWorld world){

		AnimatedSprite img = new AnimatedSprite(0,0,mBoxFaceTextureRegion);
		
		FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		mBody = PhysicsFactory.createBoxBody(world, img, BodyType.DynamicBody, objectFixtureDef);
		world.registerPhysicsConnector(new PhysicsConnector(img, mBody, true, true));
		
		img.animate(new long[]{200,200}, 0, 1, true);
		mBody.setUserData(img);
		//TODO: Are we going to use user data fields in animated sprites /Box2D bodies?	
	}
	
	public static void onLoadResources(Context context){
		if(mBoxFaceTextureRegion==null){
			new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
			mBoxFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
					                null, context, "boxface_tiled.png", 0, 0, 2, 1); // 64x32

		}
		
	}
}
