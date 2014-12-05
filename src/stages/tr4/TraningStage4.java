package stages.tr4;

import game.core.Engine.Direction;
import game.core.GameObject;
import game.core.GameObject.Hitbox;
import game.core.MainCharacter.CharacterState;
import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.AutoLoad;
import game.essentials.Animation;
import game.essentials.Factory;
import game.essentials.GFX;
import game.essentials.Image2D;
import game.movable.PathDrone;
import game.movable.SolidPlatform;
import game.objects.CheckpointsHandler;
import game.objects.Flash;
import game.objects.Particle;
import game.objects.Wind;

import java.io.File;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import stages.traning.AbstractTraningStage;
import ui.accessories.Playable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;

@AutoDispose
@AutoInstall(mainPath="res/general", path=TraningStage4.RES)
@Playable(name="Traning Stage 4", description="Practise your skills.")
public class TraningStage4 extends AbstractTraningStage
{
	static final String RES = "res/traning4";

	@AutoLoad(path=RES, type=VisualType.IMAGE)
	private Image2D elevator, hydrolics, redPlatform, head[], moving, cloud[], platform, crystal[], gateImg, enemy[], fan[], wind[], weak[], goal, headgone[], friendImg;
	private Sound elevatorStop, elevatorWorking, headMove, reporting, teleport, collect, doorOpen, weakDie/*, talking*/;
	private Music propeller;
	private BitmapFont talkingFont;
	private Flash flash;
	private CheckpointsHandler cph;
	private boolean chaserHunting;
	private int diamonds, collectedDiamonds;
	
	@Override
	public void init() 
	{
		try
		{
			super.init();
			
			talkingFont = new BitmapFont(Gdx.files.internal("res/traning1/talking.fnt"), true);
//			talking = TinySound.loadSound(new File(RES + "/talking.wav"));
			propeller = TinySound.loadMusic(new File(RES + "/propeller.wav"));
			weakDie = TinySound.loadSound(new File(RES + "/weakDie.wav"));
			collect = TinySound.loadSound(new File(RES + "/collect.wav"));
			headMove = TinySound.loadSound(new File(RES + "/headmove.wav"));
			reporting = TinySound.loadSound(new File(RES + "/reporting.wav"));
			teleport = TinySound.loadSound(new File(RES + "/teleport.wav"));
			elevatorStop = TinySound.loadSound(new File("res/steelfactory/shut.wav"));
			elevatorWorking = TinySound.loadSound(new File("res/steelfactory/elevator.wav"));
			doorOpen = TinySound.loadSound(new File("res/flyingb/open.wav"));
			setStageMusic(RES + "/song.ogg", 16.13f, .7f);
			weakDie.setVolume(.65f);
			
			flash = new Flash(Color.BLACK, 200);
			setFriendTextColor(Color.WHITE);
			setFriendImage(friendImg);
//			setTalking(talking);
			setFriendFont(talkingFont);
		}
		catch(Exception e)
		{
			System.err.println("Error loading resources.");
			e.printStackTrace();
		}
	}
	
	@Override
	public void build() 
	{
		/*
		 * General stuff
		 */
		super.build();
		game.timeColor = Color.WHITE;
		chaserHunting = false;
		diamonds = collectedDiamonds = 0;
		setPeople(gm);
		
		/*
		 * Main Character
		 */
		gm.addTileEvent(Factory.slipperWalls(gm));
		
		/*
		 * First elevator
		 */
		SolidPlatform el = new SolidPlatform(4736, 2978, gm);
		el.setImage(elevator);
		el.appendPath(4736, 1904, 0, false, ()->{
			elevatorWorking.stop();
			elevatorStop.play();
		});
		el.setMoveSpeed(0);
		
		SolidPlatform hydr = new SolidPlatform(4756, 3018, gm);
		hydr.setImage(hydrolics);
		hydr.appendPath(4756, 1944);
		hydr.setMoveSpeed(0);
		
		GameObject elevatorActivator = new GameObject();
		elevatorActivator.moveTo(el.loc.x, el.loc.y - 50);
		elevatorActivator.width = el.width;
		elevatorActivator.height = 50;
		elevatorActivator.addEvent(()->{
			if(elevatorActivator.collidesWith(gm))
			{
				discard(elevatorActivator);
				el.setMoveSpeed(5);
				hydr.setMoveSpeed(5);
				elevatorWorking.play();
			}
		});
		
		add(el,hydr,elevatorActivator);
		
		/*
		 * Red Platforms
		 */
		SolidPlatform redPlatform1 = new SolidPlatform(4318, 2160, gm);
		redPlatform1.setImage(redPlatform);
		redPlatform1.appendPath(4318, 2160, 40, false, null);
		redPlatform1.appendPath(4138, 2160, 40, false, null);
		redPlatform1.setMoveSpeed(1);
		
		SolidPlatform redPlatform2 = new SolidPlatform(3870, 2160, gm);
		redPlatform2.setImage(redPlatform);
		redPlatform2.appendPath(3870, 2160, 40, false, null);
		redPlatform2.appendPath(4050, 2160, 40, false, null);
		redPlatform2.setMoveSpeed(1);
		
		add(redPlatform1, redPlatform2);
		
		/*
		 * Head Platform
		 */
		final Particle teleAnim = new Particle();
		teleAnim.setImage(1, headgone);
		
		Animation<Image2D> headImage = new Animation<>(7, head);
		headImage.pingPong(true);
		
		SolidPlatform theHead = new SolidPlatform(3176, 2728, gm);
		theHead.setImage(headImage);
		theHead.appendPath(3176, 2728, 70, true, ()->{
			add(teleAnim.getClone(3016, 2728));
			headMove.play();
		});
		theHead.appendPath(3016, 2728, 70, true, ()->{
			add(teleAnim.getClone(3176, 2728));
			headMove.play();
		});
		
		add(Factory.soundFalloff(headMove, theHead, gm, 800, 0, 50, 1.0f));
		add(theHead);
		
		/*
		 * Moving platforms in the house
		 */
		SolidPlatform mov1 = new SolidPlatform(1984, 2176, gm);
		mov1.appendPath(1984, 2176, 120, false, null);
		mov1.appendPath(1920, 2176, 120, false, null);
		mov1.setMoveSpeed(5);
		mov1.setImage(moving);
		
		SolidPlatform mov2 = mov1.getClone(1857, 2080);
		mov2.clearData();
		mov2.appendPath(1857, 2080, 120, false, null);
		mov2.appendPath(1920, 2080, 120, false, null);
		mov2.appendPath(1857, 2080, 120, false, null);
		mov2.appendPath(2016, 2080, 120, false, null);
		
		SolidPlatform mov3 = mov1.getClone(1824, 2144);
		mov3.clearData();
		mov3.appendPath(1824, 2144, 120, false, null);
		mov3.appendPath(1664, 2144, 120, false, null);
		
		add(mov1, mov2, mov3);
		
		/*
		 * Chaser
		 */
		Animation<Image2D> chaserImage = new Animation<>(6, cloud);
		chaserImage.pingPong(true);
		
		final float startX = 90 * 16;
		final float startY = 151 * 16;
		
		PathDrone chaser = new PathDrone(startX, startY);
		chaser.setMoveSpeed(2f);
		chaser.setHitbox(Hitbox.CIRCLE);
		chaser.appendPath(84 * 16, 151 * 16);
		chaser.appendPath(84 * 16, 149 * 16);
		chaser.appendPath(80 * 16, 149 * 16);
		chaser.appendPath(80 * 16, 151 * 16);
		chaser.appendPath(76 * 16, 151 * 16);
		chaser.appendPath(76 * 16, 143 * 16);
		chaser.appendPath(72 * 16, 143 * 16);
		chaser.appendPath(72 * 16, 149 * 16);
		chaser.appendPath(64 * 16, 149 * 16);
		chaser.appendPath(64 * 16, 147 * 16);
		chaser.appendPath(60 * 16, 147 * 16);
		chaser.appendPath(60 * 16, 151 * 16);
		chaser.appendPath(56 * 16, 151 * 16);
		chaser.appendPath(56 * 16, 149 * 16);
		chaser.appendPath(50 * 16, 149 * 16);
		chaser.appendPath(50 * 16, 147 * 16);
		chaser.appendPath(48 * 16, 147 * 16, 0, false, ()->{chaser.freeze();});
		chaser.setImage(chaserImage);
		chaser.addEvent(()->{
			if(chaser.collidesWith(gm))
			{
				chaserHunting = false;
				gm.moveTo((88 * 16) + 5, 151 * 16);
				gm.vx = gm.vy = 0;
				discard(chaser);
				chaser.rollback();
				chaser.moveTo(startX, startY);
				teleport.play();
				add(flash.getClone(0, 0));
			}
		});
		
		GameObject trigger = new GameObject();
		trigger.moveTo(86 * 16, 151 * 16);
		trigger.width = trigger.height = 24;
		trigger.addEvent(()->{
			if(!chaserHunting && trigger.collidesWith(gm))
			{
				reporting.play();
				teleport.stop();
				chaserHunting = true;
				chaser.unfreeze();
				add(chaser);
			}
		});
		
		add(trigger);
		
		/*
		 * Up and down moving platform
		 */
		SolidPlatform solp = new SolidPlatform(1313, 1920, gm);
		solp.setImage(platform);
		solp.setMoveSpeed(2);
		solp.appendPath(1313, 1920, 50, false, null);
		solp.appendPath(1313, 1463, 50, false, null);
		add(solp);
		
		/*
		 * Diamonds
		 */
		addDiamond(1854, 1557);
		addDiamond(2808, 1380);
		addDiamond(2504, 785);
		addDiamond(3288, 791);
		addDiamond(3834, 806);
		
		/*
		 * Gate
		 */
		GameObject gate = new GameObject();
		gate.moveTo(4304, 416);
		gate.setImage(gateImg);
		gate.addEvent(()->{
			if(collectedDiamonds == diamonds)
			{
				discard(gate);
				gm.allowOverlapping(gate);
				doorOpen.play();
			}
		});
		gm.avoidOverlapping(gate);
		add(gate);
		
		/*
		 * Diamond protector
		 */
		PathDrone prot = new PathDrone(2834, 1421);
		prot.appendPath(2834, 1421, 0, true, null);
		prot.appendPath(2111, 1421, 0, false, null);
		prot.setImage(4, enemy);
		prot.setMoveSpeed(3);
		prot.setHitbox(Hitbox.CIRCLE);
		prot.addEvent(()->{
			if(prot.collidesWith(gm))
			{
				gm.moveTo(2201, 1181);
				gm.vx = gm.vy = 0;
				teleport.play();
				add(flash.getClone(0, 0));
			}
		});
		add(prot);
		
		/*
		 * Fan and Wind
		 */
		GameObject f = new GameObject();
		f.setImage(2, fan);
		f.moveTo(3335, 0);
		
		Wind w = new Wind(3330, 42, Direction.S, 15, 300, gm);
		w.zIndex(50);
		w.setImage(4, wind);
		w.setHitbox(Hitbox.EXACT);
		
		propeller.setVolume(0);
		propeller.play(true);
		
		add(f, w, Factory.soundFalloff(propeller, f, gm, 600, 0, 40, 1.0f));
		
		/*
		 * Weak Platforms
		 */
		
		//First phase
		addWeak(192,16);
		addWeak(190,16);
		addWeak(188,16);
		addWeak(186,16);
		addWeak(186,14);
		addWeak(186,12);
		
		addWeak(184,18);
		addWeak(182,18);
		addWeak(180,18);
		
		//Second phase, the wall
		addWeak(171,0);
		addWeak(171,2);
		addWeak(171,4);
		addWeak(171,6);
		addWeak(171,8);
		addWeak(171,10);
		addWeak(171,12);
		addWeak(171,14);
		
		//Third phase
		addWeak(167,16);
		addWeak(165,16);
		addWeak(163,16);
		addWeak(157,16);
		
		/*
		 * Goal
		 */
		GameObject door = new GameObject();
		door.moveTo(248, 1552);
		door.setImage(goal);
		door.zIndex(-1);
		door.addEvent(()->{
			if(door.collidesWith(gm))
				gm.setState(CharacterState.FINISH);
		});
		add(door);
		
		/*
		 * Friends
		 */
		add(getFriend(1212, 1881, -20, -20, "Get on the moving platform and continue upwards."),
			getFriend(1613, 1033, 40, 0, "Collect all the five gems to open the port found at far east."),
			getFriend(3498, 217, -50, -20, "Jump while running at high speed or you may get dragged down."));
		
		/*
		 * Checkpoints
		 */
		if(cph == null)
		{
			cph = new CheckpointsHandler();
			cph.appendCheckpoint(3299, 2156, 3262, 2018, 125, 166);
			cph.appendCheckpoint(2256, 2092, 2256, 2092, 32, 20);
			cph.appendCheckpoint(1479, 1052, 1479, 1022, 75, 82);
			cph.appendCheckpoint(4164, 236, 4164, 66, 185, 228);
			cph.setReachEvent(()-> GFX.renderCheckpoint());
		}
		
		Vector2 latestCp = cph.getLastestCheckpoint();
		if(latestCp != null)
			gm.loc.set(latestCp);
		
		cph.setUsers(gm);
		add(cph);
	}
	
	@Override
	protected boolean isSafe() 
	{
		return cph.getLastestCheckpoint() != null;
	}
	
	void addWeak(int x, int y)
	{
		Animation<Image2D> destroyAnim = new Animation<>(6, weak);
		destroyAnim.setLoop(false);
		
		GameObject weakP = new GameObject();
		weakP.setImage(weak[0]);
		weakP.moveTo(x * 16, y * 16);
		weakP.addEvent(Factory.weakPlatform(weakP, destroyAnim, 45, weakDie, gm));
		
		add(weakP);
	}
	
	void addDiamond(int x, int y)
	{
		diamonds++;
		
		GameObject d = new GameObject();
		d.moveTo(x, y);
		d.setImage(6, crystal);
		d.setHitbox(Hitbox.EXACT);
		d.addEvent(()->{
			if(d.collidesWith(gm))
			{
				discard(d);
				collectedDiamonds++;
				collect.play();
			}
		});
		
		add(d);
	}
}