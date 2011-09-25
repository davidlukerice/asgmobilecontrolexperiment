package com.salami.awkward.mobile.control.experiment;

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
}