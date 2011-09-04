package org.programus.android.game._engine;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.GameContext;
import org.programus.android.game._engine.data.AccData;
import org.programus.android.game._engine.ui.GameSurfaceView;
import org.programus.android.game._engine.utils.Const;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

/**
 * The main activity for the android application. 
 * The main view is an instance of {@linkplain GameSurfaceView}. 
 * @author Programus
 * 
 */
public class MainActivity extends Activity implements Const, SensorEventListener {
	
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
    
    /**
     * Invoked when resume the activity. 
     * In this method, 
     * <ol>
     * 	<li>data is loaded if exists, </li>
     * 	<li>accelerometer sensor is registered and listened, </li>
     * 	<li>pause the game. </li>
     * </ol>
     * @see {@linkplain Activity#onResume()}
     */
	@Override
	protected void onResume() {
		super.onResume();
		GameSurfaceView view = (GameSurfaceView) GameContext.getInstance().get(GameContext.GAME_VIEW); 
		view.setPausing(true); 
		SharedPreferences pref = this.getPreferences(MODE_PRIVATE); 
		view.load(pref); 
		// register sensor listener. 
		if (sm != null && accSensor != null) {
			sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_GAME); 
		}
		view.setPausing(false); 
	}

	/**
	 * Invoked when pause the activity. 
	 * In this method, 
	 * <ol>
	 * 	<li>pause the game, </li>
	 * 	<li>save game data.</li>
	 * </ol>
	 * @see {@linkplain Activity#onPause()}
	 */
	@Override
	protected void onPause() {
		super.onPause();
		GameSurfaceView view = (GameSurfaceView) GameContext.getInstance().get(GameContext.GAME_VIEW); 
		view.setPausing(true); 
		// unregister sensor listener. 
		if (sm != null) {
			sm.unregisterListener(this); 
		}
		SharedPreferences.Editor editor = this.getPreferences(MODE_PRIVATE).edit(); 
		view.save(editor); 
		editor.commit(); 
	}

	/**
	 * This method is to finish this activity when package is damaged. 
	 * @param intent
	 * @see {@linkplain Activity#onNewIntent(Intent)}
	 * @see {@linkplain GameSurfaceView#GameSurfaceView(Context, android.util.AttributeSet)}
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(this.getString(R.string.appExit), false)) {
			this.finish(); 
		}
	}

	/* (non-Javadoc)
	 * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * Invoked when the sensor value changed. 
	 * Set acceleration in this method. 
	 * @param event the event.
	 * @see {@linkplain SensorEventListener#onSensorChanged(SensorEvent)}
	 * @see {@linkplain AccData}
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		// update sensor data. 
		AccData.getInstance().setG(event.values); 
	}

	/**
	 * Invoked after the activity got/lost the focus. 
	 * Pause game when the focus lost. 
	 * @param hasFocus true if got focus. 
	 * @see {@linkplain GameSurfaceView#pauseGame()}
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		GameSurfaceView view = (GameSurfaceView) GameContext.getInstance().get(GameContext.GAME_VIEW); 
		if (!hasFocus) {
			view.pauseGame(); 
		}
	}
}