function UserProgramRoleListController($scope) {

  $scope.deleteCurrentRow = function (rowNum) {
    $scope.user.roleAssignments.splice(rowNum, 1);
  };

  $scope.availablePrograms = function () {
    return $scope.$parent.allSupportedPrograms;
  };

  $scope.showRoleAssignmentOptions = function () {
    return ($scope.user != null && $scope.user.facilityId != null)
  };

  $scope.addRole = function () {
    if (isPresent($scope.programSelected) && isPresent($scope.selectedRoleIds)) {
      var newRoleAssignment = {programId:$scope.programSelected, roleIds:$scope.selectedRoleIds};
      addRoleAssignment(newRoleAssignment);
      clearCurrentSelection();
    }

    function clearCurrentSelection() {
      $scope.programSelected = null;
      $scope.selectedRoleIds = null;
    }

    function addRoleAssignment(newRoleAssignment) {
      if (!$scope.user.roleAssignments) {
        $scope.user.roleAssignments = [];
      }
      $scope.user.roleAssignments.push(newRoleAssignment);
    }

    function isPresent(obj) {
      return obj != undefined && obj != null;
    }
  };

  $scope.getProgramName = function (programId) {
    if (!$scope.$parent.allSupportedPrograms) return;
    var programName = null;
    $.each($scope.$parent.allSupportedPrograms, function (index, supportedProgram) {
      if (supportedProgram.id == programId) {
        programName = supportedProgram.name;
        return false;
      }
    });
    return programName;
  };
}