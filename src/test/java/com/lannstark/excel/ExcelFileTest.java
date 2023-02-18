package com.lannstark.excel;

import com.google.gson.Gson;
import com.lannstark.dto.Grade;
import com.lannstark.dto.PersonalInfo;
import com.lannstark.dto.StudentDto;
import com.lannstark.dto.VariousTypeDto;
import com.lannstark.excel.sxssf.onesheet.OneSheetExcelFile;
import com.lannstark.excel.xssf.onesheet.OneSheetXSSFExcel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ExcelFileTest {
    // 파일 저장 경로
    private Gson gson = new Gson();
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

    @Test
    @DisplayName("Import one sheet XSSF Excel file (Case: various value type)")
    public void importOneSheetXSSFExcelFile() throws IOException {
        createVariousTypeExcel();
        FileInputStream inputStream = new FileInputStream(new File(String.join(File.separator, RESOURCES_PATH, "excel_type_test.xlsx")));
        ExcelFile<VariousTypeDto> excelFile = new OneSheetXSSFExcel(inputStream, VariousTypeDto.class);
        List<VariousTypeDto> data = excelFile.read(VariousTypeDto.class);
        assertThatVariousTypeData(data);
        System.out.println(gson.toJson(data));
    }

    @Test
    @DisplayName("Import one sheet XSSF Excel file (Case: multi depth header)")
    public void importStudentOneSheetXSSFExcelFile() throws IOException {
        createMultiDepthHeaderExcel();
        FileInputStream inputStream = new FileInputStream(new File(String.join(File.separator, RESOURCES_PATH, "excel_test.xlsx")));
        ExcelFile<StudentDto> excelFile = new OneSheetXSSFExcel(inputStream, StudentDto.class);
        List<StudentDto> data = excelFile.read(StudentDto.class);
        assertThatStudentDtoData(data);
        System.out.println(gson.toJson(data));
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

    private void createVariousTypeExcel() throws IOException {
        ExcelFile excelFile = new OneSheetExcelFile(Arrays.asList(VariousTypeDto.sample()), VariousTypeDto.class);
        String fileName = String.join(File.separator, RESOURCES_PATH, "excel_type_test.xlsx");
        excelFile.write(new FileOutputStream(new File(fileName)));
    }

    private void createMultiDepthHeaderExcel() throws IOException {
        ExcelFile excelFile = new OneSheetExcelFile(getSampleStudentDtoList(), StudentDto.class);
        String fileName = String.join(File.separator, RESOURCES_PATH, "excel_test.xlsx");
        excelFile.write(new FileOutputStream(new File(fileName)));
    }

    private void assertThatVariousTypeData(List<VariousTypeDto> data) {
        VariousTypeDto dto = data.get(0);
        VariousTypeDto sample = VariousTypeDto.sample();
        assertThat(dto.getByteVal() == sample.getByteVal());
        assertThat(dto.getShortVal() == sample.getShortVal());
        assertThat(dto.getIntVal() == sample.getIntVal());
        assertThat(dto.getLongVal() == sample.getLongVal());
        assertThat(dto.getFloatVal() == sample.getFloatVal());
        assertThat(dto.getDoubleVal() == sample.getDoubleVal());
        assertThat(dto.isBooleanVal() == sample.isBooleanVal());
        assertThat(dto.getCharVal() == sample.getCharVal());
        assertThat(dto.getStringVal() == sample.getStringVal());
        assertThat(dto.getEnumVal() == sample.getEnumVal());
    }

    private void assertThatStudentDtoData(List<StudentDto> data) {
        Optional<StudentDto> found = data.stream().filter(studentDto -> studentDto.getPersonalInfo().getName().equals("Alice")).findFirst();
        if (found.isPresent()) {
            StudentDto dto = found.get();
            assertThat(dto.getPersonalInfo().getAge() == 10);
            assertThat(dto.getPersonalInfo().getGender().equals("Female"));
            assertThat(dto.getGrade().getKorean() == 10);
            assertThat(dto.getGrade().getEnglish() == 20);
            assertThat(dto.getGrade().getMath() == 30);
        }
    }
}
