package org.jlab.phaser.export;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.jlab.jlog.Body;
import org.jlab.jlog.LogEntry;
import org.jlab.jlog.exception.LogCertificateException;
import org.jlab.jlog.exception.LogIOException;
import org.jlab.jlog.exception.LogRuntimeException;
import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.model.ResultRecord;
import org.jlab.phaser.model.ResultPage;
import org.jlab.phaser.swing.PhaserClientFrame;
import org.jlab.phaser.swing.dialog.ResultsDialog;

/**
 * Exports Phaser results to the Jefferson Lab electronic logbook.
 *
 * @author ryans
 */
public class ElogResultsService {

    /**
     * Convert results in the form of a ResultPage into an eLog.
     *
     * @param page The ResultPage
     * @param logbooks The logbooks to post to
     * @return The logId
     * @throws LogRuntimeException If unable to export the results to eLog with no recourse by the API user
     * @throws LogCertificateException If unable to accept the logbook server certificate
     * @throws LogIOException If unable to export the results to eLog
     */
    public Long export(ResultPage page, String logbooks) throws LogRuntimeException, LogCertificateException, LogIOException {
        String title = "Phaser Results";
        LogEntry entry = new LogEntry(title, logbooks);
        StringBuilder html = new StringBuilder();

        String count;
        String where = page.getFilter().toHumanWhereClause(PhaserClientFrame.TIMESTAMP_FORMAT);
        Paginator paginator = page.getPaginator();

        if (paginator.getTotalRecords() <= paginator.getMaxPerPage()) {
            count = "{" + paginator.getTotalRecords() + "}";
        } else {
            count = "{" + paginator.getStartNumber() + " - "
                    + paginator.getEndNumber() + " of " + paginator.getTotalRecords() + "}";
        }

        html.append("<h4>Results ").append(escapeXml(where)).append(escapeXml(count)).append("</h4>\n");
        html.append("\n");

        html.append("<table style=\"border: 1px solid black; box-shadow: 8px 8px 8px #979797;\">\n");
        html.append("<thead>\n");
        html.append("<tr style=\"border-bottom: 1px solid black; background-color: #e8f7ff;\">\n");
        html.append("<th>CAVITY</th>\n");
        html.append("<th>PHASE ERROR (DEGREES)</th>\n");
        html.append("<th>OUTCOME</th>\n");
        html.append("<th>PHASE (DEGREES)</th>\n");
        html.append("<th>START DATE</th>\n");
        html.append("<th>DURATION (SECONDS)</th>\n");
        html.append("<th>CORRECTION DATE</th>\n");
        html.append("<th>CORRECTION ERROR REASON</th>\n");
        html.append("</tr>\n");
        html.append("</thead>\n");
        html.append("<tbody>\n");

        DecimalFormat integerStyle = new DecimalFormat("#,###,##0");
        DecimalFormat floatStyle = new DecimalFormat("#,###,##0.00");
        SimpleDateFormat dateStyle = new SimpleDateFormat(PhaserClientFrame.TIMESTAMP_FORMAT);

        for (ResultRecord record : page.getRecords()) {
            html.append("<tr>\n");

            html.append("<td>").append(escapeXml(record.getCavity())).append("</td>\n");

            String phaseErrorStr = "";

            if (record.getPhaseError() != null) {
                phaseErrorStr = floatStyle.format(record.getPhaseError());
            }

            html.append("<td style=\"text-align: right;\">").append(escapeXml(phaseErrorStr)).append("</td>\n");

            html.append("<td>").append(escapeXml(record.getOutcome().name())).append("</td>\n");

            String phaseStr = "";

            if (record.getPhase() != null) {
                phaseStr = floatStyle.format(record.getPhase());
            }

            html.append("<td style=\"text-align: right;\">").append(escapeXml(phaseStr)).append("</td>\n");

            html.append("<td>").append(escapeXml(dateStyle.format(record.getStartDate()))).append("</td>\n");

            double duration
                    = (record.getEndDate().getTime() - record.getStartDate().getTime()) / 1000.0;

            html.append("<td style=\"text-align: right;\">").append(escapeXml(integerStyle.format(duration))).append("</td>\n");

            String correctionDateStr = "";

            if (record.getCorrectionDate() != null) {
                correctionDateStr = dateStyle.format(record.getCorrectionDate());
            }

            html.append("<td>").append(escapeXml(correctionDateStr)).append("</td>\n");

            String correctionErrorReasonStr = "";

            if (record.getCorrectionErrorReason() != null) {
                correctionErrorReasonStr = record.getCorrectionErrorReason();
            }

            html.append("<td>").append(escapeXml(correctionErrorReasonStr)).append("</td>\n");
            html.append("</tr>\n");
        }

        html.append("</tbody>\n");
        html.append("</table>\n");

        entry.setBody(html.toString(), Body.ContentType.HTML);

        Long logId = entry.submitNow();

        return logId;
    }
    
    private String escapeXml(String input) {
        String output = input;

        if (input != null) {
            output = output.replace("&", "&#038;"); // Must do this one first as & within other replacements
            output = output.replace("\"", "&#034;");
            output = output.replace("'", "&#039;");
            output = output.replace("<", "&#060;");
            output = output.replace(">", "&#062;");
        }
        return output;
    }
}
