package org.programus.android.game.drop;

import org.programus.android.game.Const;
import org.programus.android.game.core.Game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class DroppingBallGame extends Game implements Const {
	
	private int x; 

	@Override
	public void drawFrame(Canvas canvas) {
		int w = canvas.getWidth(); 
		int h = canvas.getHeight(); 
		Paint bkp = new Paint(); 
		bkp.setColor(Color.WHITE); 
		canvas.drawRect(0, 0, w, h, bkp); 
		Paint p = new Paint(); 
		p.setColor(Color.BLACK); 
		String msg = "W:" + canvas.getWidth() + ",H:" + canvas.getHeight(); 
		canvas.drawText(msg, x, h >> 1, p); 
		x++; 
		if (x > w) {
			x = 0; 
		}
	}
}
