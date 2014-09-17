package game.essentials;

import java.io.File;
import java.util.ArrayList;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * The {@code Image2D} is very similar to its superclass {@code Sprite} but supports fast access to pixel data.
 * @author Pojahn Moradi
 *
 */
public final class Image2D extends Sprite
{
	private int[][] pixelData;
	
	/**
	 * Creates an image without pixel data.
	 * @param path The abstract path to the image.
	 */
	public Image2D(String path)
	{
		this(path,false);
	}
	
	/**
	 * Creates an image.
	 * @param path The abstract path to the image.
	 * @param createPixelData True to create pixel data, for fast access.
	 */
	public Image2D(String path, boolean createPixelData)
	{
		super(new Texture(path));
		
		if(createPixelData)
			pixelData = getpixelData(path);
		
		flip(false, true);
	}
	
	/**
	 * Return the color at the given coordinate.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return The color in RGBA format.
	 */
	public int getColor(int x, int y)
	{
		if(pixelData == null)
			throw new RuntimeException("The color data was not initialized during construction.");
		else
			return pixelData[x][y];
	}
	
	/**
	 * Disposes the image.
	 */
	public void dispose()
	{
		getTexture().dispose();
		pixelData = null;
	}
	
	/**
	 * Copies the pixel data to the given image.
	 * @param target The image to receive a copy.
	 */
	public void transferPixelData(Image2D target)
	{
		this.pixelData = target.pixelData;
	}
	
	/**
	 * Loads the given images into an array without pixel data.
	 * @param paths The paths.
	 * @return The images.
	 */
	public static Image2D[] loadImages(String... paths)
	{
		return loadImages(paths, false);
	}
	
	/**
	 * Load all the images in the given folder into an array.
	 * @param folder The directory containing the images.
	 * @param createPixelData Whether or not to create pixel data.
	 * @return The images.
	 */
	public static Image2D[] loadImages(File folder, boolean createPixelData)
	{
		return loadImages(getFiles(folder),createPixelData);
	}
	
	/**
	 * Load all the images in the given folder without pixel data into an array.
	 * @param folder The directory containing the images.
	 * @return The images.
	 */
	public static Image2D[] loadImages(File folder)
	{
		return loadImages(folder,false);
	}
	
	/**
	 * Loads the given images into an array.
	 * @param files The paths to the images.
	 * @param createPixelData Whether or not to create pixel data.
	 * @return The images.
	 */
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