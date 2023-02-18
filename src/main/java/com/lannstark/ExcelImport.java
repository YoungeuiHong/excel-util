package com.lannstark;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImport {
    int rowStartIndex() default -1;
    int columnStartIndex() default 0;
    boolean isColumnIndexAssigned() default false;
}
