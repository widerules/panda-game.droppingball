package org.programus.android.game.dropball.objects;

import org.programus.android.game.R;
import org.programus.android.game._engine.core.Game;
import org.programus.android.game._engine.data.AccData;
import org.programus.android.game._engine.utils.Const;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Vibrator;

public class Ball extends DroppingSprite implements Const {
	private Paint paint;
	private Game game; 
	
	private AccData acc; 
	
	private BoardGroup boardGroup; 
	
	private float r; 
	private PointF speed; 
	
	private static final String SPEED_X = "ball.speed.x"; 
	private static final String SPEED_Y = "ball.speed.y";
	private static final float IMPACT_DSPEED = 5; 
	
	private Vibrator vib; 
	
	public Ball(Game game) {
		this.game = game; 
		this.acc = AccData.getInstance(); 
		
		this.vib = (Vibrator) game.getContext().getSystemService(Context.VIBRATOR_SERVICE); 
		this.speed = new PointF(); 
		
		Resources res = this.game.getContext().getResources(); 
		r = res.getDimension(R.dimen.ballRadius); 
		bounds.set(-r, -r, r, r); 
		
		int foreColor = res.getColor(R.color.foreColor); 
		paint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		paint.setColor(foreColor); 
		paint.setStyle(Paint.Style.FILL); 
		DroppingSprite.initFrictionRate(res); 
	}
	
	@Override
	public void reset() {
		int w = game.getW(); 
		this.moveTo(w >> 1, game.getH() >> 3); 
		this.speed.set(0, 0); 
	}
	
	@Override
	public void stepCalc(long dt) {
		float ax = this.acc.getScreenGx() - this.getFriction(this.speed.x); 
		float ay = this.acc.getScreenGy() - this.getFriction(this.speed.y); 
		this.speed.x += ax * dt; 
		this.speed.y += ay * dt; 
		
		PointF p1 = this.getCenter(); 
		this.move(speed.x, speed.y); 
//		Log.d(TAG, "v=" + speed + "/" + this.bounds); 
		PointF p2 = this.getCenter(); 
		
		if (boardGroup != null) {
			PointF prevSpeed = new PointF(speed.x, speed.y); 
			PointF impactPoint = boardGroup.getImpactPoint(r, p1, p2, speed); 
			if (impactPoint != null) {
				this.moveTo(impactPoint.x, impactPoint.y); 
				float maxDeltaSpeed = Math.max(Math.abs(prevSpeed.x - speed.x), Math.abs(prevSpeed.y - speed.y)); 
				if (maxDeltaSpeed > IMPACT_DSPEED) {
					vib.vibrate((int)maxDeltaSpeed); 
				}
			}
		}
		
		int w = this.game.getW(); 
		if (this.bounds.right < 0) {
			this.move(w, 0); 
		}
		if (this.bounds.left > w) {
			this.move(-w, 0); 
		}
		
	}
	
	public void updageBoardGroup(BoardGroup bgroup) {
		this.boardGroup = bgroup; 
	}
	
	public PointF getCenter() {
		return new PointF(this.bounds.centerX(), this.bounds.centerY()); 
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
		return frictionRate * speed * speed * speed; 
	}
	
	@Override
	public void save(SharedPreferences.Editor editor) {
		super.save(editor); 
		editor.putFloat(SPEED_X, this.speed.x); 
		editor.putFloat(SPEED_Y, this.speed.y); 
	}
	
	@Override
	public boolean load(SharedPreferences pref) {
		boolean ret = super.load(pref); 
		if (ret) {
			this.speed.x = pref.getFloat(SPEED_X, 0); 
			this.speed.y = pref.getFloat(SPEED_Y, 0); 
		}
		return ret; 
	}
}
