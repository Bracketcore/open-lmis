/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.AdjustmentSummaryReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.AdjustmentSummaryReportParam;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class AdjustmentSummaryReportDataProvider extends ReportDataProvider {

  @Autowired
  private AdjustmentSummaryReportMapper reportMapper;

  private AdjustmentSummaryReportParam adjustmentSummaryReportParam = null;


  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    return getMainReportData(filterCriteria, null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return reportMapper.getFilteredSortedPagedAdjustmentSummaryReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
  }


  public AdjustmentSummaryReportParam getReportFilterData(Map<String, String[]> filterCriteria) {

    if (filterCriteria != null) {
      adjustmentSummaryReportParam = new AdjustmentSummaryReportParam();
      Date originalStart = new Date();
      Date originalEnd = new Date();

      adjustmentSummaryReportParam.setZoneId(StringUtils.isBlank(filterCriteria.get("zoneId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
      adjustmentSummaryReportParam.setFacilityTypeId(StringUtils.isBlank(filterCriteria.get("facilityTypeId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
      adjustmentSummaryReportParam.setFacilityType(StringUtils.isBlank(filterCriteria.get("facilityType")[0]) ? "All Facilities" : filterCriteria.get("facilityType")[0]);
      adjustmentSummaryReportParam.setRgroup(StringUtils.isBlank(filterCriteria.get("rgroup")[0]) ? "All Reporting Groups" : filterCriteria.get("rgroup")[0]);


      adjustmentSummaryReportParam.setProductCategoryId(StringUtils.isBlank(filterCriteria.get("productCategoryId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0
      adjustmentSummaryReportParam.setProductId(StringUtils.isBlank(filterCriteria.get("productId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
      adjustmentSummaryReportParam.setRgroupId(StringUtils.isBlank(filterCriteria.get("rgroupId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0
      adjustmentSummaryReportParam.setProgramId(StringUtils.isBlank(filterCriteria.get("programId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("programId")[0])); //defaults to 0
      adjustmentSummaryReportParam.setAdjustmentTypeId(StringUtils.isBlank(filterCriteria.get("adjustmentTypeId")[0]) ? "" : filterCriteria.get("adjustmentTypeId")[0]);
      adjustmentSummaryReportParam.setAdjustmentType(StringUtils.isBlank(filterCriteria.get("adjustmentType")[0]) ? "All Adjustment Types" : filterCriteria.get("adjustmentType")[0]);

      adjustmentSummaryReportParam.setYearFrom(StringUtils.isBlank(filterCriteria.get("fromYear")[0]) ? originalStart.getYear() : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
      adjustmentSummaryReportParam.setYearTo(StringUtils.isBlank(filterCriteria.get("toYear")[0]) ? originalEnd.getYear() : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
      adjustmentSummaryReportParam.setMonthFrom(StringUtils.isBlank(filterCriteria.get("fromMonth")[0]) ? originalStart.getMonth() : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
      adjustmentSummaryReportParam.setMonthTo(StringUtils.isBlank(filterCriteria.get("toMonth")[0]) ? originalEnd.getMonth() : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0
      adjustmentSummaryReportParam.setPeriodType(StringUtils.isBlank(filterCriteria.get("periodType")[0]) ? "" : filterCriteria.get("periodType")[0].toString());
      adjustmentSummaryReportParam.setQuarterFrom(StringUtils.isBlank(filterCriteria.get("fromQuarter")[0]) ? 1 : Integer.parseInt(filterCriteria.get("fromQuarter")[0]));
      adjustmentSummaryReportParam.setQuarterTo(StringUtils.isBlank(filterCriteria.get("toQuarter")[0]) ? 1 : Integer.parseInt(filterCriteria.get("toQuarter")[0]));
      adjustmentSummaryReportParam.setSemiAnnualFrom(StringUtils.isBlank(filterCriteria.get("fromSemiAnnual")[0]) ? 1 : Integer.parseInt(filterCriteria.get("fromSemiAnnual")[0]));
      adjustmentSummaryReportParam.setSemiAnnualTo(StringUtils.isBlank(filterCriteria.get("toSemiAnnual")[0]) ? 1 : Integer.parseInt(filterCriteria.get("toSemiAnnual")[0]));

      int monthFrom = 0;
      int monthTo = 0;

      String periodType = adjustmentSummaryReportParam.getPeriodType();

      if (periodType.equals(Constants.PERIOD_TYPE_QUARTERLY)) {
        monthFrom = 3 * (adjustmentSummaryReportParam.getQuarterFrom() - 1);
        monthTo = 3 * adjustmentSummaryReportParam.getQuarterTo() - 1;

      } else if (periodType.equals(Constants.PERIOD_TYPE_MONTHLY)) {
        monthFrom = adjustmentSummaryReportParam.getMonthFrom();
        monthTo = adjustmentSummaryReportParam.getMonthTo();

      } else if (periodType.equals(Constants.PERIOD_TYPE_SEMI_ANNUAL)) {
        monthFrom = 6 * (adjustmentSummaryReportParam.getSemiAnnualFrom() - 1);
        monthTo = 6 * adjustmentSummaryReportParam.getSemiAnnualTo() - 1;
      } else if (periodType.equals(Constants.PERIOD_TYPE_ANNUAL)) {
        monthFrom = 0;
        monthTo = 11;
      }

      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, adjustmentSummaryReportParam.getYearFrom());
      calendar.set(Calendar.MONTH, monthFrom - 1);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.set(Calendar.HOUR, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      adjustmentSummaryReportParam.setStartDate(calendar.getTime());

      calendar.set(Calendar.YEAR, adjustmentSummaryReportParam.getYearTo());
      calendar.set(Calendar.MONTH, monthTo - 1);
      calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
      calendar.set(Calendar.HOUR, 12);
      calendar.set(Calendar.MINUTE, 59);
      calendar.set(Calendar.SECOND, 59);
      calendar.set(Calendar.MILLISECOND, 0);
      adjustmentSummaryReportParam.setEndDate(calendar.getTime());

    }
    return adjustmentSummaryReportParam;

  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return getReportFilterData(params).toString();
  }

}
