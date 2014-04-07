/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("HeaderController", function () {

  beforeEach(module('openlmis'));
  beforeEach(module('ui.directives'));

  var scope, loginConfig, window, localStorageService;

  describe("User is logged in", function () {

    beforeEach(inject(function ($rootScope, $controller, _localStorageService_) {
      loginConfig = {a: {}, b: {}};
      scope = $rootScope.$new();
      window = {};
      localStorageService = _localStorageService_;
      spyOn(localStorageService, 'get').andReturn("user");
      spyOn(localStorageService, 'remove');
      $controller(HeaderController, {$scope: scope, localStorageService: localStorageService, loginConfig: loginConfig, $window: window});
    }));

    it('should set login config in scope', function () {
      expect(scope.loginConfig).toEqual(loginConfig);
    });

    it('should set user in scope', function () {
      expect(scope.user).toEqual("user");
    });

    it('should clear localStorage when user logs out', function() {
      scope.logout();
      expect(localStorageService.remove).toHaveBeenCalledWith(localStorageKeys.RIGHT);
      expect(localStorageService.remove).toHaveBeenCalledWith(localStorageKeys.USERNAME);
      expect(window.location).toEqual("/j_spring_security_logout");
    });
  });

  describe("User is logged out", function () {
    beforeEach(inject(function ($rootScope, $controller, _localStorageService_) {
      loginConfig = {a: {}, b: {}};
      scope = $rootScope.$new();
      window = {};
      localStorageService = _localStorageService_;
      spyOn(localStorageService, 'get').andReturn(undefined);
      $controller(HeaderController, {$scope: scope, localStorageService: localStorageService, loginConfig: loginConfig, $window: window});
    }));

    it('should redirect user to login page', function () {
      expect(window.location).toEqual("/public/pages/login.html");
    });
  });
});