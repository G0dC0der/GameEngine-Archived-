package stages.pihell;

import game.core.GameObject;
import game.core.GameObject.Hitbox;
import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.AutoLoad;
import game.development.StageBuilder;
import game.essentials.Factory;
import game.essentials.Image2D;
import game.movable.PathDrone;
import game.movable.Projectile;
import game.movable.Weapon;
import java.io.File;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;

@AutoDispose
@Playable(name="Piranha Hell", description="Stage: Piranha Hell\nAuthor: Pojahn Moradi\nDifficulty: 7\nAverage time: - sec\nProfessional time: - sec\nObjective: Enter goal.")
@AutoInstall(mainPath="res/general", path="res/pihell")
public class PiranhaHell extends StageBuilder
{
	
	@AutoLoad(path="res/pihell",type=VisualType.IMAGE)
	private Image2D pih1[], pih2[], dullplant[], body, head, fireball;

	@AutoLoad(path="res/mtrace",type=VisualType.IMAGE)
	private Image2D flag[];
	
	private Sound fireSound;
	
	@Override
	public void init() 
	{
		super.init();
		
		fireSound = TinySound.loadSound(new File("res/pihell/fireball.wav"));
		fireSound.setVolume(.5f);
		setStageMusic("res/pihell/song.ogg", 4.18f, 1.0f);
	}
	
	@Override
	public void build() 
	{
		super.build();
		
		gm.addTileEvent(Factory.slipperWalls(gm));
		gm.hit(2);
		
		//Goal
		GameObject theFlag = new GameObject();
		theFlag.moveTo(1612, 785);
		theFlag.setImage(4, flag);
		add(theFlag);
		
		//Beginning, bottom
		addReddie(1, 30);
		addReddie(2, 30);
		addReddie(3, 30);
		addReddie(6, 30);
		addReddie(7, 30);
		addReddie(8, 30);
		addReddie(9, 30);
		addReddie(10, 30);
		addReddie(12, 30);
		addReddie(13, 30);
		addReddie(14, 30);
		addReddie(16, 30);
		addReddie(17, 30);
		addReddie(18, 30);
		addReddie(19, 30);
		addReddie(21, 30);
		addReddie(22, 30);
		addReddie(23, 30);
		addReddie(24, 30);
		addReddie(25, 30);
		addReddie(27, 30);
		addReddie(28, 30);
		addReddie(29, 30);
		addReddie(30, 30);
		addReddie(31, 30);
		addReddie(32, 30);

		addReddie(33, 27);
		addReddie(34, 27);
		addReddie(30, 23);
		
		//Stairs
		addReddie(24, 11);
		addReddie(26, 13);
		addReddie(28, 15);
		addReddie(30, 17);
		
		//Hindrance room
		addWhitey(21, 6, false);
		addWhitey(17, 6, true);
		addReddie(15, 10);
		addReddie(14, 10);
		addReddie(9, 10);
		
		//Roof
		addReddie(3, 2);
		addReddie(4, 2);
		addReddie(5, 2);
		addReddie(7, 2);
		addReddie(8, 2);
		addReddie(9, 2);
		addReddie(11, 2);
		addReddie(12, 2);
		addReddie(13, 2);
		addReddie(15, 2);
		addReddie(16, 2);
		
		//Going Down
		addReddie(41, 6);
		addReddie(42, 6);
		addReddie(43, 6);
		addReddie(44, 6);

		addReddie(45, 10);
		addReddie(46, 10);
		addReddie(47, 10);

		addReddie(48, 13);
		addReddie(49, 13);
		addReddie(50, 13);
		addReddie(51, 13);

		addReddie(52, 16);
		addReddie(53, 16);
		addReddie(54, 16);
		addReddie(55, 16);
		addReddie(56, 16);
		addReddie(57, 16);
		addReddie(58, 16);
		addReddie(59, 16);
		addReddie(63, 16);
		addReddie(64, 16);
		addReddie(67, 16);
		addReddie(68, 16);
		addReddie(69, 16);
		addReddie(72, 16);
		addReddie(73, 16);
		addReddie(74, 16);
		
		//Whitey pipes
		addWhitey(77, 8, false);
		addWhitey(78, 8, false);
		addWhitey(79, 7, true);
		addWhitey(80, 7, true);
		addWhitey(81, 8, false);
		addWhitey(82, 8, false);
		addWhitey(83, 7, true);
		addWhitey(84, 7, true);
		addWhitey(85, 8, false);
		addWhitey(86, 8, false);
		addWhitey(87, 7, true);
		addWhitey(88, 7, true);
		addWhitey(89, 8, false);
		addWhitey(90, 8, false);
		
		//Later platforms
		addReddie(92, 20);
		addReddie(94, 23);
		addReddie(91, 26);
		
		//Bottom end
		for(int x = 52; x <= 99; x++)
		{
			if(x != 88)
				addReddie(x, 30);
		}
		
		//End, whites
		addWhitey(88, 28, false);
		addWhitey(83, 26, true);
		addWhitey(77, 24, false);
		addWhitey(72, 27, true);
		addWhitey(67, 26, false);

		addWhitey(62, 23, true);
		addWhitey(61, 23, true);

		addWhitey(55, 25, false);
		
		//Bigger plants(dull)
		PathDrone dull = new PathDrone(2096, 384);
		dull.setImage(8, dullplant);
		dull.setMoveSpeed(2);
		dull.appendPath(dull.loc.x, dull.loc.y, 100, false, null);
		dull.appendPath(dull.loc.x, dull.loc.y + dull.height + 10, 100, false, null);
		dull.addEvent(Factory.hitMain(dull, gm, -1));
		dull.setHitbox(Hitbox.EXACT);
		add(dull);
		
		//Firing plant
		PathDrone plantBody = new PathDrone(1840, 384);
		plantBody.setImage(body);
		plantBody.setMoveSpeed(1.5f);
		plantBody.appendPath(1840, 384, 100, false, null);
		plantBody.appendPath(1840, 384 - plantBody.height - 40, 100, false, null);
		add(plantBody);
		
		Projectile fireBall = new Projectile(0, 0, gm);
		fireBall.setImage(fireball);
		fireBall.useFastCollisionCheck(true);
		fireBall.addEvent(()->{fireBall.rotation += 5;});
		fireBall.setMoveSpeed(3);
		fireBall.setFiringSound(fireSound);
		
		Weapon plantHead = new Weapon(1840, 384, 1, 1, 90, gm);
		plantHead.setProjectile(fireBall);
		plantHead.setRotateWhileRecover(true);
		plantHead.setRotationSpeed(.1f);
		plantHead.addEvent(Factory.follow(plantBody, plantHead, 0, plantBody.height));
		plantHead.setImage(head);
		plantHead.setFrontFire(true);
		plantHead.zIndex(99);
		add(plantHead);
		
		gm.setHitEvent((subject)->
		{
			if(subject.sameAs(fireBall))
			{
				gm.hit(-1);
			}
		});
	}
	
	void addReddie(int x, int y)
	{
		GameObject redPlant = new GameObject();
		redPlant.moveTo(x * 32 + 9, y * 32 + 16);
		redPlant.setImage(6, pih1);
		redPlant.addEvent(Factory.hitMain(redPlant, gm, -1));
		
		add(redPlant);
	}
	
	void addWhitey(int x, int y, boolean startHidden)
	{
		PathDrone whitePlant = new PathDrone(0,0);
		whitePlant.setImage(7, pih2);
		whitePlant.addEvent(Factory.hitMain(whitePlant, gm, -1));
		whitePlant.setMoveSpeed(.7f);
		
		if(!startHidden)
		{
			int myX = x * 32 + 9;
			int myY = y * 32 + 16;
			
			whitePlant.moveTo(myX, myY);
			whitePlant.appendPath(myX, myY, 70, false, null);
			whitePlant.appendPath(myX, myY + 20, 70, false, null);
		}
		else
		{
			int myX = x * 32 + 9;
			int myY = y * 32 + 16 + 20;
			
			whitePlant.moveTo(myX, myY);
			whitePlant.appendPath(myX, myY, 70, false, null);
			whitePlant.appendPath(myX, myY - 20, 70, false, null);
		}
		
		add(whitePlant);
	}
}
