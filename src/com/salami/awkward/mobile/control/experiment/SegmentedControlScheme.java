package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.input.touch.TouchEvent;

public class SegmentedControlScheme implements IControlScheme, IOnSceneTouchListener {

	enum Direction{
		NONE,
		LEFT,
		RIGHT
	}
	
	private Direction mDirection;
	private Hero mHero;
	private float mxBoundary;

	private boolean mLeftSideDown=false;
	private boolean mRightSideDown=false;

	/**
	 * 
	 * @param hero 	the hero we act on
	 * @param xBoundary pushing left/right of xBoundary pixels and below yBoundary pixels 
	 * 					moves you left/right 
	 * @param yBoundary pushing above yBoundary pixels makes you jump
	 */
	public SegmentedControlScheme(Hero hero, int xBoundary, int yBoundary){
		mDirection = Direction.NONE;
		mHero =hero;
		mxBoundary=xBoundary;
	}
	
	@Override
	public void onUpdate(float secondsElapsed) {
		if(mDirection == Direction.NONE)
			return;
	
		if(mDirection == Direction.LEFT)
			mHero.move(-1);
		else
			mHero.move(1);
		
	}
	
	@Override
	public void reset() {
		
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		boolean isPressed=false; 
		
		switch(pSceneTouchEvent.getAction()) {
		
			case TouchEvent.ACTION_DOWN:
				isPressed=true;
				break;
			case TouchEvent.ACTION_UP:
				isPressed=false;
				break;
			case TouchEvent.ACTION_MOVE:
				//TODO: I'll handle this later.
				/*FALL THROUGH FOR NOW*/
			default:
				return false;
		}
		
		if(getSide(pSceneTouchEvent.getX()) == Direction.LEFT)
			mLeftSideDown = isPressed;
		else
			mRightSideDown = isPressed;
		
		//Determine movement status from actions
		//TODO move this somewhere else/simplify?
		if(mLeftSideDown&&mRightSideDown)
			mHero.jump();
		else if(mLeftSideDown)
			mDirection = Direction.LEFT;
		else if(mRightSideDown)
			mDirection = Direction.RIGHT;
		else
			mDirection = Direction.NONE;
				
		return true;
	}

	private Direction getSide(float xPos){
		return xPos>mxBoundary ? Direction.RIGHT : Direction.LEFT;
	}
}
