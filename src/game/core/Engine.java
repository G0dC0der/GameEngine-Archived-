package game.core;

import game.core.GameObject.Event;
import game.core.MainCharacter.CharacterState;
import game.essentials.Controller;
import game.essentials.Controller.PressedButtons;
import game.essentials.HighScore;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import game.essentials.Utilities;
import java.awt.Dimension;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import kuusisto.tinysound.TinySound;
import org.lwjgl.opengl.GL11;
import pjjava.misc.OtherMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * The Engine class is the core of the game. All calculations and controls and the required function calls are being called from here.
 * @author Pojahn Moradi
 */
public final class Engine implements Screen
{
	public enum Direction{N,NE,E,SE,S,SW,W,NW};
	
	/**
	 * The default delta value.
	 */
	public static final float DELTA = 1.0f / 60.0f;
	
	/**
	 * The constant that represent solid tile.
	 */
	public static final byte SOLID 		    = 0;
	
	/**
	 * The constant that represent hollow tile.
	 */
	public static final byte HOLLOW 		= 1;
	
	/**
	 * The constant that represent the starting position.
	 */
	public static final byte START_POSITION = 2;
	
	/**
	 * The constant that represent lethal tile.
	 */
	public static final byte LETHAL		    = 3;
	
	/**
	 * The constant that represent the goal.
	 */
	public static final byte GOAL			= 4;
	
	/**
	 * Custom tile type 1. The behavior of the tile is stage specific.
	 */
	public static final byte AREA_TRIGGER_0 = 5;
	
	/**
	 * Custom tile type 2. The behavior of the tile is stage specific.
	 */
	public static final byte AREA_TRIGGER_1 = 6;
	
	/**
	 * Custom tile type 3. The behavior of the tile is stage specific.
	 */
	public static final byte AREA_TRIGGER_2 = 7;
	
	/**
	 * Custom tile type 4. The behavior of the tile is stage specific.
	 */
	public static final byte AREA_TRIGGER_3 = 8;
	
	/**
	 * Custom tile type 5. The behavior of the tile is stage specific.
	 */
	public static final byte AREA_TRIGGER_4 = 9;
	
	/**
	 * Custom tile type 6. The behavior of the tile is stage specific.
	 */
	public static final byte AREA_TRIGGER_5 = 10;
	
	/**
	 * Custom tile type 7. The behavior of the tile is stage specific.
	 */
	public static final byte AREA_TRIGGER_6 = 11;
	
	/**
	 * Custom tile type 8. The behavior of the tile is stage specific.
	 */
	public static final byte AREA_TRIGGER_7 = 12;
	
	/**
	 * Custom tile type 9. The behavior of the tile is stage specific.
	 */
	public static final byte AREA_TRIGGER_8 = 13;
	
	/**
	 * Custom tile type 10. The behavior of the tile is stage specific.
	 */
	public static final byte AREA_TRIGGER_9 = 14;
	/**
	 * The color that represent hollow tile(RGB:125,125,125).
	 */
	public static final Color GRAY 	  	  = Color.valueOf("7d7d7dff");
	/**
	 * The color that represent solid tile(RGB:90,90,90).
	 */
	public static final Color DARK_GRAY   = Color.valueOf("5a5a5aff");
	/**
	 * The color that represent the goal(RGB:255,0,0).
	 */
	public static final Color RED		  = Color.valueOf("ff0000ff");
	/**
	 * The color that represent the starting point(RGB:0,0,255).
	 */
	public static final Color BLUE        = Color.valueOf("0000ffff");
	/**
	 * The color that represent lethal tile(RGB:255,255,0).
	 */
	public static final Color YELLOW      = Color.valueOf("ffff00ff");
	/**
	 * The color that represent AREA_TRIGGER_0(RGB:0,255,0).
	 */
	public static final Color GREEN_0	  = Color.valueOf("00ff00ff");
	/**
	 * The color that represent AREA_TRIGGER_1(RGB:0,220,0).
	 */
	public static final Color GREEN_1	  = Color.valueOf("00dc00ff");
	/**
	 * The color that represent AREA_TRIGGER_2(RGB:0,190,0).
	 */
	public static final Color GREEN_2	  = Color.valueOf("00be00ff");
	/**
	 * The color that represent AREA_TRIGGER_3(RGB:0,160,0).
	 */
	public static final Color GREEN_3	  = Color.valueOf("00a000ff");
	/**
	 * The color that represent AREA_TRIGGER_4(RGB:0,130,0).
	 */
	public static final Color GREEN_4	  = Color.valueOf("008200ff");
	/**
	 * The color that represent AREA_TRIGGER_5(RGB:0,100,0).
	 */
	public static final Color GREEN_5	  = Color.valueOf("006400ff");
	/**
	 * The color that represent AREA_TRIGGER_6(RGB:0,70,0).
	 */
	public static final Color GREEN_6	  = Color.valueOf("004600ff");
	/**
	 * The color that represent AREA_TRIGGER_7(RGB:0,40,0).
	 */
	public static final Color GREEN_7	  = Color.valueOf("002800ff");
	/**
	 * The color that represent AREA_TRIGGER_8(RGB:0,10,0).
	 */
	public static final Color GREEN_8	  = Color.valueOf("000a00ff");
	/**
	 * The color that represent AREA_TRIGGER_9(BLACK)(RGB:0,0,0).
	 */
	public static final Color GREEN_9	  = Color.valueOf("000000ff");
	
	/**
	 * Default graphics used by laser firing entities. Can of course be changed.
	 */
	public static Image2D[] LASER_BEAM, LASER_BEGIN, LASER_IMPACT, LASER_CHARGE;
	
	/**
	 * The font of the timer.
	 */
	public BitmapFont timeFont, fpsFont;
	
	/**
	 * The font color of the timer.
	 */
	public Color timeColor = new Color(0,0,0,255);
	
	/**
	 * The color of the text that shows up upon death.
	 */
	public Color deathTextColor = new Color(0,0,0,255);
	
	/**
	 * The tinting color to fade to upon victory. Set it to {@code defaultTint} to disable.
	 */
	public Color wintTint = Color.valueOf("ff00ffff");
	
	/**
	 * The default tint color.
	 */
	public final Color defaultTint = Color.valueOf("fffffffe");
	
	/**
	 * Whether or not to streams sounds directly from the file rather than loading it to the memory. Must be changed before launching game in order for it to take effect.
	 */
	public boolean streamSounds = false;
	
	/**
	 * Whether or not to clear the container every frame.
	 */
	public boolean clearEachFrame = true;
	
	/**
	 * Whether or not to save replays upon victory and death.
	 */
	public boolean saveReplays = true;
	
	private static int DELTA_VALUE = 0;
	
	/**
	 * The state of the game can be manipulated with the help of these enums.
	 * @author Pojahn Moradi
	 */
	public enum GameState {ONGOING, ENDED, FINISH, PAUSED};
	
	/**
	 * The translation X of the world.
	 */
	public float tx;
	
	/**
	 * The translation Y of the world.
	 */
	public float ty;
	
	/**
	 * The zoom factor of the world.
	 */
	public float zoom;
	
	/**
	 * The angle (in degrees) to rotate the world by. 
	 */
	public float angle;
	
	/**
	 * The padding in pixels to use when zooming out.
	 */
	public int zoomPadding = 20;
	
	/**
	 * The amount of milliseconds that have passed since last death or first start.
	 */
	public int elapsedTime;
	
	/**
	 * The master volume.
	 */
	public double masterVolume = 1.0;
	
	List<GameObject> focusObjs;
	Stage stage;
	Dimension viewport;
	private List<List<PressedButtons>> replays;
	private GameState globalState;
	private boolean showFps, justRestarted, playReplay, increasingVert, increasingHor, showingDialog, replayHelp, crashed, increasingScale = true;
	private float vertLength, vertSpeed, vertValue, horLength, horSpeed, horValue, scaleMin, scaleMax, scaleSpeed, scaleValue, musicVolume;
	private int fpsWriterCounter, fps;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private HashSet<Integer> pressedKeys;
	private Color currTint;
	private Event exitEvent;
	private com.badlogic.gdx.scenes.scene2d.Stage gui;
	private Skin skin;
	private Texture errorIcon;
	
	/**
	 * Constructs an Engine.
	 * @param stage The stage to play.
	 * @param replay The replay to watch. If null is set, you will play the stage rather than watching a replay.
	 */
	public Engine(Stage stage, List<List<PressedButtons>> replays)	
	{
		stage.game = this;
		this.stage = stage;
		elapsedTime = 0;
		globalState = GameState.ONGOING;
		zoom = 1;
		pressedKeys = new HashSet<>();
		currTint = new Color(defaultTint);
		errorIcon = new Texture(Gdx.files.internal("res/data/error.png"));
		justRestarted = true;
		focusObjs = new ArrayList<>();
		viewport = new Dimension(800, 600);
		
		if(replays == null)
		{
			this.replays = new LinkedList<>();
			gui = new com.badlogic.gdx.scenes.scene2d.Stage();
			skin = new Skin(Gdx.files.internal("res/data/uiskin.json"));
		}
		else
		{
			this.replays = replays;
			playReplay = true;
		}
	}
	
	
	@Override
	public void render(float delta)
	{
		if(crashed)
		{
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
			
			gui.act(Gdx.graphics.getDeltaTime());
			gui.draw();
		}
		else
		{
			try
			{
				boolean escDown = isKeyPressed(Keys.ESCAPE);
				if((globalState == GameState.ONGOING || globalState == GameState.PAUSED) && !playReplay && escDown)
					globalState = globalState == GameState.PAUSED ? GameState.ONGOING : GameState.PAUSED;
				
				if(escDown && playReplay)
					replayHelp = !replayHelp;
				
				if(globalState == GameState.PAUSED && !playReplay)
				{
					pressedKeys.clear();
					if(stage.music != null  && stage.music.getVolume() != .101f)
					{
						if(musicVolume != .1f)
							musicVolume = (float) stage.music.getVolume();
						
						stage.music.setVolume(.101f);
					}
					
					batch.begin();
					renderPause();
					batch.end();
				}
				else
				{
					if(stage.music != null  && stage.music.getVolume() == .101f)
						stage.music.setVolume(musicVolume);
					
					update();
					paint();
				}
			}
			catch(Exception e)
			{
				crashed = true;
				if(stage.music != null)
					stage.music.stop();
				e.printStackTrace();
				showCrashDialog(e);
			}
		}
	}

	private void paint()
	{
		if(clearEachFrame)
		{
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}
		
		if(vertSpeed > 0)
			moveVert();
		if(horSpeed > 0)
			moveHor();
		if(scaleSpeed > 0)
			scale();
		
		camera.position.set(tx, ty, 0);
		camera.zoom = zoom;
		camera.rotate(angle);
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		if(globalState == GameState.FINISH)
		{
			Utilities.fadeColor(currTint, wintTint, .005f);
			batch.setColor(currTint);
		}
		
		for(GameObject go : stage.stageObjects)
		{
			if (go.visible)
			{
				if(go.drawSpecialBehind)
				{
					go.drawSpecial(batch);
					drawObject(go);
				}
				else
				{
					drawObject(go);
					go.drawSpecial(batch);
				}
			}
		}
		
		camera.position.set(viewport.width / 2, viewport.height / 2, 0);
		camera.zoom = 1;
		camera.rotate(-angle);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		renderStatusBar();

		if(globalState == GameState.ENDED)
		{
			if(!replayHelp && !playReplay)
				timeFont.draw(batch, "You are dead. Press 'R' to retry or 'B' to go back.", viewport.width / 2 - 320, viewport.height / 2);
			else
				timeFont.draw(batch, "Press 'B' to return to the main menu.", viewport.width / 2 - 230, viewport.height / 2);
		}
		else if(replayHelp)
			timeFont.draw(batch, "Press 'B' to return to the main menu.", viewport.width / 2 - 230, viewport.height / 2);
		
		if(showFps)
		{
			if(++fpsWriterCounter % 10 == 0)
				fps = (int)(1.0f/Gdx.graphics.getDeltaTime());
			
			fpsFont.setColor(Color.WHITE);
			fpsFont.draw(batch, fps + " fps", viewport.width - 60, 5);
		}
			
		batch.end();
		
		if(showingDialog)
		{
			gui.act(Gdx.graphics.getDeltaTime());
			gui.draw();
		}
	}
	
	public void update()
	{
		if(replayHelp && Gdx.input.isKeyPressed(Keys.B))
		{
			runExitEvent();
			return;
		}
		
		DELTA_VALUE = (int) (Gdx.graphics.getDeltaTime() * 1000f);
		
		if(globalState == GameState.ENDED)
		{
			if(Gdx.input.isKeyPressed(Keys.R) && !playReplay)
			{
				restart();
				stage.build();
			}
			else if(Gdx.input.isKeyPressed(Keys.B))
				runExitEvent();
		}
		else if(globalState == GameState.FINISH)
			winAction();
		
		if(globalState == GameState.ONGOING)
		{
			if(!justRestarted)
				elapsedTime += DELTA_VALUE;
			else
				justRestarted = false;
		}
		
		stage.moveEnemies();
		
		updateCamera();
		
		stage.extra();

		SoundBank.FRAME_COUNTER++;
		pressedKeys.clear();
	}
	
	@Override
	public void show()  
	{
		timeFont = new BitmapFont(Gdx.files.internal("res/data/sansserif32.fnt"), true);
		fpsFont  = new BitmapFont(Gdx.files.internal("res/data/cambria20.fnt"), true);
		
		LASER_BEAM = Image2D.loadImages(new File("res/data/laser"),false);
		LASER_BEGIN = Image2D.loadImages(new File("res/data/laser/rear"),false);
		LASER_IMPACT = Image2D.loadImages(new File("res/data/laser/end"),false);
		LASER_CHARGE = Image2D.loadImages(new File("res/data/charge"),false);
		
		if(MainCharacter.DEFAULT_HEALTH_IMAGE == null)
			MainCharacter.DEFAULT_HEALTH_IMAGE = new Image2D("res/general/hearth.png", false);
		
		TinySound.init();
		TinySound.setGlobalVolume(masterVolume);

		stage.init();
		stage.build();

		batch = new SpriteBatch();
		camera = new OrthographicCamera(viewport.width, viewport.height);
		camera.setToOrtho(true,viewport.width, viewport.height);

		Gdx.graphics.setDisplayMode(viewport.width, viewport.height, false);
		ShaderProgram.pedantic = false;
		
		Gdx.input.setInputProcessor(new InputAdapter()
		{			
			@Override
			public boolean keyDown(int key) 
			{
				pressedKeys.add(key);
				return true;
			}
		});
	}
	
	@Override
	public void dispose()
	{
		TinySound.shutdown();
		stage.dispose();
		timeFont.dispose();
		fpsFont.dispose();
		errorIcon.dispose();
		Stage.disposeBatch(LASER_BEAM, LASER_BEGIN, LASER_IMPACT, LASER_CHARGE, MainCharacter.DEFAULT_HEALTH_IMAGE);
		if(!playReplay)
		{
			skin.dispose();
			gui.dispose();
		}
		MainCharacter.DEFAULT_HEALTH_IMAGE = null;
		stage = null;
	}
	
	OrthographicCamera getCamera()
	{
		return camera;
	}
	
	/**
	 * Returns the current state of the game.
	 * @return The current state of the game.
	 */
	public GameState getGlobalState()
	{
		return globalState;
	}
	
	/**
	 * In this function can you manipulate the state of the game. You can for example pause the game, end it etc.
	 * Function is ignored if you are dead or have have finished the stage.
	 * @param globalState The state the game should be changed to.
	 */
	void setGlobalState(GameState globalState)
	{
		if(this.globalState != GameState.FINISH && this.globalState != GameState.ENDED)
		{
			this.globalState = globalState;

			if(this.globalState == GameState.ENDED)
				saveReplay("Loser");
		}
	}
	
	/**
	 * Sets the size of the viewport.
	 * @param width The width in pixels.
	 * @param height The height in pixels.
	 */
	public void setViewport(int width, int height)
	{
		viewport.width = width;
		viewport.height = height;
		
		camera = new OrthographicCamera(viewport.width, viewport.height);
		camera.setToOrtho(true,viewport.width, viewport.height);
		
		Gdx.graphics.setDisplayMode(viewport.width, viewport.height, false);
	}
	
	/**
	 * Returns the width of the viewport.
	 * @return The width in pixels.
	 */
	public int getScreenWidth()
	{
		return viewport.width;
	}
	
	/**
	 * Returns the height of the viewport.
	 * @return The height in pixels.
	 */
	public int getScreenHeight()
	{
		return viewport.height;
	}
	
	/**
	 * This function allow you to determine which {@code GameObjects} the game should focus on. <br>
	 * The screen will follow the specified {@code GameObjects} whenever they moves. <br>
	 * This is usual set to the main character(s).<br><br>
	 * 
	 * If set, {@code tx, ty} and possibly {@code scale} will be modified.
	 * @param focus The {@code GameObject} to follow.
	 */
	public void addFocusObject(GameObject focus)
	{
		focusObjs.add(focus);
	}
	
	/**
	 * Returns a copy of the focus list.
	 * @return The focus objects.
	 */
	public List<GameObject> getFocusList()
	{
		return new ArrayList<>(focusObjs);
	}
	
	/**
	 * Stops the camera from focusing on the specified object.
	 * @param obj The object to stop film.
	 */
	public void removeFocusObject(GameObject obj)
	{
		focusObjs.remove(obj);
	}
	
	/**
	 * Checks if the specified key is down. Works only the first frame the key was down.
	 * @param key The key to check.
	 * @return True if the specified key was down.
	 */
	public boolean isKeyPressed(int key)
	{
		return pressedKeys.contains(key);
	}
	
	/**
	 * Adds a vertical shake effect to the game. Set to 0 to disable the effect.
	 * @param length The length of the vertical shake. Can not be negative.
	 * @param speed The speed of the shake. Can not be negative.
	 */
	public void drugVertical(float length, float speed)
	{
		if(0 > length || 0 > speed)
			throw new IllegalArgumentException("Both values must be positive.");
		
		if(speed == 0)
			vertValue = 0;
		
		vertLength = length;
		vertSpeed = speed;
	}
	
	/**
	 * Adds a horizontal shake effect to the game. Set to 0 to disable the effect.
	 * @param length The length of the horizontal shake. Can not be negative.
	 * @param speed The speed of the shake. Can not be negative.
	 */
	public void drugHorizontal(float length, float speed)
	{
		if(0 > length || 0 > speed)
			throw new IllegalArgumentException("Both values must be positive.");
		
		if(speed == 0)
			horValue = 0;
		
		horLength = length;
		horSpeed = speed;		
	}
	
	/**
	 * Resizing the overall graphics of the game, cycling from min to max, using the given speed as the speed.
	 * @param min The minimum size.
	 * @param max The maximum size.
	 * @param speed The speed of the cycle.
	 */
	public void drugScale(float min, float max, float speed)
	{
		if(0 > min || 0 > max || 0 > speed)
			throw new IllegalArgumentException("All values must be positive.");
		
		if(speed == 0)
			scaleValue = 0;
		else
			scaleValue = 1;
		
		scaleMin = min;
		scaleMax = max;
		scaleSpeed = speed;
	}
	
	/**
	 * Clears the transformation matrix.
	 */
	public void clearTransformation()
	{
		camera.position.set(viewport.width / 2, viewport.height / 2, 0);
		camera.zoom = 1;
		camera.rotate(-angle);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}
	
	/**
	 * Restores the transformation matrix.
	 */
	public void restoreTransformation()
	{
		camera.position.set(tx, ty, 0);
		camera.zoom = zoom;
		camera.rotate(angle);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}
	
	/**
	 * Whether or not to show the fps.
	 * @param showFps True to enable fps display.
	 */
	public void showFps(boolean showFps)
	{
		this.showFps = showFps;
	}
	
	/**
	 * The event to launch when the game is exiting. More precise, this event is launched when a stage is terminated.<br>
	 * You usually want to unhide the main menu(or reconstruct it) as well as nullifying the {@code Stage} and {@code Engine} instance.<br>
	 * The resources used by the engine is cleared automatically. The resources used by the stage as disposed by the stage creator, so those do not need to be included here.
	 * @param exitEvent The event to execute upon disposal.
	 */
	public void setExitEvent(Event exitEvent)
	{
		this.exitEvent = exitEvent;
	}
	
	/**
	 * Whether or not the engine is currently displaying a replay and not game play.
	 * @return True if a replay displayed.
	 */
	public boolean playingReplay()
	{
		return playReplay;
	}

	/**
	 * Checks which button of the given controller are down.
	 * @param con The controller.
	 * @return The buttons being held down.
	 */
	public PressedButtons getPressedButtons(Controller con)
	{
		PressedButtons pb = new PressedButtons();
		pb.down 	  = Gdx.input.isKeyPressed(con.down);
		pb.left 	  = Gdx.input.isKeyPressed(con.left);
		pb.right 	  = Gdx.input.isKeyPressed(con.right);
		pb.up 		  = Gdx.input.isKeyPressed(con.up);
		pb.special1   = isKeyPressed(con.special1);
		pb.special2   = isKeyPressed(con.special2);
		pb.special3   = isKeyPressed(con.special3);
		pb.switchChar = isKeyPressed(con.switchChar);
		pb.suicide    = isKeyPressed(con.suicide);
		
		return pb;
	}

	/**
	 * The amount of milliseconds since the last frame.
	 * @return The millis.
	 */
	public static int getDelta()
	{
		return DELTA_VALUE;
	}
	
	PressedButtons getReplayFrame(int index)
	{
		List<PressedButtons> pbs = replays.get(index);
		
		if(pbs.size() <= 0)
			return MainCharacter.STILL;
		
		PressedButtons pb = pbs.get(0);
		pbs.remove(0);
		
		return pb;
	}
	
	void registerReplayFrame(int index, PressedButtons pbs)
	{
		if(index > replays.size() - 1)
		{
			for(int i = 0; i <= index; i++)
			{
				if(i >= replays.size())
					replays.add(new LinkedList<PressedButtons>());
			}
		}
		
		replays.get(index).add(pbs);
	}
	
	private void updateCamera()
	{
		final int size = focusObjs.size();
		final GameObject first = focusObjs.get(0);
		
		if(size == 1)
		{
			tx = Math.min(stage.size.width  - viewport.width,   Math.max(0, first.getCenterX() - viewport.width  / 2)) + viewport.width  / 2; 
			ty = Math.min(stage.size.height - viewport.height,  Math.max(0, first.getCenterY() - viewport.height / 2)) + viewport.height / 2;
		}
		else if(size > 1)
		{
			final float marginX = viewport.width  / 2;
			final float marginY = viewport.height / 2;
			
			float boxX	= first.currX;
			float boxY	= first.currY; 
			float boxWidth	= boxX + first.getWidth();
			float boxHeight	= boxY + first.getHeight();

			for(int i = 1; i < focusObjs.size(); i++)
			{
				GameObject focus = focusObjs.get(i);
				
				boxX = Math.min( boxX, focus.currX );
				boxY = Math.min( boxY, focus.currY );
				
				boxWidth  = Math.max( boxWidth,  focus.currX + focus.getWidth () );
				boxHeight = Math.max( boxHeight, focus.currY + focus.getHeight() );
			}
			boxWidth = boxWidth - boxX;
			boxHeight = boxHeight - boxY;
			
			boxX -= zoomPadding;
			boxY -= zoomPadding;
			boxWidth  += zoomPadding * 2;
			boxHeight += zoomPadding * 2;
			
			boxX = Math.max( boxX, 0 );
			boxX = Math.min( boxX, stage.size.width - boxWidth ); 			

			boxY = Math.max( boxY, 0 );
			boxY = Math.min( boxY, stage.size.height - boxHeight );
			
			if((float)boxWidth / (float)boxHeight > (float)viewport.width / (float)viewport.height)
				zoom = boxWidth / viewport.width;
			else
				zoom = boxHeight / viewport.height;
			
			zoom = Math.max( zoom, 1.0f );

			tx = boxX + ( boxWidth  / 2 );
			ty = boxY + ( boxHeight / 2 );
			
			if(marginX > tx)
				tx = marginX;
			else if(tx > stage.size.width - marginX)
				tx = stage.size.width - marginX;
			
			if(marginY > ty)
				ty = marginY;
			else if(ty > stage.size.height - marginY)
				ty = stage.size.height - marginY;
		}
	}
	
	private void restart()
	{
		justRestarted =  increasingScale = true;
		showingDialog = false;
		if(!playReplay)
			replays.clear();
		globalState = GameState.ONGOING;
		horValue = vertValue = DELTA_VALUE = 0;
		batch.setColor(defaultTint);
		currTint = new Color(defaultTint);
		focusObjs.clear();
		
		if(!playReplay)
		{
			Gdx.input.setInputProcessor(new InputAdapter()
			{			
				@Override
				public boolean keyDown(int key) 
				{
					pressedKeys.add(key);
					return true;
				}
			});
		}
	}
	
	private void renderStatusBar()
	{
		if(globalState == GameState.PAUSED)
			timeFont.setColor(Color.WHITE);
		else
			timeFont.setColor(timeColor);
		timeFont.draw(batch, OtherMath.round((double)elapsedTime/1000, 1) + "", 10, 10);

		int y = 40;
		
		for(int index = 0; index < stage.mains.size(); index++)
		{
			MainCharacter main = stage.mains.get(index);
			int hp = main.getHP();
			
			if(main.healthImg != null && main.getState() != CharacterState.DEAD && hp > 0)
			{
				final float width = main.healthImg.getWidth() + 3;
				
				for(int i = 0, posX = 10; i < hp; i++, posX += width)
					batch.draw(main.healthImg, posX, y);
				
				y += main.healthImg.getHeight() + 3;
			}
		}
	}
	
	private void drawObject(GameObject go)
	{
		Image2D img = go.getFrame();
		if (img != null)
			batch.draw(img, go.currX + go.offsetX, go.currY + go.offsetY, img.getWidth() / 2,img.getHeight() / 2, go.width * go.scale, go.height * go.scale, 1, 1, go.rotation);
	}
	
	private void saveReplay(String playername)
	{
		if(!playReplay && saveReplays)
		{
			HighScore hs = new HighScore();
			hs.replays = replays;
			hs.meta = stage.getMeta();
			hs.name = playername;
			hs.difficulty = stage.getDifficulty();
			hs.stageName = Utilities.prettify(stage.getClass().getSimpleName());
			hs.time = OtherMath.round((double)elapsedTime/1000, 1);
			hs.date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
			hs.className = stage.getClass();
			hs.result = globalState == GameState.ENDED ? "Death" : "Victorious";
			
			Utilities.exportObject(hs, "replays/" + cleanString(stage.getClass().getSimpleName()) + " " + cleanString(playername) + " " + hs.result + " " + hs.time + " sec " + hs.date + ".hs");
		}
	}	
	
	private void winAction()
	{
		if(playReplay || showingDialog)
			return;

		showingDialog = true;
		Gdx.input.setInputProcessor(gui);
		
		new Dialog("Stage Complete!", skin)
		{
			TextField field;
			
			{
				field = new TextField("", skin);
				text("Congratulations!\nIt took you " + OtherMath.round((double)elapsedTime/1000, 1) + " seconds to finish the stage.\nEnter your name to save your replay.");
				getContentTable().row();
				getContentTable().add(field);
				button("Retry", "retry");
				button("Return To Menu", "menu");
				setModal(false);
			}
			
			protected void result(Object object) 
			{
				String name = field.getText();
				if(name == null || name.isEmpty())
					name = "Player";
				
				if(object.equals("retry"))
				{
					saveReplay(name);
					stage.build();
					restart();
				}
				else if(object.equals("menu"))
				{
					saveReplay(name);
					runExitEvent();
				}
			}
		}.show(gui);
	}
	
	private void showCrashDialog(final Exception e)
	{
		Gdx.input.setInputProcessor(gui);
		new Dialog("Fatal Error", skin)
		{
			{
				Image img = new Image(errorIcon);
				
				getContentTable().add(img).left();
				getContentTable().row();
				getContentTable().add("Pojahns Game Engine have crashed and is unable to continue:\n").padRight(80).padTop(-52);
				getContentTable().row();
				
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Label l = new Label(sw.toString(), skin, "default-font", Color.RED);
				getContentTable().add(l);
				
				button("Return");
				setModal(true);
			}
			
			protected void result(Object object) 
			{
				runExitEvent();
			}
		}.show(gui);
	}

	private void renderPause()
	{
		Gdx.gl.glClearColor(0, 0, 0, .4f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		timeFont.setColor(Color.WHITE);
		timeFont.draw(batch, "Game is paused.", viewport.width / 2 - 120, viewport.height / 2);
		renderStatusBar();
	}
	
	private void moveVert()
	{
		if(increasingVert)
		{
			vertValue += vertSpeed;
			if(vertValue > vertLength)
				increasingVert = false;
		}
		else
		{
			vertValue -= vertSpeed;
			if(vertValue < -vertLength)
				increasingVert = true;
		}
		
		ty += vertValue;
	}
	
	private void moveHor()
	{
		if(increasingHor)
		{
			horValue += horSpeed;
			if(horValue > horLength)
				increasingHor = false;
		}
		else
		{
			horValue -= horSpeed;
			if(horValue < -horLength)
				increasingHor = true;
		}
		
		tx += horValue;		
	}
	
	private void scale()
	{
		if(increasingScale)
		{
			scaleValue += scaleSpeed;
			if(scaleValue > scaleMax)
				increasingScale = false;
		}
		else
		{
			scaleValue -= scaleSpeed;
			if(scaleValue < scaleMin)
				increasingScale = true;
		}
		
		zoom = scaleValue;
	}
	
	
	private String cleanString(String source)
	{
		StringBuilder filename = new StringBuilder();

		for (char c : source.toCharArray()) 
		{
			if (c=='.' || Character.isJavaIdentifierPart(c)) 
				filename.append(c);
			else
				filename.append("x");
		} 
		return filename.toString();
	}
	
	private void runExitEvent()
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				Gdx.app.postRunnable(new Runnable() 
				{
					@Override
					public void run() 
					{
						exitEvent.eventHandling();
					}
				});
			}
		}).start();
	}
	
	@Override public void hide() {dispose();}
	@Override public void pause() {}
	@Override public void resize(int x, int y) {}
	@Override public void resume() {}
}