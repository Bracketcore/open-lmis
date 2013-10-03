/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('EPI Use controller', function () {

  var scope, controller;

  beforeEach(inject(function ($rootScope, $controller) {
    scope = $rootScope.$new();
    controller = $controller(EpiUseRowController, {$scope: scope});
  }));

  it("should compute total of 'stockAtFirstOfMonth' and 'received' fields", function () {
    scope.groupReading = { reading: {stockAtFirstOfMonth: {value: 50}, received: {value: 75} } };

    var total = scope.getTotal();

    expect(total).toEqual(125);
  });

  it("should ignore not recorded 'stockAtFirstOfMonth' or 'received' fields in total calculation", function () {
    scope.groupReading = { reading: {stockAtFirstOfMonth: {value: 50}, received: {notRecorded: true} } };

    var total = scope.getTotal();

    expect(total).toEqual(50);
  });

  it("should return total as zero if reading object is not available", function () {
    scope.groupReading = { };

    var total = scope.getTotal();

    expect(total).toEqual(0);
  });

  it("should return total as zero if group reading object is not available", function () {

    var total = scope.getTotal();

    expect(total).toEqual(0);
  });

  it('should set input class to true if not recorded is set for a field', function() {
    scope.clearError(true);

    expect(scope.inputClass).toBeTruthy();
  });

  it('should set input class to warning class if not recorded is not set for a field', function() {
    scope.clearError(false);

    expect(scope.inputClass).toEqual('warning-error');
  });

});