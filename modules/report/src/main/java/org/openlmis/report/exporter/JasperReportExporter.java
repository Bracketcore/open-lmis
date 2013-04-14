package org.openlmis.report.exporter;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.openlmis.report.ReportManager;
import org.openlmis.report.ReportOutputOption;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.util.Constants;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Handles Exporting of Jasper reports
 */
@Component
public class JasperReportExporter implements ReportExporter {

    @Override
    public void exportReport(InputStream reportInputStream, HashMap<String, Object> reportExtraParams, List<? extends ReportData> reportData, ReportOutputOption outputOption, HttpServletResponse response) {

        try{

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportInputStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportExtraParams , new JRBeanCollectionDataSource(reportData,false));

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            String reportOutputFileName = reportExtraParams != null ? ((String) reportExtraParams.get(Constants.REPORT_NAME)) : "";

            //Jasper export handler
            export(outputOption, reportOutputFileName, jasperPrint,response,byteArrayOutputStream);

            //Write to servlet output stream
            writeToServletOutputStream(response, byteArrayOutputStream);

        } catch (JRException e) {

            e.printStackTrace();
        }
    }

    /**
     *
     * @param outputOption
     * @param jasperPrint
     * @param response
     * @param byteArrayOutputStream
     * @return
     */
    private HttpServletResponse export(ReportOutputOption outputOption, String outputFileName, JasperPrint jasperPrint, HttpServletResponse response, ByteArrayOutputStream byteArrayOutputStream) {

        switch (outputOption){

            case PDF:
                return exportPdf(jasperPrint, outputFileName, response, byteArrayOutputStream);
            case XLS:
                return exportXls(jasperPrint, outputFileName, response, byteArrayOutputStream);
        }

        return response;
    }

    /**
     * Handles exporting of jasper print to pdf format
     * @param jasperPrint
     * @param response
     * @param byteArrayOutputStream
     * @return
     */
    private HttpServletResponse exportPdf(JasperPrint jasperPrint, String outputFileName, HttpServletResponse response, ByteArrayOutputStream byteArrayOutputStream){

        JRPdfExporter exporter = new JRPdfExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);

        try {
            exporter.exportReport();

        } catch (JRException e) {
            throw new RuntimeException(e);
        }

        String fileName = outputFileName.isEmpty()? "openlmisReport.pdf" : outputFileName+".pdf";
        response.setHeader("Content-Disposition", "inline; filename="+ fileName);

        response.setContentType(Constants.MEDIA_TYPE_PDF);
        response.setContentLength(byteArrayOutputStream.size());

        return response;
    }

    /**
     *
     * @param jasperPrint
     * @param response
     * @param byteArrayOutputStream
     * @return
     */
    public HttpServletResponse exportXls(JasperPrint jasperPrint, String outputFileName, HttpServletResponse response, ByteArrayOutputStream byteArrayOutputStream){

        JRXlsExporter exporter = new JRXlsExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);

        exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);


        try {
            exporter.exportReport();

        } catch (JRException e) {
            throw new RuntimeException(e);
        }

        String fileName = outputFileName.isEmpty()? "openlmisReport.xls" : outputFileName+".xls";
        response.setHeader("Content-Disposition", "inline; filename=" + fileName);

        response.setContentType(Constants.MEDIA_TYPE_EXCEL);
        response.setContentLength(byteArrayOutputStream.size());

        return response;
    }

    /**
     * @param response
     * @param byteArrayOutputStream
     */
    private void writeToServletOutputStream(HttpServletResponse response, ByteArrayOutputStream byteArrayOutputStream) {

        try {

            ServletOutputStream outputStream = response.getOutputStream();

            byteArrayOutputStream.writeTo(outputStream);

            outputStream.flush();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
