package game.movable;

import game.core.Engine;
import game.core.Fundementals;
import game.core.GameObject;
import game.core.MovableObject;
import game.essentials.Factory;
import game.essentials.LaserBeam;
import game.objects.Particle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * This a an enemy that keep firing laser at the specified target. The laser never stop firing. A subjects {@code HitEvent} will be fired upon contact with the laser.<br>
 * As an example, you can target a {@code Circle} to get a rotating effect. <br>
 * The laser is omitting from the image's middle point.
 * @author PojahnM
 *
 */
public class TargetLaser extends PathDrone
{		
	private boolean stop, specEffect, infBeam, frontFire;
	private int delay, delayCounter;
	private byte stopTile;
	private GameObject targets[], laserTarget;
	private MovableObject dummy;
	private Particle impact;
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
		delay = 1;
		stopTile = Engine.SOLID;
		infBeam = true;
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
		
		float cx = centerX();
		float cy = centerY();
		float tcx = laserTarget.centerX(); // Target Center X
		float tcy = laserTarget.centerY(); // Target Center Y
		Vector2 finalTarget;
		
		if(infBeam)
		{
			finalTarget = Fundementals.searchTile(cx, cy, tcx, tcy, stopTile);
			if(finalTarget == null)
				finalTarget = Fundementals.findEdgePoint(cy, cy, tcx, tcy);
		}
		else
			finalTarget = new Vector2(tcx, tcy);
		
		if(impact != null && ++delayCounter % delay == 0)
			stage.add(impact.getClone(finalTarget.x - impact.width / 2, finalTarget.y - impact.height / 2));
		
		for(GameObject go : targets)
			if(go.haveHitEvent() && Fundementals.checkLine((int)cx, (int)cy, (int)finalTarget.x, (int)finalTarget.y, go))
				go.runHitEvent(this);
		
		if(specEffect)
			rotation = (float) Fundementals.getAngle(cx, cy, finalTarget.x, finalTarget.y);
		
		if(frontFire)
		{
			Vector2 front = getFrontPosition();
			cx = front.x;
			cy = front.y;
		}
		
		beam.fireAt(cx, cy, finalTarget.x, finalTarget.y, 1);
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
	 * The animation to render at the laser beams edge.
	 * @param impact The particle.
	 */
	public void setExplosion(Particle impact)
	{
		this.impact = impact;
	}
	
	/**
	 * Allow you to specify whether or not the target should stop when reached the {@code stopTile} or at the target object.
	 * @param infBeam False to limit the beam length to the target.
	 */
	public void infiniteBeam(boolean infBeam)
	{
		this.infBeam = infBeam;
	}
	
	/**
	 * Whether or not to fire from the front rather than the middle point.
	 * @param frontFire True to fire from the front.
	 */
	public void frontFire(boolean frontFire)
	{
		this.frontFire = frontFire;
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
	 * Whether or not to rotate the object's image so it is always face the lasers target.
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
	 * Checks whether or not the entity have halted its laser mission.
	 * @return True if its not firing any lasers.
	 */
	public boolean stopped()
	{
		return stop;
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
	 * Returns the laser beam associated to it.
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
