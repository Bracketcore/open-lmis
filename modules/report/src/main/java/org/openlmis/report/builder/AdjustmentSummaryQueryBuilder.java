/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.report.builder;

import org.openlmis.report.model.filter.AdjustmentSummaryReportFilter;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;


public class AdjustmentSummaryQueryBuilder {
    public static String SelectFilteredSortedPagedRecords(Map params){

        AdjustmentSummaryReportFilter filter  = (AdjustmentSummaryReportFilter)params.get("filterCriteria");
        Map<String, String[]> sorter = ( Map<String, String[]>)params.get("SortCriteria");
        BEGIN();

        SELECT("processing_periods_name as period, product productDescription, product_category_name category, facility_type_name facilityType,facility_name facilityName, adjustment_type adjustmentType, adjutment_qty adjustment, adjutment_qty * case when adjustment_additive  = 't' then 1 else -1 end AS signedadjustment, supplying_facility_name supplyingFacility");
        FROM("vw_requisition_adjustment");
        writePredicates(filter);
        //GROUP_BY("product, adjustment_type,product_category_name,facility_type_name,facility_name, supplying_facility_name, processing_periods_name");
      ORDER_BY(QueryHelpers.getSortOrder(params, "facility_type_name,facility_name, supplying_facility_name, product, product_category_name , adjustment_type"));

      // write the query to a variable, this is to make it easier to debug the query.
      String query = SQL();
      return query;
    }

    private static void writePredicates(AdjustmentSummaryReportFilter filter){
        WHERE("req_status in ('APPROVED','RELEASED')");
        if(filter != null){
            if (filter.getFacilityTypeId() != 0) {
                WHERE("facility_type_id = #{filterCriteria.facilityTypeId}");
            }

            if (filter.getStartDate() != null) {
                WHERE("processing_periods_start_date >= #{filterCriteria.startDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if (filter.getEndDate() != null) {
                WHERE("processing_periods_end_date <= #{filterCriteria.endDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if(filter.getProductCategoryId() != 0 ){
                WHERE("product_category_id = #{filterCriteria.productCategoryId}");
            }
            if(filter.getRgroupId() != 0){
                WHERE("requisition_group_id = #{filterCriteria.rgroupId}");
            }
            if(filter.getProductId() != -1 && filter.getProductId() != 0){
                WHERE("product_id= #{filterCriteria.productId}");
            }
            if(filter.getProgramId() != -1){  //Unless All programs selected
                WHERE("program_id = #{filterCriteria.programId}");
            }
            if(!filter.getAdjustmentTypeId().equals("-1") && !filter.getAdjustmentTypeId().equals("0") && !filter.getAdjustmentTypeId().equals("") ){
                WHERE("adjustment_type = #{filterCriteria.adjustmentTypeId}");
            }
        }
    }
}
