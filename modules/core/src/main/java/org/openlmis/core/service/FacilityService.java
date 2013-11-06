/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;


import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.joda.time.DateTime;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.FacilityFeedDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.*;

import static java.util.Arrays.asList;

@Service
@NoArgsConstructor
public class FacilityService {

  public static final String FACILITY_CATEGORY = "facilities";
  public static final String FACILITY_TITLE = "Facility";

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private ProgramSupportedService programSupportedService;

  @Autowired
  private RequisitionGroupService requisitionGroupService;

  @Autowired
  private GeographicZoneRepository geographicZoneRepository;

  @Autowired
  private SupervisoryNodeService supervisoryNodeService;

  @Autowired
  private EventService eventService;

  private static final Logger logger = Logger.getLogger(FacilityService.class);

  @Transactional
  public void update(Facility facility) {
    save(facility);
    programSupportedService.updateSupportedPrograms(facility);
  }

  public List<Facility> getAll() {
    return facilityRepository.getAll();
  }

  public List<FacilityType> getAllTypes() {
    return facilityRepository.getAllTypes();
  }

  public List<FacilityOperator> getAllOperators() {
    return facilityRepository.getAllOperators();
  }

  public List<GeographicZone> getAllZones() {
    return geographicZoneRepository.getAllGeographicZones();
  }

  public Facility getHomeFacility(Long userId) {
    return facilityRepository.getHomeFacility(userId);
  }

  public Facility getById(Long id) {
    Facility facility = facilityRepository.getById(id);
    facility.setSupportedPrograms(programSupportedService.getAllByFacilityId(id));
    return facility;
  }

  @Transactional
  public void updateEnabledAndActiveFor(Facility facility) {
    facility = facilityRepository.updateEnabledAndActiveFor(facility);
    notify(asList(facility));
  }

  public List<Facility> getUserSupervisedFacilities(Long userId, Long programId, Right... rights) {
    List<SupervisoryNode> supervisoryNodes = supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, rights);
    List<RequisitionGroup> requisitionGroups = requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes);
    return facilityRepository.getFacilitiesBy(programId, requisitionGroups);
  }

  public void save(Facility newFacility) {
    newFacility.validate();

    Facility storedFacility = facilityRepository.getById(newFacility.getId());

    facilityRepository.save(newFacility);

    if (!newFacility.equals(storedFacility)) {
      notify(asList(newFacility));
      if (canUpdateVirtualFacilities(newFacility, storedFacility)) {
        facilityRepository.updateVirtualFacilities(newFacility);
        notify(getChildFacilities(newFacility));
      }
    }
  }

  private boolean canUpdateVirtualFacilities(Facility newFacility, Facility oldFacility) {
    return (oldFacility == null ||
      !(newFacility.getGeographicZone().getCode().equals(oldFacility.getGeographicZone().getCode())) ||
      !(newFacility.getFacilityType().getCode().equals(oldFacility.getFacilityType().getCode()))
    );
  }

  private void notify(List<Facility> facilities) {
    //TODO newFacility doesn't have modifiedDate populated
    for (Facility facility : facilities) {
      try {
        Facility parentFacility = facilityRepository.getById(facility.getParentFacilityId());
        FacilityFeedDTO facilityFeedDTO = new FacilityFeedDTO(facility, parentFacility);
        String content = facilityFeedDTO.getSerializedContents();
        eventService.notify(new Event(UUID.randomUUID().toString(), FACILITY_TITLE, DateTime.now(), "", content, FACILITY_CATEGORY));
      } catch (URISyntaxException e) {
        logger.error("Unable to generate facility event", e);
      }
    }
  }

  public List<Facility> getForUserAndRights(Long userId, Right... rights) {
    List<SupervisoryNode> supervisoryNodesInHierarchy = supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, rights);
    List<RequisitionGroup> requisitionGroups = requisitionGroupService.getRequisitionGroupsBy(supervisoryNodesInHierarchy);
    final Set<Facility> userFacilities = new HashSet<>(facilityRepository.getAllInRequisitionGroups(requisitionGroups));
    final Facility homeFacility = facilityRepository.getHomeFacilityForRights(userId, rights);

    if (homeFacility != null) userFacilities.add(homeFacility);

    return new ArrayList<>(userFacilities);

  }

  public FacilityType getFacilityTypeByCode(FacilityType facilityType) {
    return facilityRepository.getFacilityTypeByCode(facilityType);
  }

  public Facility getByCode(Facility facility) {
    return facilityRepository.getByCode(facility.getCode());
  }

  public List<Facility> getAllForDeliveryZoneAndProgram(Long deliveryZoneId, Long programId) {
    List<Facility> facilities = facilityRepository.getAllInDeliveryZoneFor(deliveryZoneId, programId);
    for (Facility facility : facilities) {
      facility.getSupportedPrograms().add(programSupportedService.getFilledByFacilityIdAndProgramId(facility.getId(), programId));
    }
    return facilities;
  }

  public List<Facility> getAllByProgramSupportedModifiedDate(Date dateModified) {
    return facilityRepository.getAllByProgramSupportedModifiedDate(dateModified);
  }

  public Facility getFacilityWithReferenceDataForCode(String facilityCode) {
    Long facilityId = facilityRepository.getIdForCode(facilityCode);
    return getById(facilityId);
  }

  public List<Facility> searchFacilitiesByCodeOrNameAndVirtualFacilityFlag(String query, Boolean virtualFacility) {
    if (virtualFacility == null) {
      return facilityRepository.searchFacilitiesByCodeOrName(query);
    }
    return facilityRepository.searchFacilitiesByCodeOrNameAndVirtualFacilityFlag(query, virtualFacility);
  }

  public List<Facility> getEnabledWarehouses() {
    return facilityRepository.getEnabledWarehouses();
  }

  public Facility getFacilityByCode(String facilityCode) {
    Long facilityId;
    if ((facilityId = facilityRepository.getIdForCode(facilityCode)) == null) {
      throw new DataException("error.facility.code.invalid");
    }
    Facility facility = facilityRepository.getById(facilityId);
    facility.setSupportedPrograms(programSupportedService.getActiveByFacilityId(facility.getId()));
    return facility;
  }

  public List<Facility> getChildFacilities(Facility facility) {
    return facilityRepository.getChildFacilities(facility);
  }

  public List<Facility> getAllByRequisitionGroupMemberModifiedDate(Date modifiedDate) {
    return facilityRepository.getAllByRequisitionGroupMemberModifiedDate(modifiedDate);
  }
}
