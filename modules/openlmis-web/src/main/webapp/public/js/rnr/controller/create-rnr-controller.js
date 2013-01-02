function CreateRnrController($scope, ReferenceData, ProgramRnRColumnList, $location, Requisition, $route, LossesAndAdjustmentsReferenceData) {

  $scope.lossesAndAdjustmentsModal = [];

  if (!$scope.$parent.rnr) {
    Requisition.get({facilityId:$route.current.params.facility, programId:$route.current.params.program},
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

  LossesAndAdjustmentsReferenceData.get({}, function (data) {
    $scope.allTypes = data.lossAdjustmentTypes;
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

    Requisition.update({facilityId:$route.current.params.facility, programId:$route.current.params.program},
      $scope.rnr, function (data) {
        $scope.message = "R&R saved successfully!";
        $scope.error = "";
      }, {});
  };


  $scope.fillCalculatedRnrColumns = function (lineItem, rnr, data) {
    rnrModule.fill(lineItem, $scope.programRnRColumnList, rnr);
  };

  $scope.getId = function (prefix, parent, isLossAdjustment) {
    if (isLossAdjustment != null && isLossAdjustment != undefined && isLossAdjustment) {
      return prefix + "_" + parent.$parent.$parent.$index + "_" + parent.$parent.$parent.$parent.$index;
    }
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

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
  };

  function updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem) {
    var lossesAndAdjustmentTypesForLineItem = [];
    $(lineItem.lossesAndAdjustments).each(function (index, lineItemLossAndAdjustment) {
        lossesAndAdjustmentTypesForLineItem.push(lineItemLossAndAdjustment.type.name.name);
      });

    $scope.lossesAndAdjustmentTypesToDisplay = $.grep($scope.allTypes, function(lAndATypeObject){
      return $.inArray(lAndATypeObject.name.name, lossesAndAdjustmentTypesForLineItem) == -1;
    });
  }

  function updateTotalLossesAndAdjustment(quantity, additive, lineItem) {
    if (!isNaN(quantity)) {
      if (additive) {
        lineItem.totalLossesAndAdjustments += quantity;
      } else {
        lineItem.totalLossesAndAdjustments -= quantity;
      }
    }
  }

  $scope.reEvaluateTotalLossesAndAdjustments = function(lineItem) {
    lineItem.totalLossesAndAdjustments = 0;
    $(lineItem.lossesAndAdjustments).each(function(index, lossAndAdjustmentObject) {
      var quantity = parseInt(lossAndAdjustmentObject.quantity);
      updateTotalLossesAndAdjustment(quantity, lossAndAdjustmentObject.type.additive, lineItem);
    });
  };

  $scope.removeLossAndAdjustment = function (lineItem, lossAndAdjustmentToDelete) {
    lineItem.lossesAndAdjustments = $.grep(lineItem.lossesAndAdjustments, function(lossAndAdjustmentObj) {
      return lossAndAdjustmentObj != lossAndAdjustmentToDelete;
    });
    var quantity = parseInt(lossAndAdjustmentToDelete.quantity);
    updateTotalLossesAndAdjustment(quantity, !lossAndAdjustmentToDelete.type.additive, lineItem);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
  };

  $scope.addLossAndAdjustment = function (lineItem, newLossAndAdjustment) {
    var lossAndAdjustment = {"type":newLossAndAdjustment.type, "quantity":newLossAndAdjustment.quantity};
    newLossAndAdjustment.type = undefined;
    newLossAndAdjustment.quantity = undefined;
    lineItem.lossesAndAdjustments.push(lossAndAdjustment);
    var quantity = parseInt(lossAndAdjustment.quantity);
    updateTotalLossesAndAdjustment(quantity, lossAndAdjustment.type.additive, lineItem);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
  };
}

