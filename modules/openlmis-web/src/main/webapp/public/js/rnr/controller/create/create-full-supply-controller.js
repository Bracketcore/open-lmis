/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function CreateFullSupplyController($scope, messageService) {
  $scope.currentRnrLineItem = undefined;

  $scope.getId = function (prefix, parent) {
    return prefix + "_" + parent.$parent.$parent.$index;
  };

  $scope.saveLossesAndAdjustmentsForRnRLineItem = function () {
    $scope.modalError = '';

    if (!$scope.currentRnrLineItem.validateLossesAndAdjustments()) {
      $scope.modalError = messageService.get('error.correct.highlighted');
      return;
    }

    $scope.currentRnrLineItem.reEvaluateTotalLossesAndAdjustments();
    $scope.clearAndCloseLossesAndAdjustmentModal();
  };

  $scope.clearAndCloseLossesAndAdjustmentModal = function () {
    $scope.lossAndAdjustment = undefined;
    $scope.lossesAndAdjustmentsModal = false;
  };

  $scope.resetModalErrorAndSetFormDirty = function () {
    $scope.modalError = '';
    $scope.saveRnrForm.$dirty = true;
  };

  $scope.showLossesAndAdjustments = function (lineItem) {
    $scope.currentRnrLineItem = lineItem;
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.lossesAndAdjustmentsModal = true;
  };

  $scope.removeLossAndAdjustment = function (lossAndAdjustmentToDelete) {
    $scope.currentRnrLineItem.removeLossAndAdjustment(lossAndAdjustmentToDelete);
    updateLossesAndAdjustmentTypesToDisplayForLineItem($scope.currentRnrLineItem);
    $scope.resetModalErrorAndSetFormDirty();
  };

  $scope.addLossAndAdjustment = function (newLossAndAdjustment) {
    $scope.currentRnrLineItem.addLossAndAdjustment(newLossAndAdjustment);
    updateLossesAndAdjustmentTypesToDisplayForLineItem($scope.currentRnrLineItem);
    $scope.saveRnrForm.$dirty = true;
  };

  function updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem) {
    var lossesAndAdjustmentTypesForLineItem = _.pluck(_.pluck(lineItem.lossesAndAdjustments, 'type'), 'name');

    $scope.lossesAndAdjustmentTypesToDisplay = $.grep($scope.allTypes, function (lAndATypeObject) {
      return $.inArray(lAndATypeObject.name, lossesAndAdjustmentTypesForLineItem) == -1;
    });
  }
}
