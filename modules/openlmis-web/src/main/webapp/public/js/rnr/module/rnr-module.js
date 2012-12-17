var rnrModule = angular.module('rnr', ['openlmis']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/init-rnr', {controller:InitiateRnrController, templateUrl:'partials/init.html', resolve:InitiateRnrController.resolve}).
        when('/create-rnr', {controller:CreateRnrController, templateUrl:'partials/create.html'}).
        otherwise({redirectTo:'/init-rnr'});
}]).directive('rnrValidator', function () {
        return {
            require:'?ngModel',
            link:function (scope, element, attrs, ctrl) {
                var validationFunction = rnrModule[attrs.rnrValidator];
                ctrl.$parsers.unshift(function (viewValue) {
                    if (validationFunction(viewValue, element.attr('name'))) {
                        ctrl.$setValidity(element.attr('name'), true);
                        ctrl.$setValidity('rnrError', true);
                        if (viewValue == "") viewValue = undefined;
                        return viewValue;
                    } else {
                        ctrl.$setValidity(element.attr('name'), false);
                        ctrl.$setValidity('rnrError', false);
                        return undefined;
                    }
                });
            }
        };
    });

rnrModule.positiveInteger = function (value, errorHolder) {
    var toggleErrorMessageDisplay = function (valid, errorHolder) {
        if (valid) {
            document.getElementById(errorHolder).style.display = 'none';
        } else {
            document.getElementById(errorHolder).style.display = 'block';
        }
    };

    var INTEGER_REGEXP = /^\d*$/;
    var valid = INTEGER_REGEXP.test(value);

    if (errorHolder != undefined) toggleErrorMessageDisplay(valid, errorHolder)

    return valid;
};

rnrModule.fill = function (lineItem, programRnrColumnList) {

    function isNumber(number) {
        return !isNaN(parseInt(number));
    }

    function fillConsumption() {
        c = lineItem.quantityDispensed = (isNumber(a) && isNumber(b) && isNumber(d) && isNumber(e)) ? a + b - d - e : null;
    }

    function fillStockInHand() {
        e = lineItem.stockInHand = (isNumber(a) && isNumber(b) && isNumber(c) && isNumber(d)) ? a + b - d - c : null;
    }

    function fillNormalizedConsumption() {
        if (!isNumber(c)) {
            lineItem.normalizedConsumption = null;
            return;
        }
        var m = 3; // will be picked up from the database in future
        var x = isNumber(lineItem.stockOutDays) ? parseInt(lineItem.stockOutDays) : 0;
        var f = isNumber(lineItem.newPatientCount) ? parseInt(lineItem.newPatientCount) : 0;
        var dosesPerMonth = parseInt(lineItem.dosesPerMonth);
        var g = parseInt(lineItem.dosesPerDispensingUnit);
        var consumptionAdjustedWithStockOutDays = ((m * 30) - x) == 0 ? c : (c * ((m * 30) / ((m * 30) - x)));
        var adjustmentForNewPatients = (f * Math.ceil(dosesPerMonth / g) ) * m;
        lineItem.normalizedConsumption = Math.round(consumptionAdjustedWithStockOutDays + adjustmentForNewPatients);
    }

    function fillAMC() {
        lineItem.amc = lineItem.normalizedConsumption;
    }

    var getSource = function (indicator) {
        var code;
        $(programRnrColumnList).each(function (i, column) {
            if (column.indicator == indicator) {
                code = column.source.name;
                return false;
            }
        });
        return code;
    };

    var a = parseInt(lineItem.beginningBalance);
    var b = parseInt(lineItem.quantityReceived);
    var c = parseInt(lineItem.quantityDispensed);
    var d = parseInt(lineItem.lossesAndAdjustments);
    var e = parseInt(lineItem.stockInHand);

    if (getSource('C') == 'CALCULATED') fillConsumption();
    if (getSource('E') == 'CALCULATED') fillStockInHand();
    fillNormalizedConsumption();
    fillAMC();

}
