package game.core;

import static game.core.Engine.*;
import game.core.Engine.GameState;
import game.essentials.Controller;
import game.essentials.Controller.PressedButtons;
import game.essentials.Image2D;
import game.objects.Particle;

import java.util.List;

/**
 * A {@code MainCharacter} is a human-controllable and playable character that have health, can be hurt and most important of all, moves the character based on the players key strokes.
 * @author Pojahn Moradi
 */
public abstract class MainCharacter extends MovableObject
{
	public enum CharacterState { ALIVE, DEAD, FINISH };
	
	public static final PressedButtons STILL = new PressedButtons();
	public static final Image2D DEFAULT_HEALTH_IMAGE = new Image2D("res/general/hearth.png", false);
	
	/**
	 * This is the animation to play when the character have died. 
	 */
	public Particle deathImg;
	public Image2D healthImg = DEFAULT_HEALTH_IMAGE;
	protected Controller con;
	private CharacterState state;
	private int hp, invincibleCounter, frameSkipCounter, replayCounter;
	private boolean hurt, deathActionUsed;
	private PressedButtons[] ghostData;
	
	/**
	 * Constructs a MainCharacter with a default TileEvent and HitEvent.
	 */
	public MainCharacter()
	{
		super();
		hp = 0;
		invincibleCounter = frameSkipCounter = 0;
		triggerable = true;
		hurt = false;
		state = CharacterState.ALIVE;
		
		addTileEvent(tileType ->
		{
			Stage stage = Stage.STAGE;
			
			if(tileType == SOLID)
				deathAction();
			else if(tileType == LETHAL)
				hit(Stage.LETHAL_DAMAGE);
			else if(tileType == GOAL)
				stage.game.setGlobalState(GameState.FINISH);
		});

		setHitEvent(subject -> 
		{
			if(0 >= hp)
				deathAction();
		});
	}
	
	public CharacterState getState() 
	{
		return state;
	}

	public void setState(CharacterState state) 
	{
		this.state = state;
	}

	/**
	 * You can alter the characters health with this function. Use positive values to add lives or negative values to subtract.<br>
	 * When health has been subtracted, the character becomes invincible for 100 frames.<br>
	 * If the health after the subtraction is equal or less than zero, {@code deathAction} is called.
	 * @param strength
	 */
	public void hit(int strength)
	{
		if(strength >= 0)
			hp += strength;
		else if(!hurt)
		{
			hp += strength;
			hurt = true;			
		}
		
		if(0 >= hp)
			deathAction();
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
		
		if (hurt && ++frameSkipCounter % 3 != 0)
			return null;
		
		return super.getFrame();
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
	
	public void ghostify(List<PressedButtons> ghostData)
	{
		this.ghostData = ghostData.toArray(new PressedButtons[ghostData.size()]);
	}
	
	public void ghostify(PressedButtons[] ghostData)
	{
		this.ghostData = ghostData;
	}
	
	public boolean isGhost()
	{
		return ghostData != null;
	}
	
	public PressedButtons getNext()
	{
		if(ghostData == null)
			throw new IllegalStateException("This method can only be called if the MainCharacter is a ghost.");
		
		return replayCounter > ghostData.length - 1 ? STILL : ghostData[replayCounter++];
	}
	
	/**
	 * This function is called by the engine every frame and handles all the movement of this character.
	 * @param pb The buttons that are being held down by the player.
	 */
	public abstract void handleInput(PressedButtons pb);
	
	/**
	 * Called upon death. Can be called manually if engine fail to call it.<br>
	 * Usually, you in this method hides or discard your character, sets its hitbox to INVINCIBLE, state to DEAD and finally adding a death particle.
	 */
	final void deathAction()
	{
		if(!deathActionUsed)
		{
			deathActionUsed = true;
			visible = false;
			hitbox = Hitbox.INVINCIBLE;
			state = CharacterState.DEAD;
			halted = true;
			
			if(deathImg != null)
				Stage.STAGE.add(deathImg.getClone(currX + (width / 2) - (deathImg.width / 2), currY + (height / 2) - (deathImg.height / 2)));
		}
	}
}