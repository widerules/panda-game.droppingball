package org.programus.android.game.drop.scene;

import org.programus.android.game.Const;
import org.programus.android.game.core.Game;
import org.programus.android.game.core.GameScene;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class PausedScene extends GameScene implements Const {
	private int x; 

	public PausedScene(Game game) {
		super(game);
	}
	
	protected void drawFrame(Canvas canvas) {
		int w = game.getW(); 
		int h = game.getH(); 
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