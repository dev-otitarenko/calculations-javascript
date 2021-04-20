package com.maestro.lib.calculations.js.document;

import com.maestro.lib.calculations.js.document.domain.DocumentVar;
import com.maestro.lib.calculations.js.document.domain.ValidateError;
import com.maestro.lib.calculations.js.document.domain.ValidateRule;
import com.maestro.lib.calculations.js.document.domain.ValidationSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

public class DocEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocEngine.class);

    private final ScriptEngine engine;
    private final Bindings bindings;
    private final List<ValidateRule> rules;

    public DocEngine(final List<DocumentVar> data,
                     final List<ValidateRule> rules) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
            // search engine
        this.engine = manager.getEngineByName("nashorn");
            // bindings with data
        this.bindings = engine.createBindings();
        bindings.put("data", data);
            // bound with external functions
        this.engine.eval(
                "var console = Java.type('com.maestro.lib.calculations.js.JSConsole');" +
                        "\n var MATH_MODULE = Java.type('com.maestro.lib.calculations.js.JSMathModule');" +
                        "\n Math.MIN = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.MIN(arguments); };" +
                        "\n Math.MAX = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.MAX(arguments); };" +
                        "\n Math.SUM = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.SUM(arguments); };" +
                        "\n Math.AVG = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.AVG(arguments); };" +
                        "\n isChanged = function(nm, tabNum, rowNum) {" +
                            "\n for (var i in data) {" +
                                "\n f = data[i];" +
                                "\n if (f.field == nm && f.tabn == tabNum && f.nrow == rowNum) {" +
                                     "\n console.log('Dirty: ', nm, tabNum, rowNum, f.dirty);" +
                                    "\n return f.dirty;" +
                                "\n }" +
                            "\n }" +
                            "\n return false;" +
                        "\n }" +
                        "\n value = function(nm, tabNum, rowNum, v) { " +
                            "\n var f;\n" +
                            "\n if (v === undefined) { // get" +
                                "\n for (var i in data) {" +
                                    "\n f = data[i];" +
                                    "\n if (f.field == nm && f.tabn == tabNum && f.nrow == rowNum) {" +
                                        "\n return f.val;" +
                                    "\n }" +
                                "\n }" +
                                "\n return null;" +
                            "\n } else { // set" +
                                "\n for (var i in data) {" +
                                    "\n f = data[i];" +
                                    "\n if (f.field == nm && f.tabn == tabNum && f.nrow == rowNum) {" +
                                        "\n f.val = 1*v;" +
                                        "\n f.dirty = true;" +
                                        "\n data[i] = f;" +
                                        "\n return;" +
                                    "\n }" +
                                "\n }" +
                                "\n f = { field: nm, tabn: tabNum, nrow: rowNum, val: v };" +
                                "\n data.push(f);" +
                            "\n  }" +
                        " };"
        , bindings);
        this.rules = rules;
    }

    /**
     * Validates the document data using rules
     * @return List Returns the list of validation for every rule
     */
    public List<ValidateError> validate() {
        LOGGER.debug("\"validate\" is starting...");

        List<ValidateError> ret = new ArrayList<>();

        rules
            .parallelStream()
            .filter(r -> r.isOnlyChecking() && !r.getSign().equals(ValidationSign.ALL.getName()))
            .forEach(r -> {
                final String sign = r.getSign().equals(ValidationSign.EQ.getName()) ? "==" : r.getSign();
                final String rule = String.format("value(\"%s\", 0, 0) %s (%s)", r.getField(), sign, ValidateRule.parse(r.getExpression(), 0, 0));

                try {
                    LOGGER.debug("validateRule: {}", rule);
                    final Boolean res = (Boolean) engine.eval(rule, bindings);

                    ret.add(new ValidateError(res ? 1 : 0, res ? rule + " OK" : rule + " INVALID"));
                } catch (ScriptException ex) {
                    LOGGER.error("validateRule: {}", ex.getMessage());
                    ret.add(new ValidateError(-1, rule + ": " + ex.getMessage()));
                }
            });

        long success = ret.stream().filter(r -> r.getStatus() == 1).count();
        long error = ret.stream().filter(r -> r.getStatus() == 0).count();

        LOGGER.debug("\"validate\" has finished.: {} recs, ok: {}, error: {}", ret.size(), success, error);

        return ret;
    }

    /**
     * Auto-calculate all fields using corresponding rules
     */
    public List<DocumentVar> recalculate() throws ScriptException {
        LOGGER.debug("\"recalculate\" is starting...");

        rules
            .stream()
            .filter(r -> r.isOnlyFilling() && r.getSign().equals(ValidationSign.EQ.getName()))
            .forEach(r -> {
                try {
                    LOGGER.debug("\t\"recalculate\" \"{}\" = {}", r.getField(), r.getExpression());
                    engine.eval(
                        String.format("value('%s', %d, %d, %s)", r.getField(), 0, 0, ValidateRule.parse(r.getExpression(), 0, 0)),
                        bindings
                    );
                    triggerField(r.getField(), 0, 0);
                } catch (ScriptException e) {
                    LOGGER.error("recalculate: {}", e.getMessage());
                }
            });

        LOGGER.debug("\"recalculate\" has finished!");

        return (List<DocumentVar>) engine.eval("data;", bindings);
    }

    /**
     *
     * @param srcNm - The field name that will cause others calculations in the document
     * @param val - The field value
     */
    public List<DocumentVar> changeFieldValue(final String srcNm, final int tabNum, final int rowNum, final Object val) throws ScriptException {
        LOGGER.debug("\"changeFieldValue\" is starting [\"{}\" : {}]...", srcNm, val);
        // set value
        engine.eval(String.format("value('%s', %d, %d, '%s')", srcNm, tabNum, rowNum, val), bindings);
        // loop through rules
        rules
            .stream()
            .filter(r -> r.isOnlyFilling() && r.getSign().equals(ValidationSign.EQ.getName()) && r.contains("^" + srcNm))
            .forEach(r -> {
                try {
                    LOGGER.debug("\t\"changeFieldValue\" \"{}\" = {}", r.getField(), r.getExpression());
                    engine.eval(
                        String.format("value('%s', %d, %d, %s)", r.getField(), tabNum, rowNum, ValidateRule.parse(r.getExpression(), tabNum, rowNum)),
                        bindings
                    );
                    triggerField(r.getField(), tabNum, rowNum);
                } catch (ScriptException e) {
                    LOGGER.error("changeFieldValue: {}", e.getMessage());
                }
            });
        LOGGER.debug("\"changeFieldValue\" has finished!");

        return (List<DocumentVar>) engine.eval("data;", bindings);
    }

    private void triggerField(final String srcNm, final int tabNum, final int rowNum) {
        rules
            .stream()
            .filter(r -> r.isOnlyFilling() && r.getSign().equals(ValidationSign.EQ.getName()) && r.contains("^" + srcNm))
            .forEach(r -> {
                try {
                   // Boolean isChanged = (Boolean) engine.eval(String.format("isChanged('%s', %d, %d)", r.getField(), tabNum, rowNum), bindings);
                   // if (!isChanged) {
                        LOGGER.info("\t\t \"triggerField\" : \"{}\" = {}", r.getField(), r.getExpression());
                        engine.eval(
                            String.format("value('%s', %d, %d, %s)", r.getField(), tabNum, rowNum, ValidateRule.parse(r.getExpression(), tabNum, rowNum)),
                            bindings
                        );
                        triggerField(r.getField(), tabNum, rowNum);
                   //; }
                } catch (ScriptException e) {
                    LOGGER.error("calculate: {}", e.getMessage());
                }
            });
    }

    /**
     * Executes the validation rule and returns calculated value
     * @param expression Validation rule expression
     * @return Object Returns the value of expression described in ValidateRule
     * @throws ScriptException
     */
    public Object exec(final String expression, final int tabNum, final int rowNum) throws ScriptException {
        final String rule = ValidateRule.parse(expression, tabNum, rowNum);
        final Object ret = engine.eval(rule, bindings);

        LOGGER.debug("\t[execRule, \"{}\"] : {}", rule, ret);

        return ret;
    }
}