package com.maestro.lib.calculations.js;

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
            ScriptObjectMirror jsObjects = (ScriptObjectMirror) objects;
            OptionalDouble res = jsObjects
                    .values()
                    .stream()
                    .filter(o -> o instanceof Integer || o instanceof Double || NumberUtils.isCreatable((String) o))
                    .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                    .min();
            double ret = res.isPresent() ? res.getAsDouble() : 0;
            LOGGER.debug("MIN: {}", ret);

            return ret;
        }
        LOGGER.debug("MIN = 0");
        return 0;
    }

    public static double MIN(Object...objects) {
        OptionalDouble res = stream(objects)
                .filter(o -> o instanceof Integer || o instanceof Double || NumberUtils.isCreatable((String) o))
                .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                .min();
        double ret = res.isPresent() ? res.getAsDouble() : 0;
        LOGGER.debug("MIN_1: {}", ret);

        return ret;
    }

    @SuppressWarnings("restriction")
    public static double MAX(Object objects) {
        if (objects instanceof ScriptObjectMirror) {
            ScriptObjectMirror jsObjects = (ScriptObjectMirror) objects;
            OptionalDouble res = jsObjects
                    .values()
                    .stream()
                    .filter(o -> o instanceof Integer || o instanceof Double || NumberUtils.isCreatable((String) o))
                    .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                    .max();
            double ret = res.isPresent() ? res.getAsDouble() : 0;
            LOGGER.debug("MAX: {}", ret);

            return ret;
        }
        LOGGER.debug("MAX = 0");
        return 0;
    }

    public static double MAX(Object...objects) {
        OptionalDouble res = stream(objects)
                .filter(o -> o instanceof Integer || o instanceof Double || NumberUtils.isCreatable((String) o))
                .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                .max();
        double ret = res.isPresent() ? res.getAsDouble() : 0;
        LOGGER.debug("MAX_1: {}", ret);

        return ret;
    }

    @SuppressWarnings("restriction")
    public static double AVG(Object objects) {
        if (objects instanceof ScriptObjectMirror) {
            ScriptObjectMirror jsObjects = (ScriptObjectMirror) objects;
            OptionalDouble res = jsObjects
                        .values()
                        .stream()
                        .filter(o -> o instanceof Integer || o instanceof Double || NumberUtils.isCreatable((String) o))
                        .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                        .average();
            double ret = res.isPresent() ? res.getAsDouble() : 0;
            LOGGER.debug("AVG: {}", ret);

            return ret;
        }
        LOGGER.debug("AVG = 0");
        return 0;
    }

    public static double AVG(Object...objects) {
        OptionalDouble res = stream(objects)
                .filter(o -> o instanceof Integer || o instanceof Double || NumberUtils.isCreatable((String) o))
                .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                .average();
        double ret = res.isPresent() ? res.getAsDouble() : 0;
        LOGGER.debug("AVG_1: {}", ret);

        return ret;
    }

    @SuppressWarnings("restriction")
    public static double SUM(Object objects) {
        if (objects instanceof ScriptObjectMirror) {
            ScriptObjectMirror jsObjects = (ScriptObjectMirror) objects;
            double ret = jsObjects
                    .values()
                    .stream()
                    .filter(o -> o instanceof Integer || o instanceof Double || NumberUtils.isCreatable((String) o))
                    .mapToDouble(o -> (o instanceof Integer) ?
                                        Double.valueOf((Integer)o) :
                                        (o instanceof Double ? (Double)o : Double.valueOf((String)o))
                    )
                    .sum();
            LOGGER.debug("SUM: {}", ret);

            return ret;
        }
        LOGGER.debug("SUM = 0");
        return 0;
    }

    public static double SUM(Object...objects) {
        double ret = stream(objects)
                .filter(o -> o instanceof Integer || o instanceof Double || NumberUtils.isCreatable((String) o))
                .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                .sum();
        LOGGER.info("SUM_1: {}", ret);

        return ret;
    }
}
