package stages.flyingbat;

import game.core.Engine;
import game.core.Engine.Direction;
import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.GameObject.HitEvent;
import game.core.GameObject.Hitbox;
import game.core.MovableObject;
import game.core.MainCharacter.CharacterState;
import game.core.MovableObject.TileEvent;
import game.core.Stage;
import game.essentials.Controller;
import game.essentials.Factory;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.essentials.Utilities;
import game.mains.GravityMan;
import game.movable.Missile;
import game.movable.Missile.MissileProperties;
import game.movable.PathDrone;
import game.movable.PushableObject;
import game.movable.SolidPlatform;
import game.movable.Weapon;
import game.objects.Particle;
import java.io.File;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;


@Playable(name="Flying Battery", description="Stage: Flying Batteri\nAuthor: Pojahn Moradi\nDifficulty: 5\nAverage time: 120 sec\nProfessional time: 45 sec\nObjective: Finish the stage.")
public class FlyingBattery extends Stage
{
	private Pixmap stageImage;
	private Image2D backgroundImg, foregroundImg, deathImg[], mainImage[], electricImg[], doorImg, buttonImg, pushImg, blobImg, dummyImg, vchainImg, hchainImg, ballImg, ball2Img, platformImg, propellerImg[], coinImg[], windImg[], firingImg[], tankImg, shellImg;
	private Sound doorOpen, pushed, pipefire, jump, tankfire;
	private boolean used = false;
	
	@Override
	public void init() 
	{
		try
		{
			mainImage    = Image2D.loadImages(new File("res/general/main"),true);
			propellerImg = Image2D.loadImages(new File("res/flyingb/propeller"),true);
			ballImg      = new Image2D("res/flyingb/ball.png",true);
			ball2Img     = new Image2D("res/flyingb/ball2.png",true);
			tankImg      = new Image2D("res/flyingb/tank.png",true);
			foregroundImg = new Image2D("res/flyingb/foreground.png",false);
			backgroundImg = new Image2D("res/flyingb/background.png",false);
			doorImg     = new Image2D("res/flyingb/door.png",false);
			buttonImg   = new Image2D("res/flyingb/buttonimage.png",false);
			pushImg     = new Image2D("res/flyingb/pushable.png",false);
			blobImg     = new Image2D("res/flyingb/blob.png",true);
			vchainImg   = new Image2D("res/flyingb/vertchain.png",false);
			hchainImg   = new Image2D("res/flyingb/horichain.png",false);
			platformImg = new Image2D("res/flyingb/platform.png",false);
			shellImg    = new Image2D("res/flyingb/shell.png",false);
			dummyImg    = new Image2D("res/general/dummy.png",false);
			electricImg  = Image2D.loadImages(new File("res/flyingb/electric"),false);
			windImg		 = Image2D.loadImages(new File("res/flyingb/wind"),false);
			firingImg    = Image2D.loadImages(new File("res/flyingb/fireanim"),false);
			coinImg      = Image2D.loadImages(new File("res/general/starcoin"),false);
			deathImg          = Image2D.loadImages(new File("res/general/main/death"),false);
			stageImage        = new Pixmap(new FileHandle("res/flyingb/stage.png"));
			
			doorOpen = TinySound.loadSound(new File(("res/flyingb/open.wav")));
			pushed   = TinySound.loadSound(new File(("res/flyingb/pushed.wav")));
			pipefire = TinySound.loadSound(new File(("res/flyingb/pipefire.wav")));
			jump     = TinySound.loadSound(new File(("res/general/jump.wav")));
			tankfire = TinySound.loadSound(new File(("res/flyingb/tankfire.wav")));
			
			MUSIC_VOLUME = 0.6f;
			setStageMusic("res/flyingb/song.ogg", 1.318f);
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

		stageData = Utilities.createStageData(stageImage);
		basicInits();
		
		game.timeColor = Color.WHITE;
		game.deathTextColor = Color.WHITE;
		LETHAL_DAMAGE = -10;
		used = false;
		
		visibleWidth = 800;
		visibleHeight = 600;
		
		foreground(RenderOption.FULL, foregroundImg);
		background(RenderOption.FULL, backgroundImg);

		final GravityMan gm = new GravityMan();
		gm.setImage(new Frequency<>(3, mainImage));
		gm.setMultiFaced(true);
		gm.setController((Controller)Utilities.importObject("res/data/controller1.con"));
		gm.hit(1);
		gm.setJumpingSound(jump);
		gm.currX = startX;
		gm.currY = startY;
		gm.addTileEvent(Factory.slipperWalls(gm));
		gm.addTileEvent(Factory.pushEvent(gm, Engine.AREA_TRIGGER_2, 0.3f, Direction.E));
		gm.deathImg = new Particle();
		gm.deathImg.setImage(4, deathImg);
		game.setFocusObject(gm);
		add(gm);
		
		GameObject dummyElectric = new GameObject();
		dummyElectric.setImage(new Frequency<>(4, electricImg));
		dummyElectric.currX = 430;
		dummyElectric.currY = 606;
		
		final MovableObject door = new MovableObject();
		door.setImage(new Frequency<>(1, doorImg));
		door.currX = 1498;
		door.currY = 351;
		gm.avoidOverlapping(door);
		
		final PathDrone button = new PathDrone(46, 455);
		button.setImage(new Frequency<>(1, buttonImg));
		button.setMoveSpeed(1);
		gm.addTileEvent(new TileEvent()
		{
			@Override
			public void eventHandling(byte tileType) 
			{
				if(tileType == Engine.AREA_TRIGGER_1)
				{
					button.appendPath(46,465,Integer.MAX_VALUE,false,null);
					discard(door);
					gm.allowOverlapping(door);
					if(!used)
					{
						doorOpen.play();
						used = true;
					}
				}
			}
		});
		
		final PushableObject push = new PushableObject(2037, 380, gm);
		push.setImage(new Frequency<>(1, pushImg));
		push.setPushingSound(pushed, 10);
		push.setTriggerable(true);
		push.addTileEvent(new TileEvent()
		{	
			@Override
			public void eventHandling(byte tileType) 
			{
				if(tileType == Engine.LETHAL)
					discard(push);
			}
		});
		push.addEvent(new Event()
		{	
			@Override
			public void eventHandling() 
			{
				while(push.collidesWith(gm))
				{
					gm.currX-=2;
					push.moveTo(push.currX + 1, push.currY);
				}
			}
		});
		
		Blob blob = new Blob(0,0,new MovableObject[]{gm}, gm);
		blob.setImage(new Frequency<>(1, blobImg));
		blob.useFastCollisionCheck(true);
		blob.setFiringSound(pipefire);
		blob.setProperties(MissileProperties.FAST_VERY_FLOATY);
		
		Particle gunfire = new Particle();
		gunfire.setImage(new Frequency<>(4, firingImg));
		gunfire.offsetX = -65;
		gunfire.offsetY = -70;
		
		Weapon weap = new Weapon(1105, 227, 1, 1, 100, gm);
		weap.setImage(new Frequency<>(1, dummyImg));
		weap.setVisible(false);
		weap.setProjectile(blob);
		weap.setFrontFire(false);
		weap.setRotationSpeed(0);
		weap.setFiringParticle(gunfire);
		
		final PathDrone chain1 = new PathDrone(595, -30);
		chain1.setImage(new Frequency<>(1, vchainImg));
		chain1.appendPath(595, 100, 50, false, new Event()
		{	
			@Override
			public void eventHandling() 
			{
				chain1.setMoveSpeed(1);
			}
		});
		chain1.appendPath(595, -30, 20, false, new Event()
		{	
			@Override
			public void eventHandling() 
			{
				chain1.setMoveSpeed(4);
			}
		});
		
		GameObject ball1 = new GameObject();
		ball1.setHitbox(Hitbox.EXACT);
		ball1.setImage(new Frequency<>(1, ballImg));
		ball1.addEvent(Factory.follow(chain1, ball1, -(ball1.width / 2) + 3, chain1.height));
		ball1.addEvent(Factory.hitMain(ball1, gm, -1));
		
		final PathDrone chain2 = new PathDrone(739, -30);
		chain2.setImage(new Frequency<>(1, vchainImg));
		chain2.appendPath(739, 100, 100, false, new Event()
		{	
			@Override
			public void eventHandling() 
			{
				chain2.setMoveSpeed(0.5f);
			}
		});
		chain2.appendPath(739, -30, 60, false, new Event()
		{	
			@Override
			public void eventHandling() 
			{
				chain2.setMoveSpeed(3);
			}
		});
		
		GameObject ball2 = new GameObject();
		ball2.setHitbox(Hitbox.EXACT);
		ball2.setImage(new Frequency<>(1, ballImg));
		ball2.addEvent(Factory.follow(chain2, ball2, -(ball1.width / 2) + 3, chain1.height));
		ball2.addEvent(Factory.hitMain(ball2, gm, -1));
		
		final PathDrone chain3 = new PathDrone(-640, 268);
		chain3.setImage(new Frequency<>(1, hchainImg));
		chain3.appendPath(-40, 268, 10, false, new Event()
		{	
			@Override
			public void eventHandling() 
			{
				chain3.setMoveSpeed(3);
			}
		});
		chain3.appendPath(-640, 268, 10, false, new Event()
		{	
			@Override
			public void eventHandling() 
			{
				chain3.setMoveSpeed(3.5f);
			}
		});
		
		GameObject ball3 = new GameObject();
		ball3.setHitbox(Hitbox.EXACT);
		ball3.setImage(new Frequency<>(1, ballImg));
		ball3.addEvent(Factory.follow(chain3, ball3, chain3.width - 6,-21));
		ball3.addEvent(Factory.hitMain(ball3, gm, -1));
		
		final PathDrone chain4 = new PathDrone(-640, 315);
		chain4.setImage(new Frequency<>(1, hchainImg));
		chain4.appendPath(-40, 315, 10, false, new Event()
		{	
			@Override
			public void eventHandling() 
			{
				chain4.setMoveSpeed(3);
			}
		});
		chain4.appendPath(-640, 315, 10, false, new Event()
		{	
			@Override
			public void eventHandling() 
			{
				chain4.setMoveSpeed(3.5f);
			}
		});
		
		GameObject ball4 = new GameObject();
		ball4.setHitbox(Hitbox.EXACT);
		ball4.setImage(new Frequency<>(1, ballImg));
		ball4.addEvent(Factory.follow(chain4, ball4, chain3.width - 6,-21));
		ball4.addEvent(Factory.hitMain(ball4, gm, -1));
		
		SolidPlatform p1 = new SolidPlatform(2430, 154, gm);
		p1.setImage(new Frequency<>(1, platformImg));
		p1.setStrictGlueMode(false);
		p1.setMoveSpeed(2);
		p1.appendPath(2720, 154, 10, false, null);
		p1.appendPath(2430, 154, 10, false, null);
		
		SolidPlatform p2 = p1.getClone(2800, 154);
		p2.setMoveSpeed(4);
		p2.setStrictGlueMode(true);
		p2.clearData();
		p2.appendPath(2800, 800, 10, false, null);
		p2.appendPath(2800, 154, 10, false, null);
		
		SolidPlatform p3 = p1.getClone(2880, 800);
		p3.setMoveSpeed(2);
		p3.setStrictGlueMode(true);
		p3.clearData();
		p3.appendPath(3375, 131, 10, false, null);
		p3.appendPath(2880, 800, 10, false, null);
		
		GameObject prop1 = new GameObject();
		prop1.setImage(new Frequency<>(1, propellerImg));
		prop1.setHitbox(Hitbox.EXACT);
		prop1.addEvent(Factory.follow(p1, prop1, 0, 10));
		prop1.addEvent(Factory.hitMain(prop1, gm, -1));
		
		GameObject prop2 = prop1.getClone(0, 0);
		prop2.addEvent(Factory.follow(p2, prop2, 0, 10));
		prop2.addEvent(Factory.hitMain(prop2, gm, -1));
		
		GameObject prop3 = prop1.getClone(0, 0);
		prop3.addEvent(Factory.follow(p3, prop3, 0, 10));
		prop3.addEvent(Factory.hitMain(prop3, gm, -1));
		
		GameObject coin = new GameObject();
		coin.setImage(new Frequency<>(7, coinImg));
		coin.currX = 3414;
		coin.currY = 95;
		
		GameObject wind = new GameObject();
		wind.setImage(new Frequency<>(2, windImg));
		wind.currX = 2074;
		wind.currY = 0;
		
		final Missile mi = new Missile(0,0, gm);
		mi.setImage(new Frequency<>(1, shellImg));
		mi.useFastCollisionCheck(true);
		mi.setFiringSound(tankfire);

		Weapon tank = new Weapon(134, 435, 2, 20, 320, gm);
		tank.setImage(new Frequency<>(1, tankImg));
		tank.setProjectile(mi);
		tank.setRotationSpeed(0);
		tank.setFiringParticle(gunfire);
		tank.setFiringOffsets(35, -10);
		tank.setHitbox(Hitbox.EXACT);
		gm.avoidOverlapping(tank);
		gm.setHitEvent(new HitEvent()
		{
			@Override
			public void eventHandling(GameObject hitter) 
			{
				if(hitter.sameAs(mi))
					gm.hit(-1);
				
				if(gm.getHP() <= 0)
				{
					gm.setVisible(false);
					gm.setState(CharacterState.DEAD);
				}
			}
		});
		
		PathDrone ball5 = new PathDrone(313,757);
		ball5.appendPath(430, 644, 0, false, null);
		ball5.appendPath(313, 757, 0, false, null);
		ball5.setImage(new Frequency<>(1, ball2Img));
		ball5.addEvent(Factory.hitMain(ball5, gm, -1));

		PathDrone ball6 = new PathDrone(463,719);
		ball6.appendPath(463, 644, 0, false, null);
		ball6.appendPath(463,719, 0, false, null);
		ball6.setImage(new Frequency<>(1, ball2Img));
		ball6.addEvent(Factory.hitMain(ball6, gm, -1));
		ball6.setMoveSpeed(1.4f);
		
		PathDrone ball7 = new PathDrone(635,676);
		ball7.appendPath(495, 644, 0, false, null);
		ball7.appendPath(635,676, 0, false, null);
		ball7.setImage(new Frequency<>(1, ball2Img));
		ball7.addEvent(Factory.hitMain(ball7, gm, -1));
		ball7.setMoveSpeed(2.7f);
		
		add(dummyElectric);
		add(door);
		add(button);
		add(push);
		add(weap);
		add(chain1);
		add(chain2);
		add(chain3);
		add(chain4);
		add(ball1);
		add(ball2);
		add(ball3);
		add(ball4);
		add(ball5);
		add(ball6);
		add(ball7);
		add(p1);
		add(p2);
		add(p3);
		add(prop1);
		add(prop2);
		add(prop3);
		add(coin);
		add(wind);
		add(tank);
	}

	@Override
	public void dispose() 
	{
		disposeBatch(stageImage,backgroundImg, foregroundImg, deathImg, mainImage, electricImg, doorImg, buttonImg, pushImg, blobImg, dummyImg, vchainImg, hchainImg, ballImg, ball2Img, platformImg, propellerImg, coinImg, windImg, firingImg, tankImg, shellImg);
	}
}