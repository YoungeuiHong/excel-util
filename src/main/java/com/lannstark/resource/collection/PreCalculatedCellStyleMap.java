package com.lannstark.resource.collection;

import com.lannstark.resource.DataFormatDecider;
import com.lannstark.resource.ExcelCellKey;
import com.lannstark.style.ExcelCellStyle;
import com.lannstark.style.align.DefaultExcelAlign;
import com.lannstark.style.border.DefaultExcelBorders;
import com.lannstark.style.border.ExcelBorderStyle;
import com.lannstark.style.configurer.ExcelCellStyleConfigurer;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * PreCalculatedCellStyleMap
 *
 * Determines cell's style
 * In currently, PreCalculatedCellStyleMap determines {org.apache.poi.ss.usermodel.DataFormat}
 */
public class PreCalculatedCellStyleMap {

    private final DataFormatDecider dataFormatDecider;

    private final Map<ExcelCellKey, CellStyle> cellStyleMap = new HashMap<>();

    private final ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

    public PreCalculatedCellStyleMap(DataFormatDecider dataFormatDecider) {
        this.dataFormatDecider = dataFormatDecider;
    }

    public void put(Class<?> fieldType, ExcelCellKey excelCellKey, ExcelCellStyle excelCellStyle, Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        DataFormat dataFormat = wb.createDataFormat();
        cellStyle.setDataFormat(dataFormatDecider.getDataFormat(dataFormat, fieldType));
        excelCellStyle.apply(cellStyle);
        cellStyleMap.put(excelCellKey, cellStyle);
    }

    public void put(String fieldType, ExcelCellKey excelCellKey, Map<String, Integer> rgb, Workbook wb) {

        XSSFCellStyle cellStyle = (XSSFCellStyle) wb.createCellStyle();
        DataFormat dataFormat = wb.createDataFormat();

        configurer
                .foregroundColor(
                        rgb.getOrDefault("r", 255),
                        rgb.getOrDefault("g", 255),
                        rgb.getOrDefault("b", 255)
                )
                .excelAlign(DefaultExcelAlign.CENTER_CENTER)
                .excelBorders(DefaultExcelBorders.newInstance(ExcelBorderStyle.THIN))
                .configure(cellStyle);

        switch (fieldType) {
            case "String":
                cellStyle.setDataFormat(dataFormatDecider.getDataFormat(dataFormat, String.class));
                break;
            case "Float":
                cellStyle.setDataFormat(dataFormatDecider.getDataFormat(dataFormat, Float.class));
                break;
            case "Integer":
                cellStyle.setDataFormat(dataFormatDecider.getDataFormat(dataFormat, Integer.class));
                break;
        }

        cellStyleMap.put(excelCellKey, cellStyle);
    }

    public CellStyle get(ExcelCellKey excelCellKey) {
        return cellStyleMap.get(excelCellKey);
    }

    public boolean isEmpty() {
        return cellStyleMap.isEmpty();
    }

}
