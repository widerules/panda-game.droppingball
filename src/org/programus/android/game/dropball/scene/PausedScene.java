package org.programus.android.game.dropball.scene;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.core.GameScene;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game.dropball.DroppingBallGame;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;

public class PausedScene extends GameScene implements Const {
	private DroppingBallGame dGame; 
	private int bkColor;
	private Paint pauseTextPaint; 
	private String pausedText; 

	public PausedScene(Game game) {
		super(game);
		dGame = (DroppingBallGame) game; 
		Resources res = this.game.getContext().getResources(); 
		bkColor = res.getColor(R.color.pauseBkColor); 
		this.pauseTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		this.pauseTextPaint.setColor(res.getColor(R.color.pausedTextColor)); 
		this.pauseTextPaint.setTextAlign(Paint.Align.CENTER); 
		this.pauseTextPaint.setTextSize(res.getDimension(R.dimen.pausedTextSize)); 
		this.pausedText = res.getString(R.string.paused); 
	}
	
	@Override
	protected void calcFrameData() {
	}
	
	protected void drawFrame(Canvas canvas) {
		canvas.drawColor(this.bkColor); 
		dGame.getObjects().getBall().draw(canvas); 
		dGame.getObjects().getBoardGroup().draw(canvas); 
		canvas.drawText(this.pausedText, game.getW() >> 1, game.getH() >> 1, pauseTextPaint); 
	}
}
