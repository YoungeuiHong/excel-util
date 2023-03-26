package com.lannstark.resource;

import com.lannstark.ExcelColumn;
import com.lannstark.ExcelImport;
import com.lannstark.resource.collection.HeaderNode;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.lannstark.resource.ExcelRenderResourceFactory.getHeightOfHeader;
import static com.lannstark.utils.SuperClassReflectionUtils.getAnnotation;

public final class ExcelImporterResourceFactory {

    private static int ROW_START_IDX = 0;
    private static int COLUMN_START_IDX = 0;
    private static boolean isColumnIndexAssigned = false;

    public static ExcelImporterResource prepareImporterResource(Class<?> type) {
        setExcelImportInfo(type);
        List<ImportFieldInfo> importFieldInfoList = getImportFieldInfos(type);
        return new ExcelImporterResource(importFieldInfoList, ROW_START_IDX, COLUMN_START_IDX);
    }

    public static ExcelImporterResource prepareImporterResource(HeaderNode rootNode) {
        List<ImportFieldInfo> importFieldInfoList = getImportFieldInfos(rootNode);
        return new ExcelImporterResource(importFieldInfoList, rootNode.getHeightOfHeaderNode(), COLUMN_START_IDX);
    }

    private static void setExcelImportInfo(Class<?> type) {
        ExcelImport excelImport = (ExcelImport) getAnnotation(type, ExcelImport.class);
        if (excelImport == null || excelImport.rowStartIndex() < 0) {
            ROW_START_IDX = getHeightOfHeader(type);
        } else {
            ROW_START_IDX = excelImport.rowStartIndex();
        }
        if (excelImport == null) return;
        COLUMN_START_IDX = excelImport.columnStartIndex();
        isColumnIndexAssigned = excelImport.isColumnIndexAssigned();
    }

    private static List<ImportFieldInfo> getImportFieldInfos(Class<?> type) {
        List<ImportFieldInfo> importFieldInfos = new ArrayList<>();

        Queue<FieldPathInfo> fieldQueue = new LinkedList<>();
        List<FieldPathInfo> fieldInfos = Arrays.stream(type.getDeclaredFields()).map(field -> getFieldPathInfo("", field)).collect(Collectors.toList());
        fieldQueue.addAll(fieldInfos);
        int columnIndex = COLUMN_START_IDX;

        while (!fieldQueue.isEmpty()) {
            int queueSize = fieldQueue.size();
            for (int i = 0; i < queueSize; i++) {
                FieldPathInfo fieldInfo = fieldQueue.poll();
                String currFieldPath = fieldInfo.getFieldPath();
                Field field = fieldInfo.getField();

                // 추가적으로 탐색해야 하는 자식 요소는 Queue에 넣기
                List<FieldPathInfo> childFieldInfos = new ArrayList<>();
                for (Field _child : field.getType().getDeclaredFields()) {
                    if (_child.isAnnotationPresent(ExcelColumn.class)) {
                        FieldPathInfo childFieldInfo = getFieldPathInfo(currFieldPath, _child);
                        fieldInfos.add(childFieldInfo);
                        childFieldInfos.add(childFieldInfo);
                    }
                }
                fieldQueue.addAll(childFieldInfos);


                // 자식 요소가 더 이상 없으면 말단 필드 목록에 추가
                if (childFieldInfos.isEmpty()) {
                    // @ExcelColumn 어노테이션 정보 가져오기
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    if (isColumnIndexAssigned) {
                        columnIndex = annotation.columnIndex();
                    }
                    ImportFieldInfo importFieldInfo = new ImportFieldInfo(currFieldPath, field.getType().getSimpleName(), columnIndex);
                    importFieldInfos.add(importFieldInfo);
                    columnIndex++;
                }
            }
        }

        return importFieldInfos;
    }

    private static FieldPathInfo getFieldPathInfo(String fieldPath, Field field) {
        if (!fieldPath.equals("") && fieldPath != null) {
            return new FieldPathInfo(String.format("%s/%s", fieldPath, field.getName()), field);
        } else {
            return new FieldPathInfo("/" + field.getName(), field);
        }

    }

    private static List<ImportFieldInfo> getImportFieldInfos(HeaderNode rootNode) {
        List<ImportFieldInfo> importFieldInfos = new ArrayList<>();

        Queue<HeaderNode> nodeQueue = new LinkedList<>();
        rootNode.fillFieldPath();
        nodeQueue.addAll(rootNode.getChildren());

        while (!nodeQueue.isEmpty()) {
            int columnIndex = COLUMN_START_IDX;
            int queueSize = nodeQueue.size();
            for (int i = 0; i < queueSize; i++) {
                HeaderNode currNode = nodeQueue.poll();
                String currFieldPath = currNode.getFieldPath();

                // 추가적으로 탐색해야 하는 자식 요소는 Queue에 넣기
                nodeQueue.addAll(currNode.getChildren());

                // 자식 요소가 더 이상 없으면 말단 필드 목록에 추가
                int numberOfChildren = currNode.getChildren().size();
                if (numberOfChildren == 0) {
                    ImportFieldInfo importFieldInfo = new ImportFieldInfo(currFieldPath, currNode.getType(), columnIndex);
                    importFieldInfos.add(importFieldInfo);
                }

                // Column Index
                int colSpan = numberOfChildren == 0 ? 1 : numberOfChildren;
                columnIndex = columnIndex + colSpan;
            }
        }

        return importFieldInfos;
    }

}
