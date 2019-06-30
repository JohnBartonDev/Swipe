package com.vabrant.swipe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.vabrant.actionsystem.ActionController;
import com.vabrant.actionsystem.DelayAction;
import com.vabrant.actionsystem.GroupAction;
import com.vabrant.actionsystem.Pools;
import com.vabrant.actionsystem.RepeatAction;
import com.vabrant.actionsystem.Rotatable;
import com.vabrant.actionsystem.RotateAction;
import com.vabrant.actionsystem.RunnableAction;

public class InfiniteShapeSystemController implements Rotatable{
	
	private final float maxScale = 1.3f;
	private float rotation = 45;
	private float speed = 1f;
	private final float creationDuration = 0.7f;
	private float creationTimer = creationDuration;
	private final TextureRegion guttedSquareRegion;
	private final Pool<InfiniteSquare> pool;
	private final Array<InfiniteSquare> inUse;
	private final ActionController actionController;
	private final Color color;
	private final CreateSquareRunnable createSquareRunnable;
	
	public InfiniteShapeSystemController(ActionController actionController, TextureAtlas gameAtlas) {
		createSquareRunnable = new CreateSquareRunnable();
		color = new Color(Constants.PINK);
		this.actionController = actionController;
		pool = createPool();
		guttedSquareRegion = gameAtlas.findRegion("guttedSquare");
		inUse = new Array<>(5);
//		checkForNewSquare2(actionController);
	}
	
	private Pool<InfiniteSquare> createPool() {
		return new Pool<InfiniteSquare>() {
			@Override
			protected InfiniteSquare newObject() {
				return new InfiniteSquare();
			}
		};
	}
	
	private void checkForNewSquare2() {
		
		DelayAction delay = Pools.obtain(DelayAction.class);
		delay.set(creationDuration);
		
		RunnableAction runnable = Pools.obtain(RunnableAction.class);
		runnable.set(new Runnable() {
			@Override
			public void run() {
				InfiniteSquare square = pool.obtain();
//				square.rotation = 45;
				inUse.add(square);
			}
		});
		
		GroupAction groupAction = Pools.obtain(GroupAction.class);
		groupAction.sequence();
		groupAction.add(delay);
		groupAction.add(runnable);
		
		RepeatAction repeat = Pools.obtain(RepeatAction.class);
		repeat.set(groupAction);
		repeat.setContinuous();
		
		actionController.addAction(repeat);
	}
	
	private void checkForNewSquare(float delta) {
		if((creationTimer += delta) > creationDuration) {
			creationTimer = 0;
			InfiniteSquare square = pool.obtain();
//			square.rotation = 45;
			inUse.add(square);
		}
	}
	
	public void setColor(Color color) {
		this.color.set(color);
	}
	 
	public void rotate(float duration, float amount) {
		RotateAction rotateAction = Pools.obtain(RotateAction.class);
		rotateAction.capAt360(true);
		rotateAction.rotateBy(this, amount);
		rotateAction.set(duration, false, Interpolation.exp5Out);
		
		actionController.addAction(rotateAction);
	}
	
	public void randomizeColor() {
		switch(MathUtils.random(1, 4)) {
		case 1:
			setColor(Constants.YELLOW);
			break;
		case 2:
			setColor(Constants.PINK);
			break;
		case 3:
			setColor(Constants.BLUE);
			break;
		case 4:
			setColor(Constants.PURPLE);
			break;
	}
	}
	
	public void createSquare() {
		InfiniteSquare square = pool.obtain();
//		square.rotation = 45;
		inUse.add(square);
	}
	
	public void createMultipleSquares(float amount, float offset) {
		GroupAction groupAction = Pools.obtain(GroupAction.class);
		groupAction.parallel();
		
		RunnableAction a = Pools.obtain(RunnableAction.class);
		a.set(createSquareRunnable);
		
		groupAction.add(a);
		
		for(int i = 1; i < amount; i++) {
			DelayAction delay = Pools.obtain(DelayAction.class);
			delay.set(offset * i);
			
			RunnableAction action = Pools.obtain(RunnableAction.class);
			action.set(createSquareRunnable);
			
			GroupAction group = Pools.obtain(GroupAction.class);
			group.sequence();
			group.add(delay);
			group.add(action);
			
			groupAction.add(group);
		}
		
		actionController.addAction(groupAction);
	}
	
	private void updateAndRemoveSquares(float delta) {
		for(int i = inUse.size - 1; i >= 0; i--) {
			InfiniteSquare square = inUse.get(i);
			
			if(square.scale >= maxScale) {
				pool.free(inUse.removeIndex(i));
			}
			else {
				float percent = 0;
				percent = square.scale >= maxScale ? 1 : Utils.map(square.scale, 0, maxScale, 0, 1);
				percent = Interpolation.fade.apply(percent);
				square.alpha = MathUtils.lerp(1, 0, percent);
				
				square.timer += (speed * delta);
				
				percent = square.timer >= 2f ? 1 : square.timer / 2f;
				percent = Interpolation.exp5Out.apply(percent);
				square.scale = MathUtils.lerp(0, 1.3f, percent);
				
//				percent = square.rotation >= 45f ? 1 : Utils.map(square.scale, 0, maxScale, 0, 1f);
//				percent = Interpolation.circleOut.apply(percent);
//				square.rotation = MathUtils.lerp(0, 45f, percent);
			}
		}
	}
	
	public void update(float delta) {
//		checkForNewSquare(delta);
		updateAndRemoveSquares(delta);
	}
	
	public void draw(Batch batch) {
		for(int i = 0, size = inUse.size; i < size; i++) {
			InfiniteSquare square = inUse.get(i);
			square.draw(batch, guttedSquareRegion, color, rotation);
		}
	}

	@Override
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	@Override
	public float getRotation() {
		return rotation;
	}
	
	private class CreateSquareRunnable implements Runnable{
		@Override
		public void run() {
			createSquare();
		}
	}

}
