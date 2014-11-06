package game.core;

import game.core.Engine.Direction;
import game.core.Engine.GameState;
import game.core.GameObject.Event;
import game.core.MainCharacter.CharacterState;
import game.essentials.CameraEffect;
import game.essentials.Controller.PressedButtons;
import game.essentials.Image2D;
import java.awt.Dimension;
import java.io.File;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.Disposable;

/**
 * Create your own stage by extending this class and adding your unique appearance.
 * @author Pojahn
 */
public abstract class Stage 
{	
	public enum Difficulty
	{
		EASY("Easy"),
		NORMAL("Normal"),
		HARD("Hard");
		
		private String name;
		
		private Difficulty(String name)
		{
			this.name = name;
		}
		
		public String toString() 
		{
	       return name;
		}
	}
	
	/**
	 * This is the stage that currently being played. <br>
	 * This variable is set automatically when a stage has been launched.
	 */
	static Stage STAGE;
	
	/**
	 * The stage data, which stores all the tile information. The values stored in this matrix are constant found in {@code game.core.Engine}.
	 */
	public byte[][] stageData;

	/**
	 * This is the damage a {@code MainCharacter} takes when he or she interact with LETHAL tile type.
	 */
	public int lethalDamage = -1;
	
	/**
	 * The size of the stage.
	 */
	public Dimension size;
	
	/**
	 * A reference to the engine.
	 */
	public Engine game;
	
	/**
	 * {@code startX} and {@code startY} are the starting position of the main character.
	 */
	protected int startX, startY;
	
	/**
	 * The music that will play in the background.
	 */
	protected Music music;
	
	private Difficulty difficulty;
	private LinkedList<Object> discardList, appendList, trash;
	private byte[][] stageClone;
	private boolean pending;
	boolean sort;
	List<GameObject> stageObjects;
	List<MainCharacter> mains;
	List<Event> events;
	List<AbstractMap.SimpleEntry<Object, Integer>> delayedObject;
	List<CameraEffect> cameraEffects;
	
	public Stage()
	{
		discardList    = new LinkedList<>();
		appendList     = new LinkedList<>();
		stageObjects   = new LinkedList<>();
		mains          = new LinkedList<>();
		trash		   = new LinkedList<>();
		events 		   = new LinkedList<>();
		delayedObject  = new LinkedList<>();
		cameraEffects  = new LinkedList<>();
		startX = startY = -1;
		size = new Dimension();
	}
	
	/**
	 * Loads and starts the song found on the given path.
	 * @param path The path to where the song can be found.
	 * @param loopStart Where to start, in seconds, after the song have ended.
	 * @param volume The volume.
	 */
	public void setStageMusic (String path, double loopStart, float volume)
	{
		setStageMusic(TinySound.loadMusic(new File(path),true), loopStart, volume);
	}
	
	/**
	 * Starts the given song.
	 * @param music The song to start.
	 * @param loopStart Where to start, in seconds, after the song have ended.
	 * @param volume The volume.
	 */
	public void setStageMusic(Music music, double loopStart, float volume)
	{
		music.setVolume(volume);
		music.setLoopPositionBySeconds(loopStart);
		music.play(true);
		this.music = music;
	}
		
	/**
	 * Initialize the starting position, size and the visible size of the stage.<br>
	 * Should be called after {@code stageData} is initialized.
	 */
	public void basicInits()
	{
		size.width = stageData[0].length;
		size.height = stageData.length;
		
		if(startX == -1 || startY == -1)
			for (int i = 0; i < stageData.length; i++)
				for (int j = 0; j < stageData[0].length; j++)
					if (stageData[i][j] == Engine.START_POSITION)
					{
						startX = j;
						startY = i;
						return;
					}
	}
	
	/**
	 * Every triggerable unit added into this stage will passed to this function by the engine every frame.
	 * @param go The {@code MovableObject} that have moved.
	 * @param tileType A constant representing which tile type this {@code MovableObject} is currently "standing" on.
	 */
	void tileIntersection(MovableObject mo, byte tileType)
	{
		if (tileType != Engine.HOLLOW)
			mo.runTileEvents(tileType);
	}

	/**
	 * This method is called once every frame by the engine and add or removes entities and update all the existing ones.
	 */
	final void moveEnemies() 
	{
		int size = delayedObject.size();
		for(int i = 0; i < size; i++)
		{
			AbstractMap.SimpleEntry<?, Integer> pair = delayedObject.get(i);
			pair.setValue(pair.getValue() - 1);
			
			if(pair.getValue() < 0)
			{
				add(pair.getKey());
				delayedObject.remove(i);
				size--;
				i--;
			}
		}
			
		if(pending)
		{
			Object obj;
			if(!discardList.isEmpty())
				while ((obj = discardList.poll()) != null)
				{
					trash.add(obj);
					
					if(obj instanceof GameObject)
						stageObjects.remove(obj);
					else if(obj instanceof Event)
						events.remove(obj);
					else if(obj instanceof CameraEffect)
						cameraEffects.remove(obj);
				}
			
			if(!appendList.isEmpty())
				while ((obj = appendList.poll()) != null)
				{
					if (obj instanceof GameObject)
					{
						stageObjects.add((GameObject)obj);
						sort = true;
					}
					else if(obj instanceof Event)
						events.add((Event)obj);
					else if(obj instanceof CameraEffect)
						cameraEffects.add((CameraEffect)obj);
				}
			
			pending = false;
		}
		
		if(sort)
		{
			Collections.sort(stageObjects, GameObject.Z_INDEX_SORT);
			sort = false;
		}
		
		if(trash.size() > 200)
			trash.clear();
		
		mains = new LinkedList<>();
		
		for(GameObject go : stageObjects)
		{
			boolean isEnemy = go instanceof Enemy,
					isMain = go instanceof MainCharacter;
			
			if(isEnemy)
			{
				Enemy enemy = (Enemy) go;
				
				if(!enemy.halted)
				{
					enemy.moveEnemy();
					
					if(enemy.triggerable)
					{
						enemy.occupyingCells.clear();
						enemy.tileCheck();
						enemy.inspectIntersections();
					}
					
					enemy.removeQueuedEvents();
					enemy.runEvents();
					
					updateFacing(enemy);
					
					enemy.prevX = enemy.loc.x;
					enemy.prevY = enemy.loc.y;
				}
				else
					enemy.goBack();
			}
			else if(isMain)
				mains.add((MainCharacter)go);
			else
			{
				go.removeQueuedEvents();
				go.runEvents();
			}
		}
		
		int aliveMains = mains.size();
		for(int i = 0; i < mains.size(); i++)
		{
			MainCharacter main = mains.get(i);
			
			if(main.isGhost())
				aliveMains--;
			
			if(!main.halted)
			{
				updateFacing(main);
				main.prevX = main.loc.x;
				main.prevY = main.loc.y;
				
				if(main.isGhost())
					main.handleInput(main.getNext());
				else if(game.getGlobalState() != GameState.ONGOING || main.getState() != CharacterState.ALIVE)
					main.handleInput(MainCharacter.STILL);
				else
				{
					PressedButtons pbs;
					if(game.playingReplay())
						pbs = game.getReplayFrame(i);
					else
						pbs = Engine.getPressedButtons(main.con);
					
					if(!pbs.suicide)
						main.handleInput(pbs);
					else
					{
						main.setState(CharacterState.DEAD);
						main.deathAction();
					}
					
					if(!game.playingReplay())
						game.registerReplayFrame(i, pbs);
				}
				if(main.triggerable)
				{
					main.occupyingCells.clear();
					main.tileCheck();
					main.inspectIntersections();
				}
				
				main.removeQueuedEvents();
				main.runEvents();
				
				if(main.getState() == CharacterState.DEAD)
					main.deathAction();
			}
			else
				main.goBack();
			
			if(!main.isGhost() && main.getState() == CharacterState.DEAD)
				aliveMains--;
			else if(!main.isGhost() && main.getState() == CharacterState.FINISH)
				game.setGlobalState(GameState.COMPLETED);
		}   
		
		if(0 >= aliveMains)
			game.setGlobalState(GameState.ENDED);
		
		if(!events.isEmpty())
			for(Event event : events)
			{
				if(event.done())
					discard(event);
				else
					event.eventHandling();
			}
	}

	/**
	 * Adds the given objects to the game.<br>
	 * The object can be an instance of either {@code GameObject} or {@code Event}.
	 * @param objs The object to add.
	 */
	public void add(Object... objs)
	{
		pending = true;
		for(Object obj : objs)
			if(obj != null)
				appendList.add(obj);
	}
	
	/**
	 * Adds the specified object after the given amount of time elapsed.
	 * @param obj The object to be added.
	 * @param delay The time in frames.
	 */
	public void add(Object obj, int delay)
	{
		AbstractMap.SimpleEntry<Object, Integer> pair = new AbstractMap.SimpleEntry<>(obj, delay);
		delayedObject.add(pair);
	}
	
	/**
	 * Removes the specified objects from the game.<br>
	 * The object can be an instance of either {@code GameObject} or {@code Event}.
	 * @param objs The objects to remove.
	 */
	public void discard(Object... objs)
	{
		pending = true;
		for(Object obj : objs)
		{
			if(obj instanceof GameObject)
			{
				GameObject go = (GameObject) obj;
				go.dismiss();
			}
			if(obj != null)
				discardList.add(obj);
		}
	}
	
	/**
	 * An optional way of setting the background. Wraps the given image in a {@code GameObject} with {@code z-index} set to -100.
	 * @param img The image to use as background.
	 */
	public void background(Image2D img)
	{
		GameObject bg = new GameObject();
		bg.zIndex(-100);
		bg.setImage(img);
		add(bg);
	}
	
	/**
	 * An optional way of setting the foreground. Wraps the given image in a {@code GameObject} with {@code z-index} set to 100.
	 * @param img The image to use as foreground.
	 */
	public void foreground(Image2D img)
	{
		GameObject fg = new GameObject();
		fg.zIndex(100);
		fg.setImage(img);
		add(fg);
	}
	
	/**
	 * This function should be used as constructor when subclassing. Operations that only requires to be performed once(such as image and sound loading) should be done here.
	 */
	public abstract void init();
	
	/**
	 * Called upon start, death and restart. The stage is built from this method.
	 */
	public void build()
	{
		for(GameObject go : stageObjects)
			go.dismiss();
		
		trash.clear();
		stageObjects.clear();
		mains.clear();
		events.clear();
		appendList.clear();
		discardList.clear();
	}
	
	/**
	 * Dispose all your resources here.
	 */
	public abstract void dispose();
	
	/**
	 * Optional and is called once every frame after updating all the entities and translation, right before rendering.
	 */
	protected void extra()
	{}
	
	/**
	 * Called by the engine when all playable {@code MainCharacters} are dead and returns whether or not a checkpoint was reached during the play.<br>
	 * By default, this function always return false so it should be overridden by the subclasses if checkpoints are supported.<br>
	 * In case a checkpoint was gotten, the player receive a different death message. Furthermore, the time is not reseted and the replay is not saved.<br>
	 * Other than that, the stage is cleared as usual and {@code build} is called again.
	 * @return True if one or more of the main characters got a checkpoint during the play.
	 */
	protected boolean isSafe()
	{
		return false;
	}
	
	/**
	 * This function sends data to the replay file(default=empty string) that needs to be saved.<br>
	 * The meta need to be serializable. 
	 * Called automatically by the engine.
	 * @return The meta data to save in the replay file.
	 */
	protected Serializable getMeta()
	{
		return "";
	}
	
	/**
	 * Automatically called by the engine and sends the meta data from the replay, if exists.<br>
	 * By default, a stage does not contain any meta data.
	 * @param meta The meta data, sent by the engine.
	 */
	public void setMeta(Serializable meta) {}
	
	/**
	 * The difficulty the stage being played at.
	 * @return The difficulty.
	 */
	public Difficulty getDifficulty() 
	{
		return difficulty;
	}

	/**
	 * The difficulty to use when playing.
	 * @return The difficulty.
	 */
	public void setDifficulty(Difficulty difficulty) 
	{
		this.difficulty = difficulty;
	}
	
	/**
	 * Checks if the specified point contains solid tile.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return True if the given point is solid.
	 */
	public boolean isSolid(float x, float y)
	{
		return stageData[(int)x][(int)y] == Engine.SOLID;
	}
	
	/**
	 * Checks if the specified point contains hollow tile.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return True if the given point is hollow.
	 */
	public boolean isHollow(float x, float y)
	{
		return stageData[(int)x][(int)y] == Engine.HOLLOW;
	}

	/**
	 * Returns the tile type from the stage data clone.
	 * @param x The X position.
	 * @param y The Y position.
	 * @return The tile type at the given point.
	 */
	public byte getCloneData(int x, int y)
	{
		if(stageClone == null)
			cloneStageData();
		
		return stageClone[y][x];
	}
	
	/**
	 * Disposes the given objects. Support the following types:<br>
	 * @code{Image2D, Image2D[], TextureRegion, TextureRegion[], Pixmap, Texture, Texture[], ParticleEffect, Disposable, Sound and Music}.
	 * @param objs
	 */
	public static void disposeBatch(Object... objs)
	{
		for(Object obj : objs)
		{
			try
			{
				if(obj instanceof Image2D)
					((Image2D)obj).dispose();
				else if(obj instanceof Image2D[])
				{
					Image2D[] arr = (Image2D[]) obj;
					for(Image2D img : arr)
						img.dispose();
				}
				else if(obj instanceof TextureRegion)
					((TextureRegion)obj).getTexture().dispose();
				else if(obj instanceof TextureRegion[])
				{
					TextureRegion[] arr = (TextureRegion[]) obj;
					for(TextureRegion img : arr)
						img.getTexture().dispose();
				}
				else if(obj instanceof Pixmap)
					((Pixmap)obj).dispose();
				else if(obj instanceof Texture)
					((Texture)obj).dispose();
				else if(obj instanceof Texture[])
				{
					Texture[] arr = (Texture[]) obj;
					for(Texture img : arr)
						img.dispose();
				}
				else if(obj instanceof ParticleEffect)
					((ParticleEffect)obj).dispose();
				else if(obj instanceof Disposable)
					((Disposable)obj).dispose();
				else if(obj instanceof Sound)
					((Sound)obj).unload();
				else if(obj instanceof Music)
					((Music)obj).unload();
				else
					System.out.println("Unsupported type: " + obj.getClass());
			}
			catch(Exception e)
			{
				System.err.println("Failed to dispose a resource: " + obj);
			}
		}
	}
	
	/**
	 * Returns the stage that is currently being played.
	 * @return The active stage.
	 */
	public static Stage getCurrentStage()
	{
		return STAGE;
	}
	
	public static GameObject readTMX(final String path)
	{
		return new GameObject()
		{
			TiledMap map = new TmxMapLoader().load(path);
			OrthogonalTiledMapRenderer r;
			
			@Override
			public void drawSpecial(SpriteBatch batch) 
			{
				if(r == null)
				{
					r = new OrthogonalTiledMapRenderer(map, batch)
					{
						@Override
						protected void beginRender() {}
						
						@Override
						protected void endRender() {}
					};
				}
				
				AnimatedTiledMapTile.updateAnimationBaseTime();
				r.setView(Stage.STAGE.game.getCamera());
				r.render();
			}
		};
	}
	
	void updateFacing(MovableObject mo)
	{
		if(!mo.manualFacings)
		{
			Direction dir = Fundementals.getDirection(Fundementals.normalize(mo.prevX, mo.prevY, mo.loc.x, mo.loc.y));
			if(dir != null)
			{
				if(mo.doubleFaced)
				{
					if(dir != Direction.N && dir != Direction.S)
						mo.facing = dir;
				}
				else
					mo.facing = dir;
			}
		}
	}

	private void cloneStageData()
	{
		stageClone = new byte[stageData.length][stageData[0].length];
		
	    for (int i = 0; i < stageData.length; i++) 
	        System.arraycopy(stageData[i], 0, stageClone[i], 0, stageData[i].length);
	}
}