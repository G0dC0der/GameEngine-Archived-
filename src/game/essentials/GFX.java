package game.essentials;

import static game.essentials.Utilities.*;
import game.core.Fundementals;
import game.core.Stage;
import java.util.Random;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

/**
 * A collection of static methods to handle or render graphic related stuff.
 * @author Pojahn Moradi
 *
 */
public class GFX 
{
	private static final Texture dot = new Texture("res/data/blob.png");

	/**
	 * Draws an electric beam between the two given points.
	 * @param batch The sprite batch.
	 * @param x1 The x coordinate of the first point.
	 * @param y1 The y coordinate of the first point.
	 * @param x2 The x coordinate of the second point.
	 * @param y2 The y coordinate of the second point.
	 * @param displace The amount of pixel to travel outside the given line.
	 * @param detail The jagged level of the line. Lower means more.
	 * @param thickness The thickness of a bolt.
	 * @param noise How precise should the beam 
	 * @param numberOfBolts The number of bolts.
	 * @param blend Whether or not to blend the bolts.
	 * @param colors The colors of the bolts.
	 */
	public static void drawLightning(SpriteBatch batch, float x1, float y1, float x2, float y2, float displace, float detail, float thickness, float noise, int numberOfBolts, boolean blend, Color... colors) 
	{
		Color orgColor = Stage.getCurrentStage().game.defaultTint;
		if(blend)
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

		for (int i = 0; i < numberOfBolts; i++) 
		{
			batch.setColor(getRandomElement(colors));
			drawSingleP2PLightning(batch, x1, y1, x2 + getRandom(-noise, noise), y2 + getRandom(-noise, noise), 117, 1.8f, thickness);
		}
		
		batch.setColor(orgColor);
		if(blend)
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private static void drawLine(SpriteBatch batch, float _x1, float _y1, float _x2, float _y2, float thickness) 
	{
		float length = (float) Fundementals.distance(_x1, _y1, _x2, _y2);
		float dx = _x1;
		float dy = _y1;
		dx = dx - _x2;
		dy = dy - _y2;
		float angle = MathUtils.radiansToDegrees * MathUtils.atan2(dy, dx);
		angle = angle - 180;
		
		batch.draw(dot, _x1, _y1, 0f, thickness * 0.5f, length, thickness, 1f, 1f, angle, 0, 0, dot.getWidth(), dot.getHeight(), false, false);
	}

	private static void drawSingleP2PLightning(SpriteBatch batch, float x1, float y1, float x2, float y2, float displace, float detail, float thickness) 
	{
		if (displace < detail) 
			drawLine(batch, x1, y1, x2, y2, thickness);
		else 
		{
			float mid_x = (x2 + x1) * 0.5f;
			float mid_y = (y2 + y1) * 0.5f;
			mid_x += (Math.random() - 0.5f) * displace;
			mid_y += (Math.random() - 0.5f) * displace;
			drawSingleP2PLightning(batch, x1, y1, mid_x, mid_y, displace * 0.5f, detail, thickness);
			drawSingleP2PLightning(batch, x2, y2, mid_x, mid_y, displace * 0.5f, detail, thickness);
		}
	}

	private static float getRandom(float min, float max) 
	{
		Random r = new Random();
		return min + (max - min) * r.nextFloat();
	}
}