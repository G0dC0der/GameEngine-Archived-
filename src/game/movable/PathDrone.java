package game.movable;

import game.core.Enemy;
import game.core.Fundementals;
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
	private boolean rock, skip;
	private int dataCounter, stillCounter;
	private boolean playEvent;

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
	}

	@Override
	public PathDrone getClone(float x, float y)
	{
		PathDrone p = new PathDrone(x, y);
		copyData(p);
		
		if(cloneEvent != null)
			cloneEvent.cloned(p);
		
		return p;
	}
	
	protected void copyData(PathDrone dest)
	{
		super.copyData(dest);
		dest.pathData.addAll(pathData);
		dest.skip = skip;
		dest.rock = rock;
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
				
				currX = pd.targetX;
				currY = pd.targetY;
				
				if(playEvent && pd.event != null)
				{
					pd.event.eventHandling();
					playEvent = false;
				}
			}
			else
			{
				playEvent = true;
				stillCounter = 0;
				
				if(pd.jump)
					moveTo(pd.targetX, pd.targetY);
				else
					moveToward(pd.targetX, pd.targetY);
			}
		}
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
	    		moveTo(tx,ty);
	    	else if(!canNext && skip)
	    		dataCounter++;
	    	else if(!canNext && !skip)
	    		resetPrevs();
	    }
	    else
	    	moveTo(tx, ty);
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
	
	protected boolean reached(PathData pd)
	{
		return moveSpeed > Fundementals.distance(pd.targetX, pd.targetY, currX, currY);
	}

	@Override
	public void drawSpecial(SpriteBatch batch) {}
}
