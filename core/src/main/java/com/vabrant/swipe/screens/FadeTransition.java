package com.vabrant.swipe.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.vabrant.actionsystem.ActionAdapter;
import com.vabrant.actionsystem.ColorAction;
import com.vabrant.actionsystem.GroupAction;
import com.vabrant.actionsystem.Pools;
import com.vabrant.swipe.ColorableBackground;

public class FadeTransition extends ActionAdapter {
	
	private boolean isActive;
	boolean isFadingOn = true;
	private final float fadeOffDuration = 1f;
	private final float fadeOnDuration = 0.75f;
	private Screen fromScreen;
	private Screen toScreen;
	private ColorableBackground overlay;
	
	public FadeTransition(TextureAtlas gameAtlas) {
		overlay = new ColorableBackground(gameAtlas.findRegion("backgroundWhite"), Color.BLACK);
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void reset() {
		isActive = false;
		fromScreen = null;
		toScreen = null;
		isFadingOn = true;
	}
	
	public void fade(Screen fromScreen, Screen toScreen) {
		this.fromScreen = fromScreen;
		this.toScreen = toScreen;
		overlay.getColor().a = 0;
		isActive = true;
		startFadeAnimation();
	}

	private void startFadeAnimation() {
		ColorAction fadeOnAction = Pools.obtain(ColorAction.class);
		fadeOnAction.changeAlpha(overlay, 0, 1);
		fadeOnAction.set(fadeOnDuration, false, Interpolation.fade);
		fadeOnAction.addListener(this);
		
		ColorAction fadeOffAction = Pools.obtain(ColorAction.class);
		fadeOffAction.changeAlpha(overlay, 1, 0);
		fadeOffAction.set(fadeOffDuration, false, Interpolation.linear);
		
		GroupAction groupAction = Pools.obtain(GroupAction.class);
		groupAction.sequence();
		groupAction.add(fadeOnAction);
		groupAction.add(fadeOffAction);
		groupAction.addListener(this);
		
		fromScreen.game.actionController.addAction(groupAction);
	}
	
	@Override
	public void actionEnd() {
		if(isFadingOn) {
			isFadingOn = false;
			fromScreen.game.setScreen(toScreen);
		}
		else {
			reset();
		}
	}
	
	public void draw(Batch batch) {
		overlay.draw(batch, fromScreen.viewport.getWorldWidth(), fromScreen.viewport.getWorldHeight());
	}


}
