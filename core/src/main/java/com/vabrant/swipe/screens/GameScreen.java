package com.vabrant.swipe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.vabrant.swipe.BeatSync;
import com.vabrant.swipe.BeatSync.BeatSyncListener;
import com.vabrant.swipe.screens.GameScreen.BeatSyncAnimationSpeed;
import com.vabrant.swipe.screens.GameScreen.BeatSyncAnimationType;
import com.vabrant.swipe.Constants;
import com.vabrant.swipe.LivesHud;
import com.vabrant.swipe.MatchSystem;
import com.vabrant.swipe.Swipe;

public class GameScreen extends Screen implements BeatSyncListener{
	
	public enum BeatSyncAnimationType{
		SINGLE,
		DOUBLE,
		ALOT
	}
	public BeatSyncAnimationType beatSyncAnimationType = BeatSyncAnimationType.ALOT;
	
	public enum BeatSyncAnimationSpeed{
		EVERY_DOWN_BEAT,
		ONE_AND_THREE,
		ONE
	}
	public BeatSyncAnimationSpeed beatSyncAnimationSpeed = BeatSyncAnimationSpeed.EVERY_DOWN_BEAT;
	
	private float changeAnimationDuration = 3f;
	private float changeAnimationTimer;
	private boolean updateCameraAndMatrix;
	private MatchSystem matchSystem;
	private ScreenShake screenShake;
	private ScreenZoom screenZoom;
	public LivesHud livesHud;
	
	public enum GameState{
		RUNNING,
		GAME_OVER
	}
	
	public GameScreen(Swipe game) {
		super(game);
		
		TextureAtlas gameAtlas = game.assetManager.get(Constants.GAME_ATLAS);
		
		livesHud = new LivesHud(gameAtlas);
		matchSystem = new MatchSystem(this, gameAtlas);
		
		screenShake = new ScreenShake();
		screenZoom = new ScreenZoom();
		
		inputMultiplexer.addProcessor(matchSystem);
	}
	
	public void gameOver() {
		game.setScreen(new MainMenuScreen(game), true);
	}
	
	@Override
	public void show() {
		game.beatSync.setListener(this);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	@Override
	public void hide() {
		game.beatSync.setListener(null);
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		changeAnimationTimer += delta;
		
		if(changeAnimationTimer > changeAnimationDuration) {
			changeAnimationTimer = 0;
			beatSyncAnimationType = BeatSyncAnimationType.values()[MathUtils.random(0, 2)];
			beatSyncAnimationSpeed = BeatSyncAnimationSpeed.values()[MathUtils.random(0, 2)];
			game.infiniteShapeSystemController.randomizeColor();
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		matchSystem.dispose();
	}
	
	public void shakeWorld(float duration, float amount, float multiplier) {
		screenShake.shakeWorld(this, duration, amount, multiplier);
	}
	
	public void shakeAll(float duration, float amount, float multiplier) {
		screenShake.shakeAll(this, duration, amount, multiplier);
	}
	
	public void zoomWorld(float duration, float to) {
		screenZoom.zoomWorld(this, duration, to);
	}
	
	public void zoomAll(float duration, float to) {
		screenZoom.zoomAll(this, duration, to);
	}
	
	public void drawWorld(Batch batch) {
		game.infiniteShapeSystemController.draw(batch);
		matchSystem.draw(batch);
	}
	
	public void drawHud(Batch batch) {
		livesHud.draw(batch);
	}
	
	public void drawStaticItems(Batch batch) {
		game.starSystemController.draw(batch);
	}
	
	private void updateCameraAndMatrix(Batch batch, OrthographicCamera camera) {
		updateCameraAndMatrix = false;
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}
	
	public void create() {
		switch(beatSyncAnimationType) {
			case SINGLE:
				game.infiniteShapeSystemController.createSquare();
				break;
			case DOUBLE:
				game.infiniteShapeSystemController.createMultipleSquares(2, 0.08f);
				break;
			case ALOT:
				game.infiniteShapeSystemController.createMultipleSquares(4, 0.03f);
				break;
		}
	}
	 
	@Override
	public void currenteBeat(BeatSync beatSync, int beat) {
		switch(beatSyncAnimationSpeed) {
			case EVERY_DOWN_BEAT:
				create();
				break;
			case ONE_AND_THREE:
				if(beat == 1 || beat == 3) {
					create();
				}
				break;
			case ONE:
				if(beat == 1) {
					create();
				}
				break;
		}
		
		if(beat == 2 || beat == 4) {
			game.infiniteShapeSystemController.rotate(0.5f, -45f);
		}
		
		if(beat == 4) {
			zoomWorld(0.5f, 1.05f);
		}
	}
	
	@Override
	public void currentBar(BeatSync beatSync, int bar) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(Batch batch) {
		super.render(batch);
		
		OrthographicCamera camera = (OrthographicCamera)getCamera();
		float originalZoom = 1;
		float oldX = camera.position.x;
		float oldY = camera.position.y;
		float shakeX = screenShake.getShakeX();
		float shakeY = screenShake.getShakeY();
		
		drawStaticItems(batch);
		
		//======================================================= Setup All Shake / Zoom =======================================================
		if(screenZoom.zoomAll()) {
			camera.zoom = screenZoom.getZoom();
			updateCameraAndMatrix = true;
		}
		if(screenShake.shakeAll()) {
			camera.position.x -= shakeX;
			camera.position.y -= shakeY;
			updateCameraAndMatrix = true;
		}
		if(updateCameraAndMatrix) updateCameraAndMatrix(batch, camera);

		//======================================================= Setup World Shake / Zoom =======================================================
		if(screenZoom.zoomWorld()) {
			originalZoom = camera.zoom;
			camera.zoom = screenZoom.getZoom();
			updateCameraAndMatrix = true;
		}
		if(screenShake.shakeWorld()) {
			camera.position.x -= shakeX;
			camera.position.y -= shakeY;
			updateCameraAndMatrix = true;
		}
		if(updateCameraAndMatrix) updateCameraAndMatrix(batch, camera);

		//======================================================= Draw World =======================================================

		drawWorld(batch);

		//======================================================= Reset World Shake / Zoom =======================================================
		if(screenZoom.zoomWorld()) {
			camera.zoom = originalZoom;
			updateCameraAndMatrix = true;
		}
		if(screenShake.shakeWorld()) {
			camera.position.x = oldX;
			camera.position.y = oldY;
			updateCameraAndMatrix = true;
		}
		if(updateCameraAndMatrix) updateCameraAndMatrix(batch, camera);

		//======================================================= Draw Hud =======================================================

		drawHud(batch);

		//======================================================= Reset Zoom / Shake All =======================================================
		if(screenZoom.zoomAll()) {
			camera.zoom = originalZoom;
			updateCameraAndMatrix = true;
		}
		if(screenShake.shakeAll()) {
			camera.position.x = oldX;
			camera.position.y = oldY;
			updateCameraAndMatrix = true;
		}
		if(updateCameraAndMatrix) updateCameraAndMatrix(batch, camera);
				
	}

}
