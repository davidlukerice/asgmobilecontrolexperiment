/**
 * Awkward Salami Games
 * MCE
 * 12/01/2011
 */
package com.salami.awkward.mobile.control.experiment;

import com.salami.awkward.mobile.control.experiment.IControlScheme.ControlType;
import com.salami.awkward.mobile.control.experiment.tracking.StatisticsTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * The main control selection screen that allows the player the start a play through with the
 * selected control scheme
 * @author Chris
 *
 */
public class AsgmobilecontrolexperimentActivity extends Activity implements OnClickListener{
	
	Button tilt;
	Button virtual;
	Button segmented;
	Button server;
	Button about;
	
	Intent intent;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tilt = (Button)findViewById(R.id.bTilt);
        virtual = (Button)findViewById(R.id.bVirtual);
        segmented = (Button)findViewById(R.id.bSegment);
        server = (Button)findViewById(R.id.bServer);
        about = (Button)findViewById(R.id.bAbout);
        
        tilt.setOnClickListener(this);
        virtual.setOnClickListener(this);
        segmented.setOnClickListener(this);
        server.setOnClickListener(this);
        about.setOnClickListener(this);
        
        server.setVisibility(View.GONE);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bTilt:
			intent = new Intent(this, MCEGameActivity.class);
			intent.putExtra("com.salami.awkward.mobile.control.experiment.ControlScheme", ControlType.TILT);
			break;
		case R.id.bVirtual:
			intent = new Intent(this, MCEGameActivity.class);
			intent.putExtra("com.salami.awkward.mobile.control.experiment.ControlScheme", ControlType.VIRTUAL);
			break;
		case R.id.bSegment:
			intent = new Intent(this, MCEGameActivity.class);
			intent.putExtra("com.salami.awkward.mobile.control.experiment.ControlScheme", ControlType.SEGMENTED);
			break;
		case R.id.bServer:
			intent = new Intent(this, MCEGameActivity.class);
			intent.putExtra("com.salami.awkward.mobile.control.experiment.ControlScheme", ControlType.SERVER);
			break;
		case R.id.bAbout:
			intent = new Intent(this, AboutActivity.class);
			break;
		}
		
		//Clear the playID
		StatisticsTracker.getTracker().init();
		
		startActivity(intent);
	}
}	