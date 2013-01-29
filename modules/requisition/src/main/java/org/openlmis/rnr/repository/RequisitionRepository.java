package org.openlmis.rnr.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.mapper.LossesAndAdjustmentsMapper;
import org.openlmis.rnr.repository.mapper.RequisitionMapper;
import org.openlmis.rnr.repository.mapper.RnrLineItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.openlmis.rnr.domain.RnrStatus.INITIATED;

@Repository
@NoArgsConstructor
public class RequisitionRepository {

  private RequisitionMapper mapper;
  private RnrLineItemMapper rnrLineItemMapper;
  private LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;


  @Autowired
  public RequisitionRepository(RequisitionMapper requisitionMapper, RnrLineItemMapper rnrLineItemMapper, LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper) {
    this.mapper = requisitionMapper;
    this.rnrLineItemMapper = rnrLineItemMapper;
    this.lossesAndAdjustmentsMapper = lossesAndAdjustmentsMapper;
  }

  public void insert(Rnr requisition) {
    requisition.setStatus(INITIATED);
    mapper.insert(requisition);
    List<RnrLineItem> lineItems = requisition.getLineItems();
    for (RnrLineItem lineItem : lineItems) {
      lineItem.setRnrId(requisition.getId());
      lineItem.setModifiedBy(requisition.getModifiedBy());
      rnrLineItemMapper.insert(lineItem);
    }
  }

  public void update(Rnr rnr) {
    mapper.update(rnr);
    List<RnrLineItem> lineItems = rnr.getLineItems();
    for (RnrLineItem lineItem : lineItems) {
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

  public Rnr getRequisition(Integer facilityId, Integer programId, Integer periodId) {
    return mapper.getRequisition(facilityId, programId, periodId);
  }

  public List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes() {
    return lossesAndAdjustmentsMapper.getLossesAndAdjustmentsTypes();
  }

  public Rnr getById(Integer rnrId) {
    Rnr requisition = mapper.getById(rnrId);
    if (requisition == null) throw new DataException("Requisition Not Found");
    return requisition;
  }

  public List<Rnr> getAuthorizedRequisitions(RoleAssignment roleAssignment) {
    return mapper.getAuthorizedRequisitions(roleAssignment);
  }

  public Rnr getLastRequisitionToEnterThePostSubmitFlow(Integer facilityId, Integer programId) {
    return mapper.getLastRequisitionToEnterThePostSubmitFlow(facilityId, programId);
  }

  public void insertNonFullSupply(RnrLineItem rnrLineItem) {
    rnrLineItem.setQuantityReceived(0);
    rnrLineItem.setQuantityDispensed(0);
    rnrLineItem.setBeginningBalance(0);
    rnrLineItem.setStockInHand(0);
    rnrLineItem.setTotalLossesAndAdjustments(0);
    rnrLineItem.setCalculatedOrderQuantity(0);
    rnrLineItem.setNewPatientCount(0);
    rnrLineItem.setStockOutDays(0);
    rnrLineItem.setNormalizedConsumption(0);
    rnrLineItem.setAmc(0);
    rnrLineItem.setMaxStockQuantity(0);
    rnrLineItemMapper.insertNonFullSupply(rnrLineItem);
  }
}

