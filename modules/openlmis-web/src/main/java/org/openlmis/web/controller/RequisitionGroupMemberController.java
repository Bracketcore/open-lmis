package org.openlmis.web.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RequisitionGroupMemberService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created with IntelliJ IDEA.
 * User: Henok
 * Date: 7/6/13
 * Time: 5:28 PM
 */

@Controller
@NoArgsConstructor
public class RequisitionGroupMemberController extends BaseController {

    @Autowired
    RequisitionGroupMemberService requisitionGroupMemberService;

    @Autowired
    FacilityService facility;

    @Autowired
    RequisitionGroupService requisitionGroupService;

    @RequestMapping(value="/requisitionGroupMember/insert",method=POST,headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REQUISITION_GROUP')")
    public ResponseEntity<OpenLmisResponse> insert(@RequestBody RequisitionGroupMember requisitionGroupMember, HttpServletRequest request){
        ResponseEntity<OpenLmisResponse> successResponse;
        requisitionGroupMember.setModifiedBy(loggedInUserId(request));
        try {
            requisitionGroupMemberService.save(requisitionGroupMember);
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
        successResponse = success(String.format("Requisition group member has been successfully saved"));
        successResponse.getBody().addData("requisitionGroupMember", requisitionGroupMember);
        return successResponse;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    @RequestMapping(value="/requisitionGroupMember/remove/{rgId}/{facId}",method = GET,headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REQUISITION_GROUP')")
    public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value="rgId") Long requisitionGroupId, @PathVariable(value="facId") Long facilityID, HttpServletRequest request){
        ResponseEntity<OpenLmisResponse> successResponse;
        try {
            requisitionGroupMemberService.removeRequisitionGroupMember(requisitionGroupService.loadRequisitionGroupById(requisitionGroupId),facility.getById(facilityID));
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
        successResponse = success(String.format("Requisition group member has been successfully removed"));
        //successResponse.getBody().addData("requisitionGroupMember", requisitionGroupMember);
        return successResponse;
    }
}