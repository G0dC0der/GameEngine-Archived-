package stages.deathegg;

import game.core.Engine;
import game.core.Engine.Direction;
import game.core.Fundementals;
import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.GameObject.Hitbox;
import game.core.MainCharacter;
import game.core.MainCharacter.CharacterState;
import game.development.AutoDispose;
import game.development.AutoLoad;
import game.development.StageBuilder;
import game.essentials.Animation;
import game.essentials.BigImage;
import game.essentials.BigImage.RenderOption;
import game.essentials.CameraEffect;
import game.essentials.Controller;
import game.essentials.Factory;
import game.essentials.GFX;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import game.essentials.Utilities;
import game.mains.Flipper;
import game.movable.Dummy;
import game.movable.PathDrone;
import game.movable.PathDrone.PathData;
import game.movable.Projectile;
import game.movable.SimpleButton;
import game.movable.SimpleWeapon;
import game.movable.SolidPlatform;
import game.objects.CheckpointsHandler;
import game.objects.Flash;
import game.objects.OneWay;
import game.objects.Particle;

import java.io.File;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

@AutoDispose
@Playable(name="Death Egg", description="Author: Pojahn Moradi\nAverage time: 300 sec\nProfessional time: 250 sec\nObjective: Grab the crystal.")
public class DeathEgg extends StageBuilder
{
	static final String PATH = "res/deathegg";
	
	{
		setDifficulty(Difficulty.NORMAL);
	}
	
	/*
	 * Assets
	 */
	@AutoLoad(path=PATH, type=VisualType.IMAGE)
	private Image2D switchButton, band[], proj, trapDoor, lethalPiece, hollowPiece, piecespikes, plat, saw, sawholder, ow, forcefield[], field[], platform, platform2, mine[], launcher, weak, spikeball[], tele1, tele2, bottomcannon, blueflame[], ice, stopswitch, crusherbottom, crushertop, suit[], suititem, gear[];
	private Image2D mainImg[], deathImg[], tesla[], button, telepipe, gem[], teslaexp[], exp[];
	private BigImage backgroundImg, foregroundImg;
	private Pixmap map;
	private Sound jump, collect, slam, elevator;
	
	@AutoLoad(path=PATH,type=VisualType.SOUND)
	private Sound bandmove, flip, spark, gunfire, ldetect, zap, mineexp, poweroff, freeze, shatter, achievement, spikesslam;
	@AutoLoad(path=PATH,type=VisualType.MUSIC)
	private Music energy, sawwork, flamethrowerLoop;
	
	/*
	 * Entities and variables
	 */
	private Flipper gm;
	private Flash flash;
	private CheckpointsHandler cph;
	private Color orgColor;
	private boolean startFalling, immuneToSaws, stopSpark, icelimited;
	private int sparkCounter;
	
	@Override
	public void init() 
	{
		super.init();
		
		mainImg 	= Image2D.loadImages(new File("res/general/main"), true);
		deathImg 	= Image2D.loadImages(new File("res/general/main/death"), true);
		gem		 	= Image2D.loadImages(new File("res/orbitalstation/gem"), false);
		tesla		= Image2D.loadImages(new File(PATH + "/tesla"));
		teslaexp	= Image2D.loadImages(new File(PATH + "/teslaexp"));
		exp			= Image2D.loadImages(new File(PATH + "/exp"));
		telepipe	= new Image2D(PATH + "/telepipe.png");
		button		= new Image2D("res/flyingb/buttonimage.png");
		map 		= new Pixmap(new FileHandle(PATH + "/map.png"));
		stageData = Utilities.createStageData(map);
		
		foregroundImg = new BigImage(PATH + "/foreground.png", RenderOption.PORTION);
		backgroundImg = new BigImage(PATH + "/background.png", RenderOption.PARALLAX_REPEAT);
		backgroundImg.setScrollRatio(.5f);
		
		jump	= TinySound.loadSound(new File("res/general/jump.wav"));
		collect	= TinySound.loadSound(new File("res/steelfactory/collectsound.wav"));
		elevator= TinySound.loadSound(new File("res/steelfactory/elevator.wav"));
		slam	= TinySound.loadSound(new File("res/orbitalstation/slam.wav"));
		
		setStageMusic(PATH + "/song.ogg", 0.45, .7f);
		
		orgColor = field[0].getColor();
		flash = new Flash(Color.WHITE, 100);
	}
	
	@Override
	public void build() 
	{
		super.build();
		
		/*
		 * Standards
		 */
		basicInits();
		background(backgroundImg);
		foreground(foregroundImg);
		
		/*
		 * Globals
		 */
		game.timeColor = Color.WHITE;
		startFalling = immuneToSaws = stopSpark = icelimited = false;
		field[0].setColor(orgColor);
		energy.setVolume(1);
		sawwork.setVolume(0);
		sawwork.play(true);
		flamethrowerLoop.setVolume(0);
		flamethrowerLoop.play(true);
		
		/*
		 * Main Character
		 */
		gm = new Flipper();
		gm.setImage(new Animation<>(3, mainImg));
		gm.setMultiFaced(true);
		gm.setController((Controller)Utilities.importObject("res/data/controller1.con"));
		gm.hit(getDifficulty() == Difficulty.EASY ? 3 : 1);
		gm.setJumpingSound(jump);
		gm.moveTo(startX, startY);
		gm.deathImg = new Particle();
		gm.deathImg.setImage(4,deathImg);
		gm.deathImg.zIndex(101);
		game.addFocusObject(gm);
		gm.addTileEvent(Factory.slipperWalls(gm));
		add(gm);
		
		/*
		 * Flip Switches
		 */
		addSwitch(495, 3632, false);
		addSwitch(1967, 4096, true);
		addSwitch(180, 448, true);
		addSwitch(2201, 1025, false);
		
		/*
		 * Roll Band
		 */
		SolidPlatform rbd = new SolidPlatform(1280, 2784, gm);
		rbd.setImage(2, band);
		rbd.getImage().stop(true);
		rbd.setMoveSpeed(2);
		rbd.freeze();
		rbd.appendPath(1280, 4064);
		rbd.addEvent(new Event()
		{
			int counter = 0;
			
			@Override
			public void eventHandling() 
			{
				if(rbd.isMoving() && ++counter % 22 == 0)
					bandmove.play(.7);
			}
		});

		PathDrone bandDummy = new PathDrone(1280, 2784 + band[0].getHeight() + 1);
		bandDummy.width = band[0].getWidth();
		bandDummy.setMoveSpeed(2);
		bandDummy.freeze();
		bandDummy.appendPath(1280, 4064 + band[0].getHeight() + 1, 0, false, ()->{
			discard(bandDummy);
			Event event = () -> rbd.getImage().stop(true);
			add(event);
		});
			
		bandDummy.addEvent(()->{
			if(gm.collidesWith(bandDummy))
			{
				gm.loc.x -= .8f;
				rbd.getImage().stop(false);
				rbd.unfreeze();
				bandDummy.unfreeze();
			}
		});
		
		add(rbd, bandDummy);
		
		/*
		 * Simple Weapons in band area.
		 */
		Projectile bullet = new Projectile(0, 0, gm);
		bullet.setImage(proj);
		bullet.setMoveSpeed(4);
		bullet.setDisposable(true);
		bullet.scanningAllowed(false);
		
		SimpleWeapon wp1 = new SimpleWeapon(1218, 3394, bullet, Direction.E, 120);
		wp1.setFiringSound(gunfire);
		wp1.getSoundBank().useFallOff(true);
		SimpleWeapon wp2 = new SimpleWeapon(1218, 3733, bullet, Direction.E, 120);
		wp2.setFiringSound(gunfire);
		wp2.getSoundBank().useFallOff(true);
		SimpleWeapon wp3 = new SimpleWeapon(1719, 3196, bullet, Direction.W, 120);
		wp3.setFiringSound(gunfire);
		wp3.getSoundBank().useFallOff(true);
		SimpleWeapon wp4 = new SimpleWeapon(1719, 3930, bullet, Direction.W, 120);
		wp4.setFiringSound(gunfire);
		wp4.getSoundBank().useFallOff(true);
		
		add(wp1, wp2, wp3, wp4);
		
		/*
		 * Trap Door
		 */
		SolidPlatform trap = new SolidPlatform(2207, 3968 - 128, gm);
		trap.appendPath(2207, trap.loc.y + 128);
		trap.setImage(trapDoor);
		trap.setMoveSpeed(5);
		trap.freeze();
		trap.addEvent(()->{
			if(gm.loc.x > 2297)
			{
				startFalling = true;
				trap.unfreeze();
			}
		});
		add(trap);
		
		/*
		 * Falling Pieces
		 */
		addPieces(	"XXXXXXXOOOO",
					"XXOOOXXXXXX",
					"XXXXXXXOOXX",
					"XOOXXXXXXXX",
					"XXXXXXXXXOO");
		
		/*
		 * Moving Platforms
		 */
		SolidPlatform plat1 = new SolidPlatform(3536, 3312, gm);
		plat1.offsetY = -2;
		plat1.setImage(plat);
		plat1.appendPath(plat1.loc.x, plat1.loc.y, 0, true, null);
		plat1.appendPath(3904, 3522 + 30);
		plat1.setStrictGlueMode(true);
		plat1.setMoveSpeed(2);
		
		add(plat1);
		add(plat1.getClone(plat1.loc.x, plat1.loc.y), 75);
		add(plat1.getClone(plat1.loc.x, plat1.loc.y), 145);
		
		for(int i = 0, y = 2560; i < 5; i++, y += 80)
		{
			SolidPlatform solp = plat1.getClone(i % 2 == 0 ? 3507 : 3507 + 100, y);
			solp.clearData();
			solp.setMoveSpeed(1.25f);
			solp.appendPath(solp.loc.x, solp.loc.y, 40, false, null);
			solp.appendPath(solp.loc.x + (i % 2 == 0 ? 100 : -100), solp.loc.y, 40, false, null);
			add(solp);
		}
		
		SolidPlatform plat2 = plat1.getClone(3752, 3194);
		plat2.clearData();
		plat2.appendPath(plat2.loc, 20, false, null);
		plat2.appendPath(3922, 3060, 20, false, null);
		plat2.setMoveSpeed(2.4f);
		add(plat2);
		
		/*
		 * Huge Saw
		 */
		final int distance = 1761 - 251;
		
		PathDrone holder = new PathDrone(1761, 1428);
		holder.setImage(sawholder);
		holder.zIndex(2);
		holder.appendPath();
		holder.appendPath(holder.loc.x - distance, holder.loc.y);
		holder.setMoveSpeed(9.3f);
		holder.addEvent(()->{
			boolean atTop = Utilities.inRange(0, 2247, gm.loc.x) && Utilities.inRange(459, 1069, gm.loc.y);
			float x = holder.loc.x;
			float y = holder.loc.y + holder.halfWidth();
			int d = 800;

			if(Utilities.inRange(0, 2500, gm.loc.x) && Utilities.inRange(1536, 2280, gm.loc.y))
			{
				d = 1400;
				y = gm.loc.y;
			}
			
			sawwork.setVolume(atTop ? 0 : SoundBank.getVolume(x, y, gm.loc.x, gm.loc.y, d, 1, 40));
		});

		PathDrone saw1 = new PathDrone(1700, 1530);
		saw1.setHitbox(Hitbox.CIRCLE);
		saw1.setImage(saw);
		saw1.setMoveSpeed(9.3f);
		saw1.appendPath();
		saw1.appendPath(saw1.loc.x - distance, saw1.loc.y);
		saw1.addEvent(hitMain(saw1, gm, -1));
		saw1.addEvent(()->{saw1.rotation += 7;});
		saw1.setCloneEvent((clone)->{
			PathDrone pd = (PathDrone) clone;

			pd.addEvent(()->{pd.rotation += 7;});
			pd.addEvent(hitMain(pd, gm, -1));
			pd.clearData();
			pd.appendPath();
			pd.appendPath(pd.loc.x - distance, pd.loc.y);
		});
		
		add(holder, saw1, saw1.getClone(1700, 1693), saw1.getClone(1700, 1858), saw1.getClone(1700, 2023));
		
		/*
		 * Bunch of one way platforms
		 */
		OneWay ow1 = new OneWay(2959, 3644, Direction.N, gm);
		ow1.setImage(ow);
		
		add(ow1, ow1.getClone(3136, 3644), ow1.getClone(1317 + 50, 2099), ow1.getClone(1157 + 50, 2029), ow1.getClone(1157 + 50, 1929 + 20), ow1.getClone(679, 2119));
		
		/*
		 * Force fields
		 */
		GameObject 	ffield1 = addForceField(1143 + 50, 1890 + 20),
					ffiedl2 = addForceField(665, 1954);
		
		ffield1.addEvent(()->{
			if(ffield1.collidesWith(gm) || ffiedl2.collidesWith(gm))
			{
				immuneToSaws = true;
				gm.alpha = .5f;
				if(!energy.playing())
					energy.play(true);
			}
			else
			{
				immuneToSaws = false;
				gm.alpha = 1;
				energy.stop();
			}
		});
		
		/*
		 * Electric Field
		 */
		GameObject elec = new GameObject();
		elec.width++;
		elec.setImage(3,field);
		elec.setCloneEvent((clone)->{
			clone.addEvent(()->{
				if(stopSpark)
				{
					clone.getImage().stop(true);
					clone.getImage().setIndex(0);
					field[0].setColor(.0f, .3f, .75f, .8f);
				}
				else if(gm.collidesWith(clone))
					gm.hit(-5);
			});
		});
		
		add(elec.getClone(400, 1056), elec.getClone(400, 448));
		
		/*
		 * Locked Door
		 */
		SolidPlatform lockedDoor = new SolidPlatform(2240, 832, gm);
		lockedDoor.setImage(trapDoor);
		lockedDoor.addEvent(()->{
			if(stopSpark)
				discard(lockedDoor);
		});
		add(lockedDoor);
		
		/*
		 * Platforms
		 */
		int wf = 30; //Wait Frames
		
		SolidPlatform sp1 = new SolidPlatform(491, 536, gm);
		sp1.setImage(platform);
		sp1.setMoveSpeed(1);
		sp1.setStrictGlueMode(true);
		sp1.appendPath(sp1.loc, wf, false, null);
		sp1.appendPath(sp1.loc.x + 160, sp1.loc.y, wf, false, null);
		
		SolidPlatform sp2 = sp1.getClone(821, 536);
		sp2.clearData();
		sp2.appendPath(sp2.loc, wf, false, null);
		sp2.appendPath(sp2.loc.x, sp2.loc.y + 100, wf, false, null);
		
		SolidPlatform sp3 = sp1.getClone(1071, 536);
		sp3.clearData();
		
		SolidPlatform sp4 = sp1.getClone(1261, 536);
		sp4.clearData();
		sp4.appendPath(sp4.loc, wf, false, null);
		sp4.appendPath(sp4.loc.x + 100, sp4.loc.y, wf, false, null);
		sp4.setMoveSpeed(2);
		
		SolidPlatform sp5 = sp1.getClone(1541, 536);
		sp5.clearData();
		sp5.zIndex(-1);
		sp5.appendPath(sp5.loc, wf, false, null);
		sp5.appendPath(sp5.loc.x, sp5.loc.y - 100, wf, false, null);
		
		SolidPlatform sp6 = new SolidPlatform(1793, 536, gm);
		sp6.setImage(platform2);
		sp6.setMoveSpeed(1);
		sp6.setStrictGlueMode(true);
		sp6.appendPath();
		sp6.appendPath(sp6.loc.x + sp6.width(), sp6.loc.y);
		sp6.appendPath(sp6.loc.x + sp6.width(), sp6.loc.y + sp6.height());
		sp6.appendPath(sp6.loc.x, sp6.loc.y + sp6.height());
		
		SolidPlatform sp7 = sp1.getClone(1862, 992);
		sp7.clearData();
		
		SolidPlatform sp8 = sp7.getClone(1672, 992);
		sp8.appendPath(sp8.loc, wf, false, null);
		sp8.appendPath(sp8.loc.x - 300, sp8.loc.y, wf, false, null);
		
		SolidPlatform sp9 = sp7.getClone(1182, 992);
		
		SolidPlatform sp10 = sp7.getClone(1182 - 150, 992 - 100);
		
		SolidPlatform sp11 = sp7.getClone(742, 992);
		
		add(sp1, sp2, sp3, sp4, sp5, sp6, sp7, sp8, sp9, sp10, sp11);
		add(sp6.getClone(1793, 536), 92);
		
		/*
		 * Button
		 */
		SolidPlatform theButton = new SolidPlatform(759, 978, gm);
		theButton.setImage(button);
		theButton.appendPath(759, 984);
		theButton.freeze();
		theButton.zIndex(-1);
		theButton.addEvent(new Event()
		{
			@Override
			public void eventHandling() 
			{
				theButton.loc.y--;
				if(theButton.collidesWith(gm))
				{
					stopSpark = true;
					theButton.unfreeze();
					theButton.removeEvent(this);
					poweroff.play();
				}
				
				theButton.loc.y++;
			}
		});
		add(theButton);
		
		/*
		 * Launchers
		 */
		addLauncher(800, 690);
		addLauncher(1247, 752);
		addLauncher(1705, 679);
		
		/*
		 * Mines
		 */
		add(getMine(1629, 959), getMine(1519, 959), getMine(1519, 924), getMine(1436, 924));
		
		PathDrone mina = getMine(1011, 860);
		mina.appendPath();
		mina.appendPath(1084, mina.loc.y);
		add(mina);
		
		/*
		 * Weak Platforms
		 */
		for(int i = 0, x = 2861, y = 788; i < 7; i++, x -= (weak.getWidth() * 2))
			if(i % 2 == 0)
				addWeak(x, y);
		
		/*
		 * Spike Balls
		 */
		GameObject sb = new GameObject();
		sb.moveTo(2533, 749);
		sb.setImage(6, spikeball);
		sb.addEvent(Factory.hitMain(sb, gm, -1));
		sb.setHitbox(Hitbox.EXACT);
		sb.setCloneEvent((clone)->{
			clone.addEvent(Factory.hitMain(clone, gm, -1));
		});
		
		add(sb, sb.getClone(2533, 719), sb.getClone(2533, 689), /*sb.getClone(2533, 609),*/ sb.getClone(2533, 579), sb.getClone(2665, 719), sb.getClone(2665, 689),
			sb.getClone(2665, 659), sb.getClone(2665, 629), sb.getClone(2797, 709), sb.getClone(2797, 679));
		
		/*
		 * Teleporter
		 */
		GameObject teleTop = new GameObject();
		teleTop.setImage(tele1);
		teleTop.moveTo(3936, 840);
		teleTop.zIndex(-1);
		
		GameObject pipe = new GameObject();
		pipe.setImage(telepipe);
		pipe.moveTo(3937, 904);
		pipe.zIndex(1);
		
		GameObject teleBottom = new GameObject();
		teleBottom.setImage(tele2);
		teleBottom.moveTo(3937, 2384);
		teleBottom.zIndex(-1);
		
		add(teleTop, pipe, teleBottom);
		
		SolidPlatform elB = new SolidPlatform(3937, 896, gm);
		elB.alpha = 0;
		elB.setImage(ow);
		elB.width += 13;
		elB.appendPath(elB.loc.x, 2432, 0, false, ()->{
			gm.unfreeze();
			pipe.zIndex(-1);
			elevator.stop();
		});
		elB.setMoveSpeed(16);
		elB.setStrictGlueMode(true);
		elB.freeze();
		elB.addEvent(new Event()
		{
			@Override
			public void eventHandling() 
			{
				if(Fundementals.distance(gm.loc.x, gm.loc.y, 3956, 876) < 2)
				{
					elB.unfreeze();
					gm.vx = gm.vy = 0;
					gm.freeze();
					elB.removeEvent(this);
					elevator.play();
				}
			}
		});
		add(elB);
		
		/*
		 * Blue Flame
		 */
		GameObject go = new GameObject();
		go.setImage(bottomcannon);
		go.moveTo(3039, 3127);
		go.zIndex(-1);
		add(go);
		
		GameObject iceblock = new GameObject();
		iceblock.setImage(ice);
		
		for(Image2D img : blueflame)
			img.setSize(128, 256);
		
		GameObject flame = new GameObject();
		flame.setHitbox(Hitbox.EXACT);
		flame.setImage(2, blueflame);
		flame.moveTo(3005 - 3, 3109 - 7);
		flame.addEvent(()->{
			Vector2 focusPos = game.getFocusList().get(0).loc;
			
			flamethrowerLoop.setVolume(icelimited ? 
													SoundBank.getVolume(flame.centerX(), flame.loc.y, focusPos.x, focusPos.y, 600, .2f, 25) : 
													SoundBank.getVolume(flame.centerX(), flame.loc.y, gm.loc.x, gm.loc.y, 600, 1,   40));
		});
		flame.addEvent(new Event()
		{
			boolean gmFrozen;
			int counter;
			
			@Override
			public void eventHandling() 
			{
				if(icelimited)
				{
					flame.height -= 120;
					flame.alpha = .4f;
					flame.setHitbox(Hitbox.INVINCIBLE);
					flame.removeEvent(this);
				}
				else if(!gmFrozen && flame.collidesWith(gm))
				{
					gm.vx = gm.vy = 0;
					gm.freeze();
					gm.enableWallSlide(false);
					gmFrozen = true;
					counter = 60;
					freeze.play();
				}
				else if(gmFrozen)
				{
					gm.rotation += 3;
					iceblock.rotation += 3;
					iceblock.moveTo(gm.loc.x - 10, gm.loc.y - 8);

					if(--counter < 0)
					{
						gmFrozen = false;
						gm.unfreeze();
						gm.enableWallSlide(true);
						gm.rotation =  iceblock.rotation = 0;
						shatter.play();
					}
				}
				else
					iceblock.moveTo(0, 0);
			}
		});
		
		add(flame, iceblock);
		
		/*
		 * Camera
		 */
		PathDrone camera = new PathDrone(0, 0);
		camera.freeze();
		camera.setMoveSpeed(10);
		camera.appendPath(3039, 3127, 0, false, ()->{
			add(new Event()
			{
				@Override
				public void eventHandling() 
				{
					game.removeFocusObject(camera);
					game.addFocusObject(gm);
					gm.unfreeze();
					discard(this);
					music.setVolume(.7);
				}
			}, 60);
		});
		
		add(camera);

		/*
		 * Stop Ice Switch
		 */
		SimpleButton stops = new SimpleButton(3728, 434, gm);
		stops.setImage(stopswitch);
		stops.setClickEvent(()->{
			icelimited = true;
			game.removeFocusObject(gm);
			game.addFocusObject(camera);
			gm.freeze();
			camera.loc.set(gm.loc);
			camera.unfreeze();
			music.setVolume(.2);
			achievement.play();
			
		});
		add(stops);
		
		/*
		 * Crusher
		 */
		int waitFrames = 25;
		
		SolidPlatform cbottom = new SolidPlatform(3712, 4144, gm);
		cbottom.setImage(crusherbottom);
		cbottom.setMoveSpeed(7);
		cbottom.setRock(true, true);
		cbottom.appendPath(cbottom.loc, waitFrames, false, null);
		cbottom.appendPath(cbottom.loc.x, 4208, waitFrames, false, ()->{
			if(cbottom.loc.equals(cbottom.getCurrentTarget()) && cbottom.getPrevY() != cbottom.loc.y && Fundementals.distance(cbottom, gm) < 500)
			{
				add(CameraEffect.vibration(1, 10));
				slam.play(SoundBank.getVolume(cbottom, gm, 500, 1, 50));
			}
		});
		
		SolidPlatform ctop = new SolidPlatform(3740, 4064, gm);
		ctop.setImage(crushertop);
		ctop.setMoveSpeed(7);
		ctop.addEvent(()->{
			ctop.loc.y = cbottom.loc.y - ctop.height();
		});
		
		/*
		 * Suit
		 */
		GameObject sitem = new GameObject();
		sitem.setImage(suititem);
		sitem.moveTo(2884, 3182);
		sitem.addEvent(()->{
			if(sitem.collidesWith(gm))
			{
				discard(sitem);
				gm.setImage(3, suit);
				gm.setMultiFaced(true);
				cbottom.avoidOverlapping(gm);
				collect.play();
			}
		});
		
		add(cbottom, ctop, sitem);
		
		/*
		 * Gears
		 */
		PathDrone g = new PathDrone(4800, 3456);
		g.setImage(3, gear);
		g.setMoveSpeed(1.3f);
		g.appendPath();
		g.appendPath(g.loc.x, 4208);
		g.appendPath(5040, 4208);
		g.appendPath(5040, g.loc.y);
		g.setCloneEvent((clone)-> clone.addEvent(Factory.hitMain(clone, gm, -1)));
		
		int freq = 90;
		int gears = 17;
		
		for(int i = 0; i < gears; i++)
			add(g.getClone(g.loc.x, g.loc.y), i * freq);
		
		/*
		 * Goal
		 */
		GameObject goal = new GameObject();
		goal.moveTo(5027, 3456);
		goal.setImage(4, gem);
		goal.addEvent(()->{
			if(goal.collidesWith(gm))
			{
				gm.setState(CharacterState.FINISH);
				discard(goal);
				collect.play();
			}
		});
		add(goal);
		
		/*
		 * Checkpoint Handler
		 */
		Vector2 start1 = new Vector2(2515, 3692);
		Vector2 start2 = new Vector2(3347, 2540);
		Vector2 start3 = new Vector2(219,  1004);
		Vector2 start4 = new Vector2(2423, 1004);
		Vector2 start5 = new Vector2(3955, 2412);
		
		if(cph == null && getDifficulty() != Difficulty.HARD)
		{
			cph = new CheckpointsHandler();
			cph.appendCheckpoint(start1, start1.x, 3584, 150, 150);
			cph.appendCheckpoint(start2, start2.x, 2420, 40, 150);
			cph.appendCheckpoint(start3, 64, 904, 100, 100);
			cph.appendCheckpoint(start4, start4.x, 824, 100, 200);
			cph.appendCheckpoint(start5, start5.x, start5.y, 24, 20);
			cph.setReachEvent(()-> GFX.renderCheckpoint());
		}
		
		if(cph != null)
		{
			cph.setUsers(gm);

			Vector2 latestCp = cph.getLastestCheckpoint();
			if(latestCp != null)
			{
				gm.loc.set(latestCp);
				
				if(latestCp.equals(start5))
				{
					icelimited = true;
					pipe.zIndex(-1);
				}
			}
		}
		
		add(cph);
			
		/*
		 * Finalize
		 */
		gm.setHitEvent((hitter)->{
			if(hitter.sameAs(bullet))
				gm.hit(-1);
		});
	}
	
	@Override
	protected boolean isSafe() 
	{
		return cph != null && cph.getLastestCheckpoint() != null;
	}
	
	@Override
	protected void onComplete() 
	{
		if(cph != null)
			cph.reset();
	}
	
	protected void extra() 
	{
		if(!stopSpark && ++sparkCounter % 20 == 0)
			addRandomSpark();
	}
	
	void addWeak(float x, float y)
	{
		final PathData pd = new PathData(x, y);
		
		SolidPlatform w = new SolidPlatform(x, y, gm);
		w.setImage(weak);
		w.setMoveSpeed(3);
		w.appendPath(pd);

		GameObject topLayer = getTopSide(w);
		w.addEvent(new Event() 
		{
			int counter;
			
			@Override
			public void eventHandling() 
			{
				if(--counter > 0 || topLayer.collidesWith(gm))
				{
					if(counter < 0)
						counter = 10;
					pd.targetY = y - 200;
				}
				else
					pd.targetY = y;
			}
		});
		add(w, topLayer);
	}
	
	PathDrone getMine(float x, float y)
	{
		final Particle mineExp = new Particle();
		mineExp.setImage(2, exp);
		mineExp.zIndex(101);
		mineExp.setIntroSound(mineexp);
		mineExp.getSoundBank().useFallOff(true);
		mineExp.getSoundBank().maxDistance = 850;
		
		PathDrone m = new PathDrone(x, y);
		m.setHitbox(Hitbox.EXACT);
		m.setImage(3, mine);
		m.setMoveSpeed(1);
		m.addEvent(()->{
			boolean collides = m.collidesWith(gm);
			if(collides)
				gm.hit(-1);
			
			if(collides || stopSpark)
			{
				discard(m);
				add(mineExp.getClone(m.loc.x - mineExp.halfWidth(), m.loc.y - mineExp.halfHeight()));
			}
		});
		
		return m;
	}
	
	void addLauncher(float x, float y)
	{
		final Particle boom = new Particle();
		boom.setImage(1, teslaexp);
		boom.setIntroSound(zap);
		boom.getSoundBank().useFallOff(true);
		boom.zIndex(101);
		
		Dummy l = new Dummy(x,y);
		l.setImage(launcher);
		l.setTriggerable(true);
		l.setMoveSpeed(4);
		l.setHitbox(Hitbox.CIRCLE);
		l.addEvent(new Event() 
		{
			boolean detected, soundPlayed;
			Vector2 target;
			
			@Override
			public void eventHandling() 
			{
				if(detected || Fundementals.distance(l, gm) < 300)
				{
					l.rotation += 7;
					detected = true;
					
					if(!soundPlayed)
					{
						soundPlayed = true;
						ldetect.play();
					}
					
					if(target == null)
						target = Fundementals.findWallPoint(l.loc.x, l.loc.y, gm.loc.x, gm.loc.y);
					
					l.moveToward(target.x, target.y);
				}
				else
					l.rotation += 1;
				
				boolean delete = false;
				if(l.collidesWith(gm))
				{
					gm.hit(-1);
					delete = true;
				}
				
				if(delete || stopSpark)
				{
					discard(l);
					add(boom.getClone(l.loc.x - boom.halfWidth(), l.loc.y - boom.halfHeight()));
				}
			}
		});
		l.addTileEvent((tileType)->{
			if(tileType == Engine.SOLID)
			{
				discard(l);
				add(boom.getClone(l.loc.x - boom.halfWidth(), l.loc.y - boom.halfHeight()));
			}
		});
		add(l);
	}
	
	void addRandomSpark()
	{
		Particle spark = new Particle();
		spark.setImage(1, tesla);
		spark.setIntroSound(this.spark);
		spark.getSoundBank().useFallOff(true);
		spark.addEvent(()->{
			if(spark.getImage().getIndex() > tesla.length / 2)
				spark.alpha -= .05f;
		});
		spark.moveTo(MathUtils.random(400, 400 + field[0].getWidth() - tesla[0].getWidth()), MathUtils.random.nextInt(2) > 0 ? 931 : 361);
		spark.zIndex(-1);
		add(spark);
	}
	
	GameObject addForceField(float x, float y)
	{
		GameObject ffield = new GameObject();
		ffield.moveTo(x, y);
		ffield.setImage(1, forcefield);
		ffield.alpha = .7f;
		ffield.setHitbox(Hitbox.CIRCLE);
		add(ffield);
		
		return ffield;
	}
	
	void addPieces(String... pieces)
	{
		boolean[] reached = new boolean[pieces.length];
		final int 	left = 2240;
		int targetY = 4264;
		int moveSpeed = 4;
		
		for(int i = 0, y = 3712 + (24 * pieces.length); i < pieces.length; i++, y -= 24, targetY -= 24)
		{
			for(int j = 0, x = left; j < pieces[i].length(); j++, x+= 24)
			{
				final int index = i;

				if(pieces[i].charAt(j) == 'X')
				{
					SolidPlatform solp = new SolidPlatform(x, y, gm);
					solp.setImage(lethalPiece);
					solp.freeze();
					solp.setMoveSpeed(moveSpeed);
					solp.appendPath(x, targetY, 0, false, ()->{ 
						reached[index] = true; 
					});
					solp.addEvent(()->{
						if((index == 0 && startFalling) || (index > 0 && reached[index - 1]))
							solp.unfreeze();
					});
					
					PathDrone spikes = new PathDrone(0, 0);
					spikes.setImage(piecespikes);
					spikes.setMoveSpeed(moveSpeed);
					spikes.addEvent(Factory.follow(solp, spikes, 0, solp.height()));
					spikes.addEvent(Factory.hitMain(spikes, gm, -3));
					
					add(solp, spikes);
				}
				else if(pieces[i].charAt(j) == 'O')
				{
					PathDrone piece = new PathDrone(x, y);
					piece.setImage(hollowPiece);
					piece.zIndex(-1);
					piece.setMoveSpeed(moveSpeed);
					piece.freeze();
					piece.appendPath(x, targetY);
					piece.addEvent(()->{
						if((index == 0 && startFalling) || (index > 0 && reached[index - 1]))
							piece.unfreeze();
					});
					
					add(piece);
				}
			}
		}
	}
	
	void addSwitch(float x, float y, boolean flipY)
	{
		SolidPlatform sp = new SolidPlatform(x, y, gm);
		sp.setImage(switchButton);
		sp.appendPath(x, flipY ? y - 7 : y + 7);
		sp.freeze();
		sp.flipY = flipY;
		
		GameObject dummy = new GameObject();
		dummy.width = sp.width - 2;
		dummy.loc.x = sp.loc.x + 1;
		dummy.loc.y = flipY ? sp.loc.y + sp.height() + 1 : sp.loc.y - 1;
		dummy.addEvent(()->{
			if(dummy.collidesWith(gm))
			{
				discard(dummy);
				gm.flip();
				sp.unfreeze();
				flip.play();
				add(flash.getClone(0, 0));
			}
		});
		add(sp, dummy);
	}
	
	Event hitMain(GameObject obj, MainCharacter main, int power)
	{
		return ()->
		{
			if(!immuneToSaws && obj.collidesWith(main))
			{
				main.hit(power);
				main.runHitEvent(obj);
			}
		};
	}
	
	static GameObject getTopSide(GameObject target)
	{
		GameObject go = new GameObject();
		go.height = 10;
		go.width = target.width();
		go.addEvent(Factory.follow(target, go, 0, -10));
		
		return go;
	}
}
