package game.mains;

import static game.core.Engine.*;
import game.core.Engine.Direction;
import game.core.MainCharacter;
import game.core.MovableObject;
import game.core.Stage;
import game.essentials.Controller.PressedButtons;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import kuusisto.tinysound.Sound;

/*
 * Bugs:
 * - Right-very-steep-slope, can't jump But you can from a left one.
 */

/**
 * The {@code GravityMan} is the default main character used in my(Pojahn) stages. This character respects gravity, can wall jump and slide, run up and down from slopes and falls down from cliffs.<br>
 * The feel of this character is a mix of Super Meat Boy and N+. However, the public variables among with the public setters can be used to alter the properties of this character.
 * @author Pojahn Moradi
 *
 */
@SuppressWarnings("deprecation")
public class GravityMan extends MainCharacter
{
	public float maxX, maxY, accX, vx, vy, boostX, upSpeed, mass, gravity, damping, wallGravity, wallDamping, maxWallSlideSpeed, breakSpeed;
	private boolean jumpAllowed, wallSliding, jumpStarted, oldInput, allowWallJump, allowWallSlide, blockInput, moving;
	private int counter, flySpeed;
	private float prevVX;
	private byte[][] d;
	
	public GravityMan()
	{
		super();
		jumpAllowed = jumpStarted = false;
		allowWallJump = allowWallSlide = manualFacings = true;

		maxX = 230;
		accX = 500;
		boostX = 0;

		mass = 1.0f;
		gravity = -500;
		damping = 0.0001f;
		upSpeed = 180;
		maxY = 20;

		wallGravity = -100;
		wallDamping = 1.1f;
		maxWallSlideSpeed = -70;
		breakSpeed = -800;
		
		sounds = new SoundBank(1); //Jump sound
		sounds.setEmitter(this);
		
		d = Stage.getCurrentStage().stageData;
	}

	@Override
	public void handleInput(PressedButtons pb) 
	{
		if(flySpeed != 0)
		{
			if(pb.up)
				tryUp(flySpeed);
			
			if(pb.down)
				tryDown(flySpeed);
			
			if(pb.left)
				tryLeft(flySpeed);
			
			if(pb.right)
				tryRight(flySpeed);
			
			return;
		}
		
		/*
		 * Global variables
		 */
		boolean leftInput  = !blockInput && pb.left, rightInput = !blockInput && pb.right, upInput = !blockInput && pb.up;
		boolean checkAllowed = true;
		d = Stage.getCurrentStage().stageData;

		/*
		 * Steep slopes checks
		 */
		int x  = (int)  loc.x, 
			x2 = (int) (loc.x + width), 
			y  = (int) (loc.y + height);
		
		boolean canDown  = canGoDown(),
				canLeft  = canGoLeft(),
				canRight = canGoRight();
		
		if(!canDown && !canLeft && (!MovableObject.outOfBounds(x + 2, y + 2) && (d[y + 2][x + 1] != SOLID || d[y + 2][x + 2] != SOLID)))
		{
			loc.x++;
			vy = -60;
			leftInput = false;
		}
		else if (!canDown && !canRight && (!MovableObject.outOfBounds(x - 2, y + 2) && (d[y + 2][x2 - 1] != SOLID || d[y + 2][x2 - 2] != SOLID)))
		{
			loc.x--;
			vy = -60;
			rightInput = false;
		}
		
		/* 
		 * The following code is related to wall jumping and contains some global variables used everywhere in this method.
		 */
		canLeft  = canGoLeft ();
		canRight = canGoRight();
		boolean leftWall  = leftWall (),
				rightWall = rightWall();
		
		wallSliding = allowWallSlide && canGoDown() && 
				   (((wallSliding || leftInput ) && leftWall ) || 
				    ((wallSliding || rightInput) && rightWall));

		if(wallSliding)
		{
			checkAllowed = false;
			if(!oldInput)
				jumpAllowed = true;
		
			if(counter > 0)
				jumpAllowed = false;
			else if(allowWallJump && !canLeft && upInput && jumpAllowed)
			{
				leftInput = false;
				vx = -120;
				sounds.playSound(0);
			}
			else if(allowWallJump && !canRight && upInput && jumpAllowed)
			{
				rightInput = false;
				vx = 120;
				sounds.playSound(0);
			}
		}
		else if(allowWallSlide && allowWallJump && !canDown && upInput)
		{
			if(leftInput && leftWall && jumpAllowed)
			{
				leftInput = false;
				vx = -120;
				sounds.playSound(0);
			}
			else if(rightInput && rightWall && jumpAllowed)
			{
				rightInput = false;
				vx = 120;
				sounds.playSound(0);
			}
		}
		oldInput = upInput;
		
		/*
		 * The following code is related to running and ground sliding.
		 */		
		moving = false;
		prevVX = vx;
		
		if(leftInput || rightInput)
		{
			moving = true;
			
			if(leftInput)
			{
				facing = Direction.W;
				if(!canLeft && 0 > vx)
					vx = 0;

				if(vx < maxX)
					vx += accX * DELTA + boostX;
			}
			else
			{
				facing = Direction.E;
				if(!canRight && 0 < vx)
					vx = 0;
				
				if(-vx < maxX)
					vx -= accX * DELTA + boostX;
			}
			float targetX = loc.x - vx * DELTA;
			
			if(vx > 0)
				runLeft(targetX);
			else if(vx < 0)
				runRight(targetX);
		}
		
		if(!moving)
		{		
			float targetX;
			if(vx > 0)
			{
				vx -= accX * DELTA;
				targetX = loc.x - vx * DELTA;
				
				runLeft(targetX);
			}
			if(vx < 0)
			{
				vx += accX * DELTA;
				targetX = loc.x - vx * DELTA;
				
				runRight(targetX);
			}
			if(vx == prevVX)
				vx = 0;
		}

		/*
		 * The following code is related to jumps and gravity
		 */
		if(wallSliding)
			counter = 0;
		
	    if(!canGoDown() && vy <= 0)
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
	    	boolean falling = vy < 0; 
	    	float nextY = loc.y - vy * DELTA;	    	
	    	
		    if(!falling)
		    {
		    	if(canGoUp(nextY))
		    	{
		    		if(!canGoDown())
		    			sounds.playSound(0);
		    		moveTo(loc.x, nextY);
		    	}
		    	else
		    	{
		    		vy = 0;	//If we jump into an ceiling, we start falling down.
		    		jumpAllowed = false;
		    	}
		    }
		    else if(falling && canGoDown(nextY))
		    	moveTo(loc.x, nextY);
		    else
		    	tryDown(5);
		    
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
	}
	
	@Override
	public Image2D getFrame() 
	{
		boolean stopped = image.isStopped();
		if(!moving)
			image.stop(true);
			
		Image2D img =  super.getFrame();
		image.stop(stopped);
		
		return img;
	}
	
	/**
	 * The sound to play when jumping.
	 * @param sound The sound.
	 */
	public void setJumpingSound(Sound sound)
	{
		sounds.setSound(0, sound);
		sounds.setDelay(0, 20);
	}
	
	/**
	 * Whether or not to enable wall sliding.
	 * @param slide True to enable wall sliding(default).
	 */
	public void enableWallSlide(boolean slide)
	{
		allowWallSlide = slide;
	}
	
	/**
	 * Whether or not to be able to jump when wall sliding.
	 * @param walljump True to allow wall jumping.
	 */
	public void enableWallJump(boolean walljump)
	{
		allowWallJump = walljump;
	}
	
	/**
	 * The {@code GravityMan} do not use a single variable to control the speed.<br>
	 * To alter the move speed, check {@code maxX, accX} and {@code boostX}.
	 */
	@Override
	@Deprecated
	public void setMoveSpeed(float speed)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Disables the control of the character.
	 */
	@Override
	public void freeze()
	{
		blockInput = true;
	}
	
	/**
	 * Reenables the control of the character.
	 */
	@Override
	public void unfreeze()
	{
		blockInput = false;
	}
	
	/**
	 * Allow the character to fly, for debugging purposes.
	 * @param flySpeed The fly speed. 0 to disable.
	 */
	public void flyMode(int flySpeed)
	{
		this.flySpeed = flySpeed;
	}
	
	protected boolean canSlopeLeft(float targetX)
	{
		int y = (int)loc.y - 1, tar = (int) targetX;
		
		for (int i = 0; i < height; i++)
			if(d[y + i][tar] == SOLID)
				return false;
		
		return !isOverlapping(targetX, loc.y);
	}
	
	protected boolean canSlopeRight(float targetX)
	{
		int y = (int)loc.y - 1, tar = (int) (targetX + width);
		
		for (int i = 0; i < height; i++)
			if(d[y + i][tar] == SOLID)
				return false;
		
		return !isOverlapping(targetX, loc.y);
	}
	
	protected boolean leftWall()
	{
		int y = (int)loc.y, 
			x = (int)loc.x;
		
		if(isOverlapping(loc.x - 1, loc.y))
			return true;
		
		for (int i = 0; i < height / 2; i++)
			if(d[y + i][x - 1] == SOLID)
				return true;

		return false;
	}
	
	protected boolean rightWall()
	{
		int y = (int)loc.y, 
			x = (int)(loc.x + width);
		
		if(isOverlapping(loc.x + 1, loc.y))
			return true;
		
		for (int i = 0; i < height / 2; i++)
			if(d[y + i][x + 1] == SOLID)
				return true;
		
		return false;
	}
	
	protected void runLeft(float targetX)
	{
		if(isOverlapping(targetX, loc.y))
		{
			vx = 0;
			return;				
		}
		
		for(float next = loc.x; next >= targetX; next-= 0.1f)
		{
			if(canGoLeft(next))
			{
				loc.x = next;
				if(canGoDown(loc.y + 1) && !canGoDown(loc.y + 2))
					loc.y++;
			}
			else if(canSlopeLeft(next))
			{
				moveTo(next, loc.y - 1);
				tryDown(1);
			}
			else
			{
				vx = 0;
				return;
			}
		}
	}
	
	protected void runRight(float targetX)
	{
		if(isOverlapping(targetX, loc.y))
		{
			vx = 0;
			return;				
		}
		
		for(float next = loc.x; next <= targetX; next+= 0.1f)
		{
			if(canGoRight(next))
			{
				loc.x = next;
				if(canGoDown(loc.y + 1) && !canGoDown(loc.y + 2))
					loc.y++;
			}
			else if(canSlopeRight(next))
			{
				moveTo(next, loc.y - 1);
				tryDown(1);
			}
			else
			{
				vx = 0;
				break;
			}
		}
	}
	
	private void reset()
	{
    	vy = counter = 0;
    	jumpStarted = false;
	}
}