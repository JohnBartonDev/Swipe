package com.vabrant.swipe;

import com.badlogic.gdx.graphics.Color;

public class MatchItem {
	
	Shapes shape;
	Color color = new Color();
	
	public void set(Shapes shape, Color color) {
		this.shape = shape;
		this.color.set(color);
	}

}
