package game.movable;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.core.Enemy;
import game.core.Engine;
import game.core.Stage;

/**
 * An {@code Enemy} that exist a given amount of frames.<br>
 * This class is often used as a rendering event, where {@code drawSpecial} is overridden.
 * @author Pojahn Moradi
 */
public abstract class TimedEnemy extends Enemy
{
	protected int time = 0;
	private int counter = 0;
	private Engine e = Stage.STAGE.game;
	
	@Override
	public void moveEnemy()
	{
		if(counter++ > time)
			Stage.STAGE.discard(this);
	}
	
	/**
	 * Clears the transformation matrix.
	 * @param g The rendering context.
	 */
	public void clearTransformation(SpriteBatch g)
	{
		OrthographicCamera camera = stage.game.getCamera();
		camera.position.set(stage.visibleWidth / 2, stage.visibleHeight / 2, 0);
		camera.zoom = 1;
		camera.rotate(-e.angle);
		camera.update();
		g.setProjectionMatrix(camera.combined);
	}
	
	/**
	 * Restores the transformation matrix.
	 * @param g The rendering context.
	 */
	public void restoreTransformation(SpriteBatch g)
	{
		OrthographicCamera camera = stage.game.getCamera();
		camera.position.set(e.tx, e.ty, 0);
		camera.zoom = e.zoom;
		camera.rotate(e.angle);
		camera.update();
		g.setProjectionMatrix(camera.combined);
	}
}