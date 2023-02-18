package com.lannstark.dto;

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
    @ExcelColumn(headerName = "Korean", columnIndex = 5)
    private int korean;
    @ExcelColumn(headerName = "English", columnIndex = 4)
    private int english;
    @ExcelColumn(headerName = "Math", columnIndex = 3)
    private int math;
}
