/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.distribution.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.distribution.domain.DeliveryZone;

import static com.natpryce.makeiteasy.Property.newProperty;

public class DeliveryZoneBuilder {

  private static Property<DeliveryZone, String> code = newProperty();
  private static Property<DeliveryZone, String> name = newProperty();
  private static Property<DeliveryZone, Long> modifiedBy = newProperty();
  private static Property<DeliveryZone,Long> createdBy = newProperty();

  public static final String DEFAULT_CODE = "defaultCode";
  public static final String DEFAULT_NAME = "default name";
  public static final long MODIFIED_BY = 1l;
  public static final long CREATED_BY = 1l;

  public static final Instantiator<DeliveryZone> defaultDeliveryZone = new Instantiator<DeliveryZone>() {

    @Override
    public DeliveryZone instantiate(PropertyLookup<DeliveryZone> lookup) {
      DeliveryZone deliveryZone = new DeliveryZone();
      deliveryZone.setId(1l);
      deliveryZone.setCode(lookup.valueOf(code, DEFAULT_CODE));
      deliveryZone.setName(lookup.valueOf(name, DEFAULT_NAME));
      deliveryZone.setDescription("description");
      deliveryZone.setCreatedBy(lookup.valueOf(createdBy, CREATED_BY));
      deliveryZone.setModifiedBy(lookup.valueOf(modifiedBy, MODIFIED_BY));

      return deliveryZone;
    }
  };
}
