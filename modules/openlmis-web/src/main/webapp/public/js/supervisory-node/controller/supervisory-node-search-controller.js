/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function SupervisoryNodeSearchController($scope, $location, navigateBackService, SupervisoryNodesSearch) {

  $scope.searchOptions = [
    {value: "node", name: "option.value.supervisory.node"},
    {value: "parent", name: "option.value.supervisory.node.parent"}
  ];

  $scope.error = false;
  $scope.currentPage = 1;
  $scope.selectedSearchOption = navigateBackService.selectedSearchOption || $scope.searchOptions[0];

  $scope.selectSearchType = function (searchOption) {
    $scope.selectedSearchOption = searchOption;
  };

  $scope.$on('$viewContentLoaded', function () {
    $scope.query = navigateBackService.query;
  });

  $scope.search = function (page) {
    if (!$scope.query) return;
    $scope.query = $scope.query.trim();
    $scope.searchedQuery = $scope.query;
    var searchOption = $scope.selectedSearchOption.value === 'parent';

    SupervisoryNodesSearch.get({page: page, param: $scope.searchedQuery, parent: searchOption}, function (data) {
      $scope.supervisoryNodeList = data.supervisoryNodes;
      $scope.pagination = data.pagination;
      $scope.resultCount = $scope.pagination.totalRecords;
      $scope.currentPage = $scope.pagination.page;
      $scope.error = true;
    }, {});
  };

  $scope.$watch('currentPage', function () {
    if ($scope.currentPage !== 0)
      $scope.search($scope.currentPage);
  });

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.resultCount = 0;
    $scope.supervisoryNodeList = [];
    $scope.error = false;
    angular.element("#searchSupervisoryNode").focus();
  };

  $scope.triggerSearch = function (event) {
    if (event.keyCode === 13) {
      $scope.search(1);
    }
  };
}