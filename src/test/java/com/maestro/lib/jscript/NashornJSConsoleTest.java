package com.maestro.lib.jscript;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NashornJSConsoleTest {
    private ScriptEngine engine;

    @BeforeAll
    public void setUp() throws ScriptException {
        engine = ScriptEngineManagerUtils.scriptEngine("nashorn");
        engine.eval("var console = Java.type('com.maestro.lib.jscript.JSConsole');");
    }

    @Test
    void log() {
        try {
            engine.eval("console.log('test log')");
        } catch (ScriptException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void warn() {
        try {
            engine.eval("console.warn('test warn', 200, true)");
        } catch (ScriptException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void error() {
        try {
            engine.eval("console.error('test error', 100, 300, 'string')");
        } catch (ScriptException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void mustBeThrow() {
        ScriptException thrown = assertThrows(
                ScriptException.class,
                () -> { engine.eval("console.trace('test error', 100, 300, 'string')"); },
                "Expected doThing() to throw, but it didn't"
        );

        System.out.println(thrown.getMessage());
        assertTrue(thrown.getMessage().contains("console.trace is not a function in <eval>"));
    }
}