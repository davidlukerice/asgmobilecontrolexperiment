/**
 * Awkward Salami Games
 * MCE
 * 12/01/2011
 */
package com.salami.awkward.mobile.control.experiment;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Basic entity within the game.
 * @author David
 *
 */
public interface Entity {
	public enum EntityType{
			HERO_ENTITY,
			COIN_ENTITY,
			GROUND_ENTITY
			};
			

	public EntityType getEntityType(); 

    /*
     * Method overridden in child classes if they have custom logic for touching
     * another object
     * 
     * TODO: Change this to take an entity instead of a fixture when the ground,
     * floors,etc. are all entities. You can already access the entity if it's 
     * non-null through other.getBody().getUserData(), but it's a little gross.
     */
    public void onCollide(Fixture other, Contact contact);
    
    /*
     * Method overridden in child classes if they have custom logic for separating
     * from another object
     * 
     * TODO: Change this to take an entity instead of a fixture when the ground,
     * floors,etc. are all entities. You can already access the entity if it's 
     * non-null through other.getBody().getUserData(), but it's a little gross.
     */
    public void onSeparate(Fixture other, Contact contact);
}
