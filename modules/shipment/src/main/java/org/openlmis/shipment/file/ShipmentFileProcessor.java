/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.service.DbService;
import org.openlmis.order.dto.ShipmentLineItemDTO;
import org.openlmis.shipment.ShipmentLineItemTransformer;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.file.csv.handler.ShipmentFilePostProcessHandler;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.shipment.service.ShipmentService;
import org.openlmis.upload.parser.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.annotation.MessageEndpoint;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.filter;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

@MessageEndpoint
@NoArgsConstructor
public class ShipmentFileProcessor {
  private static Logger logger = LoggerFactory.getLogger(ShipmentFileProcessor.class);

  @Autowired
  private CSVParser csvParser;

  @Autowired
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;

  @Autowired
  private DbService dbService;

  @Autowired
  private MessageChannel ftpArchiveOutputChannel;

  @Autowired
  private ShipmentFileTemplateService shipmentFileTemplateService;

  @Autowired
  private ShipmentService shipmentService;

  @Autowired
  private ShipmentLineItemTransformer transformer;


  public void process(Message message) throws IOException, NoSuchFieldException, IllegalAccessException {
    File shipmentFile = (File) message.getPayload();

    ShipmentFileTemplate shipmentFileTemplate = shipmentFileTemplateService.get();

    List<ShipmentFileColumn> shipmentFileColumns = shipmentFileTemplate.getShipmentFileColumns();

    filter(shipmentFileColumns, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((ShipmentFileColumn) o).getInclude();
      }
    });

    int maxPosition = findMaximumPosition(shipmentFileColumns);

    try (ICsvListReader listReader = new CsvListReader(new FileReader(shipmentFile), STANDARD_PREFERENCE)) {

      ignoreFirstLineIfHeadersArePresent(shipmentFileTemplate, listReader);

      List<String> fieldsInOneRow;

      while ((fieldsInOneRow = listReader.read()) != null) {
        if (fieldsInOneRow.size() < maxPosition) {
          logger.warn("Shipment file should contain at least " + maxPosition + " columns");
          throw new DataException("mandatory.data.missing");
        } else {
          ShipmentLineItemDTO dto = populateDTO(fieldsInOneRow, shipmentFileColumns);
          ShipmentLineItem lineItem = transformer.transform(dto, null, null);
          shipmentService.insertShippedLineItem(lineItem);
        }
      }
    }
  }

  private ShipmentLineItemDTO populateDTO(List<String> fieldsInOneRow, List<ShipmentFileColumn> shipmentFileColumns)
    throws NoSuchFieldException, IllegalAccessException {

    ShipmentLineItemDTO dto = new ShipmentLineItemDTO();

    for (ShipmentFileColumn shipmentFileColumn : shipmentFileColumns) {
      Integer position = shipmentFileColumn.getPosition();
      String name = shipmentFileColumn.getName();
      Field field = ShipmentLineItemDTO.class.getDeclaredField(name);
      field.setAccessible(true);
      field.set(dto, fieldsInOneRow.get(position - 1));
    }
    return dto;
  }

  private void ignoreFirstLineIfHeadersArePresent(ShipmentFileTemplate shipmentFileTemplate,
                                                  ICsvListReader listReader) throws IOException {
    if (shipmentFileTemplate.getShipmentConfiguration().isHeaderInFile()) {
      listReader.getHeader(true);
    }
  }

  private int findMaximumPosition(List<ShipmentFileColumn> shipmentFileColumns) {
    int maxPosition = 0;
    for (ShipmentFileColumn shipmentFileColumn : shipmentFileColumns) {
      if (shipmentFileColumn.getPosition() > maxPosition) {
        maxPosition = shipmentFileColumn.getPosition();
      }
    }
    return maxPosition;
  }

//  public void process(Message message) throws IOException {
//    File shipmentFile = (File) message.getPayload();
//    boolean processingError = false;
//    ModelClass modelClass = new ModelClass(ShippedLineItem.class, true);
//    try (FileInputStream inputStream = new FileInputStream(shipmentFile)) {
//
//      Date currentTimestamp = dbService.getCurrentTimestamp();
//      csvParser.process(inputStream, modelClass, shipmentRecordHandler, new AuditFields(currentTimestamp));
//
//      logger.debug("Successfully processed file " + shipmentFile.getName());
//      sendArchiveToFtp(shipmentFile);
//
//    } catch (DataException | UploadException e) {
//      logger.warn("Error processing file " + shipmentFile.getName() + " with error " + e.getMessage());
//      processingError = true;
//    } finally {
//      shipmentFilePostProcessHandler.process(shipmentFile, processingError);
//      logger.info("Updated order statuses for file " + shipmentFile.getName());
//      boolean deleteStatus = deleteQuietly(shipmentFile);
//      if (!deleteStatus) {
//        logger.error("Unable to delete temporary shipment file " + shipmentFile.getName());
//      }
//    }
//  }

//  private void sendArchiveToFtp(File file) {
//    Message<File> message = withPayload(file).build();
//    ftpArchiveOutputChannel.send(message);
//    logger.debug("Shipment file " + file.getName() + " archived to FTP");
//  }

}
