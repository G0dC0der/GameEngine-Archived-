package game.core;

import static game.core.EntityStuff.*;
import game.core.Engine.Direction;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * A {@code GameObject} is the primary entity used by Pojahns Game Engine. <br>
 * This class stores information about the objects hitbox, position, size, image, rotation and more.<br>
 * {@code GameObjects} are added to the game by appending them to the stage and are removed in a similar way.
 * @author Pojahn Moradi
 */
public class GameObject
{	
	/**
	 * An {@code Event} can be used in various situations.
	 */
	public interface Event
	{
		/**
		 * The events handling code.
		 */
		void eventHandling(); 
	};
	
	/**
	 * A {@code HitEvent} is triggered by objects that have internal collision detections.
	 * See a code example on how to use HitEvents.
	 */
	public interface HitEvent
	{
		/**
		 * The events handling code.
		 * @param hitter The {@code GameObject} that fired this event.
		 */
		void eventHandling(GameObject hitter); 
	};
	
	/**
	 * A {@code Comparator} for sorting the list after {@code zIndex}.
	 */
	public static final Comparator<GameObject> Z_INDEX_SORT = new Comparator<GameObject>()
	{
		@Override
		public int compare(GameObject o1, GameObject o2) 
		{
			return o1.zIndex - o2.zIndex;
		}
	};
	
	/**
	 * These constants define accuracy of certain "watch" methods.
	 */
	public enum Accuracy {MID, MID_CORNERS, REC_CORNERS};
	
	/**
	 * These are the hitboxes of a {@code GameObject}.
	 */
	public enum Hitbox {RECTANGLE, CIRCLE, EXACT, INVINCIBLE};
	
	/**
	 * The objects X position.
	 */
	public float currX;
	
	/**
	 * The objects Y position.
	 */
	public float currY;
	
	/**
	 * The rotation(in degrees) of the objects image. 
	 */
	public float rotation;
	
	/**
	 * The objects image size will multiply with this value when rendering. Defaults to 1. <br><br>
	 * 
	 * Note that scaling do not effect the objects hitbox.
	 */
	public float scale;
	
	/**
	 * The units width.
	 */
	public float width;
	
	/**
	 * The units height.
	 */
	public float height;
	
	/**
	 * The relative X offset of the objects image.
	 */
	public float offsetX;
	
	/**
	 * The relative Y offset of the objects image.
	 */
	public float offsetY;
	
	protected ArrayList<Event> events;
	protected Frequency<Image2D> currImage;
	protected boolean visible, fast;
	protected Hitbox hitbox;
	protected SoundBank sounds;
	protected HitEvent hitEvent;
	private int id, zIndex;
	boolean drawSpecialBehind;
	LinkedList<Event> removeQueue;
	
	/**
	 * Constructs a {@code GameObject}.
	 */
	public GameObject()
	{
		visible = true;
		hitbox = Hitbox.RECTANGLE;
		width = height = scale = 1;
		currImage = new Frequency<>(1,null);
		events 	  = new ArrayList<>();
		removeQueue = new LinkedList<>();
		sounds = new SoundBank(0);
		id = new Random().nextInt();
	}
	
	public void setImage(Image2D img)
	{
		setImage(new Frequency<>(1, new Image2D[]{img}));
	}
	
	/**
	 * Sets the objects image with the default speed(1).<br>
	 * <b>Warning</b>: If your {@code GameObject} is going to be involved in a pixel perfect collision detection, your image must be an instance of {@code DataImage}.
	 * @param imgs The image/animation to use. 
	 */
	public void setImage(Image2D[] imgs)
	{
		setImage(new Frequency<>(1, imgs));
	}
	
	/**
	 * Sets the objects image with the specified speed.<br>
	 * <b>Warning</b>: If your {@code GameObject} is going to be involved in a pixel perfect collision detection, your image must be an instance of {@code DataImage}.
	 * @param imgs The image/animation to use.
	 */
	public void setImage(int speed, Image2D[] imgs)
	{
		setImage(new Frequency<>(speed, imgs));
	}
	
	/**
	 * Sets the objects image to the given object.<br>
	 * <b>Warning</b>: If your {@code GameObject} is going to be involved in a pixel perfect collision detection, your image must be an instance of {@code DataImage}.
	 * @param imgs The image/animation to use.
	 */
	public void setImage(Frequency<Image2D> obj)
	{
		currImage = obj;
		Image2D[] imgs = obj.getArray();
		width  = imgs[0].getWidth();
		height = imgs[0].getHeight();
	}
	
	/**
	 * Checks if this object is colliding with the argument, taking the both entities hitbox type into consideration.
	 * @return True if the two objects are intersecting, otherwise false.
	 */
	public boolean collidesWith(GameObject obj)
	{
		boolean notRotated = fast || obj.fast || (this.rotation == 0 && obj.rotation == 0);
		
		if (hitbox == Hitbox.RECTANGLE && obj.hitbox == Hitbox.RECTANGLE)
		{
			if(notRotated)
				return rectangleVsRecganlte(this, obj);
			else
				return rotatedRectanglesCollision(this, obj);
		}
		else if (hitbox == Hitbox.RECTANGLE && obj.hitbox == Hitbox.CIRCLE)
		{
//			if(notRotated)
				return circleVsRectangle(obj, this);
//			else
//				return rotatedRectangleVsCircle(this, obj);
		}
		else if (hitbox == Hitbox.CIRCLE && obj.hitbox == Hitbox.RECTANGLE)
		{
//			if(notRotated)
				return circleVsRectangle(this, obj);
//			else
//				return rotatedRectangleVsCircle(this, obj);
		}
		else if (obj.hitbox == Hitbox.CIRCLE && hitbox == Hitbox.CIRCLE)
		{
			return circleVsCircle(this,obj);
		}
		else if (obj.hitbox == Hitbox.INVINCIBLE || hitbox == Hitbox.INVINCIBLE)
		{
			return false;
		}
		else if (obj.hitbox == Hitbox.EXACT || hitbox == Hitbox.EXACT)
		{
//			if(notRotated)
//			{
				if (!rectangleVsRecganlte(this, obj))
					return false;
				
				return pixelPerfect(this,obj);
//			}
//			else
//				return pixelPerfectRotation(this,obj);
		}
		else
			return false;
	}
	
	public boolean collidesWithMultiple(GameObject... objs)
	{
		for(GameObject go : objs)
			if(collidesWith(go))
				return true;
		
		return false;
	}
	
	/**
	 * Checks which direction the given {@code GameObject} is in relation to this unit.
	 * @param go The object.
	 * @return The direction, a constant from game.core.Engine.
	 */
	public Direction monitor(GameObject go)
	{
		if (currX + width < go.currX)
			return Direction.E;
		if (go.currX + go.width < currX)
			return Direction.W;
		if (currY > go.currY + go.height)
			return Direction.N;
		if (go.currX > currY + height)
			return Direction.S;

		return null;
	}

	/**
	 * Return a clone of this object.
	 * Note that no events are cloned.
	 * @param x The X position of the clone.
	 * @param y The Y position of the clone.
	 * @return The cloned object.
	 */
	public GameObject getClone(float x, float y)
	{
		GameObject go = new GameObject();
		go.currX = x;
		go.currY = y;
		copyData(go);
		
		return go;
	}
	
	protected void copyData(GameObject dest)
	{
		dest.currImage = currImage.getClone();
		dest.width = width;
		dest.height = height;
		dest.scale = scale;
		dest.rotation = rotation;
		dest.hitbox = hitbox;
		dest.visible = visible;
		dest.sounds = sounds.getClone();
		dest.sounds.setEmitter(dest);
		dest.fast = fast;
		dest.id = id;
		dest.offsetX = offsetX;
		dest.offsetY = offsetY;
		dest.zIndex = zIndex;
		dest.drawSpecialBehind = drawSpecialBehind;
	}
	
	/**
	 * Whether or not to render this image.
	 * @param visible True if you want this unit to be visible/rendered.
	 */
	public void setVisible (boolean visible)
	{
		this.visible = visible;
	}
	
	/**
	 * Checks if this entity is visible.
	 * @return True if this unit have an image and {@code setVisible} is set to {@code true}.
	 */
	public boolean isVisible()
	{
		return visible && currImage != null;
	}
	
	/**
	 * Allow you to specify the z-index of the unit.<br>
	 * Also hints the engine that the entity list have to be resorted.
	 * @param index The index.
	 */
	public void zIndex(int index)
	{
		if(index != zIndex)
			Stage.STAGE.sort = true;
		
		zIndex = index;
	}
	
	/**
	 * Returns the z-index.
	 * @return The z-index.
	 */
	public int getZIndex()
	{
		return zIndex;
	}
	
	/**
	 * The animation speed. Higher value means slower animation.
	 * @param speed The speed value.
	 */
	public void setAnimationSpeed(int speed)
	{
		currImage.setSpeed(speed);
	}
	
	/**
	 * Resets the animation of the image.
	 */
	public void resetImage()
	{
		currImage.reset();
	}
	
	/**
	 * Called by the rendering method.
	 * @return The correct frame of the animation.
	 */
	public Image2D getFrame()
	{
		return (!visible || currImage == null) ? null : currImage.getObject();
	}
	
	/**
	 * Whether or not to call drawSpecial after rendering this unit or not.
	 * @param behind True if drawSpecial should be called after this unit have been rendered.
	 */
	public void setDrawSpecialBehind(boolean behind)
	{
		drawSpecialBehind = behind;
	}
	
	/**
	 * Can be overridden to render extra stuff.
	 * @param g The rendering context.
	 */
	public void drawSpecial(SpriteBatch batch) {}
	
	/**
	 * Removes the specified event.
	 * @param event The event to remove.
	 */
	public void removeEvent(Event event)
	{
		removeQueue.add(event);
	}
	
	void removeQueuedEvents()
	{
		if(!removeQueue.isEmpty())
		{
			Event event;
			while((event = removeQueue.poll()) != null)
				events.remove(event);
		}
	}
	
	/**
	 * Appends the specified event.<br>
	 * Events are executed once every frame by the engine.
	 * @param event The event to append.
	 */
	public void addEvent(Event event)
	{
		events.add(event);
	}
	
	/**
	 * Runs all the events. This function is usual called from automatic processes and not manually.
	 */
	public void runEvents()
	{
		if(!events.isEmpty())
			for(Event event : events)
				event.eventHandling();
	}
	
	/**
	 * Sets the {@code HitEvent} of this {@code GameObject}.
	 * @param hitEvent The object to use as {@code HitEvent}.
	 */
	public void setHitEvent (HitEvent hitEvent)
	{
		this.hitEvent = hitEvent;
	}
	
	/**
	 * Execute the {@code HitEvent}, if exists. 
	 * @param hitter This is the object that triggered the {@code HitEvent}.
	 */
	public void runHitEvent(GameObject hitter)
	{
		if (hitEvent != null)
			hitEvent.eventHandling(hitter);
	}
	
	/**
	 * Removes the HitEvent.
	 */
	public void removeHitEvent()
	{
		hitEvent = null;
	}
	
	/**
	 * Check if this object have a HitEvent.
	 * @return True if this GameObject have a HitEvent.
	 */
	public boolean haveHitEvent()
	{
		return hitEvent != null;
	}
	
	/**
	 * The hitbox decide what function to use when checking for collisions.<br>
	 * If INVINCIBLE is used, the collision detection method will always return false.
	 * @param hitbox The hitbox to use.
	 */
	public void setHitbox(Hitbox hitbox)
	{
		this.hitbox = hitbox;
	}
	
	/**
	 * Returns this objects hitbox.
	 * @return The hitbox of this {@code GameObject}.
	 */
	public Hitbox getHitbox()
	{
		return hitbox;
	}
	
	/**
	 * Returns the animation speed of this unit.
	 * @return The animation speed used by this GameObject.
	 */
	public int getAnimationSpeed()
	{
		return currImage.getSpeed();
	}
	
	/**
	 * Returns the front position of this unit, rotated or not.<br>
	 * @return The front position.
	 */
	public Point2D.Float getFrontPosition()
	{
		float locX = currX + width / 2;
		float locY = currY + height / 2;
		
		locX += Math.cos(Math.toRadians(rotation)) * (width / 2);
		locY += Math.sin(Math.toRadians(rotation)) * (height / 2);
		
		return new Point2D.Float(locX,locY);
	}
	
	/**
	 * Returns the rare position of this unit, rotated or not.<br>
	 * @return The rare position.
	 */
	public Point2D.Float getRarePosition()
	{
		float locX = currX + width / 2;
		float locY = currY + height / 2;
		
		locX -= Math.cos(Math.toRadians(rotation)) * (width / 2);
		locY -= Math.sin(Math.toRadians(rotation)) * (height / 2);
		
		return new Point2D.Float(locX,locY);
	}
	
	/**
	 * The {@code GameObject} that emits the sound this unit makes. This is most often set to "this" as it makes the most sense.<br>
	 * This value is set automatically in most constructor and this function is almost never used.
	 * @param go The {@code GameObject} that emits the sound this object makes.
	 */
	public void setSoundEmitter(GameObject go)
	{
		sounds.setEmitter(go);
	}
	
	/**
	 * Specifies if you want to use "sound falloff". For this to work, you also need to set an emitter.
	 * This value is set automatically in most constructor and this function is almost never used.
	 * @param falloff True if you want the sounds emitted by this object to have distance checking.
	 */
	public void useSoundFallOff(boolean falloff)
	{
		sounds.useFallOff(falloff);
	}
	
	/**
	 * The values to use when calculating sound volume(which is the distance between the emitter and the focus object).
	 * @param maxDistance Exceed this distance and the volume of the sound emitted by this object is zero.
	 * @param maxVolume The max volume of the sound emitted by this unit.
	 * @param power The power.
	 */
	public void emitterProps(float maxDistance, float maxVolume, float power)
	{
		sounds.maxDistance = maxDistance;
		sounds.maxVolume = maxVolume;
		sounds.power = power;
	}
	
	/**
	 * Called when the object is discarded from the game via the {@code discard} method.<br>
	 * Cleanup work should be done here.
	 */
	public void endUse() {}

	/**
	 * Collision detection on rotated object is an expensive operation and can be disabled if not needed.<br>
	 * Set it to true to treat a rotated object as an unrotated object.
	 * @param fast True if this object should be treated as an unrotated object.
	 */
	public void useFastCollisionCheck(boolean fast)
	{
		this.fast = fast;
	}
	
	/**
	 * Compares this entity's ID with the specified objects ID.<br><br>
	 * 
	 * Every {@code GameObject} have its own unique ID, except cloned ones. They use the same id as their mother object.<br>
	 * This function is usually called inside {@code HitEvents}.
	 * @param clone The GameObject to compare with.
	 * @return True if this {@code GameObject} is a clone of the specified {@code GameObject}.
	 */
	public final boolean sameAs(GameObject clone)
	{
		return id == clone.id;
	}
	
	/**
	 * Forces all the given objects to use the same ID as this one.
	 * @param gameObjects The {@code GameObjects} to use this ones ID.
	 */
	public final void merge(GameObject...gameObjects)
	{
		for(GameObject go : gameObjects)
			go.id = id;
	}
	
	/**
	 * Tests whether or not this {@code GameObject} can see the specified {@code GameObject}.<br>
	 * The hitbox type have no effect in the scanning methods, all hitbox types are treated equally.
	 * @param target The object we want to look at.
	 * @param accuracy The accuracy of the test.
	 * @return True if it can see it, otherwise false.
	 */
	public boolean canSee(GameObject target, Accuracy accuracy)
	{						
		if(!target.visible)
			return false;
		
		int midX1   = (int) (target.currX + target.width / 2),
			midY1   = (int) (target.currY + target.height / 2),
			midX2   = (int) (currX + width / 2),
			midY2   = (int) (currY + height / 2), 
			left1   = (int) (target.currX), 
			left2   = (int) (currX), 
			right1  = (int) (target.currX + target.width), 
			right2  = (int) (currX + width),
			top1    = (int) (target.currY),
			top2    = (int) (currY),
			bottom1 = (int) (target.currY + target.height), 
			bottom2 = (int) (currY + height);
		
		switch (accuracy)
		{
		case MID:
			return EntityStuff.solidSpace(midX1, midY1, midX2, midY2);			
		case MID_CORNERS:
			return EntityStuff.solidSpace(midX1, midY1, left2, top2)    ||
				   EntityStuff.solidSpace(midX1, midY1, right2, top2)   ||
				   EntityStuff.solidSpace(midX1, midY1, left2, bottom2) ||
				   EntityStuff.solidSpace(midX1, midY1, right2, bottom2);
		case REC_CORNERS:
			return EntityStuff.solidSpace(left1, top1, left2, top2) 	    ||
				   EntityStuff.solidSpace(right1, top1, right2, top2)     ||
				   EntityStuff.solidSpace(left1, bottom1, left2, bottom2) ||
				   EntityStuff.solidSpace(right1, bottom1, right2, bottom2);
		default:
			throw new RuntimeException("The specified accuracy is not implemented yet");
		}
	}

	/**
	 * Return the current image of the specified object. Takes multifaced {@code MovableObjects} into account.
	 * @param go The GameObject we want the current image of.
 	 * @return The image this GameObject is currently using.
	 */
	public static Image2D getCorrectImage(GameObject go)
	{
		if(go instanceof MovableObject)
		{
			MovableObject mo = (MovableObject) go;
			if(mo.multiFacings)
				return mo.getRightFrame();
		}
		return go.currImage.getCurrentObject();
	}
}