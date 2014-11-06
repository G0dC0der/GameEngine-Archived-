package stages.sand;

import static game.core.Engine.*;
import game.core.Enemy;
import game.core.Engine;
import game.core.Stage;
import game.core.Engine.Direction;
import game.core.Fundementals;
import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.GameObject.HitEvent;
import game.core.GameObject.Hitbox;
import game.core.MainCharacter.CharacterState;
import game.core.MovableObject;
import game.core.MovableObject.TileEvent;
import game.development.AutoInstall;
import game.development.StageBuilder;
import game.essentials.Factory;
import game.essentials.Animation;
import game.essentials.Image2D;
import game.movable.Dummy;
import game.movable.LaserDrone;
import game.movable.Missile;
import game.movable.Missile.MissileProperties;
import game.movable.PathDrone;
import game.movable.Projectile;
import game.movable.PushableObject;
import game.movable.SolidPlatform;
import game.objects.Particle;
import game.objects.Wind;

import java.io.File;

import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;

@AutoInstall(mainPath="res/general", path="res/sandopolis")
@Playable(name="Sandopolis",description="Stage: Sandopolis(Ugly Sun)\nAuthor: Pojahn Moradi\nDifficulty: 6\nAverage time: 87 sec\nProfessional time: 70 sec\nObjective: Kill the boss")
public class Sandopolis extends StageBuilder
{
	private Image2D flamerImg[], flamer2Img[], blockImg, spikeballImg, smallSpikeImg, sunImg, sunAngryImg[], sunPissedImg, sunEyeImg, cannonImg, missileImg, impactImg[], trailerImg[], gunfireImg[], blowingImg, chillingImg, windImg[], itemImg[], smallMain[], stoneImg, powerupImg, bossexpImg[], projImg, expImg[];
	private Sound explodesound, firesound, collect, pushed, bossdie, lasercharge, laserattack, weap2fire, weap2exp;
	private MovableObject sun, eye1, eye2;
	private GameObject blower;
	private Event followEvent, followEvent2;
	private Missile antiSunMissile, antiBlow;
	private LaserDrone weapon1;
	private Projectile proj;
	private boolean haveFired1, haveFired2, haveFired3, antiBlowDone, won, pissed;
	private int wonCounter, projCounter;
	
	public void init() 
	{
		try
		{
			super.init();
			
			smallMain     = Image2D.loadImages(new File("res/general/smallmain"),true);
			bossexpImg    = Image2D.loadImages(new File("res/awfulplace/bossexp"));
			itemImg		  = Image2D.loadImages(new File("res/sandopolis/magic"));
			flamerImg     = Image2D.loadImages(new File("res/sandopolis/flamer"),true);
			flamer2Img    = Image2D.loadImages(new File("res/sandopolis/flamer2"),true);
			windImg    	  = Image2D.loadImages(new File("res/sandopolis/wind"));
			expImg    	  = Image2D.loadImages(new File("res/sandopolis/explosion"));
			impactImg  	  = Image2D.loadImages(new File("res/clubber/impact"));
			trailerImg 	  = Image2D.loadImages(new File("res/clubber/trailer"));
			gunfireImg    = Image2D.loadImages(new File("res/clubber/gunfire"));
			sunAngryImg	  = Image2D.loadImages("res/sandopolis/sun/angry1.png", "res/sandopolis/sun/angry2.png", "res/sandopolis/sun/angry3.png");
			blockImg      = new Image2D("res/sandopolis/block.png");
			spikeballImg  = new Image2D("res/sandopolis/spikeball.png");
			smallSpikeImg = new Image2D("res/sandopolis/smallspike.png");
			sunImg		  = new Image2D("res/sandopolis/sun/sunhappy.png");
			sunPissedImg  = new Image2D("res/sandopolis/sun/pissed.png");
			sunEyeImg     = new Image2D("res/sandopolis/sun/eye.png");
			cannonImg     = new Image2D("res/sandopolis/cannon.png");
			missileImg    = new Image2D("res/sandopolis/missile.png");
			blowingImg    = new Image2D("res/sandopolis/blowing.png");
			chillingImg   = new Image2D("res/sandopolis/chilling.png");
			stoneImg   	  = new Image2D("res/sandopolis/rock.png");
			powerupImg    = new Image2D("res/sandopolis/strength.png");
			projImg    	  = new Image2D("res/sandopolis/projectile.png");
			
			explodesound  = TinySound.loadSound(new File(("res/clubber/explode.wav")));
			firesound 	  = TinySound.loadSound(new File(("res/clubber/fire.wav")));
			collect	      = TinySound.loadSound(new File(("res/general/collect1.wav")));
			pushed   	  = TinySound.loadSound(new File(("res/flyingb/pushed.wav")));
			bossdie       = TinySound.loadSound(new File(("res/awfulplace/bossdie.wav")));
			weap2fire     = TinySound.loadSound(new File(("res/sandopolis/weapon2fire.wav")));
			weap2exp      = TinySound.loadSound(new File(("res/sandopolis/weapo2explosion.wav")));
			lasercharge   = TinySound.loadSound(new File(("res/sandopolis/lasercharge.wav")));
			laserattack   = TinySound.loadSound(new File(("res/sandopolis/laserattack.wav")));
			
			setStageMusic("res/sandopolis/song.ogg", 6f, .5f);
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
		 */
		super.build();
		lethalDamage = -5;
		haveFired1 = haveFired2 = haveFired3 = antiBlowDone = won = pissed = false;

		/*
		 * Main Character
		 */
		gm.addTileEvent(Factory.slipperWalls(gm));
		gm.addTileEvent(new TileEvent()
		{	
			@Override
			public void eventHandling(byte tileType) 
			{
				if(tileType == Engine.LETHAL)
				{
					gm.setVisible(false);
					gm.setState(CharacterState.DEAD);
				}
			}
		});
		gm.hit(2);
		gm.setHitEvent(new HitEvent()
		{	
			@Override
			public void eventHandling(GameObject hitter) 
			{
				if(hitter.sameAs(weapon1) || hitter.sameAs(proj))
					gm.hit(-1);
				if(gm.getHP() <= 0)
				{
					gm.setVisible(false);
					gm.setState(CharacterState.DEAD);
				}
			}
		});
		
		/*
		 * Flamers
		 */
		PathDrone flamer1 = new PathDrone(1287, 610);
		flamer1.setHitbox(Hitbox.EXACT);
		flamer1.setImage(7, flamerImg);
		flamer1.setMultiFaced(true);
		flamer1.appendPath(1287, 610);
		flamer1.appendPath(1017, 610);
		flamer1.setMoveSpeed(0.7f);
		flamer1.addEvent(Factory.hitMain(flamer1, gm, -1));
		
		PathDrone flamer2 = flamer1.getClone(1017, 610);
		flamer2.addEvent(Factory.hitMain(flamer2, gm, -1));
		
		PathDrone flamer3 = flamer1.getClone(937, 652);
		flamer3.clearData();
		flamer3.appendPath(937, 652);
		flamer3.appendPath(567, 652);
		flamer3.addEvent(Factory.hitMain(flamer3, gm, -1));
		
		PathDrone flamer4 = flamer3.getClone(567, 652);
		flamer4.addEvent(Factory.hitMain(flamer4, gm, -1));
		
		PathDrone flamer5 = flamer3.getClone(700, 652);
		flamer5.addEvent(Factory.hitMain(flamer5, gm, -1));
		
		PathDrone flamer6 = flamer3.getClone(275, 334);
		flamer6.clearData();
		flamer6.appendPath(275, 334);
		flamer6.appendPath(20, 334);
		flamer6.addEvent(Factory.hitMain(flamer6, gm, -1));
		
		PathDrone fastFlame = new PathDrone(487, 464);
		fastFlame.setHitbox(Hitbox.EXACT);
		fastFlame.setImage(4, flamer2Img);
		fastFlame.setMultiFaced(true);
		fastFlame.clearData();
		fastFlame.appendPath(487, 464);
		fastFlame.appendPath(367, 464);
		fastFlame.setMoveSpeed(2);
		fastFlame.addEvent(Factory.hitMain(fastFlame, gm, -1));
		
		add(flamer1, flamer2, flamer3, flamer4, flamer5, flamer6, fastFlame);
		
		/*
		 * Large Stone Block
		 */
		SolidPlatform block = new SolidPlatform(2617, 428 + 13, gm);
		block.width = 148;
		block.height = 209;
		block.offsetX = 13;
		block.offsetY = 13;
		block.setMoveSpeed(2.2f);
		block.appendPath(2617, 428 + 13);
		block.appendPath(2737, 428 + 13);
		block.appendPath(2737, 406 + 13);
		block.appendPath(2837, 406 + 13);
		block.appendPath(2837, 428 + 13);
		block.appendPath(2953, 428 + 13);
		block.appendPath(2837, 428 + 13);
		block.appendPath(2837, 406 + 13);
		block.appendPath(2737, 406 + 13);
		block.appendPath(2737, 428 + 13);
		
		GameObject spikes = new GameObject();
		spikes.setImage(blockImg);
		spikes.addEvent(Factory.follow(block, spikes, -13, -13));
		spikes.addEvent(Factory.hitMain(spikes, gm, -1));
		
		add(block, spikes);
		
		/*
		 * Falling spike balls
		 */
		SpikeBall bigSpike = new SpikeBall(4, 4);
		bigSpike.setImage(spikeballImg);
		bigSpike.addEvent(Factory.hitMain(bigSpike, gm, -1));
		bigSpike.setHitbox(Hitbox.CIRCLE);
		add(bigSpike);
		
		SpikeBall smallSpike = new SpikeBall(6, 8);
		smallSpike.setImage(smallSpikeImg);
		smallSpike.addEvent(Factory.hitMain(smallSpike, gm, -1));
		smallSpike.setHitbox(Hitbox.CIRCLE);
		add(smallSpike);
		
		/*
		 * Ugly Sun
		 */
		sun = new MovableObject();
		sun.setImage(sunImg);
		sun.moveTo(1700, 300);
		sun.zIndex(10);
		
		eye1 = new Dummy(0,0);
		eye1.setImage(sunEyeImg);
		followEvent = Factory.follow(sun, eye1, 24, 33);
		
		eye2 = new Dummy(0,0);
		eye2.setImage(sunEyeImg);
		followEvent2 = Factory.follow(sun, eye2, 48, 33);
		
		add(eye1, eye2, sun);
		
		/*
		 * Cannons, anti sun projectile and animations
		 */
		final Particle trailer = new Particle();
		trailer.setImage(new Animation<>(2, trailerImg));
		
		Particle gunfire = new Particle();
		gunfire.setImage(new Animation<>(4, gunfireImg));
		
		final Particle impact = new Particle();
		impact.setImage(new Animation<>(4, impactImg));
		impact.setIntroSound(explodesound);
		
		antiSunMissile = new Missile(0, 0, sun);
		antiSunMissile.setImage(missileImg);
		antiSunMissile.setDisposable(true);
		antiSunMissile.setFiringSound(firesound);
		antiSunMissile.setTrailer(trailer);
		antiSunMissile.setImpact(impact);
		antiSunMissile.setProperties(MissileProperties.FAST_VERY_FLOATY);
		
		final GameObject leftCannon = new GameObject();
		leftCannon.setImage(cannonImg);
		leftCannon.loc.x = 4233;
		leftCannon.loc.y = 462;
		leftCannon.addEvent(new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(!haveFired1 && leftCannon.collidesWith(gm))
				{
					haveFired1 = true;
					
					add(antiSunMissile.getClone(4236, 462));
					add(antiBlow);
				}
			}
		});
		
		final GameObject rightCannon = new GameObject();
		rightCannon.setImage(cannonImg);
		rightCannon.loc.x = 17;
		rightCannon.loc.y = 339;
		rightCannon.addEvent(new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(!haveFired2 && rightCannon.collidesWith(gm))
				{
					haveFired2 = true;
					add(antiSunMissile.getClone(17, 339));
				}
			}
		});
		
		final GameObject middleCannon = new Dummy(2009, 883);
		middleCannon.setImage(cannonImg);
		middleCannon.loc.x = 2009;
		middleCannon.loc.y = 883;
		middleCannon.addEvent(new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(!haveFired3 && middleCannon.collidesWith(gm))
				{
					haveFired3 = true;
					add(antiSunMissile.getClone(2009, 883));
				}
			}
		});
		
		add(leftCannon, rightCannon, middleCannon);
		
		/*
		 * Wind blower and anti blow missile
		 */
		final Wind wind = new Wind(1393, 760, Direction.E, 150, 150, gm);
		wind.setImage(3, windImg);
		
		blower = new GameObject();
		blower.loc.x = 1353;
		blower.loc.y = 760;
		blower.setImage(blowingImg);
		blower.addEvent(Factory.hitMain(blower, gm, -1));
		blower.setHitEvent(new HitEvent() 
		{	
			@Override
			public void eventHandling(GameObject hitter) 
			{
				if(hitter.sameAs(antiBlow))
				{
					blower.setImage(chillingImg);
					discard(wind);
				}
			}
		});
		
		antiBlow = new Missile(4236, 462, new GameObject[0]);
		antiBlow.setImage(missileImg);
		antiBlow.setDisposable(true);
		antiBlow.setTrailer(trailer);
		antiBlow.setImpact(impact);
		antiBlow.setProperties(MissileProperties.FAST_VERY_FLOATY);
		antiBlow.setTarget(2844, 108);
		antiBlow.addOtherTarget(blower);
		
		add(blower, wind);
		
		/*
		 * Sun changing when getting hit
		 */
		sun.setHitEvent(new HitEvent()
		{
			int counter = 0;
			
			@Override
			public void eventHandling(GameObject hitter) 
			{
				if(hitter.sameAs(antiSunMissile))
				{
					counter++;
					
					if(counter == 1)
					{
						sun.setImage(6, sunAngryImg);
						followEvent  = Factory.follow(sun, eye1, 24, 28);
						followEvent2 = Factory.follow(sun, eye2, 48, 28);
						add(weapon1);
					}
					else if(counter == 2)
					{
						sun.setImage(sunPissedImg);
						pissed = true;
					}
					else if(counter == 3)
					{
						discard(sun);
						discard(eye1);
						discard(eye2);
						discard(weapon1);
						
						Particle bossExp = new Particle(sun.loc.x - 100, sun.loc.y - 100, new GameObject[0]);
						bossExp.setImage(1, bossexpImg);
						bossExp.setIntroSound(bossdie);
						add(bossExp);
						won = true;
						pissed = false;
					}
				}
			}
		});
		
		/*
		 * Suns weapons
		 */
		weapon1 = new LaserDrone(0, 0, 100, 5, 50, gm);
		weapon1.setImage(sunEyeImg);
		weapon1.addEvent(Factory.follow(sun, weapon1, sun.width / 2, sun.height / 2));
		weapon1.setStartupSound(lasercharge);
		weapon1.setFiringSound(laserattack);
		
		Particle explosion = new Particle();
		explosion.setImage(3, expImg);
		explosion.setIntroSound(weap2exp);
		
		proj = new Projectile(0, 0, gm);
		proj.setImage(projImg);
		proj.setImpact(explosion);
		proj.setFiringSound(weap2fire);
		proj.setMoveSpeed(10);
		proj.setDisposable(true);
		
		/*
		 * Item
		 */
		final GameObject item = new GameObject();
		item.loc.x = 70;
		item.loc.y = 342;
		item.setImage(5, itemImg);
		item.addEvent(new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(item.collidesWith(gm))
				{
					discard(item);
					gm.setImage(3, smallMain);
					gm.setMultiFaced(true);
					collect.play();
				}
			}
		});
		add(item);
		
		/*
		 * Stone block and block destroyer(the item)
		 */
		final PushableObject stone = new PushableObject(1980, 813, gm);
		stone.setImage(stoneImg);
		stone.setPushLength(0);
		stone.setPushStrength(0);
		stone.setPushingSound(pushed, 10);
		stone.mustStand(true);
		proj.avoidOverlapping(stone);
		
		final GameObject powerup = new GameObject();
		powerup.loc.x = 2848;
		powerup.loc.y = 952;
		powerup.setImage(powerupImg);
		powerup.addEvent(new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(powerup.collidesWith(gm))
				{
					discard(powerup);
					collect.play();
					stone.setPushLength(1);
					stone.setPushStrength(1);
				}
			}
		});
		
		add(stone, powerup);
	}
	
	@Override
	public void extra() 
	{
		if(won && ++wonCounter % 200 == 0)
		{
			gm.setState(CharacterState.FINISH);
			return;
		}
		
		if(10 < Fundementals.distance(sun.loc.x, 1, gm.loc.x, 1))
			sun.moveToward(gm.loc.x, gm.loc.y - 300, 3);
		
		followEvent.eventHandling();
		eye1.rotation = Fundementals.rotateTowardsPoint(eye1.loc.x, eye1.loc.y, gm.loc.x, gm.loc.y, eye1.rotation, 0.11f);
		
		followEvent2.eventHandling();
		eye2.rotation = Fundementals.rotateTowardsPoint(eye2.loc.x, eye2.loc.y, gm.loc.x, gm.loc.y, eye2.rotation, 0.11f);
	
		if(!antiBlowDone && 100 > Fundementals.distance(antiBlow.loc.x, antiBlow.loc.y, 2844, 108))
		{
			antiBlow.setTarget(1393, 760);
			antiBlowDone = true;
		}
		
		if(pissed && sun.canSee(gm, GameObject.Accuracy.MID_CORNERS) && ++projCounter % 50 == 0)
			add(proj.getClone(sun.loc.x + sun.width / 2, sun.loc.y + sun.height / 2));
	}
	
	@Override
	public void dispose() 
	{
		super.dispose();
		disposeBatch(flamerImg, flamer2Img, blockImg, spikeballImg, smallSpikeImg, sunImg, sunAngryImg, sunPissedImg, sunEyeImg, cannonImg, missileImg, impactImg, trailerImg, gunfireImg, blowingImg, chillingImg, windImg, itemImg, smallMain, stoneImg, powerupImg, bossexpImg, projImg, expImg, explodesound, firesound, collect, pushed, bossdie, lasercharge, laserattack, weap2fire, weap2exp);
	}

	static class SpikeBall extends Enemy
	{
		float mass, gravity, damping, vy, rotationSpeed;
		boolean fallingDown;
		
		SpikeBall(float moveSpeed, float rotationSpeed)
		{
			mass = 1.0f;
			gravity = -900;
			damping = 0.0001f;
			
			this.rotationSpeed = rotationSpeed;
			setMoveSpeed(moveSpeed);
			moveTo(Stage.getCurrentStage().size.width, 425);
			tryDown(33);
		}
		
		@Override
		public void moveEnemy() 
		{
			if(canGoDown() || (fallingDown && loc.y < Stage.getCurrentStage().size.height))
			{
				vy *= 1.0 - (damping * DELTA);
			    float force = mass * gravity;
			    vy += (force / mass) * DELTA;
			    
			    loc.y -= vy * DELTA;
			}
			else
				vy = 0;
			
			if(!fallingDown)
			{
				if(loc.x < 3114)
					fallingDown = true;
				else
				{
					moveToward(0, loc.y);
					rotation -= rotationSpeed;
				}
				
			}
			else if(loc.y > Stage.getCurrentStage().size.height)
			{
				fallingDown = false;
				loc.x = Stage.getCurrentStage().size.width;
				loc.y = 425;
				tryDown(33);
			}
		}
	}
}