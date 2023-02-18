package com.lannstark.dto;

import com.lannstark.*;
import com.lannstark.style.DefaultExcelCellStyle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DefaultHeaderStyle(
        style = @ExcelColumnStyle(excelCellStyleClass = DefaultExcelCellStyle.class, enumName = "BLUE_HEADER")
)
@DefaultBodyStyle(
        style = @ExcelColumnStyle(excelCellStyleClass = DefaultExcelCellStyle.class, enumName = "BODY")
)
@ExcelImport(
        isColumnIndexAssigned = true
)
public class StudentDto {
    //
    @ExcelColumn(headerName = "Personal Info")
    private PersonalInfo personalInfo;
    @ExcelColumn(headerName = "Grade")
    private Grade grade;
}
