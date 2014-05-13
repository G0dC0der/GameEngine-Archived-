package stages.bridge;

import game.core.Engine.Direction;
import game.core.GameObject;
import game.core.GameObject.Event;
import game.core.GameObject.Hitbox;
import game.core.MainCharacter.CharacterState;
import game.core.MovableObject;
import game.development.AutoInstall;
import game.development.StageBuilder;
import game.essentials.Factory;
import game.essentials.Frequency;
import game.essentials.Image2D;
import game.movable.Boo;
import java.io.File;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;

@AutoInstall(mainPath="res/general", path="res/ghostbridge")
@Playable(name="Ghost Bridge",description="Stage: Ghost Bridge\nAuthor: Pojahn Moradi\nAverage time: 70 sec\nProfessional time: 60 sec\nObjective: Collect the diamonds to open the goal gate.")
public class GhostBridge extends StageBuilder
{	
	private Image2D lavaImg[], weakImg, booaImg[], boohImg[], diamondImg[], coinImg[], gateImg;
	private Sound removeBlock, boo, collect, collect2;
	private GameObject rightDown, rightMiddle, rightUp, leftDown, leftMiddle, leftUp;
	private MovableObject gate;
	private Boo ghost;
	
	{
		setDifficulty(Difficulty.NORMAL);
	}
	
	public void init() 
	{
		try
		{
			super.init();
			
			diamondImg	= Image2D.loadImages(new File("res/clubber/diamond"),true);
			coinImg		= Image2D.loadImages(new File("res/general/starcoin"),true);
			lavaImg 	= Image2D.loadImages(new File("res/ghostbridge/lava"),true);
			booaImg 	= Image2D.loadImages(new File("res/ghostbridge/boo"),true);
			boohImg 	= Image2D.loadImages(new File("res/ghostbridge/boo/hide"),true);
			gateImg 	= new Image2D("res/ghostbridge/gate.png",false);
			weakImg 	= new Image2D("res/ghostbridge/block.png",false);
			
			removeBlock = TinySound.loadSound(new File(("res/ghostbridge/blockremove.wav")));
			boo			= TinySound.loadSound(new File(("res/ghostbridge/boo.wav")));
			collect		= TinySound.loadSound(new File(("res/general/collect1.wav")));
			collect2	= TinySound.loadSound(new File(("res/ghostbridge/collect.wav")));
			
			removeBlock.setVolume(0.4f);
			collect.setVolume(0.4f);
			boo.setVolume(0.1f);
			
			setStageMusic("res/ghostbridge/song.ogg", 24.19);
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
		gm.hit(getHP());
		gm.facing = Direction.W;
		Difficulty d = getDifficulty();
		
		/*
		 * Lava
		 */
		final GameObject lava = new GameObject();
		lava.setImage(6, lavaImg);
		lava.setHitbox(Hitbox.EXACT);
		lava.currX = 25;
		lava.currY = 762;
		lava.zIndex(100);
		lava.addEvent(()-> { if(lava.collidesWith(gm)) gm.setState(CharacterState.DEAD); });
		add(lava);
		
		/*
		 * One way blocks
		 */
		int posX = 123,
			posY = 333;
		for(int i = 0; i < 20; i++, posX += 35)
		{
			GameObject weakplatform = new GameObject();
			weakplatform.currX = posX;
			weakplatform.currY = posY;
			weakplatform.setImage(weakImg);
			weakplatform.addEvent(Factory.weakPlatform(weakplatform, null, getCollapseTime(), removeBlock, gm));
			add(weakplatform);
		}
		
		/*
		 * Ghosts
		 */
		if(d != Difficulty.EASY)
		{
			ghost = new Boo(100,100,gm);
			ghost.setImage(booaImg);
			Frequency<Image2D> hideImg = new Frequency<>(1, boohImg);
			hideImg.setMultiFaced(true);
			ghost.setHideImage(hideImg);
			ghost.setMultiFaced(true);
			ghost.declineNonVert(true);
			ghost.setHitbox(Hitbox.EXACT);
			ghost.facing = Direction.E;
			ghost.setDetectSound(boo);
			if(d == Difficulty.NORMAL)
				ghost.maxSpeed = 1.5f;
			
			add(ghost, ghost.getClone(width - 100, 100));
		}
		
		/*
		 * Gate
		 */
		gate = new MovableObject();
		gate.moveTo(455, 761);
		gate.setImage(gateImg);
		gm.avoidOverlapping(gate);
		add(gate);
		
		/*
		 * Collecting objects
		 */
		rightDown = new GameObject();
		rightDown.setImage(4, diamondImg);
		rightDown.currX = 877;
		rightDown.currY = 218;
		
		leftDown = rightDown.getClone(27, 218);		
		rightMiddle = rightDown.getClone(877, 188);		
		leftMiddle = rightDown.getClone(27, 188);
		rightUp = rightDown.getClone(877, 158);
		leftUp = rightDown.getClone(27, 158);
		
		rightDown.addEvent(getEvent(rightDown, leftDown, false));
		leftDown.addEvent(getEvent(leftDown, rightMiddle, false));
		rightMiddle.addEvent(getEvent(rightMiddle, leftMiddle, false));
		
		if(d == Difficulty.EASY)
			leftMiddle.addEvent(getEvent(leftMiddle, rightUp, true));
		else
		{
			leftMiddle.addEvent(getEvent(leftMiddle, rightUp, false));
			
			if(d == Difficulty.NORMAL)
				rightUp.addEvent(getEvent(rightUp, leftUp, true));
			else
			{
				rightUp.addEvent(getEvent(rightUp, leftUp, false));
				leftUp.addEvent(getEvent(leftUp, null, true));
				
			}
		}
		
		add(rightDown);
		
		/*
		 * Goal
		 */
		GameObject coin = new GameObject();
		coin.setImage(new Frequency<>(7, coinImg));
		coin.currX = 458;
		coin.currY = 799;
		add(coin);
		
		/*
		 * Finalizing
		 */
		gm.setHitEvent((hitter)->
		{	
			if(ghost != null && hitter.sameAs(ghost))
				gm.hit(-1);
			
			if(gm.getHP() <= 0)
			{
				gm.setVisible(false);
				gm.setState(CharacterState.DEAD);
			}
				
		});
	}
	
	Event getEvent(final GameObject src, final GameObject spawn,final boolean last)
	{
		return ()->
		{	
			if(src.collidesWith(gm))
			{
				if(last)
				{
					gm.allowOverlapping(gate);
					discard(gate);
					collect2.play();
				}
				else
					collect.play();

				discard(src);
				if(spawn != null)
					add(spawn);
			}
		};
	}
	
	int getCollapseTime()
	{
		switch(getDifficulty())
		{
			case EASY:
				return 60;
			case HARD:
				return 15;
			case NORMAL:
			default:
				return 30;	
		}
	}
	
	int getHP()
	{
		switch(getDifficulty())
		{
			case EASY:
				return 1;
			case HARD:
				return 0;
			case NORMAL:
			default:
				return 1;	
		}	
	}

	@Override
	public void dispose() 
	{
		super.dispose();
		disposeBatch(lavaImg, weakImg, booaImg, boohImg, diamondImg, coinImg, gateImg, removeBlock, boo, collect, collect2);
	}
}