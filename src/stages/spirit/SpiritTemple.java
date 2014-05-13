package stages.spirit;

import game.core.Engine;
import game.core.Engine.Direction;
import game.core.EntityStuff;
import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.GameObject.Hitbox;
import game.core.MainCharacter.CharacterState;
import game.core.MovableObject;
import game.core.MovableObject.TileEvent;
import game.core.Stage;
import game.essentials.Controller;
import game.essentials.Factory;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import game.essentials.Utilities;
import game.mains.GravityMan;
import game.movable.HorizontalDrone;
import game.movable.PathDrone;
import game.movable.PushableObject;
import game.movable.SolidPlatform;
import game.movable.Thwump;
import game.objects.Particle;
import java.io.File;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

@Playable(name="Spirit Temple", description="Stage: Spirit Temple\nAuthor: Pojahn Moradi\nDifficulty: 6\nAverage time: 200 sec\nProfessional time: 150 sec\nObjective: Enter goal.")
public class SpiritTemple extends Stage
{
	private Pixmap stageImage;
	private Image2D backgroundImg, foregroundImg, deathImg[], mainImage[], spikeImg, blockyImg[], boardImg, board2Img, ghostImg[], carpetImg[], coinImg[], silverbImg, sgloveImg, bsImage, bspImage, gstoneImg, ggloveImg, efist1, efist2, nfist1, nfist2, sfist1, sfist2, quImg, quaImg, rockImg;
	private Sound jump, pushed,  gMove, sImp, gImp, fslam, collect, steel, steel2, steel3, steel4, steel5, steel6, steel7, steel8, steel9, steel10;
	private GravityMan gm;
	private Ghost ghost;
	private boolean angry, muted;

	@Override
	public void init() 
	{
		try
		{
			mainImage   = Image2D.loadImages(new File("res/general/main"),true);
			deathImg   	= Image2D.loadImages(new File("res/general/main/death"));
			coinImg     = Image2D.loadImages(new File("res/general/starcoin"));
			blockyImg   = Image2D.loadImages(new File("res/sand/blocky"));
			ghostImg    = Image2D.loadImages(new File("res/sand/ghost"));
			carpetImg   = Image2D.loadImages(new File("res/sand/carpet"));
			spikeImg    = new Image2D("res/sand/spike.png");
			boardImg    = new Image2D("res/sand/board.png");
			board2Img   = new Image2D("res/sand/board2.png");
			silverbImg  = new Image2D("res/sand/silverblock.png");
			sgloveImg   = new Image2D("res/sand/silverglove.png");
			gstoneImg   = new Image2D("res/sand/bigblock.png");
			ggloveImg   = new Image2D("res/sand/gglove.png");
			efist1		= new Image2D("res/sand/efist1.png");
			efist2		= new Image2D("res/sand/efist2.png");
			sfist1		= new Image2D("res/sand/sfist1.png");
			sfist2		= new Image2D("res/sand/sfist2.png");
			nfist1		= new Image2D("res/sand/nfist1.png");
			nfist2		= new Image2D("res/sand/nfist2.png");
			quImg		= new Image2D("res/sand/quaker.png");
			quaImg		= new Image2D("res/sand/quakerangry.png");
			rockImg		= new Image2D("res/sand/rock.png");
			bsImage     = new Image2D("res/awfulplace/bs.png");
			bspImage    = new Image2D("res/awfulplace/bspressed.png");

			backgroundImg = new Image2D("res/sand/background.png");
			foregroundImg = new Image2D("res/sand/foreground.png");
			
			stageImage        = new Pixmap(new FileHandle("res/sand/map.png"));
			
			collect	 = TinySound.loadSound(new File(("res/general/collect1.wav")));
			jump     = TinySound.loadSound(new File(("res/general/jump.wav")));
			pushed   = TinySound.loadSound(new File(("res/flyingb/pushed.wav")));
			gMove	 = TinySound.loadSound(new File(("res/sand/alienmove.wav")));
			sImp	 = TinySound.loadSound(new File(("res/sand/silverimpact.wav")));
			gImp	 = TinySound.loadSound(new File(("res/sand/goldimpact.wav")));
			fslam	 = TinySound.loadSound(new File(("res/sand/fistslam.wav")));
			steel    = TinySound.loadSound(new File(("res/sand/steelCollide.wav")));
			steel2   = TinySound.loadSound(new File(("res/sand/steelCollide.wav")));
			steel3   = TinySound.loadSound(new File(("res/sand/steelCollide.wav")));
			steel4   = TinySound.loadSound(new File(("res/sand/steelCollide.wav")));
			steel5   = TinySound.loadSound(new File(("res/sand/steelCollide.wav")));
			steel6   = TinySound.loadSound(new File(("res/sand/steelCollide.wav")));
			steel7   = TinySound.loadSound(new File(("res/sand/steelCollide.wav")));
			steel8   = TinySound.loadSound(new File(("res/sand/steelCollide.wav")));
			steel9   = TinySound.loadSound(new File(("res/sand/steelCollide.wav")));
			steel10  = TinySound.loadSound(new File(("res/sand/steelCollide.wav")));

			MUSIC_VOLUME = 0.8f;
			setStageMusic("res/sand/song.ogg", 62.09);
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
		/*
		 * Standard stuff
		 *******************************************
		 */
		super.build();

		stageData = Utilities.createStageData(stageImage);
		game.deathTextColor = Color.WHITE;
		game.timeColor = Color.WHITE;
		
		background(RenderOption.FULL, backgroundImg);
		foreground(RenderOption.FULL, foregroundImg);
		
		basicInits();
		visibleWidth = 800;
		visibleHeight = 600;
		game.zoom = 1;
		game.angle = 0;
		muted = true;
		steel.setVolume(0);
		steel2.setVolume(0);
		steel3.setVolume(0);
		steel4.setVolume(0);
		steel5.setVolume(0);
		steel6.setVolume(0);
		steel7.setVolume(0);
		steel8.setVolume(0);
		steel9.setVolume(0);
		steel10.setVolume(0);
		
		/*
		 * Main Character
		 *******************************************
		 */
		gm = new GravityMan();
		gm.setImage(new Frequency<>(3, mainImage));
		gm.setMultiFaced(true);
		gm.setController((Controller)Utilities.importObject("res/data/controller1.con"));
		gm.hit(3);
		gm.setJumpingSound(jump);
		gm.moveTo(startX, startY);
		gm.addTileEvent(Factory.slipperWalls(gm));
		gm.zIndex(50);
		gm.deathImg = new Particle();
		gm.deathImg.setImage(4,deathImg);
		gm.deathImg.zIndex(101);
		game.setFocusObject(gm);
		add(gm);
		
		/*
		 * Spikes
		 *******************************************
		 */
		final float height = spikeImg.getHeight() * 2;

		HorizontalDrone spike1 = new HorizontalDrone(2142, 1020);
		spike1.setMoveSpeed(2);
		spike1.setHitbox(Hitbox.EXACT);
		spike1.setImage(new Frequency<>(1, spikeImg));
		spike1.addEvent(Factory.hitMain(spike1, gm, -1));
		spike1.addEvent(Factory.soundFalloff(steel, spike1, gm, 700, 3, 10,1));
		
		HorizontalDrone spike2 = spike1.getClone(2142, spike1.currY + height);
		spike2.addEvent(Factory.hitMain(spike2, gm, -1));
		spike2.addEvent(Factory.soundFalloff(steel2, spike2, gm, 700, 3, 10,1));
		
		HorizontalDrone spike3 = spike2.getClone(2142, spike2.currY + height + 35);
		spike3.addEvent(Factory.hitMain(spike3, gm, -1));
		spike3.addEvent(Factory.soundFalloff(steel3, spike3, gm, 700, 3, 10,1));
		
		HorizontalDrone spike4 = spike3.getClone(2142, spike3.currY + height);
		spike4.addEvent(Factory.hitMain(spike4, gm, -1));
		spike4.addEvent(Factory.soundFalloff(steel4, spike4, gm, 700, 3, 10,1));
		
		HorizontalDrone spike5 = spike4.getClone(2142, spike4.currY + height);
		spike5.addEvent(Factory.hitMain(spike5, gm, -1));
		spike5.addEvent(Factory.soundFalloff(steel5, spike5, gm, 700, 3, 10,1));

		HorizontalDrone spike6 = spike5.getClone(2142, spike5.currY + height);
		spike6.addEvent(Factory.hitMain(spike6, gm, -1));
		spike6.addEvent(Factory.soundFalloff(steel6, spike6, gm, 700, 3, 10,1));
		
		HorizontalDrone spike7 = spike1.getClone(2374, 1100);
		spike7.addEvent(Factory.hitMain(spike7, gm, -1));
		spike7.addEvent(Factory.soundFalloff(steel7, spike7, gm, 700, 3, 10,1));
		
		HorizontalDrone spike8 = spike7.getClone(2374, spike7.currY + height + 193);
		spike8.addEvent(Factory.hitMain(spike8, gm, -1));
		spike8.addEvent(Factory.soundFalloff(steel8, spike8, gm, 700, 3, 10,1));
		
		HorizontalDrone spike9 = spike8.getClone(2374, spike8.currY + height);
		spike9.addEvent(Factory.hitMain(spike9, gm, -1));
		spike9.addEvent(Factory.soundFalloff(steel9, spike9, gm, 700, 3, 10,1));
		
		HorizontalDrone spike10 = spike9.getClone(2374, spike9.currY + height);
		spike10.addEvent(Factory.hitMain(spike10, gm, -1));
		spike10.addEvent(Factory.soundFalloff(steel10, spike10, gm, 700, 3, 10,1));

		spike1.setCrashSound(steel);
		spike2.setCrashSound(steel2);
		spike3.setCrashSound(steel3);
		spike4.setCrashSound(steel4);
		spike5.setCrashSound(steel5);
		spike6.setCrashSound(steel6);
		spike7.setCrashSound(steel7);
		spike8.setCrashSound(steel8);
		spike9.setCrashSound(steel9);
		spike10.setCrashSound(steel10);
		
		add(spike1, spike2, spike3, spike4, spike5, spike6, spike7, spike8, spike9, spike10);
		
		/*
		 * Blocky
		 *******************************************
		 */
		Frequency<Image2D> blockyImage = new Frequency<>(6, blockyImg);
		blockyImage.setMultiFaced(true);
		
		SolidPlatform blocky1 = new SolidPlatform(1589, 257, gm);
		blocky1.setStrictGlueMode(true);
		blocky1.setMoveSpeed(1);
		blocky1.setImage(blockyImage);
		blocky1.appendPath(1624, 257, 0, false, null);
		blocky1.appendPath(1624, 371, 0, false, null);
		blocky1.appendPath(1564, 371, 0, false, null);
		blocky1.appendPath(1564, 257, 0, false, null);
		
		SolidPlatform blocky2 = new SolidPlatform(1934, 270, gm);
		blocky2.setStrictGlueMode(true);
		blocky2.setMoveSpeed(1);
		blocky2.setImage(blockyImage);
		blocky2.appendPath(2029, 270, 0, false, null);
		blocky2.appendPath(2029, 359, 0, false, null);
		blocky2.appendPath(1734, 359, 0, false, null);
		blocky2.appendPath(1734, 270, 0, false, null);
		
		add(blocky1, blocky2, blocky1.getClone(1589, 20));
		
		/*
		 * Board, ghost and flying carpet
		 *******************************************
		 */
		final MovableObject board = new MovableObject();
		board.currX = 106;
		board.currY = 853;
		board.setImage(new Frequency<>(1, boardImg));
		gm.avoidOverlapping(board);
		
		SolidPlatform carpet = new SolidPlatform(-100, 821, gm);
		carpet.setImage(new Frequency<>(5, carpetImg));
		carpet.setStrictGlueMode(true);
		carpet.setHarshResponse(false);
		carpet.offsetY = -10;
		carpet.appendPath(412, 821, 50, false, null);
		carpet.appendPath(412, 120, 50, false, null);
		
		ghost = new Ghost(1728, 1996, gm, carpet);
		ghost.setImage(new Frequency<>(7, ghostImg));
		ghost.facing = Direction.E;
		ghost.setMultiFaced(true);
		ghost.appendPath(1118, 1996, 0, false, null);
		ghost.appendPath(1118, 1672, 0, false, null);
		ghost.appendPath(988, 1672, 0, false, null);
		ghost.appendPath(988, 1962, 0, false, null);
		ghost.appendPath(758, 1962, 0, false, null);
		ghost.appendPath(172, 1658, 0, false, ()->ghost.setMoveSpeed(3));
		ghost.appendPath(162, 768, 0, false, null);
		ghost.appendPath(512, 768, 0, false, ()->
		{
			ghost.facing = Direction.W;
			ghost.reachedDest = true;
			ghost.setMoveSpeed(0);
		});
		ghost.addEvent(new Event()
		{	
			SoundBank bank;
			
			{
				bank = new SoundBank(1);
				bank.setSound(0, gMove);
				bank.setDelay(0, 80);
				ghost.resetPrevs();
			}
			
			@Override
			public void eventHandling()
			{
				ghost.collisionRespone(board);
				if(ghost.isMoving())
					bank.playSound(0);
			}
		});
		
		add(board, ghost);
		
		/*
		 * Cave board, button, silver glove and silver stones
		 *******************************************
		 */
		final MovableObject board2 = new MovableObject();
		board2.currX = 236;
		board2.currY = 1272;
		board2.setImage(new Frequency<>(1, board2Img));
		gm.avoidOverlapping(board2);
		
		final PushableObject silver1 = new PushableObject(988, 741, gm);
		silver1.setImage(new Frequency<>(1, silverbImg));
		silver1.setPushLength(0);
		silver1.mustStand(true);
		silver1.avoidOverlapping(board);
		silver1.setPushingSound(pushed, 10);
		silver1.setSlamingSound(sImp);
		silver1.resetPrevs();
		
		final PushableObject silver2 = silver1.getClone(2019, 1272);
		silver2.resetPrevs();
		
		final GameObject silverGlove = new GameObject();
		silverGlove.setImage(new Frequency<>(1, sgloveImg));
		silverGlove.currX = 554;
		silverGlove.currY = 122;
		silverGlove.addEvent(new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(gm.collidesWith(silverGlove))
				{
					collect.play();
					discard(silverGlove);
					silver1.setPushLength(1);
					silver2.setPushLength(1);
				}
			}
		});
		
		final Frequency<Image2D> nb = new Frequency<>(1, bsImage);
		final Frequency<Image2D> pb = new Frequency<>(1, bspImage);
		
		final GameObject button = new GameObject();
		button.currX = 1784;
		button.currY = 956;
		button.setImage(nb);
		button.addEvent(()->
		{	
			if(button.collidesWith(gm))
				button.setImage(pb);
			else
				button.setImage(nb);
			
			if(button.collidesWith(silver1))
			{
				discard(button);
				board2.currX = -100;
			}
		});
		
		add(button, board2, silver1, silver2, silverGlove);
		
		/*
		 * Golden glove, gold rock
		 *******************************************
		 */
		final PushableObject goldStone = new PushableObject(2851, 33, gm);
		goldStone.setImage(new Frequency<>(1, gstoneImg));
		goldStone.setPushLength(0);
		goldStone.mustStand(true);
		goldStone.width--;
		goldStone.setPushingSound(pushed, 10);
		goldStone.setSlamingSound(gImp);
		goldStone.resetPrevs();
		
		final GameObject gglove = new GameObject();
		gglove.setImage(new Frequency<>(1, ggloveImg));
		gglove.currX = 1706;
		gglove.currY = 1342;
		gglove.addEvent(()->
		{	
			if(gm.collidesWith(gglove))
			{
				collect.play();
				discard(gglove);
				goldStone.setPushLength(1);
				silver1.setPushLength(2);
				silver2.setPushLength(2);
			}
		});
		
		add(goldStone, gglove);
		
		/*
		 * Ancient fists
		 *******************************************
		 */
		addFist(1134, 1931, Direction.E);
		addFist(1134, 1845, Direction.E);
		addFist(1134, 1761, Direction.E);
		addFist(975,  1965, Direction.N);
		addFist(651,  1707, Direction.S);
		addFist(571,  1965, Direction.N);
		
		/*
		 * Quaker, falling pieces
		 *******************************************
		 */
		
		
		final PathDrone debris = new PathDrone(529, 1098);
		debris.useFastCollisionCheck(true);
		debris.setHitbox(Hitbox.CIRCLE);
		debris.setImage(new Frequency<>(1, rockImg));
		debris.freeze();
		debris.setMoveSpeed(6);
		debris.addEvent(()->debris.rotation += 6);
		debris.addEvent(Factory.hitMain(debris, gm, -1));
		debris.appendPath(529, 1098, 40,  true, null);
		debris.appendPath(547, 1624, 0, false, null);
		
		final PathDrone debris2 = debris.getClone(680, 1098);
		debris2.clearData();
		debris2.addEvent(()->debris2.rotation += 6);
		debris2.addEvent(Factory.hitMain(debris2, gm, -1));
		debris2.appendPath(680, 1098, 60,  true, null);
		debris2.appendPath(690, 1624, 0, false, null);
		
		final PathDrone debris3 = debris.getClone(780, 1098);
		debris3.clearData();
		debris3.addEvent(()->debris3.rotation += 6);
		debris3.addEvent(Factory.hitMain(debris3, gm, -1));
		debris3.appendPath(780, 1098, 0,  true, null);
		debris3.appendPath(870, 1624, 0,  false, null);
		
		final PathDrone debris4 = debris.getClone(900, 1098);
		debris4.clearData();
		debris4.addEvent(()->debris4.rotation += 6);
		debris4.addEvent(Factory.hitMain(debris4, gm, -1));
		debris4.appendPath(900, 1098, 10,  true, null);
		debris4.appendPath(960, 1624, 0,  false, null);
		
		final PathDrone debris5 = debris.getClone(1100, 1098);
		debris5.clearData();
		debris5.addEvent(()->debris5.rotation += 6);
		debris5.addEvent(Factory.hitMain(debris5, gm, -1));
		debris5.appendPath(1100, 1098,  0,  true, null);
		debris5.appendPath(1200, 1624,  0,  false, null);
		
		final PathDrone debris6 = debris.getClone(1250, 1098);
		debris6.clearData();
		debris6.addEvent(()->debris6.rotation += 6);
		debris6.addEvent(Factory.hitMain(debris6, gm, -1));
		debris6.appendPath(1250, 1098,  40,  true, null);
		debris6.appendPath(1250, 1624,  0,  false, null);
		
		final PathDrone debris7 = debris.getClone(1350, 1098);
		debris7.clearData();
		debris7.addEvent(()->debris7.rotation += 6);
		debris7.addEvent(Factory.hitMain(debris7, gm, -1));
		debris7.appendPath(1350, 1098,  0,  true, null);
		debris7.appendPath(1490, 1624,  0,  false, null);
		
		final GameObject quaker = new GameObject();
		quaker.currX = 847;
		quaker.currY = 1162;
		quaker.setImage(new Frequency<>(1, quImg));
		quaker.addEvent(new Event() 
		{
			@Override
			public void eventHandling() 
			{
				if(!angry && EntityStuff.checkLine(quaker.currX, quaker.currY, quaker.currX, 1640, gm))
				{
					quaker.setImage(new Frequency<>(1, quaImg));
					game.setFocusObject(quaker);
					gm.freeze();
					
					add(Factory.printText("May you be crushed to death!", null, null, 200, new PathDrone(quaker.currX - 60, quaker.currY + 90),0,0, null));
					add(Factory.printText("How dare you disrupt the peace?", null, null, 200, new PathDrone(quaker.currX - 60, quaker.currY + 60),0,0, new Event()
					{
						@Override
						public void eventHandling() 
						{
							game.setFocusObject(gm);
							gm.unfreeze();
							debris.unfreeze();
							debris2.unfreeze();
							debris3.unfreeze();
							debris4.unfreeze();
							debris5.unfreeze();
							debris6.unfreeze();
							debris7.unfreeze();
							angry = true;
						}
					}));
				}
			}
		});
		
		add(quaker);
		add(debris, debris2, debris3, debris4, debris5, debris6, debris7);
		
		/*
		 * Final touch ups
		 *******************************************
		 */
		GameObject coin = new GameObject();
		coin.setImage(new Frequency<>(7, coinImg));
		coin.currX = 2993;
		coin.currY = 1520;
		add(coin);
		
		gm.addTileEvent(new TileEvent()
		{	
			boolean increase = true;
			
			@Override
			public void eventHandling(byte tileType) 
			{				
				if(tileType == Engine.AREA_TRIGGER_9)
				{
					gm.setVisible(false);
					gm.setState(CharacterState.DEAD);
				}
				
				if(angry && tileType == Engine.AREA_TRIGGER_2)
				{
					if(increase)
					{
						game.zoom += 0.003f;
						game.angle += 0.5f;
						if(game.angle > 15)
							increase = false;
					}
					else
					{
						game.zoom -= 0.003f;
						game.angle -= 0.5f;
						if(game.angle < -15)
							increase = true;
					}
				}
				else if(tileType == Engine.AREA_TRIGGER_3)
				{
					game.zoom = 1;
					game.angle = 0;
				}
			}
		});
	}

	@Override
	public void extra() 
	{
		boolean close = EntityStuff.distance(gm.currX, gm.currY, 2142, 1695) < 1550;
		
		if(muted && close)
		{
			steel.setVolume(.5f);
			muted = false;
		}
		else if(!muted && !close)
		{
			steel.setVolume(0);
			muted = true;
		}
	}
	
	@SuppressWarnings("deprecation")
	void addFist(float x, float y, Direction dir)
	{
		Thwump fist = new Thwump(x, y, gm);
		fist.setAttackSpeed(3);
		fist.setRecovery(50);
		fist.setReturnSpeed(2);
		fist.setSmashingSound(fslam);

		SolidPlatform arm  = new SolidPlatform(0,0, gm);
		float offsetX = 0, offsetY = 0;
		
		switch(dir)
		{
		case E:
			offsetX = -36;
			offsetY = 17;
			fist.setFallingDirection(Direction.E);
			fist.setImage(new  Frequency<>(1, efist1));
			arm.setImage(new Frequency<>(1, efist2));
			break;
		case N:
			offsetX = 17;
			offsetY = 74;
			fist.setFallingDirection(Direction.N);
			fist.addEvent(Factory.tileDeformer(fist, Engine.AREA_TRIGGER_9, true));
			fist.setImage(new  Frequency<>(1, nfist1));
			arm.setImage(new Frequency<>(1, nfist2));
			break;
		case S:
			offsetX = 17;
			offsetY = -35;
			fist.setAttackSpeed(4);
			fist.setFallingDirection(Direction.S);
			fist.setImage(new  Frequency<>(1, sfist1));
			arm.setImage(new Frequency<>(1, sfist2));
			break;
		default:
			throw new IllegalStateException("Fist can either N,S or E");
		}
		
		arm.followTarget(fist, offsetX, offsetY);
		add(fist, arm);
	}

	@Override
	public void dispose() 
	{
		disposeBatch(stageImage, backgroundImg, foregroundImg, deathImg, mainImage, spikeImg, blockyImg, boardImg, board2Img, ghostImg, carpetImg, coinImg, silverbImg, sgloveImg, bsImage, bspImage, gstoneImg, ggloveImg, efist1, efist2, nfist1, nfist2, sfist1, sfist2, quImg, quaImg, rockImg, jump, pushed,  gMove, sImp, gImp, fslam, collect, steel, steel2, steel3, steel4, steel5, steel6, steel7, steel8, steel9, steel10);
	}
}