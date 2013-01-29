function CreateRnrController($scope, ReferenceData, ProgramRnRColumnList, $location, FacilityApprovedProducts, Requisition, Requisitions, RequisitionLineItem, $routeParams, LossesAndAdjustmentsReferenceData, $rootScope) {

  $scope.lossesAndAdjustmentsModal = [];
  $scope.rnrLineItems = [];
  $rootScope.fixToolBar();
  if (!$scope.$parent.rnr) {
    Requisition.get({facilityId:$routeParams.facility, programId:$routeParams.program, periodId:$routeParams.period},
      function (data) {
        if (data.rnr) {
          $scope.rnr = data.rnr;
          $scope.formDisabled = isFormDisabled();
          populateRnrLineItems($scope.rnr);
        } else {
          $scope.$parent.error = "Requisition does not exist. Please initiate.";
          $location.path($scope.$parent.sourceUrl);
        }
      }, {});
  } else {
    $scope.formDisabled = isFormDisabled();
    populateRnrLineItems($scope.$parent.rnr);
  }

  function populateRnrLineItems(rnr) {
      $(rnr.lineItems).each(function (i, lineItem) {
        lineItem.cost = parseFloat((lineItem.packsToShip * lineItem.price).toFixed(2)) || 0;
        if(lineItem.lossesAndAdjustments == undefined) lineItem.lossesAndAdjustments = [];
        var rnrLineItem = new RnrLineItem(lineItem);
        jQuery.extend(true, lineItem, rnrLineItem);
        $scope.rnrLineItems.push(lineItem);
      });
    }

  FacilityApprovedProducts.get({facilityId:$routeParams.facility, programId:$routeParams.program},
    function (data) {
      $scope.nonFullSupplyProducts = data.nonFullSupplyProducts;
    }, function (data) {
    });

  ReferenceData.get({}, function (data) {
    $scope.currency = data.currency;
  }, {});

  LossesAndAdjustmentsReferenceData.get({}, function (data) {
    $scope.allTypes = data.lossAdjustmentTypes;
  }, {});

  ProgramRnRColumnList.get({programId:$routeParams.program}, function (data) {
    function resetFullSupplyItemsCostIfNull(rnr) {
      if (rnr == null) return;
      if (rnr.fullSupplyItemsSubmittedCost == null)
        rnr.fullSupplyItemsSubmittedCost = 0;
    }

        if (data.rnrColumnList.length > 0) {
            $scope.programRnRColumnList = data.rnrColumnList;
            resetFullSupplyItemsCostIfNull($scope.$parent.rnr);
        } else {
            $scope.$parent.error = "Please contact Admin to define R&R template for this program";
            $location.path($scope.$parent.sourceUrl);
        }
    }, function () {
        $location.path($scope.$parent.sourceUrl);
    });

  $scope.saveRnr = function () {
    $scope.submitError = "";
    $scope.inputClass = "";
    $scope.submitMessage = "";
    if ($scope.saveRnrForm.$error.rnrError) {
      $scope.error = "Please correct errors before saving.";
      $scope.message = "";
      return;
    }

    Requisitions.update({id:$scope.rnr.id, operation:"save"},
      $scope.rnr, function (data) {
        $scope.message = data.success;
        $scope.error = "";
      }, function (data) {
        $scope.error = data.error;
        $scope.message = "";
      });
  };

  $scope.highlightRequired = function (value) {
    if ($scope.inputClass == 'required' && (isUndefined(value) || value.trim().length == 0)) {
      return "required-error";
    }
    return null;
  };

  $scope.highlightRequiredFieldInModal = function (value) {
    if (isUndefined(value)) return "required-error";
    return null;
  };

  $scope.highlightWarning = function (value, index) {
    if ((isUndefined(value) || value.trim().length == 0 || value == false) && $scope.inputClass == 'required' && $scope.rnrLineItems[index].quantityRequested) {
      return "warning-error";
    }
    return null;
  };

  function valid() {
    if ($scope.saveRnrForm.$error.rnrError) {
      $scope.submitError = "Please correct the errors on the R&R form before submitting";
      $scope.submitMessage = "";
      return false;
    }
    if ($scope.saveRnrForm.$error.required) {
      $scope.saveRnr();
      $scope.inputClass = "required";
      $scope.submitMessage = "";
      $scope.submitError = 'Please complete the highlighted fields on the R&R form before submitting';
      return false;
    }
    if (!formulaValid()) {
      $scope.saveRnr();
      $scope.submitError = "Please correct the errors on the R&R form before submitting";
      $scope.submitMessage = "";
      return false;
    }
    return true;
  }

  function isFormDisabled() {
    if ($scope.rnr || $scope.$parent.rnr) {
      if ($scope.rnr.status == 'AUTHORIZED') return true;
      if (($scope.rnr.status == 'SUBMITTED' && !$rootScope.hasPermission('AUTHORIZE_REQUISITION')) || ($scope.rnr.status == 'INITIATED' && !$rootScope.hasPermission('CREATE_REQUISITION'))) return true;
    }
    return false;
  }

  $scope.submitRnr = function () {
    if (!valid()) return;

    Requisitions.update({id:$scope.rnr.id, operation:"submit"},
      $scope.rnr, function (data) {
        $scope.rnr.status = "SUBMITTED";
        $scope.formDisabled = !$rootScope.hasPermission('AUTHORIZE_REQUISITION');
        $scope.submitMessage = data.success;
        $scope.submitError = "";
      }, function (data) {
        $scope.submitError = data.data.error;
      });
  };

  $scope.authorizeRnr = function () {
    if (!valid()) return;

    Requisitions.update({id:$scope.rnr.id, operation:"authorize"},
      $scope.rnr, function (data) {
        $scope.rnr.status = "AUTHORIZED";
        $scope.formDisabled = true;
        $scope.submitMessage = data.success;
        $scope.submitError = "";
      }, function (data) {
        $scope.submitError = data.data.error;
      });
  };

  $scope.getId = function (prefix, parent, isLossAdjustment) {
    if (isLossAdjustment != null && isLossAdjustment != isUndefined && isLossAdjustment) {
      return prefix + "_" + parent.$parent.$parent.$index + "_" + parent.$parent.$parent.$parent.$index;
    }
    return prefix + "_" + parent.$parent.$parent.$index;
  };

  $scope.hide = function () {
    return "";
  };

  $scope.saveLossesAndAdjustmentsForRnRLineItem = function (rnrLineItem, rnr, programRnrColumnList) {
    if (!isValidLossesAndAdjustments(rnrLineItem)) return;

    rnrLineItem.fill(rnr, programRnrColumnList);
    $scope.lossesAndAdjustmentsModal[rnrLineItem.id] = false;
  };

  $scope.resetModalError = function () {
    $scope.modalError = '';
  };

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem, isNonFullSupply) {
    if(isNonFullSupply) {
      return;
    }
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
  };

  $scope.showAddNonFullSupplyModal= function () {
    $scope.nonFullSupplyProductsModal = true;
    $scope.newNonFullSupply.quantityRequested.required = true;
    $scope.newNonFullSupply.reasonForRequestedQuantity = true;
  };

  $scope.removeLossAndAdjustment = function (lineItem, lossAndAdjustmentToDelete) {
    lineItem.removeLossAndAdjustment(lossAndAdjustmentToDelete);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.resetModalError();
  };

  $scope.addLossAndAdjustment = function (lineItem, newLossAndAdjustment) {
    lineItem.addLossAndAdjustment(newLossAndAdjustment);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
  };

  function isUndefined(value) {
    return (value == null || value == undefined);
  }

  function formulaValid() {
    var valid = true;
    $scope.rnrLineItems.forEach(function (lineItem) {
      if (lineItem.arithmeticallyInvalid($scope.programRnRColumnList) || lineItem.stockInHand < 0 || lineItem.quantityDispensed < 0) {
        valid = false;
      }
    });
    return valid;
  }

  function isValidLossesAndAdjustments(rnrLineItem) {
    if (!isUndefined(rnrLineItem.lossesAndAdjustments)) {
      for (var index in rnrLineItem.lossesAndAdjustments) {
        if (isUndefined(rnrLineItem.lossesAndAdjustments[index].quantity)) {
          $scope.modalError = 'Please correct the highlighted fields before submitting';
          return false;
        }
      }
    }
    $scope.modalError = '';
    return true;
  }

  function updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem) {
    var lossesAndAdjustmentTypesForLineItem = [];
    $(lineItem.lossesAndAdjustments).each(function (index, lineItemLossAndAdjustment) {
      lossesAndAdjustmentTypesForLineItem.push(lineItemLossAndAdjustment.type.name);
    });

    var allTypes = $scope.allTypes;
    $scope.lossesAndAdjustmentTypesToDisplay = $.grep(allTypes, function (lAndATypeObject) {
      return $.inArray(lAndATypeObject.name, lossesAndAdjustmentTypesForLineItem) == -1;
    });
  }

  $scope.getCellErrorClass = function (rnrLineItem, programRnRColumnList) {
    return rnrLineItem.getErrorMessage(programRnRColumnList) ? 'cell-error-highlight' : '';
  };

  $scope.getRowErrorClass = function (rnrLineItem, programRnRColumnList) {
    return $scope.getCellErrorClass(rnrLineItem, programRnRColumnList) ? 'row-error-highlight' : '';
  };


  $scope.isNonFullSupply = function(rnrLineItem){
    return !rnrLineItem.fullSupply;
  }

  $scope.labelForRnrColumn = function (columnName) {
    var label = "";
    $($scope.programRnRColumnList).each(function (index, column) {
      if (column.name == columnName) {
        label = column.label;
        return false;
      }
    });
    return label;
  };

  function populateProductInformation() {
    var product = $scope.facilityApprovedProduct.programProduct.product;
    $scope.newNonFullSupply.productCode = product.code;
    $scope.newNonFullSupply.product = (product.primaryName == null ? "" : (product.primaryName + " ")) +
      (product.form.code == null ? "" : (product.form.code + " ")) +
      (product.strength == null ? "" : (product.strength + " ")) +
      (product.dosageUnit.code == null ? "" : product.dosageUnit.code);
    $scope.newNonFullSupply.dosesPerDispensingUnit = product.dosesPerDispensingUnit;
    $scope.newNonFullSupply.packSize = product.packSize;
    $scope.newNonFullSupply.roundToZero = product.roundToZero;
    $scope.newNonFullSupply.packRoundingThreshold = product.packRoundingThreshold;
    $scope.newNonFullSupply.dispensingUnit = product.dispensingUnit;
    $scope.newNonFullSupply.fullSupply = product.fullSupply;
    $scope.newNonFullSupply.maxMonthsOfStock = $scope.facilityApprovedProduct.maxMonthsOfStock;
    $scope.newNonFullSupply.dosesPerMonth = $scope.facilityApprovedProduct.programProduct.dosesPerMonth;
    $scope.newNonFullSupply.price = $scope.facilityApprovedProduct.programProduct.currentPrice;
  }

  $scope.addNonFullSupplyLineItem = function () {

    if ($scope.saveRnrForm.$error.required.indexOf('newNonFullSupply.quantityRequested') > 0 || $scope.saveRnrForm.$error.required.indexOf('newNonFullSupply.reasonForRequestedQuantity') > 0) {

    } else {
      populateProductInformation();
      jQuery.extend(true, $scope.newNonFullSupply, new RnrLineItem());
      $scope.newNonFullSupply.fill($scope.rnr, $scope.programRnRColumnList);
      saveRnrLineItem();
    }
  };

  function saveRnrLineItem() {

    $scope.newNonFullSupply.rnrId = $scope.rnr.id;
    RequisitionLineItem.save({}, $scope.newNonFullSupply, function (data) {
      $scope.newNonFullSupply = data.newNonFullSupply;
      $scope.rnr.lineItems[$scope.rnr.lineItems.length] = $scope.newNonFullSupply
      jQuery.extend(true, $scope.newNonFullSupply, new RnrLineItem());
      $scope.rnrLineItems.push($scope.newNonFullSupply);
      $scope.nonFullSupplyProducts.splice($scope.nonFullSupplyProducts.indexOf($scope.facilityApprovedProduct), 1);
      $scope.newNonFullSupply = {};
    }, {});
  }
}
