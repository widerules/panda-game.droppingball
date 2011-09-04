package org.programus.android.game._engine.objects;

import org.programus.android.game._engine.utils.Const;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * A sprite is an object in the game. A sprite is basically in a rectangle and can move and can be drawn. 
 * @author Programus
 *
 */
public abstract class Sprite implements SpriteLike, Const {
	/** The rectangle that the sprite be contained in.  */
	protected RectF bounds = new RectF(); 

	/**
	 * Move the sprite. 
	 * @param dx the related x offset.
	 * @param dy the related y offset. 
	 */
	public void move(float dx, float dy) {
		bounds.offset(dx, dy); 
	}
	
	/**
	 * Move the sprite to specified location. 
	 * @param centerX the x
	 * @param centerY the y
	 */
	public void moveTo(float centerX, float centerY) {
		float x = bounds.centerX(); 
		float y = bounds.centerY(); 
		this.move(centerX - x, centerY - y); 
	}
	
	/**
	 * Return the instance of the internal rectangle. 
	 * @return the instance of the internal rectangle. 
	 */
	public RectF getBounds() {
		return this.bounds; 
	}
	
	/**
	 * Return a new instance of rectangle which is the same as the internal rectangle. 
	 * @return a new instance of rectangle which is the same as the internal rectangle. 
	 */
	public RectF copyBounds() {
		return new RectF(this.bounds); 
	}
	
	/**
	 * Draw the sprite. 
	 * @param canvas
	 */
	public void draw(Canvas canvas) {
	}
	
}
