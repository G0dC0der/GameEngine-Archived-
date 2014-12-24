package ui.screens;

import game.core.Stage.Difficulty;
import game.essentials.HighScore;
import game.essentials.Utilities;

import java.util.ArrayList;
import java.util.Collections;

import ui.accessories.Playable;
import ui.accessories.StageReader;
import ui.screens.ScreenManager.Task;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SelectStage implements Screen
{
	private ScreenManager manager;
	private Stage stage;
	private Skin skin;
	private ArrayList<StageHolder> stages;
	private java.util.List<HighScore> highscores;
	private List<ReplayHolder> replayList;
	private TextButton play;
	private SpriteBatch batch;
	private Texture background;
	
	public SelectStage(ScreenManager manager)
	{
		this.manager = manager;
	}
	
	@Override
	public void show() 
	{
		batch = new SpriteBatch(5);
		background = new Texture(Gdx.files.internal("res/data/selectback.png"));
		
		highscores = Utilities.readAllHighScores();
		Collections.sort(highscores, HighScore.TIME_SORT);
		Collections.reverse(highscores);
		
		java.util.List<Class<? extends game.core.Stage>> stgs = StageReader.loadAll();
		stages = new ArrayList<>(stgs.size());
		for(Class<?> clazz : stgs)
		{
			StageHolder ch = new StageHolder();
			ch.clazz = clazz;
			stages.add(ch);
		}
		
		
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		
		skin = new Skin(Gdx.files.internal("res/data/uiskin3.json"));

		Table container = new Table(skin);
		stage.addActor(container);
		container.setFillParent(true);
		
		final TextArea stageDesc = new TextArea("HEllo",skin);
		stageDesc.setDisabled(true);
		Playable p = StageReader.getPlayable(stages.get(0).clazz);
		stageDesc.setText(p.description());
		ScrollPane scroll3 = new ScrollPane(stageDesc, skin);
		scroll3.setFadeScrollBars(false);
		scroll3.layout();
		
		final List<StageHolder> stageList = new List<>(skin);
		stageList.addCaptureListener(new EventListener()
		{
			@Override
			public boolean handle(Event e) 
			{
				if(e.toString().equals("exit"))
				{
					Class<?> clazz = stageList.getSelected().clazz;
					initReplays(clazz);
					play.setText("Play");
					
					Playable p = StageReader.getPlayable(clazz);
					stageDesc.setText(p.description());
				}
				return false;
			}
		});
		ScrollPane scroll = new ScrollPane(stageList, skin);
		scroll.setFadeScrollBars(false);
		scroll.layout();
		
		replayList = new List<>(skin);
		replayList.addCaptureListener(new EventListener()
		{
			@Override
			public boolean handle(Event e) 
			{
				if(e.toString().equals("exit"))
				{
					HighScore hs = replayList.getSelected().hs;
					if(hs == null)
						play.setText("Play");
					else
						play.setText("Watch");
				}
				return false;
			}
		});
		ScrollPane scroll2 = new ScrollPane(replayList, skin);
		scroll2.setFadeScrollBars(false);
		scroll2.layout();
		
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
		
		play = new TextButton("Play", skin);
		play.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y) 
			{
				super.clicked(event, x, y);
				final game.core.Stage theStage = initStage(stageList);
				final HighScore hs = replayList.getSelected().hs;
				
				if(hs == null)
				{
					if(theStage.getDifficulty() != null)
					{
						new Dialog("Level Options", skin)
						{
							SelectBox<String>  sortBy;
							
							{
								sortBy = new SelectBox<>(skin);
								sortBy.setItems("Easy","Normal","Hard");
								sortBy.setZIndex(100);
								sortBy.setColor(Color.WHITE);
								
								text("Please choose the difficulty of the stage.");
								getContentTable().row();
								getContentTable().add(sortBy);
								button("Ok", "ok");
								setModal(true);
							}
							
							protected void result(Object object) 
							{
								if(object.equals("ok"))
								{
									Difficulty d = null;
									String sel = sortBy.getSelected();
									if(sel.equals("Easy"))
										d = Difficulty.EASY;
									else if(sel.equals("Normal"))
										d = Difficulty.NORMAL;
									else if(sel.equals("Hard"))
										d = Difficulty.HARD;
									
									theStage.setDifficulty(d);
									manager.startGame(theStage, null);
								}
							}
						}.show(stage);
					}
					else
						manager.startGame(theStage, hs);
				}
				else
				{
					theStage.setDifficulty(hs.difficulty);
					manager.startGame(theStage, hs);
				}
			}
		});
		
		container.add("Select Stage:");
		container.add("Select Replay:");
		container.add("Stage Description:");
		container.row();
		container.add(scroll).size(240, 340);
		container.add(scroll2).size(240, 340).padLeft(20);
		container.add(scroll3).size(240, 340).padLeft(20);
		container.row(); 
		container.add(" ");
		container.row(); 
		
		Table buttonContainer = new Table();
		buttonContainer.add(play).width(60).padRight(5);
		buttonContainer.add(goBack).width(60).padLeft(5);
		
		container.add(" ");
		container.add(buttonContainer);
		container.add(" ");
		
		stage.addActor(container);
		
		Array<StageHolder> arr = new Array<>();
		
		for(StageHolder ch : stages)
		{
			arr.add(ch);
		}
		stageList.setItems(arr);
		
		initReplays(stages.get(0).clazz);
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
		stage.dispose();
		skin.dispose();
		batch.dispose();
		background.dispose();
		batch = null;
		background = null;
		stage = null;
		skin = null;
		stages = null;
		replayList = null;
	}

	@Override
	public void hide() 
	{
		dispose();
	}
	
	private game.core.Stage initStage(List<StageHolder> stageList)
	{
		try 
		{
			return (game.core.Stage)stageList.getSelected().clazz.newInstance();
		} 
		catch (InstantiationException | IllegalAccessException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private void initReplays(Class<?> stage)
	{
		Array<ReplayHolder> stageScores = new Array<>();
		stageScores.add(new ReplayHolder()
		{
			@Override
			public String toString()
			{
				return "- NONE -";
			}
		});
		
		for(HighScore hs : highscores)
			if(hs.className == stage && hs.result.equals("Victorious"))
			{
				ReplayHolder rh = new ReplayHolder();
				rh.hs = hs;
				stageScores.add(rh);
			}
		
		replayList.setItems(stageScores);
	}

	@Override public void pause() {}
	@Override public void resize(int x, int u) {}
	@Override public void resume() {}
	
	private static class StageHolder
	{
		Class<?> clazz;
		
		@Override
		public String toString()
		{
			Playable p = StageReader.getPlayable(clazz);
			return p.name();
		}
	}
	
	private static class ReplayHolder
	{
		HighScore hs;
		
		@Override
		public String toString()
		{
			return hs.name + " " + hs.time + " sec " + ((hs.difficulty != null) ? hs.difficulty.toString() : "");
		}
	}
}
