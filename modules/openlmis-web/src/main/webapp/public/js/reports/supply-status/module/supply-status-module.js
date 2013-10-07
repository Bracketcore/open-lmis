angular.module('supply_status', ['openlmis', 'ngTable', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/list', {controller:SupplyStatusController, templateUrl:'partials/list.html',reloadOnSearch:false}).
            otherwise({redirectTo:'/list'});
    }]).run(
    function ($rootScope, AuthorizationService) {
        AuthorizationService.preAuthorize('VIEW_SUPPLY_STATUS_REPORT');
    }
);