/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.service;


import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.repository.ShipmentRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ShipmentServiceTest {

  @Mock
  private ShipmentRepository shipmentRepository;
  @Mock
  private OrderService orderService;
  @Mock
  private RequisitionService requisitionService;
  @Mock
  private ProductService productService;
  @InjectMocks
  private ShipmentService shipmentService;

  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Test
  public void shouldInsertShipment() throws Exception {
    ShippedLineItem shippedLineItem = spy(new ShippedLineItem(1l, "P10", 500));
    when(requisitionService.getLWById(1l)).thenReturn(new Rnr());
    when(productService.getIdForCode("P10")).thenReturn(1l);

    shipmentService.insertShippedLineItem(shippedLineItem);

    verify(requisitionService).getLWById(1l);
    verify(productService).getIdForCode("P10");
    verify(shipmentRepository).insertShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldNotInsertShipmentIfRnrIdIsNotValid() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem(1l, "P10", 500);
    when(requisitionService.getLWById(1l)).thenReturn(null);
    when(productService.getIdForCode("P10")).thenReturn(1l);


    exException.expect(DataException.class);
    exException.expectMessage("error.unknown.order");

    shipmentService.insertShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldNotInsertShipmentIfProductCodeIsNotValid() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem(1l, "P10", 500);
    when(requisitionService.getLWById(1l)).thenReturn(new Rnr());
    when(productService.getIdForCode("P10")).thenReturn(null);


    exException.expect(DataException.class);
    exException.expectMessage("error.unknown.product");

    shipmentService.insertShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldNotInsertShipmentIfQuantityNegative() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem(1l, "P10", -1);

    exException.expect(DataException.class);
    exException.expectMessage("error.negative.shipped.quantity");

    shipmentService.insertShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldInsertShipmentInfo() throws Exception {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentService.insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentRepository).insertShipmentFileInfo(shipmentFileInfo);
  }

  @Test
  public void shouldUpdateOrders() throws Exception {
    final ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentFileInfo.setId(1L);
    shipmentFileInfo.setProcessingError(FALSE);
    List<Long> orderIds = new ArrayList<>();
    orderIds.add(1L);

    shipmentService.updateStatusAndShipmentIdForOrders(orderIds, shipmentFileInfo);

    final ArgumentMatcher<List<Order>> argumentMatcher = new ArgumentMatcher<List<Order>>() {
      @Override
      public boolean matches(Object argument) {
        List<Order> orders = (List<Order>) argument;
        Order order = orders.get(0);
        return order.getShipmentFileInfo().equals(shipmentFileInfo) && order.getRnr().getId().equals(1L);
      }
    };
    verify(orderService).updateFulfilledAndShipmentIdForOrders(argThat(argumentMatcher));
  }


  @Test
  public void shouldGetProcessedTimeStampByOrderId() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shippedLineItem.setRnrId(1L);
    Date expectedTimestamp = new Date();
    when(shipmentRepository.getProcessedTimeStamp(shippedLineItem)).thenReturn(expectedTimestamp);

    Date processTimeStamp = shipmentService.getProcessedTimeStamp(shippedLineItem);

    assertThat(processTimeStamp, is(expectedTimestamp));
    verify(shipmentRepository).getProcessedTimeStamp(shippedLineItem);
  }
}
