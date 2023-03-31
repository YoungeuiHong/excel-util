package com.lannstark.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface ExcelFile<T> {

	void write(OutputStream stream) throws IOException;

	<T> List<T> read(Class<T> type) throws IOException;

	List<Object> read() throws IOException;

	List<Map<String, Object>> readFlat();

	void addRows(List<T> data);

}
