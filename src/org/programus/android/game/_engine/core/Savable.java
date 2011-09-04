package org.programus.android.game._engine.core;

import android.content.SharedPreferences;

/**
 * An interface for objects can be saved. 
 * @author Programus
 */
public interface Savable {
	/**
	 * Save object. 
	 * @param editor save objects into the {@linkplain SharedPreferences}. 
	 */
	public void save(SharedPreferences.Editor editor); 
	
	/**
	 * Load data. 
	 * @param pref the {@linkplain SharedPreferences} to load data from. 
	 * @return true if data is exists and loaded. 
	 */
	public boolean load(SharedPreferences pref); 
}
