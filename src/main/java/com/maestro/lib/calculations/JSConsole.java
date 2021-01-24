package com.maestro.lib.calculations;

/**
 * Represents jscript object "console"
 */
public class JSConsole {
    private final static ConsoleLog console;

    static {
        console = new ConsoleLog();
    }

    /**
     * console.log
     * @param objects - the list of objects
     */
    public static void log(Object... objects) {
        console.log(objects);
    }

    /**
     * console.warn
     * @param objects - the list of objects
     */
    public static void warn(Object... objects) {
        console.warn(objects);
    }

    /**
     * console.error
     * @param objects - the list of objects
     */
    public static void error(Object... objects) {
        console.error(objects);
    }
}
