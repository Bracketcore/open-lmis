/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.success;

@Controller
@NoArgsConstructor
public class ProcessingScheduleController extends BaseController {

  public static final String SCHEDULES = "schedules";
  public static final String SCHEDULE = "schedule";
  private ProcessingScheduleService processingScheduleService;

  @Autowired
  public ProcessingScheduleController(ProcessingScheduleService processingScheduleService) {
    this.processingScheduleService = processingScheduleService;
  }

  @RequestMapping(value = "/schedules", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    return OpenLmisResponse.response(SCHEDULES, processingScheduleService.getAll());
  }

  @RequestMapping(value = "/schedules", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> create(@RequestBody ProcessingSchedule processingSchedule, HttpServletRequest request) {
    processingSchedule.setModifiedBy(loggedInUserId(request));
    return saveSchedule(processingSchedule, true);
  }

  @RequestMapping(value = "/schedules/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody ProcessingSchedule processingSchedule, @PathVariable("id") Long id, HttpServletRequest request) {
    processingSchedule.setId(id);
    processingSchedule.setModifiedBy(loggedInUserId(request));
    return saveSchedule(processingSchedule, false);
  }

  @RequestMapping(value = "/schedules/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable("id") Long id) {
    try {
      ProcessingSchedule processingSchedule = processingScheduleService.get(id);
      return OpenLmisResponse.response(SCHEDULE, processingSchedule);
    } catch (DataException e) {
      return error(e, HttpStatus.NOT_FOUND);
    }
  }

  private ResponseEntity<OpenLmisResponse> saveSchedule(ProcessingSchedule processingSchedule, boolean createOperation) {
    try {
      ProcessingSchedule savedSchedule = processingScheduleService.save(processingSchedule);
      ResponseEntity<OpenLmisResponse> response;
      if (createOperation) {
        response = success(messageService.message("message.schedule.created.success", savedSchedule.getName()));
      } else {
        response = success(messageService.message("message.schedule.updated.success", savedSchedule.getName()));
      }
      response.getBody().addData(SCHEDULE, savedSchedule);
      return response;
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
  }
}