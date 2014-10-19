package game.essentials;

import game.core.Engine;
import game.core.Stage;

/**
 * An event that handle the camera manipulations. <br>
 * The camera event should be added to the stage or run manually through the {@code extra} method if one or more focus objects are set.
 * @author Pojahn Moradi
 */
public interface CameraEffect
{	
	/**
	 * Update Engine.tx, Engine.ty, Engine.rotation and/or Engine.scale.
	 */
	void update();

	/**
	 * Stops the effect.
	 */
	void stop();
	
	/**
	 * Whether or not if this event is done and can be discarded.
	 * @return True if the event is done.
	 */
	boolean isDone();
	
	/**
	 * Returns an {@code CameraEffect} event that moves vertically in a pingpong motion.<br>
	 * The function {@code isDone()} returns true either when the duration have elapsed or the event is manually stopped with the {@code stop()} function.
	 * @param length The length of the vertical movement.
	 * @param speed The speed of the vertical movement.
	 * @param duration The amount of frames the event is active. Negative values for infinite.
	 * @return The event.
	 */
	public static CameraEffect verticalMovement(float length, float speed, int duration)
	{
		return pingPongMovement(length, speed, duration, 0);
	}

	/**
	 * Returns an {@code CameraEffect} event that moves horizontally in a pingpong motion.
	 * <br>
	 * The function {@code isDone()} returns true either when the duration have elapsed or the event is manually stopped with the {@code stop()} function.
	 * @param length The length of the horizontal movement.
	 * @param speed The speed of the horizontal movement.
	 * @param duration The amount of frames the event is active. Negative values for infinite.
	 * @return The event.
	 */
	public static CameraEffect horizontalMovement(float length, float speed, int duration)
	{
		return pingPongMovement(length, speed, duration, 1);
	}
	
	/**
	 * Returns an {@code CameraEffect} event that zooms in and out in a pingpong motion with the given speed.<br>
	 * The zoom factor defaults to 1.0, which is 100%. Increasing the values zooms out rather than zooming in.<br>
	 * The function {@code isDone()} returns true either when the duration have elapsed or the event is manually stopped with the {@code stop()} function.
	 * @param min The minimum zoom. 
	 * @param max The maximum zoom.
	 * @param speed The speed of the pingpong motion.
	 * @param duration The amount of frames the event is active. Negative values for infinite.
	 * @return The event.
	 */
	public static CameraEffect zoomEffect(float min, float max, float speed, int duration)
	{
		if(0 > min || 0 > max || 0 > speed)
			throw new IllegalArgumentException("All values must be positive.");
		
		return new CameraEffect()
		{
			float scaleValue = Stage.getCurrentStage().game.zoom;
			boolean stopped, increasingScale;
			int active;
			
			@Override
			public void update() 
			{
				if(duration > 0)
					active++;
				
				if(!isDone())
				{
					if(increasingScale)
					{
						scaleValue += speed;
						if(scaleValue > max)
							increasingScale = false;
					}
					else
					{
						scaleValue -= speed;
						if(scaleValue < min)
							increasingScale = true;
					}
					
					Stage.getCurrentStage().game.zoom = scaleValue;
				}
			}
			
			@Override
			public void stop() 
			{
				stopped = true;
			}
			
			@Override
			public boolean isDone() 
			{
				return stopped || (active >= duration && duration > 0);
			}
		};
	}
	
	/**
	 * Vibrates the screen for the specified amount of frames.<br>
	 * The function {@code isDone()} returns true either when the duration have elapsed or the event is manually stopped with the {@code stop()} function.
	 * @param strength The strength of the vibration.
	 * @param duration The duration of the vibration. Negative values for infinite.
	 * @return The event.
	 */
	public static CameraEffect vibration(float strength, int duration)
	{
		return new CameraEffect()
		{
			private Engine game = Stage.getCurrentStage().game;
			boolean stopped;
			int active, counter;
			
			@Override
			public void update() 
			{
				if(duration > 0)
					active++;
				
				if(!isDone())
				{
					int value = counter++ % 4;
					
					switch(value)
					{
						case 0:
							game.tx += -strength;
							game.ty += -strength;
							break;
						case 1:
							game.tx += strength;
							game.ty += -strength;
							break;
						case 2:
							game.tx += strength;
							game.ty += strength;
							break;
						case 3:
							game.tx -= strength;
							game.ty += strength;
							break;
					}
				}
			}
			
			@Override
			public void stop() 
			{
				stopped = true;
			}
			
			@Override
			public boolean isDone() 
			{
				return stopped || (active >= duration && duration > 0);
			}
		};
	}
	
	static CameraEffect pingPongMovement(float length, float speed, int duration, int axis)
	{
		if(0 > length || 0 > speed)
			throw new IllegalArgumentException("Both values must be positive.");
		
		return new CameraEffect() 
		{
			boolean stopped, increasingVert;
			float vertValue, vertLength, vertSpeed;
			int active;
			
			{
				if(speed == 0)
					vertValue = 0;
				
				vertLength = length;
				vertSpeed = speed;
			}
			
			@Override
			public void update() 
			{
				if(duration > 0)
					active++;
				
				if(!isDone() && vertSpeed > 0)
				{
					if(increasingVert)
					{
						vertValue += vertSpeed;
						if(vertValue > vertLength)
							increasingVert = false;
					}
					else
					{
						vertValue -= vertSpeed;
						if(vertValue < -vertLength)
							increasingVert = true;
					}
					
					if(axis == 0)
						Stage.getCurrentStage().game.ty += vertValue;
					else if(axis == 1)
						Stage.getCurrentStage().game.tx += vertValue;
				}
			}
			
			@Override
			public void stop() 
			{
				stopped = true;
			}

			@Override
			public boolean isDone() 
			{
				return stopped || (active >= duration && duration > 0);
			}
		};
	}
}