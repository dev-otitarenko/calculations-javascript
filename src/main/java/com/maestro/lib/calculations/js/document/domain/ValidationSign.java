package com.maestro.lib.calculations.js.document.domain;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ValidationSign {
    ALL("*"),
    EQ ("="),
    LT("<"),
    LE("<="),
    GT(">"),
    GE(">="),
    NQ("!=");

    private String name;

    private static final Map<String, ValidationSign> ENUM_MAP;

    ValidationSign (String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    // Build an immutable map of String name to enum pairs.
    // Any Map impl can be used.
    static {
        Map<String, ValidationSign> map = new ConcurrentHashMap<String, ValidationSign>();
        for (ValidationSign instance : ValidationSign.values()) {
            map.put(instance.getName().toLowerCase(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static ValidationSign get (String name) {
        return ENUM_MAP.get(name.toLowerCase());
    }
}
