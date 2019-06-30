package com.vabrant.swipe;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LivesHud {

	private final int maxLives = 5;
	private int livesAmount;
	private final float firstLifeX = 10;
	private final float firstLifeY = 280;
	private final float livesXOffset = 10;
	private final TextureRegion lifeRegion;
	
	public LivesHud(TextureAtlas gameAtlas) {
		lifeRegion = gameAtlas.findRegion("life");
		livesAmount = maxLives;
	}
	
	public int getLivesAmount() {
		return livesAmount;
	}

	public void addLife() {
		if(livesAmount < maxLives) livesAmount++;
	}
	
	public void removeLife() {
		if(livesAmount > 0) livesAmount--; 
	}
	
	public void draw(Batch batch) {
		float x = firstLifeX;
		float y = firstLifeY;
		for(int i = 0; i < livesAmount; i++) {
			batch.draw(lifeRegion, x, y, lifeRegion.getRegionWidth() * 0.4f, lifeRegion.getRegionHeight() * 0.4f);
			x += (lifeRegion.getRegionWidth() * 0.4f) + livesXOffset;
		}
	}
}
