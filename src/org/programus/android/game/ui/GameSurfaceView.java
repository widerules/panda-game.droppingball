package org.programus.android.game.ui;

import org.programus.android.game.Const;
import org.programus.android.game.core.Game;
import org.programus.android.game.drop.DroppingBallGame;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * A surface view to give the game a canvas. 
 * @author Programus
 *
 */
public class GameSurfaceView extends SurfaceView implements Runnable, Callback, Const {
	private final static int REST = 5;
	private SurfaceHolder sfh; 
	private boolean running; 
	private Game game;
	
	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setKeepScreenOn(true); 
		this.sfh = this.getHolder();
		this.sfh.addCallback(this); 
		this.game = new DroppingBallGame(); 
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
			if (REST > 0) {
				try {
					Thread.sleep(REST);
				} catch (InterruptedException e) { } 
			}
		}
	}

	private void paint() {
		Canvas canvas = this.sfh.lockCanvas(); 
		
		this.game.setPaintTime(System.nanoTime()); 
		this.game.calcFrameData(); 
		this.game.drawFrame(canvas); 
		
		sfh.unlockCanvasAndPost(canvas); 
	}
}
