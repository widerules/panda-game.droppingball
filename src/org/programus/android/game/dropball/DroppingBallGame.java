package org.programus.android.game.dropball;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game.dropball.scene.PausedScene;
import org.programus.android.game.dropball.scene.PlayingScene;

import android.content.Context;
import android.view.MotionEvent;

public class DroppingBallGame extends Game implements Const {
	public final int STATUS_PAUSED; 
	public final int STATUS_PLAYING;
	public DroppingBallGame(Context context) {
		super(context); 
		STATUS_PAUSED = context.getResources().getInteger(R.integer.PAUSED); 
		STATUS_PLAYING = context.getResources().getInteger(R.integer.PLAYING); 
		this.setStatus(STATUS_PAUSED); 
		this.scenes.put(STATUS_PAUSED, new PausedScene(this)); 
		this.scenes.put(STATUS_PLAYING, new PlayingScene(this)); 
	}
	
	@Override
	public void start() {
		this.setStatus(STATUS_PAUSED); 
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (this.getStatus() == STATUS_PAUSED) {
				this.setStatus(STATUS_PLAYING); 
			} else {
				this.setStatus(STATUS_PAUSED); 
			}
		}
		return true; 
	}
}
