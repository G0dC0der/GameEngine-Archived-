package stages.demo;

import java.io.File;

import kuusisto.tinysound.TinySound;
import game.core.GameObject;
import game.development.AutoDispose;
import game.development.StageBuilder;
import game.essentials.Animation;
import game.essentials.BigImage;
import game.essentials.BigImage.RenderOption;
import game.essentials.Controller;
import game.essentials.Image2D;
import game.essentials.Utilities;
import game.mains.Flipper;
import game.movable.Chain;
import game.objects.Particle;
import ui.accessories.Playable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

@SuppressWarnings("all")
@AutoDispose
//@AutoInstall(path=DebuggingMap.PATH, mainPath="res/general")
//@Playable(name="Debugging Map", description="Used for debugging and developing purposes.")
public class DebuggingMap extends StageBuilder
{
	static final String PATH = "res/debugging";
	
	private Image2D rec = new Image2D("res/data/rec.png", true);
	private Image2D aLink = new Image2D("C:/link.png");
	private Image2D bg = new Image2D("C:/background.png");
	private Flipper main;
	
	@Override
	public void init() 
	{
		map = new Pixmap(new FileHandle("res/debugging/map.png"));
		backgroundImg = new BigImage("res/debugging/background.png");
		mainImage   = Image2D.loadImages(new File("res/general/main"),true);
		deathImg   	= Image2D.loadImages(new File("res/general/main/death"), false);
		jump        = TinySound.loadSound(new File("res/general/jump.wav"));
	}
	
	@Override
	public void build() 
	{
		super.build();
		stageData = Utilities.createStageData(map);
		basicInits();
		
		backgroundImg.setRenderOption(RenderOption.DEFAULT);
		background(backgroundImg);
		
		/**
		 * Start testing below:
		 */
		
		main = new Flipper();
		main.setImage(new Animation<>(3, mainImage));
		main.setMultiFaced(true);
		main.setController((Controller)Utilities.importObject("res/data/controller1.con"));
		main.hit(1);
		main.setJumpingSound(jump);
		main.moveTo(startX, startY);
		main.deathImg = new Particle();
		main.deathImg.setImage(4,deathImg);
		main.deathImg.zIndex(101);
		game.addFocusObject(main);
		add(main);
		
		
		GameObject fakeMain = new GameObject();
		fakeMain.setImage(mainImage[0]);
		fakeMain.loc.set(200,200);
		add(fakeMain);
		
		Chain chain = new Chain(6);
		chain.setImage(aLink);
		chain.endPoint1(main, true);
		chain.endPoint2(fakeMain, true);
//		chain.rotateLinks(true);
//		chain.linkOnEndpoint(false);
		add(chain);
		
		OrthographicCamera cam = new OrthographicCamera(bg.getWidth(), bg.getHeight());
		cam.setToOrtho(true);
		cam.position.set(0, 0, 0);
		cam.update();
		
		add(new GameObject()
		{
			{
				zIndex(-101);
			}
			
			@Override
			public void drawSpecial(SpriteBatch batch) 
			{
				cam.position.x += (game.tx - game.getPrevTx()) * 1.5f;
				
				cam.position.x = Math.max(game.getScreenWidth() / 2, cam.position.x);
				
				cam.position.y += game.ty - game.getPrevTy();
				cam.update();
				
				batch.setProjectionMatrix(cam.combined);
				batch.draw(bg, 0, 0);
				game.gameCamera();
			}
		});
	}
	
	@Override
	protected void extra() 
	{
		if(Gdx.input.isKeyJustPressed(Keys.F))
			main.flip();
	}
}
