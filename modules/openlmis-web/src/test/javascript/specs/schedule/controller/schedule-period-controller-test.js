/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Period", function () {
  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));

  describe("View Schedule Periods", function () {
    var scheduleId = 123;
    var scope, $httpBackend, ctrl, routeParams, messageService;
    var existingPeriod = {"id": 10, "name": "name", "description": "description", "stringEndDate": "2013-01-14", "stringStartDate": "2013-01-01"};
    var schedule = {"id": scheduleId, "name": "name", "description": "description"};

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      routeParams = $routeParams;
      routeParams.id = scheduleId;
      $httpBackend = _$httpBackend_;

      ctrl = $controller(SchedulePeriodController, {$scope: scope, $routeParams: routeParams, schedule: schedule, periods: [existingPeriod]});
    }));

    it('should show all the periods for given schedule and the schedule for these periods', function () {
      expect(scope.schedule).toEqual(schedule);
      expect(scope.periodList).toEqual([existingPeriod]);
    });

    it('should calculate no. of days', function () {
      expect(scope.calculateDays(new Date(2011, 3, 1, 0, 0).getTime(), new Date(2011, 4, 1, 0, 0).getTime())).toEqual(30);
      expect(scope.calculateDays(new Date(2011, 3, 1, 23, 59, 59).getTime(), new Date(2011, 4, 1, 0, 0).getTime())).toEqual(30);
      expect(scope.calculateDays(new Date(2011, 3, 1, 23, 59, 59).getTime(), new Date(2011, 3, 2, 0, 0).getTime())).toEqual(1);
      expect(scope.calculateDays(new Date(2011, 3, 1, 0, 0, 0).getTime(), new Date(2011, 3, 2, 23, 59, 59).getTime())).toEqual(2);
    });

    it('should calculate no. of months', function () {
      scope.newPeriod = {"name": "newName", "startDate": new Date(2011, 3, 1, 0, 0), "endDate": new Date(2011, 4, 1, 0, 0), "description": "newDescription"};
      expect(scope.calculateMonths()).toEqual(1);
    });

    it('should create a new period', function () {
      var newPeriod = {"name": "newName", "startDate": "2011-04-01", "endDate": "2011-05-01", "description": "newDescription"}
      scope.newPeriod = newPeriod;
      $httpBackend.expectPOST('/schedules/123/periods.json').respond(200, {"success": "success message"});

      scope.createPeriodForm = {$invalid: false};
      scope.createPeriod();
      $httpBackend.flush();

      expect(scope.periodList.length).toEqual(2);
      expect(scope.periodList).toEqual([newPeriod, existingPeriod]);
      expect(scope.message).toEqual("success message");
    });

    it('should create the first period', function () {
      scope.periodList = [
        {"id": "periodId", "name": "newName", "startDate": new Date(2011, 3, 1, 0, 0), "endDate": new Date(2011, 2, 1, 0, 0), "description": "newDescription"}
      ];
      scope.newPeriod = {"name": "newName", "startDate": new Date(2011, 3, 1, 0, 0), "endDate": new Date(2011, 2, 1, 0, 0), "description": "newDescription"};
      scope.deletePeriod("periodId");

      expect(scope.message).toEqual("");
      expect(scope.periodList.length).toEqual(1);
      expect(scope.newPeriod.startDate).toEqual(new Date(2011, 3, 1, 0, 0));
    });

    it('should not delete a period if start date is equal to current date', function () {
      scope.periodList = [
        {"id": "periodId", "name": "newName", "startDate": new Date(), "endDate": new Date(2011, 2, 1, 0, 0), "description": "newDescription"}
      ];
      scope.newPeriod = {"name": "newName", "startDate": new Date(2011, 3, 1, 0, 0), "endDate": new Date(2011, 2, 1, 0, 0), "description": "newDescription"};
      scope.deletePeriod("periodId");

      expect(scope.message).toEqual("");
      expect(scope.periodList.length).toEqual(1);
      expect(scope.newPeriod.startDate).toEqual(new Date(2011, 3, 1, 0, 0));
    });

    it('should delete a period if start date is greater than current date', function () {
      scope.periodList = [
        {"id": 5, "name": "newName", "startDate": new Date(9999, 3, 1, 0, 0), "endDate": new Date(2011, 2, 1, 0, 0), "description": "newDescription"}
      ];
      $httpBackend.expectDELETE('/periods/5.json').respond(200, {"success": "Period deleted successfully"});
      scope.deletePeriod(5);
      $httpBackend.flush();

      expect(scope.message).toEqual("Period deleted successfully");
      expect(scope.error).toEqual("");
      expect(scope.periodList.length).toEqual(0);
      expect(scope.newPeriod.startDate).toEqual(undefined);
    });

    it('should refresh end Date offset after creating a period', function () {
      var newPeriod = {"name": "newName", "startDate": new Date(9999, 3, 1, 0, 0), "endDate": new Date(2013, 4, 1, 0, 0), "description": "newDescription"};
      spyOn(Date, 'now').andCallFake(function () {
        return new Date(9999, 3, 1, 0, 0).getTime();
      });
      scope.refreshEndDateOffset(newPeriod.startDate.getTime());

      expect(scope.endDateOffset).toEqual(1);
    });

    it('should reset new period after creating a period', function () {
      var newPeriod = {"name": "newName", "startDate": "2011-04-01", "endDate": "2011-05-01", "description": "newDescription"};
      scope.newPeriod = newPeriod;
      $httpBackend.expectPOST('/schedules/123/periods.json').respond(200, {"success": "success message"});

      scope.createPeriodForm = {$invalid: false};
      scope.createPeriod();
      $httpBackend.flush();

      expect(scope.newPeriod.startDate).toEqual(new Date(2011, 4, 2, 0, 0));
      expect(scope.message).toEqual("success message");
    });
  });

  describe('Create First Period', function () {

    var scheduleId = 456;
    var scope, $httpBackend, ctrl, routeParams;
    var scheduleWithNoExistingPeriod = {"id": scheduleId, "name": "name", "description": "description"};

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      routeParams = $routeParams;
      routeParams.id = scheduleId;
      $httpBackend = _$httpBackend_;

      ctrl = $controller(SchedulePeriodController, {$scope: scope, $routeParams: routeParams, schedule: scheduleWithNoExistingPeriod, periods: []});
    }));

    it('should create the first period', function () {
      var newPeriod = {"name": "newName", "startDate": "2011-04-01", "endDate": "2011-05-01", "description": "newDescription"};
      scope.newPeriod = newPeriod;
      $httpBackend.expectPOST('/schedules/456/periods.json').respond(200, {"success": "success message"});

      scope.createPeriodForm = {$invalid: false};
      scope.createPeriod();
      $httpBackend.flush();

      expect(scope.periodList.length).toEqual(1);
      expect(scope.periodList).toEqual([newPeriod]);
      expect(scope.message).toEqual("success message");
    });
  });
});