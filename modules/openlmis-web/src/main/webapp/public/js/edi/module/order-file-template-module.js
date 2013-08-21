/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
angular.module('orderFileTemplate', ['openlmis', 'ui.sortable']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/configure-order-file', { controller: OrderFileTemplateController, templateUrl: 'partials/order-file-template-form.html', resolve: OrderFileTemplateController.resolve }).
    otherwise({redirectTo: '/configure-order-file'});
}]);