package game.movable;

import game.core.Engine.Direction;
import game.core.EntityStuff;
import game.core.Stage;
import game.essentials.SoundBank;
import game.objects.Particle;
import java.awt.geom.Point2D;
import kuusisto.tinysound.Sound;

/**
 * 
 * @author Pojahn Moradi
 *
 */
public class GunMachine extends PathDrone	//TODO: RETEST
{
	private float rotationSpeed, targetRotation;
	private int reload, reloadCounter;
	private boolean disabled, diaognalFire, rotating, fireAll;
	private Projectile proj;
	private Particle firingAnim;
	private final Stage s = Stage.STAGE;
	
	public GunMachine(float x, float y, int reload, Projectile proj) 
	{
		super(x, y);
		moveTo(x,y);
		this.reload = reload;
		this.proj = proj;
		this.rotationSpeed = 3;
		sounds = new SoundBank(1);
	}
	
	public void setRotationSpeed(float rotationSpeed)
	{
		this.rotationSpeed = rotationSpeed;
	}
	
	public void setFireAnimation(Particle firingAnim)
	{
		this.firingAnim = firingAnim;
	}
	
	public void setFiringSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}
	
	public void disable(boolean disable)
	{
		this.disabled = disable;
	}
	
	public void fireAll(boolean fireAll)
	{
		this.fireAll = fireAll;
	}

	@Override
	public void moveEnemy() 
	{
		super.moveEnemy();
		
		if(!disabled && --reloadCounter < 0)
		{
			if(!rotating)
			{
				float targetX, targetY;
				
				if(diaognalFire || fireAll)
				{
					Point2D.Float spawn;
					float hw = proj.width() / 2;
					float hh = proj.height() / 2;

					spawn = EntityStuff.getRotatedPoint(width() / 2, 0, centerX(), centerY(), 315);
					Projectile proj1 = proj.getClone(spawn.x - hw, spawn.y - hh);
					proj1.setTarget(getTarget(Direction.NE));
					proj1.rotation = 315;
					if(firingAnim != null)
						s.add(firingAnim.getClone(spawn.x - firingAnim.width() / 2, spawn.y - firingAnim.height() / 2));
					
					spawn = EntityStuff.getRotatedPoint(0, height() / 2, centerX(), centerY(), 45);
					Projectile proj2 = proj.getClone(spawn.x - hw, spawn.y - hh);
					proj2.setTarget(getTarget(Direction.SE));
					proj2.rotation = 45;
					if(firingAnim != null)
						s.add(firingAnim.getClone(spawn.x - firingAnim.width() / 2, spawn.y - firingAnim.height() / 2));
					
					spawn = EntityStuff.getRotatedPoint(width() / 2, 0, centerX(), centerY(), 135);
					Projectile proj3 = proj.getClone(spawn.x - hw, spawn.y - hh);
					proj3.setTarget(getTarget(Direction.SW));
					proj3.rotation = 135;
					if(firingAnim != null)
						s.add(firingAnim.getClone(spawn.x - firingAnim.width() / 2, spawn.y - firingAnim.height() / 2));
					
					spawn = EntityStuff.getRotatedPoint(width() / 2, height(), centerX(), centerY(), 225);
					Projectile proj4 = proj.getClone(spawn.x - hw, spawn.y - hh);
					proj4.setTarget(getTarget(Direction.NW));
					proj4.rotation = 225;
					if(firingAnim != null)
						s.add(firingAnim.getClone(spawn.x - firingAnim.width() / 2, spawn.y - firingAnim.height() / 2));
					
					s.add(proj1, proj2, proj3, proj4);
				}

				if(!diaognalFire || fireAll)
				{
					Projectile proj1 = proj.getClone(centerX() - (proj.width() / 2), currY - (proj.height() / 2));
					targetX = centerX() - (proj.width() / 2);
					targetY = 0;
					proj1.setTarget(targetX, targetY);
					proj1.rotation = 270;
					if(firingAnim != null)
						s.add(firingAnim.getClone(centerX() - firingAnim.width() / 2, currY - firingAnim.height() / 2));
					
					Projectile proj2 = proj.getClone(currX + width() - (proj.width() / 2), centerY() - (proj.height() / 2));
					targetX = s.size.width;
					targetY = centerY() - (proj.height() / 2);
					proj2.setTarget(targetX, targetY);
					proj2.rotation = 0;
					if(firingAnim != null)
						s.add(firingAnim.getClone(currX + width() - (firingAnim.width() / 2), centerY() - firingAnim.height() / 2));
					
					Projectile proj3 = proj.getClone(centerX() - (proj.width() / 2), currY + height() - (proj.height() / 2));
					targetX = centerX() - (proj.width() / 2);
					targetY = s.size.height;
					proj3.setTarget(targetX, targetY);
					proj3.rotation = 90;
					if(firingAnim != null)
						s.add(firingAnim.getClone(centerX() - firingAnim.width() / 2, currY + height() - firingAnim.height() / 2));
					
					Projectile proj4 = proj.getClone(currX - (proj.width() / 2), centerY() - (proj.height() / 2));
					targetX = 0;
					targetY = centerY() - (proj.height() / 2);
					proj4.setTarget(targetX, targetY);
					proj4.rotation = 180;
					if(firingAnim != null)
						s.add(firingAnim.getClone(currX - (firingAnim.width() / 2), centerY() - firingAnim.height() / 2));
					
					s.add(proj1, proj2, proj3, proj4);
				}
				
				sounds.playSound(0);
				reloadCounter = reload;
				rotating = true;
				targetRotation = rotation + 45;
				
				if(rotation > 360)
				{
					rotation = 0;
					targetRotation = 45;
				}
			}
			else
			{
				rotation += rotationSpeed;
				if(rotation > targetRotation)
					rotation = targetRotation;
				
				if(rotation == targetRotation)
				{
					rotation = rotation >= 360 ? 0 : rotation;
						
					rotating = false;
					diaognalFire = !diaognalFire;
				}
			}
		}
	}
	
	private Point2D.Float getTarget(Direction dir)
	{
		float middleX = currX + width  / 2;
		float middleY = currY + height / 2;
		float x,y;
		
		switch (dir)
		{
			case NW:
				x = middleX - 1;
				y = middleY - 1;
				return EntityStuff.findEdgePoint(middleX, middleY, x, y);
			
			case N:
				x = middleX;
				y = middleY - 1;
				return EntityStuff.findEdgePoint(middleX, middleY, x, y);
			
			case NE:
				x = middleX + 1;
				y = middleY - 1;
				return EntityStuff.findEdgePoint(middleX, middleY, x, y);
			
			case E:
				x = middleX + 1;
				y = middleY;
				return EntityStuff.findEdgePoint(middleX, middleY, x, y);
			
			case SE:
				x = middleX + 1;
				y = middleY + 1;
				return EntityStuff.findEdgePoint(middleX, middleY, x, y);
			
			case S:
				x = middleX;
				y = middleY + 1;
				return EntityStuff.findEdgePoint(middleX, middleY, x, y);
			
			case SW:
				x = middleX - 1;
				y = middleY + 1;
				return EntityStuff.findEdgePoint(middleX, middleY, x, y);
			
			case W:
				x = middleX - 1;
				y = middleY;
				return EntityStuff.findEdgePoint(middleX, middleY, x, y);
		
			default:
				return null;
		}
	}
}
