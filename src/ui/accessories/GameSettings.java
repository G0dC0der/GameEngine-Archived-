package ui.accessories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GameSettings 
{
	public boolean vsync, showFps, streamSounds, clearEachFrame, saveReplays;
	public int fps;
	public double volume;

	public GameSettings()
	{
		vsync = clearEachFrame = saveReplays = true;
		fps = 60;
		volume = 1.0;
	}
	
	public void loadSettings(String path)
	{
		try
		{
			File file = new File(path);
			if(file.exists())
			{
				BufferedReader in = new BufferedReader(new FileReader(file));
				
				String line;
				while((line = in.readLine()) != null)
				{
					String tag;
					String value;
					int index = line.indexOf("=");
					if(index != -1 && !line.startsWith(";"))
					{
						tag   = line.substring(0, index).toLowerCase();
						value = line.substring(index + 1, line.length()).toLowerCase();
						
						switch(tag)
						{
							case "showfps":
								showFps = Boolean.parseBoolean(value);
								break;
							case "fps":
								fps = Integer.parseInt(value);
								break;
							case "vsync":
								vsync = Boolean.parseBoolean(value);
								break;
							case "volume":
								volume = Double.parseDouble(value);
								break;
							case "streamsounds":
								streamSounds = Boolean.parseBoolean(value);
								break;
							case "cleareachframe":
								clearEachFrame = Boolean.parseBoolean(value);
								break;
							case "savereplays":
								saveReplays = Boolean.parseBoolean(value);
								break;
							default:
								System.err.println("Unknown tag: " + tag);
								break;
						}
					}
				}
				in.close();
			}
			else
				System.err.println("INI file not found.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
