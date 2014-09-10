package stages.tr1;

import game.core.GameObject;
import game.core.MainCharacter.CharacterState;
import game.development.AutoInstall;
import game.essentials.Factory;
import game.essentials.Image2D;
import game.essentials.Utilities;
import game.movable.SolidPlatform;

import java.io.File;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import stages.traning.AbstractTraningStage;
import ui.accessories.Playable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

@Playable(name="Traning Stage 1", description="Traning stage for beginners.")
@AutoInstall(mainPath="res/general", path="res/traning1")
public class TraningStage1 extends AbstractTraningStage
{
	private Image2D friendImg, platformImg, crystalImg[];
	private Sound talking, collect;
	private Music song;
	private BitmapFont talkingFont;
	private int crystals, collectedCrystals;
	
	@Override
	public void init() 
	{
		try
		{
			super.init();
			
			friendImg = new Image2D("res/traning1/friendImg.png");
			platformImg = new Image2D("res/traning1/platformImg.png");
			crystalImg = Image2D.loadImages(new File("res/clubber/diamond"));
			talking = Utilities.loadSound("res/traning1/talking.wav");
			collect = Utilities.loadSound("res/general/collect1.wav");
			talkingFont = new BitmapFont(Gdx.files.internal("res/traning1/talking.fnt"), true);
			setStageMusic("res/traning1/song.wav",7.80f, .6f);
			
			setFriendFont(game.fpsFont);
			setFriendTextColor(Color.WHITE);
			setFriendImage(friendImg);
			setTalking(talking);
			setFriendFont(talkingFont);
			
			game.timeColor = Color.WHITE;
		}
		catch(Exception e)
		{
			System.err.println("Warning: Could not load the resources.");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@Override
	public void build() 
	{
		super.build();
		
		crystals = collectedCrystals = 0;
		gm.addTileEvent(Factory.slipperWalls(gm));
		setPeople(gm);
		
		/*
		 * Moving Platform
		 */
		SolidPlatform solp = new SolidPlatform(350, 650, gm);
		solp.appendPath(350, 650, 10, false, null);
		solp.appendPath(861, 650, 10, false, null);
		solp.setImage(platformImg);
		solp.setMoveSpeed(1);
		add(solp);
		
		/*
		 * Crystals
		 */
		add(getCrystal(1962, 375),
			getCrystal(2112, 425),
			getCrystal(2262, 475),
			getCrystal(2312, 575),
			getCrystal(2312, 925),
			getCrystal(2312, 1175),
			getCrystal(2512, 1125),
			getCrystal(2662, 1075),
			getCrystal(2862, 1125),
			getCrystal(2812, 725),
			getCrystal(2912, 525),
			getCrystal(2787, 475),
			getCrystal(2661, 325),
			getCrystal(2312, 725));
		
		/*
		 * Friends
		 */
		add(getFriend(244, 1327, -150, -50, "Use your jump button to jump over the hindrance.\n"
											+ "Hold it to reach higher."),
			getFriend(1055, 1127, -310, -50, "You can wall jump by pressing the jump button while sliding on it.\n"
												+ "Hold the jump button to reach higher."),
			getFriend(280, 376, 20, -25, "Remeber, if you hold the jump button, you reach further/higher.\n"
										+ "Also, dont forget that you can move while floating in the air."),
			getFriend(1790, 376, -35, -30, "Collect all the crystals to complete the stage."));
	}
	
	@Override
	public void extra() 
	{
		if(collectedCrystals >= crystals)
			gm.setState(CharacterState.FINISH);
	}
	
	GameObject getCrystal(float x, float y)
	{
		crystals++;
		final GameObject crystal = new GameObject();
		crystal.currX = x;
		crystal.currY = y;
		crystal.setImage(5,crystalImg);
		crystal.addEvent(()->{
			if(crystal.collidesWith(gm))
			{
				collectedCrystals++;
				collect.play();
				discard(crystal);
			}
		});
		
		return crystal;
	}
	
	@Override
	public void dispose() 
	{
		super.dispose();
		disposeBatch(friendImg, platformImg, talking, song, crystalImg, talkingFont, collect);
	}
}
