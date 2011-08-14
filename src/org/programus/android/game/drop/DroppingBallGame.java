package org.programus.android.game.drop;

import org.programus.android.game.Const;
import org.programus.android.game.core.Game;
import org.programus.android.game.drop.scene.PausedScene;

public class DroppingBallGame extends Game implements Const {
	public DroppingBallGame() {
		this.setStatus(GameStatus.PAUSED); 
		this.scenes.put(GameStatus.PAUSED, new PausedScene(this)); 
	}
}
