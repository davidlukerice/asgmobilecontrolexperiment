/**
 * Awkward Salami Games
 * MCE
 * 12/01/2011
 */
package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

/**
 * A control scheme based on tilting the device to move
 * and tapping the screen to jump
 * @author Chris/David
 *
 */
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
		
		if(tilt>1)
            tilt=1;
		if(tilt<-1)
            tilt=-1;

       mHero.move(tilt);
	}
		
}
