/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
function IsaModalController($scope, FacilityProgramProducts, ProgramProducts, $routeParams) {

  function calculateIsa(products) {
    $(products).each(function (index, product) {

      var population = $scope.$parent.facility.catchmentPopulation;

      if (isUndefined(population) || isUndefined(product.programProductIsa)) {
        product.calculatedIsa = "--";
        return;
      }
      var programProductIsa = new ProgramProductISA();
      programProductIsa.init(product.programProductIsa);

      product.calculatedIsa = programProductIsa.calculate(population);
    });
  }

  $scope.$watch('$parent.programProductsISAModal', function () {
    if (!$scope.$parent.programProductsISAModal) return;

    if (!$scope.currentProgram) return;

    $scope.currentProgramProducts = [];

    if ($scope.$parent.allocationProgramProductsList[$scope.currentProgram.id]) {
      calculateIsa($scope.$parent.allocationProgramProductsList[$scope.currentProgram.id]);
      $scope.filteredProducts = $scope.currentProgramProducts = angular.copy($scope.$parent.allocationProgramProductsList[$scope.currentProgram.id]);
      return;
    }

    var successFunc = function (data) {

      $scope.$parent.allocationProgramProductsList[$scope.currentProgram.id] = data.programProductList;

      calculateIsa($scope.$parent.allocationProgramProductsList[$scope.currentProgram.id]);

      $scope.filteredProducts = $scope.currentProgramProducts = angular.copy($scope.$parent.allocationProgramProductsList[$scope.currentProgram.id]);

    };

    if ($routeParams.facilityId) {
      FacilityProgramProducts.get({programId: $scope.currentProgram.id, facilityId: $routeParams.facilityId}, successFunc, function (data) {
      });
    } else {
      ProgramProducts.get({programId: $scope.currentProgram.id}, successFunc, function (data) {
      });
    }

  });

  $scope.updateISA = function () {
    $scope.$parent.allocationProgramProductsList[$scope.currentProgram.id] = angular.copy($scope.currentProgramProducts);
    $scope.$parent.programProductsISAModal = false;
  }

  $scope.resetISAModal = function () {
    $scope.$parent.programProductsISAModal = false;
  }

  $scope.resetAllToCalculatedIsa = function () {
    $($scope.currentProgramProducts).each(function (index, product) {
      product.overriddenIsa = null;
    });
  }

  $scope.updateCurrentProgramProducts = function () {
    $scope.filteredProducts = [];
    $scope.query = $scope.query.trim();

    if (!$scope.query.length) {
      $scope.filteredProducts = $scope.currentProgramProducts;
      return;
    }

    $($scope.currentProgramProducts).each(function (index, product) {
      var searchString = $scope.query.toLowerCase();
      if (product.product.primaryName.toLowerCase().indexOf(searchString) >= 0) {
        $scope.filteredProducts.push(product);
      }
    });

  }

}
