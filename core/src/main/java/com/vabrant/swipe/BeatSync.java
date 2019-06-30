package com.vabrant.swipe;

import com.badlogic.gdx.audio.Music;

public class BeatSync {
	
	private boolean firstBeat;
	public float bpm = 130;
	public float crotchet = 60f / bpm;
	public float songPosition;
	public float lastBeat = 0;
	public int currentBeat = 1;
	public int currentBar = 1;
	public BeatSyncListener listener;
	
	public void setListener(BeatSyncListener listener) {
		this.listener = listener;
	}
	
	public void reset() {
		firstBeat = false;
		songPosition = 0;
		lastBeat = 0;
		currentBeat = 1;
		currentBar = 1;
	}
	
	public void update(Music music, InfiniteShapeSystemController s) {
		if(!music.isPlaying()) return;
		
		if(songPosition > music.getPosition()) {
			reset();
		}
		
		songPosition = music.getPosition();
		
		if(!firstBeat) {
			firstBeat = true;
//			s.createSquare();
			if(listener != null) listener.currenteBeat(this, currentBeat);
		}
		
		if(songPosition > lastBeat + crotchet) {
			currentBeat++;
			
			if(listener != null) {
				listener.currenteBeat(this, currentBeat);
			}
			
			if(currentBeat == 4) {
				currentBar++;
				currentBeat = 0;
				if(listener != null) listener.currentBar(this, currentBar);
			}
			
			lastBeat += crotchet;
		}
		
	}
	
	public interface BeatSyncListener{
		public void currenteBeat(BeatSync beatSync, int beat);
		public void currentBar(BeatSync beatSync, int bar);
	}


}
