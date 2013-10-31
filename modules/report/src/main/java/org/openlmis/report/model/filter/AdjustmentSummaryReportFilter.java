/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.model.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import java.text.DateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentSummaryReportFilter implements ReportData {

    private int userId;

    private String periodType;
    private int yearFrom;
    private int yearTo;
    private int monthFrom;
    private int monthTo;
    private Date startDate;
    private Date endDate;
    private int quarterFrom;
    private int quarterTo;
    private int semiAnnualFrom;
    private int semiAnnualTo;

    private int facilityTypeId;
    private String facilityType;
    private int zoneId;
    private int productId;
    private int productCategoryId;
    private int rgroupId;
    private String rgroup;
    private int programId;
    private String  adjustmentTypeId;
    private String adjustmentType;


    @Override
    public String toString(){
         if(this.getStartDate() != null && this.getEndDate() != null){
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
            StringBuilder filtersValue = new StringBuilder("");
            filtersValue.append("Period : ").append(dateFormatter.format(this.getStartDate())).append("-").append(dateFormatter.format(this.getEndDate())).append("\n").
                    append("Facility Types : ").append(this.getFacilityType()).append("\n").
                    append("Adjustment Types : ").append(this.getAdjustmentType()).append("\n").
                    append("Reporting Groups : ").append(this.getRgroup());

            return filtersValue.toString();
         }   else{
           return "No filters selected";
         }
    }
}
