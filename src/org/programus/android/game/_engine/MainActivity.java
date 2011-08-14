package org.programus.android.game._engine;

import org.programus.android.game.R;
import org.programus.android.game._engine.data.AccData;
import org.programus.android.game._engine.utils.PropHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
        this.validSettings(); 
        this.initSensor(); 
    }
    
    /**
     * Initialize sensor related variables. 
     */
    private void initSensor() {
    	sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE); 
    	accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); 
    }
    
    private void validSettings() {
    	PropHelper p = PropHelper.getInstance(); 
    	p = null; 
    	if (p == null) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
    		builder.setMessage("Package is damaged")
    			.setCancelable(false)
    			.setPositiveButton("OK", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						System.exit(1); 
					}
    			}); 
    		AlertDialog dialog = builder.create(); 
    		dialog.show(); 
    		try {
    			while(true) {
    				Thread.sleep(200); 
    			}
			} catch (InterruptedException e) {
			} 
    	}
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