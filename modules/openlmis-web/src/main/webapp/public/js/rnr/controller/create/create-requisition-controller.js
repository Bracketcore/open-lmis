/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function CreateRequisitionController($scope, requisition, pageSize, rnrColumns, lossesAndAdjustmentsTypes, facilityApprovedProducts, requisitionRights, regimenTemplate, $location, Requisitions, $routeParams, $dialog, messageService) {

  var NON_FULL_SUPPLY = 'non-full-supply';
  var FULL_SUPPLY = 'full-supply';
  var REGIMEN = 'regimen';

  $scope.visibleTab = $routeParams.supplyType;
  $scope.pageSize = pageSize;
  $scope.rnr = new Rnr(requisition, rnrColumns);
  resetCostsIfNull();

  $scope.lossesAndAdjustmentTypes = lossesAndAdjustmentsTypes;
  $scope.facilityApprovedProducts = facilityApprovedProducts;
  $scope.visibleColumns = _.where(rnrColumns, {'visible': true});
  $scope.programRnrColumnList = rnrColumns;
  $scope.requisitionRights = requisitionRights;
  $scope.regimenColumns = regimenTemplate ? regimenTemplate.columns : [];
  $scope.visibleRegimenColumns = _.where($scope.regimenColumns, {'visible': true});
  $scope.addNonFullSupplyLineItemButtonShown = _.findWhere($scope.programRnrColumnList, {'name': 'quantityRequested'});
  $scope.errorPages = {fullSupply: [], nonFullSupply: []};
  $scope.regimenCount = $scope.rnr.regimenLineItems.length;
  $scope.currency = messageService.get('label.currency.symbol');

  resetFlags();

  if ($scope.rnr.emergency) {
    $scope.requisitionType = messageService.get("requisition.type.emergency");
  } else {
    $scope.requisitionType = messageService.get("requisition.type.regular");
  }

  $scope.formDisabled = function () {
    var status = $scope.rnr.status;
    if (status === 'INITIATED' && $scope.hasPermission('CREATE_REQUISITION')) return false;
    return !(status === 'SUBMITTED' && $scope.hasPermission('AUTHORIZE_REQUISITION'));
  }();

  $scope.hasPermission = function (permission) {
    return _.find($scope.requisitionRights, function (right) {
      return right.right === permission;
    });
  };


  $scope.checkErrorOnPage = function (page) {
    return $scope.visibleTab === NON_FULL_SUPPLY ?
      _.contains($scope.errorPages.nonFullSupply, page) :
      $scope.visibleTab === FULL_SUPPLY ? _.contains($scope.errorPages.fullSupply, page) : [];
  };
  if ($scope.programRnrColumnList && $scope.programRnrColumnList.length > 0) {
  } else {
    $scope.error = "error.rnr.template.not.defined";
    $location.path("/init-rnr");
  }

  $scope.currentPage = ($routeParams.page) ? utils.parseIntWithBaseTen($routeParams.page) || 1 : 1;

  $scope.switchSupplyType = function (supplyType) {
    $scope.visibleTab = supplyType;
    $location.search({page: 1, supplyType: supplyType});
  };

  $scope.goToPage = function (page, event) {
    angular.element(event.target).parents(".dropdown").click();
    $location.search('page', page);
  };

  $scope.$watch("currentPage", function () {
    $location.search("page", $scope.currentPage);
  });

  $scope.saveRnr = function (preventMessage) {
    if (!$scope.saveRnrForm.$dirty) {
      return;
    }
    resetFlags();
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id: $scope.rnr.id, operation: "save"}, rnr, function (data) {
      if (preventMessage) return;
      $scope.message = data.success;
      setTimeout(function () {
        $scope.$apply(function () {
          angular.element("#saveSuccessMsgDiv").fadeOut('slow', function () {
            $scope.message = '';
          });
        });
      }, 3000);
      $scope.saveRnrForm.$setPristine();
    }, function (data) {
      if (!preventMessage)
        $scope.error = data.data.error;
    });
  };

  function validateAndSetErrorClass() {
    $scope.inputClass = true;
    var fullSupplyError = $scope.rnr.validateFullSupply();
    var nonFullSupplyError = $scope.rnr.validateNonFullSupply();
    $scope.fullSupplyTabError = !!fullSupplyError;
    $scope.nonFullSupplyTabError = !!nonFullSupplyError;

    if ($scope.rnr.regimenLineItems) validateRegimenLineItems();
    var regimenError;
    if ($scope.regimenLineItemInValid) {
      regimenError = messageService.get("error.rnr.validation");
    }
    return fullSupplyError || nonFullSupplyError || regimenError;
  }

  function setErrorPages() {
    $scope.errorPages = $scope.rnr.getErrorPages($scope.pageSize);
    $scope.fullSupplyErrorPagesCount = $scope.errorPages.fullSupply.length;
    $scope.nonFullSupplyErrorPagesCount = $scope.errorPages.nonFullSupply.length;
  }

  $scope.submitRnr = function () {
    resetFlags();
    resetErrorPages();
    $scope.saveRnr(true);
    var errorMessage = validateAndSetErrorClass();
    if (errorMessage) {
      setErrorPages();
      $scope.submitError = errorMessage;
      return;
    }
    showConfirmModal();

  };

  function validateRegimenLineItems() {
    var setError = false;
    $.each($scope.rnr.regimenLineItems, function (index, regimenLineItem) {
      $.each($scope.visibleRegimenColumns, function (index, regimenColumn) {
        if (regimenColumn.name !== "remarks" && isUndefined(regimenLineItem[regimenColumn.name])) {
          setError = true;
          $scope.regimenLineItemInValid = true;
          return;
        }
      });
    });
    if (!setError) $scope.regimenLineItemInValid = false;
  }

  var submitValidatedRnr = function () {
    Requisitions.update({id: $scope.rnr.id, operation: "submit"},
      {}, function (data) {
        $scope.rnr.status = "SUBMITTED";
        $scope.formDisabled = !$scope.hasPermission('AUTHORIZE_REQUISITION');
        $scope.submitMessage = data.success;
        $scope.saveRnrForm.$setPristine();
      }, function (data) {
        $scope.submitError = data.data.error;
      });
  };

  $scope.dialogCloseCallback = function (result) {
    if (result && $scope.rnr.status === 'INITIATED') {
      submitValidatedRnr();
    }
    if (result && $scope.rnr.status === 'SUBMITTED') {
      authorizeValidatedRnr();
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

  $scope.authorizeRnr = function () {
    resetFlags();
    resetErrorPages();
    $scope.saveRnr(true);
    var errorMessage = validateAndSetErrorClass();
    if (errorMessage) {
      setErrorPages();
      $scope.submitError = errorMessage;
      return;
    }
    showConfirmModal();
  };

  var authorizeValidatedRnr = function () {
    Requisitions.update({id: $scope.rnr.id, operation: "authorize"}, {}, function (data) {
      resetFlags();
      $scope.rnr.status = "AUTHORIZED";
      $scope.formDisabled = true;
      $scope.submitMessage = data.success;
      $scope.saveRnrForm.$setPristine();
    }, function (data) {
      $scope.submitError = data.data.error;
    });
  };

  $scope.hide = function () {
    return "";
  };

  $scope.highlightRequired = function (value) {
    if ($scope.inputClass && (isUndefined(value))) {
      return "required-error";
    }
    return null;
  };

  $scope.highlightRequiredFieldInModal = function (value) {
    if (isUndefined(value)) return "required-error";
    return null;
  };

  $scope.highlightWarningBasedOnField = function (value, field) {
    if ($scope.inputClass && (isUndefined(value) || value === false) && field) {
      return "warning-error";
    }
    return null;
  };

  $scope.highlightWarning = function (value) {
    if ($scope.inputClass && (isUndefined(value) || value === false)) {
      return "warning-error";
    }
    return null;
  };

  $scope.showCategory = function (index) {
    return !((index > 0 ) &&
      ($scope.pageLineItems[index].productCategory === $scope.pageLineItems[index - 1].productCategory));
  };

  $scope.getCellErrorClass = function (rnrLineItem) {
    return (typeof(rnrLineItem.getErrorMessage) != "undefined" && rnrLineItem.getErrorMessage()) ?
      'cell-error-highlight' : '';
  };

  $scope.lineItemErrorMessage = function (rnrLineItem) {
    return messageService.get(rnrLineItem.getErrorMessage());
  };

  $scope.getRowErrorClass = function (rnrLineItem) {
    return $scope.getCellErrorClass(rnrLineItem) ? 'row-error-highlight' : '';
  };

  function resetCostsIfNull() {
    var rnr = $scope.rnr;
    if (rnr === null) return;
    if (!rnr.fullSupplyItemsSubmittedCost) {
      rnr.fullSupplyItemsSubmittedCost = 0;
    }
    if (!rnr.nonFullSupplyItemsSubmittedCost) {
      rnr.nonFullSupplyItemsSubmittedCost = 0;
    }
  }

  var lineItemMap = {
    'non-full-supply': $scope.rnr.nonFullSupplyLineItems,
    'full-supply': $scope.rnr.fullSupplyLineItems,
    'regimen': $scope.rnr.regimenLineItems
  };

  var refreshGrid = function () {
    $scope.visibleTab = $routeParams.supplyType === NON_FULL_SUPPLY ? NON_FULL_SUPPLY :
      ($routeParams.supplyType === REGIMEN && $scope.regimenCount) ? REGIMEN : FULL_SUPPLY;
    $location.search('supplyType', $scope.visibleTab);

    if ($scope.visibleTab != REGIMEN) {
      $scope.numberOfPages = Math.ceil(lineItemMap[$scope.visibleTab].length / $scope.pageSize) || 1;
    } else {
      $scope.numberOfPages = 1;
    }

    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;

    if ($scope.saveRnrForm.$dirty) {
      $scope.saveRnr();
    }

    $scope.pageLineItems = lineItemMap[$scope.visibleTab].slice(($scope.pageSize * ($scope.currentPage - 1)), $scope.pageSize * $scope.currentPage);
  }();

  $scope.$on('$routeUpdate', refreshGrid);

  function resetErrorPages() {
    $scope.errorPages = {fullSupply: [], nonFullSupply: []};
  }

  function resetFlags() {
    $scope.submitError = $scope.submitMessage = $scope.error = $scope.message = "";
  }

  function removeExtraDataForPostFromRnr() {
    var rnr = {"id": $scope.rnr.id, "fullSupplyLineItems": [], "nonFullSupplyLineItems": [], "regimenLineItems": []};
    if (!$scope.pageLineItems.length) return rnr;
    if ($scope.pageLineItems[0].fullSupply === false) {
      _.each($scope.rnr.nonFullSupplyLineItems, function (lineItem) {
        rnr.nonFullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
      });
    } else if ($scope.pageLineItems[0].fullSupply === true) {
      _.each($scope.pageLineItems, function (lineItem) {
        rnr.fullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
      });
    }
    else {
      _.each($scope.pageLineItems, function (regimenLineItem) {
        rnr.regimenLineItems.push(regimenLineItem);
      });
    }
    return rnr;
  }
}

CreateRequisitionController.resolve = {
  requisition: function ($q, $timeout, Requisitions, $route, $rootScope) {
    var deferred = $q.defer();
    $timeout(function () {
      var rnr = $rootScope.rnr;
      if (rnr) {
        deferred.resolve(rnr);
        $rootScope.rnr = undefined;
        return;
      }
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

  lossesAndAdjustmentsTypes: function ($q, $timeout, LossesAndAdjustmentsReferenceData) {
    var deferred = $q.defer();
    $timeout(function () {
      LossesAndAdjustmentsReferenceData.get({}, function (data) {
        deferred.resolve(data.lossAdjustmentTypes);
      }, {});
    }, 100);
    return deferred.promise;
  },

  facilityApprovedProducts: function ($q, $timeout, $route, FacilityApprovedProducts) {
    var deferred = $q.defer();
    $timeout(function () {
      FacilityApprovedProducts.get({facilityId: $route.current.params.facility, programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.nonFullSupplyProducts);
      }, {});
    }, 100);
    return deferred.promise;
  },

  requisitionRights: function ($q, $timeout, $route, FacilityProgramRights) {
    var deferred = $q.defer();
    $timeout(function () {
      FacilityProgramRights.get({facilityId: $route.current.params.facility, programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.rights);
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

