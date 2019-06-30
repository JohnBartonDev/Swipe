package com.vabrant.swipe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Utils {
	
	public static float map(float value, float min1, float max1, float min2, float max2){
		return  min2 + (max2 - min2) * ((value - min1) / (max1 - min1));
	}
	
	public static Color createColor(float r, float g, float b, float a) {
		return new Color(r / 255f, g / 255f, b / 255f, a);
	}
	
	public static ShaderProgram createShader(String v, String f) {
		final String vert = Gdx.files.internal(v).readString();
		final String frag = Gdx.files.internal(f).readString();
		
		ShaderProgram program = new ShaderProgram(vert, frag);
		
		if(!program.isCompiled()) {
			throw new GdxRuntimeException(program.getLog());
		}
		return program;
	}

}
