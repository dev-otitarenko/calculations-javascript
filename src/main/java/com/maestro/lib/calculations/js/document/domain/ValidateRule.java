package com.maestro.lib.calculations.js.document.domain;

import com.maestro.lib.calculations.utils.RegexpUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValidateRule {
    /**
     * Field name
     */
    private String field;
    /**
     * Sign: =, >, <, >=, <=, <>, !=
     **/
    private String sign;
    /**
     * Expression for calculation. Example: A*B + C/100
     */
    private String expression;
    /**
     * Reserved
     */
    private boolean onlyFilling = true;
    /**
     * Reserved
     */
    private boolean onlyChecking = true;

    public static String parse(final String expression, final int tabNum, final int rowNum) {
        String ret = expression;
        final List<String> fields = RegexpUtils.getMatches(ret, "(\\^\\w+)");
        if (fields.size() != 0) {
            for (String f : fields) {
                final String nm = f.substring(1);
                ret = ret.replaceAll("\\^" + nm, "Doc.value(\"" + nm + "\", " + tabNum + ", " + rowNum + ")");
            }
        }
        return ret;
    }

    public boolean contains(final String nm) {
        final List<String> fields = RegexpUtils.getMatches(expression, "(\\^\\w+)");
        return fields.contains(nm);
    }
}
