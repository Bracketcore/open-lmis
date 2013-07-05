/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

services.factory('messageService', function (Messages, localStorageService, $rootScope, version) {

  var populate = function () {
    if (localStorageService.get('version') != version) {
      localStorageService.add('version', version);
      Messages.get({}, function (data) {
        localStorageService.add('messagesAvailable', true);
        for (var attr in data.messages) {
          localStorageService.add('message.' + attr, data.messages[attr]);
        }
        $rootScope.$broadcast('messagesPopulated');
      }, {});
    }
  };

  var get = function () {
    var keyWithArgs = Array.prototype.slice.call(arguments);
    var displayMessage =  localStorageService.get('message.' + keyWithArgs[0]);
    if(keyWithArgs.length > 1) {
      $.each(keyWithArgs, function (index, arg) {
        if (index > 0) {
          displayMessage = displayMessage.replace("{" + (index-1) + "}", arg);
        }
      });
    }
    return displayMessage || keyWithArgs[0];
  };

  return{
    populate:populate,
    get:get
  }
});
