describe("Facility Controller", function () {


  var scope, $httpBackend, ctrl;

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    $httpBackend.expectGET('/admin/facility/reference-data.json').respond({"facilityTypes":[
      {"type":"warehouse"}
    ], "programs":[
      {"code":"programCode"}
    ], "geographicZones":[
      {"zoneId":"testId"}
    ], "facilityOperators":[
      {"operatorCode":"testCode"}
    ]});
    ctrl = $controller(FacilityController, {$scope:scope});
    scope.facilityForm = {$error : { pattern: "" }};
  }));

  it('should make call for facilities', function () {
    $httpBackend.flush();
    expect(scope.facilityTypes).toEqual([{"type":"warehouse"}]);
    expect(scope.facilityOperators).toEqual([{"operatorCode":"testCode"}]);
    expect(scope.geographicZones).toEqual([{"zoneId":"testId"}]);
    expect(scope.programs).toEqual([{"code":"programCode"}]);
  });

  it('should give success message if save successful', function(){
    $httpBackend.expectPOST('/admin/facility.json').respond(200);
    scope.saveFacility();
    $httpBackend.flush();
    expect("Saved successfully").toEqual(scope.message);
    expect("").toEqual(scope.error);
  });

  it('should give error if save failed', function(){
    $httpBackend.expectPOST('/admin/facility.json').respond(404);
    scope.saveFacility();
    $httpBackend.flush();
    expect("Save failed").toEqual(scope.error);
    expect("").toEqual(scope.message);
  });

  it('should give field validation error message if form has errors',function() {
    scope.facilityForm.$error.pattern = "{}";
    scope.saveFacility();
    expect("Some field holds incorrect value. Check above").toEqual(scope.error);
    expect("").toEqual(scope.message);
  });


});