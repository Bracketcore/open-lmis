package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;
import static org.openlmis.rnr.domain.RnrStatus.IN_APPROVAL;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_NULL)
public class Rnr {

  private Integer id;
  private Facility facility;
  private Program program;
  private ProcessingPeriod period;
  private RnrStatus status;
  private Money fullSupplyItemsSubmittedCost = new Money("0");
  private Money nonFullSupplyItemsSubmittedCost = new Money("0");

  private List<RnrLineItem> lineItems = new ArrayList<>();
  private List<RnrLineItem> nonFullSupplyLineItems = new ArrayList<>();

  private Integer modifiedBy;
  private Date modifiedDate;
  private Date submittedDate;
  private Integer supervisoryNodeId;
  public static final String RNR_VALIDATION_ERROR = "rnr.validation.error";

  public Rnr(Integer facilityId, Integer programId, Integer periodId, Integer modifiedBy) {
    facility = new Facility();
    facility.setId(facilityId);
    program = new Program();
    program.setId(programId);
    period = new ProcessingPeriod();
    period.setId(periodId);
    this.modifiedBy = modifiedBy;
  }

  public Rnr(Integer facilityId, Integer programId, Integer periodId, List<FacilityApprovedProduct> facilityApprovedProducts, Integer modifiedBy) {
    this(facilityId, programId, periodId, modifiedBy);
    fillLineItems(facilityApprovedProducts);
  }

  public Rnr(Facility facility, Program program, ProcessingPeriod period) {
    this.facility = facility;
    this.program = program;
    this.period = period;
  }

  public void add(RnrLineItem rnrLineItem, Boolean fullSupply) {
    if (fullSupply) {
      lineItems.add(rnrLineItem);
    } else {
      nonFullSupplyLineItems.add(rnrLineItem);
    }
  }

  public boolean validate(List<RnrColumn> templateColumns) {
    for (RnrLineItem lineItem : lineItems) {
      lineItem.validate(templateColumns);
    }
    return true;
  }

  public void calculate() {
    Money totalFullSupplyCost = new Money("0");
    for (RnrLineItem lineItem : lineItems) {
        lineItem.calculate(status);
      Money costPerItem = lineItem.getPrice().multiply(BigDecimal.valueOf(lineItem.getPacksToShip()));
      totalFullSupplyCost = totalFullSupplyCost.add(costPerItem);
    }
    this.fullSupplyItemsSubmittedCost = totalFullSupplyCost;
  }

  public void fillLineItems(List<FacilityApprovedProduct> facilityApprovedProducts) {
    for (FacilityApprovedProduct programProduct : facilityApprovedProducts) {
      RnrLineItem requisitionLineItem = new RnrLineItem(null, programProduct, modifiedBy);
      add(requisitionLineItem, true);
    }
  }

  public void setBeginningBalanceForEachLineItem(Rnr previousRequisition) {
    if (previousRequisition == null) return;
    for (RnrLineItem currentLineItem : this.lineItems) {
      RnrLineItem previousLineItem = findCorrespondingLineItem(previousRequisition.getLineItems(), currentLineItem);
      if (previousLineItem != null)
        currentLineItem.setBeginningBalanceWhenPreviousStockInHandAvailable(previousLineItem.getStockInHand());
    }
  }

  public void fillLastTwoPeriodsNormalizedConsumptions(Rnr lastPeriodsRnr, Rnr secondLastPeriodsRnr) {
    addNormalizedConsumptionFrom(lastPeriodsRnr);
    addNormalizedConsumptionFrom(secondLastPeriodsRnr);
  }

  private void addNormalizedConsumptionFrom(Rnr rnr) {
    if (rnr == null) return;

    for (RnrLineItem currentLineItem : lineItems) {
      RnrLineItem previousLineItem = findCorrespondingLineItem(rnr.getLineItems(), currentLineItem);
      currentLineItem.addPreviousNormalizedConsumption(previousLineItem);
    }
  }

  private RnrLineItem findCorrespondingLineItem(List<RnrLineItem> items, final RnrLineItem item) {
    return (RnrLineItem) find(items, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        RnrLineItem lineItem = (RnrLineItem) o;
        return lineItem.getProductCode().equalsIgnoreCase(item.getProductCode());
      }
    });
  }

  public void prepareForApproval() {
    status = IN_APPROVAL;
    for (RnrLineItem item : lineItems) {
      item.setDefaultApprovedQuantity();
    }
  }

  public void copyApproverEditableFields(Rnr rnr) {
    for (RnrLineItem thisLineItem : this.lineItems) {
      RnrLineItem otherLineItem = findCorrespondingLineItem(rnr.lineItems, thisLineItem);
      thisLineItem.copyApproverEditableFields(otherLineItem);
    }
  }

  public void resetBeginningBalancesFromRequisition(Rnr savedRequisition) {
    for (RnrLineItem lineItem : getLineItems()) {
      RnrLineItem savedLineItem = getPreviouslyStoredLineItemForProduct(savedRequisition.getLineItems(), lineItem);

      if (savedLineItem.getPreviousStockInHandAvailable())
        lineItem.setBeginningBalance(savedLineItem.getStockInHand());
    }
  }

  private RnrLineItem getPreviouslyStoredLineItemForProduct(List<RnrLineItem> items, final RnrLineItem lineItem) {
    return (RnrLineItem) find(items, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        RnrLineItem savedLineItem = (RnrLineItem) o;
        return savedLineItem.getProductCode().equalsIgnoreCase(lineItem.getProductCode());
      }
    });
  }
}

