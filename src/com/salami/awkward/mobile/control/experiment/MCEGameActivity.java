package com.salami.awkward.mobile.control.experiment;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.SmoothCamera;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.hardware.SensorManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.salami.awkward.mobile.control.experiment.IControlScheme.ControlType;
import com.salami.awkward.mobile.control.experiment.parse.EntityData;
import com.salami.awkward.mobile.control.experiment.parse.LevelParser;
import com.salami.awkward.mobile.control.experiment.parse.WorldData;

public class MCEGameActivity extends BaseGameActivity{

	private static final int MENU_TRACE = Menu.FIRST;
	
	private Scene mScene;
	private Hero mHero;
	
	private IControlScheme mControls;
	
	private PhysicsWorld mPhysicsWorld;
	private WorldData mWorldData;
	
	private BitmapTextureAtlas mOnScreenButtonTexture;
	private TextureRegion mOnScreenButtonBaseTextureRegion;	
	private TextureRegion mOnScreenButton;

	private float mHeroInitPosX;
	private float mHeroInitPosY;
	
	private BitmapTextureAtlas mOnScreenControlTexture;
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;
	
	private static final int CAMERA_WIDTH = 360;
	private static final int CAMERA_HEIGHT = 240;

	
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
		//parse world data
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("");
		mWorldData =  new LevelParser("sample.xml",this).parse();
		
		//Initialize world
		this.mEngine.registerUpdateHandler(new FPSLogger());
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		
		mPhysicsWorld.setContactListener(new MCEContactListener());
		
		this.mScene = new Scene();
		this.mScene.setBackground(new ColorBackground(0, 0, 0));

		createWorldBoundaries();
		createWorldObjects();

		((BoundCamera) mEngine.getCamera()).setBounds(0, mWorldData.getWidth(), 0, mWorldData.getHeight());
		((BoundCamera) mEngine.getCamera()).setBoundsEnabled(true);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
		//if(this.getIntent().getSerializableExtra("com.salami.awkward.mobile.control.experiment.ControlScheme").equals(ControlType.VIRTUAL))
			//initOnScreenControls();
		
		return this.mScene;
	}
	
	private void createWorldBoundaries() {
		//hard-coded walls
		// TODO TODO TODO TODO TODO TODO
		// remove when xml levels are ready?
		float width = mWorldData.getWidth();
		float height = mWorldData.getHeight();
		final Shape ground = new Rectangle(0, height - 2, width, 2);
		final Shape roof = new Rectangle(0, 0, width, 2);
		final Shape left = new Rectangle(0, 0, 2, height);
		final Shape right = new Rectangle(width - 2, 0, 2, height);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		wallFixtureDef.restitution=0.1f;
		
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		
		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		
	}
	
	private void createWorldObjects() {
		for(EntityData entity : mWorldData.getEntities()){
			switch(entity.getType()){
			case HERO_ENTITY:
				mHeroInitPosX=entity.getPosX();
				mHeroInitPosY=entity.getPosY();
				break;
			case GROUND_ENTITY:
				break;
			}
		}
	}



	@Override
	public Engine onLoadEngine() {
		final SmoothCamera camera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 500,500,1);
		
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		final Engine engine = new Engine(engineOptions);

		
		try {
			if(MultiTouch.isSupported(this)) {
				engine.setTouchController(new MultiTouchController());
			} else {
				Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)", Toast.LENGTH_LONG).show();
			}
		} catch (final MultiTouchException e) {
			Toast.makeText(this, "Sorry your Android Version does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)", Toast.LENGTH_LONG).show();
		}

		return engine;
	}

	@Override
	public void onLoadResources() {
		Hero.onLoadResources(this);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mOnScreenControlTexture = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);
		
		this.mOnScreenButtonTexture = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mOnScreenButtonBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenButtonTexture, this, "onscreen_button_base.png", 0, 0);
		this.mOnScreenButton = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenButtonTexture, this, "onscreen_control_knob.png", 0, 0);
		
		this.mEngine.getTextureManager().loadTextures(this.mOnScreenControlTexture, this.mOnScreenButtonTexture);

	}

	@Override
	public void onLoadComplete() {	
		add_hero(mHeroInitPosX,mHeroInitPosY);
		mEngine.getCamera().setChaseEntity(mHero);

		//Create control scheme
		ControlType type = (ControlType) this.getIntent().getSerializableExtra("com.salami.awkward.mobile.control.experiment.ControlScheme");
		switch(type){
			case SEGMENTED:
				mControls= new SegmentedControlScheme(mHero, mEngine.getCamera(), CAMERA_WIDTH/2, CAMERA_HEIGHT/2);
				break;
			case VIRTUAL: 
				mControls= new VirtualControlScheme(mHero, mScene, this.mEngine, mOnScreenControlBaseTextureRegion, mOnScreenControlKnobTextureRegion);
				break;
			case TILT:   
				mControls= new TiltControlScheme(mHero);
				break;
			default:
				throw new RuntimeException("Control Scheme not implemented");
		}
		
		//Register control scheme handlers
		mControls.registerListeners(mScene,this);
		mEngine.registerUpdateHandler(mControls);
		
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, 5, 300) );
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, 37, 300) );
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, 69, 300) );
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, 101, 300) );
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, 133, 300) );
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, 165, 300) );
		
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, 105, 400) );
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, 137, 400) );
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, 169, 400) );
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, 201, 400) );
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, 233, 400) );
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, 265, 400) );
		

	}
	
	private void add_hero(float xPos, float yPos){
		mHero = Hero.create_hero(this, mPhysicsWorld,xPos,yPos);
		
		this.mScene.registerTouchArea(mHero);
		this.mScene.attachChild(mHero);

	}
}
