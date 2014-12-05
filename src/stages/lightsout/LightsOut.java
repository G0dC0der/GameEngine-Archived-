package stages.lightsout;

import game.core.Engine.Direction;
import game.core.GameObject;
import game.core.GameObject.Hitbox;
import game.core.MainCharacter.CharacterState;
import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.AutoLoad;
import game.development.StageBuilder;
import game.essentials.Factory;
import game.essentials.GFX;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import game.movable.Missile;
import game.movable.PathDrone;
import game.movable.SimpleWeapon;
import game.movable.SolidPlatform;
import game.objects.CheckpointsHandler;
import game.objects.Particle;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

@AutoDispose
@AutoInstall(mainPath="res/general", path=LightsOut.PATH)
@Playable(name="Lights Out", description="Stage: Lights Out\nAuthor: Pojahn Moradi\nDifficulty: 5\nAverage time: 90 sec\nProfessional time: 70 sec\nObjective: Enter goal.")
public class LightsOut extends StageBuilder
{
	static final String PATH = "res/lightsout";
	
	@AutoLoad(path=PATH, type=VisualType.IMAGE)
	private Image2D main[], death[], saw90, item, flag[], dummy, cannon, missile, explosion[], key, door, spikes, spikesblock;
	
	@AutoLoad(path=PATH, type=VisualType.SOUND)
	private Sound spikesend, spikesspawn, collect, missilefire, missileexp;
	
	private Music electric;
	
	private CheckpointsHandler cph;
	
	@Override
	public void init() 
	{
		super.init();
		
		electric = TinySound.loadMusic(new java.io.File("res/diamondcave/frying.wav"));
		setStageMusic(PATH + "/song.ogg", 0f, 1.0f);
	}
	
	@Override
	public void build() 
	{
		super.build();
		electric.setVolume(0);
		electric.play(true);
		add(Factory.soundFalloff(electric, gm, 1864, 322, 500, 0, 20, 1.0f));
		
		/*
		 * Main
		 */
		gm.setImage(3, main);
		gm.deathImg.setImage(4,death);
		gm.setMultiFaced(true);
		gm.addTileEvent(Factory.slipperWalls(gm));
		
		/*
		 * Basics
		 */
		game.timeColor = Color.WHITE;
		
		/*
		 * Top Saws, left to right.
		 */
		PathDrone saw1 = getSaw(0, 13);
		
		PathDrone saw2 = getSaw(4,7);
		saw2.appendPath();
		saw2.appendPath(4 * 30, 5 * 30);
		
		PathDrone saw3 = getSaw(7, 5);
		saw3.appendPath();
		saw3.appendPath(7 * 30, 7 * 30);
		
		PathDrone saw4 = getSaw(10, 7);
		saw4.appendPath();
		saw4.appendPath(10 * 30, 5 * 30);
		
		PathDrone saw5 = getSaw(13, 5);
		saw5.appendPath();
		saw5.appendPath(13 * 30, 7 * 30);
		
		PathDrone saw6 = getSaw(17, 13);
		saw6.loc.x -= 18;
		
		PathDrone saw7 = getSaw(23, 13);
		saw7.loc.x -= 10;
		
		PathDrone saw8 = getSaw(29,12);
		
		PathDrone saw9 = getSaw(0, 0);
		saw9.moveTo(1005, 295 + 25);
		
		PathDrone saw10 = getSaw(38, 12);
		
		PathDrone saw11 = getSaw(44, 11);
		
		add(saw1,saw2,saw3,saw4,saw5,saw6,saw7,saw8,saw9,saw10,saw11,getSaw(81, 6),getSaw(81, 11),getSaw(93, 12));
		
		/*
		 * Lightning
		 */
		GameObject light = new GameObject()
		{
			@Override
			public void drawSpecial(SpriteBatch batch) 
			{
				GFX.drawLightning(batch, 1865, 0, 1865, 423, 150, .5f, 2.5f, 0, 1, false, Color.BLACK);
			}
		};
		light.moveTo(1865 - 20, 0);
		light.width = 5;
		light.height = 423;
		light.addEvent(Factory.hitMain(light, gm, -1));
		
		/*
		 * Item
		 */
		GameObject i = new GameObject();
		i.moveTo(88, 301);
		i.setImage(item);
		i.addEvent(()->{
			if(i.collidesWith(gm))
			{
				discard(i, light);
				electric.stop();
				collect.play();
			}
		});
		add(light, i);
		
		/*
		 * Goal
		 */
		GameObject goal = new GameObject();
		goal.moveTo(2954, 244);
		goal.setImage(7, flag);
		goal.addEvent(()->{
			if(goal.collidesWith(gm))
				gm.setState(CharacterState.FINISH);
		});
		add(goal);
		
		/*
		 * Cannons
		 */
		SolidPlatform dummy1 = new SolidPlatform(690, 1033, gm);
		dummy1.setImage(dummy);
		dummy1.setMoveSpeed(1.5f);
		dummy1.appendPath();
		dummy1.appendPath(dummy1.loc.x, 884);

		SolidPlatform dummy2 = new SolidPlatform(690, 719, gm);
		dummy2.setImage(dummy);
		dummy2.setMoveSpeed(1.5f);
		dummy2.appendPath();
		dummy2.appendPath(dummy2.loc.x, 868);
		
		Particle trailer = new Particle();
		trailer.setImage(1, explosion);
		trailer.scale = .7f;
		trailer.offsetY = -5;

		Particle fireExp = new Particle();
		fireExp.setImage(3, explosion);
		fireExp.offsetX = missile.getWidth();
		fireExp.setIntroSound(missileexp);
		fireExp.getSoundBank().useFallOff(true);
		fireExp.getSoundBank().power = 15;
		
		Particle firingAnim = new Particle();
		firingAnim.setImage(2, explosion);
		firingAnim.offsetX = -15;
		firingAnim.setIntroSound(missilefire);
		firingAnim.getSoundBank().useFallOff(true);
		firingAnim.getSoundBank().power = 15;
		
		Missile proj = new Missile(0, 0, gm);
		proj.setImage(missile);
		proj.setMoveSpeed(4);
		proj.setTrailer(trailer);
		proj.setTrailerDelay(8);
		proj.setImpact(fireExp);
		proj.adjustTrailer(true);
		
		SimpleWeapon weap1 = new SimpleWeapon(0, 0, proj, Direction.E, 80);
		weap1.setImage(cannon);
		weap1.addEvent(Factory.follow(dummy1, weap1, 0, 0));
		weap1.spawnOffset(weap1.width - 10, 0);
		weap1.setFiringAnimation(firingAnim);
		
		SimpleWeapon weap2 = new SimpleWeapon(0, 0, proj, Direction.E, 80);
		weap2.setImage(cannon);
		weap2.addEvent(Factory.follow(dummy2, weap2, 0, 0));
		weap2.spawnOffset(weap1.width - 10, 0);
		weap2.setFiringAnimation(firingAnim);
		
		add(dummy1,dummy2, weap1, weap2);
		
		/*
		 * Key & Door
		 */
		GameObject theDoor = new GameObject();
		theDoor.moveTo(1620, 671 - 5);
		theDoor.setImage(door);
		gm.avoidOverlapping(theDoor);
		
		GameObject k = new GameObject();
		k.moveTo(695, 930);
		k.setImage(key);
		k.addEvent(()->{
			if(k.collidesWith(gm))
			{
				discard(theDoor,k);
				gm.allowOverlapping(theDoor);
				collect.play();
			}
		});
		add(theDoor, k);
		
		/*
		 * Start Spikes
		 */
		float xStart = 2731;
		float xEnd = 1880;
		
		for(int value = 0; value < 3; value++)
		{
			float y = 1230 + 8;
			if(value == 1)
				y = 1050 + 8;
			else if(value == 2)
				y = 870 + 8;
			
			PathDrone spik = new PathDrone(xStart, y);
			spik.appendPath(spik.loc.x,spik.loc.y,0,true,()->{spikesspawn.play(SoundBank.getVolume(spik, gm, 700, 1.0f, 30));});
			spik.appendPath(xEnd, y,0,false,()->{spikesend.play(SoundBank.getVolume(spik, gm, 700, 1.0f, 30));});
			spik.setImage(spikes);
			spik.setMoveSpeed(4);
			spik.addEvent(Factory.hitMain(spik, gm, -1));
			
			SolidPlatform solp = new SolidPlatform(xStart + spik.width, y, gm);
			solp.appendPath(solp.loc.x,solp.loc.y,0,true,null);
			solp.appendPath(xEnd + spik.width, y);
			solp.setMoveSpeed(4);
			solp.setImage(spikesblock);
			
			add(spik, solp);
		}
		
		/*
		 * Checkpoint
		 */
		Vector2 cp1 = new Vector2(1772, 355);
		
		if(cph == null)
		{
			cph = new CheckpointsHandler();
			cph.appendCheckpoint(cp1, 1602, 395, 103, 47);
			cph.setReachEvent(()-> GFX.renderCheckpoint());
		}
		
		Vector2 latestCp = cph.getLastestCheckpoint();
		if(latestCp != null)
			gm.loc.set(latestCp);
		
		cph.setUsers(gm);
		add(cph);
		
		/*
		 * Finalizing
		 */
		gm.setHitEvent((hitter)->{
			if(hitter.sameAs(proj))
				gm.hit(-1);
		});
	}
	
	protected boolean isSafe() 
	{
		return cph.getLastestCheckpoint() != null;
	}
	
	PathDrone getSaw(int x, int y)
	{
		PathDrone saw = new PathDrone(x * 30, y * 30);
		saw.setImage(saw90);
		saw.addEvent(()->{saw.rotation += 7;});
		saw.addEvent(Factory.hitMain(saw, gm, -1));
		saw.setHitbox(Hitbox.CIRCLE);
		saw.setMoveSpeed(.7f);
		
		return saw;
	}
}
