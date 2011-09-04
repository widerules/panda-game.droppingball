package org.programus.android.game._engine.utils;

import java.util.Random;

public interface Const {
	String TAG = "[DroppingBall]";
	
	/** 1 m/s^2 -> 800 / 0.093 / 1000000 px/ms^2 */
	float PX_RATE = 800 / 0.093F / 1000000; 
	
	/** Friction rate. friction / m = F_RATE * speed */
	float F_RATE = .000003F; 
	
	float ZERO_FLOAT = 0.0000000001F;
	
	Random RAND = new Random(); 
}
