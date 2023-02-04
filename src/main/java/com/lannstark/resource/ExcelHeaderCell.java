package com.lannstark.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelHeaderCell {
    //
    private String headerName;
    private int firstRow;
    private int lastRow;
    private int firstCol;
    private int lastCol;

    /**
     * 현재 행과 열의 시작 지점에 맞게 CellRange를 조정
     * @param rowStartIndex
     * @param columnStartIndex
     */
    public void adjustFirstIndex(int rowStartIndex, int columnStartIndex) {
        this.firstRow = this.firstRow + rowStartIndex;
        this.lastRow = this.lastRow + rowStartIndex;
        this.firstCol = this.firstCol + columnStartIndex;
        this.lastCol = this.lastCol + columnStartIndex;
    }

    /**
     * 셀의 사이즈가 1보다 큰지 확인하는 메서드
     * @return
     */
    public boolean containsMoreThanOneCell() {
        boolean containMoreThanOneCell = false;
        if (lastRow > firstRow || lastCol > firstCol) {
            containMoreThanOneCell = true;
        }
        return containMoreThanOneCell;
    }
}
