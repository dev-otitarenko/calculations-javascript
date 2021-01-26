package com.maestro.lib.calculations.document;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maestro.lib.calculations.utils.TestUtils;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocManagerUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocManagerUtilsTest.class);

    @ParameterizedTest
    @CsvSource(value = { "scenario-1-data-valid.json,scenario-1-rules.json"})
    void validate(final String path2document, final String path2rules) throws IOException, ScriptException {
        Gson gson = new Gson();
        final List<DocumentVar> data = convDocumentData(gson, path2document);
        final List<ValidateRule> rules = convValidateRules(gson, path2rules);

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

    @ParameterizedTest(name = "#{index} - Test with 'rule': {0}, answer: {1}")
    @CsvSource(value = {
            "^FIELD1 + ^FIELD3#600",
            "^FIELD1*^FIELD2 + ^FIELD3#2500",
            "^FIELD1 - ^FIELD2#80",
            "Math.SUM(^FIELD1,^FIELD2,^FIELD3)#620",
            "Math.MIN(^FIELD1,^FIELD2,^FIELD3)#20",
            "Math.MAX(^FIELD1,^FIELD2,^FIELD3)#500",
            "(^FIELD3 - ^FIELD1)/^FIELD2#20",
            "(^FIELD3 > ^FIELD1) ? (^FIELD3-^FIELD1) : (^FIELD1-^FIELD3)#400"
    }, delimiter = '#')
    void validate(final String rule, final int respond) throws ScriptException {
        final List<DocumentVar> data = Arrays.asList(
                new DocumentVar("FIELD1", 0, 0, 100),
                new DocumentVar("FIELD2", 0, 0, 20),
                new DocumentVar("FIELD3", 0, 0, 500)
        );

        DocManagerUtils docManager = new DocManagerUtils(data, new ArrayList<>());
        Object obj = docManager.execRule(new ValidateRule("*", "=", rule, true, true));
        assertEquals((double)obj, respond);
    }

    @ParameterizedTest
    @CsvSource(
            value = { "scenario-3-data.json:scenario-3-rules.json:scenario-3-result.json" },
            delimiter = ':'
    )
    void calculate(final String path2document,
                   final String path2rules,
                   final String path2result) throws ScriptException, IOException {
        Gson gson = new Gson();
        final List<DocumentVar> data = convDocumentData(gson, path2document);
        final List<ValidateRule> rules = convValidateRules(gson, path2rules);
        final List<DocumentVar> result = convDocumentData(gson, path2result);

        DocManagerUtils docManager = new DocManagerUtils(data, rules);
        docManager.calculate();
        final List<DocumentVar> ret = docManager.data();
        checkDocumentData(ret, result);
    }

    private List<DocumentVar> convDocumentData(Gson gson, final String fileName) throws IOException {
        final byte[] content = TestUtils.getFileContent((new TestUtils()).getResourceFile(fileName));
        Type typeData = new TypeToken<List<DocumentVar>>() {}.getType();
        final List<DocumentVar> data = gson.fromJson(new String(content), typeData);

        return data;
    }

    private List<ValidateRule> convValidateRules(Gson gson, final String fileName) throws IOException {
        final byte[] content = TestUtils.getFileContent((new TestUtils()).getResourceFile(fileName));
        Type typeRules = new TypeToken<List<ValidateRule>>() {}.getType();
        final List<ValidateRule> rules = gson.fromJson(new String(content), typeRules);

        return rules;
    }

    private void checkDocumentData(final List<DocumentVar> data_1,
                                   final List<DocumentVar> data_2) {
        data_1
                .stream()
                .forEach(f -> {
                    final DocumentVar val_res = data_2
                                                .stream()
                                                .filter(f1 -> f1.getField().equals(f.getField()) && f1.getTabn() == f.getTabn() && f1.getNrow() == f.getTabn())
                                                .findAny().orElse(null);
                    assertNotNull(val_res);
                    assertEquals(val_res.getVal(), f.getVal());
                });
    }
}