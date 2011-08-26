package org.programus.android.game.dropball.objects;

import java.text.MessageFormat;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.data.AccData;
import org.programus.android.game._engine.objects.SavableSprite;
import org.programus.android.game._engine.utils.Const;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class ScoreStorage extends SavableSprite implements Const {
	public final static String SCORE = "score.value"; 
	public final static String HI_SCORE = "score.hivalue"; 
	
	private long score;
	private long hiScore;
	private long prevScore;
	private Paint paint;
	private Game game;
	
	private MessageFormat format; 
	
	public ScoreStorage(Game game) {
		this.game = game; 
		Resources res = this.game.getContext().getResources(); 
		paint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		paint.setColor(res.getColor(R.color.scoreTextColor)); 
		paint.setTextSize(res.getDimension(R.dimen.scoreTextSize)); 
		
		format = new MessageFormat(res.getString(R.string.scoreFormat)); 
	}

	@Override
	public void reset() {
		this.prevScore = score; 
		if (score > this.hiScore) {
			this.hiScore = score; 
		}
		score = 0; 
	}

	@Override
	public void stepCalc(long dt) {
		long ds = (long)(Math.abs((AccData.getInstance().getScreenGy()) + 1) * dt); 
		this.score += ds; 
	}

	@Override
	public void draw(Canvas canvas) {
		String scoreString = format.format(new Long[]{score}); 
		Rect bounds = new Rect(); 
		paint.getTextBounds(scoreString, 0, scoreString.length(), bounds); 
		int x = 0; 
		int y = bounds.height(); 
		canvas.drawText(scoreString, x, y, paint); 
	}
	
	@Override
	public void save(SharedPreferences.Editor editor) {
		editor.putLong(SCORE, score); 
		editor.putLong(HI_SCORE, hiScore); 
	}
	
	@Override
	public boolean load(SharedPreferences pref) {
		long s = pref.getLong(SCORE, -1); 
		if (s < 0) {
			return false; 
		} else {
			this.score = s;
			this.hiScore = pref.getLong(HI_SCORE, 0); 
			return true; 
		}
	}

	public long getHiScore() {
		return hiScore;
	}

	public long getPrevScore() {
		return prevScore;
	}
}
