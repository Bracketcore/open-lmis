'use strict';
require(['../../../shared/app' , '../controller/supply-status-controller'], function (app) {
    app.loadApp();
    angular.module('supply_status', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
            when('/list', {controller:SupplyStatusController, templateUrl:'partials/list.html',reloadOnSearch:false}).
            otherwise({redirectTo:'/list'});
        }]).run(
        function ($rootScope, AuthorizationService) {
            AuthorizationService.preAuthorize('VIEW_SUPPLY_STATUS_REPORT');
        }
    );

angular.bootstrap(document, ['supply_status']);
});