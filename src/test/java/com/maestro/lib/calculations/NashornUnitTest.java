package com.maestro.lib.calculations;

import com.maestro.lib.calculations.js.document.domain.DocumentVar;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.script.*;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NashornUnitTest {
    private ScriptEngine engine;

    @BeforeAll
    public void setUp() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");
        engine.eval("var console = Java.type('com.maestro.lib.calculations.js.JSConsole');");
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
    public void jsonObject() throws ScriptException {
        Object obj = engine.eval("Java.asJSONCompatible({ number: 42, greet: 'hello', primes: [2,3,5,7,11,13] })");
        Map<String, Object> map = (Map<String, Object>) obj;

        assertEquals("hello", map.get("greet"));
        assertTrue(List.class.isAssignableFrom(map.get("primes").getClass()));
    }

    @Test
    public void extensions() throws ScriptException {
        String script = "var list = [1, 2, 3, 4, 5];" + "var result = '';" + "for each (var i in list) {" + "result+=i+'-';" + "};" + "print(result);";
        engine.eval(script);
    }

    @Test
    public void bindingsVars() throws ScriptException {
        Bindings bindings = engine.createBindings();
        bindings.put("count", 3);
        bindings.put("name", "oleksii");

        String script = "var greeting='Hello ';" + "for(var i=count;i>0;i--) { " + "greeting+=name + ' '" + "}" + "greeting";

        Object bindingsResult = engine.eval(script, bindings);
        assertEquals("Hello oleksii oleksii oleksii ", bindingsResult);
    }

    @Test
    public void bindingsSimpleJson() throws ScriptException {
        Bindings bindings = engine.createBindings();
        bindings.put("o", "{ \"name\":\"oleksii\", \"company\":\"private\" }");

        String script = "var obj = JSON.parse(o); var greeting='Hello ' + obj.name + ' from ' + obj.company; greeting";

        Object bindingsResult = engine.eval(script, bindings);
        assertEquals("Hello oleksii from private", bindingsResult);
    }

    @Test
    public void bindingsComplexJson() throws ScriptException {
        Bindings bindings = engine.createBindings();
        bindings.put("o", "[{ \"name\":\"oleksii\", \"company\":\"private\" }, { \"name\":\"david\", \"company\":\"agency\" }]");

        String script = "var obj = JSON.parse(o);" +
                        "var greeting='Hello ' + obj[0].name + ' from ' + obj[0].company;" +
                            "greeting+=', ' + obj[1].name + ' from ' + obj[1].company; greeting";;

        Object bindingsResult = engine.eval(script, bindings);
        assertEquals("Hello oleksii from private, david from agency", bindingsResult);
    }

    @Test
    public void bindingsJavaList() throws ScriptException {
        List<String> names = Arrays.asList("oleksii", "vadim");
        Bindings bindings = engine.createBindings();
        bindings.put("obj", names);

        String script = "var greeting='Hello ' + obj[0]; greeting+=', ' + obj[1]; greeting";
        Object bindingsResult = engine.eval(script, bindings);
        assertEquals("Hello oleksii, vadim", bindingsResult);
    }

    @Test
    public void bindingsJavaListObject() throws ScriptException {
        List<DocumentVar> names = Arrays.asList(
                new DocumentVar("FEILD1", 0, 0, "Oleksii"),
                new DocumentVar("FEILD2", 0, 0, "Vadim")
        );
        Bindings bindings = engine.createBindings();
        bindings.put("obj", names);

        engine.eval("for(var k in obj) { print(obj[k]); }", bindings);

        String script = "var greeting='Hello ' + obj[0].val; greeting+=', ' + obj[1].val; greeting";
        Object bindingsResult = engine.eval(script, bindings);
        assertEquals("Hello Oleksii, Vadim", bindingsResult);
    }

    @Test
    public void jvmBoundary() throws ScriptException, NoSuchMethodException {
        engine.eval("function composeGreeting(name) {" + "return 'Hello ' + name" + "}");

        Invocable invocable = (Invocable) engine;

        Object funcResult = invocable.invokeFunction("composeGreeting", "maestro");
        assertEquals("Hello maestro", funcResult);

        Object map = engine.eval("var HashMap = Java.type('java.util.HashMap');" + "var map = new HashMap();" + "map.put('hello', 'world');" + "map");

        assertTrue(Map.class.isAssignableFrom(map.getClass()));

        List<DocumentVar> names = Arrays.asList(
                new DocumentVar("FEILD1", 0, 0, "Oleksii"),
                new DocumentVar("FEILD2", 0, 0, "Vadim")
        );
        Bindings bindings = engine.createBindings();
        bindings.put("obj", names);

        engine.eval("for(var k in obj) { print(obj[k]); }", bindings);
        String script = "obj[0].val = 'OLEKSII'; obj[0].dirty = true; obj";
        Object list = engine.eval(script, bindings);

        List<DocumentVar> vars = (List<DocumentVar>) list;
        vars.forEach(System.out::println);
        assertTrue(List.class.isAssignableFrom(list.getClass()));
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
}
