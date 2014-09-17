package game.movable;

import game.core.Engine;
import game.core.EntityStuff;
import game.core.GameObject;
import game.core.MovableObject;
import game.essentials.Factory;
import game.essentials.LaserBeam;
import game.objects.Particle;

import java.awt.geom.Point2D;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * This a an enemy that keep firing laser at the specified target. The laser never stop firing. A subjects {@code HitEvent} will be fired upon contact with the laser.<br>
 * As an example, you can target a {@code Circle} to get a rotating effect. <br>
 * The laser is omitting from the image's middle point.
 * @author PojahnM
 *
 */
public class TargetLaser extends PathDrone
{		
	private boolean stop, specEffect, eye;
	private int delay, delayCounter;
	private byte stopTile;
	private GameObject targets[], laserTarget;
	private MovableObject dummy;
	private Particle impact;
	private Point2D.Float wallTarget;
	private LaserBeam beam;
	private Color laserTint;
	
	/**
	 * Constructs a {@code TargetLaser} instance.
	 * @param x The starting X position.
	 * @param y The starting Y position.
	 * @param laserTarget The object to fire the laser at.
	 * @param targets The object capable of being hit by the laser.
	 */
	public TargetLaser(float x, float y, GameObject laserTarget, GameObject... targets) 
	{
		super(x, y);

		this.targets = targets;
		this.laserTarget = laserTarget;
		stop = eye = false;
		delay = 1;
		stopTile = Engine.SOLID;
		setBeam(Factory.defaultLaser());
		laserTint = Color.valueOf("CC0000FF");
		
		dummy = new MovableObject();
		dummy.setMoveSpeed(17.2f);
	}
	
	/**
	 * Constructs a {@code TargetLaser} with a customized laser beam. Reefer to constructor float, float, GameObject, GameObject[] for more information.
	 */
	public TargetLaser(float x, float y, LaserBeam beam, GameObject laserTarget, GameObject... targets) 
	{
		this(x,y,laserTarget,targets);
		this.beam = beam;
	}
	
	@Override
	public void moveEnemy()
	{
		super.moveEnemy();
		
		if(stop)
			return;
		
		float centerX = currX + width / 2;
		float centerY = currY + height / 2;
		float tcx = laserTarget.currX + laserTarget.width / 2;  // Target Center X
		float tcy = laserTarget.currY + laserTarget.height / 2; // Target Center Y
		
		if(eye)
		{
			dummy.specialMoveToward(laserTarget.currX, tcx, 100, 3, Engine.DELTA);
			wallTarget = EntityStuff.searchTile(centerX, centerY, dummy.currX, dummy.currY, stopTile);
			if(wallTarget == null)
				wallTarget = EntityStuff.searchTile(centerY, centerY, dummy.currX, dummy.currY, stopTile);
		}
		else
		{
			wallTarget = EntityStuff.searchTile(centerX, centerY, tcx, tcy, stopTile);
			if(wallTarget == null)
				wallTarget = EntityStuff.findEdgePoint(centerY, centerY, tcx, tcy);
		}
		
		if(impact != null && ++delayCounter % delay == 0)
			stage.add(impact.getClone(wallTarget.x - impact.width / 2, wallTarget.y - impact.height / 2));
		
		for(GameObject go : targets)
		{
			if(go.haveHitEvent() && EntityStuff.checkLine((int)centerX, (int)centerY, (int)wallTarget.x, (int)wallTarget.y, go))
				go.runHitEvent(this);
		}
		
		if(specEffect)
		{
			if(!eye) 
				rotation = (float) EntityStuff.getAngle(currX + width / 2, currY + height / 2, tcx, tcy);
			else
				rotation = (float) EntityStuff.getAngle(currX + width / 2, currY + height / 2, wallTarget.x, wallTarget.y);
		}
		
		beam.fireAt(centerX, centerY, wallTarget.x, wallTarget.y, 1);
	}
	
	/**
	 * The tile type that will act as the end position for the laser beam(default: SOLID).
	 * @param stopTile The tile type.
	 */
	public void setStopTile(byte stopTile)
	{
		this.stopTile = stopTile;
	}
	
	/**
	 * In case this flag is enabled, the laser beam will rotate towards its target rather than instantly targeting it.
	 * @param eye True to enable the effect.
	 */
	public void enableEyeLaser(boolean eye)
	{
		this.eye = eye;
	}
	
	/**
	 * The animation to render at the laser beams edge.
	 * @param impact The particle.
	 */
	public void setExplosion(Particle impact)
	{
		this.impact = impact;
	}
	
	/**
	 * How often to render the explosion.
	 * @param delay The delay, in frames.
	 */
	public void setExplosionDelay(int delay)
	{
		if(delay <= 0)
			throw new IllegalArgumentException("delay must be exceed 0: " + delay);
		
		this.delay = delay;
	}
	
	/**
	 * Whether or not to rotate the objects image so it is always facing the lasers target.
	 * @param specEffet True to enable rotating.
	 */
	public void useSpecialEffect(boolean specEffect)
	{
		this.specEffect = specEffect;
	}
	
	/**
	 * This flag determine whether or not to fire.
	 * @param stop True to stop fire the laser.
	 */
	public void stop(boolean stop)
	{
		this.stop = stop;
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
	 * Returns the laser beam of the unit.
	 * @return The beam.
	 */
	public LaserBeam getBeam() 
	{
		return beam;
	}

	/**
	 * Sets the beam.
	 * @param beam The beam to use.
	 */
	public void setBeam(LaserBeam beam) 
	{
		this.beam = beam;
	}

	@Override
	public void drawSpecial(SpriteBatch b)
	{
		Color defaultColor = b.getColor();
		
		if(laserTint != null)
			b.setColor(laserTint);
		
		beam.renderLasers(b);
		
		b.setColor(defaultColor);
	}
}
