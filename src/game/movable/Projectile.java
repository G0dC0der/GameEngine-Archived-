package game.movable;

import game.core.Enemy;
import game.core.EntityStuff;
import game.core.GameObject;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import game.objects.Particle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import kuusisto.tinysound.Sound;


/**
 * The {@code Projectile} is scanning for a set of targets, firing at the closest visible one.<br>
 * Once it can see a target, it will fire itself at the targets edgepoint, and spawn the specified {@code Particle} at impact.
 * Impact can occur either when colliding with any of the targets or hitting a wall.<br><br>
 * 
 * Targets hit by the {@code Projectile} will have its {@code HitEvent} fired.
 * @author PojahnM
 */
public class Projectile extends Enemy
{
	protected float initialX, initialY, targetX, targetY;
	protected boolean scanAllowed, specEffect;
	private final GameObject[] targets;
	private Particle impact;
	private ArrayList<GameObject> otherTargets;
	private int reload, reloadCounter;
	private boolean useOnce;
	
	/**
	 * Creates a projectile.
	 * @param initialX The x coordinate of the start and respawn point.
	 * @param initialY The y coordinate of the start and respawn point.
	 * @param targets The {@code GameObjects} to scan and shot at.
	 */
	public Projectile(float initialX, float initialY, GameObject... targets)
	{
		currX = this.initialX = initialX;
		currY = this.initialY = initialY;
		this.targets = targets;
		targetX = -1;
		targetY = -1;
		scanAllowed = specEffect = true;
		reloadCounter = 0;
		otherTargets = new ArrayList<>();
		sounds = new SoundBank(1);//0 = firing sounds
	}
	
	@Override
	public Projectile getClone(float x, float y)
	{
		Projectile p = new Projectile(x, y, targets);
		copyData(p);
		
		return p;
	}
	
	protected void copyData(Projectile dest)
	{
		super.copyData(dest);
		dest.scanAllowed = scanAllowed;
		dest.specEffect = specEffect;
		dest.impact = impact;
		dest.otherTargets.addAll(otherTargets);
		dest.targetX = targetX;
		dest.targetY = targetY;
		dest.useOnce = useOnce;
		dest.reload = reload;
	}
	
	/**
	 * The sound to play when the projectile has been fired.
	 * @param firingSound The sound.
	 */
	public void setFiringSound(Sound firingSound)
	{
		sounds.setSound(0, firingSound);
	}

	@Override
	public void moveEnemy()
	{
		visible = false;
		
		if (--reloadCounter > 0)
			return;
		
		if (scanAllowed && !haveTarget())
		{
			GameObject target = EntityStuff.findClosestSeeable(this, targets);
			if(target != null)
			{
				Point2D.Float edge = EntityStuff.findEdgePoint(this, target);
				targetX = edge.x;
				targetY = edge.y;
			}
		}
		if (haveTarget())
		{
			visible = true;
			sounds.trySound(0, false);
			
			moveToward(targetX, targetY, moveSpeed);
			checkCollisions();
		}		
	}
	
	/**
	 * Checks whether or not this {@code Projectile} is reloading.
	 * @return
	 */
	public boolean isReloading()
	{
		return reloadCounter > 0;
	}
	
	/**
	 * Called by moveEnemy() when the bullet hit something.<br>
	 * Projectiles with use-once property are discarded in this function.
	 * @param subject The unit that this bullet hit, or null if it hit tile.
	 */
	protected void hit(GameObject subject)
	{
		if(subject != null)
			subject.runHitEvent(this);
		if(impact != null)
			stage.add(impact.getClone(currX - impact.width / 2, currY - impact.height / 2));

		sounds.stop(1);
		
		if (useOnce)
			stage.discard(this);
		else
		{
			targetX = targetY = -1;
			reloadCounter = reload;
			visible = false;
			sounds.allowSound(0);
			sounds.allowSound(1);
			
			currX = initialX;
			currY = initialY;		
		}
	}
	
	/**
	 * Returns the objects to scan for.
	 * @return The targets.
	 */
	protected GameObject[] getTargets()
	{
		return targets;
	}
	
	/**
	 * Checks if this bullet is collding with tile, a target or an "other target". Calls {@code hit} in case of collision.
	 */
	protected void checkCollisions()
	{
		if (!canGoTo(currX,currY))
			hit(null);
		else
		{
			for (GameObject go : targets)
			{
				if (collidesWith(go))
					hit (go);
				else
					continue;
				return;
			}
			for (GameObject go : otherTargets)
			{
				if (collidesWith(go))
					hit (go);
				else
					continue;
				return;
			}
		}
	}
	
	/**
	 * Return the closest visible target, or null if no target is seeable.
	 * @return The target.
	 */
	protected GameObject findTarget()
	{
		return EntityStuff.findClosestSeeable(this, targets);
	}
	
	/**
	 * An "other target" is a {@code GameObject} capable of being hit by this missile, but is not considered a target when scanning.
	 * @param go The unit to consider as an "other target".
	 */
	public void addOtherTarget(GameObject go)
	{
		otherTargets.add(go);
	}
	
	/**
	 * Checks whether or not this bullet currently have a target.
	 * @return True if this {@code Projectile} currently have a target.
	 */
	public boolean haveTarget()
	{
		return targetX != -1;
	}
	
	/**
	 * This method can be used for manual targeting.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 */
	public void setTarget(float x, float y)
	{
		targetX = x;
		targetY = y;
	}
	
	/**
	 * Returns the current target, or null if it have no target.
	 * @return The target.
	 */
	public Point2D.Float getTarget()
	{
		if(targetX == -1)
			return null;
		
		return new Point2D.Float(targetX, targetY);
	}
	
	/**
	 * Whether or not to disable auto scanning in case manual targeting with {@code setTarget(x,y)} is used.
	 * @param scan False to disable auto scanning.
	 */
	public void scanningAllowed(boolean scan)
	{
		this.scanAllowed = scan;
	}

	/**
	 * Using special effect causes the the class to render a bullet instead of using an image.
	 * @param specEffect True to dispose image and render a bullet instead.
	 */
	public void useSpecialEffect(boolean specEffect)
	{
		this.specEffect = specEffect;
	}
	
	/**
	 * Setting this flag to true enables the "use-once" property which means the instance will be discarded upon impact.
	 * @param useOnce True to enable the "use-once" property.
	 */
	public void setDisposable(boolean useOnce)
	{
		this.useOnce = useOnce;
	}
	
	/**
	 * In case the "use-once" property is disabled, the projectile will respawn on its initial position upon impact. The reload time is the amount of frames before a respawned {@code Projectile} can fire again.
	 * @param reload The reload time, in frames.
	 */
	public void setReloadTime(int reload)
	{
		this.reload = reload;
	}
	
	/**
	 * The impact to append at the impact point.
	 * @param impact The animation.
	 */
	public void setImpact(Particle impact)
	{
		this.impact = impact;
	}
	
	@Override
	public Image2D getFrame()
	{
		if (!specEffect)
			return super.getFrame();
		else
			return null;
	}
}