package org.programus.android.game.dropball.objects;

import java.util.Iterator;
import java.util.LinkedList;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.objects.SpriteLike;
import org.programus.android.game._engine.utils.Const;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

public class BoardGroup implements SpriteLike, Const{
	private LinkedList<Board> boards = new LinkedList<Board>(); 
	private Game game; 
	
	private static float minDistance; 
	private static float maxDistance; 
	
	public BoardGroup(Game game) {
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
		this.boards.clear(); 
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
		int line = -(game.getH() >> 1); 
		float lastBottom = this.boards.getLast().getBounds().bottom; 
		if ((game.getH() << 1) - lastBottom >= maxDistance) {
			float y = this.boards.getLast().getBounds().bottom + minDistance + RAND.nextFloat() * (maxDistance - minDistance); 
			this.addBoards(y); 
		}
		for (Iterator<Board> i = this.boards.iterator();i.hasNext();) {
			Board board = i.next(); 
			if (board.getBounds().bottom < line) {
				i.remove(); 
			} else {
				board.stepCalc(dt); 
			}
		}
	}
	
	public PointF getImpactPoint(float r, PointF p1, PointF p2, PointF speed) {
//		p2.y -= Board.getSpeed(); 
		float a = (p1.y - p2.y) / (p1.x - p2.x); 
		float b = (p1.x * p2.y - p2.x * p1.y) / (p1.x - p2.x); 
		float minX = Math.min(p1.x, p2.x); 
		float maxX = Math.max(p1.x, p2.x); 
		float minY = Math.min(p1.y, p2.y); 
		float maxY = Math.max(p1.y, p2.y); 
		
		final float DELTA = 0.0F; 
		
		for (Board board : this.boards) {
			RectF rect = board.getExpandedRect(r); 
			PointF p = new PointF(); 
			
			// Check top line
			p.y = rect.top; 
			if (speed.y > -Board.getSpeed() && rect.bottom > minY && p.y < maxY) {
				p.x = (p.y - b) / a; 
				if (((p.x > minX && p.x < maxX) || rect.contains(p1.x, p1.y)) && p.x > rect.left && p.x <= rect.right) {
					speed.y = -Board.getSpeed(); 
					Log.d(TAG, "top:" + p.x + "," + p.y + "\t/Rect:" + rect + "\tp1:" + p1.x + "," + p1.y + "/p2:" + p2.x + "," + p2.y + "/speed:" + speed.x + "," + speed.y + "/a=" + a + "/b=" + b); 
					p.x = p2.x; 
					p.y -= DELTA; 
					return p; 
				}
			}
			
			// Check bottom line
			p.y = rect.bottom; 
			if (speed.y < -Board.getSpeed() && p.y > minY && rect.top < maxY) {
				p.x = (p.y - b) / a; 
				if (p.x > minX && p.x < maxX && p.x >= rect.left && p.x < rect.right) {
					speed.y = -Board.getSpeed(); 
					Log.d(TAG, "bottom:" + p.x + "," + p.y + "\t/Rect:" + rect + "\tp1:" + p1.x + "," + p1.y + "/p2:" + p2.x + "," + p2.y + "/speed:" + speed.x + "," + speed.y + "/a=" + a + "/b=" + b); 
					p.x = p2.x; 
					p.y += DELTA;
					return p; 
				}
			}
			
			// Check left line
			p.x = rect.left; 
			if (speed.x > 0 && rect.right > minX && p.x < maxX) {
				p.y = a * p.x + b; 
				if (p.y > minY && p.y < maxY && p.y >= rect.top && p.y < rect.bottom) {
					speed.x = 0; 
					Log.d(TAG, "left:" + p.x + "," + p.y + "\t/Rect:" + rect + "\tp1:" + p1.x + "," + p1.y + "/p2:" + p2.x + "," + p2.y + "/speed:" + speed.x + "," + speed.y + "/a=" + a + "/b=" + b); 
					p.y = p2.y; 
					p.x -= DELTA; 
					return p; 
				}
			}
			
			// Check right line
			p.x = rect.right; 
			if (speed.x < 0 && p.x > minX && rect.left < maxX) {
				p.y = a * p.x + b; 
				if (p.y > minY && p.y < maxY && p.y > rect.top && p.y <= rect.bottom) {
					speed.x = 0; 
					Log.d(TAG, "right:" + p.x + "," + p.y + "\t/Rect:" + rect + "\tp1:" + p1.x + "," + p1.y + "/p2:" + p2.x + "," + p2.y + "/speed:" + speed.x + "," + speed.y + "/a=" + a + "/b=" + b); 
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
			Board brd = new Board(this.game, y); 
			Log.d(TAG, "Board created:" + brd); 
			this.boards.add(brd); 
			Board alt = brd.getAltBoard(); 
			Log.d(TAG, "    Alt Board:" + alt); 
			if (alt != null) {
				this.boards.add(alt); 
			}
			y += brd.getBounds().height() + minDistance + RAND.nextFloat() * (maxDistance - minDistance); 
		}
	}
}
