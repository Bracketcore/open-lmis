/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;
import static org.openlmis.rnr.domain.RnrStatus.*;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_NULL)
@EqualsAndHashCode(callSuper = false)
public class Rnr extends BaseModel {

  private boolean emergency;
  private Facility facility;
  private Program program;
  private ProcessingPeriod period;
  private RnrStatus status;
  private Money fullSupplyItemsSubmittedCost = new Money("0");
  private Money nonFullSupplyItemsSubmittedCost = new Money("0");

  private List<RnrLineItem> fullSupplyLineItems = new ArrayList<>();
  private List<RnrLineItem> nonFullSupplyLineItems = new ArrayList<>();
  private List<RegimenLineItem> regimenLineItems = new ArrayList<>();

  @Transient
  @JsonIgnore
  private List<RnrLineItem> allLineItems = new ArrayList<>();

  private Facility supplyingDepot;
  private Long supervisoryNodeId;
  private Date submittedDate;
  private List<Comment> comments = new ArrayList<>();

  @JsonIgnore
  private RnrCalcStrategy calcStrategy;

  public Rnr(Long facilityId, Long programId, Long periodId, Boolean emergency, Long modifiedBy, Long createdBy) {
    facility = new Facility();
    facility.setId(facilityId);
    program = new Program();
    program.setId(programId);
    period = new ProcessingPeriod();
    period.setId(periodId);
    this.emergency = emergency;
    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
  }

  public Rnr(Long facilityId, Long programId, Long periodId, Boolean emergency, List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts,
             List<Regimen> regimens, Long modifiedBy, Long createdBy) {
    this(facilityId, programId, periodId, emergency, modifiedBy, createdBy);
    fillLineItems(facilityTypeApprovedProducts);
    fillActiveRegimenLineItems(regimens);
  }

  private void fillActiveRegimenLineItems(List<Regimen> regimens) {
    for (Regimen regimen : regimens) {
      if (regimen.getActive()) {
        RegimenLineItem regimenLineItem = new RegimenLineItem(regimen.getId(), regimen.getCategory(), createdBy, modifiedBy);
        regimenLineItem.setCode(regimen.getCode());
        regimenLineItem.setName(regimen.getName());
        regimenLineItems.add(regimenLineItem);
      }
    }
  }

  public Rnr(Facility facility, Program program, ProcessingPeriod period) {
    this.facility = facility;
    this.program = program;
    this.period = period;
  }

  public Rnr(Long id) {
    this.id = id;
  }

  public void add(RnrLineItem rnrLineItem, Boolean fullSupply) {
    if (fullSupply) {
      fullSupplyLineItems.add(rnrLineItem);
    } else {
      nonFullSupplyLineItems.add(rnrLineItem);
    }
  }

  public void calculateForApproval() {
    for (RnrLineItem lineItem : fullSupplyLineItems) {
      lineItem.calculatePacksToShip(calcStrategy);
    }
    for (RnrLineItem lineItem : nonFullSupplyLineItems) {
      lineItem.calculatePacksToShip(calcStrategy);
    }
    this.fullSupplyItemsSubmittedCost = calculateCost(fullSupplyLineItems);
    this.nonFullSupplyItemsSubmittedCost = calculateCost(nonFullSupplyLineItems);
  }

  public void calculate(ProgramRnrTemplate template, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    this.fullSupplyItemsSubmittedCost = this.nonFullSupplyItemsSubmittedCost = new Money("0");
    calculateForFullSupply(template, lossesAndAdjustmentsTypes);
    calculateForNonFullSupply();
  }

  private void calculateForNonFullSupply() {
    for (RnrLineItem lineItem : nonFullSupplyLineItems) {
      lineItem.validateNonFullSupply();
      lineItem.calculatePacksToShip(calcStrategy);
      this.nonFullSupplyItemsSubmittedCost = this.nonFullSupplyItemsSubmittedCost.add(lineItem.calculateCost());
    }
  }

  private void calculateForFullSupply(ProgramRnrTemplate template, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    for (RnrLineItem lineItem : fullSupplyLineItems) {
      lineItem.validateMandatoryFields(template);
      lineItem.calculateForFullSupply(calcStrategy, period, template, this.getStatus(), lossesAndAdjustmentsTypes);
      lineItem.validateCalculatedFields(template);
      this.fullSupplyItemsSubmittedCost = this.fullSupplyItemsSubmittedCost.add(lineItem.calculateCost());
    }
  }

  private Money calculateCost(List<RnrLineItem> lineItems) {
    Money totalFullSupplyCost = new Money("0");
    for (RnrLineItem lineItem : lineItems) {
      Money costPerItem = lineItem.calculateCost();
      totalFullSupplyCost = totalFullSupplyCost.add(costPerItem);
    }
    return totalFullSupplyCost;
  }

  public void fillLineItems(List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts) {
    for (FacilityTypeApprovedProduct facilityTypeApprovedProduct : facilityTypeApprovedProducts) {
      RnrLineItem requisitionLineItem = new RnrLineItem(null, facilityTypeApprovedProduct, modifiedBy, createdBy);
      add(requisitionLineItem, true);
    }
  }

  public void setBeginningBalances(Rnr previousRequisition, boolean beginningBalanceVisible) {
    if (previousRequisition == null || previousRequisition.status == INITIATED || previousRequisition.status == SUBMITTED) {
      if (!beginningBalanceVisible) resetBeginningBalances();
      return;
    }
    for (RnrLineItem currentLineItem : this.fullSupplyLineItems) {
      RnrLineItem previousLineItem = previousRequisition.findCorrespondingLineItem(currentLineItem);
      currentLineItem.setBeginningBalanceWhenPreviousStockInHandAvailable(previousLineItem);
    }
  }

  private void resetBeginningBalances() {
    for (RnrLineItem lineItem : fullSupplyLineItems) {
      lineItem.setBeginningBalance(0);
    }
  }

  public void fillLastTwoPeriodsNormalizedConsumptions(Rnr lastPeriodsRnr, Rnr secondLastPeriodsRnr) {
    addPreviousNormalizedConsumptionFrom(lastPeriodsRnr);
    addPreviousNormalizedConsumptionFrom(secondLastPeriodsRnr);
  }

  public void setDefaultApprovedQuantity() {
    for (RnrLineItem item : fullSupplyLineItems) {
      item.setDefaultApprovedQuantity();
    }
    for (RnrLineItem item : nonFullSupplyLineItems) {
      item.setDefaultApprovedQuantity();
    }
  }

  private List<RnrLineItem> getAllLineItems() {
    if (this.allLineItems.isEmpty()) {
      this.allLineItems.addAll(this.getFullSupplyLineItems());
      this.allLineItems.addAll(this.getNonFullSupplyLineItems());
    }
    return allLineItems;
  }

  public void fillBasicInformation(Facility facility, Program program, ProcessingPeriod period) {
    this.program = program.basicInformation();
    this.period = period.basicInformation();
    this.facility = facility.basicInformation();
  }

  private void addPreviousNormalizedConsumptionFrom(Rnr rnr) {
    if (rnr == null) return;
    for (RnrLineItem currentLineItem : fullSupplyLineItems) {
      RnrLineItem previousLineItem = rnr.findCorrespondingLineItem(currentLineItem);
      currentLineItem.addPreviousNormalizedConsumptionFrom(previousLineItem);
    }
  }

  private RnrLineItem findCorrespondingLineItem(final RnrLineItem item) {
    return (RnrLineItem) find(this.getAllLineItems(), new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        RnrLineItem lineItem = (RnrLineItem) o;
        return lineItem.getProductCode().equalsIgnoreCase(item.getProductCode());
      }
    });
  }

  private RegimenLineItem findCorrespondingRegimenLineItem(final RegimenLineItem regimenLineItem) {
    return (RegimenLineItem) find(this.regimenLineItems, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        RegimenLineItem regimenLineItem1 = (RegimenLineItem) o;
        return regimenLineItem1.getCode().equalsIgnoreCase(regimenLineItem.getCode());
      }
    });
  }

  public void setFieldsAccordingToTemplate(ProgramRnrTemplate template, RegimenTemplate regimenTemplate) {
    for (RnrLineItem lineItem : this.fullSupplyLineItems) {
      lineItem.setLineItemFieldsAccordingToTemplate(template);
    }
    if (regimenTemplate.getColumns().isEmpty()) return;
    for (RegimenLineItem regimenLineItem : this.regimenLineItems) {
      regimenLineItem.setRegimenFieldsAccordingToTemplate(regimenTemplate);
    }
  }

  public void convertToOrder(Long userId) {
    this.status = RELEASED;
    this.modifiedBy = userId;
  }

  public void fillFullSupplyCost() {
    this.fullSupplyItemsSubmittedCost = calculateCost(this.fullSupplyLineItems);
  }

  public void fillNonFullSupplyCost() {
    this.nonFullSupplyItemsSubmittedCost = calculateCost(this.nonFullSupplyLineItems);
  }

  public void validateForApproval() {
    validateLineItemsForApproval(fullSupplyLineItems);
    validateLineItemsForApproval(nonFullSupplyLineItems);
  }

  private void validateLineItemsForApproval(List<RnrLineItem> lineItems) {
    for (RnrLineItem lineItem : lineItems) {
      lineItem.validateForApproval();
    }
  }

  public void copyCreatorEditableFields(Rnr rnr, ProgramRnrTemplate rnrTemplate, RegimenTemplate regimenTemplate, List<ProgramProduct> programProducts) {
    this.modifiedBy = rnr.getModifiedBy();
    copyCreatorEditableFieldsForFullSupply(rnr, rnrTemplate);
    copyCreatorEditableFieldsForNonFullSupply(rnr, rnrTemplate, programProducts);
    copyCreatorEditableFieldsForRegimen(rnr, regimenTemplate);
  }

  private void copyCreatorEditableFieldsForRegimen(Rnr rnr, RegimenTemplate regimenTemplate) {
    for (RegimenLineItem regimenLineItem : rnr.regimenLineItems) {
      RegimenLineItem savedRegimenLineItem = this.findCorrespondingRegimenLineItem(regimenLineItem);
      if (savedRegimenLineItem != null)
        savedRegimenLineItem.copyCreatorEditableFieldsForRegimen(regimenLineItem, regimenTemplate);
      savedRegimenLineItem.setModifiedBy(rnr.getModifiedBy());
    }
  }

  private void copyCreatorEditableFieldsForNonFullSupply(Rnr rnr, ProgramRnrTemplate template, List<ProgramProduct> programProducts) {
    for (final RnrLineItem lineItem : rnr.nonFullSupplyLineItems) {
      RnrLineItem savedLineItem = this.findCorrespondingLineItem(lineItem);
      if (savedLineItem == null) {
        ProgramProduct programProduct = (ProgramProduct) find(programProducts, new Predicate() {
          @Override
          public boolean evaluate(Object o) {
            ProgramProduct programProduct = (ProgramProduct) o;
            return programProduct.getProduct().getCode().equalsIgnoreCase(lineItem.getProductCode());
          }
        });
        if (programProduct != null) {
          lineItem.setModifiedBy(rnr.getModifiedBy());
          this.nonFullSupplyLineItems.add(lineItem);
        }
      } else {
          savedLineItem.setModifiedBy(rnr.getModifiedBy());
          savedLineItem.copyCreatorEditableFieldsForNonFullSupply(lineItem, template);
      }
    }
  }

  private void copyCreatorEditableFieldsForFullSupply(Rnr rnr, ProgramRnrTemplate template) {
    for (RnrLineItem lineItem : rnr.fullSupplyLineItems) {
      RnrLineItem savedLineItem = this.findCorrespondingLineItem(lineItem);
      if (savedLineItem == null)
        throw new DataException("product.code.invalid");
      savedLineItem.copyCreatorEditableFieldsForFullSupply(lineItem, template);
      savedLineItem.setModifiedBy(rnr.getModifiedBy());
    }
  }

  public void copyApproverEditableFields(Rnr rnr, ProgramRnrTemplate template) {
    this.modifiedBy = rnr.modifiedBy;
    copyApproverEditableFieldsToLineItems(rnr, template, rnr.fullSupplyLineItems);
    copyApproverEditableFieldsToLineItems(rnr, template, rnr.nonFullSupplyLineItems);
  }

  private void copyApproverEditableFieldsToLineItems(Rnr rnr, ProgramRnrTemplate template, List<RnrLineItem> lineItems) {
    for (RnrLineItem lineItem : lineItems) {
      RnrLineItem savedLineItem = this.findCorrespondingLineItem(lineItem);
      if (savedLineItem == null)
        throw new DataException("product.code.invalid");
      savedLineItem.setModifiedBy(rnr.modifiedBy);
      savedLineItem.copyApproverEditableFields(lineItem, template);
    }
  }

  public void setAuditFieldsForRequisition(Long modifiedBy, RnrStatus status) {
    this.status = status;
    this.modifiedBy = modifiedBy;
  }

  public void prepareForFinalApproval() {
    this.status = APPROVED;
  }

  public void approveAndAssignToNextSupervisoryNode(SupervisoryNode parent) {
    status = IN_APPROVAL;
    supervisoryNodeId = parent.getId();
  }

  public boolean isApprovable() {
    return status == AUTHORIZED || status == IN_APPROVAL;
  }
}

