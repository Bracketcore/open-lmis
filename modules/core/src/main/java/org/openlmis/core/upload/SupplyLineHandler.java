/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.repository.SupplyLineRepository;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@NoArgsConstructor
public class SupplyLineHandler extends AbstractModelPersistenceHandler {

  SupplyLineRepository supplyLineRepository;

  @Autowired
  public SupplyLineHandler(SupplyLineRepository supplyLineRepository) {
    super(null);
    this.supplyLineRepository = supplyLineRepository;
  }

  @Override
  protected Importable getExisting(Importable importable) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  protected void save(Importable existingRecord, Importable currentRecord, AuditFields auditFields) {
    SupplyLine supplyLine = (SupplyLine) currentRecord;
    supplyLine.setModifiedBy(auditFields.getUser());
    supplyLine.setModifiedDate(new Date());
    supplyLineRepository.insert(supplyLine);
  }

}