/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@NoArgsConstructor
public class ShipmentService {
  @Autowired
  private ShipmentRepository shipmentRepository;
  @Autowired
  private OrderService orderService;
  @Autowired
  private RequisitionService requisitionService;
  @Autowired
  private ProductService productService;


  public void insertShippedLineItem(ShipmentLineItem shipmentLineItem) {
    validateForSave(shipmentLineItem);
    validateShipment(shipmentLineItem);
    shipmentRepository.insertShippedLineItem(shipmentLineItem);
  }

  private void validateShipment(ShipmentLineItem shipmentLineItem) {
    if (requisitionService.getLWById(shipmentLineItem.getOrderId()) == null) {
      throw new DataException("error.unknown.order");
    }
    if (productService.getIdForCode(shipmentLineItem.getProductCode()) == null) {
      throw new DataException("error.unknown.product");
    }
  }

  private void validateForSave(ShipmentLineItem shipmentLineItem) {
    if (shipmentLineItem.getQuantityShipped() < 0) {
      throw new DataException("error.negative.shipped.quantity");
    }
  }

  public void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo) {
    shipmentRepository.insertShipmentFileInfo(shipmentFileInfo);
  }

  public void updateStatusAndShipmentIdForOrders(Set<Long> orderIds, ShipmentFileInfo shipmentFileInfo) {
    List<Order> orders = new ArrayList<>();
    for (Long id : orderIds) {
      Order order = new Order(id);
      order.updateShipmentFileInfo(shipmentFileInfo);
      orders.add(order);
    }
    orderService.updateFulfilledAndShipmentIdForOrders(orders);
  }

  public Date getProcessedTimeStamp(ShipmentLineItem shipmentLineItem) {
    return shipmentRepository.getProcessedTimeStamp(shipmentLineItem);
  }

}
