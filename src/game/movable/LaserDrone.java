package game.movable;

import game.core.Engine;
import game.core.EntityStuff;
import game.core.GameObject;
import game.essentials.Factory;
import game.essentials.LaserBeam;
import game.essentials.SoundBank;
import game.objects.Particle;

import java.awt.geom.Point2D;

import kuusisto.tinysound.Sound;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * This unit fire laser at the closest seeable(customizable) target. Any target intersecting with the laser beam will have its {@code HitEvent} fired with the laser drone as argument.<br>
 * The laser travels until the {@code stopTile} is hit(default: solid).
 * @author Pojahn Moradi
 */
public class LaserDrone extends PathDrone
{
	protected float targetX, targetY;
	private int laserStartup, laserDuration, reload, sucounter, ducounter, reloadCounter;
	private byte stopTile;
	private boolean scan, firing;
	private Particle exp;
	private Color laserTint;
	private final GameObject[] targets;
	private LaserBeam firingBeam, chargeBeam;
	
	/**
	 * Creates a laser drone.
	 * @param x The starting X position.
	 * @param y The starting Y position.
	 * @param laserStartup The amount of frames before the laser comes out when a target has been detected.
	 * @param laserDuration The amount of frames the laser stays active(and deadly).
	 * @param reload The amount of frames to wait before scanning for a new target after this unit have finished its current firing mission.
	 * @param targets The units capable of being hit and targeted by this unit.
	 */
	public LaserDrone(float x, float y, int laserStartup, int laserDuration, int reload, GameObject... targets)
	{
		super(x, y);
		this.laserStartup = laserStartup;
		this.laserDuration = laserDuration;
		this.targets = targets;
		this.reload = reload;
		this.scan = true;
		targetX = targetY = -1;
		sucounter = ducounter = reloadCounter = 0;
		sounds = new SoundBank(2);	//0 = startup, 1 = firing
		stopTile = Engine.SOLID;
		laserTint = Color.valueOf("CC0000FF");
		firingBeam = Factory.defaultLaser();
		chargeBeam = Factory.defaultChargeLaser();
	}
	
	/**
	 * Creates a {@code LaserDrone} with a customized laser beam. Reefer to the constructor float, float, int, int, int, GameObject[] for more details.
	 */
	public LaserDrone(float x, float y, int laserStartup, int laserDuration, int reload, LaserBeam firingBeam, LaserBeam chargeBeam, GameObject... targets)
	{
		this(x,y,laserStartup,laserDuration,reload,targets);
		this.firingBeam = firingBeam;
		this.chargeBeam = chargeBeam;
		
	}
	
	/**
	 * The sound to play when the laser is starting.
	 * @param startupSound The sound.
	 */
	public void setStartupSound(Sound startupSound)
	{
		sounds.setSound(0, startupSound);
	}
	
	/**
	 * The sound to play when the laser is active(and damaging).
	 * @param firingSound The sound.
	 */
	public void setFiringSound(Sound firingSound)
	{
		sounds.setSound(1, firingSound);
	}
	
	/**
	 * The animation to append at the lasers edge.
	 * @param exp The animation.
	 */
	public void setExplosion(Particle exp)
	{
		this.exp = exp;
	}
	
	/**
	 * Checks if this unit currently have a target.
	 * @return True if this {@code LaserDrone} have a target.
	 */
	public boolean haveTarget()
	{
		return targetX != -1;
	}
	
	/**
	 * The tile type to consider as a wall.
	 * @param stopTile The tile type.
	 */
	public void setStopTile(byte stopTile)
	{
		this.stopTile = stopTile;
	}
	
	/**
	 * Whether or not to fire at visible target.
	 * @param fireAtVisible Set to true to fire at seeable targets or false to fire the laser at the closest target even if there is a wall between them.
	 */
	public void fireAtVisible(boolean fireAtVisible)
	{
		scan = fireAtVisible;
	}
	
	/**
	 * Allows you to customize the laser tinting.
	 * @param tint The tint the lasers should use.
	 */
	public void setLaserTint(Color tint)
	{
		this.laserTint = tint;
	}
	
	/**
	 * Returns the firing beam of the {@code LaserDrone}.
	 * @return The beam.
	 */
	public LaserBeam getFiringBeam() 
	{
		return firingBeam;
	}

	/**
	 * The laser beam to use when firing.
	 * @param firingBeam The beam to use.
	 */
	public void setFiringBeam(LaserBeam firingBeam) 
	{
		this.firingBeam = firingBeam;
	}
	
	/**
	 * Returns the charge beam of the {@code LaserDrone}.
	 * @return
	 */
	public LaserBeam getChargeBeam() 
	{
		return chargeBeam;
	}
	
	/**
	 * The laser beam to use when charging.
	 * @param chargeBeam The beam to use.
	 */
	public void setChargeBeam(LaserBeam chargeBeam) 
	{
		this.chargeBeam = chargeBeam;
	}

	@Override
	public void moveEnemy()
	{
		if (--reloadCounter > 0)
		{
			if(!scan)
				super.moveEnemy();
			return;
		}
		
		if(!haveTarget())
		{
			GameObject target = null;
			if(scan)
				target = EntityStuff.findClosestSeeable(this, targets);
			else
				target = EntityStuff.findClosest(this, targets);
			
			if(target != null)
			{
				int x1 = (int) (currX + width  / 2),
					y1 = (int) (currY + height / 2),
					x2 = (int) (target.currX + target.width  / 2),
					y2 = (int) (target.currY + target.height / 2);
				
				Point2D.Float wallp = EntityStuff.searchTile(x1,y1, x2,y2, stopTile);
				if(wallp == null)
					wallp = EntityStuff.findEdgePoint(x1, y1, x2, y2);
				
				targetX = wallp.x;
				targetY = wallp.y;
				sounds.allowSound(0);
				sounds.allowSound(1);
			}
			else
				super.moveEnemy();
		}
		if(haveTarget())
		{
			if(!scan)
				super.moveEnemy();
			
			if(!firing && ++sucounter % laserStartup == 0)
			{
				firing = true;
				
				if(exp != null)
					stage.add(exp.getClone(targetX - exp.width / 2, targetY - exp.height / 2));
			}
			if(firing)
			{
				firingBeam.fireAt(currX + width / 2, currY + height / 2, targetX, targetY, 1);
				sounds.stop(0);
				sounds.trySound(1, false);
				
				for(GameObject go : targets)
					if(EntityStuff.checkLine((int)currX, (int) currY, (int)targetX, (int)targetY, go))
						go.runHitEvent(this);
				
				if(++ducounter % laserDuration == 0)
				{
					targetX = targetY = -1;
					reloadCounter = reload;
					firing = false;
				}
			}
			else
			{
				sounds.trySound(0, false);
				chargeBeam.fireAt(currX + width / 2, currY + height / 2, targetX, targetY, 1);
			}
		}
	}

	@Override
	public void drawSpecial(SpriteBatch b)
	{
		Color defaultColor = b.getColor();
		
		if(laserTint != null)
			b.setColor(laserTint);
		
		chargeBeam.renderLasers(b);
		firingBeam.renderLasers(b);
		
		b.setColor(defaultColor);
	}
}
