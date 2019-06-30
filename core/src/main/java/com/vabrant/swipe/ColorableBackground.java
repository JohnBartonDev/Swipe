package com.vabrant.swipe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.vabrant.actionsystem.Colorable;

public class ColorableBackground implements Colorable{

	private final NinePatch background;
	
	public ColorableBackground(TextureRegion region, Color color) {
		background = new NinePatch(region, color);
	}
	
	@Override
	public void setColor(Color color) {
		background.setColor(color);
	}

	@Override
	public Color getColor() {
		return background.getColor();
	}
	
	public void draw(Batch batch, float width, float height) {
		background.draw(batch, 0, 0, width, height);
	}

}
