/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright Â© 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Â 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.Â  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.Â  If not, see http://www.gnu.org/licenses. Â For additional information contact info@OpenLMIS.org.Â 
 */

package org.openlmis.restapi;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequisitionValidator {

  @Autowired
  private RequisitionService requisitionService;

  @Autowired
  private MessageService messageService;

  public void validatePeriod(Facility reportingFacility, Program reportingProgram) {

    if (!reportingFacility.getVirtualFacility()) {

      RequisitionSearchCriteria searchCriteria = new RequisitionSearchCriteria();
      searchCriteria.setProgramId(reportingProgram.getId());
      searchCriteria.setFacilityId(reportingFacility.getId());

      if (!requisitionService.getCurrentPeriod(searchCriteria).getId().equals
          (requisitionService.getPeriodForInitiating(reportingFacility, reportingProgram).getId())) {
        throw new DataException("error.rnr.previous.not.filled");
      }
    }
  }

  public void validateProducts(Report report, Rnr savedRequisition) {
    if (report.getProducts() == null) {
      return;
    }

    List<String> invalidProductCodes = new ArrayList<>();
    for (final RnrLineItem product : report.getProducts()) {
      if (savedRequisition.findCorrespondingLineItem(product) == null) {
        invalidProductCodes.add(product.getProductCode());
      }
    }
    if (invalidProductCodes.size() != 0) {
      throw new DataException(messageService.message("invalid.product.codes", invalidProductCodes.toString()));
    }
  }

}
