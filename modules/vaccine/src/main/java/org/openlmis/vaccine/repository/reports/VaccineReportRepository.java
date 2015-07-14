/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.reports;

import org.openlmis.vaccine.domain.reports.*;
import org.openlmis.vaccine.dto.ReportStatusDTO;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportMapper;
import org.openlmis.vaccine.service.reports.VaccineLineItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class VaccineReportRepository {

  @Autowired
  VaccineReportMapper mapper;

  @Autowired
  VaccineLineItemService lineItemService;


  public void insert(VaccineReport report){
    mapper.insert(report);
    saveDetails(report);
  }


  public void saveDetails(VaccineReport report){
    lineItemService.saveLogisticsLineItems(report.getLogisticsLineItems(), report.getId());
    lineItemService.saveDiseaseLineItems(report.getDiseaseLineItems(), report.getId());
    lineItemService.saveCoverageLineItems(report.getCoverageLineItems(),report.getId());
    lineItemService.saveColdChainLIneItems(report.getColdChainLineItems(), report.getId());
    lineItemService.saveVitaminLineItems(report.getVitaminSupplementationLineItems(), report.getId());
    lineItemService.saveAdverseEffectLineItems(report.getAdverseEffectLineItems(), report.getId());
    lineItemService.saveCampaignLineItems(report.getCampaignLineItems(), report.getId());
  }

  public void update(VaccineReport report){
    mapper.update(report);
    saveDetails(report);
  }

  public VaccineReport getById(Long id){
    return mapper.getById(id);
  }

  public VaccineReport getByIdWithFullDetails(Long id){
    return mapper.getByIdWithFullDetails(id);
  }

  public VaccineReport getByProgramPeriod(Long facilityId, Long programId, Long periodId ){
    return mapper.getByPeriodFacilityProgram(facilityId, programId, periodId);
  }

  public VaccineReport getLastReport(Long facilityId, Long programId) {
    return mapper.getLastReport(facilityId, programId);
  }

  public Long getScheduleFor(Long facilityId, Long programId) {
    return mapper.getScheduleFor(facilityId, programId);
  }

  public List<ReportStatusDTO> getReportedPeriodsForFacility(Long facilityId, Long programId) {
    return mapper.getReportedPeriodsForFacility(facilityId, programId);
  }
  public Long getReportIdForFacilityAndPeriod(Long facilityId, Long periodId){
    return mapper.getReportIdForFacilityAndPeriod(facilityId, periodId);
  }
  public List<DiseaseLineItem> getDiseaseSurveillance(Long reportId){
    return mapper.getDiseaseSurveillance(reportId);
  }

  public List<ColdChainLineItem> getColdChain(Long reportId){
    return mapper.getColdChain(reportId);
  }

  public List<AdverseEffectLineItem> getAdverseEffectReport(Long reportId){
    return mapper.getAdverseEffectReport(reportId);
  }

  public List<HashMap<String, Object>> getVaccineCoverageReport(Long reportId){
    return mapper.getVaccineCoverageReport(reportId);
  }

  public List<VaccineReport> getImmunizationSession(Long reportId){
    return mapper.getImmunizationSession(reportId);
  }

  public List<HashMap<String, Object>> getVaccinationReport(String productCategoryCode, Long reportId){
    return mapper.getVaccinationReport(productCategoryCode, reportId);
  }

  public List<HashMap<String, Object>> getTargetPopulation(Long facilityId, Long periodId){
    return mapper.getTargetPopulation(facilityId, periodId);
  }
  public List<VitaminSupplementationLineItem> getVitaminSupplementationReport(Long reportId){
    return mapper.getVitaminSupplementationReport(reportId);
  }
}
