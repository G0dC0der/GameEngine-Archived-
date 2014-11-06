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
	public static Image2D DEFAULT_HEALTH_IMAGE;
	
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
				hit(Stage.STAGE.lethalDamage);
			else if(tileType == GOAL)
				stage.game.setGlobalState(GameState.COMPLETED);
		});

		setHitEvent(subject -> 
		{
			if(0 >= hp)
				deathAction();
		});
	}
	
	/**
	 * Returns the state of the character.
	 * @return The state.
	 */
	public CharacterState getState() 
	{
		return state;
	}

	/**
	 * While this function is mostly set automatically by the engine, it can be set manually too.
	 * @param state The state this character should have.
	 */
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
	 * Returns the amount of lives this character have.
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
	
	Image2D getFrameByForce()
	{
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
	
	/**
	 * Transforms this main character to a ghost. Pass null to humanize again. 
	 * @param ghostData The ghost data.
	 */
	public void ghostify(List<PressedButtons> ghostData)
	{
		if(ghostData == null)
			this.ghostData = null;
		else
			this.ghostData = ghostData.toArray(new PressedButtons[ghostData.size()]);
	}
	
	/**
	 * Transforms this main character to a ghost. Pass null to humanize again. 
	 * @param ghostData The ghost data.
	 */
	public void ghostify(PressedButtons[] ghostData)
	{
		this.ghostData = ghostData;
	}
	
	/**
	 * Checks whether or not this {@code MainCharacter} is a  ghost.
	 * @return True if this unit is a ghost.
	 */
	public boolean isGhost()
	{
		return ghostData != null;
	}
	
	PressedButtons getNext()
	{
		if(ghostData == null)
			throw new IllegalStateException("This method can only be called if the MainCharacter is a ghost.");
		
		return replayCounter > ghostData.length - 1 ? STILL : ghostData[replayCounter++];
	}
	
	/**
	 * This function is called once by the engine every frame and handles all the movement of this character.
	 * @param pb The buttons that are being held down by the player.
	 */
	public abstract void handleInput(PressedButtons pb);
	
	/**
	 * Called upon death.
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
				Stage.STAGE.add(deathImg.getClone(loc.x + (width / 2) - (deathImg.width / 2), loc.y + (height / 2) - (deathImg.height / 2)));
		}
	}
}