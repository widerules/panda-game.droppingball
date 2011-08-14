package org.programus.android.game._engine.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.util.Log;

public class PropHelper implements Const {
	// singlton this class. 
	private static PropHelper inst; 
	public static synchronized PropHelper getInstance() {
		if (inst == null) {
			try {
				inst = new PropHelper();
			} catch (Exception e) {
				Log.d(TAG, "Error when read settings.", e); 
			} 
		}
		return inst;
	}
	
	private final static String SETTING_FILENAME = "DroppingBall/src/org/programus/android/game/_engine/settings.properties"; 
	private Properties prop = new Properties(); 
	
	private PropHelper() throws Exception{
		InputStream in = ClassLoader.getSystemResourceAsStream(SETTING_FILENAME); 
		prop.load(in); 
	}
	
	public String get(String name) {
		return prop.getProperty(name); 
	}
	
	public String get(String name, String defaultValue) {
		return prop.getProperty(name, defaultValue); 
	}
}
