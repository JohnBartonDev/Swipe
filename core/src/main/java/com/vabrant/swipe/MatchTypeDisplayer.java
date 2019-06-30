package com.vabrant.swipe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.vabrant.actionsystem.ActionAdapter;
import com.vabrant.actionsystem.ColorAction;
import com.vabrant.actionsystem.Colorable;
import com.vabrant.actionsystem.GroupAction;
import com.vabrant.actionsystem.Movable;
import com.vabrant.actionsystem.MoveAction;
import com.vabrant.actionsystem.Pools;
import com.vabrant.swipe.MatchSystem.MatchType;

public class MatchTypeDisplayer implements Movable{

	private boolean drawCenter;
	private boolean drawSide;
	private final float animationCenterYOffset = 50;
	private final float animationCenterXOffset = 100;
	private float animationX;
	private float animationY;
	private final float matchTextWidth;
	private final float matchTextHeight;
	private final float colorsTextWidth;
	private final float colorsTextHeight;
	private final float shapesTextWidth;
	private final float shapesTextHeight;
	private final float anyTextWidth;
	private final float anyTextHeight;
	private final Vector2 matchTextCenterPos;
	private final Vector2 colorsTextCenterPos;
	private final Vector2 shapesTextCenterPos;
	private final Vector2 anyTextCenterPos;
	private final Vector2 matchTextSidePos;
	private final Vector2 colorsTextSidePos;
	private final Vector2 shapesTextSidePos;
	private final Vector2 anyTextSidePos;
	private final TextureRegion matchTextRegion;
	private final TextureRegion colorsTextRegion;
	private final TextureRegion shapesTextRegion;
	private final TextureRegion anyTextRegion;
	private final Color centerColor;
	private final Color sideColor;
	private final DrawCenterOverListener drawCenterOverListener;
	private final DrawSideOverListener drawSideOverListener;
	private final CenterColorable centerColorable;
	private final SideColorable sideColorable;
	
	public MatchTypeDisplayer(TextureAtlas gameAtlas) {
		centerColorable = new CenterColorable();
		sideColorable = new SideColorable();
		drawCenterOverListener = new DrawCenterOverListener();
		drawSideOverListener = new DrawSideOverListener();
		centerColor = new Color(Color.WHITE);
		sideColor = new Color(Color.WHITE);
		matchTextRegion = gameAtlas.findRegion("matchText");
		colorsTextRegion = gameAtlas.findRegion("colorsText");
		shapesTextRegion = gameAtlas.findRegion("shapesText");
		anyTextRegion = gameAtlas.findRegion("anyText");
		matchTextWidth = matchTextRegion.getRegionWidth() / 2;
		matchTextHeight = matchTextRegion.getRegionHeight() / 2;
		colorsTextWidth = colorsTextRegion.getRegionWidth() / 2;
		colorsTextHeight = colorsTextRegion.getRegionHeight() / 2;
		shapesTextWidth = shapesTextRegion.getRegionWidth() / 2;
		shapesTextHeight = shapesTextRegion.getRegionHeight() / 2;
		anyTextWidth = anyTextRegion.getRegionWidth() / 2;
		anyTextHeight = anyTextRegion.getRegionHeight() / 2;
		
		final float firstLayerCenterY = 180;
		final float secondLayerCenterYOffset = 10;
		
		matchTextCenterPos = new Vector2((Constants.WORLD_WIDTH - matchTextWidth) / 2, firstLayerCenterY);
		colorsTextCenterPos = new Vector2((Constants.WORLD_WIDTH - colorsTextWidth) / 2, matchTextCenterPos.y - colorsTextHeight - secondLayerCenterYOffset);
		shapesTextCenterPos = new Vector2((Constants.WORLD_WIDTH - shapesTextWidth) / 2, matchTextCenterPos.y - shapesTextHeight - secondLayerCenterYOffset);
		anyTextCenterPos = new Vector2((Constants.WORLD_WIDTH - anyTextWidth) / 2, matchTextCenterPos.y - anyTextHeight - secondLayerCenterYOffset);
		
		final float firstLayerSideX = 400;
		final float firstLayerSideY = 50;
		final float secondLayerSideYOffset = 5;
		
		matchTextSidePos = new Vector2(firstLayerSideX, firstLayerSideY);
		
		final float colorsTextXOffset =  (colorsTextWidth - matchTextWidth) / 2;
		colorsTextSidePos = new Vector2(matchTextSidePos.x - colorsTextXOffset, matchTextSidePos.y - (colorsTextHeight / 2));
		
		final float shapesTextXOffset = (shapesTextWidth - matchTextWidth) / 2;
		shapesTextSidePos = new Vector2(matchTextSidePos.x - shapesTextXOffset, matchTextSidePos.y - (shapesTextHeight / 2));
		
		final float anyTextXOffset = (matchTextWidth - anyTextWidth) / 4;
		anyTextSidePos = new Vector2(matchTextSidePos.x + anyTextXOffset, matchTextSidePos.y - (anyTextHeight / 2));
		
		reset();
	}
	
	public void reset() {
		animationX = 0;
		animationY = 0;
		centerColor.a = 0;
		sideColor.a = 0;
	}
	
	public GroupAction animateCenterIn() {
		float duration = 0.5f;
		Interpolation interpolation = Interpolation.exp5Out;
		
		MoveAction moveAction = Pools.obtain(MoveAction.class);
		moveAction.moveYTo(this, animationCenterYOffset, 0);
		moveAction.set(duration, false, interpolation);
		
		ColorAction fadeAction = Pools.obtain(ColorAction.class);
		fadeAction.changeAlpha(centerColorable, 0, 1);
		fadeAction.set(duration, false, interpolation);
		
		GroupAction groupAction = Pools.obtain(GroupAction.class);
		groupAction.parallel();
		groupAction.add(moveAction);
		groupAction.add(fadeAction);

		drawCenter = true;
		return groupAction;
	}
	
	public GroupAction animateCenterOut() {
		float duration = 0.5f;
		Interpolation interpolation = Interpolation.exp5Out;
		
		MoveAction moveAction = Pools.obtain(MoveAction.class);
		moveAction.moveXTo(this, 0, animationCenterXOffset);
		moveAction.set(duration, false, interpolation);
		
		ColorAction fadeAction = Pools.obtain(ColorAction.class);
		fadeAction.changeAlpha(centerColorable, 1, 0);
		fadeAction.set(duration, false, interpolation);
		
		GroupAction groupAction = Pools.obtain(GroupAction.class);
		groupAction.parallel();
		groupAction.addListener(drawCenterOverListener);
		groupAction.add(moveAction);
		groupAction.add(fadeAction);
		
		drawCenter = true;
		return groupAction;
	}
	
	public ColorAction animateSideIn() {
		float duration = 0.5f;
		
		ColorAction fadeAction = Pools.obtain(ColorAction.class);
		fadeAction.changeAlpha(sideColorable, 0, 1);
		fadeAction.set(duration, false, Interpolation.exp5Out);
		
		drawSide = true;
		return fadeAction;
	}
	
	public ColorAction animateSideOut() {
		float duration = 0.5f;
		
		ColorAction fadeAction = Pools.obtain(ColorAction.class);
		fadeAction.addListener(drawSideOverListener);
		fadeAction.changeAlpha(sideColorable, 1, 0);
		fadeAction.set(duration, false, Interpolation.exp5Out);
		
		drawSide = true;
		return fadeAction;
	}
	
	public void draw(Batch batch, MatchType type) {
		if(drawCenter) {
			batch.setColor(centerColor);
			batch.draw(matchTextRegion, matchTextCenterPos.x - animationX, matchTextCenterPos.y + animationY, matchTextWidth, matchTextHeight);
			switch(type) {
				case SHAPES:
					batch.draw(shapesTextRegion, shapesTextCenterPos.x + animationX, shapesTextCenterPos.y - animationY, shapesTextWidth, shapesTextHeight);
					break;
				case COLORS:
					batch.draw(colorsTextRegion, colorsTextCenterPos.x + animationX, colorsTextCenterPos.y - animationY, colorsTextWidth, colorsTextHeight);
					break;
				case ANY:
					batch.draw(anyTextRegion, anyTextCenterPos.x + animationX, anyTextCenterPos.y - animationY, anyTextWidth, anyTextHeight);
					break;
			}
			batch.setColor(Color.WHITE);
		}
		
		if(drawSide) {
			batch.setColor(sideColor);
			batch.draw(matchTextRegion, matchTextSidePos.x, matchTextSidePos.y, matchTextWidth / 2, matchTextHeight / 2);
			switch(type) {
				case SHAPES:
					batch.draw(shapesTextRegion, shapesTextSidePos.x, shapesTextSidePos.y, shapesTextWidth / 2, shapesTextHeight / 2);
					break;
				case COLORS:
					batch.draw(colorsTextRegion, colorsTextSidePos.x, colorsTextSidePos.y, colorsTextWidth / 2, colorsTextHeight / 2);
					break;
				case ANY:
					batch.draw(anyTextRegion, anyTextSidePos.x, anyTextSidePos.y, anyTextWidth / 2, anyTextHeight / 2);
					break;
			}
			batch.setColor(Color.WHITE);
		}
	}

	@Override
	public void setX(float x) {
		animationX = x;
	}

	@Override
	public void setY(float y) {
		animationY = y;
	}

	@Override
	public float getX() {
		return animationX;
	}

	@Override
	public float getY() {
		return animationY;
	}
	
	private class DrawCenterOverListener extends ActionAdapter {
		@Override
		public void actionEnd() {
			drawCenter = false;
		}
	}
	
	private class DrawSideOverListener extends ActionAdapter{
		@Override
		public void actionEnd() {
			drawSide = false;
		}
	}

	private class SideColorable implements Colorable{
		@Override
		public void setColor(Color color) {
			sideColor.set(color);
		}

		@Override
		public Color getColor() {
			return sideColor;
		}
	}
	
	private class CenterColorable implements Colorable{
		@Override
		public void setColor(Color color) {
			centerColor.set(color);
		}

		@Override
		public Color getColor() {
			return centerColor;
		}
	}
	

}
