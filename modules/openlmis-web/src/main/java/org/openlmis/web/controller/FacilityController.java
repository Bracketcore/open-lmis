package org.openlmis.web.controller;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping(value = "/admin")
public class FacilityController {

    private FacilityService facilityService;

    @Autowired
    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @RequestMapping(value = "facilities/all.json", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Facility> getAll() {
        return facilityService.getAll();
    }

}
