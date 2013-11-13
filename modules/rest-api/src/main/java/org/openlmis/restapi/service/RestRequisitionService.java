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
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.order.service.OrderService;
import org.openlmis.restapi.domain.ReplenishmentDTO;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static java.util.Arrays.asList;
import static org.openlmis.restapi.domain.ReplenishmentDTO.prepareForREST;

@Service
@NoArgsConstructor
public class RestRequisitionService {

  @Autowired
  private UserService userService;

  @Autowired
  private RequisitionService requisitionService;

  @Autowired
  private OrderService orderService;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProgramService programService;

  @Autowired
  private ProcessingScheduleService processingScheduleService;

  @Transactional
  public Rnr submitReport(Report report, Long userId) {
    report.validate();

    Facility reportingFacility = facilityService.getVirtualFacilityByCode(report.getAgentCode());
    Program reportingProgram = programService.getValidatedProgramByCode(report.getProgramCode());
    ProcessingPeriod reportingPeriod = getValidatedProcessingPeriod(reportingFacility, reportingProgram);

    return requisitionService.initiate(reportingFacility.getId(), reportingProgram.getId(), reportingPeriod.getId(), userId, false);
  }

  private ProcessingPeriod getValidatedProcessingPeriod(Facility reportingFacility, Program reportingProgram) {
    Date programStartDate = programService.getProgramStartDate(reportingFacility.getId(), reportingProgram.getId());
    ProcessingPeriod currentPeriod = processingScheduleService.getCurrentPeriod(reportingFacility.getId(), reportingProgram.getId(), programStartDate);
    if (currentPeriod == null) {
      throw new DataException("error.permission.denied");
    }
    return currentPeriod;
  }

  @Transactional
  public Rnr approve(Report report) {
    User user = getValidatedUser(report);
    Rnr requisition = report.getRequisition();
    requisition.setModifiedBy(user.getId());

    Long facilityId = requisitionService.getFacilityId(requisition.getId());
    if(facilityId == null){
      throw new DataException("error.invalid.requisition.id");
    }
    Facility facility = facilityService.getById(facilityId);
    if (!facility.getVirtualFacility())
      throw new DataException("error.approval.not.allowed");

    requisitionService.save(requisition);
    requisitionService.approve(requisition);
    return requisition;
  }

  private User getValidatedUser(Report report) {
    User user = userService.getByUserName(report.getUserName());
    if (user == null) {
      throw new DataException("user.username.incorrect");
    }
    return user;
  }

  public ReplenishmentDTO getReplenishmentDetails(Long id) {
    Rnr requisition = requisitionService.getFullRequisitionById(id);
    ReplenishmentDTO replenishmentDTO = prepareForREST(requisition, orderService.getOrder(id));
    return replenishmentDTO;
  }
}
