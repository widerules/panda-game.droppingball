package org.programus.android.game._engine.ui;

import java.lang.reflect.Constructor;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.core.GameContext;
import org.programus.android.game._engine.core.Savable;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game._engine.utils.GameUtilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

/**
 * A surface view to give the game a canvas. This is the main view of this application. 
 * @author Programus
 *
 */
public class GameSurfaceView extends SurfaceView implements Runnable, Callback, Savable, Const {
	/**
	 * Sleep time in thread while loop. 
	 */
	private int restTime; 
	private SurfaceHolder sfh; 
	/** Thread running flag. Use to stop the thread. */
	private boolean running; 
	/** Thread paint pausing flag. Not paint if in pausing */
	private boolean pausing; 
	/** The game */
	private Game game;
	
	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.restTime = context.getResources().getInteger(R.integer.restTime); 
		this.setKeepScreenOn(true); 
		this.sfh = this.getHolder();
		this.sfh.addCallback(this); 
		// get game class from resource. 
		String gameClassName = context.getResources().getString(R.string.gameClass); 
		try {
			// create game instance. 
			Class<?> gameClass = this.getClass().getClassLoader().loadClass(gameClassName);
			Constructor<?> constructor = gameClass.getConstructor(Context.class); 
			this.game = (Game) constructor.newInstance(context); 
		} catch (Exception e) {
			// if failed. 
			Toast.makeText(context, R.string.gameInitErrorMessage, Toast.LENGTH_LONG).show(); 
			Log.d(TAG, "Exception when init game class.", e); 
			// exit the whole application. 
			GameUtilities.ExitApplication(context); 
		}
		// put game instance into the Game Context (not context of android application). 
		GameContext.getInstance().put(GameContext.GAME_VIEW, this); 
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		new Thread(this, "Main Paint Thread").start(); 
		this.game.setSize(this.getWidth(), this.getHeight()); 
		this.game.start(); 
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.running = false; 
	}

	@Override
	public void run() {
		this.running = true;
		while(this.running) {
			if (!this.pausing) {
				this.paint(); 
			}
			if (restTime > 0) {
				try {
					Thread.sleep(restTime);
				} catch (InterruptedException e) { } 
			}
		}
	}

	private synchronized void paint() {
		Canvas canvas = this.sfh.lockCanvas(); 
		if (this.game != null) {
			this.game.setPaintTime(System.currentTimeMillis()); 
			this.game.calcFrameData(); 
			this.game.drawFrame(canvas); 			
		}
		
		sfh.unlockCanvasAndPost(canvas); 
	}
	
	public void setPausing(boolean pausing) {
		this.pausing = pausing; 
	}
	
	public boolean isPausing() {
		return this.pausing; 
	}
	
	public void pauseGame() {
		this.game.pause(); 
	}
	
	public synchronized void save(SharedPreferences.Editor editor) {
		boolean p = this.isPausing(); 
		this.setPausing(true); 
		this.game.save(editor); 
		this.setPausing(p); 
		Log.d(TAG, "Saved"); 
	}
	
	public synchronized boolean load(SharedPreferences pref) {
		boolean p = this.isPausing(); 
		this.setPausing(true); 
		boolean ret = this.game.load(pref); 
		Log.d(TAG, "loaded"); 
		this.setPausing(p); 
		return ret; 
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "TouchedEvent:" + event); 
		return game.onTouchEvent(event);
	}
}
