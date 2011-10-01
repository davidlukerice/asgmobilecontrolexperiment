package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.scene.Scene;

public interface IControlScheme extends IUpdateHandler{
	
	/** Register listeners for control events such as tilt, touch, etc.
	 * This needs to be in the subclass since each one will listen for
	 * different events
	 * @param mScene
	 */
	public void registerListeners(Scene mScene);
}
