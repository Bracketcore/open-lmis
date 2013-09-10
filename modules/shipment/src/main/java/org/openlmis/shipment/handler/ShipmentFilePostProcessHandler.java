/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.handler;

import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Set;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.springframework.integration.support.MessageBuilder.withPayload;

@Component
@NoArgsConstructor
public class ShipmentFilePostProcessHandler {

  @Autowired
  private ShipmentService shipmentService;

  @Autowired
  private MessageChannel ftpErrorChannel;

  @Autowired
  private MessageChannel ftpArchiveOutputChannel;


  private static final Logger logger = Logger.getLogger(ShipmentFilePostProcessHandler.class);


  public void process(Set<Long> orderIds, File shipmentFile, boolean error) {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo(shipmentFile.getName(), error);

    shipmentService.insertShipmentFileInfo(shipmentFileInfo);

    shipmentService.updateStatusAndShipmentIdForOrders(orderIds, shipmentFileInfo);

    Message<File> message = withPayload(shipmentFile).build();

    if (error) {
      ftpErrorChannel.send(message);
      logger.warn("Shipment file " + shipmentFile.getName() + " copied to error folder");
    } else {
      ftpArchiveOutputChannel.send(message);
      logger.debug("Shipment file " + shipmentFile.getName() + " archived");
    }

    if (!deleteQuietly(shipmentFile)) {
      logger.error("Unable to delete temporary shipment file " + shipmentFile.getName());
    }
  }


}
