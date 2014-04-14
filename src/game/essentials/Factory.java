package game.essentials;

import static game.core.Engine.*;
import game.core.Engine;
import game.core.Engine.Direction;
import game.core.EntityStuff;
import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.MainCharacter;
import game.core.MovableObject;
import game.core.MovableObject.TileEvent;
import game.core.Stage;
import game.mains.GravityMan;
import game.movable.PathDrone;
import game.movable.PathDrone.PathData;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * A collection of static method that mostly returns different type of events.
 * @author Pojahn Moradi
 *
 */
public class Factory 
{
	private static Image2D[] LASER_BEAM, LASER_BEGIN, LASER_IMPACT, LASER_CHARGE;
	
	static
	{
		try
		{
			LASER_BEAM = Image2D.loadImages(new File("res/data/laser"),false);
			LASER_BEGIN = Image2D.loadImages(new File("res/data/laser/rear"),false);
			LASER_IMPACT = Image2D.loadImages(new File("res/data/laser/end"),false);
			LASER_CHARGE = Image2D.loadImages(new File("res/data/charge"),false);
		}
		catch(Exception e)
		{
			System.err.println("Failed to load laser resources.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Chains the given set of sounds, playing them after each other.
	 * @param indexes Indexes to the sound array.
	 * @param sounds The sounds to play.
	 * @param loop If the chain should loop.
	 * @return The event.
	 */
	public static Event soundChain(final int[] indexes, final Music[] sounds, final boolean loop)
	{
		return new Event() 
		{
			int counter = 0;
			boolean done;
			
			@Override
			public void eventHandling() 
			{
				if(!done)
				{
					if(counter > indexes.length - 1)
					{
						if(loop)
							counter = 0;
						else
						{
							done = true;
							return;
						}
					}
					
					Music sound = sounds[indexes[counter]];
					
					if(!sound.playing())
						sound.play(false);
					
					if(sound.done())
						counter++;
				}
			}
		};
	}
	
	
	/**
	 * Fades the animation in our or out, depending in the value of {@code strength}. For this event to work correctly, the entire animation should use the same starting alpha value.<br>
	 * If you want to have individual frames in the animation to fade different from each other, you should create multiple instances of this event where each instance is only connected to one(or more) image.<br>
	 * This event is automatically discarded when it is done and should always be added to the stage rather than to a {@code GameObject}.<br>
	 * @param targetAlpha The alpha value we want to reach. When ever the animation alpha have reached its goal, this event will be discarded.
	 * @param strength The amount of alpha to apply to the animation. This can be either negative, for fading out, or positive for fading in. 
	 * @param freq How often to add or subtract {@code strength} to the animation, in frames. 
	 * @param endEvent Not required. This event will be added to the stage when it is done.
	 * @param animation The animation to manipulate the alpha on.
	 * @return The event, which should be added to the stage and NOT a {@code GameObject}.
	 */
	public static Event fade(final float targetAlpha, final float strength, final int freq, final Event endEvent, final Image2D... animation)
	{
		return new Event()
		{
			int counter = 0;
			
			@Override
			public void eventHandling() 
			{
				if(++counter % freq == 0)
				{
					float alpha = animation[0].getColor().a;
						
					if((strength > 0 && alpha > targetAlpha) ||
					   (strength < 0 && alpha < targetAlpha))
					{
						for(Image2D img : animation)
							img.setAlpha(targetAlpha);
						
						Stage.STAGE.discard(this);
						if(endEvent != null)
							Stage.STAGE.add(endEvent);
					}
					else
					{
						float newValue = alpha + strength;
						
						for(Image2D img : animation)
							img.setAlpha(newValue);
					}
				}
			}
		};
	}
	
	/**
	 * Fades a sound in or out, depending on the value given to {@code targetVolume}.
	 * @param sound The sound to fade.
	 * @param targetVolume The target volume.
	 * @param duration The duration, in milliseconds.
	 * @param stopWhenDone Whether or not to stop the sound when the target volume have been reached.
	 * @return The event, which should be appended to the stage.
	 */
	public static Event soundFade(final Music sound, final float targetVolume, final int duration, final boolean stopWhenDone)
	{
		return new Event()
		{	
			int fadeTime = duration;
			double startVolume = sound.getVolume();
			
			@Override
			public void eventHandling() 
			{
				fadeTime -= Engine.getDelta();
				if (fadeTime < 0) 
				{
					fadeTime = 0;
					if (stopWhenDone)
						sound.stop();
					Stage.STAGE.discard(this);
				}
				else
				{
					double offset = (targetVolume - startVolume) * (1 - (fadeTime / (double)duration));
					sound.setVolume(startVolume + offset);
				}
			}
		};
	}
	
	/**
	 * If the distance between {@code go1} and {@code go2} exceed {@code maxDistance}, the sound gets muted. 
	 * @param sound The sound to be used.
	 * @param go1 The first object.
	 * @param go2 The second object.
	 * @param maxDistance The max distance.
	 * @param freq How often to update the volume, in frames. 0 = best visual results but worst performance.
	 * @power How much should the sound increase decrease when approaching it? <br>
	 * Setting this to a low value, such as 1 will means the sound effect will reach {@code max} when the two {@code GameObjects} are really close.
	 * @max The max volume of this sound.
	 * @return The event, which can be appended anywhere.
	 */
	public static Event soundFalloff(final Music sound, final GameObject go1, final GameObject go2, final float maxDistance, final int freq, final float power, final float max)
	{
		return new Event()
		{
			float counter = 0;
			
			@Override
			public void eventHandling() 
			{
				if(freq == 0 || ++counter % freq == 0)
				{
					double distance = EntityStuff.distance(go1, go2);
					float candidate = (float) (power * Math.max((1 / Math.sqrt(distance)) - (1 / Math.sqrt(maxDistance)), 0));
					
					sound.setVolume(Math.min(candidate, max));
				}
			}
		};
	}
	
	/**
	 * Reefer to soundFalloff(Music, GameObject, GameObject, float, int, float, float) for usage.
	 */
	public static Event soundFalloff(Music sound, GameObject go, float x2, float y2, float maxDistance, int freq, float power, float max)
	{
		GameObject go2 = new GameObject();
		go2.currX = x2;
		go2.currY = y2;
		
		return soundFalloff(sound,go,go2,maxDistance,freq,power,max);
	}
	
	/**
	 * Reefer to soundFalloff(Music, GameObject, GameObject, float, int, float, float) for usage.
	 */
	public static Event soundFalloff(final Sound sound, final GameObject go1, final GameObject go2, final float maxDistance, final int freq, final float power, final float max)
	{
		return new Event()
		{
			float counter = 0;
			
			@Override
			public void eventHandling() 
			{
				if(freq == 0 || ++counter % freq == 0)
				{
					double distance = EntityStuff.distance(go1, go2);
					float candidate = (float) (power * Math.max((1 / Math.sqrt(distance)) - (1 / Math.sqrt(maxDistance)), 0));
					
					sound.setVolume(Math.min(candidate, max));
				}
			}
		};
	}
	
	/**
	 * Reefer to soundFalloff(Music, GameObject, GameObject, float, int, float, float) for usage.
	 */
	public static Event soundFalloff(Sound sound, GameObject go, float x2, float y2, float maxDistance, int freq, float power, float max)
	{
		GameObject go2 = new GameObject();
		go2.currX = x2;
		go2.currY = y2;
		
		return soundFalloff(sound,go,go2,maxDistance,freq,power,max);
	}
	
	/**
	 * An alternative way of creating a tile deformer. The tile transformation is applied once every frame if its current position differ from its previous one.<br>
	 * Is not limited to rectangular hitboxes.
	 * @param target The unit which will transform the tile.
	 * @param tileType The tile type to transform to.
	 * @param transformBack Whether or not to transform the tile on its previous position back to its initial state(i e when moving).
	 * @return The event, which can be added anywhere.
	 */
	public static Event tileDeformer(final MovableObject target, final byte tileType, final boolean transformBack)
	{
		return new Event()
		{
			@Override
			public void eventHandling()
			{
				if(target.getPrevX() != target.currX || target.getPrevY() != target.currY)
				{
					byte[][] data  = Stage.STAGE.stageData;
					Frequency<Image2D> img = target.getImage();
					boolean stopped = img.isStopped();
					img.stop(true);
					Image2D image = target.getFrame();
					img.stop(stopped);
					
					int prevX = (int) target.getPrevX(),
						prevY = (int) target.getPrevY(),
						currX = (int) target.currX,
						currY = (int) target.currY;
					
					for(int x1 = prevX, x2 = currX; x1 < prevX + target.width - 1; x1++, x2++)
						for(int y1 = prevY, y2 = currY; y1 < prevY + target.height - 1; y1++, y2++)
						{
							if(transformBack)
								data[y1][x1] = Stage.STAGE.getCloneData(x1, y1);
							
							if(image.getColor(x2 - currX, y2 - currY) != 0)
								data[y2][x2] = tileType;
						}
				}
			}
		};
	}
	
	/**
	 * Creates an {@code EffectEvent} that prints text on the screen.
	 * @param text The text to print.
	 * @param textColor The text color. Null is accepted.
	 * @param font The font to use. 
	 * @param duration The amount of frames the text should be visible.
	 * @param position The position the text will be drawn on.
	 * @param ox The X offset of the text.
	 * @param oy The Y offset of the text.
	 * @param endEvent The event that will be executed(once) when done. Null is accepted.
	 * @return The event.
	 */
	public static GameObject printText(final String text, final Color textColor, final BitmapFont font, final int duration, final GameObject position, final float ox, final float oy, final Event endEvent)
	{
		return new GameObject()
		{
			boolean hasWork = true;
			int counter;
			
			@Override
			public void drawSpecial(SpriteBatch g) 
			{
				if(hasWork)
				{
					if(counter++ > duration)
					{
						hasWork = false;
						if(endEvent != null)
							endEvent.eventHandling();
					}
					if(textColor != null)
						g.setColor(textColor);
					
					font.draw(g, text,position.currX + ox, position.currY + oy);
				}
			}
		};
	}
	
	/**
	 * Manipulates the given {@code GameObjects} offset variables to make it "wobble".
	 * @param go The object to wobble.
	 * @param xMin The minimum X position of the wobble.
	 * @param xMax The maximum X position of the wobble.
	 * @param yMin The minimum Y position of the wobble.
	 * @param yMax The maximum Y position of the wobble.
	 * @param freq How often to add the wobble effect.
	 * @return
	 */
	public static Event wobble(final GameObject go, final float xMin, final float xMax, final float yMin, final float yMax, final float freq)
	{
		return new Event()
		{	
			ThreadLocalRandom r = ThreadLocalRandom.current();
			int counter = 0;
			
			@Override
			public void eventHandling() 
			{
				if(++counter % freq == 0)
				{
					go.offsetX = (float) r.nextDouble(xMin, xMax);
					go.offsetY = (float) r.nextDouble(yMin, yMax);
				}
			}
		};
	}
	
	/**
	 * Plays the given sound.
	 * @param sound The sound to be played.
	 * @return The event.
	 */
	public static Event soundEvent(final Sound sound)
	{
		return new Event()
		{
			@Override
			public void eventHandling() 
			{
				sound.play();		
			}
		};
	}
	
	/**
	 * Forces the {@code tail} to follow the {@code target}. Note that the following functionality is instant.
	 * @param target The object to follow.
	 * @param tail The object that will follow someone.
	 * @param offsetX The offset X of the following.
	 * @param offsetY The offset Y of the following.
	 * @return The event.
	 */
	public static Event follow(final GameObject target, final GameObject tail, final float offsetX, final float offsetY)
	{
		return new Event()
		{
			@Override
			public void eventHandling() 
			{
				tail.currX = target.currX + offsetX;
				tail.currY = target.currY + offsetY;
			}
		};
	}
	
	/**
	 * Updates the given {@code PathDrone} every frame, giving it a follow functionality.
	 * @param target The unit to follow.
	 * @param tail The follower.
	 * @param offsetX The offset X.
	 * @param offsetY The offset Y.
	 * @return The event.
	 */
	public static Event pathDroneFollow(final GameObject target, final PathDrone tail, final float offsetX, final float offsetY)
	{
		return new Event()
		{
			@Override
			public void eventHandling() 
			{				
				float targetX = target.currX + offsetX;
				float targetY = target.currY + offsetY;
				
				tail.clearData();
				tail.appendPath(targetX, targetY);
			}
		};
	}
	
	/**
	 * Creates an event that is behaving like a weak platform.
	 * @param target The {@code GameObject} to behave like a weak platform.
	 * @param destroyAnim The animation to use when the platform is demolishing.
	 * @param destroyTime The amount of frames it takes before the platform is fully demolished(i e it is discarded from the game).
	 * @param removeSound The sound to be played when the object is discarded.
	 * @param users The {@code GameObject} capable of interacting with this weak platform.
	 * @return The event.
	 */
	public static Event weakPlatform(final GameObject target, final Frequency<Image2D> destroyAnim, final int destroyTime, final Sound removeSound, final MovableObject... users)
	{
		return new Event()
		{
			GameObject dummy = new GameObject();
			boolean collapsing = false;
			int destroyCounter = 0;
			
			{
				for(MovableObject mo : users)
					mo.avoidOverlapping(target);
			}
			
			@Override
			public void eventHandling() 
			{
				dummy.currX = target.currX - 1;
				dummy.currY = target.currY - 1;
				dummy.width = target.width + 2;
				dummy.height= target.height+ 2;
				
				if(!collapsing && dummy.collidesWithMultiple(users))
				{
					collapsing = true;
					if(destroyAnim != null)
						target.setImage(destroyAnim);
				}
				if(collapsing && destroyCounter++ > destroyTime)
				{
					for(MovableObject mo : users)
						mo.allowOverlapping(target);
					
					Stage.STAGE.discard(target);
					if(removeSound != null)
						removeSound.play();
				}
			}
		};
	}
	
	/**
	 * Manipulates the {@code GravityMans vx} and {@code vy} variables to add push effect when intersecting with the given tile type. 
	 * @param man The man to apply push effect on.
	 * @param tile The tile the {@code man} should instersect with to trigger the pushing.
	 * @param blowStrength The strength of the push. Must always be posetive.
	 * @param maxStrength The max strength of the pushing. Must always be posetive.
	 * @param dir The direction to push.
	 * @return The event.
	 */
	public static TileEvent windEvent(final GravityMan man, final byte tile, final float blowStrength, final float maxStrength, final Direction dir)
	{
		return new TileEvent()
		{	
			@Override
			public void eventHandling(byte tileType) 
			{
				if(tile == tileType)
				{
					switch (dir)
					{
					case N:
						if(man.vy < maxStrength)
							man.vy += blowStrength;
						break;
					case NE:
						if(man.vy < maxStrength)
							man.vy += blowStrength;
						if(-man.vx < maxStrength)
							man.vx -= blowStrength;
						break;
					case E:
						if(-man.vx < maxStrength)
							man.vx -= blowStrength;
						break;
					case SE:
						if(-man.vy < maxStrength)
							man.vy -= blowStrength;
						if(-man.vx < maxStrength)
							man.vx -= blowStrength;
						break;
					case S:
						if(-man.vy < maxStrength)
							man.vy -= blowStrength;
						break;
					case SW:
						if(-man.vy < maxStrength)
							man.vy -= blowStrength;
						if(man.vx < maxStrength)
							man.vx += blowStrength;
						break;
					case W:
						if(man.vx < maxStrength)
							man.vx += blowStrength;
						break;
					case NW:
						if(man.vy < maxStrength)
							man.vy += blowStrength;
						if(man.vx < maxStrength)
							man.vx += blowStrength;					
						break;
					}
				}
			}
		};
	}
	
	/**
	 * Behaves exactly like {@code Factory.windEvent}, but modifies the position directly rather than altering the velocity.
	 */
	public static TileEvent pushEvent(final MovableObject mo, final byte tile, final float pushStrength, final Direction dir)
	{
		return new TileEvent()
		{	
			@Override
			@SuppressWarnings("deprecation")
			public void eventHandling(byte tileType) 
			{
				if(tile == tileType)
				{
					switch (dir)
					{
					case N:
						if(mo.canGoUp(mo.currY - pushStrength))
							mo.currY -= pushStrength;
						break;
					case NE:
						if(mo.canGoUp(mo.currY - pushStrength))
							mo.currY -= pushStrength;
						if(mo.canGoRight(mo.currX + pushStrength))
							mo.currX += pushStrength;
						break;
					case E:
						if(mo.canGoRight(mo.currX + pushStrength))
							mo.currX += pushStrength;
						break;
					case SE:
						if(mo.canGoDown(mo.currY + pushStrength))
							mo.currY += pushStrength;
						if(mo.canGoRight(mo.currX + pushStrength))
							mo.currX += pushStrength;
						break;
					case S:
						if(mo.canGoDown(mo.currY + pushStrength))
							mo.currY += pushStrength;
						break;
					case SW:
						if(mo.canGoDown(mo.currY + pushStrength))
							mo.currY += pushStrength;
						if(mo.canGoLeft(mo.currX - pushStrength))
							mo.currX -= pushStrength;
						break;
					case W:
						if(mo.canGoLeft(mo.currX - pushStrength))
							mo.currX -= pushStrength;
						break;
					case NW:
						if(mo.canGoUp(mo.currY - pushStrength))
							mo.currY -= pushStrength;
						if(mo.canGoLeft(mo.currX - pushStrength))
							mo.currX -= pushStrength;
						break;
					}
				}
			}
		};
	}
	
	/**
	 * Prints the specified text when the printer collides with one of the subjects.
	 * @param printer The GameObject that prints the text.
	 * @param text The text to print.
	 * @param textColor The color to use. If null is used, the current color of the graphic context will be used.
	 * @param font The font to use.
	 * @param textDuration How long the text will stay active.
	 * @param ox The x offset of the text.
	 * @param oy The y offset of the text.
	 * @param subjects The {@code GameObjects} that can trigger this event.
	 * @return The effect event.
	 */
	public static GameObject textPrinter(final GameObject printer, final String text, final Color textColor, final BitmapFont font, final int textDuration, final int ox, final int oy, final GameObject... subjects)
	{
		return new GameObject()
		{
			int counter = 0;
			
			@Override
			public void drawSpecial(SpriteBatch g) 
			{
				if(counter-- > 0)
				{
					if(textColor != null)
						font.setColor(textColor);
					
					font.draw(g, text, printer.currX + ox, printer.currY + oy);
				}
				else
				{
					for(GameObject go : subjects)
						if(go.collidesWith(printer))
						{
							counter = textDuration;
							break;
						}
				}
			}
		};
	}
	
	/**
	 * Alters the {@code MainCharacter's} health in case of collision with the given {@code GameObject}.
	 * @param obj The object capable of hitting the main character.
	 * @param main The character capable of getting hit.
	 * @param power The power to add(use negative values to deduct) upon collision.
	 * @return The {@code Event}.
	 */
	public static Event hitMain(final GameObject obj, final MainCharacter main, final int power)
	{
		return new Event()
		{
			@Override
			public void eventHandling() 
			{
				if(obj.collidesWith(main))
				{
					main.hit(power);
					main.runHitEvent(obj);
				}
			}
		};
	}
	
	/**
	 * When the specified character intersects with AREA_TRIGGER_0, wall jumping and sliding is disabled and is re-enabled when the character intersect with AREA_TRIGGER_1
	 * @param man The character to apply the effect on.
	 * @return The tile event.
	 */
	public static TileEvent slipperWalls(final GravityMan man)
	{
		return new TileEvent()
		{
			@Override
			public void eventHandling(byte tileType) 
			{
				if(tileType == AREA_TRIGGER_0)
				{
					man.enableWallJump(false);
					man.enableWallSlide(false);
				}
				else if(tileType == AREA_TRIGGER_1)
				{
					man.enableWallJump(true);
					man.enableWallSlide(true);					
				}
			}
		};
	}
	
	/**
	 * Adds the given speed to the specified units rotation value.
	 * @param go The unit to rotate.
	 * @param speed The amount to add every frame.
	 * @return The event.
	 */
	public static Event rotation(final GameObject go, final float speed)
	{
		return new Event()
		{
			@Override
			public void eventHandling() 
			{
				go.rotation += speed;
			}
		};
	}
	
	/**
	 * Simply calls {@code mo.setMoveSpeed(speed)}.
	 * @param mo The unit that will receive the speed change.
	 * @param speed The speed to set.
	 * @return The event.
	 */
	public static Event moveSpeed(final MovableObject mo, final float speed)
	{
		return new Event() 
		{
			@Override
			public void eventHandling() 
			{
				mo.setMoveSpeed(speed);
			}
		};
	}
	
	/**
	 * Decreases the given {@code GameObjects} animation speed until the limit is reached.
	 * @param go The object to modify.
	 * @param brakeSpeed How often, in frames, to slow down the animation speed.
	 * @param limit Stops when the animation speed reaches this number. Higher means slower speed.
	 * @return The event.
	 */
	public static Event animationBrake(final GameObject go, final int brakeSpeed, final int limit)
	{
		return new Event()
		{
			boolean stop = false;
			int counter, endCounter = 0;
			
			@Override
			public void eventHandling() 
			{
				if(!stop && ++counter % brakeSpeed == 0)
				{
					if(endCounter++ > limit)
					{
						stop = true;
						go.setAnimationSpeed(999999999);
						return;
					}
					go.setAnimationSpeed(go.getAnimationSpeed() + 1);
				}
			}
		};
	}
	
	/**
	 * Returns a laser beam with customized look.<br>
	 * Each of the parameters accept null, which means "skip rendering this part".
	 * @param laserBegin The "gunfire" animation, which will be rendered at the source coordinate.
	 * @param laserBeam The actual laser beam. This should be a rectangular image and is stretched and rotated to target the destination coordinate.
	 * @param laserImpact The animation to render at the destination point.
	 * @return The beam.
	 */
	public static LaserBeam threeStageLaser(final Frequency<Image2D> laserBegin, final Frequency<Image2D> laserBeam, final Frequency<Image2D> laserImpact)
	{
		return new LaserBeam()
		{
			class Task
			{
				float srcX, srcY, destX, destY;
				int active;
				
				public Task(float srcX, float srcY, float destX, float destY, int active) 
				{
					this.srcX = srcX;
					this.srcY = srcY;
					this.destX = destX;
					this.destY = destY;
					this.active = active;
				}
			}
			
			LinkedList<Task> tasks = new LinkedList<>();
			
			@Override
			public void fireAt(float srcX, float srcY, float destX, float destY, int active) 
			{
				tasks.add(new Task(srcX,srcY,destX,destY,active));
			}
			
			@Override
			public void renderLasers(SpriteBatch b) 
			{
				int size = tasks.size();
				for(int i = 0; i < size; i++)
				{
					final Task t = tasks.get(i);
					final float angle = (float)EntityStuff.getAngle(t.srcX, t.srcY, t.destX, t.destY);
					
					if(laserBeam != null)
					{
						Image2D beam = laserBeam.getObject();
						float dx = (float) (beam.getHeight() / 2 * Math.cos(Math.toRadians(angle - 90)));
						float dy = (float) (beam.getHeight() / 2 * Math.sin(Math.toRadians(angle - 90)));
						b.draw(beam, t.srcX + dx, t.srcY + dy, 0, 0, (float)EntityStuff.distance(t.srcX + dx, t.srcY + dy, t.destX, t.destY), beam.getHeight(), 1, 1, angle);
					}
					
					if(laserImpact != null)
					{
						Image2D exp = laserImpact.getObject();
						float halfWidth = exp.getWidth() / 2;
						float halfHeight = exp.getHeight() / 2;
						b.draw(exp, t.destX - halfWidth, t.destY - halfHeight, halfWidth, halfHeight, exp.getWidth(), exp.getHeight(), 1, 1, angle);
					}
					
					if(laserBegin != null)
					{
						Image2D begin = laserBegin.getObject();
						float halfWidth = begin.getWidth() / 2;
						float halfHeight = begin.getHeight() / 2;
						b.draw(begin, t.srcX - halfWidth, t.srcY - halfHeight, halfHeight, halfHeight, begin.getWidth(), begin.getHeight(), 1, 1, angle);
					}
					
					if(0 >= --t.active)
					{
						tasks.remove(t);
						size--;
					}
				}
			}
		};
	}
	
	/**
	 * Returns a {@code LaserBeam} with default images.
	 * @return The beam.
	 */
	public static LaserBeam defaultLaser()
	{
		if(LASER_BEGIN == null || LASER_BEAM == null || LASER_IMPACT == null)
			throw new NullPointerException("The laser resources is null. Check if they still exist.");
		
		Frequency<Image2D> laserBegin = new Frequency<>(3, LASER_BEGIN);
		Frequency<Image2D> laserImage = new Frequency<>(3, LASER_BEAM);
		Frequency<Image2D> laserImpact = new Frequency<>(3, LASER_IMPACT);
		laserImage.pingPong(true);
		laserImpact.pingPong(true);
		
		return threeStageLaser(laserBegin, laserImage, laserImpact);
	}

	/**
	 * Returns a charge {@code LaserBeam} with default images.
	 * @return The beam.
	 */
	public static LaserBeam defaultChargeLaser()
	{
		if(LASER_CHARGE == null)
			throw new NullPointerException("The laser resources is null. Check if they still exist.");
		
		Frequency<Image2D> charge = new Frequency<>(2, LASER_CHARGE);
		charge.pingPong(true);
		
		return threeStageLaser(null, charge, null);
	}
	
	/**
	 * Return an array of random waypoints, where the coordinates can be anywhere inside the given rectangle.
	 * @param x The X position of the rectangle.
	 * @param y The y position of the rectangle. 
	 * @param w The width of the rectangle.
	 * @param h The height of the rectangle.
	 * @param quantity The amount of waypoints to generate.
	 * @return The data.
	 */
	public static PathData[] randomWaypoints(float x, float y, float w, float h, int quantity)
	{
		List<PathData> pdlist = new ArrayList<>(quantity);
		
		for(int i = 0; i < quantity; i++)
		{
			float xp = (float) (x + Math.random() * w);
			float yp = (float) (y + Math.random() * h);
			
			pdlist.add(new PathData(xp, yp, 0, false, null));
		}
		
		return pdlist.toArray(new PathData[pdlist.size()]);
	}
	
	/**
	 * Return an array of random waypoints, where the coordinates can be anywhere inside the current stage.
	 * @return The data.
	 */
	public static PathData[] randomWaypoints()
	{
		return randomWaypoints(0, 0, Stage.STAGE.width, Stage.STAGE.height, new Random().nextInt(100) + 100);
	}
	
	/**
	 *  Return an array of random waypoints, where the coordinate simulates a bouncing effect.<br>
	 *  For example, the first coordinate can be a the left most, and the second coordinate can be either right most, up most or down most.
	 * @param go The object that will use these waypoints.
	 * @return The data.
	 */
	public static PathData[] randomWallPoints(GameObject go)
	{
		int last = -1;
		int quantity = new Random().nextInt(100) + 100;
		List<PathData> pdlist = new ArrayList<>(quantity);
		Random r = new Random();
		
		for(int i = 0; i < quantity; i++)
		{
			int dir = r.nextInt(4);
			if(dir != last)
			{
				last = dir;
				
				Point2D.Float point = getDirection(dir, go);
				pdlist.add(new PathData(point.x, point.y, 0, false, null));
			}
			else
				i--;
		}
		
		return pdlist.toArray(new PathData[pdlist.size()]);
	}
	
	static Point2D.Float getDirection(int dir, GameObject go)
	{
		Point2D.Float point = new Point2D.Float();
		final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
		Random r = new Random();
		
		switch(dir)
		{
		case UP:
			point.x = r.nextInt(Stage.STAGE.width);
			point.y = 0;
			break;
		case DOWN:
			point.x = r.nextInt(Stage.STAGE.width);
			point.y = Stage.STAGE.height - go.height;			
			break;
		case LEFT:
			point.x = 0;
			point.y = r.nextInt(Stage.STAGE.height);
			break;
		case RIGHT:
			point.x = Stage.STAGE.width - go.width;
			point.y = r.nextInt(Stage.STAGE.height);
			break;
		}
		
		return point;
	}
}