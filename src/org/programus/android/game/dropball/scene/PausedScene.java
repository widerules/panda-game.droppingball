package org.programus.android.game.dropball.scene;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.core.GameScene;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game.dropball.DroppingBallGame;
import org.programus.android.game.dropball.objects.Ball;
import org.programus.android.game.dropball.objects.ObjectCollection;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class PausedScene extends GameScene implements Const {
	private DroppingBallGame dGame; 
	private int bkColor;
	
	private Paint pauseTextPaint; 
	private String pausedText; 
	
	private Paint newTextPaint;
	private String newText; 
	
	private Paint scorePaint;
	
	private Paint recordPaint;
	private String recordText;

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
		
		this.newTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		this.newTextPaint.setColor(res.getColor(R.color.newTextColor)); 
		this.newTextPaint.setTextAlign(Paint.Align.CENTER); 
		this.newTextPaint.setTextSize(res.getDimension(R.dimen.newTextSize)); 
		this.newText = res.getString(R.string.newText); 
		
		this.scorePaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		this.scorePaint.setColor(res.getColor(R.color.pausedScoreColor)); 
		this.scorePaint.setTextAlign(Paint.Align.CENTER); 
		this.scorePaint.setTextSize(res.getDimension(R.dimen.pausedScoreSize)); 
		
		this.recordPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		this.recordPaint.setColor(res.getColor(R.color.recordColor)); 
		this.recordPaint.setTextAlign(Paint.Align.CENTER); 
		this.recordPaint.setTextSize(res.getDimension(R.dimen.recordSize)); 
		this.recordText = res.getString(R.string.recordText); 
	}
	
	@Override
	protected void calcFrameData() {
		if (dGame.getStatus() == dGame.STATUS_PAUSED) {
			Ball ball = dGame.getObjects().getBall(); 
			ball.stepCalc(dt); 
		}
	}
	
	protected void drawFrame(Canvas canvas) {
		int status = dGame.getStatus(); 
		canvas.drawColor(this.bkColor); 
		ObjectCollection objects = dGame.getObjects(); 
		objects.getBoardGroup().draw(canvas); 
		objects.getBall().draw(canvas); 
		boolean isPaused = (status == dGame.STATUS_PAUSED); 
		this.drawText(canvas, isPaused); 
	}
	
	private void drawText(Canvas canvas, boolean isPaused) {
		ObjectCollection objects = dGame.getObjects(); 
		int x = game.getW() >> 1;
		int y = game.getH() >> 1;
		if (isPaused) {
			canvas.drawText(this.pausedText, x, y, pauseTextPaint); 
		} else {
			canvas.drawText(this.newText, x, y, newTextPaint); 
		}
		Rect rect = new Rect(); 
		String score = isPaused ? objects.getScore().getCurrScoreText() : objects.getScore().getPrevScoreText(); 
		this.scorePaint.getTextBounds(score, 0, score.length(), rect); 
		final int GAP = game.getH() >> 4; 
		y += rect.height() + GAP; 
		canvas.drawText(score, x, y, scorePaint); 
		String hiScore = objects.getScore().getHiScoreText(); 
		y += rect.height() + (GAP >> 1); 
		canvas.drawText(hiScore, x, y, scorePaint); 
		if (!isPaused && objects.getScore().getPrevScore() == objects.getScore().getHiScore() && objects.getScore().getHiScore() > 0) {
			recordPaint.getTextBounds(this.recordText, 0, this.recordText.length(), rect); 
			y += rect.height() + GAP; 
			canvas.drawText(recordText, x, y, recordPaint); 
		}
	}
}
