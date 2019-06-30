package com.vabrant.swipe.screens;

import com.badlogic.gdx.math.Interpolation;
import com.vabrant.actionsystem.ActionAdapter;
import com.vabrant.actionsystem.Pools;
import com.vabrant.actionsystem.ZoomAction;
import com.vabrant.actionsystem.Zoomable;

public class ScreenZoom extends ActionAdapter implements Zoomable{
	
	private boolean zoomAll;
	private boolean zoomWorld;
	private float zoom;
	private final String actionName = "zoomAction";
	
	public boolean zoomWorld() {
		return zoomWorld;
	}
	
	public boolean zoomAll() {
		return zoomAll;
	}
	
	@Override
	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
	@Override
	public float getZoom() {
		return zoom;
	}
	
	@Override
	public void actionEnd() {
		zoomWorld = false;
		zoomAll = false;
	}
	
	@Override
	public void actionKill() {
		zoomWorld = false;
		zoomAll = false;
		zoom = 1;
	}
	
	public void zoomWorld(GameScreen screen, float duration, float to) {
		zoomWorld = true;
		zoomAll = false;
		zoom(screen, duration, to);
	}
	
	public void zoomAll(GameScreen screen, float duration, float to) {
		zoomAll = true;
		zoomWorld = false;
		zoom(screen, duration, to);
	}
	
	private void zoom(GameScreen screen, float duration, float to) {
		ZoomAction action = (ZoomAction)screen.getActionByName(actionName);
		if(action == null) {
			action = Pools.obtain(ZoomAction.class);
			action.setName(actionName);
			action.addListener(this);
			action.zoomTo(this, 1, to);
			action.set(duration, true, Interpolation.circleOut);
			screen.addAction(action);
		}
		else {
			action.restart();
			action.zoomTo(this, 1, to);
		}
	}
	

}
