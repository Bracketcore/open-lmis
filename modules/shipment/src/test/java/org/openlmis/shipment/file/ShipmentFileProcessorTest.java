/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.shipment.file;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.Shipment;
import org.openlmis.shipment.file.csv.handler.ShipmentFilePostProcessHandler;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.parser.CSVParser;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.integration.Message;

import java.io.File;
import java.io.FileInputStream;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ShipmentFileProcessor.class)
public class ShipmentFileProcessorTest {
  @Mock
  private CSVParser csvParser;
  @Mock
  private RecordHandler shipmentRecordHandler;
  @Mock
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;
  @InjectMocks
  private ShipmentFileProcessor shipmentFileProcessor;

  @Test
  public void shouldProcessCsvFileFromMessage() throws Exception {
    Message message = mock(Message.class);
    File shipmentFile = mock(File.class);
    FileInputStream shipmentInputStream = mock(FileInputStream.class);
    ModelClass shipmentModelClass = new ModelClass(Shipment.class, true);

    when(message.getPayload()).thenReturn(shipmentFile);
    whenNew(FileInputStream.class).withArguments(shipmentFile).thenReturn(shipmentInputStream);
    whenNew(ModelClass.class).withArguments(Shipment.class, true).thenReturn(shipmentModelClass);

    shipmentFileProcessor.process(message);

    verify(csvParser).process(shipmentInputStream, shipmentModelClass, shipmentRecordHandler);
    verify(shipmentFilePostProcessHandler).process(shipmentFile, false);
  }

  @Test
  public void shouldProcessInvalidFileWhenUploadExceptionIsEncountered() throws Exception {
    Message message = mock(Message.class);
    File shipmentFile = mock(File.class);
    FileInputStream shipmentInputStream = mock(FileInputStream.class);
    ModelClass shipmentModelClass = new ModelClass(Shipment.class, true);

    when(message.getPayload()).thenReturn(shipmentFile);
    whenNew(FileInputStream.class).withArguments(shipmentFile).thenReturn(shipmentInputStream);
    whenNew(ModelClass.class).withArguments(Shipment.class, true).thenReturn(shipmentModelClass);
    doThrow(new UploadException("message")).when(csvParser).
        process(shipmentInputStream, shipmentModelClass, shipmentRecordHandler);

    shipmentFileProcessor.process(message);

    verify(shipmentFilePostProcessHandler).process(shipmentFile, true);
  }

  @Test
  public void shouldProcessInvalidFileWhenDataExceptionIsEncountered() throws Exception {
    Message message = mock(Message.class);
    File shipmentFile = mock(File.class);
    FileInputStream shipmentInputStream = mock(FileInputStream.class);
    ModelClass shipmentModelClass = new ModelClass(Shipment.class, true);

    when(message.getPayload()).thenReturn(shipmentFile);
    whenNew(FileInputStream.class).withArguments(shipmentFile).thenReturn(shipmentInputStream);
    whenNew(ModelClass.class).withArguments(Shipment.class, true).thenReturn(shipmentModelClass);
    doThrow(new DataException("message")).when(csvParser).
        process(shipmentInputStream, shipmentModelClass, shipmentRecordHandler);

    shipmentFileProcessor.process(message);

    verify(shipmentFilePostProcessHandler).process(shipmentFile, true);
  }
}
