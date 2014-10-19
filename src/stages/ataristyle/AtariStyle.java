package stages.ataristyle;

import game.core.Engine.Direction;
import game.core.GameObject;
import game.core.MainCharacter.CharacterState;
import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.AutoLoad;
import game.development.StageBuilder;
import game.essentials.Factory;
import game.essentials.Animation;
import game.essentials.Image2D;
import game.movable.Projectile;
import game.movable.SimpleWeapon;
import kuusisto.tinysound.Sound;
import ui.accessories.Playable;

@AutoDispose
@AutoInstall(mainPath = "res/general", path = AtariStyle.PATH)
@Playable(name="Atari Style", description="Stage: Lights Out\nAuthor: Pojahn Moradi\nDifficulty: 5\nAverage time: 70 sec\nProfessional time: 50 sec\nObjective: Grab the coin.")
public class AtariStyle extends StageBuilder
{
	static final String PATH = "res/ataristyle";
	
	@AutoLoad(path=PATH, type=VisualType.IMAGE)
	private Image2D lethal[], turret[], weak[], goal, door, key;
	@AutoLoad(path=PATH, type=VisualType.SOUND)
	private Sound gunfire;
	private Projectile p;
	private boolean taken1, taken2, taken3;
	
	@Override
	public void init() 
	{
		super.init();
		setStageMusic(PATH + "/song.ogg", 0.0f, .7f);
	}
	
	@Override
	public void build()
	{
		super.build();
		taken1 = taken2 = taken3 = false;
		
		/*
		 * Lava
		 */
		
		addLava(73, 44, 4, 2);
		addLava(69, 44, 1, 2);
		addLava(72, 37, 1, 4);
		addLava(73, 40, 4, 1);
		
		addLava(69, 30, 1, 5);
		addLava(58, 30, 1, 6);
		addLava(54, 26, 3, 1);
		addLava(54, 39, 3, 1);
		addLava(54, 27, 1, 12);
		
		addLava(27, 52, 40, 2);

		addLava(32, 29, 2, 1);
		addLava(37, 29, 3, 1);
		addLava(43, 29, 2, 1);

		addLava(36, 10, 1, 17);

		addLava(49, 15.5f, 4, 2);
		addLava(49, 10, 4, 1.5f);

		addLava(57, 10, 2, 1);
		addLava(57, 16, 2, 1);

		addLava(61, 10, 1, 7);
		
		/*
		 * Weak
		 */
		addWeak(63, 45);
		addWeak(64, 45);
		addWeak(65, 45);
		addWeak(66, 45);
		addWeak(64, 50);
		addWeak(60, 50);
		addWeak(56, 46);
		addWeak(56, 47);
		addWeak(56, 48);
		addWeak(56, 49);
		addWeak(56, 50);
		addWeak(56, 51);
		addWeak(52, 49);
		addWeak(45, 51);
		addWeak(34, 50);
		addWeak(40, 49);
		addWeak(28, 50);
		addWeak(27, 50);
		
		/*
		 * Turrets
		 */
		p = new Projectile(0, 0, gm);
		p.setImage(2, lethal);
		p.width = p.height = 20;
		p.setMoveSpeed(7);
		
		add(getTurret(73,29, Direction.W));
		add(getTurret(27,32, Direction.S));
		add(getTurret(29,32, Direction.S),125);
		add(getTurret(7,20, Direction.S));
		add(getTurret(20,21, Direction.W));
		add(getTurret(19,34, Direction.N));
		add(getTurret(6,33, Direction.E));
		add(getTurret(37,9, Direction.S));
		add(getTurret(38,9, Direction.S));
		add(getTurret(39,9, Direction.S));
		add(getTurret(40,9, Direction.S));
		add(getTurret(41,9, Direction.S));
		add(getTurret(42,9, Direction.S));
		add(getTurret(43,9, Direction.S));
		
		/*
		 * Door & Keys
		 */
		GameObject key1 = new GameObject();
		key1.setImage(key);
		key1.moveTo(7 * 30, 27 * 30);
		key1.addEvent(()->{
			if(key1.collidesWith(gm))
			{
				discard(key1);
				taken1 = true;
			}
		});
		
		GameObject key2 = new GameObject();
		key2.setImage(key);
		key2.moveTo(19 * 30, 27 * 30);
		key2.addEvent(()->{
			if(key2.collidesWith(gm))
			{
				discard(key2);
				taken2 = true;
			}
		});
		
		GameObject key3 = new GameObject();
		key3.setImage(key);
		key3.moveTo(13 * 30, 21 * 30);
		key3.addEvent(()->{
			if(key3.collidesWith(gm))
			{
				discard(key3);
				taken3 = true;
			}
		});
		
		GameObject theDoor = new GameObject();
		theDoor.moveTo(20 * 30, 27 * 30);
		theDoor.setImage(door);
		gm.avoidOverlapping(theDoor);
		theDoor.addEvent(()->{
			if(taken1 && taken2 && taken3)
			{
				gm.allowOverlapping(theDoor);
				discard(theDoor);
			}
		});
		
		add(key1,key2,key3,theDoor);
		
		/*
		 * Goal
		 */
		GameObject g = new GameObject();
		g.moveTo(59 * 30, 13 * 30);
		g.setImage(goal);
		g.addEvent(()->{
			if(g.collidesWith(gm))
			{
				discard(g);
				gm.setState(CharacterState.FINISH);
			}
		});
		add(g);
		
		/*
		 * Finalizing
		 */
		gm.setHitEvent((hitter)->{
			if(hitter.sameAs(p))
				gm.hit(-1);
		});
	}
	
	SimpleWeapon getTurret(int x, int y, Direction dir)
	{
		SimpleWeapon sw = new SimpleWeapon(x * 30, y * 30, p, dir, 250);
		sw.setImage(5, turret);
		sw.setFiringSound(gunfire);
		sw.getSoundBank().useFallOff(true);
		sw.zIndex(101);
		
		float 	spawnX = (dir == Direction.S || dir == Direction.N) ? 5 : 0,
				spawnY = (dir == Direction.E || dir == Direction.W) ? 5 : 0;
		
		sw.spawnOffset(spawnX, spawnY);
		gm.avoidOverlapping(sw);
		
		return sw;
	}
	
	void addLava(float x, float y, float width, float height)
	{
		GameObject lava = new GameObject();
		lava.moveTo(x * 30.0f, y * 30.0f);
		lava.setImage(2, lethal);
		lava.width = width * 30.0f;
		lava.height = height * 30.0f;
		lava.addEvent(Factory.hitMain(lava, gm, -1));
		
		add(lava);
	}
	
	void addWeak(int x, int y)
	{
		GameObject w = new GameObject();
		w.moveTo(x * 30, y * 30);
		w.setImage(weak[0]);
		w.addEvent(Factory.weakPlatform(w, new Animation<>(2, weak), 80, null, gm));
		add(w);
	}
}
