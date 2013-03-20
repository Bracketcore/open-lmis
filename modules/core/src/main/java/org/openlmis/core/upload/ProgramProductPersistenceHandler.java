package org.openlmis.core.upload;

import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("programProductPersistenceHandler")
public class ProgramProductPersistenceHandler extends AbstractModelPersistenceHandler {

    private ProgramProductRepository programProductRepository;

    @Autowired
    public ProgramProductPersistenceHandler(ProgramProductRepository programProductRepository) {
        this.programProductRepository = programProductRepository;
    }

    @Override
    protected void save(Importable importable, AuditFields auditFields) {
        ProgramProduct programProduct = (ProgramProduct) importable;
        programProduct.setModifiedBy(auditFields.getUser());
        programProduct.setModifiedDate(new Date());
        programProductRepository.insert(programProduct);
    }
}


