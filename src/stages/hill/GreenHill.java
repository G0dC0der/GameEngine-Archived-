package stages.hill;

import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.GameObject.Hitbox;
import game.core.MainCharacter.CharacterState;
import game.core.Stage;
import game.essentials.BigImage;
import game.essentials.Controller;
import game.essentials.Factory;
import game.essentials.Animation;
import game.essentials.Image2D;
import game.essentials.Utilities;
import game.essentials.BigImage.RenderOption;
import game.mains.GravityMan;
import game.movable.PathDrone;
import game.movable.Projectile;
import game.movable.SolidPlatform;
import game.movable.Trailer;
import game.objects.Particle;

import java.io.File;

import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

@Playable(name="Green Hill",description="Stage: Green Hill\nAuthor: Pojahn Moradi\nDifficulty: 3\nAverage time: 70 sec\nProfessional time: 55 sec\nObjective: Collect all rings.")
public class GreenHill extends Stage
{
	private Pixmap stageImage;
	private Image2D deathImg[], backgroundImg, foregroundImg, mainImage[], platformImg, en1Img[], en2Img[], pooImg, ringImg[], collImg[], poospImg[];
	private GravityMan gm;
	private GameObject[] rings;
	private Sound jump, collectRing, pooSplash;
	private int ringCounter;
	
	@Override
	public void init() 
	{
		try
		{
			mainImage    = Image2D.loadImages(new File("res/general/main"),true);
			en1Img       = Image2D.loadImages(new File("res/hill/enemy"),true);
			en2Img       = Image2D.loadImages(new File("res/hill/enemy2"),true);
			ringImg      = Image2D.loadImages(new File("res/hill/ring"),true);
			collImg      = Image2D.loadImages(new File("res/hill/collectitem"),false);
			poospImg     = Image2D.loadImages(new File("res/hill/poosplash"),false);
			deathImg	 = Image2D.loadImages(new File("res/general/main/death"),false);
			platformImg  = new Image2D("res/hill/platform.png");
			pooImg  	 = new Image2D("res/hill/poo.png");
			stageImage	= new Pixmap(new FileHandle("res/hill/map.png"));
			foregroundImg = new BigImage("res/hill/foreground.png", RenderOption.PORTION);
			backgroundImg = new BigImage("res/hill/background.png", RenderOption.PORTION);
			
			jump        = TinySound.loadSound(new File(("res/general/jump.wav")));
			collectRing = TinySound.loadSound(new File(("res/hill/collectring.wav")));
			pooSplash   = TinySound.loadSound(new File(("res/hill/splash.wav")));
			setStageMusic("res/hill/song.ogg", 4.05, 1.0f);
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
		 * Basics
		 */
		super.build();

		if(stageData == null)
			stageData = Utilities.createStageData(stageImage);
		basicInits();

		ringCounter = 0;
		game.timeColor = new Color(100,255,100,255);
		game.deathTextColor = Color.YELLOW;
		
		foreground(foregroundImg);
		background(backgroundImg);
		
		/*
		 * Main Character
		 */
		gm = new GravityMan();
		gm.setImage(new Animation<>(3, mainImage));
		gm.setMultiFaced(true);
		gm.setController((Controller)Utilities.importObject("res/data/controller1.con"));
		gm.hit(1);
		gm.setJumpingSound(jump);
		gm.loc.x = startX;
		gm.loc.y = startY;
		gm.addTileEvent(Factory.slipperWalls(gm));
		gm.deathImg = new Particle();
		gm.deathImg.setImage(4, deathImg);
		game.addFocusObject(gm);
		add(gm);
		
		/*
		 * Obstacles
		 */
		
		SolidPlatform sp = new SolidPlatform(451, 740, gm);
		sp.setImage(new Animation<>(1, platformImg));
		sp.setMoveSpeed(2);
		sp.setStrictGlueMode(true);
		sp.appendPath(451, 740, 20, false, null);
		sp.appendPath(752, 740, 20, false, null);
		
		SolidPlatform sp2 = sp.getClone(1940, 413);
		sp2.setMoveSpeed(1);
		sp2.clearData();
		sp2.appendPath(1940, 413, 30, false, null);
		sp2.appendPath(2100, 413, 30, false, null);
		
		SolidPlatform sp3 = sp2.getClone(2100, 300);
		sp3.clearData();
		sp3.appendPath(2100, 300, 30, false, null);
		sp3.appendPath(1940, 300, 30, false, null);
		
		SolidPlatform sp4 = sp2.getClone(1940, 187);
		sp4.clearData();
		sp4.appendPath(1940, 187, 30, false, null);
		sp4.appendPath(2100, 187, 30, false, null);
		
		add(sp, sp2, sp3, sp4);
		
		PathDrone enemy1 = new PathDrone(1367, 755);
		enemy1.setImage(new Animation<>(7, en1Img));
		enemy1.setMoveSpeed(0.8f);
		enemy1.setMultiFaced(true);
		enemy1.setHitbox(Hitbox.EXACT);
		enemy1.addEvent(Factory.hitMain(enemy1, gm, -1));
		enemy1.appendPath(1367, 755, 0, false, null);
		enemy1.appendPath(1014, 755, 0, false, null);
		
		PathDrone enemy2 = enemy1.getClone(1525, 723);
		enemy2.clearData();
		enemy2.addEvent(Factory.hitMain(enemy2, gm, -1));
		enemy2.appendPath(1525, 723, 0, false, null);
		enemy2.appendPath(1623, 723, 0, false, null);
		
		add(enemy1, enemy2);
		
		Trailer bird = new Trailer(910, 10);
		bird.setImage(new Animation<>(5, en2Img));
		bird.setMultiFaced(true);
		bird.setMoveSpeed(2);
		bird.setFrequency(60);
		bird.appendPath(910,  100, 2, false, null);
		bird.appendPath(1375, 100, 2, false, null);
		
		Particle pooImpact = new Particle();
		pooImpact.setImage(new Animation<>(4, poospImg));
		pooImpact.setIntroSound(pooSplash);
		
		final FallingProjectile sproj = new FallingProjectile(0, 0, bird, gm);
		sproj.setImage(new Animation<>(1, pooImg));
		sproj.setDisposable(true);
		sproj.setImpact(pooImpact);
		bird.setSpawners(sproj);
		bird.addEvent(Factory.soundFalloff(pooSplash, bird, gm, 800, 5, 100,1));
		
		add(bird);
		
		/*
		 * Rings
		 */
		GameObject ring = new GameObject();
		ring.setImage(new Animation<>(5, ringImg));
		ring.setHitbox(Hitbox.EXACT);
		
		rings = new GameObject[]
		{ 
			ring.getClone(63, 482),   ring.getClone(83, 482),   ring.getClone(103, 482),  ring.getClone(353, 452),  ring.getClone(373, 452),  ring.getClone(462, 722),
		    ring.getClone(484, 722),  ring.getClone(509, 722),  ring.getClone(531, 722),  ring.getClone(556, 722),  ring.getClone(578, 722),  ring.getClone(603, 722),
		    ring.getClone(625, 722),  ring.getClone(650, 722),  ring.getClone(672, 722),  ring.getClone(697, 722),  ring.getClone(719, 722),  ring.getClone(745, 722),
		    ring.getClone(767, 722),  ring.getClone(790, 722),  ring.getClone(1167, 424), ring.getClone(1186, 424), ring.getClone(393, 452),  ring.getClone(1110, 642), ring.getClone(1133, 642), ring.getClone(1157, 642),
		    ring.getClone(1248, 675), ring.getClone(1018, 675), ring.getClone(1399, 703), ring.getClone(1419, 703), ring.getClone(1439, 703), ring.getClone(1529, 672), ring.getClone(1549, 672), ring.getClone(1569, 672), ring.getClone(1632, 688),
		    ring.getClone(2915, 362), ring.getClone(2935, 362), ring.getClone(2956, 362),
		    ring.getClone(1839, 170), ring.getClone(1839, 200), ring.getClone(1839, 230), ring.getClone(1839, 260), ring.getClone(1839, 290), ring.getClone(1839, 320), ring.getClone(1839, 350), ring.getClone(1839, 380)
		};
		
		addEvents(rings);
		add((Object[])rings);

		/*
		 * Finalizing
		 */
		gm.setHitEvent((hitter)->
		{	
			if(hitter.sameAs(sproj))
				gm.hit(-1);
			
			if (0 >= gm.getHP())
				gm.setState(CharacterState.DEAD);
		});
	}

	@Override
	public void extra() 
	{
		if(ringCounter >= rings.length)
			gm.setState(CharacterState.FINISH);
	}
	
	private void addEvents(GameObject[] gos)
	{
		final Particle collect = new Particle();
		collect.setImage(new Animation<>(4, collImg));
		
		for(final GameObject go : gos)
		{
			go.addEvent(new Event() 
			{	
				@Override
				public void eventHandling() 
				{
					if(go.collidesWith(gm))
					{
						ringCounter++;
						discard(go);
						add(collect.getClone(go.loc.x, go.loc.y));
						collectRing.play();
					}
				}
			});
		}
	}
	
	public static class FallingProjectile extends Projectile
	{
		GameObject parent;
		GameObject[] targets;
		
		public FallingProjectile(float initialX, float initialY, GameObject parent, GameObject... targets) 
		{
			super(initialX, initialY, targets);
			this.parent = parent;
			this.initialX = parent.loc.x;
			this.initialY = parent.loc.y;
			this.targetX = parent.loc.x;
			this.targetY = Stage.getCurrentStage().size.height;
			this.targets = targets;
		}
		
		@Override
		public FallingProjectile getClone(float x, float y)
		{
			FallingProjectile sp = new FallingProjectile(x,y,parent, targets);
			copyData(sp);
			
			return sp;
		}
	}

	@Override
	public void dispose()
	{
		disposeBatch(stageImage, deathImg, backgroundImg, foregroundImg, mainImage, platformImg, en1Img, en2Img, pooImg, ringImg, collImg, poospImg, jump, collectRing, pooSplash);
	}
}