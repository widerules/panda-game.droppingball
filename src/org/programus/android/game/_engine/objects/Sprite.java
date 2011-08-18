package org.programus.android.game._engine.objects;

import android.graphics.Canvas;
import android.graphics.RectF;

public abstract class Sprite {
	protected RectF bounds = new RectF(); 

	public abstract void reset();
	public abstract void stepCalc(long dt);
	
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
	}
	
}
