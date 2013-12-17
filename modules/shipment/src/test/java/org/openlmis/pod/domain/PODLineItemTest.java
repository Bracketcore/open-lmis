/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.pod.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;

public class PODLineItemTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowExceptionIfProductCodeIsEmpty(){
    PODLineItem podLineItem = new PODLineItem(1l, null, 100);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    podLineItem.validate();
  }

  @Test
  public void shouldThrowExceptionIfQuantityReceivedIsEmpty(){
    PODLineItem podLineItem = new PODLineItem(1l, "P100", null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    podLineItem.validate();
  }

  @Test
  public void shouldThrowExceptionIfQuantityReceivedIsNegative(){
    PODLineItem podLineItem = new PODLineItem(1l, "P100", -100);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.invalid.received.quantity");

    podLineItem.validate();
  }
}
