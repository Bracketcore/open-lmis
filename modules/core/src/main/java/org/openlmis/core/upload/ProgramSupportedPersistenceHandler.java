package org.openlmis.core.upload;

import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.upload.Importable;
import org.openlmis.upload.handler.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("programSupportedPersistenceHandler")
public class ProgramSupportedPersistenceHandler extends AbstractModelPersistenceHandler {

    private FacilityRepository facilityRepository;

    @Autowired
    public ProgramSupportedPersistenceHandler(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @Override
    protected void save(Importable importable, String modifiedBy) {
        ProgramSupported programSupported = (ProgramSupported) importable;
        programSupported.setModifiedBy(modifiedBy);
        programSupported.setModifiedDate(new Date());
        facilityRepository.addSupportedProgram(programSupported);
    }
}
