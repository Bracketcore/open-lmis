/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function DistributionController(DeliveryZoneFacilities, Refrigerators, deliveryZones, DeliveryZoneActivePrograms, messageService, DeliveryZoneProgramPeriods, navigateBackService, $http, $dialog, $scope, $location, $q, distributionService) {
  $scope.deliveryZones = deliveryZones;
  var DELIVERY_ZONE_LABEL = messageService.get('label.select.deliveryZone');
  var NONE_ASSIGNED_LABEL = messageService.get('label.noneAssigned');
  var DEFAULT_PROGRAM_MESSAGE = messageService.get('label.select.program');
  var DEFAULT_PERIOD_MESSAGE = messageService.get('label.select.period');

  $scope.zonePlaceholder = !!$scope.deliveryZones.length ? DELIVERY_ZONE_LABEL : NONE_ASSIGNED_LABEL;

  $scope.loadPrograms = function () {
    $scope.programs = $scope.periods = [];
    DeliveryZoneActivePrograms.get({zoneId: $scope.selectedZone.id}, function (data) {
      $scope.programs = data.deliveryZonePrograms;
      if ($scope.selectedProgram && $scope.fromBackNavigation) {
        $scope.selectedProgram = _.where($scope.programs, {id: $scope.selectedProgram.id})[0];
        $scope.loadPeriods();
      }
    }, function (data) {
      $scope.error = data.data.error;
    });
  };

  $scope.loadPeriods = function () {
    $scope.periods = [];
    DeliveryZoneProgramPeriods.get({zoneId: $scope.selectedZone.id, programId: $scope.selectedProgram.id}, function (data) {
      $scope.periods = data.periods.length ? data.periods.slice(0, 13) : [];
      if ($scope.selectedPeriod && $scope.fromBackNavigation) {
        $scope.fromBackNavigation = false;
        $scope.selectedPeriod = _.where($scope.periods, {id: $scope.selectedPeriod.id})[0];
      } else {
        $scope.selectedPeriod = $scope.periods.length ? $scope.periods[0] : NONE_ASSIGNED_LABEL;
      }
    }, function (data) {
      $scope.error = data.data.error;
    });
  };

  $scope.programOptionMessage = function () {
    return optionMessage($scope.programs, DEFAULT_PROGRAM_MESSAGE);
  };

  $scope.periodOptionMessage = function () {
    return optionMessage($scope.periods, DEFAULT_PERIOD_MESSAGE);
  };

  $scope.initiateDistribution = function () {

    var message;
    var distribution = {deliveryZone: $scope.selectedZone, program: $scope.selectedProgram, period: $scope.selectedPeriod};

    if (distributionService.isCached(distribution)) {
      $scope.message = messageService.get("message.distribution.already.cached",
        $scope.selectedZone.name, $scope.selectedProgram.name, $scope.selectedPeriod.name);
      return;
    }

    var distributionDefer = $q.defer();

    (function cacheDistribution() {
      $http.post('/distributions.json', distribution).success(onInitSuccess);

      function onInitSuccess(data, status) {
        message = data.success;
        if (status == 201) {
          distributionDefer.resolve(data.distribution);
        } else {
          distribution = data.distribution;
          var dialogOpts = {
            id: "distributionInitiated",
            header: messageService.get('label.distribution.initiated'),
            body: data.message
          };
          OpenLmisDialog.newDialog(dialogOpts, callback(), $dialog, messageService);
        }
      }

      function callback() {
        return function (result) {
          if (result) {
            distributionDefer.resolve(distribution);
          } else {
            distributionDefer.reject();
          }
        };
      }
    })();

    var referenceDataDefer = $q.defer();

    DeliveryZoneFacilities.get({"programId": $scope.selectedProgram.id, "deliveryZoneId": $scope.selectedZone.id }, onDeliveryZoneFacilitiesGetSuccess, {});

    function onDeliveryZoneFacilitiesGetSuccess(data) {
      if (data.facilities.length > 0) {
        var referenceData = {facilities: data.facilities};
        Refrigerators.get({"deliveryZoneId": $scope.selectedZone.id, "programId": $scope.selectedProgram.id}, function (data) {
          referenceData.refrigerators = data.refrigerators;
          referenceDataDefer.resolve(referenceData);
        }, {});
      } else {
        referenceDataDefer.reject();
        $scope.message = messageService.get("message.no.facility.available", $scope.selectedProgram.name,
          $scope.selectedZone.name);
      }
    }

    $q.all([distributionDefer.promise, referenceDataDefer.promise]).then(function (resolved) {
      var distribution = resolved[0];
      var referenceData = resolved[1];

      distributionService.put(distribution, referenceData);

      $scope.message = message;
    });
  };

  var optionMessage = function (entity, defaultMessage) {
    return utils.isEmpty(entity) ? NONE_ASSIGNED_LABEL : defaultMessage;
  };

  $scope.viewLoadAmount = function () {
    var data = {
      deliveryZone: $scope.selectedZone,
      program: $scope.selectedProgram,
      period: $scope.selectedPeriod
    };
    navigateBackService.setData(data);
    var path = "/view-load-amounts/".concat($scope.selectedZone.id).concat("/")
      .concat($scope.selectedProgram.id).concat("/").concat($scope.selectedPeriod.id);
    $location.path(path);
  };
}

DistributionController.resolve = {
  deliveryZones: function (UserDeliveryZones, $timeout, $q) {
    var deferred = $q.defer();
    $timeout(function () {
      UserDeliveryZones.get({}, function (data) {
        deferred.resolve(data.deliveryZones);
      }, {});
    }, 100);
    return deferred.promise;
  }
};

