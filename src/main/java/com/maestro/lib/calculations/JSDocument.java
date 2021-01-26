package com.maestro.lib.calculations;

import com.maestro.lib.calculations.document.DocumentVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JSDocument {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSDocument.class);

    private static List<DocumentVar> data = new ArrayList<>();

    /**
     * Sets a document data for using
     *
     * @param v The document data set
     */
    public static void set(final List<DocumentVar> v) {
        data = v;
    }

    /**
     * Gets the field value.
     *
     * @param nm The field name
     * @param tnum - The table number
     * @param nrow - The row number
     * @return Object - The field value
     */
    public static Object getValue(final String nm, final int tnum, final int nrow) {
        Optional<DocumentVar> fld = data
                                    .stream()
                                    .filter(f -> f.getField().equals(nm) && f.getTabn() == tnum && f.getNrow() == nrow)
                                    .findAny();
        if (fld.isPresent()) {
            return fld.get().getVal();
        }
        return null;
    }

    /**
     * Sets the field value for a specific field
     *
     * @param nm - The field name
     * @param tnum - The table number
     * @param nrow - The row number
     * @param val - The value
     */
    public static void setValue(final String nm,
                                final int tnum,
                                final int nrow,
                                final Object val) {
       for (int i = 0; i < data.size(); i++) {
           DocumentVar f = data.get(i);
           if (f.getField().equals(nm) && f.getTabn() == tnum && f.getNrow() == nrow) {
               f.setVal(val);
               f.setDirty(true);

               return;
           }
       }

        data.add(new DocumentVar(nm, tnum, nrow, val, true));
    }

    /**
     * Returns true if a specific field was changed
     *
     * @param nm - The field name
     * @param tnum - The table number
     * @param nrow - The row number
     * @return boolean - true - the field was changed, false - the field was not changed
     */
    public static boolean isChanged(final String nm,
                                    final int tnum,
                                    final int nrow) {
        Optional<DocumentVar> fld = data
                .stream()
                .filter(f -> f.getField().equals(nm) && f.getTabn() == tnum && f.getNrow() == nrow)
                .findAny();
        if (fld.isPresent()) {
            return fld.get().isDirty();
        }
        return false;
    }

    /**
     * Gets the document data set
     *
     * @return List - The document data set
     */
    public static List<DocumentVar> get() {
        return data;
    }
}
