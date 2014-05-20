/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.report.model.ReportParameter;

import java.text.SimpleDateFormat;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class RegimenSummaryReportParam
        extends BaseParam implements ReportParameter {

    private long period;
    private ProcessingPeriod periodObject;

    private int rgroupId;
    private int regimenCategoryId;
    private String regimenCategory;
    private String rgroup;
    private String regimen;
    private Integer regimenId;
    private int programId;
    private String program;
    private int scheduleId;
    private String schedule;
    private int periodId;
    private Integer year;

    SimpleDateFormat dateFormatter = new SimpleDateFormat();

    @Override
    public String toString() {
        if (periodObject != null) {

            StringBuilder filtersValue = new StringBuilder("");
            filtersValue.append("Period : ").append(dateFormatter.format(this.periodObject.getNextStartDate())).append("-").append(dateFormatter.format(this.periodObject.getEndDate())).append("\n").
                    append("Regimen Category : ").append(this.getRegimenCategory()).append("\n").
                    append("Regimen : ").append(this.getRegimen()).append("\n").
                    append("Schedule : ").append(this.getSchedule()).append("\n").
                    append("Program : ").append(this.getProgram()).append("\n").
                    append("Reporting Groups : ").append(this.getRgroup());

            return filtersValue.toString();
        } else {
            return "No filters selected";
        }
    }


}
