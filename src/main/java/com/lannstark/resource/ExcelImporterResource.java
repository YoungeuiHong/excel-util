package com.lannstark.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ExcelImporterResource {
    //
    private List<ImportFieldInfo> importFieldInfos;
    private int rowStartIdx;
    private int columnStartIdx;
}
