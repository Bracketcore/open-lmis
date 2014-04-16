/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import lombok.Getter;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.serializer.RightDeSerializer;
import org.openlmis.core.serializer.RightSerializer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.asList;
import static org.openlmis.core.domain.RightType.*;

/**
 * Right represents the rights available in the system along with their type.
 */
@JsonSerialize(using = RightSerializer.class)
@JsonDeserialize(using = RightDeSerializer.class)
public enum Right {

  CONFIGURE_RNR("right.configure.rnr", ADMIN, "Permission to create and edit r&r template for any program", 1),
  MANAGE_FACILITY("right.manage.facility", ADMIN, "Permission to manage facility(crud)", 2),
  MANAGE_ROLE("right.manage.role", ADMIN, "Permission to create and edit roles in the system", 5),
  MANAGE_SCHEDULE("right.manage.schedule", ADMIN, "Permission to create and edit schedules in the system", 6),
  MANAGE_USER("right.manage.user", ADMIN, "Permission to manage users(crud)", 7),
  UPLOADS("right.upload", ADMIN, "Permission to upload", 8),
  VIEW_REPORT("right.view.report", ADMIN, "Permission to view reports", 11),
  MANAGE_REPORT("right.manage.report", ADMIN, "Permission to manage reports", 10, VIEW_REPORT),
  VIEW_REQUISITION("right.view.requisition", REQUISITION, "Permission to view requisitions", 16),
  CREATE_REQUISITION("right.create.requisition", REQUISITION, "Permission to create, edit, submit and recall requisitions", 15, VIEW_REQUISITION),
  AUTHORIZE_REQUISITION("right.authorize.requisition", REQUISITION, "Permission to edit, authorize and recall requisitions", 13, VIEW_REQUISITION),
  APPROVE_REQUISITION("right.approve.requisition", REQUISITION, "Permission to approve requisitions", 12, VIEW_REQUISITION),
  CONVERT_TO_ORDER("right.convert.to.order", FULFILLMENT, "Permission to convert requisitions to order", 14),
  VIEW_ORDER("right.view.order", FULFILLMENT, "Permission to view orders", 17),
  MANAGE_PROGRAM_PRODUCT("right.manage.program.product", ADMIN, "Permission to manage program products", 3),
  MANAGE_DISTRIBUTION("right.manage.distribution", ALLOCATION, "Permission to manage an distribution", 9),
  SYSTEM_SETTINGS("right.system.settings", ADMIN, "Permission to configure Electronic Data Interchange (EDI)", 18),
  MANAGE_REGIMEN_TEMPLATE("right.manage.regimen.template", ADMIN, "Permission to manage System Settings", 4),
  FACILITY_FILL_SHIPMENT("right.fulfillment.fill.shipment", FULFILLMENT, "Permission to fill shipment data for facility", 19),
  MANAGE_POD("right.fulfillment.manage.pod", FULFILLMENT, "Permission to manage proof of delivery", 20);


  @Getter
  private final String rightName;

  @Getter
  private RightType type;

  @Getter
  private final String description;

  @Getter
  private List<Right> defaultRights;

  @Getter
  private final Integer displayOrder;

  private Right(String rightName, RightType type, String description, Integer displayOrder) {
    this(rightName, type, description, displayOrder, new Right[0]);
  }

  private Right(String rightName, RightType type, String description, Integer displayOrder, Right... rights) {
    this.rightName = rightName;
    this.type = type;
    this.description = description;
    this.displayOrder = displayOrder;
    this.defaultRights = asList(rights);
  }

  public static String commaSeparateRightNames(Right... rights) {
    List<String> rightNames = new ArrayList<>();
    for (Right right : rights) {
      rightNames.add(right.name());
    }
    return rightNames.toString().replace("[", "{").replace("]", "}");
  }

  public static class RightComparator implements Comparator<Right> {
    @Override
    public int compare(Right right1, Right right2) {
      if (right1 == right2) return 0;
      if (right1 == null) {
        return 1;
      }
      if (right2 == null) {
        return -1;
      }
      return right1.getDisplayOrder().compareTo(right2.getDisplayOrder());
    }
  }
}
