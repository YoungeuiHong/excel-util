package com.lannstark.dto;

import com.lannstark.DefaultHeaderStyle;
import com.lannstark.ExcelColumn;
import com.lannstark.ExcelColumnStyle;
import com.lannstark.resource.ExcelRenderLocation;
import com.lannstark.style.BlueHeaderStyle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DefaultHeaderStyle(style = @ExcelColumnStyle(excelCellStyleClass = BlueHeaderStyle.class))
public class VariousTypeDto {
    @ExcelColumn(headerName = "byte")
    byte byteVal;
    @ExcelColumn(headerName = "short")
    short shortVal;
    @ExcelColumn(headerName = "int")
    int intVal;
    @ExcelColumn(headerName = "long")
    long longVal;
    @ExcelColumn(headerName = "float")
    float floatVal;
    @ExcelColumn(headerName = "double")
    double doubleVal;
    @ExcelColumn(headerName = "boolean")
    boolean booleanVal;
    @ExcelColumn(headerName = "char")
    char charVal;
    @ExcelColumn(headerName = "String")
    String stringVal;
    @ExcelColumn(headerName = "enum")
    ExcelRenderLocation enumVal;

    public static VariousTypeDto sample() {
        return new VariousTypeDto(
                Byte.MAX_VALUE,
                Short.MAX_VALUE,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                Float.MAX_VALUE,
                Double.MAX_VALUE,
                true,
                'a',
                "String",
                ExcelRenderLocation.BODY
        );
    }
}
