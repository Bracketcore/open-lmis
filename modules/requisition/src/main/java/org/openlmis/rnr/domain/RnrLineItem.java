package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Product;

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
    private String maxMonthsOfStock;

    private Integer quantityReceived;
    private Integer quantityDispensed;
    private Integer beginningBalance;
    private Integer lossesAndAdjustments;
    private String reasonForLossesAndAdjustments;
    private Integer stockInHand;
    private Integer stockOutDays;
    private Integer newPatientCount;
    private Integer quantityRequested;
    private String reasonForRequestedQuantity;

    private Float amc;
    private Float normalizedConsumption;
    private Integer calculatedOrderQuantity;
    private Integer maxStockQuantity;

    private Integer quantityApproved;

    private Float cost;
    private Integer packsToShip;
    private String remarks;

    private String modifiedBy;
    private Date modifiedDate;

    public RnrLineItem(Integer rnrId, FacilityApprovedProduct facilityApprovedProduct, String modifiedBy) {
        this.rnrId = rnrId;

        // TODO : ugly
        Product product = facilityApprovedProduct.getProgramProduct().getProduct();
        this.productCode = product.getCode();
        this.unitOfIssue = product.getDispensingUnit();
        this.dosesPerDispensingUnit = product.getDosesPerDispensingUnit();
        this.dosesPerMonth = facilityApprovedProduct.getProgramProduct().getDosesPerMonth();
        this.product = productName(product);
        this.modifiedBy = modifiedBy;
    }

    private String productName(Product product) {
        return (product.getPrimaryName() == null ? "" : (product.getPrimaryName() + " ")) +
                (product.getForm().getCode() == null ? "" : (product.getForm().getCode() + " ")) +
                (product.getStrength() == null ? "" : (product.getStrength() + " ")) +
                (product.getDosageUnit().getCode() == null ? "" : product.getDosageUnit().getCode());

    }
}
