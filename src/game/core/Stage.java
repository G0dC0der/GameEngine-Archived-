package game.core;

import game.core.Engine.Direction;
import game.core.GameObject.Event;
import game.essentials.Image2D;
import game.essentials.Utilities;
import game.movable.TimedEnemy;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import kuusisto.tinysound.Music;

/**
 * Create your own stage by extending this class and adding your unique appearance.
 * @author Pojahn
 */
public abstract class Stage 
{	
	/**
	 * Playable stages need to add this annotation to their class deceleration so it can be recognized by certain methods.
	 * @author Pojahn Moradi
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Playable{}
	
	/**
	 * This enum is used when setting the background/foreground image the quick way with the predefined functions found in {@code Stage}.
	 * @author Pojahn Moradi
	 */
	public enum RenderType
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
	 * This is the stage that currently are being played. <br>
	 * This variable is set automatically when a stage has been launched.
	 */
	public static Stage STAGE;

	/**
	 * This is the damage a {@code MainCharacter} takes when he or she interact with LETHAL tile type.
	 */
	public static int LETHAL_DAMAGE = -1;
	
	/**
	 * This is the volume of the stage music. Defaults to 1.0, which is 100%.
	 */
	public static float MUSIC_VOLUME = 1f;
	
	/**
	 * The stage data, which stores all the tile information. The values stored in this matrix are constant found in {@code game.core.Engine}.
	 */
	public byte[][] stageData;
	
	/**
	 * This is the width of the stage. It is usually set automatically.
	 */
	public int width;
	
	/**
	 * This is the height of the stage. It is usually set automatically.
	 */
	public int height;
	
	/**
	 * This is the width of the game window. Can be manually set during development but can not be changed at runtime.
	 */
	public int visibleWidth;
	
	/**
	 * This is the height of the game window. Can be manually set during development but can not be changed at runtime.
	 */
	public int visibleHeight;
	
	/**
	 * A reference to the engine.
	 */
	public Engine game;
	
	/**
	 * The name of the stage. This name is used when generating welcome text and saving replays.
	 */
	protected String name;
	
	/**
	 * {@code startX} respective {@code startY} are the starting position of the main character.
	 */
	protected int startX, startY;
	
	/**
	 * The music that will play in the background. Can be either wav or ogg.
	 */
	protected Music music;
	
	private LinkedList<Object> discardList, appendList, trash;
	private byte[][] stageClone;
	private boolean pending;
	boolean sort;
	List<GameObject> stageObjects;
	List<Event> events;
	
	public Stage()
	{
		STAGE = this;
		discardList    = new LinkedList<>();
		appendList     = new LinkedList<>();
		stageObjects   = new LinkedList<>();
		trash		   = new LinkedList<>();
		events 		   = new LinkedList<>();
		visibleWidth = visibleHeight = startX = startY = -1;
	}
	
	/**
	 * Loads and starts the song found on the given path.
	 * @param path The path to where the song can be found.
	 * @param loopStart Where to start, in seconds, after the song have ended.
	 */
	public void setStageMusic (String path, double loopStart)
	{
		setStageMusic(Utilities.loadMusic(path), loopStart);
	}
	
	/**
	 * Starts the given song.
	 * @param music The song to start.
	 * @param loopStart Where to start, in seconds, after the song have ended.
	 */
	public void setStageMusic(Music music, double loopStart)
	{
		music.setVolume(MUSIC_VOLUME);
		music.setLoopPositionBySeconds(loopStart);
		music.play(true);
		this.music = music;
	}
		
	/**
	 * Initialize the starting position, size and the visible size of the stage.
	 */
	public void basicInits()
	{
		width = stageData[0].length;
		height = stageData.length;
		
		if(visibleWidth == -1 || visibleHeight == -1)
		{
			visibleWidth  = width;
			visibleHeight = height;
		}
		
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
	public void tileIntersection(MovableObject mo, byte tileType)
	{
		if (tileType != Engine.HOLLOW)
			mo.runTileEvents(tileType);
	}

	/**
	 * This method is called every frame by the engine and add or removes entities and update all the existing ones.
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
		
		for(GameObject go : stageObjects)
		{
			if(go instanceof Enemy)
			{
				Enemy enemy = (Enemy) go;
				enemy.moveEnemy();
				
				if(enemy.triggerable)
				{
					enemy.occupyingCells.clear();
					enemy.tileCheck();
					enemy.inspectIntersections();
				}
				
				if(enemy.multiFacings && !enemy.manualFacings)
				{
					Direction dir = EntityStuff.getDirection(EntityStuff.normalize(enemy.prevX + enemy.width / 2, enemy.prevY + enemy.height / 2, enemy.currX + enemy.width / 2, enemy.currY + enemy.height / 2));
					if(dir != null)
						enemy.facing = dir;
				}
				enemy.prevX = enemy.currX;
				enemy.prevY = enemy.currY;
			}
			
			go.removeQueuedEvents();
			go.runEvents();
		}
		
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
	public void background(RenderType type, Image2D... img)
	{
		SceneImage wrapper = new SceneImage(type);
		wrapper.setImage(img);
		wrapper.zIndex(-100);
		add(wrapper);
	}
	
	/**
	 * An optional way of setting the foreground. Wraps the given image in a {@code GameObject} with {@code z-index} set to 100.
	 * @param img The image to use as foreground.
	 */
	public void foreground(RenderType type, Image2D... img)
	{
		SceneImage wrapper = new SceneImage(type);
		wrapper.setImage(img);
		wrapper.zIndex(100);
		add(wrapper);
	}
	
	/**
	 * This function should be used as constructor when subclassing. Operations that only requires to be performed once(such as image and sound loading) should be done here.
	 */
	public abstract void init();
	
	/**
	 * The welcome screen, often just a {@code JOptionPane} window.
	 */
	public abstract void welcome();
	
	/**
	 * Called upon start, death and restart. The stage is built from this method.
	 */
	public void build()
	{
		for(GameObject go : stageObjects)
			go.endUse();
		
		trash.clear();
		stageObjects.clear();
		events.clear();
		appendList.clear();
		discardList.clear();
		game.clearGhosts();
		game.elapsedTime = 0;
	}
	
	/**
	 * Dispose all your resources here.
	 */
	public abstract void dispose();
	
	/**
	 * This method optional and is called once every frame.
	 */
	public abstract void extra();
	
	/**
	 * This function return data to the replay file(default=empty string) that needs to be saved.<br>
	 * Called automatically by the server.
	 * @return The meta data to save in the replay file.
	 */
	protected String getMeta()
	{
		return "";
	}
	
	/**
	 * Automatically called by the engine and sends the meta data from the replay, if exists.<br>
	 * By default, a stage does not contain any meta data.
	 * @param meta The meta data, sent by the engine.
	 */
	public void setMeta(String meta) {}
	
	/**
	 * Converts this map to a string. Note that the string can become very large(height * width + height).
	 */
	public String toString()
	{
		StringBuilder bu = new StringBuilder ((height * width) + height);
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
				bu.append(stageData[i][j]);
			
			bu.append('\n');
		}
		return bu.toString();
	}
	
	/**
	 * Creates a full clone of the stage data.
	 */
	private void cloneStageData()
	{
		stageClone = new byte[stageData.length][stageData[0].length];
		
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				stageClone[y][x] = stageData[y][x];
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
	 * A completely optional way of creating the welcome text.
	 * @param author The author of the stage.
	 * @param difficulty The difficulty of the stage. Can be in any format.
	 * @param avTime The average completion time.
	 * @param proTime The professional completion time.
	 * @param objective The stages objective.
	 * @return The generated string.
	 */
	public String getWelcomeText(String author, String difficulty, int avTime, int proTime, String objective)
	{
		return   "Stage: " + name +
			   "\nAuthor: " + author +
			   "\nDifficulty: " + difficulty +
			   "\nAvarage Time: " + avTime +
			   "\nProfessional Time: " + proTime +
			   "\nObjective: " + objective;
	}
	
	private static class SceneImage extends TimedEnemy
	{
		RenderType type;
		
		SceneImage(RenderType type)
		{
			this.type = type;
			setVisible(true);
			time = Integer.MAX_VALUE;
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
					clearTransformation(batch);
					batch.draw(super.getFrame(), 0, 0);
					restoreTransformation(batch);
					break;
				case PORTION:
					OrthographicCamera camera = STAGE.game.getCamera();
					Engine e = STAGE.game;
					int vw = Stage.STAGE.visibleWidth;
					int vh = Stage.STAGE.visibleHeight;
					
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