package com.vabrant.swipe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vabrant.swipe.BeatSync;
import com.vabrant.swipe.BeatSync.BeatSyncListener;
import com.vabrant.swipe.Button.ButtonListener;
import com.vabrant.swipe.Constants;
import com.vabrant.swipe.PlayButton;
import com.vabrant.swipe.Swipe;

public class MainMenuScreen extends Screen implements BeatSyncListener{
	
	private final float gameLogoX;
	private final float gameLogoY;
	private final float gameLogoWidth;
	private final float gameLogoHeight;
	private final TextureRegion gameLogoRegion;
	private PlayButton playButton;
	
	public MainMenuScreen(Swipe game) {
		super(game);
		
//		debug = true;
		
		TextureAtlas gameAtlas = game.assetManager.get(Constants.GAME_ATLAS);
		
		playButton = new PlayButton(this, gameAtlas);
		inputMultiplexer.addProcessor(playButton);
		
		gameLogoRegion = gameAtlas.findRegion("gameLogo");
		gameLogoWidth = gameLogoRegion.getRegionWidth() / 2;
		gameLogoHeight = gameLogoRegion.getRegionHeight() / 2;
		gameLogoX = (Constants.WORLD_WIDTH - gameLogoWidth) / 2;
		gameLogoY = Constants.WORLD_HEIGHT - gameLogoHeight - 10;
		
	}
	
	@Override
	public void show() {
		playButton.setActive(true);
		
		playButton.addListener(new ButtonListener() {
			 @Override
			public void touchUpSelected() {
				 game.setScreen(new GameScreen(game));
			}
		});
		
		if(!game.music.isPlaying()) {
			game.music.play();
		}
		game.beatSync.setListener(this);
		game.colorableBackground.setColor(Color.BLACK);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	@Override
	public void hide() {
		game.beatSync.setListener(null);
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
//		game.infiniteSquaresSystem.update(delta);
	}
	
	@Override
	public void render(Batch batch) {
		game.starSystemController.draw(batch);
		game.infiniteShapeSystemController.draw(batch);
		playButton.draw(batch);
		
		batch.draw(gameLogoRegion, gameLogoX, gameLogoY, gameLogoWidth, gameLogoHeight);
	}
	
	@Override
	public void debug(ShapeRenderer renderer) {
		super.debug(renderer);
		playButton.debug(renderer, 0, 0);
	}

	@Override
	public void currenteBeat(BeatSync beatSync, int beat) {
		if(beat == 4) {
			if((beatSync.currentBar % 2) != 1) {
				game.infiniteShapeSystemController.createSquare();
			}
		}
		else {
			game.infiniteShapeSystemController.createSquare();
		}
		
		if(beat == 4) {
			game.infiniteShapeSystemController.rotate(0.5f, -45f);
			game.infiniteShapeSystemController.randomizeColor();
		}
	}
	
	@Override
	public void currentBar(BeatSync beatSync, int bar) {
		if(bar % 2 == 0) {
			game.infiniteShapeSystemController.createMultipleSquares(4, 0.05f);
		}
	}

}
