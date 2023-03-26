package com.lannstark.excel.xssf.onesheet;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lannstark.excel.xssf.XSSFExcelFile;
import com.lannstark.resource.ImportFieldInfo;
import com.lannstark.resource.collection.HeaderNode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.io.InputStream;

import static com.lannstark.utils.JsonUtils.addJsonNode;
import static com.lannstark.utils.JsonUtils.mapper;

public final class OneSheetXSSFExcel<T> extends XSSFExcelFile<T> {

    public OneSheetXSSFExcel(InputStream inputStream, Class type) throws IOException {
        super(inputStream, type);
    }

    public OneSheetXSSFExcel(InputStream inputStream, HeaderNode rootNode) throws IOException {
        super(inputStream, rootNode);
    }

    @Override
    protected void renderJsonData() {
        Sheet sheet = wb.getSheetAt(0);
        for (int rowIdx = resource.getRowStartIdx(); rowIdx <= sheet.getLastRowNum(); rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            ObjectNode rootNode = mapper.createObjectNode();
            for (ImportFieldInfo info: resource.getImportFieldInfos()) {
                Cell cell = row.getCell(info.getColumnIndex());
                String value = dataFormatter.formatCellValue(cell);
                addJsonNode(rootNode, info.getFieldSimpleName(), info.getJsonPointer(), value);
            }
            this.data.add(rootNode);
        }
    }
}
