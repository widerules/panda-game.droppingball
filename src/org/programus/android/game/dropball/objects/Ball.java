package org.programus.android.game.dropball.objects;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.data.AccData;
import org.programus.android.game._engine.objects.Sprite;
import org.programus.android.game._engine.utils.Const;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class Ball extends Sprite implements Const {
	private Paint paint;
	private Game game; 
	
	private AccData acc; 
	
	private float r; 
	private float speedX;
	private float speedY; 
	
	public Ball(Game game) {
		this.game = game; 
		this.acc = AccData.getInstance(); 
		
		Resources res = this.game.getContext().getResources(); 
		r = res.getDimension(R.dimen.ballRadius); 
		bounds.set(-r, -r, r, r); 
		
		int foreColor = res.getColor(R.color.foreColor); 
		paint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		paint.setColor(foreColor); 
		paint.setStyle(Paint.Style.FILL); 
	}
	
	@Override
	public void reset() {
		int w = game.getW(); 
		this.moveTo(w >> 1, 200); 
	}
	
	@Override
	public void stepCalc(long dt) {
		float ax = this.acc.getScreenGx() - this.getFriction(this.speedX); 
		float ay = this.acc.getScreenGy() - this.getFriction(this.speedY); 
		this.speedX += ax * dt; 
		this.speedY += ay * dt; 
		
//		float vx2 = speedX * speedX; 
//		float vy2 = speedY * speedY; 
//		float v2 = vx2 + vy2; 
//		if (v2 > MAX_SPEED_2) {
//			float mvx2 = vx2 * MAX_SPEED_2 / v2; 
//			float mvy2 = vy2 * MAX_SPEED_2 / v2; 
//			speedX = (float) (speedX > 0 ? Math.sqrt(mvx2) : -Math.sqrt(mvx2)); 
//			speedY = (float) (speedY > 0 ? Math.sqrt(mvy2) : -Math.sqrt(mvy2)); 
//		}
		
		this.move(speedX, speedY); 
		Log.d(TAG, "v=" + speedX + "," + speedY + "/" + this.bounds); 
		
		int w = this.game.getW(); 
		int h = this.game.getH(); 
		if (this.bounds.top < 0 && this.speedY < 0) {
			this.speedY = -this.speedY; 
			this.moveTo(this.bounds.centerX(), r); 
		}
		if (this.bounds.bottom > h && this.speedY > 0) {
			this.speedY = -this.speedY; 
			this.moveTo(this.bounds.centerX(), h - r); 
		}
		if (this.bounds.right < 0) {
			this.move(w, 0); 
		}
		if (this.bounds.left > w) {
			this.move(-w, 0); 
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawOval(bounds, paint); 
		RectF altBounds = null; 
		int w = canvas.getWidth(); 
		if (bounds.left < 0) {
			altBounds = new RectF(bounds); 
			altBounds.offset(w, 0); 
		}
		if (bounds.right > w) {
			altBounds = new RectF(bounds); 
			altBounds.offset(-w, 0); 
		}
		if (altBounds != null) {
			canvas.drawOval(altBounds, paint); 
		}
	}
	
	private float getFriction(float speed) {
		return F_RATE * speed * speed * speed; 
	}
}
