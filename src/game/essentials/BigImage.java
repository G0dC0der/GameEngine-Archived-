package game.essentials;

import game.core.Engine;
import game.core.Stage;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Can be used for large images such as backgrounds and foregrounds. The must commonly used ability in this class is easy portion render.
 * @author Pojahn Moradi
 *
 */
public class BigImage extends Image2D
{
	/**
	 * Allow you to customize how the {@code BigImage} should be rendered.
	 * @author Pojahn Moradi
	 */
	public enum RenderOption
	{
		/**
		 * Renders the entire background/foreground.
		 */
		DEFAULT,
		/**
		 * Render the background/foreground at a fixed position(the screen).<br>
		 */
		FIXED, 
		/**
		 * Render the parts of the background/foreground that is visible to the human eye. Possible performance boosts.
		 */
		PORTION,
		/**
		 * Repeats the texture vertically and horizontally.
		 */
		REPEAT,
		
		/**
		 * Parallax rendering.
		 */
		PARALLAX
	};
	
	private RenderOption type;
	private float ratioX, ratioY;
	private OrthographicCamera parallaxCamera;
	
	/**
	 * Creates an image from the given path with {@code RenderOption.DEFAULT} option.
	 * @param path The path to load the image from.
	 */
	public BigImage(String path)
	{
		this(path, RenderOption.DEFAULT);
		ratioX = ratioY = 1;
	}
	
	/**
	 * Creates an image from the given path with the given render option.
	 * @param path The path to load the image from.
	 * @param type The {@code RenderOption} to use.
	 */
	public BigImage(String path, RenderOption type)
	{
		super(path,false);
		setRenderOption(type);
	}
	
	/**
	 * Allow you to set the render option.
	 * @param type The {@code RenderOption} to use.
	 */
	public void setRenderOption(RenderOption type)
	{
		this.type = type;
		
		if(this.type == RenderOption.PARALLAX)
		{
			parallaxCamera = new OrthographicCamera(getWidth(), getHeight());
			parallaxCamera.setToOrtho(true);
			parallaxCamera.position.set(0, 0, 0);
		}
	}
	
	/**
	 * The ratio to use if the PARALLAX option is used. 0.5 means move half the speed of the screen movement.
	 * @param ration The ratio.
	 */
	public void setScrollRatio(float ratio)
	{
		ratioX = ratioY = ratio;
	}
	
	public void setScrollRatioX(float ratioX)
	{
		this.ratioX = ratioX;
	}

	public void setScrollRatioY(float ratioY)
	{
		this.ratioY = ratioY;
	}
	
	@Override
	public void draw(Batch batch)
	{
		final Engine e = Stage.getCurrentStage().game;
		
		switch(type)
		{
			case DEFAULT:
				setPosition(0, 0);
				super.draw(batch);
				
				break;
			case FIXED:
				setPosition(0, 0);
				e.hudCamera();
				super.draw(batch);
				e.gameCamera();
				
				break;
			case PORTION:
				float startX 	= e.tx - e.getScreenWidth() / 2;
				float startY 	= e.ty - e.getScreenHeight() / 2;
				float width 	= e.getScreenWidth();
				float height 	= e.getScreenHeight();
				float u			= startX / getWidth();
				float v 		= startY / getHeight();
				float u2		= (startX + width ) / getWidth();
				float v2		= (startY + height) / getHeight();
				batch.draw(getTexture(), startX, startY, width, height, u, v, u2, v2);

				//These caused a bug when restarting a stage.
//				setRegion(u, v, u2, v2);
//				setSize(width, height);
//				setPosition(startX, startY);
//				setFlip(false, true);
//				super.draw(batch);
				
				break;
			case REPEAT:
				Texture img = getTexture();
				int stageWidth  = Stage.getCurrentStage().size.width;
				int stageHeight = Stage.getCurrentStage().size.height;
				int repeatX = (int) (stageWidth /  img.getWidth());
				int repeatY = (int) (stageHeight / img.getHeight());
				
				if(stageWidth  > repeatX * img.getWidth())
					repeatX++;
				if(stageHeight > repeatY * img.getHeight())
					repeatY++;
					
				for(int x = 0; x < repeatX; x++)
					for(int y = 0; y < repeatY; y++)
					{
						setPosition(x * img.getWidth(), y * img.getHeight());
						super.draw(batch);
					}
				
				break;
			case PARALLAX:
				parallaxCamera.position.x = Math.max(e.getScreenWidth() / 2, parallaxCamera.position.x + (e.tx - e.getPrevTx()) * ratioX);
				parallaxCamera.position.y = Math.max(e.getScreenHeight() / 2, parallaxCamera.position.y + (e.ty - e.getPrevTy()) * ratioY);
				parallaxCamera.update();

				batch.setProjectionMatrix(parallaxCamera.combined);
				super.draw(batch);
				e.gameCamera();
				
				break;
		}
	}
}