package game.development;

import game.core.Stage;
import game.essentials.Controller;
import game.essentials.Animation;
import game.essentials.Image2D;
import game.essentials.Utilities;
import game.mains.GravityMan;
import game.objects.Particle;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;

/**
 * Extending this class rather than {@code Stage} is an alternative way of creating a stage.<br>
 * With help of the package annotations, you can quickly load and instantiate the background, foreground, main, hearth and death images. You can also initialize the main character({@code GravityMan}), although Pojahns Resouse pack is required.<br>
 * Reefer to the tutorials for usage.
 * @author Pojahn Moradi
 *
 */
public abstract class StageBuilder extends Stage
{
	public enum VisualType {IMAGE, SOUND, MUSIC, REPLAY, WAYPOINT};
	
	protected Pixmap stageImage;
	protected Image2D backgroundImg[], foregroundImg[], deathImg[], mainImage[], extraHp[];
	protected Sound jump;
	protected GravityMan gm;
	private boolean autoInstall;
	
	@Override
	public void init() 
	{
		try
		{
			Field[] fields = this.getClass().getDeclaredFields();
			for(Field field : fields)
			{
				Annotation[] ans = field.getDeclaredAnnotations();
				for(Annotation an : ans)
				{
					if(an instanceof AutoLoad)
					{
						AutoLoad al = (AutoLoad) an;
						VisualType type = al.type();
						String path = fixPath(al.path());
						field.setAccessible(true);
						
						switch(type)
						{
							case IMAGE:
							{
								Object image;
								boolean isFile;
								File file  = new File(path + field.getName());
								File file2 = new File(path + field.getName() + ".png");
								
								if(file.exists() && file.isDirectory())
									isFile = false;
								else if(file2.exists() && file2.isFile())
									isFile = true;
								else
									throw new RuntimeException("No file or directory found for field: " + field.getName());
									
								if(field.getType().toString().contains("game.essentials.Image"))
								{
									if(isFile)
										image = new Image2D(file2.getAbsolutePath(), true);
									else
										image = Image2D.loadImages(file, true);
								}
								else
									throw new RuntimeException("@AutoLoad of type IMAGE should be placed on an game.essentials.Image field: " + field.getType());	
								
								field.set(this, image);
								break;
							}
							case SOUND:
							{
								String part = path + field.getName();
								File file = new File(part + ".wav");
								if(!file.exists())
									file = new File(part + ".ogg");
								if(!file.exists())
									throw new RuntimeException(path + field.getName() + ".wav/ogg could not be found.");
								
								Sound sound = TinySound.loadSound(new File(file.toString()));
								field.set(this, sound);
								break;
							}
							case MUSIC:
								String part = path + field.getName();
								File file = new File(part + ".wav");
								if(!file.exists())
									file = new File(part + ".ogg");
								if(!file.exists())
									throw new RuntimeException(path + field.getName() + ".wav/ogg could not be found.");
									
								Music music = TinySound.loadMusic(new File(file.toString()),true);
								field.set(this, music);
								break;
							case WAYPOINT:
								path += field.getName() + ".dat";
								field.set(this, Utilities.importObject(path));
								break;
							case REPLAY:
								path += field.getName() + ".rlp";
								field.set(this, Utilities.importObject(path));
								break;
						}
					}
				}
			}
			
			Annotation[] ans = this.getClass().getAnnotations();
			for(Annotation an : ans)
			{
				if(an instanceof AutoInstall)
				{
					autoInstall = true;
					AutoInstall al = (AutoInstall) an;
					String stagePath = fixPath(al.path());
					String mainPath  = fixPath(al.mainPath());
					
					mainImage   = Image2D.loadImages(new File(mainPath + "main"),true);
					extraHp     = Image2D.loadImages(new File(mainPath + "health"), true);
					deathImg   	= Image2D.loadImages(new File(mainPath + "main/death"), false);
					jump        = TinySound.loadSound(new File(mainPath + "jump.wav"));
					
					try{backgroundImg = Image2D.loadImages(stagePath + "background.png");} catch(Exception e){System.err.println("Background image not found");}
					try{foregroundImg = Image2D.loadImages(stagePath + "foreground.png");} catch(Exception e){System.err.println("Foreground image not found");}
					    stageImage    = new Pixmap(new FileHandle(stagePath + "map.png"));
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Error: Could not load one or more of the resources.");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@Override
	public void build()
	{
		super.build();
		
		if(autoInstall)
		{
			stageData = Utilities.createStageData(stageImage);
			basicInits();
			
			if(backgroundImg != null)
				background(RenderOption.PORTION, backgroundImg);
			if(foregroundImg != null)
				foreground(RenderOption.PORTION, foregroundImg);
			
			/*
			 * Main Character
			 *******************************************
			 */
			gm = new GravityMan();
			gm.setImage(new Animation<>(3, mainImage));
			gm.setMultiFaced(true);
			gm.setController((Controller)Utilities.importObject("res/data/controller1.con"));
			gm.hit(1);
			gm.setJumpingSound(jump);
			gm.moveTo(startX, startY);
			gm.deathImg = new Particle();
			gm.deathImg.setImage(4,deathImg);
			gm.deathImg.zIndex(101);
			game.addFocusObject(gm);
			add(gm);
		}
	}
	
	@Override
	public void dispose() 
	{
		Field[] fields = this.getClass().getDeclaredFields();
		
		if(getClass().isAnnotationPresent(AutoDispose.class))
		{
			for(Field field : fields)
			{
				field.setAccessible(true);
				try 
				{
					disposeBatch(field.get(this));
				} 
				catch (IllegalArgumentException | IllegalAccessException e) 
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			disposeBatch(backgroundImg, foregroundImg, deathImg, mainImage, extraHp, jump, stageImage, music);
			
			for(Field field : fields)
			{
				field.setAccessible(true);
				
				Annotation[] ans = field.getDeclaredAnnotations();
				for(Annotation an : ans)
				{
					if(an instanceof AutoDispose)
					{
						try 
						{
							disposeBatch(field.get(this));
						} 
						catch (IllegalArgumentException | IllegalAccessException e) 
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	private static String fixPath(String path)
	{
		return path.endsWith("/") || path.endsWith("\\") ? path : path + "/";
	}
}
