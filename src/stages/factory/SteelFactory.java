package stages.factory;

import game.core.Engine;
import game.core.Engine.Direction;
import game.core.EntityStuff;
import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.GameObject.HitEvent;
import game.core.GameObject.Hitbox;
import game.core.MainCharacter.CharacterState;
import game.core.MovableObject;
import game.core.MovableObject.TileEvent;
import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.AutoLoad;
import game.development.StageBuilder;
import game.essentials.Factory;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.movable.Dummy;
import game.movable.HorizontalDrone;
import game.movable.Missile;
import game.movable.Missile.MissileProperties;
import game.movable.PathDrone;
import game.movable.Projectile;
import game.movable.SolidPlatform;
import game.movable.Weapon;
import game.objects.Particle;
import game.objects.Shrapnel;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;
import com.badlogic.gdx.graphics.Color;

@AutoDispose
@AutoInstall(mainPath="res/general",path="res/steelfactory")
@Playable(name="Steel Factory", description="Stage: Steel Factory\nAuthor: Pojahn Moradi\nDifficulty: 9\nAverage time: 220 sec\nProfessional time: 150 sec\nObjective: Enter the exit door.")
public class SteelFactory extends StageBuilder
{
	@AutoLoad(path="res/steelfactory", type=VisualType.IMAGE)
	private Image2D clocker[], laser[], beam[], electric[], electric2[], energy[], engine[], propeller[], zapper[], swleft[], swright[], thing[], splits[], explosion[], proj[], area, collect, saw, shutter, band, cup, cdown, cleft, mup, mdown, mleft, spikecar, spikecar2, turretimg, missileimg, pushup, stalker, metroit, goal, dummy, lobot;
	
	private Image2D[] wind, smallExp, firingAnim, metExplode;
	
	@AutoLoad(path="res/steelfactory", type=VisualType.SOUND)
	private Sound laserremove, magicfire, magicexplode, collectsound, health, collectsound2, elpower, shut, magnetmove, magnetfinish, bam, elevator, robottalk;
	
	@AutoLoad(path="res/steelfactory", type=VisualType.MUSIC)
	private Music laserloop, sawssound, collapsing, propellersound, bandmove, song;
	
	private Sound tick1, tick2, boom, buzz1, buzz2, buzz3, buzz4, mfire, exp, sexp;
	
	private SolidPlatform crusherLeft, crusherDown, crusherUp, solp, stalk;
	private Dummy magnetDown, magnetLeft, magnetUp;
	private Event moveMU, moveML, moveMD;
	private ArrayList<GameObject> saws = new ArrayList<>();
	private boolean collect1, collect2, collect3, collect4, collect5, energy1, energy2, energy3, once;
	private int deathCounter = -1;
	
	@Override
	public void init()
	{
		try
		{
			super.init();
			
			wind       = Image2D.loadImages(new File("res/climb/wind3"),true);
			smallExp   = Image2D.loadImages(new File("res/clubber/trailer"));
			firingAnim = Image2D.loadImages(new File("res/clubber/gunfire"));
			metExplode = Image2D.loadImages(new File("res/shroom/explosion"));
			
			tick1 = TinySound.loadSound(new File(("res/steelfactory/tick.wav")));
			tick2 = TinySound.loadSound(new File(("res/steelfactory/tick.wav")));
			buzz1 = TinySound.loadSound(new File(("res/steelfactory/buzz.wav")));
			buzz2 = TinySound.loadSound(new File(("res/steelfactory/buzz.wav")));
			buzz3 = TinySound.loadSound(new File(("res/steelfactory/buzz.wav")));
			buzz4 = TinySound.loadSound(new File(("res/steelfactory/buzz.wav")));
			boom  = TinySound.loadSound(new File(("res/shroom/boom.wav")));
			mfire = TinySound.loadSound(new File(("res/clubber/fire.wav")));
			exp   = TinySound.loadSound(new File(("res/collapsingcave/exp2.wav")));
			sexp  = TinySound.loadSound(new File(("res/collapsingcave/exp1.wav")));
			
			setStageMusic(song, 4.48f, .7f);
			magicfire.setVolume(0.65f);
			magicexplode.setVolume(0.5f);
			collapsing.setVolume(0.7f);
			exp.setVolume(0.5);
			sexp.setVolume(0.1);
		}
		catch(Exception e)
		{
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
		
		gm.hit(2);
		gm.facing = Direction.W;
		gm.zIndex(50);
		
		game.timeColor = game.deathTextColor = Color.WHITE;
		collect1 = collect2 = collect3 = collect4 = collect5 = energy1 = energy2 = energy3 = once = false;
		deathCounter++;

		saws.clear();
		
		game.drugVertical(0, 0);
		game.drugHorizontal(0, 0);
		
		collapsing.stop();
		elevator.stop();
		bandmove.stop();
		sawssound.stop();
		laserloop.play(true, 0);

		/*
		 * Clockers
		 */
		final HorizontalDrone cl1 = new HorizontalDrone(4135, 1278);
		cl1.setImage(1, clocker);
		cl1.setHitbox(Hitbox.EXACT);
		cl1.addEvent(Factory.hitMain(cl1, gm, -1));
		cl1.setMoveSpeed(7);
		cl1.addEvent(new Event()
		{	
			int counter = 0;
			@Override
			public void eventHandling()
			{
				if(++counter % 13 == 0 && tick1.getVolume() > 0.0f)
					tick1.play();
			}
		});
		add(Factory.soundFalloff(tick1, cl1, gm, 400, 3, 50,1));

		final HorizontalDrone cl2 = cl1.getClone(4790, 1278);
		cl2.addEvent(Factory.hitMain(cl2, gm, -1));
		cl2.addEvent(new Event()
		{	
			int counter = 0;
			@Override
			public void eventHandling()
			{
				if(++counter % 16 == 0 && tick2.getVolume() > 0.0f)
					tick2.play();
			}
		});
		add(Factory.soundFalloff(tick2, cl2, gm, 400, 3, 50,1));
		
		cl1.avoidOverlapping(cl2);
		cl2.avoidOverlapping(cl1);

		add(cl1, cl2);

		/*
		 * Magic Weapon
		 */
		Projectile magic = new Projectile(0, 0, gm);
		magic.setMoveSpeed(3);
		magic.setImage(3, proj);
		magic.setHitbox(Hitbox.EXACT);
		magic.setFiringSound(magicfire);
		
		Particle part = new Particle();
		part.setVisible(false);
		part.setIntroSound(magicexplode);
		
		magic.setImpact(part); 
		
		final Weapon ld = new Weapon(4000, 1362, 1, 1, 100, gm);
		Frequency<Image2D> laserImage = new Frequency<>(5, laser);
		laserImage.pingPong(true);
		ld.setImage(laserImage);
		ld.setProjectile(magic);
		ld.setRotationSpeed(0.08f);
		ld.addEvent(()->ld.moveToward(gm.currX, gm.currY, .5f));
		ld.addEvent(()->{if(gm.getState() == CharacterState.DEAD)ld.halt(true);});
		
		/*
		 * Bottom saws
		 */
		for(int x = 55, y = 3900; x < size.width - 200 ; x += 282)
		{
			GameObject sawObj = new GameObject();
			sawObj.currX = x;
			sawObj.currY = y;
			sawObj.setHitbox(Hitbox.CIRCLE);
			sawObj.setImage(saw);
			sawObj.addEvent(()->sawObj.rotation-=3);
			saws.add(sawObj);
			add(sawObj);
		}
		
		final GameObject soundEmitter = new GameObject();
		soundEmitter.currY = 3900;
		soundEmitter.setVisible(false);
		soundEmitter.addEvent(()->soundEmitter.currX = gm.currX);
		add(soundEmitter);

		/*
		 * Area, five collectible objects and bottom laser
		 */
		Dummy solidArea = new Dummy(4210,1557);
		solidArea.setImage(area);
		solidArea.setHitbox(Hitbox.EXACT);
		solidArea.zIndex(-1);
		gm.avoidOverlapping(solidArea);
		
		add(solidArea);
		
		final GameObject co1 = new GameObject();
		co1.setImage(collect);
		co1.currX = 4568;
		co1.currY = 1597;
		co1.addEvent(getCollectEvent(co1, 1));
		
		GameObject co2 = co1.getClone(4423, 1717);
		co2.addEvent(getCollectEvent(co2, 2));
		
		GameObject co3 = co1.getClone(4719, 2033);
		co3.addEvent(getCollectEvent(co3, 3));
		
		GameObject co4 = co1.getClone(4537, 2025);
		co4.addEvent(getCollectEvent(co4, 4));
		
		GameObject co5 = co1.getClone(4322, 2006);
		co5.addEvent(getCollectEvent(co5, 5));
		
		add(co1,co2,co3,co4,co5);
		
		final GameObject lasersound = new GameObject();
		final GameObject laserbeam = new GameObject();
		laserbeam.currX = 4165;
		laserbeam.currY = 2325;
		laserbeam.setImage(4, beam);
		laserbeam.setHitbox(Hitbox.EXACT);
		laserbeam.addEvent(()->
		{	
			if(collect1 && collect2 && collect3 && collect4 && collect5)
			{
				discard(laserbeam,ld,lasersound,lasersound);
				laserremove.play();
				boom.play();
				laserloop.stop();
				add(Factory.soundFalloff(sawssound, gm, soundEmitter, 600, 3, 80,1));
				sawssound.play(true,0);
				
				Particle exp = new Particle(ld.currX,ld.currY);
				exp.setImage(3, metExplode);
				add(exp);
			}
			if(laserbeam.collidesWith(gm))
				gm.hit(-99);
		});
		add(laserbeam);
		
		lasersound.currY = laserbeam.currY;
		lasersound.addEvent(()->
		{	
			if(laserbeam.currX - 30 < gm.currX && laserbeam.currX + laserbeam.width + 30 > gm.currX)
				lasersound.currX = gm.currX;
		});
		
		add(lasersound, Factory.soundFalloff(laserloop, gm, lasersound, 400, 3, 70,1));
		
		/*
		 * Electrics
		 */
		
		final GameObject elRU = new GameObject();
		elRU.currX = 4805;
		elRU.currY = 2421;
		elRU.setHitbox(Hitbox.EXACT);
		Frequency<Image2D> elRUImg = new Frequency<>(2, electric);
		elRUImg.addEvent(()->buzz1.play(), 28);
		elRU.setImage(elRUImg);
		elRU.addEvent(Factory.hitMain(elRU, gm, -1));
		elRU.addEvent(Factory.soundFalloff(buzz1, gm, elRU.currX, elRU.currY + elRU.height / 2, 300, 3, 25,1));
		
		GameObject elRD = elRU.getClone(4805, 2761);
		elRD.addEvent(Factory.hitMain(elRD, gm, -1));
		Frequency<Image2D> elRDImg = new Frequency<>(2, electric);
		elRDImg.setIndex(15);
		elRDImg.addEvent(()->buzz2.play(), 28);
		elRD.setImage(elRDImg);
		elRD.addEvent(Factory.soundFalloff(buzz2, gm, elRD.currX, elRD.currY + elRD.height / 2, 300, 3, 25,1));
		
		final GameObject elLU = new GameObject();
		elLU.currX = 4134;
		elLU.currY = 2421;
		elLU.setHitbox(Hitbox.EXACT);
		Frequency<Image2D> elLUImg = new Frequency<>(2, electric2);
		elLUImg.setIndex(14);
		elLUImg.addEvent(()->buzz3.play(), 28);
		elLU.setImage(elLUImg);
		elLU.addEvent(Factory.hitMain(elLU, gm, -1));
		elLU.addEvent(Factory.soundFalloff(buzz3, gm, elLU.currX, elLU.currY + elLU.height / 2, 300, 3, 25,1));
		
		GameObject elLD = new GameObject();
		elLD.currX = 4134;
		elLD.currY = 2761;
		elLD.setHitbox(Hitbox.EXACT);
		Frequency<Image2D> elLDImg = new Frequency<>(2, electric2);
		elLDImg.addEvent(()->buzz4.play(),28);
		elLD.setImage(elLDImg);
		elLD.addEvent(Factory.hitMain(elLD, gm, -1));
		elLD.addEvent(Factory.soundFalloff(buzz4, gm, elLD.currX, elLD.currY + elLD.height / 2, 300, 3, 25,1));
		
		add(elRU,elRD,elLU,elLD);
		
		/*
		 * Three energy pieces for the movable platform
		 */
		GameObject en1 = new GameObject();
		en1.currX = 4136;
		en1.currY = 2782;
		en1.setImage(3, energy);
		en1.addEvent(getEnergyEvent(en1, 1));
		
		GameObject en2 = en1.getClone(4821, 2782);
		en2.addEvent(getEnergyEvent(en2, 2));
		
		GameObject en3 = en1.getClone(4821, 2534);
		en3.addEvent(getEnergyEvent(en3, 3));
		
		add(en1,en2,en3);
		
		/*
		 * Shutter
		 */
		final SolidPlatform theShutter = new SolidPlatform(3424,3021,gm);
		theShutter.setImage(shutter);
		theShutter.setMoveSpeed(0);
		theShutter.appendPath(4134, 3021, 99999, false, new Event()
		{	
			@Override
			public void eventHandling() 
			{
				theShutter.setMoveSpeed(0);
				theShutter.moveTo(4134, 3021);
				shut.play();
				collapsing.stop();
				
				if(gm.currX > theShutter.currY + theShutter.height)
				{
					discard(elRU);
					discard(elLU);
					discard(cl1);
					discard(cl2);
					discard(solidArea);
					gm.allowOverlapping(solidArea);
					gm.zIndex(0);
				}
			}
		});
		add(theShutter);

		/*
		 * Movable Platform
		 */
		solp = new SolidPlatform(4709, 3839, gm);
		solp.setImage(band);
		solp.setMoveSpeed(1.7f);
		solp.setTileDeformer(true, Engine.SOLID);
		solp.setTransformBack(true);
		solp.setStrictGlueMode(true);
		add(solp);
		
		/*
		 * Magnets and crushers
		 */
		crusherDown = new SolidPlatform(3133, 3171, gm);
		crusherDown.setImage(cdown);
		crusherDown.setMoveSpeed(10);
		
		magnetDown = new Dummy(0, 0);
		magnetDown.setImage(mdown);
		moveMD = Factory.follow(crusherDown, magnetDown, 75, -magnetDown.height);
		magnetDown.addEvent(moveMD);
		
		crusherLeft = new SolidPlatform(2400, size.height - 250, gm);
		crusherLeft.setImage(cleft);
		crusherLeft.setMoveSpeed(4);
		
		magnetLeft = new Dummy(0, 0);
		magnetLeft.setImage(mleft);
		moveML = Factory.follow(crusherLeft, magnetLeft, -magnetLeft.width, 75);
		magnetLeft.addEvent(moveML);
		
		crusherUp = new SolidPlatform(2697, 3805, gm);
		crusherUp.setImage(cup);
		crusherUp.setMoveSpeed(4);
		crusherUp.setTileDeformer(true, Engine.SOLID);
		crusherUp.setTransformBack(true);
		
		magnetUp = new Dummy(0, 0);
		magnetUp.setImage(mup);
		moveMU = Factory.follow(crusherUp, magnetUp,  75, crusherUp.height);
		magnetUp.addEvent(moveMU);

		add(crusherDown, magnetDown, crusherLeft, magnetLeft, crusherUp, magnetUp);
		
		/*
		 * Engine and wind
		 */
		final PathDrone theEngine = new PathDrone(400, 3400);
		theEngine.setImage(1, engine);
		theEngine.setMoveSpeed(4);
		
		final GameObject theWind = new GameObject();
		theWind.setImage(3, wind);
		theWind.setHitbox(Hitbox.EXACT);
		theWind.addEvent(Factory.follow(theEngine, theWind, theEngine.width - 45, 10));
		theWind.addEvent(()-> {if(gm.collidesWith(theWind)) gm.currX += 1;});
		
		final Dummy thePropeller = new Dummy(0,0);
		thePropeller.setImage(2, propeller);
		thePropeller.addEvent(Factory.follow(theEngine, thePropeller, theEngine.width - 45, 10));
		thePropeller.addEvent(Factory.hitMain(thePropeller, gm, -1));
		
		theEngine.appendPath(1300, 3400, 0, false, ()-> theEngine.setMoveSpeed(1.7f));
		theEngine.appendPath(344, 3400,0,false,()->
		{	
			add(Factory.animationBrake(thePropeller, 25, 50));
			discard(theWind);
			add(Factory.soundFade(propellersound, 0, 1000, true));
		});
		theEngine.appendPath(30, size.height - 110, 0, false, ()-> theEngine.setMoveSpeed(0));
		
		/*
		 * Zapper
		 */
		final PathDrone zap = new PathDrone(572, 3455);
		zap.setImage(1, zapper);
		zap.setHitbox(Hitbox.EXACT);
		zap.addEvent(Factory.hitMain(zap, gm, -1));
		zap.setMoveSpeed(4);
		zap.appendPath(1700, 3455, 0, false, ()-> zap.setMoveSpeed(6));
		zap.appendPath(1100, 3455, 0, false, ()-> zap.setMoveSpeed(2));
		zap.appendPath(1100, 3300);
		zap.appendPath(1100, 3530, 0, false, ()-> zap.setMoveSpeed(5));
		zap.appendPath(1050, 3530);
		zap.appendPath(1050, 3455, 0, false, ()-> zap.setMoveSpeed(3));
		zap.appendPath(600, 3455);
		zap.appendPath(1810, 3455, 0, false, ()-> zap.setMoveSpeed(6));
		zap.appendPath(400, 3300,  0, false, ()-> zap.setMoveSpeed(3));
		zap.appendPath(350, 3500);	
		zap.appendPath(0, size.height - 40, 0, false, ()-> zap.setMoveSpeed(0));	
		
		/*
		 * Top-Middle side saws
		 */
		GameObject leftSaws = new GameObject();
		leftSaws.currX = 196;
		leftSaws.currY = 2481;
		leftSaws.setImage(4, swleft);
		leftSaws.addEvent(Factory.hitMain(leftSaws, gm, -1));
		
		GameObject rightSaws = new GameObject();
		rightSaws.currX = 615;
		rightSaws.currY = 2481;
		rightSaws.setImage(4, swright);
		rightSaws.addEvent(Factory.hitMain(rightSaws, gm, -1));
		
		add(leftSaws, rightSaws);
		
		/*
		 * Weird things
		 */
		GameObject thing1 = new GameObject();
		thing1.setImage(3, thing);
		thing1.setHitbox(Hitbox.EXACT);
		thing1.addEvent(Factory.hitMain(thing1, gm, -1));
		thing1.currX = 236;
		thing1.currY = 2956;
		
		float distance = 20;
		
		GameObject thing2 = thing1.getClone(567, 2746);
		thing2.addEvent(Factory.hitMain(thing2, gm, -1));
		
		GameObject thing3 = thing1.getClone(567, 2536);
		thing3.addEvent(Factory.hitMain(thing3, gm, -1));
		
		GameObject thing4 = thing1.getClone(366 - distance, 2944);
		thing4.addEvent(Factory.hitMain(thing4, gm, -1));

		GameObject thing5 = thing1.getClone(401, 2944);
		thing5.addEvent(Factory.hitMain(thing5, gm, -1));

		GameObject thing6 = thing1.getClone(436 + distance, 2944);
		thing6.addEvent(Factory.hitMain(thing6, gm, -1));

		GameObject thing7 = thing1.getClone(366 - distance, 2734);
		thing7.addEvent(Factory.hitMain(thing7, gm, -1));

		GameObject thing8 = thing1.getClone(401, 2734);
		thing8.addEvent(Factory.hitMain(thing8, gm, -1));

		GameObject thing9 = thing1.getClone(436 + distance, 2734);
		thing9.addEvent(Factory.hitMain(thing9, gm, -1));
		
		GameObject thing10 = thing1.getClone(366 - distance, 2524);
		thing10.addEvent(Factory.hitMain(thing10, gm, -1));

		GameObject thing11 = thing1.getClone(401, 2524);
		thing11.addEvent(Factory.hitMain(thing11, gm, -1));

		GameObject thing12 = thing1.getClone(436 + distance, 2524);
		thing12.addEvent(Factory.hitMain(thing12, gm, -1));
		
		add(thing1,thing2,thing3,thing4,thing5,thing6,thing7,thing8,thing9,thing10,thing11,thing12);
		
		/*
		 * Spike Cars
		 */
		PathDrone car1 = new PathDrone(865, 2345);
		car1.setImage(spikecar);
		car1.setHitbox(Hitbox.EXACT);
		car1.setMoveSpeed(1.6f);
		car1.appendPath(865, 2345);
		car1.appendPath(1028, 2345);
		car1.addEvent(Factory.hitMain(car1, gm, -1));
		
		PathDrone car2 = car1.getClone(1503, 2275);
		car2.clearData();
		car2.appendPath(1503, 2275);
		car2.appendPath(1672, 2275);
		car2.addEvent(Factory.hitMain(car2, gm, -1));
		
		PathDrone car3 = car1.getClone(2134, 2435);
		car3.clearData();
		car3.appendPath(2134, 2435);
		car3.appendPath(2523, 2435);
		car3.addEvent(Factory.hitMain(car3, gm, -1));
		
		PathDrone car4 = car1.getClone(2020, 2366);
		car4.setImage(spikecar2);
		car4.clearData();
		car4.appendPath(2020, 2366);
		car4.appendPath(2020, 2282);
		car4.addEvent(Factory.hitMain(car4, gm, -1));
		
		PathDrone car5 = car1.getClone(2896, 2360);
		car5.clearData();
		car5.appendPath(2896, 2360);
		car5.appendPath(3137, 2360);
		car5.addEvent(Factory.hitMain(car5, gm, -1));
		
		add(car1,car2,car3,car4,car5);
		
		/*
		 * Missile turret
		 */
		Particle trailer = new Particle();
		trailer.setImage(3,smallExp);
		
		Particle splitImpact = new Particle();
		splitImpact.setImage(3,smallExp);
		splitImpact.setIntroSound(sexp);
		
		Projectile split = new Projectile(0, 0, gm);
		split.setImage(3, splits);
		split.setMoveSpeed(8);
		split.setImpact(splitImpact);
		split.setHitbox(Hitbox.EXACT);
		
		Shrapnel shra = new Shrapnel(0, 0, split, gm);
		shra.setImage(3,explosion);
		shra.offsetY = 20;
		shra.setIntroSound(exp);
		
		Missile missile = new Missile(0, 0, gm);
		missile.setImage(missileimg);
		missile.setMoveSpeed(8);
		missile.setImpact(shra);
		missile.setTrailer(trailer);
		missile.setProperties(MissileProperties.FAST_VERY_FLOATY);
		
		Particle firingParticle = new Particle();
		firingParticle.setImage(3, firingAnim);
		firingParticle.setIntroSound(mfire);
		
		final Weapon turret = new Weapon(1535, 1865, 1, 1, 250, gm);
		turret.setImage(turretimg);
		turret.setFiringParticle(firingParticle);
		turret.setFrontFire(true);
		turret.merge(split,missile,magic);
		turret.setProjectile(missile);
		turret.setFiringParticle(firingParticle);
		turret.setRotationSpeed(0.01f);
		turret.addEvent(()->{if(gm.getState() == CharacterState.DEAD)turret.halt(true);});
		turret.addEvent(()->
		{	
			float value = (turret.currX - gm.currX > 0) ? 2 : -2;			
			
			if(turret.canGoTo(turret.currX + value, 1865))
				turret.moveToward(gm.currX, 1865, 1.5f);
		});
		
		/*
		 * Push Up
		 */
		final SolidPlatform pushing = new SolidPlatform(0,0, gm);
		pushing.setMoveSpeed(7);
		pushing.setImage(pushup);
		pushing.appendPath(3299, 2467, 1, true, null);
		pushing.appendPath(3299, 600,  0, false, ()->
		{
			elevator.stop();
			pushing.freeze();
		});
		pushing.freeze();
		add(pushing);
		
		/*
		 * Stalker
		 */
		stalk = new SolidPlatform(3344, 435, gm);
		stalk.setImage(stalker);
		stalk.setMoveSpeed(4);
		stalk.appendPath(785, 435,0,false,()->{
			shut.play();
			collapsing.stop();
			game.drugVertical(0, 0);
			game.drugHorizontal(0, 0);
		});
		gm.allowOverlapping(stalk);
		
		/*
		 * Meteorites
		 */
		final Projectile met1 = getMetroid(2233, 577), met2 = getMetroid(1313, 577), met3 = getMetroid(1313, 545), met4 = getMetroid(1000, 561), met5 = getMetroid(1000, 455),
						 met6 = getMetroid(383, 561), met7 = getMetroid(343, 561), met8 = getMetroid(303, 561),
						 met10 = getMetroid(1, 549), met11 = getMetroid(1, 467);
		met1.merge(met2,met3,met4,met5,met6,met7,met8,met10,met11);
		
		/*
		 * Blocking poles at before goal
		 */
		final MovableObject p1 = new MovableObject();
		p1.moveTo(1472, 541);
		p1.setImage(dummy);
		
		final MovableObject p2 = p1.getClone(1275, 439);
		final MovableObject p3 = p1.getClone(1095, 430);
		final MovableObject p4 = p1.getClone(1095, 550);
		
		/*
		 * Goal
		 */
		GameObject goalDoor = new GameObject();
		goalDoor.setImage(goal);
		goalDoor.currX = 784;
		goalDoor.currY = 517;
		add(goalDoor);
		
		/*
		 * Hearts
		 */
		Dummy heart1 = new Dummy(4134, 2530);
		heart1.setImage(5, extraHp);
		heart1.addEvent(getHeartEvent(heart1));
		heart1.zIndex(-1);
		
		Dummy heart2 = heart1.getClone(241, 2547);
		heart2.addEvent(getHeartEvent(heart2));
		
		add(heart1, heart2);
		
		/*
		 * Lobot
		 */
		if(deathCounter >= 7)
		{
			final GameObject lob = new GameObject();
			lob.currX = 4398;
			lob.currY = 361;
			lob.setImage(lobot);
			lob.addEvent(()->
			{
				if(!once && lob.collidesWith(gm))
				{
					once = true;
					robottalk.play();
					gm.freeze();
					gm.vx = 0;
					gm.vy = 0;
					add(Factory.printText("This stage is so hard!\nHere, have an extra heart :)", Color.WHITE, null, 200, lob, -40, -40, ()->gm.unfreeze()));

					PathDrone heart3 = new PathDrone(lob.currX + lob.width / 2 - 10, lob.currY + lob.height / 2 - 30);
					heart3.setImage(5, extraHp);
					heart3.addEvent(getHeartEvent(heart3));
					heart3.setMoveSpeed(.5f);
					heart3.appendPath(heart3.currX, heart3.currY - 40);
					
					add(heart3);
				}
			});
			add(lob);
		}
		
		/*
		 * Hit Event
		 */
		gm.setHitEvent(new HitEvent()
		{	
			@Override
			public void eventHandling(GameObject hitter) 
			{
				if(hitter.sameAs(ld) || hitter.sameAs(turret) || hitter.sameAs(met1))
					gm.hit(-1);
				
				if (0 >= gm.getHP())
					gm.hit(-99);
			}
		});
		
		/*
		 * Tile Event
		 */
		gm.addTileEvent(new TileEvent() 
		{
			boolean event1,event2,event3,event4,event5,event6,event7;
			
			@Override
			public void eventHandling(byte tileType) 
			{
				switch(tileType)
				{
					case Engine.AREA_TRIGGER_0:
						if(!event1)
						{
							add(ld);
							event1 = true;
						}
						break;
					case Engine.AREA_TRIGGER_1:
						if(!event2)
						{
							theShutter.setMoveSpeed(10);
							collapsing.play(true);
							event2 = true;
						}
						break;
					case Engine.AREA_TRIGGER_2:
						if(!event3)
						{
							event3 = true;
							
							bandmove.play(true, 0);
							add(Factory.soundFalloff(bandmove, solp, gm, 400, 3, 100, 0.7f));
							
							if(energy1 && energy2 && energy3)
							{								
								solp.appendPath(4490, 3839,0,false,moveMagnet(0));
								solp.appendPath(3800, 3839,0,false,moveMagnet(1));
								solp.appendPath(3417, 3683);
								solp.appendPath(3417, 3255,0,false,moveMagnet(2));
								solp.appendPath(3020, 3255);
								solp.appendPath(2571, 3490,0,false,moveMagnet(3));
								solp.appendPath(2000, 3490,0,false,()->
								{
									propellersound.play(true,0);
									add(zap, theEngine, theWind, thePropeller, Factory.soundFalloff(propellersound, gm, theEngine, 600, 3, 100, 1));
								});
								solp.appendPath(353, 3490, 0, false, ()->solp.setMoveSpeed(1.3f));
								solp.appendPath(353, 2467);
								solp.appendPath(510, 2467,10,false,new Event()
								{
									@Override
									public void eventHandling() 
									{
										solp.setMoveSpeed(0);
										solp.clearData();
										add(turret);
										discard(zap);
										bandmove.stop();
									}
								});
								
							}
							else
								solp.appendPath(solp.currX - 400, size.height - solp.height);
						}
						break;
					case Engine.AREA_TRIGGER_4:
						if(!event7)
						{
							event7 = true;
							Factory.tileDeformer(p1, Engine.SOLID, false).eventHandling();
							Factory.tileDeformer(p2, Engine.SOLID, false).eventHandling();
							Factory.tileDeformer(p3, Engine.SOLID, false).eventHandling();
							Factory.tileDeformer(p4, Engine.SOLID, false).eventHandling();
						}
						break;
					case Engine.AREA_TRIGGER_8:
						if(!event4)
						{
							event4 = true;
							discard(crusherDown, crusherLeft, crusherUp, magnetDown, magnetLeft, magnetUp);
						}
						break;
					case Engine.AREA_TRIGGER_6:
						if(!event5)
						{
							event5 = true;
							pushing.unfreeze();
							elevator.play(0.5);
						}
						break;
					case Engine.AREA_TRIGGER_9:
						if(!event6)
						{
							event6 = true;
							game.drugVertical(2, 2);
							game.drugHorizontal(2, 2);
							add(stalk,met1,met2,met3,met4,met5,met6,met7,met8,met10,met11);
							collapsing.play(true);
							gm.avoidOverlapping(stalk);
						}
						
				}
			}
		});
	}
	
	Event getHeartEvent(final GameObject heart)
	{
		return new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(heart.collidesWith(gm))
				{
					health.play();
					discard(heart);
					gm.hit(1);
				}
			}
		};
	}
	
	Projectile getMetroid(float x, float y)
	{
		Particle exp = new Particle();
		exp.setImage(3, metExplode);
		exp.setIntroSound(bam);
		
		Projectile proj = new Projectile(x,y,gm, stalk);
		proj.setMoveSpeed(6.5f);
		proj.setImpact(exp);
		proj.useFastCollisionCheck(true);
		proj.addEvent(()->proj.rotation+=6);
		proj.setTarget(size.width, y);
		proj.setImage(metroit);
		proj.setDisposable(true);
		proj.setHitbox(Hitbox.CIRCLE);
		
		return proj;
	}
	
	Event moveMagnet(final int number)
	{
		return new Event()
		{
			@Override
			public void eventHandling() 
			{
				if(EntityStuff.distance(gm, solp) > 270) //Dont move the magnets if gm is not on the band
					return;
				
				switch(number)
				{
					case 0:
						magnetmove.play();
						crusherDown.appendPath(4120, 3598, 0, false, ()-> crusherDown.setMoveSpeed(2.7f));
						crusherDown.appendPath(4120, size.height - 176, 0, false, ()->
						{
							magnetfinish.play();
							magnetDown.currX += 0.01f;
							Factory.tileDeformer(magnetDown, Engine.SOLID, false).eventHandling();
							crusherDown.clearData();
						});
						break;
					case 1:
						magnetmove.play();
						crusherLeft.appendPath(3107, 3369);
						crusherLeft.appendPath(3700, 3369, 0, false, ()->
						{	
							magnetfinish.play();
							magnetLeft.currX += 0.01f;
							Factory.tileDeformer(magnetLeft, Engine.SOLID, false).eventHandling();
							crusherLeft.clearData();
						});
						break;
					case 2:
						magnetmove.play();
						crusherUp.appendPath(3037, 3425);
						crusherUp.appendPath(3037, 3056,0,false,()->
						{
							magnetfinish.play();
							magnetUp.currX += 0.01f;
							Factory.tileDeformer(magnetUp, Engine.SOLID, false).eventHandling();
							crusherUp.clearData();
						});
						break;
					case 3:
						magnetmove.play();
						magnetmove.play();
						magnetmove.play();
						crusherUp.setMoveSpeed(10);
						crusherUp.appendPath(2484, 3580);
						crusherUp.appendPath(2054, 3611,0,false,()->
						{
							magnetfinish.play();
							crusherUp.moveTo(2054, 3611);
							magnetUp.currX += 0.01f;
							moveMU.eventHandling();
							Factory.tileDeformer(magnetUp, Engine.SOLID, false).eventHandling();
							crusherUp.clearData();
						});
						
						crusherLeft.setMoveSpeed(15);
						crusherLeft.appendPath(2054, 3850);
						crusherLeft.appendPath(2038, 3377,0,false,()->
						{	
							magnetfinish.play();
							crusherLeft.moveTo(2038, 3377);
							magnetLeft.currX += 0.01f;
							moveML.eventHandling();
							Factory.tileDeformer(magnetLeft, Engine.SOLID, false).eventHandling();
							crusherLeft.clearData();
						});
						
						crusherDown.setMoveSpeed(20);
						crusherDown.appendPath(2467, 3237);
						crusherDown.appendPath(2054, 3377,0,false,()->
						{	
							magnetfinish.play();
							crusherDown.moveTo(2054, 3377);
							magnetDown.currX += 0.01f;
							moveMD.eventHandling();
							Factory.tileDeformer(magnetDown, Engine.SOLID, false).eventHandling();
							crusherDown.clearData();
						});
						
						break;
				}				
			}
		};
	}
	
	Event getEnergyEvent(final GameObject obj, final int number)
	{
		return new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(obj.collidesWith(gm))
				{
					switch(number)
					{
					case 1:
						energy1 = true;
						break;
					case 2:
						energy2 = true;
						break;
					case 3:
						energy3 = true;
						break;
					}
					
					discard(obj);
					
					if(energy1 && energy2 && energy3)
						elpower.play();
					else
						collectsound2.play();
				}
			}
		};
	}
	
	Event getCollectEvent(final GameObject obj, final int number)
	{
		return new Event()
		{	
			@Override
			public void eventHandling() 
			{
				if(obj.collidesWith(gm))
				{
					discard(obj);
					collectsound.play();
					switch(number)
					{
					case 1:
						collect1 = true;
						break;
					case 2:
						collect2 = true;
						break;
					case 3:
						collect3 = true;
						break;
					case 4:
						collect4 = true;
						break;
					case 5:
						collect5 = true;
						break;
					}
				}
				
			}
		};
	}
	
	@Override
	public void extra()
	{
		if(gm.currY + gm.height >= 3900)
			for(GameObject go : saws)
				if(go.collidesWith(gm))
					gm.hit(-99);
	}
	
	@Override
	public Serializable getMeta()
	{
		return "deathCounter=" + (deathCounter + 1);
	}
	
	@Override
	public void setMeta(Serializable meta)
	{
		String data = (String) meta;
		
		if(!data.isEmpty())
			deathCounter = Integer.parseInt(data.substring(data.indexOf("=") + 1, data.length()));
	}
} 