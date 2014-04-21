package game.essentials;

import static game.core.Engine.*;
import game.core.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Disposable;

/**
 * This class contains a collection of static method to ease sound/image loading, color- and stage data creations etc.
 * @author Pojahn Moradi
 *
 */
public class Utilities
{
	/**
	 * Creates stage data from the given image.
	 * @param img The image to use as source.
	 * @return The stage data.
	 * @throws IOException 
	 */
	public static byte[][] createStageData(Pixmap img)
	{
		byte[][] data = new byte[img.getHeight()][img.getWidth()];
		boolean found = false;
		
		for (int i = 0; i < img.getHeight(); i++)
			for (int j = 0; j < img.getWidth(); j++)
			{
				data[i][j] = HOLLOW;
				
				Color c = new Color(img.getPixel(j, i));
				
				if (comp(c,GRAY))
					data[i][j] = HOLLOW;
				else if (comp(c,DARK_GRAY))
					data[i][j] = SOLID;
				else if (comp(c,BLUE))
				{
					if (!found)
					{
						data[i][j] = START_POSITION;
						found = true;
					}
					else
						data[i][j] = HOLLOW;
				}
				else if (comp(c,RED))
					data[i][j] = GOAL;
				else if (comp(c,YELLOW))
					data[i][j] = LETHAL;
				else if (comp(c,GREEN_0))
					data[i][j] = AREA_TRIGGER_0;
				else if (comp(c,GREEN_1))
					data[i][j] = AREA_TRIGGER_1;
				else if (comp(c,GREEN_2))
					data[i][j] = AREA_TRIGGER_2;
				else if (comp(c,GREEN_3))
					data[i][j] = AREA_TRIGGER_3;
				else if (comp(c,GREEN_4))
					data[i][j] = AREA_TRIGGER_4;
				else if (comp(c,GREEN_5))
					data[i][j] = AREA_TRIGGER_5;
				else if (comp(c,GREEN_6))
					data[i][j] = AREA_TRIGGER_6;
				else if (comp(c,GREEN_7))
					data[i][j] = AREA_TRIGGER_7;
				else if (comp(c,GREEN_8))
					data[i][j] = AREA_TRIGGER_8;
				else if (comp(c,GREEN_9))
					data[i][j] = AREA_TRIGGER_9;
			}
		return data;
	}
	
	private static boolean comp(Color c1, Color c2)
	{
		return  c1.a == c2.a && 
				c1.r == c2.r && 
				c1.g == c2.g &&
				c1.b == c2.b;
	}
	
	/**
	 * Loads the sound from the given path.
	 * @param path The sounds path.
	 * @return The sound at the given path.
	 */
	public static Sound loadSound(String path)
	{
		return TinySound.loadSound(new File(path),Stage.STAGE.game.streamSounds);
	}
	
	/**
	 * Loads the sound from the given path.
	 * @param path The sounds path.
	 * @return The sound at the given path.
	 */
	public static Music loadMusic(String path)
	{
		return TinySound.loadMusic(new File(path),Stage.STAGE.game.streamSounds);
	}
	
	public static void exportObject(Object obj, String path)
	{
		ObjectOutputStream out = null;
		
		try
		{
			File file = new File(path);
			if(file.exists())
				file.delete();
			
			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(obj);
			out.flush();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		finally
		{
			if(out != null)
			{
				try
				{
					out.close();
				}
				catch(Exception e)
				{
					System.out.println("Error closing stream");
					e.printStackTrace();
				}
			}
		}
	}
	
	public static Object importObject(String path)
	{
		ObjectInputStream in = null;
		Object obj = null;
		
		try
		{
			in = new ObjectInputStream(new FileInputStream(new File(path)));
			obj = in.readObject();
		}
		catch(IOException | ClassNotFoundException ioe)
		{
			ioe.printStackTrace();
		}
		finally
		{
			if(in != null)
			{
				try
				{
					in.close();
				}
				catch(Exception e)
				{
					System.out.println("Error closing stream");
					e.printStackTrace();
				}
			}
		}
		return obj;
	}
	
	/**
	 * Updates {@code source} to look more like {@code target}.
	 * @param source The color to update.
	 * @param target The target color.
	 */
	public static void fadeColor(Color source, Color target, float speed)
	{
		if(Math.abs(source.a - target.a) > speed)
		{
			if(source.a < target.a)
				source.a += speed;
			else if(source.a > target.a)
				source.a -= speed;
		}
		
		if(Math.abs(source.r - target.r) > speed)
		{
			if(source.r < target.r)
				source.r += speed;
			else if(source.r > target.r)
				source.r -= speed;
		}
		
		if(Math.abs(source.g - target.g) > speed)
		{
			if(source.g < target.g)
				source.g += speed;
			else if(source.g > target.g)
				source.g -= speed;
		}
		
		if(Math.abs(source.b - target.b) > speed)
		{
			if(source.b < target.b)
				source.b += speed;
			else if(source.b > target.b)
				source.b -= speed;
		}
	}
	
	public static void dispose(Disposable obj)
	{
		try
		{
			obj.dispose();
		}
		catch(Exception e)
		{
			System.err.println("Could not dispose object: " + obj);
		}
	}
	
	public static List<HighScore> readAllHighScores()
	{
		ArrayList<HighScore> highscores = new ArrayList<>();
		for(File file : new File("replays").listFiles())
		{
			if(file.isFile() && file.canRead())
			{
				Object obj = Utilities.importObject(file.getAbsolutePath());
				try
				{
					HighScore hs = (HighScore) obj;
					highscores.add(hs);
				}
				catch(ClassCastException cce)
				{
					System.err.println("Illegal file in the replays directory: " + file.getAbsolutePath());
				}
			}
		}
		
		return highscores;
	}
	
	public static String prettify(String str)
	{
		StringBuilder bu = new StringBuilder(str.length() + 5);
		
		for(int i = 0; i < str.length(); i++)
		{
			char c = str.charAt(i);
			
			if(i > 0 && Character.isUpperCase(c))
				bu.append(" ");
			
			bu.append(c);
		}
		return bu.toString();
	}
	
//	public static PressedButtons[][] convert(List<List<PressedButtons>> replays)
//	{
//		PressedButtons[][] pbs = new PressedButtons[replays.size()][];
//		
//		for(int i = 0; i < pbs.length; i++)
//		{
//			pbs[i] = new PressedButtons[replays.get(i).size()];
//			for(int j = 0; j < pbs[i].length; j++)
//				pbs[i][j] = replays.get(i).get(j);
//		}
//		return pbs;
//	}
}