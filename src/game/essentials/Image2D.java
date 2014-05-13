package game.essentials;

import java.io.File;
import java.util.ArrayList;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public final class Image2D extends Sprite
{
	private int[][] pixelData;
	
	public Image2D(String path)
	{
		this(path,false);
	}
	
	public Image2D(String path, boolean createPixelData)
	{
		super(new Texture(path));
		
		if(createPixelData)
			pixelData = getpixelData(path);
		
		flip(false, true);
	}
	
	public int getColor(int x, int y)
	{
		if(pixelData == null)
			throw new RuntimeException("The color data was not initialized during construction.");
		else
			return pixelData[x][y];
	}
	
	public void dispose()
	{
		getTexture().dispose();
		pixelData = null;
	}
	
	public void transferPixelData(Image2D target)
	{
		this.pixelData = target.pixelData;
	}
	
	public static Image2D[] loadImages(String... paths)
	{
		return loadImages(paths, false);
	}
	
	public static Image2D[] loadImages(File folder, boolean createPixelData)
	{
		return loadImages(getFiles(folder),createPixelData);
	}
	
	public static Image2D[] loadImages(File folder)
	{
		return loadImages(folder,false);
	}
	
	public static Image2D[] loadImages(String[] files, boolean createPixelData)
	{
		Image2D[] imgs = new Image2D[files.length];
		for(int i = 0; i < imgs.length; i++)
			imgs[i] = new Image2D(files[i],createPixelData);
		
		return imgs;
	}
	
	private static int[][] getpixelData(String path)
	{		
		Pixmap img = new Pixmap(new FileHandle(path));
		
		int[][] cd = new int[img.getWidth()][img.getHeight()];
		
		for (int x = 0; x < img.getWidth(); x++)
			for (int y = 0; y < img.getHeight(); y++)
			{
				int value = img.getPixel(x, y);
				
				if((value & 0x000000ff) / 255f == 0.0f)
					value = 0;
				
				cd[x][y] = value;
			}
		return cd;
	}
	
	private final static String[] getFiles(File folder)
	{
		File[] list = new File(folder.toString()).listFiles();
		ArrayList<String> files = new ArrayList<>();
		
		for (int i = 0; i < list.length; i++)
			if (list[i].isFile())
				files.add(list[i].toString());
		
		return files.toArray(new String[files.size()]);
	}
}