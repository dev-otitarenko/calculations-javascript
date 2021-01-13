package com.maestro.lib.jscript;

import java.util.List;

public class DocumentManagerUtils {
    private List<DocumentVar> data;

    public DocumentManagerUtils(final List<DocumentVar> data) {
        this.data = data;
    }

    public void validate(final List<ValidateRule> rules) {
        rules
                .parallelStream()
                .filter(r -> r.isOnlyChecking() && !r.getSign().equals("*"))
                .forEach(r -> {

                });

    }
}
