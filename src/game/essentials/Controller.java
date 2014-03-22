package game.essentials;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import com.badlogic.gdx.Input.Keys;

/**
 * The controller class stores a bunch of integers, where each one links to a key on the keyboard.<br>
 * Every {@code MainCharacter} stores on {@code Controller} object that represent which keys on the keyboard the character uses.
 * @author Pojahn Moradi
 *
 */
public class Controller implements java.io.Serializable
{
	private static final long serialVersionUID = 2090940770780402510L;
	
	public int left, right, down, up, suicide, switchChar, special1, special2, special3;
	
	public Controller(){}
	
	@Override
	public String toString()
	{
		return String.format("left: %d, right: %d, down: %d, up: %d , suicide: %d, switchChar: %d, special1: %d, special2: %d, special3: %d", left, right, down, up, suicide, switchChar, special1, special2, special3);
	}
	
	/**
	 * Returns a {@code Controller} object with default settings:<br>
	 * Left: Arrow Left<br>
	 * Right: Arrow Right<br>
	 * Down: - not set -<br>
	 * Up: Space<br>
	 * Pause: Escape<br>
	 * Suicide: Q<br>
	 * Special 1: Non-num 1<br>
	 * Special 2: Non-num 2<br>
	 * Special 3: Non-num 3
	 * @return The object.
	 */
	public static Controller getDefaultP1Controller()
	{
		Controller c = new Controller();
		c.left = Keys.LEFT;
		c.right = Keys.RIGHT;
		c.up = Keys.SPACE;
		c.suicide = Keys.Q;
		c.special1 = Keys.NUM_1;
		c.special2 = Keys.NUM_2;
		c.special3 = Keys.NUM_3;
		
		return c;
	}
	
	/**
	 * An instance of {@code PressedButtons} represent the keys that where pressed down a frame.<br>
	 * The engine analyzes the buttons that are pressed down every frame and stores the data in a {@code PressedButtons} instance.<br>
	 * @author Pojahn Moradi
	 */
	public static class PressedButtons implements java.io.Serializable
	{
		private static final long serialVersionUID = -7826496236803590984L;
		
		/**
		 * True if this key was pressed down.
		 */
		public boolean left, right, down, up, suicide, switchChar, special1, special2, special3;
		
		/**
		 * This function is responsible for creating replay files.<br>
		 * The engine is automatically calling this method but can be called manually if required.
		 * @param meta The meta data of the stage.
		 * @param pbs The pressed buttons.
		 * @param path The path to save the file in.
		 */
		public static void encode(String meta, PressedButtons[] pbs, String path)
		{
			path = checkAvailability(path);
			File file = new File(path);
			if(file.exists())
				file.delete();

			try(BufferedWriter out = new BufferedWriter(new FileWriter(file)))
			{
				out.write("[meta]" + meta + "[/meta]");
				
				for(PressedButtons pb : pbs)
				{
					StringBuilder bu = new StringBuilder(11);
					bu.append((pb.left) 	  ? "1" : "0");
					bu.append((pb.right) 	  ? "1" : "0");
					bu.append((pb.down) 	  ? "1" : "0");
					bu.append((pb.up) 		  ? "1" : "0");
					bu.append((pb.suicide)    ? "1" : "0");
					bu.append((pb.switchChar) ? "1" : "0");
					bu.append((pb.special1)   ? "1" : "0");
					bu.append((pb.special2)   ? "1" : "0");
					bu.append((pb.special3)   ? "1" : "0");
					bu.append("-");
					
					out.write(bu.toString());
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		/**
		 * Reads a replay file from the given path and returns it as an object array, where the first element is an {@code String} that is the meta data and the second and final element is an array of {@code PressedButtons}.<br>
		 * This function is automatically called by the server but can be used manually when creating stages that contain ghosts.
		 * @param path The path to read from.
		 * @return The results.
		 */
		public static Object[] decode(String path)
		{
			Object[] replayData = new Object[2];
			String[] data = null;
			
			try(BufferedReader in = new BufferedReader(new FileReader(new File(path))))
			{
				String text = in.readLine();
				int index = text.indexOf("[/meta]");
				if(index != -1)
				{
					replayData[0] = text.substring(0, index);	
					data = text.substring(text.indexOf("[/meta]") + 7).split("-");
				}
				else
					data = text.split("-");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			PressedButtons[] pbs = new PressedButtons[data.length];
			for(int i = 0; i < pbs.length; i++)
			{
				pbs[i] = new PressedButtons();
				pbs[i].left 	  = data[i].charAt(0) == '1';
				pbs[i].right 	  = data[i].charAt(1) == '1';
				pbs[i].down 	  = data[i].charAt(2) == '1';
				pbs[i].up 		  = data[i].charAt(3) == '1';
				pbs[i].suicide 	  = data[i].charAt(5) == '1';
				pbs[i].switchChar = data[i].charAt(6) == '1';
				pbs[i].special1	  = data[i].charAt(7) == '1';
				pbs[i].special2   = data[i].charAt(8) == '1';
				pbs[i].special3   = data[i].charAt(9) == '1';
			}
			
			replayData[1] = pbs;
			return replayData;
		}
	}
	
	private static String checkAvailability(String path)
	{
		for(int i = 2; i < 100; i++)
		{
			File file = new File(path);
			if(file.exists())
			{
				if(i == 2)
					path = path.replace(".rlp", "#" + i + ".rlp");
				else
					path = path.replace("#" + (i - 1) + ".rlp", "#" + i + ".rlp");
			}
			else
				return path;
		}
		
		return path;
	}
}