package org.programus.android.game._engine.core;

import java.util.HashMap;
import java.util.Map;

import org.programus.android.game._engine.utils.Const;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

/**
 * A class to represent a general game. 
 * Extend this class to implement the specific game. 
 * <p>
 * A game include some scenes, which stored in a map. And there are also some status. 
 * Every status must have a scene ({@linkplain GameScene}) to 
 * <ol>
 * 	<li>calculate and generate data for one frame; ({@linkplain GameScene#calcFrameData()}) </li>
 * 	<li>draw the frame. ({@linkplain GameScene#drawFrame(Canvas)}) </li>
 * </ol>
 * </p>
 * @author Programus
 *
 */
public class Game implements Const, Savable {
	/** The {@linkplain Context} of the android application */
	protected Context context; 
	/** The width of the game canvas */
	protected int w;
	/** The height of the game canvas */ 
	protected int h;
	/** The time of current frame (ms) */
	protected long currMsTime;
	/** The time of previous frame (ms) */
	protected long prevMsTime;
	
	private int status; 
	
	/** The map storing the scenes. Key will be a status and value is an instance of {@linkplain GameScene}.  */
	protected Map<Integer, GameScene> scenes = new HashMap<Integer, GameScene>(); 
	
	/**
	 * Constructor. 
	 * @param context the {@linkplain Context} of this android application. 
	 */
	public Game(Context context) {
		this.context = context; 
	}
	
	public Context getContext() {
		return context;
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

	/**
	 * A method to start the game. The method will be invoked to reset game too. 
	 * Normally reset objects here. 
	 * This is an empty method for this class. So need to be override. 
	 */
	public void start() {
	}
	
	/**
	 * A method to pause the game. 
	 * This is an empty method for this class. So need to be override. 
	 */
	public void pause() {
	}
	
	/* (non-Javadoc)
	 * @see org.programus.android.game._engine.core.Savable#save(android.content.SharedPreferences.Editor)
	 */
	@Override
	public void save(SharedPreferences.Editor editor) {
	}
	
	/* (non-Javadoc)
	 * @see org.programus.android.game._engine.core.Savable#load(android.content.SharedPreferences)
	 */
	@Override
	public boolean load(SharedPreferences pref) {
		return false; 
	}

	/**
	 * Draw current frame. 
	 * @param canvas
	 */
	public final void drawFrame(Canvas canvas) {
		GameScene currScene = scenes.get(status); 
		currScene.drawFrame(canvas); 
	}

	/**
	 * Set the current time to the game and scenes. 
	 * This method is invoked in {@linkplain GameSurfaceView#paint()}. 
	 * @param msTime
	 */
	public final void setPaintTime(long msTime) {
		for (GameScene scene : scenes.values()) {
			scene.setPaintTime(msTime); 
		}
	}

	/**
	 * Calculate and generate all data for current frame render. 
	 */
	public final void calcFrameData() {
		for (GameScene scene : scenes.values()) {
			scene.calcFrameData(); 
		}
	}
	
	/**
	 * Event handler for touch event. 
	 * @param event the event. 
	 * @return true if processed. Default: false. 
	 * @see {@linkplain android.view.View#onTouchEvent(MotionEvent)}
	 */
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

}
