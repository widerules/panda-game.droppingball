package org.programus.android.game._engine.ui;

import java.lang.reflect.Constructor;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game._engine.utils.GameUtilities;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

/**
 * A surface view to give the game a canvas. 
 * @author Programus
 *
 */
public class GameSurfaceView extends SurfaceView implements Runnable, Callback, Const {
	private int restTime; 
	private SurfaceHolder sfh; 
	private boolean running; 
	private Game game;
	
	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.restTime = context.getResources().getInteger(R.integer.restTime); 
		this.setKeepScreenOn(true); 
		this.sfh = this.getHolder();
		this.sfh.addCallback(this); 
		String gameClassName = context.getResources().getString(R.string.gameClass); 
		try {
			Class<?> gameClass = this.getClass().getClassLoader().loadClass(gameClassName);
			Constructor<?> constructor = gameClass.getConstructor(Context.class); 
			this.game = (Game) constructor.newInstance(context); 
		} catch (Exception e) {
			Toast.makeText(context, R.string.gameInitErrorMessage, Toast.LENGTH_LONG).show(); 
			Log.d(TAG, "Exception when init game class.", e); 
			GameUtilities.ExitApplication(context); 
		}
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
			this.paint(); 
			if (restTime > 0) {
				try {
					Thread.sleep(restTime);
				} catch (InterruptedException e) { } 
			}
		}
	}

	private void paint() {
		Canvas canvas = this.sfh.lockCanvas(); 
		if (this.game != null) {
			this.game.setPaintTime(System.currentTimeMillis()); 
			this.game.calcFrameData(); 
			this.game.drawFrame(canvas); 			
		}
		
		sfh.unlockCanvasAndPost(canvas); 
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "TouchedEvent:" + event); 
		
		return game.onTouchEvent(event);
	}
}
