package com.lannstark.excel.xssf;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lannstark.excel.ExcelFile;
import com.lannstark.resource.ExcelImporterResource;
import com.lannstark.resource.ExcelImporterResourceFactory;
import com.lannstark.resource.collection.HeaderNode;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.lannstark.utils.JsonUtils.gson;
import static com.lannstark.utils.JsonUtils.mapper;

public abstract class XSSFExcelFile<T> implements ExcelFile<T> {
    protected XSSFWorkbook wb;
    protected ExcelImporterResource resource;
    protected List<ObjectNode> data = new ArrayList<>();
    protected List<Map<String, Object>> flatData = new ArrayList<>();
    protected final DataFormatter dataFormatter = new DataFormatter();

    protected XSSFExcelFile(InputStream inputStream, Class<T> type) throws IOException {
        this.wb = new XSSFWorkbook(inputStream);
        this.resource = ExcelImporterResourceFactory.prepareImporterResource(type);
        renderJsonData();
    }

    protected XSSFExcelFile(InputStream inputStream, HeaderNode rootNode) throws  IOException {
        this.wb = new XSSFWorkbook(inputStream);
        this.resource = ExcelImporterResourceFactory.prepareImporterResource(rootNode);
        renderJsonData();
    }

    protected void renderJsonData() {}

    public <T> List<T> read(Class<T> type) throws IOException {
        List<T> dtoList = new ArrayList<>();
        for (ObjectNode rootNode : data) {
            String json = mapper.writeValueAsString(rootNode);
            Object dto = gson.fromJson(json, type);
            dtoList.add(type.cast(dto));
        }
        return dtoList;
    }

    public List<Object> read() throws IOException {
        List<Object> dataList = new ArrayList<>();
        for (ObjectNode node : data) {
            String json = mapper.writeValueAsString(node);
            dataList.add(gson.fromJson(json, Object.class));
        }
        return dataList;
    }

    public List<Map<String, Object>> readFlat() {
        //
        return flatData;
    }

    public void write(OutputStream stream) throws IOException {
        wb.write(stream);
        wb.close();
        stream.close();
    }

    public void addRows(List<T> data) {}


}


