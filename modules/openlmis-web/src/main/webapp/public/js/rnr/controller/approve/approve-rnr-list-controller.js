/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ApproveRnrListController($scope, requisitionList, $location, messageService) {
  $scope.requisitions = requisitionList;
  $scope.filteredRequisitions = $scope.requisitions;
  $scope.selectedItems = [];

  $scope.gridOptions = { data: 'filteredRequisitions',
    multiSelect: false,
    selectedItems: $scope.selectedItems,
    afterSelectionChange: function (rowItem, event) {
      $scope.openRnr()
    },
    showFooter: false,
    showSelectionCheckbox: false,
    enableColumnResize: true,
    showColumnMenu: false,
    sortInfo: { fields: ['submittedDate'], directions: ['asc'] },
    showFilter: false,
    columnDefs: [
      {field: 'programName', displayName: messageService.get("program.header") },
      {field: 'facilityCode', displayName: messageService.get("option.value.facility.code")},
      {field: 'facilityName', displayName: messageService.get("option.value.facility.name")},
      {field: 'periodStartDate', displayName: messageService.get("label.period.start.date"), cellFilter: "date:'dd/MM/yyyy'"},
      {field: 'periodEndDate', displayName: messageService.get("label.period.end.date"), cellFilter: "date:'dd/MM/yyyy'"},
      {field: 'submittedDate', displayName: messageService.get("label.date.submitted"), cellFilter: "date:'dd/MM/yyyy'"},
      {field: 'modifiedDate', displayName: messageService.get("label.date.modified"), cellFilter: "date:'dd/MM/yyyy'"}
    ]
  };

  $scope.openRnr = function () {
    $scope.$parent.period = {'startDate': $scope.selectedItems[0].periodStartDate, 'endDate': $scope.selectedItems[0].periodEndDate};
    $location.url("rnr-for-approval/" + $scope.selectedItems[0].id + '/' + $scope.selectedItems[0].programId + '?supplyType=full-supply&page=1');
  };

  $scope.filterRequisitions = function () {
    $scope.filteredRequisitions = [];
    var query = $scope.query || "";
    var searchField = $scope.searchField;

    $scope.filteredRequisitions = $.grep($scope.requisitions, function (rnr) {
      return (searchField) ? contains(rnr[searchField], query) : matchesAnyField(query, rnr);
    });

    $scope.resultCount = $scope.filteredRequisitions.length;
  };

  function contains(string, query) {
    return string.toLowerCase().indexOf(query.toLowerCase()) != -1;
  }

  function matchesAnyField(query, rnr) {
    var rnrString = "|" + rnr.programName + "|" + rnr.facilityName + "|" + rnr.facilityCode + "|";
    return contains(rnrString, query);
  }
}

ApproveRnrListController.resolve = {
  requisitionList: function ($q, $timeout, RequisitionForApproval) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionForApproval.get({}, function (data) {
        deferred.resolve(data.rnr_list);
      }, {});
    }, 100);
    return deferred.promise;
  }
};