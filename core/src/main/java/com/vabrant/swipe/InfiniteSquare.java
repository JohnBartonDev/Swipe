package com.vabrant.swipe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool.Poolable;

public class InfiniteSquare implements Poolable{
	
	float scale = 0f;
//	float rotation = 0;
	float timer;
	float alpha;
//	Color color = new Color(Constants.PINK);
	
	public void draw(Batch batch, TextureRegion guttedSquareRegion, Color color, float rotation) {
		float x = (Constants.WORLD_WIDTH - 480) / 2;
		float y = (Constants.WORLD_HEIGHT - 480) / 2;
		
		color.a = alpha;
		batch.setColor(color);
		batch.draw(guttedSquareRegion, x, y, 480/2, 480/2, 480, 480, scale, scale, rotation);
		batch.setColor(Color.WHITE);
	}

	@Override
	public void reset() {
//		rotation = 0;
		scale = 0;
		timer = 0;
		alpha = 1;
//		color.a = 1;
	}

}
