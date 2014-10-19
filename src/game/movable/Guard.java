package game.movable;

import game.core.Fundementals;
import game.core.GameObject;
import game.essentials.SoundBank;
import kuusisto.tinysound.Sound;

/**
 * This class behaves like a security guard, moving around in a fixed path and attacks when it sees something bad.<br>
 * The {@code Guard} follows its waypoints and "attack" whenever it sees a target and return to its waypoint-pathing when the target becomes unseeable.
 * @author Pojahn Moradi
 *
 */
public class Guard extends PathDrone
{
	private GameObject[] targets;
	private GameObject currTarget;
	private float huntSpeed;
	
	/**
	 * Constructs a {@code Guard}.
	 * @param x The starting X position.
	 * @param y The starting Y position.
	 * @param huntSpeed The move speed to use when "hunting".
	 * @param targets The object this instance can hunt.
	 */
	public Guard(float x, float y, float huntSpeed, GameObject... targets) 
	{
		super(x, y);
		this.targets = targets;
		this.huntSpeed = huntSpeed;
		sounds = new SoundBank(1);
		sounds.setEmitter(this);
	}

	@Override
	public void moveEnemy()
	{
		currTarget = Fundementals.findClosestSeeable(this, targets);
		if(currTarget != null)
		{
			sounds.trySound(0, false);
			moveToward(currTarget.currX, currTarget.currY, huntSpeed);
		}
		else
		{
			super.moveEnemy();
			sounds.allowSound(0);
		}
	}
	
	/**
	 * The sound to play when a target has been detected.
	 * @param sound The sound.
	 */
	public void setDetectSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}
}
