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
  MANAGE_POD("right.fulfillment.manage.pod", FULFILLMENT, "Permission to manage proof of delivery", 20),

  DELETE_REQUISITION("Delete Requisition", REQUISITION, "Permission to delete requisitions", 21),
  
  ACCESS_ILS_GATEWAY("Access ILS Gateway",ADMIN,"Permission to access the ILS Gateway",23),
  MANAGE_PRODUCT_ALLOWED_FOR_FACILITY("Admin - Manage Products Allowed for Facilities", ADMIN, "Permission to manage allowed products for facilities",24),
  MANAGE_SETTING("Admin - Configure Settings", ADMIN, "Permission to configure settings.",25),
  MANAGE_REQ_GRP_PROG_SCHEDULE("Admin - Manage Requisition Group Program Schedule", ADMIN, "Permission to manage requisition groups programs schedule", 26),
  MANAGE_SUPERVISORY_NODE("Admin - Manage Supervisory Nodes",ADMIN,"Permission to manage supervisory nodes",27),
  MANAGE_REQUISITION_GROUP("Admin - Manage Requisition Groups", ADMIN, "Permission to manage requisition groups",28),
  MANAGE_PRODUCT("Admin - Manage Products", ADMIN, "Permission to manage products",29),
  MANAGE_SUPPLYLINE("Admin - Manage Supply Line", ADMIN, "Permission to manage supply line",30),
  MANAGE_GEOGRAPHIC_ZONES ("Admin - Manage Geographic Zones", ADMIN, "Permission to manage geographic zones.",31),

  MANAGE_EQUIPMENT_SETTINGS ("Admin - Manage Equipment Settings", ADMIN, "Permission to manage equipment settings.",60),
  MANAGE_EQUIPMENT_INVENTORY ("Admin - Manage Equipment Inventory", ADMIN, "Permission to manage equipment inventory.",61),
  SERVICE_VENDOR_RIGHT("Admin - Service Vendor Rights", ADMIN, "Permission to use system as equipment Service Vendor", 62),


  VIEW_FACILITY_REPORT("Report - Facility Listing (V1)", REPORT, "Permission to view Facility List Report",32),
  VIEW_MAILING_LABEL_REPORT("Report - Facility Listing (V2)", REPORT, "Permission to view mailing labels for facilities",33),
  VIEW_SUMMARY_REPORT("Report - Summary Report", REPORT, "Permission to view Summary Report",34),
  VIEW_CONSUMPTION_REPORT("Report - Consumption Report", REPORT, "Permission to view consumption report",35),
  VIEW_AVERAGE_CONSUMPTION_REPORT("Report - Average Consumption Report", REPORT, "Permission to view average consumption report",36),
  VIEW_REPORTING_RATE_REPORT("Report - Reporting Rate Report", REPORT, "Permission to view reporting rate report",37),
  VIEW_NON_REPORTING_FACILITIES("Report - Non Reporting Facility Report", REPORT, "Permission to view Non reporting facilities report",38),
  VIEW_ADJUSTMENT_SUMMARY_REPORT("Report - Adjustment Summary Report", REPORT, "Permission to view adjustment summary Report",39),
  VIEW_SUPPLY_STATUS_REPORT("Report - Supply Status by Facility",REPORT, "Permission to view Supply Status by Facility Report",40),
  VIEW_STOCKED_OUT_REPORT("Report - Stocked Out Report", REPORT, "Permission to view stocked out product report",41),
  VIEW_DISTRICT_CONSUMPTION_REPORT("Report - District Consumption Comparison", REPORT, "Permission to view district consumption comparison report",42),
  VIEW_ORDER_REPORT("Report - Order Report", REPORT, "Permission to view Order Report",45),
  VIEW_STOCK_IMBALANCE_REPORT("Report - Stock Imbalance Report", REPORT, "Permission to view Stock Imbalance Report",46),
  VIEW_RNR_FEEDBACK_REPORT("Report - Report and Requisition Feedback", REPORT, "Permission to view Report and Requisition Feedback Report",47),
  VIEW_ORDER_FILL_RATE_REPORT("Report - Order Fill Rate Report", REPORT, "Permission to view Order Fill Rate Report",48),
  VIEW_REGIMEN_SUMMARY_REPORT("Report - Regimen Summary Report", REPORT, "Permission to view Regimen Summary Report",49),
  VIEW_DISTRICT_FINANCIAL_SUMMARY_REPORT("Report - District Financial Summary Report", REPORT, "Permission to view District Financial Summary Report",50),
  VIEW_DASHBOARD_POC("Report - View Dashboard POC", REPORT, "View Dashboard POC",51),
  VIEW_USER_SUMMARY_REPORT("Report - View User Summary Report", REPORT, "Permission to view user summary Report",52);




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
