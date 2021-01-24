package com.maestro.lib.jscript.document;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

public class DocumentManagerUtils {
    private final List<DocumentVar> data;
    private final List<ValidateRule> rules;

    public DocumentManagerUtils(final List<DocumentVar> data,
                                final List<ValidateRule> rules) {
        this.data = data;
        this.rules = rules;
    }

    public List<ValidateError> validate(final ScriptEngine engine) throws ScriptException {
        Bindings bindings = engine.createBindings();
        bindings.put("data", data);

        engine.eval("var console = Java.type('com.maestro.lib.jscript.JSConsole');" +
                    "\nvar MATH_MODULE = Java.type('com.maestro.lib.jscript.JSMathModule');" +
                    "\n Math.MIN = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.MIN(arguments); };" +
                    "\n Math.MAX = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.MAX(arguments); };" +
                    "\n Math.SUM = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.SUM(arguments); };" +
                    "\n Math.AVG = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.AVG(arguments); };" +
                    "\n var getValue = function(nm, tbNum, rn) { var rdata; for (var i in data) { rdata = data[i]; if (rdata.field === nm) return rdata.val; } return null; };", bindings);
        List<ValidateError> ret = new ArrayList<>();

        rules
            .parallelStream()
            .filter(r -> r.isOnlyChecking() && !r.getSign().equals("*"))
            .forEach(r -> ret.add(execRule(engine, r, bindings)));
        return ret;
    }

    private ValidateError execRule(final ScriptEngine engine, final ValidateRule r, final Bindings bindings) {
        final String sign = r.getSign().equals("=") ? "==" : r.getSign();
        final String rule = String.format("getValue(\"%s\", 0, 0) %s (%s)", r.getField(), sign, parseRule(r.getExpression()));

        try {
            Object val = engine.eval(rule, bindings);
            return new ValidateError(1, rule + ": " + val.toString());
        } catch (ScriptException ex) {
            return new ValidateError(0, rule + ": " +  ex.getMessage());
        }
    }

    private String parseRule(String rule) {
        final List<String> fields = RegExpUtils.getMatches(rule, "(\\w+)");
        if (fields.size() != 0) {
            for (String f : fields) {
                rule = rule.replaceAll(f, "getValue(\"" + f + "\", 0, 0)");
            }
        }

        return rule;
    }
}

