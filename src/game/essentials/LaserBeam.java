package game.essentials;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * An optional way of implementing laser beams and is used by {@code LaserDrone} and {@code TargetLaser}.<br>
 * Basically, {@code renderLasers} should be called every and if there is something to render(by calling {@code fireAt}), render it.
 * @author Pojahn Moradi
 *
 */
public interface LaserBeam 
{
	/**
	 * Register a laser beam(but not rendering it).
	 * @param srcX The lasers starting x position.
	 * @param srcY The lasers starting y position.
	 * @param destX The lasers end x position.
	 * @param destY The lasers end x position.
	 * @param active The amount of frames the laser should stay active.
	 */
	void fireAt(float srcX, float srcY, float destX, float destY, int active);
	
	/**
	 * Render all the registered laser beams.
	 * @param b The batch.
	 */
	void renderLasers(SpriteBatch b);
}
