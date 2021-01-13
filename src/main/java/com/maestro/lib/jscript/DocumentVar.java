package com.maestro.lib.jscript;

import lombok.Data;

@Data
public class DocumentVar {
    private String field; // field name
    private String tabn; // table number
    private int nrow; // row number
    private Object val; // value
}
