package com.maestro.lib.jscript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

/**
 * LOGGING
 */
public class ConsoleLog {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleLog.class);

    public void log(Object... objects) {
        LOGGER.info(processMessage(objects));
    }

    public void warn(Object... objects) {
        LOGGER.warn(processMessage(objects));
    }

    public void error(Object... objects) {
        LOGGER.error(processMessage(objects));
    }

    private String processMessage(Object... objects) {
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
