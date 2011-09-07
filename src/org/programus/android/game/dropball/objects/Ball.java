package org.programus.android.game.dropball.objects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

/**
 * The BALL! which is the most important thing in this game and is the hero of this game. 
 * @author Programus
 */
public class Ball extends DroppingSprite implements Const {
	/** {@linkplain Paint} object for drawing the ball.  */
	private Paint paint;
	/** the game */
	private DroppingBallGame game; 
	
	/** object to get accelerometer data from */
	private AccData acc; 
	
	/** the radius of the ball */
	private float r; 
	/** speed in unit: px/second */
	private PointF speed; 
	
	/** 
	 * Which side of any board the ball is moving on. 0 means the ball is alone. 
	 * @see {@linkplain BoardGroup#SIDE_ABOVE}
	 * @see {@linkplain BoardGroup#SIDE_UNDER}
	 * @see {@linkplain BoardGroup#SIDE_LEFT}
	 * @see {@linkplain BoardGroup#SIDE_RIGHT}
	 */
	private int sideOnBoard; 
	
	private int gforceColor; 
	private int nforceColor;
	private int cforceColor;
	private int lineColor; 
	private int speedColor; 
	
	private PathEffect linePE; 
	
	/** key for save */
	private static final String SPEED_X = "ball.speed.x"; 
	/** key for save */
	private static final String SPEED_Y = "ball.speed.y";
	/** The min change of speed when impact. If the speed didn't change so much, the impact effect will not be enabled. */
	private static final float IMPACT_DSPEED = 5; 
	/** The possible max speed of the ball. It is calculated from the friction rate and gravity. */
	private final float MAX_SPEED; 
	
	/** vibrator for impact effect. */
	private Vibrator vib; 
	/** sound pool for impact effect. */
	private SoundPool soundPool; 
	/** for impact sound effect. */
	private AudioManager am; 
	/** The sound vol gap between the most left and most right. To make a stereo sound effect. */
	private final static float LR_GAP = .3F; 
	/** for impact sfx. */
	private int impactSoundId; 
	
	/** A list of rects to draw the tail shadows. */
	private List<RectF> shadows = Collections.synchronizedList(new LinkedList<RectF>()); 
	/** You cannot get shadows more than this */
	private int maxShadowNum = 100; 
	/** If you got a shadow effect, you must have shadows more than this */
	private int minShadowNum = 90; 
	private int maxShadowAlpha = 0x8f; 
	private int minShadowAlpha = 0x00; 
	/** Leave a shadow every <i>createShadowInterval</i> frames */
	private int createShadowInterval = 2; 
	/** Count time to know whether it reaches the createShadowInterval */
	private int timeCounter = 0; 
	
	/**
	 * Just a boring constructor that initialize everything it can initialize. 
	 * @param game the game the ball plays in. 
	 */
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
		// reset the position. 
		this.moveTo(w >> 1, game.getH() >> 3); 
		this.speed.set(0, 0); 
		this.shadows.clear(); 
	}
	
	/* (non-Javadoc)
	 * @see org.programus.android.game._engine.objects.SpriteLike#stepCalc(long)
	 */
	@Override
	public void stepCalc(long dt) {
		BoardGroup boardGroup = game.getObjects().getBoardGroup(); 
		this.timeCounter += 1; 
		if (this.game.getStatus() == this.game.STATUS_PLAYING) {
			Log.d(TAG, "dt=" + dt); 
			// if playing the game. 
			this.sideOnBoard = 0; // don't worry about whether the ball is on the board here. 
			// calculate the acceleration for each direction. 
			float ax = this.acc.getScreenGx() - this.getFriction(this.speed.x); 
			float ay = this.acc.getScreenGy() - this.getFriction(this.speed.y); 
			// v = a * dt
			this.speed.x += ax * dt; 
			this.speed.y += ay * dt; 
			
			// record the point before move the ball. 
			PointF p1 = this.getCenter(); 
			// move it in spite there might be a obstacle. 
			this.move(speed.x, speed.y); 
			// record the point after moving. 
			PointF p2 = this.getCenter(); 
			
			if (boardGroup != null) {
				// record the speed before detecting impact. 
				// For detect whether an impact happened. (Remember the IMPACT_DSPEED?)
				PointF prevSpeed = new PointF(speed.x, speed.y); 
				// Detect the impact point. It will be null if there is no impact happened. 
				PointF impactPoint = boardGroup.getImpactPoint(r, p1, p2, speed); 
				if (impactPoint != null) {
					// An impact happened. 
					// The ball cannot move through any board, so has back to the impact point. 
					// Here is a known issue: the ball will vibrate up and down a little when moving on a board.
					this.moveTo(impactPoint.x, impactPoint.y); 
					// detect impact strength and play an impact effect if necessary. 
					this.impactEffect(prevSpeed, this.bounds.centerX()); 
				}
			}
			
			// detect left and right edge. 
			int w = this.game.getW(); 
			if (this.bounds.right < 0) {
				// the ball will be moved to the most left if it move out of the screen from right. 
				this.move(w, 0); 
			}
			if (this.bounds.left > w) {
				// the same for left edge. see previous comment for details. 
				this.move(-w, 0); 
			}
			
			// move all shadows. shadows should be moved in the speed of boards. 
			// this makes the shadows look still related to the boards. 
			this.moveShadows(); 
			// detect whether it is the time to leave a new shadow. 
			if (this.timeCounter >= this.createShadowInterval) {
				this.shadows.add(new RectF(this.bounds)); 
				this.timeCounter = 0;
			}
		} else {
			// when game is pausing or a new game. 
			if (boardGroup != null) {
				// just let me know whether the ball is on a board and what side it is on if so. 
				// this is to draw the n force
				this.sideOnBoard = boardGroup.getSideOnBoard(r, getCenter(), speed); 
			}
		}
		// calculate the number of the shadow. the more score you got, the more shadow you have. 
		int n = (int)(this.game.getObjects().getScore().getScore() >> 16) + minShadowNum; 
		// but no more than the max. 
		if (n > this.maxShadowNum) {
			n = this.maxShadowNum; 
		}
		
		// remove old shadows. 
		synchronized(this.shadows) {
			while (this.shadows.size() > n) {
				shadows.remove(0); 
			}
		}
		
		// About the shadow removal, there is another solution that
		// give every shadow a lifetime, if it is the end of the life time, 
		// remove the shadow... 
		// This solution will also remove shadows in paused scene.
		// maybe more interesting and cooler...
	}
	
	private void moveShadows() {
		for (RectF rect : shadows) {
			// shadows move in the same speed as boards. 
			rect.offset(0, -Board.getSpeed()); 
		}
	}
	
	/**
	 * make impact effect both in sound and vibrate, or more in the future...
	 * @param prevSpeed the previous speed
	 * @param x the x of the current ball location. for stereo calculation. 
	 */
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
	
	/**
	 * Return the center point of the ball. 
	 * @return the center point of the ball. 
	 */
	public PointF getCenter() {
		return new PointF(this.bounds.centerX(), this.bounds.centerY()); 
	}
	
	@Override
	public void draw(Canvas canvas) {
		this.drawShadows(canvas); 
		// decoration includes force lines, speed lines
		this.drawDecorationAndBall(canvas, bounds); 
		// if the ball is out of screen on x axis,
		// an alt ball need to be drawn
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
			this.drawDecorationAndBall(canvas, altBounds); 
		}
	}
	
	private void drawShadows(Canvas canvas) {
		int oldAlpha = paint.getAlpha(); 
		paint.setStyle(Paint.Style.STROKE); 
		double alpha = minShadowAlpha; 
		double step = (maxShadowAlpha - minShadowAlpha) / (double)shadows.size(); 
		for (RectF rect : this.shadows) {
			alpha += step; 
			paint.setAlpha((int)alpha); 
			canvas.drawOval(rect, paint); 
			// same as alt bounds of ball. 
			RectF alt = null; 
			int w = canvas.getWidth(); 
			if (rect.left < 0) {
				alt = new RectF(rect); 
				alt.offset(w, 0); 
			}
			if (rect.right > w) {
				alt = new RectF(rect); 
				alt.offset(-w, 0); 
			}
			if (alt != null) {
				canvas.drawOval(alt, paint); 
			}
		}
		paint.setAlpha(oldAlpha); 
	}
	
	private void drawDecorationAndBall(Canvas canvas, RectF rect) {
		boolean playingGame = this.game.getStatus() == this.game.STATUS_PLAYING; 
		Paint.Style oldStyle = paint.getStyle(); 
		int oldColor = paint.getColor(); 
		PathEffect oldPE = paint.getPathEffect(); 
		
		this.drawSpeedLine(canvas, rect); 
		paint.setStyle(Paint.Style.FILL); 
		paint.setColor(oldColor); 
		paint.setPathEffect(oldPE); 
		canvas.drawOval(rect, paint); 
		if (!playingGame) {
			this.drawForceLines(canvas, rect); 
		}
		
		paint.setStyle(oldStyle); 
		paint.setColor(oldColor); 
		paint.setPathEffect(oldPE); 
	}
	
	private void drawForceLines(Canvas canvas, RectF rect) {
		final float M = 20; 
		final float STD_ARROW_LEN = 7; 
		final float STD_ARROW_WIDTH = 3; 
		float fx = -AccData.getInstance().getGx() * M; 
		float fy = AccData.getInstance().getGy() * M; 
		double f = Math.sqrt(fx * fx + fy * fy); 
		double degrees = Math.toDegrees(Math.atan2(fy, fx)); 
		
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
	}
	
	private void drawSpeedLine(Canvas canvas, RectF rect) {
		final float RATE = 10; 
		final float SPEED_WIDTH = 2; 
		final float OFFSET = 0; 
		Path mPath = new Path(); 
		float lx = (this.sideOnBoard == BoardGroup.SIDE_LEFT || this.sideOnBoard == BoardGroup.SIDE_RIGHT) ? 0 : speed.x * RATE; 
		float ly = (this.sideOnBoard == BoardGroup.SIDE_ABOVE || this.sideOnBoard == BoardGroup.SIDE_UNDER) ? 0 : (speed.y + Board.getSpeed()) * RATE; 
		double l = Math.sqrt(lx * lx + ly * ly); 
		double degrees = Math.toDegrees(Math.atan2(ly, lx)); 
		
		mPath.moveTo((float)-l, 0); 
		mPath.lineTo(0, -SPEED_WIDTH); 
		mPath.lineTo(0, SPEED_WIDTH); 
		mPath.close(); 
		
		Path uPath = new Path(); 
		Path dPath = new Path(); 
		
		uPath.addPath(mPath, rect.centerX() - OFFSET, rect.centerY() - r + SPEED_WIDTH); 
		dPath.addPath(mPath, rect.centerX() - OFFSET, rect.centerY() + r - SPEED_WIDTH); 
		mPath.offset(rect.centerX() - (OFFSET + r), rect.centerY()); 

		paint.setStyle(Paint.Style.FILL); 
		paint.setColor(speedColor); 
		paint.setPathEffect(null); 
		canvas.save(); 
		canvas.rotate((float)degrees, rect.centerX(), rect.centerY()); 
		canvas.drawPath(mPath, paint); 
		canvas.drawPath(uPath, paint); 
		canvas.drawPath(dPath, paint); 
		canvas.restore(); 
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
