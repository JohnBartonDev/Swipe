package com.vabrant.swipe;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class StaticStarSystem implements StarSystem{

	@Override
	public void init(Star star) {
		star.size = MathUtils.random(0.5f, 1f);
		star.x = MathUtils.random(0, Constants.WORLD_WIDTH - star.size);
		star.y = MathUtils.random(0, Constants.WORLD_HEIGHT - star.size);
	}
	
	@Override
	public void update(Star star) {
	}
	
	@Override
	public void draw(Batch batch, TextureRegion region, Star star) {
		batch.draw(region, star.x, star.y, star.size, star.size);
	}

}
