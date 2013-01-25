describe("User", function () {

  beforeEach(module('openlmis.services'));

  describe("User Controller", function () {

    var scope, $httpBackend, ctrl, routeParams, user;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      ctrl = $controller(UserController, {$scope:scope});
      scope.userForm = {$error:{ pattern:"" }};
    }));

    it('should give success message if save successful', function () {
      scope.user = {"userName":"User420"};
      $httpBackend.expectPOST('/admin/users.json').respond(200, {"success":"Saved successfully"});
      scope.saveUser();
      $httpBackend.flush();
      expect("Saved successfully").toEqual(scope.message);
      expect(scope.showError).toBeFalsy();
    });

    it('should give error message if save not successful', function () {
      scope.user = {"userName":"User420"};
      $httpBackend.expectPOST('/admin/users.json').respond(400, {"error":"errorMsg"});
      scope.saveUser();
      $httpBackend.flush();
      expect("errorMsg").toEqual(scope.error);
      expect(scope.showError).toBeTruthy();
    });

    it("should throw error when username contains space", function () {
      scope.user = {"userName":"User 420"};
      expect(scope.validateUserName()).toBeTruthy();
    });

    it("should get facilities when user enters 3 characters in search", function () {
      var facilityResponse = {"facilityList":[
        {"code":"F101"}
      ]};
      $httpBackend.expectGET('/facilitiesByCodeOrName.json?searchParam=F10').respond(facilityResponse);

      scope.query = "F10";
      scope.showFacilitySearchResults();

      $httpBackend.flush();
      expect(scope.filteredFacilities).toEqual([
        {"code":"F101"}
      ]);
    })

    it("should filter facilities by facility code when more than 3 characters are entered for search", function () {
      scope.facilityList = [
        {"name":"Village1","code":"F10111"},
        {"name":"Village2", "code":"F10200"}
      ];

      scope.query = "F101";
      scope.showFacilitySearchResults();

      expect(scope.filteredFacilities).toEqual([
        {"name":"Village1","code":"F10111"}
      ]);
    })

    it("should filter facilities by facility name when more than 3 characters are entered for search", function () {
      scope.facilityList = [
        {"name":"Village Dispensary", "code":"F10111"},
        {"name":"Facility2", "code":"F10200"}
      ];

      scope.query = "Vill";
      scope.showFacilitySearchResults();

      expect(scope.filteredFacilities).toEqual([
        {"name":"Village Dispensary", "code":"F10111"}
      ]);
    })

   it("should get supported programs for the facility selected and all the roles when user tries to add program role mapping", function(){
     scope.programAndRoleList = [];
     var facility = {"id":1, "code":"F1756", "name":"Village Dispensary", "supportedPrograms":[
       {"code":"ARV", "name":"ARV", "description":"ARV", "active":true},
       {"code":"HIV", "name":"HIV", "description":"HIV", "active":true}
     ]};
     scope.facilitySelected = facility;

     $httpBackend.expectGET('/admin/facility/1.json').respond({"facility":facility});
     $httpBackend.expectGET('/roles.json').respond({"roles":{"id":1, "name":"Admin"}});

     scope.displayProgramRoleMapping();
     $httpBackend.flush();
     expect(scope.programAndRoleList[0].supportedPrograms).toEqual(facility.supportedPrograms);
     expect(scope.programAndRoleList[0].roles).toEqual({"id":1, "name":"Admin"});

   })

  });

  describe("User Edit Controller", function () {

    var scope, $httpBackend, ctrl, user;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      routeParams = $routeParams;
      routeParams.userId = 1;
      var user = {"id":1};
      $httpBackend = _$httpBackend_;
      $httpBackend.when('GET', '/admin/user/1.json').respond({"userName":"User420"});
      ctrl = $controller(UserController, {$scope:scope, $routeParams:routeParams, user:user});
    }));


    it('should get user', function () {
      expect(scope.user).toEqual(user);
    });

  });

});