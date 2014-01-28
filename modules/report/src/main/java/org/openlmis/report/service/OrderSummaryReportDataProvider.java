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
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.report.mapper.OrderSummaryReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.OrderReportParam;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class OrderSummaryReportDataProvider extends ReportDataProvider {

  @Autowired
  private OrderSummaryReportMapper reportMapper;

  @Autowired
  private ConfigurationSettingService configurationService;

  @Autowired
  private ReportLookupService reportLookupService;

  private OrderReportParam orderReportParam;


  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    return getMainReportData(filterCriteria, filterCriteria, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return reportMapper.getFilteredSortedPagedOrderSummaryReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
  }

  public OrderReportParam getReportFilterData(Map<String, String[]> filterCriteria) {

    if (filterCriteria != null) {
      orderReportParam = new OrderReportParam();

      if (filterCriteria.containsKey("orderId")) {
        orderReportParam.setOrderId(Long.parseLong(filterCriteria.get("orderId")[0]));

        orderReportParam.setFacility(reportLookupService.getFacilityNameForRnrId(orderReportParam.getOrderId()));
        orderReportParam.setPeriod(reportLookupService.getPeriodTextForRnrId(orderReportParam.getOrderId()));
        orderReportParam.setProgram(reportLookupService.getProgramNameForRnrId(orderReportParam.getOrderId()));
        orderReportParam.setProduct("All products");
      } else {

        orderReportParam.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
        orderReportParam.setFacilityId(filterCriteria.get("facilityId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityId")[0])); //defaults to 0
        orderReportParam.setFacilityType((filterCriteria.get("facilityType") == null || filterCriteria.get("facilityType")[0].equals("")) ? "ALL Facilities" : filterCriteria.get("facilityType")[0]);

        orderReportParam.setScheduleId(filterCriteria.get("scheduleId") == null ? 0 : Integer.parseInt(filterCriteria.get("scheduleId")[0])); //defaults to 0
        orderReportParam.setSchedule(filterCriteria.get("schedule")[0]);

        orderReportParam.setProductId(filterCriteria.get("productId") == null ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
        if (orderReportParam.getProductId() == 0) {
          orderReportParam.setProduct("All Products");
        } else if (orderReportParam.getProductId() == -1) {
          orderReportParam.setProduct(configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS).isEmpty() ? "Indicator Products" : configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS));
        }
        orderReportParam.setOrderType(filterCriteria.get("orderType") == null ? "" : filterCriteria.get("orderType")[0]);
        orderReportParam.setPeriodId(filterCriteria.get("periodId") == null ? 0 : Integer.parseInt(filterCriteria.get("periodId")[0])); //defaults to 0
        orderReportParam.setPeriod(filterCriteria.get("period")[0]);
        orderReportParam.setProgramId(filterCriteria.get("programId") == null ? 0 : Integer.parseInt(filterCriteria.get("programId")[0])); //defaults to 0
        orderReportParam.setProgram(filterCriteria.get("program")[0]);
      }
    }
    return orderReportParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return getReportFilterData(params).toString();
  }

  @Override
  public HashMap<String, String> getAdditionalReportData(Map params) {
    HashMap<String, String> result = new HashMap<String, String>();
    result.put("ADDRESS", configurationService.getConfigurationStringValue("ORDER_REPORT_ADDRESS"));
    result.put("CUSTOM_REPORT_TITLE", configurationService.getConfigurationStringValue("ORDER_REPORT_TITLE"));
    result.put("ORDER_SUMMARY_SHOW_SIGNATURE_SPACE_FOR_CUSTOMER", configurationService.getConfigurationStringValue("ORDER_SUMMARY_SHOW_SIGNATURE_SPACE_FOR_CUSTOMER"));
    result.put("ORDER_SUMMARY_SHOW_DISCREPANCY_SECTION", configurationService.getConfigurationStringValue("ORDER_SUMMARY_SHOW_DISCREPANCY_SECTION"));
    return result;
  }
}
