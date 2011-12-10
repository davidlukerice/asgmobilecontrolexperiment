package com.salami.awkward.mobile.control.experiment;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Used to destinguish world object types.
 * TODO: Probably just have this as an object that Hero,Ground, &c extends
 * 			Was just testing this out
 * @author David Rice
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
