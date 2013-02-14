function RequisitionFullSupplyController($scope, LossesAndAdjustmentsReferenceData) {
  $scope.lossesAndAdjustmentsModal = [];

  LossesAndAdjustmentsReferenceData.get({}, function (data) {
    $scope.allTypes = data.lossAdjustmentTypes;
  }, function () {
  });

  $scope.getId = function (prefix, parent, isLossAdjustment) {
    if (isLossAdjustment != null && isLossAdjustment != isUndefined && isLossAdjustment) {
      return prefix + "_" + parent.$parent.$parent.$index + "_" + parent.$parent.$parent.$parent.$index;
    }
    return prefix + "_" + parent.$parent.$parent.$index;
  };

  // TODO: Push this method to rnr-line-item
  $scope.saveLossesAndAdjustmentsForRnRLineItem = function (rnrLineItem) {
    if (!isValidLossesAndAdjustments(rnrLineItem)) return;

    rnrLineItem.reEvaluateTotalLossesAndAdjustments();
    $scope.lossesAndAdjustmentsModal[rnrLineItem.id] = false;
  };

  $scope.resetModalError = function () {
    $scope.modalError = '';
  };

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
  };

  // TODO: Push this method to rnr-line-item
  $scope.removeLossAndAdjustment = function (lineItem, lossAndAdjustmentToDelete) {
    lineItem.removeLossAndAdjustment(lossAndAdjustmentToDelete);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.resetModalError();
  };

  // TODO: Push this method to rnr-line-item
  $scope.addLossAndAdjustment = function (lineItem, newLossAndAdjustment) {
    lineItem.addLossAndAdjustment(newLossAndAdjustment);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
  };

  // TODO: Push this method to rnr-line-item
  function isValidLossesAndAdjustments(rnrLineItem) {
    $scope.modalError = '';
    if (isUndefined(rnrLineItem.lossesAndAdjustments)) return true;

    for (var index in rnrLineItem.lossesAndAdjustments) {
      if (isUndefined(rnrLineItem.lossesAndAdjustments[index].quantity)) {
        $scope.modalError = 'Please correct the highlighted fields before submitting';
        return false;
      }
    }
    return true;
  }

  function updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem) {
    var lossesAndAdjustmentTypesForLineItem = _.pluck(_.pluck(lineItem.lossesAndAdjustments, 'type'), 'name');

    $scope.lossesAndAdjustmentTypesToDisplay = $.grep($scope.allTypes, function (lAndATypeObject) {
      return $.inArray(lAndATypeObject.name, lossesAndAdjustmentTypesForLineItem) == -1;
    });
  }
}
