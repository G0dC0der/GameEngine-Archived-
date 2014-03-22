package game.objects;

import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.MovableObject;
import game.core.Stage;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import kuusisto.tinysound.Sound;

/**
 * The {@code BlockSpawner} spawns the given blocks(with delay between the each spawn) when one of the given users collide with it.<br>
 * The blocks are added to the game when the {@code BlockSpawner}, which resembles a <b>button</b>, is being pressed down. The blocks are discard(with delay between each removal) when the button no longer pressed down.
 * @author Pojahn Moradi
 *
 */
public class BlockSpawner extends GameObject implements Event
{
	private boolean solid, permanent, resetBlockImage, trigger;
	private int spawnDelay, removeDelay;
	private MovableObject[] users;
	private GameObject[] blocks;
	private Frequency<Image2D> actionImage, orgImage;
	private Particle removePart;
	private int spawnCounter, removeCounter, index;
	private boolean cleared;
	private final Stage s = Stage.STAGE;
	
	/**
	 * Construct a {@code BlockSpawner} at the given point with the given users.
	 * @param x The X coordinate to start at.
	 * @param y The y coordinate to start at.
	 * @param users The units capable of pressing the button.
	 */
	public BlockSpawner(float x, float y, MovableObject... users)
	{
		currX = x;
		currY = y;
		this.users = users;
		sounds = new SoundBank(2);//0 = Spawn sound, 1 = remove sound
		cleared = true;
		spawnDelay = removeDelay = 10;
		
		addEvent(this);
	}
	
	/**
	 * The objects(blocks) to spawn when the button is being pushed down.
	 * @param blocks The blocks.
	 */
	public void setBlocks(GameObject... blocks)
	{
		GameObject[] bls = new GameObject[blocks.length + 1];
		bls[0] = null;
		for(int i = 1; i < bls.length; i++)
			bls[i] = blocks[i - 1];
		
		this.blocks = bls;
	}
	
	/**
	 * Whether or not the objects spawned will be solid to the users.
	 * @param solid True to make the blocks solid.
	 */
	public void blocksSolid(boolean solid)
	{
		this.solid = solid;
	}
	
	/**
	 * Determines whether or not if the blocks spawned should stay there permanently rather than disappearing when the button is released.
	 * @param permanent True to enable permanent blocks.
	 */
	public void setPermanent(boolean permanent)
	{
		this.permanent = permanent;
	}
	
	/**
	 * The spawn interval when the button is pressed down.
	 * @param spawnDelay Time in frames.
	 */
	public void setSpawnDelay(int spawnDelay)
	{
		this.spawnDelay = spawnDelay;
		spawnCounter = spawnDelay - 1;
	}
	
	/**
	 * The delete interval when the button is released.
	 * @param removeDelay Time in frames.
	 */
	public void setRemoveDelay(int removeDelay)
	{
		this.removeDelay = removeDelay;
		removeCounter = removeDelay - 1;
	}
	
	/**
	 * The image to use when the button is being pressed down or when pressed down manually.
	 * @param image The image.
	 */
	public void setActionImage(Frequency<Image2D> image)
	{
		actionImage = image;
		orgImage = currImage;
	}
	
	/**
	 * The particle that will be added to the block that is about to be removed.
	 * @param removePart The particle.
	 */
	public void setRemoveParticle(Particle removePart)
	{
		this.removePart = removePart;
	}
	
	/**
	 * Whether or not to reset the block image before adding it to the game.
	 * @param resetBlockImage True to reset the image.
	 */
	public void resetBlockImage(boolean resetBlockImage)
	{
		this.resetBlockImage = resetBlockImage;
	}
	
	/**
	 * The sound to play when a block has been added.
	 * @param sound The sound.
	 */
	public void setSpawnSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}
	
	/**
	 * The sound to play when a block has been removed.
	 * @param sound The sound.
	 */
	public void setRemoveSound(Sound sound)
	{
		sounds.setSound(1, sound);
	}
	
	/**
	 * Allow you to manually push down the button.
	 * @param trigger True to push down the button.
	 */
	public void triggerBlocks(boolean trigger)
	{
		this.trigger = trigger;
	}

	@Override
	public void eventHandling() 
	{
		if(0 > index)
			index = 0;
		
		if(trigger || collidesWithMultiple(users))
		{
			if(actionImage != null)
				currImage = actionImage;
			
			if(index + 1 <= blocks.length - 1 && ++spawnCounter % spawnDelay == 0)
			{
				index++;
				
				if(index == 0)
					return;
				
				s.add(blocks[index]);
				if(resetBlockImage)
					blocks[index].resetImage();
				sounds.playSound(0);
				if(solid)
					for(MovableObject mo : users)
						mo.avoidOverlapping(blocks[index]);
				
				cleared = false;
			}
		}
		else
		{
			if(actionImage != null)
				currImage = orgImage;
			
			if(!cleared && !permanent && ++removeCounter % removeDelay == 0)
			{
				if(index > blocks.length - 1)
					index--;
				
				if(index == 0)
					return;
				
				sounds.playSound(1);
				if(solid)
					for(MovableObject mo : users)
						mo.allowOverlapping(blocks[index]);
				
				if(index <= 0)
					cleared = true;
				
				if(removePart != null)
					s.add(removePart.getClone(blocks[index].currX, blocks[index].currY));
				
				s.discard(blocks[index]);
				index--;
			}
		}
	}
}