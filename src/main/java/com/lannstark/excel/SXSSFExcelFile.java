package com.lannstark.excel;

import com.lannstark.exception.ExcelInternalException;
import com.lannstark.resource.*;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

import static com.lannstark.utils.SuperClassReflectionUtils.getField;

public abstract class SXSSFExcelFile<T> implements ExcelFile<T> {

	protected static final SpreadsheetVersion supplyExcelVersion = SpreadsheetVersion.EXCEL2007;

	protected SXSSFWorkbook wb;
	protected Sheet sheet;
	protected ExcelRenderResource resource;

	/**
	 *SXSSFExcelFile
	 * @param type Class type to be rendered
	 */
	public SXSSFExcelFile(Class<T> type) {
		this(Collections.emptyList(), type, new DefaultDataFormatDecider());
	}

	/**
	 * SXSSFExcelFile
	 * @param data List Data to render excel file. data should have at least one @ExcelColumn on fields
	 * @param type Class type to be rendered
	 */
	public SXSSFExcelFile(List<T> data, Class<T> type) {
		this(data, type, new DefaultDataFormatDecider());
	}

	/**
	 * SXSSFExcelFile
	 * @param data List Data to render excel file. data should have at least one @ExcelColumn on fields
	 * @param type Class type to be rendered
	 * @param dataFormatDecider Custom DataFormatDecider
	 */
	public SXSSFExcelFile(List<T> data, Class<T> type, DataFormatDecider dataFormatDecider) {
		validateData(data);
		this.wb = new SXSSFWorkbook();
		this.resource = ExcelRenderResourceFactory.prepareRenderResource(type, wb, dataFormatDecider);
		renderExcel(data);
	}

	protected void validateData(List<T> data) { }

	protected abstract void renderExcel(List<T> data);

	protected void renderHeadersWithNewSheet(Sheet sheet, int rowStartIndex, int columnStartIndex) {
		// 헤더 높이만큼 Row 생성하기
		int headerHeight = resource.getExcelHeader().getHeaderHeight();
		for (int depth = 0; depth < headerHeight; depth++) {
			if (sheet.getLastRowNum() < rowStartIndex + depth) {
				sheet.createRow(rowStartIndex + depth);
			}
		}

		for (String fieldPath : resource.getFieldPaths()) {
			ExcelHeaderCell headerCell = resource.getExcelHeader().getExcelHeaderCell(fieldPath);
			headerCell.adjustFirstIndex(rowStartIndex, columnStartIndex);
			Row row = sheet.getRow(headerCell.getFirstRow());
			Cell cell = row.createCell(headerCell.getFirstCol());
			cell.setCellStyle(resource.getCellStyle(fieldPath, ExcelRenderLocation.HEADER));
			cell.setCellValue(headerCell.getHeaderName());

			if (headerCell.containsMoreThanOneCell()) {
				sheet.addMergedRegion(new CellRangeAddress(
						headerCell.getFirstRow(), headerCell.getLastRow(), headerCell.getFirstCol(), headerCell.getLastCol()));
			}
		}
	}

	protected void renderBody(Object data, int rowIndex, int columnStartIndex) {
		Row row = sheet.createRow(rowIndex);
		int columnIndex = columnStartIndex;
		for (String fieldPath : resource.getFieldPaths()) {
			// 말단 Field인 경우에만 Body에 데이터 작성
			if (!resource.getLeafFieldPaths().contains(fieldPath)) {
				continue;
			}
			Cell cell = row.createCell(columnIndex++);
			try {
				cell.setCellStyle(resource.getCellStyle(fieldPath, ExcelRenderLocation.BODY));
				Object cellValue = getDataValue(fieldPath, data);
				renderCellValue(cell, cellValue);
			} catch (Exception e) {
				throw new ExcelInternalException(e.getMessage(), e);
			}
		}
	}

	// TODO 적절한 위치로 메서드 이동시키기
	private Object getDataValue(String fieldPath, Object dtoData) throws Exception {
		//
		Queue<String> fieldNameQueue = new LinkedList<>(Arrays.asList(fieldPath.split(",")));
		Object data = dtoData;
		Field field = null;

		while (!fieldNameQueue.isEmpty()) {
			field = getField(data.getClass(), fieldNameQueue.poll());
			field.setAccessible(true);
			data = field.get(data);
		}

		return data;
	}

	private void renderCellValue(Cell cell, Object cellValue) {
		if (cellValue instanceof Number) {
			Number numberValue = (Number) cellValue;
			cell.setCellValue(numberValue.doubleValue());
			return;
		}
		cell.setCellValue(cellValue == null ? "" : cellValue.toString());
	}

	public void write(OutputStream stream) throws IOException {
		wb.write(stream);
		wb.close();
		wb.dispose();
		stream.close();
	}

}
