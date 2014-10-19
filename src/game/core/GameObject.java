package game.core;

import static game.core.Fundementals.*;
import game.core.Engine.Direction;
import game.essentials.Animation;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * A {@code GameObject} is the primary entity used by Pojahns Game Engine. <br>
 * This class stores information about the objects hitbox, position, size, image, rotation and more.<br>
 * {@code GameObjects} are added to the game by appending them to the stage and are removed in a similar way.
 * @author Pojahn Moradi
 */
public class GameObject
{	
	/**
	 * Cloning {@code GameObject} comes with limitations such as {@code Events} not being cloned. Those problems can be addressed in a {@code CloneEvent}.<br>
	 * These events are executed when a clone is made, with the clone sent as the argument.
	 * @author Pojahn Moradi
	 */
	@FunctionalInterface
	public interface CloneEvent
	{
		/**
		 * The cloned instance can be accessed here to add an {@code Event} for example.
		 * @param clone The cloned object.
		 */
		void cloned(GameObject clone);
	};
	
	/**
	 * An {@code Event} can be used in various situations.
	 */
	@FunctionalInterface
	public interface Event
	{
		/**
		 * The events handling code.
		 */
		void eventHandling(); 
		
		/**
		 * Events that are done are discarded by the engine. The default method always returns false.
		 * @return True if this event is dont and should be discarded by the game.
		 */
		default boolean done()
		{
			return false;
		}
	};
	
	/**
	 * A {@code HitEvent} is triggered by objects that have internal collision detections.
	 * Reefer to the tutorials on usage.
	 */
	@FunctionalInterface
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
	public static final Comparator<GameObject> Z_INDEX_SORT = (o1, o2) -> {return o1.zIndex - o2.zIndex;};
	
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
	 */
	public float scale;
	
	/**
	 * The units width. Should be equal to the image width.
	 */
	public float width;
	
	/**
	 * The units height. Should be equal to the image height.
	 */
	public float height;

	/**
	 * The alpha value used when rendering.
	 */
	public float alpha;
	
	/**
	 * Whether or not to flip the entity´s image horizontally when rendering.
	 */
	public boolean flipX;

	/**
	 * Whether or not to flip the entity´s image vertically when rendering.
	 */
	public boolean flipY;
	
	/**
	 * The relative X offset of the objects image.<br>
	 */
	public float offsetX;
	
	/**
	 * The relative Y offset of the objects image.
	 */
	public float offsetY;
	
	/**
	 * Used for debugging purposes.
	 */
	public String name;
	
	protected CloneEvent cloneEvent;
	protected Animation<Image2D> image;
	protected boolean visible, fast;
	protected Hitbox hitbox;
	protected SoundBank sounds;
	protected HitEvent hitEvent;
	private int id, zIndex;
	boolean drawSpecialBehind;
	LinkedList<Event> removeQueue, events;
	
	/**
	 * Constructs a {@code GameObject} with with, height and scale set to 1 and visibility set to true.
	 */
	public GameObject()
	{
		alpha = 1;
		visible = true;
		hitbox = Hitbox.RECTANGLE;
		width = height = scale = 1;
		image = new Animation<>(1,null, null);
		events 	  = new LinkedList<>();
		removeQueue = new LinkedList<>();
		sounds = new SoundBank(0);
		id = new Random().nextInt();
	}
	
	/**
	 * Returns the width times the scale of the unit.
	 * @return The with.
	 */
	public float width()
	{
		return width * scale;
	}
	
	/**
	 * Return the height times scale of the unit.
	 * @return The height.
	 */
	public float height()
	{
		return height * scale;
	}
	
	/**
	 * Returns the center X position, taking scale into account.
	 * @return The X coordinate.
	 */
	public float centerX()
	{
		return currX + (width() / 2);
	}
	
	/**
	 * Returns the center Y position, taking scale into account.
	 * @return The Y coordinate.
	 */
	public float centerY()
	{
		return currY + (height() / 2);
	}
	
	/**
	 * The half width.
	 */
	public float halfWidth()
	{
		return width() / 2;
	}
	
	/**
	 * The half height.
	 */
	public float halfHeight()
	{
		return height() / 2;
	}
	
	/**
	 * Moves to the specified point, without any performing any checks at all.
	 * @param x The X coordinate to jump to.
	 * @param y The Y coordinate to jump to.
	 */
	public void moveTo(float x, float y)
	{
		currX = x;
		currY = y;
	}
	
	/**
	 * Allow you to set the image for the {@code GameObject}.<br>
	 * This method also set {@code width} and {@code height}.
	 * @param img The image to use.
	 */
	public void setImage(Image2D img)
	{
		setImage(new Animation<>(1, new Image2D[]{img}));
	}
	
	/**
	 * Sets the objects image with the specified speed.<br>
	 * This method also set {@code width} and {@code height}.
	 * @param imgs The image/animation to use.
	 */
	public void setImage(int speed, Image2D[] imgs)
	{
		setImage(new Animation<>(speed, imgs));
	}
	
	/**
	 * Sets the objects image to the given object.<br>
	 * This method also set {@code width} and {@code height}.
	 * @param imgs The image/animation to use.
	 */
	public void setImage(Animation<Image2D> obj)
	{
		image = obj;
		Image2D[] imgs = obj.getArray();
		width  = imgs[0].getWidth();
		height = imgs[0].getHeight();
	}
	
	/**
	 * Returns the object's image.
	 * @return The image of the {@code GameObject}.
	 */
	public Animation<Image2D> getImage()
	{
		return image;
	}
	
	/**
	 * Checks if this object is colliding with the argument, taking the both entities hitbox type into consideration.
	 * @return True if the two objects are intersecting, otherwise false.
	 */
	public boolean collidesWith(GameObject obj)
	{
		boolean straight1 = fast || rotation == 0;
		boolean straight2 = obj.fast || obj.rotation == 0;
		
		if (hitbox == Hitbox.RECTANGLE && obj.hitbox == Hitbox.RECTANGLE)
		{
			if(straight1 && straight2)
				return rectangleVsRecganle(this, obj);
			else
				return rotatedRectanglesCollision(this, obj);
		}
		else if (hitbox == Hitbox.RECTANGLE && obj.hitbox == Hitbox.CIRCLE)
		{
			if(straight1)
				return circleVsRectangle(obj, this);
			else
				return rotatedRectangleVsCircle(this, obj);
		}
		else if (hitbox == Hitbox.CIRCLE && obj.hitbox == Hitbox.RECTANGLE)
		{
			if(straight2)
				return circleVsRectangle(this, obj);
			else
				return rotatedRectangleVsCircle(obj, this);
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
			if(straight1 && straight2)
			{
				if (!rectangleVsRecganle(this, obj))
					return false;
				
				return pixelPerfect(this,obj);
			}
			else
			{
				Rectangle r1 = (rotation == 0) ? new Rectangle(currX, currY, width, height)					: getBoundingBox(this);
				Rectangle r2 = (rotation == 0) ? new Rectangle(obj.currX, obj.currY, obj.width, obj.height) : getBoundingBox(obj);
				
				if(!rectangleVsRectangle(r1.x, r1.y, r1.width, r1.height, r2.x, r2.y, r2.width, r2.height))
					return false;
				
				return pixelPerfectRotation(this, obj);
			}
		}
		else
			return false;
	}
	
	/**
	 * Returns the first {@code GameObject} in the given list this object is colliding with.
	 * @param objs The objects to check with.
	 * @return The object this unit is colliding with.
	 */
	public GameObject collidesWithMultiple(GameObject... objs)
	{
		for(GameObject go : objs)
			if(collidesWith(go))
				return go;
		
		return null;
	}
	
	public boolean at(float x, float y)
	{
		return currX == x && currY == y;
	}
	
	public boolean at(float x, float y, double tolerance)
	{
		return tolerance >= Fundementals.distance(currX, currY, x, y);
	}
	
	/**
	 * Checks which direction the given {@code GameObject} is in relation to this unit.
	 * @param go The object.
	 * @return The direction, which is either N, S, E or W.
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
	 * Return a clone of this object. No events are cloned.
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
		
		if(cloneEvent != null)
			cloneEvent.cloned(go);
		
		return go;
	}
	
	/**
	 * Sets the clone event for this unit.
	 * @param cloneEvent The event to be executed when this {@code GameObject} gets cloned.
	 */
	public void setCloneEvent(CloneEvent cloneEvent)
	{
		this.cloneEvent = cloneEvent;
	}
	
	protected void copyData(GameObject dest)
	{
		dest.image = image.getClone();
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
		dest.alpha = alpha;
		dest.flipX = flipX;
		dest.flipY = flipY;
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
	 * @return True if this unit have an image, {@code setVisible} set to {@code true} and an alpha value higher than 0.0.
	 */
	public boolean isVisible()
	{
		return visible && image != null && alpha > 0.0f;
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
		image.setSpeed(speed);
	}
	
	/**
	 * Resets the animation of the image.
	 */
	public void resetImage()
	{
		image.reset();
	}
	
	/**
	 * Called by the rendering method.
	 * @return The correct frame of the animation.
	 */
	public Image2D getFrame()
	{
		return (!visible || image == null) ? null : image.getObject();
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
	 * Can be overridden to render extra stuff. Called by the engine right before rendering and after updating the entities and translate/scale variables.
	 * @param batch The rendering context.
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
			{
				if(event.done())
					removeEvent(event);
				else
					event.eventHandling();
			}
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
	 * Executes the {@code HitEvent}, if exists. 
	 * @param hitter The object that triggered the {@code HitEvent}.
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
		return image.getSpeed();
	}
	
	/**
	 * Returns the front position of this unit, rotated or not.<br>
	 * @return The front position.
	 */
	public Vector2 getFrontPosition()
	{
		float locX = currX + width() / 2;
		float locY = currY + height() / 2;
		
		locX += Math.cos(Math.toRadians(rotation)) * (width() / 2);
		locY += Math.sin(Math.toRadians(rotation)) * (height() / 2);
		
		return new Vector2(locX,locY);
	}
	
	/**
	 * Returns the rare position of this unit, rotated or not.<br>
	 * @return The rare position.
	 */
	public Vector2 getRarePosition()
	{
		float locX = currX + width() / 2;
		float locY = currY + height() / 2;
		
		locX -= Math.cos(Math.toRadians(rotation)) * (width() / 2);
		locY -= Math.sin(Math.toRadians(rotation)) * (height() / 2);
		
		return new Vector2(locX,locY);
	}
	
	/**
	 * Returns the {@code SoundBank} used by the unit.
	 */
	public SoundBank getSoundBank()
	{
		return sounds;
	}
	
	/**
	 * Called when the object is discarded from the game via the {@code discard} method.<br>
	 * Cleanup work should be done here by overriding it.
	 */
	public void dismiss() {}

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
		
		int midX1   = (int) (target.currX + target.width() / 2),
			midY1   = (int) (target.currY + target.height() / 2),
			midX2   = (int) (currX + width() / 2),
			midY2   = (int) (currY + height() / 2), 
			left1   = (int) (target.currX), 
			left2   = (int) (currX), 
			right1  = (int) (target.currX + target.width()), 
			right2  = (int) (currX + width()),
			top1    = (int) (target.currY),
			top2    = (int) (currY),
			bottom1 = (int) (target.currY + target.height()), 
			bottom2 = (int) (currY + height());
		
		switch (accuracy)
		{
		case MID:
			return Fundementals.solidSpace(midX1, midY1, midX2, midY2);			
		case MID_CORNERS:
			return Fundementals.solidSpace(midX1, midY1, left2, top2)    ||
				   Fundementals.solidSpace(midX1, midY1, right2, top2)   ||
				   Fundementals.solidSpace(midX1, midY1, left2, bottom2) ||
				   Fundementals.solidSpace(midX1, midY1, right2, bottom2);
		case REC_CORNERS:
			return Fundementals.solidSpace(left1, top1, left2, top2) 	    ||
				   Fundementals.solidSpace(right1, top1, right2, top2)     ||
				   Fundementals.solidSpace(left1, bottom1, left2, bottom2) ||
				   Fundementals.solidSpace(right1, bottom1, right2, bottom2);
		default:
			throw new RuntimeException("The specified accuracy is not implemented yet");
		}
	}
}