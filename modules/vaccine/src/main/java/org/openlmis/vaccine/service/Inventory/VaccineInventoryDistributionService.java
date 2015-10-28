/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.service.Inventory;

import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItem;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItemLot;
import org.openlmis.vaccine.repository.Inventory.VaccineInventoryDistributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Exposes the services for handling stock cards.
 */

@Service
@NoArgsConstructor
public class VaccineInventoryDistributionService {

    @Autowired
    VaccineInventoryDistributionRepository repository;

    @Autowired
    ProgramService programService;

    @Autowired
    FacilityService facilityService;

    public void save(VaccineDistribution distribution, Long userId) {
        //Get supervised facility period
        Facility homeFacility = facilityService.getHomeFacility(userId);
        Long facilityId = homeFacility.getId();
        List<Program> programs = programService.getAllIvdPrograms();
        Long programId = programs.get(0).getId();

        ProcessingPeriod period = getCurrentPeriod(facilityId, programId, distribution.getDistributionDate());
        if (period != null) {
            distribution.setPeriodId(period.getId());
        }

        if (distribution.getId() != null) {
            repository.updateDistribution(distribution);
        } else {
            repository.saveDistribution(distribution);
        }

        for (VaccineDistributionLineItem lineItem : distribution.getLineItems()) {
            lineItem.setDistributionId(distribution.getId());
            if (lineItem.getId() != null) {
                repository.updateDistributionLineItem(lineItem);
            } else {
                repository.saveDistributionLineItem(lineItem);
            }

            if (lineItem.getLots() != null) {
                for (VaccineDistributionLineItemLot lot : lineItem.getLots()) {
                    lot.setDistributionLineItemId(lineItem.getId());
                    if (lot.getId() != null) {
                        repository.updateDistributionLineItemLot(lot);
                    } else {
                        repository.saveDistributionLineItemLot(lot);
                    }
                }
            }
        }
    }

    public List<VaccineDistribution> getDistributedFacilitiesByPeriod(Long userId) {
        Facility homeFacility = facilityService.getHomeFacility(userId);
        Long facilityId = homeFacility.getId();
        List<Program> programs = programService.getAllIvdPrograms();
        Long programId = programs.get(0).getId();

        ProcessingPeriod period = getCurrentPeriod(facilityId, programId, new Date());
        if (period != null) {
            return repository.getDistributedFacilitiesByPeriod(period.getId());
        } else {
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH) + 1;
            return repository.getDistributedFacilitiesByMonth(month);
        }
    }

    public List<VaccineDistribution> getDistributedFacilitiesByDate(Long userId) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH) + 1;

        return repository.getDistributedFacilitiesByMonth(month);
    }

    public ProcessingPeriod getCurrentPeriod(Long facilityId, Long programId, Date distributionDate) {
        return repository.getCurrentPeriod(facilityId, programId, distributionDate);
    }

}
