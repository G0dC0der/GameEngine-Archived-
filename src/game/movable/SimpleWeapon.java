package game.movable;

import com.badlogic.gdx.math.Vector2;
import kuusisto.tinysound.Sound;
import game.core.Engine.Direction;
import game.core.Fundementals;
import game.essentials.SoundBank;
import game.objects.Particle;

/**
 * The {@code SimpleWeapon} who unlike the {@code Weapon} class do not scan and fire at a set of targets. Instead, it fire constantly at the given direction with the given reload time.
 * @author Pojahn Moradi
 */
public class SimpleWeapon extends PathDrone
{	
	private Direction projDir;
	private Projectile proj;
	private Particle fireAnim;
	private int reloadTime, reloadCounter;
	private float offsetX, offsetY;
	
	/**
	 * Constructs a {@code SimpleWeapon} that fires at the given direction with the given reload time.
	 * @param x The x coordinate to start at.
	 * @param y The y coordinate to start at.
	 * @param proj The projectile to fire.
	 * @param projDir The direction to fire the projectile at.
	 * @param reloadTime The amount of frames between the shots.
	 */
	public SimpleWeapon(float x, float y, Projectile proj, Direction projDir, int reloadTime) 
	{
		super(x,y);
		this.proj = proj;
		this.projDir = projDir;
		this.reloadTime = reloadTime;
		sounds = new SoundBank(1); //Firing sound
		sounds.setEmitter(this);
	}
	
	/**
	 * Allow you to customize the offset of which the projectiles are spawned at.
	 * @param offsetX The relative x coordinate.
	 * @param offsetY The relative y coordinate.
	 */
	public void spawnOffset(float offsetX, float offsetY)
	{
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	
	/**
	 * The animation to spawn at the projectiles starting point.
	 * @param fireAnim The particle to spawn.
	 */
	public void setFiringAnimation(Particle fireAnim)
	{
		this.fireAnim = fireAnim;
	}
	
	/**
	 * Checks whether or not the weapon is reloading.
	 * @return True if its currently reloading.
	 */
	public boolean reloading()
	{
		return reloadCounter > 0;
	}
	
	/**
	 * The sound to play when firing a projectile.
	 * @param sound The sound.
	 */
	public void setFiringSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}
	
	@Override
	public void moveEnemy() 
	{
		if(--reloadCounter < 0)
		{
			float startX = currX + offsetX;
			float startY = currY + offsetY;
			
			Projectile projClone = proj.getClone(startX, startY);
			projClone.setDisposable(true);
			projClone.scanningAllowed(false);
			Vector2 target = Fundementals.getEdgePoint(startX, startY, projDir);
			projClone.setTarget(target.x, target.y);
			
			if(fireAnim != null)
				stage.add(fireAnim.getClone(startX, startY));
			
			stage.add(projClone);
			reloadCounter = reloadTime;
			
			sounds.playSound(0);
		}
	}
}