package org.programus.android.game.dropball.scene;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.core.GameScene;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game.dropball.DroppingBallGame;

import android.graphics.Canvas;
import android.util.Log;

public class PlayingScene extends GameScene implements Const {
	private DroppingBallGame dGame; 
	private int bkColor;
	
	public PlayingScene(Game game) {
		super(game);
		dGame = (DroppingBallGame) game; 
		bkColor = this.game.getContext().getResources().getColor(R.color.backColor); 
	}
	
	@Override
	protected void drawFrame(Canvas canvas) {
		canvas.drawColor(this.bkColor); 
		dGame.getObjects().getBall().draw(canvas); 
		dGame.getObjects().getBoardGroup().draw(canvas); 
	}

	@Override
	protected void calcFrameData() {
		if (dGame.getStatus() == dGame.STATUS_PLAYING) {
			Log.d(TAG, "dt=" + dt); 
			dGame.getObjects().getBall().stepCalc(dt); 
			dGame.getObjects().getBoardGroup().stepCalc(dt); 
		}
	}
}
