/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.openlmis.core.domain.Right.commaSeparateRightNames;

@Component
@NoArgsConstructor
public class  FacilityRepository {

  @Autowired
  private FacilityMapper mapper;
  @Autowired
  private CommaSeparator commaSeparator;
  @Autowired
  private GeographicZoneRepository geographicZoneRepository;
  private static Integer LOWEST_GEO_LEVEL;

  public List<Facility> getAll() {
    return mapper.getAll();
  }

  public void save(Facility facility) {
    try {
      validateAndSetFacilityOperatedBy(facility);
      validateAndSetFacilityType(facility);
      validateGeographicZone(facility);
      validateEnabledAndActive(facility);
      if (facility.getId() == null) {
        mapper.insert(facility);
      } else {
        mapper.update(facility);
      }
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("error.duplicate.facility.code");
    } catch (DataIntegrityViolationException integrityViolationException) {
      String errorMessage = integrityViolationException.getMessage().toLowerCase();
      if (errorMessage.contains("foreign key") || errorMessage.contains("not-null constraint")) {
        throw new DataException("error.reference.data.missing");
      }
      throw new DataException("error.incorrect.length");
    }
  }

  private void validateEnabledAndActive(Facility facility) {
    if (facility.getEnabled() == Boolean.FALSE && facility.getActive() == Boolean.TRUE)
      throw new DataException("error.enabled.false");
  }

  private void validateGeographicZone(Facility facility) {
    if (LOWEST_GEO_LEVEL == null) {
      LOWEST_GEO_LEVEL = geographicZoneRepository.getLowestGeographicLevel();
    }
    GeographicZone geographicZone = geographicZoneRepository.getByCode(facility.getGeographicZone().getCode());
    facility.setGeographicZone(geographicZone);

    if (facility.getGeographicZone() == null) {
      throw new DataException("error.reference.data.invalid.geo.zone.code");
    }

    if (!facility.getGeographicZone().getLevel().getLevelNumber().equals(LOWEST_GEO_LEVEL)) {
      throw new DataException("error.geo.zone.not.at.lowest.level");
    }
  }

  private void validateAndSetFacilityType(Facility facility) {
    FacilityType facilityType = facility.getFacilityType();
    if (facilityType == null || facilityType.getCode() == null || facilityType.getCode().isEmpty())
      throw new DataException("error.reference.data.facility.type.missing");

    String facilityTypeCode = facilityType.getCode();
    FacilityType existingFacilityType = mapper.getFacilityTypeForCode(facilityTypeCode);

    if (existingFacilityType == null)
      throw new DataException("error.reference.data.invalid.facility.type");

    facility.setFacilityType(existingFacilityType);

  }

  private void validateAndSetFacilityOperatedBy(Facility facility) {
    if (facility.getOperatedBy() == null) return;

    String operatedByCode = facility.getOperatedBy().getCode();
    if (operatedByCode == null || operatedByCode.isEmpty()) return;

    Long operatedById = mapper.getOperatedByIdForCode(operatedByCode);
    if (operatedById == null)
      throw new DataException("error.reference.data.invalid.operated.by");

    facility.setOperatedBy(mapper.getFacilityOperatorById(operatedById));
  }

  public List<FacilityType> getAllTypes() {
    return mapper.getAllTypes();
  }

  public List<FacilityOperator> getAllOperators() {
    return mapper.getAllOperators();
  }

  public Facility getHomeFacility(Long userId) {
    return mapper.getHomeFacility(userId);
  }

  public Facility getById(Long id) {
    return mapper.getById(id);
  }

  public Facility updateEnabledAndActiveFor(Facility facility) {
    mapper.updateEnabledAndActiveFor(facility);
    //TODO is this required??
    return mapper.getById(facility.getId());
  }

  public List<Facility> getFacilitiesBy(Long programId, List<RequisitionGroup> requisitionGroups) {
    return mapper.getFacilitiesBy(programId, commaSeparator.commaSeparateIds(requisitionGroups));
  }

  public List<Facility> getAllInRequisitionGroups(List<RequisitionGroup> requisitionGroups) {
    return mapper.getAllInRequisitionGroups(commaSeparator.commaSeparateIds(requisitionGroups));
  }

  public Long getIdForCode(String code) {
    Long facilityId = mapper.getIdForCode(code);

    if (facilityId == null)
      throw new DataException("error.facility.code.invalid");

    return facilityId;
  }

  public List<Facility> searchFacilitiesByCodeOrName(String searchParam) {
    return mapper.searchFacilitiesByCodeOrName(searchParam);
  }

  public Facility getHomeFacilityForRights(Long userId, Right... rights) {
    return mapper.getHomeFacilityWithRights(userId, commaSeparateRightNames(rights));
  }

  public FacilityType getFacilityTypeByCode(FacilityType facilityType) {
    facilityType = mapper.getFacilityTypeForCode(facilityType.getCode());
    if (facilityType == null) {
      throw new DataException("error.facility.type.code.invalid");
    }
    return facilityType;
  }

  public Facility getByCode(String code) {
    return mapper.getByCode(code);
  }

  public List<Facility> getAllInDeliveryZoneFor(Long deliveryZoneId, Long programId) {
    return mapper.getAllInDeliveryZoneFor(deliveryZoneId, programId);
  }

  public List<Facility> getAllByProgramSupportedModifiedDate(Date dateModified) {
    return mapper.getAllByProgramSupportedModifiedDate(dateModified);
  }

  public List<Facility> searchFacilitiesByCodeOrNameAndVirtualFacilityFlag(String query, Boolean virtualFacility) {
    return mapper.searchFacilitiesByCodeOrNameAndVirtualFacilityFlag(query, virtualFacility);
  }

  public List<Facility> getEnabledWarehouses() {
    return mapper.getEnabledWarehouses();
  }

  public List<Facility> getChildFacilities(Facility facility) {
    return mapper.getChildFacilities(facility);
  }

  public void updateVirtualFacilities(Facility parentFacility) {
    mapper.updateVirtualFacilities(parentFacility);
  }

  public List<Facility> getAllByRequisitionGroupMemberModifiedDate(Date modifiedDate) {
    return mapper.getAllByRequisitionGroupMemberModifiedDate(modifiedDate);
  }

  public List<Facility> getAllByModifiedDate(Date modifiedDate) {
    return mapper.getAllByModifiedDate(modifiedDate);
  }
}
