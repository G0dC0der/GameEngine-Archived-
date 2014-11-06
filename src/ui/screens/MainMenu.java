package ui.screens;

import game.essentials.Utilities;
import ui.screens.ScreenManager.Task;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class MainMenu implements Screen
{
	private Stage stage;
	private SpriteBatch batch;
	private ScreenManager manager;
	private Texture background;
	private Skin skin;
	
	public MainMenu(ScreenManager manager)
	{
		this.manager = manager;
	}
	
	@Override
	public void dispose() 
	{
		Utilities.dispose(stage);
		Utilities.dispose(batch);
		Utilities.dispose(background);
		Utilities.dispose(skin);
	}

	@Override
	public void hide() 
	{
		dispose();
	}

	@Override
	public void show() 
	{
		stage = new Stage(new ScalingViewport(Scaling.none, 800, 600));
		Gdx.input.setInputProcessor(stage);
		skin = new Skin(Gdx.files.internal("res/data/uiskin.json"));
		
		batch = new SpriteBatch();
		background = new Texture(Gdx.files.internal("res/data/background.png"));

		TextButton selectStage = new TextButton("Select Stage", skin);
		selectStage.setPosition(330, 400);
		selectStage.setSize(140, 43);
		selectStage.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y) 
			{
				super.clicked(event, x, y);
				manager.nextTask(Task.OPEN_STAGE_CHOOSER);
			}
		});
		
		TextButton stats = new TextButton("Highscores", skin);
		stats.setPosition(330, 350);
		stats.setSize(140, 43);
		stats.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y) 
			{
				super.clicked(event, x, y);
				manager.nextTask(Task.OPEN_STATS);
			}
		});
		
		TextButton controllerCreator = new TextButton("Create Controller", skin);
		controllerCreator.setPosition(330, 300);
		controllerCreator.setSize(140, 43);
		controllerCreator.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y) 
			{
				super.clicked(event, x, y);
				manager.nextTask(Task.OPEN_CONTROLLER_CREATOR);
			}
		});
		
		TextButton about = new TextButton("About", skin);
		about.setPosition(330, 250);
		about.setSize(140, 43);
		about.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y) 
			{
				super.clicked(event, x, y);
				new Dialog("Pojahn's Game Engine", skin)
				{
					{
						text("Pojahn's Game Engine\nVersion: 2.0\nDeveloped by Pojahn Moradi 2012-2014.");
						button("Ok");
						
					}
				}.show(stage);
			}
		});
		
		TextButton exit = new TextButton("Exit", skin);
		exit.setPosition(330, 200);
		exit.setSize(140, 43);
		exit.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y) 
			{
				super.clicked(event, x, y);
				Gdx.app.exit();
			}
		});
			
		stage.addActor(selectStage);
		stage.addActor(stats);
		stage.addActor(controllerCreator);
		stage.addActor(about);
		stage.addActor(exit);
	}

	@Override
	public void render(float delta) 
	{
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(background, 0, 0);
		batch.end();
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int x, int y) {}

	@Override
	public void resume() {}

	@Override
	public void pause() {}
}
