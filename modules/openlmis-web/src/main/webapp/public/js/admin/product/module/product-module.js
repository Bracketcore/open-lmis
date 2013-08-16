angular.module('product', ['openlmis','ui.bootstrap.modal', 'ui.bootstrap.dialog', 'ui.bootstrap.dropdownToggle']).config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/list', {controller: ProductController, templateUrl: 'partials/list.html'}).
            when('/edit/:id', {controller: ProductEditController, templateUrl: 'partials/edit.html'}).
            when('/create', {controller: ProductCreateController, templateUrl: 'partials/create.html'}).
            otherwise({redirectTo: '/list'});
    }]).run(function ($rootScope, AuthorizationService) {
        $rootScope.productSelected = "selected";
        AuthorizationService.preAuthorize('MANAGE_PRODUCT');
    });
