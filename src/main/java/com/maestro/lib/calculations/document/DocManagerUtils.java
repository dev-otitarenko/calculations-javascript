package com.maestro.lib.calculations.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

public class DocManagerUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocManagerUtils.class);

    private final ScriptEngine engine;
    private final Bindings bindings;
    private List<DocumentVar> data;
    private final List<ValidateRule> rules;

    public DocManagerUtils(final List<DocumentVar> data,
                           final List<ValidateRule> rules) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("nashorn");
        this.bindings = engine.createBindings();
        this.bindings.put("data", data);
        this.engine.eval(
                "var console = Java.type('com.maestro.lib.calculations.JSConsole');" +
                "\n var DOC_MODULE = Java.type('com.maestro.lib.calculations.JSDocument');" +
                "\n var MATH_MODULE = Java.type('com.maestro.lib.calculations.JSMathModule');" +
                "\n DOC_MODULE.set(data);" +
                "\n Math.MIN = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.MIN(arguments); };" +
                "\n Math.MAX = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.MAX(arguments); };" +
                "\n Math.SUM = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.SUM(arguments); };" +
                "\n Math.AVG = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.AVG(arguments); };" +
                "\n getValue = function(nm, tbNum, rn) { return DOC_MODULE.getValue(nm, tbNum, rn); };" +
                "\n setValue = function(nm, v, tbNum, rn) { return DOC_MODULE.setValue(nm, tbNum, rn, v); };" +
                "\n isValueChanged = function(nm, tbNum, rn) { return DOC_MODULE.isChanged(nm, tbNum, rn); };",
                bindings);
        this.data = data;
        this.rules = rules;
    }

    public List<ValidateError> validate() {
        List<ValidateError> ret = new ArrayList<>();
        rules
            .stream().parallel()
            .filter(r -> r.isOnlyChecking() && !r.getSign().equals("*"))
            .forEach(r -> ret.add(validateRule(r)));
        return ret;
    }

    public List<ValidateError> validateParallel() {
        List<ValidateError> ret = new ArrayList<>();
        rules
            .parallelStream()
            .filter(r -> r.isOnlyChecking() && !r.getSign().equals("*"))
            .forEach(r -> ret.add(validateRule(r)));
        return ret;
    }

    public void calculate() throws ScriptException {
        rules
            .stream()
            .filter(r -> r.isOnlyFilling() && r.getSign().equals("="))
            .forEach(r -> {
                try {
                    final String nm = r.getField();
                    final String expr = String.format("setValue('%s', %s, 0, 0)", nm, parseRule(r.getExpression()));

                    LOGGER.info("calculate: {}", expr);
                    engine.eval(expr, bindings);
                    calculate(nm);
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            });
        Object result = engine.eval("DOC_MODULE.get()", bindings);
        if (result instanceof List) {
            this.data = (List<DocumentVar>)result;
        } else {
            throw new ScriptException("ScriptException: result is not List");
        }
    }

    private void calculate(final String srcNm) {
        rules
            .stream()
            .filter(r -> r.isOnlyFilling() && r.getSign().equals("=") && r.getExpression().contains("^" + srcNm))
            .forEach(r -> {
                try {
                    Boolean isChanged = (Boolean)engine.eval(String.format("isValueChanged('%s', %d, %d)", srcNm, 0, 0), bindings);
                    if (!isChanged) {
                        final String nm = r.getField();
                        final String expr = String.format("setValue('%s', %s, 0, 0)", nm, parseRule(r.getExpression()));

                        LOGGER.info("\t[%s] calculate: {}", srcNm, expr);
                        engine.eval(expr, bindings);

                        calculate(nm);
                    }
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            });

    }

    public Object execRule(final ValidateRule r) throws ScriptException {
        return engine.eval(parseRule(r.getExpression()), bindings);
    }

    private ValidateError validateRule(final ValidateRule r) {
        final String sign = r.getSign().equals("=") ? "==" : r.getSign();
        final String rule = String.format("getValue(\"%s\", 0, 0) %s (%s)", r.getField(), sign, parseRule(r.getExpression()));

        LOGGER.info("validateRule: {}", rule);

        try {
            Object val = engine.eval(rule, bindings);
            Boolean res = (Boolean)val;
            return new ValidateError(res == true ? 1 : 0, res == true ?  rule + " OK" : rule + " INVALID");
        } catch (ScriptException ex) {
            return new ValidateError(-1, rule + ": " +  ex.getMessage());
        }
    }

    private String parseRule(final String rule) {
        String ret = rule;
        final List<String> fields = RegExpUtils.getMatches(ret, "(\\^\\w+)");
        if (fields.size() != 0) {
            for (String f : fields) {
                final String nm = f.substring(1);
                ret = ret.replaceAll("\\^" + nm, "getValue(\"" + nm + "\", 0, 0)");
            }
        }
        return ret;
    }

    public List<DocumentVar> data() {
        return this.data;
    }
}

