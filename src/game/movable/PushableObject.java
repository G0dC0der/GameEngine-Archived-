package game.movable;

import static game.core.Engine.*;
import game.core.Enemy;
import game.core.Engine.Direction;
import game.core.GameObject;
import game.core.MovableObject;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.essentials.SoundBank;

import java.util.LinkedList;

import kuusisto.tinysound.Sound;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * This class represent an object that can be pushed.
 * @author Pojahn Moradi
 *
 */
public class PushableObject extends Enemy
{
	/**
	 * Physics property.
	 */
	public float mass, gravity, damping;
	private float vy, pushLength, pushStrength;
	private boolean useGravity, mustStand;
	private MovableObject[] pushers;
	private GameObject obj;
	private float nextX;
	
	/**
	 * Constructs a {@code PushableObject}.
	 * @param x The starting X coordinate.
	 * @param y The starting Y coordinate.
	 * @param pushers The objects capable of pushing this object.
	 */
	public PushableObject(float x, float y, MovableObject... pushers)
	{
		nextX = currX = x;
		currY = y;
		this.pushers = pushers;
		useGravity = true;
		mass = 1.0f;
		gravity = -500;
		damping = 0.0001f;
		pushLength = 1;
		pushStrength = 1;
		obj = new GameObject();
		sounds = new SoundBank(2); //0 = landing sound, 1 = pushing sound
		resetPrevs();
		
		for(MovableObject mo : pushers)
			mo.avoidOverlapping(this);
	}
	
	@Override
	public PushableObject getClone(float x, float y)
	{
		PushableObject po = new PushableObject(x, y, pushers);
		copyData(po);
		
		return po;
	}
	
	protected void copyData(PushableObject dest)
	{
		super.copyData(dest);
		dest.mass = mass;
		dest.gravity = gravity;
		dest.damping = damping;
		dest.vy = vy;
		dest.pushLength = pushLength;
		dest.pushStrength = pushStrength;
		dest.useGravity = useGravity;
		dest.mustStand = mustStand;
		dest.obj = obj.getClone(0, 0);
		dest.sounds.setDelay(1, sounds.getDelay(1));
		dest.updateDummy();
		
	}
	
	@Override
	public void moveEnemy() 
	{
		if(useGravity)
		{
			if(!canGoDown())
			{
				if(vy < -100)
					sounds.playSound(0);
				vy = 0;
			}
			else
			{
				 vy *= 1.0 - (damping * DELTA);
				 float force = mass * gravity;
				 vy += (force / mass) * DELTA;
				 float nextY = currY - vy * DELTA;
				 if(canGoTo(currX,nextY))
					 moveTo(currX, nextY);
				 else
					 tryDown(10);
			}
		}
		updateDummy();
		if(pushLength > 0)
		{		
			for(MovableObject mo : pushers)
			{
				if(canPush(mo))
				{
					float value = mo.currX - mo.getPrevX();
					
					if(value == 0)	//Bug fix,  when wall sliding and mustStand is true.
					{
						if(mo.facing == Direction.E)
							value++;
						else if(mo.facing == Direction.W)
							value--;
					}

					if(value > 0)
						nextX = currX + pushLength;
					else if(value < 0)
						nextX = currX - pushLength;
				}
			}
		}
		int pos    = (int) currX, 
			target = (int) nextX;
		
		if(pos != target)
		{
			if(target > pos)
			{				
				tryRight(pushStrength);
				if(nextX < currX)
					currX = nextX;
			}
			else
			{
				tryLeft(pushStrength);
				if(nextX > currX)
					currX = nextX;
			}
		}
		
		if(currX != getPrevX())
			sounds.trySound(1, true);
	}
	
	/**
	 * Allows you to enable or disable gravity for the {@code PushableObject}.
	 * @param gravity False to disable gravity.
	 */
	public void useGravity(boolean gravity)
	{
		useGravity = gravity;
	}
	
	/**
	 * Decide if the pushers must stand to be able to push the object.
	 * @param mustStand True if the pusher must stand to push the object.
	 */
	public void mustStand(boolean mustStand)
	{
		this.mustStand = mustStand;
	}
	
	/**
	 * The sound to play when a {@code PushableObject} with gravity is slammed to the ground.
	 * @param sound The sound.
	 */
	public void setSlamingSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}
	
	/**
	 * The sound to play when this object is being pushed.
	 * @param sound The sound.
	 * @param delay How often, in frames, to play the sound.
	 */
	public void setPushingSound(Sound sound, int delay)
	{
		sounds.setSound(1, sound);
		sounds.setDelay(1, delay);
	}
	
	/**
	 * The amount of pixels to push the {@code PushableObject} upon contact with a pusher.
	 * @param pushStrength The strength.
	 */
	public void setPushStrength(float pushStrength)
	{
		if(pushStrength < 0)
			throw new IllegalArgumentException("No negative values allowed.");
		
		this.pushStrength = pushStrength;
	}
	
	/**
	 * The amount of frames to apply the {@code pushStrength} upon contact with a pusher.
	 * @param pushSpeed The length.
	 */
	public void setPushLength(float pushSpeed)
	{
		if(pushSpeed < 0)
			throw new IllegalArgumentException("No negative values allowed.");
		
		this.pushLength = pushSpeed;
	}
	
	@Override
	public void moveTo(float x, float y)
	{
		currX = x;
		currY = y;
		updateDummy();
	}
	
	@Override
	public void setImage(Frequency<Image2D> img)
	{
		super.setImage(img);
		obj.width = width + 2;
		obj.height = height;
	}
	
	@Override
	public void endUse()
	{
		for(MovableObject mo : pushers)
			mo.allowOverlapping(this);
	}
	
	/**
	 * Adds more pushers to the object
	 * @param pushers Objects capable of pushing this rock.
	 */
	public void addPushers(MovableObject... pushers)
	{
		LinkedList<MovableObject> list = new LinkedList<>();
		for(MovableObject mo : this.pushers)
			list.add(mo);
		
		for(MovableObject mo : pushers)
			list.add(mo);
		
		this.pushers = list.toArray(new MovableObject[list.size()]);
		
		for(MovableObject mo : this.pushers)
			mo.avoidOverlapping(this);
	}

	@Override
	public void drawSpecial(SpriteBatch batch) {}
	
	protected boolean canPush(MovableObject pusher)
	{
		if(mustStand)
			return currY + height - 2 > pusher.currY && !pusher.canGoDown() && obj.collidesWith(pusher);
			
		return currY + height - 2 > pusher.currY && obj.collidesWith(pusher);
	}
	
	protected void updateDummy()
	{
		obj.currX = currX - 1;
		obj.currY = currY;
	}
}