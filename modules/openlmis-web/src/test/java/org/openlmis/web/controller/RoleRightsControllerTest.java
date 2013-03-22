/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Boolean.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.core.domain.Right.CONFIGURE_RNR;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.openlmis.core.matchers.Matchers.facilityMatcher;
import static org.openlmis.core.matchers.Matchers.programMatcher;
import static org.openlmis.web.controller.RoleRightsController.*;
import static org.openlmis.web.response.OpenLmisResponse.SUCCESS;

@RunWith(MockitoJUnitRunner.class)
public class RoleRightsControllerTest {

  Role role;

  @Mock
  RoleRightsService roleRightsService;

  private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

  RoleRightsController controller;
  private static final int LOGGED_IN_USERID = 11;

  @Before
  public void setUp() throws Exception {
    controller = new RoleRightsController(roleRightsService);
    MockHttpSession mockHttpSession = new MockHttpSession();
    httpServletRequest.setSession(mockHttpSession);
    mockHttpSession.setAttribute(USER_ID, LOGGED_IN_USERID);
    role = new Role("test role", FALSE, "test role description");
  }

  @Test
  public void shouldFetchAllRightsInSystem() throws Exception {
    Set<Right> rights = new HashSet<>();
    when(roleRightsService.getAllRights()).thenReturn(rights);
    ResponseEntity<OpenLmisResponse> result = controller.getAllRights();
    assertThat((Set<Right>) result.getBody().getData().get(RIGHTS), is(rights));
    verify(roleRightsService).getAllRights();
  }

  @Test
  public void shouldSaveRole() throws Exception {
    ResponseEntity<OpenLmisResponse> responseEntity = controller.createRole(role, httpServletRequest);
    verify(roleRightsService).saveRole(role);
    assertThat(role.getModifiedBy(), is(LOGGED_IN_USERID));
    String successMsg = (String) responseEntity.getBody().getData().get(SUCCESS);
    assertThat(successMsg, is("'test role' created successfully"));
  }

  @Test
  public void shouldGiveErrorIfRoleNotSaved() throws Exception {
    doThrow(new DataException("Error message")).when(roleRightsService).saveRole(role);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.createRole(role, httpServletRequest);

    verify(roleRightsService).saveRole(role);
    assertThat(responseEntity.getBody().getErrorMsg(), is("Error message"));
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CONFLICT));
  }

  @Test
  public void shouldGetAllRolesWithRights() throws Exception {
    List<Role> roles = new ArrayList<>();
    when(roleRightsService.getAllRoles()).thenReturn(roles);
    OpenLmisResponse response = controller.getAll().getBody();
    assertThat((List<Role>) response.getData().get(ROLES), is(roles));
    verify(roleRightsService).getAllRoles();
  }

  @Test
  public void shouldGetRoleById() throws Exception {
    Role role = new Role();
    int roleId = 1;
    when(roleRightsService.getRole(roleId)).thenReturn(role);

    OpenLmisResponse response = controller.get(roleId).getBody();

    assertThat((Role) response.getData().get(ROLE), is(role));
    verify(roleRightsService).getRole(roleId);
  }

  @Test
  public void shouldUpdateRoleAndRights() throws Exception {
    Role role = new Role(123, "Role Name",null, "Desc", null, null, new HashSet<>(asList(CONFIGURE_RNR)));

    OpenLmisResponse response = controller.updateRole(role.getId(), role, httpServletRequest).getBody();

    assertThat(response.getSuccessMsg(), is("Role Name updated successfully"));
    verify(roleRightsService).updateRole(role);
  }

  @Test
  public void shouldReturnErrorMsgIfUpdateFails() throws Exception {

    Role role = new Role(123, "Role Name", FALSE, "Desc");

    doThrow(new DataException("Duplicate Role found")).when(roleRightsService).updateRole(role);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.updateRole(role.getId(), role, httpServletRequest);

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CONFLICT));
    assertThat(responseEntity.getBody().getErrorMsg(), is("Duplicate Role found"));
  }

  @Test
  public void shouldGetRightsForUserAndFacilityProgram() throws Exception {
    Set<Right> rights = new HashSet<Right>() {{
      add(CREATE_REQUISITION);
    }};
    Integer facilityId = 1;
    Integer programId = 1;
    when(roleRightsService.getRightsForUserAndFacilityProgram(eq(LOGGED_IN_USERID), any(Facility.class), any(Program.class))).thenReturn(rights);
    ResponseEntity<OpenLmisResponse> response = controller.getRightsForUserAndFacilityProgram(facilityId, programId, httpServletRequest);

    assertThat((Set<Right>) response.getBody().getData().get("rights"), is(rights));
    verify(roleRightsService).getRightsForUserAndFacilityProgram(eq(LOGGED_IN_USERID), argThat(facilityMatcher(facilityId)), argThat(programMatcher(programId)));
  }
}
