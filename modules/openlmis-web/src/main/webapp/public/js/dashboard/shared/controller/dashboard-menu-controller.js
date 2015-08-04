
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function DashboardMenuController($scope, $location, dashboardMenuService, UserSupervisedActivePrograms, GetLastPeriods) {

    $scope.dashboardTabs = dashboardMenuService.tabs;
    $scope.dashboardTabs = [];
    UserSupervisedActivePrograms.get(function(data){
        $scope.programsList = data.programs;
        angular.forEach(data.programs, function(prog){

            dashboardMenuService.addTab("'"+prog.name+"'",'/public/pages/dashboard/index.html#/dashboard-programs?prog='+prog.name,prog.name,false, prog.id);
           // alert(prog.name);
        });
        $scope.dashboardTabs = dashboardMenuService.tabs;
     //   dashboardMenuService.tabs = [];

    });


    GetLastPeriods.get({}, function(data){
        $scope.lastPeriods = data.lastPeriods;
        dashboardMenuService.tabs = [];
        angular.forEach( $scope.lastPeriods, function(period){

            dashboardMenuService.addTab("'"+period.name+"'",'/public/pages/dashboard/index.html#/dashboard-programs?prog='+$scope.$parent.currentTab +'&period='+period.name,period.name,false, period.id);
        });
        $scope.dashboardPeriodTabs = dashboardMenuService.tabs;
        dashboardMenuService.tabs = [];

    });

    $scope.$on('dashboardTabUpdated', function(){
       // $scope.dashboardTabs = dashboardMenuService.tabs;
    });
    $scope.closeTab = function(tabName){
        dashboardMenuService.closeTab(tabName);
    };

}

