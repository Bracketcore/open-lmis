/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.StockImbalanceReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.StockImbalanceReportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * User: Wolde
 * Date: 7/27/13
 * Time: 4:45 PM
 */
@Service
@NoArgsConstructor
public class StockImbalanceReportDataProvider extends ReportDataProvider {

    private StockImbalanceReportMapper reportMapper;

    @Autowired
    public StockImbalanceReportDataProvider(StockImbalanceReportMapper mapper) {
        this.reportMapper = mapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET,RowBounds.NO_ROW_LIMIT);
        return reportMapper.getReport(getReportFilterData(filterCriteria), filterCriteria, rowBounds);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET,RowBounds.NO_ROW_LIMIT);
        return reportMapper.getReport(getReportFilterData(filterCriteria), null, rowBounds);
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1) * pageSize,pageSize);
        return reportMapper.getReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filter) {
        return 0;
    }

    @Override
    public ReportData getReportFilterData(Map<String, String[]> filterCriteria) {
        StockImbalanceReportFilter stockImbalanceReportFilter = null;

        if(filterCriteria != null){
            stockImbalanceReportFilter = new StockImbalanceReportFilter();

            stockImbalanceReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            stockImbalanceReportFilter.setFacilityType( (filterCriteria.get("facilityType") == null || filterCriteria.get("facilityType")[0].equals("")) ? "ALL Facilities" : filterCriteria.get("facilityType")[0]);
            stockImbalanceReportFilter.setFacility(filterCriteria.get("facility") == null ? "" : filterCriteria.get("facility")[0]);

            stockImbalanceReportFilter.setRgroup( (filterCriteria.get("rgroup") == null || filterCriteria.get("rgroup")[0].equals("")) ? "ALL Reporting Groups" : filterCriteria.get("rgroup")[0]);


            stockImbalanceReportFilter.setProductCategoryId(filterCriteria.get("productCategoryId") == null ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0
            stockImbalanceReportFilter.setProductCategory( (filterCriteria.get("productCategory") == null || filterCriteria.get("productCategory")[0].equals("")) ? "ALL Product Categories" : filterCriteria.get("productCategory")[0]);
            stockImbalanceReportFilter.setProductId(filterCriteria.get("productId") == null ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
            stockImbalanceReportFilter.setProduct( (filterCriteria.get("product") == null || filterCriteria.get("productId")[0].equals("")) ? "ALL Products" : (filterCriteria.get("productId")[0].equals("-1") ? "All Indicator Products" : filterCriteria.get("product")[0]));

            stockImbalanceReportFilter.setRgroupId(filterCriteria.get("rgroupId") == null ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0
            stockImbalanceReportFilter.setProgramId(filterCriteria.get("programId") == null ? 0 : Integer.parseInt(filterCriteria.get("programId")[0]));
            stockImbalanceReportFilter.setProgram(filterCriteria.get("program") == null ? "" : filterCriteria.get("program")[0]);
            stockImbalanceReportFilter.setScheduleId(filterCriteria.get("scheduleId") == null ? 0 : Integer.parseInt(filterCriteria.get("scheduleId")[0]));
            stockImbalanceReportFilter.setSchedule(filterCriteria.get("schedule") == null ? "" : filterCriteria.get("schedule")[0]);
            stockImbalanceReportFilter.setPeriod(filterCriteria.get("period") == null ? "" : filterCriteria.get("period")[0]);
            stockImbalanceReportFilter.setPeriodId(filterCriteria.get("periodId") == null ? 0 : Integer.parseInt(filterCriteria.get("periodId")[0]));
            stockImbalanceReportFilter.setYear(filterCriteria.get("year") == null ? 0 : Integer.parseInt(filterCriteria.get("year")[0]));
        }
        return stockImbalanceReportFilter;
    }
}
