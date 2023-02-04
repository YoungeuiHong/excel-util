package com.lannstark.resource;

import com.lannstark.ExcelColumn;
import com.lannstark.dto.ExcelDto;
import com.lannstark.dto.StudentDto;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ExcelRenderResourceFactoryTest {

    @Test
    public void excelRenderResourceCreationTest() {
        // given & when
        ExcelRenderResource resource
                = ExcelRenderResourceFactory.prepareRenderResource(ExcelDto.class, new SXSSFWorkbook(), new DefaultDataFormatDecider());

        // then
        assertThat(resource.getFieldPaths()).isEqualTo(Arrays.asList("name", "age"));

        assertCenterThinCellStyle(resource.getCellStyle("name", ExcelRenderLocation.HEADER), (byte) 223, (byte) 235, (byte) 246);
        assertCenterThinCellStyle(resource.getCellStyle("age", ExcelRenderLocation.HEADER), (byte) 0, (byte) 0, (byte) 0);
    }

    private void assertCenterThinCellStyle(CellStyle cellStyle,
                                           byte red, byte green, byte blue) {
        assertThat(cellStyle.getAlignment()).isEqualTo(HorizontalAlignment.CENTER);
        assertThat(cellStyle.getVerticalAlignment()).isEqualTo(VerticalAlignment.CENTER);
        assertThat(cellStyle.getBorderTop()).isEqualTo(BorderStyle.THIN);
        assertThat(cellStyle.getBorderRight()).isEqualTo(BorderStyle.THIN);
        assertThat(cellStyle.getBorderLeft()).isEqualTo(BorderStyle.THIN);
        assertThat(cellStyle.getBorderBottom()).isEqualTo(BorderStyle.THIN);
        XSSFColor nameHeaderCellColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
        assertThat(nameHeaderCellColor.getRGB()).isEqualTo(new Byte[]{red, green, blue});
    }

    @Test
    @DisplayName("generate field path")
    public void generateFieldPath() {
        List<FieldPathInfo> fieldPathInfos = getAllFieldPathInfo(StudentDto.class);
        List<String> fieldPaths = fieldPathInfos.stream().map(FieldPathInfo::getFieldPath).collect(Collectors.toList());
        assertThat(fieldPaths.containsAll(
                Arrays.asList(
                        "personalInfo",
                        "grade",
                        "personalInfo,name",
                        "personalInfo,age",
                        "personalInfo,gender",
                        "grade,korean",
                        "grade,english",
                        "grade,math"
                )
        ));
    }

    /**
     * 부모 Field의 fieldPath와 자신의 Field를 사용하여 fieldPath를 생성하는 메서드
     * @param fieldPath
     * @param field
     * @return
     */
    private FieldPathInfo getFieldPathInfo(String fieldPath, Field field) {
        if (StringUtils.isNotBlank(fieldPath)) {
            return new FieldPathInfo(String.format("%s,%s", fieldPath, field.getName()), field);
        } else {
            return new FieldPathInfo(field.getName(), field);
        }

    }

    /**
     * 클래스 내에서 @ExcelColumn 어노테이션이 붙은 모든 필드의 FieldPathInfo 리스트를 생성하는 메서드
     * @param clazz
     * @return
     */
    private List<FieldPathInfo> getAllFieldPathInfo(Class<?> clazz) {
        //
        Queue<FieldPathInfo> queue = new LinkedList<>();
        List<FieldPathInfo> fieldPathInfos = Arrays.stream(clazz.getDeclaredFields()).map(field -> getFieldPathInfo("", field)).collect(Collectors.toList());
        queue.addAll(fieldPathInfos);

        while (!queue.isEmpty()) {
            int queueSize = queue.size();

            for (int i = 0; i < queueSize; i++) {
                FieldPathInfo fieldPathInfo = queue.poll();
                String currFieldPath = fieldPathInfo.getFieldPath();
                Field field = fieldPathInfo.getField();
                // 추가적으로 탐색해야 하는 자식 요소를 Queue에 넣기
                for (Field _child : field.getType().getDeclaredFields()) {
                    if (_child.isAnnotationPresent(ExcelColumn.class)) {
                        FieldPathInfo childFieldPathInfo = getFieldPathInfo(currFieldPath, _child);
                        fieldPathInfos.add(childFieldPathInfo);
                        queue.add(childFieldPathInfo);
                    }
                }
            }
        }

        return fieldPathInfos;
    }

}
