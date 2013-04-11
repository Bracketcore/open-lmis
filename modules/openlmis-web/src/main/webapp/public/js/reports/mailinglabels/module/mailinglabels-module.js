'use strict';
angular.module('mailinglabels', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/list', {controller:ListMailinglabelsController, templateUrl:'partials/list.html',reloadOnSearch:false}).
            otherwise({redirectTo:'/list'});
    }]).run(function($rootScope) {
        // $rootScope.Selected = "selected";
    });