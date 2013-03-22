/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.repository.mapper.RoleAssignmentMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

@RunWith(MockitoJUnitRunner.class)
public class RoleAssignmentRepositoryTest {
  private RoleAssignmentRepository repository;

  @Mock
  private RoleAssignmentMapper mapper;

  @Before
  public void setUp() throws Exception {
    repository = new RoleAssignmentRepository(mapper);
  }

  @Test
  public void shouldInsertUserProgramRoleMapping() throws Exception {
    repository.insertRoleAssignment(1, 3, 1, 2);

    verify(mapper).insertRoleAssignment(1, 3, 1, 2);
  }

  @Test
  public void shouldDeleteAllRoleAssignmentsForTheUser() throws Exception {
    repository.deleteAllRoleAssignmentsForUser(1);

    verify(mapper).deleteAllRoleAssignmentsForUser(1);
  }

  @Test
  public void shouldGetSupervisorRoles() throws Exception {
    List<RoleAssignment> expected = new ArrayList<>();
    when(mapper.getSupervisorRoles(1)).thenReturn(expected);
    List<RoleAssignment> actual = repository.getSupervisorRoles(1);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetHomeFacilityRoles() throws Exception {
    List<RoleAssignment> expected = new ArrayList<>();
    when(mapper.getHomeFacilityRoles(1)).thenReturn(expected);
    List<RoleAssignment> actual = repository.getHomeFacilityRoles(1);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetRoleAssignmentsForAGivenUserOnAGivenProgramWithRights() throws Exception {
    Integer userId = 1;
    Integer programId = 2;
    List<RoleAssignment> expected = new ArrayList<>();
    when(mapper.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, "['CREATE_REQUISITION', 'AUTHORIZE_REQUISITION']")).thenReturn(expected);
    List<RoleAssignment> actual = repository.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    assertThat(actual, is(expected));
  }
}
