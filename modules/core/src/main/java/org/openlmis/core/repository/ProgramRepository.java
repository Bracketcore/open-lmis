package org.openlmis.core.repository;

import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ProgramRepository {

    @Autowired
    ProgramMapper programMapper;

    @Autowired
    ProgramSupportedMapper programSupportedMapper;

    @Autowired
    RoleRightsMapper roleRightsMapper;

    public List<Program> getAllActive() {
        return programMapper.getAllActive();
    }

    public List<Program> getByFacility(Integer facilityId) {
        return programMapper.getActiveByFacility(facilityId);
    }

    public List<Program> getAll() {
        return programMapper.getAll();
    }

    public List<Program> getActiveProgramsForUser(Integer userId, Right right){
        return programMapper.getActiveProgramsForUser(userId, right);
    }

    public List<Program> filterActiveProgramsAndFacility(List<RoleAssignment> roleAssignments, Integer facilityId) {
        String programIds = getCommaSeparatedProgramsForDB(roleAssignments);
        return programSupportedMapper.filterActiveProgramsAndFacility(programIds, facilityId);
    }

    private String getCommaSeparatedProgramsForDB(List<RoleAssignment> roleAssignments) {
        List<Integer> programIds = new ArrayList<>();
        for (RoleAssignment roleAssignment : roleAssignments) {
            programIds.add(roleAssignment.getProgramId());
        }
        return programIds.toString().replace("[", "{").replace("]", "}");
    }
}
