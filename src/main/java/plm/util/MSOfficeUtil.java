package plm.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.flame.util.FlameUtils;

public final class MSOfficeUtil {

    public static String getStringFromRow(Row row, int col) {
        return getValueFromRow(row, col);
    }

    public static String getValueFromRow(Row row, int col) {
        if (row == null)
            return "";

        Cell cell = row.getCell(col);
        if (cell == null)
            return "";

        CellType type = cell.getCellType();
        String value = "";
        switch (type) {
            case BOOLEAN:
                value = Boolean.toString(cell.getBooleanCellValue());
                break;
            case NUMERIC:
                value = Double.toString(cell.getNumericCellValue());
                if (FlameUtils.isInteger(value)) {
                    value = new java.text.DecimalFormat("###########").format(cell.getNumericCellValue());
                }
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            case BLANK:
                value = "";
                break;
            default:
                value = cell.getStringCellValue();
        }
        if (value == null)
            return "";
        else
            return value.trim();
    }

    public static String getMergedValue(Sheet sheet, int row, int col) {
        String value = getStringFromSheet(sheet, row, col);

        if (FlameUtils.isEmpty(value)) {
            int mergeCount = sheet.getNumMergedRegions();

            for (int i = 0; i < mergeCount; i++) {
                CellRangeAddress ca = sheet.getMergedRegion(i);
                int firstCol = ca.getFirstColumn();
                int lastCol = ca.getLastColumn();
                int firstRow = ca.getFirstRow();
                int lastRow = ca.getLastRow();

                if (row >= firstRow && row <= lastRow && col >= firstCol && col <= lastCol) {
                    Row fRow = sheet.getRow(firstRow);
                    return getValueFromRow(fRow, firstCol);
                }
            }
        }

        return value;
    }

    public static void setStringForRow(Row row, int col, String value) {
        if (row == null || col < 0)
            return;
        value = ((value == null) ? "" : value);

        Cell cell = row.getCell(col);
        if (cell == null) {
            cell = row.createCell(col);
        }
        cell.setCellFormula(CellType.STRING.toString());
        cell.setCellValue(value);
    }

    public static void setStringForRow(Row row, int col, String value, CellStyle style) {
        if (row == null || col < 0)
            return;
        value = ((value == null) ? "" : value);

        Cell cell = row.getCell(col);
        if (cell == null) {
            cell = row.createCell(col);
        }
        cell.setCellFormula(CellType.STRING.toString());
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    public static String getStringFromSheet(Sheet sheet, int rownum, int colnum) {
        if (sheet == null)
            return "";
        Row row = sheet.getRow(rownum);
        if (row == null)
            return "";
        String str = getStringFromRow(row, colnum);
        if (str != null)
            return str.trim();
        else
            return "";
    }

    public static void setStringForSheet(Sheet sheet, int rownum, int colnum, String value) {
        if (sheet == null || rownum < 0 || colnum < 0)
            return;
        value = ((value == null) ? "" : value);
        Row row = sheet.getRow(rownum);
        if (row == null)
            return;
        setStringForRow(row, colnum, value);
    }

    public static void insertRow(Sheet sheet, int startRow, int rows) {
        sheet.shiftRows(startRow + 1, sheet.getLastRowNum(), rows, true, false);
        startRow = startRow - 1;

        for (int i = 0; i < rows; i++) {
            short m;

            startRow = startRow + 1;
            Row srcRow = sheet.getRow(startRow);
            Row tgtRow = sheet.createRow(startRow + 1);
            tgtRow.setHeight(srcRow.getHeight());

            for (m = srcRow.getFirstCellNum(); m < srcRow.getLastCellNum(); m++) {
                Cell srcCell = srcRow.getCell(m);
                Cell tgtCell = tgtRow.createCell(m);

                tgtCell.setCellStyle(srcCell.getCellStyle());
                tgtCell.setCellFormula(srcCell.getCellType().toString());
            }
        }
    }

    private MSOfficeUtil() {
    }
}
