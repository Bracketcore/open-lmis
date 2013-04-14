package org.openlmis.report.service;

import org.openlmis.report.DataSourceType;
import org.openlmis.report.model.ReportData;

import java.util.List;

/**
 */
public abstract class ReportDataProvider {

    public final List<? extends ReportData> getReportDataByFilterCriteria(ReportData filterCriteria, DataSourceType dataSourceType){

        if(dataSourceType.equals(DataSourceType.BEAN_COLLECTION_DATA_SOURCE)){
            return getBeanCollectionReportData(filterCriteria);
        }else if (dataSourceType.equals(DataSourceType.RESULT_SET_DATA_SOURCE)){
            return getResultSetReportData(filterCriteria);
        }

        return null;
    }
    public final List<? extends ReportData> getReportDataByFilterCriteria(ReportData filterCriteria){
        return getReportDataByFilterCriteria(filterCriteria, DataSourceType.BEAN_COLLECTION_DATA_SOURCE);
    }

    protected abstract List<? extends ReportData> getBeanCollectionReportData(ReportData filterCriteria);
    protected abstract List<? extends ReportData> getResultSetReportData(ReportData filterCriteria);
    public abstract List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(ReportData filterCriteria,ReportData SortCriteria ,int page,int pageSize);
    public abstract int getReportDataCountByFilterCriteria(ReportData facilityReportFilter);
}
