package org.programus.android.game._engine.core;

import android.graphics.Canvas;

public class GameScene {
	protected long currNanoTime;
	protected long prevNanoTime;
	protected long dt; 
	protected Game game; 
	
	public GameScene(Game game) {
		this.game = game; 
	}
	
	protected void calcFrameData() {
	}

	protected void drawFrame(Canvas canvas) {
	}

	protected void setPaintTime(long nanoTime) {
		this.prevNanoTime = this.currNanoTime;
		this.currNanoTime = nanoTime;
		dt = this.currNanoTime - this.prevNanoTime; 
	}
}
