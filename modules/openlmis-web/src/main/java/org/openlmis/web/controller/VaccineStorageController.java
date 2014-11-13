package org.openlmis.web.controller;

import org.openlmis.core.exception.DataException;
import org.openlmis.help.domain.HelpContent;
import org.openlmis.help.domain.HelpTopic;
import org.openlmis.vaccine.domain.VaccineStorage;
import org.openlmis.vaccine.service.StorageTypeService;
import org.openlmis.vaccine.service.TempratureService;
import org.openlmis.vaccine.service.VaccineStorageService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.openlmis.web.response.OpenLmisResponse.error;

/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
@Controller
public class VaccineStorageController extends BaseController  {
    @Autowired
    private VaccineStorageService storageService;
    @Autowired
    private StorageTypeService storageTypeService;
    @Autowired
    private TempratureService tempratureService;

    public static final String VACCINESTORAGE = "vaccineStorage";
    public static final String VACCINESTORAGEDATAIL = "vaccineStorage";
    public static final String VACCINESTORAGELIST = "vaccineStorageList";
    public static final String TEMPERATURELIST = "temperatureList";
    public static final String STORAGETYPELIST = "storageTypeList";
    @RequestMapping(value = "/createVaccineStorage", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody VaccineStorage vaccineStorage, HttpServletRequest request) {
        //System.out.println(" here saving help Content");
        vaccineStorage.setCreatedBy(loggedInUserId(request));
        vaccineStorage.setModifiedBy(loggedInUserId(request));
        vaccineStorage.setModifiedDate(new Date());
        vaccineStorage.setCreatedDate(new Date());
        //System.out.println(" help content id is " + helpContent.getName());


        return saveVaccineStorage(vaccineStorage, true);
    }

    private ResponseEntity<OpenLmisResponse> saveVaccineStorage(VaccineStorage vaccineStorage, boolean createOperation) {
        try {
            if (createOperation) {
                System.out.println("creating "+vaccineStorage.getLocation());
                this.storageService.addVaccineStorage(vaccineStorage);
            } else {
                this.storageService.updateVaccineStorage(vaccineStorage);
            }
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + vaccineStorage.getId()) + "' " + (createOperation ? "created" : "updated") + " successfully");
            response.getBody().addData(VACCINESTORAGE, this.storageService.loadVaccineStorageDetail(vaccineStorage.getId()));
            response.getBody().addData(VACCINESTORAGELIST, this.storageService.loadVaccineStorageList());
            return response;
        } catch (DuplicateKeyException exp) {
            System.out.println(exp.getStackTrace());
            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        } catch (DataException e) {
            System.out.println(e.getStackTrace());
            return error(e, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        }
    }
        /*

         */

        @RequestMapping(value = "/vaccineStorageDetail/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
        public ResponseEntity<OpenLmisResponse> getVaccineStorageDetail(@PathVariable("id") Long id) {
            //System.out.println(" here calling");
            VaccineStorage vaccineStorage = this.storageService.loadVaccineStorageDetail(id);
            return OpenLmisResponse.response(VACCINESTORAGEDATAIL, vaccineStorage);
        }


    @RequestMapping(value = "/vaccineStorageList", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getVaccineStorageList() {
        //System.out.println(" here calling");
        return OpenLmisResponse.response(VACCINESTORAGELIST, this.storageService.loadVaccineStorageList());
    }
        @RequestMapping(value = "/updateVaccineStorage", method = RequestMethod.POST, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
        public ResponseEntity<OpenLmisResponse> update(@RequestBody VaccineStorage vaccineStorage, HttpServletRequest request) {
            //System.out.println(" updating ");
            vaccineStorage.setModifiedBy(loggedInUserId(request));
            vaccineStorage.setModifiedDate(new Date());

            //System.out.println(" help topic id is" + helpTopic.getName());
            return saveVaccineStorage(vaccineStorage, false);
        }
    @RequestMapping(value = "/deleteVaccineStorage", method = RequestMethod.POST, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> delete(@RequestBody VaccineStorage vaccineStorage, HttpServletRequest request) {

        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + vaccineStorage.getId()) + "Deleted successfully");
        response.getBody().addData(VACCINESTORAGE, vaccineStorage);
        response.getBody().addData(VACCINESTORAGELIST, this.storageService.loadVaccineStorageList());
        this.storageService.deleteVccineStorage(vaccineStorage);
        return response;
    }


    @RequestMapping(value = "/storageTypeList", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getStorageTypeList() {
        //System.out.println(" here calling");
        return OpenLmisResponse.response(STORAGETYPELIST, this.storageTypeService.loadStorageTypeList());
    }

    @RequestMapping(value = "/tempratureList", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getTemperatureList() {

        return OpenLmisResponse.response(TEMPERATURELIST, this.tempratureService.loadTempratureList());
    }
    }

