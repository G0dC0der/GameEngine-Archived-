package game.movable;

import game.core.Enemy;
import game.core.Fundementals;
import game.core.GameObject;
import game.essentials.Image2D;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/*
 * To support calculation of chains:
 * 
 * 	for (float c = 0; c <= distance; c += diameter)
 *	{
 *    	p = lerp(point1, point2, c / distance);
 *   	// draw...	
 *	}
 */

/**
 * The {@code Chain} class simulates a chain, which means rendering a bunch of images between two end points.
 * @author Pojahn Moradi
 */
public class Chain extends Enemy
{
	private Vector2 pt1, pt2;
	private GameObject src1, src2;
	private int links;
	private boolean rotate, centerize1, centerize2, linkOnEndpoint;
	
	/**
	 * Creates a chain between two unspecified end point with the given amount of links.
	 * @param links The amount of links/chains.
	 */
	public Chain(int links)
	{
		this.links = links;
		linkOnEndpoint = true;
	}
	
	/**
	 * The amount of links/chains to use.
	 * @param links The value.
	 */
	public void setLinks(int links)
	{
		this.links = links;
	}
	
	/**
	 * Whether or not a chain should always be stuck on each end points.
	 * @param linkOnEndpoint False to disable.
	 */
	public void linkOnEndpoint(boolean linkOnEndpoint)
	{
		this.linkOnEndpoint = linkOnEndpoint;
	}
	
	/**
	 * Whether or not to rotate the chains.
	 * @param rotate True to rotate.
	 */
	public void rotateLinks(boolean rotate)
	{
		this.rotate = rotate;
	}
	
	/**
	 * The first end point.
	 * @param src1 The {@code GameObject} to use as end point.
	 * @param centerize True if the chain should be centerized on the source object.
	 */
	public void endPoint1(GameObject src1, boolean centerize)
	{
		this.src1 = src1;
		this.centerize1 = centerize;
	}
	
	/**
	 * The second end point.
	 * @param src2 The {@code GameObject} to use as end point.
	 * @param centerize True if the chain should be centerized on the source object.
	 */
	public void endPoint2(GameObject src2, boolean centerize)
	{
		this.src2 = src2;
		this.centerize2 = centerize;
	}
	
	/**
	 * The first end point.
	 * @param pt1 The vector to use as end point.
	 */
	public void endPoint1(Vector2 pt1)
	{
		this.pt1 = pt1;
	}
	
	/**
	 * The second end point.
	 * @param pt1 The vector to use as end point.
	 */
	public void endPoint2(Vector2 pt2)
	{
		this.pt2 = pt2;
	}

	@Override
	public final void drawSpecial(SpriteBatch batch) 
	{
		Vector2 endPoint1 = src1 == null ? pt1 : (centerize1 ? new Vector2(src1.centerX() - halfWidth(), src1.centerY() - halfHeight()) : src1.loc);
		Vector2 endPoint2 = src1 == null ? pt2 : (centerize2 ? new Vector2(src2.centerX() - halfWidth(), src2.centerY() - halfHeight()) : src2.loc);
		float rotation = (rotate) ? (float)Fundementals.getAngle(endPoint1, endPoint2) : 0f;
		int start = (linkOnEndpoint) ? 0 : 1;
		int end   = (linkOnEndpoint) ? links : links + 2;
		int cond  = (linkOnEndpoint) ? links : end - 1;
		
		for(int i = start; i < cond; i++)
		{
			Vector2 linkPos = new Vector2(endPoint1).lerp(endPoint2, (float)i/(float)(end - 1));
			batch.draw(super.getFrame(), linkPos.x, linkPos.y, centerX(), centerY(), width(), height(), 1, 1, rotation);
		}
	}
	
	@Override
	public Image2D getFrame() 
	{
		return null;
	}

	@Override
	public void moveEnemy() {}
}