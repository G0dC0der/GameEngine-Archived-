package game.movable;

import game.core.Enemy;
import game.essentials.SoundBank;
import kuusisto.tinysound.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * The {@code VerticalDrone} is a {@code Enemy} that moves in a vertical path, bouncing on walls(solid tile).
 * @author Pojahn Moradi
 *
 */
public class VerticalDrone extends Enemy
{
	boolean moveUp;
	
	/**
	 * Constructs a {@code VerticalDrone} at the given path.
	 * @param x The starting X position.
	 * @param y The starting Y position.
	 */
	public VerticalDrone (float x, float y)
	{
		super();
		currX = x;
		currY = y;
		moveUp = true;
		sounds = new SoundBank(1);
	}
	
	@Override
	public void moveEnemy() 
	{
		if (moveSpeed > 0 && canMove)
		{
			if (moveUp)
				tryLeft((int)moveSpeed);
			else
			{
				moveUp = false;
				sounds.allowSound(0);
			}
			if (!moveUp)
				tryRight((int)moveSpeed);
			else
			{
				sounds.allowSound(0);
				moveUp = true;
			}
		}
		
		sounds.trySound(0, false);
	}
	
	/**
	 * The sound to play when bouncing on walls.
	 * @param sound The sound.
	 */
	public void setCrashSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}

	@Override
	public void drawSpecial(SpriteBatch batch) {}
}
