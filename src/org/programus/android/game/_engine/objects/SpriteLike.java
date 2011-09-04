package org.programus.android.game._engine.objects;

import android.graphics.Canvas;

/**
 * Something is like a sprite could implements this interface. 
 * @author Programus
 *
 */
public interface SpriteLike {
	void draw(Canvas canvas); 
	void reset(); 
	void stepCalc(long dt); 
}
