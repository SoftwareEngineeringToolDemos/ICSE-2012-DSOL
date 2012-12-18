package org.dsol.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action {
	
	public static String N_A="N/A";

	String value() default "";
	String name() default "";
	String service() default N_A;
	int priority() default 1;
	boolean compensation() default false;
		
}
