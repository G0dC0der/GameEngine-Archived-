package ui.accessories;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Playable stages need to add this annotation to their class deceleration so it can be recognized by certain methods.
 * @author Pojahn Moradi
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Playable
{
	String name() default "Unnamed Stage";
	String description() default "";
	String thumpnail() default "";
}