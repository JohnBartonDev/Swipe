package com.vabrant.swipe;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class MatchInput extends InputAdapter{
	
	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
			case Keys.LEFT:
				break;
		}
		return super.keyDown(keycode);
	}

}
