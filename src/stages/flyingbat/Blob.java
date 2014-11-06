package stages.flyingbat;

import game.core.Engine;
import game.core.GameObject;
import game.core.MovableObject;
import game.essentials.Factory;
import game.movable.Missile;

class Blob extends Missile
{
	MovableObject spawnOn;
	GameObject[] targets;
	
	Blob(float initialX, float initialY, GameObject[] targets, MovableObject subject) 
	{
		super(initialX, initialY, targets);
		this.spawnOn = subject;
		this.targets = targets;
	}
	
	@Override
	public Blob getClone(float x, float y)
	{
		Blob p = new Blob(x, y, targets, spawnOn);
		copyData(p);
		
		return p;
	}
	
	@Override
	protected void hit(GameObject subject)
	{
		super.hit(subject);

		MovableObject mo = new MovableObject();
		mo.setImage(image);
		mo.loc.x = loc.x;
		mo.loc.y = loc.y;
		adjust(spawnOn, mo);
		
		Event deform = Factory.tileDeformer(mo, Engine.SOLID, false);
		deform.eventHandling();
		
		stage.add(mo);
	}
	
	private static void adjust(MovableObject src, MovableObject target)
	{
		float nextX = target.loc.x + src.loc.x - src.getPrevX();
		float nextY = target.loc.y + src.loc.y - src.getPrevY();
		
		if(target.canGoTo(nextX, nextY))
			target.moveTo(nextX, nextY);
		
		for(int i = 0; i < target.height; i++)
		{
			if(src.collidesWith(target))
				target.loc.y--;
			else
				break;
		}
		for(int i = 0; i < target.height * 2; i++)
		{
			if(src.collidesWith(target))
				target.loc.y++;
			else
				break;
		}
		
		for(int i = 0; i < target.width; i++)
		{
			if(src.collidesWith(target))
				target.loc.x--;
			else
				break;
		}
		for(int i = 0; i < target.width * 2; i++)
		{
			if(src.collidesWith(target))
				target.loc.x++;
			else
			
				break;
		}
		if(src.collidesWith(target))
			target.loc.x -= target.width;
		
		if(src.collidesWith(target))
			target.loc.y -= target.height;
	}
}