package com.maestro.lib.calculations.document;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocManagerUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocManagerUtilsTest.class);

    @ParameterizedTest
    @MethodSource("providerForDocumentManager")
    void validate(final List<DocumentVar> data, final List<ValidateRule> rules) throws ScriptException {
        LOGGER.info("validate [scenario with general streams]");
            // Scenario - 1 (general stream)
        long startTime = System.nanoTime();

        DocManagerUtils docManager = new DocManagerUtils(data, rules);
        List<ValidateError> errors = docManager.validate();
        long timeElapsed = System.nanoTime() - startTime;

        LOGGER.info("validate [scenario with general streams]: Execution time in milliseconds : " + timeElapsed / 1000000);
        errors.forEach(e -> LOGGER.info("validate [scenario with general streams]: {}", e));

        LOGGER.info("validate [scenario with general streams]");

            // Scenario - 2 (parallel stream)
        startTime = System.nanoTime();

        docManager = new DocManagerUtils(data, rules);
        errors = docManager.validateParallel();
        errors.forEach(e -> System.out.println(e.getStatus() + " " + e.getMessage()));

        timeElapsed = System.nanoTime() - startTime;

        System.out.println("[Scenario with parallel stream - 2] Execution time in milliseconds : " + timeElapsed / 1000000);
        errors.forEach(e -> LOGGER.info("validate [scenario with general streams]: {}", e));
    }

    @ParameterizedTest(name = "#{index} - Test with 'rule': {1}, answer: {2}")
    @MethodSource("providerForExecRule")
    void validate( final List<DocumentVar> data, final String rule, final int respond) throws ScriptException {
        DocManagerUtils docManager = new DocManagerUtils(data, new ArrayList<>());
        Object obj = docManager.execRule(new ValidateRule("*", "=", rule, true, true));
        assertEquals((double)obj, respond);
    }

    @ParameterizedTest(name = "#{index} - Test with 'rule': {1}, answer: {2}")
    @MethodSource("calculateProvider")
    void calculate(final List<DocumentVar> data, final List<ValidateRule> rules) throws ScriptException {
        DocManagerUtils docManager = new DocManagerUtils(data, rules);
        docManager.calculate();
        final List<DocumentVar> ret = docManager.data();
        ret.stream().forEach(v -> System.out.println(v));
    }

    static Stream<Arguments> providerForDocumentManager() {
        final List<ValidateRule> rules = Arrays.asList(
                new ValidateRule("FIELD100", "=", "^FIELD1*^FIELD2 + ^FIELD3", true, true),
                new ValidateRule("FIELD101", "=", "^FIELD1 + ^FIELD3", true, true),
                new ValidateRule("FIELD102", "=", "^FIELD1 - ^FIELD3", true, true),
                new ValidateRule("FIELD200", "=", "Math.SUM(^FIELD101,^FIELD102,^FIELD103)", true, true),
                new ValidateRule("FIELD201", "=", "Math.MIN(^FIELD101,^FIELD102,^FIELD103)", true, true),
                new ValidateRule("FIELD202", "=", "Math.MAX(^FIELD101,^FIELD102,^FIELD103)", true, true)
        );
        return Stream.of(
                arguments(Arrays.asList(
                            new DocumentVar("FIELD1", 0, 0, 100),
                            new DocumentVar("FIELD2", 0, 0, 2),
                            new DocumentVar("FIELD3", 0, 0, 50),
                            new DocumentVar("FIELD100", 0, 0, 250),
                            new DocumentVar("FIELD101", 0, 0, 150),
                            new DocumentVar("FIELD102", 0, 0, 50),
                            new DocumentVar("FIELD200", 0, 0, 450),
                            new DocumentVar("FIELD201", 0, 0, 50),
                            new DocumentVar("FIELD202", 0, 0, 250)
                        ),
                        rules
                )
        );
    }

    static Stream<Arguments> calculateProvider() {
        final List<ValidateRule> rules = Arrays.asList(
                new ValidateRule("FIELD100", "=", "^FIELD1*^FIELD2 + ^FIELD3", true, true),
                new ValidateRule("FIELD101", "=", "^FIELD1 + ^FIELD3", true, true),
                new ValidateRule("FIELD102", "=", "^FIELD1 - ^FIELD3", true, true),
                new ValidateRule("FIELD200", "=", "Math.SUM(^FIELD101,^FIELD102,^FIELD103)", true, true),
                new ValidateRule("FIELD201", "=", "Math.MIN(^FIELD101,^FIELD102,^FIELD103)", true, true),
                new ValidateRule("FIELD202", "=", "Math.MAX(^FIELD101,^FIELD102,^FIELD103)", true, true)
        );
        return Stream.of(
                arguments(Arrays.asList(
                            //
                        new DocumentVar("FIELD1", 0, 0, 100),
                        new DocumentVar("FIELD2", 0, 0, 2),
                        new DocumentVar("FIELD3", 0, 0, 50),
                            //
                        new DocumentVar("FIELD100", 0, 0, null),
                        new DocumentVar("FIELD101", 0, 0, null),
                        new DocumentVar("FIELD102", 0, 0, null),
                        new DocumentVar("FIELD200", 0, 0, null),
                        new DocumentVar("FIELD201", 0, 0, null),
                        new DocumentVar("FIELD202", 0, 0, null)
                    ),
                    rules
                )
        );
    }

    static Stream<Arguments> providerForExecRule() {
        final List<DocumentVar> data = Arrays.asList(
                new DocumentVar("FIELD1", 0, 0, 100),
                new DocumentVar("FIELD2", 0, 0, 20),
                new DocumentVar("FIELD3", 0, 0, 500)
        );
        return Stream.of(
                    // Scenario - 1
                arguments(data, "^FIELD1 + ^FIELD3", 600),
                    // Scenario - 2
                arguments(data, "^FIELD1*^FIELD2 + ^FIELD3", 2500),
                    // Scenario - 3
                arguments(data, "^FIELD1 - ^FIELD2", 80),
                // Scenario - 4
                arguments(data, "Math.SUM(^FIELD1,^FIELD2,^FIELD3)", 620),
                // Scenario - 5
                arguments(data, "Math.MIN(^FIELD1,^FIELD2,^FIELD3)", 20),
                // Scenario - 5
                arguments(data, "Math.MAX(^FIELD1,^FIELD2,^FIELD3)", 500),
                // Scenario - 6
                arguments(data, "(^FIELD3 - ^FIELD1)/^FIELD2", 20),
                // Scenario - 7
                arguments(data, "(^FIELD3 > ^FIELD1) ? (^FIELD3-^FIELD1) : (^FIELD1-^FIELD3)", 400)
        );
    }
}