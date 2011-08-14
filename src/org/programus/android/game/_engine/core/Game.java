package org.programus.android.game._engine.core;

import java.util.HashMap;
import java.util.Map;

import org.programus.android.game._engine.utils.Const;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

public class Game implements Const {
	protected Context context; 
	protected int w;
	protected int h;
	protected long currNanoTime;
	protected long prevNanoTime;
	
	private int status; 
	protected Map<Integer, GameScene> scenes = new HashMap<Integer, GameScene>(); 
	
	public Game(Context context) {
		this.context = context; 
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
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
	
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

}
