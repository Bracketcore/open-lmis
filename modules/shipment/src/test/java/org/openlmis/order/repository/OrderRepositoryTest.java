/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.repository.mapper.OrderMapper;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class OrderRepositoryTest {

  @Mock
  private OrderMapper orderMapper;
  @InjectMocks
  private OrderRepository orderRepository;

  @Test
  public void shouldSaveOrder() throws Exception {

    Order order = new Order();
    orderRepository.save(order);
    verify(orderMapper).insert(order);
  }

  @Test
  public void shouldGetOrders() {
    List<Order> expectedOrders = new ArrayList<>();
    when(orderMapper.getAll()).thenReturn(expectedOrders);

    List<Order> orders = orderRepository.getOrders();

    verify(orderMapper).getAll();
    assertThat(orders, is(expectedOrders));
  }

  @Test
  public void shouldUpdateStatusAndShipmentIdForOrder() throws Exception {
    List<Order> orders = new ArrayList<>();
    Order order1 = new Order();
    Order order2 = new Order();
    orders.add(order1);
    orders.add(order2);

    orderRepository.updateStatusAndShipmentIdForOrder(orders);

    verify(orderMapper, times(2)).updateShipmentInfo(any(Order.class));
  }

  @Test
  public void shouldGetOrderById() throws Exception {
    Order expectedOrder = new Order();
    when(orderMapper.getById(1L)).thenReturn(expectedOrder);
    Order savedOrder = orderRepository.getById(1L);
    verify(orderMapper).getById(1L);
    assertThat(savedOrder, is(expectedOrder));
  }

  @Test
  public void shouldGetOrderFileColumns() {
    OrderFileColumn orderFileColumn = new OrderFileColumn();
    orderFileColumn.setDataFieldLabel("facility.code");
    orderFileColumn.setColumnLabel("Facility code");
    orderFileColumn.setPosition(1);
    orderFileColumn.setIncludeInOrderFile(false);
    List<OrderFileColumn> orderFileColumns = asList(orderFileColumn);
    when(orderMapper.getOrderFileColumns()).thenReturn(orderFileColumns);
    assertThat(orderRepository.getOrderFileTemplate(), is(orderFileColumns));
    verify(orderMapper).getOrderFileColumns();
  }

  @Test
  public void shouldSaveOrderFileColumns() throws Exception {
    OrderFileColumn orderFileColumn = new OrderFileColumn();
    List<OrderFileColumn> orderFileColumns = asList(orderFileColumn);
    Long userId = 1L;
    orderRepository.saveOrderFileColumns(orderFileColumns, userId);
    verify(orderMapper).deleteOrderFileColumns();
    verify(orderMapper, times(1)).insertOrderFileColumn(orderFileColumn);
  }
}
