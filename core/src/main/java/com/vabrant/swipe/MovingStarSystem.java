package com.vabrant.swipe;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;


/**
 * 
 * @author The Coding Train https://www.youtube.com/watch?v=17WoOqgXsRM
 *
 */
public class MovingStarSystem implements StarSystem {

	float speed = 5;
	
	@Override
	public void init(Star star) {
		star.x = MathUtils.random(-Constants.WORLD_WIDTH, Constants.WORLD_WIDTH);
		star.y = MathUtils.random(-Constants.WORLD_HEIGHT, Constants.WORLD_HEIGHT);
		star.z = MathUtils.random(Constants.WORLD_WIDTH);
		star.pz = star.z;
		star.speed = MathUtils.random(0, 8);
	}
	
	@Override
	public void update(Star star) {
		star.z -= speed;
		
		if(star.z < 1) {
			star.x = MathUtils.random(-Constants.WORLD_WIDTH/2, Constants.WORLD_WIDTH/2);
			star.y = MathUtils.random(-Constants.WORLD_HEIGHT/2, Constants.WORLD_HEIGHT/2);
			star.z = Constants.WORLD_WIDTH;
			star.speed = MathUtils.random(0, 8);
		}
	}
	
	@Override
	public void draw(Batch batch, TextureRegion region, Star star) {
		float sX = Utils.map(star.x / star.z, 0, 1, 0, Constants.WORLD_WIDTH);
		float sY = Utils.map(star.y / star.z, 0, 1, 0, Constants.WORLD_HEIGHT);
		float pX = Utils.map(star.x / star.pz, 0, 1, 0, Constants.WORLD_WIDTH);
		float pY = Utils.map(star.y / star.pz, 0, 1, 0, Constants.WORLD_HEIGHT);
		float size = Utils.map(star.z, 0, Constants.WORLD_WIDTH, 5, 0);
		float xOffset = Constants.WORLD_WIDTH / 2;
		float yOffset = Constants.WORLD_HEIGHT / 2;
		batch.draw(region, sX + xOffset, sY + yOffset, size, size);
	}

}
