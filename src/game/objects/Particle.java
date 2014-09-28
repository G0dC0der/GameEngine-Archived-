package game.objects;

import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.Stage;
import game.essentials.SoundBank;
import kuusisto.tinysound.Sound;

/**
 * The {@code Particle} is an "use once" object, meaning once the last frame of its animation have been displayed, the object is discarded from the game. 
 * This class is often associated with explosions etc.
 * There are two ways to use this class, either with the empty constructor or the custom constructor, which will trigger the victims {@code HitEvent} in case of collision.
 * @author Pojahn Moradi
 */
public class Particle extends GameObject implements Event
{
	protected GameObject[] victims;
	
	/**
	 * Constructs a customized {@code Particle} where the animation is capable of interacting with the given victims.
	 * @param x The x coordinate to spawn the particle at.
	 * @param y The y coordinate to spawn the particle at.
	 * @param victims The object capable of being hit by the particle.
	 */
	public Particle(float x, float y, GameObject... victims)
	{
		currX = x;
		currY = y;
		if(victims != null && victims.length > 0)
			this.victims = victims;
		sounds = new SoundBank(1);
		sounds.setEmitter(this);
		
		addEvent(this);
	}
	
	/**
	 * Constructs a {@code Particle} with no capabilities to interact with {@code GameObjects}.<br>
	 * The position of the {@code Particle} will later be set when cloning it.
	 */
	public Particle()
	{
		this(0,0,new GameObject[0]);
	}

	@Override
	public void eventHandling() 
	{
		sounds.trySound(0, false);

		if (!visible || currImage.getIndex() >= currImage.getArray().length - 2)
			Stage.STAGE.discard(this);

		if(victims != null)
			for (GameObject go : victims)
				if (go.haveHitEvent() && collidesWith(go))
					go.runHitEvent(this);
	}
	
	/**
	 * The sound to play when the {@code Particle} appears.
	 * @param sound The sound.
	 */
	public void setIntroSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}
	
	@Override
	public Particle getClone(float x, float y)
	{
		Particle p = new Particle(x, y, victims);
		copyData(p);
		
		return p;
	}
}