/**
 * Awkward Salami Games
 * MCE
 * 12/01/2011
 */
package com.salami.awkward.mobile.control.experiment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * A activity that comes up when the app is launched and
 * shows a splashscreen
 * @author Chris
 */
public class Splash extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.splash);
		
		Thread timer = new Thread(){
			public void run(){
				try{
					sleep(2500);
				} catch(InterruptedException e){
					e.printStackTrace();
				} finally{
					Intent openStartingPoint = new Intent("com.salami.awkward.mobile.control.experiment.ASGMOBILECONTROLEXPERIMENTACTIVITY");
					startActivity(openStartingPoint);
				}
			}
		};
		
		timer.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}
