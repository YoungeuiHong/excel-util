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
public class StudentDto {
    //
    @ExcelColumn
    private PersonalInfo personalInfo;
    @ExcelColumn
    private Grade grade;
}
