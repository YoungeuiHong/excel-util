package com.lannstark.excel.import_;

import java.io.IOException;
import java.util.List;

public interface ImportedExcel<T> {
    List<Object> read() throws IOException;
}
