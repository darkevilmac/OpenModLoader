package xyz.openmodloader.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Any field in the main class of a mod may have this annotation.
 * The fields will automatically be populated with the sided Delegate instance.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Delegate {

    Class<?> client() default Delegate.class;

    Class<?> server() default Delegate.class;

}
