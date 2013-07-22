angular.module('supplyline', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).config(['$routeProvider', function ($routeProvider) {
            $routeProvider.
                when('/list', {controller: SupplylineController, templateUrl: 'partials/list.html'}).
                otherwise({redirectTo: '/list'});
        }]).run(function ($rootScope, AuthorizationService) {
            $rootScope.supplylineSelected = "selected";
            AuthorizationService.preAuthorize('MANAGE_SUPPLYLINE');
        });
