/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller.equipment;

import org.openlmis.core.exception.DataException;
import org.openlmis.equipment.domain.ProgramEquipmentType;
import org.openlmis.equipment.service.ProgramEquipmentTypeProductService;
import org.openlmis.equipment.service.ProgramEquipmentTypeService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping(value = "/equipment/program-equipment/")
public class ProgramEquipmentTypeController extends BaseController {

  @Autowired
  ProgramEquipmentTypeService programEquipmentTypeService;

  @Autowired
  ProgramEquipmentTypeProductService programEquipmentTypeProductService;

  @RequestMapping(value = "save", method = RequestMethod.POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody ProgramEquipmentType programEquipmentType, HttpServletRequest request){
    ResponseEntity<OpenLmisResponse> successResponse;

    Long userId = loggedInUserId(request);
    Date date = new Date();

    if(programEquipmentType.getId() == null){
      programEquipmentType.setCreatedBy(userId);
      programEquipmentType.setCreatedDate(date);
      programEquipmentType.setEnableTestCount(false);
      programEquipmentType.setEnableTotalColumn(false);
      programEquipmentType.setDisplayOrder(0);
    }

    programEquipmentType.setModifiedBy(userId);
    programEquipmentType.setModifiedDate(date);

    try {
      programEquipmentTypeService.Save(programEquipmentType);
    }
    catch (DataException e){
      return OpenLmisResponse.error(e, HttpStatus.BAD_REQUEST);
    }

    successResponse = OpenLmisResponse.success("Program Equipment association successfully saved.");
    successResponse.getBody().addData("programEquipment", programEquipmentType);
    return successResponse;
  }

  @RequestMapping(value = "getByProgram/{programId}",method = RequestMethod.GET,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getProgramEquipmentByProgram(@PathVariable(value="programId") Long programId){
    return OpenLmisResponse.response("programEquipments", programEquipmentTypeService.getByProgramId(programId));
  }

    @RequestMapping(value="remove/{programEquipmentId}")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
    public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value = "programEquipmentId") Long programEquipmentId){
        ResponseEntity<OpenLmisResponse> successResponse;

        try{
            //remove the program_equipment_products first
            programEquipmentTypeProductService.removeAllByEquipmentProducts(programEquipmentId);

            //then  go for the program_equipment
            programEquipmentTypeService.remove(programEquipmentId);
        }
        catch(DataException e){
            return OpenLmisResponse.error(e,HttpStatus.BAD_REQUEST);
        }

        successResponse = OpenLmisResponse.success("Program equipment successfully removed.");
        return successResponse;
    }
}