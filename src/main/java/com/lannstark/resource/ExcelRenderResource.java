package com.lannstark.resource;

import com.lannstark.resource.collection.PreCalculatedCellStyleMap;
import lombok.Getter;
import org.apache.poi.ss.usermodel.CellStyle;

import java.util.List;

@Getter
public class ExcelRenderResource {
    private PreCalculatedCellStyleMap styleMap;
    private ExcelHeader excelHeader;
    private List<String> fieldPaths;
    private List<String> leafFieldPaths;

    public ExcelRenderResource(
            PreCalculatedCellStyleMap styleMap,
            ExcelHeader excelHeader,
            List<String> fieldPaths,
            List<String> leafFieldPaths
    ) {
        this.styleMap = styleMap;
        this.excelHeader = excelHeader;
        this.fieldPaths = fieldPaths;
        this.leafFieldPaths = leafFieldPaths;
    }

    public CellStyle getCellStyle(String dataFieldName, ExcelRenderLocation excelRenderLocation) {
        return styleMap.get(ExcelCellKey.of(dataFieldName, excelRenderLocation));
    }

}
