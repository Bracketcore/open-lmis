package org.openlmis.web.controller;

import org.openlmis.core.domain.Regimen;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.RegimenService;
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

import java.util.List;

import static org.openlmis.web.controller.BaseController.UNEXPECTED_EXCEPTION;
import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
public class RegimenController extends BaseController {

  @Autowired
  RegimenService service;
  public static final String REGIMENS = "regimens";

  @RequestMapping(value = "/regimens", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REGIMEN_TEMPLATE')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody List<Regimen> regimens) {
    try {
      service.save(regimens);
      return success("regimens.saved.successfully");
    } catch (Exception e) {
      return error(UNEXPECTED_EXCEPTION, HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/programId/{programId}/regimens", method  = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REGIMEN_TEMPLATE')")
  public ResponseEntity<OpenLmisResponse> getByProgram(@PathVariable Long programId) {
    try{
      ResponseEntity<OpenLmisResponse> response;
      List<Regimen> regimens =  service.getByProgram(programId);
      response = response("regimens", regimens);
      return response;
    }catch(DataException dataException){
      return error(UNEXPECTED_EXCEPTION, HttpStatus.BAD_REQUEST);
    }
  }
}
