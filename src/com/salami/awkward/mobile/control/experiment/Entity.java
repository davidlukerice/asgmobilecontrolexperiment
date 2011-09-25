package com.salami.awkward.mobile.control.experiment;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

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
     */
    public void onCollide(Fixture other);
    
    /*
     * Method overridden in child classes if they have custom logic for separating
     * from another object
     */
    public void onSeparate(Fixture other);
}
