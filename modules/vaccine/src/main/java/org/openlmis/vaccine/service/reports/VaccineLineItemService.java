/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.service.reports;

import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.vaccine.domain.reports.DiseaseLineItem;
import org.openlmis.vaccine.domain.reports.LogisticsLineItem;
import org.openlmis.vaccine.domain.reports.VaccineCoverageItem;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportDiseaseLineItemMapper;
import org.openlmis.vaccine.repository.reports.VaccineReportCoverageItemRepository;
import org.openlmis.vaccine.repository.reports.VaccineReportDiseaseLineItemRepository;
import org.openlmis.vaccine.repository.reports.VaccineReportLogisticsLineItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VaccineLineItemService {

  @Autowired
  VaccineReportLogisticsLineItemRepository repository;

  @Autowired
  VaccineReportDiseaseLineItemRepository diseaseLineItemRepository;
  @Autowired
  VaccineReportCoverageItemRepository coverageItemRepository;

  public void saveLogisticsLineItems(List<LogisticsLineItem> lineItems){
    for(LogisticsLineItem lineItem: lineItems){
      if(lineItem.getId() == null){
        repository.insert(lineItem);
      }else{
        repository.update(lineItem);
      }
    }
  }

  public void saveDiseaseLineItems(List<DiseaseLineItem> lineItems){
    for(DiseaseLineItem lineItem: lineItems){
      if(lineItem.getId() == null){
        diseaseLineItemRepository.insert(lineItem);
      }else{
        diseaseLineItemRepository.update(lineItem);
      }
    }
  }

  public void saveCoverageLineItems(List<VaccineCoverageItem> lineItems) {
    for(VaccineCoverageItem lineItem: lineItems){
      if(lineItem.getId() == null){
        coverageItemRepository.insert(lineItem);
      }else{
        coverageItemRepository.update(lineItem);
      }
    }
  }
}
