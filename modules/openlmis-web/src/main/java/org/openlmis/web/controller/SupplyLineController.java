/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller;

/**
 * User: mahmed
 * Date: 6/19/13
 */
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.SupplyLineRepository;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.SupplyLineServiceExtension;
import org.openlmis.web.response.OpenLmisResponse;
import org.openlmis.web.response.OpenLmisResponse.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.core.exception.DataException;
import static org.openlmis.web.response.OpenLmisResponse.error;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.openlmis.core.domain.Right.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
@NoArgsConstructor
public class SupplyLineController extends BaseController {

    public static final String SUPPLYLINES= "supplylines";
    public static final String SUPPLYLINE= "supplyline";
    public static final String SUPPLYLINELIST= "supplyLineList";
    @Autowired
    private SupplyLineService supplyLineService;

    @Autowired
    private SupplyLineServiceExtension supplyLineServiceExt;

    @Autowired
    public SupplyLineController(SupplyLineService supplyLineService) {
        this.supplyLineService = supplyLineService;
    }

    // TODO: implement this function in the repository
    // and remove this reference to the mapper.
    @Autowired
    private SupervisoryNodeMapper supervisoryNodeMapper;

    @Autowired
    private ProgramService programService;

    @Autowired
    private FacilityService facilityService;

    // supply line for add/update
    @RequestMapping(value = "/supplylines", method = RequestMethod.GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLYLINE')")
    public ResponseEntity<OpenLmisResponse> getAllSupplyLine() {
        return OpenLmisResponse.response(SUPPLYLINES, supplyLineServiceExt.getAllSupplyLine());
    }

    @RequestMapping(value = "/supplylines/{id}", method = RequestMethod.GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLYLINE')")
    public ResponseEntity<OpenLmisResponse> get(@PathVariable("id") Long id) {
        try{
            SupplyLine supplyLine = supplyLineServiceExt.getSupplylineById(id);
            return OpenLmisResponse.response(SUPPLYLINE, supplyLine);
        } catch (DataException e){
            return error(e, HttpStatus.NOT_FOUND);
        }
    }

    // create
    @RequestMapping(value = "/supplylines", method = RequestMethod.POST, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLYLINE')")
    public ResponseEntity<OpenLmisResponse> create(@RequestBody SupplyLine supplyLine, HttpServletRequest request) {
        supplyLine.setModifiedBy(loggedInUserId(request));
        supplyLine.setCreatedBy(loggedInUserId(request));
        supplyLine.setCreatedDate(new Date());
        // load the supervisory node ... and attach it to the supply line object
        // this is requred by the valiation and the save.
        SupervisoryNode sn = supervisoryNodeMapper.getSupervisoryNode(Long.parseLong(supplyLine.getSupervisoryNode().getId().toString()));
        supplyLine.setSupervisoryNode(sn);
        // load the programs
        Program program = programService.getById(Long.parseLong(supplyLine.getProgram().getId().toString()));
        supplyLine.setProgram(program);

        Facility facility = facilityService.getById(Long.parseLong(supplyLine.getSupplyingFacility().getId().toString()));
        supplyLine.setSupplyingFacility(facility);

        // there has to be a better way to do the code above
        supplyLine.setModifiedDate(new Date());
        return saveSupplyline(supplyLine, true);
    }

    // update
    @RequestMapping(value = "/supplylines/{id}", method = RequestMethod.PUT, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLYLINE')")
    public ResponseEntity<OpenLmisResponse> update(@RequestBody SupplyLine supplyLine, @PathVariable("id") Long id, HttpServletRequest request) {
        supplyLine.setId(id);
        // load the supervisory node ... and attach it to the supply line object
        // this is requred by the valiation and the save.
        SupervisoryNode sn = supervisoryNodeMapper.getSupervisoryNode(Long.parseLong(supplyLine.getSupervisoryNode().getId().toString()));
        supplyLine.setSupervisoryNode(sn);
        // load the programs
        Program program = programService.getById(Long.parseLong(supplyLine.getProgram().getId().toString()));
        supplyLine.setProgram(program);
        Facility facility = facilityService.getById(Long.parseLong(supplyLine.getSupplyingFacility().getId().toString()));
        supplyLine.setSupplyingFacility(facility);
        // there has to be a better way to do the code above
        return saveSupplyline(supplyLine, false);
    }

    // save/update
    private ResponseEntity<OpenLmisResponse> saveSupplyline(SupplyLine supplyLine, boolean createOperation) {
        try {
            supplyLine.setExportOrders(true);
            supplyLineService.save(supplyLine);
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success("'" + supplyLine.getDescription() + "' "+ (createOperation?"created":"updated") +" successfully");


            if (createOperation) {
            response.getBody().addData(SUPPLYLINE, supplyLineServiceExt.getSupplylineDetailById(supplyLine.getId()));
            } else
            {
                response.getBody().addData(SUPPLYLINE, supplyLineServiceExt.getSupplylineDetailById(supplyLine.getId()));
            }
            response.getBody().addData(SUPPLYLINES, supplyLineServiceExt.getAllSupplyLine());
            return response;
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
    }

       // mahmed - 07.11.2013  delete
       @RequestMapping(value = "/supplylineDelete/{id}", method = RequestMethod.GET, headers = ACCEPT_JSON)
       @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLYLINE')")
       public ResponseEntity<OpenLmisResponse> delete(@PathVariable("id") Long id, HttpServletRequest request) {
           try{
               supplyLineServiceExt.deleteById(id);
               ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success("Supply line deleted successfully");
               response.getBody().addData(SUPPLYLINES, supplyLineServiceExt.getAllSupplyLine());
               return response;
           }
           catch (DataException e) {
               return error(e, HttpStatus.BAD_REQUEST);
           }
       }

    @RequestMapping(value = "/supplyingDepots.json", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getSupplyingDepots(HttpServletRequest request){

      ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("supplylines", supplyLineService.getSupplyDepots(loggedInUserId(request)));
      return response;
    }


}
