package game.movable;

import game.core.GameObject;
import game.core.MovableObject;
import game.essentials.Factory;

public class SimpleButton extends SolidPlatform
{
	private Event clickEvent, dummyEvent;
	private MovableObject[] subjects;
	private GameObject dummy;
	private boolean pressed;
	
	public SimpleButton(float x, float y, MovableObject... subjects) 
	{
		super(x, y, subjects);
		this.subjects = subjects;
		setMoveSpeed(1);

		dummy = new GameObject();
		dummyEvent = Factory.follow(this, dummy, 0, -1);
	}
	
	public void setClickEvent(Event clickEvent)
	{
		this.clickEvent = clickEvent;
	}
	
	@Override
	public void moveEnemy() 
	{
		super.moveEnemy();
		
		if(!pressed)
		{
			dummy.width = width();
			dummyEvent.eventHandling();
		}
		
		if(!pressed && dummy.collidesWithMultiple(subjects) != null)
		{
			pressed = true;
			appendPath(loc.x, loc.y + halfHeight());
			clickEvent.eventHandling();
		}
	}
}
