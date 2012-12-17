describe('RnrModuleTest', function () {

    beforeEach(module('rnr'));

    it('should validate  integer field', function () {
        expect(rnrModule.positiveInteger(100)).toEqual(true);
        expect(rnrModule.positiveInteger(0)).toEqual(true);
        expect(rnrModule.positiveInteger(-1)).toEqual(false);
        expect(rnrModule.positiveInteger('a')).toEqual(false);
        expect(rnrModule.positiveInteger(5.5)).toEqual(false);
    });

    describe('Fill consumption when it is marked as a computed column', function () {
        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"USER_INPUT"}}
            ];
        });

        it('should not calculate consumption when one of the dependant columns is not set', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":2, "quantityDispensed":null, "lossesAndAdjustments":3, "stockInHand":null};
            rnrModule.fill(lineItem, programRnrColumnList);
            expect(null).toEqual(lineItem.quantityDispensed);
        });

        it('should calculate consumption', function () {
            var lineItem = {"id":1, "beginningBalance":5, "quantityReceived":20, "quantityDispensed":null, "lossesAndAdjustments":5, "stockInHand":10};

            rnrModule.fill(lineItem, programRnrColumnList);
            expect(10).toEqual(lineItem.quantityDispensed);
        });
    });

    describe('Fill stock in hand when it is marked as a computed column', function () {

        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"USER_INPUT"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}}
            ];
        });

        it('should not calculate stock in hand when one of the dependant columns is not set', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":2, "quantityDispensed":1, "lossesAndAdjustments":null, "stockInHand":null};

            rnrModule.fill(lineItem, programRnrColumnList);
            expect(null).toEqual(lineItem.stockInHand);
        });

        it('should calculate stock in hand when all values are 0 - NaN check', function () {
            var lineItem = {"id":1, "beginningBalance":0, "quantityReceived":0, "quantityDispensed":0, "lossesAndAdjustments":0, "stockInHand":null};

            rnrModule.fill(lineItem, programRnrColumnList);
            expect(0).toEqual(lineItem.stockInHand);
        });

        it('should calculate stock in hand', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":2, "lossesAndAdjustments":4, "stockInHand":null};

            rnrModule.fill(lineItem, programRnrColumnList);
            expect(5).toEqual(lineItem.stockInHand);
        });
    });

    describe('Fill normalized consumption', function () {

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

        it('should calculate normalized consumption when newPatientCount is not set', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":null, "lossesAndAdjustments":4, "stockInHand":2, "stockOutDays":5, "newPatientCount":null, "dosesPerMonth":30, "dosesPerDispensingUnit":28};

            rnrModule.fill(lineItem, programRnrColumnList);
            expect(5).toEqual(lineItem.normalizedConsumption);
        });

        it('should calculate normalized consumption when facility is stocked out for the entire reporting period', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":null, "lossesAndAdjustments":4, "stockInHand":2, "stockOutDays":90, "newPatientCount":10, "dosesPerMonth":30, "dosesPerDispensingUnit":28};

            rnrModule.fill(lineItem, programRnrColumnList);
            expect(65).toEqual(lineItem.normalizedConsumption);
        });

        it('should calculate normalized consumption when newPatientCount and stockOutDays is not set', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":null, "lossesAndAdjustments":4, "stockInHand":2, "stockOutDays":null, "newPatientCount":null, "dosesPerMonth":30, "dosesPerDispensingUnit":28};

            rnrModule.fill(lineItem, programRnrColumnList);
            expect(5).toEqual(lineItem.normalizedConsumption);
        });

        it('should calculate normalized consumption when newPatientCount is set', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":null, "lossesAndAdjustments":4, "stockInHand":2, "stockOutDays":5, "newPatientCount":10, "dosesPerMonth":30, "dosesPerDispensingUnit":28};

            rnrModule.fill(lineItem, programRnrColumnList);
            expect(65).toEqual(lineItem.normalizedConsumption);
        });
    });
});

