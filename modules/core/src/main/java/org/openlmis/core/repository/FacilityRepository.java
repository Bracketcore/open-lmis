package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@NoArgsConstructor
public class FacilityRepository {

    private FacilityMapper facilityMapper;
    private ProgramSupportedMapper programSupportedMapper;
    private ProgramMapper programMapper;

    @Autowired
    public FacilityRepository(FacilityMapper facilityMapper, ProgramSupportedMapper programSupportedMapper, ProgramMapper programMapper) {
        this.facilityMapper = facilityMapper;
        this.programSupportedMapper = programSupportedMapper;
        this.programMapper = programMapper;
    }

    public List<Facility> getAll() {
        return facilityMapper.getAll();
    }

    public RequisitionHeader getHeader(int facilityId) {
        return facilityMapper.getRequisitionHeaderData(facilityId);
    }

    @Transactional
    public void saveOrUpdate(Facility facility) {
        facility.setModifiedDate(DateTime.now().toDate());
        if (facility.getId() == null)
            try {
                facilityMapper.insert(facility);
                addListOfSupportedPrograms(facility);
            } catch (DuplicateKeyException duplicateKeyException) {
                throw new RuntimeException("Duplicate Facility Code found");
            } catch (DataIntegrityViolationException integrityViolationException) {
                if (integrityViolationException.getMessage().toLowerCase().contains("foreign key")) {
                    throw new RuntimeException("Missing Reference data");
                }
            }
        else {
        List<Program> previouslySupportedPrograms = programMapper.getByFacilityId(facility.getId());
            facilityMapper.update(facility);
            deleteObsoleteProgramMappings(facility, previouslySupportedPrograms);
            addUpdatableProgramMappings(facility,previouslySupportedPrograms);
        }

    }

    private void deleteObsoleteProgramMappings(Facility facility, List<Program> previouslySupportedPrograms) {
        List<Program> supportedPrograms = facility.getSupportedPrograms();
        for(Program previouslySupportedProgram : previouslySupportedPrograms){
            if(!(supportedPrograms).contains(previouslySupportedProgram)){
                programSupportedMapper.deleteObsoletePrograms(facility.getId(),previouslySupportedProgram.getCode());
            }
        }
    }

    private void addUpdatableProgramMappings(Facility facility, List<Program> previouslySupportedPrograms) {
        for(Program supportedProgram : facility.getSupportedPrograms()){
            if(!(previouslySupportedPrograms).contains(supportedProgram)){
                ProgramSupported newProgramsSupported = new ProgramSupported(facility.getCode(), supportedProgram.getCode(), supportedProgram.getActive(), facility.getModifiedBy(), facility.getModifiedDate());
                addSupportedProgram(newProgramsSupported);
            }
        }
    }

    private void addListOfSupportedPrograms(Facility facility) {
        List<Program> supportedPrograms = facility.getSupportedPrograms();
        for (Program supportedProgram : supportedPrograms) {
            ProgramSupported programSupported = new ProgramSupported(facility.getCode(), supportedProgram.getCode(), supportedProgram.getActive(), facility.getModifiedBy(), facility.getModifiedDate());
            addSupportedProgram(programSupported);
        }
    }

    public void addSupportedProgram(ProgramSupported programSupported) {
        try {
            programSupported.setModifiedDate(DateTime.now().toDate());
            programSupportedMapper.addSupportedProgram(programSupported);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new RuntimeException("Facility has already been mapped to the program");
        } catch (DataIntegrityViolationException integrityViolationException) {
            if (integrityViolationException.getMessage().toLowerCase().contains("facility_code")) {
                throw new RuntimeException("Invalid facility programCode specified");
            } else {
                throw new RuntimeException("Invalid program programCode specified");
            }
        }
    }

    public List<FacilityType> getAllTypes() {
        return facilityMapper.getAllTypes();
    }

    public List<FacilityOperator> getAllOperators() {
        return facilityMapper.getAllOperators();
    }

    public List<GeographicZone> getAllGeographicZones() {
        return facilityMapper.getAllGeographicZones();
    }

    public Facility getHomeFacility(String user) {
        return facilityMapper.getHomeFacility(user);
    }

    public Facility getFacility(int id) {
        Facility facility = facilityMapper.get(id);
        facility.setSupportedPrograms(programMapper.getByFacilityId(facility.getId()));
        return facility;
    }
}
