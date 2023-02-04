package com.lannstark.excel;

import com.lannstark.dto.Grade;
import com.lannstark.dto.PersonalInfo;
import com.lannstark.dto.StudentDto;
import com.lannstark.excel.onesheet.OneSheetExcelFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExcelFileTest {
    // 파일 저장 경로
    private final String RESOURCES_PATH = String.join(File.separator, ".", "src", "test", "resources");

    @Test
    @DisplayName("Create OneSheetExcelFile")
    public void createOneSheetExcelFile() {
        //
        ExcelFile excelFile = new OneSheetExcelFile(getSampleStudentDtoList(), StudentDto.class);

        try {
            String fileName = String.join(File.separator, RESOURCES_PATH, "excel_test.xlsx");
            excelFile.write(new FileOutputStream(new File(fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<StudentDto> getSampleStudentDtoList() {
        //
        List<StudentDto> data = Arrays.asList(
                new StudentDto(
                        new PersonalInfo("Alice", 10, "Female"),
                        new Grade(10, 20, 30)
                ),
                new StudentDto(
                        new PersonalInfo("Edward", 20, "Male"),
                        new Grade(40, 50, 60)
                ),
                new StudentDto(
                        new PersonalInfo("Katie", 30, "Female"),
                        new Grade(70, 80, 90)
                )
        );

        return data;
    }

}
