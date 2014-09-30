package game.movable;

import static game.core.Engine.*;
import game.core.Enemy;
import game.core.EntityStuff;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Most enemies inherit this class rather than {@code Enemy} as it offer a common used functionality: waypoint pathing.<br>
 * The {@code PathDrone} moves to the given waypoints and return to the first waypoint when the final one has been reached.<br>
 * Events and other functionality can be added to the waypoints to customize the behavior of the drone.
 * @author Pojahn Moradi
 */
public class PathDrone extends Enemy
{
	/**
	 * Instances of this class are passed to a {@code PathDrone} object which it will use to navigate.
	 * @author Pojahn Moradi
	 *
	 */
	public static class PathData implements java.io.Serializable
	{
		private static final long serialVersionUID = 8830401729501579357L;
		
		/**
		 * The coordinate to move to.
		 */
		public final float targetX, targetY;
		/**
		 * The amount of frames to stay at the target when reached.
		 */
		public int frames;
		/**
		 * Whether or not to jump at the target(i e moving there instantly).
		 */
		public boolean jump;
		/**
		 * The event to fire when the target has been reached.
		 */
		public Event event;
		
		public PathData(float targetX, float targetY, int frames, boolean jump, Event event)
		{
			this.targetX = targetX;
			this.targetY = targetY;
			this.frames = frames;
			this.jump = jump;
			this.event = event;
		}
		
		public PathData(float targetX, float targetY)
		{
			this(targetX,targetY,0,false,null);
		}
		
		@Override
		public String toString()
		{
			return new StringBuilder(15).append(targetX).append(" ").append(targetY).append(" ").append(frames).append(" ").append(jump).toString();
		}
	}
	
	private LinkedList<PathData> pathData;
	private boolean rock, skip, acc;
	private float v1,v2,v3, mass, gravity, damping, vy;
	private int dataCounter, stillCounter;
	private boolean playEvent, useGravity;

	/**
	 * Constructs a {@code PathDrone} with no waypoints.
	 * @param x The starting X position.
	 * @param y The starting Y position.
	 */
	public PathDrone(float x, float y)
	{
		currX = x;
		currY = y;
		pathData = new LinkedList<>();
		dataCounter = stillCounter = 0;
		mass = 1.0f;
		gravity = -500;
		damping = 0.0001f;
	}

	@Override
	public PathDrone getClone(float x, float y)
	{
		PathDrone p = new PathDrone(x, y);
		copyData(p);
		
		return p;
	}
	
	protected void copyData(PathDrone dest)
	{
		super.copyData(dest);
		dest.pathData.addAll(pathData);
		dest.skip = skip;
		dest.rock = rock;
		dest.acc = acc;
		dest.v1 = v1;
		dest.v2 = v2;
		dest.v3 = v3;
		dest.useGravity = useGravity;
		dest.mass = mass;
		dest.gravity = gravity;
		dest.damping = damping;
	}
	
	/**
	 * Appends a path to the waypoint list.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 * @param frames The amount of frames to stay at the target.
	 * @param jump Whether or not to jump to the target.
	 * @param event The event to execute when the target has been reached.
	 */
	public void appendPath(float x, float y, int frames, boolean jump, Event event)
	{
		pathData.add(new PathData(x, y, frames, jump, event));
	}
	
	/**
	 * Appends a path to the waypoint list.
	 * @param pd The waypoint.
	 */
	public void appendPath(PathData pd)
	{
		pathData.add(pd);
	}
	
	/**
	 * Appends an array of paths to the waypoint list.
	 * @param list A list of waypoints.
	 */
	public void appendPath(PathData[] list)
	{
		for(PathData pd : list)
			if(pd != null)
				pathData.add(pd);
	}
	
	/**
	 * Appends a path to the waypoint list.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 */
	public void appendPath(float x, float y)
	{
		appendPath(x,y,0,false,null);
	}
	
	/**
	 * Appends the current position to the waypoint list.
	 */
	public void appendPath()
	{
		pathData.add(new PathData(currX, currY, 0, false, null));
	}
	
	/**
	 * Creates a clone of the current waypoint list, reverses the order and appends it to the list.
	 */
	public void appendReversed()
	{
		int size = pathData.size();
		PathData[] reversed = new PathData[size];
		for(int i = 1; i < size; i++)
			reversed[i] = pathData.get(size - i - 1);
		
		appendPath(reversed);
	}
	
	/**
	 * Clears all the waypoints from this unit.
	 */
	public void clearData()
	{
		pathData.clear();
		rollback();
	}
	
	/**
	 * Sets the current waypoint to the first one(reseting).
	 */
	public void rollback()
	{
		dataCounter = stillCounter = 0;
	}
	
	/**
	 * Whether or not to use gravity. If used, the Y-coordinate in the waypoint list will be discarded.
	 * <b>This feature is buggy and will be fixed later on.</b>
	 * @param useGravity
	 */
	public void enableGravity(boolean useGravity)
	{
		this.useGravity = useGravity;
	}
	
	@Override
	public void moveEnemy()
	{
		if(!pathData.isEmpty() && moveSpeed > 0 && canMove)
		{
			if(dataCounter >= pathData.size())
				dataCounter = 0;
			
			PathData pd = pathData.get(dataCounter);
			
			if(reached(pd))
			{
				if(++stillCounter > pd.frames)
					dataCounter++;
				resetPrevs();
				if(playEvent && pd.event != null)
				{
					pd.event.eventHandling();
					playEvent = false;
				}
				drag();
				return;
			}
			
			playEvent = true;
			stillCounter = 0;
			
			if(pd.jump)
				moveTo(pd.targetX, pd.targetY);
			else if(!acc)
				moveToward(pd.targetX, pd.targetY, moveSpeed);
			else
				specialMoveToward(pd.targetX, pd.targetY, v1,v2,v3);
			
			drag();
		}
		else
			drag();
	}
	
	@Override
	public void moveToward(float targetX, float targetY, float steps)
	{
		if (!canMove)
			return;
		
	    float fX = targetX - currX;
	    float fY = targetY - currY;
	    double dist = Math.sqrt( fX*fX + fY*fY );
	    double step = steps / dist;
	    
	    float tx = (float) (currX + fX * step);
	    float ty = (float) (currY + fY * step);
	    
	    if(rock)
	    {
	    	boolean canNext = canGoTo(tx, ty) && !isOverlapping(tx, ty);	    	
	    	if(canNext)
	    	{
	    		if(!useGravity)
	    			moveTo(tx,ty);
	    		else
	    			currX = tx;
	    	}
	    	else if(!canNext && skip)
	    		dataCounter++;
	    	else if(!canNext && !skip)
	    		resetPrevs();
	    }
	    else
	    {
	    	if(!useGravity)
	    		moveTo(tx, ty);
	    	else
	    		currX = tx;
	    }
	}
	
	@Override
	public void specialMoveToward(float targetX, float targetY, float spec1, float spec2, float spec3)
	{
		if (!canMove)
			return;
		
		float dx = targetX - currX;
		float dy = targetY - currY;
		
		float accX = spec1 * dx - spec2 * moveSpeed;
		float accY = spec1 * dy - spec2 * moveSpeed;
		
		float velocityX = moveSpeed + spec3 * accX;
		float velocityY = moveSpeed + spec3 * accY;
		
		float tx = currX + spec3 * velocityX;
		float ty = currY + spec3 * velocityY;
		
		if(rock)
		{	
	    	boolean canNext = canGoTo(tx, ty) && !isOverlapping(tx, ty);	    	
	    	if(canNext)
	    	{
	    		if(!useGravity)
	    			moveTo(tx,ty);
	    		else
	    			currX = tx;
	    	}
	    	else if(!canNext && skip)
	    		dataCounter++;
	    	else if(!canNext && !skip)
	    		resetPrevs();
		}
		else
	    {
	    	if(!useGravity)
	    		moveTo(tx, ty);
	    	else
	    		currX = tx;
	    }
	}
	
	/**
	 * A PathDrone that is rocky will respect walls and solid objects.
	 * @param rock True if this PathDrone should be "rocky".
	 * @param skip This flag determine how a rocky drone should behave when blocked(by a wall or solid object). Setting it to true will cause the drone to try the next waypoint and false halts it until the path is clear.
	 */
	public void setRock(boolean rock, boolean skip)
	{
		this.rock = rock;
		this.skip = skip;
	}
	
	/**
	 * This function allow you to specify acceleration settings.<br>
	 * Note that that the acceleration algorithm is a special one, where it starts at max speed and slows down when getting closer to its target.
	 * @param acc Whether or not to accelerate.
	 * @param v1 Setting 1
	 * @param v2 Setting 2
	 * @param v3 The delta.
	 */
	public void setAccelerate(boolean acc, float v1, float v2, float v3)
	{
		this.acc = acc;
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
	}
	
	protected boolean reached(PathData pd)
	{
		if(!useGravity)
			return moveSpeed > EntityStuff.distance(pd.targetX, pd.targetY, currX, currY);
		else
			return moveSpeed > EntityStuff.distance(pd.targetX, 1, currX, 1);
	}
	
	protected void drag()
	{
		if(useGravity)
		{
		    vy *= 1.0 - (damping * DELTA);
		    float force = mass * gravity;
		    vy += (force / mass) * DELTA;
		    float nextY = currY - vy * DELTA;
		    
		    if(canGoTo(currX,nextY))
		    	currY = nextY;
		    else
		    	vy = 0;
		}
	}

	@Override
	public void drawSpecial(SpriteBatch batch) {}
}
