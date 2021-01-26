package com.maestro.lib.calculations;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NashornJSMathModuleTest {
    private ScriptEngine engine;

    @BeforeAll
    public void setUp() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");
        engine.eval("var MATH_MODULE = Java.type('com.maestro.lib.calculations.JSMathModule');");
    }

    @Test
    public void testJavaModule() throws ScriptException {
        Object result = engine.eval("MATH_MODULE.AVG(10, 20, 30);");
        System.out.println("AVG = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 20);
        else
            assertEquals(((Integer) result).intValue(), 20);

        result = engine.eval("MATH_MODULE.AVG(10.0, 20.0, 30.0);");
        System.out.println("AVG = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 20);
        else
            assertEquals(((Integer) result).intValue(), 20);

        result = engine.eval("MATH_MODULE.AVG(\"9.5\", \"20.0\", \"30.5\");");
        System.out.println("AVG = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 20);
        else
            assertEquals(((Integer) result).intValue(), 20);

        result = engine.eval("MATH_MODULE.SUM(150, 250, 50);");
        System.out.println("SUM = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 450);
        else
            assertEquals(((Integer) result).intValue(), 450);


        result = engine.eval("MATH_MODULE.SUM(150.0, 250.0, 50.0);");
        System.out.println("SUM = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 450);
        else
            assertEquals(((Integer) result).intValue(), 450);

        result = engine.eval("MATH_MODULE.SUM(\"150.50\", \"250.30\", \"50.20\");");
        System.out.println("SUM = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 451.0);
        else
            assertEquals(((Integer) result).intValue(), 450.0);
            //
        result = engine.eval("MATH_MODULE.MIN(10, 20, 30);");
        System.out.println("MIN = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 10);
        else
            assertEquals(((Integer) result).intValue(), 10);

        result = engine.eval("MATH_MODULE.MIN(\"10\", \"20\", \"30\");");
        System.out.println("MIN = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 10);
        else
            assertEquals(((Integer) result).intValue(), 10);

            //
        result = engine.eval("MATH_MODULE.MAX(10, 20, 30);");
        System.out.println("MAX = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 30);
        else
            assertEquals(((Integer) result).intValue(), 30);

        result = engine.eval("MATH_MODULE.MAX(10.0, 20.0, 30.0);");
        System.out.println("MAX = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 30);
        else
            assertEquals(((Integer) result).intValue(), 30);

        result = engine.eval("MATH_MODULE.MAX(\"10.0\", \"20.0\", \"30.30\");");
        System.out.println("MAX = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 30.3);
        else
            assertEquals(((Integer) result).intValue(), 30.3);
    }

    @Test
    public void testOverrideJSMathWithJavaModule() throws ScriptException {
        engine.eval(
                "\n Math.MIN = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.MIN(arguments); };" +
                    "\n Math.MAX = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.MAX(arguments); };" +
                    "\n Math.SUM = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.SUM(arguments); };" +
                    "\n Math.AVG = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.AVG(arguments); };");
        Object result = engine.eval("Math.AVG(10, 20, '30');");
        System.out.println("Math.AVG = " + result);
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 20);
        else
            assertEquals(((Integer) result).intValue(), 20);

        result = engine.eval("Math.SUM(10, 20, 30);");
        System.out.println("Math.SUM = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 60);
        else
            assertEquals(((Integer) result).intValue(), 60);

        result = engine.eval("Math.MIN(10, 20, 30);");
        System.out.println("Math.MIN = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 10);
        else
            assertEquals(((Integer) result).intValue(), 10);

        result = engine.eval("Math.MAX(10, 20, 30);");
        System.out.println("Math.MAX = " + result.toString());
        if (result instanceof Double)
            assertEquals(((Double) result).doubleValue(), 30);
        else
            assertEquals(((Integer) result).intValue(), 30);
    }
}
