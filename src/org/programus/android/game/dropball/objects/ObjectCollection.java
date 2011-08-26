package org.programus.android.game.dropball.objects;

import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.core.Savable;
import org.programus.android.game._engine.utils.Const;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ObjectCollection implements Savable, Const {
	private Game game; 
	private Ball ball; 
	private BoardGroup bgroup; 
	private ScoreStorage score;
	
	public ObjectCollection(Game game) {
		this.game = game; 
		this.initObjects(); 
	}
	
	private void initObjects() {
		this.ball = new Ball(this.game); 
		this.bgroup = new BoardGroup(this.game); 
		this.score = new ScoreStorage(this.game); 
	}

	public Ball getBall() {
		return ball;
	}
	
	public BoardGroup getBoardGroup() {
		return bgroup;
	}
	
	public ScoreStorage getScore() {
		return score;
	}

	public void reset() {
		this.ball.reset(); 
		this.bgroup.reset(); 
		this.score.reset(); 
	}

	@Override
	public boolean load(SharedPreferences pref) {
		return this.ball.load(pref) && this.bgroup.load(pref) && this.score.load(pref);
	}

	@Override
	public void save(Editor editor) {
		this.ball.save(editor); 
		this.bgroup.save(editor); 
		this.score.save(editor); 
	}
}
