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
public class PersonalInfo {
    //
    @ExcelColumn(headerName = "Name", columnIndex = 0)
    private String name;
    @ExcelColumn(headerName = "Age", columnIndex = 1)
    private int age;
    @ExcelColumn(headerName = "Gender", columnIndex = 2)
    private String gender;
}
