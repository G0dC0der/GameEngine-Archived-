package game.objects;

import game.core.Engine;
import game.core.GameObject;
import game.core.Stage;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Allows easy creation of a flash GFX, which is displayed over the entire screen.<br>
 * The flash starts at full strength and fade down.
 * @author Pojahn Moradi
 */
public class Flash extends GameObject
{
	private float duration, framesAlive;
	private Texture flashImage;
	
	{
		zIndex(10_000);
	}
	
	/**
	 * Creates a flash.
	 * @param color The color of the frames.
	 * @param duration The amount of frames the flash will last.
	 */
	public Flash(Color color, float duration)
	{
		this.duration = (float) duration;
		
		Pixmap px = new Pixmap(1, 1, Format.RGBA8888);
		px.setColor(color);
		px.fill();
		
		this.flashImage = new Texture(px); 
		px.dispose();
	}
	
	/**
	 * Creates a flash with a texture rather than a single color. If the image is not the same size as the viewport, it will be stretched to cover it.
	 * @param flashImage The image, which should not contain any transparency. 
	 * @param duration The amount of frames the flash will last.
	 */
	public Flash(Texture flashImage, float duration)
	{
		this.duration = duration;
		this.flashImage = flashImage;
	}
	
	@Override
	public void drawSpecial(SpriteBatch batch) 
	{
		if(framesAlive < duration)
		{
			framesAlive++;
			
			final Engine eng = Stage.getCurrentStage().game;
			Color orgColor = batch.getColor();
			Color newColor = new Color(orgColor);
			newColor.a = (duration - framesAlive) * (1.0f / duration);
			
			batch.setColor(newColor);
			eng.hudCamera();
			batch.draw(flashImage, 0, 0, eng.getScreenWidth(), eng.getScreenHeight());
			eng.gameCamera();
			batch.setColor(orgColor);
		}
		else
			Stage.getCurrentStage().discard(this);
	}
	
	@Override
	public Flash getClone(float x, float y) 
	{
		Flash flash = new Flash(flashImage,duration);
		copyData(flash);
		
		if(cloneEvent != null)
			cloneEvent.cloned(flash);
		
		return flash;
	}
}