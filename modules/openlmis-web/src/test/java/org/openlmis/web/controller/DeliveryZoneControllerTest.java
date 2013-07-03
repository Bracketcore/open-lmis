/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.AllocationPermissionService;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.MANAGE_DISTRIBUTION;
import static org.openlmis.web.controller.DeliveryZoneController.DELIVERY_ZONES;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneControllerTest {


  @InjectMocks
  DeliveryZoneController controller;

  @Mock
  DeliveryZoneService service;

  @Mock
  AllocationPermissionService permissionService;


  MockHttpServletRequest request;
  private static final String USER = "user";
  private static final Long USER_ID = 1l;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);
  }

  @Test
  public void shouldGetAllDeliveryZonesForUser() throws Exception {

    List<DeliveryZone> deliveryZones = new ArrayList<>();
    when(service.getByUserForRight(USER_ID, MANAGE_DISTRIBUTION)).thenReturn(deliveryZones);

    ResponseEntity<OpenLmisResponse> response = controller.getDeliveryZonesForInitiatingAllocation(request);

    assertThat((List<DeliveryZone>) response.getBody().getData().get(DELIVERY_ZONES), is(deliveryZones));
  }

  @Test
  public void shouldGetProgramsForADeliveryZone() throws Exception {
    List<Program> programs = new ArrayList<>();
    when(service.getProgramsForDeliveryZone(1l)).thenReturn(programs);
    when(permissionService.hasPermissionOnZone(USER_ID, 1l)).thenReturn(true);
    ResponseEntity<OpenLmisResponse> response = controller.getProgramsForDeliveryZone(1l);

    assertThat((List<Program>) response.getBody().getData().get("deliveryZonePrograms"), is(programs));
  }

  @Test
  public void shouldGetDeliveryZoneById() throws Exception {
    DeliveryZone zone = new DeliveryZone();
    when(service.getById(1l)).thenReturn(zone);
    when(permissionService.hasPermissionOnZone(USER_ID, 1l)).thenReturn(true);

    ResponseEntity<OpenLmisResponse> response = controller.get(1l);

    verify(service).getById(1l);
    assertThat((DeliveryZone) response.getBody().getData().get("zone"), is(zone));

  }
}
