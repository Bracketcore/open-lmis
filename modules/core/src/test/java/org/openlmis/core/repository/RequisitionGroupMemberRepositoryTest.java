/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupMemberMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupProgramScheduleMapper;

import java.util.ArrayList;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;

@RunWith(MockitoJUnitRunner.class)
public class RequisitionGroupMemberRepositoryTest {

  ArrayList<Integer> programIdList;
  public static final Integer RG_ID = 1;
  public static final Integer FACILITY_ID = 100;
  RequisitionGroup requisitionGroup;
  RequisitionGroupMember requisitionGroupMember;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  RequisitionGroupMemberMapper requisitionGroupMemberMapper;

  @Mock
  RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;

  @Mock
  RequisitionGroupMapper requisitionGroupMapper;

  @Mock
  FacilityRepository facilityRepository;

  @Mock
  ProgramMapper programMapper;

  @Mock
  private RequisitionGroupRepository requisitionGroupRepository;

  private RequisitionGroupMemberRepository repository;

  @Before
  public void setUp() throws Exception {
    requisitionGroup = make(a(defaultRequisitionGroup));
    Facility facility = make(a(defaultFacility));

    requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMember.setFacility(facility);

    programIdList = new ArrayList<>();
    programIdList.add(1);

    initMocks(this);
    repository = new RequisitionGroupMemberRepository(requisitionGroupMemberMapper, requisitionGroupProgramScheduleMapper, requisitionGroupMapper, facilityRepository, programMapper, requisitionGroupRepository);
  }

  @Test
  public void shouldSaveMappingIfMappingDoesNotExist() throws Exception {
    when(requisitionGroupMemberMapper.doesMappingExist(requisitionGroupMember.getRequisitionGroup().getId(),
      requisitionGroupMember.getRequisitionGroup().getId())).thenReturn(0);
    repository.insert(requisitionGroupMember);

    verify(requisitionGroupMemberMapper).insert(requisitionGroupMember);
  }

  @Test
  public void shouldSaveMappingIfMappingAlreadyExists() throws Exception {
    when(requisitionGroupMemberMapper.doesMappingExist(requisitionGroupMember.getRequisitionGroup().getId(),
      requisitionGroupMember.getFacility().getId())).thenReturn(1);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Facility to Requisition Group mapping already exists");

    repository.insert(requisitionGroupMember);

    verify(requisitionGroupMemberMapper, never()).insert(requisitionGroupMember);
  }
}

