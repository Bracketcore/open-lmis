'use strict';
require(['../../../shared/app' , '../controller/reporting-rate-controller'], function (app) {
    app.loadApp();
    angular.module('reporting_rate', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider.
                when('/list', {controller:ReportingRateController, templateUrl:'partials/list.html',reloadOnSearch:false}).
                otherwise({redirectTo:'/list'});
        }]).run(
        function ($rootScope, AuthorizationService) {
            AuthorizationService.preAuthorize('VIEW_REPORTS');
        }
    );

    angular.bootstrap(document, ['reporting_rate']);
});