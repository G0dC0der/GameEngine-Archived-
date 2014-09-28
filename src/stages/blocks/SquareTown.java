package stages.blocks;

import static com.badlogic.gdx.graphics.Color.*;
import game.core.Engine;
import game.core.Engine.Direction;
import game.core.GameObject;
import game.core.GameObject.Hitbox;
import game.core.MainCharacter.CharacterState;
import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.AutoLoad;
import game.development.StageBuilder;
import game.essentials.Factory;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.essentials.Utilities;
import game.movable.PathDrone;
import game.movable.SolidPlatform;
import game.objects.OneWay;
import java.io.File;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import ui.accessories.Playable;
import com.badlogic.gdx.graphics.Color;

@AutoDispose
@AutoInstall(mainPath="res/general", path="res/blocks")
@Playable(name="Square Town", description="Stage: Square Town\nAuthor: Pojahn Moradi\nAverage time: 150 sec\nProfessional time: 100 sec\nObjective: Enter the goal.")
public class SquareTown extends StageBuilder
{
	{
		setDifficulty(Difficulty.NORMAL);
	}
	
	@AutoLoad(path="res/blocks", type=VisualType.IMAGE)
	private Image2D backgroundImg, spikeyImg[], spikeyminiImg[], platformImg, crabImg[], gemImg[], yellowBlockImg, redBlockImg, eyeImg[], blockadeImg, gem2Img[], spikesnImg, spikeseImg, spikeswImg, steelBlockImg, flagPoleImg;
	private Image2D flagImg[]; 
	
	private Sound collect1, collect2;
	private Music crabMove1, crabMove2;
	
	private int collectedGems, gems, collectedGems2, gems2;
	
	@Override
	public void init() 
	{
		try
		{
			super.init();
			
			flagImg = Image2D.loadImages(new File("res/climb/flag"), false);
			
			collect1 = Utilities.loadSound("res/general/collect1.wav");
			collect2 = Utilities.loadSound("res/general/collect2.wav");
			crabMove1 = Utilities.loadMusic("res/blocks/crabMoveLoop.wav");
			crabMove2 = Utilities.loadMusic("res/blocks/crabMoveLoop.wav");
			
			game.timeColor = Color.WHITE;
			lethalDamage = -5;
			
			crabMove1.setVolume(0);
			crabMove1.play(true);
			crabMove2.setVolume(0);
			crabMove2.play(true);
			
			setStageMusic("res/blocks/song.ogg",29.57f, .65f);
		}
		catch(Exception e)
		{
			System.err.println("Failed to load resources.");
			e.printStackTrace();
			dispose();
			System.exit(-1);
		}
	}
	
	@Override
	public void build() 
	{
		/*
		 * Basics
		 */
		super.build();
		collectedGems = gems = collectedGems2 = gems2 = 0;
		background(RenderOption.FIXED, backgroundImg);
		if(getDifficulty() != Difficulty.HARD)
			gm.hit(2);
		gm.zIndex(50);
		gm.addTileEvent(Factory.slipperWalls(gm));
		gm.addTileEvent((tileType)->{
			if(tileType == Engine.LETHAL)
				gm.setState(CharacterState.DEAD);
		});
		
		/*
		 * Spikey
		 */
		PathDrone spikey = new PathDrone(240, 1202);
		spikey.appendPath(spikey.currX, spikey.currY);
		spikey.appendPath(1020, spikey.currY);
		spikey.appendPath(1020, 1501);
		spikey.appendPath(240, 1501);
		spikey.addEvent(Factory.hitMain(spikey, gm, -1));
		spikey.setImage(4, spikeyImg);
		spikey.setHitbox(Hitbox.EXACT);
		spikey.setMoveSpeed(getSpikeySpeed());
		add(spikey);
		
		/*
		 * Platforms and gems
		 */
		OneWay ow1 = new OneWay(360, 1171, Direction.N, gm);
		ow1.setImage(platformImg);
		
		OneWay ow2 = new OneWay(854, 1171, Direction.N, gm);
		ow2.setImage(platformImg);
		
		add(ow1,ow2);
		
		add(getGem(405,1116),
			getGem(899,1116),
			getGem(652,1176),
			getGem(552,1176),
			getGem(752,1176));
		
		/*
		 * Crabs
		 */
		PathDrone crab1 = new PathDrone(1181, 1302);
		crab1.appendPath(crab1.currX, crab1.currY);
		crab1.appendPath(crab1.currX, 792);
		crab1.setImage(getCrabAnimationSpeed(), crabImg);
		crab1.setMoveSpeed(getCrabSpeed());
		crab1.setHitbox(Hitbox.EXACT);
		crab1.addEvent(Factory.hitMain(crab1, gm, -1));
		crab1.addEvent(Factory.soundFalloff(crabMove1, crab1, gm, 400, 1, 25, 1));
		
		PathDrone crab2 = new PathDrone(1181, 182);
		crab2.appendPath(crab2.currX, crab2.currY);
		crab2.appendPath(crab2.currX, 432);
		crab2.setImage(getCrabAnimationSpeed(), crabImg);
		crab2.setMoveSpeed(getCrabSpeed());
		crab2.setHitbox(Hitbox.EXACT);
		crab2.addEvent(Factory.hitMain(crab2, gm, -1));
		crab2.addEvent(Factory.soundFalloff(crabMove2, crab2, gm, 400, 1, 25, 1));
		
		add(crab1, crab2);
		
		/*
		 * Top Block
		 */
		final SolidPlatform topBlock = getBlock(1620, 180, YELLOW);
		topBlock.appendPath(1620, 300);
		add(topBlock);
		
		/*
		 * Crushing Block Wall
		 */
		float xs = 1200, ys = 480, xe = 1620;
		
		final SolidPlatform w1 = getBlock(xs, ys, YELLOW);
		w1.appendPath(xe, ys);
		w1.setMoveSpeed(getWallSpeed());
		w1.setTileDeformer(false, Engine.HOLLOW);
		ys += w1.height;
		
		final SolidPlatform w2 = getBlock(xs, ys, RED);
		w2.appendPath(xe, ys);
		w2.setTileDeformer(false, Engine.HOLLOW);
		w2.setMoveSpeed(getWallSpeed());
		ys += w1.height;
		
		final SolidPlatform w3 = getBlock(xs, ys, YELLOW);
		w3.appendPath(xe, ys);
		w3.setTileDeformer(false, Engine.HOLLOW);
		w3.setMoveSpeed(getWallSpeed());
		ys += w1.height;
		
		final SolidPlatform w4 = getBlock(xs, ys, RED);
		w4.appendPath(xe, ys);
		w4.setTileDeformer(false, Engine.HOLLOW);
		w4.setMoveSpeed(getWallSpeed());
		ys += w1.height;
		
		final SolidPlatform w5 = getBlock(xs, ys, YELLOW);
		w5.appendPath(xe, ys);
		w5.setTileDeformer(false, Engine.HOLLOW);
		w5.setMoveSpeed(getWallSpeed());
		ys += w1.height;
		
		add(w1,w2,w3,w4,w5);
		
		/*
		 * Second batch of gems and blockade block.
		 */
		add(getGem2(1300, 700),
			getGem2(1400, 588),
			getGem2(1541, 665),
			getGem2(1656, 490));
		
		final SolidPlatform embargo = new SolidPlatform(xe, ys, gm);
		embargo.setImage(blockadeImg);
		embargo.appendPath(xe + embargo.width, ys, 0, false, ()->{discard(embargo);});
		embargo.freeze();
		embargo.addEvent(()->{
			if(gems2 == collectedGems2)
				embargo.unfreeze();
		});
		add(embargo);
		
		/*
		 * Rectangular moving blocks
		 */
		if(getDifficulty() != Difficulty.HARD)
		{
			//Upper right
			SolidPlatform rec1 = getBlock(1620, 840, RED);
			rec1.appendPath(1620, 840);
			rec1.appendPath(1500, 840);
			rec1.appendPath(1500, 960);
			rec1.appendPath(1620, 960);
			rec1.setMoveSpeed(2);
			rec1.unfreeze();
			
			//Upper left
			SolidPlatform rec2 = getBlock(1260, 840, RED);
			rec2.appendPath(1260, 840);
			rec2.appendPath(1380, 840);
			rec2.appendPath(1380, 960);
			rec2.appendPath(1260, 960);
			rec2.setMoveSpeed(2);
			rec2.unfreeze();
			
			//Lower left
			SolidPlatform rec3 = getBlock(1380, 960, RED);
			rec3.appendPath(1380, 960);
			rec3.appendPath(1260, 960);
			rec3.appendPath(1260, 1080);
			rec3.appendPath(1380, 1080);
			rec3.setMoveSpeed(2);
			rec3.unfreeze();
			
			SolidPlatform rec4 = getBlock(1500, 960, RED);
			rec4.appendPath(1500, 960);
			rec4.appendPath(1620, 960);
			rec4.appendPath(1620, 1080);
			rec4.appendPath(1500, 1080);
			rec4.setMoveSpeed(2);
			rec4.unfreeze();
			
			add(rec1, rec2, rec3, rec4);
		}
		else
		{
			//Upper right
			PathDrone rec1 = new PathDrone(1620, 840);
			rec1.appendPath(1620, 840);
			rec1.appendPath(1500, 840);
			rec1.appendPath(1500, 960);
			rec1.appendPath(1620, 960);
			rec1.setImage(4, spikeyImg);
			rec1.setHitbox(Hitbox.EXACT);
			rec1.addEvent(Factory.hitMain(rec1, gm, -1));
			rec1.setMoveSpeed(2);
			
			//Upper left
			PathDrone rec2 = rec1.getClone(1260, 840);
			rec2.clearData();
			rec2.addEvent(Factory.hitMain(rec2, gm, -1));
			rec2.appendPath(1260, 840);
			rec2.appendPath(1380, 840);
			rec2.appendPath(1380, 960);
			rec2.appendPath(1260, 960);
			
			//Lower left
			PathDrone rec3 = rec1.getClone(1380, 960);
			rec3.clearData();
			rec3.addEvent(Factory.hitMain(rec3, gm, -1));
			rec3.appendPath(1380, 960);
			rec3.appendPath(1260, 960);
			rec3.appendPath(1260, 1080);
			rec3.appendPath(1380, 1080);
			
			PathDrone rec4 = rec1.getClone(1500, 960);
			rec4.clearData();
			rec4.addEvent(Factory.hitMain(rec4, gm, -1));
			rec4.appendPath(1500, 960);
			rec4.appendPath(1620, 960);
			rec4.appendPath(1620, 1080);
			rec4.appendPath(1500, 1080);
			
			add(rec1, rec2, rec3, rec4);
		}
		
		/*
		 * Spikes
		 */
		GameObject spike1 = new GameObject();
		spike1.moveTo(1440, 1366);
		spike1.setHitbox(Hitbox.EXACT);
		spike1.setImage(spikesnImg);
		spike1.addEvent(Factory.hitMain(spike1, gm, -1));
		
		GameObject spike2 = spike1.getClone(1620, 1261);
		spike2.setImage(spikeseImg);
		spike2.addEvent(Factory.hitMain(spike2, gm, -1));
		
		GameObject spike3 = spike1.getClone(1666, 1381);
		spike3.setImage(spikeswImg);
		spike3.addEvent(Factory.hitMain(spike3, gm, -1));
		
		add(spike1, spike2, spike3);
		
		/*
		 * Block going up and down
		 */
		SolidPlatform bl = getBlock(1380, 1560, YELLOW);
		bl.appendPath(1380, 1440);
		bl.appendPath(1380, 1560, 0, true, null);
		bl.setMoveSpeed(1.8f);
		bl.unfreeze();
		
		add(bl);
		
		/*
		 * Two pushing blocks
		 */
		SolidPlatform push1 = getBlock(2520, 1860, RED);
		push1.appendPath(2520, 1860);
		push1.appendPath(1921, 1860);
		push1.appendPath(1921, 1980);
		push1.appendPath(2520, 1980, 0, true, null);
		push1.setMoveSpeed(getPushSpeed());
		push1.unfreeze();
		
		SolidPlatform push2 = getBlock(2520, 1800, YELLOW);
		push2.appendPath(2520, 1800);
		push2.appendPath(1921, 1800);
		push2.appendPath(1921, 1920);
		push2.appendPath(2520, 1920, 0, true, null);
		push2.setMoveSpeed(getPushSpeed());
		push2.unfreeze();
		
		add(push1, push2);
		
		if(getDifficulty() != Difficulty.EASY)
		{
			/*
			 * Metal Blocks
			 */
			SolidPlatform met1 = new SolidPlatform(2760, 1859, gm);
			met1.setImage(steelBlockImg);
			met1.setMoveSpeed(2);
			met1.setTileDeformer(true, Engine.SOLID);
			met1.setTransformBack(true);
			met1.appendPath(2760, 1859);
			met1.appendPath(2760, 1680);
			met1.appendPath(2820, 1680);
			met1.appendPath(2820, 1859, 100, true, null);
			
			SolidPlatform met2 = met1.getClone(2760, 1441);
			met2.clearData();
			met2.appendPath(2760, 1441);
			met2.appendPath(2760, 1620);
			met2.appendPath(2820, 1620);
			met2.appendPath(2820, 1441, 100, true, null);
			
			add(met1, met2);
			
			/*
			 * Up and down metal blocks
			 */
			float xStart = 2820, yBottom = 1380, yTop = 1380 - steelBlockImg.getHeight();
					
			for(int i = 0; i < 7; i++)
			{
				SolidPlatform solp = new SolidPlatform(0, 0, gm);
				solp.setImage(steelBlockImg);
				solp.setMoveSpeed(.5f);
				
				if(i % 2 == 0)
				{
					solp.moveTo(xStart, yBottom);
					solp.appendPath(xStart, yBottom);
					solp.appendPath(xStart, yTop);
				}
				else
				{
					solp.moveTo(xStart, yTop);
					solp.appendPath(xStart, yTop);
					solp.appendPath(xStart, yBottom);
				}
				add(solp);
				
				xStart += steelBlockImg.getWidth();
			}
		}
		
		/*
		 * Final Steel blocks
		 */
		SolidPlatform solp = new SolidPlatform(3000, 1080, gm);
		solp.setImage(steelBlockImg);
		solp.setMoveSpeed(1.5f);
		solp.setStrictGlueMode(true);
		solp.appendPath(3000, 1080, 80, false, null);
		solp.appendPath(3000, 1080 - (steelBlockImg.getHeight() * 2), 80, false, null);
		
		SolidPlatform solp2 = solp.getClone(2880, 900);
		solp2.clearData();
		solp2.appendPath(2880, 900, 80, false, null);
		solp2.appendPath(2880 - (steelBlockImg.getWidth() * 7), 900, 80, false, null);
		
		SolidPlatform solp3 = solp.getClone(2280, 780);
		solp3.setMoveSpeed(1);
		solp3.clearData();
		solp3.appendPath(2280, 780);
		solp3.appendPath(2340, 780);
		solp3.appendPath(2340, 840);
		solp3.appendPath(2280, 840);
		
		SolidPlatform solp4 = solp3.getClone(2340, 840);
		solp4.clearData();
		solp4.appendPath(2340, 840);
		solp4.appendPath(2280, 840);
		solp4.appendPath(2280, 780);
		solp4.appendPath(2340, 780);

		SolidPlatform solp5 = solp3.getClone(3120, 480);
		solp5.clearData();
		solp5.appendPath(3120, 480);
		solp5.appendPath(3120 + solp5.width, 480);
		
		SolidPlatform solp6 = solp3.getClone(3120 + solp5.width, 480 - (solp5.height * 3));
		solp6.clearData();
		solp6.appendPath(3120, 480 - (solp5.height * 3));
		solp6.appendPath(3120 + solp5.width, 480 - (solp5.height * 3));
		
		
		add(solp, solp2, solp3, solp4, solp5, solp6);
		
		/*
		 * Mini Spikeies
		 */
		PathDrone mspikey = new PathDrone(2489, 688);
		mspikey.setImage(5, spikeyminiImg);
		mspikey.setMoveSpeed(2);
		mspikey.setHitbox(Hitbox.EXACT);
		mspikey.addEvent(Factory.hitMain(mspikey, gm, -1));
		mspikey.appendPath(2489, 688);
		mspikey.appendPath(2580, 688);
		mspikey.appendPath(2580, 780);
		mspikey.appendPath(2489, 780);
		
		PathDrone smpikey2 = mspikey.getClone(2940, 780);
		smpikey2.clearData();
		smpikey2.addEvent(Factory.hitMain(smpikey2, gm, -1));
		smpikey2.appendPath(2940, 780);
		smpikey2.appendPath(2940, 688);
		smpikey2.appendPath(2850, 688);
		smpikey2.appendPath(2850, 780);
		
		add(mspikey, smpikey2);
		
		/*
		 * Flag
		 */
		GameObject flagPole = new GameObject();
		flagPole.setImage(flagPoleImg);
		flagPole.moveTo(2836, 139);
		
		GameObject flag = new GameObject();
		flag.setImage(4, flagImg);
		flag.moveTo(2840, 136);
		
		add(flagPole, flag);
		
		/*
		 * Finalizing
		 */
		gm.addTileEvent((tileType)->{
			if(collectedGems == gems && tileType == Engine.AREA_TRIGGER_2)
			{
				gems = -1;
				topBlock.unfreeze();
			}
			else if(tileType == Engine.AREA_TRIGGER_3)
			{
				w1.unfreeze();
				w2.unfreeze();
				w3.unfreeze();
				w4.unfreeze();
				w5.unfreeze();
			}
		});
	}
	
	SolidPlatform getBlock(float x, float y, Color color)
	{
		final Frequency<Image2D> eyes = new Frequency<>(6, eyeImg);
		eyes.stop(true);
		eyes.setLoop(false);
		
		final GameObject eyeObj = new GameObject();
		
		SolidPlatform block = new SolidPlatform(x, y, gm)
		{
			@Override
			public void unfreeze() 
			{
				super.unfreeze();
				eyes.stop(false);
			}
			
			@Override
			public void endUse() 
			{
				discard(eyeObj);
			}
		};
		block.setImage(color.equals(Color.RED) ? redBlockImg : yellowBlockImg);
		block.freeze();
		block.setMoveSpeed(1.5f);
		block.zIndex(60);
		block.setTileDeformer(true, Engine.SOLID);
		block.setTransformBack(true);
		
		eyeObj.setImage(eyes);
		eyeObj.addEvent(Factory.follow(block, eyeObj, 11, 10));
		eyeObj.zIndex(61);
		add(eyeObj);
		
		return block;
	}
	
	GameObject getGem(float x, float y)
	{
		gems++;
		
		final GameObject gem = new GameObject();
		gem.moveTo(x, y);
		gem.setImage(6, gemImg);
		gem.addEvent(()->{
			if(gem.collidesWith(gm))
			{
				collectedGems++;
				discard(gem);
				collect1.play();
			}
		});
		
		return gem;
	}
	
	GameObject getGem2(float x, float y)
	{
		gems2++;
		
		Frequency<Image2D> gemImage = new Frequency<>(4, gem2Img);
		gemImage.pingPong(true);
		
		final GameObject gem = new GameObject();
		gem.moveTo(x, y);
		gem.setImage(gemImage);
		gem.addEvent(()->{
			if(gem.collidesWith(gm))
			{
				collectedGems2++;
				discard(gem);
				collect2.play();
			}
		});
		
		return gem;
	}
	
	float getSpikeySpeed()
	{
		switch(getDifficulty())
		{
			case EASY:
				return 3.0f;
			case NORMAL:
				return 6.0f;
			case HARD:
				return 9.0f;
		}
		return 0f;
	}
	
	float getCrabSpeed()
	{
		switch(getDifficulty())
		{
			case EASY:
				return 1.0f;
			case NORMAL:
				return 2.0f;
			case HARD:
				return 3.0f;
		}
		return 0f;
	}
	
	int getCrabAnimationSpeed()
	{
		switch(getDifficulty())
		{
			case EASY:
				return 6;
			case NORMAL:
				return 4;
			case HARD:
				return 3;
		}
		return 0;
	}
	
	float getWallSpeed()
	{
		switch(getDifficulty())
		{
			case EASY:
				return .65f;
			case NORMAL:
				return .8f;
			case HARD:
				return .8f;
		}
		return 0;
	}
	
	float getPushSpeed()
		{
		switch(getDifficulty())
		{
			case EASY:
				return 3;
			case NORMAL:
				return 4;
			case HARD:
				return 5;
		}
		return 0;
	}
	
	@Override
	public void dispose() 
	{
		super.dispose();
	}
}
