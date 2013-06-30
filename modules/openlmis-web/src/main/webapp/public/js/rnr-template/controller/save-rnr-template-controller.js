/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function SaveRnrTemplateController($scope, rnrTemplateForm, program, messageService, $routeParams, RnRColumnList, $location) {
  $scope.rnrColumns = rnrTemplateForm.rnrColumns;
  $scope.sources = rnrTemplateForm.sources;
  $scope.validateFormula = $scope.rnrColumns[0].formulaValidationRequired;
  $scope.program = program;
  $scope.$parent.message = "";
  $scope.selectProgramUrl = "/public/pages/admin/rnr-template/index.html#/select-program";
  $scope.arithmeticValidationLabel = false;

  var setRnRTemplateValidateFlag = function () {
    $.each($scope.rnrColumns, function (index, column) {
      column.formulaValidationRequired = $scope.validateFormula;
    });
  };

  $scope.save = function () {
    updatePosition();
    setRnRTemplateValidateFlag();
    RnRColumnList.post({programId: $routeParams.programId}, $scope.rnrColumns, function () {
      $scope.$parent.message = messageService.get("template.save.success");
      $scope.error = "";
      $scope.errorMap = undefined;
      $location.path('select-program');
    }, function (data) {
      if (data != null) {
        $scope.errorMap = data.data;
      }
      updateErrorMessage("form.error");
    });
  };

  function updatePosition() {
    $scope.rnrColumns.forEach(function (rnrColumn, index) {
      rnrColumn.position = index + 1;
    });
  }

  $scope.sources.forEach(function (source) {
    source.description = messageService.get(source.description);
  });

  function updateErrorMessage(message) {
    $scope.error = message;
    $scope.message = "";
  }


  $scope.setArithmeticValidationMessageShown = function () {
    $.each($scope.rnrColumns, function (index, column) {
      if (column.sourceConfigurable) {
        if (column.source.code == 'U' && column.visible == true) {
          $scope.arithmeticValidationMessageShown = true;
        }
        else {
          $scope.arithmeticValidationMessageShown = false;
          return false;
        }
      }
    });
  };
  $scope.setArithmeticValidationMessageShown();

  var setArithmeticValidationLabel = function () {
    if ($scope.validateFormula) {
      $scope.arithmeticValidationStatusLabel = messageService.get("label.on");
      $scope.arithmeticValidationToggleLabel = messageService.get("label.off");
      $scope.arithmeticValidationMessage = "msg.rnr.arithmeticValidation.turned.on";
    } else {
      $scope.arithmeticValidationStatusLabel =  messageService.get("label.off");
      $scope.arithmeticValidationToggleLabel =  messageService.get("label.on");
      $scope.arithmeticValidationMessage = "";
    }
  };
  setArithmeticValidationLabel();

  $scope.toggleValidateFormulaFlag = function () {
    $scope.validateFormula = !$scope.validateFormula;
    setArithmeticValidationLabel();
  }
}

SaveRnrTemplateController.resolve = {
  rnrTemplateForm:function ($q, RnRColumnList, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      RnRColumnList.get({programId:id}, function (data) {
        deferred.resolve(data.rnrTemplateForm);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  },

  program:function ($q, Program, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      Program.get({id:id}, function (data) {
        deferred.resolve(data.program);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  }
};
