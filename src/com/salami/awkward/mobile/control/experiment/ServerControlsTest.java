/**
 * Awkward Salami Games
 * MCE
 * 12/01/2011
 */
package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.salami.awkward.mobile.control.experiment.controls.IControlScheme;
import com.salami.awkward.mobile.control.experiment.tracking.StatisticsTracker;
import com.salami.awkward.mobile.control.experiment.tracking.StatisticsTracker.Goal;

/**
 * Control scheme to test the tracking system
 * Starts tracking on touch down and ends on touch up
 * @author David
 */
public class ServerControlsTest implements IControlScheme, IOnSceneTouchListener{

	private Hero mHero;
	
	/**
	 * 
	 * */
	public  ServerControlsTest(Hero hero){
		mHero=hero;
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
				mHero.move(1);
				break;
			case TouchEvent.ACTION_UP:
				StatisticsTracker.getTracker().finishTracking(true);
				mHero.move(0);
				mHero.jump();
				break;
			case TouchEvent.ACTION_MOVE:
				mHero.move(1);
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
