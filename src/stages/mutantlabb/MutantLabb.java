package stages.mutantlabb;

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
import game.essentials.BigImage.RenderOption;
import game.essentials.CameraEffect;
import game.essentials.Factory;
import game.essentials.GFX;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import game.movable.EvilDog;
import game.movable.LaserDrone;
import game.movable.Missile;
import game.movable.PathDrone;
import game.movable.PathDrone.PathData;
import game.movable.Shuttle;
import game.movable.SolidPlatform;
import game.movable.TimedEnemy;
import game.movable.Weapon;
import game.objects.CheckpointsHandler;
import game.objects.OneWay;
import game.objects.Particle;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

@AutoDispose
@AutoInstall(mainPath="res/general", path=MutantLabb.PATH)
@Playable(name="Mutant Labb", description="Author: Pojahn Moradi\nAverage time: 110 sec\nProfessional time: 80 sec\nObjective: Enter the exit door.")
public class MutantLabb extends StageBuilder
{
	static final String PATH = "res/mutantlabb";
	
	@AutoLoad(path=PATH, type=VisualType.IMAGE)
	private Image2D roombg, door1, door2, lamp1, lamp2, lamp3, lamp4, bubbles[], glass, glasscrack, metroid[], goalbutton, glassdestroyed, alien[], alienattack[], stun[], bug[], turret, bullet, laseralien[], door, impact[];
	private Image2D ow, health[], trailer[];
	
	@AutoLoad(path=PATH, type=VisualType.MUSIC)
	private Music labbsong, stress;

	@AutoLoad(path=PATH, type=VisualType.SOUND)
	private Sound glassbreak, sireen, glasscracking, metroid01, metroid02, metroid03, metroid04, monster1, monster2, monster3, achievement, laserfire, lasermonster1, lasermonster2, lasermonster3, firesound, slam;
	private Sound hpgain, boom;
	
	private Vector2[] wp1, wp2, wp3;
	
	private CheckpointsHandler cph;
	
	private int smashCounter;
	private boolean buttonDown, metAttacking, stunned;
	
	@Override
	public void init() 
	{
		super.init();
		
		health = Image2D.loadImages(new File("res/general/health"));
		trailer = Image2D.loadImages(new File("res/clubber/trailer"));
		ow = new Image2D("res/diamondcave/ow.png");
		ow.setColor(Color.valueOf("b8b7b7FF"));
		
		boom = TinySound.loadSound(new File("res/collapsingcave/exp3.wav"));
		hpgain = TinySound.loadSound(new File("res/steelfactory/health.wav"));
		setStageMusic(PATH + "/song.ogg", 2.28f, .7f);
		labbsong.setVolume(0);
		labbsong.play(true);
		labbsong.pause();
		
		stress.setLoopPositionBySeconds(7.62);
		
		foregroundImg.setRenderOption(RenderOption.PORTION);
		backgroundImg.setRenderOption(RenderOption.PARALLAX_REPEAT);
		backgroundImg.setScrollRatio(.5f);
	}
	
	@Override
	public void build()
	{
		super.build();
		
		/*
		 * Globals
		 */
		game.timeColor = Color.WHITE;
		buttonDown = metAttacking = stunned = false;
		gm.hit(2);
		gm.facing = Direction.W;
		
		if(!music.playing())
			music.play(true);
		
		if(stress.playing())
			stress.stop();
		
		/*
		 * Checkpoint
		 */
		if(cph == null)
		{
			cph = new CheckpointsHandler();
			cph.setReachEvent(()-> GFX.renderCheckpoint());
			cph.appendCheckpoint(6023, 1324, 6024, 1248, 110, 110);
		}
		
		Vector2 latestCp = cph.getLastestCheckpoint();
		if(latestCp != null)
			gm.loc.set(latestCp);
		
		cph.setUsers(gm);
		add(cph);
			
		/*
		 * Room Background
		 */
		GameObject room = new GameObject();
		room.setImage(roombg);
		room.zIndex(-50);
		room.moveTo(1836, 1248);
		add(room);
		
		Event musicHandle = Factory.roomMusic(new Rectangle(room.loc.x - 100,
															room.loc.y,
															room.width + 200,
															room.height), 
												labbsong, 
												music,
												.01f, 1, gm);
		add(musicHandle);
		
		/*
		 * Doors
		 */
		SolidPlatform goalDoor = getDoor(1160, 1540, true);
		SolidPlatform locked1 = getDoor(3176, 1540, true);
		SolidPlatform locked2 = getDoor(5864, 1348, true);
		SolidPlatform trapdoor1 = getDoor(5832, 1256 - door1.getHeight(), false);
		SolidPlatform trapdoor2 = getDoor(3264, 1360, false);
		
		add(goalDoor, locked1, locked2, trapdoor1, trapdoor2);
		
		/*
		 * Room stuff
		 */
		GameObject glass1 = new GameObject();
		glass1.setImage(glass);
		glass1.zIndex(-5);
		glass1.alpha = .8f;
		glass1.moveTo(1967, 1328);
		
		GameObject glass2 = glass1.getClone(2210, 1328);
		GameObject glass3 = glass1.getClone(2452, 1328);
		
		GameObject bubble1 = new GameObject();
		bubble1.zIndex(-4);
		bubble1.setImage(3, bubbles);
		bubble1.moveTo(1967, 1344);
		
		GameObject bubble2 = bubble1.getClone(2210, 1344);
		GameObject bubble3 = bubble1.getClone(2452, 1344);
		
		add(glass1, glass2, glass3, bubble1);
		add(bubble2, 20);
		add(bubble3, 40);
		
		/*
		 * Crack Event
		 */
		if(cph.getLastestCheckpoint() == null)
		{
			add(new Event()
			{
				@Override
				public void eventHandling() 
				{
					if(gm.loc.x > 2813 && !buttonDown)
					{
						glass3.setImage(glasscrack);
						glasscracking.play();
						discard(this);
					}
				}
			});
		}
		
		/*
		 * Metroids
		 */
		EvilDog met1 = getMetroid(1976, 1368);
		EvilDog met2 = getMetroid(2219, 1368);
		EvilDog met3 = getMetroid(2461, 1368);
		met1.drag = .4f;
		met2.drag = .3f;
		add(met1,met2,met3);
		
		/*
		 * Goal Button
		 */
		SolidPlatform gbutton = new SolidPlatform(6574, 1328, gm);
		gbutton.setImage(goalbutton);
		gbutton.freeze();
		gbutton.setMoveSpeed(1.5f);
		gbutton.appendPath(gbutton.loc.x, gbutton.loc.y + gbutton.halfHeight(), 0, false, ()->{
			buttonDown = true;
			gm.freeze();
			game.removeFocusObject(gm);
			
			PathDrone camera = new PathDrone(gm.loc.x, gm.loc.y);
			camera.appendPath(camera.loc, 60, true, null);
			camera.appendPath(1160, 1540, 60, true, ()->{
				camera.freeze();
				
				add(()-> goalDoor.unfreeze(), 60);
				onceEvent(()->{
					game.removeFocusObject(camera);
					game.addFocusObject(gm);
					gm.unfreeze();
					achievement.play();
				},140);
			});
			
			add(camera);
			game.addFocusObject(camera);
		});
		GameObject dummy = new GameObject();
		dummy.moveTo(gbutton.loc.x, gbutton.loc.y - 1);
		dummy.width = gbutton.width;
		dummy.addEvent(()->{
			if(dummy.collidesWith(gm))
			{
				gbutton.unfreeze();
				discard(dummy);
			}
		});
		add(dummy, gbutton);
		
		/*
		 * Metroid Release
		 */
		add(new Event()
		{
			@Override
			public void eventHandling() 
			{
				if(buttonDown && gm.loc.x < 6064)
				{
					onceEvent(()-> sireen.play(), 30);
					glassbreak.play();
					stress.play(true);
					music.pause();
					discard(bubble1, bubble2, bubble3, musicHandle);
					glass1.setImage(glassdestroyed);
					glass2.setImage(glassdestroyed);
					glass3.setImage(glassdestroyed);
					locked1.unfreeze();
					locked2.unfreeze();
					trapdoor1.unfreeze();
					trapdoor2.unfreeze();
					met1.unfreeze();
					met2.unfreeze();
					met3.unfreeze();
					discard(this);
					add(new TimedEnemy()
					{
						{
							time = 300;
							zIndex(300);
						}
						
						public void drawSpecial(SpriteBatch batch) 
						{
							game.hudCamera();
							game.timeFont.draw(batch, "Warning! Mutants escaped. Security door shut.", 90, 50);
							game.gameCamera();
						}
					});
				}
			}
		});
		
		/*
		 * Alien
		 */
		Animation<Image2D> smash = new Animation<>(4, alienattack);
		smash.setLoop(false);
		
		PathDrone monster = new PathDrone(1536, 2273);
		monster.setDoubleFaced(true, true);
		monster.setImage(8, alien);
		monster.setMoveSpeed(1);
		monster.setHitbox(Hitbox.EXACT);
		monster.appendPath();
		monster.appendPath(1885, monster.loc.y);
		monster.addEvent(Factory.hitMain(monster, gm, -1));
		monster.addEvent(()->{
			if(++smashCounter % 110 == 0)
			{
				smash.reset();
				monster.setImage(smash);
				monster.setMoveSpeed(0);
				
				Sound s = getRandomMonster();
				double volume = SoundBank.getVolume(monster, gm, 500, 1, 40);
				if(volume > 0.0)
					s.play(volume);
			}
		});
		add(monster);
		
		smash.addEvent(()->{
			final double dist = Fundementals.distance(monster, gm);
			
			if(dist < 300 && !stunned)
			{
				stunned = true;
				gm.setImage(1, stun);
				gm.setMultiFaced(true);
				gm.vy = 0;
				
				livingEvent(()-> gm.vx = 0, 60);
				onceEvent(()->{
					stunned = false;
					gm.setImage(4, mainImage);
					gm.setMultiFaced(true);
				},60);
			}
			
			if(dist < 600)
			{
				slam.play(SoundBank.getVolume(monster, gm, 600, 1, 50));
				add(CameraEffect.vibration(1.5f, 40));
			}
			
		}, 4);
		smash.addEvent(()->{
			monster.setImage(8, alien);
			monster.setMoveSpeed(1);
		}, alienattack.length - 1);
		
		/*
		 * One Ways
		 */
		OneWay ow1 = new OneWay(1618, 2187, Direction.N, gm);
		ow1.setImage(ow);
		add(ow1, ow1.getClone(1751, ow1.loc.y), ow1.getClone(1883, ow1.loc.y));
		
		/*
		 * Health
		 */
		GameObject hp = new GameObject();
		hp.moveTo(3431,1896);
		hp.setImage(4, health);
		hp.addEvent(()->{
			if(hp.collidesWith(gm))
			{
				gm.hit(1);
				discard(hp);
				hpgain.play();
			}
		});
		add(hp);
		
		/*
		 * Bugs
		 */
		GameObject b = new GameObject();
		b.setImage(6, bug);
		b.setHitbox(Hitbox.EXACT);
		b.setCloneEvent((clone)-> {
			clone.addEvent(Factory.hitMain(clone, gm, -1));
			clone.addEvent(Factory.wobble(clone, -.5f, .5f, -.5f, .5f, 7));
		});
		
		add(b.getClone(200, 851), b.getClone(240, 851), b.getClone(280, 851), b.getClone(320, 851), b.getClone(360, 851), b.getClone(400, 851),
			b.getClone(380, 1100), b.getClone(420, 1100), b.getClone(460, 1100), b.getClone(500, 1100), b.getClone(540, 1100),  b.getClone(620, 1100));
		
		/*
		 * Turret & Projectile
		 */
		final Particle gunfire = new Particle();
		gunfire.setIntroSound(firesound);
		gunfire.getSoundBank().useFallOff(true);
		gunfire.getSoundBank().maxDistance = 1300;
		
		final Particle p = new Particle();
		p.setImage(2, trailer);
		
		final Particle imp = new Particle();
		imp.setImage(2, impact);
		imp.zIndex(200);
		imp.setIntroSound(boom);
		imp.getSoundBank().useFallOff(true);
		imp.getSoundBank().maxVolume = .7f;
		
		Missile proj = new Missile(0, 0, gm);
		proj.setImage(bullet);
		proj.adjustTrailer(true);
		proj.setTrailer(p);
		proj.setImpact(imp);
		proj.thrust = 750;
		
		Weapon turr = new Weapon(3456, 768, 1, 1, 90, gm);
		turr.zIndex(1);
		turr.setProjectile(proj);
		turr.setImage(turret);
		turr.setFiringOffsets(turr.halfWidth(), turr.halfHeight());
		turr.setFiringParticle(gunfire);
		add(turr);
		
		if(!game.playingReplay())
		{
			wp1 = getLaserMonsterWP();
			wp2 = getLaserMonsterWP();
			wp3 = getLaserMonsterWP();
		}
		
		/*
		 * Laser Monsters
		 */
		for(int i = 0; i < 3; i++)
		{
			Vector2[] waypoints;
			if(i == 0) waypoints = wp1;
			else if(i == 1) waypoints = wp2;
			else waypoints = wp3;
			
			Shuttle sh = new Shuttle(4687, 772);
			sh.setImage(4, laseralien);
			sh.appendPath(waypoints);
			sh.zIndex(200);
			sh.thrust = 100;
			
			LaserDrone ld = new LaserDrone(4687, 772, 50, 10, 60, gm);
			ld.addEvent(Factory.follow(sh, ld, sh.halfWidth(), sh.halfHeight()));
			ld.setLaserTint(Color.valueOf("00f428FF"));
			ld.setFiringSound(laserfire);
			ld.getSoundBank().useFallOff(true);
			ld.getSoundBank().power = 40;
			ld.getSoundBank().maxDistance = 1600;
			ld.addEvent(new Event()
			{
				int counter, delay = MathUtils.random(120, 200);
				
				@Override
				public void eventHandling() 
				{
					if(++counter % delay == 0)
						getRandomAlienLaser().play(SoundBank.getVolume(ld, gm, 1000, 1, 40));
					
					if(buttonDown)
						discard(ld);
				}
			});
			proj.merge(ld);
			
			add(sh, ld);
		}
		
		/*
		 * Goal
		 */
		GameObject goal = new GameObject();
		goal.setImage(door);
		goal.moveTo(1929, 3006);
		goal.zIndex(-1);
		goal.addEvent(new Event()
		{
			@Override
			public void eventHandling() 
			{
				if(goal.collidesWith(gm))
				{
					goal.removeEvent(this);
					stress.stop();
					music.resume();
					met1.freeze();
					met2.freeze();
					met3.freeze();
					gm.setState(CharacterState.FINISH);
				}
			}
		});
		add(goal);
		
		/*
		 * Finalize
		 */
		gm.setHitEvent((hitter)->{
			if(hitter.sameAs(proj))
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
		cph.reset();
	}
	
	EvilDog getMetroid(float x, float y)
	{
		GameObject dummy = new GameObject();
		dummy.width = gm.width;
		dummy.height = gm.height;
		dummy.loc.set(gm.loc);
		
		EvilDog m = new EvilDog(x, y, -1, dummy)
		{
			boolean attacking, recover;
			int attackingCounter, recoveryCounter;
			
			@Override
			public void moveEnemy() 
			{
				if(isFrozen() || game.getGlobalState() != GameState.ONGOING)
					return;
				
				float targetX = gm.loc.x - 28;
				float targetY = gm.loc.y - 63;
				
				if(!recover && !metAttacking && (attacking || Fundementals.distance(loc.x, loc.y, targetX, targetY) < 25))
				{
					attacking = metAttacking = true;
					getImage().stop(false);
					gm.hit(-1);
					playRandomMetroid();
				}
				
				if(attacking)
				{
					if(++attackingCounter % 95 == 0)
					{
						attacking = metAttacking = false;
						getImage().reset();
						getImage().stop(true);
						recover = true;
						vx = vy = 0;
					}
					else
						moveTo(targetX, targetY);
				}

				if(recover && ++recoveryCounter % 120 == 0)
					recover = false;
				
				if(!attacking)
				{
					dummy.moveTo(targetX, targetY);
					super.moveEnemy();
				}
			}
			
			@Override
			public void unfreeze() 
			{
				zIndex(150);
				super.unfreeze();
			}
		};
		m.setImage(5, metroid);
		m.getImage().stop(true);
		m.zIndex(-6);
		m.moveTo(x, y);
		m.freeze();
		m.thrust = 200;
		
		return m;
	}
	
	SolidPlatform getDoor(float x, float y, boolean horizontal)
	{
		SolidPlatform solp;
		
		if(horizontal)
		{
			solp = new SolidPlatform(0, 0, gm)
			{
				int counter;
				float posX = 1;
				float posY = 4;
				boolean goingBack;
				
				@Override
				public void drawSpecial(SpriteBatch batch) 
				{
					if(++counter % 5 == 0)
					{
						if(goingBack)
						{
							posX -= 8;
							if(posX < 1)
								goingBack = false;
						}
						else
						{
							posX += 8;
							if(posX > 65)
							{
								goingBack = true;
								posX -= 8;
							}
						}
					}
					
					batch.draw(goingBack ? lamp4 : lamp3, posX + loc.x, posY + loc.y);
				}
			};
		}
		else
		{
			solp = new SolidPlatform(0, 0, gm)
			{
				int counter;
				float posX = 7;
				float posY = 1;
				boolean goingBack;
				
				@Override
				public void drawSpecial(SpriteBatch batch) 
				{
					if(++counter % 5 == 0)
					{
						if(goingBack)
						{
							posY -= 8;
							if(posY < 7)
								goingBack = false;
						}
						else
						{
							posY += 8;
							if(posY > 65)
							{
								goingBack = true;
								posY -= 8;
							}
						}
					}
					
					batch.draw(goingBack ? lamp2: lamp1, posX + loc.x, posY + loc.y);
				}
			};
		}
		
		solp.setMoveSpeed(2);
		solp.freeze();
		solp.moveTo(x, y);
		solp.setImage(horizontal ? door2 : door1);
		solp.zIndex(1);
		solp.drawSpecialBehind(false);
		if(horizontal)
			solp.appendPath(solp.loc.x - solp.width(), solp.loc.y);
		else
			solp.appendPath(solp.loc.x, solp.loc.y + solp.height());
		
		return solp;
	}
	
	void playRandomMetroid()
	{
		int value = MathUtils.random(1, 4);
		
		switch(value)
		{
			case 1:
				metroid01.play();
				break;
			case 2:
				metroid02.play();
				break;
			case 3:
				metroid03.play();
				break;
			case 4:
				metroid04.play();
				break;
		}
	}
	
	Sound getRandomMonster()
	{
		int value = MathUtils.random(1, 3);
		
		switch(value)
		{
			case 1:
				return monster1;
			case 2:
				return monster2;
			case 3:
				return monster3;
		}
		
		return null;
	}
	
	Sound getRandomAlienLaser()
	{
		int value = MathUtils.random(1, 3);
		
		switch(value)
		{
			case 1:
				return lasermonster1;
			case 2:
				return lasermonster2;
			case 3:
				return lasermonster3;
		}
		
		return null;
	}
	
	@Override
	protected Serializable getMeta() 
	{
		ArrayList<Vector2[]> list = new ArrayList<>(3);
		list.add(wp1);
		list.add(wp2);
		list.add(wp3);
		
		return list;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setMeta(Serializable meta) 
	{
		ArrayList<Vector2[]> list = (ArrayList<Vector2[]>) meta;
		wp1 = list.get(0);
		wp2 = list.get(1);
		wp3 = list.get(2);
	}
	
	Vector2[] getLaserMonsterWP()
	{
		int padding = 100;
		PathData[] pd = Factory.randomWallPoints(4704 + padding, 5597 - padding, 768 + padding, 1152 - padding);
		Vector2[] wps = new  Vector2[pd.length];
		for(int j = 0; j < wps.length; j++)
			wps[j] = new Vector2(pd[j].targetX, pd[j].targetY);
		
		return wps;
	}
}
