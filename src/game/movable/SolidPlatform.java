package game.movable;

import game.core.Engine;
import game.core.GameObject;
import game.core.MovableObject;
import game.core.Stage;
import game.essentials.Animation;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import kuusisto.tinysound.Sound;

/**
 * A {@code SolidPlatform} represent a movable platform that subjects can stand and slide(if subject is a {@code GravityMan}) on.<br>
 * The platform can also be a tile deformer, meaning it will convert the tile the platform is standing on to the given tile.<br>
 * Furthermore, the {@code MovablePlatform} can behave like a "weak platform", meaning it will collapse after collision.
 * @author Pojahn Moradi
 *
 */
public class SolidPlatform extends PathDrone
{
	private MovableObject[] subjects;
	private boolean weak, tileDeformer, transformBack, strict, harsh;
	private int destroyFrames;
	private byte transformTo;
	private Animation<Image2D> destroyImage;
	private GameObject target;
	private float targetOffsetX, targetOffsetY;
	private int counter = 0;
	private boolean collapsing;
	
	/**
	 * Constructs a {@code MovableObject}.
	 * @param x The X coordinate to start at.
	 * @param y The Y coordinate to start at.
	 * @param subjects The object capable of interacting with the platform.
	 */
	public SolidPlatform(float x, float y, MovableObject... subjects) 
	{
		super(x,y);
		this.subjects = subjects;
		destroyFrames = 100;
		transformTo = Engine.SOLID;
		harsh = true;
		sounds = new SoundBank(1);
		sounds.setEmitter(this);
		
		for(MovableObject mo : subjects)
			mo.avoidOverlapping(this);
	}
	
	@Override
	public SolidPlatform getClone(float x, float y)
	{
		SolidPlatform p = new SolidPlatform(x, y, subjects);
		copyData(p);
		
		if(cloneEvent != null)
			cloneEvent.cloned(p);
		
		return p;
	}
	
	protected void copyData(SolidPlatform dest)
	{
		super.copyData(dest);
		dest.weak = weak;
		dest.tileDeformer = tileDeformer;
		dest.transformBack = transformBack;
		dest.strict = strict;
		dest.destroyFrames = destroyFrames;
		dest.transformTo = transformTo;
		dest.destroyImage = destroyImage;
		dest.harsh = harsh;
	}
	
	/**
	 * The sound to play when a subject for the first time collide with the weak platform.
	 * @param sound The sound.
	 */
	public void setCollapseSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}

	@Override
	public void moveEnemy()
	{
		if(tileDeformer && transformBack)
			deformBack();
		
		if(target == null)
			super.moveEnemy();
		else
		{
			currX = target.currX + targetOffsetX;
			currY = target.currY + targetOffsetY;
		}
		
		if(collapsing && counter++ > destroyFrames)
		{
			Stage.getCurrentStage().discard(this);
			return;
		}
		
		if(tileDeformer)
			deform();
		
		for(MovableObject mo : subjects)
		{
			if(collides(mo))
			{
				if(weak)
				{
					collapsing = true;
					
					sounds.trySound(0, false);
					if(destroyImage != null)
						image = destroyImage;
				}
				
				adjust(mo, harsh);
			}
		}
	}
	
	@Override
	public void dismiss()
	{
		for(MovableObject mo : subjects)
			mo.allowOverlapping(this);
		
		if(tileDeformer && transformBack)
			deformBack();
	}
	
	/**
	 * Allows you to enable tile transformation, which means the platform will convert all the tiles it is currently standing on to the given tile type.
	 * @param tileDeformer Whether or not to transform tile.
	 * @param transformTo The tile type to transforms to.
	 */
	public void setTileDeformer(boolean tileDeformer, byte transformTo)
	{
		this.tileDeformer = tileDeformer;
		this.transformTo = transformTo;
	}
	
	/**
	 * In case tile transformation is enabled, this flag decide whether or not to transform the tile on its previous position back to its original type.
	 * @param transformBack True if the platform should transform back.
	 */
	public void setTransformBack(boolean transformBack)
	{
		this.transformBack = transformBack;
	}
	
	/**
	 * Allows you to enable "weak" effect, which means the platform will collapse after the first interaction with a subject.
	 * @param weak True to enable "weak" effect.
	 */
	public void setWeak(boolean weak)
	{
		this.weak = weak;
	}
	
	/**
	 * In case "weak" effect is enabled, this method controls the amount of frames before the weak platform collapses after a interaction with a subject.
	 * @param destroyFrames Time in frames.
	 */
	public void setDestroyFrames(int destroyFrames)
	{
		this.destroyFrames = destroyFrames;
	}
	
	/**
	 * The image to use when the weak platform have interacted with a subject.
	 * @param destroyImage The image.
	 */
	public void setDestroyImage(Animation<Image2D> destroyImage)
	{
		this.destroyImage = destroyImage;
	}
	
	/**
	 * Allows you to specify the glue mode.
	 */
	public void setStrictGlueMode(boolean strict)
	{
		this.strict = strict;
	}
	
	/**
	 * Allow you to disable harsh pushing which means you wont get crushed if pushed towards a wall.
	 * @param harsh False to disable harsh pusing.
	 */
	public void setHarshResponse(boolean harsh)
	{
		this.harsh = harsh;
	}
	
	
	/**
	 * Use {@code Factory.pathDroneFollow} instead.
	 */
	@Deprecated
	public void followTarget(GameObject target, float targetOffsetX, float targetOffsetY)
	{
		this.target = target;
		this.targetOffsetX = targetOffsetX;
		this.targetOffsetY = targetOffsetY;
	}
	
	/**
	 * Forces the platform to collapse.
	 */
	public void forceCollapse()
	{
		collapsing = true;
		sounds.trySound(0, true);
		if(destroyImage != null)
			image = destroyImage;
	}
	
	/**
	 * Rectangular hitbox is enforced on {@code SolidPlatform}.
	 */
	@Override
	@Deprecated
	public final void setHitbox(Hitbox hitbox)
	{
		throw new UnsupportedOperationException("Solid Platform must use rectangular hitbox.");
	}
	
	/**
	 * Transform the tile on the platforms previous position to the original type.
	 */
	protected void deformBack()
	{
		byte[][] d = stage.stageData;
		for(int x = 1; x < width - 2; x++)
			for(int y = 1; y < height - 2; y++)
			{
				int posX = (int) (x + getPrevX());
				int posY = (int) (y + getPrevY());

				d[posY][posX] = stage.getCloneData(posX, posY); 
			}
	}
	
	/**
	 * Transform the tile on the platforms current position to the set tile type.
	 */
	protected void deform()
	{
		byte[][] d = Stage.getCurrentStage().stageData;
		
		for(int x = 1; x < width - 2; x++)
			for(int y = 1; y < height - 2; y++)
			{
				int posX = (int) (x + currX);
				int posY = (int) (y + currY);
				
				d[posY][posX] = transformTo; 
			}
	}
	
	/**
	 * Checks if the given unit is standing/sliding/touching this platform.
	 */
	protected boolean collides(MovableObject mo)
	{
		boolean bool = strict && (collidingWhen(this, currX, currY - 1, mo)             || collidingWhen(mo, mo.currX, mo.currY + moveSpeed + 2, this) ||
				   				  collidingWhen(this, currX - moveSpeed - 4, currY, mo) || collidingWhen(this, currX + moveSpeed + 4, currY, mo));
		
		if(!bool)
			bool = !strict && (collidingWhen(this, currX, currY - 1, mo) || collidingWhen(this, currX - 1, currY, mo) || 
					           collidingWhen(this, currX + 1, currY, mo));
		
		return bool;
	}
	
	/**
	 * Move go1 to the specified points and check for collision with go2.
	 */
	protected static boolean collidingWhen(GameObject go1, float go1X, float go1Y, GameObject go2)
	{
		float x = go1.currX;
		float y = go1.currY;
		go1.currX = go1X;
		go1.currY = go1Y;
		
		boolean colliding = go1.collidesWith(go2);
		
		go1.currX = x;
		go1.currY = y;
		
		return colliding;
	}
}
