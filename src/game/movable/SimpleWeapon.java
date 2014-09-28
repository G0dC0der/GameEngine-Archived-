package game.movable;

import java.awt.geom.Point2D;

import kuusisto.tinysound.Sound;
import game.core.Engine.Direction;
import game.core.EntityStuff;
import game.essentials.SoundBank;
import game.objects.Particle;

public class SimpleWeapon extends PathDrone
{
	@FunctionalInterface
	public interface CloneEvent
	{
		void cloned(Projectile clone);
	};
	
	private Direction projDir;
	private Projectile proj;
	private Particle fireAnim;
	private CloneEvent cloneEvent;
	private int reloadTime, reloadCounter;
	private float offsetX, offsetY;
	
	public SimpleWeapon(float x, float y, Projectile proj, Direction projDir, int reloadTime) 
	{
		super(x,y);
		this.proj = proj;
		this.projDir = projDir;
		this.reloadTime = reloadTime;
		sounds = new SoundBank(1); //Firing sound
		sounds.setEmitter(this);
	}
	
	public void spawnOffset(float offsetX, float offsetY)
	{
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	
	public void setFiringAnimation(Particle fireAnim)
	{
		this.fireAnim = fireAnim;
	}
	
	public void setCloneEvent(CloneEvent cloneEvent)
	{
		this.cloneEvent = cloneEvent;
	}
	
	public boolean reloading()
	{
		return --reloadCounter > 0;
	}
	
	public void setFiringSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}
	
	@Override
	public void moveEnemy() 
	{
		if(!reloading())
		{
			float startX = currX + offsetX;
			float startY = currY + offsetY;
			
			Projectile projClone = proj.getClone(startX, startY);
			projClone.setDisposable(true);
			projClone.scanningAllowed(false);
			Point2D.Float target = EntityStuff.getEdgePoint(startX, startY, projDir);
			projClone.setTarget(target.x, target.y);
			
			if(fireAnim != null)
				stage.add(fireAnim.getClone(startX, startY));
			
			if(cloneEvent != null)
				cloneEvent.cloned(projClone);
			
			stage.add(projClone);
			reloadCounter = reloadTime;
			
			sounds.playSound(0);
		}
	}
	
}