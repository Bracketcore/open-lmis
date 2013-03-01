package org.openlmis.core.upload;

import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("geographicZonePersistenceHandler")
public class GeographicZonePersistenceHandler extends AbstractModelPersistenceHandler {
  GeographicZoneRepository repository;

  @Autowired
  public GeographicZonePersistenceHandler(GeographicZoneRepository repository) {
    this.repository = repository;
  }

  @Override
  protected void save(Importable modelClass, Integer modifiedBy) {
    GeographicZone geographicZone = (GeographicZone) modelClass;
    geographicZone.setModifiedBy(modifiedBy);
    repository.save(geographicZone);
  }
}