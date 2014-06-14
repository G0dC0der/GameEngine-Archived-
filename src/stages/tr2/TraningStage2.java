package stages.tr2;

import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.MainCharacter.CharacterState;
import game.development.AutoInstall;
import game.essentials.Factory;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import game.essentials.Utilities;
import game.movable.Gold;

import java.io.File;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import stages.traning.AbstractTraningStage;
import ui.accessories.Playable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

@Playable(name="Traning Stage 2", description="Traning stage for intermediate players.")
@AutoInstall(mainPath="res/general", path="res/traning2")
public class TraningStage2  extends AbstractTraningStage
{
	private Image2D friendImg, ropeImg, keyImg, doorImg, buttonImg, flagImg[], goldImg[];
	private Sound collect, press, open, shut;
	private Music song, talk1, talk2, talk3;
	private BitmapFont talkingFont;
	private int buttons, pressedButtons;
	
	@Override
	public void init() 
	{
		try
		{
			super.init();
			
			friendImg = new Image2D("res/traning2/friend.png");
			ropeImg = new Image2D("res/traning2/rope.png");
			keyImg = new Image2D("res/traning2/key.png");
			doorImg = new Image2D("res/traning2/door.png");
			buttonImg = new Image2D("res/traning2/bu.png");
			flagImg = Image2D.loadImages(new File("res/climb/flag"));
			goldImg = Image2D.loadImages(new File("res/traning2/collect"));
			
			talk1 = Utilities.loadMusic("res/traning2/talk1.wav");
			talk2 = Utilities.loadMusic("res/traning2/talk2.wav");
			talk3 = Utilities.loadMusic("res/traning2/talk3.wav");
			press = Utilities.loadSound("res/traning2/press.wav");
			open = Utilities.loadSound("res/traning2/dooropen.wav");
			shut = Utilities.loadSound("res/traning2/doorshut.wav");
			collect = Utilities.loadSound("res/general/collect1.wav");
			talkingFont = new BitmapFont(Gdx.files.internal("res/traning1/talking.fnt"), true);
			setStageMusic("res/traning2/song.ogg",0f);
			
			setFriendFont(game.fpsFont);
			setFriendTextColor(Color.YELLOW);
			setFriendImage(friendImg);
			setTalking(new int[]{0,1,2,2,1,0,2},new Music[]{talk1,talk2,talk3});
			setFriendFont(talkingFont);
			
			game.timeColor = Color.YELLOW;
		}
		catch(Exception e)
		{
			System.err.println("Warning: Could not load the resources.");
			e.printStackTrace();
			dispose();
			System.exit(-1);
		}
	}
	
	@Override
	public void build() 
	{
		super.build();
		gm.addTileEvent(Factory.slipperWalls(gm));
		buttons = pressedButtons = 0;
		setPeople(gm);
		
		/*
		 * Gold
		 */
		Frequency<Image2D> goldImage = new Frequency<>(5, goldImg);
		goldImage.pingPong(true);
		
		Gold gold = new Gold(435, 261, -1000, gm);
		gold.setImage(goldImage);
		gold.setCollectSound(collect);
		
		add(gold,
			gold.getClone(497, 261),
			gold.getClone(723, 261),
			gold.getClone(785, 261));
		
		/*
		 * Key + door
		 */
		final GameObject door = new GameObject();
		door.moveTo(2016, 672);
		door.setImage(doorImg);
		gm.avoidOverlapping(door);
		
		final GameObject key = new GameObject();
		key.moveTo(1261, 647);
		key.setImage(keyImg);
		key.addEvent(()->{
			if(key.collidesWith(gm))
			{
				collect.play();
				discard(door,key);
				gm.allowOverlapping(door);
			}
		});
		
		add(door,key);

		/*
		 * Buttons and door
		 */
		final SoundBank bank = new SoundBank(2);
		bank.setSound(0, open);
		bank.setSound(1, shut);
		bank.forbidSound(1);
		
		final GameObject door2 = new GameObject();
		door2.moveTo(2688, 192);
		door2.setImage(doorImg);
		door2.addEvent(()->{
			if(pressedButtons == buttons)
			{
				door2.setVisible(false);
				gm.allowOverlapping(door2);
				bank.trySound(0, false);
				bank.allowSound(1);
			}
			else if(!door2.collidesWith(gm))
			{
				door2.setVisible(true);
				gm.avoidOverlapping(door2);
				bank.trySound(1, false);
				bank.allowSound(0);
			}
		});
		add(door2);
		
		add(getButton(2629, 669),
			getButton(2629, 477),
			getButton(2629, 285),
			getButton(2437, 573),
			getButton(2437, 381));
		
		/*
		 * Rope
		 */
		final GameObject rope = new GameObject();
		rope.setImage(ropeImg);
		rope.moveTo(2984, 482);
		rope.addEvent(()->{
			final float limit = gm.maxX - (gm.maxX / 5);
			
			if(gm.vx > limit || gm.vx < -limit || gm.vy > 10 || gm.vy < -10)
				gm.allowOverlapping(rope);
			else if(!gm.collidesWith(rope))
				gm.avoidOverlapping(rope);
		});
		gm.avoidOverlapping(rope);
		
		add(rope);
		
		/*
		 * Friends
		 */
		add(getFriend(823, 631, 30, -10, "Get the key to open the door."),
			getFriend(2359, 727, -50, -43, "Press all the buttons and\nquickly pass through the gate."),
			getFriend(2906, 415, -25, -43, "Move cautiously at the rope or you might fall down."));
		
		/*
		 * Goal
		 */
		final GameObject flag = new GameObject();
		flag.setImage(3,flagImg);
		flag.moveTo(3972, 412);
		flag.addEvent(()->{
			if(flag.collidesWith(gm))
				gm.setState(CharacterState.FINISH);
		});
		add(flag);
	}
	
	GameObject getButton(float x, float y)
	{
		buttons++;
		final GameObject button = new GameObject();
		button.setImage(buttonImg);
		button.moveTo(x, y);
		button.addEvent(new Event()
		{
			int counter;
			boolean pressed;
			
			@Override
			public void eventHandling() 
			{
				if(!pressed && button.collidesWith(gm))
				{
					pressed = true;
					button.setVisible(false);
					pressedButtons++;
					counter = 600;
					press.play();
				}
				
				if(pressed && counter-- < 0)
				{
					pressed = false;
					button.setVisible(true);
					pressedButtons--;
				}
			}
		});
		
		return button;
	}
	
	@Override
	public void dispose() 
	{
		super.dispose();
		disposeBatch(friendImg, song, talkingFont, collect, ropeImg, doorImg, keyImg, buttonImg, press, open, shut, talk1, talk2, talk3, flagImg, goldImg);
	}
}
