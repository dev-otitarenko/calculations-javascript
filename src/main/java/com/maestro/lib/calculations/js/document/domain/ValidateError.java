package com.maestro.lib.calculations.js.document.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
/**
 * Class describing a validation error
 */
public class ValidateError {
    /**
     * Status of validation: 1 if all is Ok, else 0
     */
    private int status;
    /**
     * Message with error if status != 0
     */
    private String message;

    @Override
    public String toString() {
        return String.format("ValidateError { status: %d, message: '%s' }", status, message);
    }
}
