package org.programus.android.game.dropball.objects;

import org.programus.android.game._engine.core.Game;

public class ObjectCollection {
	private Game game; 
	
	private Ball ball; 
	
	public ObjectCollection(Game game) {
		this.game = game; 
		this.initObjects(); 
	}
	
	private void initObjects() {
		this.ball = new Ball(this.game); 
	}

	public Ball getBall() {
		return ball;
	}

	public void reset() {
		this.ball.reset(); 
	}
}
