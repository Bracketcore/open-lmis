function ListRoleController($scope, AllRoles) {
  AllRoles.get({}, function(data) {
       $scope.roles = data.roles;
  }, {}
  );
}
