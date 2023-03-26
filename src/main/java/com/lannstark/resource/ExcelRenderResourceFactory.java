package com.lannstark.resource;

import com.lannstark.DefaultBodyStyle;
import com.lannstark.DefaultHeaderStyle;
import com.lannstark.ExcelColumn;
import com.lannstark.ExcelColumnStyle;
import com.lannstark.exception.InvalidExcelCellStyleException;
import com.lannstark.exception.NoExcelColumnAnnotationsException;
import com.lannstark.resource.collection.HeaderNode;
import com.lannstark.resource.collection.PreCalculatedCellStyleMap;
import com.lannstark.style.ExcelCellStyle;
import com.lannstark.style.NoExcelCellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.lannstark.utils.SuperClassReflectionUtils.getAnnotation;

/**
 * ExcelRenderResourceFactory
 */
public final class ExcelRenderResourceFactory {

	public static ExcelRenderResource prepareRenderResource(HeaderNode rootNode, Workbook wb, DataFormatDecider dataFormatDecider) {
		// ExcelRenderResource 필드
		PreCalculatedCellStyleMap styleMap = new PreCalculatedCellStyleMap(dataFormatDecider);
		ExcelHeader excelHeader = new ExcelHeader();
		List<String> fieldPaths = new ArrayList<>();
		List<String> leafFieldPaths = new ArrayList<>();

		rootNode.fillFieldPath();
		Queue<HeaderNode> nodeQueue = new LinkedList<>();
		nodeQueue.addAll(rootNode.getChildren());

		int currRow = 0;
		int currDepth = 1;

		while (!nodeQueue.isEmpty()) {
			int currCol = 0;

			int queueSize = nodeQueue.size();
			for (int i = 0; i < queueSize; i++) {
				HeaderNode currNode = nodeQueue.poll();

				// StyleMap에 정보 넣기
				// - Header 스타일
				styleMap.put(currNode.getType(), ExcelCellKey.of(currNode.getFieldPath(), ExcelRenderLocation.HEADER), currNode.getHeaderRGB(), wb);

				// - Body 스타일
				styleMap.put(currNode.getType(), ExcelCellKey.of(currNode.getFieldPath(), ExcelRenderLocation.BODY), currNode.getBodyRGB(), wb);

				// Field Path 정보 셋팅
				fieldPaths.add(currNode.getFieldPath());
				if (currNode.getChildren().size() == 0) {
					leafFieldPaths.add(currNode.getFieldPath());
				}

				// Excel Header 정보 셋팅
				int heightOfHeader = rootNode.getHeightOfHeaderNode();
				excelHeader.setHeaderHeight(heightOfHeader);
				int numberOfChildren = currNode.getChildren().size();
				int rowHeight = numberOfChildren == 0 ? heightOfHeader - currDepth + 1 : 1;
				int colSpan = numberOfChildren == 0 ? 1 : numberOfChildren;
				ExcelHeaderCell excelHeaderCell = new ExcelHeaderCell(
						currNode.getColumnName(),
						currRow,
						currRow + rowHeight -1,
						currCol,
						currCol + colSpan -1
				);
				excelHeader.put(currNode.getFieldPath(), excelHeaderCell);
				currCol = currCol + colSpan; // 현재 위치한 Column의 인덱스 변경

				// 추가적으로 탐색해야 하는 자식 요소는 Queue에 넣기
				nodeQueue.addAll(currNode.getChildren());
			}
			// 현재 위치한 Row의 인덱스와 Header의 Depth 변경
			currDepth++;
			currRow++;
		}

		return new ExcelRenderResource(styleMap, excelHeader, fieldPaths, leafFieldPaths);
	}

	// TODO Refactor
	public static ExcelRenderResource prepareRenderResource(Class<?> type, Workbook wb,
															DataFormatDecider dataFormatDecider) {
		// ExcelRenderResource 필드
		PreCalculatedCellStyleMap styleMap = new PreCalculatedCellStyleMap(dataFormatDecider);
		ExcelHeader excelHeader = new ExcelHeader();
		List<String> fieldPaths = new ArrayList<>();
		List<String> leafFieldPaths = new ArrayList<>();

		// Default Style
		ExcelColumnStyle classDefinedHeaderStyle = getHeaderExcelColumnStyle(type);
		ExcelColumnStyle classDefinedBodyStyle = getBodyExcelColumnStyle(type);

		// class 내에 @ExcelColumn 어노테이션이 붙은 모든 Field를 탐색하기
		Queue<FieldPathInfo> fieldQueue = new LinkedList<>();
		List<FieldPathInfo> fieldInfos = Arrays.stream(type.getDeclaredFields()).map(field -> getFieldPathInfo("", field)).collect(Collectors.toList());
		fieldQueue.addAll(fieldInfos);

		int currRow = 0;
		int currDepth = 1;

		while (!fieldQueue.isEmpty()) {
			int currCol = 0;

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

				// Field Path 목록에 추가
				fieldPaths.add(currFieldPath);

				// 자식 요소가 더 이상 없으면 말단 필드 목록에 추가
				if (childFieldInfos.isEmpty()) {
					leafFieldPaths.add(currFieldPath);
				}

				// @ExcelColumn 어노테이션 정보 가져오기
				ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);

				// StyleMap에 정보 넣기
				// - Header 스타일
				styleMap.put(
						String.class,
						ExcelCellKey.of(currFieldPath, ExcelRenderLocation.HEADER),
						getCellStyle(decideAppliedStyleAnnotation(classDefinedHeaderStyle, annotation.headerStyle())), wb);

				// - Body 스타일
				Class<?> fieldType = field.getType();
				styleMap.put(
						fieldType,
						ExcelCellKey.of(currFieldPath, ExcelRenderLocation.BODY),
						getCellStyle(decideAppliedStyleAnnotation(classDefinedBodyStyle, annotation.bodyStyle())), wb);

				// Excel Header 정보 셋팅
				int heightOfHeader = getHeightOfHeader(type);
				excelHeader.setHeaderHeight(heightOfHeader);
				int numberOfChildren = childFieldInfos.size();
				int rowHeight = numberOfChildren == 0 ? heightOfHeader - currDepth + 1 : 1;
				int colSpan = numberOfChildren == 0 ? 1 : numberOfChildren;
				ExcelHeaderCell excelHeaderCell = new ExcelHeaderCell(
						annotation.headerName(),
						currRow,
						currRow + rowHeight -1,
						currCol,
						currCol + colSpan -1
				);
				excelHeader.put(currFieldPath, excelHeaderCell);
				currCol = currCol + colSpan; // 현재 위치한 Column의 인덱스 변경
			}

			// 현재 위치한 Row의 인덱스와 Header의 Depth 변경
			currDepth++;
			currRow++;
		}

		if (styleMap.isEmpty()) {
			throw new NoExcelColumnAnnotationsException(String.format("Class %s has not @ExcelColumn at all", type));
		}

		return new ExcelRenderResource(styleMap, excelHeader, fieldPaths, leafFieldPaths);
	}

	private static ExcelColumnStyle getHeaderExcelColumnStyle(Class<?> clazz) {
		Annotation annotation = getAnnotation(clazz, DefaultHeaderStyle.class);
		if (annotation == null) {
			return null;
		}
		return ((DefaultHeaderStyle) annotation).style();
	}

	private static ExcelColumnStyle getBodyExcelColumnStyle(Class<?> clazz) {
		Annotation annotation = getAnnotation(clazz, DefaultBodyStyle.class);
		if (annotation == null) {
			return null;
		}
		return ((DefaultBodyStyle) annotation).style();
	}

	private static ExcelColumnStyle decideAppliedStyleAnnotation(ExcelColumnStyle classAnnotation,
																 ExcelColumnStyle fieldAnnotation) {
		if (fieldAnnotation.excelCellStyleClass().equals(NoExcelCellStyle.class) && classAnnotation != null) {
			return classAnnotation;
		}
		return fieldAnnotation;
	}

	private static ExcelCellStyle getCellStyle(ExcelColumnStyle excelColumnStyle) {
		Class<? extends ExcelCellStyle> excelCellStyleClass = excelColumnStyle.excelCellStyleClass();
		// 1. Case of Enum
		if (excelCellStyleClass.isEnum()) {
			String enumName = excelColumnStyle.enumName();
			return findExcelCellStyle(excelCellStyleClass, enumName);
		}

		// 2. Case of Class
		try {
			return excelCellStyleClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new InvalidExcelCellStyleException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private static ExcelCellStyle findExcelCellStyle(Class<?> excelCellStyles, String enumName) {
		try {
			return (ExcelCellStyle) Enum.valueOf((Class<Enum>) excelCellStyles, enumName);
		} catch (NullPointerException e) {
			throw new InvalidExcelCellStyleException("enumName must not be null", e);
		} catch (IllegalArgumentException e) {
			throw new InvalidExcelCellStyleException(
					String.format("Enum %s does not name %s", excelCellStyles.getName(), enumName), e);
		}
	}

	/**
	 * 엑셀 Header Depth 계산에 사용되는 DFS 메서드
	 * @param clazz
	 * @param currDepth
	 * @return
	 */
	public static int getMaxDepth(Class<?> clazz, int currDepth) {
		// 자식 필드 중 @ExcelColumn이 달린 필드가 없다면 현재 depth를 리턴
		if (!Arrays.stream(clazz.getDeclaredFields()).anyMatch(_child -> _child.isAnnotationPresent(ExcelColumn.class))) {
			return currDepth;
		} else {
			int maxDepth = 1;
			currDepth++;
			for (Field field : clazz.getDeclaredFields()) {
				int depth = getMaxDepth(field.getType(), currDepth);
				maxDepth = Math.max(depth, maxDepth);
			}
			return maxDepth;
		}
	}

	/**
	 * Header 높이 계산
	 * @param clazz
	 * @return
	 */
	public static int getHeightOfHeader(Class<?> clazz) {
		//
		return getMaxDepth(clazz, 0);
	}

	/**
	 * 부모 Field의 fieldPath와 본인 Field의 name을 합쳐서 fieldPath를 리턴하는 메서드
	 * @param fieldPath
	 * @param field
	 * @return
	 */
	private static FieldPathInfo getFieldPathInfo(String fieldPath, Field field) {
		if (!fieldPath.equals("") && fieldPath != null) {
			return new FieldPathInfo(String.format("%s,%s", fieldPath, field.getName()), field);
		} else {
			return new FieldPathInfo(field.getName(), field);
		}

	}
}
