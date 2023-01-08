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
public class PersonalInfo {
    //
    @ExcelColumn
    private String name;
    @ExcelColumn
    private int age;
    @ExcelColumn
    private String gender;
}
