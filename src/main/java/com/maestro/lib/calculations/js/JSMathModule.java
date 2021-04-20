package com.maestro.lib.calculations.js;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.lang3.math.NumberUtils;
import java.util.OptionalDouble;

import static java.util.Arrays.stream;

/**
 * Represents jscript module with math functions
 */
public class JSMathModule {
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
            return ret;
        }

        return 0;
    }

    public static double MIN(Object...objects) {
        OptionalDouble res = stream(objects)
                .filter(o -> o instanceof Integer || o instanceof Double || NumberUtils.isCreatable((String) o))
                .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                .min();
        double ret = res.isPresent() ? res.getAsDouble() : 0;
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
            return ret;
        }

        return 0;
    }

    public static double MAX(Object...objects) {
        OptionalDouble res = stream(objects)
                .filter(o -> o instanceof Integer || o instanceof Double || NumberUtils.isCreatable((String) o))
                .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                .max();
        double ret = res.isPresent() ? res.getAsDouble() : 0;
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
            return ret;
        }
        return 0;
    }

    public static double AVG(Object...objects) {
        OptionalDouble res = stream(objects)
                .filter(o -> o instanceof Integer || o instanceof Double || NumberUtils.isCreatable((String) o))
                .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                .average();
        double ret = res.isPresent() ? res.getAsDouble() : 0;
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
            return ret;
        }

        return 0;
    }

    public static double SUM(Object...objects) {
        double ret = stream(objects)
                .filter(o -> o instanceof Integer || o instanceof Double || NumberUtils.isCreatable((String) o))
                .mapToDouble(o -> (o instanceof Integer) ? Double.valueOf((Integer)o) : (o instanceof Double ? (Double)o : Double.valueOf((String)o)))
                .sum();
        return ret;
    }
}
