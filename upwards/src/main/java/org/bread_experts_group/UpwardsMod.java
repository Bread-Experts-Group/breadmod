package org.bread_experts_group;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface UpwardsMod {
	String modID();

	String dependencyLocation();
}