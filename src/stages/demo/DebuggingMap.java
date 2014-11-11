package stages.demo;

import game.core.GameObject;
import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.StageBuilder;
import game.essentials.Animation;
import game.essentials.Controller;
import game.essentials.Image2D;
import game.essentials.BigImage.RenderOption;
import game.essentials.ParallaxBackground;
import game.essentials.ParallaxBackground.ParallaxLayer;
import game.mains.GravityMan;
import game.movable.Chain;
import game.objects.CheckpointsHandler;
import game.objects.Particle;
import ui.accessories.Playable;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

@SuppressWarnings("all")
@AutoDispose
@AutoInstall(path=DebuggingMap.PATH, mainPath="res/general")
//@Playable(name="Debugging Map", description="Used for debugging and developing purposes.")
public class DebuggingMap extends StageBuilder
{
	static final String PATH = "res/debugging";
	
	private Image2D rec = new Image2D("res/data/rec.png", true);
	private Image2D aLink = new Image2D("C:/link.png");
	private Image2D bg = new Image2D("C:/background.png");
	
	@Override
	public void build() 
	{
		super.build();
		
		backgroundImg.setRenderOption(RenderOption.DEFAULT);
		
		/**
		 * Start testing below:
		 */
		
		gm.flyMode(5);
		
		GameObject fakeMain = new GameObject();
		fakeMain.setImage(mainImage[0]);
		fakeMain.loc.set(200,200);
		add(fakeMain);
		
		Chain chain = new Chain(6);
		chain.setImage(aLink);
		chain.endPoint1(gm, true);
		chain.endPoint2(fakeMain, true);
//		chain.rotateLinks(true);
//		chain.linkOnEndpoint(false);
		add(chain);
		
		OrthographicCamera cam = new OrthographicCamera(bg.getWidth(), bg.getHeight());
		cam.setToOrtho(true);
		cam.position.set(bg.getWidth() / 2, bg.getHeight() / 2, 0);
		
		ParallaxLayer layer1 = new ParallaxLayer(bg, .5f, 0);
		
		ParallaxBackground pbg = new ParallaxBackground(cam, layer1);
		
		GameObject go = new GameObject()
		{
			@Override
			public void drawSpecial(SpriteBatch batch) 
			{
				pbg.draw(batch);
			}
		};
		go.zIndex(-105);
		add(go);
	}
	
	@Override
	protected void extra() 
	{
	}
}
