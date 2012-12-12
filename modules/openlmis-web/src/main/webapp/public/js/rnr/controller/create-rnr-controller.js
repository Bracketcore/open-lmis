function CreateRnrController($scope, RequisitionHeader, ProgramRnRColumnList, $location, $http) {

    $scope.positiveInteger = function (value, errorHolder) {
        var INTEGER_REGEXP = /^\d*$/;
        var valid = INTEGER_REGEXP.test(value);

        if (errorHolder != undefined) toggleErrorMessageDisplay(valid, errorHolder)

        return valid;
    };

    var toggleErrorMessageDisplay = function (valid, errorHolder) {
        if (valid) {
            document.getElementById(errorHolder).style.display = 'none';
        } else {
            document.getElementById(errorHolder).style.display = 'block';
        }
    };

    $scope.positiveFloat = function (value) {
        var FLOAT_REGEXP = /^\d+(\.\d\d)?$/;
        return FLOAT_REGEXP.test(value);
    };

    RequisitionHeader.get({facilityId:$scope.$parent.facility}, function (data) {
        $scope.header = data.requisitionHeader;
    }, function () {
        $location.path("init-rnr");
    });

    ProgramRnRColumnList.get({programCode:$scope.$parent.program.code}, function (data) {
        if (validate(data)) {
            $scope.$parent.error = "";
            $scope.programRnRColumnList = data.rnrColumnList;
        } else {
            $scope.$parent.error = "Please contact Admin to define R&R template for this program";
            $location.path('init-rnr');
        }
    }, function () {
        $location.path('init-rnr');
    });

    var validate = function (data) {
        return (data.rnrColumnList.length > 0);
    };

    $scope.saveRnr = function () {
        if ($scope.saveRnrForm.$error.rnrError != undefined && $scope.saveRnrForm.$error.rnrError != false && $scope.saveRnrForm.$error.rnrError.length > 0) {
            $scope.error = "Please correct errors before saving.";
            $scope.message = "";
            return;
        }
        $http.post('/logistics/rnr/' + $scope.$parent.rnr.id + '/save.json', $scope.$parent.rnr).success(function (data) {
            $scope.message = "R&R saved successfully!";
            $scope.error = "";
        });
    };

    $scope.fillCalculatedRnrColumns = function (index) {
        var lineItem = $scope.$parent.rnr.lineItems[index];
        var a = parseInt(lineItem.beginningBalance);
        var b = parseInt(lineItem.quantityReceived);
        var c = parseInt(lineItem.quantityDispensed);
        var d = parseInt(lineItem.lossesAndAdjustments);
        var e = parseInt(lineItem.stockInHand);

        var cSource = getSource('C');
        var eSource = getSource('E');

        fillConsumption();
        fillStockInHand();
        fillNormalizedConsumption();

        function fillNormalizedConsumption() {
            var m = 3;
            var x = isNaN(parseInt(lineItem.stockOutDays)) ? 0 : parseInt(lineItem.stockOutDays);
            var f = isNaN(parseInt(lineItem.newPatientCount)) ? 0 : parseInt(lineItem.newPatientCount);
            var dosesPerMonth = 1;
            var g = 1;
            // TODO : dosesPerMonth from programproduct table, g from product, rounding logic
            lineItem.normalizedConsumption = Math.round(((m * 30) - x) == 0 ? c : (c * ((m * 30) / ((m * 30) - x))) + ((f * dosesPerMonth / g ) * m));
        }

        function fillConsumption() {
            if (cSource == 'CALCULATED') {
                c = lineItem.quantityDispensed = (a && b && d && e) ? a + b - d - e : null;
            }
        }

        function fillStockInHand() {
            if (eSource == 'CALCULATED') {
                e = lineItem.stockInHand = (a && b && c && d) ? a + b - d - c : null;
            }
        }
    };

    var getSource = function (indicator) {
        var code;
        $($scope.programRnRColumnList).each(function (i, column) {
            if (column.indicator == indicator) {
                code = column.source.name;
                return false;
            }
        });
        return code;
    };

    $scope.getId = function (prefix, parent) {
        return prefix + "_" + parent.$parent.$parent.$index;
    };

    $scope.getIndex = function (parent) {
        return parent.$parent.$parent.$index;
    }
}