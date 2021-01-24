package com.maestro.lib.calculations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptEngineManagerUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptEngineManagerUtils.class);

    public static ScriptEngine scriptEngine(String nm) {
        ScriptEngineManager manager = new ScriptEngineManager();
//        List<ScriptEngineFactory> engines = manager.getEngineFactories();
//
////        for (ScriptEngineFactory engine : engines) {
////            LOGGER.info("Engine name: {}, version: {}, language: {}", engine.getEngineName(), engine.getEngineVersion(), engine.getLanguageName());
////            LOGGER.info("Short Names:");
////            for (String names : engine.getNames()) {
////                LOGGER.info(names);
////            }
////        }
        return manager.getEngineByName(nm);
    }
}