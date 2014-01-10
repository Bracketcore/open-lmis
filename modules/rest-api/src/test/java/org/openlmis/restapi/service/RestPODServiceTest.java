/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.restapi.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.service.PODService;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class RestPODServiceTest {

  @InjectMocks
  private RestPODService restPODService;

  @Mock
  private OrderService orderService;

  @Mock
  private PODService podService;

  @Rule
  private ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldUpdatePOD() {
    OrderPOD orderPod = new OrderPOD(3L);
    orderPod.setOrderId(4L);

    OrderPOD spyOrderPod = spy(orderPod);
    doNothing().when(spyOrderPod).validate();
    when(orderService.getOrder(4L)).thenReturn(new Order());
    when(podService.getPODByOrderId(4L)).thenReturn(null);

    restPODService.updatePOD(spyOrderPod, 1L);

    verify(orderService).getOrder(4L);
    verify(podService).getPODByOrderId(4L);
    verify(podService).updatePOD(orderPod);
  }

  @Test
  public void shouldThrowErrorIfInvalidOrder() throws Exception {
    OrderPOD orderPod = new OrderPOD(3L);
    orderPod.setOrderId(4L);

    OrderPOD spyOrderPod = spy(orderPod);
    doNothing().when(spyOrderPod).validate();
    when(orderService.getOrder(4L)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.invalid.order");

    restPODService.updatePOD(spyOrderPod, 1L);
  }

  @Test
  public void shouldThrowErrorIfPODAlreadyConfirmed() throws Exception {
    OrderPOD orderPod = new OrderPOD(3L);
    orderPod.setOrderId(4L);

    OrderPOD spyOrderPod = spy(orderPod);
    doNothing().when(spyOrderPod).validate();
    when(orderService.getOrder(4L)).thenReturn(new Order());
    when(podService.getPODByOrderId(4L)).thenReturn(new OrderPOD());

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.delivery.already.confirmed");

    restPODService.updatePOD(spyOrderPod, 1L);
  }
}
