package org.programus.android.game.dropball.scene;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.core.GameScene;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game.dropball.DroppingBallGame;
import org.programus.android.game.dropball.objects.Ball;
import org.programus.android.game.dropball.objects.BoardGroup;
import org.programus.android.game.dropball.objects.ObjectCollection;
import org.programus.android.game.dropball.objects.ScoreStorage;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * The scene for playing.
 * @author Programus
 *
 */
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
		ObjectCollection objects = dGame.getObjects(); 
		objects.getBall().draw(canvas); 
		objects.getBoardGroup().draw(canvas); 
		objects.getScore().draw(canvas); 
	}

	@Override
	protected void calcFrameData() {
		if (dGame.getStatus() == dGame.STATUS_PLAYING) {
			Ball ball = dGame.getObjects().getBall(); 
			BoardGroup bgroup = dGame.getObjects().getBoardGroup(); 
			ScoreStorage score = dGame.getObjects().getScore(); 
			
			bgroup.stepCalc(dt); 
			ball.updageBoardGroup(bgroup); 
			ball.stepCalc(dt); 
			score.stepCalc(dt); 
			
			if (this.gameOver(ball)) {
				dGame.setStatus(dGame.STATUS_NEW); 
				// reset game.
				dGame.setUseLoadedData(false); 
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
