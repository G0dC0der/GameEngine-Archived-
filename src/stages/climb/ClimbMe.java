package stages.climb;

import game.core.Engine;
import game.core.Engine.Direction;
import game.core.EntityStuff;
import game.core.GameObject;
import game.core.GameObject.Hitbox;
import game.core.Stage;
import game.essentials.Controller;
import game.essentials.Factory;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.essentials.Utilities;
import game.mains.GravityMan;
import game.movable.Bouncer;
import game.movable.PathDrone;
import game.movable.Projectile;
import game.movable.SolidPlatform;
import game.objects.Particle;
import game.objects.Wind;

import java.io.File;

import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

@Playable(name="Climb Me", description="Stage: Climb Me\nAuthor: Pojahn Moradi\nDifficulty: 4\nAverage time: 250 sec\nProfessional time: 140 sec\nObjective: Reach the top.")
public class ClimbMe extends Stage
{
	private Pixmap stageImage;
	private Image2D backgroundImg, foregroundImg, weakImg, weakdImg, movapImg, enemyImg, arrowImg, blockImg, block2Img,  solpImg, cannonImg, bouncerImg;
	private Image2D[] mainImage, deathImg, propImg, prop2Img, prop3Img, windImg, wind2Img, wind3Img, flagImg;
	private GravityMan gm;
	private SolidPlatform sol1, sol2;
	private Projectile arrow;
	private float x1, y1, x2, y2, x3, y3, x4, y4, moveSpeed, reloadCounter;
	private boolean reloading;
	private Sound jump, cannonfire, bumper, arrowfire, collapse;
	
	@Override
	public void init() 
	{
		try
		{
			mainImage   = Image2D.loadImages(new File("res/general/main"),true);
			deathImg    = Image2D.loadImages(new File("res/general/main/death"), true);
			propImg   	= Image2D.loadImages(new File("res/climb/propeller"),true);
			prop2Img   	= Image2D.loadImages(new File("res/climb/propeller2"),true);
			prop3Img   	= Image2D.loadImages(new File("res/climb/propeller3"),true);
			flagImg   	= Image2D.loadImages(new File("res/climb/flag"),true);
			windImg   	= Image2D.loadImages(new File("res/climb/wind"),false);
			wind2Img   	= Image2D.loadImages(new File("res/climb/wind2"),false);			
			wind3Img   	= Image2D.loadImages(new File("res/climb/wind3"),false);
			weakImg 	= new Image2D("res/climb/weak.png");
			weakdImg 	= new Image2D("res/climb/weakdest.png");
			movapImg 	= new Image2D("res/climb/movablep.png");
			enemyImg 	= new Image2D("res/climb/enemy.png",true);
			arrowImg 	= new Image2D("res/climb/arrow.png");
			blockImg 	= new Image2D("res/climb/block.png",true);
			block2Img 	= new Image2D("res/climb/block2.png");
			solpImg 	= new Image2D("res/climb/platform.png");
			cannonImg 	= new Image2D("res/climb/cannon.png");
			bouncerImg 	= new Image2D("res/climb/bouncer.png");

			backgroundImg = new Image2D("res/climb/background.png");
			foregroundImg = new Image2D("res/climb/foreground.png");
			stageImage 	  = new Pixmap(new FileHandle("res/climb/map.png"));

			jump       = TinySound.loadSound(new File(("res/general/jump.wav")));
			cannonfire = TinySound.loadSound(new File(("res/climb/cannonfire.wav")));
			bumper	   = TinySound.loadSound(new File(("res/climb/bumper.wav")));
			arrowfire  = TinySound.loadSound(new File(("res/climb/arrowfire.wav")));
			collapse   = TinySound.loadSound(new File(("res/climb/collapsing.wav")));

			setStageMusic("res/climb/song.ogg", 0, 1.0f);
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
		game.timeColor = game.deathTextColor = Color.WHITE;
		lethalDamage = -3;
		
		foreground(RenderOption.PORTION, foregroundImg);
		background(RenderOption.PORTION, backgroundImg);
		
		/*
		 * Main Character
		 */
		gm = new GravityMan();
		gm.setImage(new Frequency<>(3, mainImage));
		gm.setMultiFaced(true);
		gm.setController((Controller)Utilities.importObject("res/data/controller1.con"));
		gm.hit(2);
		gm.setJumpingSound(jump);;
		gm.moveTo(startX, startY);
		gm.deathImg = new Particle();
		gm.deathImg.setImage(4, deathImg);
		gm.deathImg.zIndex(250);
		game.addFocusObject(gm);
		add(gm);
		
		/*
		 * Lower Weak Platforms
		 */
		Frequency<Image2D> destroyImage = new Frequency<>(1, weakdImg);
		
		GameObject weak1 = new GameObject();
		weak1.setImage(new Frequency<>(1, weakImg));
		weak1.currX = 270;
		weak1.currY = 4162;
		weak1.addEvent(Factory.weakPlatform(weak1, destroyImage, 70, collapse, gm));
		
		GameObject weak2 = weak1.getClone(370, 4263);
		weak2.addEvent(Factory.weakPlatform(weak2, destroyImage, 70, collapse, gm));
		
		GameObject weak3 = weak1.getClone(518, 4322);
		weak3.addEvent(Factory.weakPlatform(weak3, destroyImage, 70, collapse, gm));

		GameObject weak4 = weak1.getClone(650, 4185);
		weak4.addEvent(Factory.weakPlatform(weak4, destroyImage, 70, collapse, gm));
		
		GameObject weak5 = weak1.getClone(650, 4185 + weak1.height);
		weak5.addEvent(Factory.weakPlatform(weak5, destroyImage, 70, collapse, gm));

		GameObject weak6 = weak1.getClone(650, 4185 + weak1.height * 2);
		weak6.addEvent(Factory.weakPlatform(weak6, destroyImage, 70, collapse, gm));
		
		GameObject weak7 = weak1.getClone(810, 4188);
		weak7.addEvent(Factory.weakPlatform(weak7, destroyImage, 70, collapse, gm));
		
		add(weak1, weak2, weak3, weak4, weak5, weak6, weak7);
		
		/*
		 * Movable Platforms and enemies
		 */
		moveSpeed = 1.5f;
		x1 = 1107 - gm.width;
		y1 = 3717;
		x2 = 672;
		y2 = 4003;
		
		sol1 = new SolidPlatform(x1, y1, gm);
		sol1.setImage(new Frequency<>(1, movapImg));
		sol1.setMoveSpeed(moveSpeed);
		sol1.appendPath(x2, y2, 5, false, null);
		sol1.appendPath(x1, y1, 5, false, null);
		
		x3 = 164 + gm.width;
		y3 = 3717;
		x4 = 600;
		y4 = 4003;
		
		sol2 = sol1.getClone(x3, y3);
		sol2.clearData();
		sol2.appendPath(x4, y4, 5, false, null);
		sol2.appendPath(x3, y3, 5, false, null);
		
		add(sol1, sol2);
		
		PathDrone enemy1 = new PathDrone(338, 3702);
		enemy1.setImage(enemyImg);
		enemy1.setMoveSpeed(1.3f);
		enemy1.setHitbox(Hitbox.EXACT);
		enemy1.addEvent(Factory.hitMain(enemy1, gm, -1));
		enemy1.appendPath(338, 3702, 0, false, null);
		enemy1.appendPath(338, 3962, 0, false, null);
		
		PathDrone enemy2 = enemy1.getClone(338 + enemy1.width, 3962);
		enemy2.addEvent(Factory.hitMain(enemy2, gm, -1));
		enemy2.clearData();
		enemy2.appendPath(338 + enemy1.width, 3702, 0, false, null);
		enemy2.appendPath(338 + enemy1.width, 3962, 0, false, null);
		
		PathDrone enemy3 = enemy1.getClone(size.width - 300, 3962);
		enemy3.addEvent(Factory.hitMain(enemy3, gm, -1));
		enemy3.clearData();
		enemy3.appendPath(size.width - 300, 3702, 0, false, null);
		enemy3.appendPath(size.width - 300, 3962, 0, false, null);
		
		PathDrone enemy4 = enemy1.getClone(size.width - 300 + enemy1.width, 3702);
		enemy4.addEvent(Factory.hitMain(enemy4, gm, -1));
		enemy4.clearData();
		enemy4.appendPath(size.width - 300 + enemy1.width, 3962, 0, false, null);
		enemy4.appendPath(size.width - 300 + enemy1.width, 3702, 0, false, null);
		
		add(enemy1, enemy2, enemy3, enemy4);
		
		/*
		 * Evil Face projectile
		 */
		arrow = new Projectile(1011, 2830, new GameObject[0]);
		arrow.setImage(arrowImg);
		arrow.setMoveSpeed(7);
		arrow.setTarget(0, 2830);
		arrow.addOtherTarget(gm);
		arrow.useSpecialEffect(false);
		arrow.setFiringSound(arrowfire);
		
		/*
		 * Crushing Blocks
		 */
		SolidPlatform block1 = new SolidPlatform(887, 2972, gm);
		block1.setImage(blockImg);
		block1.setMoveSpeed(1.3f);
		block1.appendPath(887, 2972);
		block1.appendPath(947, 2972);
		
		SolidPlatform block2 = new SolidPlatform(887, 3180, gm);
		block2.setImage(block2Img);
		block2.setMoveSpeed(1.4f);
		block2.appendPath(887, 3180);
		block2.appendPath(984, 3180);
		
		SolidPlatform block3 = new SolidPlatform(887, 3518, gm);
		block3.setImage(blockImg);
		block3.setMoveSpeed(2.5f);
		block3.appendPath(887, 3518);
		block3.appendPath(1122, 3518);
		
		SolidPlatform block4 = new SolidPlatform(1056, 3297, gm);
		block4.setImage(block2Img);
		block4.setMoveSpeed(2);
		block4.appendPath(1056, 3297);
		block4.appendPath(1159, 3297);
		
		SolidPlatform block5 = new SolidPlatform(1055, 3143, gm);
		block5.setImage(blockImg);
		block5.setMoveSpeed(2);
		block5.appendPath(1055, 3143);
		block5.appendPath(1055, 3053);
		
		SolidPlatform block6 = new SolidPlatform(1055, 2890, gm);
		block6.setImage(blockImg);
		block6.setMoveSpeed(2);
		block6.setTileDeformer(true, Engine.SOLID);
		block6.setTransformBack(true);
		block6.appendPath(1055, 2890);
		block6.appendPath(1055, 2632);
		
		SolidPlatform block7 = new SolidPlatform(1122, 2632, gm);
		block7.setImage(blockImg);
		block7.setMoveSpeed(2);
		block7.setTileDeformer(true, Engine.SOLID);
		block7.setTransformBack(true);
		block7.appendPath(1122, 2632);
		block7.appendPath(1122, 2890);
		
		add(block1, block2, block3, block4, block5, block6, block7);
		
		/*
		 * Propeller and Winds
		 */
		GameObject prop1 = new GameObject();
		prop1.setImage(new Frequency<>(2, propImg));
		prop1.currX = 54;
		prop1.currY = 2043;
		prop1.setHitbox(Hitbox.EXACT);
		prop1.addEvent(Factory.hitMain(prop1, gm, -1));
		
		Wind wind1 = new Wind(54, 2083 - windImg[0].getHeight(), Direction.N, 20, 350, gm);
		wind1.setImage(new Frequency<>(3, windImg));
		wind1.zIndex(50);
		
		add(prop1, wind1);
		
		GameObject prop2 = new GameObject();
		prop2.setImage(new Frequency<>(2, prop2Img));
		prop2.currX = 453;
		prop2.currY = 1180;
		prop2.setHitbox(Hitbox.EXACT);
		prop2.addEvent(Factory.hitMain(prop2, gm, -1));
		
		Wind wind2 = new Wind(453, 1200, Direction.S, 20, 350, gm);
		wind2.setImage(new Frequency<>(3, wind2Img));
		wind2.zIndex(50);
		
		add(prop2, wind2);
		
		GameObject prop3 = new GameObject();
		prop3.setImage(new Frequency<>(2, prop3Img));
		prop3.currX = 577;
		prop3.currY = 1262;
		prop3.setHitbox(Hitbox.EXACT);
		prop3.addEvent(Factory.hitMain(prop3, gm, -1));
		
		Wind wind3 = new Wind(587, 1262, Direction.E, 20, 350, gm);
		wind3.setImage(new Frequency<>(3, wind3Img));
		wind3.zIndex(50);
		
		add(prop3, wind3);
		
		/*
		 * Upper solid platform, cannon and dummy objects
		 */
		SolidPlatform solid = new SolidPlatform(1034, 704, gm);
		solid.setImage(solpImg);
		solid.setStrictGlueMode(true);
		solid.setMoveSpeed(2.3f);
		solid.appendPath(1034, 704);
		solid.appendPath(1034, 444);
		
		Bouncer cannon = new Bouncer(0, 0, 1200, 1, Direction.W, gm);
		cannon.setImage(cannonImg);
		cannon.addEvent(Factory.follow(solid, cannon, 0, -cannonImg.getHeight() + 2));
		cannon.setShakeSound(cannonfire,5);
		cannon.setHitbox(Hitbox.CIRCLE);
		
		GameObject weakUpper = weak1.getClone(191, 662);
		weakUpper.addEvent(Factory.weakPlatform(weakUpper, destroyImage, 70, collapse, gm));
		
		SolidPlatform dummyp = new SolidPlatform(1034, 704 - cannonImg.getHeight(), gm);
		dummyp.width = cannonImg.getWidth() + 2;
		dummyp.setStrictGlueMode(true);
		dummyp.setMoveSpeed(2.3f);
		dummyp.appendPath(1034, 704 - cannonImg.getHeight());
		dummyp.appendPath(1034, 444 - cannonImg.getHeight());
		
		add(solid, cannon, weakUpper, dummyp);
		
		/*
		 * Top Bouncer and finish flag
		 */
		Bouncer topb = new Bouncer(987, 206, 200, 2, Direction.N, gm);
		topb.setImage(bouncerImg);
		topb.setMoveSpeed(1.5f);
		topb.setShakeSound(bumper,5);
		topb.setShake(true, 10, 2, 2);
		topb.appendPath(987, 206);
		topb.appendPath(100, 206);
		
		GameObject flag = new GameObject();
		flag.setImage(new Frequency<>(3, flagImg));
		flag.currX = 1102;
		flag.currY = 164;
		
		add(topb, flag);
		
		/*
		 * Finalizing
		 */
		gm.setHitEvent(hitter -> { if(hitter.sameAs(arrow)) gm.hit(-1); });
	}

	@Override
	public void extra()
	{
		if(moveSpeed > EntityStuff.distance(sol1.currX, sol1.currY, x1, y1) && moveSpeed < EntityStuff.distance(sol2.currX, sol2.currY, x3, y3))
			sol1.setMoveSpeed(0);
		else
			sol1.setMoveSpeed(moveSpeed);
		
		if(!reloading && EntityStuff.checkLine(1011, 2840, 0, 2840, gm))
		{
			Projectile clone = arrow.getClone(1011, 2830);
			clone.setTarget(0, 2830);
			add(clone);
			reloading = true;
		}
		else if(reloading && ++reloadCounter % 100 == 0)
			reloading = false;
	}

	@Override
	public void dispose() 
	{
		disposeBatch(stageImage, backgroundImg, foregroundImg, weakImg, weakdImg, movapImg, enemyImg, arrowImg, blockImg, block2Img,  solpImg, cannonImg, bouncerImg, 
						mainImage, deathImg, propImg, prop2Img, prop3Img, windImg, wind2Img, wind3Img, flagImg, jump, cannonfire, bumper, arrowfire, collapse);
	}
}