function FacilityController($scope, FacilityReferenceData, $routeParams, $http, Facility) {


    FacilityReferenceData.get({}, function (data) {
        $scope.facilityTypes = data.facilityTypes;
        $scope.geographicZones = data.geographicZones;
        $scope.facilityOperators = data.facilityOperators;
        $scope.programs = data.programs;

//TODO Need a more elegant solution
        if ($routeParams.facilityId) {
            Facility.get({id:$routeParams.facilityId}, function (data) {
                $scope.facility = data.facility;
                populateFlags($scope);
                //TODO Need a more elegant solution
                var foo = [];
                $.each($scope.facility.supportedPrograms, function (index, supportedProgram) {
                    $.each($scope.programs, function (index, program) {
                        if (supportedProgram.code == program.code) {
                            program.active = supportedProgram.active;
                            foo.push(program);
                        }
                    })
                });
                $scope.facility.supportedPrograms = foo;
            }, {});
        }
        else {
            $scope.facility = {};
            $scope.facility.dataReportable = "true";
        }

    }, {});

    $scope.saveFacility = function () {
        if ($scope.facilityForm.$error.pattern || $scope.facilityForm.$error.required) {
            $scope.showError = "true";
            $scope.error = "There are some errors in the form. Please resolve them.";
            $scope.message = "";
        }
        else {
            $http.post('/admin/facility.json', $scope.facility).success(function (data) {
                $scope.showError = "true";
                $scope.error = "";
                $scope.message = data.success;
            }).error(function (data) {
                    $scope.showError = "true";
                    $scope.message = "";
                    $scope.error = data.error;
                });
        }
    };
    var postFacilityRequest = function (requestUrl) {

        $http.post(requestUrl, $scope.facility).success(function (data) {
            $scope.showError = "true";
            $scope.error = "";
            $scope.message = data.success;
            $scope.facility = data.facility;
            populateFlags($scope);
        }).error(function (data) {
                $scope.showError = "true";
                $scope.message = "";
                $scope.error = data.error;
                $scope.facility = facility;
                populateFlags();
            });
    }
    $scope.deleteFacility = function () {
        postFacilityRequest('/admin/facility/update/delete.json');
    }

    $scope.restoreFacility = function (active) {
        $scope.facility.active = active;
        postFacilityRequest('/admin/facility/update/restore.json');
    }
}

var populateFlags = function ($scope) {
    $(['suppliesOthers', 'sdp', 'hasElectricity', 'online', 'hasElectronicScc', 'hasElectronicDar', 'active', 'dataReportable']).each(function (index, field) {
        var value = $scope.facility[field];
        $scope.facility[field] = (value == null) ? "" : value.toString();
    });
};
