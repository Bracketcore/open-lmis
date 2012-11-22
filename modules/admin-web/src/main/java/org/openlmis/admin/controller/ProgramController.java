package org.openlmis.admin.controller;

import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProgramController {

    private ProgramService programService;

    @Autowired
    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @RequestMapping(value = "/admin/programs", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Program> getAllPrograms() {
        return programService.getAll();
    }

    @RequestMapping(value = "/logistics/facility/{facilityCode}/programs.json", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Program> getProgramsForFacility(@PathVariable(value = "facilityCode") String facilityCode) {
        return programService.getByFacilityCode(facilityCode);
    }

}
