package game.movable;

import game.core.Enemy;
import game.core.Engine.Direction;
import game.core.Fundementals;
import game.core.MovableObject;
import game.essentials.Animation;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import kuusisto.tinysound.Sound;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * The {@code Boo} class represent the ghost from Super Mario Bros. The ghost will move towards its target when turned away from it.<br>
 * The victims {@code HitEvent} will be fired when the ghost collides with them.
 * @author Pojahn Moradi
 */
public class Boo extends Enemy
{
	private MovableObject victim;
	private Animation<Image2D> hideImage, huntImage;
	private boolean stone, resetHideImage, resetHuntImage, decline;
	private boolean hunting;
	public float velocity, acc, maxSpeed, unfreezeRadius;
	
	/**
	 * Constructs a {@code Boo} with default settings.
	 * @param x The X coordinate to start at.
	 * @param y The Y coordinate to start at.
	 * @param victim The {@code MovableObject} to chase/follow.
	 */
	public Boo(float x, float y, MovableObject victim)
	{
		super();
		currX = x;
		currY = y;
		this.victim = victim;
		maxSpeed = 3;
		acc = 0.03f;
		unfreezeRadius = 70;
		sounds = new SoundBank(1);
		sounds.setEmitter(this);
	}
	
	@Override
	public Boo getClone(float x, float y)
	{
		Boo boo = new Boo(x, y, victim);
		copyData(boo);
		
		if(cloneEvent != null)
			cloneEvent.cloned(boo);
		
		return boo;
	}
	
	public void copyData(Boo dest)
	{
		super.copyData(dest);
		
		if(hideImage != null)
			dest.hideImage = hideImage.getClone();
		dest.huntImage = huntImage.getClone();
		dest.stone = stone;
		dest.resetHideImage = resetHideImage;
		dest.resetHuntImage = resetHuntImage;
		dest.decline = decline;
		//dest.prevFacing = prevFacing;
		dest.velocity = velocity;
		dest.acc = acc;
		dest.maxSpeed = maxSpeed;
		dest.unfreezeRadius = unfreezeRadius;
	}
	
	@Override
	public void moveEnemy() 
	{
		if(decline)
			facing = (victim.currX + victim.width / 2 > currX + width) ? Direction.E : Direction.W;
		
		if(canSneak())
		{
			if(maxSpeed > velocity + acc)
				velocity += acc;
			
			if(resetHideImage && hideImage != null)
				hideImage.reset();
			
			moveToward(victim.currX + victim.width / 2, victim.currY + victim.height / 2, velocity);
			
			if(!hunting)
				image = huntImage;
			
			if(stone && !hunting)
				victim.allowOverlapping(this);
			
			hunting = true;
			
			sounds.trySound(0, false);
		}
		else
		{
			sounds.allowSound(0);
			
			if(hideImage != null && hunting)
				super.setImage(hideImage);

			velocity = 0;
			
			if(resetHuntImage)
				huntImage.reset();
			
			if(stone)
			{
				if(hunting)
					victim.avoidOverlapping(this);
				
				hunting = false;
				return;//Avoid doing a collision check.
			}
			hunting = false;
		}
		if(collidesWith(victim))
		{
			victim.runHitEvent(this);
			velocity = 0;
		}
	}
	
	/**
	 * Sets the image used when chasing.
	 */
	@Override
	public void setImage(Animation<Image2D> obj)
	{
		super.setImage(obj);
		huntImage = obj;
	}
	
	/**
	 * Sets the image used when hiding. If none is set, the default/hunting image will be used as hiding image.
	 */
	public void setHideImage(Animation<Image2D> hideImage)
	{
		this.hideImage = hideImage;	
	}
	
	/**
	 * Whether or not to become an solid object when hiding.
	 * @param stone True if this ghost should become and solid to its victim when hiding.
	 */
	public void isStone(boolean stone)
	{
		this.stone = stone;
	}
	
	/**
	 * The sound to play when the chasing begins.
	 * @param sound The sound.
	 */
	public void setDetectSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}
	
	/**
	 * Whether or not to reset the hide image when the ghost is switching into hunt mode.<br>
	 * This flag us used if you have a non-looping {@code Frequency} object. Example a ghost that transform to a stone when hiding.
	 * @param reset If this entity should reset its hide image when hunting.
	 */
	public void resetHideImage(boolean reset)
	{
		this.resetHideImage = reset;
	}
	
	/**
	 * Whether or not to reset the hunt image when the ghost is switching into hide mode.<br>
	 * This flag is used for non-looping {@code Frequency} objects.
	 * @param reset True if the image should be reseted.
	 */
	public void resetHuntImage(boolean reset)
	{
		this.resetHuntImage = reset;
	}
	
	/**
	 * Whether or not to use directions other than E and W for a multi-faced unit. This can be used if you want to achieve classic Boo-style image where there is only a W and E image.<br>
	 * All the 8 facings images have to exists thought(but the can be blank).
	 * @param decline Whether or not to decline facings that are not W or E.
	 */
	public void declineNonVert(boolean decline)
	{
		this.decline = decline;
		setManualFacing(true);
	}

	@Override
	public void drawSpecial(SpriteBatch batch) {}
	
	protected boolean canSneak()
	{
		if(stone && !hunting && Fundementals.distance(currX + width / 2, currY + height / 2, victim.currX + victim.width / 2, victim.currY + victim.height / 2) < unfreezeRadius)
			return false;
		
		boolean toTheLeft = currX + width / 2 > victim.currX;
		
		if( toTheLeft && (victim.facing == Direction.NW || victim.facing == Direction.W || victim.facing == Direction.SW))
			return true;
		if(!toTheLeft && (victim.facing == Direction.NE || victim.facing == Direction.E || victim.facing == Direction.SE))
			return true;
			
		return false;
	}
}
