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


import org.hamcrest.Matcher;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.FacilityFeedDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.db.categories.UnitTests;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({DateTime.class, FacilityService.class, FacilityServiceTest.class})
public class FacilityServiceTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private FacilityRepository facilityRepository;
  @Mock
  private ProgramRepository programRepository;
  @Mock
  private ProgramSupportedService programSupportedService;
  @Mock
  private SupervisoryNodeService supervisoryNodeService;
  @Mock
  private RequisitionGroupService requisitionGroupService;

  @Mock
  private GeographicZoneRepository geographicZoneRepository;

  @Mock
  private EventService eventService;

  @Mock
  private FacilityProgramProductService facilityProgramProductService;

  @InjectMocks
  private FacilityService facilityService;

  @Test
  public void shouldReturnNullIfUserIsNotAssignedAFacility() {
    when(facilityRepository.getHomeFacility(1L)).thenReturn(null);
    assertThat(facilityService.getHomeFacility(1L), is(nullValue()));
  }

  @Test
  public void shouldGetFacilityById() throws Exception {
    Long facilityId = 1L;
    List<ProgramSupported> supportedPrograms = asList(new ProgramSupported());
    Facility facility = new Facility();

    when(programSupportedService.getAllByFacilityId(facilityId)).thenReturn(supportedPrograms);
    when(facilityRepository.getById(facilityId)).thenReturn(facility);

    Facility returnedFacility = facilityService.getById(facilityId);

    assertThat(returnedFacility, is(facility));
    assertThat(returnedFacility.getSupportedPrograms(), is(supportedPrograms));
  }

  @Test
  public void shouldUpdateFacilityEnabledAndActiveFor() throws Exception {
    Facility facility = make(a(defaultFacility));
    Facility parentFacility = new Facility(2l);
    parentFacility.setCode("PF");
    facility.setParentFacilityId(parentFacility.getId());
    FacilityFeedDTO facilityFeedDTO = new FacilityFeedDTO(facility, parentFacility);

    when(facilityRepository.updateEnabledAndActiveFor(facility)).thenReturn(facility);

    when(facilityRepository.getById(facility.getParentFacilityId())).thenReturn(parentFacility);

    DateTime dateTime = new DateTime();
    mockStatic(DateTime.class);
    when(DateTime.now()).thenReturn(dateTime);
    UUID uuid = UUID.randomUUID();
    mockStatic(UUID.class);
    Mockito.when(UUID.randomUUID()).thenReturn(uuid);

    facilityService.updateEnabledAndActiveFor(facility);

    verify(facilityRepository).updateEnabledAndActiveFor(facility);
    verify(facilityRepository).getById(facility.getParentFacilityId());
    verify(eventService).notify(argThat(eventMatcher(uuid, "Facility", dateTime, "",
        facilityFeedDTO.getSerializedContents(), "facilities")));

  }

  private static Matcher<Event> eventMatcher(final UUID uuid, final String title, final DateTime timestamp,
                                             final String uri, final String content, final String category) {
    return new ArgumentMatcher<Event>() {
      @Override
      public boolean matches(Object argument) {
        Event event = (Event) argument;
        return event.getUuid().equals(uuid.toString()) && event.getTitle().equals(title) && event.getTimeStamp().equals(timestamp) &&
            event.getUri().toString().equals(uri) && event.getContents().equals(content) && event.getCategory().equals(category);
      }
    };
  }

  @Test
  public void shouldReturnUserSupervisedFacilitiesForAProgram() {
    Long userId = 1L;
    Long programId = 1L;
    List<Facility> facilities = new ArrayList<>();
    List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
    List<RequisitionGroup> requisitionGroups = new ArrayList<>();
    when(facilityRepository.getFacilitiesBy(programId, requisitionGroups)).thenReturn(facilities);
    when(supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION)).thenReturn(supervisoryNodes);
    when(requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes)).thenReturn(requisitionGroups);

    List<Facility> result = facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION);

    verify(facilityRepository).getFacilitiesBy(programId, requisitionGroups);
    verify(supervisoryNodeService).getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION);
    verify(requisitionGroupService).getRequisitionGroupsBy(supervisoryNodes);
    assertThat(result, is(facilities));
  }


  @Test
  public void shouldSearchFacilitiesByCodeOrNameAndVirtualFacilityFlag() throws Exception {
    List<Facility> facilityList = Arrays.asList(new Facility());
    when(facilityRepository.searchFacilitiesByCodeOrNameAndVirtualFacilityFlag("query", true)).thenReturn(facilityList);

    List<Facility> returnedFacilities = facilityService.searchFacilitiesByCodeOrNameAndVirtualFacilityFlag("query", true);

    assertThat(returnedFacilities, is(facilityList));
  }

  @Test
  public void shouldSearchFacilitiesByCodeOrNameIfVirtualFacilityFlagIsNotPresenr() throws Exception {
    List<Facility> facilityList = Arrays.asList(new Facility());
    when(facilityRepository.searchFacilitiesByCodeOrName("query")).thenReturn(facilityList);

    List<Facility> returnedFacilities = facilityService.searchFacilitiesByCodeOrNameAndVirtualFacilityFlag("query", null);

    assertThat(returnedFacilities, is(facilityList));
  }

  @Test
  public void shouldThrowExceptionIfProgramsSupportedInvalidWhileUpdating() throws Exception {
    Facility facility = new Facility();
    final Date nullDate = null;
    List<ProgramSupported> programs = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(1L)), with(isActive, true), with(startDate, nullDate))));
    }};

    facility.setSupportedPrograms(programs);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("supported.programs.invalid");

    facilityService.update(facility);
  }

  @Test
  public void shouldUpdateFacilityAndNotifyForFeedIfCoreAttributeChanges() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    List<ProgramSupported> programsForFacility = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(2L)))));
    }};
    when(programSupportedService.getAllByFacilityId(facility.getId())).thenReturn(programsForFacility);
    facility.setSupportedPrograms(programsForFacility);
    Facility savedFacility = make(a(FacilityBuilder.defaultFacility));
    savedFacility.setName("Updated Name");
    when(facilityRepository.getById(facility.getId())).thenReturn(savedFacility);

    facilityService.update(facility);

    verify(facilityRepository).save(facility);
    verify(programSupportedService).updateSupportedPrograms(facility);
    verify(eventService).notify(any(Event.class));
  }

  @Test
  public void shouldUpdateFacilityAndNotNotifyForFeedIfNoCoreAttributeChanges() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    List<ProgramSupported> programsForFacility = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(2L)))));
    }};
    facility.setSupportedPrograms(programsForFacility);
    Facility savedFacility = make(a(FacilityBuilder.defaultFacility));
    when(facilityRepository.getById(facility.getId())).thenReturn(savedFacility);

    facilityService.update(facility);

    verify(facilityRepository).save(facility);
    verify(programSupportedService).updateSupportedPrograms(facility);
    verify(eventService, never()).notify(any(Event.class));
  }

  @Test
  public void shouldUpdateFacilityAndNotifyChildFacilitiesIfFacilityTypeChange() throws Exception {
    Long parentId = 59L;
    Facility parentFacility = make(a(defaultFacility, with(facilityId, parentId), with(type, "NGO")));
    Facility savedFacility = make(a(defaultFacility, with(facilityId, parentId), with(type, "GOVT")));

    Facility childFacility = make(a(defaultFacility, with(parentFacilityId, parentId)));

    when(facilityRepository.getById(59L)).thenReturn(savedFacility);
    when(facilityRepository.getChildFacilities(parentFacility)).thenReturn(asList(childFacility));

    facilityService.update(parentFacility);

    verify(facilityRepository).save(parentFacility);
    verify(programSupportedService).updateSupportedPrograms(parentFacility);
    verify(facilityRepository).getChildFacilities(parentFacility);
    verify(facilityRepository).updateVirtualFacilities(parentFacility);
    verify(eventService, times(2)).notify(any(Event.class));
  }

  @Test
  public void shouldUpdateFacilityAndNotifyChildFacilitiesIfGeoZoneChange() throws Exception {
    Long parentId = 59L;
    Facility parentFacility = make(a(defaultFacility, with(facilityId, parentId), with(geographicZoneCode, "AAA")));
    Facility savedFacility = make(a(defaultFacility, with(facilityId, parentId), with(geographicZoneCode, "BBB")));

    Facility childFacility = make(a(defaultFacility, with(parentFacilityId, parentId)));

    when(facilityRepository.getById(59L)).thenReturn(savedFacility);
    when(facilityRepository.getChildFacilities(parentFacility)).thenReturn(asList(childFacility));

    facilityService.update(parentFacility);

    verify(facilityRepository).save(parentFacility);
    verify(programSupportedService).updateSupportedPrograms(parentFacility);
    verify(facilityRepository).getChildFacilities(parentFacility);
    verify(facilityRepository).updateVirtualFacilities(parentFacility);
    verify(eventService, times(2)).notify(any(Event.class));
  }

  @Test
  public void shouldGetAllFacilitiesForUserAndRights() throws Exception {
    //Arrange
    Right[] rights = {Right.VIEW_REQUISITION, Right.APPROVE_REQUISITION};
    Facility homeFacility = new Facility();
    List<Facility> supervisedFacilities = new ArrayList<>();
    supervisedFacilities.add(homeFacility);
    List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
    List<RequisitionGroup> requisitionGroups = new ArrayList<>();
    when(facilityRepository.getHomeFacilityForRights(1L, rights)).thenReturn(homeFacility);
    when(supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(1L, rights)).thenReturn(supervisoryNodes);
    when(requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes)).thenReturn(requisitionGroups);
    when(facilityRepository.getAllInRequisitionGroups(requisitionGroups)).thenReturn(supervisedFacilities);

    //Act
    List<Facility> actualFacilities = facilityService.getForUserAndRights(1L, rights);

    //Assert
    assertThat(actualFacilities, is(supervisedFacilities));
    assertThat(actualFacilities.contains(homeFacility), is(true));
    verify(facilityRepository).getHomeFacilityForRights(1L, rights);
    verify(supervisoryNodeService).getAllSupervisoryNodesInHierarchyBy(1L, rights);
    verify(requisitionGroupService).getRequisitionGroupsBy(supervisoryNodes);
    verify(facilityRepository).getAllInRequisitionGroups(requisitionGroups);
  }

  @Test
  public void shouldNotGetHomeFacilityWhenItIsNull() throws Exception {
    //Arrange
    Right[] rights = {Right.VIEW_REQUISITION, Right.APPROVE_REQUISITION};
    Facility supervisedFacility = new Facility();
    List<Facility> supervisedFacilities = new ArrayList<>();
    supervisedFacilities.add(supervisedFacility);
    List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
    List<RequisitionGroup> requisitionGroups = new ArrayList<>();
    when(facilityRepository.getHomeFacilityForRights(1L, rights)).thenReturn(null);
    when(supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(1L, rights)).thenReturn(supervisoryNodes);
    when(requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes)).thenReturn(requisitionGroups);
    when(facilityRepository.getAllInRequisitionGroups(requisitionGroups)).thenReturn(supervisedFacilities);

    //Act
    List<Facility> actualFacilities = facilityService.getForUserAndRights(1L, rights);

    //Assert
    assertThat(actualFacilities, is(supervisedFacilities));
    assertThat(actualFacilities.size(), is(1));
    verify(facilityRepository).getHomeFacilityForRights(1L, rights);
    verify(supervisoryNodeService).getAllSupervisoryNodesInHierarchyBy(1L, rights);
    verify(requisitionGroupService).getRequisitionGroupsBy(supervisoryNodes);
    verify(facilityRepository).getAllInRequisitionGroups(requisitionGroups);
  }


  @Test
  public void shouldGetAllFacilitiesInDeliveryZoneForSupportedProgram() throws Exception {
    List<Facility> memberFacilities = new ArrayList<>();
    Facility facility = new Facility(1L);

    Facility facility2 = new Facility(2L);

    memberFacilities.add(facility);
    memberFacilities.add(facility2);

    Long deliveryZoneId = 1l;
    Long programId = 1l;
    when(facilityRepository.getAllInDeliveryZoneFor(deliveryZoneId, programId)).thenReturn(memberFacilities);
    ProgramSupported programSupported = new ProgramSupported();
    when(programSupportedService.getFilledByFacilityIdAndProgramId(facility.getId(), programId)).thenReturn(programSupported);
    when(programSupportedService.getFilledByFacilityIdAndProgramId(facility2.getId(), programId)).thenReturn(programSupported);

    List<Facility> facilities = facilityService.getAllForDeliveryZoneAndProgram(deliveryZoneId, programId);

    assertThat(facilities, is(memberFacilities));
    assertThat(facilities.get(0).getSupportedPrograms(), is(asList(programSupported)));
    assertThat(facilities.get(1).getSupportedPrograms(), is(asList(programSupported)));
    verify(facilityRepository).getAllInDeliveryZoneFor(deliveryZoneId, programId);
  }

  @Test
  public void shouldGetAllFacilitiesByModifiedDate() throws Exception {
    List<Facility> expectedFacilities = new ArrayList<>();
    Date dateModified = new Date();
    PowerMockito.when(facilityRepository.getAllByProgramSupportedModifiedDate(dateModified)).thenReturn(expectedFacilities);

    List<Facility> facilities = facilityService.getAllByProgramSupportedModifiedDate(dateModified);

    assertThat(facilities, is(expectedFacilities));
    verify(facilityRepository).getAllByProgramSupportedModifiedDate(dateModified);

  }

  @Test
  public void shouldGetFacilityWithReferenceDataForCode() throws Exception {

    String facilityCode = "F10";
    Long facilityId = 1l;
    Facility expectedFacility = new Facility();
    when(facilityRepository.getIdForCode(facilityCode)).thenReturn(facilityId);
    when(facilityRepository.getById(facilityId)).thenReturn(expectedFacility);
    when(programSupportedService.getAllByFacilityId(facilityId)).thenReturn(asList(new ProgramSupported()));
    Facility facility = facilityService.getFacilityWithReferenceDataForCode(facilityCode);

    verify(facilityRepository).getIdForCode(facilityCode);
    verify(facilityRepository).getById(facilityId);
    verify(programSupportedService).getAllByFacilityId(facilityId);
    assertThat(facility, is(expectedFacility));
  }

  @Test
  public void shouldGetWarehouses() throws Exception {

    List<Facility> expectedWarehouses = asList(new Facility());
    when(facilityRepository.getEnabledWarehouses()).thenReturn(expectedWarehouses);

    List<Facility> warehouses = facilityService.getEnabledWarehouses();

    verify(facilityRepository).getEnabledWarehouses();
    assertThat(warehouses, is(expectedWarehouses));
  }

  @Test
  public void shouldGetFacilityByCode() throws Exception {

    String facilityCode = "F11";
    List<ProgramSupported> programSupported = asList(new ProgramSupported());
    Facility expectedFacility = new Facility();
    Long facilityId = 1L;
    expectedFacility.setId(facilityId);
    when(facilityRepository.getIdForCode(facilityCode)).thenReturn(facilityId);
    when(facilityRepository.getById(facilityId)).thenReturn(expectedFacility);
    when(programSupportedService.getActiveByFacilityId(facilityId)).thenReturn(programSupported);

    Facility facility = facilityService.getFacilityByCode(facilityCode);

    assertThat(facility, is(expectedFacility));
    verify(facilityRepository).getIdForCode(facilityCode);
    verify(facilityRepository).getById(facilityId);
    verify(programSupportedService).getActiveByFacilityId(facilityId);
    assertThat(facility.getSupportedPrograms(), is(programSupported));
  }

  @Test
  public void shouldThrowErrorIfFacilityCodeInvalid() throws Exception {
    String invalidCode = "BlahBlahBlah";
    when(facilityRepository.getIdForCode(invalidCode)).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.facility.code.invalid");

    facilityService.getFacilityByCode(invalidCode);
  }

  @Test
  public void shouldGetAllChildFacilitiesForFacility() throws Exception {
    Facility facility = new Facility(1L);
    List<Facility> expectedFacilities = asList(new Facility(5L));
    when(facilityRepository.getChildFacilities(facility)).thenReturn(expectedFacilities);

    List<Facility> childFacilities = facilityService.getChildFacilities(facility);
    verify(facilityRepository).getChildFacilities(facility);
    assertThat(childFacilities, is(expectedFacilities));
  }

  @Test
  public void shouldGetVirtualFacility() throws Exception {
    Facility expectedFacility = make(a(defaultFacility, with(enabled, true), with(active, true), with(parentFacilityId, 333L)));
    Facility parentFacility = make(a(defaultFacility, with(facilityId, 333L)));
    when(facilityRepository.getByCode("code")).thenReturn(expectedFacility);
    when(facilityRepository.getById(333L)).thenReturn(parentFacility);
    Facility actualFacility = facilityService.getValidatedVirtualFacilityByCode("code");

    assertThat(actualFacility, is(expectedFacility));
  }

  @Test
  public void shouldThrowErrorIfCodeInvalid() throws Exception {
    when(facilityRepository.getByCode("code")).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.facility.code.invalid");

    facilityService.getValidatedVirtualFacilityByCode("code");
  }
}
