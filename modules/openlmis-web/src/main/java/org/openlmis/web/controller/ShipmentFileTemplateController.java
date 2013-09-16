/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class ShipmentFileTemplateController extends BaseController {

  @Autowired
  ShipmentFileTemplateService service;

  @RequestMapping(value = "/shipment-file-template", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_EDI')")
  public ResponseEntity<OpenLmisResponse> get() {
    return response("shipment_template", service.get());
  }

  @RequestMapping(value = "/shipment-file-template", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_EDI')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody ShipmentFileTemplate shipmentFileTemplate,
                                                 HttpServletRequest request) {
    shipmentFileTemplate.validateAndSetModifiedBy(loggedInUserId(request));
    service.update(shipmentFileTemplate);

    return success("shipment.file.configuration.success");
  }
}
