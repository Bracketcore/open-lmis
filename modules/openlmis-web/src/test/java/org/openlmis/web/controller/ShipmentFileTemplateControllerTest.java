/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.springframework.http.HttpStatus.OK;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ShipmentFileTemplateControllerTest {


  private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
  @Mock
  ShipmentFileTemplateService shipmentFileTemplateService;

  @InjectMocks
  ShipmentFileTemplateController controller;
  private Long userId = 111L;

  @Before
  public void setUp() throws Exception {
    MockHttpSession mockHttpSession = new MockHttpSession();
    httpServletRequest.setSession(mockHttpSession);
    mockHttpSession.setAttribute(USER_ID, userId);

  }

  @Test
  public void shouldGetShipmentFileTemplate() {

    ShipmentFileTemplate expectedShipmentFileTemplate = new ShipmentFileTemplate();

    when(shipmentFileTemplateService.get()).thenReturn(expectedShipmentFileTemplate);

    ResponseEntity<OpenLmisResponse> response = controller.get();

    assertThat((ShipmentFileTemplate) response.getBody().getData().get("shipment_template"), is(expectedShipmentFileTemplate));
    verify(shipmentFileTemplateService).get();
  }

  @Test
  public void shouldUpdateShipmentFileTemplate() {
    ShipmentFileColumn shipmentFileColumn1 = new ShipmentFileColumn("name", "Label", 1, false, true, "dd/mm/yy");
    ShipmentFileColumn shipmentFileColumn2 = new ShipmentFileColumn("name", "Label", 2, false, true, "dd/mm/yy");
    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate(
      new ShipmentConfiguration(true),
      asList(shipmentFileColumn1, shipmentFileColumn2));


    ResponseEntity<OpenLmisResponse> response = controller.update(shipmentFileTemplate, httpServletRequest);

    assertThat(response.getStatusCode(), is(OK));
    assertThat(response.getBody().getSuccessMsg(), is("shipment.file.configuration.success"));
    assertThat(shipmentFileTemplate.getShipmentConfiguration().getModifiedBy(), is(userId));
    for (ShipmentFileColumn column : shipmentFileTemplate.getShipmentFileColumns()) {
      assertThat(column.getModifiedBy(), is(userId));
    }

    verify(shipmentFileTemplateService).update(shipmentFileTemplate);

  }
}
