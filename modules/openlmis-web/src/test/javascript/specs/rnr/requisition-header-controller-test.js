describe('RequisitionController', function () {

  var scope, ctrl, httpBackend, location, routeParams, requisitionHeader, controller;

  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller) {
    scope = $rootScope.$new();
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    scope.$parent.facility = "10134";
    scope.$parent.program = {code:"programCode", "id":1};
    scope.saveRnrForm = {$error:{ rnrError:false }};
    routeParams = {"facility":"1", "program":"1"};

    requisitionHeader = {"requisitionHeader":{"facilityName":"National Warehouse",
      "facilityCode":"10134", "facilityType":{"code":"Warehouse"}, "facilityOperatedBy":"MoH", "maximumStockLevel":3, "emergencyOrderPoint":0.5,
      "zone":{"label":"state", "value":"Arusha"}, "parentZone":{"label":"state", "value":"Arusha"}}};

    httpBackend.when('GET', '/logistics/facility/1/requisition-header.json').respond(requisitionHeader);

    ctrl = controller(RequisitionController, {$scope:scope, $location:location, $routeParams:routeParams});
  }));

  it('should get header data', function () {
    httpBackend.flush();
    expect(scope.header).toEqual({"facilityName":"National Warehouse",
      "facilityCode":"10134", "facilityType":{"code":"Warehouse"}, "facilityOperatedBy":"MoH", "maximumStockLevel":3, "emergencyOrderPoint":0.5,
      "zone":{"label":"state", "value":"Arusha"}, "parentZone":{"label":"state", "value":"Arusha"}});
  });

  it('should prepare period display name', function () {
    scope.$parent.period = {"name":"Period 1", "startDate":1358274600000, "endDate":1367260200000};
    expect(scope.periodDisplayName()).toEqual('16/01/2013 - 30/04/2013');
  });
});
