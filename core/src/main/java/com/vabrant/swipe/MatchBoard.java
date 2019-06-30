package com.vabrant.swipe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vabrant.actionsystem.ActionAdapter;
import com.vabrant.actionsystem.Pools;
import com.vabrant.actionsystem.Scalable;
import com.vabrant.actionsystem.ScaleAction;

public class MatchBoard implements Scalable{
	
	private boolean drawCenterItem;
	boolean isAnimating;
	private boolean drawBoard;
	private float scaleX;
	private float scaleY;
	private boolean hasCreatedAnimationTexture;
	private final float centerItemSize = 100;
	private final float centerItemInnerOutlineSize = 90;
	private final float centerItemInnerOutlineOffset = (centerItemSize - centerItemInnerOutlineSize) / 2;
	private final float centerItemInnerSize = 75;
	private final float centerItemInnerOffset = (centerItemInnerOutlineSize - centerItemInnerSize) / 2;
	private final float sideItemWidth = 100;
	private final float sideItemHeight = 50;
	private final float sideItemInnerOutlineWidth = 90;
	private final float sideItemInnerOutlineHeight = 40;
	private final float sideItemInnerOutlineXOffset = (sideItemWidth - sideItemInnerOutlineWidth) / 2;
	private final float sideItemInnerOutlineYOffset = (sideItemHeight - sideItemInnerOutlineHeight) / 2;
	private final float sideItemInnerWidth = 85;
	private final float sideItemInnerHeight = 35;
	private final float sideItemInnerXOffset = (sideItemInnerOutlineWidth - sideItemInnerWidth) / 2;
	private final float sideItemInnerYOffset = (sideItemInnerOutlineHeight - sideItemInnerHeight) / 2;
	private final float iconSize = 25;
	private final float iconSideXOffset = (sideItemInnerWidth - iconSize) / 2;
	private final float iconSideYOffset = (sideItemInnerHeight - iconSize) / 2;
	private final float iconCenterOffset = (centerItemInnerSize - iconSize) / 2;
	MatchItem leftItem;
	MatchItem rightItem;
	MatchItem topItem;
	MatchItem bottomItem;
	MatchItem centerItem;
	private final Array<Color> colorTemp;
	private final Array<Shapes> shapesTemp;
	private FrameBuffer frameBuffer;
	private Texture animationTexture;
	private final TextureRegion centerItemRegion;
	private final TextureRegion sideItemRegion;
	private final TextureRegion starRegion;
	private final TextureRegion triangleRegion;
	private final TextureRegion circleRegion;
	private final TextureRegion squareRegion;
	private final Vector2 centerItemPos;
	private final Vector2 leftItemPos;
	private final Vector2 rightItemPos;
	private final Vector2 topItemPos;
	private final Vector2 bottomItemPos;
	private final AnimationOverListener animationOverListener;
	public final TurnOffBoardListener turnOffBoardListener;
//	private final ShaderProgram skewShader;
	
	public MatchBoard(TextureAtlas gameAtlas) {
//		skewShader = Utils.createShader("shaders/skew.vert", "shaders/defaultFrag.frag");

		turnOffBoardListener = new TurnOffBoardListener();
		animationOverListener = new AnimationOverListener();
		colorTemp = new Array<>(4);
		shapesTemp = new Array<>(4);
		
		centerItem = new MatchItem();
		leftItem = new MatchItem();
		rightItem = new MatchItem();
		topItem = new MatchItem();
		bottomItem = new MatchItem();
		
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 960, 640, false);//960 * 640
		
		centerItemRegion = gameAtlas.findRegion("square");
		sideItemRegion = gameAtlas.findRegion("matchItem");
		starRegion = gameAtlas.findRegion("star");
		triangleRegion = gameAtlas.findRegion("triangle");
		circleRegion = gameAtlas.findRegion("circle");
		squareRegion = gameAtlas.findRegion("square");

		float centerX = (Constants.WORLD_WIDTH - centerItemSize) / 2;
		float centerY = (Constants.WORLD_HEIGHT - centerItemSize) / 2;
		centerItemPos = new Vector2(centerX, centerY);
		topItemPos = new Vector2(centerX, centerY + centerItemSize);
		leftItemPos = new Vector2(centerX, centerY);
		rightItemPos = new Vector2(centerX + centerItemSize + sideItemHeight, centerY);
		bottomItemPos = new Vector2(centerX, centerY - sideItemHeight);
		
//		debug();
//		ShaderProgram.pedantic = false;
	}
	
	private void debug() {
		setupRandomArrays();
		randomizeSideItems();
		
		scaleX = 1;
		scaleY = 1;
		isAnimating = true;
		drawBoard = true;
	}
	
	public void setupRandomArrays() {
		colorTemp.clear();
		colorTemp.add(Constants.BLUE);
		colorTemp.add(Constants.PINK);
		colorTemp.add(Constants.YELLOW);
		colorTemp.add(Constants.PURPLE);
		
		shapesTemp.clear();
		shapesTemp.add(Shapes.CIRCLE);
		shapesTemp.add(Shapes.SQUARE);
		shapesTemp.add(Shapes.STAR);
		shapesTemp.add(Shapes.TRIANGLE);
	}
	
	public void reset() {
		scaleX = 0;
		scaleY = 0;
	}
	
	public void randomizeCenterItem() {
		centerItem.set(shapesTemp.random(), colorTemp.random());
	}
	
	public void randomizeSideItems() {
		setRandomShapeAndColor(leftItem);
		setRandomShapeAndColor(rightItem);
		setRandomShapeAndColor(topItem);
		setRandomShapeAndColor(bottomItem);
	}
	
	private void setRandomShapeAndColor(MatchItem item) {
		Color color = colorTemp.random();
		Shapes shape = shapesTemp.random();
		item.set(shape, color);
		colorTemp.removeValue(color, false);
		shapesTemp.removeValue(shape, false);
	}
	
	public void drawCenterItem(boolean drawCenterItem) {
		this.drawCenterItem = drawCenterItem;
	}
	
	public ScaleAction animateIn() {
		drawBoard = true;
		isAnimating = true;
		hasCreatedAnimationTexture = false;
		
		ScaleAction scaleIn = Pools.obtain(ScaleAction.class);
		scaleIn.addListener(animationOverListener);
		scaleIn.scaleXTo(this, 0, 1);
		scaleIn.scaleYTo(this, 0, 1);
		scaleIn.set(0.5f, false, Interpolation.exp5Out);
		return scaleIn;
	}
	
	public ScaleAction animateOut() {
		drawBoard = true;
		isAnimating = true;
		hasCreatedAnimationTexture = false;
		
		ScaleAction scaleOut = Pools.obtain(ScaleAction.class);
		scaleOut.addListener(animationOverListener);
		scaleOut.scaleXTo(this, 1, 0);
		scaleOut.scaleYTo(this, 1, 0);
		scaleOut.set(0.5f, false, Interpolation.exp5Out);
		return scaleOut;
	}
	
	public void drawBoard(boolean drawBoard) {
		this.drawBoard = drawBoard;
	}
	
	private void createAnimationTexture(Batch batch) {
		if(batch.isDrawing()) batch.end();
		
		frameBuffer.begin();
//		batch.setShader(skewShader);
		Gdx.gl.glClearColor(1, 1, 1, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		drawBoard(batch);
		batch.end();
//		batch.setShader(null);
		frameBuffer.end();
		
		animationTexture = frameBuffer.getColorBufferTexture();
		
		batch.begin();
		
		hasCreatedAnimationTexture = true;
	}
	
	private TextureRegion getShapeRegion(Shapes shape) {
		switch(shape) {
			case CIRCLE:
				return circleRegion;
			case SQUARE:
				return squareRegion;
			case STAR:
				return starRegion;
			case TRIANGLE:
				return triangleRegion;
		}
		return null;
	}
	
	public void draw(Batch batch) {
		if(!drawBoard) return;
		
		if(!isAnimating) {
			drawBoard(batch);
		}
		else {
			if(!hasCreatedAnimationTexture) createAnimationTexture(batch);
			batch.draw(animationTexture, 0, 0, animationTexture.getWidth() / 4, animationTexture.getHeight() / 4, animationTexture.getWidth() / 2, animationTexture.getHeight() / 2, scaleX, scaleY, 0, 0, 0, animationTexture.getWidth(), animationTexture.getHeight(), false, true);
		}
	}
	
	private void drawBoard(Batch batch) {
		//outer part
		batch.draw(centerItemRegion, centerItemPos.x, centerItemPos.y, centerItemSize, centerItemSize);
		batch.draw(sideItemRegion, topItemPos.x, topItemPos.y, sideItemWidth, sideItemHeight);
		batch.draw(sideItemRegion, leftItemPos.x, leftItemPos.y, 0, 0, sideItemWidth, sideItemHeight, 1, 1, 90);
		batch.draw(sideItemRegion, rightItemPos.x, rightItemPos.y, 0, 0, sideItemWidth, sideItemHeight, 1, 1, 90);
		batch.draw(sideItemRegion, bottomItemPos.x, bottomItemPos.y, sideItemWidth, sideItemHeight);

		//outline
		batch.setColor(Color.BLACK);
		batch.draw(centerItemRegion, centerItemPos.x + centerItemInnerOutlineOffset, centerItemPos.y + centerItemInnerOutlineOffset, centerItemInnerOutlineSize, centerItemInnerOutlineSize);
		batch.draw(sideItemRegion, topItemPos.x + sideItemInnerOutlineXOffset, topItemPos.y + sideItemInnerOutlineYOffset, sideItemInnerOutlineWidth, sideItemInnerOutlineHeight);
		batch.draw(sideItemRegion, leftItemPos.x - sideItemInnerOutlineXOffset, leftItemPos.y + sideItemInnerOutlineYOffset, 0, 0, sideItemInnerOutlineWidth, sideItemInnerOutlineHeight, 1, 1, 90);
		batch.draw(sideItemRegion, rightItemPos.x - sideItemInnerOutlineXOffset, rightItemPos.y + sideItemInnerOutlineYOffset, 0, 0, sideItemInnerOutlineWidth, sideItemInnerOutlineHeight, 1, 1, 90);
		batch.draw(sideItemRegion, bottomItemPos.x + sideItemInnerOutlineXOffset, bottomItemPos.y + sideItemInnerOutlineYOffset, sideItemInnerOutlineWidth, sideItemInnerOutlineHeight);

		//main
		batch.setColor(topItem.color);
		batch.draw(sideItemRegion, topItemPos.x + sideItemInnerOutlineXOffset + sideItemInnerXOffset, topItemPos.y + sideItemInnerOutlineYOffset + sideItemInnerYOffset, sideItemInnerWidth, sideItemInnerHeight);
		batch.setColor(leftItem.color);
		batch.draw(sideItemRegion, leftItemPos.x - sideItemInnerOutlineXOffset - sideItemInnerXOffset, leftItemPos.y + sideItemInnerOutlineYOffset + sideItemInnerYOffset, 0, 0, sideItemInnerWidth, sideItemInnerHeight, 1, 1, 90);
		batch.setColor(rightItem.color);
		batch.draw(sideItemRegion, rightItemPos.x - sideItemInnerOutlineXOffset - sideItemInnerXOffset, rightItemPos.y + sideItemInnerOutlineYOffset + sideItemInnerYOffset, 0, 0, sideItemInnerWidth, sideItemInnerHeight, 1, 1, 90);
		batch.setColor(bottomItem.color);
		batch.draw(sideItemRegion, bottomItemPos.x + sideItemInnerOutlineXOffset + sideItemInnerXOffset, bottomItemPos.y + sideItemInnerOutlineYOffset + sideItemInnerYOffset, sideItemInnerWidth, sideItemInnerHeight);

		//shapes
		batch.setColor(Color.WHITE);
		batch.draw(getShapeRegion(topItem.shape), topItemPos.x + sideItemInnerOutlineXOffset + sideItemInnerXOffset + iconSideXOffset, topItemPos.y + sideItemInnerOutlineYOffset + sideItemInnerYOffset + iconSideYOffset, iconSize, iconSize);
		batch.draw(getShapeRegion(leftItem.shape), leftItemPos.x - sideItemInnerOutlineXOffset - sideItemInnerXOffset - iconSideXOffset, leftItemPos.y + sideItemInnerOutlineYOffset + sideItemInnerYOffset + sideItemInnerHeight - iconSideYOffset , iconSize, iconSize);
		batch.draw(getShapeRegion(rightItem.shape), rightItemPos.x - sideItemInnerOutlineXOffset - sideItemInnerXOffset - iconSideXOffset, rightItemPos.y + sideItemInnerOutlineYOffset + sideItemInnerYOffset + sideItemInnerHeight - iconSideYOffset, iconSize, iconSize);
		batch.draw(getShapeRegion(bottomItem.shape), bottomItemPos.x + sideItemInnerOutlineXOffset + sideItemInnerXOffset + iconSideXOffset, bottomItemPos.y + sideItemInnerOutlineYOffset + sideItemInnerYOffset + iconSideYOffset, iconSize, iconSize);
		
		//center color and shape
		if(drawCenterItem) {
			batch.setColor(centerItem.color);
			batch.draw(centerItemRegion, centerItemPos.x + centerItemInnerOutlineOffset + centerItemInnerOffset, centerItemPos.y + centerItemInnerOutlineOffset + centerItemInnerOffset, centerItemInnerSize, centerItemInnerSize);
			batch.setColor(Color.WHITE);
			batch.draw(getShapeRegion(centerItem.shape), centerItemPos.x + centerItemInnerOutlineOffset + centerItemInnerOffset + iconCenterOffset, centerItemPos.y + centerItemInnerOutlineOffset + centerItemInnerOffset + iconCenterOffset, iconSize, iconSize);
		}
	}

	@Override
	public void setScaleX(float x) {
		scaleX = x;
	}

	@Override
	public void setScaleY(float y) {
		scaleY = y;
	}

	@Override
	public float getScaleX() {
		return scaleX;
	}

	@Override
	public float getScaleY() {
		return scaleY;
	}
	
	public void dispose() {
		frameBuffer.dispose();
	}
	
	private class AnimationOverListener extends ActionAdapter{
		@Override
		public void actionEnd() {
			isAnimating = false;
		}
	}
	
	private class TurnOffBoardListener extends ActionAdapter{
		@Override
		public void actionEnd() {
			drawBoard = false;
		}
	}

}
