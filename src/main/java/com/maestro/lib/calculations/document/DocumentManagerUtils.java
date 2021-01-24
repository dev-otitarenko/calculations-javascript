package com.maestro.lib.calculations.document;

import com.maestro.lib.calculations.ScriptEngineManagerUtils;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

public class DocumentManagerUtils {
    private final ScriptEngine engine;
    private final Bindings bindings;
    private final List<DocumentVar> data;
    private final List<ValidateRule> rules;

    public DocumentManagerUtils(final List<DocumentVar> data,
                                final List<ValidateRule> rules) throws ScriptException {
        this.engine = ScriptEngineManagerUtils.scriptEngine("nashorn");
        this.bindings = engine.createBindings();
        this.bindings.put("data", data);
        this.engine.eval("var console = Java.type('com.maestro.lib.calculations.JSConsole');" +
                "\nvar MATH_MODULE = Java.type('com.maestro.lib.calculations.JSMathModule');" +
                "\n Math.MIN = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.MIN(arguments); };" +
                "\n Math.MAX = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.MAX(arguments); };" +
                "\n Math.SUM = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.SUM(arguments); };" +
                "\n Math.AVG = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.AVG(arguments); };" +
                "\n var getValue = function(nm, tbNum, rn) { var rdata; for (var i in data) { rdata = data[i]; if (rdata.field === nm) return rdata.val; } return null; };", bindings);
        this.data = data;
        this.rules = rules;
    }

    public List<ValidateError> validate() {
        List<ValidateError> ret = new ArrayList<>();
        rules
            .stream()
            .filter(r -> r.isOnlyChecking() && !r.getSign().equals("*"))
            .forEach(r -> ret.add(validateRule(r)));
        return ret;
    }

    public Object execRule(final ValidateRule r) throws ScriptException {
        return engine.eval(parseRule(r.getExpression()), bindings);
    }

    private ValidateError validateRule(final ValidateRule r) {
        final String sign = r.getSign().equals("=") ? "==" : r.getSign();
        final String rule = String.format("getValue(\"%s\", 0, 0) %s (%s)", r.getField(), sign, parseRule(r.getExpression()));

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
}

