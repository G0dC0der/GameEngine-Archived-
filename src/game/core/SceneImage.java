package game.core;

import game.core.Stage.RenderOption;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import static game.core.Stage.*;

class SceneImage extends GameObject
{
	RenderOption type;
	
	SceneImage(RenderOption type)
	{
		this.type = type;
		setVisible(true);
	}
	
	@Override
	public void drawSpecial(SpriteBatch batch)
	{
		switch(type)
		{
			case FULL:
				STAGE.game.drawObject(this);
				break;
			case FIXED:
				STAGE.game.clearTransformation();
				STAGE.game.drawObject(this);
				STAGE.game.restoreTransformation();
				break;
			case PORTION:
				OrthographicCamera camera = STAGE.game.getCamera();
				Engine e = STAGE.game;
				int vw = Stage.STAGE.game.getScreenWidth();
				int vh = Stage.STAGE.game.getScreenHeight();
				
				camera.up.set(0, 1, 0);
				camera.direction.set(0, 0, -1);
				camera.update();
				batch.setProjectionMatrix(camera.combined);
				
				batch.draw(getFrame().getTexture(),
							e.tx - vw / 2,
							e.ty - vh / 2,
							(int)(e.tx - vw / 2),
							(int)(e.ty - vh / 2),
							vw,
							vh);
				
				camera.up.set(0, -1, 0);
				camera.direction.set(0, 0, 1);
				camera.update();
				batch.setProjectionMatrix(STAGE.game.getCamera().combined);
				break;
			case REPEAT:
				Texture img = getFrame().getTexture();
				int stageWidth  = Stage.STAGE.size.width;
				int stageHeight = Stage.STAGE.size.height;
				int repeatX = (int) (stageWidth /  img.getWidth());
				int repeatY = (int) (stageHeight / img.getHeight());
				
				if(stageWidth  > repeatX * img.getWidth())
					repeatX++;
				if(stageHeight > repeatY * img.getHeight())
					repeatY++;
					
				for(int x = 0; x < repeatX; x++)
					for(int y = 0; y < repeatY; y++)
						batch.draw(img, x * img.getWidth(), y * img.getHeight());
				
				break;
		}
	}
}