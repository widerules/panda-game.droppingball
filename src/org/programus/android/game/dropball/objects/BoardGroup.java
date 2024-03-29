package org.programus.android.game.dropball.objects;

import java.util.LinkedList;
import java.util.ListIterator;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Savable;
import org.programus.android.game._engine.objects.SavableSprite;
import org.programus.android.game._engine.objects.SpriteLike;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game.dropball.DroppingBallGame;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

/**
 * The group of all boards in this game. 
 * @author Programus
 *
 */
public class BoardGroup implements SpriteLike, Savable, Const{
	private LinkedList<Board> boards = new LinkedList<Board>(); 
	private LinkedList<Board> idleBoards = new LinkedList<Board>(); 
	private DroppingBallGame game; 
	
	private static float minDistance; 
	private static float maxDistance; 
	
	private static final String BOARD_SPEED = "board.speed"; 
	private static final String BOARD_NUM = "board.number"; 
	private static final String BOARDS = "board.list_"; 
	
	public static final int SIDE_ABOVE = 1;
	public static final int SIDE_UNDER = 2; 
	public static final int SIDE_LEFT = 3;
	public static final int SIDE_RIGHT = 4; 
	
	public BoardGroup(DroppingBallGame game) {
		this.game = game; 
		this.initParams(); 
	}
	
	private void initParams() {
		if (BoardGroup.maxDistance < 1.0) {	// This means that the distance is still not set. 
			Resources res = game.getContext().getResources(); 
			BoardGroup.minDistance = res.getDimension(R.dimen.boardMinDistance); 
			BoardGroup.maxDistance = res.getDimension(R.dimen.boardMaxDistance); 
		}
	}

	private void initBoards() {
		int h = game.getH(); 
		int y = h >> 2; 
		while (!this.boards.isEmpty()) {
			this.idleBoards.add(this.boards.removeFirst()); 
		}
		Board.setSpeed(0); 
		this.addBoards(y); 
	}

	@Override
	public void draw(Canvas canvas) {
		for (Board board : this.boards) {
			board.draw(canvas); 
		}
	}

	@Override
	public void reset() {
		this.initBoards(); 
	}

	@Override
	public void stepCalc(long dt) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				float lastBottom = 0; 
				synchronized(boards) {
					lastBottom = boards.size() > 0 ? boards.getLast().getBounds().bottom : game.getH() >> 2; 
				}
				if ((game.getH() << 1) - lastBottom >= maxDistance) {
					float y = 0; 
					synchronized(boards) {
						y = (boards.size() > 0 ? boards.getLast().getBounds().bottom : 0) + minDistance + RAND.nextFloat() * (maxDistance - minDistance); 
					}
					addBoards(y); 
				}
			}
		}, "Add boards thread").start(); 
		
		Board.speedCalc(dt); 
		int line = -(game.getH() >> 1); 
		ListIterator<Board> i = null; 
		synchronized(this.boards) {
			i = this.boards.listIterator(); 
		}
		while (i.hasNext()) {
			Board board = i.next(); 
			if (board.getBounds().bottom < line) {
				this.idleBoards.addFirst(board); 
				i.remove(); 
			} else {
				board.stepCalc(dt); 
			}
		}
	}
	
	public int getSideOnBoard(float r, PointF p, PointF speed) {
		int side = 0; 
		final float EQU_GAP = 1;
		for (Board board : this.boards) {
			RectF rect = board.getExpandedRect(r); 
			if (rect.top - p.y > rect.height()) {
				// if the board is under the ball and too far, over the loop. 
				break; 
			} else if (p.y - rect.bottom > rect.height()) {
				// if the board is above the ball and too far, check next board. 
				continue; 
			}
			
			if (rect.left < p.x && rect.right > p.x) {
				if (Math.abs(rect.top - p.y) < EQU_GAP && (speed.y >= -Board.getSpeed())) {
					side = SIDE_ABOVE; 
					break; 
				}
				if (Math.abs(rect.bottom - p.y) < EQU_GAP && (speed.y <= -Board.getSpeed())) {
					side = SIDE_UNDER;
					break;
				}
			}
			if (rect.top < p.y && rect.bottom > p.y && speed.x >= 0) {
				if (Math.abs(rect.left - p.x) < EQU_GAP) {
					side = SIDE_LEFT;
					break;
				}
				if (Math.abs(rect.right - p.x) < EQU_GAP && speed.x <= 0) {
					side = SIDE_RIGHT;
					break;
				}
			}
		}
		return side; 
	}
	
	public PointF getImpactPoint(float r, PointF p1, PointF p2, PointF speed) {
//		p2.y -= Board.getSpeed(); 
		float a = (p1.y - p2.y) / (p1.x - p2.x); 
		float b = (p1.x * p2.y - p2.x * p1.y) / (p1.x - p2.x); 
		float minX = Math.min(p1.x, p2.x); 
		float maxX = Math.max(p1.x, p2.x); 
		float minY = Math.min(p1.y, p2.y); 
		float maxY = Math.max(p1.y, p2.y); 
		
		final float DELTA = 0F; 
		
		for (Board board : this.boards) {
			RectF rect = board.getExpandedRect(r); 
			PointF p = new PointF(); 
			
			if (rect.top - maxY > rect.height()) {
				// if the board is under the ball and too far, over the loop. 
				break; 
			} else if (minY - rect.bottom > rect.height()) {
				// if the board is above the ball and too far, check next board. 
				continue; 
			}
						
//			Log.d(TAG, "before:" + p.x + "," + p.y + "\t/Rect:" + rect + "\tp1:" + p1.x + "," + p1.y + "/p2:" + p2.x + "," + p2.y + "/speed:" + speed.x + "," + speed.y + "/a=" + a + "/b=" + b); 
			// Check top line
			p.y = rect.top; 
			if (speed.y >= -Board.getSpeed() && rect.bottom >= minY && p.y <= maxY) {
				p.x = (p.y - b) / a; 
//				Log.d(TAG, "p: " + p.x + "," + p.y + "/contains in rect:" + rect.contains(p1.x, p1.y)); 
				if ((p.x > minX && p.x < maxX && p.x > rect.left && p.x <= rect.right) || rect.contains(p1.x, p1.y)) {
					speed.y = -Board.getSpeed(); 
//					Log.d(TAG, "top:" + p.x + "," + p.y + "\t/Rect:" + rect + "\tp1:" + p1.x + "," + p1.y + "/p2:" + p2.x + "," + p2.y + "/speed:" + speed.x + "," + speed.y + "/a=" + a + "/b=" + b); 
					p.x = p2.x; 
					p.y -= DELTA; 
					return p; 
				}
			}
			
			// Check bottom line
			p.y = rect.bottom; 
			if (speed.y <= -Board.getSpeed() && p.y >= minY && rect.top <= maxY) {
				p.x = (p.y - b) / a; 
				if (p.x > minX && p.x < maxX && p.x >= rect.left && p.x < rect.right) {
					speed.y = -Board.getSpeed(); 
//					Log.d(TAG, "bottom:" + p.x + "," + p.y + "\t/Rect:" + rect + "\tp1:" + p1.x + "," + p1.y + "/p2:" + p2.x + "," + p2.y + "/speed:" + speed.x + "," + speed.y + "/a=" + a + "/b=" + b); 
					p.x = p2.x; 
					p.y += DELTA;
					return p; 
				}
			}
			
			// Check left line
			p.x = rect.left; 
			if (speed.x >= 0 && rect.right >= minX && p.x <= maxX) {
				p.y = a * p.x + b; 
				if (p.y > minY && p.y < maxY && p.y >= rect.top && p.y < rect.bottom) {
					speed.x = -speed.x / 2; 
//					Log.d(TAG, "left:" + p.x + "," + p.y + "\t/Rect:" + rect + "\tp1:" + p1.x + "," + p1.y + "/p2:" + p2.x + "," + p2.y + "/speed:" + speed.x + "," + speed.y + "/a=" + a + "/b=" + b); 
					p.y = p2.y; 
					p.x -= DELTA; 
					return p; 
				}
			}
			
			// Check right line
			p.x = rect.right; 
			if (speed.x <= 0 && p.x >= minX && rect.left <= maxX) {
				p.y = a * p.x + b; 
				if (p.y > minY && p.y < maxY && p.y > rect.top && p.y <= rect.bottom) {
					speed.x = -speed.x / 2; 
//					Log.d(TAG, "right:" + p.x + "," + p.y + "\t/Rect:" + rect + "\tp1:" + p1.x + "," + p1.y + "/p2:" + p2.x + "," + p2.y + "/speed:" + speed.x + "," + speed.y + "/a=" + a + "/b=" + b); 
					p.y = p2.y; 
					p.x += DELTA; 
					return p; 
				}
			}
		}
		return null; 
	}
	
	private void addBoards(float y) {
		while (y < (game.getH() << 1)) {
			Board brd = null; 
			if (this.idleBoards.isEmpty()) {
				brd = new Board(this.game, y); 
			} else {
				brd = idleBoards.removeFirst().generateRandomXPosition(y); 
				brd.reset(); 
			}
			Log.d(TAG, "Board created:" + brd); 
			synchronized(this.boards) {
				this.boards.add(brd); 
			}
			Board alt = brd.getAltBoard(this.idleBoards.isEmpty() ? null : this.idleBoards.removeFirst()); 
			Log.d(TAG, "    Alt Board:" + alt); 
			if (alt != null) {
				synchronized(this.boards) {
					this.boards.add(alt); 
				}
			}
			y += brd.getBounds().height() + minDistance + RAND.nextFloat() * (maxDistance - minDistance); 
		}
		Log.d(TAG, "Boards: " + boards.size() + "/Idle Boards:" + idleBoards.size()); 
	}

	@Override
	public boolean load(SharedPreferences pref) {
		boolean ret = pref.contains(BOARD_NUM); 
		if (ret) {
			Board.setSpeed(pref.getFloat(BOARD_SPEED, 0)); 
			final int n = pref.getInt(BOARD_NUM, 0); 
			while (!this.boards.isEmpty()) {
				this.idleBoards.add(this.boards.removeFirst()); 
			}
			for (int i = 0; i < n; i++) {
				Board b = this.idleBoards.isEmpty() ? new Board(this.game, false) : this.idleBoards.removeFirst(); 
				RectF r = b.getBounds(); 
				String keyPrefix = BOARDS + i; 
				r.set(
						pref.getFloat(keyPrefix + SavableSprite.LEFT,	0), 
						pref.getFloat(keyPrefix + SavableSprite.TOP, 	0),
						pref.getFloat(keyPrefix + SavableSprite.RIGHT, 	0),
						pref.getFloat(keyPrefix + SavableSprite.BOTTOM,	0)); 
				this.boards.add(b); 
			}
			Log.d(TAG, "loaded boards:" + boards); 
		}
		return ret;
	}

	@Override
	public void save(Editor editor) {
		editor.putFloat(BOARD_SPEED, Board.getSpeed()); 
		int i = 0; 
		for (Board b : this.boards) {
			RectF r = b.getBounds(); 
			String keyPrefix = BOARDS + (i++); 
			editor.putFloat(keyPrefix + SavableSprite.LEFT, 	r.left); 
			editor.putFloat(keyPrefix + SavableSprite.RIGHT, 	r.right); 
			editor.putFloat(keyPrefix + SavableSprite.TOP, 		r.top); 
			editor.putFloat(keyPrefix + SavableSprite.BOTTOM, 	r.bottom); 
		}
		editor.putInt(BOARD_NUM, i); 
		Log.d(TAG, "saved boards:" + boards); 
	}
}
