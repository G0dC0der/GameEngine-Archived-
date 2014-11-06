package stages.spirit;

import game.core.Fundementals;
import game.core.GameObject;
import game.essentials.Factory;
import game.movable.PathDrone;
import com.badlogic.gdx.graphics.Color;

class Ghost extends PathDrone
{
	private GameObject follower, reward;
	private int distanceCheck = 150;
	private boolean firstEncounter, used, stop;
	public boolean reachedDest;
	
	public Ghost(float x, float y, GameObject follower, GameObject reward) 
	{
		super(x, y);
		this.follower = follower;
		this.reward = reward;
		firstEncounter = true;
		moveSpeed = 0;
	}

	@Override
	public void moveEnemy()
	{
		super.moveEnemy();
		
		if(!stop && Fundementals.distance(this, follower) < distanceCheck)
		{
			if(firstEncounter)
			{
				firstEncounter = false;
				stage.add(Factory.printText("Stay close to the top and I will give you a present.", new Color(255,0,255,255), stage.game.fpsFont, 200, this, -150,0, new Event()
				{
					@Override
					public void eventHandling() 
					{
						distanceCheck = 400;
						Ghost.this.moveSpeed = 1.5f;
					}
				}));
			}
			else
			{
				if(reachedDest)
				{
					reward.loc.x = 512;
					stage.discard(this);
					stage.add(reward);
				}
			}
		}
		else if(!firstEncounter)
		{
			stop = true;
		}
		
		if(stop && !firstEncounter && !used && reachedDest && Fundementals.distance(this, follower) < 150)
		{
			used = true;
			stage.add(Factory.printText("You didnt stay close enough...", new Color(255,0,255,255), stage.game.fpsFont, 200, this,-220,-20, null));
		}
	}
}