package com.vabrant.swipe;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * 
 * @author Nathan Sweet
 */
public class InputMultiplexer implements InputProcessor{
	
	private Vector2 touch;
	private Viewport viewport;
	private Array<InputProcessor> processors;
	
	public InputMultiplexer(Viewport viewport) {
		this.viewport = viewport;
		touch = new Vector2();
		processors = new Array<>();
	}
	
	public void addProcessor(InputProcessor... processors) {
		for (int i = 0; i < processors.length; i++)
			this.processors.add(processors[i]);
	}
	
	public void addProcessor (int index, InputProcessor processor) {
		if (processor == null) throw new NullPointerException("processor cannot be null");
		processors.insert(index, processor);
	}

	public void removeProcessor (int index) {
		processors.removeIndex(index);
	}

	public void addProcessor (InputProcessor processor) {
		if (processor == null) throw new NullPointerException("processor cannot be null");
		processors.add(processor);
	}

	public void removeProcessor (InputProcessor processor) {
		processors.removeValue(processor, true);
	}

	/** @return the number of processors in this multiplexer */
	public int size () {
		return processors.size;
	}

	public void clear () {
		processors.clear();
	}

	public void setProcessors (Array<InputProcessor> processors) {
		this.processors = processors;
	}

	public Array<InputProcessor> getProcessors () {
		return processors;
	}

	public boolean keyDown (int keycode) {
		for (int i = 0, n = processors.size; i < n; i++)
			if (processors.get(i).keyDown(keycode)) return true;
		return false;
	}

	public boolean keyUp (int keycode) {
		for (int i = 0, n = processors.size; i < n; i++)
			if (processors.get(i).keyUp(keycode)) return true;
		return false;
	}

	public boolean keyTyped (char character) {
		for (int i = 0, n = processors.size; i < n; i++)
			if (processors.get(i).keyTyped(character)) return true;
		return false;
	}

	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		touch = viewport.unproject(touch.set(screenX, screenY));
		for (int i = 0, n = processors.size; i < n; i++)
			if (processors.get(i).touchDown((int)touch.x, (int)touch.y, pointer, button)) return true;
		return false;
	}

	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		touch = viewport.unproject(touch.set(screenX, screenY));
		for (int i = 0, n = processors.size; i < n; i++)
			if (processors.get(i).touchUp((int)touch.x , (int)touch.y , pointer, button)) return true;
		return false;
	}

	public boolean touchDragged (int screenX, int screenY, int pointer) {
		touch = viewport.unproject(touch.set(screenX, screenY));
		for (int i = 0, n = processors.size; i < n; i++)
			if (processors.get(i).touchDragged((int)touch.x, (int)touch.y, pointer)) return true;
		return false;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		touch = viewport.unproject(touch.set(screenX, screenY));
		for (int i = 0, n = processors.size; i < n; i++)
			if (processors.get(i).mouseMoved((int)touch.x, (int)touch.y)) return true;
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		for (int i = 0, n = processors.size; i < n; i++)
			if (processors.get(i).scrolled(amount)) return true;
		return false;
	}

}
