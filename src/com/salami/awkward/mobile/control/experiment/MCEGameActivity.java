package com.salami.awkward.mobile.control.experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.SmoothCamera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.HorizontalAlign;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.salami.awkward.mobile.control.experiment.Entity.EntityType;
import com.salami.awkward.mobile.control.experiment.IControlScheme.ControlType;
import com.salami.awkward.mobile.control.experiment.parse.EntityData;
import com.salami.awkward.mobile.control.experiment.parse.LevelParser;
import com.salami.awkward.mobile.control.experiment.parse.WorldData;
import com.salami.awkward.mobile.control.experiment.tracking.StatisticsTracker;
import com.salami.awkward.mobile.control.experiment.tracking.StatisticsTracker.Goal;
import com.salami.awkward.mobile.control.experiment.tracking.StatisticsTracker.TestType;

public class MCEGameActivity extends BaseGameActivity{

	private static final int MENU_TRACE = Menu.FIRST;
	
	private static final String SEND_TO_SERVER_TITLE =
			"All Coins Collected!";
	
	private static final String SEND_TO_SERVER_MESSAGE =
			"Would you like to have anonymous information on your gameplay statistics sent to our server?  "  
			+"Statistical measurements based on your data and the data of other users may be put on the web at \n"
			+"http://awkwardsalamigames.com/mce.html";
				
	
	private Scene mScene;
	private Hero mHero;
	
	private IControlScheme mControls;
	
	private PhysicsWorld mPhysicsWorld;
	private WorldData mWorldData;
	
	private BitmapTextureAtlas mOnScreenButtonTexture;
	private TextureRegion mOnScreenButtonBaseTextureRegion;	
	private TextureRegion mOnScreenButton;
	private TiledTextureRegion mButton;
	
	private BitmapTextureAtlas mOnScreenControlTexture;
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;
	
	private float mWorldWidth;
	private float mWorldHeight;
	private int mTotalGoodCoins;
	private List<Goal> goals;
	private int currentGoalIndex;
	private Handler handler;
	private boolean scheduleRepopulate;
	
	private ArrayList<Coin> coins;
	private float mHeroX;
	private float mHeroY;
	
	private BitmapTextureAtlas mFontTexture;
	private Font mFont;
	private ChangeableText goodBatts;
	private ChangeableText badBatts;
	private ChangeableText totalBatts;
	private ChangeableText timeElapsed;
	
	private static final int CAMERA_HEIGHT = 320;
	private static final boolean DEBUG_GOAL_MODE = true;

	//private int mCameraWidth; //calc'd from display metrics
	
	/*@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.segmentedcontrol);
	}*/
		
	// ===========================================================
	// Loading Callback
	// ===========================================================
	
	public Scene onLoadScene() {		
		//parse world data
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("");
		mWorldData =  new LevelParser("level.xml",this).parse();
		
		//Initialize world
		this.mEngine.registerUpdateHandler(new FPSLogger());
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		
		mPhysicsWorld.setContactListener(new MCEContactListener());
		
		this.mScene = new Scene();
		this.mScene.setBackground(new ColorBackground(0.43137f, 0.67843f, 1.000f));

		createWorldBoundaries(mWorldData.getWidth(), mWorldData.getHeight());

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
		//if(this.getIntent().getSerializableExtra("com.salami.awkward.mobile.control.experiment.ControlScheme").equals(ControlType.VIRTUAL))
			//initOnScreenControls();
			
		return this.mScene;
	}
	
	private void createWorldBoundaries(float width, float height) {
		mWorldWidth = width;
		mWorldHeight = height;
		((BoundCamera) mEngine.getCamera()).setBounds(0, mWorldWidth, 0, mWorldHeight);
		((BoundCamera) mEngine.getCamera()).setBoundsEnabled(true);
	}
	
	private void createWorldObjects(Goal currentGoal) {
		mTotalGoodCoins=0;
		for(EntityData entity : mWorldData.getEntities()){
			switch(entity.getType()){
			case HERO_ENTITY:
				mHeroX = entity.getPosX();
				mHeroY = entity.getPosY();
				add_hero(entity.getPosX(),entity.getPosY());
				break;
			case GROUND_ENTITY:
				//TODO: Load specific type of ground (normal, left, right, top)
				add_ground(entity.getPosX(),entity.getPosY(),entity.getWidth(),entity.getHeight());
				break;
			case COIN_ENTITY:
				add_coin(entity, currentGoal);
				break;
			}
			
		}
	}
	
	@Override
	public void onBackPressed() {
		//if( StatisticsTracker.TEST_TYPE != TestType.INTERVIEW ){
			super.onBackPressed();
		//}
	}

	private void add_coin(EntityData entity, Goal currentGoal) {
		if(entity.isGood()){
			add_coin(entity.getPosX(), entity.getPosY(), entity.getWidth(), entity.getHeight(), entity.getGUID(),entity.isGood());
			mTotalGoodCoins++;
		}
		else if(currentGoal == Goal.ACCURACY)
			add_coin(entity.getPosX(), entity.getPosY(), entity.getWidth(), entity.getHeight(), entity.getGUID(),entity.isGood());
		else if(currentGoal==Goal.DEXTERITY){
			add_coin(entity.getPosX(), entity.getPosY(), entity.getWidth(), entity.getHeight(), entity.getGUID(),true);
			mTotalGoodCoins++;
		}
	}

	@Override
	public Engine onLoadEngine() {
		final Camera camera = new BoundCamera(0, 0, getCameraWidth(), getCameraHeight());
		
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(getCameraWidth(),getCameraHeight()), camera);
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
		
		this.mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
        FontFactory.setAssetBasePath("font/");
        this.mFont = FontFactory.createFromAsset(this.mFontTexture, this, "LCD.ttf", 18, true, Color.BLACK);
		
		this.mOnScreenControlTexture = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);
		
		this.mOnScreenButtonTexture = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mButton = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mOnScreenButtonTexture, this, "button_tile.png", 0, 0, 2, 1);
			
		this.mEngine.getTextureManager().loadTextures(this.mOnScreenControlTexture, this.mOnScreenButtonTexture, this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);
		
	}

	@Override
	public void onLoadComplete() {	

		createWorldObjects(Goal.COLLECTION);
		goals = new ArrayList<Goal>();
		goals.add(Goal.COLLECTION);
		goals.add(Goal.ACCURACY);
		goals.add(Goal.DEXTERITY);
		currentGoalIndex=0;
		//handler = new Handler();
		
		mEngine.getCamera().setChaseEntity(mHero);
		//((SmoothCamera)mEngine.getCamera()).setCenterDirect(mHero.getX(), mHero.getY());

		//Create control scheme
		ControlType type = (ControlType) this.getIntent().getSerializableExtra("com.salami.awkward.mobile.control.experiment.ControlScheme");
		
		switch(type){
			case SEGMENTED:
				mControls= new SegmentedControlScheme(mHero, mEngine.getCamera(), getCameraWidth()/2,
						getCameraHeight()/2);
				break;
			case VIRTUAL: 
				mControls= new VirtualControlScheme(mHero, mScene, this.mEngine,
						mOnScreenControlBaseTextureRegion, mOnScreenControlKnobTextureRegion,
						mButton, getCameraWidth(), getCameraHeight());
				break;
			case TILT:   
				mControls= new TiltControlScheme(mHero);
				break;
			case SERVER:
				mControls= new ServerControlsTest(mHero);
				break;
			default:
				throw new RuntimeException("Control Scheme not implemented");
		}
		
		goodBatts = new ChangeableText(0, 0, this.mFont, "Good batteries collected: 0      ");
		badBatts = new ChangeableText(0, 0, this.mFont, "Bad batteries collected: 0   ");
		totalBatts = new ChangeableText(0, 0, this.mFont, "Batteries collected: 0       ");
		timeElapsed = new ChangeableText(0, 0, this.mFont, "Time elapsed:     ");

		
		//Register control scheme handlers
		mControls.registerListeners(mScene,this);
		mEngine.registerUpdateHandler(mControls);
		
		mScene.registerUpdateHandler(new IUpdateHandler(){

			@Override
			public void onUpdate(float pSecondsElapsed) {
				
				totalBatts.setPosition(mEngine.getCamera().getMinX()+10, mEngine.getCamera().getMaxY() - 310);
				badBatts.setPosition(mEngine.getCamera().getMinX()+10, mEngine.getCamera().getMaxY() - 290);
				goodBatts.setPosition(mEngine.getCamera().getMinX()+10, mEngine.getCamera().getMaxY() - 310);
				timeElapsed.setPosition(mEngine.getCamera().getMinX()+10, mEngine.getCamera().getMaxY() - 290);
				
				if (StatisticsTracker.getTracker().getCurrentGoal() == Goal.COLLECTION){
					totalBatts.setText("Batteries collected: " + StatisticsTracker.getTracker().getNumGoodCoins() + " / 21");
					goodBatts.setText("");
					badBatts.setText("");
					timeElapsed.setText("");
				}
				
				if (StatisticsTracker.getTracker().getCurrentGoal() == Goal.ACCURACY){
					totalBatts.setText("");
					goodBatts.setText("Good batteries collected: " + StatisticsTracker.getTracker().getNumGoodCoins() + " / 21");
					badBatts.setText("Bad batteries collected: " + StatisticsTracker.getTracker().getNumBadCoins());
				}
				
				if (StatisticsTracker.getTracker().getCurrentGoal() == Goal.DEXTERITY){
					goodBatts.setText("");
					badBatts.setText("");
					totalBatts.setText("Batteries collected: " + StatisticsTracker.getTracker().getNumGoodCoins() + " / 34");
					timeElapsed.setText("Time elapsed: " + ((System.currentTimeMillis() - StatisticsTracker.getTracker().getGoalStartTime())/1000f));
				}

				
				for(int i=0; i<coins.size();){
					Coin c = coins.get(i);
					if(c.isCollected()){											
						eraseCoin(c);
						coins.remove(i);
					}
					else{
						i++;
					}
				}
				if(scheduleRepopulate){
					resetWorldObjects(StatisticsTracker.getTracker().getCurrentGoal());
					scheduleRepopulate=false;
				}
			}

			@Override
			public void reset() {}
			
		});
		
		StatisticsTracker.getTracker().setControlMode(type);
		StatisticsTracker.getTracker().init();
		displayGoalOkBox(Goal.COLLECTION);	
		
		mScene.attachChild(totalBatts);
		mScene.attachChild(timeElapsed);
		mScene.attachChild(goodBatts);
		mScene.attachChild(badBatts);

	}
	
	public void checkFinishConditions(){
		System.out.println(StatisticsTracker.getTracker().getNumGoodCoins());
		if((DEBUG_GOAL_MODE && StatisticsTracker.getTracker().getNumGoodCoins() ==1) 
			|| StatisticsTracker.getTracker().getNumGoodCoins()==mTotalGoodCoins)
		{
			transitionToNextLevel();
		}
	}
	
	private void transitionToNextLevel(){
		
		boolean done = false;
		currentGoalIndex++;
		if(currentGoalIndex==goals.size()){
			currentGoalIndex=0;  //return to main screen?
			done = true;
		}
		Goal nextGoal = goals.get(currentGoalIndex);
		
		finishTracking(done,nextGoal);
		
		mHero.resetPosition();
	    mControls.reset();
		
		
	}
	
	/**
	 * Displays a dialog box asking the user if they want to send the data to the server
	 * @param exitOnCompletion if true the game exits when this is done, otherwise it 
	 * 			continues to the next goal.
	 */
	private void finishTracking(final boolean exitOnCompletion, final Goal nextGoal) {
		final MCEGameActivity self = this;
		this.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(self)
				.setTitle(SEND_TO_SERVER_TITLE)
				.setMessage(SEND_TO_SERVER_MESSAGE)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
						StatisticsTracker.getTracker().finishTracking(true);
						if(exitOnCompletion)
							finishGame();
						else{
							displayGoalOkBox(nextGoal);
						}
					}
				})
				.setCancelable(false);
				
				if(StatisticsTracker.TEST_TYPE != TestType.INTERVIEW)
				{
					alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { 
							StatisticsTracker.getTracker().finishTracking(false);
							if(exitOnCompletion)
								finishGame();
							else
								displayGoalOkBox(nextGoal);
						}
					});
				}
				alert.show();
				
			}
		});
	}
	
	public void finishGame() {
		TestType testType =StatisticsTracker.TEST_TYPE;
		final String msg;
		
		if( StatisticsTracker.getTracker().currentPlayID != -1) {
			msg="Your play id: "+StatisticsTracker.getTracker().currentPlayID+". Use this on awkwardsalamigames.com/mce.html to see your results.";
		}
		else {
			msg="Congratulations! Feel free to play again and send some data to help us with our experiment!";
		}
		
		final MCEGameActivity self=this; 
		this.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				new AlertDialog.Builder(self)
				.setTitle("You won!")
				.setMessage(msg)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
						finish();
					}
				})
				.setCancelable(false)
				.show();	
			}
		});
		

	}
		
	
	private void resetWorldObjects(Goal currentGoal) {
		eraseCoins();
		mHero.resetPosition();
		
		System.out.println(0);
		mTotalGoodCoins=0;
		for(EntityData entity : mWorldData.getEntities()){
			if(entity.getType()==EntityType.COIN_ENTITY){
				add_coin(entity, currentGoal);
			}
		}

	}
	
	private void eraseCoin(Coin c){
		mScene.detachChild(c);
		mPhysicsWorld.destroyBody(c.getBody());
	}

	private void eraseCoins() {
		for(Coin c: coins){
			eraseCoin(c);
		}
		coins.clear();
		//mPhysicsWorld.dispose();
		//mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		
		//mPhysicsWorld.setContactListener(new MCEContactListener());
	}

	private void add_hero(float xPos, float yPos){
		mHero = Hero.create_hero(this, mPhysicsWorld,xPos,yPos);
		
		this.mScene.registerTouchArea(mHero);
		this.mScene.attachChild(mHero);

	}
	
	private void add_ground(float posX, float posY, int width, int height)
	{
		this.mScene.attachChild( Ground.create_ground(this, mPhysicsWorld, posX,posY,width,height) );	
	}
	
	private void add_coin(float posX, float posY, int width, int height, int guid, boolean isGood){
		if(coins == null){
			coins = new ArrayList<Coin>();
		}
		Coin item = Coin.create_coin(this, mPhysicsWorld, posX, posY, guid,isGood);
		this.mScene.attachChild(item);
		coins.add(item);
	}
	
	//wrappers around width/height because I want to be able to change which 
	//one is a constant without breaking naming conventions
	private int getCameraWidth(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float res = ( (float) metrics.widthPixels/ metrics.heightPixels)*getCameraHeight();
		return (int) res;
	}
	
	private int getCameraHeight(){
		return CAMERA_HEIGHT;
	}
	
	public void displayGoalOkBox(final Goal goal){
		final MCEGameActivity self = this;
		
		String buildMessage = goal +": ";
		switch(goal){
		case COLLECTION:
			buildMessage += "Get all the batteries!\n";
			ControlType control = StatisticsTracker.getTracker().getControlMode();
			if(control== ControlType.SEGMENTED) {
				buildMessage += "\n\nSegmented controls: Touch left to go left and right to go right. Touch on both sides of the screen to jump!\n";
			}
			else if(control == ControlType.TILT) {
				buildMessage += "\n\nTilt controls: Tilt to move, tap to jump!\n";
			}
			
			break;
		case ACCURACY:
			buildMessage += "Get all the green batteries, but avoid red batteries!\n";
			break;
		case DEXTERITY:
			buildMessage += "Get all the batteries as fast as you can!\n";
			break;
		}

		
		
		final String message = buildMessage;
		this.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				new AlertDialog.Builder(self)
				.setTitle(goal.toString())
				.setMessage(message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
						
							StatisticsTracker.getTracker().beginTracking(goal);
							scheduleRepopulate=true;
					}
				})
				.setCancelable(false)
				.show();
				
			}
		});
	}
	
}
