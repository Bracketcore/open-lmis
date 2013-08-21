/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

app.directive('autoSave', function ($route, IndexedDB, $timeout) {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {

      var save = function () {
        IndexedDB.put(attrs.objectStore, scope[attrs.autoSave]);
      };

      $timeout(function () {
        element.find(':text, textarea').bind('blur', save);
        element.find(':radio, :checkbox').bind('change', save);
      }, 100);
    }
  };
});