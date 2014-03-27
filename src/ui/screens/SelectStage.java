package ui.screens;

import com.badlogic.gdx.Screen;

public class SelectStage implements Screen
{
	private ScreenManager manager;
	
	public SelectStage(ScreenManager manager)
	{
		this.manager = manager;
	}
	
	@Override
	public void show() 
	{
		
	}

	@Override
	public void render(float delta) 
	{
		
	}
	
	@Override
	public void dispose() 
	{
		
	}

	@Override
	public void hide() 
	{
		dispose();
	}

	@Override public void pause() {}
	@Override public void resize(int x, int u) {}
	@Override public void resume() {}
}
