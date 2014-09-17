package game.movable;

import game.core.Engine.Direction;
import game.core.MovableObject;
import game.core.Stage;
import game.essentials.SoundBank;
import game.objects.Particle;
import kuusisto.tinysound.Sound;

/**
 * The {@code Goomba} class can other than hurting a subject also be killed by it(by jumping on it). 
 * @author Pojahn Moradi
 */
public class Goomba extends PathDrone 
{
	private MovableObject[] enemies;
	private Particle deathImg;
	private int[] hitSubjects;
	private int hitFrames;
	private Direction dir;
	
	/**
	 * Constructs a {@code Goomba}.
	 * @param x The x coordinate to start at.
	 * @param y The y coordinate to start at.
	 * @param direction A constant from {@code game.core.Engine} that can be either N, E, W or S and represent which direction of {@code Goombas} image is the hurtbox. For example, if its set to N, you can kill the {@code Goomba} by jumping on top of it.
	 * @param enemies The units capable of getting hit and killing the {@code Goomba}.
	 */
	public Goomba(float x, float y, Direction direction, MovableObject... enemies)
	{
		super(x, y);
		
		if(isValidDirection(direction))
			this.dir = direction;
		else
			throw new IllegalArgumentException("Illegal direction: " + direction);
		
		this.enemies = enemies;
		hitSubjects = new int[enemies.length];
		hitFrames = 100;
		sounds = new SoundBank(2);//1 = Walking Sound, 2 = Hit Sound
	}
	
	@Override
	public void moveEnemy()
	{
		super.moveEnemy();

		sounds.playSound(0);
		
		for(int i = 0; i < enemies.length; i++)
		{
			MovableObject mo = enemies[i];
			hitSubjects[i]--;
			
			if(collidesWith(mo))
			{
				if(hitSubjects[i] <= 0 && isAttacking(mo))
				{
					Stage.STAGE.discard(this);
					if(deathImg != null)
						Stage.STAGE.add(deathImg.getClone(currX, currX));
					
					sounds.stop(0);
					sounds.playSound(1);		
				}
				else
				{
					mo.runHitEvent(this);
					if(hitFrames > 0)
						hitSubjects[i] = hitFrames;
				}
			}
		}
	}
	
	/**
	 * The particle to add at the current position of the {@code Goomba} upon death.
	 * @param deathImg The particle.
	 */
	public void setDeathParticle(Particle deathImg)
	{
		this.deathImg = deathImg;
	}
	
	/**
	 * The sound to play when the {@code Goomba} is moving.
	 * @param sound The sound to play.
	 * @param delay The amount of frames between each play.
	 */
	public void setMovingSound(Sound sound, int delay)
	{
		sounds.setSound(0, sound);
		sounds.setDelay(0, delay);
	}
	
	/**
	 * The sound to play upon death.
	 * @param sound The sound.
	 */
	public void setDeathSound(Sound sound)
	{
		sounds.setSound(1, sound);
	}
	
	/**
	 * If a subject is hit by the {@code Goomba}, this value specify the amount of frames to disable collision detection between the two.
	 * @param frames Time in frames.
	 */
	public void subjectHitFrames(int frames)
	{
		hitFrames = frames;
	}
	
	boolean isValidDirection(Direction dir)
	{
		return dir == Direction.N || dir == Direction.E || dir == Direction.S || dir == Direction.W;
	}
	
	boolean isAttacking(MovableObject mo)
	{
		Direction f = mo.facing;
		
		switch(dir)
		{
			case N:
				return f == Direction.S || f == Direction.SW || f == Direction.SE;
			case E:
				return f == Direction.W || f == Direction.SW || f == Direction.NW;
			case S:
				return f == Direction.N || f == Direction.NW || f == Direction.NE;
			case W:
				return f == Direction.E || f == Direction.SE || f == Direction.NE;
			default:
				throw new RuntimeException();
		}
	}
}
