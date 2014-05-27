package stages.traning;

import game.core.EntityStuff;
import game.core.GameObject;
import game.core.MovableObject;
import game.core.Engine.Direction;
import game.core.GameObject.Event;
import game.core.Stage;
import game.development.StageBuilder;
import game.essentials.Factory;
import game.essentials.Image2D;
import kuusisto.tinysound.Sound;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class AbstractTraningStage extends StageBuilder
{
	private Image2D[] friendImage;
	private GameObject[] people;
	private Sound talking;
	private BitmapFont friendFont;
	private Color friendTextColor;
	
	public GameObject getFriend(final float x, final float y, final float textOffsetX, final float textOffsetY, final String text)
	{
		final MovableObject friend = new MovableObject();
		friend.setImage(friendImage);
		friend.moveTo(x, y);
		friend.setDoubleFaced(true, true);
		friend.facing = Direction.N;
		friend.setManualFacing(true);
		friend.addEvent(new Event() 
		{
			int recoveryFrames = 0;
			GameObject currEvent;
			
			@Override
			public void eventHandling() 
			{
				if(0 > recoveryFrames-- && friend.collidesWithMultiple(people) != null)
				{
					talking.play();
					recoveryFrames = 230;
					currEvent = Factory.printText(text, friendTextColor, friendFont, 220, friend, textOffsetX, textOffsetY, ()->Stage.STAGE.discard(currEvent));
					add(currEvent);
				}
				
				//Watch the closest guy
				GameObject closest = EntityStuff.findClosest(friend, people);
				if(closest != null)
				{
					if(closest.currX + closest.width / 2 > friend.currX + friend.width / 2)
						friend.facing = Direction.NE;
					else
						friend.facing = Direction.NW;
				}
			}
		});
		
		return friend;
	}

	public void setFriendImage(Image2D... friendImage) 
	{
		this.friendImage = friendImage;
	}

	public void setPeople(GameObject... people) 
	{
		this.people = people;
	}

	public void setTalking(Sound talking) 
	{
		this.talking = talking;
	}

	public void setFriendFont(BitmapFont friendFont) 
	{
		this.friendFont = friendFont;
	}

	public void setFriendTextColor(Color friendTextColor) 
	{
		this.friendTextColor = friendTextColor;
	}
}