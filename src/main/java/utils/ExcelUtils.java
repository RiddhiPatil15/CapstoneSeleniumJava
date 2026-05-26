package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

    private static Workbook workbook;

    static {
        try {
            FileInputStream file = new FileInputStream(
                    System.getProperty("user.dir")
                            + "/src/test/resources/testdata/TestData.xlsx"
            );
            workbook = new XSSFWorkbook(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= COMMON =================

    public static int getRowIndex(String sheetName, String testCaseId) {

        Sheet sheet = workbook.getSheet(sheetName);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            if (row != null && row.getCell(0) != null) {

                row.getCell(0).setCellType(CellType.STRING);

                if (row.getCell(0).getStringCellValue().equalsIgnoreCase(testCaseId)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static String getCellValue(Sheet sheet, int rowNum, String columnName) {

        if (rowNum == -1) return null;

        Row headerRow = sheet.getRow(0);
        Row dataRow = sheet.getRow(rowNum);

        int colIndex = -1;

        for (Cell cell : headerRow) {
            if (cell.getStringCellValue().equalsIgnoreCase(columnName)) {
                colIndex = cell.getColumnIndex();
                break;
            }
        }

        if (colIndex == -1 || dataRow == null) return null;

        Cell cell = dataRow.getCell(colIndex);
        if (cell == null) return null;

        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    // ================= USERS (DO NOT CHANGE CONTRACT) =================

    public static String getUserData(String testCaseId, String columnName) {

        int rowIndex = getRowIndex("Users", testCaseId);
        return getCellValue(workbook.getSheet("Users"), rowIndex, columnName);
    }

    public static String getUserDataByRow(int rowNum, String columnName) {
        return getCellValue(workbook.getSheet("Users"), rowNum, columnName);
    }

    // ================= NOTES =================

    // ONLY ADD THIS METHOD — DO NOT MODIFY EXISTING METHODS

    public static List<String[]> getNotes(String testCaseId) {

        Sheet sheet = workbook.getSheet("Notes");

        List<String[]> notes = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            if (row == null || row.getCell(0) == null) continue;

            row.getCell(0).setCellType(CellType.STRING);

            if (row.getCell(0).getStringCellValue().equalsIgnoreCase(testCaseId)) {

                String category = getCellValue(sheet, i, "Category");
                String title = getCellValue(sheet, i, "Title");
                String description = getCellValue(sheet, i, "Description");

                // null-safe (important for client flexibility)
                if (title == null || description == null) continue;

                notes.add(new String[]{category, title, description});
            }
        }

        return notes;
    }

    // ================= NEGATIVE =================

    public static String getNegativeData(String testCaseId, String columnName) {

        int rowIndex = getRowIndex("NegativeData", testCaseId);
        return getCellValue(workbook.getSheet("NegativeData"), rowIndex, columnName);
    }

    public static String getCellData(
            String sheetName,
            String testCaseId,
            String columnName
    ) {

        try {

            Sheet sheet =
                    workbook.getSheet(sheetName);

            Row headerRow =
                    sheet.getRow(0);

            int columnIndex = -1;

            // find column
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {

                if (headerRow.getCell(i)
                        .getStringCellValue()
                        .trim()
                        .equalsIgnoreCase(columnName)) {

                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex == -1) {

                throw new RuntimeException(
                        "Column not found: " + columnName
                );
            }

            // find row using TC_ID
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String currentTcId =
                        row.getCell(0)
                                .getStringCellValue();

                if (currentTcId.equalsIgnoreCase(testCaseId)) {

                    Cell cell =
                            row.getCell(columnIndex);

                    if (cell == null) {
                        return "";
                    }

                    return cell.toString();
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed reading Excel data: "
                            + e.getMessage()
            );
        }

        return "";
    }
}