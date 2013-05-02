package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.report.mapper.NonReportingFacilityReportMapper;
import org.openlmis.report.mapper.SummaryReportMapper;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openlmis.report.model.report.NonReportingFacilityReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
@Component
@NoArgsConstructor
public class NonReportingFacilityReportDataProvider extends ReportDataProvider {


    private NonReportingFacilityReportMapper reportMapper;


    @Autowired
    public NonReportingFacilityReportDataProvider(NonReportingFacilityReportMapper mapper) {
        this.reportMapper = mapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {

        List<NonReportingFacilityReport> reportList = new ArrayList<NonReportingFacilityReport>();
        NonReportingFacilityReport report = new NonReportingFacilityReport();
        report.details =  reportMapper.getReport(filterCriteria);
        report.summary = reportMapper.getReportSummary(filterCriteria);
        reportList.add( report );

        List<? extends ReportData> list;
        list = reportList;
        return list;
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.0
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        List<NonReportingFacilityReport> reportList = new ArrayList<NonReportingFacilityReport>();
        NonReportingFacilityReport report = new NonReportingFacilityReport();
        report.details =  reportMapper.getReport(filterCriteria);
        report.summary = reportMapper.getReportSummary(filterCriteria);
        reportList.add( report );

        List<? extends ReportData> list;
        list = reportList;
        return list;
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ReportData getReportFilterData(Map<String, String[]> params) {
       return new ReportData() {
                        @Override
                        public String toString() {
                            return "The Period: " ;
                        }
                    };
     }
}
