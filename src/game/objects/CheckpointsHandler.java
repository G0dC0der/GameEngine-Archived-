package game.objects;

import game.core.Fundementals;
import game.core.GameObject;
import game.core.GameObject.Event;
import java.util.ArrayList;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * This class offer an optional way of handling checkpoints for your stage. <br>
 * The coder can append rectangular areas that represent the field a checkpoint is captured.<br>
 * The latest appended checkpoint have the most priority. Thus, if the player dies with all checkpoints is captured, the player will respawn at the latest appended checkpoint.
 * @author Pojahn Moradi
 */
public class CheckpointsHandler implements Event
{
	private static class Checkpoint
	{
		float startX, startY, x, y, width, height;
		boolean taken;

		Checkpoint(float startX, float startY, float x, float y, float width, float height) 
		{
			super();
			this.startX = startX;
			this.startY = startY;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}
	
	private ArrayList<Checkpoint> checkpoints;
	private GameObject users[];
	private Event reachEvent;
	
	/**
	 * Constructs an {@code CheckpointsHandler}.
	 */
	public CheckpointsHandler()
	{
		checkpoints = new ArrayList<>();
	}
	
	/**
	 * The {@code GameObjects} capable of capturing the checkpoints. Should be called once every death/retry since references often change.
	 * @param users The entities capable of taking the checkpoints.
	 */
	public void setUsers(GameObject... users)
	{
		this.users = users;
	}
	
	/**
	 * Appends a checkpoint.
	 * @param startPos The coordinate to respawn at.
	 * @param area The area you have to intersect with to capture the checkpoint.
	 */
	public void appendCheckpoint(Vector2 startPos, Rectangle area)
	{
		appendCheckpoint(startPos.x, startPos.y, area.x, area.y, area.width, area.height);
	}
	
	/**
	 * Appends a checkpoint.
	 * @param startX The X coordinate to respawn at.
	 * @param startY The Y coordinate to respawn at.
	 * @param area The area you have to intersect with to capture the checkpoint.
	 */
	public void appendCheckpoint(float startX, float startY, Rectangle area)
	{
		appendCheckpoint(startX, startY, area.x, area.y, area.width, area.height);
	}
	
	/**
	 * Appends a checkpoint.
	 * @param startPos The coordinate to respawn at.
	 * @param x The X coordinate of the checkpoint field.
	 * @param y The y coordinate of the checkpoint field.
	 * @param width The width of the checkpoint field.
	 * @param height The height of the checkpoint field.
	 */
	public void appendCheckpoint(Vector2 startPos, float x, float y, float width, float height)
	{
		appendCheckpoint(startPos.x, startPos.y, x,y,width,height);
	}
	
	/**
	 * Appends a checkpoint.
	 * @param startX The X coordinate to respawn at.
	 * @param startY The Y coordinate to respawn at.
	 * @param x The X coordinate of the checkpoint field.
	 * @param y The y coordinate of the checkpoint field.
	 * @param width The width of the checkpoint field.
	 * @param height The height of the checkpoint field.
	 */
	public void appendCheckpoint(float startX, float startY, float x, float y, float width, float height)
	{
		checkpoints.add(new Checkpoint(startX,startY,x,y,width,height));
	}
	
	/**
	 * Marks all checkpoints as uncaptured.
	 */
	public void reset()
	{
		for(Checkpoint cp : checkpoints)
			cp.taken = false;
	}
	
	/**
	 * Returns the current respawn coordinate, or null if there are no checkpoint captured.
	 * @return The respawn coordinate.
	 */
	public Vector2 getLastestCheckpoint()
	{
		for(int i = checkpoints.size() - 1; i >= 0; i--)
		{
			Checkpoint cp = checkpoints.get(i);
			
			if(cp.taken)
				return new Vector2(cp.startX, cp.startY);
		}
		
		return null;
	}
	
	/**
	 * Check if the given checkpoint is captured.
	 * @param cpIndex The index of which they where added.
	 * @return True if it was reached.
	 */
	public boolean reached(int cpIndex)
	{
		return checkpoints.get(cpIndex).taken;
	}
	
	/**
	 * The {@code Event} to execute once a checkpoint is reached.
	 * @param reachEvent The event.
	 */
	public void setReachEvent(Event reachEvent)
	{
		this.reachEvent = reachEvent;
	}
	
	@Override
	public void eventHandling() 
	{
		Outer:
		for(Checkpoint cp : checkpoints)
		{
			if(!cp.taken)
			{
				for(GameObject user : users)
				{
					if(Fundementals.rectangleVsRectangle(user.currX, user.currY, user.width, user.height, cp.x, cp.y, cp.width, cp.height))
					{
						cp.taken = true;
						reachEvent.eventHandling();
						continue Outer;
					}
				}
			}
		}
	}
}