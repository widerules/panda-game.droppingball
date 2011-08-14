package org.programus.android.game.core;

import java.util.HashMap;
import java.util.Map;

import org.programus.android.game.Const;
import org.programus.android.game.drop.GameStatus;

import android.graphics.Canvas;
import android.util.Log;

public class Game implements Const {
	
	protected int w;
	protected int h;
	protected long currNanoTime;
	protected long prevNanoTime;
	
	private GameStatus status; 
	protected Map<GameStatus, GameScene> scenes = new HashMap<GameStatus, GameScene>(); 

	public GameStatus getStatus() {
		return status;
	}

	public void setStatus(GameStatus status) {
		this.status = status;
	}

	public void setSize(int width, int height) {
		this.w = width; 
		this.h = height;
		Log.d(TAG, "w=" + w + ",h=" + h); 
	}
	
	public int getW() {
		return this.w;
	}
	
	public int getH() {
		return this.h; 
	}

	public void start() {
	}

	public final void drawFrame(Canvas canvas) {
		GameScene currScene = scenes.get(status); 
		currScene.drawFrame(canvas); 
	}

	public final void setPaintTime(long nanoTime) {
		for (GameScene scene : scenes.values()) {
			scene.setPaintTime(nanoTime); 
		}
	}

	public final void calcFrameData() {
		for (GameScene scene : scenes.values()) {
			scene.calcFrameData(); 
		}
	}

}
