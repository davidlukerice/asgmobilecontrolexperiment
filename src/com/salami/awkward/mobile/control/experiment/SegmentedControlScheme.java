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
	private float myBoundary; //TODO: remove, use multitouch
	

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
		myBoundary=yBoundary;
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
		if(pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove()){
			
			//Actually jump if we should
			if(pSceneTouchEvent.getY() < myBoundary) {
				mHero.jump();
				return true;
			}
			
			//otherwise update direction status for onUpdate
			if(pSceneTouchEvent.getX() < mxBoundary) 
				mDirection=Direction.LEFT;
			else
				mDirection=Direction.RIGHT;
		}
		else if(pSceneTouchEvent.isActionUp())
			mDirection=Direction.NONE;
		
		return true;
	}

}
