/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("User Search Controller", function () {

  var scope, $httpBackend, ctrl, navigateBackService, location, messageService, userList;
  beforeEach(module('openlmis'));
  var searchTextId = 'searchTextId';

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _navigateBackService_, $location, _messageService_) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    scope.query = "joh";
    navigateBackService = _navigateBackService_;
    navigateBackService.query = '';
    location = $location;
    ctrl = $controller;
    messageService = _messageService_;
    ctrl('UserSearchController', {$scope: scope, userList: userList , messageService: messageService});
  }));

  it('should get all users depending on search criteria when three characters are entered in search', function () {
    var user = {"id": 1, "firstName": "john", "lastName": "Doe", "email": "john_doe@gmail.com"};
    var userResponse = {"userList": [user]};
    scope.query = "joh";

    $httpBackend.when('GET', '/users.json?param=' + scope.query).respond(userResponse);
    scope.showUserSearchResults();
    $httpBackend.flush();

    expect(scope.userList).toEqual([user]);
    expect(scope.resultCount).toEqual(1);
  });

  it('should filter users when more than 3 characters are entered for search with first 3 characters matching previous search', function () {
    scope.previousQuery = "joh";
    scope.query = "john_d";
    var user = {"id": 1, "firstName": "john", "lastName": "Doe", "email": "john_doe@gmail.com"};
    scope.userList = [user];

    scope.showUserSearchResults();

    expect(scope.filteredUsers).toEqual([user]);
    expect(scope.resultCount).toEqual(1);
  });

  it("should get and filter users when more than 3 characters are pasted for search and first 3 chars does not match " +
      "with previous query's first three chars", function () {
    scope.previousQuery = "abcd";
    scope.query = "lokesh";

    var user1 = {"id": 2, "userName": "lok", "firstName": "lokesh", "lastName": "Doe", "email": "lokesh_doe@gmail.com"};
    var user2 = {"id": 2, "userName": "loke", "firstName": "lokaaahh", "lastName": "Doe", "email": "lokaaahh_doe@gmail.com"};
    var userResponse = {"userList": [user1, user2]};
    $httpBackend.when('GET', '/users.json?param=lok').respond(userResponse);

    scope.showUserSearchResults(searchTextId);
    $httpBackend.flush();

    expect(scope.userList).toEqual([user1, user2]);
    expect(scope.filteredUsers).toEqual([user1]);
    expect(scope.resultCount).toEqual(1);
  });

  it("should save query into shared service on clicking edit link", function () {
    spyOn(navigateBackService, 'setData');
    spyOn(location, 'path');
    scope.query = "lokesh";
    scope.editUser(2);
    expect(navigateBackService.setData).toHaveBeenCalledWith({query: "lokesh"});
    expect(location.path).toHaveBeenCalledWith('edit/2');
  });

  it("should retain previous query value and update filtered query list when dom is loaded", function () {
    var query = "lok";
    navigateBackService.setData({query: query});
    $httpBackend.expect('GET', '/users.json?param=lok').respond(200, {});

    ctrl('UserSearchController', {$scope: scope, userList:userList });

    $httpBackend.flush();
    expect(query).toEqual(scope.query);
  });

  it("should open reset password modal", function () {
    var user = {id: 1, firstName: "User", active: true};
    scope.changePassword(user);
    expect(scope.password1).toEqual("");
    expect(scope.password2).toEqual("");
    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("");
    expect(scope.changePasswordModal).toEqual(true);
    expect(scope.user).toEqual(user);
  });

  it("should not open reset password modal if user is inactive", function () {
    var user = {id: 1, firstName: "User", active: false};
    scope.changePassword(user);
    expect(scope.changePasswordModal).toBeUndefined();
  });

  it("should reset password modal", function () {
    scope.resetPasswordModal();
    expect(scope.changePasswordModal).toEqual(false);
    expect(scope.user).toEqual(undefined);
  });

  it("should update user password if password matches and is valid",function () {
    scope.password1 = scope.password2 = "Abcd1234!";
    scope.user = {id: 1, firstName: "User"};

    $httpBackend.expect('PUT', '/admin/resetPassword/1.json').respond(200, {success: "password updated"});
    scope.updatePassword();
    $httpBackend.flush();
    expect(scope.message).toEqual("password updated")
    expect(scope.error).toEqual(undefined)
  });

  it("should update show error if password is not valid",function () {
    scope.password1 = scope.password2 = "invalid";
    scope.user = {id: 1, firstName: "User"};
    spyOn(messageService, 'get');
    scope.updatePassword();
    expect(messageService.get).toHaveBeenCalledWith("error.password.invalid");
  });

  it("should update show error if passwords do not match" ,function () {
    scope.password1 = "Abcd1234!";
  scope.password2 = "invalid";
    scope.user = {id: 1, firstName: "User"};
    spyOn(messageService, 'get');
    scope.updatePassword();
    expect(messageService.get).toHaveBeenCalledWith("error.password.mismatch");
  });

});