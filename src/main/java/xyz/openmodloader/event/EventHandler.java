package xyz.openmodloader.event;

import java.lang.annotation.*;

/**
 * Annotation marking a method as an event handler.
 * The method must take 1 parameter, a class extending {@link Event}.
 * @see EventBus#register(Object) 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
}
