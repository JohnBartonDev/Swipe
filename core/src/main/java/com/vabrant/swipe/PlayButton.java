package com.vabrant.swipe;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.vabrant.swipe.Button.ButtonListener;
import com.vabrant.swipe.screens.Screen;

public class PlayButton extends Button {
	
	private float x;
	private float y;
	private final float width;
	private final float height;
	private TextureRegion playTextRegion;
	
	public PlayButton(Screen screen, TextureAtlas gameAtlas) {
		super(screen);
		
		playTextRegion = gameAtlas.findRegion("playText");

		width = playTextRegion.getRegionWidth() / 2;
		height = playTextRegion.getRegionHeight() / 2;
		
		x = (Constants.WORLD_WIDTH - width) / 2;
		y = (Constants.WORLD_HEIGHT - height) / 2;
		
		float boundsX = x - (width / 2);
		float boundsY = y - (height / 2);
		getTouchBounds().set(boundsX, boundsY, width * 2, height * 2);
	}
	
	public void draw(Batch batch) {
		batch.draw(playTextRegion, x, y, width, height);
	}

}
