package game.movable;

import game.core.Enemy;
import game.core.Engine;
import game.core.Fundementals;
import java.util.LinkedList;
import com.badlogic.gdx.math.Vector2;

/**
 * The {@code Shuttle} moves towards a set of given waypoints in the order that they were added, like the {@code PathDrone}.<br>
 * Although they sound similar, there is one major difference. The {@code Shuttle} use acceleration for a smoother movement. <br>
 * Thus, the accuracy not as high as the {@code PathDrone}. The higher {@code drag} and {@code thrust} you have, the less accuracy. This is required to maintain the smoothness of the movement.
 * @author Pojahn Moradi
 */
public class Shuttle extends Enemy
{
	private static class Waypoint
	{
		Vector2 target;
		Event event;

		Waypoint(float x, float y, Event event)
		{
			this.target = new Vector2(x,y);
			this.event = event;
		}
	}
	
	public float thrust, drag, delta, vx, vy;
	private int counter;
	private LinkedList<Waypoint> waypoints;
	
	/**
	 * Creates a {@code Shuttle} at the given point with {@code thrust} set to 500, {@code drag} 0.5 and {@code delta} to 1/60.
	 * @param x The x coordinate to start at.
	 * @param y The y coordinate to start at.
	 */
	public Shuttle(float x, float y)
	{
		moveTo(x,y);
		this.waypoints = new LinkedList<>();
		
		thrust = 500f;
		drag = .5f;
		delta = Engine.DELTA;
	}
	
	/**
	 * Appends the current position as a waypoint.
	 */
	public void appendPath()
	{
		appendPath(currX,currY);
	}
	
	/**
	 * Appends a waypoint.
	 * @param x The x coordinate of the waypoint.
	 * @param y The y coordinate of the waypoint.
	 */
	public void appendPath(float x, float y)
	{
		appendPath(x,y,null);
	}
	
	/**
	 * Appends a waypoint with an {@code Event} to execute once this waypoint is reached.
	 * @param x The x coordinate of the waypoint.
	 * @param y The y coordinate of the waypoint.
	 * @param reachEvent The {@code Event} to execute once the waypoint is reached.
	 */
	public void appendPath(float x, float y, Event reachEvent)
	{
		waypoints.add(new Waypoint(x,y, reachEvent));
	}
	
	/**
	 * Clear all waypoints.
	 */
	public void clearWaypoints()
	{
		waypoints.clear();
	}
	
	/**
	 * Skips the current waypoint and start moving to the next one.
	 */
	public void nextWaypoint()
	{
		counter = ++counter % waypoints.size();
	}
	
	/**
	 * Resets to the first waypoints.
	 */
	public void firstWaypoint()
	{
		counter = 0;
	}
	
	/**
	 * Skips all waypoints to the last one.
	 */
	public void lastWaypoint()
	{
		counter = waypoints.size() - 1;
	}
	
	@Override
	@Deprecated
	public void setMoveSpeed(float moveSpeed) 
	{
		throw new UnsupportedOperationException("The Shuttle class use maxX, maxY, accX and accY to control the speed.");
	}
	
	@Override
	public void moveEnemy() 
	{
		if(canMove && !waypoints.isEmpty())
		{
			Waypoint wp = waypoints.get(counter);
			
			if(Fundementals.distance(currX, currY, wp.target.x, wp.target.y) < thrust * drag / 2)
			{
				counter = ++counter % waypoints.size();
				if(wp.event != null)
					wp.event.eventHandling();
			}
			else
			{
				Vector2 norP = Fundementals.normalize(currX, currY, wp.target.x, wp.target.y);
				
				float accelx = thrust * -norP.x - drag * vx;
				float accely = thrust * -norP.y - drag * vy;
			 
				vx += delta * accelx;
				vy += delta * accely;
				
				currX += delta * vx;
				currY += delta * vy;
			}
		}
	}
}