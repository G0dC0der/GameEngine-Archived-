package game.movable;

import game.core.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * This entity's purpose is to move around in a circular path.
 * @author Pojahn Moradi
 *
 */
public class Circle extends Enemy
{
	private float centerX, centerY, radius, counter;
	
	/**
	 * Constructs fully customized {@code Circle}.
	 * @param centerX Center x of the circular path.
	 * @param centerY Center y of the circular path.
	 * @param radius The radius of the circular path.
	 * @param startingAngle The starting angle in radians.
	 */
	public Circle(float centerX, float centerY, float radius, float startingAngle)
	{
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
		counter = startingAngle;
	}

	@Override
	public void moveEnemy() 
	{
		counter+=moveSpeed;
		
		currX = (float) (radius * Math.cos(counter) + centerX);
	    currY = (float) (radius * Math.sin(counter) + centerY);
	}
	
	/**
	 * Sets the X center coordinate of the circular path.
	 * @param centerX The X coordinate.
	 */
	public void setCenterX(float centerX)
	{
		this.centerX = centerX;
	}
	
	/**
	 * Sets the Y center coordinate of the circular path.
	 * @param centerY The Y coordinate.
	 */
	public void setCenterY(float centerY)
	{
		this.centerY = centerY;
	}
	
	/**
	 * Sets the radius of the circular path.
	 * @param radius The radius.
	 */
	public void setRadius(float radius)
	{
		this.radius = radius;
	}

	@Override
	public void drawSpecial(SpriteBatch batch) {}
}
