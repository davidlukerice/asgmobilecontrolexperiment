package com.salami.awkward.mobile.control.experiment.controls;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.badlogic.gdx.math.Vector2;
import com.salami.awkward.mobile.control.experiment.Hero;

/**
 * Relative control scheme. Touch anywhere to center the analog stick and drag to move.
 * Touch again to jump
 * @author tim
 */
public class RelativeControlScheme implements IControlScheme, IOnSceneTouchListener {

	Hero mHero;
	Camera mCamera;
	
	boolean stickIsActive;
	float stickCenterX;
	float stickPosX;
	int stickPointerID;

	
	/**
	 * Build the relative control scheme
	 * @param hero	the hero to act on
	 */
	public RelativeControlScheme(Hero hero, Camera camera)
	{
		mHero=hero;
		mCamera = camera;
	}
	
	@Override
	public void onUpdate(float pSecondsElapsed) {
		if(stickIsActive)
		{
			float distance = stickPosX-stickCenterX;
			distance /= 30;
			
			if(distance<-1)
				distance=-1;
			if(distance>1)
				distance=1;
			
			mHero.move(distance);
		}
		else
		{
			mHero.move(0);
		}
	}

	@Override
	public void reset() {
		stickIsActive=false;
	}

	@Override
	public void registerListeners(Scene scene, BaseGameActivity activity) {
		scene.setOnSceneTouchListener(this);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent){
		
		float cameraAdjustedPosX = pSceneTouchEvent.getX()-mCamera.getMinX();
		
		int touchType = pSceneTouchEvent.getAction();
		if(!stickIsActive) 
		{
			
			if(touchType == TouchEvent.ACTION_DOWN) 
			{
				stickIsActive=true;
				stickCenterX=cameraAdjustedPosX;
				stickPosX=cameraAdjustedPosX;
				stickPointerID = pSceneTouchEvent.getPointerID();
				return true;
			}
		}
		
		switch(pSceneTouchEvent.getAction()) {
			case TouchEvent.ACTION_DOWN:
				mHero.jump();
				break;
			case TouchEvent.ACTION_UP:
				if(pSceneTouchEvent.getPointerID()==stickPointerID) 
				{
					stickIsActive=false;
				}
				break;
			case TouchEvent.ACTION_MOVE:
				if(pSceneTouchEvent.getPointerID()==stickPointerID)
				{
					
					stickPosX=cameraAdjustedPosX;
				}
				break;
			default:
				return false;
		}
		return true;
	}

}
