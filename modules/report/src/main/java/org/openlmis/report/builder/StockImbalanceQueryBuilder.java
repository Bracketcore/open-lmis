package org.openlmis.report.builder;

import org.openlmis.report.model.filter.StockImbalanceReportFilter;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * User: Wolde
 * Date: 7/27/13
 * Time: 4:40 PM
 */
public class StockImbalanceQueryBuilder {
    public static String getQuery(Map params){


        StockImbalanceReportFilter filter  = (StockImbalanceReportFilter)params.get("filterCriteria");
        BEGIN();
        SELECT("supplyingfacility,  facility,  product,  stockinhand physicalCount,  amc,  mos months,  required orderQuantity, CASE WHEN status = 'SO' THEN  'Stocked Out' WHEN status ='US' then  'Below Minimum' WHEN status ='OS' then  'Over Stocked' END AS status ");
        FROM("vw_stock_status");
        writePredicates(filter);
        ORDER_BY("supplyingfacility");
        return SQL();

        }
    private static void writePredicates(StockImbalanceReportFilter filter){
        WHERE("status <> 'SP'");
        if(filter != null){
            if (filter.getFacilityTypeId() != 0 && filter.getFacilityTypeId() != -1) {
                WHERE("facilitytypeid = #{filterCriteria.facilityTypeId}");
            }
            if(filter.getFacility() != null && !filter.getFacility().isEmpty()){
                WHERE("facility = #{filterCriteria.facility}");
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
            if(filter.getProductId() != 0){
                WHERE("productid= #{filterCriteria.productId}");
            } else {
                WHERE("indicator_product = true");
            }
        }
    }
}
