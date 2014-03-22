package game.movable;

import game.core.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * A dummy class with no special functionality.
 * @author Pojahn Moradi
 *
 */
public class Dummy extends Enemy
{
	public Dummy(float x, float y)
	{
		super();
		moveTo(x,y);
	}
	
	@Override
	public Dummy getClone(float x, float y)
	{
		Dummy d = new Dummy(x,y);
		copyData(d);
		
		return d;
	}
	
	@Override
	public void moveEnemy() 
	{}

	@Override
	public void drawSpecial(SpriteBatch batch) {}
}
