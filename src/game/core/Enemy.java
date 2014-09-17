package game.core;

/**
 * An Enemy is most often moving obstacles that can be appended to the stage.
 * @author Pojahn Moradi
 */
public abstract class Enemy extends MovableObject
{
	protected final Stage stage = Stage.STAGE;
	
	/**
	 * This function is called automatically once every frame by the engine when added to the game.<br>
	 * It is called before rendering it.
	 */
	public abstract void moveEnemy();
}