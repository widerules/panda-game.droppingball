package org.programus.android.game.data;

/**
 * A class to store the accelerometer data. 
 * @author Programus
 *
 */
public class AccData {
	private static AccData data = new AccData(); 
	private AccData() {}
	public static AccData getInstance() {
		return data;
	}
	
	private final static float RATE = 1F;
	private float gx;
	private float gy;
	private float gz;
	
	public void setG(float[] values) {
		if (values.length >= 3) {
			gx = values[0]; 
			gy = values[1]; 
			gz = values[2]; 
		}
	}
	
	public float getGx() {
		return gx;
	}
	public float getScreenGx() {
		return -gx * RATE;
	}
	public void setGx(float gx) {
		this.gx = gx;
	}
	public float getScreenGy() {
		return gy * RATE; 
	}
	public float getGy() {
		return gy;
	}
	public void setGy(float gy) {
		this.gy = gy;
	}
	public float getGz() {
		return gz;
	}
	public void setGz(float gz) {
		this.gz = gz;
	}
}
