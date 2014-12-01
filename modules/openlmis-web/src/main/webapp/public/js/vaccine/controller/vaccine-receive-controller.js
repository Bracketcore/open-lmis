/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function VaccineReceiveController($scope,$route,VaccineDistributionBatches,$location,messageService,GetDonors,Products,Manufacturers,DistributionTypes,VaccineDistributionStatus,VaccineStorageByFacility,GeographicZones,ReceiveVaccines){

    $scope.message = "";

    $scope.selectedStorages = [];
    $scope.batches = [];

    if(!isUndefined($route.current.params.distributionBatchId)){
        VaccineDistributionBatches.get({id: $route.current.params.distributionBatchId}, function (data){

            $scope.distributionBatch = $scope.getDistributionBatchWithDateObjects(data.distributionBatch);

        });
    }
    if(!isUndefined($route.current.params.facilityId)){
        $scope.selectedFacilityId = $route.current.params.facilityId;
    }


    $scope.convertStringToCorrectDateFormat = function(stringDate) {
        if (stringDate) {
            return stringDate.split("-").reverse().join("-");
        }
        return null;
    };

    $scope.getDistributionBatchWithDateObjects = function(distributionBatch) {
        if(!isUndefined(distributionBatch)){
            distributionBatch.productionDate = $scope.convertStringToCorrectDateFormat(distributionBatch.stringProductionDate);
            distributionBatch.expiryDate = $scope.convertStringToCorrectDateFormat(distributionBatch.stringExpiryDate);
            distributionBatch.receiveDate = $scope.convertStringToCorrectDateFormat(distributionBatch.stringReceiveDate);
            distributionBatch.recallDate = $scope.convertStringToCorrectDateFormat(distributionBatch.stringRecallDate);
        }

        return distributionBatch;
    };

    $scope.origins = [{id:0,name:'France'},{id:1,name:'USA'}];

    VaccineDistributionStatus.get({}, function(data){
       $scope.status = data.status;
        $scope.receivedStatus = [];
       angular.forEach($scope.status, function(status){
           if(!isUndefined(status.transactionType) && status.transactionType.name === "Received"){
               $scope.receivedStatus.push(status);
           }
       });
    });

    DistributionTypes.get({}, function(data){
        $scope.distributionTypes = data.distributionTypes;
    });
    Manufacturers.get({}, function (data) {
        $scope.manufacturers = data.manufacturers;
    });
    GetDonors.get({},function(data){
        $scope.donors = data.donors;
    });

    Products.get({}, function(data){
        $scope.products = data.productList;
    });

    $scope.zones = [];
    GeographicZones.get({},function(data){
        angular.forEach(data.zones, function(zone){
           if(zone.levelId == 2){
               $scope.zones.push(zone);
           }
        });

        $scope.zones.unshift({id:0, name:'MSD HQ'});
    });

    VaccineStorageByFacility.get({facilityId:$scope.selectedFacilityId}, function(data){
        $scope.storages = data.vaccineStorageList;
        $scope.groupedByStorageTypes = [];
        angular.forEach($scope.storages, function(storage){
            var groupedByTypes =_.filter($scope.storages,function(location){if(location.storageType.id == storage.storageType.id){return location;}});
            if(isUndefined(_.findWhere($scope.groupedByStorageTypes,{storageTypeName:storage.storageType.name}))){
                $scope.groupedByStorageTypes.push({storageTypeName:storage.storageType.name, locations:groupedByTypes});
            }
        });
    });
    $scope.cancelDistributionBatchSave = function () {
        $location.path('#/distribution-batch');
    };


    $scope.addBatches = function (distribution) {
        $scope.batch = undefined;
        $scope.distribution = distribution;
        $scope.addBatchesModal = true;
    };
    $scope.resetAddBatchesModal = function () {
        $scope.addBatchesModal = false;
        $scope.error = undefined;
        $scope.distribution = undefined;
    };

    $scope.saveDistributionBatch = function(){

        if ($scope.inventoryTransactionForm.$error.required) {
            $scope.error = messageService.get("form.error");
            $scope.showError = true;
            return false;
        }

        var successHandler = function (msgKey) {
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = messageService.get(msgKey, $scope.inventoryTransaction.id);
            $scope.$parent.inventoryTransactionId = $scope.inventoryTransaction.id;
            $location.path('/');
        };

        var saveSuccessHandler = function (response) {
            $scope.inventoryTransaction = response.receiveVaccine.inventoryTransaction;
            successHandler(response.success);
        };

        var updateSuccessHandler = function () {
            successHandler("message.distribution.batch.updated.success");
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.message = "";
            $scope.error = response.data.error;
        };
        $scope.inventoryTransaction.confirmedBy = {id:2};
        $scope.inventoryTransaction.fromFacility = {id:$scope.selectedFacilityId};
        $scope.inventoryTransaction.toFacility = {id:$scope.selectedFacilityId};

        var receiveVaccine = {inventoryTransaction:$scope.inventoryTransaction, inventoryBatches:$scope.batches};

        if ($scope.inventoryTransaction.id) {
            ReceiveVaccines.update({id:$scope.distributionBatch.id}, receiveVaccine, updateSuccessHandler, errorHandler);
        } else {
            ReceiveVaccines.save({}, receiveVaccine, saveSuccessHandler, errorHandler);
        }
    };

    $scope.saveBatches = function(){

        $scope.batches.push($scope.batch);
        $scope.addBatchesModal = undefined;
    };
}
