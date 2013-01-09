describe("Period", function () {
  beforeEach(module('openlmis.services'));

  describe("View Schedule Periods", function () {
    var scheduleId = 123;
    var scope, $httpBackend, ctrl, routeParams;
    var existingPeriod = {"id":10, "name":"name", "description":"description"};
    var schedule = {"id":scheduleId, "name":"name", "description":"description"};

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      routeParams = $routeParams;
      routeParams.id = scheduleId;
      $httpBackend = _$httpBackend_;

      $httpBackend.expectGET('/schedules/123.json').respond(200, {"schedule":schedule});
      $httpBackend.expectGET('/schedules/123/periods.json').respond(200, {"periods":[existingPeriod]});
      ctrl = $controller(SchedulePeriodController, {$scope:scope, $routeParams:routeParams});
    }));

    it('should show all the periods for given schedule and the schedule for these periods', function () {
      $httpBackend.flush();
      expect(scope.schedule).toEqual(schedule);
      expect(scope.periodList).toEqual([existingPeriod]);
    });

    it('should calculate no. of days', function () {
      $httpBackend.flush();
      expect(scope.calculateDays(new Date(2011, 3, 1, 0, 0).getTime(), new Date(2011, 4, 1, 0, 0).getTime())).toEqual(30);
      expect(scope.calculateDays(new Date(2011, 3, 1, 0, 0).getTime(), new Date(2011, 3, 1, 0, 0).getTime())).toEqual(null);
      expect(scope.calculateDays(new Date(2011, 3, 1, 23, 59, 59).getTime(), new Date(2011, 4, 1, 0, 0).getTime())).toEqual(30);
      expect(scope.calculateDays(new Date(2011, 3, 1, 23, 59, 59).getTime(), new Date(2011, 3, 2, 0, 0).getTime())).toEqual(1);
    });

    it('should calculate no. of months', function () {
      $httpBackend.flush();
      scope.newPeriod = {"name":"newName", "startDate":new Date(2011, 3, 1, 0, 0), "endDate":new Date(2011, 4, 1, 0, 0), "description":"newDescription"};
      expect(scope.calculateMonths()).toEqual(1);
    });

    it('should create a new period', function () {
      $httpBackend.flush();
      var newPeriod = {"name":"newName", "startDate":new Date(2011, 3, 1, 0, 0), "endDate":new Date(2011, 4, 1, 0, 0), "description":"newDescription"};
      scope.newPeriod = newPeriod;
      $httpBackend.expectPOST('/schedules/123/periods.json').respond(200, {"success":"success message"});

      scope.createPeriodForm = {$invalid:false};
      scope.createPeriod();
      $httpBackend.flush();
      expect(scope.periodList.length).toEqual(2);
      expect(scope.periodList).toEqual([newPeriod, existingPeriod]);
      expect(scope.message).toEqual("success message");
    });

    it('should not create a new period if Start Date is greater than End Date', function () {
      $httpBackend.flush();
      scope.newPeriod = {"name":"newName", "startDate":new Date(2011, 3, 1, 0, 0), "endDate":new Date(2011, 2, 1, 0, 0), "description":"newDescription"};
      scope.createPeriodForm = {$invalid:false};
      scope.createPeriod();
      expect(scope.periodList.length).toEqual(1);
      expect(scope.periodList).toEqual([existingPeriod]);
      expect(scope.error).toEqual("End Date must be greater than Start Date");
      expect(scope.message).toEqual("");
    });

    it('should not delete a period if start date is less than current date', function () {
      scope.periodList = [{"id": "periodId", "name":"newName", "startDate":new Date(2011, 3, 1, 0, 0), "endDate":new Date(2011, 2, 1, 0, 0), "description":"newDescription"}];
      scope.newPeriod = {"name":"newName", "startDate":new Date(2011, 3, 1, 0, 0), "endDate":new Date(2011, 2, 1, 0, 0), "description":"newDescription"};
      scope.deletePeriod("periodId");
      expect(scope.error).toEqual("Period's Start Date is smaller than Current Date");
      expect(scope.message).toEqual("");
      expect(scope.periodList.length).toEqual(1);
      expect(scope.newPeriod.startDate).toEqual(new Date(2011, 3, 1, 0, 0));
    });

    it('should not delete a period if start date is equal to current date', function () {
      scope.periodList = [{"id": "periodId", "name":"newName", "startDate":new Date(), "endDate":new Date(2011, 2, 1, 0, 0), "description":"newDescription"}];
      scope.newPeriod = {"name":"newName", "startDate":new Date(2011, 3, 1, 0, 0), "endDate":new Date(2011, 2, 1, 0, 0), "description":"newDescription"};
      scope.deletePeriod("periodId");
      expect(scope.error).toEqual("Period's Start Date is smaller than Current Date");
      expect(scope.message).toEqual("");
      expect(scope.periodList.length).toEqual(1);
      expect(scope.newPeriod.startDate).toEqual(new Date(2011, 3, 1, 0, 0));
    });

    it('should delete a period if start date is greater than current date', function () {
      scope.periodList = [{"id": "periodId", "name":"newName", "startDate":new Date(9999, 3, 1, 0, 0), "endDate":new Date(2011, 2, 1, 0, 0), "description":"newDescription"}];
      $httpBackend.expectDELETE('/periods/periodId.json').respond(200, {"success" : "Period deleted successfully"});
      scope.deletePeriod("periodId");
      $httpBackend.flush();
      expect(scope.message).toEqual("Period deleted successfully");
      expect(scope.error).toEqual("");
      expect(scope.periodList.length).toEqual(0);
      expect(scope.newPeriod.startDate).toEqual(undefined);
    });

   /* it('should delete a period and update start date', function() {
      scope.periodList = [{"id": "periodId1", "name":"newName", "startDate":new Date(9999, 3, 1, 0, 0), "endDate":new Date(2011, 2, 1, 0, 0), "description":"newDescription"},
        {"id": "periodId2", "name":"newName", "startDate":new Date(9999, 3, 1, 0, 0), "endDate":new Date(2011, 2, 1, 0, 0), "description":"newDescription"}];
      $httpBackend.expectDELETE('/periods/periodId1.json').respond(200, {"success" : "Period deleted successfully"});
      scope.newPeriod = {"name":"newName", "startDate":new Date(2011, 3, 1, 0, 0), "endDate":new Date(2011, 2, 1, 0, 0), "description":"newDescription"};
      scope.deletePeriod("periodId1");
      $httpBackend.flush();
      expect(scope.message).toEqual("Period deleted successfully");
      expect(scope.error).toEqual("");
      expect(scope.periodList.length).toEqual(1);
      expect(scope.newPeriod.startDate).toEqual(new Date(2011, 2, 2, 0, 0));
    });*/
  });
});