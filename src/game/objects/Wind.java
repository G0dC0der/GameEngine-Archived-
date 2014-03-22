package game.objects;

import static game.core.Engine.*;
import game.core.GameObject;
import game.mains.GravityMan;

/**
 * This class behaves like wind, pushing the given subjects at the given direction when colliding.
 * @author Pojahn Moradi
 */
public class Wind extends GameObject 
{	
	private Direction dirr;
	private float blowStrength, maxStrength;
	private GravityMan[] victims;
	
	/**
	 * Constructs a {@code Wind}.
	 * @param x The X coordinate to stand on.
	 * @param y The Y coordinate to stand on.
	 * @param dirr A constant from {@code game.core.Engine} representing which direction to push at.
	 * @param blowStrength The amount of velocity to apply each frame when interacting.
	 * @param maxStrength The maximum amount of velocity to be added.
	 * @param victims The units capable of being pushed by the wind.
	 */
	public Wind (float x, float y, Direction dirr, float blowStrength, float maxStrength, GravityMan... victims)
	{
		currX = x;
		currY = y;
		this.dirr = dirr;
		this.blowStrength = blowStrength;
		this.maxStrength = maxStrength;
		this.victims = victims;
		
		addEvent(new WindEvent());
	}
	
	class WindEvent implements Event
	{
		@Override
		public void eventHandling() 
		{
			for (GravityMan man : victims)
			{
				if (collidesWith(man))
				{
					switch (dirr)
					{
						case N:
							if(man.vy < maxStrength)
								man.vy += blowStrength;
							break;
						case NE:
							if(man.vy < maxStrength)
								man.vy += blowStrength;
							if(-man.vx < maxStrength)
								man.vx -= blowStrength;
							break;
						case E:
							if(-man.vx < maxStrength)
								man.vx -= blowStrength;
							break;
						case SE:
							if(-man.vy < maxStrength)
								man.vy -= blowStrength;
							if(-man.vx < maxStrength)
								man.vx -= blowStrength;
							break;
						case S:
							if(-man.vy < maxStrength)
								man.vy -= blowStrength;
							break;
						case SW:
							if(-man.vy < maxStrength)
								man.vy -= blowStrength;
							if(man.vx < maxStrength)
								man.vx += blowStrength;
							break;
						case W:
							if(man.vx < maxStrength)
								man.vx += blowStrength;
							break;
						case NW:
							if(man.vy < maxStrength)
								man.vy += blowStrength;
							if(man.vx < maxStrength)
								man.vx += blowStrength;					
							break;
					}
				}
			}
		}
	}
}