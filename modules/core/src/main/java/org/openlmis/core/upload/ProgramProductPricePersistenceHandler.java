/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.service.ProgramProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("programProductPricePersistenceHandler")
public class ProgramProductPricePersistenceHandler extends AbstractModelPersistenceHandler {

  private ProgramProductService programProductService;
  @Autowired
  public ProgramProductPricePersistenceHandler(ProgramProductService service) {
    this.programProductService = service;
  }


  @Override
  protected BaseModel getExisting(BaseModel record) {
    return null;
  }

  @Override
  protected void save(BaseModel record) {
    programProductService.updateProgramProductPrice((ProgramProductPrice) record);
  }

  @Override
  protected String getDuplicateMessageKey() {
    return null;
  }

}
