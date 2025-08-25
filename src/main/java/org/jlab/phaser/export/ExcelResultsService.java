package org.jlab.phaser.export;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.model.ResultPage;
import org.jlab.phaser.model.ResultRecord;
import org.jlab.phaser.swing.generated.PhaserClientFrame;

/**
 * Exports Phaser results to Microsoft Excel format.
 *
 * @author ryans
 */
public class ExcelResultsService {

  /**
   * Convert results in the form of a ResultPage into an Excel formatted output stream.
   *
   * @param page The ResultPage
   * @param out The output stream
   * @throws IOException If unable to export the results to Excel format
   */
  public void export(ResultPage page, OutputStream out) throws IOException {
    Workbook wb = new XSSFWorkbook();
    Sheet sheet1 = wb.createSheet("Phaser Results");

    String count;
    String where = page.getFilter().toHumanWhereClause(PhaserClientFrame.TIMESTAMP_FORMAT);
    Paginator paginator = page.getPaginator();

    if (paginator.getTotalRecords() <= paginator.getMaxPerPage()) {
      count = "{" + paginator.getTotalRecords() + "}";
    } else {
      count =
          "{"
              + paginator.getStartNumber()
              + " - "
              + paginator.getEndNumber()
              + " of "
              + paginator.getTotalRecords()
              + "}";
    }

    int rownum = 0;
    Row row = sheet1.createRow(rownum++);
    row.createCell(0).setCellValue("Results " + where + count);

    sheet1.createRow(rownum++); // spacer row

    row = sheet1.createRow(rownum++);
    row.createCell(1).setCellValue("CAVITY");
    row.createCell(2).setCellValue("PHASE ERROR (DEGREES)");
    row.createCell(3).setCellValue("OUTCOME");
    row.createCell(4).setCellValue("PHASE (DEGREES)");
    row.createCell(5).setCellValue("START DATE");
    row.createCell(6).setCellValue("DURATION (SECONDS)");
    row.createCell(7).setCellValue("CORRECTION DATE");
    row.createCell(8).setCellValue("CORRECTION ERROR REASON");

    CreationHelper createHelper = wb.getCreationHelper();
    CellStyle integerStyle = wb.createCellStyle();
    integerStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,###,##0"));
    CellStyle floatStyle = wb.createCellStyle();
    floatStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,###,##0.00"));
    CellStyle dateStyle = wb.createCellStyle();
    dateStyle.setDataFormat(
        createHelper.createDataFormat().getFormat(PhaserClientFrame.TIMESTAMP_FORMAT));

    Cell c;

    for (ResultRecord record : page.getRecords()) {
      row = sheet1.createRow(rownum++);

      row.createCell(1).setCellValue(record.getCavity());

      c = row.createCell(2);
      c.setCellStyle(floatStyle);
      if (record.getPhaseError() != null) {
        c.setCellValue(record.getPhaseError());
      }

      row.createCell(3).setCellValue(record.getOutcome().name());

      c = row.createCell(4);
      c.setCellStyle(floatStyle);
      if (record.getPhase() != null) {
        c.setCellValue(record.getPhase());
      }

      c = row.createCell(5);
      c.setCellStyle(dateStyle);
      c.setCellValue(record.getStartDate());

      c = row.createCell(6);
      c.setCellStyle(integerStyle);
      double duration = (record.getEndDate().getTime() - record.getStartDate().getTime()) / 1000.0;
      c.setCellValue(duration);

      c = row.createCell(7);
      c.setCellStyle(dateStyle);
      if (record.getCorrectionDate() != null) {
        c.setCellValue(record.getCorrectionDate());
      }

      c = row.createCell(8);
      if (record.getCorrectionErrorReason() != null) {
        c.setCellValue(record.getCorrectionErrorReason());
      }
    }

    sheet1.autoSizeColumn(1);
    sheet1.autoSizeColumn(2);
    sheet1.autoSizeColumn(3);
    sheet1.autoSizeColumn(4);
    sheet1.autoSizeColumn(5);
    sheet1.autoSizeColumn(6);
    sheet1.autoSizeColumn(7);
    sheet1.autoSizeColumn(8);

    wb.write(out);
  }
}
