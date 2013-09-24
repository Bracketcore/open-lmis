/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('ConvertToOrderListController', function () {

  var scope, ctrl, httpBackend, controller, routeParams, location;
  var requisitions;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(module('ui.bootstrap.dialog'));

  beforeEach(inject(function ($httpBackend, $rootScope, $controller,
                              $routeParams, $location) {
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;
    routeParams = $routeParams;
    location = $location;
    scope.maxNumberOfPages = 2;

    var pagedRequisitions = {
      rnr_list: [
        {"facilityName": "first facility", "programName": "first program", "facilityCode": "first code", supplyingDepot: "supplying depot first "},
        {"facilityName": "second facility", "programName": "second program", "facilityCode": "second code", supplyingDepot: "supplying depot second"},
        {"facilityName": "third facility", "programName": "third program", "facilityCode": "third code", supplyingDepot: "supplying depot third"}
      ],
      number_of_pages: 2
    }
    requisitions = pagedRequisitions.rnr_list;
    ctrl = controller(ConvertToOrderListController, {$scope: scope, pagedRequisitionList: pagedRequisitions, $location: location, $routeParams: routeParams});
  }));

  it('should set page line items based on pageSize', function () {
    routeParams.page = 2;
    scope.pageSize = 2;
    httpBackend.expectGET('/requisitions-for-convert-to-order.json?page=2').respond({"rnr_list": [requisitions[2]]});
    scope.$broadcast("$routeUpdate");
    httpBackend.flush();

    expect(scope.currentPage).toEqual(2);
    expect(scope.filteredRequisitions).toEqual([requisitions[2]]);
  });

  it('should show all requisitions if filter is not applied', function () {
    expect(scope.filteredRequisitions).toEqual(requisitions);
    expect(scope.query).toBeUndefined();
    expect(scope.searchField).toBeUndefined();
  });


  it('should Filter requisitions against program name', function () {
    scope.query = "first";
    scope.searchField = "programName";

    httpBackend.expectGET('/requisitions-for-convert-to-order.json?page=1&searchType=programName&searchVal=first').respond({"rnr_list": [requisitions[0]]});

    scope.fetchFilteredRequisitions();

    httpBackend.flush();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitions[0]);
  });

  it('should Filter requisitions against facility name', function () {
    scope.query = "second facility";
    scope.searchField = "facilityName";

    httpBackend.expectGET('/requisitions-for-convert-to-order.json?page=1&searchType=facilityName&searchVal=second+facility').respond({"rnr_list": [requisitions[1]]});

    scope.fetchFilteredRequisitions();

    httpBackend.flush();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitions[1]);
  });

  it('should Filter requisitions against facility code', function () {
    scope.query = "second CO";
    scope.searchField = "facilityCode";

    httpBackend.expectGET('/requisitions-for-convert-to-order.json?page=1&searchType=facilityCode&searchVal=second+CO').respond({"rnr_list": [requisitions[1]]});

    scope.fetchFilteredRequisitions();

    httpBackend.flush();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitions[1]);
  });

  it('should un-select selected requisitions when filtering', function () {
    scope.query = "second CO";
    scope.searchField = "facilityCode";
    scope.gridOptions.selectedItems = [requisitions[0]];

    httpBackend.expectGET('/requisitions-for-convert-to-order.json?page=1&searchType=facilityCode&searchVal=second+CO').respond({"rnr_list": [requisitions[1]]});

    scope.fetchFilteredRequisitions();

    httpBackend.flush();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.gridOptions.selectedItems.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitions[1]);
  });

  it('should Filter requisitions against supplying depot', function () {
    scope.query = "depot first";
    scope.searchField = "supplyingDepot";

    httpBackend.expectGET('/requisitions-for-convert-to-order.json?page=1&searchType=supplyingDepot&searchVal=depot+first').respond({"rnr_list": [requisitions[0]]});

    scope.fetchFilteredRequisitions();

    httpBackend.flush();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitions[0]);
  });

  it('should be able to Filter requisitions against all fields also', function () {
    scope.query = "second";
    scope.searchField = "";

    httpBackend.expectGET('/requisitions-for-convert-to-order.json?page=1&searchType=&searchVal=second').respond({"rnr_list": [requisitions[1]]});

    scope.fetchFilteredRequisitions();

    httpBackend.flush();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitions[1]);
  });

  it('should be able to case-insensitively Filter requisitions', function () {
    scope.query = "seCOnD";
    scope.searchField = "";

    httpBackend.expectGET('/requisitions-for-convert-to-order.json?page=1&searchType=&searchVal=seCOnD').respond({"rnr_list": [requisitions[1]]});

    scope.fetchFilteredRequisitions();

    httpBackend.flush();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitions[1]);
  });

  it("should convert the selected requisitions to order", function () {
    httpBackend.expectPOST('/orders.json', scope.gridOptions.selectedItems).respond(201);
    httpBackend.expectGET('/requisitions-for-convert-to-order.json?page=1').respond({"rnr_list": [requisitions[1]]});
    scope.dialogCloseCallback(true);

    httpBackend.flush();
    expect(scope.message).toEqual("msg.rnr.converted.to.order");
    expect(scope.error).toEqual("");
    expect(scope.noRequisitionSelectedMessage).toEqual("");
    expect(scope.filteredRequisitions).toEqual([requisitions[1]]);
  });

  it('should display confirm modal if convert to order button is clicked with some Rnrs selected', function () {
    scope.gridOptions.selectedItems = [requisitions[0]];
    spyOn(OpenLmisDialog, 'newDialog');
    scope.convertToOrder();
    httpBackend.expectGET('/public/pages/partials/dialogbox.html').respond(200);
    expect(OpenLmisDialog.newDialog).toHaveBeenCalled();
  });

  it('should convert to order if ok is clicked on the confirm modal', function () {
    scope.gridOptions.selectedItems = [requisitions[0]];
    httpBackend.expectPOST('/orders.json', scope.gridOptions.selectedItems).respond(201);
    httpBackend.expectGET('/requisitions-for-convert-to-order.json?page=1').respond({"rnr_list": [requisitions[1]]});
    scope.dialogCloseCallback(true);
    httpBackend.flush();
    expect(scope.message).toEqual("msg.rnr.converted.to.order");
    expect(scope.error).toEqual("");
    expect(scope.selectedItems.length).toEqual(0);
  });

  it('should show error message if ok is clicked on the confirm modal ' +
    'and some requistions have already been converted to order', function () {
    scope.gridOptions.selectedItems = [requisitions[0]];
    httpBackend.expectPOST('/orders.json', scope.gridOptions.selectedItems).
      respond(409, {"error": "msg.rnr.already.converted.to.order"});
    httpBackend.expectGET('/requisitions-for-convert-to-order.json?page=1').respond({"rnr_list": [requisitions[1]]});
    scope.dialogCloseCallback(true);
    httpBackend.flush();
    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("msg.rnr.already.converted.to.order");
    expect(scope.selectedItems.length).toEqual(0);
    expect(scope.filteredRequisitions).toEqual([requisitions[1]]);
  });

  it('should give message if no requisition selected', function () {
    scope.gridOptions.selectedItems = [];
    scope.convertToOrder();

    expect(scope.noRequisitionSelectedMessage).toEqual("msg.select.atleast.one.rnr");
  });
});

