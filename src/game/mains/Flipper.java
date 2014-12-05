package game.mains;

import static game.core.Engine.*;
import game.core.Engine.Direction;

public class Flipper extends GravityMan
{
	private boolean flip;
	
	public Flipper() {}
	
	public void flip()
	{
		flip = !flip;
		gravity = -gravity;
		wallGravity = -wallGravity;
		flipY = !flipY;
		upSpeed = -upSpeed;
	}
	
	@Override
	public boolean canGoDown() 
	{
		return flip ? super.canGoUp() : super.canGoDown();
	}
	
	@Override
	public boolean canGoUp() 
	{
		return flip ? super.canGoDown() : super.canGoUp();
	}
	
	@SuppressWarnings("deprecation")
	protected void jumpHandling()
	{
		if(!flip)
		{
			super.jumpHandling();
			return;
		}
		
		float velY = vy;
		
		if(wallSliding)
			counter = 0;
		
	    if(!canGoDown() && vy >= 0)
	    {
	    	reset();

	    	if(!upInput)
	    		jumpAllowed = true;
	    }
	    else
	    {
	    	float grav = (wallSliding) ? wallGravity : gravity;
	    	float damp = (wallSliding) ? wallDamping : damping;
	    	
	    	if(!wallSliding || (wallSliding && maxWallSlideSpeed < vy))
	    	{
			    vy *= 1.0 - (damp * DELTA);
			    float force = mass * grav;
			    vy += (force / mass) * DELTA;
	    	}
	    	else if(wallSliding && maxWallSlideSpeed > vy)
	    	{
			    vy *= 1.0 - (damp * DELTA);
			    float force = mass * breakSpeed;
			    vy -= (force / mass) * DELTA;
	    	}
	    }
	    
	    int old = counter;
	    if(wallSliding && !allowWallJump)
	    	jumpAllowed = false;
	    if(canGoDown() && !upInput)
	    	jumpAllowed = false;
	    else if(jumpAllowed && upInput)
	    {
	    	jumpStarted = true;
	    	if(++counter > maxY)
	    	{
	    		jumpAllowed = false;
	    		counter = 0;
	    	}
	    	else
	    		vy = upSpeed;
	    }
	    else if(jumpStarted && old == counter)
	    	jumpAllowed = false;
	    
	    if(vy != 0)
	    {
	    	boolean falling = vy > 0; 
	    	float nextY = loc.y - vy * DELTA;	    	
	    	
		    if(!falling)
		    {
		    	if(canGoDown(nextY))
		    		moveTo(loc.x, nextY);
		    	else
		    	{
		    		vy = 0;	//If we jump into an ceiling, we start falling down.
		    		jumpAllowed = false;
		    	}
		    }
		    else if(falling && canGoUp(nextY))
		    	moveTo(loc.x, nextY);
		    else
		    	tryUp(5);
		    
		    if(checkAllowed)
		    {
			    if(facing == Direction.E)
			    	facing = Direction.NE;
			    else if(facing == Direction.W)
			    	facing = Direction.NW;
		    }
	    }
	    else if(checkAllowed)
	    {
	    	if(facing == Direction.NE)
	    		facing = Direction.E;
	    	else if(facing == Direction.NW)
	    		facing = Direction.W;
	    }
	    
	    if(velY == 0 && jumpAllowed && upInput)
	    	sounds.playSound(0);
	}
	
	@Override
	public boolean tryUp(float steps)
	{
		for (int i = 0; i < steps; i++)
		{
			if (super.canGoUp())
				loc.y--;
			else
				return false;
		}
		return true;
	}
	
	@Override
	public boolean tryDown(float steps)
	{
		for (int i = 0; i < steps; i++)
		{
			if (super.canGoDown())
				loc.y++;
			else
				return false;
		}
		return true;
	}
}
