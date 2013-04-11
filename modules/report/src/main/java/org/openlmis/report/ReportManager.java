package org.openlmis.report;

import lombok.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;
import org.openlmis.report.exception.ReportException;
import org.openlmis.report.exporter.ReportExporter;
import org.openlmis.report.model.FacilityReport;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages both interactive web and jasper report integration
 */
@NoArgsConstructor
@AllArgsConstructor
public class ReportManager {

    private ReportAccessAuthorizer reportAccessAuthorizer;

    private ReportExporter reportExporter;

    private List<Report> reports;

    private Map<String,Report> reportsByKey;

    private List<String> reportKeys;

    private Report report;

    @Autowired
    private UserService userService;

    public ReportManager(ReportAccessAuthorizer reportAccessAuthorizer, ReportExporter reportExporter, List<Report> reports) {

        this(reports);
        this.reportExporter = reportExporter;
        this.reportAccessAuthorizer = reportAccessAuthorizer;
    }

    private ReportManager(List<Report> reports){

        this.reports = reports;

        if(reports != null){

            reportsByKey = new HashMap<>();

            for (Report report: reports){
                reportsByKey.put(report.getReportKey(),report);
            }

        }
    }

    /**
     *
     * @param report
     * @param parameter
     * @param outputOption
     * @param response
     */
    public void showReport(Integer userId, Report report, ReportData parameter, ReportOutputOption outputOption, HttpServletResponse response){

       if (report == null){
           throw new ReportException("invalid report");
       }

       User currentUser = userService.getById(userId);
       List<? extends ReportData> dataSource = report.getReportDataProvider().getReportDataByFilterCriteria(parameter, DataSourceType.BEAN_COLLECTION_DATA_SOURCE);

       reportExporter.exportReport(this.getClass().getClassLoader().getResourceAsStream(report.getTemplate()),getReportExtraParams(report, currentUser.getUserName()), dataSource, outputOption, response );

    }

    /**
     *
     * @param reportKey
     * @param parameter
     * @param outputOption
     * @param response
     */
    public void showReport(Integer userId, String reportKey, ReportData parameter, ReportOutputOption outputOption, HttpServletResponse response){

        showReport(userId, getReportByKey(reportKey), parameter, outputOption, response);
    }

    /**
     * Used to extract extra parameters that are used by report header and footer.
     * @param report
     * @return
     */
    private HashMap<String, Object> getReportExtraParams(Report report, String generatedBy){

        if (report == null) return null;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(Constants.REPORT_NAME, report.getName());
        params.put(Constants.REPORT_ID, report.getId());
        params.put(Constants.REPORT_TITLE, report.getTitle());
        params.put(Constants.REPORT_VERSION, report.getVersion());
        params.put(Constants.LOGO,this.getClass().getClassLoader().getResourceAsStream("logo.png"));
        params.put(Constants.GENERATED_BY, generatedBy);

        return params;
    }

    /*
        Returns list of report keys of all registered Reports that are managed by ReportManager class.
        This report keys can be used for generating tree view(report navigation) on the web.
     */
    public List<String> getReportKeys() {
        return (List<String>) reportsByKey.keySet();
    }

    public Report getReportByKey(String reportKey){
        return reportsByKey.get(reportKey);
    }
}
