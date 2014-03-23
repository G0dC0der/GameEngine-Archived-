package game.development;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adding {@code AutoInstall} to your class will load and initialize the following: background and foreground images, main character instance and image(+ jump sound and death animation) and the map.<br>
 * The background, foreground and the map are loaded from the {@code path} folder. The exact structure is:<br>
 * - path/background.png<br>
 * - path/foreground.png<br>
 * - path/map.png<br>
 * The main characters resources are loaded from {@code mainPath}, and the files that will be used are:<br>
 * - mainPath/main<br>
 * - mainPath/main/death<br>
 * - mainPath/hearth.png<br>
 * - mainPath/jump.wav<br>
 * If any of the mentioned files does not exist, the function will either fail or print an error message.<br><br>
 * 
 * Additionally, adding this annotation forces the {@code StageBuilder} to call the required methods to make the stage work properly, avoiding boilerplate code.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoInstall 
{
	/**
	 * This is the path used to load the background, foreground and map.
	 */
	String path();
	
	/**
	 * The folder where the main characters content is located.
	 */
	String mainPath();
}
