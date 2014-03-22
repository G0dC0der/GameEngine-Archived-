package game.core;

import static game.core.Engine.*;
import game.core.Engine.GameState;
import game.essentials.Controller;
import game.essentials.Controller.PressedButtons;
import game.essentials.Image2D;
import game.objects.Particle;

/**
 * A {@code MainCharacter} is a human-controllable and playable character that have health, can be hurt and most important of all, moves the character based on the players key strokes.
 * @author Pojahn Moradi
 */
public abstract class MainCharacter extends MovableObject
{
	/**
	 * This is the animation to play when the character have died. 
	 */
	public Particle deathImg;
	protected int hp, hurtSkip;
	protected Controller con;
	protected boolean moving;
	private int invincibleCounter, frameSkipCounter;
	private boolean hurt, useOnce;
	
	/**
	 * Constructs a MainCharacter with a default TileEvent and HitEvent.
	 */
	public MainCharacter()
	{
		super();
		hp = 0;
		invincibleCounter = frameSkipCounter = 0;
		hurtSkip = 3;
		triggerable = true;
		moving = hurt = false;
		
		addTileEvent(new TileEvent()
		{
			@Override
			public void eventHandling(byte tileType) 
			{
				Stage stage = Stage.STAGE;
				
				if(tileType == SOLID)
					deathAction();
				else if(tileType == LETHAL)
				{
					hit(Stage.LETHAL_DAMAGE);
					
					if(hp <= 0)
						deathAction();
				}
				else if(tileType == GOAL)
					stage.game.setState(GameState.FINISH);
			}
		});
		
		setHitEvent(new HitEvent()
		{
			@Override
			public void eventHandling(GameObject subject) 
			{
				if (0 >= hp)
					deathAction();
			}
		});
	}
	
	/**
	 * You can alter the characters health with this function. Use positive values to add lives or negative values to subtract.<br>
	 * When health has been subtracted, the character becomes invincible for 100 frames.
	 * @param strength
	 */
	public void hit(int strength)
	{
		if(strength > 0)
			hp += strength;
		else if(!hurt)
		{
			hp += strength;
			hurt = true;			
		}
	}
	
	/**
	 * Return the amount of lives this character have.
	 * @return The lives.
	 */
	public int getHP()
	{
		return hp;
	}
	
	/**
	 * Checks if this character is currently hurt(is in the temporary invincible mode).
	 * @return True if this character is currently hurt.
	 */
	public boolean isHurt()
	{
		return hurt;
	}
	
	@Override
	public Image2D getFrame()
	{
		if (hurt && ++invincibleCounter % 100 == 0)
			hurt = false;
		
		if (!visible || (hurt && ++frameSkipCounter % hurtSkip != 0))
			return null;
		
		if(!multiFacings)
			return super.getFrame();
		
		if (moving)
			currImage.getObject();
		
		return getRightFrame();
	}
	
	/**
	 * The controller this character should use.<br>
	 * A controller is linked keys on the keyboard.
	 * @param con The controller to use.
	 */
	public void setController(Controller con)
	{
		this.con = con;
	}
	
	/**
	 * This function is called by the engine every frame and handles all the movement of this character.
	 * @param pb The buttons that are being held down by the player.
	 */
	public abstract void handleInput(PressedButtons pb);
	
	/**
	 * Called by the engine every frame when dead.
	 */
	protected void deathAction()
	{
		if(!useOnce)
		{
			useOnce = true;
			visible = false;
			hitbox = Hitbox.INVINCIBLE;
			Stage.STAGE.game.setState(GameState.DEAD);
			Stage.STAGE.add(deathImg.getClone(currX + (width / 2) - (deathImg.width / 2), currY + (height / 2) - (deathImg.height / 2)));
		}
	}
}