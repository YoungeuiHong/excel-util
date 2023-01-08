package com.lannstark.header;

import com.lannstark.ExcelColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    //
    @ExcelColumn
    private int korean;
    @ExcelColumn
    private int english;
    @ExcelColumn
    private int math;
}
