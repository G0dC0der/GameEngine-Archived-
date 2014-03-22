package game.movable;

import game.core.Enemy;
import game.core.Engine.Direction;
import game.core.GameObject;
import game.core.MovableObject;
import game.core.Stage;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import kuusisto.tinysound.Sound;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * The {@code Thwump} class resembles Thwomp from Super Mario Bros, where the unit stays up in the sky and fall downwards when an enemy(the targets) is beneath it.
 * @author Pojahn Moradi
 *
 */
public final class Thwump extends Enemy
{
	/**
	 * This enum is used by {@code Thwump} and allow you to specify its hitbox.<br>
	 * FALLING_DIR means the side of the rectangle the {@code Thwump} is moving at is deadly.<br>
	 * FULL means the entire unit is deadly.
	 * NONE means the unit behaves like a solid object.
	 * @author Pojahn Moradi
	 *
	 */
	public enum KillingBox
	{
		FALLING_DIR,
		FULL,
		NONE;
	}
	
	protected enum State
	{
		SLEEP,
		FALLING,
		RISING;
	}
	
	private State state;
	private float initialX, initialY;
	@SuppressWarnings("unused")
	private int attackSpeed, returnSpeed, recovery, recoveryCounter, x1, y1, x2, y2;
	private Direction fallingDir;
	private KillingBox killingbox;
	private MovableObject[] targets;
	private Frequency<Image2D> attackImage, sleepImage, returnImage;
	private final GameObject dummy, scanDummy;
	
	/**
	 * Constructs a {@code Thwump} instance.
	 * @param initialX The initial X position, which is starting position and the position the {@code Thwump} will return to after a attack mission.
	 * @param initialY The initial Y position, which is starting position and the position the {@code Thwump} will return to after a attack mission.
	 * @param targets The {@code MovableObjects} to scan for and attack.
	 */
	public Thwump(float initialX, float initialY, MovableObject... targets)
	{
		dummy = new GameObject();
		scanDummy = new GameObject();
		currX = this.initialX = initialX;
		currY = this.initialY = initialY;
		this.targets = targets;
		attackSpeed = 7;
		returnSpeed = 3;
		setFallingDirection(Direction.S);
		recovery = 40;
		killingbox = KillingBox.NONE;
		state = State.SLEEP;
		sounds = new SoundBank(1);
		
		for(MovableObject mo : targets)
			mo.avoidOverlapping(this);
	}
	
	/**
	 * The sound the play when the {@code Thwump} have smashed into a wall.
	 * @param sound
	 */
	public void setSmashingSound(Sound sound)
	{
		sounds.setSound(0, sound);
	}

	@Override
	public void moveEnemy() 
	{
		if(killingbox == KillingBox.FALLING_DIR)
		{
			for(MovableObject mo : targets)
				if(dummy.collidesWith(mo))
					mo.runHitEvent(this);
		}
		else if(killingbox == KillingBox.FULL)
		{
			for(MovableObject mo : targets)
				if(collidesWith(mo))
					mo.runHitEvent(this);
		}
		if(--recoveryCounter > 0)
			return;

		if(state == State.SLEEP)
		{
			for(GameObject go : targets)
			{
				if(scanDummy.collidesWith(go) && go.canSee(this, Accuracy.MID))
				{
					fall();
					break;
				}
			}
		}
		else
		{
			resetPrevs();
			move();

			if(killingbox == KillingBox.FALLING_DIR)
				moveDummy();
			
			for(MovableObject mo : targets)
				if(SolidPlatform.collidingWhen(this, currX, currY - 1, mo) || SolidPlatform.collidingWhen(this, currX - 1, currY, mo) ||  SolidPlatform.collidingWhen(this, currX + 1, currY, mo))
					adjust(mo, true);
		}
	}
	
	@Override
	public void endUse()
	{
		for(MovableObject mo : targets)
			mo.allowOverlapping(this);
	}
	
	protected void move()
	{
		boolean falling = state == State.FALLING;
		int speed = (falling) ? attackSpeed : returnSpeed;

		switch(fallingDir)
		{
		case S:
			if(falling)
			{
				if(!tryDown(speed))
					rollBack();
			}
			else if(!tryUp(speed))
				reset();
			break;

		case N:
			if(falling)
			{
				if(!tryUp(speed))
					rollBack();
			}
			else if(!tryDown(speed))
				reset();
			break;

		case E:
			if(falling)
			{
				if(!tryRight(speed))
					rollBack();
			}
			else if(!tryLeft(speed))
				reset();
			break;

		case W:
			if(falling)
			{
				if(!tryLeft(speed))
					rollBack();
			}
			else if(!tryRight(speed))
				reset();
			break;
		default:
			throw new IllegalStateException("Unknown direction: " + fallingDir);
		}
	}
	
	protected void fall()
	{
		state = State.FALLING;
		if(attackImage != null)
		{
			currImage = attackImage;
			currImage.reset();
		}
	}
	
	protected void rollBack()
	{
		recoveryCounter = recovery;
		state = State.RISING;
		if(returnImage != null)
		{
			currImage = returnImage;
			currImage.reset();
		}
	}
	
	protected void reset()
	{
		state = State.SLEEP;
		currImage = sleepImage;
		currImage.reset();
	}

	@Override
	public void drawSpecial(SpriteBatch batch) {}
	
	/**
	 * Allows you to specify which direction the {@code Thwump} will fall in(default: Engine.S).
	 * @param fallingDir An constant from {@code game.core.Engine} representing a direction.
	 */
	public void setFallingDirection(Direction fallingDir)
	{
		this.fallingDir = fallingDir;
		
		switch(fallingDir)
		{
		case S:
			x1 = (int) currX;
			y1 = (int)(currY + height);
			x2 = (int) (currX + width);
			y2 = (int) (currY + height);
			break;
		case N:
			x1 = (int) currX;
			y1 = (int) currY;
			x2 = (int)(currX + width);
			y2 = (int) currY;
			break;
		case E:
			x1 = (int)(currX + width);
			y1 = (int) currY;
			x2 = (int)(currX + width);
			y2 = (int)(currY + height);
			break;
		case W:
			x1 = (int) currX;
			y1 = (int) currY;
			x2 = (int) currX;
			y2 = (int)(currY + height);
			break;
		default:
			throw new IllegalArgumentException("Illegal falling direction: " + fallingDir);
		}
		initDummies();
	}
	
	/**
	 * The killing hitbox to use.
	 * @param killingbox The killing hitbox.
	 */
	public void setKillingBox(KillingBox killingbox)
	{
		this.killingbox = killingbox;
	}
	
	/**
	 * The attack speed to use.
	 * @param attackSpeed The speed.
	 */
	public void setAttackSpeed(int attackSpeed)
	{
		this.attackSpeed = attackSpeed;
	}
	
	/**
	 * The speed used when moving back to its initial position.
	 * @param returnSpeed The speed.
	 */
	public void setReturnSpeed(int returnSpeed)
	{
		this.returnSpeed = returnSpeed;
	}
	
	/**
	 * The amount of frames before a returned {@code Thwump} can attack again.
	 * @param recovery
	 */
	public void setRecovery(int recovery)
	{
		this.recovery = recovery;
	}
	
	/**
	 * The image to use when falling. If not set, the idle image will be used.
	 * @param attackImage The image.
	 */
	public void setAttackImage(Frequency<Image2D> attackImage)
	{
		this.attackImage = attackImage;
	}

	/**
	 * The image to use when returning. If not set, the idle image will be used.
	 * @param returnImage The image.
	 */
	public void setReturnImage(Frequency<Image2D> returnImage)
	{
		this.returnImage = returnImage;
	}
	
	/**
	 * The image to use when idle.
	 */
	@Override
	public void setImage(Frequency<Image2D> obj)
	{
		super.setImage(obj);
		sleepImage = obj;
		initDummies();
	}
	
	/**
	 * Use {@code setAttackSpeed} instead.
	 */
	@Override
	@Deprecated
	public final void setMoveSpeed(float speed)
	{
		throw new UnsupportedOperationException("Use attackSpeed and returnspeed instead.");
	}
	
	/**
	 * A {@code Thwump} must be rectangular.
	 */
	@Override
	@Deprecated
	public final void setHitbox(Hitbox hitbox)
	{
		throw new UnsupportedOperationException("Rectangular hitbox is enforced on Thwump");
	}
	
	private void initDummies()
	{		
		switch(fallingDir)
		{
			case N:
			case S:
				dummy.width = width;
				dummy.height = 4;
				break;
			case W:
			case E:
				dummy.width = 4;
				dummy.height = height;
				break;
			default:
				throw new IllegalStateException("Illegal direction of Thwump: " + fallingDir.toString());
		}
		
		switch(fallingDir)
		{
			case N:
				scanDummy.currX = x1;
				scanDummy.currY = 0;
				scanDummy.width = width;
				scanDummy.height = y1;
				break;
			case S:
				scanDummy.currX = x1;
				scanDummy.currY = currY + height;
				scanDummy.width = width;
				scanDummy.height = Stage.STAGE.height - currY - height;
				break;
			case W:
				scanDummy.currX = 0;
				scanDummy.currY = currY;
				scanDummy.width = currX;
				scanDummy.height = height;
				break;
			case E:
				scanDummy.currX = currX + width;
				scanDummy.currY = currY;
				scanDummy.width = Stage.STAGE.width - currX - width;
				scanDummy.height = height;
				break;
			default:
				throw new IllegalStateException("Illegal direction of Thwump: " + fallingDir.toString());
		}
	}
	
	protected void moveDummy()
	{
		switch(fallingDir)
		{
			case N:
				dummy.currX = currX;
				dummy.currY = currY - 1;
				break;
			case S:
				dummy.currX = currX;
				dummy.currY = currY + height + 1;
				break;
			case E:
				dummy.currX = currX + width + 1;
				dummy.currY = currY;
				break;
			case W:
				dummy.currX = currX - 1;
				dummy.currY = currY;
				break;
			default:
				throw new IllegalStateException("Illegal direction of Thwump: " + fallingDir.toString());
		}
	}
	
	@Override
	public boolean tryUp(float steps)
	{
		int targetX = (int) initialX,
			targetY = (int) initialY;
		boolean rising = state == State.RISING;
			
		for (int i = 0; i < steps; i++)
		{
			if(rising && (int)currX == targetX && (int)currY == targetY)
				return false;
			if (canGoUp())
				currY--;
			else
			{
				sounds.trySound(0, true);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean tryDown(float steps)
	{
		int targetX = (int) initialX,
			targetY = (int) initialY;
		boolean rising = state == State.RISING;
		
		for (int i = 0; i < steps; i++)
		{
			if(rising && (int)currX == targetX && (int)currY == targetY)
				return false;
			if (canGoDown())
				currY++;
			else
			{
				sounds.trySound(0, true);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean tryLeft(float steps)
	{
		int targetX = (int) initialX,
			targetY = (int) initialY;
		boolean rising = state == State.RISING;
			
		for (int i = 0; i < steps; i++)
		{
			if(rising && (int)currX == targetX && (int)currY == targetY)
				return false;
			if (canGoLeft())
				currX--;
			else
			{
				sounds.trySound(0, true);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean tryRight(float steps)
	{
		int targetX = (int) initialX,
			targetY = (int) initialY;
		boolean rising = state == State.RISING;
			
		for (int i = 0; i < steps; i++)
		{
			if(rising && (int)currX == targetX && (int)currY == targetY)
				return false;
			if (canGoRight())
				currX++;
			else
			{
				sounds.trySound(0, true);
				return false;
			}
		}
		return true;
	}
}
