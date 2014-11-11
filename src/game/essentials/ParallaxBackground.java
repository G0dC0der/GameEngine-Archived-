package game.essentials;

import game.core.Engine;
import game.core.Stage;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * An class easing the work to create parallax backgrounds.<br>
 * Instances of this class should wrapped in a {@code GameObject} where the parallax background is render through the overridden {@code drawSpecial} method.
 * @author bitowl (http://bitowl.de/day16/)
 */
public class ParallaxBackground 
{
	/**
	 * A layer for the {@code ParallaxBackground}.
	 * @author bitowl (http://bitowl.de/day16/)
	 */
	public static class ParallaxLayer 
	{
		private Image2D image;
		private float ratioX, ratioY, positionX, positionY;

		/**
		 * An layer for the {@code ParallaxBackground}.
		 * @param image The image.
		 * @param pRatioX The ratio to move in the x axis. 0.5 means half the speed of player movement.
		 * @param pRatioY The ratio to move in the y axis. 0.5 means half the speed of player movement.
		 */
		public ParallaxLayer(Image2D image, float pRatioX, float pRatioY) 
		{
			this.image = image;
			ratioX = pRatioX;
			ratioY = pRatioY;
		}

		private void moveX(float txDiff) 
		{
			positionX += txDiff * ratioX;
		}
		
		private void moveY(float tyDiff) 
		{
			positionY += tyDiff * ratioY;
		}
	}
	
	private ParallaxLayer[] layers;
	private Camera camera;
	
	/**
	 * Creates a {@code ParallaxBackground} with the given layers and camera.
	 * @param camera The camera to use.
	 * @param layers The layers. Those layers further "away" should be positioned at the end of the array.
	 */
	public ParallaxBackground(Camera camera, ParallaxLayer... layers)
	{
		this.camera = camera;
		this.layers = layers;
	}
	
	/**
	 * Renders all the layers. Should be called every frame.
	 * @param batch The batch.
	 */
	public void draw(Batch batch)
	{
		final Engine game = Stage.getCurrentStage().game;
		
		update(game.tx - game.getPrevTx(), game.ty - game.getPrevTy());
		camera.update();
		batch.setProjectionMatrix(camera.projection);
		
		for (ParallaxLayer layer : layers)
		{
			layer.image.setPosition(-camera.viewportWidth  / 2 - layer.positionX, -camera.viewportHeight / 2 - layer.positionY);
			layer.image.setFlip(false, false);
			layer.image.draw(batch);
		}

		game.gameCamera();
	}
	
	private void update(float txDiff, float tyDiff)
	{
		for(ParallaxLayer layer : layers)
		{
			layer.moveX(txDiff);
			layer.moveY(tyDiff);
		}
	}
}
