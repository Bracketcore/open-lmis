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
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.RequisitionGroupBuilder.code;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;


@RunWith(MockitoJUnitRunner.class)
public class SupervisoryNodeRepositoryTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private SupervisoryNodeMapper supervisoryNodeMapper;
  @Mock
  private FacilityRepository facilityRepository;

  @Mock
  private RequisitionGroupRepository requisitionGroupRepository;

  private SupervisoryNodeRepository repository;
  private SupervisoryNode supervisoryNode;

  @Before
  public void setUp() throws Exception {
    supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(10);
    supervisoryNode.setFacility(new Facility());
    SupervisoryNode parent = new SupervisoryNode();
    parent.setCode("PSN");
    parent.setId(20);
    supervisoryNode.setParent(parent);
    repository = new SupervisoryNodeRepository(supervisoryNodeMapper, facilityRepository, requisitionGroupRepository);
  }

  @Test
  public void shouldInsertSupervisoryNode() throws Exception {
    repository.insert(supervisoryNode);
    verify(supervisoryNodeMapper).insert(supervisoryNode);
  }

  @Test
  public void shouldUpdateSupervisoryNode() throws Exception {
    repository.update(supervisoryNode);
    verify(supervisoryNodeMapper).update(supervisoryNode);
  }

  @Test
  public void shouldReturnIdForTheGivenCode() {
    when(supervisoryNodeMapper.getIdForCode("ABC")).thenReturn(10);
    assertThat(repository.getIdForCode("ABC"), is(10));
  }

  @Test
  public void shouldThrowExceptionWhenCodeDoesNotExist() {
    when(supervisoryNodeMapper.getIdForCode("ABC")).thenReturn(null);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid SupervisoryNode Code");
    repository.getIdForCode("ABC");
  }

  @Test
  public void shouldReturnParentIdForASupervisoryNode() {
    when(supervisoryNodeMapper.getSupervisoryNode(10)).thenReturn(supervisoryNode);

    supervisoryNode.getParent().setId(null);
    assertThat(repository.getSupervisoryNodeParentId(10), is(nullValue()));

    supervisoryNode.getParent().setId(20);
    assertThat(repository.getSupervisoryNodeParentId(10), is(20));
  }

  @Test
  public void shouldGetSupervisoryNodeForFacilityProgram() throws Exception {
    Facility facility = new Facility(1);
    Program program = new Program(1);
    SupervisoryNode expectedSupervisoryNode = new SupervisoryNode();
    RequisitionGroup requisitionGroup = make(a(defaultRequisitionGroup, with(code, "test code")));
    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program, facility)).thenReturn(requisitionGroup);
    when(supervisoryNodeMapper.getFor(requisitionGroup.getCode())).thenReturn(expectedSupervisoryNode);

    SupervisoryNode actualSupervisoryNode = repository.getFor(facility, program);

    assertThat(actualSupervisoryNode, is(expectedSupervisoryNode));
  }

  @Test
  public void shouldReturnSupervisoryNodeAsNullWhenThereIsNoScheduleForAGivenRequisitionGroupAndProgram() throws Exception {
    Facility facility = new Facility(1);
    Program program = new Program(1);
    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program, facility)).thenReturn(null);

    SupervisoryNode actualSupervisoryNode = repository.getFor(facility, program);

    assertThat(actualSupervisoryNode, is(nullValue()));
  }

  @Test
  public void shouldGetAllSupervisoryNodesInHierarchy() throws Exception {
    Integer userId = 1;
    Integer programId = 1;
    List<SupervisoryNode> expectedList = new ArrayList<>();
    when(supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyBy(userId, programId, "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}")).thenReturn(expectedList);
    List<SupervisoryNode> actualList = repository.getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    verify(supervisoryNodeMapper).getAllSupervisoryNodesInHierarchyBy(userId, programId, "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}");
    assertThat(actualList, is(expectedList));
  }

  @Test
  public void shouldGetParentNodeForAGiveSupervisoryNode() throws Exception {
    SupervisoryNode expected = new SupervisoryNode();
    when(supervisoryNodeMapper.getParent(1)).thenReturn(expected);
    final SupervisoryNode actual = repository.getParent(1);
    verify(supervisoryNodeMapper).getParent(1);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetAllSupervisoryNodes() throws Exception {
    List<SupervisoryNode> expected = new ArrayList<>();
    when(supervisoryNodeMapper.getAll()).thenReturn(expected);

    List<SupervisoryNode> actual = repository.getAll();

    verify(supervisoryNodeMapper).getAll();
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetSupervisoryNodesWithRightsForUser() throws Exception {
    List<SupervisoryNode> expected = new ArrayList<>();
    when(supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyByUserAndRights(1, "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}")).thenReturn(expected);

    List<SupervisoryNode> actual = repository.getAllSupervisoryNodesInHierarchyBy(1, CREATE_REQUISITION, AUTHORIZE_REQUISITION);

    assertThat(actual, is(expected));
    verify(supervisoryNodeMapper).getAllSupervisoryNodesInHierarchyByUserAndRights(1, "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}");
  }

  @Test
  public void shouldGetAllParentSupervisoryNodesInHierarchy() throws Exception {
    List<SupervisoryNode> expected = new ArrayList<>();
    SupervisoryNode supervisoryNode = new SupervisoryNode(1);
    when(supervisoryNodeMapper.getAllParentSupervisoryNodesInHierarchy(supervisoryNode)).thenReturn(expected);

    List<SupervisoryNode> actual = repository.getAllParentSupervisoryNodesInHierarchy(supervisoryNode);

    verify(supervisoryNodeMapper).getAllParentSupervisoryNodesInHierarchy(supervisoryNode);
    assertThat(actual, is(expected));
  }
}
