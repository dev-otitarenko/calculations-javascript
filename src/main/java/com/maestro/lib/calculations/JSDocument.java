package com.maestro.lib.calculations;

import com.maestro.lib.calculations.document.DocumentVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JSDocument {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSDocument.class);

    private static List<DocumentVar> data = new ArrayList<>();

    public static void set(final List<DocumentVar> v) {
        data = v;
    }

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
//       data
//          .stream()
//          .map(f -> f.getField().equals(nm) && f.getTabn() == tnum && f.getNrow() == nrow ? f.setVal(val);)
//        LOGGER.info("put {}, {}, {}, \"{}\"", nm, tnum, nrow, val);
//
//        List<DocumentVar> ret = new ArrayList<>();
//        ret.addAll(data);
//        ret.add(new DocumentVar(nm, tnum, nrow, val, true));
//
//        data = ret;
    }

    public static List<DocumentVar> get() {
        return data;
    }
}
