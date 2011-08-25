package org.programus.android.game._engine.core;

import java.util.HashMap;
import java.util.Map;

public class GameContext {
	public final static String SHARED_PREF = "SharedPreferences"; 
	public final static String GAME_VIEW = "GameView"; 
	
	private static GameContext inst = new GameContext(); 
	private Map<String, Object> contextObjects = new HashMap<String, Object>(); 
	private GameContext() {
	}
	
	public static GameContext getInstance() {
		return inst; 
	}
	
	public void put(String key, Object value) {
		this.contextObjects.put(key, value); 
	}
	
	public Object get(String key) {
		return this.contextObjects.get(key); 
	}
}
