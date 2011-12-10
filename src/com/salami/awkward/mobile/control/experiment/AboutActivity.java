/**
 * Awkward Salami Games
 * MCE
 * 12/01/2011
 */
package com.salami.awkward.mobile.control.experiment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Activity that gives basic details about AS.G
 * @author Chris
 *
 */
public class AboutActivity extends Activity implements OnClickListener{

	Button website;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);
		
		website = (Button)findViewById(R.id.bWebsite);
		website.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://awkwardsalamigames.com/default.htm")));
	}
}
