package org.programus.android.game.dropball.objects;

import org.programus.android.game._engine.core.Savable;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game.dropball.DroppingBallGame;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * A collection of the objects in game. 
 * It mainly include a ball, a board group and a score inside. 
 * @author Programus. 
 *
 */
public class ObjectCollection implements Savable, Const {
	private DroppingBallGame game; 
	private Ball ball; 
	private BoardGroup bgroup; 
	private ScoreStorage score;
	
	public ObjectCollection(DroppingBallGame game) {
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
