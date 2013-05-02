package org.openlmis.report.model.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionReportFilter implements ReportData {

    //top filters
    private int userId;

    // period selections
    //private String periodType;
    private int yearFrom;
    private int yearTo;
    private int monthFrom;
    private int monthTo;

    private int facilityTypeId;
    private int zoneId;
    private int productId;
    private int facilityId;

    private Date startDate;
    private Date endDate;

}
