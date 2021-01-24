package com.maestro.lib.calculations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSMathModuleTest {
    @Test
    void MIN_with_valid_values() {
        double ret = JSMathModule.MIN(100, "300", 400, 240);
        assertEquals(ret, 100);
    }

    @Test
    void MIN_with_invalid_values_1() {
        double ret = JSMathModule.MIN(200, 100, "ggg", "dddd");
        assertEquals(ret, 100);
    }

    @Test
    void MIN_with_all_invalid_values() {
        double ret = JSMathModule.MIN("ggg", "dddd");
        assertEquals(ret, 0);
    }

    @Test
    void MAX_with_valid_values() {
        double ret = JSMathModule.MAX("100", 300, 400, 240);
        assertEquals(ret, 400);
    }

    @Test
    void MAX_with_invalid_values_1() {
        double ret = JSMathModule.MAX(100, 300, 400, 240, "gggg", "dddd");
        assertEquals(ret, 400);
    }

    @Test
    void MAX_with_all_invalid_values() {
        double ret = JSMathModule.MAX("ggg", "dddd");
        assertEquals(ret, 0);
    }
//
//    @Test
//    void AVG() {
//        double ret = JSMathModule.AVG(100, 200, 300);
//        assertEquals(ret, 200);
//    }
//
    @Test
    void SUM_with_valid_values_1() {
        double ret = JSMathModule.SUM(100, "200", 300);
        assertEquals(ret, 600);
    }

    @Test
    void SUM_with_valid_values_2() {
        double ret = JSMathModule.SUM(10, 50, 120.50);
        assertEquals(ret, 180.50);
    }

    @Test
    void SUM_with_invalid_values_1() {
        double ret = JSMathModule.SUM("100", "ggg", "12ggg");
        assertEquals(ret, 100);
    }

    @Test
    void SUM_with_all_invalid_values() {
        double ret = JSMathModule.SUM("ggg", "12ggg");
        assertEquals(ret, 0);
    }
}