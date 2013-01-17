package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rnr {

  private Integer id;
  private Facility facility;
  private Program program;
  private Integer facilityId;
  private Integer programId;
  private Integer periodId;
  private RnrStatus status;
  private Float fullSupplyItemsSubmittedCost = 0f;
  private Float nonFullSupplyItemsSubmittedCost = 0f;

  private List<RnrLineItem> lineItems = new ArrayList<>();

  private Integer modifiedBy;
  private Date modifiedDate;
  private Date submittedDate;
  public static final String RNR_VALIDATION_ERROR = "rnr.validation.error";

  public Rnr(Integer facilityId, Integer programId, Integer periodId, Integer modifiedBy) {
    this.facilityId = facilityId;
    this.programId = programId;
    this.periodId = periodId;
    this.modifiedBy = modifiedBy;
  }

  public void add(RnrLineItem rnrLineItem) {
    lineItems.add(rnrLineItem);
  }

  public boolean validate(boolean formulaValidated) {
    for (RnrLineItem lineItem : lineItems) {
      lineItem.validate(formulaValidated);
    }
    return true;
  }

  public void calculate() {
    Float totalFullSupplyCost = 0f;
    for (RnrLineItem lineItem : lineItems) {
      lineItem.calculate();
      totalFullSupplyCost += lineItem.getPacksToShip() * lineItem.getPrice();
    }
    this.fullSupplyItemsSubmittedCost = totalFullSupplyCost;
  }
}

