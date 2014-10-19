package stages.orbitalstation;

import static game.core.Engine.*;
import game.core.Enemy;
import game.core.Fundementals;

class FireBall extends Enemy
{
	private float initialX, initialY, mass, gravity, damping, vy, flyPower;
	
	FireBall(float initialX, float initialY, float flyPower)
	{
		moveTo(initialX, initialY);
		this.initialX = initialX;
		this.initialY = initialY;
		this.flyPower = flyPower;
		
		mass = 1.0f;
		gravity = -500;
		damping = 0.0001f;
	}
	
	void setFlyPower(float flyPower)
	{
		this.flyPower = flyPower;
	}

	@Override
	public void moveEnemy() 
	{
		if(Fundementals.distance(initialX, initialY, currX, currY) < 30)
			vy = flyPower;
		
		vy *= 1.0 - (damping * DELTA);
	    float force = mass * gravity;
	    vy += (force / mass) * DELTA;
	    
	    currY -= vy * DELTA;
	}
	
	@Override
	public FireBall getClone(float x, float y) 
	{
		FireBall fb = new FireBall(x, y, flyPower);
		copyData(fb);
		
		return fb;
	}
	
	protected void copyData(FireBall dest) 
	{
		super.copyData(dest);
		dest.mass = mass;
		dest.gravity = gravity;
		dest.damping = damping;
	}
}
