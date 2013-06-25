/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function SaveRegimenTemplateController($scope, program, regimens, regimenCategories, messageService, Regimens, $location) {

  $scope.program = program;
  $scope.regimens = regimens;
  $scope.regimenCategories = regimenCategories;
  $scope.selectProgramUrl = "/public/pages/admin/regimen-template/index.html#/select-program";
  $scope.regimensByCategory = {};
  $scope.$parent.message = "";

  function addRegimenByCategory(regimen) {
    var regimenCategoryId = regimen.category.id;
    var regimenList = $scope.regimensByCategory[regimenCategoryId];
    if (regimenList) {
      regimenList.push(regimen);
    } else {
      regimenList = [regimen];
    }
    $scope.regimensByCategory[regimenCategoryId] = regimenList;
  }

  function filterRegimensByCategory(regimens) {
    $(regimens).each(function (index, regimen) {
      addRegimenByCategory(regimen);
    });
  }

  filterRegimensByCategory($scope.regimens);

  $scope.addNewRegimen = function () {
    if ($scope.newRegimenForm.$error.required) {
      $scope.newRegimenError = messageService.get('label.missing.values');
    } else {
      $scope.newRegimen.programId = $scope.program.id;
      $scope.newRegimen.displayOrder = 1;
      $scope.newRegimen.disable = true;
      addRegimenByCategory($scope.newRegimen);
      $scope.newRegimenError = null;
      $scope.newRegimen = null;
    }
  };

  $scope.editRow = function (regimen) {
    regimen.disable = false;
  };

  $scope.saveRow = function (regimen) {
    if (!$scope.regimenEditForm.$error.required) {
      regimen.disable = true;
    }
  };

  $scope.save = function () {
    var regimenListToSave = [];
    var regimenLists = _.values($scope.regimensByCategory);
    $(regimenLists).each(function (index, regimenList) {
      $(regimenList).each(function (index, regimen) {
        regimen.disable = undefined;
      });
      regimenListToSave = regimenListToSave.concat(regimenList);
    });
    Regimens.post({programId: $scope.program.id}, regimenListToSave, function () {
      $scope.$parent.message = messageService.get('regimens.saved.successfully');
      $scope.program.regimenTemplateConfigured = true;
      $location.path('select-program');
    }, function (data) {
    });
  };
}

SaveRegimenTemplateController.resolve = {

  regimens: function ($q, ProgramRegimens, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      ProgramRegimens.get({programId: id}, function (data) {
        deferred.resolve(data.regimens);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  },

  program: function ($q, Program, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      Program.get({id: id}, function (data) {
        deferred.resolve(data.program);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  },

  regimenCategories: function ($q, RegimenCategories, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      RegimenCategories.get({}, function (data) {
        deferred.resolve(data.regimen_categories);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  }

};