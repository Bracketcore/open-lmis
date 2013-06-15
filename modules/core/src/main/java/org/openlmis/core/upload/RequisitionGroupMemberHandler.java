/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.service.RequisitionGroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class RequisitionGroupMemberHandler extends AbstractModelPersistenceHandler {

  private RequisitionGroupMemberService requisitionGroupMemberService;

  @Autowired
  public RequisitionGroupMemberHandler(RequisitionGroupMemberService requisitionGroupMemberService) {
    this.requisitionGroupMemberService = requisitionGroupMemberService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return requisitionGroupMemberService.getExisting((RequisitionGroupMember) record);
  }

  @Override
  protected void save(BaseModel record) {
    requisitionGroupMemberService.save((RequisitionGroupMember) record);
  }

  @Override
  protected String getDuplicateMessageKey() {
    return "error.duplicate.requisition.group.member";
  }

}
