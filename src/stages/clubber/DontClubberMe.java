package stages.clubber;

import game.core.Engine;
import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.MainCharacter.CharacterState;
import game.core.Stage;
import game.essentials.Controller;
import game.essentials.Factory;
import game.essentials.Animation;
import game.essentials.Image2D;
import game.essentials.Utilities;
import game.mains.GravityMan;
import game.movable.Missile;
import game.movable.RectangleDrone;
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

@Playable(name="Dont Clubber Me", description="Stage: Dont Clubber Me!\nAuthor: Pojahn Moradi\nDifficulty: 3\nAverage time: 50 sec\nProfessional time: 30 sec\nCollect the four diamonds.")
public class DontClubberMe extends Stage
{
	private Pixmap stageImage;
	private Image2D backgroundImg, foregroundImg, deathImg[], keyimg, pressure1, pressure2, mainImage[], doorimg, owImage[], sawimg, missileimg, weaponimg, diamond[], trailerimg[], gunfireimg[], impactimg[];
	private Sound collectSound, explodesound, firesound, jump;
	private RectangleDrone saw1, saw2, saw3, saw4;
	private GravityMan gm;
	private int counter = 0;
	private boolean collected1, collected2, collected3, collected4;
	
	@Override
	public void init() 
	{
		try
		{
			explodesound	= TinySound.loadSound(new File(("res/clubber/explode.wav")));
			firesound		= TinySound.loadSound(new File(("res/clubber/fire.wav")));
			collectSound	= TinySound.loadSound(new File(("res/clubber/collect.wav")));
			jump			= TinySound.loadSound(new File(("res/general/jump.wav")));
			
			backgroundImg = new Image2D("res/clubber/background.png",false);
			foregroundImg = new Image2D("res/clubber/foreground.png",false);
			stageImage	= new Pixmap(new FileHandle("res/clubber/stage.png"));
			keyimg		= new Image2D("res/clubber/key.png",false);
			pressure1	= new Image2D("res/clubber/pressure1.png",true);
			pressure2	= new Image2D("res/clubber/pressurerot.png",true);
			sawimg		= new Image2D("res/clubber/saw.png",false);
			missileimg	= new Image2D("res/clubber/missile.png",false);
			weaponimg 	= new Image2D("res/clubber/turret.png",false);
			doorimg		= new Image2D("res/general/door/open00.png",false);
			impactimg	= Image2D.loadImages(new File("res/clubber/impact"),false);
			trailerimg	= Image2D.loadImages(new File("res/clubber/trailer"),false);
			owImage		= Image2D.loadImages(new File("res/clubber/cube"),false);
			diamond		= Image2D.loadImages(new File("res/clubber/diamond"),false);
			gunfireimg	= Image2D.loadImages(new File("res/clubber/gunfire"),false);
			mainImage	= Image2D.loadImages(new File("res/general/main"),false);
			deathImg	= Image2D.loadImages(new File("res/general/main/death"),false);

			setStageMusic(TinySound.loadMusic(new File("res/clubber/song.ogg"),true), 0, 1.0f);
			
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
		
		if(stageData == null)
			stageData = Utilities.createStageData(stageImage);
		basicInits();
		
		game.timeColor = Color.BLACK;
		lethalDamage = -3;
		counter = 0;
		collected1 = collected2 = collected3 = collected4 = false;
		
		background(RenderOption.PORTION,backgroundImg);
		foreground(RenderOption.PORTION,foregroundImg);

		gm = new GravityMan();
		gm.setImage(new Animation<>(3, mainImage));
		gm.setMultiFaced(true);
		gm.setController((Controller)Utilities.importObject("res/data/controller1.con"));
		gm.hit(3);
		gm.setJumpingSound(jump);
		gm.deathImg = new Particle();
		gm.deathImg.setImage(4, deathImg);
		game.addFocusObject(gm);
		gm.currX = startX;
		gm.currY = startY;
		add(gm);
		
		Animation<Image2D> pressureV = new Animation<>(1, pressure1);
		Animation<Image2D> pressureH = new Animation<>(1, pressure2);
		
		SolidPlatform p1 = new SolidPlatform(900, 589, gm);
		p1.setImage(pressureV);
		p1.setTileDeformer(true, Engine.SOLID);
		p1.setTransformBack(true);
		p1.setRock(true, true);
		p1.setStrictGlueMode(false);
		p1.appendPath(900, 0, 1, false, null);
		p1.appendPath(900, 589, 1, false, null);
		
		SolidPlatform p2 = p1.getClone(1103, 589);
		p2.clearData();
		p2.appendPath(1103, 0, 1, false, null);
		p2.appendPath(1103, 589, 1, false, null);
		p2.setMoveSpeed(1);
		
		SolidPlatform p3 = p1.getClone(1000, 57);
		p3.clearData();
		p3.appendPath(1000, 1000, 1, false, null);
		p3.appendPath(1000, 58, 1, false, null);
		p3.setMoveSpeed(1.6f);
		
		SolidPlatform p4 = p1.getClone(1103, 503);
		p4.setImage(pressureH);
		p4.clearData();
		p4.appendPath(0, 503, 1, false, null);
		p4.appendPath(1103, 503, 1, false, null);
		
		SolidPlatform p5 = p4.getClone(1103, 203);
		p5.clearData();
		p5.appendPath(0, 203, 1, false, null);
		p5.appendPath(1103, 203, 1, false, null);
		p5.setMoveSpeed(1);
		
		p1.avoidOverlapping(p2,p3,p4,p5);
		p2.avoidOverlapping(p1,p3,p4,p5);
		p3.avoidOverlapping(p1,p2,p4,p5);
		p4.avoidOverlapping(p1,p2,p3,p5);
		p5.avoidOverlapping(p1,p2,p3,p4);
		
		final GameObject door = new GameObject();
		door.currX = 882;
		door.currY = 64;
		door.setImage(doorimg);
		gm.avoidOverlapping(door);

		final GameObject key = new GameObject();
		key.setImage(new Animation<>(1, keyimg));
		key.currX = 1598;
		key.currY = 150;
		key.addEvent(()->{
			if(key.collidesWith(gm))
			{
				discard(door,key);
				gm.allowOverlapping(door);
			}
		});

		saw1 = new RectangleDrone(1500, 60, 195, 158, true);
		saw1.setImage(new Animation<>(1, sawimg));
		saw1.useFastCollisionCheck(true);
		saw1.addEvent(Factory.hitMain(saw1, gm, -1));
		
		saw2 = new RectangleDrone(1500, 60, 195, 158, true);
		saw2.setImage(new Animation<>(1, sawimg));
		saw2.useFastCollisionCheck(true);
		saw2.addEvent(Factory.hitMain(saw2, gm, -1));
		
		saw3 = new RectangleDrone(1500, 60, 195, 158, true);
		saw3.setImage(new Animation<>(1, sawimg));
		saw3.useFastCollisionCheck(true);
		saw3.addEvent(Factory.hitMain(saw3, gm, -1));
		
		saw4 = new RectangleDrone(1500, 60, 195, 158, true);
		saw4.setImage(new Animation<>(1, sawimg));
		saw4.useFastCollisionCheck(true);
		saw4.addEvent(Factory.hitMain(saw4, gm, -1));
		
		Particle trailer = new Particle();
		trailer.setImage(new Animation<>(2, trailerimg));
		
		Particle gunfire = new Particle();
		gunfire.setImage(new Animation<>(4, gunfireimg));
		
		final Particle impact = new Particle();
		impact.setImage(new Animation<>(4, impactimg));
		impact.setIntroSound(explodesound);
		
		final Missile m = new Missile(1240, 527, gm);
		m.setImage(new Animation<>(1, missileimg));
		m.useFastCollisionCheck(true);
		m.setTrailer(trailer);
		m.setTrailerDelay(7);
		m.setFiringSound(firesound);
		m.setImpact(impact);
		
		Weapon weap = new Weapon(1195, 510, 1, 1, 120, gm);
		weap.setImage(new Animation<>(1, weaponimg));
		weap.setProjectile(m);
		weap.setFrontFire(true);
		weap.setRotationSpeed(1.2f);
		weap.setFiringParticle(gunfire);
		
		final GameObject sign = Factory.textPrinter("Collect all the diamonds to finish the stage.", Color.WHITE, null, 200, -200, -50, gm);
		sign.currX = 580;
		sign.currY = 73;
		add(sign);
		
		GameObject weakp = new GameObject();
		weakp.currX = 300;
		weakp.currY = 300;
		weakp.setImage(owImage[0]);
		weakp.addEvent(Factory.weakPlatform(weakp, new Animation<>(6, owImage), 20, null, gm));
		
		GameObject weakp2 = weakp.getClone(500, 500);
		weakp2.addEvent(Factory.weakPlatform(weakp2, new Animation<>(6, owImage), 20, null, gm));
		
		GameObject weakp3 = weakp.getClone(160, 600);
		weakp3.addEvent(Factory.weakPlatform(weakp3, new Animation<>(6, owImage), 20, null, gm));
		
		GameObject weakp4 = weakp.getClone(400, 800);
		weakp4.addEvent(Factory.weakPlatform(weakp4, new Animation<>(6, owImage), 20, null, gm));
		
		final GameObject dia1 = new GameObject();
		dia1.currX = 303;
		dia1.currY = 270;
		dia1.setImage(new Animation<>(6, diamond));
		dia1.addEvent(new Event() 
		{
			@Override
			public void eventHandling() 
			{
				if(dia1.collidesWith(gm))
				{
					collected1 = true;
					discard(dia1);
					collectSound.play();
				}
			}
		});
		
		final GameObject dia2 = dia1.getClone(503, 470);
		dia2.addEvent(new Event() 
		{
			@Override
			public void eventHandling() 
			{
				if(dia2.collidesWith(gm))
				{
					collected2 = true;
					discard(dia2);
					collectSound.play();
				}
			}
		});
		
		final GameObject dia3 = dia1.getClone(163, 570);
		dia3.addEvent(new Event() 
		{
			@Override
			public void eventHandling() 
			{
				if(dia3.collidesWith(gm))
				{
					collected3 = true;
					discard(dia3);
					collectSound.play();
				}
			}
		});
		
		final GameObject dia4 = dia1.getClone(403, 770);
		dia4.addEvent(new Event() 
		{
			@Override
			public void eventHandling() 
			{
				if(dia4.collidesWith(gm))
				{
					collected4 = true;
					discard(dia4);
					collectSound.play();
				}
			}
		});
		
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
		
		add(p1);
		add(p2);
		add(p3);
		add(p4);
		add(p5);
		add(saw1);
		add(key);
		add(door);
		add(weap);
		add(sign);
		add(weakp);
		add(weakp2);
		add(weakp3);
		add(weakp4);
		add(dia1);
		add(dia2);
		add(dia3);
		add(dia4);
	}

	@Override
	public void extra() 
	{
		if(collected1 && collected2 && collected3 && collected4)
			gm.setState(CharacterState.FINISH);
		
		saw1.rotation += 10;
		saw2.rotation += 10;
		saw3.rotation += 10;
		saw4.rotation += 10;
		
		counter++;
		
		if(counter == 60)
			add(saw2);
		
		if(counter == 120)
			add(saw3);
		
		if(counter == 170)
			add(saw4);
	}


	@Override
	public void dispose()
	{
		disposeBatch(stageImage,backgroundImg, foregroundImg, deathImg, keyimg, pressure1, pressure2, mainImage, doorimg, owImage, sawimg, missileimg, weaponimg, diamond, trailerimg, gunfireimg, impactimg, collectSound, explodesound, firesound, jump);
		gm = null;
	}
}