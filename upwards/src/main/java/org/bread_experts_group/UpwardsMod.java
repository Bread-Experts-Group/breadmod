package org.bread_experts_group;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Identifies a mod for using the Upwards language loader (essentially a more flexible Jar-in-Jar).
 */
@Target(ElementType.TYPE)
public @interface UpwardsMod {
	/**
	 * The mod ID this loading is for.
	 */
	String modID();

	/**
	 * Where dependencies are located in this mod's JAR (or build directory).
	 */
	String dependencyLocation();

	/**
	 * A mod ID to piggyback loading off of. Any libraries available to that mod are available
	 * to this mod and it's libraries.
	 */
	@SuppressWarnings("unused") String piggybackModID() default "";
}