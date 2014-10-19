package stages.diamondcave;

import game.core.Enemy;
import game.core.GameObject;
import game.essentials.Animation;
import game.essentials.Image2D;
import kuusisto.tinysound.Sound;

class Bear extends Enemy
{
	private Animation<Image2D> attackImage, orgImage;
	private GameObject targets[], scanBox;
	private Sound attack;
	private int recovery;
	private boolean attacking;
	
	Bear(float x, float y, GameObject... targets)
	{
		moveTo(x,y);
		this.targets = targets;
		
		scanBox = new GameObject();
		scanBox.moveTo(currX - 100, currY + 50);
		scanBox.width = 100;
		scanBox.height = 20;
	}
	
	public void setAttackSound(Sound attack)
	{
		this.attack = attack;
	}

	public void setAttackImage(Animation<Image2D> attackImage)
	{
		this.attackImage = attackImage;
	}
	
	@Override
	public void setImage(Animation<Image2D> obj) 
	{
		super.setImage(obj);
		orgImage = image;
	}
	
	@Override
	public void moveEnemy() 
	{
		if(recovery-- < 0)
		{
			if(!attacking && scanBox.collidesWithMultiple(targets) != null)
			{
				attacking = true;
				image = attackImage;
				currX -= 15;
				attack.play();
//				rotation = -7.5f;
			}
			
			if(attacking && attackImage.hasEnded())
			{
				attackImage.reset();
				image = orgImage;
				attacking = false;
				recovery = 120;
				rotation = 0;
				currX += 15;
			}
		}
		
		GameObject subject = collidesWithMultiple(targets);
		if(subject != null)
			subject.runHitEvent(this);
	}
}
