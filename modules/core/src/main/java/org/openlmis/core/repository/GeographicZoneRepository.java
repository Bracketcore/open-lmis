package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.GeographicLevelMapper;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class GeographicZoneRepository {

  private GeographicZoneMapper mapper;
  private GeographicLevelMapper geographicLevelMapper;

  @Autowired
  public GeographicZoneRepository(GeographicZoneMapper mapper, GeographicLevelMapper geographicLevelMapper) {
    this.mapper = mapper;
    this.geographicLevelMapper = geographicLevelMapper;
  }

  public void save(GeographicZone geographicZone) {
    try {
      validateAndSetGeographicZone(geographicZone);
      mapper.insert(geographicZone);
    } catch (DuplicateKeyException exception) {
      throw new DataException("Duplicate Geographic Zone Code");
    } catch (DataIntegrityViolationException exception) {
      throw new DataException("Incorrect Data Length");
    }
  }

  private void validateAndSetGeographicZone(GeographicZone geographicZone) {
    geographicZone.setLevel(mapper.getGeographicLevelByCode(geographicZone.getLevel().getCode()));
    if (geographicZone.getLevel() == null)
      throw new DataException("Invalid Geographic Level Code");
    if (geographicZone.getParent() == null) {
      geographicZone.setParent(mapper.getGeographicZoneByCode("Root"));
      return;
    }
    geographicZone.setParent(mapper.getGeographicZoneByCode(geographicZone.getParent().getCode()));
    if (geographicZone.getParent() == null)
      throw new DataException("Invalid Geographic Zone Parent Code");
  }

  public GeographicZone getByCode(String code) {
    return mapper.getGeographicZoneByCode(code);
  }

  public Integer getLowestGeographicLevel() {
    return geographicLevelMapper.getLowestGeographicLevel();
  }

  public List<GeographicZone> getAllGeographicZones() {
    return mapper.getAllGeographicZones();
  }

  public List<GeographicZone> getAllZones() {
        return mapper.getAllZones();
  }
}
