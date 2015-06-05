/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ManageEquipmentController($scope, $routeParams,$dialog, $location,messageService, Equipments,EquipmentTypes,EquipmentType,RemoveEquipment,currentEquipmentTypeId,ProgramCompleteList,EquipmentTypesByProgram,currentProgramId,ColdChainPqsStatus,SaveEquipment,$timeout) {

    //Load All Equipment Types if No program filter applied
    $scope.getAllEquipmentTypes = function () {
        EquipmentTypes.get(function (data) {
          $scope.equipmentTypes = data.equipment_type;
          });
     };

    //Load Equipment types by program if program filter applied
    $scope.getAllEquipmentTypesByProgram = function (initLoad) {
            currentProgramId.set($scope.programId);
            if(initLoad){
                    $scope.equipments={};
                    $scope.equipmentTypeId=undefined;
                    currentEquipmentTypeId.set($scope.equipmentTypeId);
             }
            EquipmentTypesByProgram.get({programId: $scope.programId}, function (data) {
              //Re load all if no match found
              if(data.equipment_types.length === 0)
              {
                $scope.getAllEquipmentTypes();
              }
              else{
                $scope.equipmentTypes = data.equipment_types;
              }
              });
         };

    $scope.getAllPrograms = function () {
        ProgramCompleteList.get(function (data) {
          $scope.programs = data.programs;
        });
      };


   $scope.listEquipments=function()
   {
      var id=$scope.equipmentTypeId;
      currentEquipmentTypeId.set($scope.equipmentTypeId);
      Equipments.get({
           equipmentTypeId:id
           },function (data) {
               $scope.equipments = data.equipments;
       });
      EquipmentType.get({
            id: id
          }, function (data) {
            $scope.equipment_type = data.equipment_type;
       });

      ColdChainPqsStatus.get(function (data) {
              $scope.pqsStatus = data.pqs_status;
       });

   };

   var ASC=true;
   $scope.sortBy=function(title){
        if(ASC){
          $scope.orderTitle=title;
          ASC=false;
        }
        else{
          $scope.orderTitle='-'+title;
          ASC=true;
        }
   };

   $scope.showRemoveEquipmentConfirmDialog = function (id) {
       $scope.selectedEquipment=id;
       var options = {
         id: "removeEquipmentConfirmDialog",
         header: "Confirmation",
         body: "Are you sure you want to remove the Equipment"
       };
       OpenLmisDialog.newDialog(options, $scope.removeEquipmentConfirm, $dialog, messageService);
     };

     $scope.removeEquipmentConfirm = function (result) {
         if (result) {
           RemoveEquipment.get({equipmentTypeId:$scope.equipmentTypeId, id: $scope.selectedEquipment}, function (data) {
             $scope.$parent.message = messageService.get(data.success);
             $timeout(function () {
                 $scope.$parent.message = false;
             }, 3000);
             $scope.listEquipments();
           }, function () {
             $scope.error = messageService.get(data.error);
           });

         }
         $scope.selectedEquipment=undefined;
       };

     $scope.getAllPrograms();
     $scope.programId=currentProgramId.get();

     if( $scope.programId !== undefined)
     {
         $scope.getAllEquipmentTypesByProgram(false);
     }
     else{
         $scope.getAllEquipmentTypes();
     }

     $scope.equipmentTypeId=currentEquipmentTypeId.get();
     if( $scope.equipmentTypeId !== undefined)
     {
          $scope.listEquipments();
     }

     $scope.updatePqsStatus=function(eq){
        $scope.equipment=eq;
        $scope.equipment.equipmentTypeName = "coldChainEquipment";
        var onSuccess = function(data){
             eq.showSuccess = true;
             $timeout(function () {
                eq.showSuccess = false;
              }, 2000);
          };
         var onError = function(data){

         };
        SaveEquipment.save($scope.equipment, onSuccess, onError);
     };
     if($scope.$parent.message)
     {
        $timeout(function () {
          $scope.$parent.message = false;
        }, 3000);
     }

}