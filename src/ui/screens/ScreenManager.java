package ui.screens;

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
	
	private Screen splash, menu, controllerCreator, statsScreen;
	
	@Override
	public void create() 
	{
		splash = new Splash(this);
		menu = new MainMenu(this);
		controllerCreator = new ControllerCreator(this);
		statsScreen = new Stats(this);
		
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
				
				break;
		}
	}
}
