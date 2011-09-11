package org.programus.android.game._engine.utils;

import java.util.LinkedList;

import android.graphics.RectF;

/**
 * A pool for RectF. 
 * This includes a linked list that stores the idle rect. 
 * @author Programus
 */
public class RectPool implements Const {
	private final static int INIT_COUNT = 10; 
	private static RectPool inst = new RectPool(); 
	private RectPool() {
		synchronized(idleRects) {
			for (int i = 0; i < INIT_COUNT; i++) {
				idleRects.add(new RectF()); 
			}
		}
	}
	
	public static RectPool getInstance() {
		return inst;
	}
	
	private LinkedList<RectF> idleRects = new LinkedList<RectF>(); 
	
	public RectF getRect() {
		RectF rect = null; 
		synchronized (idleRects) {
			rect = idleRects.isEmpty() ? new RectF() : idleRects.removeLast(); 
		}
		return rect; 
	}
	
	public RectF getRect(RectF src) {
		RectF rect = null; 
		synchronized (idleRects) {
			rect = idleRects.isEmpty() ? new RectF() : idleRects.removeLast(); 
			rect.set(src); 
		}
		return rect; 
	}
	
	public void returnRect(RectF rect) {
		synchronized (idleRects) {
			idleRects.addLast(rect); 
		}
	}
	
	public int size() {
		return idleRects.size(); 
	}
}
