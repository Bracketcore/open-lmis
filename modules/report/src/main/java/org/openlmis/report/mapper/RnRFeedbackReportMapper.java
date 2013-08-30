/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.RnRFeedbackReportQueryBuilder;
import org.openlmis.report.model.report.RnRFeedbackReport;
import org.openlmis.report.model.ReportData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User: mahmed
 * Date: 7/11/13
 * Time: 11:42 PM
 */

@Repository
public interface RnRFeedbackReportMapper {
    @SelectProvider(type=RnRFeedbackReportQueryBuilder.class, method="SelectFilteredSortedPagedRecords")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<RnRFeedbackReport> getFilteredSortedPagedRnRFeedbackReport(
            @Param("filterCriteria") ReportData filterCriteria,
            @Param("sortCriteria") Map params,
            @Param("RowBounds") RowBounds rowBounds
    );


}
