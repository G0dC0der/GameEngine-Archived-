package game.movable;

import game.core.Engine;
import game.core.EntityStuff;
import game.core.GameObject;
import game.essentials.SoundBank;
import game.objects.Particle;
import java.awt.geom.Point2D;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import kuusisto.tinysound.Sound;

/**
 * This unit fire laser at the closest seeable(customizable) target. Any target intersecting with the laser beam will have its {@code HitEvent} fired with the laser drone as argument.<br>
 * The laser travels until the {@code stopTile} is hit(default: solid).
 * @author Pojahn Moradi
 */
public class LaserDrone extends PathDrone
{
	/**
	 * This is the colors used when drawing the laser. The first index is the color that will appear at the edges of the laser,
	 * and the last color is the inner color. The more elements this one have, the wider laser.
	 */
	public Color[] laserColors, startupColors;
	protected float targetX, targetY;
	private int laserStartup, laserDuration, reload, sucounter, ducounter, reloadCounter;
	private byte stopTile;
	private boolean scan, firing;
	private Particle exp;
	private final GameObject[] targets;
	
	/**
	 * Creates a laser drone.
	 * @param x The starting X position.
	 * @param y The starting Y position.
	 * @param laserStartup The amount of frames before the laser comes out when a target has been detected.
	 * @param laserDuration The amount of frames the laser stays active(and deadly).
	 * @param reload The amount of frames to wait before scanning for a new target after this unit have finished its current firing mission.
	 * @param scan Set to true to fire at seeable targets or false to fire the laser at the closest target even if there is a wall between them.
	 * @param targets The units capable of being hit and targeted by this unit.
	 */
	public LaserDrone(float x, float y, int laserStartup, int laserDuration, int reload, boolean scan, GameObject... targets)
	{
		super(x, y);
		this.laserStartup = laserStartup;
		this.laserDuration = laserDuration;
		this.scan = scan;
		this.targets = targets;
		this.reload = reload;
		targetX = targetY = -1;
		sucounter = ducounter = reloadCounter = 0;
		sounds = new SoundBank(2);	//0 = startup, 1 = firing
		stopTile = Engine.SOLID;
		
		laserColors = new Color[4];
		int red = 255;
		for (int i = 0; i < laserColors.length; i++)
		{
			laserColors[i] = new Color(red,0,0,200);
			red -= 20;
		}
		
		startupColors = new Color[2];
		red = 255;
		for (int i = 0; i < startupColors.length; i++)
		{
			startupColors[i] = new Color(red,0,0,25);
			red -= 20;
		}
	}
	
	/**
	 * The sound to play when the laser is starting.
	 * @param startupSound The sound.
	 */
	public void setStartupSound(Sound startupSound)
	{
		sounds.setSound(0, startupSound);
	}
	
	/**
	 * The sound to play when the laser is active(and damaging).
	 * @param firingSound The sound.
	 */
	public void setFiringSound(Sound firingSound)
	{
		sounds.setSound(1, firingSound);
	}
	
	/**
	 * The animation to append at the lasers edge.
	 * @param exp The animation.
	 */
	public void setExplosion(Particle exp)
	{
		this.exp = exp;
	}
	
	/**
	 * Checks if this unit currently have a target.
	 * @return True if this {@code LaserDrone} have a target.
	 */
	public boolean haveTarget()
	{
		return targetX != -1;
	}
	
	/**
	 * The tile type to consider as a wall.
	 * @param stopTile The tile type.
	 */
	public void setStopTile(byte stopTile)
	{
		this.stopTile = stopTile;
	}
	
	@Override
	public void moveEnemy()
	{
		if (--reloadCounter > 0)
		{
			if(!scan)
				super.moveEnemy();
			return;
		}
		
		if(!haveTarget())
		{
			GameObject target = null;
			if(scan)
				target = EntityStuff.findClosestSeeable(this, targets);
			else
				target = EntityStuff.findClosest(this, targets);
			
			if(target != null)
			{
				int x1 = (int) (currX + width  / 2),
					y1 = (int) (currY + height / 2),
					x2 = (int) (target.currX + target.width  / 2),
					y2 = (int) (target.currY + target.height / 2);
				
				Point2D.Float wallp = EntityStuff.searchTile(x1,y1, x2,y2, stopTile);
				if(wallp == null)
					wallp = EntityStuff.findEdgePoint(x1, y1, x2, y2);
				
				targetX = wallp.x;
				targetY = wallp.y;
				sounds.allowSound(0);
				sounds.allowSound(1);
			}
			else
				super.moveEnemy();
		}
		if(haveTarget())
		{
			if(!scan)
				super.moveEnemy();
			
			if(!firing && ++sucounter % laserStartup == 0)
			{
				firing = true;
				
				if(exp != null)
					stage.add(exp.getClone(targetX - exp.width / 2, targetY - exp.height / 2));
			}
			if(firing)
			{
				for(GameObject go : targets)
					if(EntityStuff.checkLine((int)currX, (int) currY, (int)targetX, (int)targetY, go))
						go.runHitEvent(this);
				
				if(++ducounter % laserDuration == 0)
				{
					targetX = targetY = -1;
					reloadCounter = reload;
					firing = false;
				}
			}
		}
	}

	@Override
	public void drawSpecial(SpriteBatch g)	//TODO:
	{
//		int colorCounter = 0;
//		float x = currX + width / 2,
//			  y = currY + height / 2,
//			  lineWidth;
//		
//		if(!firing && haveTarget())
//		{
//			lineWidth = startupColors.length;
//			sounds.trySound(0, false);
//			
//			while(lineWidth > 0)
//			{
//				g.setColor(startupColors[colorCounter++]);
//				g.setLineWidth(lineWidth--);
//	
//				g.drawLine(x,y,targetX, targetY);
//			}
//		}
//		if(firing)
//		{
//			lineWidth = laserColors.length;
//			sounds.trySound(1, false);
//			
//			while(lineWidth > 0)
//			{
//				g.setColor(laserColors[colorCounter++]);
//				g.setLineWidth(lineWidth--);
//	
//				g.drawLine(x,y,targetX, targetY);
//			}
//		}
	}
}
