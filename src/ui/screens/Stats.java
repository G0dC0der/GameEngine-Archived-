package ui.screens;

import game.essentials.HighScore;
import game.essentials.Utilities;

import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import ui.accessories.StageReader;
import ui.screens.ScreenManager.Task;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

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
	private SelectBox<String> sortBy;
	private CheckBox viewFailBox, ascendingBox;
	
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
		
		viewFailBox = new CheckBox(" List Failures ", skin);
		viewFailBox.setChecked(true);
		ascendingBox = new CheckBox(" Ascending Order", skin);
		ascendingBox.setChecked(true);
		
		sortBy = new SelectBox<>(skin);
		sortBy.setItems("Player","Stage","Time","Date");
		sortBy.setZIndex(100);
		sortBy.setColor(Color.WHITE);
		
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
		
		TextButton refresh = new TextButton("Apply", skin);
		refresh.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y) 
			{
				super.clicked(event, x, y);
//				highScores = Utilities.readAllHighScores();		//Disable for now.
				table.clear();
				setColumns();
				setTableElements();
			}
		});
		
		font = new BitmapFont(Gdx.files.internal("res/data/cambria20.fnt"));
		LabelStyle style = new LabelStyle();
		style.font = font;
		
		container.add(new Label("Highscores", style));
		container.row();
		container.add(scroll).size(650, 400).padTop(40);
		container.row();
		
		Pixmap dot = new Pixmap(1, 1, Format.RGBA8888);
		dot.setColor(0x00000088);
		dot.fill();
		
		Sprite dotImg = new Sprite(new Texture(dot));
		dot.dispose();
		
		SelectBoxStyle dropdownStyle = sortBy.getStyle();
		dropdownStyle.listStyle.background = new SpriteDrawable(dotImg);
		
		Table filterTable = new Table(skin);
		filterTable.add("Sort By:").width(-10);
		filterTable.add(sortBy).width(85);
		filterTable.row();
		filterTable.add(viewFailBox);
		filterTable.add(ascendingBox);
		filterTable.row().padTop(10);

		filterTable.add(refresh).width(80).padRight(-42);
		filterTable.add(goBack).width(80).padLeft(-16);
		
		container.add(filterTable).padTop(12);
		
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
		String value = sortBy.getSelected();
		switch (value)
		{
			case "Player":
				Collections.sort(highScores, HighScore.NAME_SORT);
				break;
			case "Stage":
				Collections.sort(highScores, HighScore.STAGE_SORT);
				break;
			case "Time":
				Collections.sort(highScores, HighScore.TIME_SORT);
				break;
			case "Date":
				Collections.sort(highScores, HighScore.DATE_SORT);
				break;
			default:
				break;
		}
		
		if(ascendingBox.isChecked())
			Collections.reverse(highScores);
		
		boolean viewFail = viewFailBox.isChecked();
		for(final HighScore hs : highScores)
		{
			if(viewFail || (!viewFail && hs.result.equals("Victorious")))
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
						if(hs.className == null || hs.replay == null)
							JOptionPane.showMessageDialog(null, "Corrupted or incomplete highscore file.", "Runtime Error", JOptionPane.ERROR_MESSAGE);
						else if(!StageReader.AVAILABLE_STAGE.contains(hs.className))
							JOptionPane.showMessageDialog(null, "Stage \"" + hs.stageName + "\" do not exist.", "Runtime Error", JOptionPane.ERROR_MESSAGE);
						else
						{
							try 
							{
								manager.startGame((game.core.Stage)hs.className.newInstance(), hs);
							} 
							catch (InstantiationException | IllegalAccessException e) 
							{
								e.printStackTrace();
							}
						}
					}
				});
				table.add(viewButton).width(50).height(20);
				table.row();
			}
		}
	}

	@Override
	public void pause() {}

	@Override
	public void resize(int x, int y) {}

	@Override
	public void resume() {}
}