package com.maestro.lib.jscript;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.OptionalDouble;

import static java.util.Arrays.stream;

/**
 * Represents jscript module with math functions
 */
public class JSMathModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSMathModule.class);

    @SuppressWarnings("restriction")
    public static double MIN(Object objects) {
        if (objects instanceof ScriptObjectMirror) {
            ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) objects;
            OptionalDouble ret = scriptObjectMirror
                    .values()
                    .stream()
                    .filter(o -> o instanceof Integer || o instanceof Double || (NumberUtils.isCreatable((String) o) && NumberUtils.isDigits((String) o)))
                    .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                    .min();
            return ret.isPresent() ? ret.getAsDouble() : 0;
        }
        return 0;
    }

    public static double MIN(Object...objects) {
        LOGGER.info("MIN: {}", objects.length);
        OptionalDouble ret = stream(objects)
                .filter(o -> o instanceof Integer || o instanceof Double || (NumberUtils.isCreatable((String) o) && NumberUtils.isDigits((String) o)))
                .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                .min();
        return ret.isPresent() ? ret.getAsDouble() : 0;
    }

    @SuppressWarnings("restriction")
    public static double MAX(Object objects) {
        if (objects instanceof ScriptObjectMirror) {
            ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) objects;
            OptionalDouble ret = scriptObjectMirror
                    .values()
                    .stream()
                    .filter(o -> o instanceof Integer || o instanceof Double || (NumberUtils.isCreatable((String) o) && NumberUtils.isDigits((String) o)))
                    .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                    .max();
            return ret.isPresent() ? ret.getAsDouble() : 0;
        }
        return 0;
    }

    public static double MAX(Object...objects) {
        LOGGER.info("MAX: {}", objects.length);
        OptionalDouble ret = stream(objects)
                .filter(o -> o instanceof Integer || o instanceof Double || (NumberUtils.isCreatable((String) o) && NumberUtils.isDigits((String) o)))
                .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                .max();
        return ret.isPresent() ? ret.getAsDouble() : 0;
    }

    @SuppressWarnings("restriction")
    public static double AVG(Object objects) {
        if (objects instanceof ScriptObjectMirror) {
            ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) objects;
//            LOGGER.info("AVG_1: {}", scriptObjectMirror.isArray());
//            for (Map.Entry<String, Object> entry : scriptObjectMirror.entrySet()) {
//                LOGGER.info("\t AVG1 {} : {}", entry.getKey(), entry.getValue());
//            }

            OptionalDouble ret = scriptObjectMirror
                        .values()
                        .stream()
                        .filter(o -> o instanceof Integer || o instanceof Double || (NumberUtils.isCreatable((String) o) && NumberUtils.isDigits((String) o)))
                        .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                        .average();
            return ret.isPresent() ? ret.getAsDouble() : 0;
        }
        return 0;
    }

    public static double AVG(Object...objects) {
        LOGGER.info("AVG: {}", objects.length);
        OptionalDouble ret = stream(objects)
                .filter(o -> o instanceof Integer || o instanceof Double || (NumberUtils.isCreatable((String) o) && NumberUtils.isDigits((String) o)))
                .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                .average();
        return ret.isPresent() ? ret.getAsDouble() : 0;
    }

    @SuppressWarnings("restriction")
    public static double SUM(Object objects) {
        if (objects instanceof ScriptObjectMirror) {
            ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) objects;
            return scriptObjectMirror
                    .values()
                    .stream()
                    .filter(o -> o instanceof Integer || o instanceof Double || (NumberUtils.isCreatable((String) o) && NumberUtils.isDigits((String) o)))
                    .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                    .sum();
        }
        return 0;
    }

    public static double SUM(Object...objects) {
        LOGGER.info("SUM: {}", objects.length);
        return stream(objects)
                .filter(o -> o instanceof Integer || o instanceof Double || (NumberUtils.isCreatable((String) o) && NumberUtils.isDigits((String) o)))
                .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                .sum();
    }
}
