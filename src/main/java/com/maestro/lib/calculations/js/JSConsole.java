package com.maestro.lib.calculations.js;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Represents jscript object "console"
 */
public class JSConsole {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSConsole.class);

    /**
     * console.log
     * @param objects - the list of objects
     */
    public static void log(Object... objects) {
        LOGGER.info(processMessage(objects));
    }

    /**
     * console.warn
     * @param objects - the list of objects
     */
    public static void warn(Object... objects) {
        LOGGER.warn(processMessage(objects));
    }

    /**
     * console.error
     * @param objects - the list of objects
     */
    public static void error(Object... objects) {
        LOGGER.error(processMessage(objects));
    }

    private static String processMessage(Object... objects) {
        StringBuilder sb = new StringBuilder();
        for (final Object o : objects) {
            if (o instanceof String) {
                if (sb.length() != 0) sb.append(",");
                sb.append((String)o);
            } else if (o instanceof Integer) {
                if (sb.length() != 0) sb.append(",");
                sb.append(o);
            } else if (o instanceof Double) {
                if (sb.length() != 0) sb.append(",");
                sb.append(o);
            } else if (o instanceof Float) {
                if (sb.length() != 0) sb.append(",");
                sb.append(o);
            } else if (o instanceof Date) {
                if (sb.length() != 0) sb.append(",");
                sb.append(o);
            } else {
                if (sb.length() != 0) sb.append(",");
                sb.append(o.toString());
            }
        }
        return sb.toString();
    }
}
