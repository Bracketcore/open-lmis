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
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RefrigeratorService;
import org.openlmis.distribution.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.collect;
import static org.apache.commons.collections.CollectionUtils.select;

@Service
@NoArgsConstructor
public class FacilityDistributionService {

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private RefrigeratorService refrigeratorService;

  @Autowired
  private EpiUseService epiUseService;

  @Autowired
  private FacilityVisitService facilityVisitService;

  @Autowired
  private DistributionRefrigeratorsService distributionRefrigeratorsService;

  @Autowired
  private EpiInventoryService epiInventoryService;

  @Autowired
  private VaccinationCoverageService vaccinationCoverageService;

  public Map<Long, FacilityDistribution> createFor(Distribution distribution) {
    Long deliveryZoneId = distribution.getDeliveryZone().getId();
    Long programId = distribution.getProgram().getId();

    Map<Long, FacilityDistribution> facilityDistributions = new HashMap<>();

    List<Facility> facilities = facilityService.getAllForDeliveryZoneAndProgram(deliveryZoneId, programId);
    List<Refrigerator> distributionRefrigerators = refrigeratorService.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZoneId, programId);
    Boolean isChildCoverage = true;
    List<VaccinationProduct> vaccinationProducts = vaccinationCoverageService.getReferenceData(isChildCoverage);

    for (Facility facility : facilities) {
      facilityDistributions.put(facility.getId(), createDistributionData(facility, distribution, distributionRefrigerators, vaccinationProducts));
    }

    return facilityDistributions;
  }

  FacilityDistribution createDistributionData(final Facility facility, Distribution distribution, List<Refrigerator> refrigerators, List<VaccinationProduct> vaccinationProducts) {
    List<RefrigeratorReading> refrigeratorReadings = getRefrigeratorReadings(facility.getId(), refrigerators);

    FacilityVisit facilityVisit = new FacilityVisit(facility, distribution);
    facilityVisitService.save(facilityVisit);
    FacilityDistribution facilityDistribution = new FacilityDistribution(facilityVisit, facility, distribution, refrigeratorReadings, vaccinationProducts);
    epiUseService.save(facilityDistribution.getEpiUse());
    epiInventoryService.save(facilityDistribution.getEpiInventory());
    return facilityDistribution;
  }

  private List<RefrigeratorReading> getRefrigeratorReadings(final Long facilityId, List<Refrigerator> refrigerators) {
    return (List<RefrigeratorReading>) collect(select(refrigerators, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((Refrigerator) o).getFacilityId().equals(facilityId);
      }
    }), new Transformer() {
      @Override
      public Object transform(Object o) {
        return new RefrigeratorReading((Refrigerator) o);
      }
    });
  }


  public boolean save(FacilityDistribution facilityDistribution) {
    boolean canSync = facilityVisitService.save(facilityDistribution.getFacilityVisit());
    if (canSync) {
      epiUseService.save(facilityDistribution.getEpiUse());
      distributionRefrigeratorsService.save(facilityDistribution.getFacilityVisit().getFacilityId(), facilityDistribution.getRefrigerators());
      vaccinationCoverageService.save(facilityDistribution.getFullCoverage());
      epiInventoryService.save(facilityDistribution.getEpiInventory());
    }
    return canSync;
  }

  public Map<Long, FacilityDistribution> get(Distribution distribution) {
    Map<Long, FacilityDistribution> facilityDistributions = new HashMap<>();

    List<FacilityVisit> unSyncedFacilities = facilityVisitService.getUnSyncedFacilities(distribution.getId());
    for (FacilityVisit facilityVisit : unSyncedFacilities) {
      facilityDistributions.put(facilityVisit.getFacilityId(), getDistributionData(facilityVisit, distribution));
    }
    return facilityDistributions;
  }

  private FacilityDistribution getDistributionData(FacilityVisit facilityVisit, Distribution distribution) {
    EpiUse epiUse = epiUseService.getBy(facilityVisit.getId());

    List<Refrigerator> refrigerators = refrigeratorService.getRefrigeratorsForADeliveryZoneAndProgram(distribution.getDeliveryZone().getId(), distribution.getProgram().getId());
    DistributionRefrigerators distributionRefrigerators = new DistributionRefrigerators(getRefrigeratorReadings(facilityVisit.getFacilityId(), refrigerators));

    Facility facility = facilityService.getById(facilityVisit.getFacilityId());

    EpiInventory epiInventory = epiInventoryService.getBy(facilityVisit.getId());
    VaccinationFullCoverage coverage = vaccinationCoverageService.getBy(facilityVisit.getId());

    FacilityDistribution facilityDistribution = new FacilityDistribution(facilityVisit, epiUse, distributionRefrigerators, epiInventory, coverage);
    facilityDistribution.setFacility(facility);
    return facilityDistribution;
  }
}
