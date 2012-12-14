package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;

import java.util.Date;

@Data
@NoArgsConstructor
public class RnrLineItem {

    private Integer id;
    private Integer rnrId;
    //todo hack to display it on UI. This is concatenated string of Product properties like name, strength, form and dosage unit
    private String product;
    private String productCode;
    private Integer dosesPerMonth;
    private Integer dosesPerDispensingUnit;
    private String unitOfIssue;

    private Integer quantityReceived;

  //TODO  rename to quantityConsumed
    private Integer quantityDispensed;
    private Integer beginningBalance;
    private Integer estimatedConsumption;
    private Integer stockInHand;
    private Integer quantityRequested;
    private String reasonForRequestedQuantity;
    private Integer calculatedOrderQuantity;

    private Integer quantityApproved;
    private Integer lossesAndAdjustments;
    private String reasonForLossesAndAdjustments;

    private Integer newPatientCount;
    private Integer stockOutDays;
    private Float normalizedConsumption;
    private Float amc;
    private Integer maxStockQuantity;

    private Integer packsToShip;
    private Float cost;
    private String remarks;

    private String modifiedBy;
    private Date modifiedDate;

    public RnrLineItem(Integer rnrId, ProgramProduct programProduct, String modifiedBy) {
        this.rnrId = rnrId;

        Product product = programProduct.getProduct();
        this.productCode = product.getCode();
        this.unitOfIssue = product.getDispensingUnit();
        this.dosesPerDispensingUnit = product.getDosesPerDispensingUnit();
        this.dosesPerMonth = programProduct.getDosesPerMonth();
        this.product = productName(product);
        this.modifiedBy = modifiedBy;
    }

    private String productName(Product product) {
        return  (product.getPrimaryName() == null ? "" : (product.getPrimaryName() + " ")) +
                (product.getForm().getCode() == null ? "" : (product.getForm().getCode() + " ")) +
                (product.getStrength() == null ? "" : (product.getStrength() + " ")) +
                (product.getDosageUnit().getCode() == null ? "" : product.getDosageUnit().getCode());

    }
}
