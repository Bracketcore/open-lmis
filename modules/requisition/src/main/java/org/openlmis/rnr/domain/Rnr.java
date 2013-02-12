package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
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
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;

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

  private Facility supplyingFacility;
  private Integer supervisoryNodeId;
  private Integer modifiedBy;
  private Date modifiedDate;
  private Date submittedDate;
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
      lineItem.calculate(period, status);
      Money costPerItem = lineItem.getPrice().multiply(BigDecimal.valueOf(lineItem.getPacksToShip()));
      totalFullSupplyCost = totalFullSupplyCost.add(costPerItem);
    }
    this.fullSupplyItemsSubmittedCost = totalFullSupplyCost;
    Money totalNonFullSupplyCost = new Money("0");
    for (RnrLineItem lineItem : nonFullSupplyLineItems) {
      lineItem.calculate(period, status);
      Money costPerItem = lineItem.getPrice().multiply(BigDecimal.valueOf(lineItem.getPacksToShip()));
      totalNonFullSupplyCost = totalNonFullSupplyCost.add(costPerItem);
    }
    this.nonFullSupplyItemsSubmittedCost = totalNonFullSupplyCost;
  }

  public void fillLineItems(List<FacilityApprovedProduct> facilityApprovedProducts) {
    for (FacilityApprovedProduct programProduct : facilityApprovedProducts) {
      RnrLineItem requisitionLineItem = new RnrLineItem(null, programProduct, modifiedBy);
      add(requisitionLineItem, true);
    }
  }

  public void setBeginningBalanceForEachLineItem(Rnr previousRequisition, boolean beginningBalanceVisible) {
    if (previousRequisition == null) {
      if (!beginningBalanceVisible) resetBeginningBalances();
      return;
    }
    for (RnrLineItem currentLineItem : this.lineItems) {
      RnrLineItem previousLineItem = previousRequisition.findCorrespondingLineItem(currentLineItem);
      currentLineItem.setBeginningBalanceWhenPreviousStockInHandAvailable(previousLineItem);
    }
  }

  private void resetBeginningBalances() {
    for (RnrLineItem lineItem : lineItems) {
      lineItem.resetBeginningBalance();
    }
  }

  public void fillLastTwoPeriodsNormalizedConsumptions(Rnr lastPeriodsRnr, Rnr secondLastPeriodsRnr) {
    addPreviousNormalizedConsumptionFrom(lastPeriodsRnr);
    addPreviousNormalizedConsumptionFrom(secondLastPeriodsRnr);
  }

  public void prepareForApproval() {
    status = IN_APPROVAL;
    for (RnrLineItem item : lineItems) {
      item.setDefaultApprovedQuantity();
    }
    for (RnrLineItem item : nonFullSupplyLineItems) {
      item.setDefaultApprovedQuantity();
    }
  }

  public void copyApproverEditableFields(Rnr rnr) {
    this.modifiedBy = rnr.getModifiedBy();
    for (RnrLineItem thisLineItem : this.lineItems) {
      RnrLineItem otherLineItem = rnr.findCorrespondingLineItem(thisLineItem);
      thisLineItem.copyApproverEditableFields(otherLineItem);
      thisLineItem.setModifiedBy(rnr.getModifiedBy());
    }
    for (RnrLineItem thisLineItem : this.nonFullSupplyLineItems) {
      RnrLineItem otherLineItem = rnr.findCorrespondingLineItem(thisLineItem);
      thisLineItem.copyApproverEditableFields(otherLineItem);
      thisLineItem.setModifiedBy(rnr.getModifiedBy());
    }
  }

  public void fillBasicInformation(Facility facility, Program program, ProcessingPeriod period) {
    this.program = program.basicInformation();
    this.period = period.basicInformation();
    this.facility = facility.basicInformation();
  }

  public void fillBasicInformationForSupplyingFacility(Facility facility) {
    this.supplyingFacility = facility.basicInformation();
  }

  private void addPreviousNormalizedConsumptionFrom(Rnr rnr) {
    if (rnr == null) return;
    for (RnrLineItem currentLineItem : lineItems) {
      RnrLineItem previousLineItem = rnr.findCorrespondingLineItem(currentLineItem);
      currentLineItem.addPreviousNormalizedConsumptionFrom(previousLineItem);
    }
  }

  private RnrLineItem findCorrespondingLineItem(final RnrLineItem item) {
    return (RnrLineItem) find(this.lineItems, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        RnrLineItem lineItem = (RnrLineItem) o;
        return lineItem.getProductCode().equalsIgnoreCase(item.getProductCode());
      }
    });
  }

  public void copyUserEditableFieldsForSaveSubmitOrAuthorize(Rnr otherRequisition, List<RnrColumn> programRnrColumns) {
    this.modifiedBy = otherRequisition.modifiedBy;
    for (RnrLineItem thisLineItem : lineItems) {
      RnrLineItem otherLineItem = otherRequisition.findCorrespondingLineItem(thisLineItem);
      thisLineItem.copyUserEditableFieldsForSaveSubmitOrAuthorize(otherLineItem, programRnrColumns);
      thisLineItem.setModifiedBy(otherRequisition.getModifiedBy());
    }
    this.nonFullSupplyLineItems = otherRequisition.nonFullSupplyLineItems;
    for (RnrLineItem thisLineItem : this.nonFullSupplyLineItems) {
      thisLineItem.setModifiedBy(otherRequisition.getModifiedBy());
    }
  }

  public void prepareFor(RnrStatus status) {
    calculate();
    this.status = status;
    if(status.equals(SUBMITTED)) submittedDate = new Date();
  }

  public void setFieldsAccordingToTemplate(ProgramRnrTemplate template) {
    for(RnrLineItem lineItem : lineItems){
      lineItem.setLineItemFieldsAccordingToTemplate(template);
    }
  }

}

