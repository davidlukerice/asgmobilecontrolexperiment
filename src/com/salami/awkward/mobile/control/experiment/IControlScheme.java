/**
 * Awkward Salami Games
 * MCE
 * 12/01/2011
 */
package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.ui.activity.BaseGameActivity;

/**
 * Basic interface detailing the different control schemes that are 
 * available for the game
 *
 */
public interface IControlScheme extends IUpdateHandler{
	enum ControlType{
		TILT, 
		SEGMENTED,
		VIRTUAL,
		SERVER
	}
	
	/** Register listeners for control events such as tilt, touch, etc.
	 * This needs to be in the subclass since each one will listen for
	 * different events
	 * @param scene 	Needed to register some listeners
	 * @param activity	Needed to register others
	 * 
	 */
	public void registerListeners(Scene scene, BaseGameActivity activity);
}
