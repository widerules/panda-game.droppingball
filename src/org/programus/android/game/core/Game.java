package org.programus.android.game.core;

import android.graphics.Canvas;

public class Game {
	
	protected int w;
	protected int h;
	private long currNanoTime;
	private long prevNanoTime;

	public void setSize(int width, int height) {
		this.w = width; 
		this.h = height;
	}

	public void start() {
	}

	public void drawFrame(Canvas canvas) {
	}

	public void setPaintTime(long nanoTime) {
		this.prevNanoTime = this.currNanoTime;
		this.currNanoTime = nanoTime;
	}

	public void calcFrameData() {
		long dt = this.currNanoTime - this.prevNanoTime; 
		
	}

}
