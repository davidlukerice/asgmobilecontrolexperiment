package com.salami.awkward.mobile.control.experiment;

import com.salami.awkward.mobile.control.experiment.IControlScheme.ControlType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AsgmobilecontrolexperimentActivity extends Activity implements OnClickListener{
	
	Button tilt;
	Button virtual;
	Button segmented;
	Button server;
	
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
        
        tilt.setOnClickListener(this);
        virtual.setOnClickListener(this);
        segmented.setOnClickListener(this);
       // server.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		intent = new Intent(this, MCEGameActivity.class);
		switch(v.getId()){
		case R.id.bTilt:
			intent.putExtra("com.salami.awkward.mobile.control.experiment.ControlScheme", ControlType.TILT);
			break;
		case R.id.bVirtual:
			intent.putExtra("com.salami.awkward.mobile.control.experiment.ControlScheme", ControlType.VIRTUAL);
			break;
		case R.id.bSegment:
			intent.putExtra("com.salami.awkward.mobile.control.experiment.ControlScheme", ControlType.SEGMENTED);
			break;
		case R.id.bServer:
			intent.putExtra("com.salami.awkward.mobile.control.experiment.ControlScheme", ControlType.SERVER);
			break;
		}
		
		startActivity(intent);
	}
}	