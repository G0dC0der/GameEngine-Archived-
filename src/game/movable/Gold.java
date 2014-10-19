package game.movable;

import game.core.GameObject;
import game.essentials.SoundBank;
import kuusisto.tinysound.Sound;

/**
 * This class offer an easy solution to manipulate the game clock.<br>
 * The instance is removed upon collision.
 * @author Pojahn Moradi
 */
public class Gold extends PathDrone
{
	private float value;
	private GameObject[] grabers;
	
	/**
	 * Constructs a {@code Gold} object.
	 * @param x The starting X position.
	 * @param y The starting Y position.
	 * @param value The amount of time, in milliseconds, to add or subtract upon collision wit a victim.
	 * @param grabers The objects capable of interacting with this instance.
	 */
	public Gold (float x, float y, float value, GameObject... grabers)
	{
		super(x,y);
		this.grabers = grabers;
		this.value = value;
		sounds = new SoundBank(1);
		sounds.setEmitter(this);
	}
	
	@Override
	public void moveEnemy()
	{
		super.moveEnemy();
		
		for (GameObject graber : grabers)
		{
			if (collidesWith(graber))
			{
				setVisible(false);
				stage.game.elapsedTime += value;
				stage.discard(this);
				sounds.trySound(0, true);
			}
		}
	}
	
	public Gold getClone(float x, float y)
	{
		Gold g = new Gold(x, y, value, grabers);
		copyData(g);
		
		if(cloneEvent != null)
			cloneEvent.cloned(g);
		
		return g;
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
