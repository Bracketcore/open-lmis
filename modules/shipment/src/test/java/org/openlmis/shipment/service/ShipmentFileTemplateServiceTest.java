/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.repository.ShipmentTemplateRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@PrepareForTest(ShipmentFileTemplateService.class)
@RunWith(PowerMockRunner.class)
public class ShipmentFileTemplateServiceTest {

  @Mock
  ShipmentTemplateRepository repository;

  @InjectMocks
  ShipmentFileTemplateService service;


  @Test
  public void shouldUpdateTemplate() {
    List<ShipmentFileColumn> shipmentFileColumns = new ArrayList<ShipmentFileColumn>() {{
      add(new ShipmentFileColumn());
      add(new ShipmentFileColumn());
      add(new ShipmentFileColumn());
    }};
    EDIConfiguration shipmentConfiguration = new EDIConfiguration();
    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumns);

    service.update(shipmentFileTemplate);
    verify(repository).updateShipmentConfiguration(shipmentConfiguration);
    verify(repository, times(3)).update(any(ShipmentFileColumn.class));
  }

  @Test
  public void shouldGetShipmentTemplate() throws Exception {
    EDIConfiguration shipmentConfiguration = new EDIConfiguration();
    List<ShipmentFileColumn> shipmentFileColumns = new ArrayList<>();
    when(repository.getShipmentConfiguration()).thenReturn(shipmentConfiguration);
    when(repository.getAllShipmentFileColumns()).thenReturn(shipmentFileColumns);

    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate();
    whenNew(ShipmentFileTemplate.class).withArguments(shipmentConfiguration, shipmentFileColumns).thenReturn(shipmentFileTemplate);

    ShipmentFileTemplate shipmentFileTemplateDTO = service.get();

    assertThat(shipmentFileTemplateDTO, is(shipmentFileTemplate));
    verify(repository).getShipmentConfiguration();
    verify(repository).getAllShipmentFileColumns();
  }
}
