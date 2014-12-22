package game.movable;

import game.core.Enemy;
import game.core.Engine;
import game.core.Fundementals;
import game.core.GameObject;
import game.core.Stage;
import game.essentials.Animation;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import game.objects.Particle;
import com.badlogic.gdx.math.Vector2;
import kuusisto.tinysound.Sound;

/**
 * This enemy chases the closest given target if not to far away and launches its {@code HitEvent} upon collision. <br>
 * The movement is similar to the {@code Missile},  but the big difference between the two is that the {@code EvilDog} go through walls rather than exploding.
 * @author Pojahn Moradi
 */
public class EvilDog extends Enemy
{
	public float thrust, drag, delta, vx, vy;
	private float maxDistance;
	private boolean hunting;
	private GameObject[] targets;
	private Particle impact;
	private Animation<Image2D> idleImg, huntImg;
	
	/**
	 * Constructs an {@code EvilDog} at the given point with {@code thrust, drag} and {@code delta} set to move very fast.
	 * @param x The x coordinate to start at.
	 * @param y The y coordinate to start at.
	 * @param maxDistance The maximum distance operating at.
	 * @param targets The objects to hunt.
	 */
	public EvilDog(float x, float y, float maxDistance, GameObject... targets)
	{
		super();
		moveTo(x,y);
		this.maxDistance = maxDistance;
		this.targets = targets;
		thrust = 500f;
		drag = .5f;
		delta = Engine.DELTA;
		
		sounds = new SoundBank(1); //Collision
		sounds.setEmitter(this);
		sounds.setDelay(0, 60);
	}
	
	/**
	 * The {@code Particle} to display when colliding with a target.
	 * @param impact The impact animation.
	 */
	public void collisionAnim(Particle impact)
	{
		this.impact = impact;
	}
	
	/**
	 * The sound to play when colliding with a target.
	 * @param sound The sound.
	 */
	public void setCollisionSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}
	
	/**
	 * How often to play the collision sound when colliding with a target.
	 * @param delay The time in frames.
	 */
	public void setCollisionSoundDelay(int delay)
	{
		sounds.setDelay(0, delay);
	}
	
	/**
	 * Returns true if the dog is currently hunting a target.
	 */
	public boolean hunting()
	{
		return hunting;
	}
	
	/**
	 * The image to use when all targets are out of range.
	 * @param idleImg The image.
	 */
	public void idleImage(Animation<Image2D> idleImg)
	{
		this.idleImg = idleImg;
	}
	
	@Override
	public void setImage(Animation<Image2D> obj) 
	{
		huntImg = obj;
		super.setImage(obj);
	}

	@Override
	public void moveEnemy() 
	{
		if(isFrozen())
			return;
		
		GameObject closest = Fundementals.findClosest(this, targets);

		if(closest != null && (maxDistance < 0 || maxDistance > Fundementals.distance(this, closest)))
		{
			image = huntImg;
			hunting = true;
			
			Vector2 norP = Fundementals.normalize(closest, this);
			
			float accelx = thrust * norP.x - drag * vx;
			float accely = thrust * norP.y - drag * vy;
		 
			vx += delta * accelx;
			vy += delta * accely;

			loc.x += delta * vx;
			loc.y += delta * vy;
			
			if(collidesWith(closest))
			{
				if(impact != null)
					Stage.getCurrentStage().add(impact.getClone(loc.x, loc.y));
				
				closest.runHitEvent(this);
				sounds.trySound(0, true);
			}
		}
		else if(idleImg != null)
		{
			hunting = false;
			image = idleImg;
		}
	}
}