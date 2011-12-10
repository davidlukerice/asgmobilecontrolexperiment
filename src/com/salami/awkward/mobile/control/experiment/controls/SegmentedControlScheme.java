package com.salami.awkward.mobile.control.experiment.controls;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.engine.camera.Camera;

import com.salami.awkward.mobile.control.experiment.Hero;


public class SegmentedControlScheme implements IControlScheme, IOnSceneTouchListener{

	enum Direction{
		NONE,
		LEFT,
		RIGHT
	}
	
	private static final long AIR_TURN_WAIT_DURATION = 30;
	private static final long JUMP_DIRECTION_CHANGE_WAIT_DURATION = 50;
	
	private Direction mDirection;
	private Hero mHero;
	private float mxBoundary;

	private boolean mLeftSideDown=false;
	private boolean mRightSideDown=false;
	
	private Camera mCamera;
	
	private long lastRelease;
	private boolean inWaitForReleaseMode;
	
	private long lastPress;
	
	

	/**
	 * 
	 * @param hero 	the hero we act on
	 * @param xBoundary pushing left/right of xBoundary pixels and below yBoundary pixels 
	 * 					moves you left/right 
	 * @param yBoundary pushing above yBoundary pixels makes you jump
	 */
	public SegmentedControlScheme(Hero hero, Camera camera, int xBoundary, int yBoundary){
		mDirection = Direction.NONE;
		mHero =hero;
		mxBoundary=xBoundary;
		mCamera=camera;
		lastRelease=0;
		lastPress=0;
		inWaitForReleaseMode=false;
	}
	
	@Override
	public void onUpdate(float secondsElapsed) {
		
		if(inWaitForReleaseMode){
			if(System.currentTimeMillis()-lastRelease<AIR_TURN_WAIT_DURATION){
				return;
			}
			else{
				inWaitForReleaseMode=false;
			}
		}
		
		//Determine movement status from actions
		if(mLeftSideDown&&mRightSideDown){
			mHero.jump();
			if(System.currentTimeMillis()-lastPress<JUMP_DIRECTION_CHANGE_WAIT_DURATION){
				mDirection=Direction.NONE;
			}
		}
		else if(mLeftSideDown)
			mDirection = Direction.LEFT;
		else if(mRightSideDown)
			mDirection = Direction.RIGHT;
		else
			mDirection = Direction.NONE;
		
		
		if(mDirection == Direction.NONE)
			mHero.move(0);
		else if(mDirection == Direction.LEFT)
			mHero.move(-1);
		else
			mHero.move(1);
		
		
	}
	
	@Override
	public void reset() {
		mLeftSideDown=false;
		mRightSideDown=false;
		mDirection = Direction.NONE;
		mHero.move(0);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		boolean isPressed=false; 

		
		switch(pSceneTouchEvent.getAction()) {
		
			case TouchEvent.ACTION_DOWN:
				if(!mLeftSideDown&&!mRightSideDown){
					lastPress=System.currentTimeMillis();
				}
				isPressed=true;
				break;
			case TouchEvent.ACTION_UP:
				if(mLeftSideDown&&mRightSideDown){			
					lastRelease=System.currentTimeMillis();
					inWaitForReleaseMode=true;
				}
				isPressed=false;
				break;
			case TouchEvent.ACTION_MOVE:
				/*FALL THROUGH FOR NOW*/
			default:
				return false;
		}
		
		if(getSide(pSceneTouchEvent.getX()) == Direction.LEFT)
			mLeftSideDown = isPressed;
		else
			mRightSideDown = isPressed;
		
		return true;
	}

	private Direction getSide(float xPos){
		
		return (xPos-mCamera.getMinX())>mxBoundary ? Direction.RIGHT : Direction.LEFT;
	}

	@Override
	public void registerListeners(Scene scene, BaseGameActivity activity) {
		scene.setOnSceneTouchListener(this);
		
	}
}
