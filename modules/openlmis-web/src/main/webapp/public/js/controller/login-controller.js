function LoginController($scope, $http, $rootScope) {
    $scope.doLogin = function (targetUrl) {
        var data = $.param({j_username:$scope.username, j_password:$scope.password});
        $http({
            method:'POST',
            url:'/j_spring_security_check',
            data:data,
            headers:{'Content-Type':'application/x-www-form-urlencoded'}
        }).success(function (data) {
                if (data.authenticated == "true") {
                    if (targetUrl)      {
                        window.location = targetUrl;
                    }
                    else {
                        location.reload();
                    }
                } else if(data.error == "true") {
                    $scope.error = "The username or password you entered is incorrect. Please try again.";
                }
            }).
            error(function (data) {
                alert("Error");
            });
    }
}