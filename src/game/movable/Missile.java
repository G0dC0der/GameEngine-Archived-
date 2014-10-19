package game.movable;

import game.core.Fundementals;
import game.core.GameObject;
import game.essentials.Image2D;
import game.objects.Particle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * The {@code Missile} class represent a heat seeking missile, moving in a "curve". The missile will be launched at the closest seeable target and respawn on its initial position upon impact.<br><br>
 * Further more, any target hit by the missile will have its {@code HitEvent} fired with the missile passed as argument.<br>
 * Finally, when the missile is idle and scanning for a target, the image is not rendered.
 * @author Pojahn Moradi
 */
public class Missile extends Projectile
{
	/**
	 * These constants offer an easy and optional way modify the missiles properties.
	 * @author Pojahn Moradi
	 */
	public enum MissileProperties
	{
		SLOW_ACCURARTE,
		MEDIUM_FLOATY,
		FAST_VERY_FLOATY,
		INACCURATE,
		SLOW_CHASE;
	}
	
	/**
	 * Change this to alter the missiles property.
	 */
	public float thrust, drag, delta;
	private float vx, vy;
	private int trailerDelay, delayCounter, reloadCounter;
	private boolean faceTarget, pointAtDirection;
	private Particle trailer;
	GameObject currTarget;
	
	/**
	 * Creates a {@code Missile}.
	 * @param initialX The starting and initial X position.
	 * @param initialY The starting and initial Y position.
	 * @param targets The target to scan for and launch at.
	 */
	public Missile(float initialX, float initialY, GameObject... targets) 
	{
		super(initialX, initialY, targets);
		vx = 1;
		trailerDelay = 3;
		faceTarget = true;
		setProperties(MissileProperties.MEDIUM_FLOATY);
	}
	
	@Override
	public Missile getClone(float x, float y)
	{
		Missile m = new Missile(x, y, getTargets());
		copyData(m);
		
		if(cloneEvent != null)
			cloneEvent.cloned(m);
		
		return m;
	}
	
	protected void copyData(Missile dest)
	{
		super.copyData(dest);
		dest.faceTarget = faceTarget;
		dest.thrust = thrust;
		dest.drag = drag;
		dest.delta = delta;
		dest.trailerDelay = trailerDelay;
		dest.trailer = trailer;
		dest.currTarget = currTarget;
	}
	
	@Override
	public void moveEnemy()
	{		
		visible = false;
		
		if (--reloadCounter > 0)
			return;
			
		if (scanAllowed && (currTarget == null || !haveTarget()))
		{
			GameObject go = findTarget();
			if(go != null)
			{
				targetX = go.currX + go.width  / 2;
				targetY = go.currY + go.height / 2;
				currTarget = go;
			}
		}
		if (haveTarget())
		{
			if(currTarget != null)
			{
				targetX = currTarget.currX + currTarget.width  / 2;
				targetY = currTarget.currY + currTarget.height / 2;
			}
			
			float dx = targetX - currX;
			float dy = targetY - currY;
			double length = Math.sqrt( dx*dx + dy*dy );
			dx /= length;
			dy /= length;
				 
			float accelx = thrust * dx - drag * vx;
			float accely = thrust * dy - drag * vy;
		 
			vx = vx + delta * accelx;
			vy = vy + delta * accely;
			
			currX = currX + delta * vx;
			currY = currY + delta * vy;
			
			visible = true;
			sounds.trySound(0, false);
			
			checkCollisions();
		}
	}
	
	/**
	 * Sets the {@code thrust, drag} and {@code delta} to a set of predefined values, depending on the argument.
	 * @param prop The property.
	 */
	public void setProperties(MissileProperties prop)
	{
		switch(prop)
		{
		case SLOW_ACCURARTE:
			thrust = 400f;
			drag = 3.5f;
			delta = (float)1f/50f;
			break;
		case MEDIUM_FLOATY:
			thrust = 400f;
			drag = 2f;
			delta = (float)1f/60f;
			break;
		case FAST_VERY_FLOATY:
			thrust = 400f;
			drag = 1f;
			delta = (float)1f/40f;
			break;
		case INACCURATE:
			thrust = 400f;
			drag = 0.7f;
			delta = (float)1f/50f;
			break;
		case SLOW_CHASE:
			thrust = 400f;
			drag = 10f;
			delta = (float)1f/60f;			
			break;
		}
	}
	
	/**
	 * Whether or not this missile should always point at the target.
	 * @param faceTarget True if this missile should always point at its current target.
	 */
	public void setFaceTarget(boolean faceTarget)
	{
		this.faceTarget = faceTarget;
	}
	
	/**
	 * Whether or not this missile should rotate towards its direction.
	 * @param pointAtDirection True to rottate towards its current direction.
	 */
	public void pointAtDirection(boolean pointAtDirection)
	{
		this.pointAtDirection = pointAtDirection;
	}
	
	/**
	 * The animation to spawn at the missiles tail.
	 * @param trailer The animation.
	 */
	public void setTrailer(Particle trailer)
	{
		this.trailer = trailer;
	}
	
	/**
	 * How often to spawn the trailer(every N frames, where N is {@code trailerDelay}).
	 * @param trailerDelay The value.
	 */
	public void setTrailerDelay(int trailerDelay)
	{
		this.trailerDelay = trailerDelay;
	}
	
	@Override
	public Image2D getFrame()
	{
		if(!visible)
			return null;
		else
			return image.getObject();
	}
		
	@Override
	public void drawSpecial(SpriteBatch g)
	{
		float centerX = currX + width / 2;
		float centerY = currY + height / 2;
		
			if(faceTarget)
				rotation = (float) Fundementals.getAngle(centerX, centerY, targetX, targetY);
			else if (pointAtDirection)
				rotation = (float) Math.toDegrees(Math.atan2(vy, vx));
		
		if(visible && haveTarget() && trailer != null && ++delayCounter % trailerDelay == 0)
		{
			if(pointAtDirection)
			{
				Vector2 loc = getRarePosition();
				stage.add(trailer.getClone(loc.x - trailer.width / 2, loc.y - trailer.height / 2));
			}
			else
				stage.add(trailer.getClone(centerX - trailer.width / 2, centerY - trailer.height / 2));
		}
	}
	
	@Override
	protected void hit(GameObject subject)
	{
		super.hit(subject);
		vx = 1;
		vy = 0;
	}
}