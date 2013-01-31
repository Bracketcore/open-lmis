function UserController($scope, $routeParams, Users, SearchFacilitiesByCodeOrName, Facility, Roles, UserById) {
  $scope.programAndRoleList = [];
  $scope.userNameInvalid = false;


  if ($routeParams.userId) {
    UserById.get({id:$routeParams.userId}, function (data) {
      $scope.user = data.user;
      loadRoleAssignments();
    }, {});
  } else {
    $scope.user={};
  }

  $scope.saveUser = function () {
    Users.save({}, $scope.user, function (data) {
      $scope.user = data.user;
      $scope.showError = false;
      $scope.error = "";
      $scope.message = data.success;
    }, function (data) {
      $scope.showError = true;
      $scope.message = "";
      $scope.error = data.data.error;
    });
  };

  $scope.validateUserName = function () {
    if ($scope.user.userName != null && $scope.user.userName.trim().indexOf(' ') >= 0) {
      $scope.userNameInvalid = true;
    } else {
      $scope.userNameInvalid = false;
    }
  };

  $scope.showFacilitySearchResults = function () {
    var query = $scope.query;
    var len = (query == undefined) ? 0 : query.length;

    if (len >= 3) {
      if (len == 3) {
        SearchFacilitiesByCodeOrName.get({searchParam:query}, function (data) {
          $scope.facilityList = data.facilityList;
          $scope.filteredFacilities = $scope.facilityList;
          $scope.resultCount = $scope.filteredFacilities.length;
        }, {});
      }
      else {
        filterFacilitiesByCodeOrName();
      }
    }
  };

  $scope.setSelectedFacility = function (facility) {
    $scope.user.facilityId = facility.id;
    $scope.facilitySelected = facility;
    loadRoleAssignments();
    $scope.query = null;
  };

  $scope.clearSelectedFacility = function () {
    $scope.facilitySelected = null;
    $scope.programAndRoleList = [];
    $scope.user.roleAssignments=[];

    setTimeout(function () {
      angular.element("#searchFacility").focus();
    });
  };


  var loadRoleAssignments = function () {
    if (!isNullOrUndefined($scope.user.facilityId)) {
      if (isNullOrUndefined($scope.allSupportedPrograms)) {
        Facility.get({id:$scope.user.facilityId}, function (data) {
          $scope.allSupportedPrograms = data.facility.supportedPrograms;
        });
      }

      if (isNullOrUndefined($scope.allRoles)) {
        Roles.get({}, function (data) {
          $scope.allRoles = data.roles;
        });
      }
    }
  };

  var filterFacilitiesByCodeOrName = function () {
    $scope.filteredFacilities = [];

    angular.forEach($scope.facilityList, function (facility) {
      if (facility.code.toLowerCase().indexOf($scope.query.toLowerCase()) >= 0 || facility.name.toLowerCase().indexOf($scope.query.toLowerCase()) >= 0) {
        $scope.filteredFacilities.push(facility);
      }
      $scope.resultCount = $scope.filteredFacilities.length;
    })
  };

  var isNullOrUndefined = function (obj) {
    return obj == undefined || obj == null;
  }
}

