package com.maestro.lib.jscript;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.script.*;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NashornUnitTest {
    private ScriptEngine engine;

    @BeforeAll
    public void setUp() throws ScriptException {
        engine = ScriptEngineManagerUtils.scriptEngine("nashorn");
        engine.eval("var console = Java.type('com.maestro.lib.jscript.JSConsole');");
    }

    @Test
    public void trim() throws ScriptException {
        engine.eval("print(\" Hello world \".trim())");
    }

    @Test
    public void bindProperties() throws ScriptException {
        engine.eval(new InputStreamReader(NashornUnitTest.class.getResourceAsStream("/js/bind.js")));
    }

    @Test
    public void typedArrays() throws ScriptException {
        engine.eval(new InputStreamReader(NashornUnitTest.class.getResourceAsStream("/js/typedArrays.js")));
    }

    @Test
    public void basicUsage() throws ScriptException {
        Object result = engine.eval("var greeting='hello world';" + "print(greeting);" + "greeting");

        assertEquals("hello world", result);
    }

    @Test
    public void jsonObjectExample() throws ScriptException {
        Object obj = engine.eval("Java.asJSONCompatible({ number: 42, greet: 'hello', primes: [2,3,5,7,11,13] })");
        Map<String, Object> map = (Map<String, Object>) obj;

        assertEquals("hello", map.get("greet"));
        assertTrue(List.class.isAssignableFrom(map.get("primes").getClass()));
    }

    @Test
    public void extensionsExamples() throws ScriptException {
        String script = "var list = [1, 2, 3, 4, 5];" + "var result = '';" + "for each (var i in list) {" + "result+=i+'-';" + "};" + "print(result);";
        engine.eval(script);
    }

    @Test
    public void bindingsExamples() throws ScriptException {
        Bindings bindings = engine.createBindings();
        bindings.put("count", 3);
        bindings.put("name", "oleksii");

        String script = "var greeting='Hello ';" + "for(var i=count;i>0;i--) { " + "greeting+=name + ' '" + "}" + "greeting";

        Object bindingsResult = engine.eval(script, bindings);
        assertEquals("Hello oleksii oleksii oleksii ", bindingsResult);
    }

    @Test
    public void jvmBoundaryExamples() throws ScriptException, NoSuchMethodException {
        engine.eval("function composeGreeting(name) {" + "return 'Hello ' + name" + "}");

        Invocable invocable = (Invocable) engine;

        Object funcResult = invocable.invokeFunction("composeGreeting", "maestro");
        assertEquals("Hello maestro", funcResult);

        Object map = engine.eval("var HashMap = Java.type('java.util.HashMap');" + "var map = new HashMap();" + "map.put('hello', 'world');" + "map");

        assertTrue(Map.class.isAssignableFrom(map.getClass()));
    }

    @Test
    public void testMathModule() throws ScriptException {
        engine.eval("load('classpath:js/mathModule.js');");
        engine.eval("console.log('test');");
        Object result = engine.eval("Math.AVG(10, 20, 30);");
        System.out.println("Math.AVG = " + result.toString());
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

//    @Test
//    public void loadExamples() throws ScriptException {
//        Object loadResult = engine.eval("load('classpath:js/script.js');" + "increment(5)");
//
//        assertEquals(6, ((Double) loadResult).intValue());
//
//        Object math = engine.eval("var math = loadWithNewGlobal('classpath:js/mathModule.js');" + "math.increment(5);");
//
//        assertEquals(6.0, math);
//    }
}
