package org.programus.android.game.dropball.objects;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Ball {
	private RectF bounds; 
	private Paint paint;
	private Game game; 
	private float speedX;
	private float speedY; 
	
	public Ball(Game game) {
		this.game = game; 
		
		Resources res = this.game.getContext().getResources(); 
		float r = res.getDimension(R.dimen.ballRadius); 
		bounds = new RectF(-r, -r, r, r); 
		
		int foreColor = res.getColor(R.color.foreColor); 
		paint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		paint.setColor(foreColor); 
	}
	
	public void move(float dx, float dy) {
		bounds.offset(dx, dy); 
	}
	
	public void moveTo(float centerX, float centerY) {
		float x = bounds.centerX(); 
		float y = bounds.centerY(); 
		this.move(centerX - x, centerY - y); 
	}
	
	public RectF getBounds() {
		return this.bounds; 
	}
	
	public RectF copyBounds() {
		return new RectF(this.bounds); 
	}
	
	public void draw(Canvas canvas) {
		canvas.drawOval(bounds, paint); 
	}
}
