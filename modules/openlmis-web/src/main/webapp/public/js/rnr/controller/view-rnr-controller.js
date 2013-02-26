function ViewRnrController($scope, $routeParams, ProgramRnRColumnList, RequisitionById, ReferenceData) {

  $scope.lossesAndAdjustmentsModal = [];
  $scope.fullSupplyLink = "#/requisition/" + $routeParams.id + '/program/' + $routeParams.programId + '/full-supply';

  $scope.nonFullSupplyLink = "#/requisition/" + $routeParams.id + '/program/' + $routeParams.programId + '/non-full-supply';

  $scope.showNonFullSupply = !($routeParams.supplyType == 'full-supply');

  $scope.gridLineItems = [];
  $scope.columnDefs = [];


  if (!$scope.rnr || $scope.rnr.id != $routeParams.id || $scope.rnr.status != $scope.rnrStatus) {
    RequisitionById.get({id:$routeParams.id}, function (data) {
      $scope.$parent.rnr = data.rnr;
      populateRnrLineItems();
      fillGrid();
      getTemplate();
    });
  } else {
    populateRnrLineItems();
    fillGrid();
    getTemplate();
  }

  if (!$scope.currency) {
    ReferenceData.get({}, function (data) {
      $scope.$parent.currency = data.currency;
    });
  }

  function getTemplate() {
    if (!$scope.rnrColumns) {
      $scope.$parent.rnrColumns = [];
      ProgramRnRColumnList.get({programId:$routeParams.programId}, function (data) {
        $scope.$parent.rnrColumns = data.rnrColumnList;
        prepareColumnDefs();
      });
    } else {
      prepareColumnDefs();
    }
  }



  function prepareColumnDefs() {
    $scope.columnDefs = [];
    $($scope.rnrColumns).each(function (index, column) {
      if (!column.visible) return;

      if (column.name == 'lossesAndAdjustments') {
        if ($scope.showNonFullSupply) {
          $scope.columnDefs.push({field:'totalLossesAndAdjustments', displayName:column.label});
        } else {
          $scope.columnDefs.push({field:column.name, displayName:column.label, cellTemplate:lossesAndAdjustmentsTemplate()});
        }
        return;
      }
      if (column.name == "cost" || column.name == "price") {
        $scope.columnDefs.push({field:column.name, displayName:column.label, cellTemplate:currencyTemplate('COL_FIELD')});
        return;
      }
      if ($scope.rnr.status != 'APPROVED' && column.name == 'quantityApproved') return;

      $scope.columnDefs.push({field:column.name, displayName:column.label});
    });
  }

  function fillGrid() {
    $scope.gridLineItems = $scope.showNonFullSupply ? $scope.rnr.nonFullSupplyLineItems : $scope.rnr.lineItems;
  }

  $scope.rnrGrid = {
    data:'gridLineItems',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    showColumnMenu:false,
    showFilter:false,
    rowHeight:44,
    enableSorting:false,
    columnDefs:'columnDefs'
  };


  function currencyTemplate(value) {
    return '<span  class = "cell-text" ng-show = "showCurrencySymbol(' + value + ')"  ng-bind="currency"></span >&nbsp; &nbsp;<span ng-bind = "' + value + '" class = "cell-text" ></span >'
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
      '<input type="button" class="btn btn-success save-button" style="width: 75px" ng-click="closeLossesAndAdjustmentsForRnRLineItem(row.entity)" value="Close"/>' +
      '</div>' +
      '</div>' +
      '<div>' +
      '<a ng-click="showLossesAndAdjustmentModalForLineItem(row.entity)" class="rnr-adjustment">' +
      '<span class="adjustment-value" ng-bind="row.entity.totalLossesAndAdjustments"></span>' +
      '</a>' +
      '</div>';
  }

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

  $scope.totalCost = function () {
    if (!$scope.rnr) return;
    return parseFloat(parseFloat($scope.rnr.fullSupplyItemsSubmittedCost) + parseFloat($scope.rnr.nonFullSupplyItemsSubmittedCost)).toFixed(2);
  };


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

  $scope.closeLossesAndAdjustmentsForRnRLineItem = function (rnrLineItem) {
    $scope.lossesAndAdjustmentsModal[rnrLineItem.id] = false;
  };

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
  };
}