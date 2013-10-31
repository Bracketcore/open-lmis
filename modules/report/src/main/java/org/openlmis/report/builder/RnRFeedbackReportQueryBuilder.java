/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.builder;

import org.openlmis.report.model.report.RnRFeedbackReport;
import org.openlmis.report.model.filter.RnRFeedbackReportFilter;


import java.util.Calendar;
import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class RnRFeedbackReportQueryBuilder {
    public static String SelectFilteredSortedPagedRecords(Map params){


        RnRFeedbackReportFilter filter  = (RnRFeedbackReportFilter)params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("sortCriteria");
        String orderType =   filter.getOrderType() == null ? null : filter.getOrderType();

        //Regular Orders
        if(orderType == null || orderType.isEmpty() || orderType.equals("Regular")){
            // main product
            BEGIN();
            SELECT("facility_code AS facilityCode, facility_name AS facility, productcode as productCode, product, productcode as productCodeMain, product as productMain, dispensingunit AS unit, beginningbalance as beginningBalance, quantityreceived AS totalQuantityReceived, quantitydispensed AS totalQuantityDispensed, totallossesandadjustments AS adjustments, stockinhand AS physicalCount, amc AS adjustedAMC, amc * nominaleop AS newEOP, maxstockquantity AS maximumStock, quantityrequested AS orderQuantity, quantityshipped AS quantitySupplied, quantity_shipped_total AS totalQuantityShipped, 0 AS emergencyOrder, 0 AS productIndex, err_open_balance, err_qty_required, err_qty_received, err_qty_stockinhand");
            FROM("vw_rnr_feedback");
            WHERE("(substitutedproductcode is null or (productcode is not null and substitutedproductcode is not null))");
            writePredicates(filter);
            String query = SQL();
            RESET();
            //substitute product
            BEGIN();
            SELECT("facility_code AS facilityCode, facility_name AS facility, substitutedproductcode as productCode, substitutedproductname as product, productcode as productCodeMain, product as productMain, null AS unit, null as beginningBalance, null as totalQuantityReceived, null AS totalQuantityDispensed, null as adjustments, null AS physicalCount, null AS adjustedAMC, null AS newEOP, null AS maximumStock, null AS orderQuantity, substitutedproductquantityshipped quantitySupplied, null AS totalQuantityShipped, 0 AS emergencyOrder, 1 AS productIndex, 0 as err_open_balance, 0 as err_qty_required, 0 as err_qty_received, 0 as err_qty_stockinhand");
            FROM("vw_rnr_feedback");
            WHERE("substitutedproductcode is not null");
            writePredicates(filter);
            query += " UNION " + SQL() + " order by facility, productcodeMain, productIndex";
            return query;

        } else{  //Emergency orders

            // main product
            BEGIN();
            SELECT("facility_code AS facilityCode, facility_name AS facility, productcode as productCode, product, productcode as productCodeMain, dispensingunit AS unit, beginningbalance as beginningBalance, quantityreceived AS totalQuantityReceived, quantitydispensed AS totalQuantityDispensed, totallossesandadjustments AS adjustments, stockinhand AS physicalCount, amc AS adjustedAMC, amc * nominaleop AS newEOP, maxstockquantity AS maximumStock, quantityrequested AS orderQuantity, quantityshipped AS quantitySupplied, quantity_shipped_total AS totalQuantityShipped, 1 AS emergencyOrder, 0 AS productIndex, err_open_balance, err_qty_required, err_qty_received, err_qty_stockinhand");
            FROM("vw_rnr_feedback");
            WHERE("(substitutedproductcode is null or (productcode is not null and substitutedproductcode is not null))");
            writePredicates(filter);
            String query = SQL();
            RESET();
            //substitute product
            BEGIN();
            SELECT("facility_code AS facilityCode, facility_name AS facility, productcode as productCode, product, productcode as productCodeMain, dispensingunit AS unit, beginningbalance as beginningBalance, quantityreceived AS totalQuantityReceived, quantitydispensed AS totalQuantityDispensed, totallossesandadjustments AS adjustments, stockinhand AS physicalCount, amc AS adjustedAMC, amc * nominaleop AS newEOP, maxstockquantity AS maximumStock, quantityrequested AS orderQuantity, quantityshipped AS quantitySupplied, quantity_shipped_total AS totalQuantityShipped, 1 AS emergencyOrder, 0 AS productIndex, err_open_balance, err_qty_required, err_qty_received, err_qty_stockinhand");
            FROM("vw_rnr_feedback");
            WHERE("substitutedproductcode is not null");
            writePredicates(filter);
            query += " UNION " + SQL() + " order by facility, productMain, productIndex";
            return query;
        }
    }

    private static void writePredicates(RnRFeedbackReportFilter  filter){
        WHERE("req_status = 'RELEASED'");
        WHERE("program_id = "+filter.getProgramId());

        if (filter.getFacilityId() != 0 && filter.getFacilityId() != -1) {
            WHERE("facility_id = "+filter.getFacilityId());
        }

        //WHERE("facility_id = "+filter.getFacilityId());
        WHERE("processing_periods_id = "+filter.getPeriodId());

        if (filter.getProductId() != -1 && filter.getProductId() != 0) {
            WHERE("product_id ="+ filter.getProductId());
        }else if(filter.getProductId()== -1){
            WHERE("indicator_product = true");
        }


    }
}