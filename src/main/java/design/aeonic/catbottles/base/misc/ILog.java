package design.aeonic.catbottles.base.misc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import design.aeonic.catbottles.CatBottles;

/**
 * A wrapping interface for {@link Logger}, with a few added methods for simpler prettified logging.
 * @author aeonic-dev
 */
public interface ILog {
	Logger Log = LogManager.getLogger();
	
	
	/**
	 * Gets the calling class to use as a log prefix, for overriding in static classes.
	 */
	default Class<?> getCallingClass() {
		return this.getClass();
	}
	
	/**
	 * Gets the log's prefix, including the mod name and the mod-level path of the user class.
	 * @return the prefix
	 */
	default String getLogPrefix() {
		return String.format("[%s > %s] ", CatBottles.MOD_NAME, getCallingClass().getName().replace("design.aeonic." + CatBottles.MOD_ID + ".", ""));
	}
	
	/**
	 * Identical to {@link #info(String, Object...)}, but with zero additional arguments.
	 */
	default void info(String s) {
		info(s, new Object[] {});
	}
	
	/**
	 * Identical to {@link #error(String, Object...)}, but with zero additional arguments.
	 */
	default void error(String s) {
		error(s, new Object[] {});
	}
	
	/**
	 * Identical to {@link #debug(String, Object...)}, but with zero additional arguments.
	 */
	default void debug(String s) {
		debug(s, new Object[] {});
	}
	
	/**
	 * Logs at level {@code INFO} with the mod's prefix.
	 * @param s the message to log
	 * @param args the arguments to pass to the logger
	 */
	default void info(String s, Object... args) {
		Log.info(getLogPrefix() + s, args);
	}
	
	/**
	 * Logs at level {@code ERROR} with the mod's prefix.
	 * @param s the message to log
	 * @param args the arguments to pass to the logger
	 */
	default void error(String s, Object... args) {
		Log.error(getLogPrefix() + s, args);
	}
	
	/**
	 * Logs at level {@code DEBUG} with the mod's prefix.
	 * @param s the message to log
	 * @param args the arguments to pass to the logger
	 */
	default void debug(String s, Object... args) {
		Log.debug(getLogPrefix() + s, args);
	}
	
	/**
	 * Creates an anonymous ILog for use within static classes, overriding the prefix to fit.
	 * @param outerClass the class of the calling object
	 * @return the new ILog object
	 */
	static ILog LogStatic(Class<?> outerClass) {
		return new ILog() {
			private final Class<?> callingClass = outerClass;
			
			@Override
			public Class<?> getCallingClass() {
				return callingClass;
			}
		};
	}
	
}
