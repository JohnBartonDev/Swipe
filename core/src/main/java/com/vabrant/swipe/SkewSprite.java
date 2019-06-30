package com.vabrant.swipe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class SkewSprite {

	private float invTexWidth;
	private float invTexHeight;
	private float x;
	private float y;
	private float width;
	private float height;
	private final float[] vertices = new float[20];
	private Texture texture;
	private Color color = new Color(Color.WHITE);
	
	public SkewSprite(Texture texture) {
//		texture = new Texture(texture.getTextureData());
		this.texture = texture;
		invTexWidth = 1.0f / this.texture.getWidth();
		invTexHeight = 1.0f / this.texture.getHeight();
		width = texture.getWidth() / 2;
		height = texture.getHeight() / 2;
		setColor(1, 1, 1, 1);
	}
	
	public void setColor(float r, float g, float b, float a){
		this.color.set(r, g, b, a);
		float c = Color.toFloatBits(r, g, b, a);
		float[] vertices = this.vertices;
		vertices[Batch.C1] = c;
		vertices[Batch.C2] = c;
		vertices[Batch.C3] = c;
		vertices[Batch.C4] = c;
	}
	
	public void draw(Batch batch) {
		batch.draw(texture, getVertices(), 0, 20);
	}
	
	public float[] getVertices(){
		float[] vertices = this.vertices;
		
		float x2 = x + width;
		float y2 = y + height;
		float srcX = 0;
		float srcY = 0;
		float srcWidth = texture.getWidth();
		float srcHeight = texture.getHeight();
		float u = srcX * invTexWidth;
		float v = srcY * invTexHeight;
		float v2 = (srcY + srcHeight) * invTexHeight;
		float u2 = (srcX + srcWidth) * invTexWidth;
		
		//bottom left
		vertices[Batch.X1] = x - 60;
		vertices[Batch.Y1] = y + 30;
		vertices[Batch.U1] = u;
		vertices[Batch.V1] = v;
		
		//top left
		vertices[Batch.X2] = x;
		vertices[Batch.Y2] = y2;
		vertices[Batch.U2] = u;
		vertices[Batch.V2] = v2;
		
		//top right
		vertices[Batch.X3] = x2;
		vertices[Batch.Y3] = y2;
		vertices[Batch.U3] = u2;
		vertices[Batch.V3] = v2;
		
		//bottom right
		vertices[Batch.X4] = x2 + 60;
		vertices[Batch.Y4] = y + 60;
		vertices[Batch.U4] = u2;
		vertices[Batch.V4] = v;
		
		return vertices;
	}
	
}
