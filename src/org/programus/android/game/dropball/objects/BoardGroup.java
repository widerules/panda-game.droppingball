package org.programus.android.game.dropball.objects;

import java.util.Iterator;
import java.util.LinkedList;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.objects.SpriteLike;
import org.programus.android.game._engine.utils.Const;

import android.content.res.Resources;
import android.graphics.Canvas;
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
		this.addBoards(y); 
		
		Board.setSpeed(5); 
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
