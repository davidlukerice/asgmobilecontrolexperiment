package com.salami.awkward.mobile.control.experiment.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import com.salami.awkward.mobile.control.experiment.Entity.EntityType;

import android.content.Context;
import android.sax.*;
import android.util.Xml;


public class LevelParser {
	
    // names of the XML tags
    public static final String WORLD = "world";
    
    public static final String POS_X = "pos_x";
    public static final String POS_Y = "pos_y";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    
    public static final String ENTITY = "entity";
    
    public static final String TYPE = "type";
    public static final String HERO = "hero";
    public static final String COIN = "coin";
    public static final String GROUND = "ground";
    
    public static final String IS_GOOD = "is_good";    //good/bad coins 
    
    private String mPath;
    private Context mContext;
    private int nextGUID=0;
    
    //These are class variables so i can access them in callbacks
    private float mParsedWorldWidth; 
    private float mParsedWorldHeight;
    private EntityData mCurrentEntity;
    private List<EntityData> mEntities;
    
    public LevelParser(String path, Context context){
    	mPath=path;
    	mContext=context;
    	mParsedWorldWidth=0;
    	mParsedWorldHeight=0;
    	mEntities = new ArrayList<EntityData>();
    }
    
    public WorldData parse(){
    	RootElement root = new RootElement( WORLD);
    	
    	/*
    	 * World parsing
    	 */    
    	Element worldWidth = root.getChild(WIDTH);
    	worldWidth.setEndTextElementListener(new EndTextElementListener(){
			@Override
			public void end(String body) {
				mParsedWorldWidth =Float.parseFloat(body);
			}
    	});
    	
    	Element worldHeight = root.getChild(HEIGHT);
    	worldHeight.setEndTextElementListener(new EndTextElementListener(){
			@Override
			public void end(String body) {
				mParsedWorldHeight =Float.parseFloat(body);
			}
    	});
    	
    	/*
    	 * Hero Parsing
    	 */
    	Element hero = root.getChild(HERO);
    	hero.setElementListener(new ElementListener(){
    		
			@Override
			public void start(Attributes arg0) {
				// TODO Auto-generated method stub
				mCurrentEntity = new EntityData();
				mCurrentEntity.setType(EntityType.HERO_ENTITY);
			}
			
			@Override
            public void end() {
                mEntities.add(mCurrentEntity);
            }
        });
    	
    	hero.getChild(POS_X).setEndTextElementListener(new EndTextElementListener(){
			@Override
			public void end(String body) {
				mCurrentEntity.setPosX(Float.parseFloat(body));
			}
    	});
    	
    	hero.getChild(POS_Y).setEndTextElementListener(new EndTextElementListener(){
			@Override
			public void end(String body) {
				mCurrentEntity.setPosY(Float.parseFloat(body));
			}
    	});
    	    	
    	/*
    	 * ground parsing
    	 */
    	Element ground = root.getChild(GROUND);
    	ground.setElementListener(new ElementListener(){
    		
			@Override
			public void start(Attributes arg0) {
				// TODO Auto-generated method stub
				mCurrentEntity = new EntityData();
				mCurrentEntity.setType(EntityType.GROUND_ENTITY);
			}
			
			@Override
            public void end() {
                mEntities.add(mCurrentEntity);
            }
        });
    	
    	ground.getChild(POS_X).setEndTextElementListener(new EndTextElementListener(){
			@Override
			public void end(String body) {
				mCurrentEntity.setPosX(Float.parseFloat(body));
			}
    	});
    	
    	ground.getChild(POS_Y).setEndTextElementListener(new EndTextElementListener(){
			@Override
			public void end(String body) {
				mCurrentEntity.setPosY(Float.parseFloat(body));
			}
    	});

    	    	
    	ground.getChild(WIDTH).setEndTextElementListener(new EndTextElementListener(){
			@Override
			public void end(String body) {
				mCurrentEntity.setWidth(Integer.parseInt(body));
				mCurrentEntity.setWidthDefined(true);
			}
    	});
    	
    	ground.getChild(HEIGHT).setEndTextElementListener(new EndTextElementListener(){
			@Override
			public void end(String body) {
				mCurrentEntity.setHeight(Integer.parseInt(body));
				mCurrentEntity.setHeightDefined(true);
			}
    	});
    	
    	/*
    	 * Coin Parsing
    	 */
    	Element coin = root.getChild(COIN);
    	coin.setElementListener(new ElementListener(){
    		
			@Override
			public void start(Attributes arg0) {
				// TODO Auto-generated method stub
				mCurrentEntity = new EntityData();
				mCurrentEntity.setType(EntityType.COIN_ENTITY);
			}
			
			@Override
            public void end() {
				mCurrentEntity.setGUID(nextGUID);
				nextGUID++;
                mEntities.add(mCurrentEntity);
            }
        });
    	
    	coin.getChild(POS_X).setEndTextElementListener(new EndTextElementListener(){
			@Override
			public void end(String body) {
				mCurrentEntity.setPosX(Float.parseFloat(body));
			}
    	});
    	
    	coin.getChild(POS_Y).setEndTextElementListener(new EndTextElementListener(){
			@Override
			public void end(String body) {
				mCurrentEntity.setPosY(Float.parseFloat(body));
			}
    	});
    	
    	coin.getChild(IS_GOOD).setEndTextElementListener(new EndTextElementListener(){
			@Override
			public void end(String body) {
				mCurrentEntity.setGood(Boolean.parseBoolean(body));
			}
    	});
    	    	
    	try {
            Xml.parse(this.loadFile(), Xml.Encoding.UTF_8, 
            root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    	
        return new WorldData(mParsedWorldWidth,mParsedWorldHeight,mEntities);
    	
    }

	private InputStream loadFile() throws IOException {
		
		return mContext.getAssets().open(mPath);
	}

	protected EntityType resolveType(String body) {
		if(body.equals(GROUND))
			return EntityType.GROUND_ENTITY;
		else if(body.equals(COIN))
			return EntityType.COIN_ENTITY;
		else if(body.equals(HERO))
			return EntityType.HERO_ENTITY;
		throw new RuntimeException("Parsing failure: type must be hero, ground, or coin");
	}
}



