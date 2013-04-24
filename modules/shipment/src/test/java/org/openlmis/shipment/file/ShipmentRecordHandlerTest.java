/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.service.ShipmentService;
import org.openlmis.upload.model.AuditFields;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShipmentRecordHandlerTest {
  @Mock
  private ShipmentService shipmentService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();;

  @InjectMocks
  private ShipmentRecordHandler shipmentRecordHandler;

  @Test
  public void shouldInsert() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    Date currentTimestamp = new Date();
    shipmentRecordHandler.execute(shippedLineItem, 1, new AuditFields(currentTimestamp));
    assertThat(shippedLineItem.getModifiedDate(), is(currentTimestamp));
    verify(shipmentService).insertShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldThrowExceptionIfOrderIdIsAlreadyProcessed() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, -1);
    Date date = new Date(calendar.getTimeInMillis());

    AuditFields auditFields = new AuditFields();
    auditFields.setCurrentTimestamp(new Date());

    ShippedLineItem shippedLineItemFromDB = new ShippedLineItem();
    shippedLineItemFromDB.setModifiedDate(date);

    when(shipmentService.getShippedLineItem(shippedLineItem)).thenReturn(shippedLineItemFromDB);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("Order Number Already Processed");

    shipmentRecordHandler.execute(shippedLineItem,1,auditFields);
  }

  @Test
  public void shouldUpdateShippedLineItemIfOrderIdPresentWithSameTimeStamp() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    Date currentTimestamp = new Date();
    AuditFields auditFields = new AuditFields(currentTimestamp);

    int shippedLineItemFromDbId = 1;
    ShippedLineItem shippedLineItemFromDB = new ShippedLineItem();
    shippedLineItemFromDB.setModifiedDate(currentTimestamp);
    shippedLineItemFromDB.setId(shippedLineItemFromDbId);

    when(shipmentService.getShippedLineItem(shippedLineItem)).thenReturn(shippedLineItemFromDB);

    shipmentRecordHandler.execute(shippedLineItem,1,auditFields);

    assertThat(shippedLineItem.getModifiedDate(), is(currentTimestamp));
    assertThat(shippedLineItem.getId(), is(shippedLineItemFromDbId));
    verify(shipmentService).updateShippedLineItem(shippedLineItem);
  }
}
