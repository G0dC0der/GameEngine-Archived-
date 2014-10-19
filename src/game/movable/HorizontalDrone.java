package game.movable;

import game.core.Enemy;
import game.essentials.SoundBank;
import kuusisto.tinysound.Sound;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * This object moves in a horizontal direction, bouncing on walls, independent on waypoints.<br>
 * For collision detection, you have to manually add an event.
 * @author Pojahn Moradi
 */
public class HorizontalDrone extends Enemy
{
	boolean moveLeft, startLeft;
	
	/**
	 * Constructs a {@code HorizontalDrone}.
	 * @param x The starting X position.
	 * @param y The starting Y position.
	 * @param startLeft Whether or not to start moving left rather than right.
	 */
	public HorizontalDrone (float x, float y, boolean startLeft)
	{
		super();
		currX = x;
		currY = y;
		this.startLeft = moveLeft = startLeft;
		sounds = new SoundBank(1);
		sounds.setEmitter(this);
	}
	
	public HorizontalDrone(float x, float y)
	{
		this(x,y,true);
	}
	
	@Override
	public void moveEnemy() 
	{
		if (moveSpeed >= 1 && canMove)
		{
			if(moveLeft && !tryLeft((int)moveSpeed))
			{
				moveLeft = false;
				sounds.playSound(0);
			}
			if (!moveLeft && !tryRight((int)moveSpeed))
			{
				sounds.playSound(0);
				moveLeft = true;
			}
		}
	}
	
	/**
	 * The sound to play when bouncing on a wall.
	 * @param sound The sound to play.
	 */
	public void setCrashSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}
	
	/**
	 * Whether or not to start moving left rather than right.
	 * @param startLeft True if this unit should start moving to the left.
	 */
	public void startLeft(boolean startLeft)
	{
		this.startLeft = moveLeft = startLeft;
	}
	
	@Override
	public HorizontalDrone getClone(float x, float y)
	{
		HorizontalDrone hd = new HorizontalDrone(x, y, startLeft);
		copyData(hd);
		
		return hd;
	}

	@Override
	public void drawSpecial(SpriteBatch batch) {}
}