/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.builder;

import org.openlmis.report.model.filter.StockedOutReportFilter;
import org.openlmis.report.model.report.StockedOutReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/* Date: 4/11/13
* Time: 11:34 AM
*/
public class StockedOutReportQueryBuilder {

    public static String getQuery(Map params){


        StockedOutReportFilter filter  = (StockedOutReportFilter)params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("SortCriteria");
        BEGIN();
        SELECT("DISTINCT supplyingfacility,facilitycode, facility, product, facilitytypename, location, processing_period_name");
        FROM("vw_stock_status");
        WHERE("status = 'SO'");
        writePredicates(filter);
        ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, StockedOutReport.class,"supplyingfacility asc, facility asc, product asc, processing_period_name asc"));
        // copy the sql over to a variable, this makes the debugging much more possible.
        String sql = SQL();
        return sql;


    }
    private static void writePredicates(StockedOutReportFilter filter){
        WHERE("req_status in ('APPROVED','RELEASED')");
        if(filter != null){
            if (filter.getFacilityTypeId() != 0 && filter.getFacilityTypeId() != -1) {
                WHERE("facilitytypeid = #{filterCriteria.facilityTypeId}");
            }
            if (filter.getZoneId() != 0 && filter.getZoneId() != -1) {
                WHERE("gz_id = #{filterCriteria.zoneId}");
            }
            if (filter.getStartDate() != null) {
                WHERE("startdate >= #{filterCriteria.startDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if (filter.getEndDate() != null) {
                WHERE("enddate <= #{filterCriteria.endDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if(filter.getProductCategoryId() != 0 && filter.getProductCategoryId() != -1 ){
                WHERE("categoryid = #{filterCriteria.productCategoryId}");
            }
            if(filter.getRgroupId() != 0 && filter.getRgroupId() != -1){
                WHERE("rgid = #{filterCriteria.rgroupId}");
            }
            if(filter.getProductId() > 0){
                WHERE("productid= #{filterCriteria.productId}");
            } else if (filter.getProductId() == 0) {
                WHERE("indicator_product = true");
            }
            if(filter.getProgramId() != 0 && filter.getProgramId() != -1){
                 WHERE("programid = #{filterCriteria.programId}");
            }
            if(filter.getFacilityId() != 0 && filter.getFacilityId() != -1){
                WHERE("facility_id = #{filterCriteria.facilityId}");
            }
        }
    }

    public static String getTotalFacilities(Map params){

        StockedOutReportFilter filter  = (StockedOutReportFilter)params.get("filterCriteria");

        BEGIN();
        SELECT("COUNT(*) facilityCount");
        FROM("vw_stock_status");
        writePredicates(filter);
        return SQL();
    }

    public static String getTotalStockedoutFacilities(Map params){

        StockedOutReportFilter filter  = (StockedOutReportFilter)params.get("filterCriteria");

        BEGIN();
        SELECT("COUNT(*) facilityCount");
        FROM("vw_stock_status");
        WHERE("status = 'SO'");
        writePredicates(filter);
        return SQL();
    }




}
