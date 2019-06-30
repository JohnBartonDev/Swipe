package com.vabrant.swipe;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class StarSystemController {
	
	public enum StarSystemType {
		STATIC,
		MOVING
	}
	public StarSystemType type;
	
	private final int maxStars = 300;
	private Array<Star> stars;
	private Array<Star> staticStars;
	private TextureRegion starRegion;
	private StaticStarSystem staticStarSystem;
	private MovingStarSystem movingStarSystem;
	private StarSystem currentStarSystem;
	
	public StarSystemController(TextureAtlas gameAtlas) {
		starRegion = gameAtlas.findRegion("circle");
		
		staticStarSystem = new StaticStarSystem();
		movingStarSystem = new MovingStarSystem();
		
		type = StarSystemType.MOVING;
		
		staticStars = new Array<>(100);
		for(int i = 0; i < 100; i++) {
			Star star = new Star();
			staticStarSystem.init(star);
			staticStars.add(star);
		}
		
		stars = new Array<>(maxStars);
		for(int i = 0; i < maxStars; i++) {
			stars.add(new Star());
		}
		
		setStarSystemType(StarSystemType.MOVING);
		
		for(int i = 0; i < stars.size; i++) {
			currentStarSystem.init(stars.get(i));
		}
	}
	
	public void setStarSystemType(StarSystemType type) {
		switch(type) {
			case STATIC:
				currentStarSystem = staticStarSystem;
				break;
			case MOVING:
				currentStarSystem = movingStarSystem;
				break;
		}
	}
	
	public void update(float delta) {
		for(int i = 0; i < stars.size; i++) {
			currentStarSystem.update(stars.get(i));
		}
	}
	
	public void draw(Batch batch) {
		for(int i = 0; i < staticStars.size; i++) {
			staticStarSystem.draw(batch, starRegion, staticStars.get(i));
		}
		
		for(int i = 0; i < maxStars; i++) {
			currentStarSystem.draw(batch, starRegion, stars.get(i));
		}
	}

}
