package ui.screens;

import game.essentials.HighScore;
import game.essentials.Utilities;
import java.util.List;
import ui.screens.ScreenManager.Task;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Stats implements Screen
{
	private ScreenManager manager;
	private List<HighScore> highScores;
	private Skin skin;
	private Stage stage;
	private Table container, table;
	private BitmapFont font;
	private Label view;
	private SpriteBatch batch;
	private Texture background;
	
	public Stats(ScreenManager manager)
	{
		this.manager = manager;
	}

	@Override
	public void show() 
	{
		highScores = Utilities.readAllHighScores();
		
		stage = new Stage(800, 600, false);
		Gdx.input.setInputProcessor(stage);
		Gdx.graphics.setVSync(false);
		
		batch = new SpriteBatch(1);
		background = new Texture("res/data/scores.png");

		container = new Table();
		stage.addActor(container);
		container.setFillParent(true);
		
		skin = new Skin(Gdx.files.internal("res/data/uiskin.json"));
		
		table = new Table(skin);
		setColumns();

		final ScrollPane scroll = new ScrollPane(table, skin);
		scroll.setFadeScrollBars(false);
		scroll.layout();

		view = new Label("View", skin);
		view.setColor(Color.BLUE);
		setTableElements();

		TextButton goBack = new TextButton("Return", skin);
		goBack.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y) 
			{
				super.clicked(event, x, y);
				manager.nextTask(Task.OPEN_MENU);
			}
		});
		
		TextButton refresh = new TextButton("Refresh", skin);
		refresh.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y) 
			{
				super.clicked(event, x, y);
				highScores = Utilities.readAllHighScores();
				table.clear();
				setColumns();
				setTableElements();
			}
		});
		
		font = new BitmapFont(Gdx.files.internal("res/data/cambria20.fnt"));
		LabelStyle style = new LabelStyle();
		style.font = font;
		
		Label label = new Label("Highscores", style);
		
		container.add(label).padTop(-100);
		container.row();
		container.add(scroll).size(650, 400);
		container.row();
		
		Table buttonTable = new Table(skin);
		buttonTable.add(goBack).width(80);
		buttonTable.add(" ").width(5);
		buttonTable.add(refresh).width(80);
		container.add(buttonTable).padBottom(-100);
	}
	
	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		batch.draw(background, 0, 0);
		batch.end();
		
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void dispose() 
	{
		highScores = null;
		Utilities.dispose(stage);
		Utilities.dispose(skin);
		Utilities.dispose(font);
		Utilities.dispose(batch);
		Utilities.dispose(background);
	}

	@Override
	public void hide() 
	{
		dispose();
	}
	
	private void setColumns()
	{		
		table.add(" Player Name").width(120);
		table.add(" Stage").width(125);
		table.add(" Time").width(100);
		table.add(" Date").width(100);
		table.add(" Result").width(100);
		table.add(" Replay").width(50);
		table.row();
		table.add(" ");
		table.row();
	}
	
	private void setTableElements()
	{
		for(final HighScore hs : highScores)
		{
			table.add(" " + hs.name).width(120);
			table.add(" " + hs.stageName).width(125);
			table.add(" " + hs.time + " sec").width(100);
			table.add(" " + hs.date).width(100);
			table.add(" " + hs.result).width(100);
			TextButton viewButton = new TextButton("Watch",skin);
			viewButton.addListener(new ClickListener()
			{
				@Override
				public void clicked(InputEvent event, float x, float y) 
				{
					super.clicked(event, x, y);
					
				}
			});
			table.add(viewButton).width(50).height(20);
			table.row();
		}
	}

	@Override
	public void pause() {}

	@Override
	public void resize(int x, int y) {}

	@Override
	public void resume() {}
}