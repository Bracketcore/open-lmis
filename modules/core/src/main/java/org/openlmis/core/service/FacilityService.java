package org.openlmis.core.service;


import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.domain.RequisitionHeader;
import org.openlmis.core.repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacilityService {

    private FacilityRepository facilityRepository;

    @Autowired
    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    public List<Facility> getAll() {
        return facilityRepository.getAll();
    }

    public RequisitionHeader getRequisitionHeader(String code) {
        return facilityRepository.getHeader(code);
    }

    public void save(Facility facility) {
        facilityRepository.save(facility);
    }

    public void addSupportedProgram(ProgramSupported programSupported) {
        facilityRepository.addSupportedProgram(programSupported);
    }

}
