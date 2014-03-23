package game.movable;

import game.core.MovableObject;
import game.core.Stage;

/**
 * This class offers an easy way to temporary change the move speed of a {@code MovableObject}.<br>
 * The object is hidden upon collision with a subject and discarded when the effect is done.
 * @author Pojahn Moradi
 *
 */
public class Speed extends PathDrone
{
	private MovableObject victims[], hit;
	private float speedBoost, ogSpeed;
	private int active, counter;
	
	/**
	 * Constructs a {@code Speed} object.
	 * @param x The starting X position.
	 * @param y The starting Y position.
	 * @param speedBoost The amount of speed to add when the object is collected.
	 * @param active The amount of frames the speed effect will stay active.
	 * @param victims The objects capable of collecting this item.
	 */
	public Speed (float x, float y, float speedBoost, int active, MovableObject... victims)
	{
		super(x,y);
		this.speedBoost = speedBoost;
		this.active = active;
		this.victims = victims;
		counter = 0;
		ogSpeed = -1;
	}
	
	@Override
	public void moveEnemy()
	{
		if(hit != null)
		{
			if(active++ > counter)
			{
				Stage.STAGE.discard(this);
				hit.setMoveSpeed(ogSpeed);
			}
		}
		else
		{
			super.moveEnemy();
			
			for(MovableObject mo : victims)
				if(collidesWith(mo))
				{
					setVisible(false);
					ogSpeed = mo.getMoveSpeed();
					hit = mo;
					hit.setMoveSpeed(hit.getMoveSpeed() + speedBoost);
					return;
				}
		}
	}
}