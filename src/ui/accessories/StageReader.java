package ui.accessories;

import game.core.Stage;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import stages.demo.DemoStage;

public class StageReader 
{
	public static List<Class<Stage>> loadAll()
	{
		List<Class<Stage>> stages = new LinkedList<>(); 
		
		try
		{
			stages.addAll(loadStages(new File("stages")));
			stages.addAll(readAllStages());
		}
		catch(Exception e)
		{
			System.err.println("Error reading stages.");
			e.printStackTrace();
		}
		return stages;
	}
	
	/**
	 * Loads and returns all the {@code Playable} stages found in the jars.
	 * @param folder The folder containing all the jar files.
	 * @return The {@code Class} objects of the classes.
	 */
	private static List<Class<Stage>> loadStages(File folder) throws Exception
	{
		List<Class<Stage>> stageClasses = new LinkedList<>();
		
		for(File jarPath : folder.listFiles())
		{
			if(jarPath.getAbsolutePath().endsWith(".jar"))
			{
				JarFile jarFile = null;
				try
				{
					URLClassLoader clazzLoader = URLClassLoader.newInstance(new URL[]{jarPath.toURI().toURL()});
			        
			        jarFile = new JarFile(jarPath);
			        Enumeration<JarEntry> entries = jarFile.entries();
			        
			        while (entries.hasMoreElements()) 
			        {
			            JarEntry element = entries.nextElement();
			            
			            if (element.getName().endsWith(".class")) 
			            {
		                	@SuppressWarnings("unchecked")
		                    Class<Stage> clazz = (Class<Stage>) clazzLoader.loadClass(element.getName().replaceAll(".class", "").replaceAll("/", "."));
		                    
		                    if(Stage.class.isAssignableFrom(clazz) && getPlayable(clazz) != null)
		                    	stageClasses.add(clazz);
			            }
			        }
		        }
	      
				catch(Exception e)
				{
					System.err.println("Invalid jar, skipping: " + jarPath.getAbsolutePath());
					e.printStackTrace();
				}
				finally
				{
					try
					{
						jarFile.close();
					}
					catch(Exception e){}
				}
			}
		}
        return stageClasses;
	}
	
	/**
	 * Read and returns all the playable stages from the {@code stages} package.
	 * @return The classes.
	 * @throws IOException
	 */
	private static List<Class<Stage>> readAllStages() throws Exception
	{
		List<Class<Stage>> clazzez = new ArrayList<>();
		String[] packages = getResourceListing(DemoStage.class, "stages/");
		
		for(String packageName : packages)
		{
			if(!packageName.contains("."))
			{
				List<String> packageClasses = getClassNamesFromPackage("stages." + packageName);
				for(String className : packageClasses)
				{
					@SuppressWarnings("unchecked")
					Class<Stage> clazz = (Class<Stage>) Class.forName(correctName(className));
					if(Stage.class.isAssignableFrom(clazz) && getPlayable(clazz) != null && !clazzez.contains(clazz))
						clazzez.add(clazz);
				}
			}
			
		}
		return clazzez;
	}
	
	public static Playable getPlayable(Class<?> clazz)
	{
		Annotation[] ans = clazz.getAnnotations();
		for(Annotation an : ans)
			if(an instanceof Playable)
				return (Playable)an;
		
		return null;
	}
	
	private static ArrayList<String> getClassNamesFromPackage(String packageName) throws IOException, URISyntaxException, ClassNotFoundException
	{
		String pakkage = packageName.replace("/", ".").concat(".");
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL packageURL;
		ArrayList<String> names = new ArrayList<String>();
		
		packageName = packageName.replace(".", "/");
		packageURL = classLoader.getResource(packageName);

		if (packageURL.getProtocol().equals("jar")) 
		{
			String jarFileName;
			JarFile jf;
			Enumeration<JarEntry> jarEntries;
			String entryName;

			jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
			jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));
			jf = new JarFile(jarFileName);
			jarEntries = jf.entries();
			
			while (jarEntries.hasMoreElements()) 
			{
				try
				{
					entryName = jarEntries.nextElement().getName();
					if (entryName.startsWith(packageName) && entryName.length() > packageName.length() + 5) 
					{
						entryName = entryName.substring(packageName.length(), entryName.lastIndexOf('.'));
						names.add(pakkage + entryName);
					}
				}
				catch(StringIndexOutOfBoundsException e){}
			}
			try
			{
				jf.close();
				
			}
			catch(Exception e){}
		} 
		else //Not a jar.
		{
			URI uri = new URI(packageURL.toString());
			File folder = new File(uri.getPath());
			File[] contenuti = folder.listFiles();
			String entryName;
			for (File actual : contenuti) 
			{
				entryName = actual.getName();
				entryName = entryName.substring(0, entryName.lastIndexOf('.'));
				names.add(pakkage + entryName);
			}
		}
		return names;
	}
	
	/**
   * List directory contents for a resource folder. Not recursive.
   * This is basically a brute-force implementation.
   * Works for regular files and also JARs.
   * 
   * Example usage: getResourceListing(Race.class, "stages"), this would return all the files and folders in the "stages" package.
   * 
   * @author Greg Briggs
   * @param clazz Any java class that lives in the same place as the resources you want.
   * @param path Should end with "/", but not start with one.
   * @return Just the name of each member item, not the full paths.
   */
	private static String[] getResourceListing(Class<?> clazz, String path) throws URISyntaxException, IOException 
	{
		URL dirURL = clazz.getClassLoader().getResource(path);
		if (dirURL != null && dirURL.getProtocol().equals("file"))
			return new File(dirURL.toURI()).list();

		if (dirURL == null) 
		{
			String me = clazz.getName().replace(".", "/") + ".class";
			dirURL = clazz.getClassLoader().getResource(me);
		}

		if (dirURL.getProtocol().equals("jar")) 
		{
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));

			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries();

			Set<String> result = new HashSet<String>();

			while (entries.hasMoreElements()) 
			{
				String name = entries.nextElement().getName();
				if (name.startsWith(path)) 
				{
					String entry = name.substring(path.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0) 
						entry = entry.substring(0, checkSubdir);
					result.add(entry);
				}
			}

			try 
			{
				jar.close();
			} 
			catch (Exception e) {}

			return result.toArray(new String[result.size()]);
		}
		throw new UnsupportedOperationException("Cannot list files for URL "+ dirURL);
	}
	  
	private static String correctName(String name) 
	{
		if (name.contains(".."))
			name = name.replace("..", ".");

		if (name.contains("./"))
			name = name.replace("./", ".");

		if (name.contains(".\\"))
			name = name.replace(".\\", ".");

		if (name.contains("/"))
			name = name.replace("/", ".");

		if (name.contains("\\"))
			name = name.replace("\\", ".");

		return name;
	}
}