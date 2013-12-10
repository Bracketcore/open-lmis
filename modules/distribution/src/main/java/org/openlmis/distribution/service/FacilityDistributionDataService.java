/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.service.FacilityService;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.EpiUse;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.openlmis.distribution.domain.FacilityDistributionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@NoArgsConstructor
public class FacilityDistributionDataService {

  @Autowired
  FacilityService facilityService;

  @Autowired
  EpiUseService epiUseService;

  public List<FacilityDistributionData> getFor(Distribution distribution) {
    List<Facility> facilities = facilityService.getAllForDeliveryZoneAndProgram(distribution.getDeliveryZone().getId(), distribution.getProgram().getId());
    List<FacilityDistributionData> facilityDistributions = new ArrayList<>();
    for (Facility facility : facilities) {
      facilityDistributions.add(createDistributionData(facility, distribution));
    }
    return facilityDistributions;
  }

  public FacilityDistributionData createDistributionData(Facility facility, Distribution distribution) {
    EpiUse epiUse = createEpiUse(facility, distribution);

    return new FacilityDistributionData(epiUse);
  }

  private EpiUse createEpiUse(Facility facility, Distribution distribution) {
    EpiUse epiUse = new EpiUse(distribution.getId(), facility.getId());

    Set<ProductGroup> productGroupSet = new HashSet<>();

    if (facility.getSupportedPrograms().size() != 0) {
      ProgramSupported programSupported = facility.getSupportedPrograms().get(0);
      for (FacilityProgramProduct facilityProgramProduct : programSupported.getProgramProducts()) {
        if (facilityProgramProduct.isActive() && facilityProgramProduct.getProduct().getActive()) {
          ProductGroup productGroup = facilityProgramProduct.getProduct().getProductGroup();
          if (productGroupSet.add(productGroup)) {
            epiUse.getLineItems().add(new EpiUseLineItem(productGroup.getId(), productGroup.getName()));
          }
        }
      }
      epiUseService.saveLineItems(epiUse);
    }
    return epiUse;
  }
}
