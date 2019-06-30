package com.vabrant.swipe.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.vabrant.swipe.Constants;
import com.vabrant.swipe.Swipe;

public class SplashScreen extends Screen{

	private boolean isTransitioning;
	private boolean isLoading;
	private final float splashX;
	private final float splashY;
	private final float defaultDuration = 2f;
	private float timer;
	private final String splashPath = "textures/splash.png";
	private final Texture splashTexture; 
	
	public SplashScreen(Swipe game) {
		super(game);
		
		game.assetManager.load(splashPath, Texture.class);
		game.assetManager.finishLoading();
		
		splashTexture = game.assetManager.get(splashPath);
		
		splashX = (Constants.WORLD_WIDTH - splashTexture.getWidth()) / 2;
		splashY = (Constants.WORLD_HEIGHT - splashTexture.getHeight()) / 2;
	}
	
	@Override
	public void show() {
		AssetManager assetManager = game.assetManager;
		assetManager.load(Constants.GAME_ATLAS, TextureAtlas.class);
		assetManager.load(Constants.GAME_MUSIC, Music.class);
		assetManager.load(Constants.MATCH_SFX, Sound.class);
		assetManager.load(Constants.WRONG_MATCH_SFX, Sound.class);
		isLoading = true;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if(isLoading) {
			if(game.assetManager.update()) {
				game.init();
				isLoading = false;
			}
		}
		
		if(!isLoading && !isTransitioning && (timer += delta) > defaultDuration) {
			isTransitioning = true;
			game.fadeToScreen(new MainMenuScreen(game));
		}
	}
	
	@Override
	public void render(Batch batch) {
		if(splashTexture != null) batch.draw(splashTexture, splashX, splashY);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		game.assetManager.unload(splashPath);;
	}

}
