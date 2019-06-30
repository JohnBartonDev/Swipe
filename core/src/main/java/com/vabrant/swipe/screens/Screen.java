package com.vabrant.swipe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vabrant.actionsystem.Action;
import com.vabrant.actionsystem.ActionController;
import com.vabrant.swipe.Constants;
import com.vabrant.swipe.InputMultiplexer;
import com.vabrant.swipe.Swipe;

public class Screen {
	
	public boolean debugCameraAndViewport;
	public boolean debug;
	public float screenX;
	public float screenY;
	public float worldX;
	public float worldY;
	public Swipe game;
	public Viewport viewport;
	private ActionController actionController;
	public final InputMultiplexer inputMultiplexer;
	
	public Screen(Swipe game) {
		this.game = game;
		viewport = new ExtendViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
		actionController = new ActionController(15);
		inputMultiplexer = new InputMultiplexer(viewport);
	}

	public void resize(int width, int height) {
		viewport.update(width, height, true);
		worldX = (viewport.getWorldWidth() - Constants.WORLD_WIDTH) / 2;
		worldY = (viewport.getWorldHeight() - Constants.WORLD_HEIGHT) / 2;
	}
	
	public void debug(ShapeRenderer renderer) {
		debugCameraAndWorld(renderer);
	}
	
	private void debugCameraAndWorld(ShapeRenderer renderer) {
		renderer.setColor(Color.RED);
		renderer.rect(0 + 1, 0, viewport.getWorldWidth() - 1, viewport.getWorldHeight() - 1);
		renderer.setColor(Color.GREEN);
		renderer.rect(worldX, worldY, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
	}
	
	public Array<Action> getActions(){
		return actionController.getActions();
	}
	
	public void addAction(Action action) {
		actionController.addAction(action); 
	}

	public Action getActionByName(String name) {
		return actionController.getActionByName(name);
	}
	
	public void killAction(String name) {
		actionController.killAction(name);
	}
	
	public void pauseAction(String name, boolean value) {
		actionController.pauseAction(name, value);
	}
	
	public void pauseAllActions() {
		actionController.pauseAllActions();
	}
	
	public void resumeAllActions() {
		actionController.resumeAllActions();
	}
	
	public Camera getCamera() {
		return viewport.getCamera();
	}
	
	public float getX() {
		return worldX + screenX;
	}
	
	public float getY() {
		return worldY + screenY;
	}

	public void update(float delta) {
		actionController.update(delta);
	}
	
	public void render(Batch batch) {};
	public void pause() {};
	public void resume() {};
	public void dispose() {};
	public void hide() {};
	public void show() {};
	
}
