package org.programus.android.game._engine.core;

import android.content.SharedPreferences;

public interface Savable {
	public void save(SharedPreferences.Editor editor); 
	public boolean load(SharedPreferences pref); 
}
