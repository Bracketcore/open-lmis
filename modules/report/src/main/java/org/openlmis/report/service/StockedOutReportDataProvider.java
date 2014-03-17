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
import org.openlmis.report.mapper.StockedOutReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.StockedOutReportParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class StockedOutReportDataProvider extends ReportDataProvider {

  private StockedOutReportMapper reportMapper;

  private StockedOutReportParam stockedOutReportParam = null;

  @Autowired
  public StockedOutReportDataProvider(StockedOutReportMapper mapper) {
    this.reportMapper = mapper;
  }

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    return reportMapper.getReport(getReportFilterData(filterCriteria), null, rowBounds);
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return reportMapper.getReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
  }

  public StockedOutReportParam getReportFilterData(Map<String, String[]> filterCriteria) {
    if (filterCriteria != null) {
      stockedOutReportParam = new StockedOutReportParam();

      stockedOutReportParam.setFacilityTypeId(StringUtils.isBlank(filterCriteria.get("facilityType")[0]) ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
      stockedOutReportParam.setFacilityId(StringUtils.isBlank(filterCriteria.get("facility")[0]) ? 0 : Integer.parseInt(filterCriteria.get("facility")[0])); //defaults to 0
      stockedOutReportParam.setRgroupId(StringUtils.isBlank(filterCriteria.get("requisitionGroup")[0]) ? 0 : Integer.parseInt(filterCriteria.get("requisitionGroup")[0])); //defaults to 0
      stockedOutReportParam.setProductCategoryId(StringUtils.isBlank(filterCriteria.get("productCategory")[0]) ? 0 : Integer.parseInt(filterCriteria.get("productCategory")[0])); //defaults to 0
      stockedOutReportParam.setProductId(StringUtils.isBlank(filterCriteria.get("product")[0]) ? 0 : Integer.parseInt(filterCriteria.get("product")[0]));
      stockedOutReportParam.setProgramId(StringUtils.isBlank(filterCriteria.get("program")[0]) ? 0 : Integer.parseInt(filterCriteria.get("program")[0]));
      stockedOutReportParam.setPeriodId(StringUtils.isBlank(filterCriteria.get("period")[0]) ? 0 : Integer.parseInt(filterCriteria.get("period")[0]));

    }
    return stockedOutReportParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return getReportFilterData(params).toString();
  }

  @Override
  public HashMap<String, String> getAdditionalReportData(Map params) {
    HashMap<String, String> result = new HashMap<String, String>();

    // spit out the summary section on the report.
    String totalFacilities = reportMapper.getTotalFacilities(params).get(0).toString();
    String stockedOut = reportMapper.getStockedoutTotalFacilities(params).get(0).toString();
    result.put("TOTAL_FACILITIES", totalFacilities);
    result.put("TOTAL_STOCKEDOUT", stockedOut);

    // Assume by default that the 100% of facilities didn't report
    Long percent = Long.parseLong("100");
    if (totalFacilities != "0") {
      percent = Math.round((Double.parseDouble(stockedOut) / Double.parseDouble(totalFacilities)) * 100);

    }
    result.put("PERCENTAGE_STOCKEDOUT", percent.toString());
    return result;
  }


}
