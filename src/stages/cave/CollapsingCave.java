package stages.cave;

import game.core.Enemy;
import game.core.Engine.GameState;
import game.core.EntityStuff;
import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.GameObject.Hitbox;
import game.core.MovableObject;
import game.core.Stage;
import game.essentials.Controller;
import game.essentials.Factory;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.essentials.Utilities;
import game.mains.GravityMan;
import game.movable.Dummy;
import game.movable.PathDrone;
import game.movable.SolidPlatform;
import game.objects.Particle;
import java.io.File;
import java.util.Random;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

@Playable(name="Collapsing Cave",
		  description="Stage: Collapsing Cave\nAuthor: Pojahn Moradi\nDifficulty: 4\nAverage time: 40 sec\nProfessional time: 32 sec\nObjective: Collect the four crystals and enter goal(the flag).")
public class CollapsingCave extends Stage
{
	private Pixmap stageImage;
	private Image2D backgroundImg, foregroundImg, deathImg[], mainImage[], crusherImg, drillImg[], bottomImg[], middleImg, collectImg[], flagImg[];
	private Sound collect, jump, exp1, exp2, exp3, exp4, slam;
	private Music collapsing, drilling;
	private GravityMan gm;
	private SolidPlatform crusher;
	private boolean coll1, coll2, coll3, coll4, done;
	private GameObject item1, flag, dust;
	private MovableObject camera;
	private PathDrone drill;
	private ParticleEffect ps;
	private Random r = new Random();
	private int soundCounter;
	
	@Override
	public void init() 
	{
		game.timeColor = Color.WHITE;
		game.deathTextColor = Color.WHITE;
		visibleWidth = 800;
		visibleHeight = 600;
		
		try
		{
			crusherImg  = new Image2D("res/collapsingcave/crusher.png", false);
			middleImg   = new Image2D("res/collapsingcave/middlepart.png", false);
			mainImage   = Image2D.loadImages(new File("res/general/main"), true);
			drillImg    = Image2D.loadImages(new File("res/collapsingcave/drill"),true);
			bottomImg   = Image2D.loadImages(new File("res/collapsingcave/bottompart"), true);
			collectImg  = Image2D.loadImages(new File("res/collapsingcave/collect"), false);
			flagImg  	= Image2D.loadImages(new File("res/collapsingcave/flag"), false);
			
			backgroundImg = new Image2D("res/collapsingcave/background.png", false);
			foregroundImg = new Image2D("res/collapsingcave/foreground.png", false);
			deathImg   	  = Image2D.loadImages(new File("res/general/main/death"), false);
			
			game.lifeImage   = new Image2D("res/general/hearth.png", false);
			stageImage        = new Pixmap(new FileHandle("res/collapsingcave/stage.png"));
			
			collapsing = Utilities.loadMusic("res/collapsingcave/collapsing.wav");
			drilling   = Utilities.loadMusic("res/collapsingcave/drilling.wav");
			collect    = Utilities.loadSound("res/general/collect3.wav");
			jump       = Utilities.loadSound("res/general/jump.wav");
			exp1       = Utilities.loadSound("res/collapsingcave/exp1.wav");
			exp2       = Utilities.loadSound("res/collapsingcave/exp2.wav");
			exp3       = Utilities.loadSound("res/collapsingcave/exp3.wav");
			exp4       = Utilities.loadSound("res/collapsingcave/exp4.wav");
			slam       = Utilities.loadSound("res/collapsingcave/slam.wav");
			
			ps = new ParticleEffect();
			ps.load(new FileHandle("res/collapsingcave/muzzle.p"), new FileHandle("res/collapsingcave"));
			ps.flipY();
			ps.getEmitters().get(0).setContinuous(true);
			ps.start();
			
			setStageMusic(TinySound.loadMusic(new File("res/collapsingcave/song.ogg"), true), 0f);
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
		basicInits();
		
		background(RenderOption.FULL, backgroundImg);
		foreground(RenderOption.FULL, foregroundImg);
		game.drugVertical(0, 0);
		game.drugHorizontal(0, 0);
		coll1 = coll2 = coll3 = coll4 = done = false;
		collapsing.stop();
		drilling.play(true, 0);
		
		/*
		 * Main Character
		 *******************************************
		 */
		gm = new GravityMan();
		gm.setImage(new Frequency<>(3, mainImage));
		gm.setMultiFaced(true);
		gm.setController((Controller)Utilities.importObject("res/data/controller1.con"));
		gm.hit(1);
		gm.setJumpingSound(jump);
		gm.moveTo(startX, startY);
		gm.deathImg = new Particle();
		gm.deathImg.setImage(4, deathImg);
		gm.zIndex(10);
		gm.freeze();
		game.setMainCharacter(gm);

		add(gm);
		add(Factory.soundFalloff(drilling, gm, 580, 215, 1200, 0, 10, 1));
		
		/*
		 * Collapsing roof
		 *******************************************
		 */
		crusher = new SolidPlatform(0, -1300, gm);
		crusher.setImage(crusherImg);
		crusher.appendPath(0, -38, Integer.MAX_VALUE, false, new Event()
		{
			@Override
			public void eventHandling() 
			{
				game.drugVertical(0, 0);
				game.drugHorizontal(0, 0);
				collapsing.stop();
				slam.play();
			}
		});
		crusher.zIndex(99);
		crusher.setMoveSpeed(0);
		
		/*
		 * Drill and parts
		 *******************************************
		 */
		drill = new PathDrone(547, 215);
		drill.setImage(new Frequency<>(1, drillImg));
		drill.setMoveSpeed(2);
		drill.zIndex(10);
		drill.setHitbox(Hitbox.EXACT);
		drill.appendPath(547, 60, 0, false, new Event()
		{	
			@Override
			public void eventHandling() 
			{
				drill.setMoveSpeed(.1f);
				camera.unfreeze();
				game.setFocusObject(camera);
				add(dust);
			}
		});
		drill.appendPath(547, -100, Integer.MAX_VALUE, false, new Event()
		{
			@Override
			public void eventHandling() 
			{
				crusher.setMoveSpeed(1.7f);
				discard(dust);
				collapsing.play(true);
				drilling.stop();
				game.drugVertical(2, 2);
				game.drugHorizontal(2, 2);
			}
		});
		drill.addEvent(Factory.hitMain(drill, gm, -1));
		
		Enemy middlePart = new PathDrone(0,0);
		middlePart.addEvent(Factory.follow(drill, middlePart, 8, 100));
		middlePart.setImage(middleImg);
		
		Enemy bottomPart = new PathDrone(0,0);
		bottomPart.addEvent(Factory.follow(drill, bottomPart, 17, 160));
		bottomPart.setImage(bottomImg);
		bottomPart.setHitbox(Hitbox.EXACT);
		
		gm.avoidOverlapping(middlePart, bottomPart);
		game.setFocusObject(drill);
		
		add(drill);
		add(middlePart);
		add(bottomPart);
		add(crusher);
		
		/*
		 * Collect items
		 *******************************************
		 */
		item1 = new Dummy(677, 1314);
		item1.setImage(6, collectImg);	
		final GameObject item2 = item1.getClone(826, 669);		
		final GameObject item3 = item1.getClone(335, 584);		
		final GameObject item4 = item1.getClone(957, 267);
		item2.addEvent(new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(item2.collidesWithMultiple(gm))
				{
					discard(item2);
					coll2 = true;
					collect.play();
				}
			}
		});
		item3.addEvent(new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(item3.collidesWithMultiple(gm))
				{
					discard(item3);
					coll3 = true;
					collect.play();
				}
			}
		});
		item4.addEvent(new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(item4.collidesWithMultiple(gm))
				{
					discard(item4);
					coll4 = true;
					collect.play();
				}
			}
		});
		
		add(item1);
		add(item2);
		add(item3);
		add(item4);
		
		/*
		 * Particle Object
		 *******************************************
		 */
		dust = new GameObject()
		{
			@Override
			public void drawSpecial(SpriteBatch batch)
			{
				ps.setPosition(drill.currX + drill.width / 2, drill.currY + 50);
				ps.draw(batch, Gdx.graphics.getDeltaTime());
			}
		};
		dust.zIndex(98);
		
		/*
		 * Flag
		 *******************************************
		 */
		flag = new Dummy(1057, 1298);
		flag.setImage(4, flagImg);
		add(flag);
		
		/*
		 * Camera
		 *******************************************
		 */
		camera = new MovableObject();
		camera.moveTo(560, 60);
		camera.freeze();
	}

	@Override
	public void extra() 
	{
		if(drill.getMoveSpeed() == .1f && crusher.getMoveSpeed() == 0 && ++soundCounter % 15 == 0)
		{
			double distance = EntityStuff.distance(gm.currX, gm.currY, drill.currX + drill.width / 2, drill.currY);
			double candidate = 10 * Math.max((1 / Math.sqrt(distance)) - (1 / Math.sqrt(1200)), 0);
			double volume = Math.min(candidate, 1);
			
			switch(r.nextInt(4))
			{
			case 0:
				exp1.play(volume);
			case 1:
				exp2.play(volume);
			case 2:
				exp3.play(volume);
			case 3:
				exp4.play(volume);
			}
		}
		
		if(!coll1 && item1.collidesWithMultiple(gm))
		{
			discard(item1);
			coll1 = true;
			collect.play();
		}
		
		if(coll1 && coll2 && coll3 && coll4 && flag.collidesWithMultiple(gm))
			game.setState(GameState.FINISH);
	
		if(!done)
		{
			camera.moveToward(gm.currX, gm.currY, 7);
			if(EntityStuff.distance(camera, gm) < 200)
			{
				done = true;
				game.setFocusObject(gm);
				gm.unfreeze();
			}
		}
	}

	@Override
	public void dispose() 
	{
		collect.unload();
		jump.unload();
		exp1.unload();
		exp2.unload();
		exp3.unload();
		exp4.unload();
		slam.unload();
		collapsing.unload();
		drilling.unload();
		music.unload();
		stageImage.dispose();
		ps.dispose();
	}
}