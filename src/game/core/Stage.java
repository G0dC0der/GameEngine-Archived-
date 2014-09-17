package game.core;

import game.core.Engine.Direction;
import game.core.Engine.GameState;
import game.core.GameObject.Event;
import game.core.MainCharacter.CharacterState;
import game.essentials.Controller.PressedButtons;
import game.essentials.Image2D;
import java.awt.Dimension;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
	 * This enum is used when setting the background/foreground image the quick way with the predefined functions found in {@code Stage}.
	 * @author Pojahn Moradi
	 */
	public enum RenderOption
	{
		/**
		 * Renders the entire background/foreground.
		 */
		FULL,
		/**
		 * Render the background/foreground at a fixed position(the screen).<br>
		 */
		FIXED, 
		/**
		 * Render the parts of the background/foreground that is visible to the human eye. Possible performance boosts.
		 */
		PORTION
	};
	
	/**
	 * This is the stage that currently being played. <br>
	 * This variable is set automatically when a stage has been launched.
	 */
	public static Stage STAGE;
	
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
	
	public Stage()
	{
		STAGE = this;
		discardList    = new LinkedList<>();
		appendList     = new LinkedList<>();
		stageObjects   = new LinkedList<>();
		mains          = new LinkedList<>();
		trash		   = new LinkedList<>();
		events 		   = new LinkedList<>();
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
					
					enemy.prevX = enemy.currX;
					enemy.prevY = enemy.currY;
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
				main.prevX = main.currX;
				main.prevY = main.currY;
				
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
						pbs = game.getPressedButtons(main.con);
					
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
			}
			else
				main.goBack();
			
			if(!main.isGhost() && main.getState() == CharacterState.DEAD)
				aliveMains--;
			else if(!main.isGhost() && main.getState() == CharacterState.FINISH)
				game.setGlobalState(GameState.FINISH);
		}   
		
		if(0 >= aliveMains)
			game.setGlobalState(GameState.ENDED);
		
		if(!events.isEmpty())
			for(Event event : events)
				event.eventHandling();
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
				go.endUse();
			}
			if(obj != null)
				discardList.add(obj);
		}
	}
	
	/**
	 * An optional way of setting the background. Wraps the given image in a {@code GameObject} with {@code z-index} set to -100.
	 * @param img The image to use as background.
	 */
	public GameObject background(RenderOption type, Image2D... img)
	{
		SceneImage wrapper = new SceneImage(type);
		wrapper.setImage(3,img);
		wrapper.zIndex(-100);
		add(wrapper);
		
		return wrapper;
	}
	
	/**
	 * An optional way of setting the foreground. Wraps the given image in a {@code GameObject} with {@code z-index} set to 100.
	 * @param img The image to use as foreground.
	 */
	public GameObject foreground(RenderOption type, Image2D... img)
	{
		SceneImage wrapper = new SceneImage(type);
		wrapper.setImage(3,img);
		wrapper.zIndex(100);
		add(wrapper);
		
		return wrapper;
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
			go.endUse();
		
		trash.clear();
		stageObjects.clear();
		mains.clear();
		events.clear();
		appendList.clear();
		discardList.clear();
		game.elapsedTime = 0;
	}
	
	/**
	 * Dispose all your resources here.
	 */
	public abstract void dispose();
	
	/**
	 * Optional and is called once every frame after updating all the entities and translation, right before rendering.
	 */
	public void extra()
	{}
	
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
		if((mo.multiFacings || mo.doubleFaced) && !mo.manualFacings)
		{
			Direction dir = EntityStuff.getDirection(EntityStuff.normalize(mo.prevX + mo.width / 2, mo.prevY + mo.height / 2, mo.currX + mo.width / 2, mo.currY + mo.height / 2));
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
	
	private static class SceneImage extends GameObject
	{
		RenderOption type;
		
		SceneImage(RenderOption type)
		{
			this.type = type;
			setVisible(true);
		}
		
		@Override
		public Image2D getFrame()
		{
			return null;
		}
		
		@Override
		public void drawSpecial(SpriteBatch batch)
		{
			switch(type)
			{
				case FULL:
					batch.draw(super.getFrame(), 0, 0);
					break;
				case FIXED:
					STAGE.game.clearTransformation();
					batch.draw(super.getFrame(), 0, 0);
					STAGE.game.restoreTransformation();
					break;
				case PORTION:
					OrthographicCamera camera = STAGE.game.getCamera();
					Engine e = STAGE.game;
					int vw = Stage.STAGE.game.getScreenWidth();
					int vh = Stage.STAGE.game.getScreenHeight();
					
					camera.up.set(0, 1, 0);
					camera.direction.set(0, 0, -1);
					camera.update();
					batch.setProjectionMatrix(camera.combined);
					
					batch.draw(super.getFrame().getTexture(),
								e.tx - vw / 2,
								e.ty - vh / 2,
								(int)(e.tx - vw / 2),
								(int)(e.ty - vh / 2),
								vw,
								vh);
					
					camera.up.set(0, -1, 0);
					camera.direction.set(0, 0, 1);
					camera.update();
					batch.setProjectionMatrix(STAGE.game.getCamera().combined);
					break;
			}
		}
	}
}