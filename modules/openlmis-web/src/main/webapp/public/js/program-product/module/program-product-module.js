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
    }]);

angular.bootstrap(document, ['programProductModule']);