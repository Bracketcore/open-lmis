/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Configuration;
import org.openlmis.core.repository.ConfigurationRepository;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.repository.OrderRepository;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.service.RequisitionService;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.openlmis.rnr.builder.RequisitionBuilder.*;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

  @Mock
  private ConfigurationRepository configurationRepository;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private RequisitionService requisitionService;

  @SuppressWarnings("unuse")
  @InjectMocks
  private OrderService orderService;

  @Test
  public void shouldSaveOrder() throws Exception {
    Order order = new Order();
    orderService.save(order);
    verify(orderRepository).save(order);
  }

  @Test
  public void shouldConvertRequisitionsToOrder() throws Exception {
    List<Rnr> rnrList = new ArrayList<>();
    Rnr rnr = new Rnr();
    rnrList.add(rnr);
    Long userId = 1L;
    orderService.convertToOrder(rnrList, userId);
    Order order = new Order(rnr);
    whenNew(Order.class).withArguments(rnr).thenReturn(order);
    verify(requisitionService).releaseRequisitionsAsOrder(rnrList, userId);
    verify(orderRepository).save(order);
  }

  @Test
  public void shouldGetOrdersFilledWithRequisition() throws Exception {
    Rnr rnr1 = make(a(defaultRnr, with(id, 78L)));
    final Order order1 = new Order();
    order1.setRnr(rnr1);

    Rnr rnr2 = make(a(defaultRnr, with(periodId, 2L), with(id, 72L)));

    final Order order2 = new Order();
    order2.setRnr(rnr2);

    List<Order> expectedOrders = new ArrayList<Order>() {{
      add(order1);
      add(order2);
    }};

    when(orderRepository.getOrders()).thenReturn(expectedOrders);
    when(requisitionService.getFullRequisitionById(rnr1.getId())).thenReturn(rnr1);
    when(requisitionService.getFullRequisitionById(rnr2.getId())).thenReturn(rnr2);

    List<Order> orders = orderService.getOrders();
    assertThat(orders, is(expectedOrders));
    verify(orderRepository).getOrders();
    verify(requisitionService).getFullRequisitionById(rnr1.getId());
    verify(requisitionService).getFullRequisitionById(rnr2.getId());
  }

  @Test
  public void shouldUpdateFulfilledAndShipmentIdForOrders() throws Exception {
    List<Order> orders = new ArrayList<>();

    orderService.updateFulfilledAndShipmentIdForOrders(orders);

    verify(orderRepository).updateStatusAndShipmentIdForOrder(orders);
  }

  @Test
  public void shouldGetOrderWithoutUnorderedProducts() {
    Long orderId = 1L;
    Long rnrId = 1L;
    Order order = new Order();

    Rnr rnr = new Rnr();
    rnr.setId(rnrId);
    RnrLineItem rnrLineItem = new RnrLineItem();
    List<RnrLineItem> lineItems = new ArrayList<>();
    rnrLineItem.setPacksToShip(0);
    lineItems.add(rnrLineItem);
    rnr.setFullSupplyLineItems(lineItems);
    rnr.setNonFullSupplyLineItems(lineItems);
    order.setRnr(rnr);

    when(orderRepository.getById(orderId)).thenReturn(order);
    when(requisitionService.getFullRequisitionById(rnr.getId())).thenReturn(rnr);

    Order expectedOrder = orderService.getOrderForDownload(orderId);

    verify(orderRepository).getById(orderId);
    verify(requisitionService).getFullRequisitionById(rnr.getId());
    assertThat(order.getRnr().getFullSupplyLineItems().size(), is(0));
    assertThat(order.getRnr().getNonFullSupplyLineItems().size(), is(0));
    assertThat(expectedOrder, is(order));
  }

  @Test
  public void shouldGetOrderFileTemplateWithConfiguration() throws Exception {
    Configuration configuration = new Configuration();
    List<OrderFileColumn> orderFileColumns = new ArrayList<>();
    OrderFileTemplateDTO expectedOrderFileTemplateDTO = new OrderFileTemplateDTO(configuration, orderFileColumns);
    when(configurationRepository.getConfiguration()).thenReturn(configuration);
    when(orderRepository.getOrderFileTemplate()).thenReturn(orderFileColumns);
    OrderFileTemplateDTO actualOrderFileTemplateDTO = orderService.getOrderFileTemplateDTO();
    verify(configurationRepository).getConfiguration();
    verify(orderRepository).getOrderFileTemplate();
    assertThat(actualOrderFileTemplateDTO, is(expectedOrderFileTemplateDTO));
  }
}
