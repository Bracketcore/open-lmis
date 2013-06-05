/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('RnrLineItem', function () {
  beforeEach(module('rnr'));

  describe('Create RnrLineItem', function () {
    it('Should set previousNormalizedConsumptions to [] if it is null in json data', function () {
      var programRnrColumnList = [
        {"column1":"column 1"}
      ];

      var rnrLineItem = new RnrLineItem({}, 5, programRnrColumnList, "INITIATED");

      expect(rnrLineItem.previousNormalizedConsumptions).toEqual([]);
    });

    it('should initialize losses and adjustments, if not present in R&R', function () {

      var rnrLineItem = new RnrLineItem({'id':123, 'product':'Commodity Name' }, null, null);
      expect(rnrLineItem.lossesAndAdjustments).toEqual([]);
    });

  });

  describe('Calculate consumption', function () {
    var programRnrColumnList;
    var rnr;

    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}}
      ];
      rnr = {};
    });

    it('should calculate consumption', function () {
      var lineItem = {"beginningBalance":5, "quantityReceived":20, "quantityDispensed":null, "stockInHand":10};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);
      rnrLineItem.totalLossesAndAdjustments = 5;

      rnrLineItem.calculateConsumption();

      expect(rnrLineItem.quantityDispensed).toEqual(20);
    });

    it('should not calculate consumption when one of the dependant columns is not set', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":2, "quantityDispensed":null, "totalLossesAndAdjustments":3, "stockInHand":null};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);

      rnrLineItem.calculateConsumption();

      expect(rnrLineItem.quantityDispensed).toEqual(null);
    });

    it('should not calculate consumption when it is not a calculated field', function () {
      programRnrColumnList = [
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"USER_INPUT"}}
      ];
      var rnrLineItem = new RnrLineItem(null, rnr, programRnrColumnList);

      rnrLineItem.calculateConsumption();

      expect(rnrLineItem.quantityDispensed).toEqual(null);
    });
  });

  describe('Calculate stock in hand', function () {
    var programRnrColumnList;
    var rnr;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}}
      ];
      rnr = {};
    });

    it('should calculate stock in hand when all values are 0 - NaN check', function () {
      var lineItem = {"beginningBalance":0, "quantityReceived":0, "quantityDispensed":0, "totalLossesAndAdjustments":0, "stockInHand":null};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);

      rnrLineItem.calculateStockInHand();

      expect(rnrLineItem.stockInHand).toEqual(0);
    });

    it('should calculate stock in hand', function () {
      var lineItem = {"beginningBalance":10, "quantityReceived":10, "quantityDispensed":10, "stockInHand":null};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);
      rnrLineItem.totalLossesAndAdjustments = 1;

      rnrLineItem.calculateStockInHand();

      expect(rnrLineItem.stockInHand).toEqual(11);
    });

    it('should not calculate stock in hand when one of the dependant columns is not set', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":2, "quantityDispensed":null, "totalLossesAndAdjustments":null, "stockInHand":null};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);

      rnrLineItem.calculateStockInHand();

      expect(rnrLineItem.stockInHand).toEqual(null);
    });

    it('should not calculate stock in hand when it is not a calculated field', function () {
      programRnrColumnList = [
        {"indicator":"E", "name":"stockInHand", "source":{"name":"USER_INPUT"}}
      ];
      var rnrLineItem = new RnrLineItem(null, rnr, programRnrColumnList);

      rnrLineItem.calculateStockInHand();

      expect(rnrLineItem.stockInHand).toEqual(null);
    });


  });

  describe('Calculate normalized consumption', function () {
    var programRnrColumnList;
    var rnr;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
        {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
        {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
        {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
        {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
        {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
      ]
      rnr = {};
    });

    it('should calculate normalized consumption', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":5,
        "stockOutDays":5, "newPatientCount":10, "dosesPerMonth":30, "dosesPerDispensingUnit":28};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);
      rnrLineItem.totalLossesAndAdjustments = -4;

      rnrLineItem.calculateNormalizedConsumption();

      expect(rnrLineItem.normalizedConsumption).toEqual(65);
    });

    it('should not calculate normalized consumption when newPatientCount is displayed but not set', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":4, "totalLossesAndAdjustments":4, "stockOutDays":5, "newPatientCount":null};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);

      rnrLineItem.calculateNormalizedConsumption();

      expect(rnrLineItem.normalizedConsumption).toEqual(null);
    });

    it('should not calculate normalized consumption when consumption is empty', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":2, "quantityDispensed":null, "totalLossesAndAdjustments":3};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateNormalizedConsumption();

      expect(rnrLineItem.normalizedConsumption).toEqual(null);
    });

    it('should not calculate normalized consumption when stockOutDays is not set', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":13, "totalLossesAndAdjustments":4, "stockOutDays":null, "newPatientCount":10};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);


      rnrLineItem.calculateNormalizedConsumption();

      expect(rnrLineItem.normalizedConsumption).toEqual(null);
    });

    it('should calculate normalized consumption when facility is stocked out for the entire reporting period', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":13, "totalLossesAndAdjustments":4, "stockOutDays":90, "newPatientCount":10, "dosesPerMonth":30, "dosesPerDispensingUnit":28};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateNormalizedConsumption();

      expect(rnrLineItem.normalizedConsumption).toEqual(73);
    });

    it('should calculate normalized consumption when newPatientCount is not in the template', function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
        {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
        {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
        {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
        {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
      ];
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":5, "totalLossesAndAdjustments":-4, "stockOutDays":5, "newPatientCount":null, "dosesPerMonth":30, "dosesPerDispensingUnit":28};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateNormalizedConsumption();

      expect(rnrLineItem.normalizedConsumption).toEqual(5);
    });
  });

  describe('Calculate AMC', function () {
    it('should calculate AMC when number of months in a period is 3 or more', function () {
      var rnrLineItem = new RnrLineItem({}, 3, null, 'INITIATED');
      rnrLineItem.normalizedConsumption = 10;
      rnrLineItem.previousNormalizedConsumptions = [];

      rnrLineItem.calculateAMC();

      expect(rnrLineItem.amc).toEqual(3);
    });

    it('should calculate AMC when number of months in a period is 2', function () {
      var rnrLineItem = new RnrLineItem({}, 2, null, 'INITIATED');
      rnrLineItem.normalizedConsumption = 10;
      rnrLineItem.previousNormalizedConsumptions = [14];

      rnrLineItem.calculateAMC();

      expect(rnrLineItem.amc).toEqual(6);
    });

    it('should calculate AMC when number of months in a period is 2 but previous normalized consumption is not available', function () {
      var rnrLineItem = new RnrLineItem({}, 2, null, 'INITIATED');
      rnrLineItem.normalizedConsumption = 10;
      rnrLineItem.previousNormalizedConsumptions = [];

      rnrLineItem.calculateAMC();

      expect(rnrLineItem.amc).toEqual(5);
    });

    it('should calculate AMC when number of months in a period is 1', function () {
      var rnrLineItem = new RnrLineItem({}, 1, null, 'INITIATED');
      rnrLineItem.normalizedConsumption = 10;
      rnrLineItem.previousNormalizedConsumptions = [14, 12];

      rnrLineItem.calculateAMC();

      expect(rnrLineItem.amc).toEqual(12);
    });

    it('should calculate AMC when number of months in a period is 1 and only one of the two previous normalized consumption is available', function () {
      var rnrLineItem = new RnrLineItem({}, 1, null, 'INITIATED');
      rnrLineItem.normalizedConsumption = 10;
      rnrLineItem.previousNormalizedConsumptions = [14];

      rnrLineItem.calculateAMC();

      expect(rnrLineItem.amc).toEqual(12);
    });

    it('should reset AMC to null when normalized consumption is not present', function () {
      var lineItem = {"normalizedConsumption":null};
      var rnrLineItem = new RnrLineItem({amc:5}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateAMC();

      expect(rnrLineItem.amc).toEqual(null);
    });
  });

  describe('Calculate Max Stock Quantity', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
        {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
        {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
        {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
        {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
        {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
      ];
    });

    it('should calculate maxStockQuantity', function () {
      var lineItem = {"amc":15, "maxMonthsOfStock":3};
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateMaxStockQuantity();

      expect(rnrLineItem.maxStockQuantity).toEqual(45);
    });

    it('should not calculate maxStockQuantity if amc is not available', function () {
      var lineItem = {"maxMonthsOfStock":3};
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateMaxStockQuantity();

      expect(rnrLineItem.maxStockQuantity).toEqual(null);
    });
  });

  describe('Calculate Calculated Order Quantity', function () {
    it('should calculate calculatedOrderQuantity', function () {
      var lineItem = {"stockInHand":7, "maxStockQuantity":10};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateCalculatedOrderQuantity();

      expect(rnrLineItem.calculatedOrderQuantity).toEqual(3);
    });

    it('should not calculate calculatedOrderQuantity when stock in hand is not present', function () {
      var lineItem = {"stockInHand":null, "maxStockQuantity":3};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateCalculatedOrderQuantity();

      expect(rnrLineItem.calculatedOrderQuantity).toEqual(null);
    });

    it('should not calculate calculatedOrderQuantity when maxStockQuantity is not present', function () {
      var lineItem = {"stockInHand":7, "maxStockQuantity":null};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateCalculatedOrderQuantity();

      expect(rnrLineItem.calculatedOrderQuantity).toEqual(null);
    });

    it('should calculate calculatedOrderQuantity to be 0 when value goes negative', function () {
      var lineItem = {"stockInHand":10, "maxStockQuantity":3};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateCalculatedOrderQuantity();

      expect(rnrLineItem.calculatedOrderQuantity).toEqual(0);
    });
  });

  describe('Calculate Packs To Ship', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
        {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"USER_INPUT"}},
        {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
        {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
        {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
        {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
      ];
    });

    it('should calculate packsToShip when calculated quantity is available and requested quantity is null', function () {
      var rnrLineItem = new RnrLineItem(null, null, null, 'INITIATED');
      rnrLineItem.calculatedOrderQuantity = 8;

      spyOn(rnrLineItem, 'calculatePacksToShip');

      rnrLineItem.fillPacksToShip();
      expect(rnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(8);
    });

    it('should calculate packsToShip for the given quantity', function () {
      var lineItem = {"packSize":12};
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);
      spyOn(rnrLineItem, 'applyRoundingRulesToPacksToShip');

      rnrLineItem.calculatePacksToShip(25);

      expect(rnrLineItem.packsToShip).toEqual(2);
      expect(rnrLineItem.applyRoundingRulesToPacksToShip).toHaveBeenCalledWith(25);
    });
  });

  describe('Apply rounding rules to packs to ship', function () {
    it('should set packsToShip to one when packsToShip is zero and roundToZero is false', function () {
      var lineItem = {"packsToShip":0, "roundToZero":false, "packSize":12, "packRoundingThreshold":7};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.applyRoundingRulesToPacksToShip(5);

      expect(rnrLineItem.packsToShip).toEqual(1);
    });

    it('should set packsToShip to zero when packsToShip is zero and roundToZero is true', function () {
      var lineItem = {"packsToShip":0, "roundToZero":true, "packSize":12, "packRoundingThreshold":7};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.applyRoundingRulesToPacksToShip(5);

      expect(rnrLineItem.packsToShip).toEqual(0);
    });

    it('should increment packsToShip by one when number of remaining items is greater than packRoundingThreshold ', function () {
      var lineItem = {"packsToShip":2, "packSize":12, "packRoundingThreshold":7};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.applyRoundingRulesToPacksToShip(8);

      expect(rnrLineItem.packsToShip).toEqual(3);
    });

    it('should not increment packsToShip when number of remaining items is greater than packRoundingThreshold ', function () {
      var lineItem = {"packsToShip":2, "packSize":12, "packRoundingThreshold":7};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.applyRoundingRulesToPacksToShip(6);

      expect(rnrLineItem.packsToShip).toEqual(2);
    });
  });

  describe('Calculate Cost', function () {
    it('should set cost when pricePerPack and packsToShip are available', function () {
      var lineItem = {"packsToShip":11, "price":200};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateCost();

      expect(rnrLineItem.cost).toEqual(2200.00.toFixed(2));
    });

    it('should set cost to zero when packsToShip is not available', function () {
      var lineItem = {"price":200};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateCost();

      expect(rnrLineItem.cost).toEqual(0);
    });
  });

  describe('Calculate Total', function () {
    it('should set total when beginningBalance and quantityReceived are available', function () {
      var lineItem = {"beginningBalance":11, "quantityReceived":200};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateTotal();

      expect(rnrLineItem.total).toEqual(211);
    });

    it('should not calculate total when beginningBalance is not available', function () {
      var lineItem = {"quantityReceived":200};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateTotal();

      expect(rnrLineItem.total).toEqual(null);
    });

    it('should not calculate total when quantityReceived is not available', function () {
      var lineItem = {"beginningBalance":200};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateTotal();

      expect(rnrLineItem.total).toEqual(null);
    });
  });

  describe('Losses and adjustment for line item', function () {
    it('should create losses and adjustment object out of losses and adjustment json data when RnrLineItem Is Created', function () {
      var lossAndAdjustment1 = {"type":{"name":"Loss1", "additive":true}, "quantity":45};
      var lossAndAdjustment2 = {"type":{"name":"Adjust1", "additive":false}, "quantity":55};
      var lineItem = {"id":1, "lossesAndAdjustments":[lossAndAdjustment1, lossAndAdjustment2]};

      var rnrLineItem = new RnrLineItem(lineItem);

      expect(rnrLineItem.lossesAndAdjustments.length).toEqual(2);

      expect("isQuantityValid" in rnrLineItem.lossesAndAdjustments[0]).toBeTruthy();
      expect("isQuantityValid" in rnrLineItem.lossesAndAdjustments[1]).toBeTruthy();
    });

    it("should re evaluate total losses and adjustments for line item", function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();

      var lossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
      var lineItem = {"id":"1", "totalLossesAndAdjustments":40, lossesAndAdjustments:[lossAndAdjustment]};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      spyOn(rnrLineItem, "updateTotalLossesAndAdjustment");

      rnrLineItem.reEvaluateTotalLossesAndAdjustments();

      expect(rnrLineItem.updateTotalLossesAndAdjustment).toHaveBeenCalledWith(45, true);
    });

    it("should remove losses and adjustments for line item and update total losses and adjustments", function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var lossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
      var lineItem = {"id":"1", "totalLossesAndAdjustments":45, lossesAndAdjustments:[new LossAndAdjustment(lossAndAdjustment)]};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      spyOn(rnrLineItem, "updateTotalLossesAndAdjustment");

      rnrLineItem.removeLossAndAdjustment(rnrLineItem.lossesAndAdjustments[0]);

      expect(rnrLineItem.updateTotalLossesAndAdjustment).toHaveBeenCalledWith(45, false);
      expect(rnrLineItem.lossesAndAdjustments).toEqual([]);
    });

    it("should add losses and adjustments for line item and update total losses and adjustments", function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var lossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
      var expectedLossAndAdjustment = new LossAndAdjustment({"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45});
      var lineItem = {"id":"1", "totalLossesAndAdjustments":0, lossesAndAdjustments:[]};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      spyOn(rnrLineItem, "updateTotalLossesAndAdjustment");
      rnrLineItem.addLossAndAdjustment(lossAndAdjustment);

      expect(rnrLineItem.updateTotalLossesAndAdjustment).toHaveBeenCalledWith(45, true);
      expect(rnrLineItem.lossesAndAdjustments).toEqual([expectedLossAndAdjustment]);
      expect("isQuantityValid" in rnrLineItem.lossesAndAdjustments[0]).toBeTruthy();
    });

    it('should update total losses and adjustments and add additive lossAndAdjustment', function () {
      var rnr = {"id":1};
      var programRnrColumnList = [];
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, {"totalLossesAndAdjustments":20});

      spyOn(rnrLineItem, "fillConsumptionOrStockInHand");

      rnrLineItem.updateTotalLossesAndAdjustment(15, true);

      expect(rnrLineItem.fillConsumptionOrStockInHand).toHaveBeenCalled();
      expect(rnrLineItem.totalLossesAndAdjustments).toEqual(35);
    });

    it('should update total losses and adjustments and subtract non-additive lossAndAdjustment', function () {
      var rnr = {"id":1};
      var programRnrColumnList = [];
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, {"totalLossesAndAdjustments":40});

      spyOn(rnrLineItem, "fillConsumptionOrStockInHand");

      rnrLineItem.updateTotalLossesAndAdjustment(15, false);

      expect(rnrLineItem.fillConsumptionOrStockInHand).toHaveBeenCalled();
      expect(rnrLineItem.totalLossesAndAdjustments).toEqual(25);
    });

    it('should return true on validate losses and adjustments if no losses and adjustments present in the rnrLineItem', function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var rnrLineItem = new RnrLineItem({"id":"1"}, rnr, programRnrColumnList);

      expect(rnrLineItem.validateLossesAndAdjustments()).toBeTruthy();
    });

    it('should return false if any loss and adjustment is not valid', function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var lossAndAdjustment1 = {"type":{"name":"LOSS1", "additive":true}, "quantity":45};
      var lossAndAdjustment2 = {"type":{"name":"LOSS2", "additive":true}, "quantity":89};

      var lineItem = {"id":"1", lossesAndAdjustments:[lossAndAdjustment1, lossAndAdjustment2]};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);

      spyOn(rnrLineItem.lossesAndAdjustments[0], "isQuantityValid").andReturn(true);
      spyOn(rnrLineItem.lossesAndAdjustments[1], "isQuantityValid").andReturn(false);

      expect(rnrLineItem.validateLossesAndAdjustments()).toBeFalsy();
    });

    it('should return true if all losses and adjustments are valid', function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var lossAndAdjustment1 = {"type":{"name":"LOSS1", "additive":true}, "quantity":45};
      var lossAndAdjustment2 = {"type":{"name":"LOSS2", "additive":true}, "quantity":89};

      var lineItem = {"id":"1", lossesAndAdjustments:[lossAndAdjustment1, lossAndAdjustment2]};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);

      spyOn(rnrLineItem.lossesAndAdjustments[0], "isQuantityValid").andReturn(true);
      spyOn(rnrLineItem.lossesAndAdjustments[1], "isQuantityValid").andReturn(true);

      expect(rnrLineItem.validateLossesAndAdjustments()).toBeTruthy();
    });

  });

  describe('Arithmetic validation', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true}
      ];
    });

    it("should do arithmetic validations if on ", function () {
      var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":3, "totalLossesAndAdjustments":-3, "stockInHand":3};
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);
      var arithmeticallyInvalid = rnrLineItem.arithmeticallyInvalid();

      expect(arithmeticallyInvalid).toEqual(true);

      rnrLineItem.quantityDispensed = 0;

      arithmeticallyInvalid = rnrLineItem.arithmeticallyInvalid();
      expect(arithmeticallyInvalid).toEqual(false);

    });

    it("should return false arithmetic validations if off ", function () {
      programRnrColumnList[0].formulaValidationRequired = false;
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);

      expect(rnrLineItem.arithmeticallyInvalid()).toEqual(false);
    });
  });

  describe('Error message to be displayed', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true}
      ];
    });


    it("should give error message for arithmetic validation error ", function () {
      var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":3, "stockInHand":3};
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      spyOn(rnrLineItem, 'arithmeticallyInvalid').andReturn("error");
      var errorMsg = rnrLineItem.getErrorMessage();
      expect(errorMsg).toEqual("The entries are arithmetically invalid, please recheck");
    });

    it("should give error message for negative stock in hand", function () {
      var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":33, "stockInHand":-3};
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      var errorMsg = rnrLineItem.getErrorMessage();

      expect(errorMsg).toEqual("Stock On Hand is calculated to be negative, please validate entries");
    });

    it("should give error message for negative quantity dispensed ", function () {
      programRnrColumnList[0].formulaValidationRequired = false;
      var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":-3, "stockInHand":3};
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      var errorMsg = rnrLineItem.getErrorMessage();

      expect(errorMsg).toEqual("Total Quantity Consumed is calculated to be negative, please validate entries");
    });

  });

  describe('Get Source name', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true}
      ];
    });

    it('should get rnr column source name for the provided indicator', function () {
      var lineItem = new RnrLineItem({"id":15}, null, programRnrColumnList);
      expect(lineItem.getSource("A")).toEqual("USER_INPUT");
    });
  });

  describe('Execution workflow for calculation', function () {
    var programRnrColumnList;
    var rnr;
    var rnrLineItem;

    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true}
      ];
      rnr = {"id":1};
      var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":4, "quantityDispensed":-3, "stockInHand":9};
      rnrLineItem = new RnrLineItem(lineItem, 1, programRnrColumnList, 'INITIATED');
      rnrLineItem.totalLossesAndAdjustments = 34;
    });

    it('should test execution flow when consumption or stock in hand gets filled', function () {
      spyOn(rnrLineItem, "calculateConsumption");
      spyOn(rnrLineItem, "calculateStockInHand");
      spyOn(rnrLineItem, "fillNormalizedConsumption");
      spyOn(utils, "parseIntWithBaseTen");

      rnrLineItem.fillConsumptionOrStockInHand();

      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(3);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(4);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(-3);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(34);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(9);

      expect(rnrLineItem.calculateConsumption).toHaveBeenCalled();
      expect(rnrLineItem.calculateStockInHand).toHaveBeenCalled();
      expect(rnrLineItem.fillNormalizedConsumption).toHaveBeenCalled();
    });

    it('should test execution flow when packs to ship gets filled and order quantity is quantity requested', function () {
      rnrLineItem.quantityRequested = 31;

      spyOn(rnrLineItem, "calculatePacksToShip");
      spyOn(rnrLineItem, "calculateCost");

      rnrLineItem.fillPacksToShip();

      expect(rnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(31);
      expect(rnrLineItem.calculateCost).toHaveBeenCalled();
    });

    it('should test execution flow when packs to ship gets filled and order quantity is not present', function () {
      rnrLineItem.quantityRequested = null;
      rnrLineItem.calculatedOrderQuantity = 12;

      spyOn(rnrLineItem, "calculatePacksToShip");
      spyOn(rnrLineItem, "calculateCost");

      rnrLineItem.fillPacksToShip();

      expect(rnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(12);
      expect(rnrLineItem.calculateCost).toHaveBeenCalled();
    });

    it('should test execution flow when normalized consumption gets filled', function () {
      spyOn(rnrLineItem, "calculateNormalizedConsumption");
      spyOn(rnrLineItem, "fillAMC");

      rnrLineItem.fillNormalizedConsumption();

      expect(rnrLineItem.calculateNormalizedConsumption).toHaveBeenCalled();
      expect(rnrLineItem.fillAMC).toHaveBeenCalled();
    });

    xit('should test execution flow when rnr line item cost gets filled when it is of full supply type', function () {
      rnrLineItem.fullSupply = true;

      spyOn(rnrLineItem, "calculateCost");

      rnrLineItem.fillCost();

      expect(rnrLineItem.calculateCost).toHaveBeenCalled();
    });

    xit('should test execution flow when rnr line item cost gets filled when it is of non-full supply type', function () {
      rnrLineItem.fullSupply = false;

      spyOn(rnrLineItem, "calculateCost");
      spyOn(rnrLineItem, "calculateNonFullSupplyItemsSubmittedCost");

      rnrLineItem.fillCost();

      expect(rnrLineItem.calculateCost).toHaveBeenCalled();
      expect(rnrLineItem.calculateNonFullSupplyItemsSubmittedCost).toHaveBeenCalled();
    });

    it('should test execution flow when amc gets filled', function () {
      spyOn(rnrLineItem, "calculateAMC");
      spyOn(rnrLineItem, "fillMaxStockQuantity");

      rnrLineItem.fillAMC();

      expect(rnrLineItem.calculateAMC).toHaveBeenCalled();
      expect(rnrLineItem.fillMaxStockQuantity).toHaveBeenCalled();
    });

    it('should test execution flow when max stock quantity gets filled', function () {
      spyOn(rnrLineItem, "calculateMaxStockQuantity");
      spyOn(rnrLineItem, "fillCalculatedOrderQuantity");

      rnrLineItem.fillMaxStockQuantity();

      expect(rnrLineItem.calculateMaxStockQuantity).toHaveBeenCalled();
      expect(rnrLineItem.fillCalculatedOrderQuantity).toHaveBeenCalled();
    });

    it('should test execution flow when calculated order quantity gets filled', function () {
      spyOn(rnrLineItem, "calculateCalculatedOrderQuantity");
      spyOn(rnrLineItem, "fillPacksToShip");

      rnrLineItem.fillCalculatedOrderQuantity();

      expect(rnrLineItem.calculateCalculatedOrderQuantity).toHaveBeenCalled();
      expect(rnrLineItem.fillPacksToShip).toHaveBeenCalled();
    });

    xit('should consider approved quantity as zero when negative or not defined', function () {
      rnrLineItem.quantityApproved = -30;

      spyOn(rnrLineItem, "calculatePacksToShip");
      spyOn(rnrLineItem, "fillCost");

      rnrLineItem.fillPacksToShip();

      expect(rnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(0);
      expect(rnrLineItem.fillCost).toHaveBeenCalled();
    });

    it('should update cost when approved quantity gets filled', function () {
      spyOn(rnrLineItem, "calculateCost");

      rnrLineItem.fillPacksToShip();

      expect(rnrLineItem.calculateCost).toHaveBeenCalled();
    });

    it('should consider approved quantity to calculate packs to ship status is in approval', function () {
      rnrLineItem = new RnrLineItem({}, 5, [], 'IN_APPROVAL');
      rnrLineItem.quantityApproved = 7;
      rnrLineItem.quantityRequested = 78;
      rnrLineItem.calculatedOrderQuantity = 90;
      spyOn(rnrLineItem, 'calculatePacksToShip');

      rnrLineItem.fillPacksToShip();

      expect(rnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(7);
    });

    it('should consider approved quantity to calculate packs to ship status is in approval', function () {
      rnrLineItem = new RnrLineItem({}, 5, [], 'APPROVED');
      rnrLineItem.quantityApproved = 7;
      rnrLineItem.quantityRequested = 78;
      rnrLineItem.calculatedOrderQuantity = 90;
      spyOn(rnrLineItem, 'calculatePacksToShip');

      rnrLineItem.fillPacksToShip();

      expect(rnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(7);
    });

    it('should return true if visible user input fields are filled', function () {
      programRnrColumnList = [
        {"source":{"name":"USER_INPUT"}, "name":"beginningBalance", "visible":true},
        {"source":{"name":"USER_INPUT"}, "name":"quantityReceived", "visible":true},
        {"source":{"name":"USER_INPUT"}, "name":"quantityDispensed", "visible":true},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"newPatientCount"},
        {"source":{"name":"USER_INPUT"}, "visible":false, "name":"stockOutDays"}
      ];
      var rnrLineItem = {'beginningBalance':'45', 'quantityDispensed':'23', 'quantityReceived':3, 'newPatientCount':45,
        'stockOutDays':''};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);
      var isValid = rnrLineItem.validateRequiredFieldsForFullSupply();
      expect(isValid).toBeTruthy();
    });

    it('should return false if visible user input field missing', function () {
      programRnrColumnList = [
        {"source":{"name":"USER_INPUT"}, "name":"beginningBalance", "visible":true},
        {"source":{"name":"USER_INPUT"}, "name":"quantityReceived", "visible":true},
        {"source":{"name":"USER_INPUT"}, "name":"quantityDispensed", "visible":true},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"newPatientCount"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"stockOutDays"}
      ];
      var rnrLineItem = {'beginningBalance':'', 'quantityDispensed':'23', 'quantityReceived':3, 'newPatientCount':45,
        'stockOutDays':''};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);
      var isValid = rnrLineItem.validateRequiredFieldsForFullSupply();
      expect(isValid).toBeFalsy();
    });

    it('should return false if requested quantity is filled and reason is not filled', function () {
      programRnrColumnList = [
        {"source":{"name":"USER_INPUT"}, "name":"beginningBalance", "visible":true},
        {"source":{"name":"USER_INPUT"}, "name":"quantityReceived", "visible":true},
        {"source":{"name":"USER_INPUT"}, "name":"quantityDispensed", "visible":true},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"newPatientCount"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"stockOutDays"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"quantityRequested"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"reasonForRequestedQuantity"}
      ];
      var rnrLineItem = {'beginningBalance':'45', 'stockOutDays':'23', 'quantityDispensed':'23', 'quantityReceived':'89', 'newPatientCount':45,
        'quantityRequested':'7', 'reasonForRequestedQuantity':''};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);
      var isValid = rnrLineItem.validateRequiredFieldsForFullSupply();
      expect(isValid).toBeFalsy();
    });

    it('should return true if requested quantity is filled and reason is also filled', function () {
      programRnrColumnList = [
        {"source":{"name":"USER_INPUT"}, "name":"beginningBalance", "visible":true},
        {"source":{"name":"USER_INPUT"}, "name":"quantityReceived", "visible":true},
        {"source":{"name":"USER_INPUT"}, "name":"quantityDispensed", "visible":true},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"newPatientCount"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"stockOutDays"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"quantityRequested"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"reasonForRequestedQuantity"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"remarks"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"lossesAndAdjustments"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"quantityApproved"}
      ];
      var rnrLineItem = {'beginningBalance':'45', 'stockOutDays':'23', 'quantityDispensed':'23', 'quantityReceived':'89', 'newPatientCount':45,
        'quantityRequested':'7', 'reasonForRequestedQuantity':'reason', remarks:'', lossesAndAdjustments:'', quantityApproved:''};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);
      var isValid = rnrLineItem.validateRequiredFieldsForFullSupply();
      expect(isValid).toBeTruthy();
    });

    it('should return true if required fields for non full supply are not filled', function () {
      programRnrColumnList = [
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"quantityRequested"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"reasonForRequestedQuantity"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"remarks"}
      ];
      var rnrLineItem = {'quantityRequested':'', 'reasonForRequestedQuantity':'reason', remarks:''};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);
      var isValid = rnrLineItem.validateRequiredFieldsForNonFullSupply();
      expect(isValid).toBeFalsy();
    });

    it('should return true if required fields for non full supply are filled', function () {
      programRnrColumnList = [
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"quantityRequested"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"reasonForRequestedQuantity"},
        {"source":{"name":"USER_INPUT"}, "visible":true, "name":"remarks"}
      ];
      var rnrLineItem = {'quantityRequested':'45', 'reasonForRequestedQuantity':'reason', remarks:''};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);
      var isValid = rnrLineItem.validateRequiredFieldsForNonFullSupply();
      expect(isValid).toBeTruthy();
    });

    it('should validate stock in hand formula and return true if stock in hand positive', function () {
      var rnrLineItem = {'stockInHand':90, 'quantityDispensed':90};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);
      var isValid = rnrLineItem.formulaValid();
      expect(isValid).toBeTruthy();
    });

    it('should validate stock in hand formula and return false if stock in hand negative', function () {
      var rnrLineItem = {'stockInHand':-90, 'quantityDispensed':90};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);
      var isValid = rnrLineItem.formulaValid();
      expect(isValid).toBeFalsy();
    });

    it('should validate stock in hand formula and return true if quantity dispensed positive', function () {
      var rnrLineItem = {'quantityDispensed':90, 'stockInHand':90};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);
      var isValid = rnrLineItem.formulaValid();
      expect(isValid).toBeTruthy();
    });

    it('should validate stock in hand formula and return false if quantity dispensed negative', function () {
      var rnrLineItem = {'quantityDispensed':-90, 'stockInHand':90};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);
      var isValid = rnrLineItem.formulaValid();
      expect(isValid).toBeFalsy();
    });

    it('should validate stock in hand formula and and return false if arithmetically invalid', function () {
      var rnrLineItem = {'quantityDispensed':90, 'stockInHand':90};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);

      spyOn(rnrLineItem, 'arithmeticallyInvalid').andReturn(true);

      var isValid = rnrLineItem.formulaValid();

      expect(isValid).toBeFalsy();
    });

    it('should validate stock in hand formula and and return false if arithmetically valid', function () {
      var rnrLineItem = {'quantityDispensed':90, 'stockInHand':90};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);

      spyOn(rnrLineItem, 'arithmeticallyInvalid').andReturn(false);

      var isValid = rnrLineItem.formulaValid();

      expect(isValid).toBeTruthy();
    });

    it('should validate line item and and return true if valid', function () {
      var rnrLineItem = {fullSupply:true};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);

      spyOn(rnrLineItem, 'formulaValid').andReturn(true);
      spyOn(rnrLineItem, 'validateRequiredFieldsForFullSupply').andReturn(true);

      var isValid = rnrLineItem.valid();

      expect(isValid).toBeTruthy();
    });

    it('should validate line item and and return false if invalid', function () {
      var rnrLineItem = {fullSupply:true};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);

      spyOn(rnrLineItem, 'formulaValid').andReturn(false);
      spyOn(rnrLineItem, 'validateRequiredFieldsForFullSupply').andReturn(true);

      var isValid = rnrLineItem.valid();

      expect(isValid).toBeFalsy();
    });

    it('should validate line item and and return false if arithmetically invalid', function () {
      var rnrLineItem = {fullSupply:true};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);

      spyOn(rnrLineItem, 'formulaValid').andReturn(true);
      spyOn(rnrLineItem, 'validateRequiredFieldsForFullSupply').andReturn(false);

      var isValid = rnrLineItem.valid();

      expect(isValid).toBeFalsy();
    });

    it('should validate line item and and return false if invalid', function () {
      var rnrLineItem = {fullSupply:false};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);

      spyOn(rnrLineItem, 'validateRequiredFieldsForNonFullSupply').andReturn(false);

      var isValid = rnrLineItem.valid();

      expect(isValid).toBeFalsy();
    });

    it('should validate non full supply line item and and return true if valid', function () {
      var rnrLineItem = {fullSupply:false};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList);

      spyOn(rnrLineItem, 'validateRequiredFieldsForNonFullSupply').andReturn(true);

      var isValid = rnrLineItem.valid();

      expect(isValid).toBeTruthy();
    });

    it('should validate non full supply line item in approved rnr and and return true if valid', function () {
      var rnrLineItem = {fullSupply:false};
      rnrLineItem = new RnrLineItem(rnrLineItem, null, programRnrColumnList, 'IN_APPROVAL');

      spyOn(rnrLineItem, 'validateForApproval').andReturn(true);

      var isValid = rnrLineItem.valid();

      expect(isValid).toBeTruthy();
    });

    it('should return true if quantity approved filled', function () {
      var rnrLineItem = {fullSupply:false, quantityApproved:56};
      rnrLineItem = new RnrLineItem(rnrLineItem, 5, [], 'IN_APPROVAL');
      var valid = rnrLineItem.validateForApproval();

      expect(valid).toBeTruthy();
    });

    it('should return false if quantity approved filled', function () {
      var rnrLineItem = {fullSupply:false, quantityApproved:''};
      rnrLineItem = new RnrLineItem(rnrLineItem, 5, [], 'IN_APPROVAL');
      var valid = rnrLineItem.validateForApproval();

      expect(valid).toBeFalsy();
    });

    it('should reduce rnr line item to have only productCode, approvedQuantity and remarks', function () {
      var rnrLineItem = {id:1, beginningBalance:10, quantityDispensed:5, quantityReceived:2, fullSupply:true, quantityApproved:3, remarks:'some remarks', productCode: 'P10'};
      rnrLineItem = new RnrLineItem(rnrLineItem, 5, [], 'IN_APPROVAL');
      var reducedRnrLineItem = rnrLineItem.reduceForApproval();

      expect(reducedRnrLineItem).toEqual( {id:1, productCode: 'P10', quantityApproved:3, remarks:'some remarks'});
    });

  });

  describe('Compare RnrLineItems', function () {

    function createRnrLineItem(productCategoryDisplayOrder, productCategory, productCode, productDisplayOrder) {
      var rnrLineItem = new RnrLineItem();
      rnrLineItem.productCategoryDisplayOrder = productCategoryDisplayOrder;
      rnrLineItem.productCategory = productCategory;
      rnrLineItem.productCode = productCode;
      rnrLineItem.productDisplayOrder = productDisplayOrder;
      return rnrLineItem;
    }

    it('Should compare rnr line items', function () {
      var rnrLineItem1 = createRnrLineItem(1, "C1", "P990", null);
      var rnrLineItem2 = createRnrLineItem(10, "C3", "P990", null);
      var rnrLineItem3 = createRnrLineItem(1, "C1", "P990", 1);
      var rnrLineItem4 = createRnrLineItem(1, "C1", "P990", 3);
      var rnrLineItem5 = createRnrLineItem(10, "C1", "aaa", null);
      var rnrLineItem6 = createRnrLineItem(10, "C1", "ggg", null);
      var rnrLineItem7 = createRnrLineItem(10, "C2", null, null);
      var rnrLineItem8 = createRnrLineItem(10, "C1", null, null);

      expect(rnrLineItem1.compareTo(rnrLineItem2)).toBeLessThan(0);
      expect(rnrLineItem2.compareTo(rnrLineItem2)).toBe(0);
      expect(rnrLineItem3.compareTo(rnrLineItem4)).toBeLessThan(0);
      expect(rnrLineItem4.compareTo(rnrLineItem3)).toBeGreaterThan(0);
      expect(rnrLineItem5.compareTo(rnrLineItem6)).toBeLessThan(0);
      expect(rnrLineItem3.compareTo(rnrLineItem6)).toBeLessThan(0);
      expect(rnrLineItem6.compareTo(rnrLineItem3)).toBeGreaterThan(0);
      expect(rnrLineItem6.compareTo(undefined)).toBeLessThan(0);
      expect(rnrLineItem7.compareTo(rnrLineItem8)).toBeGreaterThan(0);
    });

    it('Should compare rnr line items on product code when display order is same', function () {
      var rnrLineItem1 = createRnrLineItem(1, "C1", "P2", 1);
      var rnrLineItem2 = createRnrLineItem(1, "C1", "P10", 1);

      expect(rnrLineItem1.compareTo(rnrLineItem2)).toBeGreaterThan(0);
    });

  });
});

