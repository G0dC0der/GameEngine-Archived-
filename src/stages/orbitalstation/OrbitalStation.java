package stages.orbitalstation;

import static game.core.Engine.*;
import game.core.Engine;
import game.core.Engine.Direction;
import game.core.Engine.GameState;
import game.core.Fundementals;
import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.GameObject.Hitbox;
import game.core.MainCharacter.CharacterState;
import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.AutoLoad;
import game.development.StageBuilder;
import game.essentials.Animation;
import game.essentials.CameraEffect;
import game.essentials.Factory;
import game.essentials.GFX;
import game.essentials.Image2D;
import game.essentials.LaserBeam;
import game.essentials.SoundBank;
import game.movable.EvilDog;
import game.movable.PathDrone;
import game.movable.PathDrone.PathData;
import game.movable.Projectile;
import game.movable.RotatingCannon;
import game.movable.Shuttle;
import game.movable.SolidPlatform;
import game.movable.TargetLaser;
import game.objects.CheckpointsHandler;
import game.objects.OneWay;
import game.objects.Particle;
import game.objects.Wind;
import java.io.File;
import java.util.LinkedList;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

@AutoDispose
@AutoInstall(mainPath="res/general",path=OrbitalStation.PATH)
@Playable(name="Orbital Station", description="Stage: Orbital Station\nAuthor: Pojahn Moradi\nDifficulty: 8\nAverage time: 230 sec\nProfessional time: 180 sec\nObjective: Enter goal.")
public class OrbitalStation extends StageBuilder
{
	static final String PATH = "res/orbitalstation";
	
	{
		setDifficulty(Difficulty.NORMAL);
	}
	
	@AutoLoad(path=PATH, type=VisualType.WAYPOINT)
	private PathData[] data1, data2;

	@AutoLoad(path=PATH, type=VisualType.IMAGE)
	private Image2D alphaImage, lava[], platform, lavaenemy[], mine[], fireball[], saw, holder, spikes, gunmachine, bullet, elevator, spaceGate, guard[], crusherLeft, part, crusherUp, crusherDown, button, buttonbody, fan[], suit[], suitItem, gem[], laserbeam, megacrusher, trapblock, exp[];
	
	@AutoLoad(path=PATH, type=VisualType.SOUND)
	private Sound guarding, slam, gmfire, gmexplode, powerdown, laserexp;

	@AutoLoad(path=PATH, type=VisualType.MUSIC)
	private Music blowingFan;
	
	private Image2D oneway, explosion[], gunfire[], key, wind[], bigExplosion[];
	private Music stageMusic, spaceMusic, lavaMusic, sawLoop, collapsing;
	private Sound elevatorMove, elevatorDone, collect, mineExplode, shut;
	
	private GameObject heatOverlay;
	private SolidPlatform megac;
	
	private CheckpointsHandler cph;
	
	private float orginY = 105 * 32, maxAlpha, minAlpha;
	private boolean alphaValueIncreasing, suited, unfreezeSaws, goNuts, used, used2, poweredDown;
	
	@Override
	public void init() 
	{
		super.init();
		
		oneway = new Image2D("res/blocks/platformImg.png");
		key = new Image2D("res/shroom/key.png");
		explosion = Image2D.loadImages(new File("res/awfulplace/smallexp"));
		bigExplosion = Image2D.loadImages(new File("res/awfulplace/bossexp"));
		gunfire = Image2D.loadImages(new File("res/flyingb/fireanim"));
		wind = Image2D.loadImages(new File("res/sandopolis/wind"));
		
		elevatorMove = TinySound.loadSound(new File("res/steelfactory/elevator.wav"));
		elevatorDone = TinySound.loadSound(new File("res/steelfactory/bam_.wav"));
		shut = TinySound.loadSound(new File("res/steelfactory/shut.wav"));
		collapsing = TinySound.loadMusic(new File("res/steelfactory/collapsing.wav"));
		collect = TinySound.loadSound(new File("res/steelfactory/collectsound.wav"));
		mineExplode = TinySound.loadSound(new File("res/awfulplace/bossdie.wav"));
		
		stageMusic = TinySound.loadMusic(new File(PATH + "/song.ogg"), true);
		stageMusic.setLoopPositionBySeconds(1.63f);
		stageMusic.setLoop(true);
		
		lavaMusic = TinySound.loadMusic(new File(PATH + "/mines.ogg"), true);
		lavaMusic.setLoopPositionBySeconds(14.95f);
		lavaMusic.setLoop(true);
		
		spaceMusic = TinySound.loadMusic(new File(PATH + "/space.ogg"), true);
		spaceMusic.setLoopPositionBySeconds(4.48f);
		spaceMusic.setLoop(true);
		
		sawLoop = TinySound.loadMusic(new File("res/traning5/sawLoop.wav"), true);
	}
	
	@Override
	public void build() 
	{
		/*
		 * Reseting
		 */
		super.build();
		alphaValueIncreasing = true;
		suited = unfreezeSaws = goNuts = used = used2 = poweredDown = false;
		maxAlpha = minAlpha = 0;
		
		elevatorMove.stop();
		collapsing.stop();
		sawLoop.stop();
		sawLoop.setVolume(0);
		sawLoop.setLoop(true);
		blowingFan.setVolume(0);
		blowingFan.play(true);
		spaceMusic.pause();
		lavaMusic.pause();
		stageMusic.setVolume(.7f);
		if(!stageMusic.playing())
			stageMusic.play(true);
		
		/*
		 * Alpha
		 */
		heatOverlay = new GameObject(){
			@Override
			public void drawSpecial(SpriteBatch batch) 
			{
				if(heatOverlay.alpha > 0.0f)
				{
					Color orgColor = batch.getColor();
					
					batch.setColor(orgColor.r, orgColor.g, orgColor.b, heatOverlay.alpha);
					game.hudCamera();
					batch.draw(alphaImage, 0, 0);
					game.gameCamera();
					batch.setColor(orgColor.r, orgColor.g, orgColor.b, orgColor.a);
				}
			}
		};
		heatOverlay.alpha = 0;
		heatOverlay.zIndex(500);
		add(heatOverlay);
		
		/*
		 * Lava
		 */
		Animation<Image2D> lavaImage = new Animation<>(7, lava);
		GameObject theLava = new GameObject()
		{
			@Override
			public void drawSpecial(SpriteBatch batch) 
			{
				Image2D img = lavaImage.getObject();
				
				for(int i = 0, x = 0; i < 310; i++, x += img.getWidth())
					batch.draw(img, x, 3936);
			}
		};
		theLava.zIndex(101);
		add(theLava);
		
		/*
		 * Bottom One Way
		 */
		OneWay ow = new OneWay((81 * 32) + 16, 117 * 32, Direction.N, gm);
		ow.setImage(oneway);
		ow.width = 32 * 2;
		ow.alpha = .5f;
		add(ow);
		
		/*
		 * Bottom platforms
		 */
		SolidPlatform solp1 = new SolidPlatform(size.width, (123 * 32) - 23, gm);
		solp1.setImage(platform);
		solp1.appendPath(0 - solp1.width, solp1.currY);
		solp1.appendPath(solp1.currX, solp1.currY, 0, true, null);
		solp1.setMoveSpeed(1.5f);

		add(solp1, solp1.getClone(size.width - 2000, solp1.currY), solp1.getClone(size.width - 3500, solp1.currY));
		
		/*
		 * Rectangle moving lava enemy
		 */
		float 	x1 = 63 * 32, 
				x2 = 68 * 32, 
				y1 = 117 * 32, 
				y2 = 122 * 32;
		
		PathDrone lavae1 = new PathDrone(x1, y1);
		lavae1.setImage(7, lavaenemy);
		lavae1.setMoveSpeed(1);
		lavae1.appendPath();
		lavae1.appendPath(x2, y1);
		lavae1.appendPath(x2, y2);
		lavae1.appendPath(x1, y2);
		lavae1.addEvent(Factory.hitMain(lavae1, gm, -1));
		
		PathDrone lavae2 = new PathDrone(x2, y2);
		lavae2.setImage(7, lavaenemy);
		lavae2.setMoveSpeed(1);
		lavae2.appendPath();
		lavae2.appendPath(x1, y2);
		lavae2.appendPath(x1, y1);
		lavae2.appendPath(x2, y1);
		lavae2.addEvent(Factory.hitMain(lavae2, gm, -1));
		
		add(lavae1, lavae2);
		
		/*
		 * Lava mines
		 */
		Particle mineExp = new Particle();
		mineExp.setImage(3, bigExplosion);
		mineExp.setIntroSound(mineExplode);
		mineExp.zIndex(500);
		
		PathDrone mine1 = new PathDrone(1500, 3677);
		mine1.appendPath(data1);
		mine1.setImage(4, mine);
		mine1.setHitbox(Hitbox.EXACT);
		mine1.setMoveSpeed(2);
		mine1.addEvent(()->{
			if(mine1.collidesWith(gm))
			{
				gm.hit(-1);
				discard(mine1);
				add(mineExp.getClone(mine1.currX - mineExp.halfWidth(), mine1.currY - mineExp.halfHeight()));
			}
		});
		
		PathDrone mine2 = mine1.getClone(mine1.currX, mine1.currY);
		mine2.clearData();
		mine2.appendPath(data2);
		mine2.addEvent(()->{
			if(mine2.collidesWith(gm))
			{
				gm.hit(-1);
				discard(mine2);
				add(mineExp.getClone(mine2.currX - mineExp.halfWidth(), mine2.currY - mineExp.halfHeight()));
			}
		});
		
		add(mine1, mine2);
		
		/*
		 * Vertically moving fireballs
		 */
		FireBall fb1 = new FireBall(33 * 32, 132 * 32, 650);
		fb1.setImage(5, fireball);
		fb1.setHitbox(Hitbox.EXACT);
		fb1.addEvent(Factory.hitMain(fb1, gm, -1));
		
		FireBall fb2 = new FireBall(27 * 32, 132 * 32, 700);
		fb2.setImage(5, fireball);
		fb2.setHitbox(Hitbox.EXACT);
		fb2.addEvent(Factory.hitMain(fb2, gm, -1));
		
		FireBall fb3 = new FireBall(26 * 32, 132 * 32, 700);
		fb3.setImage(5, fireball);
		fb3.setHitbox(Hitbox.EXACT);
		fb3.addEvent(Factory.hitMain(fb3, gm, -1));
		
		FireBall fb4 = new FireBall(25 * 32, 132 * 32, 700);
		fb4.setImage(5, fireball);
		fb4.setHitbox(Hitbox.EXACT);
		fb4.addEvent(Factory.hitMain(fb4, gm, -1));
		
		FireBall fb5 = new FireBall(15 * 32, 132 * 32, 700);
		fb5.setImage(5, fireball);
		fb5.setHitbox(Hitbox.EXACT);
		fb5.addEvent(Factory.hitMain(fb5, gm, -1));
		
		FireBall fb6 = new FireBall(13 * 32, 132 * 32, 700);
		fb6.setImage(5, fireball);
		fb6.setHitbox(Hitbox.EXACT);
		fb6.addEvent(Factory.hitMain(fb6, gm, -1));
		
		add(fb1,80);
		add(fb2,fb5,fb6);
		add(fb3,10);
		add(fb4,20);
		
		/*
		 * Saws
		 */
		addSaw(337, 2706, false);
		addSaw(267, 2766, false);
		addSaw(187, 2766, false);
		addSaw(107, 2706, false);
		
		addSaw(337, 2766 - 300, true);
		addSaw(267, 2706 - 300, true);
		addSaw(187, 2706 - 300, true);
		addSaw(107, 2766 - 300, true);
		
		/*
		 * Spikes
		 */
		GameObject spk1 = new GameObject();
		spk1.moveTo(406, 2372);
		spk1.setImage(spikes);
		spk1.addEvent(Factory.hitMain(spk1, gm, -1));
		
		add(spk1);
		
		/*
		 * Gun Machines
		 */
		Particle bulletExplosion = new Particle();
		bulletExplosion.setImage(2, explosion);
		bulletExplosion.zIndex(200);
		bulletExplosion.setIntroSound(gmexplode);
		bulletExplosion.getSoundBank().useFallOff(true);
		bulletExplosion.getSoundBank().maxVolume = .6f;
		
		Particle firingAnim = new Particle();
		firingAnim.setImage(2, gunfire);
		firingAnim.zIndex(200);
		firingAnim.setIntroSound(gmfire);
		firingAnim.getSoundBank().useFallOff(true);
		firingAnim.getSoundBank().maxVolume = .6f;
		
		Projectile proj = new Projectile(0, 0, gm);
		proj.setImage(bullet);
		proj.setMoveSpeed(4);
		proj.setDisposable(true);
		proj.scanningAllowed(false);
		proj.setImpact(bulletExplosion);
		
		RotatingCannon gunm = new RotatingCannon(941, 1704, proj);
		gunm.zIndex(50);
		gunm.setImage(gunmachine);
		gunm.setFireAnimation(firingAnim);
		
		RotatingCannon gunm2 = new RotatingCannon(1218, 1704, proj);
		gunm2.zIndex(50);
		gunm2.setImage(gunmachine);
		gunm2.setFireAnimation(firingAnim);
		
		RotatingCannon gunm3 = new RotatingCannon(1495, 1704, proj);
		gunm3.zIndex(50);
		gunm3.setImage(gunmachine);
		gunm3.setFireAnimation(firingAnim);
		
		add(gunm,gunm2,gunm3);
		
		/*
		 * Elevator
		 */
		SolidPlatform elv = new SolidPlatform(1920, 2017, gm);
		elv.setImage(elevator);
		elv.appendPath(1920, 416,0,false,()->{
			elevatorMove.stop();
			elevatorDone.play();
		});
		elv.setMoveSpeed(5.5f);
		elv.freeze();
		add(elv);
		
		/*
		 * Space Gate
		 */
		SolidPlatform sGate = new SolidPlatform(4160, 416, gm);
		sGate.setImage(spaceGate);
		add(sGate);
		
		/*
		 * Key
		 */
		GameObject theKey = new GameObject();
		theKey.setImage(key);
		theKey.moveTo(272, 388);
		theKey.addEvent(()->{
			if(theKey.collidesWith(gm))
			{
				collect.play();
				discard(theKey);
				sGate.appendPath(sGate.currX - sGate.width, sGate.currY);
				goNuts = true;
			}
		});
		add(theKey);
		
		/*
		 * Space Guards
		 */
		addSpaceGuard(2006, 122, "Remove yourself! The space belong to us!");
		addSpaceGuard(456, 122, "Take that item and face annihilation.");
		addSpaceGuard(3326, 122, "We will destroy you.");
		
		/*
		 * Crushers
		 */
		LinkedList<SolidPlatform> allCrusher = new LinkedList<>();
		
		SolidPlatform crusher1 = new SolidPlatform(4028, 1152, gm);
		crusher1.setImage(crusherLeft);
		crusher1.setMoveSpeed(6);
		crusher1.appendPath(4028, crusher1.currY, 0, false, ()->{crusher1.setMoveSpeed(6);});
		crusher1.appendPath(4316, crusher1.currY, 50, false, ()->{
			crusher1.setMoveSpeed(2);
			if(Fundementals.distance(crusher1, gm) < 600)
				add(CameraEffect.vibration(1,30));
			
			double volume = SoundBank.getVolume(crusher1, gm, 600, 1.0f, 40);
			if(volume > 0.0f)
				slam.play(volume);
		});
		
		SolidPlatform part1 = new SolidPlatform(4028 - part.getWidth(), 1155, gm);
		part1.setImage(part);
		part1.setMoveSpeed(6);
		part1.appendPath(4028 - part1.width, part1.currY, 0, false, ()->{part1.setMoveSpeed(6);});
		part1.appendPath(4316 - part1.width, part1.currY, 50, false, ()->{part1.setMoveSpeed(2);});
		
		add(crusher1, part1);
		allCrusher.add(crusher1);
		allCrusher.add(part1);
		
		SolidPlatform crusher2 = new SolidPlatform(4065, 1920, gm);
		crusher2.setImage(crusherUp);
		crusher2.setMoveSpeed(6);
		crusher2.appendPath(4065, 1504, 50, false, ()->{
			crusher2.setMoveSpeed(3);

			if(Fundementals.distance(crusher2, gm) < 600)
				add(CameraEffect.vibration(1,30));
			
			double volume = SoundBank.getVolume(crusher2, gm, 600, 1.0f, 40);
			if(volume > 0.0f)
				slam.play(volume);
		});
		crusher2.appendPath(4065, 1920, 20, false, ()->{crusher2.setMoveSpeed(6.5f);});
		
		add(crusher2);
		allCrusher.add(crusher2);
		
		for(int i = 0, x = 4372, y = 2210; i < 2; i++, x += (crusherDown.getWidth() * 2))
		{
			SolidPlatform cr = new SolidPlatform(x, y, gm);
			cr.setImage(crusherDown);
			cr.setMoveSpeed(6);
			cr.appendPath(cr.currX, cr.currY, 50, false, null);
			cr.appendPath(cr.currX, 2272, 50, false, ()->{
				if(Fundementals.distance(cr.currX, cr.currY + cr.height, gm.currX, gm.currY) < 300)
					add(CameraEffect.vibration(.5f,30));
				
				double volume = SoundBank.getVolume(cr.currX, cr.currY + cr.height, gm.currX, gm.currY, 600, 1.0f, 40);
				if(volume > 0.0f)
					slam.play(volume);
			});
			
			add(cr);
			allCrusher.add(cr);
		}
		
		for(int i = 0, x = 4107, y = 2036; i < 7; i++, x -= crusherDown.getWidth())
		{
			SolidPlatform cr = new SolidPlatform(x, y, gm);
			cr.setImage(crusherDown);
			cr.setMoveSpeed(4.5f);
			cr.appendPath(cr.currX, cr.currY, 10, false, ()->{cr.setMoveSpeed(7);});
			cr.appendPath(cr.currX, 2272, 130, false, ()->{
				cr.setMoveSpeed(5);
				if(Fundementals.distance(cr.currX, cr.currY + cr.height, gm.currX, gm.currY) < 400)
					add(CameraEffect.vibration(.5f,30));
				
				double volume = SoundBank.getVolume(cr.currX, cr.currY + cr.height, gm.currX, gm.currY, 600, 1.0f, 40);
				if(volume > 0.0f)
					slam.play(volume);
			});
			
			add(cr, i * 20);
			allCrusher.add(cr);
		}
		
		/*
		 * Propeller & Wind
		 */
		GameObject prop = new GameObject();
		prop.setImage(2, fan);
		prop.moveTo(384, 3264);
		
		Wind theWind = new Wind(423, 3265, Direction.E, 10, 70, gm);
		theWind.setImage(4, wind);
		theWind.height = 96;
		theWind.width = 550;
		theWind.zIndex(1);
		
		add(Factory.soundFalloff(blowingFan, prop, gm, 800, 0, 30, 1.0f));
		add(prop, theWind);
		
		/*
		 * Laser beams
		 */
		Shuttle target1 = new Shuttle(2575 - 100, 2795);
		target1.appendPath();
		target1.appendPath(2575 + 100, 2795);
		target1.drag = 1.2f;
		target1.thrust = 400;
		
		TargetLaser laser1 = getTargetLaser(2554, 2625, target1);
		
		GameObject target2 = new GameObject();
		target2.moveTo(2087 + laser1.halfWidth(), 2573 + 100);
		
		TargetLaser laser2 = getTargetLaser(2087, 2573, target2);
		laser2.addEvent(new Event()
		{
			int counter = 0;

			@Override
			public void eventHandling() 
			{
				if(poweredDown)
					laser2.stop(true);
				else if(++counter % 50 == 0)
					laser2.stop(!laser2.stopped());
				
			}
		});
		
		GameObject target3 = new GameObject();
		target3.moveTo(1913 - 100, 2651 + laser2.halfHeight());
		
		TargetLaser laser3 = getTargetLaser(1913, 2651, target3);
		laser3.addEvent(new Event()
		{
			int counter = 0;

			@Override
			public void eventHandling() 
			{
				if(poweredDown)
					laser3.stop(true);
				else if(++counter % 110 == 0)
					laser3.stop(!laser3.stopped());
			}
		});
		
		add(target1, laser1, target2, laser2, target3, laser3);
		laser1.merge(laser2,laser3);
		
		/*
		 * Button & Button body
		 */
		GameObject bbody = new GameObject();
		bbody.setImage(buttonbody);
		bbody.moveTo(2976, 2203);
		gm.avoidOverlapping(bbody);

		GameObject theButton = new GameObject();
		theButton.setImage(button);
		theButton.moveTo(2979, 2192);
		theButton.addEvent(()->{
			if(theButton.collidesWith(gm))
			{
				powerdown.play();
				discard(theButton, theWind);
				prop.getImage().stop(true);
				gm.freeze();
				game.removeFocusObject(gm);
				stageMusic.setVolume(.25f);
				blowingFan.stop();
				poweredDown = true;
				laser1.stop(true);
				for(SolidPlatform crushe : allCrusher)
					crushe.freeze();
				
				PathDrone camera = new PathDrone(gm.centerX(), gm.centerY());
				camera.setMoveSpeed(7);
				camera.appendPath(1593, 2689);
				camera.appendPath(1003, 2719);
				camera.appendPath(1003, 3319);
				camera.appendPath(513, 3319, 99999, false, ()->{
					add(new Event()
					{
						boolean done;
						
						@Override
						public void eventHandling() 
						{
							game.removeFocusObject(camera);
							game.addFocusObject(gm);
							gm.unfreeze();
							stageMusic.setVolume(.7);
							done = true;
						}
						
						@Override
						public boolean done()
						{
							return done;
						}
					}, 60);
				});
				
				add(camera, 60);
				
				game.addFocusObject(camera);
			}
		});
		
		add(bbody, theButton);
		
		/*
		 * Suit Item
		 */
		GameObject item = new GameObject();
		item.setImage(suitItem);
		item.moveTo(497, 3346);
		item.addEvent(()->{
			if(item.collidesWith(gm))
			{
				discard(item);
				suited = true;
				gm.setImage(new Animation<>(3, suit));
				gm.setMultiFaced(true);
				collect.play();
			}
		});
		add(item);
		
		/*
		 * Gem
		 */
		GameObject goal = new GameObject();
		goal.setImage(4, gem);
		goal.moveTo(24, 3826);
		goal.addEvent(()->{
			if(goal.collidesWith(gm))
			{
				discard(goal);
				gm.setState(CharacterState.FINISH);
				collect.play();
			}
		});
		add(goal);
		
		/*
		 * Trap Block
		 */
		SolidPlatform trapb = new SolidPlatform(1600, 2590, gm);
		trapb.setImage(trapblock);
		trapb.setTileDeformer(true, Engine.SOLID);
		trapb.setTransformBack(true);
		trapb.setMoveSpeed(2);
		trapb.appendPath(1600, 2656);
		trapb.freeze();
		add(trapb);
		
		/*
		 * Mega Crusher
		 */
		CameraEffect vib = CameraEffect.vibration(2, -1);
		
		megac = new SolidPlatform(size.width, size.height, gm);
		megac.setImage(megacrusher);
		megac.setMoveSpeed(6.5f);
		megac.appendPath(1664, 2464,0,false,()->{
			shut.play();
			collapsing.stop();
			vib.stop();
		});
		
		/*
		 * Checkpoints
		 */
		Vector2 cp1 = new Vector2(548, 2764);
		Vector2 cp2 = new Vector2(1808, 1996);
		Vector2 cp3 = new Vector2(4195, 620);
		Vector2 cp4 = new Vector2(517, 3340);
		
		if(cph == null && getDifficulty() != Difficulty.HARD)
		{
			cph = new CheckpointsHandler();
			cph.setReachEvent(()->{
				GFX.renderCheckpoint();
			});
			
			if(getDifficulty() == Difficulty.EASY)
				cph.appendCheckpoint(cp1, 480, 2687, 150, 150);
			
			cph.appendCheckpoint(cp2, 1777, 1946, 70, 70);
			
			if(getDifficulty() == Difficulty.EASY)
				cph.appendCheckpoint(cp3, 4155, 570, 70, 70);
			
			cph.appendCheckpoint(cp4, 384, 3264, 150, 150);
		}
		
		if(cph != null)
		{
			cph.setUsers(gm);
			
			Vector2 latestCp = cph.getLastestCheckpoint();
			if(latestCp != null)
			{
				gm.moveTo(latestCp.x, latestCp.y);
	
				if(latestCp.equals(cp4))
				{
					discard(theButton, theWind);
					poweredDown = true;
					laser1.stop(true);
				}
			}
			
			add(cph);
		}
		
		/*
		 * Main Character
		 */
		game.timeColor = Color.WHITE;
		gm.facing = Direction.W;
		if(getDifficulty() != Difficulty.HARD)
			gm.hit(2);
		gm.addTileEvent(Factory.slipperWalls(gm));
		gm.addTileEvent((tileType)->{
			if(tileType == Engine.AREA_TRIGGER_2)
				unfreezeSaws = true;
			else if(tileType == Engine.AREA_TRIGGER_3)
			{
				if(!used)
				{
					used = true;
					elevatorMove.play();
				}
				elv.unfreeze();
			}
			else if(tileType == Engine.AREA_TRIGGER_4)
				gm.gravity = -200;
			else if(tileType == Engine.AREA_TRIGGER_5)
			{
				gm.gravity = -500;
				sGate.clearData();
				sGate.appendPath(4160, 416);
				goNuts = false;
			}
		});
		gm.setHitEvent((hitter)->{
			if(hitter.sameAs(proj))
				gm.hit(-1);
			else if(hitter.sameAs(laser1))
			{
				if(!used2)
				{
					trapb.unfreeze();
					collapsing.setVolume(.6);
					collapsing.play(true);
					add(vib);
					
					megac.moveTo(2730, 2464);
					add(megac);
					used2 = true;
				}
			}
		});
	}
	
	@Override
	protected boolean isSafe() 
	{
		return cph != null && cph.getLastestCheckpoint() != null;
	}
	
	@Override
	public void extra() 
	{
		/*
		 * Suit Check
		 */
		if(!suited && gm.currY - (orginY + 250) > 0)
			gm.hit(-1);
		
		/*
		 * Alpha image
		 */
		float distance = gm.currY - orginY;
		
		if(distance > 0)
		{
			maxAlpha = Math.min(distance / 300.0f, .85f);
			minAlpha = Math.max(maxAlpha - 0.2f, 0.0f);

			if(alphaValueIncreasing)
			{
				if(heatOverlay.alpha >= maxAlpha)
					alphaValueIncreasing = false;
				else
					heatOverlay.alpha += 0.005f;
			}
			else
			{
				if(heatOverlay.alpha <= minAlpha)
					alphaValueIncreasing = true;
				else
					heatOverlay.alpha -= 0.005f;
			}
		}
		else if(heatOverlay.alpha > 0.0f)
			heatOverlay.alpha -= 0.005f;
		
		/*
		 * Lava song and stage music fading 
		 */
		if(distance > 0)
		{
			//Fading stage music
			float newVolume = 20.0f / distance;
			if(0.1f > newVolume)
				newVolume = 0;
			else if(newVolume > 0.7f)
				newVolume = 0.7f;
			
			stageMusic.setVolume(newVolume);
			
			if(newVolume == 0.0f && stageMusic.playing())
				stageMusic.pause();
			else if(newVolume > 0.0f && !stageMusic.playing())
				stageMusic.resume();
			
			//Fading lava song
			float orginY2 = 109 * 32;
			float distance2 = gm.currY - orginY2;
			
			if(distance2 > 0)
			{
				newVolume = Math.min(distance2 / 100.0f, .7f);
				newVolume = Math.max(newVolume, 0.0f);
				
				if(newVolume == 0.0f && lavaMusic.playing())
					lavaMusic.pause();
				else if(newVolume > 0 && !lavaMusic.playing())
					lavaMusic.resume();
				
				lavaMusic.setVolume(newVolume);
			}
		}
		
		/*
		 * Fading stage music in space
		 */
		float height = 600;
		if(Fundementals.rectangleVsRectangle(1920, 416, 64, height, gm.currX, gm.currY, gm.width(), gm.height()))
		{
			float orginY = 416 + height, newVolume;
			distance = orginY - gm.currY;
			
			if(distance > 0)
			{
				newVolume = Math.min(30.0f / distance, .7f);
				newVolume = Math.max(newVolume, 0.0f);
				
				if(0.1f > newVolume)
					newVolume = 0;
				
				if(newVolume == 0)
					stageMusic.pause();
				
				stageMusic.setVolume(newVolume);
			}
		}
		
		/*
		 * Fading space song
		 */
		float orginY = 700, newVolume;
		distance = orginY - gm.currY;
		if(distance > 0)
		{
			newVolume = Math.min(distance / 500.0f, 1.0f);
			newVolume = Math.max(newVolume, 0.0f);
			
			if(!spaceMusic.playing() && newVolume > 0)
				spaceMusic.resume();
			else if(spaceMusic.playing() && newVolume == 0)
				spaceMusic.pause();
			
			spaceMusic.setVolume(newVolume);
			
			/*
			 * Fade Stage Music after space
			 */
			if(gm.currX > 3500)
			{
				newVolume = Math.min(10.0f / distance, .7f);
				newVolume = Math.max(newVolume, 0.0f);
				
				if(0.1f > newVolume)
					newVolume = 0;
				
				if(newVolume == 0.0f)
					stageMusic.pause();
				if(newVolume > 0.0f && !stageMusic.playing())
					stageMusic.resume();
				stageMusic.setVolume(newVolume);
			}
		}
		
		if(unfreezeSaws)
		{
			double volume = SoundBank.getVolume(gm.currX, gm.currY, 247, 2310, 800, 1.0f, 30);
			if(volume > 0.0f && !sawLoop.playing())
				sawLoop.resume();
			else if(volume == 0.0f && sawLoop.playing())
				sawLoop.pause();
			
			sawLoop.setVolume(volume);
		}
	}
	
	TargetLaser getTargetLaser(float x, float y, GameObject laserTarget)
	{
		TargetLaser laser = new TargetLaser(x, y, laserTarget, gm);
		laser.setImage(laserbeam);
		laser.useSpecialEffect(true);
		laser.setLaserTint(Color.GREEN);
		laser.setDrawSpecialBehind(true);
		laser.setBeam(getLaser());
		laser.useFastCollisionCheck(true);
		laser.addEvent(()->{
			if(laser.collidesWith(megac))
			{
				discard(laser, laserTarget);
				
				Particle e = new Particle();
				e.setImage(4, exp);
				e.setIntroSound(laserexp);
				e.moveTo(laser.centerX() - e.halfWidth(), laser.centerY() - e.halfHeight());
				
				add(e);
			}
		});
		
		return laser;
	}
	
	void addSpaceGuard(float x, float y, String text)
	{
		EvilDog ed = new EvilDog(x, y, 5000, gm);
		ed.drag = .13f;
		ed.thrust = 130;
		ed.freeze();
		ed.setHitbox(Hitbox.EXACT);
		ed.setImage(4, guard);
		ed.zIndex(1000);
		ed.addEvent(Factory.hitMain(ed, gm, -1));
		ed.addEvent(new Event()
		{
			boolean talked;
			int counter = 0;
			int talkFreq = MathUtils.random(350, 600);
			
			@Override
			public void eventHandling() 
			{
				if(goNuts && game.getGlobalState() == GameState.ONGOING)
				{
					ed.unfreeze();
					
					if(++counter % talkFreq == 0)
						guarding.play();
				}
				else
					ed.freeze();
				
				if(!talked && !goNuts && Fundementals.distance(ed, gm) < 305)
				{
					guarding.play();
					talked = true;
					add(Factory.printText(text, Color.WHITE, null, 400, ed, -250, ed.height(), null));
				}
			}
		});
		
		add(ed);
	}
	
	void addSaw(float x, float y, boolean flippedY)
	{
		PathDrone h = new PathDrone(x, (flippedY) ? y - holder.getHeight() : y);
		h.setMoveSpeed(.7f);
		h.setImage(holder);
		h.appendPath(x, h.currY - ((flippedY) ? 420 : 550));
		h.addEvent(()->{
			if(unfreezeSaws)
				h.unfreeze();
		});
		h.freeze();
		h.zIndex(5);
		h.flipY = flippedY;
		
		GameObject s = new GameObject();
		s.addEvent(Factory.follow(h, s, -10, (flippedY) ? 670 : -12));
		s.addEvent(Factory.hitMain(s, gm, -10));
		s.addEvent(()->{
			if(unfreezeSaws)
				s.rotation += 10;
		});
		s.setHitbox(Hitbox.CIRCLE);
		s.setImage(saw);
		
		add(h,s);
	}
	
	static LaserBeam getLaser()
	{
		Animation<Image2D> laserImage = new Animation<>(3, LASER_BEAM);
		Animation<Image2D> laserImpact = new Animation<>(3, LASER_IMPACT);
		laserImage.pingPong(true);
		laserImpact.pingPong(true);
		return Factory.threeStageLaser(null, laserImage, laserImpact);
	}
}
