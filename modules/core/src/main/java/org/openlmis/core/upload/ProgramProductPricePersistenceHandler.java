package org.openlmis.core.upload;

import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.upload.Importable;

public class ProgramProductPricePersistenceHandler extends AbstractModelPersistenceHandler {
  private ProgramProductService programProductService;

  public ProgramProductPricePersistenceHandler(ProgramProductService service) {

    this.programProductService = service;
  }

  @Override
  protected void save(Importable modelClass, String modifiedBy) {
    ProgramProductPrice programProductPrice = (ProgramProductPrice) modelClass;
    programProductPrice.setModifiedBy(modifiedBy);
    programProductService.save(programProductPrice);
  }
}
