package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequisitionLineItem {

    private int id;
    private int rnrId;
    private String productCode;

    private int quantityReceived;
    private int quantityDispensed ;
    private int beginningBalance;
    private int estimatedConsumption;
    private int stockInHand;
    private int quantityRequested;
    private String reasonForRequestedQuantity;
    private int calculatedOrderQuantity;

    private int quantityApproved;
    private int lossesAndAdjustments;
    private String reasonForLossesAndAdjustments;

    private int patientCount;
    private int stockOutDays;
    private float normalizedConsumption;
    private float amc;
    private String maxStockQuantity;

    private int packsToShip;
    private float cost;
    private String remarks;

    public RequisitionLineItem(int rnrId, String productCode) {
        this.rnrId = rnrId;
        this.productCode = productCode;
    }

}
