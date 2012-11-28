package org.openlmis.core.repository;

import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ProgramRepository {

    @Autowired
    ProgramMapper programMapper;

    public List<Program> getAllActive() {
        return programMapper.getAllActive();
    }

    public List<Program> getByFacilityCode(String facilityCode) {
        return programMapper.getActiveByFacilityCode(facilityCode);
    }

    public List<Program> getAll() {
        return programMapper.getAll();
    }
}
