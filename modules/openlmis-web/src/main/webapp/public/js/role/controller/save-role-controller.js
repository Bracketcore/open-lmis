function SaveRoleController($scope, $routeParams,$location, Roles, Role, Rights) {
  $scope.$parent.error = "";
  $scope.$parent.message = "";
  $scope.role = {rights:[]};

  if ($routeParams.id) {
    Role.get({id:$routeParams.id}, function (data) {
      $scope.role = data.role;
    });
  }

  Rights.get({}, function (data) {
    $scope.rights = data.rights;
  }, {});

  $scope.updateRights = function (checked, rightClicked) {
    if (checked == true) {
      $scope.role.rights.push(rightClicked);
    } else {
      deleteRight(rightClicked);
    }
  };

  function deleteRight(rightClicked) {
    $.each($scope.role.rights, function (index, rightObj) {
      if (rightClicked.right == rightObj.right) {
        $scope.role.rights.splice(index, 1);
      }
    });
  }

  $scope.contains = function (right) {
    var containFlag = false;
    $($scope.role.rights).each(function (index, assignedRight) {
      if (assignedRight.right == right) {
        containFlag = true;
        return false;
      }
    });
    return containFlag;
  };

  $scope.saveRole = function () {
    var errorHandler = function (data) {
      $scope.error = data.data.error;
      $scope.message = "";
    };

    var successHandler = function (data) {
      $scope.$parent.message = data.success;
      $scope.$parent.$error = "";
      $location.path('list');
    };

    if ($scope.roleForm.$invalid) {
      $scope.showError = true;
    } else {
      var id = $routeParams.id;
      if (id) {
        Role.update({id:id}, $scope.role, successHandler, errorHandler);
      } else {
        Roles.save({}, $scope.role, successHandler, errorHandler);
      }
    }
  }
}
