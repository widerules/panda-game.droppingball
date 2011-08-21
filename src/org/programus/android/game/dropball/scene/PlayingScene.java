package org.programus.android.game.dropball.scene;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.core.GameScene;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game.dropball.DroppingBallGame;
import org.programus.android.game.dropball.objects.Ball;
import org.programus.android.game.dropball.objects.BoardGroup;

import android.graphics.Canvas;
import android.graphics.RectF;

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
//			Log.d(TAG, "dt=" + dt); 
			Ball ball = dGame.getObjects().getBall(); 
			BoardGroup bgroup = dGame.getObjects().getBoardGroup(); 
			bgroup.stepCalc(dt); 
			ball.updageBoardGroup(bgroup); 
			ball.stepCalc(dt); 
			
			if (this.gameOver(ball)) {
				dGame.setStatus(dGame.STATUS_PAUSED); 
				game.start(); 
			}
		}
	}
	
	private boolean gameOver(Ball ball) {
		RectF bounds = ball.getBounds(); 
		int h = dGame.getH(); 
		return (bounds.bottom < -(h >> 2) || bounds.top > h + (h >> 2));
	}
}
