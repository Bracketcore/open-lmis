/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProgramProductController extends BaseController {

  @Autowired
  private ProgramProductService programProductService;

  public static final String PROGRAM_PRODUCT_LIST = "PROGRAM_PRODUCT_LIST";

  public ResponseEntity<OpenLmisResponse> getProgramProductsByProgram(@PathVariable Long programId) {
    List<ProgramProduct> programProductsByProgram = programProductService.getProgramProductsWithISAByProgram(programId);
    return OpenLmisResponse.response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
  }

  public void saveProgramProductISA(ProgramProductISA programProductISA) {
    programProductService.saveProgramProductISA(programProductISA);
  }
}

