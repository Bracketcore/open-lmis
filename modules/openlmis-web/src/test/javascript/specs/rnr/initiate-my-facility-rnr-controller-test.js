describe('InitiateMyFacilityRnrController', function () {
  var scope, ctrl, $httpBackend, facilities, programs;

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    facilities = [
      {"id": "10134", "name": "National Warehouse", "description": null}
    ];
    programs = [
      {"code": "HIV", "name": "HIV", "description": "HIV", "active": true}
    ];

    $httpBackend.expectGET('/logistics/user/facilities.json').respond(200, {"facilityList": facilities});
    ctrl = $controller(InitiateMyFacilityRnrController, {$scope: scope});
  }));

  xit('should set facilities in scope', function () {
    $httpBackend.flush();

    expect(scope.$parent.facilities).toEqual(facilities);
  });

  xit('should load user supported programs for selected facility for create R&R', function () {
    scope.$parent.selectedFacilityId = facilities[0].id;
    $httpBackend.expectGET('/facility/10134/user/programs.json').respond({"programList": programs});

    scope.loadPrograms();
    $httpBackend.flush();

    expect(scope.$parent.programs).toEqual(programs);
  });

  xit('should not load user supported programs if there is no selected facility for create R&R', function () {
    scope.$parent.selectedFacilityId = null;

    scope.loadPrograms();
    $httpBackend.flush();

    expect(scope.$parent.programs).toEqual(null);
    expect(scope.$parent.selectedProgram).toEqual(null);
  });
});