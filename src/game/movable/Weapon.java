package game.movable;

import game.core.EntityStuff;
import game.core.GameObject;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.objects.Particle;
import java.awt.geom.Point2D;

/**
 * The {@code Weapon} can act as a machine gun or rocker launcher. It is a turret, rotating towards the target(if enabled), spawning clones of the given projectile.
 * @author Pojahn Moradi
 *
 */
public class Weapon extends PathDrone
{
	private float targetX, targetY, firingOffsetX, firingOffsetY, rotationSpeed;
	private int burst, burstDelay, reload, burstCounter, delayCounter, reloadCounter;
	private boolean rotationAllowed, alwaysRotate, frontFire, firing, rotateWhileRecover;
	private GameObject targets[], currTarget;
	private Projectile proj;
	private Particle firingParticle;
	private Frequency<Image2D> firingImage, orgImage;
	private boolean usingTemp;
	
	/**
	 * Constructs a {@code Weapon}. The projectile to fire is set with {@code setProjectile} and is required to avoid a null pointer exception.
	 * @param x The X coordinate to start at.
	 * @param y The Y coordinate to start at.
	 * @param burst The amount of ammo the turret fires before reloading.
	 * @param burstDelay The amount of frames between shots.
	 * @param emptyReload The amount of frames to reload when running out of ammo.
	 * @param targets The units to be targeted and shot at.
	 */
	public Weapon(float x, float y, int burst, int burstDelay, int emptyReload, GameObject... targets)
	{
		super(x,y);
		this.burst = burst;
		this.reload = emptyReload;
		this.targets = targets;
		this.burstDelay = burstDelay;
		alwaysRotate = firing = false;
		rotationAllowed = rotateWhileRecover = true;
		burstCounter = reloadCounter = 0;
		delayCounter = burstDelay - 1;
	}

	@Override
	public void moveEnemy()
	{
		if(usingTemp && currImage.hasEnded())
		{
			usingTemp = false;
			firingImage = currImage;
			firingImage.reset();
			currImage = orgImage;
		}
		
		super.moveEnemy();

		if(!haveTarget())
			findTarget();
		
		rotateWeapon();
		
		if (--reloadCounter > 0)
			return;
		else if(reloadCounter == 0)
			rotationAllowed = true;
		
		if(haveTarget())
		{
			if(!haveBullets())
			{
				burstCounter = 0;
				delayCounter = burstDelay - 1;
				reloadCounter = reload;
				rotationAllowed = rotateWhileRecover;
				firing = false;
			}
			else if((firing || isTargeting()) && ++delayCounter % burstDelay == 0)
			{
				if(firingImage != null)
				{
					orgImage = currImage;
					currImage = firingImage;
					usingTemp = true;
				}
				rotationAllowed = false;
				firing = true;
				burstCounter++;
				
				Projectile projClone = null;
				Particle partClone = null;
				if(frontFire)
				{
					Point2D.Float front = getFrontPosition();
					projClone = proj.getClone(front.x - proj.width / 2 + firingOffsetX, front.y - proj.height / 2 + firingOffsetY);
					if(firingParticle != null)
						partClone = firingParticle.getClone(front.x - firingParticle.width / 2 + firingOffsetX, front.y - firingParticle.height / 2 + firingOffsetY);
				}
				else
				{
					projClone = proj.getClone(currX + firingOffsetX, currY + firingOffsetY);
					if(firingParticle != null)
						partClone = firingParticle.getClone(currX + firingOffsetX, currY + firingOffsetY);
				}
				projClone.setTarget(targetX, targetY);
				projClone.setDisposable(true);
				stage.add(projClone);
				if(partClone != null)
					stage.add(partClone);
			}
		}
	}
	
	/**
	 * Checks if the turret currently have a target.
	 * @return True if the turret have a target.
	 */
	public boolean haveTarget()
	{
		return currTarget != null;
	}
	
	/**
	 * Checks if the turret can fire instantly or have to reload.
	 * @return True if the turret can fire instantly.
	 */
	public boolean haveBullets()
	{
		return burst > burstCounter;
	}
	
	/**
	 * The projectile to fire.
	 * @param proj The {@code Projectile}.
	 */
	public void setProjectile(Projectile proj)
	{
		this.proj = proj;
	}
	
	/**
	 * The given values + the {@code Weapons} position will be the starting point of the bullet(unless {@code frontfire} is used).
	 * @param x The X offset.
	 * @param y The Y offset.
	 */
	public void setFiringOffsets(float x, float y)
	{
		firingOffsetX = x;
		firingOffsetY = y;
	}
	
	/**
	 * The {@code Particle} to spawn at the bullets firing position when attacking.
	 * @param firingParticle The particle.
	 */
	public void setFiringParticle(Particle firingParticle)
	{
		this.firingParticle = firingParticle;
	}
	
	/**
	 * Whether or not to fire at the front position, in case the image for example have a barrel. This is used together with {@code setRotationSpeed(>0)} and requires the used image to be formated in a special way.<br>
	 * Reefer to the tutorial list for usage.
	 * @param frontFire True to fire at the front of the barrel.
	 */
	public void setFrontFire(boolean frontFire)
	{
		this.frontFire = frontFire;
	}
	
	/**
	 * The rotation speed of the turret(0 default and disables). Enabling rotating also means that the turret have to face the target before allowed to fire.
	 * @param rotationSpeed The rotation speed. Should always be positive and low(example 0.09f).
	 */
	public void setRotationSpeed(float rotationSpeed)
	{
		this.rotationSpeed = rotationSpeed;
	}
	
	/**
	 * Determines if the turret should rotate even if no target is visible(rather than freezing).
	 * @param alwaysRotate True to enables constant rotation.
	 */
	public void setAlwaysRotate(boolean alwaysRotate)
	{
		this.alwaysRotate = alwaysRotate;
	}
	
	/**
	 * The image the use for the turret when firing. The turrets image will change back to its initial image when the last frame of the firing image have been rendered, meaning no support for looping firing image.
	 * @param firingImage The firing image.
	 */
	public void setFiringImage(Frequency<Image2D> firingImage)
	{
		this.firingImage = firingImage;
	}
	
	/**
	 * Whether or not to rotate towards the target when reloading rather than just freezing.
	 * @param rotateWhileRecover False to disable rotation while recovering.
	 */
	public void setRotateWhileRecover(boolean rotateWhileRecover)
	{
		this.rotateWhileRecover = rotateWhileRecover;
	}
	
	/**
	 * Initiate the current target.
	 */
	protected void findTarget()
	{
		currTarget = EntityStuff.findClosestSeeable(this, targets);
	}
	
	/**
	 * Tries to rotate the turret towards the current target.
	 */
	protected void rotateWeapon()
	{
		if(rotationAllowed && rotationSpeed != 0.0f && haveTarget())
		{
			float x1 = currX + width / 2,
				  y1 = currY + height / 2,
				  x2 = currTarget.currX + currTarget.width / 2,
				  y2 = currTarget.currY + currTarget.height / 2;
			
			if(alwaysRotate || currTarget.canSee(this, Accuracy.MID))
				rotation = EntityStuff.rotateTowardsPoint(x1,y1,x2,y2, rotation, rotationSpeed);
		}
	}
	
	/**
	 * Checks if the turret is currently targeting the current target.
	 * @return True if the turret is aiming at a target.
	 */
	protected boolean isTargeting()
	{
		if(rotationSpeed == 0.0f)
		{
			Point2D.Float position = EntityStuff.findEdgePoint(this, currTarget);
			targetX = position.x;
			targetY = position.y;
			return canSee(currTarget, Accuracy.MID);
		}
		
		float centerX = currX + width / 2;
		float centerY = currY + height / 2;
		
		Point2D.Float front = getFrontPosition();
		Point2D.Float edge = EntityStuff.findEdgePoint(centerX, centerY, front.x, front.y);
		Point2D.Float wall = EntityStuff.findWallPoint(centerX, centerY, edge.x, edge.y);
		
		GameObject dummy = new GameObject();
		dummy.currX = currTarget.currX + currTarget.width / 2;
		dummy.currY = currTarget.currY + currTarget.height / 2;
		boolean targeting = EntityStuff.checkLine((int)centerX, (int)centerY, (int)wall.x, (int)wall.y, dummy);
		if(targeting)
		{
			Point2D.Float edge2 = EntityStuff.findEdgePoint(this, dummy);
			targetX = edge2.x;
			targetY = edge2.y;
		}
		return targeting;
	}
}