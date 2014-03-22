package game.movable;

import game.core.Enemy;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * This class offers an easy way to move an {@code Enemy} in a rectangular path.
 * @author Pojahn Moradi
 *
 */
public class RectangleDrone extends Enemy
{
	private float x, y, w, h, targetX, targetY;
	private int targetDirr;
	private final boolean clockwise;
	
	/**
	 * Constructs a {@code RectangleDrone}.
	 * @param x The starting X position.
	 * @param y The starting Y position.
	 * @param w The width of the rectangle.
	 * @param h The height of the rectangle.
	 * @param clockwise True if this unit should move clockwise.
	 */
	public RectangleDrone (float x, float y, float w, float h, boolean clockwise)
	{
		super();
		this.currX = this.x = x;
		this.currY = this.y = y;
		this.w = w;
		this.h = h;
		this.clockwise = clockwise;
		
		targetDirr = (clockwise) ? Keys.RIGHT: Keys.DOWN;
		targetX = (clockwise) ? x + w : x;
		targetY = (clockwise) ? y : y + h;
	}

	@Override
	public void moveEnemy() 
	{
		switch (targetDirr)
		{
		case Keys.RIGHT:
			for (int i = 0; i < moveSpeed; i++)
			{
				if (targetX > currX)
					currX++;
				else
				{
					targetDirr = (clockwise) ? Keys.DOWN: Keys.UP;
					targetY = (clockwise) ? y + h : y;
					break;
				}
			}
			break;
		case Keys.DOWN:
			for (int i = 0; i < moveSpeed; i++)
			{
				if (targetY > currY)
					currY++;
				else
				{
					targetDirr = (clockwise) ? Keys.LEFT : Keys.RIGHT;
					targetX = (clockwise) ? x : x + w;
					break;
				}
			}
			break;
		case Keys.LEFT:
			for (int i = 0; i < moveSpeed; i++)
			{
				if (currX > targetX)
					currX--;
				else
				{
					targetDirr = (clockwise) ? Keys.UP : Keys.DOWN;
					targetY = (clockwise) ? y : y + h;
					break;
				}
			}
			break;
		case Keys.UP:
			for (int i = 0; i < moveSpeed; i++)
			{
				if (currY > targetY)
					currY--;
				else
				{
					targetDirr = (clockwise) ? Keys.RIGHT : Keys.LEFT;
					targetX = (clockwise) ? x + w : x;
					break;
				}
			}
			break;
		}
	}

	@Override
	public void drawSpecial(SpriteBatch batch) {}
}
