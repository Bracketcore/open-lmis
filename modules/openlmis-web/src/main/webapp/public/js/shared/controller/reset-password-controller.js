/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ResetPasswordController($scope, UpdateUserPassword, $location, $route, tokenValid,messageService) {
  if(!tokenValid) {
    window.location = 'access-denied.html';
  }

  $scope.resetPassword = function () {
    var reWhiteSpace = new RegExp("\\s");
    var digits = new RegExp("\\d");
    if ($scope.password1.length < 8 || $scope.password1.length > 16 || !digits.test($scope.password1) || reWhiteSpace.test($scope.password1)) {
      $scope.error = messageService.get("error.password.invalid");
      return;
    }
    if ($scope.password1 != $scope.password2) {
      $scope.error = messageService.get('error.password.mismatch');
      return;
    }

    UpdateUserPassword.update({token:$route.current.params.token}, $scope.password1, function (data) {
      $location.path('/reset/password/complete');
    }, function (data) {
      window.location = 'access-denied.html';
    });

  }
}

function ValidateTokenController() {
}

function ResetCompleteController($scope) {

  $scope.goToLoginPage = function () {
    window.location = 'login.html'
  }

}

ValidateTokenController.resolve = {

  tokenValid:function ($q, $timeout, ValidatePasswordToken, $route, $location) {
    var deferred = $q.defer();
    $timeout(function () {
      ValidatePasswordToken.get({token:$route.current.params.token }, function (data) {
        $location.path('/reset/' + $route.current.params.token);
      }, function (data) {
        window.location = 'access-denied.html';
      });
    }, 100);
    return deferred.promise;
  }

}

ResetPasswordController.resolve = {

  tokenValid:function ($q, $timeout, ValidatePasswordToken, $route, $location) {
    var deferred = $q.defer();
    $timeout(function () {
      ValidatePasswordToken.get({token:$route.current.params.token }, function (data) {
          deferred.resolve(data.TOKEN_VALID);
      }, function (data) {
        window.location = 'access-denied.html';
      });
    }, 100);
    return deferred.promise;
  }

}