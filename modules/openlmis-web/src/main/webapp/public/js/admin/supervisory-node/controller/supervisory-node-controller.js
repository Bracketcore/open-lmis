/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function SupervisoryNodeController($scope,$dialog,messageService, ReportFacilityTypes, $routeParams, $location, SupervisoryNodeCompleteList, SaveSupervisoryNode, GetSupervisoryNode, GeographicZoneCompleteList, GetFacilityCompleteList,RemoveSupervisoryNode, GetRequisitionGroupsForSupervisoryNode) {
    $scope.geographicZoneNameInvalid = false;
    $scope.supervisoryNode = {};
    $scope.facilities = {};
    $scope.geographicZones = {};
    $scope.message={};
    $scope.facilitiesLoaded = false;

    if ($routeParams.supervisoryNodeId) {
        GetSupervisoryNode.get({id: $routeParams.supervisoryNodeId}, function (data) {
            $scope.supervisoryNode = data.supervisoryNode;
        }, {});
    }

    SupervisoryNodeCompleteList.get(function (data) {
        $scope.supervisoryNodes = data.supervisoryNodes;
        $scope.supervisoryNodes.push("");
    });

    GeographicZoneCompleteList.get(function(data){
        $scope.geographicZones = data.geographicZones;
    });

    GetFacilityCompleteList.get(function(data){
        $scope.allFacilities = $scope.allFacilitiesFiltered = data.facilities;
        $scope.facilitiesLoaded = true;
    });

    $scope.facilityTypes = ReportFacilityTypes.get(function(data){
        $scope.facilityTypes = data.facilityTypes;
    });

    $scope.saveSupervisoryNode = function () {
        var successHandler = function (response) {
            $scope.supervisoryNode = response.supervisoryNode;
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = response.success;
            $scope.$parent.supervisoryNodeId = $scope.supervisoryNode.id;
            $location.path('');
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = response.data.error;
        };

        if(!$scope.supervisoryNode.facility || !$scope.supervisoryNode.name || !$scope.supervisoryNode.code){
            $scope.showError = true;
            $scope.error = "Please fill in all required fields.";
            return false;
        }

        if(!$scope.supervisoryNode.parent.id){
            $scope.supervisoryNode.parent = null;
        }

        SaveSupervisoryNode.save($scope.supervisoryNode,successHandler,errorHandler);

        return true;
    };

    $scope.saveSupervisoryNodeMember=function(){
        $scope.closeModal();
        return true;
    };

    $scope.validateSupervisoryNodeName = function () {
        $scope.supervisoryNodeNameInvalid = $scope.supervisoryNode.name === null;
    };

    $scope.associateFacility=function(){
        $scope.allFacilitiesFiltered = $scope.allFacilities;
        $scope.supervisoryNodeMemberModal = true;
    };

    $scope.closeModal=function(){
        $scope.geographicZone = null;
        $scope.supervisoryNodeMember = null;
        $scope.facilityType = null;
        $scope.allFacilitiesFiltered = null;
        $scope.supervisoryNodeMemberModal = false;
    };

    $scope.filterFacilityList=function(){
        $scope.allFacilitiesFiltered=[];
        if($scope.facilityType === null && $scope.geographicZone === null){
            $scope.allFacilitiesFiltered = $scope.allFacilities;
        }
        else{
            angular.forEach($scope.allFacilities,function(facility){
                if($scope.facilityType){
                    if(facility.facilityType.id == $scope.facilityType.id){
                        $scope.allFacilitiesFiltered.push(facility);
                    }
                }
                else if($scope.geographicZone){
                    if(facility.geographicZone.id === $scope.geographicZone.id){
                        $scope.allFacilitiesFiltered.push(facility);
                    }
                }
            });
        }
    };

    $scope.showRemoveSupervisoryNodeMemberConfirmDialog = function () {
        $scope.selectedSupervisoryNode = $scope.supervisoryNode;

        GetRequisitionGroupsForSupervisoryNode.get({supervisoryNodeId:$scope.supervisoryNode.id}, function(data){

            var requisitionGroupsUnderSN = data.requisitionGroups;

            if(requisitionGroupsUnderSN.length > 0){
                $scope.showError = true;
                $scope.error = "Supervisory node has requisition groups under it.  Please first remove the requisition groups!";
                return false;
            }

            var options = {
                id: "removeSupervisoryNodeMemberConfirmDialog",
                header: "Confirmation",
                body: "Are you sure you want to remove the supervisory node: " + $scope.selectedSupervisoryNode.name
            };
            OpenLmisDialog.newDialog(options, $scope.removeSupervisoryNodeMemberConfirm, $dialog, messageService);

        });

    };

    $scope.removeSupervisoryNodeMemberConfirm = function (result) {
        if (result) {
            $scope.removeSupervisoryNode($scope.selectedSupervisoryNode.id);
            $scope.$parent.reloadTheList = true;
            $scope.$parent.message = "Supervisory node: " + $scope.selectedSupervisoryNode.name + " has been successfully removed. ";
            $location.path('#/list');

        }
        $scope.selectedSupervisoryNode = undefined;
    };

    $scope.removeSupervisoryNode = function(id){
        RemoveSupervisoryNode.get({id: id});
    };
}

