package game.objects;

import game.core.EntityStuff;
import game.core.GameObject;
import game.core.Stage;
import game.movable.Projectile;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * A {@code Shrapnel} is a special type of {@code Particle} that spawns a bunch of projectiles(splits), flying in all directions.
 * @author Pojahn Moradi 
 *
 */
public class Shrapnel extends Particle
{
	private enum ExplosionType { SPLIT , RANDOM }
	
	private Projectile split;
	private ExplosionType expType;
	private boolean once;
	private int min, max;
	private Random r = new Random();
	
	/**
	 * Constructs a {@code Shrapnel}.
	 * @param x The X coordinate to start at. This value is often obsolete due to the usage of {@code getClone}.
	 * @param y The Y coordinate to start at. This value is often obsolete due to the usage of {@code getClone}.
	 * @param shrapnel The projectile that will be spawned upon impact of the particle.
	 * @param victims The {@code GameObjects} capable of being hit by this <i>particle</i>(Note: this is <b>not</b> the splits).
	 */
	public Shrapnel(float x, float y, Projectile shrapnel, GameObject... victims) 
	{
		super(x, y, victims);
		this.split = shrapnel;
		expType = ExplosionType.SPLIT;
		min = 5;
		max = 10;
	}

	
	@Override
	public void eventHandling()
	{
		if(!once)
		{
			once = true;
			Stage s = Stage.STAGE;
			float middleX = currX + width  / 2,
				  middleY = currY + height / 2;
			
			if(expType == ExplosionType.SPLIT)
			{
				Point2D.Float[] edgePoints = getEightDirection();
				
				for(int i = 0; i < edgePoints.length; i++)
				{
					Projectile proj = split.getClone(middleX, middleY);
					proj.setDisposable(true);
					proj.setTarget(edgePoints[i].x, edgePoints[i].y);
					s.add(proj);
				}
			}
			else
			{
				int splits = r.nextInt(max - min) + min,
					width  = Stage.STAGE.width,
					height = Stage.STAGE.height;
				
				for(int i = 0; i < splits; i++)
				{
					Point2D.Float edgePoints = EntityStuff.findEdgePoint(middleX, middleY, r.nextInt(width), r.nextInt(height));
					Projectile proj = split.getClone(currX, currY);
					proj.setDisposable(true);
					proj.setTarget(edgePoints.x, edgePoints.y);
					s.add(proj);
				}
			}
		}
		
		super.eventHandling();
	}
	
	/**
	 * Allows you to set the projectile that will be spawned upon the impact of the particle.
	 * @param shrapnel The split.
	 */
	public void setShrapnel(Projectile shrapnel)
	{
		this.split = shrapnel;
	}
	
	/**
	 * Causes random amounts of splits to be launched at random locations.<br>
	 * <b>Warning</b>: This feature is deprecated because the usage of random functions, meaning replays will be played different each time.
	 * @param min The minimum amount of splits to spawn.
	 * @param max The maximum amount of splits to spawn.
	 */
	@Deprecated
	public void useRandomExplosion(int min, int max)
	{
		this.min = min;
		this.max = max;
		expType = ExplosionType.RANDOM;
	}
	
	/**
	 * Causes the splits to be launched at N, NE, E, SE, S, SW, W and NW(meaning 8 sharpnels will be spawned upon the particle impact).<br>
	 * This is used by default.
	 */
	public void useSplitExplosion()
	{
		expType = ExplosionType.SPLIT;
	}
	
	Point2D.Float[] getEightDirection()
	{
		float middleX = currX + width  / 2,
			  middleY = currY + height / 2,
			  x,y;
		
		//NW Point
		x = middleX - 1;
		y = middleY - 1;
		Point2D.Float p1 = EntityStuff.findEdgePoint(middleX, middleY, x, y);
		
		//N Point
		x = middleX;
		y = middleY - 1;
		Point2D.Float p2 = EntityStuff.findEdgePoint(middleX, middleY, x, y);
		
		//NE Point
		x = middleX + 1;
		y = middleY - 1;
		Point2D.Float p3 = EntityStuff.findEdgePoint(middleX, middleY, x, y);
		
		//E Point
		x = middleX + 1;
		y = middleY;
		Point2D.Float p4 = EntityStuff.findEdgePoint(middleX, middleY, x, y);
		
		//SE Point
		x = middleX + 1;
		y = middleY + 1;
		Point2D.Float p5 = EntityStuff.findEdgePoint(middleX, middleY, x, y);
		
		//S Point
		x = middleX;
		y = middleY + 1;
		Point2D.Float p6 = EntityStuff.findEdgePoint(middleX, middleY, x, y);
		
		//SW Point
		x = middleX - 1;
		y = middleY + 1;
		Point2D.Float p7 = EntityStuff.findEdgePoint(middleX, middleY, x, y);
		
		//W Point
		x = middleX - 1;
		y = middleY;
		Point2D.Float p8 = EntityStuff.findEdgePoint(middleX, middleY, x, y);
		
		return new Point2D.Float[]{p1,p2,p3,p4,p5,p6,p7,p8};
	}
	
	public Shrapnel getClone(float x, float y)
	{
		Shrapnel s = new Shrapnel(x, y, split, victims);
		copyData(s);
		
		return s;
	}
	
	protected void copyData(Shrapnel dest)
	{
		super.copyData(dest);
		dest.min = min;
		dest.max = max;
		dest.expType = expType;
	}
}
