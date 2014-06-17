/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Facility Approved Product", function () {

  beforeEach(module('openlmis'));

  describe("Controller", function () {
    var scope, ctrl, supplyLine, $httpBackend, location, programs, facilityTypeList;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      location = $location;
      scope.query = "P10";
      supplyLine = {"program": {"name": "TB"}, "supplyingFacility": {"name": "supplying"}};

      programs = [
        {"name": "TB", "id": 1},
        {"name": "MALARIA", "id": 2}
      ];

      facilityTypeList = [
        {"id": 1, "name": "district1"},
        {"id": 2, "name": "district2"}
      ];
      ctrl = $controller('FacilityApprovedProductController', {$scope: scope, facilityTypes: facilityTypeList, programs: programs});
    }));

    it("should set currentPage, programs and facility types in scope", function () {
      expect(scope.facilityTypes).toEqual(facilityTypeList);
      expect(scope.programs).toEqual(programs);
      expect(scope.currentPage).toEqual(1);
      expect(scope.showResults).toEqual(false);
    });

    it("should clear search and load products", function () {
      spyOn(scope, 'loadProducts');

      scope.clearSearch();

      expect(scope.query).toEqual("");
      expect(scope.loadProducts).toHaveBeenCalledWith(1);
    });

    it('should trigger search on enter key', function () {
      var event = {"keyCode": 13};
      var searchSpy = spyOn(scope, 'loadProducts');

      scope.triggerSearch(event);

      expect(searchSpy).toHaveBeenCalledWith(1);
    });

    it('should get results according to specified page', function () {
      scope.currentPage = 5;
      var searchSpy = spyOn(scope, 'loadProducts');

      scope.$apply(function () {
        scope.currentPage = 6;
      });

      expect(searchSpy).toHaveBeenCalledWith(6);
    });

    it('should not get results if specified page is 0', function () {
      scope.currentPage = 3;
      var searchSpy = spyOn(scope, 'loadProducts');

      scope.$apply(function () {
        scope.currentPage = 0;
      });

      expect(searchSpy).not.toHaveBeenCalled();
    });

    it("should load products based on facilityType, program and search query", function () {
      scope.program = {"id": 2};
      scope.facilityType = {"id": 6};

      var response = {"facilityApprovedProducts": [
        {"name": "fap"}
      ], "pagination": {"totalRecords": 2, "page": 1}};

      $httpBackend.when("GET", '/facilityApprovedProducts.json?facilityTypeId=6&page=1&programId=2&searchParam=P10').respond(response);
      scope.loadProducts(1);
      $httpBackend.flush();

      expect(scope.facilityApprovedProducts).toEqual([
        {"name": "fap"}
      ]);
      expect(scope.pagination).toEqual(response.pagination);
      expect(scope.currentPage).toEqual(1);
      expect(scope.totalItems).toEqual(2);
      expect(scope.showResults).toEqual(true);
    });

    it("should load all products based on facilityType and program if no query specified", function () {
      scope.program = {"id": 2};
      scope.facilityType = {"id": 6};
      scope.query = "";

      var response = {"facilityApprovedProducts": [
        {"name": "fap"}
      ], "pagination": {"totalRecords": 2, "page": 1}};

      $httpBackend.when("GET", '/facilityApprovedProducts.json?facilityTypeId=6&page=1&programId=2&searchParam=').respond(response);
      scope.loadProducts(1);
      $httpBackend.flush();

      expect(scope.facilityApprovedProducts).toEqual([
        {"name": "fap"}
      ]);
      expect(scope.pagination).toEqual(response.pagination);
      expect(scope.currentPage).toEqual(1);
      expect(scope.totalItems).toEqual(2);
      expect(scope.showResults).toEqual(true);
    });

    it("should not load products if facilityType not selected", function () {
      spyOn($httpBackend, 'expectGET');
      scope.program = {"id": 2};

      scope.loadProducts(1);

      expect($httpBackend.expectGET).not.toHaveBeenCalled();
    });

    it("should not load products if program not selected", function () {
      spyOn($httpBackend, 'expectGET');
      scope.facilityType = {"id": 2};

      scope.loadProducts(1);

      expect($httpBackend.expectGET).not.toHaveBeenCalled();
    });
  });
});
