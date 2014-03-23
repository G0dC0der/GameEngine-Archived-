package game.core;

import game.essentials.Controller;
import game.essentials.Controller.PressedButtons;
import game.essentials.HighScore;
import game.essentials.Image2D;
import game.essentials.SoundBank;
import game.essentials.Utilities;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import kuusisto.tinysound.TinySound;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * The Engine class is the core of the game. All calculations and controls and the required function calls are being called from here.
 * @author Pojahn Moradi
 */
public final class Engine implements ApplicationListener
{
	public enum Direction{N,NE,E,SE,S,SW,W,NW};
	
	/**
	 * The default delta value.
	 */
	public static final float DELTA = (float) 1 / 60;
	
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
	 * The font of the timer.
	 */
	public static BitmapFont TIME_FONT, FPS_FONT;
	
	/**
	 * The font color of the timer.
	 */
	public static Color TIME_COLOR = new Color(0,0,0,255);
	
	/**
	 * The color of the text that shows up upon death.
	 */
	public static Color DEATH_TEXT_COLOR = new Color(0,0,0,255);
	
	public static Color WIN_TINT = Color.valueOf("ff00ffff");
	
	/**
	 * The image to use to indicate the amount of health the main character have.
	 */
	public static Image2D LIFE_IMAGE;
	
	/**
	 * The volume of the game, where 1.0 is 100%.
	 */
	public static double GAME_VOLUME = 1.0;
	
	/**
	 * Whether or not to streams sounds directly from the file rather than loading it to the memory. Must be changed before launching game in order for it to take effect.
	 */
	public static boolean STREAM_SOUNDS = false;
	
	/**
	 * Whether or not to clear the container every frame.
	 */
	public static boolean CLEAR_EACH_FRAME = true;
	
	/**
	 * Whether or not to save replays upon victory and death.
	 */
	public static boolean SAVE_REPLAYS = true;
	
	public static final Color DEFAULT_TINT = Color.valueOf("fffffffe");
	
	private static final PressedButtons STILL = new PressedButtons();	
	private static int DELTA_VALUE = 0;
	
	/**
	 * The state of the game can be manipulated with the help of these enums.
	 * @author Pojahn Moradi
	 */
	public enum GameState {ALIVE, DEAD, FINISH, PAUSED};
	
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
	
	public int elapsedTime;
	GameObject focus;
	MainCharacter main;
	Stage stage;
	private GameState state;
	private boolean showFps, justRestarted, autoTranslate, playReplay, increasingVert, increasingHor, showingDialog, increasingScale = true;
	private List<PressedButtons> replay;
	private List<GhostContainer> ghosts;
	private int frameCounter, fpsWriterCounter, fps;
	private float vertLength, vertSpeed, vertValue, horLength, horSpeed, horValue, scaleMin, scaleMax, scaleSpeed, scaleValue;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private HashSet<Integer> pressedKeys;
	private Color currTint;
	
	/**
	 * Constructs an Engine.
	 * @param title The title of the game window.
	 * @param stage The stage to play.
	 * @param replay The replay to watch. If null is set, you will play the stage rather than watching a replay.
	 */
	public Engine(String title, Stage stage, List<PressedButtons> replay)
	{
		stage.game = this;
		this.stage = stage;
		elapsedTime = 0;
		state = GameState.ALIVE;
		autoTranslate = true;
		zoom = 1;
		ghosts  = new ArrayList<>(); 
		pressedKeys = new HashSet<>();
		currTint = new Color(DEFAULT_TINT);
		
		if(replay == null)
			this.replay = new LinkedList<>();
		else
		{
			this.replay = replay;
			playReplay = true;
		}
	}
	
	
	@Override
	public void render()
	{
		if((state == GameState.ALIVE || state == GameState.PAUSED) && isKeyPressed(Keys.ESCAPE))
			state = state == GameState.PAUSED ? GameState.ALIVE : GameState.PAUSED;
		
		if(state == GameState.PAUSED)
		{
			pressedKeys.clear();
			if(stage.music != null  && stage.music.getVolume() != .1f)
				stage.music.setVolume(.1f);
			
			batch.begin();
			renderPause();
			batch.end();
		}
		else
		{
			if(stage.music != null  && stage.music.getVolume() != Stage.MUSIC_VOLUME)
				stage.music.setVolume(Stage.MUSIC_VOLUME);
			
			update();
			paint();
		}
	}

	private void paint()
	{
		if(CLEAR_EACH_FRAME)
		{
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}
		
		if(autoTranslate)
		{
			float focusX = focus.currX + focus.width  / 2,
				  focusY = focus.currY + focus.height / 2;
			
			tx = Math.min(stage.width  - stage.visibleWidth,   Math.max(0, focusX - stage.visibleWidth  / 2)) + stage.visibleWidth  / 2; 
			ty = Math.min(stage.height - stage.visibleHeight,  Math.max(0, focusY - stage.visibleHeight / 2)) + stage.visibleHeight / 2;
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
		
		if(state == GameState.FINISH)
		{
			Utilities.fadeColor(currTint, WIN_TINT, .005f);
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
		
		camera.position.set(stage.visibleWidth / 2, stage.visibleHeight / 2, 0);
		camera.zoom = 1;
		camera.rotate(-angle);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		renderStatusBar();

		if(state == GameState.DEAD)
			TIME_FONT.draw(batch, "You are dead. Press 'R' to retry or 'E' to quit.", stage.visibleWidth / 2 - 300, stage.visibleHeight / 2);
		
		if(showFps)
		{
			if(++fpsWriterCounter % 5 == 0)
				fps = (int)(1.0f/Gdx.graphics.getDeltaTime());
			
			FPS_FONT.setColor(Color.WHITE);
			FPS_FONT.draw(batch, fps + " fps", stage.visibleWidth - 60, 5);
		}
			
		batch.end();
	}
	
	public void update()
	{
		DELTA_VALUE = (int) (Gdx.graphics.getDeltaTime() * 1000f);

		PressedButtons pb;
		
		if(playReplay)
			pb = replay.get(frameCounter++);
		else
		{
			pb = getPressedButtons(main.con);
			
			if(!Gdx.input.isKeyPressed(Keys.ESCAPE))
				replay.add(pb);
		}
		
		if(pb.suicide)
			setState(GameState.DEAD);

		if (state == GameState.DEAD)
		{
			main.deathAction();
			
			if(Gdx.input.isKeyPressed(Keys.R))
			{
				restart();
				stage.build();
			}
			else if(Gdx.input.isKeyPressed(Keys.E))
				System.exit(0);
		}
		else if(state == GameState.FINISH)
			winAction();
		
		main.prevX = main.currX;
		main.prevY = main.currY;
		if(state == GameState.ALIVE)
		{
			main.handleInput(pb);

			if(!justRestarted)
				elapsedTime += DELTA_VALUE;
			else
				justRestarted = false;
		}
		else if(state == GameState.FINISH)
			main.handleInput(STILL);
		
		for(GhostContainer ghost : ghosts)
		{
			ghost.character.prevX = ghost.character.currX;
			ghost.character.prevY = ghost.character.currY;
			ghost.character.handleInput((ghost.replay.length > ghost.counter + 1) ? ghost.replay[ghost.counter++] : STILL);
			
			ghost.character.runEvents();
			ghost.character.occupyingCells.clear();
			ghost.character.tileCheck();
			ghost.character.inspectIntersections();
		}
		
		stage.moveEnemies();
		stage.extra();

		main.occupyingCells.clear();
		main.tileCheck();
		main.inspectIntersections();

		SoundBank.FRAME_COUNTER++;
		pressedKeys.clear();
	}
	
	@Override
	public void create()  
	{
		TIME_FONT = new BitmapFont(Gdx.files.internal("res/data/sansserif32.fnt"), true);
		FPS_FONT  = new BitmapFont(Gdx.files.internal("res/data/cambria20.fnt"), true);
		
		TinySound.init();
		TinySound.setGlobalVolume(GAME_VOLUME);

		batch = new SpriteBatch();
		camera = new OrthographicCamera(stage.visibleWidth, stage.visibleHeight);
		camera.setToOrtho(true);

		Gdx.graphics.setDisplayMode(stage.visibleWidth, stage.visibleHeight, false);
		
		stage.welcome();
		stage.init();
		stage.build();
		
		main.currX = stage.startX;
		main.currY = stage.startY;
		
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
		TIME_FONT.dispose();
		FPS_FONT.dispose();
		
	}
	
	public OrthographicCamera getCamera()
	{
		return camera;
	}
	
	/**
	 * Returns the main character.
	 * @return The main character.
	 */
	public MainCharacter getMain()
	{
		return main;
	}
	
	/**
	 * Sets the main character.
	 * @param main The unit to use as the main character.
	 */
	public void setMainCharacter(MainCharacter main)
	{
		this.main = main;
	}
	
	/**
	 * Returns the current state of the game.
	 * @return The current state of the game.
	 */
	public GameState getState()
	{
		return state;
	}
	
	/**
	 * In this function can you manipulate the state of the game. You can for example pause the game, end it etc.
	 * Function is ignored if you are dead or have have finished the stage.
	 * @param state The state the game should be changed to.
	 */
	public void setState(GameState state)
	{
		if(this.state != GameState.FINISH && this.state != GameState.DEAD)
		{
			this.state = state;

			if(this.state == GameState.DEAD)
				saveReplay("Loser");
		}
	}
	
	/**
	 * This function allow you to determine which {@code GameObject} the game should focus on. <br>
	 * The screen will follow the specified {@code GameObject} whenever it moves. <br>
	 * This is usual set to the main character.<br><br>
	 * 
	 * This function will only work if {@code autoTranslate} is set to true.
	 * @param focus The {@code GameObject} to follow.
	 */
	public void setFocusObject(GameObject focus)
	{
		this.focus = focus;
	}

	/**
	 * Returns the focus object.
	 * @return The focus object.
	 */
	public GameObject getFocusObject()
	{
		return focus;
	}
	
	/**
	 * True by default and specifies if the engine should translate the matrix for you.
	 * @param genVars True if you want the engine to handle the translation for you. False if you wish to translate yourself.
	 */
	public void autoTranslate(boolean genVars)
	{
		this.autoTranslate = genVars;
	}

	/**
	 * Appends the specified ghost to the game.
	 * @param character The {@code GameObject} to be used as a ghost.
	 * @param replay The replay data this ghost should use for its movement.
	 */
	public void addGhost(MainCharacter character, PressedButtons[] replay)
	{
		GhostContainer rd = new GhostContainer();
		rd.character = character;
		rd.replay = replay;
		
		ghosts.add(rd);
		stage.add(character);
	}
	
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
	
	public void showFps(boolean showFps)
	{
		this.showFps = showFps;
	}
	
	@Override
	public void pause() {}
	
	@Override
	public void resize(int x, int y) {}
	
	@Override
	public void resume() {}

	/**
	 * The amount of milliseconds since the last frame.
	 * @return The millis.
	 */
	public static int getDelta()
	{
		return DELTA_VALUE;
	}
	
	void clearGhosts()
	{
		ghosts.clear();
	}
	
	private void restart()
	{
		justRestarted =  increasingScale = true;
		showingDialog = false;
		if(!playReplay)
			replay.clear();
		frameCounter = 0;
		state = GameState.ALIVE;
		horValue = vertValue = DELTA_VALUE = 0;
		batch.setColor(DEFAULT_TINT);
		currTint = new Color(DEFAULT_TINT);
	}
	
	private void renderStatusBar()
	{
		if(state == GameState.PAUSED)
			TIME_FONT.setColor(Color.WHITE);
		else
			TIME_FONT.setColor(TIME_COLOR);
		TIME_FONT.draw(batch, String.valueOf((double)elapsedTime/1000), 10, 10);
		
		int hp = main.getHP();
		if(LIFE_IMAGE != null && state != GameState.DEAD)
		{
			final int width = (int) (LIFE_IMAGE.getWidth() + 2);
			for(int i = 0, posX = 10; i < hp; i++, posX += width)
				batch.draw(LIFE_IMAGE, posX, 40);
		}
	}
	
	private void drawObject(GameObject go)
	{
		Image2D img = go.getFrame();
		if (img != null && go.visible)
			batch.draw(img, go.currX + go.offsetX, go.currY + go.offsetY, img.getWidth() / 2,img.getHeight() / 2, go.width * go.scale, go.height * go.scale, 1, 1, go.rotation);
	}
	
	private void saveReplay(String playername)
	{
		if(!playReplay && SAVE_REPLAYS)
		{
			PressedButtons[] replayData = new PressedButtons[replay.size()];
			for(int i = 0; i < replayData.length; i++)
				replayData[i] = replay.get(i);

			HighScore hs = new HighScore();
			hs.replay = replayData;
			hs.meta = stage.getMeta();
			hs.name = playername;
			hs.stageName = stage.name;
			hs.time = (double)elapsedTime/1000;
			hs.date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
			hs.className = stage.getClass();
			hs.result = state == GameState.DEAD ? "Death" : "Victorious";
			
			Utilities.exportObject(hs, "replays/" + stage.name + " " + playername + " " + hs.result + " " + hs.time + " sec " + hs.date + ".hs");
		}
	}	
	
	private void winAction()
	{
		if(playReplay || showingDialog)
			return;

		showingDialog = true;
		
		final JDialog d = new JDialog();
		d.setTitle("Success!");

		final JTextField textField = new JTextField(10);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 1));
		panel.add(new JLabel("Congratulations!"));
		panel.add(new JLabel("It took you " + (double)elapsedTime/1000 + " seconds to finish the stage."));
		panel.add(new JLabel("Enter your name to save your stats."));			
		panel.add(textField);
		
		JButton retryButton = new JButton("Retry");
		retryButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) 
			{
				d.dispose();
				String input = textField.getText();
				String playerName = (input == null || input.isEmpty()) ? "Player" : input;
				
				saveReplay(playerName);
				stage.build();
				restart();
			}
		});
		JButton menuButton = new JButton("Return To Menu");
		menuButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) 
			{
				d.dispose();
				String input = textField.getText();
				String playerName = (input == null || input.isEmpty()) ? "Player" : input;

				saveReplay(playerName);
				Gdx.app.exit();
			}
		});
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1, 2));
		panel2.add(retryButton);
		panel2.add(menuButton);

		d.add(panel, "Center");
		d.add(panel2, "South");
		d.pack();
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setLocationRelativeTo(null);
		d.setModal(false);
		d.setVisible(true);		
	}

	private void renderPause()
	{
		Gdx.gl.glClearColor(0, 0, 0, .4f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		TIME_FONT.setColor(Color.WHITE);
		TIME_FONT.draw(batch, "Game is paused.", stage.visibleWidth / 2 - 120, stage.visibleHeight / 2);
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
	
	private PressedButtons getPressedButtons(Controller con)
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
	
	private static class GhostContainer
	{
		MainCharacter character;
		PressedButtons[] replay;
		int counter;
	}

}