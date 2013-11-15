/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.order.service.OrderService;
import org.openlmis.restapi.domain.ReplenishmentDTO;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.openlmis.restapi.domain.ReplenishmentDTO.prepareForREST;

@Service
@NoArgsConstructor
public class RestRequisitionService {

  @Autowired
  private RequisitionService requisitionService;

  @Autowired
  private OrderService orderService;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProgramService programService;

  @Transactional
  public Rnr submitReport(Report report, Long userId) {
    report.validate();

    Facility reportingFacility = facilityService.getOperativeFacilityByCode(report.getAgentCode());
    Program reportingProgram = programService.getValidatedProgramByCode(report.getProgramCode());

    validate(reportingFacility, reportingProgram);

    Rnr rnr = requisitionService.initiate(reportingFacility, reportingProgram, userId, false);

    report.getRnrWithSkippedProducts(rnr);

    return requisitionService.save(rnr);
  }

  private void validate(Facility reportingFacility, Program reportingProgram) {
    if (reportingFacility.getVirtualFacility()) return;

    RequisitionSearchCriteria searchCriteria = new RequisitionSearchCriteria();
    searchCriteria.setProgramId(reportingProgram.getId());
    searchCriteria.setFacilityId(reportingFacility.getId());
    if (!requisitionService.getCurrentPeriod(searchCriteria).getId().equals(requisitionService.getPeriodForInitiating(reportingFacility, reportingProgram))) {
      throw new DataException("error.rnr.previous.not.filled");
    }
  }

  @Transactional
  public void approve(Report report, Long userId) {
    Rnr requisition = report.getRequisition();
    requisition.setModifiedBy(userId);

    Rnr savedRequisition = requisitionService.getFullRequisitionById(requisition.getId());

    if (!savedRequisition.getFacility().getVirtualFacility())
      throw new DataException("error.approval.not.allowed");

    if (savedRequisition.getFullSupplyLineItems().size() != report.getProducts().size()) {
      throw new DataException("error.number.of.line.items.mismatch");
    }

    requisitionService.save(requisition);
    requisitionService.approve(requisition);
  }

  public ReplenishmentDTO getReplenishmentDetails(Long id) {
    Rnr requisition = requisitionService.getFullRequisitionById(id);
    ReplenishmentDTO replenishmentDTO = prepareForREST(requisition, orderService.getOrder(id));
    return replenishmentDTO;
  }
}
