package game.movable;

import game.core.Enemy;
import game.core.Stage;

/**
 * An {@code Enemy} that exist a given amount of frames.<br>
 * This class is often used as a rendering event, where {@code drawSpecial} is overridden.
 * @author Pojahn Moradi
 */
public abstract class TimedEnemy extends Enemy
{
	protected int time = 0;
	private int counter = 0;
	
	@Override
	public void moveEnemy()
	{
		if(counter++ > time)
		{
			Stage.STAGE.discard(this);
		}
	}
}