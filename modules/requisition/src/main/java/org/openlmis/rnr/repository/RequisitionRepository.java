/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.repository.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.openlmis.rnr.domain.RnrStatus.INITIATED;

@Repository
public class RequisitionRepository {

  @Autowired
  private RequisitionMapper requisitionMapper;
  @Autowired
  private RnrLineItemMapper rnrLineItemMapper;
  @Autowired
  private LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;
  @Autowired
  private CommentMapper commentMapper;
  @Autowired
  private CommaSeparator commaSeparator;
  @Autowired
  private RequisitionStatusChangeMapper requisitionStatusChangeMapper;


  public void insert(Rnr requisition) {
    requisition.setStatus(INITIATED);
    requisitionMapper.insert(requisition);
    insertLineItems(requisition, requisition.getFullSupplyLineItems());
    insertLineItems(requisition, requisition.getNonFullSupplyLineItems());
  }

  private void insertLineItems(Rnr requisition, List<RnrLineItem> lineItems) {
    for (RnrLineItem lineItem : lineItems) {
      lineItem.setRnrId(requisition.getId());
      lineItem.setModifiedBy(requisition.getModifiedBy());
      rnrLineItemMapper.insert(lineItem);
    }
  }

  public void update(Rnr rnr) {
    requisitionMapper.update(rnr);
    updateFullSupplyLineItems(rnr);
    updateNonFullSupplyLineItems(rnr);
  }

  private void updateNonFullSupplyLineItems(Rnr rnr) {
    rnrLineItemMapper.deleteAllNonFullSupplyForRequisition(rnr.getId());
    for (RnrLineItem lineItem : rnr.getNonFullSupplyLineItems()) {
      rnrLineItemMapper.insertNonFullSupply(lineItem);
    }
  }


  private void updateFullSupplyLineItems(Rnr requisition) {
    for (RnrLineItem lineItem : requisition.getFullSupplyLineItems()) {
      rnrLineItemMapper.update(lineItem);
      lossesAndAdjustmentsMapper.deleteByLineItemId(lineItem.getId());
      insertLossesAndAdjustmentsForLineItem(lineItem);
    }
  }

  private void insertLossesAndAdjustmentsForLineItem(RnrLineItem lineItem) {
    for (LossesAndAdjustments lossAndAdjustment : lineItem.getLossesAndAdjustments()) {
      lossesAndAdjustmentsMapper.insert(lineItem, lossAndAdjustment);
    }
  }

  public Rnr getRequisitionWithLineItems(Facility facility, Program program, ProcessingPeriod period) {
    return requisitionMapper.getRequisitionWithLineItems(facility, program, period);
  }


  public List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes() {
    return lossesAndAdjustmentsMapper.getLossesAndAdjustmentsTypes();
  }

  public Rnr getById(Long rnrId) {
    Rnr requisition = requisitionMapper.getById(rnrId);
    if (requisition == null) throw new DataException("Requisition Not Found");
    return requisition;
  }

  public List<Rnr> getAuthorizedRequisitions(RoleAssignment roleAssignment) {
    return requisitionMapper.getAuthorizedRequisitions(roleAssignment);
  }

  public Rnr getLastRequisitionToEnterThePostSubmitFlow(Long facilityId, Long programId) {
    return requisitionMapper.getLastRequisitionToEnterThePostSubmitFlow(facilityId, programId);
  }

  public List<Rnr> getApprovedRequisitions() {
    return requisitionMapper.getApprovedRequisitions();
  }

  public List<Rnr> get(Facility facility, Program program, List<ProcessingPeriod> periods) {
    return requisitionMapper.get(facility, program, commaSeparator.commaSeparateIds(periods));
  }

  public Integer getCategoryCount(Rnr requisition, boolean fullSupply) {
    return rnrLineItemMapper.getCategoryCount(requisition, fullSupply);
  }

  public List<Comment> getCommentsByRnrID(Long rnrId) {
    return commentMapper.getByRnrId(rnrId);
  }

  public void insertComment(Comment comment) {
    commentMapper.insert(comment);
  }

  public void logStatusChange(Rnr requisition) {
    RequisitionStatusChange statusChange = new RequisitionStatusChange(requisition);
    requisitionStatusChangeMapper.insert(statusChange);
  }
}

