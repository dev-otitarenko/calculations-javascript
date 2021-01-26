package com.maestro.lib.calculations.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
/**
 * Class describing row in a data set
 */
public class DocumentVar {
    /**
     * A field name
     */
    private String field;
    /**
     * Table number
     */
    private int tabn;
    /**
     * Row number
     */
    private int nrow;
    /**
     * Value
     */
    private Object val;
    /**
     *  Is changed??
     */
    private boolean dirty;

    public DocumentVar(final String field, final int tabn, final int nrow, final Object val) {
        this.field = field;
        this.tabn = tabn;
        this.nrow = nrow;
        this.val = val;
        this.dirty = false;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public String toString() {
        return String.format("DocumentVat { [%b] field: \"%s\", tabn: %d, nrow: %d, val: %s }", this.dirty, this.field, this.tabn, this.nrow, this.val);
    }
}
