package stages.diamondcave;

import game.core.Engine;
import game.core.Engine.Direction;
import game.core.Engine.GameState;
import game.core.GameObject;
import game.core.GameObject.Hitbox;
import game.core.MainCharacter.CharacterState;
import game.core.MovableObject.TileEvent;
import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.AutoLoad;
import game.development.StageBuilder;
import game.essentials.Factory;
import game.essentials.Frequency;
import game.essentials.GFX;
import game.essentials.Image2D;
import game.essentials.Pair;
import game.movable.Circle;
import game.movable.PathDrone;
import game.movable.PathDrone.PathData;
import game.movable.Projectile;
import game.movable.SimpleWeapon;
import game.movable.SolidPlatform;
import game.objects.OneWay;
import game.objects.Particle;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import ui.accessories.Playable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

@AutoDispose
@AutoInstall(mainPath="res/general", path=DiamondCave.PATH)
@Playable(name="Diamond Cave", description="Stage: Diamond Cave\nAuthor: Pojahn Moradi\nDifficulty: 5\nAverage time: 140 sec\nProfessional time: 90 sec\nObjective: Grab the gem.")
public class DiamondCave extends StageBuilder
{
	final static String PATH = "res/diamondcave";
	
	@AutoLoad(path=PATH, type=VisualType.IMAGE)
	private Image2D spikes, spikes2, bear[], bearAttack[], sharp, weak, blank, ow, cannon, iceproj, iceexp[], bug1[], bug2[], bug3[], bug4[], gunfire[], movingp, bacteria[], crystal[], bling[];
	@AutoLoad(path=PATH, type=VisualType.SOUND)
	private Sound roar, cannonfire, zapp, bum;
	@AutoLoad(path=PATH, type=VisualType.MUSIC)
	private Music frying;
	private Image2D weakDie[], background, foreground;
	private Particle blingbling;
	private PathData[] data1, data2, data3;
	private int counter = 0;
	
	@Override
	public void init()
	{
		super.init();
		
		background = new Image2D(PATH + "/backgroundImg.png");
		foreground = new Image2D(PATH + "/foregroundImg.png");
		
		weakDie = new Image2D[10];
		for(int i = 0; i < weakDie.length; i++)
		{
			if(i % 2 == 0)
				weakDie[i] = weak;
			else
				weakDie[i] = blank;
		}
		
		setStageMusic(PATH + "/song.ogg", 5.85, .5f);
	}
	
	@Override
	public void build() 
	{
		/*
		 * Basic Stuff
		 */
		super.build();
		lethalDamage = -3;
		game.timeColor = Color.WHITE;
		
		background(RenderOption.PORTION, background);
		foreground(RenderOption.PORTION, foreground);
		
		/*
		 * Main Character
		 */
		gm.addTileEvent(Factory.slipperWalls(gm));
		gm.hit(2);
		gm.addTileEvent(new TileEvent()
		{
			int recovery;
			boolean drawBolt;
			GameObject renderer;
			
			{
				renderer = new GameObject()
				{
					@Override
					public void drawSpecial(SpriteBatch batch) 
					{
						if(drawBolt && game.getGlobalState() == GameState.ONGOING)
						{
							gm.hit(-1);
							GFX.drawLightning(batch, 3717, -50, gm.centerX(), gm.centerY(), 150, .5f, 2, 0, 2, true, Color.WHITE, Color.YELLOW);
							frying.play(true);
						}
						else if(frying.playing())
							frying.stop();
					}
				};
				add(renderer);
			}
			
			@Override
			public void eventHandling(byte tileType) 
			{
				recovery--;
				
				if(tileType == Engine.AREA_TRIGGER_2)
				{
					if(recovery < 0)
					{
						add(Factory.printText("Warning, restricted area!", Color.WHITE, null, 60, gm, -150, -50, null));
						recovery = 60;
					}
				}
				else if(tileType == Engine.AREA_TRIGGER_3)
					drawBolt = true;
			}
		});
				
		/*
		 * Early Spikes
		 */
//		GameObject sp = new GameObject();
//		sp.moveTo(1403 + 8, 976);
//		sp.setImage(spikes);
//		sp.setHitbox(Hitbox.EXACT);
//		sp.addEvent(Factory.hitMain(sp, gm, -1));
//		sp.alpha = .7f;
//		
//		GameObject sp2 = new GameObject();
//		sp2.moveTo(1403, 976);
//		sp2.setImage(spikes2);
//		sp2.setHitbox(Hitbox.EXACT);
//		sp2.addEvent(Factory.hitMain(sp2, gm, -1));
//		sp2.alpha = .7f;
//		add(sp, sp2);
		
		/*
		 * Bear
		 */
		Bear b = new Bear(1544, 227, gm);
		b.setImage(7, bear);
		b.setAttackImage(new Frequency<>(6, bearAttack));
		b.zIndex(200);
		b.setHitbox(Hitbox.EXACT);
		b.setAttackSound(roar);
		
		add(b);
		
		/*
		 * Bouncing Random Enemies
		 */
		if(!game.playingReplay())
		{
			data1 = randomWallPoints();
			data2 = randomWallPoints();
			data3 = randomWallPoints();
		}
		
		PathDrone ram1 = new PathDrone(2303, 297);
		ram1.setImage(sharp);
		ram1.setMoveSpeed(1.6f);
		ram1.setRock(true, true);
		ram1.setHitbox(Hitbox.CIRCLE);
		PathDrone ram2 = ram1.getClone(ram1.currX + ram1.width + 100, ram1.currY);
		PathDrone ram3 = ram1.getClone(ram1.currX, ram1.currY + ram1.height + 100);
		final int rotationSpeed = 15;
		
		ram1.appendPath(data1);
		ram1.addEvent(Factory.hitMain(ram1, gm, -1));
		ram1.avoidOverlapping(ram2, ram3);
		ram1.addEvent(()->{ram1.rotation += rotationSpeed;});
		
		ram2.appendPath(data2);
		ram2.addEvent(Factory.hitMain(ram2, gm, -1));
		ram2.avoidOverlapping(ram1, ram3);
		ram2.addEvent(()->{ram2.rotation += rotationSpeed;});
		
		ram3.appendPath(data3);
		ram3.addEvent(Factory.hitMain(ram3, gm, -1));
		ram3.avoidOverlapping(ram1, ram2);
		ram3.addEvent(()->{ram3.rotation += rotationSpeed;});
		
		add(ram1,ram2,ram3);
		
		/*
		 * Weak Platforms
		 */
		for(int i = 0, x = 2303, y = 1026; i < 10; i++, x += weak.getWidth())
		{
			GameObject w = new GameObject();
			w.moveTo(x, y);
			w.setImage(weak);
			w.addEvent(Factory.weakPlatform(w, new Frequency<>(10, weakDie), 120, null, gm));
			add(w);
		}
		
		/*
		 * One-Way Platforms
		 */
		OneWay oneway = new OneWay(2684, 992, Direction.N, gm);
		oneway.setImage(ow);
		
		add(oneway, oneway.getClone(2857, 922), oneway.getClone(2677, 832), oneway.getClone(2777, 742), oneway.getClone(2827, 652), oneway.getClone(2667, 582), oneway.getClone(2897, 552));
		
		/*
		 * Cannons
		 */
		Particle explosion = new Particle();
		explosion.setImage(5, iceexp);
		explosion.zIndex(200);
		explosion.offsetY = 15;
		explosion.setIntroSound(bum);
		explosion.getSoundBank().useFallOff(true);
		explosion.getSoundBank().maxVolume = .7f;
		explosion.getSoundBank().power = 15;
		
		Particle fireAnim = new Particle();
		fireAnim.setImage(2, gunfire);
		fireAnim.zIndex(200);
		fireAnim.offsetX = -fireAnim.halfWidth();
		fireAnim.offsetY = -13;
		
		Projectile proj = new Projectile(0,0,gm);
		proj.setImage(iceproj);
		proj.scanningAllowed(false);
		proj.setMoveSpeed(2.5f);
		proj.useFastCollisionCheck(true);
		proj.setHitbox(Hitbox.CIRCLE);
		proj.setImpact(explosion);
		
		for(int i = 0, reloadTime = 250, x = 3032, y = 480; i < 9; i++, y += cannon.getHeight() + 15)
		{
			SimpleWeapon w = new SimpleWeapon(x, y, proj, Direction.W, reloadTime);
			w.spawnOffset(0, 1);
			w.setImage(cannon);
			w.zIndex(10);
			w.setFiringSound(cannonfire);
			w.setFiringAnimation(fireAnim);
			w.getSoundBank().useFallOff(true);
			w.setCloneEvent((cloned)->{
				cloned.addEvent(()->{cloned.rotation += 10;});
			});
			gm.avoidOverlapping(w);
			
			if(i % 2 == 0)
				add(w);
			else
				add(w, reloadTime / 2);
		}
		
		/*
		 * Insects
		 */
		PathDrone ins1 = new PathDrone(3725, 508);
		ins1.setImage(6, bug1);
		ins1.setDoubleFaced(true, true);
		ins1.setMoveSpeed(1);
		ins1.setHitbox(Hitbox.EXACT);
		ins1.addEvent(Factory.hitMain(ins1, gm, -1));
		ins1.appendPath(3725, 508);
		ins1.appendPath(3265, 508);
		
		PathDrone ins2 = ins1.getClone(3725, 447);
		ins2.setImage(6, bug2);
		ins2.clearData();
		ins2.addEvent(Factory.hitMain(ins2, gm, -1));
		ins2.appendPath(3725, 447);
		ins2.appendPath(3265, 447);
		
		PathDrone ins3 = ins1.getClone(3819, 556);
		ins3.setImage(6, bug3);
		ins3.setDoubleFaced(false, false);
		ins3.clearData();
		ins3.appendPath(3819, 556);
		ins3.appendPath(3819, 919);
		ins3.addEvent(Factory.hitMain(ins3, gm, -1));
		ins3.addEvent(()->{ins3.flipY = ins3.facing == Direction.N;});
		
		PathDrone ins4 = ins3.getClone(3767, 919);
		ins4.setImage(6, bug4);
		ins4.clearData();
		ins4.appendPath(3767, 919);
		ins4.appendPath(3767, 556);
		ins4.addEvent(Factory.hitMain(ins4, gm, -1));
		ins4.addEvent(()->{ins4.flipY = ins4.facing == Direction.N;});
		
		PathDrone ins5 = ins4.getClone(4017, 919);
		ins5.clearData();
		ins5.appendPath(4017, 919);
		ins5.appendPath(4017, 759);
		ins5.addEvent(Factory.hitMain(ins5, gm, -1));
		ins5.addEvent(()->{ins5.flipY = ins5.facing == Direction.N;});
		
		PathDrone ins6 = ins3.getClone(4119, 759);
		ins6.clearData();
		ins6.appendPath(4119, 759);
		ins6.appendPath(4119, 919);
		ins6.addEvent(Factory.hitMain(ins6, gm, -1));
		ins6.addEvent(()->{ins6.flipY = ins6.facing == Direction.N;});
		
		PathDrone ins7 = ins4.getClone(4017, 716);
		ins7.clearData();
		ins7.appendPath(4017, 716);
		ins7.appendPath(4017, 556);
		ins7.addEvent(Factory.hitMain(ins7, gm, -1));
		ins7.addEvent(()->{ins7.flipY = ins7.facing == Direction.N;});
		
		PathDrone ins8 = ins3.getClone(4119, 556);
		ins8.clearData();
		ins8.appendPath(4119, 556);
		ins8.appendPath(4119, 716);
		ins8.addEvent(Factory.hitMain(ins8, gm, -1));
		ins8.addEvent(()->{ins8.flipY = ins8.facing == Direction.N;});
		
		add(ins1, ins2, ins3, ins4, ins5, ins6, ins7, ins8);
		
		/*
		 * Solid Platform
		 */
		SolidPlatform solp = new SolidPlatform(4973, 807, gm);
		solp.setImage(movingp);
		solp.setMoveSpeed(1);
		solp.freeze();
		solp.appendPath(5839, 807);
		solp.appendPath(5839, 159);
		solp.appendPath(4849, 159,0,false,()->{solp.freeze();});
		add(solp);
		
		/*
		 * Bacterias
		 */
		Circle c = new Circle(5279, 152, 50, 0);
		c.setMoveSpeed(.04f);
		
		PathDrone backt = getBacteria(0, 0);
		backt.clearData();
		backt.addEvent(Factory.follow(c, backt, 0, 0));
		
		
		add(/* Bottom line */getBacteria(5249,782), getBacteria(5489, 782), getBacteria(5519,782), getBacteria(5619, 782), getBacteria(5689,782),
			/* Vertical line */ getBacteria(5859 + 12, 642), getBacteria(5829 - 12, 642), getBacteria(5829, 442), getBacteria(5829, 412), getBacteria(5829, 382),
			getBacteria(5859, 312), getBacteria(5859, 282), getBacteria(5859, 252),
			/* Top Line */ getBacteria(new PathData(5589, 182), new PathData(5589, 122)), getBacteria(new PathData(5559, 122), new PathData(5559, 182)),
			getBacteria(4929, 152), getBacteria(4929, 182), getBacteria(4929, 32), getBacteria(4929, 62), backt, c);
		
		/*
		 * Goal
		 */
		GameObject goal = new GameObject();
		goal.setImage(6, crystal);
		goal.moveTo(4861, 131);
		goal.addEvent(()->{
			if(goal.collidesWith(gm))
			{
				discard(goal);
				gm.setState(CharacterState.FINISH);
			}
		});
		add(goal);
		
		/*
		 * Finalizing
		 */
		gm.setHitEvent((hitter)->
		{
			if(hitter.sameAs(b) || hitter.sameAs(proj))
				gm.hit(-1);
		});
		
		gm.addTileEvent((tileType)->
		{
			if(tileType == Engine.AREA_TRIGGER_4)
				solp.unfreeze();
		});
		
		blingbling = new Particle();
		blingbling.setImage(9, bling);
		blingbling.zIndex(500);
	}
	
	@Override
	public void extra() 
	{
		if(Gdx.input.isKeyPressed(Keys.A))
			gm.moveTo(game.tx, game.ty);
		
		if(++counter % 10 == 0)
			add(blingbling.getClone(MathUtils.random(game.tx - game.getScreenWidth() / 2, game.tx + game.getScreenWidth() / 2), MathUtils.random(game.ty - game.getScreenHeight() / 2, game.ty + game.getScreenHeight() / 2)));
	}
	
	PathDrone getBacteria(float x, float y)
	{
		return getBacteria(new PathData(x, y, 0, false, null));
	}
	
	PathDrone getBacteria(PathData... waypoints)
	{
		Pair<Integer, Integer> value = new Pair<>();
		value.obj1 = 0;
		
		PathDrone b = new PathDrone(waypoints[0].targetX, waypoints[0].targetY);
		b.setImage(ThreadLocalRandom.current().nextInt(3,8), bacteria);
		b.setMoveSpeed(1.2f);
		b.setHitbox(Hitbox.EXACT);
		b.addEvent(Factory.hitMain(b, gm, -1));
		b.appendPath(waypoints);
		b.addEvent(()->
		{
			if(--value.obj1 < 0 && b.collidesWith(gm))
			{
				value.obj1 = 60;
				zapp.play();
			}
		});
		
		return b;
	}
	
	@Override
	protected Serializable getMeta() 
	{
		ArrayList<PathData[]> waypoints = new ArrayList<>();
		waypoints.add(data1);
		waypoints.add(data2);
		waypoints.add(data3);
		
		return waypoints;
	}
	
	@Override
	public void setMeta(Serializable meta) 
	{
		@SuppressWarnings("unchecked")
		ArrayList<PathData[]> waypoints = (ArrayList<PathData[]>) meta;
		data1 = waypoints.get(0);
		data2 = waypoints.get(1);
		data3 = waypoints.get(2);
	}
	
	PathData[] randomWallPoints()
	{
		int last = -1;
		int quantity = new Random().nextInt(100) + 100;
		List<PathData> pdlist = new ArrayList<>(quantity);
		Random r = new Random();
		
		for(int i = 0; i < quantity; i++)
		{
			int dir = r.nextInt(4);
			if(dir != last)
			{
				last = dir;
				
				Point2D.Float point = getDirection(dir);
				pdlist.add(new PathData(point.x, point.y, 0, false, null));
			}
			else
				i--;
		}
		
		return pdlist.toArray(new PathData[pdlist.size()]);
	}
	
	Point2D.Float getDirection(int dir)
	{
		Point2D.Float point = new Point2D.Float();
		final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
		final int minX = 2303;
		final int maxX = 2553 - 32;
		final int minY = 297;
		final int maxY = 667;
		
		Random r = new Random();
		
		switch(dir)
		{
		case UP:
			point.x = r.nextInt(maxX - minX) + minX;
			point.y = minY;
			break;
		case DOWN:
			point.x = r.nextInt(maxX - minX) + minX;
			point.y = maxY;			
			break;
		case LEFT:
			point.x = minX;
			point.y = r.nextInt(maxY - minY) + minY;
			break;
		case RIGHT:
			point.x = maxX;
			point.y = r.nextInt(maxY - minY) + minY;
			break;
		}
		
		return point;
	}
}
