/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.pod.service;

import org.openlmis.core.exception.DataException;
import org.openlmis.fulfillment.shared.FulfillmentPermissionService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.pod.repository.PODRepository;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.openlmis.core.domain.Right.MANAGE_POD;
import static org.openlmis.order.domain.OrderStatus.*;

@Service
public class PODService {

  @Autowired
  private PODRepository repository;

  @Autowired
  private OrderService orderService;

  @Autowired
  private RequisitionService requisitionService;

  @Autowired
  private FulfillmentPermissionService fulfillmentPermissionService;

  @Autowired
  private ShipmentService shipmentService;

  @Transactional
  public OrderPOD createPOD(OrderPOD orderPOD) {
    checkPermissions(orderPOD);

    if (orderService.hasStatus(orderPOD.getOrderId(), RELEASED, READY_TO_PACK, TRANSFER_FAILED)) {
      Rnr requisition = requisitionService.getFullRequisitionById(orderPOD.getOrderId());
      orderPOD.fillPODWithRequisition(requisition);
    } else if (orderService.hasStatus(orderPOD.getOrderId(), PACKED)) {
      List<ShipmentLineItem> shipmentLineItems = shipmentService.getLineItems(orderPOD.getOrderId());
      orderPOD.fillPodLineItems(shipmentLineItems);
    }

    return repository.insert(orderPOD);
  }

  public void updateOrderStatus(OrderPOD orderPod) {
    Order order = new Order(orderPod.getOrderId());
    order.setStatus(OrderStatus.RECEIVED);
    orderService.updateOrderStatus(order);
  }

  public void insertLineItems(OrderPOD orderPod) {
    for (OrderPODLineItem orderPodLineItem : orderPod.getPodLineItems()) {
      orderPodLineItem.setPodId(orderPod.getId());
      orderPodLineItem.setCreatedBy(orderPod.getCreatedBy());
      orderPodLineItem.setModifiedBy(orderPod.getModifiedBy());
      repository.insertPODLineItem(orderPodLineItem);
    }
  }

  public void checkPermissions(OrderPOD orderPod) {
    if (!fulfillmentPermissionService.hasPermission(orderPod.getModifiedBy(), orderPod.getOrderId(), MANAGE_POD)) {
      throw new DataException("error.permission.denied");
    }
  }

  public OrderPOD getPODByOrderId(Long orderId) {
    return repository.getPODByOrderId(orderId);
  }

  public List<OrderPODLineItem> getNPreviousOrderPodLineItems(String productCode,
                                                              Rnr requisition,
                                                              Integer n,
                                                              Date startDate) {
    return repository.getNPodLineItems(productCode, requisition, n, startDate);
  }

  public void insertPOD(OrderPOD orderPod) {
    repository.insertPOD(orderPod);
  }

  public OrderPOD getPodById(Long podId) {
    return repository.getPOD(podId);
  }

  @Transactional
  public OrderPOD save(OrderPOD orderPOD) {
    OrderPOD existingPod = repository.getPOD(orderPOD.getId());
    checkPermissions(existingPod);

    existingPod.copy(orderPOD);

    return repository.update(existingPod);
  }

  @Transactional
  public OrderPOD submit(Long podId, Long userId) {
    OrderPOD orderPOD = repository.getPOD(podId);
    orderPOD.setModifiedBy(userId);

    checkPermissions(orderPOD);
    orderPOD.validate();

    orderService.updateOrderStatus(new Order(orderPOD.getOrderId(), RECEIVED));

    return repository.update(orderPOD);
  }
}
