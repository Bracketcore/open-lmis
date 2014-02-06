/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.web.model.FacilityReferenceData;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.openlmis.core.domain.Facility.createFacilityToBeDeleted;
import static org.openlmis.core.domain.Facility.createFacilityToBeRestored;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@NoArgsConstructor
public class FacilityController extends BaseController {

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProgramService programService;

  @Autowired
  RequisitionGroupService requisitionGroupService;

  @RequestMapping(value = "/facilities", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public List<Facility> get(@RequestParam(value = "searchParam", required = false) String searchParam,
                            @RequestParam(value = "virtualFacility", required = false) Boolean virtualFacility) {
    if (searchParam != null) {
      return facilityService.searchFacilitiesByCodeOrNameAndVirtualFacilityFlag(searchParam, virtualFacility);
    } else {
      return facilityService.getAll();
    }
  }

  @RequestMapping(value = "/user/facilities", method = GET)
  public List<Facility> getHomeFacility(HttpServletRequest httpServletRequest) {
    return Arrays.asList(facilityService.getHomeFacility(loggedInUserId(httpServletRequest)));
  }

  @RequestMapping(value = "/facilities/reference-data", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public Map getReferenceData() {
    FacilityReferenceData facilityReferenceData = new FacilityReferenceData();
    return facilityReferenceData.addFacilityTypes(facilityService.getAllTypes()).
      addFacilityOperators(facilityService.getAllOperators()).
      addGeographicZones(facilityService.getAllZones()).
      addPrograms(programService.getAll()).get();
  }

  @RequestMapping(value = "/facilities/{id}", method = GET, headers = ACCEPT_JSON)
  //@PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_FACILITY')")
  //this method is also being used by the reports
  public ResponseEntity<OpenLmisResponse> getFacility(@PathVariable(value = "id") Long id) {
    return response("facility", facilityService.getById(id));
  }


  @RequestMapping(value = "/create/requisition/supervised/{programId}/facilities.json", method = GET)
  public ResponseEntity<ModelMap> getUserSupervisedFacilitiesSupportingProgram(@PathVariable(
    value = "programId") Long programId, HttpServletRequest request) {
    ModelMap modelMap = new ModelMap();
    Long userId = loggedInUserId(request);
    List<Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION,
      AUTHORIZE_REQUISITION);
    modelMap.put("facilities", facilities);
    return new ResponseEntity<>(modelMap, HttpStatus.OK);
  }

  @RequestMapping(value = "/facilities", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity insert(@RequestBody Facility facility, HttpServletRequest request) {
    facility.setCreatedBy(loggedInUserId(request));
    facility.setModifiedBy(loggedInUserId(request));
    ResponseEntity<OpenLmisResponse> response;
    try {
      facilityService.update(facility);
    } catch (DataException exception) {
      return createErrorResponse(facility, exception);
    }
    response = success(messageService.message("message.facility.created.success", facility.getName()));
    response.getBody().addData("facility", facility);
    return response;
  }

  @RequestMapping(value = "/facilities/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity<OpenLmisResponse> update(@PathVariable("id") long id,
                                                 @RequestBody Facility facility,
                                                 HttpServletRequest request) {
    facility.setId(id);
    facility.setModifiedBy(loggedInUserId(request));

    try {
      facilityService.update(facility);
    } catch (DataException exception) {
      return createErrorResponse(facility, exception);
    }

    String successMessage = messageService.message("message.facility.updated.success", facility.getName());
    OpenLmisResponse openLmisResponse = new OpenLmisResponse("facility", facility);
    return openLmisResponse.successEntity(successMessage);
  }

  @RequestMapping(value = "/user/facilities/view", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> listForViewing(HttpServletRequest request) {
    return response("facilities",
      facilityService.getForUserAndRights(loggedInUserId(request), VIEW_REQUISITION));
  }

  @RequestMapping(value = "/facilities/{facilityId}", method = DELETE, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity<OpenLmisResponse> softDelete(HttpServletRequest httpServletRequest, @PathVariable Long facilityId) {
    Facility facilityToBeDeleted = createFacilityToBeDeleted(facilityId, loggedInUserId(httpServletRequest));
    facilityService.updateEnabledAndActiveFor(facilityToBeDeleted);
    Facility deletedFacility = facilityService.getById(facilityId);

    String successMessage = messageService.message("disable.facility.success", deletedFacility.getName(), deletedFacility.getCode());
    OpenLmisResponse response = new OpenLmisResponse("facility", deletedFacility);
    return response.successEntity(successMessage);
  }


  @RequestMapping(value = "/facilities/{id}/restore", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity<OpenLmisResponse> restore(HttpServletRequest request, @PathVariable("id") long facilityId) {
    Facility facilityToBeDeleted = createFacilityToBeRestored(facilityId, loggedInUserId(request));

    facilityService.updateEnabledAndActiveFor(facilityToBeDeleted);

    Facility restoredFacility = facilityService.getById(facilityId);

    String successMessage = messageService.message("enable.facility.success", restoredFacility.getName(), restoredFacility.getCode());

    OpenLmisResponse response = new OpenLmisResponse("facility", restoredFacility);
    return response.successEntity(successMessage);
  }

  @RequestMapping(value = "/deliveryZones/{deliveryZoneId}/programs/{programId}/facilities", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DISTRIBUTION')")
  public ResponseEntity<OpenLmisResponse> getFacilitiesForDeliveryZoneAndProgram(@PathVariable("deliveryZoneId") Long deliveryZoneId,
                                                                                 @PathVariable("programId") Long programId) {
    List<Facility> facilities = facilityService.getAllForDeliveryZoneAndProgram(deliveryZoneId, programId);
    return response("facilities", Facility.filterForActiveProducts(facilities));
  }

  @RequestMapping(value = "/enabledWarehouses", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> getEnabledWarehouses() {
    List<Facility> enabledWarehouses = facilityService.getEnabledWarehouses();

    return response("enabledWarehouses", enabledWarehouses);
  }

  private ResponseEntity<OpenLmisResponse> createErrorResponse(Facility facility, DataException exception) {
    OpenLmisResponse openLmisResponse = new OpenLmisResponse("facility", facility);
    return openLmisResponse.errorEntity(exception, BAD_REQUEST);
  }

  @RequestMapping(value="/facilities/getListInRequisitionGroup/{id}",method= GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REQUISITION_GROUP')")
  public ResponseEntity<OpenLmisResponse> getFacilityListInARequisitionGroup(@PathVariable("id") Long id, HttpServletRequest request){
      RequisitionGroup requisitionGroup = requisitionGroupService.loadRequisitionGroupById(id);
      List<RequisitionGroup> requisitionGroups= new ArrayList<RequisitionGroup>();
      requisitionGroups.add(requisitionGroup);
      return OpenLmisResponse.response("facilities",facilityService.getCompleteListInRequisitionGroups(requisitionGroups));
  }

  @RequestMapping(value = "/facilities/facilityType/{facilityTypeId}", method = GET, headers = ACCEPT_JSON)
  //@PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity<OpenLmisResponse> getFacilityListForAFacilityType(@PathVariable("facilityTypeId") Long facilityTypeId) {

    return OpenLmisResponse.response("facilities",facilityService.getFacilitiesListForAFacilityType(facilityTypeId));

  }

  @RequestMapping(value = "/facility/supplyingFacilities", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity<OpenLmisResponse> getSupplyingFacilitiesCompleteList() {
      return OpenLmisResponse.response("facilities",facilityService.getSupplyingFacilitiesCompleteList());
  }

}
