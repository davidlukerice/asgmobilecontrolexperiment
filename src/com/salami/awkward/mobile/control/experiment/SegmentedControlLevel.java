package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SegmentedControlLevel extends BaseGameActivity implements IOnSceneTouchListener, IOnAreaTouchListener{

	private static final int MENU_TRACE = Menu.FIRST;
	
	private Scene mScene;
	private Body mHero;
	private PhysicsWorld mPhysicsWorld;
	private static final int CAMERA_WIDTH = 360;
	private static final int CAMERA_HEIGHT = 240;
	
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mBoxFaceTextureRegion;
	
	/*@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.segmentedcontrol);
	}*/
	
	// ===========================================================
	// Menu Stuff
	// ===========================================================
	
	@Override
	public boolean onCreateOptionsMenu(final Menu pMenu) {
		pMenu.add(Menu.NONE, MENU_TRACE, Menu.NONE, "Start Method Tracing");
		return super.onCreateOptionsMenu(pMenu);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu pMenu) {
		pMenu.findItem(MENU_TRACE).setTitle(this.mEngine.isMethodTracing() ? "Stop Method Tracing" : "Start Method Tracing");
		return super.onPrepareOptionsMenu(pMenu);
	}

	@Override
	public boolean onMenuItemSelected(final int pFeatureId, final MenuItem pItem) {
		switch(pItem.getItemId()) {
			case MENU_TRACE:
				if(this.mEngine.isMethodTracing()) {
					this.mEngine.stopMethodTracing();
				} else {
					this.mEngine.startMethodTracing("AndEngine_" + System.currentTimeMillis() + ".trace");
				}
				return true;
			default:
				return super.onMenuItemSelected(pFeatureId, pItem);
		}
	}
	
	// ===========================================================
	// Loading Callback
	// ===========================================================
	
	public Scene onLoadScene() {
		
		//Initialize world
		this.mEngine.registerUpdateHandler(new FPSLogger());
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

		this.mScene = new Scene();
		this.mScene.setBackground(new ColorBackground(0, 0, 0));
		this.mScene.setOnSceneTouchListener(this);

		//Create walls
		final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
		final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		this.mScene.setOnAreaTouchListener(this);


		return this.mScene;
	}
	
	@Override
	public Engine onLoadEngine() {
		//TODO copied from TiltLevel
		Toast.makeText(this, "Touch up to go up. Touch left to go left. Touch right to go right", Toast.LENGTH_LONG).show();
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {
		// TODO copied from TiltLevel

		mBitmapTextureAtlas= new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		mBoxFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				                mBitmapTextureAtlas, this, "boxface_tiled.png", 0, 0, 2, 1); // 64
		this.mEngine.getTextureManager().loadTexture(this.mBitmapTextureAtlas);
	
		

	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
		//Add hero
		

		AnimatedSprite img = new AnimatedSprite(50,50,mBoxFaceTextureRegion);
		
		FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		mHero = PhysicsFactory.createBoxBody(mPhysicsWorld, img, BodyType.DynamicBody, objectFixtureDef);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(img, mHero, true, true));
		
		img.animate(new long[]{200,200}, 0, 1, true);

		mScene.attachChild(img);
	}
	
	// ===========================================================
	// Input Listeners
	// ===========================================================

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			//if(pSceneTouchEvent.isActionDown()) {
				System.out.println("inner loop");
				this.movePlayer(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				return true;	
			//}
		}
		return false;
	}
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		// TODO Auto-generated method stub
		System.out.println("When does onAreaTouched get called?");
		return true;
	}

	// ===========================================================
	// Movement Methods
	// ===========================================================
	
	private void movePlayer(float touchX, float touchY) {
		
		//kludge for now
		if(touchY<CAMERA_HEIGHT/2){
			jump();
			return;
		}
		
		Vector2 velocity = Vector2Pool.obtain(15,0);
		if(touchX<CAMERA_WIDTH/2){
			velocity.mul(-1);
		}
		
		mHero.applyForce(velocity, mHero.getPosition());
		Vector2Pool.recycle(velocity);
	}
	
	private void jump() {
			mHero.applyForce(mPhysicsWorld.getGravity().mul(-3),mHero.getPosition());
		
	}
}
