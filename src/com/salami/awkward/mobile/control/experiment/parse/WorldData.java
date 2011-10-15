package com.salami.awkward.mobile.control.experiment.parse;

import java.util.List;

import com.salami.awkward.mobile.control.experiment.Ground;



public class WorldData {
	//these are floats since there isn't a really good reason for them not to be,
	//but they should probably always be integers since they're measured in tiles
	private float width;
	private float height;
	private List<EntityData> entities;
	
	public WorldData(float width, float height, List<EntityData> entities){
		this.setWidth(width);
		this.setHeight(height);
		this.setEntities(entities);
		
		for(int index = 0; index < entities.size(); ++index){
			System.out.println(entities.get(index).getType());
		}
	}

	public float getWidth() {
		return width*Ground.TILE_WIDTH;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height*Ground.TILE_HEIGHT;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public List<EntityData> getEntities() {
		return entities;
	}

	public void setEntities(List<EntityData> entities) {
		this.entities = entities;
	}
}
