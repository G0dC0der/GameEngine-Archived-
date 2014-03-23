package ui;

import ui.accessories.GameSettings;
import ui.screens.ScreenManager;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class TheMainClass 
{
	public static void main(String... args) 
	{
		GameSettings settings = new GameSettings();
		settings.loadSettings("res/data/game.ini");
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Pojahns Game Engine";
		cfg.width = 800;
		cfg.height = 600;
		cfg.vSyncEnabled = settings.vsync; 
		cfg.resizable = false;
		cfg.useGL30 = true; 
		cfg.backgroundFPS = settings.fps;
		cfg.foregroundFPS = settings.fps;
		cfg.addIcon("res/data/icon128x128.png", FileType.Internal);
		cfg.addIcon("res/data/icon32x32.png", FileType.Internal);  
		cfg.addIcon("res/data/icon16x16.png", FileType.Internal);
		
		new LwjglApplication(new ScreenManager(), cfg); 
	}	
}
