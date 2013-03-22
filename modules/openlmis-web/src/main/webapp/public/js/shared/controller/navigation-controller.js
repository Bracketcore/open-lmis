/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function NavigationController( $scope, UserContext, localStorageService, $rootScope) {

    $scope.loadRights = function () {
        var rights = localStorageService.get(localStorageKeys.RIGHT);

        if(rights == undefined){
            UserContext.get({}, function (data) {
              if(data.authenticated) {
                  rights = getRights(data.rights);
                  localStorageService.add(localStorageKeys.RIGHT, rights);
              }
            }, {});
        }

        $rootScope.rights = rights;

        $(".navigation > ul").show();
    }();

    $scope.showSubmenu = function () {
        $(".navigation li:not(.navgroup)").on("click", function () {
            $(this).children("ul").show();
        });
    }();

    $rootScope.hasPermission = function (permission) {
      return (($rootScope.rights != undefined) &&  ($rootScope.rights.indexOf(permission) > -1));
    };

  function getRights(rightList) {
    var rights = [];
    $.each(rightList, function (index, right) {
      rights.push(right.right);
    });
    return rights;
  }

}
