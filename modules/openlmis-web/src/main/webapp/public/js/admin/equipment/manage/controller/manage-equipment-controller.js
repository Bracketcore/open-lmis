/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ManageEquipmentController($scope, $routeParams, $location, Equipments,EquipmentTypes,EquipmentType) {

//  Equipments.get(function (data) {
//    $scope.equipments = data.equipments;
//  });

   EquipmentTypes.get(function (data) {
      $scope.equipmentTypes = data.equipment_type;
    });

   $scope.listEquipments=function()
   {
      var id=$scope.equipmentTypeId;
      EquipmentType.get({
            id: id
          }, function (data) {
            $scope.equipment_type = data.equipment_type;
            if($scope.equipment_type.coldChain)
            {
              Equipments.get({
                type:"cce"
              },function (data) {
                  $scope.equipments = data.equipments;
                });
            }
            else{
              Equipments.get({
                 type:"noncce"
              },function (data) {
                   $scope.equipments = data.equipments;
                 });
            }
          });
   }
}