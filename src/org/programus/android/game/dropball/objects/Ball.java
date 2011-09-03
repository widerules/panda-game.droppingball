package org.programus.android.game.dropball.objects;

import org.programus.android.game.R;
import org.programus.android.game._engine.data.AccData;
import org.programus.android.game._engine.utils.Const;
import org.programus.android.game.dropball.DroppingBallGame;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;

public class Ball extends DroppingSprite implements Const {
	private Paint paint;
	private DroppingBallGame game; 
	
	private AccData acc; 
	
	private BoardGroup boardGroup; 
	
	private float r; 
	private PointF speed; 
	
	private int sideOnBoard; 
	
	private int gforceColor; 
	private int nforceColor;
	private int cforceColor;
	private int lineColor; 
	private int speedColor; 
	
	private PathEffect linePE; 
	
	private static final String SPEED_X = "ball.speed.x"; 
	private static final String SPEED_Y = "ball.speed.y";
	private static final float IMPACT_DSPEED = 5; 
	private final float MAX_SPEED; 
	
	private Vibrator vib; 
	private SoundPool soundPool; 
	private AudioManager am; 
	private final static float LR_GAP = .3F; 
	private int impactSoundId; 
	
	public Ball(DroppingBallGame game) {
		this.game = game; 
		this.acc = AccData.getInstance(); 
		
		this.vib = (Vibrator) game.getContext().getSystemService(Context.VIBRATOR_SERVICE); 
		this.soundPool = new SoundPool(5, AudioManager.STREAM_RING, 0); 
		this.impactSoundId = this.soundPool.load(game.getContext(), R.raw.impact, 1); 
		this.am = (AudioManager) game.getContext().getSystemService(Context.AUDIO_SERVICE); 
		this.speed = new PointF(); 
		
		Resources res = this.game.getContext().getResources(); 
		r = res.getDimension(R.dimen.ballRadius); 
		bounds.set(-r, -r, r, r); 
		
		int foreColor = res.getColor(R.color.foreColor); 
		paint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		paint.setColor(foreColor); 
		paint.setStrokeWidth(1); 
		paint.setStyle(Paint.Style.FILL); 
		DroppingSprite.initFrictionRate(res); 
		MAX_SPEED = (float)Math.pow(10 * PX_RATE / frictionRate, 1./3); 
		Log.d(TAG, "MAX_SPEED=" + MAX_SPEED); 
		
		this.gforceColor = res.getColor(R.color.gforce); 
		this.nforceColor = res.getColor(R.color.nforce); 
		this.cforceColor = res.getColor(R.color.cforce); 
		this.lineColor = res.getColor(R.color.lines); 
		this.speedColor = res.getColor(R.color.speed); 
		this.linePE = new DashPathEffect(new float[]{3, 4}, 0); 
	}

	@Override
	public void reset() {
		int w = game.getW(); 
		this.moveTo(w >> 1, game.getH() >> 3); 
		this.speed.set(0, 0); 
	}
	
	@Override
	public void stepCalc(long dt) {
		if (this.game.getStatus() == this.game.STATUS_PLAYING) {
			float ax = this.acc.getScreenGx() - this.getFriction(this.speed.x); 
			float ay = this.acc.getScreenGy() - this.getFriction(this.speed.y); 
			this.speed.x += ax * dt; 
			this.speed.y += ay * dt; 
			
			PointF p1 = this.getCenter(); 
			this.move(speed.x, speed.y); 
			PointF p2 = this.getCenter(); 
			
			if (boardGroup != null) {
				PointF prevSpeed = new PointF(speed.x, speed.y); 
				PointF impactPoint = boardGroup.getImpactPoint(r, p1, p2, speed); 
				if (impactPoint != null) {
					this.moveTo(impactPoint.x, impactPoint.y); 
					this.impactEffect(prevSpeed, this.bounds.centerX()); 
				}
			}
			
			int w = this.game.getW(); 
			if (this.bounds.right < 0) {
				this.move(w, 0); 
			}
			if (this.bounds.left > w) {
				this.move(-w, 0); 
			}
		} else {
			if (boardGroup != null) {
				this.sideOnBoard = boardGroup.getSideOnBoard(r, getCenter(), speed); 
			}
		}
	}
	
	private void impactEffect(PointF prevSpeed, float x) {
		float maxDeltaSpeed = Math.max(Math.abs(prevSpeed.x - speed.x), Math.abs(prevSpeed.y - speed.y)); 
		if (maxDeltaSpeed > IMPACT_DSPEED) {
			if (this.impactSoundId != 0) {
				float maxVol = (float) am.getStreamMaxVolume(AudioManager.STREAM_RING) / am.getStreamMaxVolume(AudioManager.STREAM_RING); 
				Log.d(TAG, "MaxDeltaSpeed:" + maxDeltaSpeed); 
				float masterVol = maxVol * (maxDeltaSpeed / MAX_SPEED); 
				if (masterVol > 1) {
					masterVol = 1; 
				}
				int w = game.getW(); 
				float leftGap = LR_GAP * ((x + w) % w) / w;
				float rightGap = LR_GAP - leftGap; 
				this.soundPool.play(this.impactSoundId, masterVol - leftGap, masterVol - rightGap, 1, 0, 1); 
			}
			vib.vibrate((int)maxDeltaSpeed); 
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
		boolean playingGame = this.game.getStatus() == this.game.STATUS_PLAYING; 
		canvas.drawOval(bounds, paint); 
		if (!playingGame) {
			this.drawDecoration(canvas, bounds); 
		}
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
			if (!playingGame) {
				this.drawDecoration(canvas, altBounds); 
			}
		}
	}
	
	private void drawDecoration(Canvas canvas, RectF rect) {
		this.drawForceLines(canvas, rect); 
//		if (sideOnBoard != 0) {
//			this.drawForceLine(canvas, rect, new PointF(0, fy)); 
//			this.drawForceLine(canvas, rect, new PointF(fx, 0)); 
//		}
	}
	
	private void drawForceLines(Canvas canvas, RectF rect) {
		final float M = 20; 
		final float STD_ARROW_LEN = 7; 
		final float STD_ARROW_WIDTH = 3; 
		float fx = -AccData.getInstance().getGx() * M; 
		float fy = AccData.getInstance().getGy() * M; 
		double f = Math.sqrt(fx * fx + fy * fy); 
		double degrees = Math.toDegrees(Math.atan2(fy, fx)); 
		
		Paint.Style oldStyle = paint.getStyle(); 
		int oldColor = paint.getColor(); 
		PathEffect oldPE = paint.getPathEffect(); 
		
		// create g path and g line path. 
		Path gpath = new Path(); 
		Path gplinePath = new Path(); 
		gpath.moveTo(0, 0); 
		gpath.lineTo((float)f, 0); 
		gplinePath.addPath(gpath); 
		float arrowLength = Math.min((float)f/2, STD_ARROW_LEN); 
		gpath.rLineTo(-arrowLength, STD_ARROW_WIDTH / 2); 
		gpath.rLineTo(0, -STD_ARROW_WIDTH); 
		gpath.lineTo((float)f, 0); 
		gpath.offset(rect.centerX(), rect.centerY()); 
		
		// draw g. 
		canvas.save(); 
		canvas.rotate((float)degrees, rect.centerX(), rect.centerY()); 
		paint.setPathEffect(null); 
		paint.setStyle(Paint.Style.STROKE); 
		paint.setColor(this.gforceColor); 
		canvas.drawPath(gpath, paint); 
		canvas.restore(); 
		
		if (
				(fy > 0 && this.sideOnBoard == BoardGroup.SIDE_ABOVE) ||
				(fy < 0 && this.sideOnBoard == BoardGroup.SIDE_UNDER) ||
				(fx > 0 && this.sideOnBoard == BoardGroup.SIDE_LEFT) ||
				(fx < 0 && this.sideOnBoard == BoardGroup.SIDE_RIGHT)) {
			// create n path, c path and n line path.
			Path npath = new Path(); 
			Path nlinePath = new Path(); 
			Path cpath = new Path(); 
			npath.moveTo(0, 0); 
			cpath.moveTo(0, 0); 
			Path xpath = null; 
			Path ypath = null; 
			float xx = fx; 
			float yy = fy; 
			float glx = rect.centerX();
			float gly = rect.centerY();
			if (this.sideOnBoard == BoardGroup.SIDE_ABOVE || this.sideOnBoard == BoardGroup.SIDE_UNDER) {
				xpath = cpath; 
				ypath = npath; 
				yy = -yy; 
				gly -= fy; 
			} else {
				xpath = npath; 
				ypath = cpath; 
				xx = -xx; 
				glx -= fx; 
			}
			xpath.lineTo(xx, 0); 
			ypath.lineTo(0, yy); 
			nlinePath.addPath(npath, fx, fy); 
			arrowLength = Math.min(Math.abs(yy)/2, STD_ARROW_LEN); 
			ypath.rLineTo(STD_ARROW_WIDTH / 2, yy < 0 ? arrowLength : -arrowLength); 
			ypath.rLineTo(-STD_ARROW_WIDTH, 0); 
			ypath.lineTo(0, yy); 
			arrowLength = Math.min(Math.abs(xx)/2, STD_ARROW_LEN); 
			xpath.rLineTo(xx < 0 ? arrowLength : -arrowLength, STD_ARROW_WIDTH / 2); 
			xpath.rLineTo(0, -STD_ARROW_WIDTH); 
			xpath.lineTo(xx, 0); 
			
			// draw n force & c force & n line
			canvas.save(); 
			canvas.translate(rect.centerX(), rect.centerY()); 
			paint.setPathEffect(null); 
			paint.setStyle(Paint.Style.STROKE); 
			paint.setColor(nforceColor); 
			canvas.drawPath(npath, paint); 
			paint.setColor(cforceColor); 
			canvas.drawPath(cpath, paint); 
			paint.setPathEffect(linePE); 
			paint.setColor(lineColor); 
			canvas.drawPath(nlinePath, paint); 
			canvas.restore(); 
			
			// draw g line
			gplinePath.offset(glx, gly); 
			canvas.save(); 
			canvas.rotate((float)degrees, glx, gly); 
			paint.setPathEffect(linePE); 
			paint.setStyle(Paint.Style.STROKE); 
			paint.setColor(lineColor); 
			canvas.drawPath(gplinePath, paint); 
			canvas.restore(); 
		}		
		
		paint.setStyle(oldStyle); 
		paint.setColor(oldColor); 
		paint.setPathEffect(oldPE); 
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
