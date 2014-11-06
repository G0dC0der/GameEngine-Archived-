package game.movable;

import game.core.Engine.Direction;
import game.core.Fundementals;
import game.core.MovableObject;
import game.essentials.SoundBank;
import game.mains.GravityMan;

import java.util.Random;

import kuusisto.tinysound.Sound;

/**
 * This class works only for {@code GravityMan} and represent a bouncing object. Any interaction with a {@code Bouncer} will alter the victims velocity variables, launching him away.
 * @author Pojahn Moradi
 */
public class Bouncer extends PathDrone
{
	private float bounceStrength, shakeX, shakeY;
	private int times, shakeCounter, shakeTime;
	private final GravityMan[] victims;
	private int[] pushCounter;
	private Direction explicitDir, victimDirrections[];
	private boolean shake;
	private final Random r;
	
	/**
	 * Creates a bounce block.
	 * @param x The x coordinate to start at.
	 * @param y The y coordinate to start at.
	 * @param bounceStrength The strength of the push. Use positive values.
	 * @param times How many frames should the bouncing-action apply?
	 * @param explicitDir A constant from game.core.Engine representing which way to push. Use null to push the direction the victim collided from.
	 * @param victims The objects possible of being pushed by this Bounce block.
	 */
	public Bouncer (float x, float y, float bounceStrength, int times, Direction explicitDir, GravityMan... victims)
	{
		super(x,y);
		
		this.times = times;
		this.bounceStrength = bounceStrength;
		this.explicitDir = explicitDir;
		this.victims = victims;
		victimDirrections = new Direction[victims.length];
		pushCounter = new int[victims.length];
		shakeCounter = 0;
		sounds = new SoundBank(1);
		sounds.setEmitter(this);
		r = new Random();
	}
	
	@Override
	public Bouncer getClone(float x, float y)
	{
		Bouncer b = new Bouncer(x, y, bounceStrength, times, explicitDir, victims);
		copyData(b);
		
		if(cloneEvent != null)
			cloneEvent.cloned(b);
		
		return b;
	}
	
	protected void copyData(Bouncer dest)
	{
		super.copyData(dest);
		dest.shakeX = shakeX;
		dest.shakeY = shakeY;
		dest.shakeTime = shakeTime;
		dest.shake = shake;
	}

	/**
	 * Setting {@code shake} to true makes this bouncer vibrate/shake when colliding with a target.
	 * @param shake True to enable shaking/vibrating.
	 * @param shakeTime The amount of frames to shake when colliding with a target.
	 * @param shakeX The X strength of the shake.
	 * @param shakeY The Y strength of the shake.
	 */
	public void setShake(boolean shake, int shakeTime, float shakeX, float shakeY)
	{
		this.shake = shake;
		this.shakeTime = shakeTime;
		this.shakeX = shakeX;
		this.shakeY = shakeY;
	}
	
	/**
	 * The sound to play when a target have collided with this object.
	 * @param sound The sound.
	 */
	public void setShakeSound(Sound sound, int delay)
	{
		sounds.setSound(0, sound);
		sounds.setDelay(0, delay);
	}
	
	@Override
	public void moveEnemy()
	{
		super.moveEnemy();
		
		if(shake && shakeCounter-- > 0)
		{
			float x = (float) r.nextGaussian(),
				  y = (float) r.nextGaussian();
			
			offsetX = x + ((x > 0.5f) ? shakeX : -shakeX);
			offsetY = y + ((y > 0.5f) ? shakeY : -shakeY); 
		}
		else
			offsetX = offsetY = 0;
		float middleX = loc.x + width / 2,
			  middleY = loc.y + height / 2;
		
		for (int i = 0; i < victims.length; i++)
		{
			MovableObject mo = victims[i];
			if (!collidesWith(mo))
				victimDirrections[i] = Fundementals.getDirection(Fundementals.normalize(middleX, middleY, mo.getPrevX() + mo.width / 2, mo.getPrevY() + mo.height / 2));
			else
			{
				pushCounter[i] = times;
				sounds.playSound(0);
			}
		}
		pushSubjects();
	}
	
	void pushSubjects()
	{
		for (int i = 0; i < victims.length; i++)
		{
			if (pushCounter[i]-- <= 0)
				continue;
			
			shakeCounter = shakeTime;
			
			if (pushCounter[i]-- == times){}
			
			GravityMan man = victims[i];
			
			Direction pushingDirr = (explicitDir == null) ? victimDirrections[i] : explicitDir;
			switch (pushingDirr)
			{
			case N:
				man.vy = bounceStrength;
				break;
			case NE:
				man.vy = bounceStrength;
				man.vx = -bounceStrength;
				break;
			case E:
				man.vx = -bounceStrength;
				break;
			case SE:						
				man.vy = -bounceStrength;
				man.vx = -bounceStrength;
				break;
			case S:
				man.vy = -bounceStrength;
				break;
			case SW:
				man.vy = -bounceStrength;
				man.vx = bounceStrength;
				break;
			case W:
				man.vx = bounceStrength;
				break;
			case NW:
				man.vy = bounceStrength;
				man.vx = bounceStrength;
				break;
			}
		}
	}
}