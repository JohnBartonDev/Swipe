package com.vabrant.swipe;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface StarSystem {
	public void init(Star star);
	public void update(Star star);
	public void draw(Batch batch, TextureRegion region, Star star);
}
