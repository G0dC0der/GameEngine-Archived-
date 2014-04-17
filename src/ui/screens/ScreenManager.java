package ui.screens;

import game.core.Engine;
import game.core.GameObject.Event;
import game.core.Stage;
import game.essentials.HighScore;
import java.util.Arrays;
import ui.accessories.GameSettings;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ScreenManager extends Game
{
	enum Task
	{
		OPEN_MENU,
		OPEN_CONTROLLER_CREATOR,
		OPEN_STAGE_CHOOSER,
		OPEN_STATS
	}
	
	private Screen splash, menu, controllerCreator, statsScreen, selectStage;
	
	@Override
	public void create() 
	{
		splash = new Splash(this);
		menu = new MainMenu(this);
		controllerCreator = new ControllerCreator(this);
		statsScreen = new Stats(this);
		selectStage = new SelectStage(this);
		
		setScreen(splash);
	}
	
	/**
	 * Called by a {@code Screen} when its done.
	 * @param task What to do next.
	 */
	public void nextTask(Task task)
	{
		switch(task)
		{
			case OPEN_MENU:
				setScreen(menu);
				break;
			case OPEN_CONTROLLER_CREATOR:
				setScreen(controllerCreator);
				break;
			case OPEN_STATS:
				setScreen(statsScreen);
				break;
			case OPEN_STAGE_CHOOSER:
				setScreen(selectStage);
				break;
		}
	}
	
	public void startGame(Stage stage, HighScore replay)
	{
		GameSettings settings = new GameSettings();
		settings.loadSettings("res/data/game.ini");
		
		if(replay != null)
			stage.setMeta(replay.meta);
		
		Engine engine = new Engine("Pojahns Game Engine", stage, replay == null ? null : Arrays.asList(replay.replay));
		engine.clearEachFrame = settings.clearEachFrame;
		engine.showFps(settings.showFps);
		engine.streamSounds = settings.streamSounds;
		engine.saveReplays = settings.saveReplays;
		engine.gameVolume = settings.volume;
		engine.setExitEvent(new Event()
		{
			@Override
			public void eventHandling() 
			{
				setScreen(menu);
			}
		});
		
		setScreen(engine);
	}
	
	@Override
	public void setScreen(Screen screen) 
	{
		super.setScreen(screen);
		System.gc();
	}
}
