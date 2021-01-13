package com.maestro.lib.jscript;

import lombok.Data;

/*
* Specific validation rule
 */
@Data
public class ValidateRule {
    private String field; // field name
    private String sign; // sign
    private String expression; // expression
    private boolean onlyFilling;
    private boolean onlyChecking;
}
