package stages.shroom;

import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.GameObject.Hitbox;
import game.core.MainCharacter.CharacterState;
import game.development.AutoInstall;
import game.development.StageBuilder;
import game.essentials.CameraEffect;
import game.essentials.Factory;
import game.essentials.Animation;
import game.essentials.Image2D;
import game.movable.Missile;
import game.movable.Missile.MissileProperties;
import game.movable.PathDrone;
import game.movable.SolidPlatform;
import game.movable.Weapon;
import game.objects.Particle;

import java.io.File;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

@AutoInstall(mainPath="res/general", path="res/shroom")
@Playable(name="Dont Eat Shroom",description="Stage: Dont Eat Shrooms!\nAuthor: Pojahn Moradi\nDifficulty: 7\nAverage time: 180 sec\nProfessional time: 120 sec\nObjective: Get the key and enter goal.")
public class DontEatShroom extends StageBuilder
{
	private Image2D platformImg, shredderImg[], barsImg, goalImg, keyImg, shroomImg, dotsImg, drugEffectImg[], gunfireImg[], explosionImg[], cannonImg, missileImg, trailerImg[];
	private Sound collect, eat, jumpdruged, boom, cannonfire;
	private Music drugedSong;
	private CameraEffect eff1, eff2, eff3, eff4, heff1, heff2, heff3, heff4, zeff;
	private int counter = 0, drugStrength;
	
	@Override
	public void init() 
	{
		try
		{
			super.init();
			
			explosionImg  = Image2D.loadImages(new File("res/shroom/explosion"),true);
			gunfireImg    = Image2D.loadImages(new File("res/flyingb/fireanim"));
			trailerImg    = Image2D.loadImages(new File("res/clubber/trailer"));
			shredderImg	  = Image2D.loadImages(new File("res/shroom/shredder"));
			drugEffectImg = Image2D.loadImages(new File("res/shroom/drugeffect"));
			platformImg   = new Image2D("res/shroom/platform.png");
			barsImg		  = new Image2D("res/shroom/bars.png");
			keyImg		  = new Image2D("res/shroom/key.png");
			goalImg		  = new Image2D("res/shroom/goal.png");			
			dotsImg		  = new Image2D("res/shroom/dots.png");
			shroomImg	  = new Image2D("res/shroom/shroom.png");
			cannonImg	  = new Image2D("res/shroom/cannon.png");
			missileImg	  = new Image2D("res/shroom/missile.png");
			
			collect    = TinySound.loadSound(new File(("res/general/collect3.wav")));
			eat    	   = TinySound.loadSound(new File(("res/shroom/eat.wav")));
			jumpdruged = TinySound.loadSound(new File(("res/shroom/jumpdruged.wav")));
			boom       = TinySound.loadSound(new File(("res/shroom/boom.wav")));
			cannonfire = TinySound.loadSound(new File(("res/shroom/cannonfire.wav")));
			
			setStageMusic("res/shroom/song.ogg", 7.631, .8f);
			drugedSong = TinySound.loadMusic(new File(("res/shroom/songdruged.ogg")),true);
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
		
		drugStrength = 0;
		counter++;
		gm.zIndex(50);
		game.timeColor = Color.WHITE;
		
		eff1 = CameraEffect.verticalMovement(1, 1, -1);
		eff2 = CameraEffect.verticalMovement(2, 2, -1);
		eff3 = CameraEffect.verticalMovement(3, 3, -1);
		eff4 = CameraEffect.verticalMovement(5, 5, -1);
		
		heff1 = CameraEffect.horizontalMovement(1, 1, -1);
		heff2 = CameraEffect.horizontalMovement(2, 2, -1);
		heff3 = CameraEffect.horizontalMovement(3, 3, -1);
		heff4 = CameraEffect.horizontalMovement(5, 5, -1);
		
		zeff = CameraEffect.zoomEffect(0.5f, 1.0f, .0025f, -1);

		game.zoom = 1;
		game.angle = 0;
		
		if(!music.playing())
		{
			drugedSong.stop();
			music.play(true);
		}
		drugedSong.setVolume(0);
		music.setVolume(.8f);
		
		/*
		 * Horizontal moving platforms
		 */
		SolidPlatform p1 = new SolidPlatform(1616, 2340, gm);
		p1.setImage(platformImg);
		p1.appendPath(1616, 2340);
		p1.appendPath(1903, 2340);
		p1.setMoveSpeed(2);
		
		SolidPlatform p2 = new SolidPlatform(2312, 2340, gm);
		p2.setImage(platformImg);
		p2.appendPath(2312, 2340);
		p2.appendPath(2025, 2340);
		p2.setMoveSpeed(2);
		
		add(p1,p2);
		
		/*
		 * Shredders
		 */
		PathDrone sh1 = new PathDrone(2692,2564);
		sh1.setImage(2,shredderImg);
		sh1.addEvent(Factory.hitMain(sh1, gm, -1));
		sh1.appendPath(2692,2564);
		sh1.appendPath(2788,2564);
		sh1.setMoveSpeed(5);
		sh1.setHitbox(Hitbox.CIRCLE);
		
		PathDrone sh2 = new PathDrone(2959,2397);
		sh2.setImage(2,shredderImg);
		sh2.addEvent(Factory.hitMain(sh2, gm, -1));
		sh2.appendPath(2959,2397);
		sh2.appendPath(2959,2299);
		sh2.appendPath(2862,2299);
		sh2.appendPath(2862,2397);
		sh2.setMoveSpeed(2);
		sh2.setHitbox(Hitbox.CIRCLE);
		
		PathDrone sh3 = new PathDrone(2862,2299);
		sh3.setImage(2,shredderImg);
		sh3.addEvent(Factory.hitMain(sh3, gm, -1));
		sh3.appendPath(2862,2299);
		sh3.appendPath(2862,2397);
		sh3.appendPath(2959,2397);
		sh3.appendPath(2959,2299);
		sh3.setMoveSpeed(2);
		sh3.setHitbox(Hitbox.CIRCLE);
		
		PathDrone sh4 = new PathDrone(3129,2229);
		sh4.setImage(2,shredderImg);
		sh4.addEvent(Factory.hitMain(sh4, gm, -1));
		sh4.appendPath(3129,2229);
		sh4.appendPath(3031,2130);
		sh4.setMoveSpeed(3);
		sh4.setHitbox(Hitbox.CIRCLE);
		
		add(sh1, sh2, sh3, sh4);
		
		/*
		 * Bars
		 */
		final GameObject bars = new GameObject();
		bars.setImage(barsImg);
		bars.loc.x = 819;
		bars.loc.y = 1121;
		add(bars);
		
		/*
		 * Key and goal object
		 */
		final GameObject goal = new GameObject();
		goal.setImage(goalImg);
		goal.loc.x = 823;
		goal.loc.y = 1128;
		goal.addEvent(()->{if(gm.collidesWith(goal)) gm.setState(CharacterState.FINISH);});
		
		final GameObject key = new GameObject();
		key.loc.x = 1197;
		key.loc.y = 2540;
		key.setImage(keyImg);
		key.addEvent(()->
		{	
			if(key.collidesWith(gm))
			{
				collect.play();
				discard(key);
				discard(bars);
				add(goal);
				add(getMushroom());
			}
		});
		add(key);
		
		/*
		 * Cannon and missile
		 */
		Particle trailer = new Particle();
		trailer.setImage(3, trailerImg);

		Particle firing = new Particle();
		firing.setImage(4, gunfireImg);
		firing.setIntroSound(cannonfire);
		
		final Particle impact = new Particle(0,0,gm);
		impact.setImage(3, explosionImg);
		impact.setHitbox(Hitbox.EXACT);
		impact.setIntroSound(boom);
		
		final Missile m = new Missile(0, 0, gm);
		m.setImpact(impact);
		m.setImage(missileImg);
		m.setTrailer(trailer);
		m.setTrailerDelay(2);
		m.setProperties(MissileProperties.FAST_VERY_FLOATY);
		m.adjustTrailer(true);
		m.setFaceTarget(false);
		
		Weapon weapon = new Weapon(1901, 750,1,1,100, gm);
		weapon.setImage(cannonImg);
		weapon.setRotationSpeed(0.03f);
		weapon.setProjectile(m);
		weapon.setFiringParticle(firing);
		weapon.setFrontFire(true);
		add(weapon);
		
		
		/*
		 * Finalizing
		 */
		gm.setHitEvent((hitter)->
		{	
			if(hitter.sameAs(m))
				gm.hit(-1);
			
			if(gm.getHP() <= 0)
			{
				gm.setVisible(false);
				gm.setState(CharacterState.DEAD);
			}
		});
	}
	
	GameObject getMushroom()
	{
		final PathDrone dots = new PathDrone(0, 0);
		dots.setImage(dotsImg);
		dots.appendPath(-59, 0);
		dots.appendPath(0, 0, 0, true, null);
		dots.setMoveSpeed(1.7f);
		dots.zIndex(500);
		
		final GameObject drug = new GameObject()
		{
			Animation<Image2D> drugAnim = new Animation<>(5, drugEffectImg);
			Color alpha = new Color(game.defaultTint);
			
			{
				alpha.a = 0;
			}
			
			@Override
			public void drawSpecial(SpriteBatch batch) 
			{
				if(alpha.a <= 1)
					alpha.a += .002;
				if(alpha.a > 1)
					alpha.a = 1;
					
				game.hudCamera();
				
				Image2D img = drugAnim.getObject();
				batch.setColor(alpha);
				batch.draw(img, 0, 0, img.getWidth() * scale, img.getHeight() * scale);
				batch.setColor(game.defaultTint);
				
				game.gameCamera();
			}
		};
		drug.setVisible(true);
		drug.scale = 2;
		drug.zIndex(400);
		
		final GameObject m = new GameObject();
		m.loc.x = 666;
		m.loc.y = 2538;
		m.setImage(shroomImg);
		m.addEvent(new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(m.collidesWith(gm))
				{
					final int value = counter;
					add(Factory.soundFade(music, 0, 4000, true));
					eat.play();
					
					discard(m);
					add(Factory.fade(1, 0.005f, 10, null, drugEffectImg));
					add(drug);
					drugStrength++;
					
					new Thread()
					{
						@Override
						public void run()
						{
							try
							{
								Thread.sleep(4200);
							}
							catch(Exception e){}
							if(value == counter)
							{
								drugedSong.play(true,0);
								add(Factory.soundFade(drugedSong, 0.7f, 12000, false));
								drugStrength++;
							}
						}
					}.start();
					
					new Thread()
					{
						@Override
						public void run()
						{
							try
							{
								Thread.sleep(8000);
							}
							catch(Exception e){}
							if(value == counter)
							{
								gm.setJumpingSound(jumpdruged);
								drugStrength++;
							}
						}
					}.start();
					
					new Thread()
					{
						@Override
						public void run()
						{
							try
							{
								Thread.sleep(12000);
							}
							catch(Exception e){}
							if(value == counter)
							{
								add(dots);
								drugStrength++;
							}
						}
					}.start();
				}
			}
		});
		
		return m;
	}
	
	@Override
	public void extra() 
	{
		if(drugStrength == 1)
		{
			eff1.update();
			heff1.update();
		}
		else if(drugStrength == 2)
		{
			eff2.update();
			heff2.update();
		}
		else if(drugStrength == 3)
		{
			eff3.update();
			heff3.update();
		}
		else if(drugStrength == 4)
		{
			eff4.update();
			heff4.update();
			zeff.update();
		}
	}
	
	@Override
	public void dispose() 
	{
		super.dispose();
		disposeBatch(platformImg, shredderImg, barsImg, goalImg, keyImg, shroomImg, dotsImg, drugEffectImg, gunfireImg, explosionImg, cannonImg, missileImg, trailerImg, collect, eat, jumpdruged, boom, cannonfire, drugedSong);
	}
}
