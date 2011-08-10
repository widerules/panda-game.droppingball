package org.programus.android.game;

import org.programus.android.game.data.AccData;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class MainActivity extends Activity implements SensorEventListener {
	
	private SensorManager sm; 
	private Sensor accSensor; 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.initSensor(); 
    }
    
    /**
     * Initialize sensor related variables. 
     */
    private void initSensor() {
    	sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE); 
    	accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); 
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		// register sensor listener. 
		if (sm != null && accSensor != null) {
			sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_GAME); 
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// unregister sensor listener. 
		if (sm != null) {
			sm.unregisterListener(this); 
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// update sensor data. 
		AccData.getInstance().setG(event.values); 
	}
}