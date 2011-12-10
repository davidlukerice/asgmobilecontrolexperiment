/**
 * Awkward Salami Games
 * MCE
 * 12/01/2011
 */
package com.salami.awkward.mobile.control.experiment.tracking;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.util.EntityUtils;
import com.badlogic.gdx.math.Vector2;
import com.loopj.android.http.*;
import org.json.*;

import com.salami.awkward.mobile.control.experiment.controls.IControlScheme.ControlType;
import com.salami.awkward.mobile.control.experiment.*;

/**
 * Statistics tracker that gathers data from goals that will be communicated back to the server
 * somehow. This class is a singleton (subject to change).  The idea is that it's reused for
 * each level.  It doesn't track data from old levels right now since I don't think we need
 * to as long as we send it somewhere else
 * @author tim/david
 *
 */
public class StatisticsTracker {
	private static StatisticsTracker stats;
	
	public static StatisticsTracker getTracker(){
		if(stats == null)
			stats = new StatisticsTracker();
		return stats;
	}
	
	public enum TestType {
		INTERVIEW,
		MARKET,
		DEVELOPER
	}
	
	public enum Goal{
		COLLECTION,
		ACCURACY,
		DEXTERITY
	}
	
	public static final TestType TEST_TYPE = TestType.DEVELOPER;
	public static final int TEST_VERSION = 1;
	private Goal currentGoal;

	private long goalStartTime;
	private long goalEndTime; 
	
	//Time of last time the player resumed/goalStartTime if he hasn't
	private long unpauseStartTime;
	private long duration;
	
	private List<CoinData> coinsGathered;//List of stats for coins acquired
	private int numGoodCoins;
	private int numBadCoins;
	private int numDeaths;
	private ControlType mControlType;
	
	//Keeps track of the playthrough to help group the three runs
	public int currentPlayID;
	
	// Booleans used to solve racecondition bug of starting a new control play run before
	// Getting data back from the server. It'll recieve it and switch the playID back to the play
	// before
	private boolean hasSubmittedData;
	private boolean hasStartedNewGame;
	
	private StatisticsTracker(){
		coinsGathered = new ArrayList<CoinData>();
		numDeaths=0;
		numGoodCoins=0;
		numBadCoins=0;
		
		hasSubmittedData = false;
		hasStartedNewGame = false;
	}
	
	public void init() {
		//Show that the playID needs to be reloaded
		currentPlayID = -1;
		hasStartedNewGame = true;
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
	
	public String getGoalString(){
		String goal="";
		switch (currentGoal) {
		case COLLECTION:
			goal = "collection";
			break;
		case ACCURACY:
			goal="accuracy";
			break;
		case DEXTERITY:
			goal = "dexterity";
			break;
		default:
			throw new RuntimeException("Bad Goal Type");
		}
		return goal;
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
	
	public ControlType getControlMode() {
		return mControlType;
	}
	
	public long getGoalStartTime() {
		return goalStartTime;
	}
	
	public long getDuration() {
		return duration+ (System.currentTimeMillis()-unpauseStartTime);
	}
	
	public String getControlModeString() {
		String ctrlScheme;
		
		switch (mControlType) {
		case TILT:
			ctrlScheme = "tilt";
			break;
		case VIRTUAL:
			ctrlScheme = "virtual";
			break;
		case SEGMENTED:
			ctrlScheme = "segmented";
			break;
		case SERVER:
			ctrlScheme = "server";
			break;
		default:
			throw new RuntimeException("Bad Control Type");
		}
		return ctrlScheme;
	}
	
	public void pauseTiming() {
		duration+=System.currentTimeMillis()-unpauseStartTime;
		unpauseStartTime=System.currentTimeMillis();
	}
	
	public void resumeTiming() {
		unpauseStartTime=System.currentTimeMillis();
	}

	
	public void endTiming() {
		goalEndTime =System.currentTimeMillis();
		pauseTiming();
		
		System.out.println("end timing: " + goalEndTime);
	}
	
	public void setControlMode(ControlType type) {
		mControlType = type;
	}
	
	public void beginTracking(Goal goal){
		currentGoal=goal;
		System.out.println("begin tracking " + currentGoal);
		resetData();
		
	}
	
	private void resetData(){
		goalStartTime= System.currentTimeMillis();
		unpauseStartTime=goalStartTime;
		coinsGathered.clear();
		numDeaths=0;
		numGoodCoins=0;
		numBadCoins=0;
		duration=0;
	}
	
	public void finishTracking(boolean sendToServer){
		displayData();
		if(sendToServer)
			sendData();
	}
		
	//TODO: probably not
	private void displayData(){
		System.out.println( goalEndTime-goalStartTime);
		System.out.println(currentGoal);
		System.out.println(numGoodCoins);
		System.out.println(numDeaths);
	}
	
	private void sendData(){
		System.out.println("Sending data");
		System.out.println(duration);
		
		//Send data to server	
		RequestParams params = new RequestParams();
		
		//Add the playID if there exists one already
		if (currentPlayID != -1) {
			params.put("playID", ""+this.currentPlayID);
        }
		
		params.put("ctrlScheme", this.getControlModeString());
		params.put("goal", this.getGoalString());
		
		params.put("totalTime", ""+duration);
		params.put("deaths", ""+this.getNumDeaths());
		
		// Distinguishes if the player is a developer, tester, or player from the marketplace
		params.put("testType", this.TEST_TYPE.toString());
		
		// State the version number to 
		params.put("version", ""+this.TEST_VERSION);
		
		//coinData : JSON formatted coin data
		JSONArray coin_array = new JSONArray();
		for (CoinData data : this.coinsGathered) {
			JSONArray arr = new JSONArray();
			try {
				arr.put(data.position.x);
				arr.put(data.position.y);
				arr.put(data.isGood);
				arr.put(data.timeObtained);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			coin_array.put(arr);
		}
		
		String coinData = coin_array.toString();
		//System.out.println("coinData: "+coinData);
		params.put("coinData", coinData);
		
		this.hasSubmittedData = true;
		this.hasStartedNewGame = false;
		
		ServerClient.post("addRun.php", params, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        System.out.println("response: "+response);
		        //If there is no error, the response is the playID, so set it if it's not already
		        
		        System.out.println("*****ID Before sent: " + currentPlayID + " and recieved: " + response);
		        
		        if (currentPlayID == -1 && hasSubmittedData && !hasStartedNewGame) {
		        	try {
		        		currentPlayID = Integer.parseInt(response);
		        		System.out.println("CurrentPlayID: " + currentPlayID);
		        	} catch(NumberFormatException e) {
		        		System.out.println("Error: "+e);
		        		currentPlayID = -1;
		        	}
		        }
		        
		        hasSubmittedData = false;
		    }
		});
		
	}	
}
