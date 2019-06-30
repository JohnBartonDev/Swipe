package com.vabrant.swipe;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.vabrant.actionsystem.ActionAdapter;
import com.vabrant.actionsystem.DelayAction;
import com.vabrant.actionsystem.GroupAction;
import com.vabrant.actionsystem.Pools;
import com.vabrant.actionsystem.RunnableAction;
import com.vabrant.swipe.screens.GameScreen;
import com.vabrant.swipe.screens.GameScreen.BeatSyncAnimationSpeed;
import com.vabrant.swipe.screens.GameScreen.BeatSyncAnimationType;

public class MatchSystem extends InputAdapter{

	public enum MatchType{
		COLORS,
		SHAPES,
		ANY
	}
	private MatchType matchType;
	private MatchType lastMatchType;
	private MatchType[] matchTypeValues = MatchType.values();
	
	public enum MatchState{
		IDLE, 
		SETUP,
		RUNNING,
		OVER
	}
	private MatchState state = MatchState.IDLE;
	
	private int sameMatchTypeCounter;
	private boolean restrictInput = true;
	private final float nextCenterItemDuration = 0.3f;
	private final int maximumMatchItems = 15;
	private final int minimumMatchItems = 10;
	private int remainingMatchItems;
	private final float matchTypeCenterDisplayerDuration = 1f; 
	private MatchBoard board;
	private MatchTypeDisplayer displayer;
	private final GameScreen gameScreen;
	private final SetupOverListener setupOverListener;
	private final NextCenterItemRunnable nextCenterItemRunnable;
	private final EndCycleListener endCycleListener;
	private final GameOverListener gameOverListener;
	private Sound matchSound;
	private Sound wrongMatchSound;
	
	public MatchSystem(GameScreen gameScreen, TextureAtlas gameAtlas) {
		this.gameScreen = gameScreen;
		gameOverListener = new GameOverListener();
		endCycleListener = new EndCycleListener();
		nextCenterItemRunnable = new NextCenterItemRunnable();
		setupOverListener = new SetupOverListener();
		board = new MatchBoard(gameAtlas);
		displayer = new MatchTypeDisplayer(gameAtlas);
		matchSound = gameScreen.game.assetManager.get(Constants.MATCH_SFX);
		wrongMatchSound = gameScreen.game.assetManager.get(Constants.WRONG_MATCH_SFX);
		
		state = MatchState.SETUP;
		startCycle();
	}
	
	public void startCycle() {
		gameScreen.game.infiniteShapeSystemController.randomizeColor();
		gameScreen.livesHud.addLife();
		
		matchType = matchTypeValues[MathUtils.random(0, 2)];
		
		if(lastMatchType != null) {
			if(matchType.equals(lastMatchType)) {
				if(sameMatchTypeCounter == 2) {
					for(int i = 0; i < matchTypeValues.length; i++) {
						if(!matchTypeValues[i].equals(matchType)) {
							matchType = matchTypeValues[i];
							break;
						}
					}
					sameMatchTypeCounter = 0;
				}
				else {
					sameMatchTypeCounter++;
				}
			}
		}
		
		lastMatchType = matchType;
		
		remainingMatchItems = MathUtils.random(minimumMatchItems, maximumMatchItems);
		board.setupRandomArrays();
		board.randomizeSideItems();

		DelayAction initialDelay = Pools.obtain(DelayAction.class);
		initialDelay.set(1);
		
		DelayAction centerDisplayerDurationDelay = Pools.obtain(DelayAction.class);
		centerDisplayerDurationDelay.set(matchTypeCenterDisplayerDuration);

		GroupAction animateBoardAndSideDisplayerInGroup = Pools.obtain(GroupAction.class);
		animateBoardAndSideDisplayerInGroup.parallel();
		animateBoardAndSideDisplayerInGroup.add(board.animateIn());
		animateBoardAndSideDisplayerInGroup.add(displayer.animateSideIn());
		
		GroupAction animateCenterDisplayerOutAndBoardAndSideDisplayerInGroup = Pools.obtain(GroupAction.class);
		animateCenterDisplayerOutAndBoardAndSideDisplayerInGroup.parallel(0.13f);
		animateCenterDisplayerOutAndBoardAndSideDisplayerInGroup.add(displayer.animateCenterOut());
		animateCenterDisplayerOutAndBoardAndSideDisplayerInGroup.add(animateBoardAndSideDisplayerInGroup);
		
		GroupAction mainGroup = Pools.obtain(GroupAction.class);
		mainGroup.sequence();
		mainGroup.addListener(setupOverListener);
		mainGroup.add(initialDelay);
		mainGroup.add(displayer.animateCenterIn());
		mainGroup.add(centerDisplayerDurationDelay);
		mainGroup.add(animateCenterDisplayerOutAndBoardAndSideDisplayerInGroup);
		
		gameScreen.addAction(mainGroup);
	}
	
	public void endCycle() {
		GroupAction groupAction = Pools.obtain(GroupAction.class);
		groupAction.parallel();
		groupAction.addListener(endCycleListener);
		groupAction.add(board.animateOut());
		groupAction.add(displayer.animateSideOut());
		
		gameScreen.addAction(groupAction);
	}
	
	public void gameOver() {
		state = MatchState.OVER;
		
		DelayAction endDelay = Pools.obtain(DelayAction.class);
		endDelay.set(1f);
		
		GroupAction animateOutGroup = Pools.obtain(GroupAction.class);
		animateOutGroup.parallel();
		animateOutGroup.addListener(board.turnOffBoardListener);
		animateOutGroup.add(board.animateOut());
		animateOutGroup.add(displayer.animateSideOut());
		
		GroupAction mainGroup = Pools.obtain(GroupAction.class);
		mainGroup.sequence();
		mainGroup.addListener(gameOverListener);
		mainGroup.add(animateOutGroup);
		mainGroup.add(endDelay);
		
		gameScreen.addAction(mainGroup);
	}
	
	public void draw(Batch batch) {
		board.draw(batch);
		displayer.draw(batch, matchType);
	}

	public void dispose() {
		board.dispose();
	}
	
	private void transitionToNextCenterItem() {
		DelayAction nextItemDelay = Pools.obtain(DelayAction.class);
		nextItemDelay.set(nextCenterItemDuration);
		
		RunnableAction runnableAction = Pools.obtain(RunnableAction.class);
		runnableAction.set(nextCenterItemRunnable);
		
		GroupAction groupAction = Pools.obtain(GroupAction.class);
		groupAction.sequence();
		groupAction.add(nextItemDelay);
		groupAction.add(runnableAction);
		
		gameScreen.addAction(groupAction);
	}
	
	private void match() {
		matchSound.play();
		board.drawCenterItem(false);
		gameScreen.zoomWorld(0.5f, 0.95f);
		remainingMatchItems--;
		
		if(remainingMatchItems <= 0) {
			state = MatchState.SETUP;
			endCycle();
		}
		else {
			restrictInput = true;
			transitionToNextCenterItem();
		}
	}
	
	private void noMatch() {
		wrongMatchSound.play();
		gameScreen.zoomAll(0.5f, 1.1f);
		gameScreen.shakeAll(0.3f, 4, 0.4f);
		gameScreen.livesHud.removeLife();
		
		if(gameScreen.livesHud.getLivesAmount() == 0) {
			gameOver();
		}
	}
	
	private void checkForAnyMatch(int keycode) {
		MatchItem item = getItem(keycode);
		if(item != null) {
			if(board.centerItem.shape.equals(item.shape) || board.centerItem.color.equals(item.color)) {
				match();
			}
			else {
				noMatch();
			}
		}
	}
	
	private void checkForShapesMatch(int keycode) {
		MatchItem item = getItem(keycode);
		if(item != null) {
			if(board.centerItem.shape.equals(item.shape)) {
				match();
			}
			else {
				noMatch();
			}
		}
	}
	
	private void checkForColorsMatch(int keycode) {
		MatchItem item = getItem(keycode);
		if(item != null) {
			if(board.centerItem.color.equals(item.color)) {
				match();
			}
			else {
				noMatch();
			}
		}
	}
	
	private MatchItem getItem(int keycode) {
		switch(keycode) {
			case Keys.LEFT:
			case Keys.A:
				return board.leftItem;
			case Keys.RIGHT:
			case Keys.D:
				return board.rightItem;
			case Keys.UP:
			case Keys.W:
				return board.topItem;
			case Keys.DOWN:
			case Keys.S:
				return board.bottomItem;
		}
		return null;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(!state.equals(MatchState.RUNNING) || restrictInput) return false;
		
		switch(matchType) {
			case SHAPES:
				checkForShapesMatch(keycode);
				break;
			case COLORS:
				checkForColorsMatch(keycode);
				break;
			case ANY:
				checkForAnyMatch(keycode);
				break;
		}
		return super.keyDown(keycode);
	}
	
	private class GameOverListener extends ActionAdapter{
		@Override
		public void actionEnd() {
			gameScreen.gameOver();
		}
	}
	
	private class EndCycleListener extends ActionAdapter{
		@Override
		public void actionEnd() {
			board.drawBoard(false);
			board.reset();
			displayer.reset();
			startCycle();
		}
	}
	
	private class NextCenterItemRunnable implements Runnable{
		@Override
		public void run() {
			board.randomizeCenterItem();
			board.drawCenterItem(true);
			restrictInput = false;
		}
	}
	
	private class SetupOverListener extends ActionAdapter{
		@Override
		public void actionEnd() {
			state = MatchState.RUNNING;
			board.setupRandomArrays();
			transitionToNextCenterItem();
		}
	}
}
