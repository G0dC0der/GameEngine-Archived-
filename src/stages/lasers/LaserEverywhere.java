package stages.lasers;

import game.core.Engine;
import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.GameObject.HitEvent;
import game.core.MainCharacter.CharacterState;
import game.development.AutoInstall;
import game.development.StageBuilder;
import game.essentials.Image2D;
import game.movable.Circle;
import game.movable.TargetLaser;
import game.objects.Particle;
import java.io.File;
import com.badlogic.gdx.graphics.Color;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;

@AutoInstall(mainPath="res/general", path="res/lasereverywhere")
@Playable(name="Laser Everywhere!", description="Stage: Laser Everywhere!\nAuthor: Pojahn Moradi\nAverage time: 80 sec\nProfessional time: 70 sec\nObjective:Collect the diamond and enter goal(flag).")
public class LaserEverywhere extends StageBuilder
{
	{
		setDifficulty(Difficulty.NORMAL);
	}
	
	private Image2D flagImg[], diamondImg[], dummyImg, miniexpImg[];
	private Sound boom1,boom2,boom3,boom4,boom5,boom6,boom7,boom8;
	private Music laserLoop;
	private TargetLaser tl1, tl2, tl3, tl4, tl5, tl6, tl7, tl8;
	private boolean taken, added;

	public void init() 
	{
		try
		{
			super.init();
			
			miniexpImg  = Image2D.loadImages(new File("res/clubber/trailer"),false);
			diamondImg  = Image2D.loadImages(new File("res/clubber/diamond"),false);
			flagImg		= Image2D.loadImages(new File("res/lasereverywhere/flag"),false);
			dummyImg	= new Image2D("res/lasereverywhere/dummy.png");
			
			laserLoop = TinySound.loadMusic(new File(("res/lasereverywhere/laser.wav")));
			boom1     = TinySound.loadSound(new File(("res/lasereverywhere/boom.wav")));
			boom2     = TinySound.loadSound(new File(("res/lasereverywhere/boom.wav")));
			boom3     = TinySound.loadSound(new File(("res/lasereverywhere/boom.wav")));
			boom4     = TinySound.loadSound(new File(("res/lasereverywhere/boom.wav")));
			boom5     = TinySound.loadSound(new File(("res/lasereverywhere/boom.wav")));
			boom6     = TinySound.loadSound(new File(("res/lasereverywhere/boom.wav")));
			boom7     = TinySound.loadSound(new File(("res/lasereverywhere/boom.wav")));
			boom8     = TinySound.loadSound(new File(("res/lasereverywhere/boom.wav")));
			
			setStageMusic("res/lasereverywhere/song.ogg", 1.869);
			
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
		Difficulty d = getDifficulty();
		taken = added = false;
		laserLoop.stop();
		game.timeColor = Color.WHITE;
		if(d == Difficulty.EASY)
			gm.hit(1);
		
		/*
		 * Diamond
		 */
		final GameObject diamond = new GameObject();
		diamond.setImage(6, diamondImg);
		diamond.currX = 64;
		diamond.currY = 106;
		diamond.addEvent(new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(!taken && diamond.collidesWith(gm))
				{
					taken = true;
					laserLoop.play(true);
					discard(diamond);
				}
			}
		});
		add(diamond);
		
		/*
		 * Flag
		 */
		final GameObject f = new GameObject();
		f.setImage(4, flagImg);
		f.currX = 125;
		f.currY = 1261;
		f.addEvent(()->{if(taken && f.collidesWith(gm)) gm.setState(CharacterState.FINISH);});
		add(f);
		
		/*
		 * Circle pathing
		 */
		float mx = size.width / 2,
			  my = size.height / 2;
		float movespeed = getSpeed();
		
		Circle c1 = new Circle(mx, my, 100, 0);
		c1.setMoveSpeed(movespeed);
		c1.setImage(dummyImg);
		
		Circle c2 = new Circle(mx, my, 100, (float) Math.toRadians(45));
		c2.setMoveSpeed(movespeed);
		c2.setImage(dummyImg);
		
		Circle c3 = new Circle(mx, my, 100, (float) Math.toRadians(90));
		c3.setMoveSpeed(movespeed);
		c3.setImage(dummyImg);
		
		Circle c4 = new Circle(mx, my, 100, (float) Math.toRadians(135));
		c4.setMoveSpeed(movespeed);
		c4.setImage(dummyImg);
		
		Circle c5 = new Circle(mx, my, 100, (float) Math.toRadians(180));
		c5.setMoveSpeed(movespeed);
		c5.setImage(dummyImg);
		
		Circle c6 = new Circle(mx, my, 100, (float) Math.toRadians(225));
		c6.setMoveSpeed(movespeed);
		c6.setImage(dummyImg);
		
		Circle c7 = new Circle(mx, my, 100, (float) Math.toRadians(270));
		c7.setMoveSpeed(movespeed);
		c7.setImage(dummyImg);
		
		Circle c8 = new Circle(mx, my, 100, (float) Math.toRadians(315));
		c8.setMoveSpeed(movespeed);
		c8.setImage(dummyImg);
		
		add(c1, c2, c3, c4, c5, c6, c7, c8);
		
		/*
		 * Lasers
		 */
		
		Particle impact1 = new Particle();
		impact1.setImage(3, miniexpImg);
		impact1.setIntroSound(boom1);
		impact1.setSoundEmitter(impact1);
		impact1.useSoundFallOff(true);
		impact1.emitterProps(550, 1, 30);
		
		Particle impact2 = impact1.getClone(0, 0);
		impact1.setIntroSound(boom2);
		
		Particle impact3 = impact1.getClone(0, 0);
		impact1.setIntroSound(boom3);
		
		Particle impact4 = impact1.getClone(0, 0);
		impact1.setIntroSound(boom4);
		
		Particle impact5 = impact1.getClone(0, 0);
		impact1.setIntroSound(boom5);
		
		Particle impact6 = impact1.getClone(0, 0);
		impact1.setIntroSound(boom6);
		
		Particle impact7 = impact1.getClone(0, 0);
		impact1.setIntroSound(boom7);
		
		Particle impact8 = impact1.getClone(0, 0);
		impact1.setIntroSound(boom8);
		
		tl1 = new TargetLaser(mx,my, c1, gm);
		tl1.setImage(dummyImg);
		tl1.setExplosion(impact1);
		tl1.setExplosionDelay(6);
		tl1.setStopTile(Engine.AREA_TRIGGER_0);
		
		tl2 = new TargetLaser(mx,my, c2, gm);
		tl2.setImage(dummyImg);
		tl2.setExplosion(impact2);
		tl2.setExplosionDelay(6);
		tl2.setStopTile(Engine.AREA_TRIGGER_0);
		
		tl3 = new TargetLaser(mx,my, c3, gm);
		tl3.setImage(dummyImg);
		tl3.setExplosion(impact3);
		tl3.setExplosionDelay(6);
		tl3.setStopTile(Engine.AREA_TRIGGER_0);
		
		tl4 = new TargetLaser(mx,my, c4, gm);
		tl4.setImage(dummyImg);
		tl4.setExplosion(impact4);
		tl4.setExplosionDelay(6);
		tl4.setStopTile(Engine.AREA_TRIGGER_0);
		
		tl5 = new TargetLaser(mx,my, c5, gm);
		tl5.setImage(dummyImg);
		tl5.setExplosion(impact5);
		tl5.setExplosionDelay(6);
		tl5.setStopTile(Engine.AREA_TRIGGER_0);
		
		tl6 = new TargetLaser(mx,my, c6, gm);
		tl6.setImage(dummyImg);
		tl6.setExplosion(impact6);
		tl6.setExplosionDelay(6);
		tl6.setStopTile(Engine.AREA_TRIGGER_0);
		
		tl7 = new TargetLaser(mx,my, c7, gm);
		tl7.setImage(dummyImg);
		tl7.setExplosion(impact7);
		tl7.setExplosionDelay(6);
		tl7.setStopTile(Engine.AREA_TRIGGER_0);
		
		tl8 = new TargetLaser(mx,my, c8, gm);
		tl8.setImage(dummyImg);
		tl8.setExplosion(impact8);
		tl8.setExplosionDelay(6);
		tl8.setStopTile(Engine.AREA_TRIGGER_0);
		
		tl1.merge(tl2, tl3, tl4, tl5, tl6, tl7, tl8);
		
		gm.setHitEvent(new HitEvent()
		{	
			@Override
			public void eventHandling(GameObject hitter) 
			{
				if(hitter.sameAs(tl1))
				{
					gm.hit(-1);
					if(gm.getHP() <= 0)
						gm.setState(CharacterState.DEAD);
				}	
			}
		});
	}
	
	float getSpeed()
	{
		switch(getDifficulty())
		{
			case EASY:
				return 0.002f;
			case HARD:
				return 0.00415f;
			case NORMAL:
			default:
				return 0.0032f;	
		}	
	}
	
	@Override
	public void extra()
	{
		if(!added && taken)
		{
			added = true;
			add(tl1, tl2, tl3, tl4, tl5, tl6, tl7, tl8);
		}
	}
	
	@Override
	public void dispose() 
	{
		super.dispose();
		disposeBatch(flagImg, diamondImg, dummyImg, miniexpImg,boom1,boom2,boom3,boom4,boom5,boom6,boom7,boom8);
	}
}