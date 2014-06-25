package org.openlmis.report.service.lookup;

import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.openlmis.report.mapper.lookup.RnRStatusSummaryReportMapper;
import org.openlmis.report.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * User: Issa
 * Date: 2/18/14
 * Time: 5:32 PM
 */
@Service
public class DashboardLookupService {

    @Autowired
    DashboardMapper dashboardMapper;
   // @Autowired
    //AverageConsumptionReportMapper avgMapper;

    @Autowired
    RnRStatusSummaryReportMapper rnRStatusSummaryReportMapper;

    public static String  getCommaSeparatedIds(List<Long> idList){

        return idList == null ? "{}" : idList.toString().replace("[", "{").replace("]", "}");
    }

    public List<ItemFillRate> getItemFillRate(Long periodId, Long facilityId, Long programId,List<Long> productListId){

        return dashboardMapper.getItemFillRate(periodId, facilityId, programId,getCommaSeparatedIds(productListId));
    }

    public OrderFillRate getOrderFillRate(Long periodId, Long facilityId, Long programId){

        return dashboardMapper.getOrderFillRate(periodId, facilityId, programId);
    }

    public List<ShipmentLeadTime> getShipmentLeadTime(Long periodId, Long programId, List<Long> rgroupId){
        return dashboardMapper.getShipmentLeadTime(periodId,programId, getCommaSeparatedIds(rgroupId));

    }

    public List<StockingInfo> getStockEfficiencyData(Long userId,Long periodId, Long programId, Long zoneId, List<Long> productListId){
        return dashboardMapper.getStockEfficiencyData(userId,periodId, programId, zoneId , getCommaSeparatedIds(productListId));

    }
    public List<StockingInfo> getStockEfficiencyDetailData(Long userId,Long periodId, Long programId, Long zoneId, List<Long> productListId){
        return dashboardMapper.getStockEfficiencyDetailData(userId, periodId, programId,zoneId, getCommaSeparatedIds(productListId));

    }

    public List<StockOut> getStockOutFacilities(Long periodId, Long programId, Long productId, List<Long> requisitionGroupId){
        return dashboardMapper.getStockOutFacilities(periodId, programId, productId, getCommaSeparatedIds(requisitionGroupId));

    }
    public List<StockOut> getStockOutFacilitiesByRequisitionGroup(Long periodId, Long programId, Long productId, Long requisitionGroupId){
        return dashboardMapper.getStockOutFacilitiesForRequisitionGroup(periodId, programId, productId, requisitionGroupId);

    }
    public List<AlertSummary> getAlerts(Long userId, Long programId, Long periodId, Long zoneId){
        return dashboardMapper.getAlerts(userId, programId, periodId, zoneId );

    }
    public List<AlertSummary> getStockedOutAlerts(Long userId, Long programId, Long periodId, Long zoneId){
        return dashboardMapper.getStockedOutAlerts(userId, programId, periodId, zoneId );

    }

    public List<AlertSummary> getNotificationAlerts(){
        return dashboardMapper.getNotificationAlerts();

    }

    public List<HashMap> getNotificationsByCategory(Long userId, Long programId, Long periodId, Long zoneId,String detailTable) {
        if (detailTable == null || detailTable.isEmpty()) return null;
        return dashboardMapper.getNotificationDetails(userId,programId,periodId,zoneId,detailTable);
    }

    public List<HashMap> getStockedOutNotificationDetails(Long userId, Long programId, Long periodId, Long zoneId, Long productId,String detailTable) {
        if (detailTable == null || detailTable.isEmpty()) return null;
        return dashboardMapper.getStockedOutNotificationDetails(userId,programId,periodId,zoneId,productId,detailTable);
    }

    public void sendNotification(Notification notification){
        if(notification == null) return;

        if(notification.getEmails()!= null && !notification.getEmails().isEmpty()){
            for (String email : notification.getEmails()){
                if(email != null && !email.isEmpty()){
                    dashboardMapper.saveEmailNotification(email,notification.getEmailMessage());
                }
            }
        }

        if (notification.getPhoneNumbers() != null && !notification.getPhoneNumbers().isEmpty()){
            for (String phoneNumber : notification.getPhoneNumbers()){
                if(phoneNumber !=null && !phoneNumber.isEmpty()){
                    dashboardMapper.saveSmsNotification(notification.getSmsMessage(),phoneNumber,"O");
                }
            }
        }
    }

    public String getYearOfPeriodById(Long id){
        return dashboardMapper.getYearOfPeriodById(id);
    }

    public List<RnRStatusSummaryReport>getRnRStatusSummary(Long requisionGroupId){
        return rnRStatusSummaryReportMapper.getRnRStatusSummaryData(requisionGroupId);
    }

    public List<HashMap> getReportingPerformance(Long userId,Long periodId, Long programId,  Long zoneId){
        return dashboardMapper.getReportingPerformance(userId,periodId,programId, zoneId);
    }
    public List<ReportingPerformance> getReportingPerformanceDetail(Long userId,Long periodId, Long programId, Long zoneId, String status){
        return dashboardMapper.getReportingPerformanceDetail(userId,periodId,programId,zoneId, status);
    }
    public List<RnRStatusSummaryReport>getRnRStatusDetails(Long requisitionGroupId,Long programId,Long periodId){
        return rnRStatusSummaryReportMapper.getRnRStatusDetails(requisitionGroupId,programId,periodId);
    }

    public List<RnRStatusSummaryReport>getRnRStatusByRequisitionGroupAndPeriod(Long requisitionGroupId,Long periodId,Long programId){
        return  rnRStatusSummaryReportMapper.getRnRStatusByRequisitionGroupAndPeriod(requisitionGroupId,periodId,programId);
    }
    public List<RnRStatusSummaryReport>getRnRStatusByRequisitionGroupAndPeriodData(Long requisitionGroupId,Long periodId){
        return rnRStatusSummaryReportMapper.getRnRStatusByRequisitionGroupAndPeriodData(requisitionGroupId,periodId);
    }

    public List<RnRStatusSummaryReport> getRnRStatusDetail(Long periodId, Long programId,  Long requisitionGroupId, String status){
        return rnRStatusSummaryReportMapper.getRnRStatusDetail(periodId,programId, requisitionGroupId, status);
    }

}
