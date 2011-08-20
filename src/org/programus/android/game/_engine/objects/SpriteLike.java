package org.programus.android.game._engine.objects;

import android.graphics.Canvas;

public interface SpriteLike {
	void draw(Canvas canvas); 
	void reset(); 
	void stepCalc(long dt); 
}
