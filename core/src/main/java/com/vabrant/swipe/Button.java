package com.vabrant.swipe;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.vabrant.swipe.screens.Screen;

public class Button extends InputAdapter{
	
	public interface ButtonListener {
		public default void touchDown() {};
		public default void touchDownSelected() {};
		public default void touchUp() {};
		public default void touchUpSelected() {};
	}
	
	private boolean isActive;
	Rectangle touchBounds;
	private Array<ButtonListener> listeners;
	private Screen screen;
	
	public Button(Screen screen) {
		this(screen, 0, 0, 0, 0);
	}

	public Button(Screen screen, float width, float height) {
		this(screen, 0, 0, width, height);
	}
	
	public Button(Screen screen, float x, float y, float width, float height) {
		this.screen = screen;
		touchBounds = new Rectangle(x, y, width, height);
		listeners = new Array<>();
	}
	
	public Rectangle getTouchBounds() {
		return touchBounds;
	}
	
	public void addListener(ButtonListener listener) {
		listeners.add(listener);
	}
	
	public boolean isActve() {
		return isActive;
	}
	
	public void setActive(boolean active) {
		isActive = active;
	}
	
	public boolean contains(int x, int y) {
		float buttonX = touchBounds.x;
		float buttonY = touchBounds.y;
		
		buttonX += screen.getX();
		buttonY += screen.getY();
		
		return buttonX <= x && buttonX + touchBounds.width >= x && buttonY <= y && buttonY + touchBounds.height >= y;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!isActive || listeners.size == 0) return false;
		if(contains(screenX, screenY)) {
			for(int i = 0, size = listeners.size; i < size; i++) {
				listeners.get(i).touchDownSelected();
			}
			return true;
		}
		else {
			for(int i = 0, size = listeners.size; i < size; i++) {
				listeners.get(i).touchDown();
			}
		}	
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!isActive || listeners.size == 0) return false;
		if(contains(screenX, screenY)) {
			for(int i = 0, size = listeners.size; i < size; i++) {
				listeners.get(i).touchUpSelected();
			}
			return true;
		}
		else {
			for(int i = 0, size = listeners.size; i < size; i++) {
				listeners.get(i).touchUp();
			}
		}
		return false;
	}
	
	public void debug(ShapeRenderer renderer, float screenXOffset, float screenYOffset) {
		renderer.set(ShapeType.Line);
		renderer.setColor(Color.GREEN);
		renderer.rect(touchBounds.x + screenXOffset, touchBounds.y + screenYOffset, touchBounds.width, touchBounds.height);
	}

}
