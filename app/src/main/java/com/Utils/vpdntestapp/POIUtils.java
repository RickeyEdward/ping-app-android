package com.Utils.vpdntestapp;

import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class POIUtils {

  /**
   * Excel 按行增加内容
   */
  public static void createExcelRow(Sheet sheet, String[] values, int rowIndex) {
    Row row = sheet.createRow(rowIndex);
    for (int i = 0; i < values.length; i++) {
      row.createCell(i).setCellValue(values[i]);
    }
  }

  public static void createExcelRow(Sheet sheet, Object[] values, int rowIndex) {
    Row row = sheet.createRow(rowIndex);
    for (int i = 0; i < values.length; i++) {
      Cell cell = row.createCell(i);
      Object value = values[i];
      if (value instanceof Number) {
        cell.setCellValue(((Number) value).doubleValue());
      } else if (value instanceof String) {
        cell.setCellValue((String) value);
      } else if (value instanceof Calendar) {
        cell.setCellValue((Calendar) value);
      } else if (value instanceof Date) {
        cell.setCellValue((Date) value);
      } else if (value instanceof Boolean) {
        cell.setCellValue((Boolean) value);
      } else {
        cell.setCellValue(String.valueOf(value));
      }

    }
  }

//  public static SXSSFWorkbook createWorkbookWithInitialSheet(String sheetName,
//    List<String> headers) {
//
//    SXSSFWorkbook xssfWorkbook = new SXSSFWorkbook(1000);
//    SXSSFSheet sheet;
//
//    if (sheetName != null) {
//      sheet = xssfWorkbook.createSheet(sheetName);
//    } else {
//      sheet = xssfWorkbook.createSheet();
//    }
//
//    SXSSFRow row = sheet.createRow(0);
//
//    for (int i = 0; i < headers.size(); i++) {
//      row.createCell(i).setCellValue(headers.get(i));
//    }
//
//    return xssfWorkbook;
//  }

}
