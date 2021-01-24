package com.maestro.lib.jscript.document;

import com.maestro.lib.jscript.ScriptEngineManagerUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentManagerUtilsTest {
    private ScriptEngine engine;


    @BeforeAll
    public void setUp() throws ScriptException {
        engine = ScriptEngineManagerUtils.scriptEngine("nashorn");
    }

    @ParameterizedTest(name = "#{index} - Test with 'rule': {0}")
    @MethodSource("providerForDocumentManager")
    void validate(final String scenario, final List<DocumentVar> data, final List<ValidateRule> rules) throws ScriptException {
        DocumentManagerUtils docManager = new DocumentManagerUtils(data, rules);
        final List<ValidateError> errors = docManager.validate(engine);
        errors.forEach(e -> System.out.println(e.getStatus() + " " + e.getMessage()));
        assertEquals(errors.size(), 3);
    }

    static Stream<Arguments> providerForDocumentManager() {
        return Stream.of(
                arguments("Scenario-1, AllValid",
                         Arrays.asList(
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
                        Arrays.asList(
                            new ValidateRule("FIELD100", "=", "FIELD1*FIELD2 + FIELD3", true, true),
                            new ValidateRule("FIELD101", "=", "FIELD1 + FIELD3", true, true),
                            new ValidateRule("FIELD102", "=", "FIELD1 - FIELD3", true, true),
                            //
                            new ValidateRule("FIELD200", "=", "Math.SUM(FIELD101,FIELD102,FIELD103)", true, true),
                            new ValidateRule("FIELD201", "=", "Math.MIN(FIELD101,FIELD102,FIELD103)", true, true),
                            new ValidateRule("FIELD202", "=", "Math.MAX(FIELD101,FIELD102,FIELD103)", true, true)
                        )
                )
        );
    }
}