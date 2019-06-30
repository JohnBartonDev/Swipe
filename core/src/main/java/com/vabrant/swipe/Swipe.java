package com.vabrant.swipe;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Logger;
import com.vabrant.actionsystem.ActionController;
import com.vabrant.swipe.screens.FadeTransition;
import com.vabrant.swipe.screens.Screen;
import com.vabrant.swipe.screens.SplashScreen;

public class Swipe implements ApplicationListener {
	
	private boolean disposeCurrentScreen;
	private boolean switchScreens;
	public SpriteBatch batch;
	public AssetManager assetManager;
	public Screen currentScreen;
	public Screen nextScreen;
	public ShapeRenderer debugRenderer; 
	public Color clearColor;
	public ActionController actionController;
	public ColorableBackground colorableBackground;
	public FadeTransition fadeTransition;
	public InfiniteShapeSystemController infiniteShapeSystemController;
	public StarSystemController starSystemController;
	public Music music;
	public BeatSync beatSync;
	
	@Override
	public void create() {
		beatSync = new BeatSync();
		actionController = new ActionController(20);
		clearColor = new Color(Color.WHITE);
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		assetManager.getLogger().setLevel(Logger.DEBUG);
		currentScreen = new SplashScreen(this);
		currentScreen.show();
	}
	
	public void init() {
		TextureAtlas gameAtlas = assetManager.get(Constants.GAME_ATLAS);
		fadeTransition = new FadeTransition(gameAtlas);
		colorableBackground = new ColorableBackground(gameAtlas.findRegion("backgroundWhite"), Color.WHITE); 
		infiniteShapeSystemController = new InfiniteShapeSystemController(actionController, gameAtlas);
		starSystemController = new StarSystemController(gameAtlas);
		
		music = assetManager.get(Constants.GAME_MUSIC);
		music.setLooping(true);
		
		infiniteShapeSystemController.createSquare();
	}

	@Override
	public void resize(int width, int height) {
		currentScreen.resize(width, height);
	}

	@Override
	public void pause() {
		currentScreen.pause();
	}

	@Override
	public void resume() {
		currentScreen.resume();
	}

	@Override
	public void dispose() {
		currentScreen.dispose();
		assetManager.dispose();
		batch.dispose();
	}
	
	public void setScreen(Screen screen) {
		setScreen(screen, true);
	}
	
	public void fadeToScreen(Screen nextScreen) {
		fadeTransition.fade(currentScreen, nextScreen);
	}
	
	public void setScreen(Screen screen, boolean disposeCurrentScreen) {
		if(screen == null) throw new IllegalArgumentException("Screen is null.");
		switchScreens = true;
		nextScreen = screen;
		this.disposeCurrentScreen = disposeCurrentScreen;
	}
	
	private void switchScreens() {
		switchScreens = false;
		//reset and dispose the current screen
		if(currentScreen != null) {
			currentScreen.hide();
			if(disposeCurrentScreen) currentScreen.dispose();
			currentScreen = null;
			
		}
		//set and show the next screen 
		currentScreen = nextScreen;
		nextScreen = null;
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		currentScreen.show();
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float delta = Gdx.graphics.getDeltaTime();
		
		if(actionController != null) actionController.update(delta);
		currentScreen.update(delta);
		if(starSystemController != null) starSystemController.update(delta);
		if(infiniteShapeSystemController != null) infiniteShapeSystemController.update(delta);
		if(music != null && infiniteShapeSystemController != null) beatSync.update(music, infiniteShapeSystemController);
		
		batch.setProjectionMatrix(currentScreen.viewport.getCamera().combined);
		batch.enableBlending();
		batch.begin();
		
		if(colorableBackground != null) colorableBackground.draw(batch, currentScreen.viewport.getWorldWidth(), currentScreen.viewport.getWorldHeight());
		
		currentScreen.render(batch);
		
		if(fadeTransition != null && fadeTransition.isActive()) fadeTransition.draw(batch);
		batch.end();
//		System.out.println(batch.renderCalls);
		
		if(currentScreen.debug) {
			if(debugRenderer == null) {
				debugRenderer = new ShapeRenderer();
				debugRenderer.setAutoShapeType(true);
			}
			
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			debugRenderer.setProjectionMatrix(currentScreen.viewport.getCamera().combined);
			debugRenderer.begin();
			currentScreen.debug(debugRenderer);
			debugRenderer.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}
		
		if(switchScreens) switchScreens();
	}
}