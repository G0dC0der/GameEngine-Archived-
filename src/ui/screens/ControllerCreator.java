package ui.screens;

import game.essentials.Controller;
import game.essentials.Utilities;

import java.util.ArrayList;

import ui.screens.ScreenManager.Task;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ControllerCreator extends InputAdapter implements Screen
{
	private ScreenManager manager;
	private ArrayList<Integer> pressedKeys;
	private SpriteBatch batch;
	private BitmapFont font;
	private Controller con;
	private int counter;
	
	public ControllerCreator(ScreenManager manager)
	{
		this.manager = manager;
	}
	
	@Override
	public void show() 
	{
		Gdx.input.setInputProcessor(this);
		pressedKeys = new ArrayList<>();
		batch = new SpriteBatch(1);
		font = new BitmapFont(Gdx.files.internal("res/data/cambria20.fnt"), false);
		con = new Controller();
	}

	@Override
	public void render(float delta) 
	{
		String text = counter == 9 ? "Press '1' to save the controller for player 1, '2' for player 2, '3' for player 3 and '4' for player 4." :
									 "Press the key you want to use " + getKey() + ".";
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		if(counter == 9)
			font.drawWrapped(batch, text, 190, 300, 500);
		else
			font.draw(batch, text, 190, 300);
		font.setScale(.75f);
		font.draw(batch, "Press ESQ to return to the main menu", 0, 595);
		font.setScale(1);
		batch.end();
		
		if(!pressedKeys.isEmpty())
		{
			switch(counter)
			{
				case 0:
					con.left = pressedKeys.get(0);
					break;
				case 1:
					con.right = pressedKeys.get(0);
					break;
				case 2:
					con.down = pressedKeys.get(0);
					break;
				case 3:
					con.up = pressedKeys.get(0);
					break;
				case 4:
					con.suicide = pressedKeys.get(0);
					break;
				case 5:
					con.switchChar = pressedKeys.get(0);
					break;
				case 6:
					con.special1 = pressedKeys.get(0);
					break;
				case 7:
					con.special2 = pressedKeys.get(0);
					break;
				case 8:
					con.special3 = pressedKeys.get(0);
					break;
				case 9:
					int key = pressedKeys.get(0);
					String playerId = null;
					
					switch(key)
					{
						case Keys.NUM_1:
							playerId = "1";
							break;
						case Keys.NUM_2:
							playerId = "2";
							break;
						case Keys.NUM_3:
							playerId = "3";
							break;
						case Keys.NUM_4:
							playerId = "4";
							break;
						default:
							counter--;
					}
					if(playerId != null)
						Utilities.exportObject(con, "res/data/controller" + playerId + ".con");
					break;
			}
			counter++;
		}
		
		pressedKeys.clear();
		if(counter > 9)
			manager.nextTask(Task.OPEN_MENU);
	}
	
	@Override
	public void dispose() 
	{
		Utilities.dispose(batch);
		Utilities.dispose(font);
	}

	@Override
	public void hide() 
	{
		dispose();
	}

	@Override
	public void pause() {}

	@Override
	public void resize(int x, int y) {}

	@Override
	public void resume() {}

	@Override
	public boolean keyDown(int key) 
	{
		if(key == Keys.ESCAPE)
			manager.nextTask(Task.OPEN_MENU);
		
		if(!pressedKeys.contains(key))
			pressedKeys.add(key);
		return false;
	}
	
	private String getKey()
	{
		switch(counter)
		{
			case 0:
				return "for moving left";
			case 1:
				return "for moving right";
			case 2:
				return "for moving down";
			case 3:
				return "for moving up(jumping)";
			case 4:
				return "to suicide";
			case 5:
				return "to switch character";
			case 6:
				return "for special 1";
			case 7:
				return "for special 2";
			case 8:
				return "for special 3";
			default:
				return "";
		}
	}
}