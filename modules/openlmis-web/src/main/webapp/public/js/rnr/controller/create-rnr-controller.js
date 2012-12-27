function CreateRnrController($scope, ReferenceData, ProgramRnRColumnList, $location, Requisition, $route) {

  if (!$scope.$parent.rnr) {
    Requisition.get({facilityId:encodeURIComponent($route.current.params.facility), programId:encodeURIComponent($route.current.params.program)},
      function (data) {
        $scope.rnr = data.rnr;
      }, function (data) {
        $scope.$parent.error = data.data.error;
        $location.path($scope.$parent.sourceUrl);
      });
  }

  ReferenceData.get({}, function (data) {
    $scope.currency = data.currency;
  }, {});


  ProgramRnRColumnList.get({programId:$route.current.params.program}, function (data) {
    function resetFullSupplyItemsCostIfNull(rnr) {
      if (rnr == null) return;
      if (rnr.fullSupplyItemsSubmittedCost == null)
        rnr.fullSupplyItemsSubmittedCost = 0;
    }

    function resetTotalSubmittedCostIfNull(rnr) {
      if (rnr == null) return;
      if (rnr.totalSubmittedCost == null)
        rnr.totalSubmittedCost = 0;
    }

    if (validate(data)) {
      $scope.programRnRColumnList = data.rnrColumnList;
      resetFullSupplyItemsCostIfNull($scope.$parent.rnr);
      resetTotalSubmittedCostIfNull($scope.$parent.rnr);
    } else {
      $scope.$parent.error = "Please contact Admin to define R&R template for this program";
      $location.path($scope.$parent.sourceUrl);
    }
  }, function () {
    $location.path($scope.$parent.sourceUrl);
  });

  // TODO : is this required?
  var validate = function (data) {
    return (data.rnrColumnList.length > 0);
  };

  $scope.saveRnr = function () {
    if ($scope.saveRnrForm.$error.rnrError) {
      $scope.error = "Please correct errors before saving.";
      $scope.message = "";
      return;
    }

    Requisition.update({facilityId:encodeURIComponent($route.current.params.facility), programId:encodeURIComponent($route.current.params.program)},
      $scope.rnr, function (data) {
        $scope.message = "R&R saved successfully!";
        $scope.error = "";
      }, {});
  };


  $scope.fillCalculatedRnrColumns = function (lineItem, rnr, data) {
    rnrModule.fill(lineItem, $scope.programRnRColumnList, rnr);
  };

  $scope.getId = function (prefix, parent) {
    return prefix + "_" + parent.$parent.$parent.$index;
  };

  $scope.hide = function () {
    return "";
  };

  $scope.showCurrencySymbol = function (value) {
    if (value != 0 && (value == undefined || value == null || value == false)) {
      return "";
    }
    return "defined";
  };

  $scope.showSelectedColumn = function (columnName) {
    if (($scope.rnr.status == "INITIATED" || $scope.rnr.status == "CREATED") && columnName == "quantityApproved")
      return undefined;
    return "defined";
  };
}