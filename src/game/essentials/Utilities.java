package game.essentials;

import static game.core.Engine.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
	 * Saves the given object at the given path.
	 * @param obj The object to save.
	 * @param path The full path(inclusive file name).
	 */
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
	
	/**
	 * Loads the object from the given path.
	 * @param path The full path(inclusive file name). 
	 * @return The object.
	 */
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
	
	public static boolean inRange(double minBound, double maxBound, double value)
	{
		return minBound <= value && value <= maxBound;
	}
	
	public static boolean nearlyEqual(float value1, float value2, float tolerance)
	{
	    final float absA = Math.abs(value1);
	    final float absB = Math.abs(value2);
	    final float diff = Math.abs(value1 - value2);

	    return (value1 * value2 == 0) ? 
	    		diff < (tolerance * tolerance) : 
	    		diff / (absA + absB) < tolerance;
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
			source.a = Math.max(0, Math.min(source.a, 1.0f));
		}
		
		if(Math.abs(source.r - target.r) > speed)
		{
			if(source.r < target.r)
				source.r += speed;
			else if(source.r > target.r)
				source.r -= speed;
			source.r = Math.max(0, Math.min(source.r, 1.0f));
		}
		
		if(Math.abs(source.g - target.g) > speed)
		{
			if(source.g < target.g)
				source.g += speed;
			else if(source.g > target.g)
				source.g -= speed;
			source.g = Math.max(0, Math.min(source.g, 1.0f));
		}
		
		if(Math.abs(source.b - target.b) > speed)
		{
			if(source.b < target.b)
				source.b += speed;
			else if(source.b > target.b)
				source.b -= speed;
			source.b = Math.max(0, Math.min(source.b, 1.0f));
		}
	}
	
	/**
	 * Disposes the given objects.
	 * @param arr The objects to dispose.
	 */
	public static void dispose(Disposable... arr)
	{
		for(Disposable obj : arr)
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
	}
	
	/**
	 * Read all highscores from the highscore directory.
	 * @return The highscores.
	 */
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
	
	/**
	 * Adds a space before all capital characters.
	 * @param str The string to fix.
	 * @return The fixed string.
	 */
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
	
	/**
	 * Fills the given {@code List} with all the fields found in the given class.
	 * @param fields The {@code List} to fill.
	 * @param type The class to check.
	 * @return Same as the given {@code List}.
	 */
	public static List<Field> getAllFields(List<Field> fields, Class<?> type) 
	{
	    for (Field field: type.getDeclaredFields()) 
	        fields.add(field);

	    if (type.getSuperclass() != null)
	        fields = getAllFields(fields, type.getSuperclass());
	    
	    return fields;
	}
	
	/**
	 * Returns a random element from the given array.
	 * @param array The array.
	 * @return A random element.
	 */
	public static <T> T getRandomElement(T[] array)
	{
		if(array.length == 0)
			return null;
		
		return array[new Random().nextInt(array.length)];
	}
}