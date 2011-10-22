package com.salami.awkward.mobile.control.experiment.parse;

import com.salami.awkward.mobile.control.experiment.Entity.EntityType;
import com.salami.awkward.mobile.control.experiment.Ground;

public class EntityData {
	private float posX;
	private float posY;
	private int width;
	private int height;
	private int guid;
	private boolean isGood;
	private boolean widthDefined;
	private boolean heightDefined;
	private EntityType type;
	
	public EntityData(){
		this(0,0);
	}
	public EntityData(float posX, float posY){
		setPosX(posX);
		setPosY(posY);
		setWidthDefined(false);
		setHeightDefined(false);
	}
	
	public float getPosX() {
		return posX*Ground.TILE_WIDTH;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY*Ground.TILE_HEIGHT;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}
	public int getWidth() {
		if(isWidthDefined())
			return width*Ground.TILE_WIDTH;
		else
			return Ground.TILE_WIDTH;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		if(isHeightDefined())
			return height*Ground.TILE_HEIGHT;
		else
			return Ground.TILE_HEIGHT;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public boolean isGood() {
		return isGood;
	}
	public void setGood(boolean isGood) {
		this.isGood = isGood;
		System.out.println(this.isGood);
	}
	public EntityType getType() {
		return type;
	}
	public void setType(EntityType type) {
		this.type = type;
	}
	public boolean isWidthDefined() {
		return widthDefined;
	}
	public void setWidthDefined(boolean widthDefined) {
		this.widthDefined = widthDefined;
	}
	public boolean isHeightDefined() {
		return heightDefined;
	}
	public void setHeightDefined(boolean heightDefined) {
		this.heightDefined = heightDefined;
	}
	public int getGUID() {
		return guid;
	}
	public void setGUID(int guid) {
		this.guid = guid;
	}


}
