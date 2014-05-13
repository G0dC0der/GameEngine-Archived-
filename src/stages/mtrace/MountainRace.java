package stages.mtrace;

import game.core.Engine;
import game.core.Engine.Direction;
import game.core.Engine.GameState;
import game.core.GameObject;
import game.core.GameObject.Hitbox;
import game.core.MovableObject;
import game.core.MovableObject.TileEvent;
import game.development.AutoInstall;
import game.development.AutoLoad;
import game.development.StageBuilder;
import game.essentials.Controller.PressedButtons;
import game.essentials.Factory;
import game.essentials.Image2D;
import game.mains.GravityMan;
import game.movable.Bouncer;
import game.objects.OneWay;
import game.objects.Wind;

import java.io.File;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;

import com.badlogic.gdx.graphics.Color;

@AutoInstall(mainPath="res/general", path="res/mtrace")
@Playable(name="Mountain Race", description="Stage: Mountain Race\nAuthor: Pojahn Moradi\nDifficulty: 5\nAverage time: 100 sec\nProfessional time: 70 sec\nObjective: Race to the finish.")
public class MountainRace extends StageBuilder
{
	@AutoLoad(path="res/general", type=VisualType.IMAGE)
	public Image2D bounce;
	
	@AutoLoad(path="res/race", type=VisualType.IMAGE)
	public Image2D[] cont1, cont2, cont3;
	
	@AutoLoad(path="res/mtrace", type=VisualType.IMAGE)
	public Image2D windmaker, clouds, singlecloud, twinclouds, wind[], flag[];
	
	private Sound bouncesound, contjump1, contjump2, contjump3;
	private Music blow;
	
	private PressedButtons[] replay1, replay2, replay3;
	
	@Override
	@SuppressWarnings("deprecation")
	public void init()
	{
		try
		{
			super.init();
			
			bouncesound = TinySound.loadSound(new File(("res/mtrace/bouncesound.wav")));
			contjump1 = TinySound.loadSound(new File(("res/general/jump.wav")));
			contjump2 = TinySound.loadSound(new File(("res/general/jump.wav")));
			contjump3 = TinySound.loadSound(new File(("res/general/jump.wav")));
			
			replay1 = (PressedButtons[]) PressedButtons.decode("res/mtrace/cont1.rlp")[1];
			replay2 = (PressedButtons[]) PressedButtons.decode("res/mtrace/cont2.rlp")[1];
			replay3 = (PressedButtons[]) PressedButtons.decode("res/mtrace/cont3.rlp")[1];
			
			blow = TinySound.loadMusic(new File(("res/mtrace/blow.wav")));
			blow.play(true, 0);
			
			setStageMusic("res/mtrace/song.wav", 3.90f);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@Override
	public void build()
	{
		/*
		 * Standard stuff
		 */
		super.build();
		gm.addTileEvent(Factory.slipperWalls(gm));
		gm.zIndex(50);
		
		/*
		 * Contestants
		 */
		GravityMan ghost1 = new GravityMan();
		ghost1.setImage(3, cont1);
		ghost1.moveTo(startX, startY);
		ghost1.addTileEvent(Factory.slipperWalls(ghost1));
		ghost1.setMultiFaced(true);
		ghost1.setJumpingSound(contjump1);
		ghost1.removeTileEvent(null);
		ghost1.ghostify(replay1);
		ghost1.addTileEvent(new TileEvent()
		{	
			boolean used = false;
			
			@Override
			public void eventHandling(byte tileType) 
			{
				if(!used && tileType == Engine.GOAL)
				{
					used = true;
					
					String pos = game.getGlobalState() == GameState.FINISH ? "4th" : "3rd";
					add(Factory.printText(pos + " place goes to Weed Guy!", Color.BLACK, null, 150, gm, -150, -150, null));
				}
			}
		});
		add(Factory.soundFalloff(contjump1, ghost1, gm, 500, 0, 20,1));
		
		GravityMan ghost2 = new GravityMan();
		ghost2.setImage(3, cont2);
		ghost2.moveTo(startX, startY);
		ghost2.addTileEvent(Factory.slipperWalls(ghost2));
		ghost2.setMultiFaced(true);
		ghost2.setJumpingSound(contjump2);
		ghost2.removeTileEvent(null);
		ghost2.ghostify(replay2);
		ghost2.addTileEvent(new TileEvent()
		{	
			boolean used = false;
			
			@Override
			public void eventHandling(byte tileType) 
			{
				if(!used && tileType == Engine.GOAL)
				{
					used = true;
					
					String pos = game.getGlobalState() == GameState.FINISH ? "3rd" : "2nd";
					add(Factory.printText(pos + " place goes to White Boy!", Color.BLACK, null, 150, gm, -150, -150, null));
				}
			}
		});
		add(Factory.soundFalloff(contjump2, ghost2, gm, 500, 0, 20,1));
		
		GravityMan ghost3 = new GravityMan();
		ghost3.setImage(3, cont3);
		ghost3.moveTo(startX, startY);
		ghost3.addTileEvent(Factory.slipperWalls(ghost3));
		ghost3.setMultiFaced(true);
		ghost3.setJumpingSound(contjump3);
		ghost3.removeTileEvent(null);
		ghost3.ghostify(replay3);
		ghost3.addTileEvent(new TileEvent()
		{	
			boolean used = false;
			
			@Override
			public void eventHandling(byte tileType) 
			{
				if(!used && tileType == Engine.GOAL)
				{
					used = true;
					
					String pos = game.getGlobalState() == GameState.FINISH ? "2nd" : "1st";
					add(Factory.printText(pos + " place goes to Blackie!", Color.BLACK, null, 150, gm, -150, -150, null));
				}
			}
		});
		add(Factory.soundFalloff(contjump3, ghost3, gm, 500, 0, 20,1));
		add(ghost1,ghost2,ghost3);
		
		/*
		 * One-Way clouds
		 */
		OneWay ow1 = new OneWay(3267, 323, Direction.N, gm, ghost1, ghost2, ghost3);
		ow1.setImage(clouds);
		
		OneWay ow2 = new OneWay(3147, 323, Direction.N, gm, ghost1, ghost2, ghost3);
		ow2.setImage(singlecloud);
		
		OneWay ow3 = new OneWay(2977, 323, Direction.N, gm, ghost1, ghost2, ghost3);
		ow3.setImage(twinclouds);
		
		OneWay ow4 = new OneWay(2797, 323, Direction.N, gm, ghost1, ghost2, ghost3);
		ow4.setImage(singlecloud);
		
		OneWay ow5 = new OneWay(2412, 323, Direction.N, gm, ghost1, ghost2, ghost3);
		ow5.setImage(clouds);
		
		OneWay ow6 = new OneWay(2282, 323, Direction.N, gm, ghost1, ghost2, ghost3);
		ow6.setImage(singlecloud);
		
		OneWay ow7 = new OneWay(2182, 323, Direction.N, gm, ghost1, ghost2, ghost3);
		ow7.setImage(singlecloud);
		
		OneWay ow8 = new OneWay(2090, 323, Direction.N, gm, ghost1, ghost2, ghost3);
		ow8.setImage(singlecloud);
		
		OneWay ow9 = new OneWay(1702, 323, Direction.N, gm, ghost1, ghost2, ghost3);
		ow9.setImage(clouds);
		
		OneWay ow10 = new OneWay(1402, 423, Direction.N, gm, ghost1, ghost2, ghost3);
		ow10.setImage(clouds);
		
		OneWay ow11 = new OneWay(1012, 423, Direction.N, gm, ghost1, ghost2, ghost3);
		ow11.setImage(clouds);

		add(ow1,ow2,ow3,ow4,ow5,ow6,ow7,ow8,ow9,ow10,ow11);
		
		/*
		 * Blowing cloud and wind
		 */
		GameObject cl = new GameObject();
		cl.setImage(windmaker);
		cl.addEvent(Factory.wobble(cl, -2, 2, -2, 2, 10));
		cl.currX = 1432;
		cl.currY = 129;
		
		Wind thewind = new Wind(1627, 155, Direction.SE, 7, 7, gm, ghost1, ghost2, ghost3);
		thewind.setHitbox(Hitbox.EXACT);
		thewind.setImage(3, wind);
		thewind.zIndex(200);
		
		add(cl, thewind, Factory.soundFalloff(blow, gm, thewind.currX + thewind.width / 2, thewind.currY + thewind.height / 2, 1000, 0, 50,1));
		
		/*
		 * Bouncer
		 */
		MovableObject dummy = new MovableObject();
		dummy.moveTo(3391,632);
		
		Bouncer b1 = new Bouncer(3661, 790, 400, 1, Direction.N, gm, ghost1, ghost2, ghost3);
		b1.setShakeSound(bouncesound,5);
		b1.setImage(bounce);
		b1.addEvent(Factory.soundFalloff(bouncesound, gm, dummy, 1000, 5, 100,1));
		
		add(b1,b1.getClone(3321, 742),b1.getClone(3481, 552),b1.getClone(3141, 612));
		
		/*
		 * Flag(goal)
		 */
		GameObject winflag = new GameObject();
		winflag.setImage(4, flag);
		winflag.currX = 782;
		winflag.currY = 499;
		
		add(winflag);
	}
	
	@Override
	public void dispose() 
	{
		super.dispose();
		disposeBatch(bounce, cont1, cont2, cont3,windmaker, clouds, singlecloud, twinclouds, wind, flag,bouncesound, contjump1, contjump2, contjump3,blow);
	}
}