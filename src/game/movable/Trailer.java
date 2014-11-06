package game.movable;

import game.core.GameObject;

/**
 * The {@code Trailer} is a {@code PathDrone} that spawns clones of the given object with the given frequency.<br>
 * The objects will be spawned at the {@code Trailers} position.
 * @author Pojahn Moradi
 *
 */
public class Trailer extends PathDrone 
{
	private GameObject[] trailers;
	private int freq, counter, counter2, spitCounter, limit;
	private boolean stop;
	
	/**
	 * Constructs a {@code Trailer} at the given points with no trailers and 10 as frequency.
	 * @param x The starting X position.
	 * @param y The starting y position.
	 */
	public Trailer(float x, float y)
	{
		this(x,y, 10, new GameObject[0]);
	}
	
	/**
	 * Constructs a customized {@code Trailer} instance.
	 * @param x The starting X position.
	 * @param y The starting y position.
	 * @param freq Time in frames between spawns.
	 * @param trailers The objects that will be spawned(cloned).
	 */
	public Trailer(float x, float y, int freq, GameObject... trailers) 
	{
		super(x, y);
		this.freq = freq;
		this.trailers = trailers;
		stop = false;
		counter = counter2 = spitCounter = 0;
		limit = Integer.MAX_VALUE;
	}
	
	@Override
	public void moveEnemy()
	{
		if(canSpit() && trailers.length > 0)
		{
			if (counter2 >= trailers.length)
				counter2 = 0;
			
			if(++counter % freq == 0)
			{
				spitCounter++;
				stage.add(trailers[counter2++].getClone(loc.x, loc.y));
			}
		}
		super.moveEnemy();
	}
	
	/**
	 * The objects to spawn/clone.
	 * @param trailers The objects.
	 */
	public void setSpawners(GameObject... trailers)
	{
		this.trailers = trailers;
		counter2 = 0;
	}
	
	/**
	 * Time in frames between spawns.
	 * @param freq The time.
	 */
	public void setFrequency(int freq)
	{
		this.freq = freq;
		counter = 0;
	}
	
	/**
	 * Checks whether or not the unit is capable fo spawning.
	 * @return True it it can spawn.
	 */
	public boolean canSpit()
	{
		return !stop && spitCounter < limit;
	}
	
	/**
	 * Allow you to customize the maximum amount of objects to be spawned(default: unlimited).
	 * @param limit The limit.
	 */
	public void spitLimit(int limit)
	{
		this.limit = limit;
	}
	
	/**
	 * Returns the amount of units the {@code Trailer} has spawned(total number).
	 * @return The quantity.
	 */
	public int getSpitCounter()
	{
		return spitCounter;
	}
	
	/**
	 * Stops the {@code Trailer} from spawning objects(but not moving).
	 * @param stop True to stop it.
	 */
	public void stop(boolean stop)
	{
		this.stop = stop;
	}
}