/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ApproveRnrController($scope, requisition, Requisitions, rnrColumns, $location, currency, $routeParams, $timeout, $rootScope) {

  $scope.rnr = new Rnr(requisition, rnrColumns);
  $scope.rnrColumns = rnrColumns;
  $scope.currency = currency;

  $scope.error = "";
  $scope.message = "";

  $scope.lossesAndAdjustmentsModal = [];
  $scope.pageLineItems = [];
  $scope.columnDefinitions = [];
  $scope.showPositiveIntegerError = [];
  $scope.errorPages = {};
  $scope.shownErrorPages = [];

  $scope.isDirty = false;

  $scope.goToPage = function (page, event) {
    angular.element(event.target).parents(".dropdown").click();
    $location.search('page', page);
  };

  function updateSupplyTypeForGrid() {
    $scope.showNonFullSupply = !!($routeParams.supplyType == 'non-full-supply');
  }

  $scope.rowToggle = function (row) {
    if (row.collapsed) {
      row.toggleExpand();
    }
  };

    $scope.$on('ngGridEventRows', function () {
      $timeout(function () {
        $(angular.element('.ngAggregate')).each(function (i, aggregate) {
          aggregate.click();
        });
      });
    });

  function prepareColumnDefinitions() {
    var columnDefinitions = [
      {field:'productCategory', displayName:'Product Category', width:0}
    ];
    var visibleColumns = _.where($scope.rnrColumns, {'visible':true});
    if (visibleColumns.length > 0) {
      $(visibleColumns).each(function (i, column) {
        switch (column.name) {
          case 'price':
          case 'cost' :
            columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:currencyTemplate('row.entity.' + column.name)});
            break;
          case 'lossesAndAdjustments' :
            columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:lossesAndAdjustmentsTemplate()});
            break;
          case 'quantityApproved' :
            columnDefinitions.push({field:column.name, displayName:column.label, width:140, cellTemplate:positiveIntegerCellTemplate(column.name, 'row.entity.quantityApproved')});
            break;
          case 'remarks' :
            columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:freeTextCellTemplate(column.name, 'row.entity.remarks')});
            break;
          default :
            columnDefinitions.push({field:column.name, displayName:column.label});
        }
      });
      $scope.columnDefinitions = columnDefinitions;
    } else {
      $scope.$parent.error = "Please contact Admin to define R&R template for this program";
    }
  }

  function updateShownErrorPages() {
    $scope.shownErrorPages = $scope.showNonFullSupply ? $scope.errorPages.nonFullSupply : $scope.errorPages.fullSupply;
  }

  function fillPagedGridData() {
    updateShownErrorPages();
    var gridLineItems = $scope.showNonFullSupply ? $scope.rnr.nonFullSupplyLineItems : $scope.rnr.fullSupplyLineItems;
    $scope.numberOfPages = Math.ceil(gridLineItems.length / $scope.pageSize) ? Math.ceil(gridLineItems.length / $scope.pageSize) : 1;
    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
    $scope.pageLineItems = gridLineItems.slice(($scope.pageSize * ($scope.currentPage - 1)), $scope.pageSize * $scope.currentPage);
  }

  updateSupplyTypeForGrid();
  fillPagedGridData();
  prepareColumnDefinitions();


  $scope.$watch("currentPage", function () {
    if (!$routeParams.supplyType) $location.search('supplyType', 'full-supply');
    $location.search("page", $scope.currentPage);
  });

  $scope.closeLossesAndAdjustmentsForRnRLineItem = function (rnrLineItem) {
    $scope.lossesAndAdjustmentsModal[rnrLineItem.id] = false;
  };

  $scope.switchSupplyType = function (supplyType) {
    $location.search('page', 1);
    $location.search('supplyType', supplyType);
  };

  $scope.$on('$routeUpdate', function () {
    if (!utils.isValidPage($routeParams.page, $scope.numberOfPages)) {
      $location.search('page', 1);
      return;
    }
    if ($scope.isDirty)
      $scope.saveRnr();
    updateSupplyTypeForGrid();
    fillPagedGridData();
  });

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
  };

  function lossesAndAdjustmentsTemplate() {
    return '<div class="ngCellText" ng-hide="row.entity.fullSupply"><span ng-bind="row.entity.totalLossesAndAdjustments" ></span></div>' +
      '<div id="lossesAndAdjustments" modal="lossesAndAdjustmentsModal[row.entity.id]">' +
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
      '<a ng-click="showLossesAndAdjustmentModalForLineItem(row.entity)" class="rnr-adjustment" ng-show="row.entity.fullSupply">' +
      '<span class="adjustment-value" ng-bind="row.entity.totalLossesAndAdjustments"></span>' +
      '</a>';
  }

  function currencyTemplate(value) {
    return '<div class="ngCellText"><span  class = "cell-text" ng-show = "showCurrencySymbol(' + value + ')"  ng-bind="currency"></span >&nbsp; &nbsp;<span ng-bind = "' + value + '" class = "cell-text" ></span ></div>'
  }

  function freeTextCellTemplate(field, value) {
    return '<div><input maxlength="250" ng-change = \'setDirty()\' name="' + field + '" ng-model="' + value + '"/></div>';
  }

  function positiveIntegerCellTemplate(field, value) {
    return '<div><ng-form name="positiveIntegerForm"> <input ng-change = \'fillPacksToShip(row.entity)\' ' +
      'ui-event="{blur : \'showPositiveIntegerError[row.entity.id] = false\'}"' +
      'ng-class="{\'required-error\': approvedQuantityRequiredFlag && positiveIntegerForm.' + field + '.$error.required}" ' +
      '  ng-required="true" maxlength="8"  name=' + field + ' ng-model=' + value + ' />' +
      '<span class="rnr-form-error" id=' + field + ' ng-show="showPositiveIntegerError[row.entity.id]" >Please Enter Numeric value</span></ng-form></div>';
  }

  function aggregateTemplate() {
    return "<div ng-click=\"rowToggle(row)\" ng-style=\"{'left': row.offsetleft}\" class=\"ngAggregate\">" +
      "    <span class=\"ngAggregateText\">{{row.label CUSTOM_FILTERS}}</span>" +
      "    <div style='display: none;' class=\"{{row.aggClass()}}\"></div>" +
      "</div>" +
      "";
  }

  $scope.rnrGrid = {
    data:'pageLineItems',
    enableRowSelection:false,
    showFooter:false,
    showSelectionCheckbox:false,
    showColumnMenu:false,
    aggregateTemplate:aggregateTemplate(),
    showFilter:false,
    rowHeight:44,
    enableSorting:false,
    enableColumnResize: true,
    enableColumnReordering: false,
    columnDefs:'columnDefinitions',
    groups:['productCategory']
  };

  $scope.setDirty = function () {
    $scope.isDirty = true;
  };

  $scope.fillPacksToShip = function (lineItem) {
    $scope.setDirty();
    if (!isUndefined(lineItem.quantityApproved)) {
      $scope.showPositiveIntegerError[lineItem.id] = !utils.isPositiveNumber(lineItem.quantityApproved);
      lineItem.quantityApproved = utils.parseIntWithBaseTen(lineItem.quantityApproved);
    }

    $scope.rnr.fillPacksToShip(lineItem);
  };


  $scope.totalCost = function () {
    if (!$scope.rnr) return;
    return parseFloat(parseFloat($scope.rnr.fullSupplyItemsSubmittedCost) + parseFloat($scope.rnr.nonFullSupplyItemsSubmittedCost)).toFixed(2);
  };

  function removeExtraDataForPostFromRnr() {
    var rnr = {"id":$scope.rnr.id, "fullSupplyLineItems":[], "nonFullSupplyLineItems":[]};

    _.each($scope.rnr.fullSupplyLineItems, function (lineItem) {
      rnr.fullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    _.each($scope.rnr.nonFullSupplyLineItems, function (lineItem) {
      rnr.nonFullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    return rnr;
  }

  var fadeSaveMessage = function () {
    $scope.$apply(function () {
      angular.element("#saveSuccessMsgDiv").fadeOut('slow', function () {
        $scope.message = '';
      });
    });
  };

  $scope.saveRnr = function (preventMessage) {
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"save"},
      rnr, function (data) {
        if (preventMessage == true) return;
        $scope.message = data.success;
        $scope.error = "";
        setTimeout(fadeSaveMessage, 3000);
      }, function (data) {
        $scope.error = data.error;
        $scope.message = "";
      });
    $scope.isDirty = false;
  };

  function validateAndSetErrorClass() {
    var fullSupplyError = $scope.rnr.validateFullSupplyForApproval();
    var nonFullSupplyError = $scope.rnr.validateNonFullSupplyForApproval();
    $scope.fullSupplyTabError = !!fullSupplyError;
    $scope.nonFullSupplyTabError = !!nonFullSupplyError;

    return fullSupplyError || nonFullSupplyError;
  }

  function setErrorPages() {
    $scope.errorPages = $scope.rnr.getErrorPages($scope.pageSize);
    updateShownErrorPages();
  }

  function resetErrorPages() {
    $scope.errorPages = {fullSupply:[], nonFullSupply:[]};
  }

  $scope.checkErrorOnPage = function (page) {
    return $scope.showNonFullSupply ? _.contains($scope.errorPages.nonFullSupply, page) : _.contains($scope.errorPages.fullSupply, page);
  };

  $scope.approveRnr = function () {
    $scope.approvedQuantityRequiredFlag = true;
    resetErrorPages();
    var error = validateAndSetErrorClass();
    if (error) {
      setErrorPages();
      $scope.saveRnr(true);
      $scope.error = error;
      $scope.message = '';
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
}

ApproveRnrController.resolve = {

  requisition:function ($q, $timeout, RequisitionForApprovalById, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionForApprovalById.get({id:$route.current.params.rnr}, function (data) {
        deferred.resolve(data.rnr);
      }, {});
    }, 100);
    return deferred.promise;
  },

  rnrColumns:function ($q, $timeout, ProgramRnRColumnList, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRnRColumnList.get({programId:$route.current.params.program}, function (data) {
        deferred.resolve(data.rnrColumnList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  currency:function ($q, $timeout, ReferenceData) {
    var deferred = $q.defer();
    $timeout(function () {
      ReferenceData.get({}, function (data) {
        deferred.resolve(data.currency);
      }, {});
    }, 100);
    return deferred.promise;
  }
};

