/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
var programProductModule = angular.module('programProductModule', ['openlmis', 'ui.bootstrap.modal']).config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
          when('/select-push-program', {
            controller: ProgramProductController,
            templateUrl: 'partials/list.html',
            resolve: ProgramProductController.resolve }).

          otherwise({redirectTo: '/select-push-program'});
    }]).directive('numericValidator', function () {
      return {
        require: '?ngModel',
        link: function (scope, element, attrs, ctrl) {
          var validationFunction = programProductModule[attrs.numericValidator];

          element.bind('blur', function () {
            validationFunction(ctrl.$modelValue, element.attr('name'));
          });
          ctrl.$parsers.unshift(function (viewValue) {
            if (validationFunction(viewValue, element.attr('name'))) {
              if (viewValue == "")  viewValue = undefined;
              return viewValue;
            } else {
              ctrl.$viewValue = ctrl.$modelValue;
              ctrl.$render();
              return ctrl.$modelValue;
            }
          });
        }
      };
    });

angular.bootstrap(document, ['programProductModule']);

programProductModule.numericValue = function (value, errorHolder) {
  var NUMBERIC_REGEXP_FIXED_PRECISION = /^(\d{0,3}\.\d{0,3}|\d{0,3})$/;
  var REGEX_FOR_THREE_DIGITS_AFTER_DECIMAL = /\.\d{3}.$/
  var NUMBER_REGEXP = /^\d*\.?\d{1,4}$/;
  var valid = (value == undefined) ? true : NUMBERIC_REGEXP_FIXED_PRECISION.test(value);

  if (errorHolder != undefined && REGEX_FOR_THREE_DIGITS_AFTER_DECIMAL.test(value) != true) {
    document.getElementById(errorHolder).style.display = ((value == undefined) ? true : (NUMBER_REGEXP.test(value))) ? 'none' : 'block';
  }

  return valid;
};

programProductModule.positiveInteger = function (value, errorHolder) {
  var POSITIVE_INTEGER_REGEXP_FIXED_LENGTH = /^\d*$/;

  var valid = (value == undefined) ? true : POSITIVE_INTEGER_REGEXP_FIXED_LENGTH.test(value);

  if (errorHolder != undefined) {
    document.getElementById(errorHolder).style.display = (valid) ? 'none' : 'block';
  }

  return valid;
};

programProductModule.integer = function (value, errorHolder) {
  var INTEGER_REGEXP_FIXED_LENGTH = /^[-]?\d{0,6}$/;
  var REGEX_FOR_SIX_DIGITS=/\d{6}.$/
  var INTEGER_REGEXP = /^[-]?\d*$/;
  var valid = (value == undefined) ? true : INTEGER_REGEXP_FIXED_LENGTH.test(value);

  if (errorHolder != undefined && REGEX_FOR_SIX_DIGITS.test(value) != true) {
    document.getElementById(errorHolder).style.display = ((value == undefined) ? true : (INTEGER_REGEXP.test(value))) ? 'none' : 'block';
  }

  return valid;
};
