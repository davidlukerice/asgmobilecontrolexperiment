package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.salami.awkward.mobile.control.experiment.tracking.StatisticsTracker;
import com.salami.awkward.mobile.control.experiment.tracking.StatisticsTracker.Goal;

/**
 * Control scheme to test the tracking system
 * Starts tracking on touch down and ends on touch up
 * @author liger13
 *
 */
public class ServerControlsTest implements IControlScheme, IOnSceneTouchListener{

	private Text debug_text;
	
	/**
	 * 
	 * */
	public  ServerControlsTest(){

	}
	
	@Override
	public void onUpdate(float secondsElapsed) {
		
	}
	
	@Override
	public void reset() {
		
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		switch(pSceneTouchEvent.getAction()) {
		
			case TouchEvent.ACTION_DOWN:
				StatisticsTracker.getTracker().beginTracking(Goal.COLLECTION);
				break;
			case TouchEvent.ACTION_UP:
				StatisticsTracker.getTracker().finishTracking();
				break;
			default:
				return false;
		}
			
		return true;
	}

	@Override
	public void registerListeners(Scene scene, BaseGameActivity activity) {
		scene.setOnSceneTouchListener(this);
	}
}
