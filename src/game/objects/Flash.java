package game.objects;

import game.core.Engine;
import game.core.GameObject;
import game.core.Stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Flash extends GameObject
{
	private float duration, framesAlive;
	private Texture flashImage;
	
	{
		zIndex(10_000);
	}
	
	public Flash(Color color, float duration)
	{
		this.duration = (float) duration;
		
		Pixmap px = new Pixmap(1, 1, Format.RGBA8888);
		px.setColor(color);
		px.fill();
		
		this.flashImage = new Texture(px); 
		px.dispose();
	}
	
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
			
			final Engine eng = Stage.STAGE.game;
			Color orgColor = batch.getColor();
			Color newColor = new Color(orgColor);
			newColor.a = (duration - framesAlive) * (1.0f / duration);
			
			batch.setColor(newColor);
			eng.clearTransformation();
			batch.draw(flashImage, 0, 0, eng.getScreenWidth(), eng.getScreenHeight());
			eng.restoreTransformation();
			batch.setColor(orgColor);
		}
		else
			Stage.STAGE.discard(this);
	}
	
	public Flash getClone(float x, float y) 
	{
		Flash flash = new Flash(flashImage,duration);
		copyData(flash);
		
		return flash;
	}
}