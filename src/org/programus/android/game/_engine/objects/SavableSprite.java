package org.programus.android.game._engine.objects;

import org.programus.android.game._engine.core.Savable;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * A class to represent the savable sprite. This class save and load the internal rectangle. 
 * @author Programus
 *
 */
public abstract class SavableSprite extends Sprite implements Savable {
	
	public static final String LEFT = "bounds.left"; 
	public static final String RIGHT = "bounds.right"; 
	public static final String TOP = "bounds.top"; 
	public static final String BOTTOM = "bounds.bottom"; 
	
	protected String name = ""; 

	@Override
	public boolean load(SharedPreferences pref) {
		String className = this.getClass().getCanonicalName() + name; 
		boolean ret = pref.contains(className + BOTTOM); 
		if (ret) {
			this.bounds.left = pref.getFloat(className + LEFT, 0); 
			this.bounds.right = pref.getFloat(className + RIGHT, 0); 
			this.bounds.top = pref.getFloat(className + TOP, 0); 
			this.bounds.bottom = pref.getFloat(className + BOTTOM, 0); 
		}
		return ret; 
	}

	@Override
	public void save(Editor editor) {
		String className = this.getClass().getCanonicalName() + name; 
		editor.putFloat(className + LEFT, this.bounds.left); 
		editor.putFloat(className + RIGHT, this.bounds.right); 
		editor.putFloat(className + TOP, this.bounds.top); 
		editor.putFloat(className + BOTTOM, this.bounds.bottom); 
	}
}
