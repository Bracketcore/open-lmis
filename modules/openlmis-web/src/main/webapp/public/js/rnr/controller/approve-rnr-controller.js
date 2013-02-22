function ApproveRnrController($scope, RequisitionForApprovalById, Requisitions, ProgramRnRColumnList, $location, ReferenceData, $routeParams, LossesAndAdjustmentsReferenceData) {
  $scope.error = "";
  $scope.message = "";

  $scope.fullSupplyLink = "#rnr-for-approval/" + $routeParams.rnr + '/' + $routeParams.facility + '/' + $routeParams.program + '/' + 'full-supply';

  $scope.nonFullSupplyLink = "#rnr-for-approval/" + $routeParams.rnr + '/' + $routeParams.facility + '/' + $routeParams.program + '/' + 'non-full-supply';

  $scope.showNonFullSupply = ($routeParams.supplyType == 'non-full-supply');

  function populateRnrLineItems() {
    var lineItemsJson = $scope.rnr.lineItems;
    $scope.rnr.lineItems = [];
    $(lineItemsJson).each(function (i, lineItem) {
      var rnrLineItem = new RnrLineItem(lineItem, $scope.rnr, $scope.$parent.programRnrColumnList);

      rnrLineItem.updateCostWithApprovedQuantity();
      $scope.rnr.lineItems.push(rnrLineItem);
    });

    var nonFullSupplyLineItemsJson = $scope.rnr.nonFullSupplyLineItems;
    $scope.rnr.nonFullSupplyLineItems = [];
    $(nonFullSupplyLineItemsJson).each(function (i, lineItem) {
      var rnrLineItem = new RnrLineItem(lineItem, $scope.rnr, $scope.$parent.programRnrColumnList);

      rnrLineItem.updateCostWithApprovedQuantity();
      $scope.rnr.nonFullSupplyLineItems.push(rnrLineItem);
    });
  }

  $scope.lossesAndAdjustmentsModal = [];
  LossesAndAdjustmentsReferenceData.get({}, function (data) {
    $scope.allTypes = data.lossAdjustmentTypes;
  }, {});

  $scope.closeLossesAndAdjustmentsForRnRLineItem = function (rnrLineItem) {
    $scope.lossesAndAdjustmentsModal[rnrLineItem.id] = false;
  };

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
  };

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

  function lossesAndAdjustmentsTemplate() {
    return '<div id="lossesAndAdjustments" modal="lossesAndAdjustmentsModal[row.entity.id]">' +
              '<div class="modal-header"><h3>Losses And Adjustments</h3></div>' +
              '<div class="modal-body">' +
                '<hr ng-show="row.entity.lossesAndAdjustments.length > 0"/>' +
                '<div class="adjustment-list" ng-show="row.entity.lossesAndAdjustments.length > 0">' +
                  '<ul>' +
                    '<li ng-repeat="oneLossAndAdjustment in row.entity.lossesAndAdjustments" class="clearfix">' +
                      '<span class="tpl-adjustment-type" ng-bind="oneLossAndAdjustment.type.description"></span>' +
                      '<span class="tpl-adjustment-qty" ng-bind="oneLossAndAdjustment.quantity"></span>' +
                    '</li>' +
                  '</ul>' +
                '</div>' +
                '<div class="adjustment-total clearfix alert alert-warning" ng-show="row.entity.lossesAndAdjustments.length > 0">' +
                  '<span class="pull-left">Total</span> ' +
                  '<span ng-bind="row.entity.totalLossesAndAdjustments"></span>' +
                '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
                '<input type="button" class="btn btn-success save-button" ng-click="closeLossesAndAdjustmentsForRnRLineItem(row.entity)" value="Close"/>' +
              '</div>' +
           '</div>' +
           '<div ng-class="highlightNestedFieldsWithError(row.entity[column.name], \'quantity\')">' +
              '<a ng-click="showLossesAndAdjustmentModalForLineItem(row.entity)" class="rnr-adjustment">' +
               '<span class="adjustment-value" ng-bind="row.entity.totalLossesAndAdjustments"></span>' +
              '</a>' +
           '</div>';
  }

  function currencyTemplate(value) {
    return '<span  class = "cell-text" ng-show = "showCurrencySymbol(' + value + ')"  ng-bind="currency"></span >&nbsp; &nbsp;<span ng-bind = "' + value + '" class = "cell-text" ></span >'
  }

  function freeTextCellTemplate(field, value) {
    return '<div><input maxlength="250" name="' + field + '" ng-model="' + value + '"/></div>';
  }

  function positiveIntegerCellTemplate(field, value) {
    return '<div><ng-form name="positiveIntegerForm"  > <input ui-event="{blur : \'row.entity.updateCostWithApprovedQuantity()\'}" ng-class="{\'required-error\': approvedQuantityRequiredFlag && positiveIntegerForm.' + field + '.$error.required}" ' +
      '  ng-required="true" maxlength="8"  name=' + field + ' ng-model=' + value + '  ng-change="validatePositiveInteger(positiveIntegerForm.' + field + '.$error,' + value + ')" />' +
      '<span class="rnr-form-error" id=' + field + ' ng-show="positiveIntegerForm.' + field + '.$error.pattern" ng-class="{\'required-error\': approvedQuantityInvalidFlag && positiveIntegerForm.' + field + '.$error.positiveInteger}">Please Enter Numeric value</span></ng-form></div>';
  }

  function isPositiveNumber(value) {
    var INTEGER_REGEXP = /^\d*$/;
    return INTEGER_REGEXP.test(value);

  }

  function isUndefined(value) {
    return (value == null || value == undefined);
  }

  function prepareColumnDefinitions() {
    var columnDefinitions = [];
    var visibleColumns = _.where($scope.programRnrColumnList, {'visible':true});
    if (visibleColumns.length > 0) {
      $(visibleColumns).each(function (i, column) {
        if (column.name == "cost" || column.name == "price") {
          columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:currencyTemplate('row.entity.' + column.name)});
          return;
        }
        if (column.name == "lossesAndAdjustments") {
          columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:lossesAndAdjustmentsTemplate()});
          return;
        }
        if (column.name == "quantityApproved") {
          columnDefinitions.push({field:column.name, displayName:column.label, width:140, cellTemplate:positiveIntegerCellTemplate(column.name, 'row.entity.quantityApproved')});
          return;
        }
        if (column.name == "remarks") {
          columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:freeTextCellTemplate(column.name, 'row.entity.remarks')});
          return;
        }
        columnDefinitions.push({field:column.name, displayName:column.label});
      });
      $scope.$parent.columnDefinitions = columnDefinitions;
    } else {
      $scope.$parent.error = "Please contact Admin to define R&R template for this program";
    }
  }

  if ($scope.$parent.rnr == undefined || $scope.$parent.rnr.id != $routeParams.rnr) {
    $scope.$parent.programRnrColumnList = [];
    $scope.$parent.columnDefinitions = [];
    RequisitionForApprovalById.get({id:$routeParams.rnr},
      function (data) {
        $scope.$parent.rnr = data.rnr;
        populateRnrLineItems();
      }, {}
    );
    ProgramRnRColumnList.get({programId:$routeParams.program}, function (data) {
      $scope.$parent.programRnrColumnList = data.rnrColumnList;
      prepareColumnDefinitions();
    }, {});

    $scope.$parent.fullSupplyGrid = { data:'rnr.lineItems',
        canSelectRows:false,
        displayFooter:false,
        displaySelectionCheckbox:false,
        showColumnMenu:false,
        showFilter:false,
        rowHeight:44,
        enableSorting:false,
        columnDefs:'columnDefinitions'
      };

      $scope.$parent.nonFullSupplyGrid = { data:'rnr.nonFullSupplyLineItems',
        canSelectRows:false,
        displayFooter:false,
        displaySelectionCheckbox:false,
        showColumnMenu:false,
        showFilter:false,
        rowHeight:44,
        enableSorting:false,
        columnDefs:'columnDefinitions'
      };

  }

  ReferenceData.get({}, function (data) {
    $scope.currency = data.currency;
  }, {});

  $scope.validatePositiveInteger = function (error, value) {
    if (value == undefined) {
      error.positiveInteger = false;
      return
    }
    error.pattern = !isPositiveNumber(value);
  };


  $scope.totalCost = function () {
    if (!$scope.rnr) return;
    return parseFloat(parseFloat($scope.rnr.fullSupplyItemsSubmittedCost) + parseFloat($scope.rnr.nonFullSupplyItemsSubmittedCost)).toFixed(2);
  };

  function removeExtraDataForPostFromRnr() {
    var rnr = {"id":$scope.rnr.id, "lineItems":[], "nonFullSupplyLineItems":[]};

    _.each($scope.rnr.lineItems, function (lineItem) {
      rnr.lineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    _.each($scope.rnr.nonFullSupplyLineItems, function (lineItem) {
      rnr.nonFullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    return rnr;
  }

  $scope.saveRnr = function () {
    $scope.approvedQuantityInvalidFlag = false;
    $($scope.lineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved != undefined && !isPositiveNumber(lineItem.quantityApproved)) {
        $scope.approvedQuantityInvalidFlag = true;
        return false;
      }
    });
    if ($scope.approvedQuantityInvalidFlag) {
      $scope.error = "Please correct errors before saving.";
      $scope.message = "";
      return;
    }
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"save"},
      rnr, function (data) {
        $scope.message = data.success;
        $scope.error = "";
      }, function (data) {
        $scope.error = data.error;
        $scope.message = "";
      });
  };

  $scope.approveRnr = function () {
    $scope.approvedQuantityRequiredFlag = false;
    $($scope.rnr.lineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved == undefined || !isPositiveNumber(lineItem.quantityApproved)) {
        $scope.approvedQuantityRequiredFlag = true;
        return false;
      }
    });
    $($scope.rnr.nonFullSupplyLineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved == undefined || !isPositiveNumber(lineItem.quantityApproved)) {
        $scope.approvedQuantityRequiredFlag = true;
        return false;
      }
    });
    if ($scope.approvedQuantityRequiredFlag) {
      $scope.error = "Please complete the R&R form before approving";
      $scope.message = "";
      return;
    }
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"approve"},
      rnr, function (data) {
        $scope.$parent.message = data.success;
        $scope.error = "";
        $location.path("rnr-for-approval");
      }, function (data) {
        $scope.error = data.error;
        $scope.message = "";
      });
  }


  $scope.showCurrencySymbol = function (value) {
    if (value != 0 && (isUndefined(value) || value.length == 0 || value == false)) {
      return "";
    }
    return "defined";
  };

  $scope.periodDisplayName = function () {
    if (!$scope.rnr) return;

    var startDate = new Date($scope.rnr.period.startDate);
    var endDate = new Date($scope.rnr.period.endDate);

    return utils.getFormattedDate(startDate) + ' - ' + utils.getFormattedDate(endDate);
  };
}
