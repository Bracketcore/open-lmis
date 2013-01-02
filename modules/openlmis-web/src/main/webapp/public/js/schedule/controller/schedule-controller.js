function ScheduleController($scope, Schedules, Schedule) {
  $scope.newSchedule = {};
  $scope.schedules = {};
  $scope.editSchedule = {};
  Schedules.get({}, function (data) {
    $scope.initialSchedules = angular.copy(data.schedules, $scope.initialSchedules);
    $scope.schedules = data.schedules;

  }, function (data) {
    $location.path($scope.$parent.sourceUrl);
  });

  $scope.createSchedule = function () {

    Schedules.save({}, $scope.newSchedule, function (data) {
      $scope.schedules.unshift(data.schedule);
      $scope.message = "Schedule Saved Successfully";
      $scope.error = "";
      $scope.newSchedule = {};
    }, function (data) {
      $scope.message = "";
      $scope.error = data.data.error;
    });
  }

  $scope.updateSchedule = function (schedule) {
    function updateUiData(sourceSchedule) {
      var schedulesLength = $scope.schedules.length;
      for (var i = 0; i < schedulesLength; i++) {
        if ($scope.schedules[i].id == sourceSchedule.id) {
          $scope.schedules[i].code = sourceSchedule.code;
          $scope.schedules[i].name = sourceSchedule.name;
          $scope.schedules[i].description = sourceSchedule.description;
          $scope.schedules[i].modifiedBy = sourceSchedule.modifiedBy;
          $scope.schedules[i].modifiedDate = sourceSchedule.modifiedDate;
        }
      }
    }

    Schedule.update({id:schedule.id}, schedule, function (data) {
      updateUiData(data.schedule);
      $scope.message = "Schedule Saved Successfully";
      $scope.error = "";
      $scope.newSchedule = {};
      $scope.editSchedule = {};
    }, function (data) {
      $scope.message = "";
      $scope.error = data.data.error;
    });
  }

  $scope.scheduleLoaded = function () {
    return !($scope.schedules == undefined || $scope.schedules == null);
  }

  $scope.startScheduleEdit = function (scheduleUnderEdit) {
    $scope.editSchedule.code = scheduleUnderEdit.code;
    $scope.editSchedule.name = scheduleUnderEdit.code;
    $scope.editSchedule.description = scheduleUnderEdit.code;
  }

  $scope.endScheduleEdit = function (scheduleUnderEdit) {
    scheduleUnderEdit.code = $scope.editSchedule.code;
    scheduleUnderEdit.name = $scope.editSchedule.code;
    scheduleUnderEdit.description = $scope.editSchedule.code;
  }
}