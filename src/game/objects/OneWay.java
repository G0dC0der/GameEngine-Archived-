package game.objects;

import game.core.Engine.Direction;
import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.MovableObject;

/**
 * {@code OnWay} platforms are objects that are solid in a certain direction.<br>
 * This class behaves like the smiling clouds from Super Mario Bros 3, where you can jump right through it and stand on it when landing.
 * @author Pojahn Moradi
 *
 */
public class OneWay extends GameObject implements Event
{
	private MovableObject targets[];
	private boolean[] block;
	private Direction direction;

	/**
	 * 
	 * @param x The x coordinate to stand at.
	 * @param y The y coordinate to stand at.
	 * @param direction Can be one of the following constants from {@code game.core.Engine}: N, S, E or W. Using for example N would mean the platform is solid if you are above it.
	 * @param targets The objects capable of interacting with this platform.
	 */
	public OneWay(float x, float y, Direction direction, MovableObject... targets)
	{
		currX = x;
		currY = y;
		this.direction = direction;
		this.targets = targets;
		block = new boolean[targets.length];
		addEvent(this);
	}

	@Override
	public void eventHandling() 
	{
		for(int i = 0; i < targets.length; i++)
		{
			MovableObject mo = targets[i];
			boolean bool;
			
			switch(direction)
			{
				case S:
					bool = mo.currY >= currY + height;
					break;
				case N:
					bool = mo.currY + mo.height <= currY;
					break;
				case E:
					bool = currX + width <= mo.currX;
					break;
				case W:
					bool = currX >= mo.currX + mo.width;
					break;
				default: 
					throw new RuntimeException();
			}
			
			if(bool)
			{
				if(!block[i])
				{
					block[i] = true;
					mo.avoidOverlapping(this);
				}
			}
			else if(block[i])
			{
				block[i] = false;
				mo.allowOverlapping(this);
			}
		}
	}
	
	public OneWay getClone(float x, float y)
	{
		OneWay ow = new OneWay(x, y, direction, targets);
		copyData(ow);
		
		return ow;
	}
}
