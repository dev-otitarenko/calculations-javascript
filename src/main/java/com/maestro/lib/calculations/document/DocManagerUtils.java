package com.maestro.lib.calculations.document;

import com.maestro.lib.calculations.utils.RegexpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DocManagerUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocManagerUtils.class);

    private final ScriptEngine engine;
    private static List<DocumentVar> data = new ArrayList<>();
    private final List<ValidateRule> rules;

    public DocManagerUtils(final List<DocumentVar> data,
                           final List<ValidateRule> rules) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("nashorn");
        this.engine.eval(
                "var console = Java.type('com.maestro.lib.calculations.js.JSConsole');" +
                        "\n var DOC_MODULE = Java.type('com.maestro.lib.calculations.document.DocManagerUtils');" +
                        "\n var MATH_MODULE = Java.type('com.maestro.lib.calculations.js.JSMathModule');" +
                        "\n Math.MIN = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.MIN(arguments); };" +
                        "\n Math.MAX = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.MAX(arguments); };" +
                        "\n Math.SUM = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.SUM(arguments); };" +
                        "\n Math.AVG = function() {  if (arguments.length == 0) return 0; return MATH_MODULE.AVG(arguments); };" +
                        "\n getValue = function(nm, tbNum, rn) { return DOC_MODULE.getValue(nm, tbNum, rn); };" +
                        "\n setValue = function(nm, v, tbNum, rn) { return DOC_MODULE.setValue(nm, tbNum, rn, v); };" +
                        "\n isValueChanged = function(nm, tbNum, rn) { return DOC_MODULE.isChanged(nm, tbNum, rn); };");
        this.data = data;
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
            .filter(r -> r.isOnlyChecking() && !r.getSign().equals("*"))
            .forEach(r -> {
                final String sign = r.getSign().equals("=") ? "==" : r.getSign();
                final String rule = String.format("getValue(\"%s\", 0, 0) %s (%s)", r.getField(), sign, parseRule(r.getExpression()));

                LOGGER.debug("validate, rule: {}", rule);

                try {
                    Object val = engine.eval(rule);
                    Boolean res = (Boolean) val;
                    ret.add(new ValidateError(res == true ? 1 : 0, res == true ? rule + " OK" : rule + " INVALID"));
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
    public void calculateAllFields() {
        LOGGER.debug("\"calculateAllFields\" is starting...");

        rules
            .stream()
            .filter(r -> r.isOnlyFilling() && r.getSign().equals("="))
            .forEach(r -> {
                try {
                    final String nm = r.getField();
                    final String expr = String.format("setValue('%s', %s, 0, 0)", nm, parseRule(r.getExpression()));

                    LOGGER.debug("[{}] calculateAllFields: {}", nm, expr);
                    engine.eval(expr);
                    calculate(nm);
                } catch (ScriptException e) {
                    LOGGER.error("calculateAllFields: {}", e.getMessage());
                }
            });

        LOGGER.debug("\"calculateAllFields\" has finished...");
    }

    /**
     *
     * @param srcNm - The field name that will cause others calculations in the document
     * @param val - The field value
     * @throws ScriptException
     */
    public void changeFieldValue(final String srcNm, final Object val) throws ScriptException {
        LOGGER.debug("\"changeFieldValue\" is starting... field: {}, value: {}", srcNm, val);
        // set value
        engine.eval(String.format("setValue('%s', %s, 0, 0)", srcNm, val));
        // loop through rules
        rules
            .stream()
            .filter(r -> r.isOnlyFilling() && r.getSign().equals("="))
            .forEach(r -> {
                if (r.getExpression().indexOf("^" + srcNm) >= 0) {
                    try {
                        final String nm = r.getField();
                        final String expr = String.format("setValue('%s', %s, 0, 0)", nm, parseRule(r.getExpression()));

                        LOGGER.debug("changeFieldValue: {}", expr);
                        engine.eval(expr);

                        calculate(nm);
                    } catch (ScriptException e) {
                        LOGGER.error("changeFieldValue: {}", e.getMessage());
                    }
                }
            });
        LOGGER.debug("\"changeFieldValue\" has finished...");
    }

    private void calculate(final String srcNm) {
        rules
            .stream()
            .filter(r -> r.isOnlyFilling() && r.getSign().equals("=") && r.getExpression().contains("^" + srcNm))
            .forEach(r -> {
                try {
                    Boolean isChanged = (Boolean) engine.eval(String.format("isValueChanged('%s', %d, %d)", srcNm, 0, 0));
                    if (!isChanged) {
                        final String nm = r.getField();
                        final String expr = String.format("setValue('%s', %s, 0, 0)", nm, parseRule(r.getExpression()));

                        LOGGER.info("\t[{}] calculate: {}", srcNm, expr);
                        engine.eval(expr);

                        calculate(nm);
                    }
                } catch (ScriptException e) {
                    LOGGER.error("calculate: {}", e.getMessage());
                }
            });
    }

    /**
     * Executes the validation rule and returns calculated value
     * @param r The validation rule
     * @return Object Returns the value of expression described in ValidateRule
     * @throws ScriptException
     */
    public Object execRule(final ValidateRule r) throws ScriptException {
        return engine.eval(parseRule(r.getExpression()));
    }

    private String parseRule(final String rule) {
        String ret = rule;
        final List<String> fields = RegexpUtils.getMatches(ret, "(\\^\\w+)");
        if (fields.size() != 0) {
            for (String f : fields) {
                final String nm = f.substring(1);
                ret = ret.replaceAll("\\^" + nm, "getValue(\"" + nm + "\", 0, 0)");
            }
        }
        return ret;
    }

    /**
     * Returns the list of a document data
     * @return List
     */
    public List<DocumentVar> data() {
        return this.data;
    }

    /**
     * Gets the field value.
     *
     * @param nm   The field name
     * @param tnum - The table number
     * @param nrow - The row number
     * @return Object - The field value
     */
    public static Object getValue(final String nm, final int tnum, final int nrow) {
        Optional<DocumentVar> fld = data
                .stream()
                .filter(f -> f.getField().equals(nm) && f.getTabn() == tnum && f.getNrow() == nrow)
                .findAny();
        if (fld.isPresent()) {
            return fld.get().getVal();
        }
        return null;
    }

    /**
     * Sets the field value for a specific field
     *
     * @param nm   - The field name
     * @param tnum - The table number
     * @param nrow - The row number
     * @param val  - The value
     */
    public static void setValue(final String nm,
                                final int tnum,
                                final int nrow,
                                final Object val) {
        for (int i = 0; i < data.size(); i++) {
            DocumentVar f = data.get(i);
            if (f.getField().equals(nm) && f.getTabn() == tnum && f.getNrow() == nrow) {
                f.setVal(val);
                f.setDirty(true);

                return;
            }
        }

        data.add(new DocumentVar(nm, tnum, nrow, val, true));
    }

    /**
     * Returns true if a specific field was changed
     *
     * @param nm   - The field name
     * @param tnum - The table number
     * @param nrow - The row number
     * @return boolean - true - the field was changed, false - the field was not changed
     */
    public static boolean isChanged(final String nm,
                                    final int tnum,
                                    final int nrow) {
        Optional<DocumentVar> fld = data
                .stream()
                .filter(f -> f.getField().equals(nm) && f.getTabn() == tnum && f.getNrow() == nrow)
                .findAny();
        if (fld.isPresent()) {
            return fld.get().isDirty();
        }
        return false;
    }
}

