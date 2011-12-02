/**
 * Awkward Salami Games
 * MCE
 * 12/01/2011
 */
package com.salami.awkward.mobile.control.experiment;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * This maps Box2D collision callbacks to the entities involved and calls the appropriate
 * callbacks in the entity. Only one of these needs to be around at a time.
 * 
 * @author Tim
 */
public class MCEContactListener implements ContactListener{
    /**
     * Called when two fixtures begin to touch.
     */
    public final void beginContact (Contact contact)
    {
    	//Map contact fixtures to associated entity instances and call appropriate callbacks

    	Object objA=contact.getFixtureA().getBody().getUserData();
    	Object objB=contact.getFixtureB().getBody().getUserData();
    	
    	if(objA != null)
    		((Entity) objA).onCollide(contact.getFixtureB(),contact);
    	
    	if(objB != null)
    		((Entity) objB).onCollide(contact.getFixtureA(),contact);
    }

    /**
     * Called when two fixtures cease to touch.
     */
    public final void endContact (Contact contact)
    {
    	//Map contact fixtures to associated entity instances and call appropriate callbacks

    	Object objA=contact.getFixtureA().getBody().getUserData();
    	Object objB=contact.getFixtureB().getBody().getUserData();
    	
    	if(objA != null)
    		((Entity) objA).onSeparate(contact.getFixtureB(),contact);
    	
    	if(objB != null)
    		((Entity) objB).onSeparate(contact.getFixtureA(),contact);
    }
    

    /*
    This is called after a contact is updated. This allows you to inspect a
    contact before it goes to the solver. If you are careful, you can modify the
    contact manifold (e.g. disable contact).
    A copy of the old manifold is provided so that you can detect changes.
    Note: this is called only for awake bodies.
    Note: this is called even when the number of contact points is zero.
    Note: this is not called for sensors.
    Note: if you set the number of contact points to zero, you will not
    get an EndContact callback. However, you may get a BeginContact callback
    the next step.
    */
    public void preSolve(Contact contact, Manifold oldManifold)
    {
    	
    }

    /* 
     * This lets you inspect a contact after the solver is finished. This is useful
     for inspecting impulses.
     Note: the contact manifold does not include time of impact impulses, which can be
     arbitrarily large if the sub-step is small. Hence the impulse is provided explicitly
     in a separate data structure.
     Note: this is only called for contacts that are touching, solid, and awake. 
     */
    public void postSolve(Contact contact, ContactImpulse impulse)
    {
    	
    }

}
