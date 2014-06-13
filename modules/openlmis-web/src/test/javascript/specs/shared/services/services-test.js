/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Services", function () {
  var httpMock, successStub, failureStub;


  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($httpBackend) {
    httpMock = $httpBackend;
    successStub = jasmine.createSpy();
    failureStub = jasmine.createSpy();

  }));

  afterEach(function () {
    httpMock.verifyNoOutstandingExpectation();
    httpMock.verifyNoOutstandingRequest();
  });

  describe("ApproveRnrService", function () {

    var requisitionForApprovalService;

    beforeEach(inject(function (RequisitionForApproval) {
      requisitionForApprovalService = RequisitionForApproval;
    }));

    it('should GET R&Rs pending for approval', function () {
      var requisitions = {"rnr_list": []};
      httpMock.expect('GET', "/requisitions-for-approval.json").respond(requisitions);
      requisitionForApprovalService.get({}, function (data) {
        expect(data.rnr_list).toEqual(requisitions.rnr_list);
      }, function () {
      });
      httpMock.flush();
    });
  });

  describe("SupplyLineSearchService", function () {

    var supplyLineSearchService;

    beforeEach(inject(function (SupplyLinesSearch) {
      supplyLineSearchService = SupplyLinesSearch;
    }));

    it('should GET searched supplyLines', function () {
      var supplyLinesResponse = {"supplyLines": [], "pagination": {}};
      httpMock.expect('GET', "/supplyLines/search.json").respond(supplyLinesResponse);
      supplyLineSearchService.get({}, function (data) {
        expect(data.supplyLines).toEqual(supplyLinesResponse.supplyLines);
        expect(data.pagination).toEqual(supplyLinesResponse.pagination);
      }, function () {
      });
      httpMock.flush();
    });
  });

  describe("FacilityApprovedProductsSearch", function () {

    var facilityApprovedProductsSearch;

    beforeEach(inject(function (FacilityApprovedProductsSearch) {
      facilityApprovedProductsSearch = FacilityApprovedProductsSearch;
    }));

    it('should GET searched FacilityTypeApprovedProducts', function () {
      var FacilityApprovedProductsResponse = {"facilityApprovedProducts": [], "pagination": {}};
      httpMock.expect('GET', "/facilityApprovedProducts.json").respond(FacilityApprovedProductsResponse);
      facilityApprovedProductsSearch.get({}, function (data) {
        expect(data.facilityApprovedProducts).toEqual(FacilityApprovedProductsResponse.facilityApprovedProducts);
        expect(data.pagination).toEqual(FacilityApprovedProductsResponse.pagination);
      }, function () {
      });
      httpMock.flush();
    });
  });

  describe("programProductsFilter", function () {

    var programProductsFilter;

    beforeEach(inject(function (ProgramProductsFilter) {
      programProductsFilter = ProgramProductsFilter;
    }));

    it('should filter program products', function () {
      var programProductsFilterResponse = {"programProducts": []};
      var programId = 1, facilityTypeId = 2;
      httpMock.expectGET('/programProducts/filter/programId/' + programId + '/facilityTypeId/' + facilityTypeId + '.json')
          .respond(200, {programProductList: programProductsFilterResponse});

      programProductsFilter.get({'programId': programId, 'facilityTypeId': facilityTypeId},
          function (data) {
            successStub();
            expect(data.programProductList).toEqual(programProductsFilterResponse);
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status', function () {
      var programId = 1, facilityTypeId = 2;

      httpMock.expectGET('/programProducts/filter/programId/' + programId + '/facilityTypeId/' + facilityTypeId + '.json')
          .respond(404);

      programProductsFilter.get({'programId': programId, 'facilityTypeId': facilityTypeId},
          function () {
            successStub();
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });
});