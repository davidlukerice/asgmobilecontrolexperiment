package com.salami.awkward.mobile.control.experiment;

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
	
	Intent intent;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tilt = (Button)findViewById(R.id.bTilt);
        virtual = (Button)findViewById(R.id.bVirtual);
        segmented = (Button)findViewById(R.id.bSegment);
        
        tilt.setOnClickListener(this);
        virtual.setOnClickListener(this);
        segmented.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bTilt:
			intent = new Intent(this, TiltLevel.class);
			startActivity(intent);
			
			break;
		case R.id.bVirtual:
			intent = new Intent(this, VirtualJoystickLevel.class);
			startActivity(intent);
			
			break;
		case R.id.bSegment:
			intent = new Intent(this, SegmentedControlLevel.class);
			startActivity(intent);
			
			break;
		}
	}
}