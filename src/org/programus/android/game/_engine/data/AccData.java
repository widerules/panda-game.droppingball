package org.programus.android.game._engine.data;

import org.programus.android.game._engine.utils.Const;

/**
 * A class to store the accelerometer data. 
 * @author Programus
 *
 */
public class AccData implements Const {
	private static AccData data = new AccData(); 
	private AccData() {}
	public static AccData getInstance() {
		return data;
	}
	
	private final static float RATE = 1F;
	private float gx;
	private float gy;
	private float gz;
	
	public synchronized void setG(float[] values) {
		if (values.length >= 3) {
			gx = values[0]; 
			gy = values[1]; 
			gz = values[2]; 
		}
	}
	
	public synchronized float getGx() {
		return gx;
	}
	public synchronized float getScreenGx() {
		return -gx * RATE * PX_RATE;
	}
	public synchronized float getScreenGy() {
		return gy * RATE * PX_RATE; 
	}
	public synchronized float getGy() {
		return gy;
	}
	public synchronized float getGz() {
		return gz;
	}
}
