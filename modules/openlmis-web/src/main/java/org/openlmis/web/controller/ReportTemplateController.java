/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.reporting.model.ReportTemplate;
import org.openlmis.core.exception.DataException;
import org.openlmis.reporting.service.ReportTemplateService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class ReportTemplateController extends BaseController {
  public static final String JASPER_CREATE_REPORT_SUCCESS = "create.report.success";
  public static final String JASPER_CREATE_REPORT_ERROR = "create.report.error";

  ReportTemplateService reportTemplateService;

  @Autowired
  public ReportTemplateController(ReportTemplateService reportTemplateService) {
    this.reportTemplateService = reportTemplateService;
  }

  @RequestMapping(value = "/report-templates", method = POST)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REPORTS')")
  public ResponseEntity<OpenLmisResponse> createJasperReportTemplate(HttpServletRequest request, MultipartFile file, String name) {
    try {
      ReportTemplate reportTemplate = new ReportTemplate(name, file, loggedInUserId(request));
      reportTemplateService.insert(reportTemplate);
      return success(messageService.message(JASPER_CREATE_REPORT_SUCCESS), MediaType.TEXT_HTML_VALUE);
    } catch (IOException e) {
      return error(messageService.message(JASPER_CREATE_REPORT_ERROR), OK, MediaType.TEXT_HTML_VALUE);
    } catch (DataException e) {
      return OpenLmisResponse.error(e, OK, MediaType.TEXT_HTML_VALUE);
    }
  }

  @RequestMapping(value = "/report-templates", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_REPORTS')")
  public List<ReportTemplate> getAll() {
    return reportTemplateService.getAll();
  }
}
