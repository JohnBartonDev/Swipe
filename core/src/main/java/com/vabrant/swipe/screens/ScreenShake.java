package com.vabrant.swipe.screens;

import com.vabrant.actionsystem.ActionAdapter;
import com.vabrant.actionsystem.Pools;
import com.vabrant.actionsystem.Shakable;
import com.vabrant.actionsystem.ShakeAction;

public class ScreenShake extends ActionAdapter implements Shakable{
	
	private boolean shakeAll;
	private boolean shakeWorld;
	private float shakeX;
	private float shakeY;
	private final String actionName = "screenShake";
	
	public boolean shakeWorld() {
		return shakeWorld;
	}
	
	public boolean shakeAll() {
		return shakeAll;
	}

	@Override
	public void setShakeX(float x) {
		shakeX = x;
	}

	@Override
	public void setShakeY(float y) {
		shakeY = y;
	}

	@Override
	public void setShakeAngle(float angle) {
		
	}

	@Override
	public float getShakeX() {
		return shakeX;
	}

	@Override
	public float getShakeY() {
		return shakeY;
	}

	@Override
	public float getShakeAngle() {
		return 0;
	}
	
	@Override
	public void actionEnd() {
		shakeWorld = false;
		shakeAll = false;
	}
	
	public void shakeAll(GameScreen screen, float duration, float amount, float multiplier) {
		shakeAll = true;
		shakeWorld = false;
		shake(screen, duration, amount, multiplier);
	}
	
	public void shakeWorld(GameScreen screen, float duration, float amount, float multiplier) {
		shakeWorld = true;
		shakeAll = false;
		shake(screen, duration, amount, multiplier);
	}
	
	private void shake(GameScreen screen, float duration, float amount, float multiplier) {
		ShakeAction action = (ShakeAction)screen.getActionByName(actionName);
		if(action == null) {
			action = Pools.obtain(ShakeAction.class);
			action.setName(actionName);
			action.addListener(this);
			action.set(duration, false, null);
			action.shake(this, amount, 0, multiplier);
			screen.addAction(action);
		}
		else {
			action.restart();
			action.shake(this, amount, 0, multiplier);
		}
	}

}
