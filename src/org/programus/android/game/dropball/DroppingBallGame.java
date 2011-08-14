package org.programus.android.game.dropball;

import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game.dropball.scene.PausedScene;

public class DroppingBallGame extends Game implements Const {
	public DroppingBallGame() {
		this.setStatus(GameStatus.PAUSED); 
		this.scenes.put(GameStatus.PAUSED, new PausedScene(this)); 
	}
}
