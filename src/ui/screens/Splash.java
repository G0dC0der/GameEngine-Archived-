package ui.screens;

import ui.screens.ScreenManager.Task;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Splash implements Screen
{
	private ScreenManager manager;
	private SpriteBatch batch;
	private Texture splashImage;
	private float x = 800, time;
	
	public Splash(ScreenManager manager)
	{
		this.manager = manager;
	}
	
	@Override
	public void dispose() 
	{
		batch.dispose();
		splashImage.dispose();
	}

	@Override
	public void hide() 
	{
		dispose();
	}

	@Override
	public void show() 
	{
		splashImage = new Texture(new FileHandle("res/data/splash.png"));
		batch = new SpriteBatch();
	}

	@Override
	public void render(float delta) 
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(x != 0)
			x-=10;
		
		time += delta;
		
		batch.begin();
		batch.draw(splashImage, x, 0);
		batch.end();
		
		if(time > 1f)//3f)
			manager.nextTask(Task.OPEN_MENU);
	}

	@Override
	public void resize(int x, int y) {}

	@Override
	public void resume() {}

	@Override
	public void pause() {}
}
