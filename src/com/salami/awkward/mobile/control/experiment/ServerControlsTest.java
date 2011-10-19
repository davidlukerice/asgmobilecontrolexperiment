package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.ui.activity.BaseGameActivity;

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
		boolean isPressed=false; 
		switch(pSceneTouchEvent.getAction()) {
		
			case TouchEvent.ACTION_DOWN:
				
				
				break;
			case TouchEvent.ACTION_UP:
				
				
				break;
			case TouchEvent.ACTION_MOVE:
				
				
			default:
				return false;
		}
			
		return true;
	}

	@Override
	public void registerListeners(Scene scene, BaseGameActivity activity) {
		scene.setOnSceneTouchListener(this);
		
		
		//activity.getFontManager().loadFont(this.mFont);
		/*
		debug_text = new Text(100, 60, this.mFont, "Hello AndEngine!\nYou can even have multilined text!", HorizontalAlign.CENTER);
		scene.attachChild(debug_text);
		*/
	}
}
