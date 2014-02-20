/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function AdultCoverageController($scope, $routeParams, distributionService) {
  $scope.distribution = distributionService.distribution;
  $scope.selectedFacilityId = $routeParams.facility;
  $scope.adultCoverage = $scope.distribution.facilityDistributions[$scope.selectedFacilityId].adultCoverage;

  $scope.categories = {
    PREGNANT_WOMEN: 'Pregnant Women',
    MIF_COMMUNITY: 'MIF 15-49 years - Community',
    MIF_STUDENTS: 'MIF 15-49 years - Students',
    MIF_WORKERS: 'MIF 15-49 years - Workers',
    STUDENTS_NOT_MIF: 'Students not MIF',
    WORKERS_NOT_MIF: 'Workers not MIF',
    OTHER_NOT_MIF: 'Other not MIF'
  };

  var convertListToMap = function () {
    var map = {};
    $scope.adultCoverage.adultCoverageLineItems.forEach(function (lineItem) {
      map[lineItem.demographicGroup] = lineItem;
    });
    return map;
  };

$scope.adultCoverageMap = convertListToMap();
}
