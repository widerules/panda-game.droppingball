package org.programus.android.game.dropball.objects;

import org.programus.android.game._engine.objects.Sprite;

public class Board extends Sprite {
	
	private static float speed; 

	@Override
	public void reset() {
	}

	@Override
	public void stepCalc(long dt) {
		// TODO Auto-generated method stub

	}

	public static float getSpeed() {
		return speed;
	}

	public static void setSpeed(float speed) {
		Board.speed = speed;
	}

}
