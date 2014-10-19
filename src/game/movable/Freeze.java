package game.movable;

import game.core.MovableObject;
import game.essentials.SoundBank;
import kuusisto.tinysound.Sound;

/**
 * A {@code Freeze} is an object that solidify any victim colliding with it.<br>
 * The object will be hidden when a victim has been frozen and later on deleted.
 * @author Pojahn Moradi
 */
public class Freeze extends PathDrone
{
	private int freezeTime, counter;
	private boolean startCounting;
	private MovableObject[] victims;
	private MovableObject frozen;
	
	/**
	 * Constructs a {@code Freeze} object.
	 * @param x The starting X position.
	 * @param y The starting Y position.
	 * @param freezeTime The amount of frames to freeze a victim.
	 * @param victims The objects possible of being hit by this instance.
	 */
	public Freeze (float x, float y, int freezeTime, MovableObject... victims)
	{
		super(x,y);
		startCounting = false;
		this.freezeTime = freezeTime;
		this.victims = victims;
		sounds = new SoundBank(1);
		sounds.setEmitter(this);
	}
	
	@Override
	public void moveEnemy()
	{
		if (!startCounting)
		{
			super.moveEnemy();
			
			for (MovableObject victim : victims)
			{
				if (collidesWith(victim))
				{
					startCounting = true;
					setVisible(false);
					victim.freeze();
					frozen = victim;
					sounds.trySound(0, true);
					
					return;
				}
			}
		}
		else if (startCounting && counter++ > freezeTime)
		{
			frozen.unfreeze();
			stage.discard(this);
		}
	}
	
	/**
	 * The sound to play when a victim have collided with this object.
	 * @param sound The sound.
	 */
	public void setCollectSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}
}