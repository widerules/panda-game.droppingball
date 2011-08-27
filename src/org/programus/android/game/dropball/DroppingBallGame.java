package org.programus.android.game.dropball;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.core.GameScene;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game.dropball.objects.ObjectCollection;
import org.programus.android.game.dropball.scene.PausedScene;
import org.programus.android.game.dropball.scene.PlayingScene;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;

public class DroppingBallGame extends Game implements Const {
	public final int STATUS_NEW; 
	public final int STATUS_PAUSED; 
	public final int STATUS_PLAYING;
	
	private boolean useLoadedData; 
	private ObjectCollection oc; 
	
	public DroppingBallGame(Context context) {
		super(context); 
		this.oc = new ObjectCollection(this); 
		
		STATUS_NEW = context.getResources().getInteger(R.integer.NEW); 
		STATUS_PAUSED = context.getResources().getInteger(R.integer.PAUSED); 
		STATUS_PLAYING = context.getResources().getInteger(R.integer.PLAYING); 
		this.setStatus(STATUS_NEW); 
		GameScene pausedScene = new PausedScene(this); 
		this.scenes.put(STATUS_NEW, pausedScene); 
		this.scenes.put(STATUS_PAUSED, pausedScene); 
		this.scenes.put(STATUS_PLAYING, new PlayingScene(this)); 
	}
	
	public ObjectCollection getObjects() {
		return this.oc; 
	}
	
	@Override
	public void start() {
		if (!useLoadedData) {
			this.setStatus(STATUS_NEW); 
			this.oc.reset(); 
		} else {
			this.setStatus(this.oc.getScore().getScore() == 0 ? STATUS_NEW : STATUS_PAUSED); 
			useLoadedData = false; 
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (this.getStatus() == STATUS_PLAYING) {
				this.setStatus(STATUS_PAUSED); 
			} else {
				this.setStatus(STATUS_PLAYING); 
			}
		}
		return true; 
	}
	
	@Override
	public void save(SharedPreferences.Editor editor) {
		this.oc.save(editor); 
	}
	
	@Override
	public boolean load(SharedPreferences pref) {
		this.useLoadedData = this.oc.load(pref); 
//		if (this.useLoadedData && this.oc.getScore().getScore() == 0) {
//			this.setStatus(STATUS_PAUSED); 
//		} else {
//			this.setStatus(STATUS_NEW); 
//		}
		return this.useLoadedData; 
	}

	public boolean isUseLoadedData() {
		return useLoadedData;
	}

	public void setUseLoadedData(boolean useLoadedData) {
		this.useLoadedData = useLoadedData;
	}
	
	@Override
	public void pause() {
		this.setStatus(STATUS_PAUSED); 
	}
}
