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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
 public class RoleAssignmentServiceTest {

  RoleAssignmentService service;

  @Mock
  RoleAssignmentRepository roleAssignmentRepository;

  @Before
  public void setUp() throws Exception {
    service = new RoleAssignmentService(roleAssignmentRepository);
  }

  @Test
  public void shouldDeleteRoleAssignmentsOfAUser() throws Exception {
    service.deleteAllRoleAssignmentsForUser(1L);
    verify(roleAssignmentRepository).deleteAllRoleAssignmentsForUser(1L);
  }

  @Test
  public void shouldGetSupervisorRoleAssignments() throws Exception {

    List<RoleAssignment> expected = new ArrayList<>();
    when(roleAssignmentRepository.getSupervisorRoles(1L)).thenReturn(expected);
    List<RoleAssignment> actual = service.getSupervisorRoles(1L);

    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetHomeFacilityRoleAssignments() throws Exception {

    List<RoleAssignment> expected = new ArrayList<>();
    when(roleAssignmentRepository.getHomeFacilityRoles(1L)).thenReturn(expected);
    List<RoleAssignment> actual = service.getHomeFacilityRoles(1L);

    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetAllocationRoleAssignments() throws Exception {

    List<RoleAssignment> expected = new ArrayList<>();
    when(roleAssignmentRepository.getAllocationRoles(1L)).thenReturn(expected);
    List<RoleAssignment> actual = service.getAllocationRoles(1L);

    assertThat(actual, is(expected));
    verify(roleAssignmentRepository).getAllocationRoles(1L);
  }

  @Test
  public void shouldGetAdminRoleAssignments() throws Exception {

    RoleAssignment expected = new RoleAssignment();
    when(roleAssignmentRepository.getAdminRole(1L)).thenReturn(expected);
    RoleAssignment actual = service.getAdminRole(1L);

    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetRoleAssignmentsForAGivenUserOnAGivenProgramWithRights() throws Exception {
    Long userId =1L;
    Long programId =2L;
    List<RoleAssignment> expected = new ArrayList<>();
    when(roleAssignmentRepository.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION)).thenReturn(expected);
    List<RoleAssignment> actual = service.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION);

    assertThat(actual, is(expected));
  }

  @Test
  public void shouldSaveUserRoleAssignments() throws Exception {
    User user = new User();
    final RoleAssignment homeRoleAssignment = new RoleAssignment();
    homeRoleAssignment.setProgramId(1L);
    final RoleAssignment supervisorRoleAssignment = new RoleAssignment();
    supervisorRoleAssignment.setProgramId(2L);
    final RoleAssignment allocationRoleAssignment = new RoleAssignment();
    allocationRoleAssignment.setProgramId(3L);
    List<RoleAssignment> homeFacilityRoles = new ArrayList<RoleAssignment>(){{add(homeRoleAssignment);}};
    List<RoleAssignment> supervisorRoles = new ArrayList<RoleAssignment>(){{add(supervisorRoleAssignment);}};;
    List<RoleAssignment> allocationRoles = new ArrayList<RoleAssignment>(){{add(allocationRoleAssignment);}};
    user.setHomeFacilityRoles(homeFacilityRoles);
    user.setSupervisorRoles(supervisorRoles);
    user.setAllocationRoles(allocationRoles);
    final RoleAssignment adminRole = new RoleAssignment();
    user.setAdminRole(adminRole);

    service.saveRolesForUser(user);

    verify(roleAssignmentRepository).insert(homeFacilityRoles, user.getId());
    verify(roleAssignmentRepository).insert(allocationRoles, user.getId());
    verify(roleAssignmentRepository).insert(supervisorRoles, user.getId());
    verify(roleAssignmentRepository).insert(Arrays.asList(adminRole), user.getId());
  }
}
