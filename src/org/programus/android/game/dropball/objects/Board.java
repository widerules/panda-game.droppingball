package org.programus.android.game.dropball.objects;

import org.programus.android.game.R;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game.dropball.DroppingBallGame;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;

/**
 * Class to represent the boards. 
 * @author Programus
 *
 */
public class Board extends DroppingSprite implements Const {
	
	private static float speed = 0; 
	private static float acceleration = 0; 
	private Paint fillPaint;
	private DroppingBallGame game; 
	
	/**
	 * This constructor will generate the rect for the bounds automatically if bGenerateRectData is true. 
	 * A rect will have a random width and x position. 
	 * @param game
	 * @param bGenerateRectData true will generate the rect. 
	 */
	public Board(DroppingBallGame game, boolean bGenerateRectData) {
		this.game = game; 
		
		Resources res = this.game.getContext().getResources(); 		
		int foreColor = res.getColor(R.color.foreColor); 
		this.fillPaint = new Paint(); 
		this.fillPaint.setColor(foreColor); 
		this.fillPaint.setStyle(Style.FILL); 
		
		if (Board.speed < ZERO_FLOAT) {
			Board.speed = res.getDimension(R.dimen.boardInitSpeed); 
		}
		
		if (Board.acceleration < ZERO_FLOAT) {
			Board.acceleration = res.getDimension(R.dimen.boardAcceleration); 
			DroppingSprite.initFrictionRate(res); 
		}
		
		if (bGenerateRectData) {
			float height = res.getDimension(R.dimen.boardThickness); 
			float width = this.generateRandomWidth(game.getW()); 
			float x = this.generateRandomX(game.getW()); 
			this.bounds.set(x, 0, x + width, height); 
		}
	}
	
	/**
	 * This constructor will generate the rect for the bounds automatically. 
	 * @param game
	 * @param top the top position of the board. 
	 */
	public Board(DroppingBallGame game, float top) {
		this(game, true); 
		this.move(0, top); 
	}
	
	private int generateRandomWidth(int w) {
		int minW = w >> 3;			// w / 8
		int maxW = (w >> 2) * 3; 	// w*3/4
		return minW + RAND.nextInt(maxW - minW);
	}
	
	private int generateRandomX(int w) {
		return RAND.nextInt(w); 
	}
	
	public Board getAltBoard() {
		Board alt = null; 
		int w = game.getW(); 
		if (this.bounds.right > w) {
			alt = new Board(this.game, false); 
			alt.bounds.set(
					bounds.left - w, 
					bounds.top, 
					bounds.right - w, 
					bounds.bottom); 
		}
		return alt; 
	}

	public RectF getExpandedRect(float u) {
		return new RectF(
				this.bounds.left - u, 
				this.bounds.top - u, 
				this.bounds.right + u, 
				this.bounds.bottom + u); 
	}

	@Override
	public void reset() {
	}
	
	public static void speedCalc(long dt) {
		float a = acceleration - frictionRate * speed * speed * speed;  
		speed += a * dt; 
	}

	@Override
	public void stepCalc(long dt) {
		this.move(0, -speed); 
	}
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(this.bounds, fillPaint); 
	}

	public static float getSpeed() {
		return speed;
	}

	public static void setSpeed(float speed) {
		Board.speed = speed;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Board: ").append(this.bounds); 
		return sb.toString(); 
	}

}
