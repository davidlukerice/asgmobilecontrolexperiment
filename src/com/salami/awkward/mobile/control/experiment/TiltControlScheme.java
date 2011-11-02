package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class TiltControlScheme implements IControlScheme, IAccelerometerListener, IOnSceneTouchListener{

	private Hero mHero;
	
	public TiltControlScheme(Hero hero) {
		mHero=hero;
	}
	@Override
	public void onUpdate(float pSecondsElapsed) {
		//nothing
	}

	@Override
	public void reset() {
		//nothing?
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		mHero.jump();
		return false;  //?
	}

	@Override
	public void registerListeners(Scene scene, BaseGameActivity activity) {
		scene.setOnSceneTouchListener(this);
		activity.getEngine().enableAccelerometerSensor(activity, this);
	}

	@Override
	public void onAccelerometerChanged(AccelerometerData pAccelerometerData) {
		float tilt = pAccelerometerData.getX() / 5.0f;
		
		mHero.move(tilt);
		
		
//		if(tilt < -5){
//			mHero.move(-1);
//		}else if(tilt > 5){
//			mHero.move(1);
//		}
	}
		
}
