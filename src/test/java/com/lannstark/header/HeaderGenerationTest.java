package com.lannstark.header;

import com.google.gson.Gson;
import com.lannstark.ExcelColumn;
import com.lannstark.dto.StudentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class HeaderGenerationTest {

    private Gson gson = new Gson();

    @Test
    @DisplayName("엑셀 헤더의 Depth 계산하기")
    public void getDepthOfHeader() {
        int maxDepth = getDepthOfHeader(StudentDto.class);
        assertThat(maxDepth == 2);
    }

    @Test
    @DisplayName("엑셀 Header 생성하기")
    public void generateHeader() {
        int maxDepth = getDepthOfHeader(StudentDto.class);
        List<CellInfo> header = generateHeader(StudentDto.class, 1, 1, maxDepth);
        System.out.println(gson.toJson(header));
    }

    @Test
    @DisplayName("@ExcelColumn 어노테이션이 붙은 Field 수집")
    public void getAllFieldInfo() {
        List<FieldInfo> fieldInfos = getAllFieldInfo(StudentDto.class, "com.lannstark");
        System.out.println(gson.toJson(fieldInfos));
    }

    /**
     * 엑셀 Header의 Depth (= 세로 높이) 계산하기
     * @param clazz
     * @return
     */
    private int getDepthOfHeader(Class<?> clazz) {
        //
        return getDepth(clazz, 0);
    }

    /**
     * 엑셀 Header Depth 계산에 사용되는 DFS 메서드
     * @param clazz
     * @param currDepth
     * @return
     */
    private int getDepth(Class<?> clazz, int currDepth) {
        // 자식 필드 중 @ExcelColumn이 달린 필드가 없다면 현재 depth를 리턴
        if (!Arrays.stream(clazz.getDeclaredFields()).anyMatch(_child -> _child.isAnnotationPresent(ExcelColumn.class))) {
            return currDepth;
        } else {
            int maxDepth = 1;
            currDepth++;
            for (Field field : clazz.getDeclaredFields()) {
                int depth = getDepth(field.getType(), currDepth);
                maxDepth = Math.max(depth, maxDepth);
            }
            return maxDepth;
        }
    }

    /**
     * 엑셀 헤더 생성 메서드
     * 현재 행의 위치와 열의 위치를 기준으로 엑셀 헤더 각 셀의 firstRow, lastRow, firstCol, lastCol 인덱스를 계산
     * @param clazz
     * @param firstRow
     * @param firstCol
     * @param maxDepth
     * @return
     */
    private List<CellInfo> generateHeader(Class<?> clazz, int firstRow, int firstCol, int maxDepth) {
        //
        List<CellInfo> cellInfos = new ArrayList<>();

        Queue<Field> queue = new LinkedList<>();
        queue.addAll(Arrays.asList(clazz.getDeclaredFields()));

        int currRow = firstRow;
        int currDepth = 1;

        while (!queue.isEmpty()) {
            int length = queue.size();
            int currCol = firstCol;
            for (int i = 0; i < length; i++) {
                Field field = queue.poll();

                int numberOfChildren = 0;
                for (Field _child : field.getType().getDeclaredFields()) {
                    // 자식 요소의 개수 구하기
                    if (_child.isAnnotationPresent(ExcelColumn.class)) {
                        numberOfChildren++;
                    }

                    // 추가적으로 탐색해야 하는 자식 요소는 Queue에 넣기
                    if (_child.isAnnotationPresent(ExcelColumn.class)) {
                        queue.add(_child);
                    }
                }

                // 해당 필드의 FieldInfo 만들기
                // row height는 자식 요소가 있으면 1, 자식 요소가 없으면 헤더의 Maximum depth - 자신의 depth + 1로 구할 수 있다.
                int rowHeight = numberOfChildren == 0 ? maxDepth - currDepth + 1 : 1;
                int colSpan = numberOfChildren == 0 ? 1 : numberOfChildren;

                CellInfo cellInfo = new CellInfo(
                        field.getName(),
                        currRow,
                        currRow + rowHeight - 1,
                        currCol,
                        currCol + colSpan - 1
                );
                cellInfos.add(cellInfo);

                currCol = currCol + colSpan;
            }

            currDepth++;
            currRow++;
        }

        return cellInfos;
    }

    /**
     * BFS 방식으로 클래스 내에 ExcelColumn 어노테이션이 붙은 Field를 모두 가져오기
     * @param clazz
     * @param path
     * @return
     */
    private List<FieldInfo> getAllFieldInfo(Class<?> clazz, String path) {
        //
        List<FieldInfo> fieldInfos = new ArrayList<>();
        int level = 1;

        Queue<Field> queue = new LinkedList<>();
        queue.addAll(Arrays.asList(clazz.getDeclaredFields()));

        while (!queue.isEmpty()) {
            int length = queue.size();

            for (int i = 0; i < length; i++) {
                Field field = queue.poll();

                List<Field> children = new ArrayList<>();
                Class<?> fieldTYpe = field.getType();
                for (Field _field : fieldTYpe.getDeclaredFields()) {
                    if (fieldTYpe != null && !fieldTYpe.isPrimitive() && fieldTYpe.getPackage().getName().startsWith(path) && field.isAnnotationPresent(ExcelColumn.class)) {
                        children.add(_field);
                    }
                }
                queue.addAll(children);

                FieldInfo fieldInfo = new FieldInfo(
                        field.getName(),
                        level,
                        new ArrayList<>() // TODO 자식 요소 넣가
                );

                fieldInfos.add(fieldInfo);
            }

            level++;
        }

        return fieldInfos;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private class FieldInfo {
        //
        private String name;
        private int depth;
        private List<FieldInfo> children;

        public FieldInfo(String name, int depth) {
            this.name = name;
            this.depth = depth;
            this.children = new ArrayList<>();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private class CellInfo {
        //
        private String name;
        private int firstRow;
        private int lastRow;
        private int firstCol;
        private int lastCol;
    }


}
