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
import org.openlmis.report.mapper.OrderFillRateReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.ReportParameter;
import org.openlmis.report.model.params.OrderFillRateReportParam;
import org.openlmis.report.model.report.MasterReport;
import org.openlmis.report.model.report.OrderFillRateReport;
import org.openlmis.report.util.Constants;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@NoArgsConstructor
public class OrderFillRateReportDataProvider extends ReportDataProvider {

  private OrderFillRateReportMapper reportMapper;
  private ConfigurationSettingService configurationService;

  @Autowired
  public OrderFillRateReportDataProvider(OrderFillRateReportMapper mapper, ConfigurationSettingService configurationService) {
    this.reportMapper = mapper;
    this.configurationService = configurationService;
  }

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    return reportMapper.getReport(getReportFilterData(filterCriteria), null, rowBounds);
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
      List<MasterReport> reportList = new ArrayList<MasterReport>();
      MasterReport report = new MasterReport();
      report.details = reportMapper.getReport(getReportFilterData(filterCriteria),SortCriteria,rowBounds);

      List<OrderFillRateReport> summary = reportMapper.getReportData(getReportFilterData(filterCriteria),SortCriteria,rowBounds);
/*
      OrderFillRateReport  percentage = new OrderFillRateReport();
      percentage.setORDER_FILL_RATE("Percentage Order-Fill-Rate");
      String totalQuantityApproved = reportMapper.getReportTotalQuantityOrdered(filterCriteria).get(0).toString();
      String totalQuantityReceived = reportMapper.getReportTotalQuantityReceived(filterCriteria).get(0).toString();

      Long percent = Long.parseLong("100");
      if (totalQuantityApproved != "0") {
          percent = Math.round((Double.parseDouble(totalQuantityReceived) / Double.parseDouble(totalQuantityApproved)) * 100);
      }

      percentage.setORDER_FILL_RATE(percent.toString()+"%");

      summary.add(0, percentage);*/
      report.summary = summary;
      reportList.add(report);

      List<? extends ReportData> list;
      list = reportList;
      return list;
  }

  public ReportParameter getReportFilterData(Map<String, String[]> filterCriteria) {
    OrderFillRateReportParam orderFillRateReportParam = null;

    if (filterCriteria != null) {
      orderFillRateReportParam = new OrderFillRateReportParam();

        orderFillRateReportParam.setProductCategoryId(StringHelper.isBlank(filterCriteria, "productCategory") ? 0L : Long.parseLong(filterCriteria.get("productCategory")[0]));
        if(filterCriteria.containsKey("product") && !StringHelper.isBlank(filterCriteria,"product")){
            orderFillRateReportParam.setProductId(Long.parseLong(filterCriteria.get("product")[0])); //defaults to 0
        }else{
            orderFillRateReportParam.setProductId(0L);
        }
        orderFillRateReportParam.setScheduleId(StringHelper.isBlank(filterCriteria, "schedule") ? 0 : Integer.parseInt(filterCriteria.get("schedule")[0]));
        orderFillRateReportParam.setRgroupId(StringHelper.isBlank(filterCriteria, "requisitionGroup") ? 0 : Integer.parseInt(filterCriteria.get("requisitionGroup")[0]));
        orderFillRateReportParam.setProgramId(StringHelper.isBlank(filterCriteria,"program") ? 0L : Long.parseLong(filterCriteria.get("program")[0]));
        orderFillRateReportParam.setPeriodId(StringHelper.isBlank(filterCriteria,"period") ? 0L : Long.parseLong(filterCriteria.get("period")[0]));
        orderFillRateReportParam.setFacilityId(StringHelper.isBlank(filterCriteria,"facility") ? 0L : Long.parseLong(filterCriteria.get("facility")[0]));
        orderFillRateReportParam.setFacilityTypeId(StringHelper.isBlank(filterCriteria,"facilityType") ? 0 : Integer.parseInt(filterCriteria.get("facilityType")[0])); //defaults to 0
        //orderFillRateReportParam.setZoneId(StringHelper.isBlank(filterCriteria, "zone")? 0 : Integer.parseInt(filterCriteria.get("zone")[0]));


/*
        orderFillRateReportParam.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
      orderFillRateReportParam.setFacilityType((filterCriteria.get("facilityType") == null || filterCriteria.get("facilityType")[0].equals("")) ? "All Facility Types" : filterCriteria.get("facilityType")[0]);
      orderFillRateReportParam.setFacility(filterCriteria.get("facility") == null ? "" : filterCriteria.get("facility")[0]);

      orderFillRateReportParam.setRgroup((filterCriteria.get("rgroup") == null || filterCriteria.get("rgroup")[0].equals("")) ? "All Reporting Groups" : filterCriteria.get("rgroup")[0]);
      orderFillRateReportParam.setFacilityId(filterCriteria.get("facilityId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityId")[0]));


      orderFillRateReportParam.setProductCategoryId(filterCriteria.get("productCategoryId") == null ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0
      orderFillRateReportParam.setProductCategory((filterCriteria.get("productCategory") == null || filterCriteria.get("productCategory")[0].equals("")) ? "All Product Categories" : filterCriteria.get("productCategory")[0]);
      orderFillRateReportParam.setProductId(filterCriteria.get("productId") == null ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0

      if (orderFillRateReportParam.getProductId() == 0)
        orderFillRateReportParam.setProduct("All Products");
      else if (orderFillRateReportParam.getProductId() == -1)//Indicator Products
        orderFillRateReportParam.setProduct(configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS).isEmpty() ? "Indicator Products" : configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS));
      else
        orderFillRateReportParam.setProduct(filterCriteria.get("product")[0]);

      orderFillRateReportParam.setRgroupId(filterCriteria.get("rgroupId") == null ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0
      orderFillRateReportParam.setProgramId(filterCriteria.get("programId") == null ? 0 : Integer.parseInt(filterCriteria.get("programId")[0]));

      if (orderFillRateReportParam.getProgramId() == 0 || orderFillRateReportParam.getProgramId() == -1)
        orderFillRateReportParam.setProgram("All Programs");
      else
        orderFillRateReportParam.setProgram(filterCriteria.get("program")[0]);

      orderFillRateReportParam.setScheduleId(filterCriteria.get("scheduleId") == null ? 0 : Integer.parseInt(filterCriteria.get("scheduleId")[0]));
      orderFillRateReportParam.setSchedule(filterCriteria.get("schedule") == null ? "" : filterCriteria.get("schedule")[0]);
      orderFillRateReportParam.setPeriod(filterCriteria.get("period") == null ? "" : filterCriteria.get("period")[0]);
      orderFillRateReportParam.setPeriodId(filterCriteria.get("periodId") == null ? 0 : Integer.parseInt(filterCriteria.get("periodId")[0]));
      orderFillRateReportParam.setYear(filterCriteria.get("year") == null ? 0 : Integer.parseInt(filterCriteria.get("year")[0]));
    */
    }
    return orderFillRateReportParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return getReportFilterData(params).toString();
  }

   @Override
   public HashMap<String, String> getAdditionalReportData(Map params){

        HashMap<String, String> result = new HashMap<String, String>() ;

        // spit out the summary section on the report.

       String totalQuantityApproved = reportMapper.getReportTotalQuantityOrdered(params).get(0).toString();
       String totalQuantityReceived = reportMapper.getReportTotalQuantityReceived(params).get(0).toString();

       result.put("total",totalQuantityApproved);
        // Assume by default that the 100% of facilities didn't report
        Long percent = Long.parseLong("100");
       if (totalQuantityApproved != "0") {
           percent = Math.round((Double.parseDouble(totalQuantityReceived) / Double.parseDouble(totalQuantityApproved)) * 100);
       }

        result.put("ORDER_FILL_RATE",percent.toString());
        return result;
    }

}
