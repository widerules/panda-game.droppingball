package org.programus.android.game.dropball.objects;

import org.programus.android.game._engine.core.Game;

import android.content.Context;
import android.content.res.Resources;

public class ObjectCollection {
	private Game game; 
	
	private Ball ball; 
	
	public ObjectCollection(Game game) {
		this.game = game; 
	}
	
	private void initObjects() {
		Context context = game.getContext(); 
		Resources res = context.getResources(); 
	}
}
