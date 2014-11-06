package stages.demo;

import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.StageBuilder;
import game.essentials.Animation;
import game.essentials.Controller;
import game.essentials.Image2D;
import game.essentials.BigImage.RenderOption;
import game.mains.GravityMan;
import game.objects.CheckpointsHandler;
import game.objects.Particle;
import ui.accessories.Playable;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

@SuppressWarnings("all")
@AutoDispose
@AutoInstall(path=DebuggingMap.PATH, mainPath="res/general")
//@Playable(name="Debugging Map", description="Used for debugging and developing purposes.")
public class DebuggingMap extends StageBuilder
{
	static final String PATH = "res/debugging";
	
	private Image2D rec = new Image2D("res/data/rec.png", true);
	
	@Override
	public void build() 
	{
		super.build();
		
		backgroundImg.setRenderOption(RenderOption.PORTION);
		
		/**
		 * Start testing below:
		 */
		
		gm.flyMode(5);
		
		game.zoom = 1.2f;
	}
	
	@Override
	protected void extra() 
	{
	}
}
