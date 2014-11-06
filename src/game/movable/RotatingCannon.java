package game.movable;

import game.core.Engine.Direction;
import game.core.Fundementals;
import game.essentials.SoundBank;
import game.objects.Particle;
import com.badlogic.gdx.math.Vector2;
import kuusisto.tinysound.Sound;

/**
 * This entity was inspired by the rotating cannons found in the Super Mario Bros series. <br>
 * It starts with fire straight at four direction, rotates 45 degrees and then fires diagonally at four directions.
 * @author Pojahn Moradi
 */
public class RotatingCannon extends PathDrone
{
	private float rotationSpeed, targetRotation;
	private int reload, reloadCounter;
	private boolean disabled, diaognalFire, rotating, fireAll;
	private Projectile proj;
	private Particle firingAnim;
	
	/**
	 * Constructs a {@code RotatingCannon} with the standard properties set.
	 * @param x The x coordinate to start at.
	 * @param y The Y coordinate to start at.
	 * @param proj The projectile to fire. Be sure to set it to {@code disposable}.
	 */
	public RotatingCannon(float x, float y, Projectile proj) 
	{
		super(x, y);
		moveTo(x,y);
		this.proj = proj;
		rotationSpeed = 3;
		reload = 50;
		sounds = new SoundBank(1);
		sounds.setEmitter(this);
	}
	
	/**
	 * The amount of frames to wait when the rotation is done before firing.
	 * @param reload The time in frames.
	 */
	public void setReloadTime(int reload)
	{
		this.reload = reload;
	}
	
	/**
	 * The speed to rotate at directly after a shot.
	 * @param rotationSpeed The speed.
	 */
	public void setRotationSpeed(float rotationSpeed)
	{
		this.rotationSpeed = rotationSpeed;
	}
	
	/**
	 * The animation to spawn on the firing locations.
	 * @param firingAnim The animation.
	 */
	public void setFireAnimation(Particle firingAnim)
	{
		this.firingAnim = firingAnim;
	}
	
	/**
	 * The sound to play when firing.
	 * @param sound The sound.
	 */
	public void setFiringSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}
	
	/**
	 * Disables the machine from rotating and firing.
	 * @param disable True to disable.
	 */
	public void disable(boolean disable)
	{
		this.disabled = disable;
	}
	
	/**
	 * Instead of firing straight, rotating and then fire diagonally, this feature allow you to fire at all eight directions at the same time.
	 * @param fireAll
	 */
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
					Vector2 spawn;

					spawn = Fundementals.getRotatedPoint(loc.x + width, centerY(), centerX(), centerY(), 45);
					Projectile proj1 = proj.getClone(spawn.x - proj.halfWidth(), spawn.y - proj.halfHeight());
					proj1.setTarget(getTarget(Direction.SE));
					proj1.rotation = 45;
					if(firingAnim != null)
						stage.add(firingAnim.getClone(spawn.x - firingAnim.halfWidth(), spawn.y - firingAnim.halfHeight()));
					
					spawn = Fundementals.getRotatedPoint(loc.x + width, centerY(), centerX(), centerY(), 135);
					Projectile proj2 = proj.getClone(spawn.x - proj.halfWidth(), spawn.y - proj.halfHeight());
					proj2.setTarget(getTarget(Direction.SW));
					proj2.rotation = 135;
					if(firingAnim != null)
						stage.add(firingAnim.getClone(spawn.x - firingAnim.halfWidth(), spawn.y - firingAnim.halfHeight()));
					
					spawn = Fundementals.getRotatedPoint(loc.x + width, centerY(), centerX(), centerY(), 225);
					Projectile proj3 = proj.getClone(spawn.x - proj.halfWidth(), spawn.y - proj.halfHeight());
					proj3.setTarget(getTarget(Direction.NW));
					proj3.rotation = 225;
					if(firingAnim != null)
						stage.add(firingAnim.getClone(spawn.x - firingAnim.halfWidth(), spawn.y - firingAnim.halfHeight()));

					spawn = Fundementals.getRotatedPoint(loc.x + width, centerY(), centerX(), centerY(), 315);
					Projectile proj4 = proj.getClone(spawn.x - proj.halfWidth(), spawn.y - proj.halfHeight());
					proj4.setTarget(getTarget(Direction.NE));
					proj4.rotation = 315;
					if(firingAnim != null)
						stage.add(firingAnim.getClone(spawn.x - firingAnim.halfWidth(), spawn.y - firingAnim.halfHeight()));
					
					stage.add(proj1, proj2, proj3, proj4);
				}

				if(!diaognalFire || fireAll)
				{
					Projectile proj1 = proj.getClone(centerX() - (proj.width() / 2), loc.y - (proj.height() / 2));
					targetX = centerX() - (proj.width() / 2);
					targetY = 0;
					proj1.setTarget(targetX, targetY);
					proj1.rotation = 270;
					if(firingAnim != null)
						stage.add(firingAnim.getClone(centerX() - firingAnim.width() / 2, loc.y - firingAnim.height() / 2));
					
					Projectile proj2 = proj.getClone(loc.x + width() - (proj.width() / 2), centerY() - (proj.height() / 2));
					targetX = stage.size.width;
					targetY = centerY() - (proj.height() / 2);
					proj2.setTarget(targetX, targetY);
					proj2.rotation = 0;
					if(firingAnim != null)
						stage.add(firingAnim.getClone(loc.x + width() - (firingAnim.width() / 2), centerY() - firingAnim.height() / 2));
					
					Projectile proj3 = proj.getClone(centerX() - (proj.width() / 2), loc.y + height() - (proj.height() / 2));
					targetX = centerX() - (proj.width() / 2);
					targetY = stage.size.height;
					proj3.setTarget(targetX, targetY);
					proj3.rotation = 90;
					if(firingAnim != null)
						stage.add(firingAnim.getClone(centerX() - firingAnim.width() / 2, loc.y + height() - firingAnim.height() / 2));
					
					Projectile proj4 = proj.getClone(loc.x - (proj.width() / 2), centerY() - (proj.height() / 2));
					targetX = 0;
					targetY = centerY() - (proj.height() / 2);
					proj4.setTarget(targetX, targetY);
					proj4.rotation = 180;
					if(firingAnim != null)
						stage.add(firingAnim.getClone(loc.x - (firingAnim.width() / 2), centerY() - firingAnim.height() / 2));
					
					stage.add(proj1, proj2, proj3, proj4);
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
	
	private Vector2 getTarget(Direction dir)
	{
		float middleX = loc.x + width  / 2;
		float middleY = loc.y + height / 2;
		float x,y;
		
		switch (dir)
		{
			case NW:
				x = middleX - 1;
				y = middleY - 1;
				return Fundementals.findEdgePoint(middleX, middleY, x, y);
			
			case N:
				x = middleX;
				y = middleY - 1;
				return Fundementals.findEdgePoint(middleX, middleY, x, y);
			
			case NE:
				x = middleX + 1;
				y = middleY - 1;
				return Fundementals.findEdgePoint(middleX, middleY, x, y);
			
			case E:
				x = middleX + 1;
				y = middleY;
				return Fundementals.findEdgePoint(middleX, middleY, x, y);
			
			case SE:
				x = middleX + 1;
				y = middleY + 1;
				return Fundementals.findEdgePoint(middleX, middleY, x, y);
			
			case S:
				x = middleX;
				y = middleY + 1;
				return Fundementals.findEdgePoint(middleX, middleY, x, y);
			
			case SW:
				x = middleX - 1;
				y = middleY + 1;
				return Fundementals.findEdgePoint(middleX, middleY, x, y);
			
			case W:
				x = middleX - 1;
				y = middleY;
				return Fundementals.findEdgePoint(middleX, middleY, x, y);
		
			default:
				return null;
		}
	}
}
