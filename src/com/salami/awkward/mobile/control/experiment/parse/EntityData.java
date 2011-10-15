package com.salami.awkward.mobile.control.experiment.parse;

import com.salami.awkward.mobile.control.experiment.Entity.EntityType;

public class EntityData {
	private float posX;
	private float posY;
	private int width;
	private int height;
	private boolean isGood;
	private EntityType type;
	
	public EntityData(){
		this.posX=0;
		this.posY=0;
	}
	public EntityData(float posX, float posY){
		this.posX=posX;
		this.posY=posY;
	}
	
	public void create(){
		//not sure if ill use this
	}
	
	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public boolean isGood() {
		return isGood;
	}
	public void setGood(boolean isGood) {
		this.isGood = isGood;
	}
	public EntityType getType() {
		return type;
	}
	public void setType(EntityType type) {
		this.type = type;
	}


}
