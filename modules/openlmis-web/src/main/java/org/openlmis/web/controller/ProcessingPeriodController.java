/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
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
import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.success;

@Controller
@NoArgsConstructor
public class ProcessingPeriodController extends BaseController {

  public static final String PERIODS = "periods";

  @Autowired
  private ProcessingScheduleService processingScheduleService;

  @RequestMapping(value = "/schedules/{scheduleId}/periods", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> getAll(@PathVariable("scheduleId") Long scheduleId) {
    List<ProcessingPeriod> periodList = processingScheduleService.getAllPeriods(scheduleId);
    return OpenLmisResponse.response(PERIODS, periodList);
  }

  @RequestMapping(value = "/schedules/{scheduleId}/periods", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> save(@PathVariable("scheduleId") Long scheduleId, @RequestBody ProcessingPeriod processingPeriod, HttpServletRequest request) {
    processingPeriod.setScheduleId(scheduleId);
    processingPeriod.setModifiedBy(loggedInUserId(request));
    try {
      processingScheduleService.savePeriod(processingPeriod);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    ResponseEntity<OpenLmisResponse> successResponse = success(messageService.message("message.period.added.success"));
    successResponse.getBody().addData("id", processingPeriod.getId());
    return successResponse;
  }

  @RequestMapping(value = "/periods/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SCHEDULE')")
  public ResponseEntity<OpenLmisResponse> delete(@PathVariable("id") Long id) {
    try {
      processingScheduleService.deletePeriod(id);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    return success(messageService.message("message.period.deleted.success"));
  }
}
