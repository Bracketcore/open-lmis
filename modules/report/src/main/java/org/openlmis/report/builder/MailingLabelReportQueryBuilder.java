
/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.builder;

import org.openlmis.report.model.filter.MailingLabelReportFilter;
import org.openlmis.report.model.report.MailingLabelReport;
import org.openlmis.report.model.report.StockImbalanceReport;
import org.openlmis.report.model.sorter.MailingLabelReportSorter;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;

/**
 * User: user
 * Date: 4/10/13
 * Time: 6:36 AM
 */
public class MailingLabelReportQueryBuilder {

    public static String SelectFilteredSortedPagedMailingLabelsSql(Map params){

        MailingLabelReportFilter filter  =(MailingLabelReportFilter)params.get("filterCriteria");
        MailingLabelReportSorter sorter = (MailingLabelReportSorter)params.get("SortCriteria");
        BEGIN();
        SELECT("F.id, F.code, F.name, F.active as active, F.address1, F.address2 , FT.name as facilityType, GZ.name as region, FO.code as owner, F.latitude::text ||',' ||  F.longitude::text  ||', ' || F.altitude::text gpsCoordinates, CASE WHEN U.officePhone IS NULL THEN '' ELSE U.officePhone || ' ,' END || CASE WHEN U.cellPhone IS NULL THEN '' ELSE U.cellPhone || ' ,' END || F.mainPhone as phoneNumber, U.email email, F.fax as fax, U.firstName || ' ' || U.lastName || ', ' || jobtitle contact ");
        FROM("facilities F");
        JOIN("facility_types FT on FT.id = F.typeid");
        LEFT_OUTER_JOIN("geographic_zones GZ on GZ.id = F.geographiczoneid");
        LEFT_OUTER_JOIN("facility_operators FO on FO.id = F.operatedbyid");
        LEFT_OUTER_JOIN("requisition_group_members ON f.id = requisition_group_members.facilityid");
        LEFT_OUTER_JOIN("requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid");
        LEFT_OUTER_JOIN("Users U on U.facilityId = F.id ");

        if(filter != null){
           if (filter.getFacilityTypeId() != 0) {
                WHERE("F.typeid = "+ filter.getFacilityTypeId());
            }
            if(filter.getRgroupId() != 0){
                WHERE("requisition_groups.id = "+ filter.getRgroupId());
            }
        }
        Map sortCriteria = (Map) params.get("filterCriteria");
        ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, MailingLabelReport.class, "F.name asc"));

       /* if(sorter != null){
            if(sorter.getFacilityName().equalsIgnoreCase("asc")){
                ORDER_BY("F.name asc");
            }
            if(sorter.getFacilityName().equalsIgnoreCase("desc")){
                ORDER_BY("F.name desc");
            }

            if(sorter.getCode().equalsIgnoreCase("asc")){
                ORDER_BY("F.code asc");
            }
            if(sorter.getCode().equalsIgnoreCase("desc")){
                ORDER_BY("F.code desc");
            }

            if(sorter.getFacilityType().equalsIgnoreCase("asc")){
                ORDER_BY("F.typeid asc");
            }
            if(sorter.getFacilityType().equalsIgnoreCase("desc")){
                ORDER_BY("F.typeid desc");
            }
        }*/


        return SQL();
    }
}
