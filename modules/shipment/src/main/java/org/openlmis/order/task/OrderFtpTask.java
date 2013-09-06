/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package org.openlmis.order.task;

import org.apache.camel.CamelExecutionException;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.service.FacilityFtpDetailsService;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.helper.OrderCsvHelper;
import org.openlmis.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openlmis.order.domain.OrderStatus.RELEASED;
import static org.openlmis.order.domain.OrderStatus.TRANSFER_FAILED;

@Component
public class OrderFtpTask {

  @Autowired
  private SupplyLineService supplyLineService;

  @Autowired
  private FacilityFtpDetailsService facilityFtpDetailsService;

  @Autowired
  private OrderService orderService;

  @Autowired
  private OrderCsvHelper orderCsvHelper;

  @Autowired
  private OrderFtpSender ftpSender;


  private static Logger logger = Logger.getLogger(OrderFtpTask.class);

  private static String CONNECTION_REFUSED = "Connection refused";
  private static String LOGIN_INCORRECT = "530 Login incorrect";
  private static String PERMISSION_DENIED = "Error writing file";

  @ServiceActivator(inputChannel = "orderInputChannel")
  public void processOrder(List<Order> orders) {
    for (Order order : orders) {
      order = orderService.getOrderForDownload(order.getId());
      SupplyLine supplyLine = order.getSupplyLine();

      if (supplyLine == null) {
        updateOrder(order, TRANSFER_FAILED, "Supply line missing");
        continue;
      }

      supplyLine = supplyLineService.getById(supplyLine.getId());
      FacilityFtpDetails supplyingFacilityFtpDetails = facilityFtpDetailsService.getByFacilityId(supplyLine.getSupplyingFacility());

      if (supplyingFacilityFtpDetails == null) {
        updateOrder(order, TRANSFER_FAILED, "FTP credentials are missing");
        continue;
      }

      OrderFileTemplateDTO orderFileTemplateDTO = orderService.getOrderFileTemplateDTO();
      String fileName = orderFileTemplateDTO.getOrderConfiguration().getFilePrefix() + order.getId() + ".csv";

      File file = new File(fileName);
      try (FileWriter fileWriter = new FileWriter(file)) {
        orderCsvHelper.writeCsvFile(order, orderFileTemplateDTO, fileWriter);
        fileWriter.flush();
        ftpSender.sendFile(supplyingFacilityFtpDetails, file);
      } catch (CamelExecutionException camelException) {
        handleException(camelException, order);
      } catch (Exception e) {
        logger.error("Error in ftp of order file", e);
        updateOrder(order, TRANSFER_FAILED, null);
      }
      updateOrder(order, RELEASED, null);
    }
  }

  private void handleException(CamelExecutionException camelException, Order order) {
    logger.error("Error in ftp of order file", camelException);
    Pattern pattern = Pattern.compile(camelException.getCause().getMessage());
    updateOrderForException(CONNECTION_REFUSED, pattern, order, "Unable to connect");
    updateOrderForException(LOGIN_INCORRECT, pattern, order, "Invalid credentials");
    updateOrderForException(PERMISSION_DENIED, pattern, order, "Inappropriate permissions");
    orderService.updateOrderStatus(order);
  }

  private void updateOrderForException(String exception, Pattern pattern, Order order, String ftpComment) {
    Matcher matcher = pattern.matcher(exception);
    if (matcher.find()) {
      order.setStatus(TRANSFER_FAILED);
      order.setFtpComment(ftpComment);
    }
  }

  private void updateOrder(Order order, OrderStatus status, String ftpComment) {
    order.setStatus(status);
    order.setFtpComment(ftpComment);
    orderService.updateOrderStatus(order);
  }

}
