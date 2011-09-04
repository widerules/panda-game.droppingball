package org.programus.android.game._engine.core;

import android.graphics.Canvas;

/**
 * A class to represent the general game scene. Extends this class to implements the scenes for specified game. 
 * @author Programus
 *
 */
public class GameScene {
	/** The system time of current frame. */
	protected long currMsTime;
	/** The system time of previous frame. */
	protected long prevMsTime;
	/** The time between previous frame and current frame. */
	protected long dt; 
	/** The game */
	protected Game game; 
	
	public GameScene(Game game) {
		this.game = game; 
	}
	
	/**
	 * Invoked before render current frame to generate data for current frame. 
	 * Need to be override. 
	 */
	protected void calcFrameData() {
	}

	/**
	 * Draw current frame. 
	 * @param canvas
	 */
	protected void drawFrame(Canvas canvas) {
	}

	protected void setPaintTime(long msTime) {
		this.prevMsTime = this.currMsTime;
		this.currMsTime = msTime;
		dt = this.currMsTime - this.prevMsTime; 
	}
}
