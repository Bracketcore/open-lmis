/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("supervisoryNodeHandler")
@NoArgsConstructor
public class SupervisoryNodeHandler extends AbstractModelPersistenceHandler {

    private SupervisoryNodeService supervisoryNodeService;

    @Autowired
    public SupervisoryNodeHandler(SupervisoryNodeService supervisoryNodeService) {
        this.supervisoryNodeService = supervisoryNodeService;
    }

    @Override
    protected void save(Importable modelClass, AuditFields auditFields) {
        SupervisoryNode supervisoryNode = (SupervisoryNode) modelClass;
        supervisoryNode.setModifiedBy(auditFields.getUser());
        supervisoryNode.setModifiedDate(new Date());
        supervisoryNodeService.save(supervisoryNode);
    }
}
