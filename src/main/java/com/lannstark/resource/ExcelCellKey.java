package com.lannstark.resource;

import java.util.Objects;

public final class ExcelCellKey {

	private final String fieldPath;
	private final ExcelRenderLocation excelRenderLocation;

	private ExcelCellKey(String fieldPath, ExcelRenderLocation excelRenderLocation) {
		this.fieldPath = fieldPath;
		this.excelRenderLocation = excelRenderLocation;
	}

	public static ExcelCellKey of(String fieldName, ExcelRenderLocation excelRenderLocation) {
		assert excelRenderLocation != null;
		return new ExcelCellKey(fieldName, excelRenderLocation);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ExcelCellKey that = (ExcelCellKey) o;
		return Objects.equals(fieldPath, that.fieldPath) &&
				excelRenderLocation == that.excelRenderLocation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldPath, excelRenderLocation);
	}

}
