package org.programus.android.game.dropball.objects;

import org.programus.android.game._engine.core.Game;

public class ObjectCollection {
	private Game game; 
	private Ball ball; 
	private BoardGroup bgroup; 
	
	public ObjectCollection(Game game) {
		this.game = game; 
		this.initObjects(); 
	}
	
	private void initObjects() {
		this.ball = new Ball(this.game); 
		this.bgroup = new BoardGroup(this.game); 
	}

	public Ball getBall() {
		return ball;
	}
	
	public BoardGroup getBoardGroup() {
		return bgroup;
	}

	public void reset() {
		this.ball.reset(); 
		this.bgroup.reset(); 
	}
}
