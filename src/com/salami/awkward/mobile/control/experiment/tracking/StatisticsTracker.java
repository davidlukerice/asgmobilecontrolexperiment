package com.salami.awkward.mobile.control.experiment.tracking;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.util.EntityUtils;
import com.badlogic.gdx.math.Vector2;
import com.loopj.android.http.*;
import org.json.*;

import com.salami.awkward.mobile.control.experiment.IControlScheme.ControlType;

/**
 * Statistics tracker that gathers data from goals that will be communicated back to the server
 * somehow. This class is a singleton (subject to change).  The idea is that it's reused for
 * each level.  It doesn't track data from old levels right now since I don't think we need
 * to as long as we send it somewhere else
 * @author tim
 *
 */
public class StatisticsTracker {
	private static StatisticsTracker stats;
	
	public static StatisticsTracker getTracker(){
		if(stats == null)
			stats = new StatisticsTracker();
		return stats;
	}
		
	public enum Goal{
		COLLECTION,
		ACCURACY,
		DEXTERITY
	}
	
	private static final boolean INTERVIEW_MODE = false;
	
	private Goal currentGoal;

	private long goalStartTime;
	private List<CoinData> coinsGathered;//List of stats for coins acquired
	private int numGoodCoins;
	private int numBadCoins;
	private int numDeaths;
	private ControlType mControlType;
	
	private StatisticsTracker(){
		coinsGathered = new ArrayList<CoinData>();
		numDeaths=0;
		numGoodCoins=0;
		numBadCoins=0;
	}
	
	public void addCoin(Vector2 position, boolean isGood) {
		coinsGathered.add(new CoinData(position,isGood, System.currentTimeMillis()-goalStartTime));
		if(isGood)
			numGoodCoins++;
		else
			numBadCoins++;
	
	}
	
	public Goal getCurrentGoal(){
		return currentGoal;
	}
	
	public int getNumDeaths() {
		return numDeaths;
	}

	public void incrementDeathCount() {
		numDeaths++;
	}
	
	public int getNumGoodCoins() {
		return numGoodCoins;
	}

	public int getNumBadCoins() {
		return numBadCoins;
	}
		
	public void setControlMode(ControlType type) {
		mControlType = type;
	}
	
	public void beginTracking(Goal goal){
		currentGoal=goal;
		resetData();
		
	}
	
	private void resetData(){
		goalStartTime= System.currentTimeMillis();
		coinsGathered.clear();
		numDeaths=0;
		numGoodCoins=0;
		numBadCoins=0;
	}
	
	public void finishTracking(){
		displayData();
		sendData();
	}
		
	//TODO
	//Display data or send it to the server or something.
	private void displayData(){
		
		
	}
	
	private void sendData(){

		long duration = System.currentTimeMillis()-goalStartTime;
		System.out.println("Sending data");
		

		//Send data to server	
		RequestParams params = new RequestParams();
		ServerClient.post("addRun.php", params, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        System.out.println(response);
		        //TODO: Get back the playID and store it for uses on the second two calls
		    }
		});
		
	}	
}
