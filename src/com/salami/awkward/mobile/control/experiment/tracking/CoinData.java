/**
 * Awkward Salami Games
 * MCE
 * 12/01/2011
 */
package com.salami.awkward.mobile.control.experiment.tracking;

import com.badlogic.gdx.math.Vector2;

/**
 * Basic data entry for the coin
 * Used to collect information about collected coins
 * before sending to the server
 * @author Tim
 *
 */
public class CoinData {

	public CoinData(Vector2 position, boolean isGood, long timeObtained){
		this.position=position;
		this.isGood=isGood;
		this.timeObtained=timeObtained;
	}
	public Vector2 position;
	public boolean isGood;
	public long timeObtained;
}
