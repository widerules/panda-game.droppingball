package org.programus.android.game.dropball.objects;

import org.programus.android.game.R;
import org.programus.android.game._engine.objects.Sprite;

import android.content.res.Resources;

public abstract class DroppingSprite extends Sprite {
	protected static float frictionRate = -1; 
	
	protected static void initFrictionRate(Resources res) {
		if (frictionRate < 0) {
			frictionRate = res.getDimension(R.dimen.frictionRate); 
		}
	}
}