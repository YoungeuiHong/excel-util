package com.lannstark.excel.import_;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lannstark.resource.ExcelImporterResource;
import com.lannstark.resource.ExcelImporterResourceFactory;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.lannstark.utils.JsonUtils.gson;
import static com.lannstark.utils.JsonUtils.mapper;

public abstract class XSSFExcelFile<T> implements ImportedExcel<T> {
    protected XSSFWorkbook wb;
    protected ExcelImporterResource resource;
    protected Class<T> dataType;
    protected List<ObjectNode> data = new ArrayList<>();
    protected final DataFormatter dataFormatter = new DataFormatter();

    protected XSSFExcelFile(InputStream inputStream, Class<T> type) throws IOException {
        this.wb = new XSSFWorkbook(inputStream);
        this.resource = ExcelImporterResourceFactory.prepareImporterResource(type);
        this.dataType = type;
        renderJsonData();
    }

    protected void renderJsonData() {}

    public List<Object> read() throws IOException {
        List<Object> dtoList = new ArrayList<>();
        for (ObjectNode rootNode : data) {
            String json = mapper.writeValueAsString(rootNode);
            Object dto = gson.fromJson(json, this.dataType);
            dtoList.add(dto);
        }
        return dtoList;
    }


}


