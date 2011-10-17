package com.salami.awkward.mobile.control.experiment.tracking;


import java.util.ArrayList;
import java.util.List;

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
		
	public enum Level{
		NONE,		//useful at start and finish of game
		COLLECTION,
		ACCURACY,
		DEXTERITY
	}
	
	private Level currentLevel;
	
	private long levelStartTime;
	private List<Integer> coinsGathered;//List of GUIDs for coins acquired
	private int numGoodCoins;
	private int numBadCoins;
	private int numDeaths;
	
	private StatisticsTracker(){
		currentLevel=Level.NONE;
		coinsGathered = new ArrayList<Integer>();
		numDeaths=0;
		numGoodCoins=0;
		numBadCoins=0;
	}
	
	public void addCoin(int GUID, boolean isGood) {
		coinsGathered.add(GUID);
		if(isGood)
			numGoodCoins++;
		else
			numBadCoins++;
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
	

	public void transitionToLevel(Level level){
		if(currentLevel != Level.NONE) {
			sendData();
		}
		
		currentLevel=level;
		resetData();
	}
	
	private void resetData(){
		levelStartTime= System.currentTimeMillis();
		coinsGathered.clear();
		numDeaths=0;
		numGoodCoins=0;
		numBadCoins=0;
	}
		
	//TODO
	//Display data or send it to the server or something.
	public void sendData(){
		long duration = System.currentTimeMillis()-levelStartTime;
	}
	
	
}
