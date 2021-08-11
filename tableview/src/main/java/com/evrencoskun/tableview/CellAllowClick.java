package com.evrencoskun.tableview;

public class CellAllowClick {

    private final boolean insideCell;
    private final boolean insideRowHeader;
    private final boolean insideColumnHeader;

    public CellAllowClick(boolean insideCell, boolean insideRowHeader, boolean insideColumnHeader) {
        this.insideCell = insideCell;
        this.insideRowHeader = insideRowHeader;
        this.insideColumnHeader = insideColumnHeader;
    }

    public boolean isInsideCell() {
        return insideCell;
    }

    public boolean isInsideRowHeader() {
        return insideRowHeader;
    }

    public boolean isInsideColumnHeader() {
        return insideColumnHeader;
    }
}
