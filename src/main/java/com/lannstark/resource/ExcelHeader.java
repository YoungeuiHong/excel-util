package com.lannstark.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelHeader {
    //
    private int headerHeight;
    private Map<String, ExcelHeaderCell> excelHeaderCellMap = new HashMap<>();

    public void put(String fieldPath, ExcelHeaderCell excelHeaderCell) {
        //
        this.excelHeaderCellMap.put(fieldPath, excelHeaderCell);
    }

    public ExcelHeaderCell getExcelHeaderCell(String fieldPath) {
        //
        return this.excelHeaderCellMap.get(fieldPath);
    }
}
