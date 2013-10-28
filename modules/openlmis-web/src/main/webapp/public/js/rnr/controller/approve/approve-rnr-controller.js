/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ApproveRnrController($scope, requisition, Requisitions, rnrColumns, regimenTemplate, $location, pageSize, $routeParams, $dialog, messageService) {
  $scope.visibleTab = $routeParams.supplyType;
  $scope.rnr = new Rnr(requisition, rnrColumns);
  $scope.rnrColumns = rnrColumns;
  $scope.regimenColumns = regimenTemplate ? regimenTemplate.columns : [];
  $scope.currency = messageService.get('label.currency.symbol');
  $scope.pageSize = pageSize;
  $scope.visibleColumns = _.where(rnrColumns, {'visible': true});
  $scope.error = $scope.message = "";
  $scope.regimenCount = $scope.rnr.regimenLineItems.length;

  $scope.errorPages = {};
  $scope.shownErrorPages = [];

  var lineItemMap = {
    'nonFullSupply': $scope.rnr.nonFullSupplyLineItems,
    'fullSupply': $scope.rnr.fullSupplyLineItems,
    'regimen': $scope.rnr.regimenLineItems
  };

  var NON_FULL_SUPPLY = 'nonFullSupply';
  var FULL_SUPPLY = 'fullSupply';
  var REGIMEN = 'regimen';

  $scope.requisitionType = $scope.rnr.emergency ? "requisition.type.emergency" : "requisition.type.regular";

  $scope.goToPage = function (page, event) {
    angular.element(event.target).parents(".dropdown").click();
    $location.search('page', page);
  };

  $scope.highlightRequired = function (value) {
    if ($scope.approvedQuantityRequiredFlag && (isUndefined(value))) {
      return "required-error";
    }
    return null;
  };

  $scope.showCategory = function (index) {
    return !((index > 0 ) &&
      ($scope.page[$scope.visibleTab][index].productCategory === $scope.page[$scope.visibleTab][index - 1].productCategory));
  };

  function updateShownErrorPages() {
    $scope.shownErrorPages = $scope.visibleTab === FULL_SUPPLY ? $scope.errorPages.fullSupply : $scope.errorPages.nonFullSupply;
    $scope.errorPagesCount = !isUndefined($scope.shownErrorPages) ? $scope.shownErrorPages.length : null;
  }

  $scope.saveRnr = function (preventMessage) {
    if (isUndefined($scope.approvalForm) || !$scope.approvalForm.$dirty) {
      return;
    }
    resetFlags();
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id: $scope.rnr.id, operation: "save"},
        rnr, function (data) {
          if (preventMessage === true) return;
          $scope.message = data.success;
          $scope.error = "";
          setTimeout(fadeSaveMessage, 3000);
        }, function (data) {
          $scope.error = data.data.error;
          $scope.message = "";
        });
    $scope.approvalForm.$setPristine();
  };

  var refreshGrid = function () {
    $scope.saveRnr();
    $scope.page = {fullSupply: [], nonFullSupply: [], regimen: []};
    $scope.visibleTab = ($routeParams.supplyType === NON_FULL_SUPPLY) ? NON_FULL_SUPPLY : ($routeParams.supplyType === REGIMEN && $scope.regimenCount) ? REGIMEN : FULL_SUPPLY;

    $location.search('supplyType', $scope.visibleTab);
    updateShownErrorPages();

    if ($scope.visibleTab != REGIMEN) {
      $scope.numberOfPages = Math.ceil(lineItemMap[$scope.visibleTab].length / $scope.pageSize) || 1;
    } else {
      $scope.numberOfPages = 1;
    }
    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;

    $scope.page[$scope.visibleTab] = lineItemMap[$scope.visibleTab].slice($scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);
  };

  $scope.$on('$routeUpdate', refreshGrid);

  refreshGrid();

  $scope.$watch("currentPage", function () {
    $location.search("page", $scope.currentPage);
  });

  $scope.switchSupplyType = function (supplyType) {
    if (supplyType === $scope.visibleTab)
      return;
    $location.search({page: 1, supplyType: supplyType});
  };

  $scope.getId = function (prefix, parent) {
    return prefix + "_" + parent.$parent.$index;
  };

  function removeExtraDataForPostFromRnr() {
    var rnr = _.pick(this, 'id', 'fullSupplyLineItems', 'nonFullSupplyLineItems');
    if (!$scope.page[$scope.visibleTab].length) return rnr;

    rnr[$scope.visibleTab+'LineItems'] = _.map($scope.page[$scope.visibleTab], function(lineItem) {
      return lineItem.reduceForApproval();
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
    $scope.errorPages = {fullSupply: [], nonFullSupply: []};
    updateShownErrorPages();
  }

  $scope.checkErrorOnPage = function (page) {
    return $scope.visibleTab === NON_FULL_SUPPLY ?
      _.contains($scope.errorPages.nonFullSupply, page) : _.contains($scope.errorPages.fullSupply, page);
  };

  $scope.dialogCloseCallback = function (result) {
    if (result) {
      angular.element('.toggleFullScreen').show();
      $scope.fullScreen = false;
      approveValidatedRnr();
    }
  };

  var showConfirmModal = function () {
    var options = {
      id: "confirmDialog",
      header: messageService.get("label.confirm.action"),
      body: messageService.get("msg.question.confirmation")
    };
    OpenLmisDialog.newDialog(options, $scope.dialogCloseCallback, $dialog, messageService);
  };

  $scope.approveRnr = function () {
    $scope.approvedQuantityRequiredFlag = true;
    resetFlags();
    resetErrorPages();
    $scope.saveRnr(true);
    var error = validateAndSetErrorClass();
    if (error) {
      setErrorPages();
      $scope.error = error;
      $scope.message = '';
      return;
    }
    showConfirmModal();
  };

  function resetFlags() {
    $scope.error = "";
    $scope.message = "";
  }

  var approveValidatedRnr = function () {
    Requisitions.update({id: $scope.rnr.id, operation: "approve"}, {}, function (data) {
      $scope.$parent.message = data.success;
      $scope.error = "";
      $location.path("rnr-for-approval");
    }, function (data) {
      $scope.error = data.data.error;
      $scope.message = "";
    });
  };

}

ApproveRnrController.resolve = {

  requisition: function ($q, $timeout, Requisitions, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      Requisitions.get({id: $route.current.params.rnr}, function (data) {
        deferred.resolve(data.rnr);
      }, {});
    }, 100);
    return deferred.promise;
  },

  rnrColumns: function ($q, $timeout, ProgramRnRColumnList, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRnRColumnList.get({programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.rnrColumnList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  pageSize: function ($q, $timeout, LineItemPageSize) {
    var deferred = $q.defer();
    $timeout(function () {
      LineItemPageSize.get({}, function (data) {
        deferred.resolve(data.pageSize);
      }, {});
    }, 100);
    return deferred.promise;
  },

  regimenTemplate: function ($q, $timeout, $route, ProgramRegimenTemplate) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRegimenTemplate.get({programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.template);
      }, {});
    }, 100);
    return deferred.promise;
  }
};
