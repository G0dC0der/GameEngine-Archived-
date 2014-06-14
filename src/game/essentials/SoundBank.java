package game.essentials;

import game.core.EntityStuff;
import game.core.GameObject;
import game.core.Stage;
import kuusisto.tinysound.Sound;

/**
 * A {@code SoundBank} is an object used by enemies and game objects to ease the use of sound.
 * @author Pojahn Moradi
 *
 */
public class SoundBank
{
	/**
	 * The amount of frames that have passed since the game started(reseted by the engine every death or win).<br><br>
	 * 
	 * Warning: Should not be modified!
	 */
	public static int FRAME_COUNTER = 0;
	
	private static class Unit
	{
		Sound sound;
		boolean allowed;
		int delay, time;
		
		Unit(Sound sound)
		{
			this.sound = sound;
			allowed = true;
			delay = -1;
		}
	}
	
	private Unit[] units;
	private GameObject emitter;
	private boolean falloff;
	public float maxDistance, maxVolume, power;
	
	/**
	 * Creates a {@code SoundBank} with the given size.
	 * @param size The amount of sounds this object can store.
	 */
	public SoundBank(int size)
	{
		units = new Unit[size];
		maxDistance = 400;
		maxVolume = 1.0f;
		power = 40;
	}
	
	/**
	 * Creates an instance with the given sounds.
	 * @param sounds The sounds this bank will store.
	 */
	public SoundBank(Sound[] sounds)
	{
		setArray(sounds);
	}
	
	/**
	 * Sets the given sound at the given position.<br>
	 * Make sure the index doesn't exceed the size of the bank.
	 * @param index The index to put the sound into.
	 * @param sound The sound.
	 */
	public void setSound(int index, Sound sound)
	{
		if(index < units.length)
			units[index] = new Unit(sound);
	}
	
	/**
	 * Plays the sound at the given index, if one exists.
	 * @param index The songs index.
	 */
	public void playSound(int index)
	{
		trySound(index, true);
	}
	
	/**
	 * Play the sound at the given position, if allowed and if exists.
	 * @param index The songs index.
	 * @param ignore Whether or not to ignore the "allowed" checking. If this flag is false and the sound is allowed to play, the allowed variable will change to false after playing the sound. It can then be reenabled by calling {@code allowSound(int)}.
	 */
	public void trySound(int index, boolean ignore)
	{
		if (index < units.length && units[index] != null && units[index].sound != null)
		{
			if(units[index].delay > 0 && FRAME_COUNTER < units[index].time)
				return;
			
			if(falloff && emitter != null)
			{
				double distance = EntityStuff.distance(emitter, Stage.STAGE.game.getFocusObject());
				double candidate = power * Math.max((1 / Math.sqrt(distance)) - (1 / Math.sqrt(maxDistance)), 0);
				
				units[index].sound.setVolume(Math.min(candidate, maxVolume));
			}
			
			if(ignore)
			{
				units[index].sound.play();
				units[index].time = units[index].delay + FRAME_COUNTER;
			}
			else if(units[index].allowed)
			{
				units[index].allowed = false;
				units[index].sound.play();
				units[index].time = units[index].delay + FRAME_COUNTER;
			}
		}
	}
	
	/**
	 * Returns the delay from the sound at the given index, or -1 if no sound could be fount.
	 * @param index The sounds index.
	 * @return The delay.
	 */
	public int getDelay(int index)
	{
		if(index < units.length && units[index] != null)
			return units[index].delay;
		else
			return -1;
	}
	
	/**
	 * The object that emits the sound. Necessary if you want to use sound falloff.<br>
	 * Usually called automatically.
	 * @param emmiter The object that will emit the sound this bank is playing.
	 */
	public void setEmitter(GameObject emmiter)
	{
		this.emitter = emmiter;
	}
	
	/**
	 * Whether or not to use sound falloff. For this to work, you also need to set an emitter. <br>
	 * The volume of the sound will be the results of a distance check between the emitter and the engines focus object.
	 * @param falloff True if this bank should use sound fall off.
	 */
	public void useFallOff(boolean falloff)
	{
		this.falloff = falloff;
	}
	
	/**
	 * Allow the sound at the given index to be played, if exists.<br>
	 * The sound is later disallowed when playing it with the ignoring flag set to false.
	 * @param index The index.
	 */
	public void allowSound(int index)
	{
		if (index < units.length && units[index] != null && units[index].sound != null)
			units[index].allowed = true;
	}

	/**
	 * Forbids the sound at the given index.
	 * @param index The index.
	 */
	public void forbidSound(int index)
	{
		if (index < units.length && units[index] != null && units[index].sound != null)
			units[index].allowed = false;
	}
	
	/**
	 * Stops the sound at the given index, if exists.
	 * @param index The index.
	 */
	public void stop(int index)
	{
		if (index < units.length && units[index] != null && units[index].sound != null)
			units[index].sound.stop();
	}
	
	/**
	 * Stops all sounds, if any exists.
	 */
	public void stopAll()
	{
		for(int i = 0; i < units.length; i++)
			if(units[i] != null && units[i].sound != null)
				units[i].sound.stop();
	}
	
	/**
	 * Adds a delay limit to the sound at the given index.
	 * @param index The index.
	 * @param delay The amount of frames before a played sound is allowed to be played again.
	 */
	public void setDelay(int index, int delay)
	{
		if(units[index] != null)
			units[index].delay = delay;
	}
	
	
	/**
	 * Reconstructs the instance to use the given sound array.
	 * @param sounds The array to use.
	 */
	public final void setArray(Sound[] sounds)
	{
		if(sounds == null)
			units = new Unit[0];
		else
		{
			units = new Unit[sounds.length];
			for(int i = 0; i < units.length; i++)
				units[i] = new Unit(sounds[i]);
		}
	}
	
	/**
	 * Return the sounds used by this bank.
	 * @return
	 */
	public Sound[] getArray()
	{
		if(units == null)
			return null;
		
		Sound[] sounds = new Sound[units.length];
		for(int i = 0; i < sounds.length; i++)
		{
			if(units[i] != null)
				sounds[i] = units[i].sound;
			else
				sounds[i] = null;
		}
		return sounds;
	}
	
	/**
	 * Returns a clone of this sound bank.
	 * @return A new instance of this object.
	 */
	public SoundBank getClone()
	{
		SoundBank bank = new SoundBank(units.length);
		
		for(int i = 0; i < bank.units.length; i++)
			if(units[i] != null)
				bank.setSound(i, units[i].sound);
		
		bank.falloff = falloff;
		bank.maxDistance = maxDistance;
		bank.maxVolume = maxVolume;
		bank.power = power;
		
		return bank;
	}
}
