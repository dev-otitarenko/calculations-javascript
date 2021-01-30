package com.maestro.lib.calculations.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regexp utils
 */
public class RegexpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegexpUtils.class);

    /**
     *
     * @param s input string
     * @param p regexp patter
     * @return List list of string
     */
    public static List<String> getMatches(String s, String p) {
        LOGGER.trace("getMatches: {}, {}", s, p);

        List<String> matches = new ArrayList<>();
        final Matcher m = Pattern.compile(p).matcher(s);
        while(m.find()) {
            matches.add(m.group(1));
        }

        matches.stream().parallel().forEach(i -> LOGGER.trace("getMatches: {}", i));

        return matches;
    }
}
