/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.service;

import lombok.NoArgsConstructor;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@NoArgsConstructor
public class ShipmentService {
  private ShipmentRepository shipmentRepository;

  @Autowired
  public ShipmentService(ShipmentRepository repository) {
    shipmentRepository = repository;
  }

  public void insertShippedLineItem(ShippedLineItem shippedLineItem) {
     shipmentRepository.insertShippedLineItem(shippedLineItem);
  }

  public void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo) {
    shipmentRepository.insertShipmentFileInfo(shipmentFileInfo);
  }

  public void updateOrders(Set<Integer> orderIds, ShipmentFileInfo shipmentFileInfo) {

  }
}
