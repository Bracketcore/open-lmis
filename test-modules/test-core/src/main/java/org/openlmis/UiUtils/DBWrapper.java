/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.UiUtils;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.lang.System.getProperty;

public class DBWrapper {

  public static final int DEFAULT_MAX_MONTH_OF_STOCK = 3;
  public static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/open_lmis";
  public static final String DEFAULT_DB_USERNAME = "postgres";
  public static final String DEFAULT_DB_PASSWORD = "p@ssw0rd";
  Connection connection;

  public DBWrapper() throws IOException, SQLException {
    String dbUser = getProperty("dbUser", DEFAULT_DB_USERNAME);
    String dbPassword = getProperty("dbPassword", DEFAULT_DB_PASSWORD);
    String dbUrl = getProperty("dbUrl", DEFAULT_DB_URL);

    loadDriver();

    connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
  }

  public void closeConnection() throws SQLException {
    if (connection != null) connection.close();
  }

  private void loadDriver() {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      System.exit(1);

    }
  }

  private void update(String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(sql);
    }
  }

  private void update(String sql, Object... params) throws SQLException {
    update(format(sql, params));
  }

  private ResultSet query(String sql) throws SQLException {
    return connection.createStatement().executeQuery(sql);
  }

  private List<Map<String, String>> select(String sql, Object... params) throws SQLException {
    ResultSet rs = query(sql, params);
    ResultSetMetaData md = rs.getMetaData();
    int columns = md.getColumnCount();
    List<Map<String, String>> list = new ArrayList<>();
    while (rs.next()) {
      Map<String, String> row = new HashMap<>();
      for (int i = 1; i <= columns; ++i) {
        row.put(md.getColumnName(i), rs.getString(i));
      }
      list.add(row);
    }
    return list;
  }

  private ResultSet query(String sql, Object... params) throws SQLException {
    return query(format(sql, params));
  }

  public void insertUser(String userId, String userName, String password, String facilityCode, String email)
    throws SQLException, IOException {

    update("delete from users where userName like('%s')", userName);

    update("INSERT INTO users(id, userName, password, facilityId, firstName, lastName, email, active, verified) " +
      "VALUES ('%s', '%s', '%s', (SELECT id FROM facilities WHERE code = '%s'), 'Fatima', 'Doe', '%s','true','true')",
      userId, userName, password, facilityCode, email);
  }

  public void insertPeriodAndAssociateItWithSchedule(String period, String schedule) throws SQLException, IOException {
    insertProcessingPeriod(period, period, "2013-09-29", "2020-09-30", 66, schedule);
  }

  public void deleteProcessingPeriods() throws SQLException, IOException {
    update("delete from processing_periods");
  }

  public List<String> getProductDetailsForProgram(String programCode) throws SQLException {
    List<String> prodDetails = new ArrayList<>();

    ResultSet rs = query("select programs.code as programCode, programs.name as programName, " +
      "products.code as productCode, products.primaryName as productName, products.description as desc, " +
      "products.dosesPerDispensingUnit as unit, PG.name as pgName " +
      "from products, programs, program_products PP, product_categories PG " +
      "where programs.id = PP.programId and PP.productId=products.id and " +
      "PG.id = products.categoryId and programs.code='" + programCode + "' " +
      "and products.active='true' and PP.active='true'");

    while (rs.next()) {
      String programName = rs.getString(2);
      String productCode = rs.getString(3);
      String productName = rs.getString(4);
      String desc = rs.getString(5);
      String unit = rs.getString(6);
      String pcName = rs.getString(7);
      prodDetails.add(programName + "," + productCode + "," + productName + "," + desc + "," + unit + "," + pcName);
    }
    return prodDetails;
  }

  public List<String> getProductDetailsForProgramAndFacilityType(String programCode, String facilityCode) throws SQLException {

    List<String> prodDetails = new ArrayList<>();

    ResultSet rs = query("select prog.code as programCode, prog.name as programName, prod.code as productCode, " +
      "prod.primaryName as productName, prod.description as desc, prod.dosesPerDispensingUnit as unit, " +
      "pg.name as pgName from products prod, programs prog, program_products pp, product_categories pg, " +
      "facility_approved_products fap, facility_types ft where prog.id=pp.programId and pp.productId=prod.id and " +
      "pg.id = prod.categoryId and fap. programProductId = pp.id and ft.id=fap.facilityTypeId and prog.code='" +
      programCode + "' and ft.code='" + facilityCode + "' " + "and prod.active='true' and pp.active='true'");

    while (rs.next()) {
      String programName = rs.getString(2);
      String productCode = rs.getString(3);
      String productName = rs.getString(4);
      String desc = rs.getString(5);
      String unit = rs.getString(6);
      String pgName = rs.getString(7);
      prodDetails.add(programName + "," + productCode + "," + productName + "," + desc + "," + unit + "," + pgName);
    }
    return prodDetails;
  }

  public void updateActiveStatusOfProduct(String productCode, String active) throws SQLException {
    update("update products set active='%s' where code='%s'", active, productCode);
  }

  public int getRecordCountInTable(String tableName) throws SQLException {
    ResultSet rs = query("select count(*) from %s", tableName);

    if (rs.next()) {
      return rs.getInt("count");
    }
    return -1;
  }

  public void updateActiveStatusOfProgramProduct(String productCode, String programCode, String active) throws SQLException {
    update("update program_products set active='%s' WHERE" +
      " programId = (select id from programs where code='%s')  AND" +
      " productId = (select id from products where code='%s')", active, programCode, productCode);
  }

  public List<String> getFacilityCodeNameForDeliveryZoneAndProgram(String deliveryZoneName, String program, boolean active) throws SQLException {
    List<String> codeName = new ArrayList<>();
    ResultSet rs = query("select f.code, f.name from facilities f, programs p, programs_supported ps, delivery_zone_members dzm, delivery_zones dz where " +
      "dzm.DeliveryZoneId=dz.id and " +
      "f.active='" + active + "' and " +
      "p.id= ps.programId and " +
      "p.code='" + program + "' and " +
      "dz.id = dzm.DeliveryZoneId and " +
      "dz.name='" + deliveryZoneName + "' and " +
      "dzm.facilityId = f.id and " +
      "ps.facilityId = f.id;");

    while (rs.next()) {
      String code = rs.getString("code");
      String name = rs.getString("name");
      codeName.add(code + " - " + name);
    }
    return codeName;
  }

  public void updateVirtualPropertyOfFacility(String facilityCode, String flag) throws SQLException, IOException {
    update("UPDATE facilities SET virtualFacility='%s' WHERE code='%s'", flag, facilityCode);
  }

  public void deleteDeliveryZoneMembers(String facilityCode) throws SQLException, IOException {
    update("delete from delivery_zone_members where facilityId in (select id from facilities where code ='%s')", facilityCode);
  }

  public String getVirtualPropertyOfFacility(String facilityCode) throws SQLException, IOException {
    String flag = "";
    ResultSet rs = query("select virtualFacility from facilities where code = '%s'", facilityCode);

    if (rs.next()) {
      flag = rs.getString("virtualFacility");
    }
    return flag;
  }

  public String getActivePropertyOfFacility(String facilityCode) throws SQLException, IOException {
    String flag = "";
    ResultSet rs = query("select active from facilities where code='%s'", facilityCode);

    if (rs.next()) {
      flag = rs.getString("active");
    }
    return flag;
  }

  public void updateUser(String password, String email) throws SQLException, IOException {
    update("DELETE FROM user_password_reset_tokens");
    update("update users set password = '%s', active = TRUE, verified = TRUE  where email = '%s'", password, email);
  }

  public void updateRestrictLogin(String userName, boolean status) throws SQLException, IOException {
    update("update users set restrictLogin = '%s' where userName = '%s'", status, userName);
  }

  public String getRestrictLogin(String userName) throws SQLException, IOException {
    String status = null;
    ResultSet rs = query("select restrictLogin from users where userName='%s'", userName);

    if (rs.next()) {
      status = rs.getString("restrictLogin");
    }
    return status;
  }

  public void insertRequisitions(int numberOfRequisitions, String program, boolean withSupplyLine) throws SQLException, IOException {
    int numberOfRequisitionsAlreadyPresent = 0;
    boolean flag = true;
    ResultSet rs = query("select count(*) from requisitions");
    if (rs.next()) {
      numberOfRequisitionsAlreadyPresent = Integer.parseInt(rs.getString(1));
    }

    for (int i = numberOfRequisitionsAlreadyPresent + 1; i <= numberOfRequisitions + numberOfRequisitionsAlreadyPresent; i++) {
      insertProcessingPeriod("PeriodName" + i, "PeriodDesc" + i, "2012-12-01", "2015-12-01", 1, "M");
      update("insert into requisitions (facilityId, programId, periodId, status, emergency, " +
        "fullSupplyItemsSubmittedCost, nonFullSupplyItemsSubmittedCost, supervisoryNodeId) " +
        "values ((Select id from facilities where code='F10'),(Select id from programs where code='" + program + "')," +
        "(Select id from processing_periods where name='PeriodName" + i + "'), 'APPROVED', 'false', 50.0000, 0.0000, " +
        "(select id from supervisory_nodes where code='N1'))");

      update("INSERT INTO requisition_line_items " +
        "(rnrId, productCode,product,productDisplayOrder,productCategory,productCategoryDisplayOrder, beginningBalance, quantityReceived, quantityDispensed, stockInHand, " +
        "dispensingUnit, maxMonthsOfStock, dosesPerMonth, dosesPerDispensingUnit, packSize,fullSupply,totalLossesAndAdjustments,newPatientCount,stockOutDays,price,roundToZero,packRoundingThreshold) VALUES" +
        "((SELECT max(id) FROM requisitions), 'P10','antibiotic Capsule 300/200/600 mg',1,'Antibiotics',1, '0', '11' , '1', '10' ,'Strip','3', '30', '10', '10','t',0,0,0,12.5000,'f',1);");
    }
    if (withSupplyLine) {
      ResultSet rs1 = query("select * from supply_lines where supervisoryNodeId = " +
        "(select id from supervisory_nodes where code = 'N1') and programId = " +
        "(select id from programs where code='" + program + "') and supplyingFacilityId = " +
        "(select id from facilities where code = 'F10')");
      if (rs1.next()) {
        flag = false;
      }
    }
    if (withSupplyLine) {
      if (flag) {
        insertSupplyLines("N1", program, "F10", true);
      }
    }

  }

  public void insertFulfilmentRoleAssignment(String userName, String roleName, String facilityCode) throws SQLException {
    update("insert into fulfillment_role_assignments(userId, roleId, facilityId) values " +
      "((select id from users where username='" + userName + "')," +
      "(select id from roles where name='" + roleName + "'),(select id from facilities where code='" + facilityCode + "'))");

  }

  public String getDeliveryZoneNameAssignedToUser(String user) throws SQLException, IOException {
    String deliveryZoneName = "";
    ResultSet rs = query("select name from delivery_zones where id in(select deliveryZoneId from role_assignments where " +
      "userId=(select id from users where username='" + user + "'))");

    if (rs.next()) {
      deliveryZoneName = rs.getString("name");
    }
    return deliveryZoneName;
  }

  public String getRoleNameAssignedToUser(String user) throws SQLException, IOException {
    String userName = "";
    ResultSet rs = query("select name from roles where id in(select roleId from role_assignments where " +
      "userId=(select id from users where username='" + user + "'))");

    if (rs.next()) {
      userName = rs.getString("name");
    }
    return userName;
  }

  public void insertFacilities(String facility1, String facility2) throws IOException, SQLException {
    update("INSERT INTO facilities\n" +
      "(code, name, description, gln, mainPhone, fax, address1, address2, geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicSCC, hasElectronicDAR, active, goLiveDate, goDownDate, satellite, comment, enabled, virtualFacility) values\n" +
      "('" + facility1 + "','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',5,2,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE', 'FALSE'),\n" +
      "('" + facility2 + "','Central Hospital','IT department','G7646',9876234981,'fax','A','B',5,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE', 'FALSE');\n");

    update("insert into programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES" +
      " ((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 1, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 2, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 5, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 1, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 5, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 2, '11/11/12', true, 1)");

  }

  public void insertVirtualFacility(String facilityCode, String parentFacilityCode) throws IOException, SQLException {
    update("INSERT INTO facilities\n" +
      "(code, name, description, gln, mainPhone, fax, address1, address2, geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicSCC, hasElectronicDAR, active, goLiveDate, goDownDate, satellite, comment, enabled, virtualFacility,parentFacilityId) values\n" +
      "('" + facilityCode + "','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',5,2,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE', 'TRUE',(SELECT id FROM facilities WHERE code = '" + parentFacilityCode + "'))");

    update("insert into programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES" +
      " ((SELECT id FROM facilities WHERE code = '" + facilityCode + "'), 1, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facilityCode + "'), 2, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facilityCode + "'), 5, '11/11/12', true, 1)");

    update("insert into requisition_group_members (requisitionGroupId, facilityId, createdDate, modifiedDate) values " +
      "((select requisitionGroupId from requisition_group_members where facilityId=(SELECT id FROM facilities WHERE code = '" + parentFacilityCode + "'))," +
      "(SELECT id FROM facilities WHERE code = '" + facilityCode + "'),NOW(),NOW())");

  }

  public void deletePeriod(String periodName) throws IOException, SQLException {
    update("delete from processing_periods where name='" + periodName + "';");
  }

  public void insertFacilitiesWithDifferentGeoZones(String facility1, String facility2, String geoZone1, String geoZone2) throws IOException, SQLException {
    update("INSERT INTO facilities\n" +
      "(code, name, description, gln, mainPhone, fax, address1, address2, geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicSCC, hasElectronicDAR, active, goLiveDate, goDownDate, satellite, comment, enabled, virtualFacility) values\n" +
      "('" + facility1 + "','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',(select id from geographic_zones where code='" + geoZone1 + "'),2,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE', 'FALSE'),\n" +
      "('" + facility2 + "','Central Hospital','IT department','G7646',9876234981,'fax','A','B',(select id from geographic_zones where code='" + geoZone2 + "'),2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE', 'FALSE');\n");

    update("insert into programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 1, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 2, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 5, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 1, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 5, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 2, '11/11/12', true, 1);");

  }

  public void insertGeographicZone(String code, String name, String parentName) throws IOException, SQLException {
    update("insert into geographic_zones (code, name, levelId, parentId) " +
      "values ('%s','%s',(select max(levelId) from geographic_zones)," +
      "(select id from geographic_zones where code='%s'))", code, name, parentName);
  }

  public void allocateFacilityToUser(String userId, String facilityCode) throws IOException, SQLException {
    update("update users set facilityId = (Select id from facilities where code = '%s') where id = '%s'", facilityCode, userId);

  }

  public void updateSourceOfAProgramTemplate(String program, String label, String source) throws IOException, SQLException {
    update("update program_rnr_columns set source = '%s'" +
      " where programId = (select id from programs where code = '%s') and label = '%s'", source, program, label);
  }

  public void deleteData() throws SQLException, IOException {
    update("delete from budget_line_items");
    update("delete from budget_file_info");
    update("delete from role_rights where roleId not in(1)");
    update("delete from role_assignments where userId not in (1)");
    update("delete from fulfillment_role_assignments");
    update("delete from roles where name not in ('Admin')");
    update("delete from facility_approved_products");
    update("delete from program_product_price_history");
    update("delete from pod_line_items");
    update("delete from pod");
    update("delete from orders");
    update("DELETE FROM requisition_status_changes");

    update("delete from user_password_reset_tokens");
    update("delete from comments");
    update("delete from facility_visits");
    update("delete from epi_use_line_items");
    update("delete from epi_use");
    update("delete from refrigerator_problems");
    update("delete from refrigerator_readings");
    update("delete from distribution_refrigerators");

    update("delete from distributions");
    update("delete from users where userName not like('Admin%')");
    update("DELETE FROM requisition_line_item_losses_adjustments");
    update("DELETE FROM requisition_line_items");
    update("DELETE FROM regimen_line_items");
    update("DELETE FROM requisitions");

    update("delete from program_product_isa");
    update("delete from facility_approved_products");
    update("delete from facility_program_products");
    update("delete from program_products");
    update("delete from products");
    update("delete from product_categories");
    update("delete from product_groups");

    update("delete from supply_lines");
    update("delete from programs_supported");
    update("delete from requisition_group_members");
    update("delete from program_rnr_columns");
    update("delete from requisition_group_program_schedules");
    update("delete from requisition_groups");
    update("delete from requisition_group_members");
    update("delete from delivery_zone_program_schedules");
    update("delete from delivery_zone_warehouses");
    update("delete from delivery_zone_members");
    update("delete from role_assignments where deliveryZoneId in (select id from delivery_zones where code in('DZ1','DZ2'))");
    update("delete from delivery_zones");

    update("delete from supervisory_nodes");
    update("delete from refrigerators");
    update("delete from facility_ftp_details");
    update("delete from facilities");
    update("delete from geographic_zones where code not in ('Root','Arusha','Dodoma', 'Ngorongoro')");
    update("delete from processing_periods");
    update("delete from processing_schedules");
    update("delete from atomfeed.event_records");
    update("delete from regimens");
    update("delete from program_regimen_columns");

  }

  public void insertRole(String role, String description) throws SQLException, IOException {
    ResultSet rs = query("Select id from roles where name='%s'", role);

    if (!rs.next()) {
      update("INSERT INTO roles(name, description) VALUES('%s', '%s')", role, description);
    }
  }

  public void insertSupervisoryNode(String facilityCode, String supervisoryNodeCode, String supervisoryNodeName, String supervisoryNodeParentCode) throws SQLException, IOException {
    update("delete from supervisory_nodes");
    update("INSERT INTO supervisory_nodes (parentId, facilityId, name, code) " +
      "VALUES (%s, (SELECT id FROM facilities WHERE code = '%s'), '%s', '%s')",
      supervisoryNodeParentCode, facilityCode, supervisoryNodeName, supervisoryNodeCode);
  }

  public void insertSupervisoryNodeSecond(String facilityCode,
                                          String supervisoryNodeCode,
                                          String supervisoryNodeName,
                                          String supervisoryNodeParentCode) throws SQLException, IOException {
    update("INSERT INTO supervisory_nodes" +
      "  (parentId, facilityId, name, code) VALUES" +
      "  ((select id from  supervisory_nodes where code ='%s'), (SELECT id FROM facilities WHERE code = '%s'), '%s', '%s')"
      , supervisoryNodeParentCode, facilityCode, supervisoryNodeName, supervisoryNodeCode);
  }

  public void insertRequisitionGroups(String code1, String code2, String supervisoryNodeCode1, String supervisoryNodeCode2) throws SQLException, IOException {
    ResultSet rs = query("Select id from requisition_groups;");

    if (rs.next()) {
      update("delete from requisition_groups;");
    }
    update("INSERT INTO requisition_groups ( code ,name,description,supervisoryNodeId )values\n" +
      "('" + code2 + "','Requistion Group 2','Supports EM(Q1M)',(select id from  supervisory_nodes where code ='" + supervisoryNodeCode2 + "')),\n" +
      "('" + code1 + "','Requistion Group 1','Supports EM(Q2M)',(select id from  supervisory_nodes where code ='" + supervisoryNodeCode1 + "'))");
  }

  public void insertRequisitionGroupMembers(String RG1facility, String RG2facility) throws SQLException, IOException {
    ResultSet rs = query("Select requisitionGroupId from requisition_group_members;");

    if (rs.next()) {
      update("delete from requisition_group_members;");

    }
    update("INSERT INTO requisition_group_members ( requisitionGroupId ,facilityId )values\n" +
      "((select id from  requisition_groups where code ='RG1'),(select id from  facilities where code ='" + RG1facility + "')),\n" +
      "((select id from  requisition_groups where code ='RG2'),(select id from  facilities where code ='" + RG2facility + "'));");
  }

  public void insertRequisitionGroupProgramSchedule() throws SQLException, IOException {
    ResultSet rs = query("Select requisitionGroupId from requisition_group_members;");

    if (rs.next()) {
      update("delete from requisition_group_program_schedules;");
    }
    update("insert into requisition_group_program_schedules ( requisitionGroupId , programId , scheduleId , directDelivery ) values\n" +
      "((select id from requisition_groups where code='RG1'),(select id from programs where code='ESS_MEDS'),(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
      "((select id from requisition_groups where code='RG1'),(select id from programs where code='MALARIA'),(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
      "((select id from requisition_groups where code='RG1'),(select id from programs where code='HIV'),(select id from processing_schedules where code='M'),TRUE),\n" +
      "((select id from requisition_groups where code='RG2'),(select id from programs where code='ESS_MEDS'),(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
      "((select id from requisition_groups where code='RG2'),(select id from programs where code='MALARIA'),(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
      "((select id from requisition_groups where code='RG2'),(select id from programs where code='HIV'),(select id from processing_schedules where code='M'),TRUE);\n");
  }

  //TODO
  public void insertRoleAssignment(String userID, String roleName) throws SQLException, IOException {
    update("delete from role_assignments where userId='" + userID + "';");

    update(" INSERT INTO role_assignments\n" +
      "            (userId, roleId, programId, supervisoryNodeId) VALUES \n" +
      "    ('" + userID + "', (SELECT id FROM roles WHERE name = '" + roleName + "'), 1, null),\n" +
      "    ('" + userID + "', (SELECT id FROM roles WHERE name = '" + roleName + "'), 1, (SELECT id from supervisory_nodes WHERE code = 'N1'));");
  }

  //TODO
  public void insertRoleAssignmentForSupervisoryNodeForProgramId1(String userID, String roleName, String supervisoryNode) throws SQLException, IOException {
    update("delete from role_assignments where userId='" + userID + "';");

    update(" INSERT INTO role_assignments\n" +
      "            (userId, roleId, programId, supervisoryNodeId) VALUES \n" +
      "    ('" + userID + "', (SELECT id FROM roles WHERE name = '" + roleName + "'), 1, null),\n" +
      "    ('" + userID + "', (SELECT id FROM roles WHERE name = '" + roleName + "'), 1, (SELECT id from supervisory_nodes WHERE code = '" + supervisoryNode + "'));");
  }

  public void updateRoleGroupMember(String facilityCode) throws SQLException, IOException {
    update("update requisition_group_members set facilityId = " +
      "(select id from facilities where code ='" + facilityCode + "') where " +
      "requisitionGroupId=(select id from requisition_groups where code='RG2');");
    update("update requisition_group_members set facilityId = " +
      "(select id from facilities where code ='F11') where requisitionGroupId = " +
      "(select id from requisition_groups where code='RG1');");
  }

  public void alterUserID(String userName, String userId) throws SQLException, IOException {
    update("delete from user_password_reset_tokens;");
    update("delete from users where id='" + userId + "' ;");
    update(" update users set id='" + userId + "' where username='" + userName + "'");
  }

  public void insertProducts(String product1, String product2) throws SQLException, IOException {

    update("delete from facility_approved_products;");
    update("delete from program_products;");
    update("delete from products;");
    update("delete from product_categories;");


    update("INSERT INTO product_categories (code, name, displayOrder) values ('C1', 'Antibiotics', 1);");
    update("INSERT INTO products\n" +
      "(code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived, displayOrder, categoryId) values\n" +
      "('" + product1 + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE,    1, (Select id from product_categories where code='C1')),\n" +
      "('" + product2 + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                    FALSE,      TRUE,   5, (Select id from product_categories where code='C1'));\n");
  }

  public void deleteCategoryFromProducts() throws SQLException, IOException {
    update("UPDATE products SET categoryId = null");
  }

  public void deleteDescriptionFromProducts() throws SQLException, IOException {
    update("UPDATE products SET description = null");
  }

  public void insertProductWithCategory(String product, String productName, String category) throws SQLException, IOException {

    update("INSERT INTO product_categories (code, name, displayOrder) values ('" + category + "', '" + productName + "', 1);");
    update("INSERT INTO products\n" +
      "(code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived, displayOrder, categoryId) values\n" +
      "('" + product + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    '" + productName + "', '" + productName + "',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE,    1, (Select id from product_categories where code='C1'));\n");

  }

  public void insertProductGroup(String group) throws SQLException, IOException {
    update("INSERT INTO product_groups (code, name) values ('" + group + "', '" + group + "-Name');");
  }

  public void insertProductWithGroup(String product, String productName, String group, boolean status) throws SQLException, IOException {
    update("INSERT INTO products\n" +
      "(code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived, displayOrder, productGroupId) values\n" +
      "('" + product + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    '" + productName + "', '" + productName + "',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          " + status + ",TRUE,       TRUE,         1,                    FALSE,      TRUE,    1, (Select id from product_groups where code='" + group + "'));\n");

  }

  public void updateProgramToAPushType(String program, boolean flag) throws SQLException {
    update("update programs set push='" + flag + "' where code='" + program + "';");
  }

  public void insertProgramProducts(String product1, String product2, String program) throws SQLException, IOException {
    ResultSet rs = query("Select id from program_products;");

    if (rs.next()) {
      update("delete from facility_approved_products;");
      update("delete from program_products;");
    }

    update("INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product1 + "'), 30, 12.5, true),\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product2 + "'), 30, 12.5, true);");
  }

  public void insertProgramProduct(String product, String program, String doses, String active) throws SQLException, IOException {


    update("INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product + "'), '" + doses + "', 12.5, '" + active + "');");
  }

  public void insertProgramProductsWithCategory(String product, String program) throws SQLException, IOException {


    update("INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product + "'), 30, 12.5, true);");
  }

  public void insertProgramProductISA(String program, String product, String whoRatio, String dosesPerYear, String wastageFactor, String bufferPercentage, String minimumValue, String maximumValue, String adjustmentValue) throws SQLException, IOException {
    update("INSERT INTO program_product_isa(programProductId, whoRatio, dosesPerYear, wastageFactor, bufferPercentage, minimumValue, maximumValue, adjustmentValue) VALUES\n" +
      "((SELECT ID from program_products where programId = " +
      "(SELECT ID from programs where code='" + program + "') and productId = " +
      "(SELECT id from products WHERE code = '" + product + "')),"
      + whoRatio + "," + dosesPerYear + "," + wastageFactor + "," + bufferPercentage + "," + minimumValue + ","
      + maximumValue + "," + adjustmentValue + ");");
  }

  public void deleteFacilityApprovedProducts() throws SQLException {
    update("DELETE FROM facility_approved_products");
  }

  public void insertFacilityApprovedProduct(String productCode, String programCode, String facilityTypeCode) throws Exception {

    String facilityTypeIdQuery = format("(SELECT id FROM facility_types WHERE code = '%s')", facilityTypeCode);
    String productIdQuery = format("(SELECT id FROM products WHERE  code = '%s')", productCode);
    String programIdQuery = format("(SELECT ID from programs where code = '%s' )", programCode);
    String programProductIdQuery = format("(SELECT id FROM program_products WHERE programId = %s AND productId = %s)",
      programIdQuery, productIdQuery);

    update(format("INSERT INTO facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) VALUES (%s,%s,%d)",
      facilityTypeIdQuery, programProductIdQuery, DEFAULT_MAX_MONTH_OF_STOCK));
  }

  public String fetchNonFullSupplyData(String productCode, String facilityTypeID, String programID) throws SQLException, IOException {
    ResultSet rs = query("Select p.code, p.displayOrder, p.primaryName, pf.code as ProductForm," +
      "p.strength as Strength, du.code as DosageUnit from facility_approved_products fap, " +
      "program_products pp, products p, product_forms pf , dosage_units du where fap. facilityTypeId='" + facilityTypeID + "' " +
      "and p. fullSupply = false and p.active=true and pp.programId='" + programID + "' and p. code='" + productCode + "' " +
      "and pp.productId=p.id and fap. programProductId=pp.id and pp.active=true and pf.id=p.formId " +
      "and du.id = p.dosageUnitId order by p. displayOrder asc;");
    String nonFullSupplyValues = null;
    if (rs.next()) {
      nonFullSupplyValues = rs.getString("primaryName") + " " + rs.getString("productForm") + " " + rs.getString("strength") + " " + rs.getString("dosageUnit");
    }
    return nonFullSupplyValues;
  }

  public void insertSchedule(String scheduleCode, String scheduleName, String scheduleDesc) throws SQLException, IOException {
    update("INSERT INTO processing_schedules(code, name, description) values('" + scheduleCode + "', '" + scheduleName + "', '" + scheduleDesc + "');");
  }

  public void insertProcessingPeriod(String periodName, String periodDesc, String periodStartDate, String periodEndDate, Integer numberOfMonths, String scheduleCode) throws SQLException, IOException {
    update("INSERT INTO processing_periods\n" +
      "(name, description, startDate, endDate, numberOfMonths, scheduleId, modifiedBy) VALUES\n" +
      "('" + periodName + "', '" + periodDesc + "', '" + periodStartDate + " 00:00:00', '" + periodEndDate + " 23:59:59', " + numberOfMonths + ", (SELECT id FROM processing_schedules WHERE code = '" + scheduleCode + "'), (SELECT id FROM users LIMIT 1));");
  }

  public void configureTemplate(String program) throws SQLException, IOException {
    update("INSERT INTO program_rnr_columns\n" +
      "(masterColumnId, programId, visible, source, formulaValidationRequired, position, label) VALUES\n" +
      "(1, (select id from programs where code = '" + program + "'),  true, 'U', false,1,  'Skip'),\n" +
      "(2, (select id from programs where code = '" + program + "'),  true, 'R', false,2,  'Product Code'),\n" +
      "(3, (select id from programs where code = '" + program + "'),  true, 'R', false,3,  'Product'),\n" +
      "(4, (select id from programs where code = '" + program + "'),  true, 'R', false,4,  'Unit/Unit of Issue'),\n" +
      "(5, (select id from programs where code = '" + program + "'),  true, 'U', false,5,  'Beginning Balance'),\n" +
      "(6, (select id from programs where code = '" + program + "'),  true, 'U', false,6,  'Total Received Quantity'),\n" +
      "(7, (select id from programs where code = '" + program + "'),  true, 'C', false,7,  'Total'),\n" +
      "(8, (select id from programs where code = '" + program + "'),  true, 'U', false,8,  'Total Consumed Quantity'),\n" +
      "(9, (select id from programs where code = '" + program + "'),  true, 'U', false,9,  'Total Losses / Adjustments'),\n" +
      "(10, (select id from programs where code = '" + program + "'),  true, 'C', true,10,  'Stock on Hand'),\n" +
      "(11, (select id from programs where code = '" + program + "'),  true, 'U', false,11, 'New Patients'),\n" +
      "(12, (select id from programs where code = '" + program + "'), true, 'U', false,12, 'Total StockOut days'),\n" +
      "(13, (select id from programs where code = '" + program + "'), true, 'C', false,13, 'Adjusted Total Consumption'),\n" +
      "(14, (select id from programs where code = '" + program + "'), true, 'C', false,14, 'Average Monthly Consumption(AMC)'),\n" +
      "(15, (select id from programs where code = '" + program + "'), true, 'C', false,15, 'Maximum Stock Quantity'),\n" +
      "(16, (select id from programs where code = '" + program + "'), true, 'C', false,16, 'Calculated Order Quantity'),\n" +
      "(17, (select id from programs where code = '" + program + "'), true, 'U', false,17, 'Requested quantity'),\n" +
      "(18, (select id from programs where code = '" + program + "'), true, 'U', false,18, 'Requested quantity explanation'),\n" +
      "(19, (select id from programs where code = '" + program + "'), true, 'U', false,19, 'Approved Quantity'),\n" +
      "(20, (select id from programs where code = '" + program + "'), true, 'C', false,20, 'Packs to Ship'),\n" +
      "(21, (select id from programs where code = '" + program + "'), true, 'R', false,21, 'Price per pack'),\n" +
      "(22, (select id from programs where code = '" + program + "'), true, 'C', false,22, 'Total cost'),\n" +
      "(23, (select id from programs where code = '" + program + "'), true, 'U', false,23, 'Expiration Date'),\n" +
      "(24, (select id from programs where code = '" + program + "'), true, 'U', false,24, 'Remarks');");
  }

  public void configureTemplateForCommTrack(String program) throws SQLException, IOException {
    update("INSERT INTO program_rnr_columns\n" +
      "(masterColumnId, programId, visible, source, position, label) VALUES\n" +
      "(2, (select id from programs where code = '" + program + "'),  true, 'R', 1,  'Product Code'),\n" +
      "(3, (select id from programs where code = '" + program + "'),  true, 'R', 2,  'Product'),\n" +
      "(4, (select id from programs where code = '" + program + "'),  true, 'R', 3,  'Unit/Unit of Issue'),\n" +
      "(5, (select id from programs where code = '" + program + "'),  true, 'U', 4,  'Beginning Balance'),\n" +
      "(6, (select id from programs where code = '" + program + "'),  true, 'U', 5,  'Total Received Quantity'),\n" +
      "(7, (select id from programs where code = '" + program + "'),  true, 'C', 6,  'Total'),\n" +
      "(8, (select id from programs where code = '" + program + "'),  true, 'C', 7,  'Total Consumed Quantity'),\n" +
      "(9, (select id from programs where code = '" + program + "'),  true, 'U', 8,  'Total Losses / Adjustments'),\n" +
      "(10, (select id from programs where code = '" + program + "'),  true, 'U', 9,  'Stock on Hand'),\n" +
      "(11, (select id from programs where code = '" + program + "'),  true, 'U', 10, 'New Patients'),\n" +
      "(12, (select id from programs where code = '" + program + "'), true, 'U', 11, 'Total StockOut days'),\n" +
      "(13, (select id from programs where code = '" + program + "'), true, 'C', 12, 'Adjusted Total Consumption'),\n" +
      "(14, (select id from programs where code = '" + program + "'), true, 'C', 13, 'Average Monthly Consumption(AMC)'),\n" +
      "(15, (select id from programs where code = '" + program + "'), true, 'C', 14, 'Maximum Stock Quantity'),\n" +
      "(16, (select id from programs where code = '" + program + "'), true, 'C', 15, 'Calculated Order Quantity'),\n" +
      "(17, (select id from programs where code = '" + program + "'), true, 'U', 16, 'Requested quantity'),\n" +
      "(18, (select id from programs where code = '" + program + "'), true, 'U', 17, 'Requested quantity explanation'),\n" +
      "(19, (select id from programs where code = '" + program + "'), true, 'U', 18, 'Approved Quantity'),\n" +
      "(20, (select id from programs where code = '" + program + "'), true, 'C', 19, 'Packs to Ship'),\n" +
      "(21, (select id from programs where code = '" + program + "'), true, 'R', 20, 'Price per pack'),\n" +
      "(22, (select id from programs where code = '" + program + "'), true, 'C', 21, 'Total cost'),\n" +
      "(23, (select id from programs where code = '" + program + "'), true, 'U', 22, 'Expiration Date'),\n" +
      "(24, (select id from programs where code = '" + program + "'), true, 'U', 23, 'Remarks');");
  }

  public String getFacilityName(String code) throws IOException, SQLException {
    String name = null;
    ResultSet rs = query("select name from facilities where code = '" + code + "';");

    if (rs.next()) {
      name = rs.getString("name");
    }
    return name;
  }

  public String getFacilityPopulation(String code) throws IOException, SQLException {
    String population = null;
    ResultSet rs = query("select catchmentPopulation from facilities where code = '" + code + "';");

    if (rs.next()) {
      population = rs.getString("catchmentPopulation");
    }
    return population;
  }

  public String getOverriddenIsa(String facilityCode, String program, String product) throws IOException, SQLException {
    String overriddenIsa = null;
    ResultSet rs = query("select overriddenIsa from facility_program_products " +
      "where facilityId = '" + getFacilityID(facilityCode) + "' and programProductId = " +
      "(select id from program_products where programId='" + getProgramID(program) + "' and productId='" + getProductID(product) + "');");

    if (rs.next()) {
      overriddenIsa = rs.getString("overriddenIsa");
    }
    return overriddenIsa;
  }

  public void InsertOverriddenIsa(String facilityCode, String program, String product, int overriddenIsa) throws IOException, SQLException {
    update("INSERT INTO facility_program_products (facilityId, programProductId,overriddenIsa) VALUES (" + getFacilityID(facilityCode) + "," +
      "(select id from program_products where programId='" + getProgramID(program) + "' and productId='" + getProductID(product) + "')," + overriddenIsa + ");");
  }

  public void updateOverriddenIsa(String facilityCode, String program, String product, String overriddenIsa) throws IOException, SQLException {
    update("Update facility_program_products set overriddenIsa=" + overriddenIsa + " where facilityId='"
      + getFacilityID(facilityCode) + "' and programProductId = (select id from program_products where programId='"
      + getProgramID(program) + "' and productId='" + getProductID(product) + "');");
  }

  public String[] getProgramProductISA(String program, String product) throws IOException, SQLException {
    String isaParams[] = new String[7];
    ResultSet rs = query("select * from program_product_Isa where " +
      " programProductId = (select id from program_products where programId='" + getProgramID(program) + "' and productId='" + getProductID(product) + "');");

    if (rs.next()) {
      isaParams[0] = rs.getString("whoRatio");
      isaParams[1] = rs.getString("dosesPerYear");
      isaParams[2] = rs.getString("wastageFactor");
      isaParams[3] = rs.getString("bufferPercentage");
      isaParams[4] = rs.getString("adjustmentValue");
      isaParams[5] = rs.getString("minimumValue");
      isaParams[6] = rs.getString("maximumValue");
    }
    return isaParams;
  }

  public Long getFacilityID(String facilityCode) throws IOException, SQLException {
    Long id = null;
    ResultSet rs = query("select id from facilities where code='" + facilityCode + "';");

    if (rs.next()) {
      id = rs.getLong("id");
    }
    return id;
  }

  public String getFacilityFieldBYCode(String field, String code) throws IOException, SQLException {
    String facilityField = null;
    ResultSet rs = query("SELECT %s FROM facilities WHERE code = '%s'", field, code);

    if (rs.next()) {
      facilityField = rs.getString(1);
    }
    return facilityField;
  }

  public void updateFacilityFieldBYCode(String field, String value, String code) throws IOException, SQLException {
    update("update facilities set " + field + "='" + value + "' where code='" + code + "';");
  }

  public void insertSupplyLines(String supervisoryNode, String programCode, String facilityCode, boolean exportOrders) throws IOException, SQLException {
    update("insert into supply_lines (description, supervisoryNodeId, programId, supplyingFacilityId,exportOrders) values\n" +
      "('supplying node for " + programCode + "', (select id from supervisory_nodes where code = '" + supervisoryNode + "'), (select id from programs where code='" + programCode + "'),(select id from facilities where code = '" + facilityCode + "')," + exportOrders + ");\n");
  }

  public void updateSupplyLines(String previousFacilityCode, String newFacilityCode) throws IOException, SQLException {
    update("update supply_lines SET supplyingFacilityId=(select id from facilities where code = '" + newFacilityCode + "') " +
      "where supplyingFacilityId=(select id from facilities where code = '" + previousFacilityCode + "');");
  }

  public void insertValuesInRequisition(boolean emergencyRequisitionRequired) throws IOException, SQLException {
    update("update requisition_line_items set beginningBalance=1,  quantityReceived=1, quantityDispensed = 1, " +
      "newPatientCount = 1, stockOutDays = 1, quantityRequested = 10, reasonForRequestedQuantity = 'bad climate', " +
      "normalizedConsumption = 10, packsToShip = 1");
    update("update requisitions set fullSupplyItemsSubmittedCost = 12.5000, nonFullSupplyItemsSubmittedCost = 0.0000");

    if (emergencyRequisitionRequired) {
      update("update requisitions set emergency='true'");
    }
  }

  public void insertValuesInRegimenLineItems(String patientsOnTreatment,
                                             String patientsToInitiateTreatment,
                                             String patientsStoppedTreatment,
                                             String remarks) throws IOException, SQLException {
    update("update regimen_line_items set patientsOnTreatment='" + patientsOnTreatment
      + "', patientsToInitiateTreatment='" + patientsToInitiateTreatment + "', patientsStoppedTreatment='"
      + patientsStoppedTreatment + "',remarks='" + remarks + "';");

  }

  public void insertApprovedQuantity(Integer quantity) throws IOException, SQLException {
    update("update requisition_line_items set quantityApproved=" + quantity);

  }

  public void updateRequisitionStatus(String status, String username, String program) throws IOException, SQLException {
    update("update requisitions set status='" + status + "';");
    ResultSet rs = query("select id from requisitions where programId = (select id from programs where code='" + program + "');");
    while (rs.next()) {
      update("insert into requisition_status_changes(rnrId, status, createdBy, modifiedBy) values(" + rs.getString("id") + ", '" + status + "', " +
        "(select id from users where username = '" + username + "'), (select id from users where username = '" + username + "'));");
    }
    update("update requisitions set supervisoryNodeId = (select id from supervisory_nodes where code='N1');");
    update("update requisitions set createdBy= (select id from users where username = '" + username + "') , modifiedBy= (select id from users where username = '" + username + "');");
  }

  public void updateRequisitionStatusByRnrId(String status, String username, int rnrId) throws IOException, SQLException {
    update("update requisitions set status='" + status + "' where id=" + rnrId + ";");
    update("insert into requisition_status_changes(rnrId, status, createdBy, modifiedBy) values(" + rnrId + ", '" + status + "', " +
      "(select id from users where username = '" + username + "'), (select id from users where username = '" + username + "'));");

    update("update requisitions set supervisoryNodeId = (select id from supervisory_nodes where code='N1');");
    update("update requisitions set createdBy= (select id from users where username = '" + username + "') , modifiedBy= (select id from users where username = '" + username + "');");
  }

  public String getSupplyFacilityName(String supervisoryNode, String programCode) throws IOException, SQLException {
    String facilityName = null;
    ResultSet rs = query("select name from facilities where id=" +
      "(select supplyingFacilityId from supply_lines where supervisoryNodeId=" +
      "(select id from supervisory_nodes where code='" + supervisoryNode + "') and programId = " +
      "(select id from programs where code='" + programCode + "'));");

    if (rs.next()) {
      facilityName = rs.getString("name");
    }
    return facilityName;

  }

  public String getUserID(String userName) throws IOException, SQLException {
    String userId = null;
    ResultSet rs = query("select id from users where username='" + userName + "'");
    if (rs.next()) {
      userId = rs.getString("id");
    }
    return userId;
  }

  public int getMaxRnrID() throws IOException, SQLException {
    int rnrId = 0;
    ResultSet rs = query("select max(id) from requisitions");
    if (rs.next()) {
      rnrId = Integer.parseInt(rs.getString("max"));
    }
    return rnrId;
  }

  public void setupMultipleProducts(String program, String facilityType, int numberOfProductsOfEachType, boolean defaultDisplayOrder) throws SQLException, IOException {


    update("delete from facility_approved_products;");
    update("delete from program_products;");
    update("delete from products;");
    update("delete from product_categories;");

    String iniProductCodeNonFullSupply = "NF";

    String iniProductCodeFullSupply = "F";

    update("INSERT INTO product_categories (code, name, displayOrder) values ('C1', 'Antibiotics', 1);");
    ResultSet rs = query("Select id from product_categories where code='C1';");

    int categoryId = 0;
    if (rs.next()) {
      categoryId = rs.getInt("id");
    }

    String insertSql;

    insertSql = "INSERT INTO products (code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived, displayOrder, categoryId) values\n";

    for (int i = 0; i < numberOfProductsOfEachType; i++) {
      if (defaultDisplayOrder) {
        insertSql = insertSql + "('" + iniProductCodeFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE,    1, " + categoryId + "),\n";
        insertSql = insertSql + "('" + iniProductCodeNonFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                FALSE,      TRUE,    1, " + categoryId + "),\n";
      } else {
        insertSql = insertSql + "('" + iniProductCodeFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE,    " + i + ", " + categoryId + "),\n";
        insertSql = insertSql + "('" + iniProductCodeNonFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                FALSE,      TRUE,    " + i + ", " + categoryId + "),\n";
      }
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";\n";

    update(insertSql);

    insertSql = "INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES\n";

    for (int i = 0; i < numberOfProductsOfEachType; i++) {
      insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + iniProductCodeFullSupply + i + "'), 30, 12.5, true),\n";
      insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + iniProductCodeNonFullSupply + i + "'), 30, 12.5, true),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";


    update(insertSql);


    insertSql = "INSERT INTO facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) VALUES\n";

    for (int i = 0; i < numberOfProductsOfEachType; i++) {
      insertSql = insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + iniProductCodeFullSupply + i + "')), 3),\n";
      insertSql = insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + iniProductCodeNonFullSupply + i + "')), 3),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";

    update(insertSql);

  }

  public void setupMultipleCategoryProducts(String program, String facilityType, int numberOfCategories, boolean defaultDisplayOrder) throws SQLException, IOException {

    update("delete from facility_approved_products;");
    update("delete from program_products;");
    update("delete from products;");
    update("delete from product_categories;");

    String iniProductCodeNonFullSupply = "NF";
    String iniProductCodeFullSupply = "F";

    String insertSql = "INSERT INTO product_categories (code, name, displayOrder) values\n";
    for (int i = 0; i < numberOfCategories; i++) {
      if (defaultDisplayOrder) {
        insertSql = insertSql + "('C" + i + "',  'Antibiotics" + i + "',1),\n";
      } else {
        insertSql = insertSql + "('C" + i + "',  'Antibiotics" + i + "'," + i + "),\n";
      }
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";
    update(insertSql);


    insertSql = "INSERT INTO products (code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived, displayOrder, categoryId) values\n";

    for (int i = 0; i < 11; i++) {
      insertSql = insertSql + "('" + iniProductCodeFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                 FALSE,      TRUE,    1, (select id from product_categories where code='C" + i + "')),\n";
      insertSql = insertSql + "('" + iniProductCodeNonFullSupply + i + "',  'a',             'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,      TRUE,         1,                 FALSE,      TRUE,    1, (select id from product_categories where code='C" + i + "')),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";\n";

    update(insertSql);

    insertSql = "INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES\n";

    for (int i = 0; i < 11; i++) {
      insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + iniProductCodeFullSupply + i + "'), 30, 12.5, true),\n";
      insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + iniProductCodeNonFullSupply + i + "'), 30, 12.5, true),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";
    update(insertSql);

    insertSql = "INSERT INTO facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) VALUES\n";

    for (int i = 0; i < 11; i++) {
      insertSql = insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + iniProductCodeFullSupply + i + "')), 3),\n";
      insertSql = insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + iniProductCodeNonFullSupply + i + "')), 3),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";

    update(insertSql);

  }

  public void assignRight(String roleName, String roleRight) throws SQLException, IOException {
    update("INSERT INTO role_rights\n" +
      "  (roleId, rightName) VALUES\n" +
      "  ((select id from roles where name='" + roleName + "'), '" + roleRight + "');");
  }

  public void updatePacksToShip(String packsToShip) throws SQLException {
    update("update requisition_line_items set packsToShip='" + packsToShip + "';");
  }

  public Long getProgramID(String program) throws IOException, SQLException {
    Long programID = null;
    ResultSet rs = query("SELECT ID from programs where code='" + program + "'");

    if (rs.next()) {
      programID = rs.getLong("id");
    }
    return programID;

  }

  public Long getProductID(String product) throws IOException, SQLException {
    Long productID = null;
    ResultSet rs = query("SELECT ID from products where code='" + product + "'");

    if (rs.next()) {
      productID = rs.getLong("id");
    }
    return productID;

  }

  public String getRequisitionStatus(Long requisitionId) throws IOException, SQLException {
    String requisitionStatus = null;
    ResultSet rs = query("SELECT status from requisitions where id=" + requisitionId);

    if (rs.next()) {
      requisitionStatus = rs.getString("status");
    }
    return requisitionStatus;

  }

  public String getOrderStatus(Long orderId) throws IOException, SQLException {
    String orderStatus = null;
    ResultSet rs = query("SELECT status from orders where id=" + orderId);

    if (rs.next()) {
      orderStatus = rs.getString("status");
    }
    return orderStatus;

  }

  public String getOrderId() throws IOException, SQLException {
    String orderId = null;
    ResultSet rs = query("SELECT id from orders");

    if (rs.next()) {
      orderId = rs.getString("id");
    }
    return orderId;

  }

  public void insertPastPeriodRequisitionAndLineItems(String facilityCode, String program, String periodName, String product) throws IOException, SQLException {
    update("DELETE FROM requisition_line_item_losses_adjustments;");
    update("DELETE FROM requisition_line_items;");
    update("DELETE FROM requisitions;");

    update("INSERT INTO requisitions " +
      "(facilityId, programId, periodId, status) VALUES " +
      "((SELECT id FROM facilities WHERE code = '" + facilityCode + "'), (SELECT ID from programs where code='" + program + "'), (select id from processing_periods where name='" + periodName + "'), 'RELEASED');");

    update("INSERT INTO requisition_line_items " +
      "(rnrId, productCode, beginningBalance, quantityReceived, quantityDispensed, stockInHand, normalizedConsumption, " +
      "dispensingUnit, maxMonthsOfStock, dosesPerMonth, dosesPerDispensingUnit, packSize,fullSupply) VALUES" +
      "((SELECT id FROM requisitions), '" + product + "', '0', '11' , '1', '10', '1' ,'Strip','0', '0', '0', '10','t');");

  }

  public String getRowsCountFromDB(String tableName) throws IOException, SQLException {
    String rowCount = null;
    ResultSet rs = query("SELECT count(*) as count from " + tableName + "");

    if (rs.next()) {
      rowCount = rs.getString("count");
    }
    return rowCount;

  }

  public void insertRoleAssignmentForDistribution(String userName, String roleName, String deliveryZoneCode) throws SQLException, IOException {

    update("INSERT INTO role_assignments\n" +
      "  (userId, roleId, deliveryZoneId) VALUES\n" +
      "  ((SELECT id FROM USERS WHERE username='" + userName + "'), (SELECT id FROM roles WHERE name = '" + roleName + "'),\n" +
      "  (SELECT id FROM delivery_zones WHERE code='" + deliveryZoneCode + "'));");
  }

  public void deleteDeliveryZoneToFacilityMapping(String deliveryZoneName) throws SQLException, IOException {
    update("delete from delivery_zone_members where deliveryZoneId in (select id from delivery_zones where name='" + deliveryZoneName + "');");
  }

  public void deleteProgramToFacilityMapping(String programCode) throws SQLException, IOException {
    update("delete from programs_supported where programId in (select id from programs where code='" + programCode + "');");
  }

  public void updateActiveStatusOfFacility(String facilityCode, String active) throws SQLException, IOException {
    update("update facilities set active='" + active + "' where code='" + facilityCode + "';");
  }

  public void updatePopulationOfFacility(String facility, String population) throws SQLException, IOException {

    update("update facilities set catchmentPopulation=" + population + " where code='" + facility + "';");
  }

  public void insertDeliveryZone(String code, String name) throws SQLException {
    update("INSERT INTO delivery_zones ( code ,name)values\n" +
      "('" + code + "','" + name + "');");
  }

  public void insertWarehouseIntoSupplyLinesTable(String facilityCodeFirst,
                                                  String programFirst, String supervisoryNode, boolean exportOrders) throws SQLException {
    update("INSERT INTO supply_lines (supplyingFacilityId, programId, supervisoryNodeId, description, exportOrders, createdBy, modifiedBy) values " +
      "(" + "(select id from facilities where code = '" + facilityCodeFirst + "')," + "(select id from programs where name ='" + programFirst + "')," + "(select id from supervisory_nodes where code='" + supervisoryNode + "'),'warehouse', " + exportOrders + ", '1', '1');");
  }

  public void insertDeliveryZoneMembers(String code, String facility) throws SQLException {
    update("INSERT INTO delivery_zone_members ( deliveryZoneId ,facilityId )values\n" +
      "((select id from  delivery_zones where code ='" + code + "'),(select id from  facilities where code ='" + facility + "'));");
  }

  public void insertDeliveryZoneProgramSchedule(String code, String program, String scheduleCode) throws SQLException {
    update("INSERT INTO delivery_zone_program_schedules\n" +
      "(deliveryZoneId, programId, scheduleId ) values(\n" +
      "(select id from delivery_zones where code='" + code + "'),\n" +
      "(select id from programs where code='" + program + "'),\n" +
      "(select id from processing_schedules where code='" + scheduleCode + "')\n" +
      ");");
  }

  public void insertProcessingPeriodForDistribution(int numberOfPeriodsRequired, String schedule) throws IOException, SQLException {
    for (int counter = 1; counter <= numberOfPeriodsRequired; counter++) {
      String startDate = "2013-01-0" + counter;
      String endDate = "2013-01-0" + counter;
      insertProcessingPeriod("Period" + counter, "PeriodDecs" + counter, startDate, endDate, 1, schedule);
    }
  }

  public void updateActiveStatusOfProgram(String programCode, boolean status) throws SQLException {
    update("update programs SET active=" + status + " where code='" + programCode + "';");
  }

  public void insertRegimenTemplateColumnsForProgram(String programName) throws SQLException {
    update("INSERT INTO program_regimen_columns(name, programId, label, visible, dataType) values\n" +
      "('code',(SELECT id FROM programs WHERE name='" + programName + "'), 'header.code',true,'regimen.reporting.dataType.text'),\n" +
      "('name',(SELECT id FROM programs WHERE name='" + programName + "'),'header.name',true,'regimen.reporting.dataType.text'),\n" +
      "('patientsOnTreatment',(SELECT id FROM programs WHERE name='" + programName + "'),'Number of patients on treatment',true,'regimen.reporting.dataType.numeric'),\n" +
      "('patientsToInitiateTreatment',(SELECT id FROM programs WHERE name='" + programName + "'),'Number of patients to be initiated treatment',true,'regimen.reporting.dataType.numeric'),\n" +
      "('patientsStoppedTreatment',(SELECT id FROM programs WHERE name='" + programName + "'),'Number of patients stopped treatment',true,'regimen.reporting.dataType.numeric'),\n" +
      "('remarks',(SELECT id FROM programs WHERE name='" + programName + "'),'Remarks',true,'regimen.reporting.dataType.text');");
  }

  public void insertRegimenTemplateConfiguredForProgram(String programName, String categoryCode, String code, String name, boolean active) throws SQLException {
    update("update programs set regimenTemplateConfigured='true' where name='" + programName + "';");
    update("INSERT INTO regimens\n" +
      "  (programId, categoryId, code, name, active,displayOrder) VALUES\n" +
      "  ((SELECT id FROM programs WHERE name='" + programName + "'), (SELECT id FROM regimen_categories WHERE code = '" + categoryCode + "'),\n" +
      "  '" + code + "','" + name + "','" + active + "',1);");
  }

  public void setRegimenTemplateConfiguredForAllPrograms(boolean flag) throws SQLException {
    update("update programs set regimenTemplateConfigured='" + flag + "';");
  }

  public String getAllActivePrograms() throws SQLException {
    String programsString = "";
    ResultSet rs = query("select * from programs where active=true;");

    while (rs.next()) {
      programsString = programsString + rs.getString("name");
    }
    return programsString;
  }

  public void updateProgramRegimenColumns(String programName, String regimenColumnName, boolean visible) throws SQLException {
    update("update program_regimen_columns set visible=" + visible + " where name ='" + regimenColumnName +
      "'and programId=(SELECT id FROM programs WHERE name='" + programName + "');");
  }

  public void setupOrderFileConfiguration(String filePrefix, String headerInFile) throws IOException, SQLException {
    update("DELETE FROM order_configuration;");
    update("INSERT INTO order_configuration \n" +
      "  (filePrefix, headerInFile) VALUES\n" +
      "  ('" + filePrefix + "', '" + headerInFile + "');");
  }

  public void setupShipmentFileConfiguration(String headerInFile) throws IOException, SQLException {
    update("DELETE FROM shipment_configuration;");
    update("INSERT INTO shipment_configuration(headerInFile) VALUES('" + headerInFile + "')");
  }

  public void setupOrderFileOpenLMISColumns(String dataFieldLabel, String includeInOrderFile, String columnLabel,
                                            int position, String Format) throws IOException, SQLException {
    update("UPDATE order_file_columns SET " +
      "includeInOrderFile='" + includeInOrderFile + "', columnLabel='" + columnLabel + "', position=" + position
      + ", format='" + Format + "' where dataFieldLabel='" + dataFieldLabel + "'");
  }

  public void deleteOrderFileNonOpenLMISColumns() throws SQLException {
    update("DELETE FROM order_file_columns where openLMISField = FALSE");
  }

  public void setupOrderFileNonOpenLMISColumns(String dataFieldLabel, String includeInOrderFile,
                                               String columnLabel, int position) throws IOException, SQLException {
    update("INSERT INTO order_file_columns (dataFieldLabel, includeInOrderFile, columnLabel, position, openLMISField) " +
      "VALUES ('" + dataFieldLabel + "','" + includeInOrderFile + "','" + columnLabel + "'," + position + ", FALSE)");
  }

  public String getCreatedDate(String tableName, String dateFormat) throws SQLException {
    String createdDate = null;
    ResultSet rs = query("SELECT to_char(createdDate, '" + dateFormat + "' ) from " + tableName);

    if (rs.next()) {
      createdDate = rs.getString(1);
    }
    return createdDate;
  }

  public void updateProductToHaveGroup(String product, String productGroup) throws SQLException {
    update("UPDATE products set productGroupId = (SELECT id from product_groups where code = '" + productGroup + "') where code = '" + product + "'");
  }

  public void deleteReport(String reportName) throws SQLException {
    update("DELETE FROM report_templates where name = '" + reportName + "'");
  }

  public void insertOrders(String status, String username, String program) throws IOException, SQLException {
    ResultSet rs = query("select id from requisitions where programId=(select id from programs where code='" + program + "');");
    while (rs.next()) {
      update("update requisitions set status='RELEASED' where id =" + rs.getString("id"));


      update("insert into orders(Id, status,supplyLineId, createdBy, modifiedBy) values(" + rs.getString("id")
        + ", '" + status + "', (select id from supply_lines where supplyingFacilityId = " +
        "(select facilityId from fulfillment_role_assignments where userId = " +
        "(select id from users where username = '" + username + "')) limit 1) ," +
        "(select id from users where username = '" + username + "'), (select id from users where username = '" + username + "'));");

    }
  }

  public void setupUserForFulfillmentRole(String username, String roleName, String facilityCode) throws IOException, SQLException {
    update("insert into fulfillment_role_assignments(userId, roleId,facilityId) values((select id from users where userName = '" + username +
      "'),(select id from roles where name = '" + roleName + "')," +
      "(select id from facilities where code = '" + facilityCode + "'));");

  }

  public HashMap getFacilityVisitDetails() throws SQLException {
    HashMap m1 = new HashMap();
    ResultSet rs = query("select observations,confirmedByName,confirmedByTitle,verifiedByName,verifiedByTitle from facility_visits;");
    while (rs.next()) {
      m1.put("observations", rs.getString("observations"));
      m1.put("confirmedByName", rs.getString("confirmedByName"));
      m1.put("confirmedByTitle", rs.getString("confirmedByTitle"));
      m1.put("verifiedByName", rs.getString("verifiedByName"));
      m1.put("verifiedByTitle", rs.getString("verifiedByTitle"));
    }
    return m1;
  }

  public String getWarehouse1Name(String facilityCode) throws SQLException {
    String warehouseName = "";
    ResultSet rs = query("select name from facilities where code='" + facilityCode + "';");
    if (rs.next())
      warehouseName = rs.getString(1);
    return warehouseName;
  }

  public void disableFacility(String warehouseName) throws SQLException {
    update("UPDATE facilities SET enabled='false' WHERE name='" + warehouseName + "';");
  }

  public void enableFacility(String warehouseName) throws SQLException {
    update("UPDATE facilities SET enabled='true' WHERE name='" + warehouseName + "';");
  }

  public int getPODLineItemQuantityReceived(long orderId, String productCode) throws Exception {
    try (ResultSet rs1 = query("SELECT id FROM pod WHERE OrderId = %d", orderId)) {
      if (rs1.next()) {
        int podId = rs1.getInt("id");
        ResultSet rs2 = query("SELECT quantityReceived FROM pod_line_items WHERE podId = %d and productCode='%s'", podId, productCode);
        if (rs2.next()) {
          return rs2.getInt("quantityReceived");
        }
      }
    }
    return -1;
  }

  public void setExportOrdersFlagInSupplyLinesTable(boolean flag, String facilityCode) throws SQLException {
    update("UPDATE supply_lines SET exportOrders='" + flag + "' WHERE supplyingFacilityId=(select id from facilities where code='" + facilityCode + "');");
  }

  public void enterValidDetailsInFacilityFtpDetailsTable(String facilityCode) throws SQLException {
    update("INSERT INTO facility_ftp_details(facilityId, serverHost, serverPort, username, password, localFolderPath) VALUES" +
      "((SELECT id FROM facilities WHERE code = '%s' ), '192.168.34.1', 21, 'openlmis', 'openlmis', '/ftp');", facilityCode);
  }

  public int getRequisitionGroupId(String facilityCode) throws SQLException {
    int rgId = 0;
    ResultSet rs = query("SELECT requisitionGroupId FROM requisition_group_members where facilityId=(SELECT id FROM facilities WHERE code ='" + facilityCode + "');");
    if (rs.next()) {
      rgId = rs.getInt(1);
    }
    return rgId;
  }

  public List<Integer> getAllProgramsOfFacility(String facilityCode) throws SQLException {
    List<Integer> l1 = new ArrayList<>();
    ResultSet rs = query("SELECT programId FROM programs_supported WHERE facilityId = (SELECT id FROM facilities WHERE code ='%s')", facilityCode);
    while (rs.next()) {
      l1.add(rs.getInt(1));
    }
    return l1;
  }

  public String getProgramFieldForProgramIdAndFacilityCode(int programId, String facilityCode, String field) throws SQLException {
    String res = null;
    ResultSet rs = query("SELECT %s FROM programs_supported WHERE programId = %d AND facilityId = (SELECT id FROM facilities WHERE code = '%s')",
      field, programId, facilityCode);

    if (rs.next()) {
      res = rs.getString(1);
    }
    return res;
  }

  public Date getProgramStartDateForProgramIdAndFacilityCode(int programId, String facilityCode) throws SQLException {
    Date date = null;
    ResultSet rs = query("SELECT startDate FROM programs_supported WHERE programId = %d AND facilityId = (SELECT id FROM facilities WHERE code ='%s')",
      programId, facilityCode);

    if (rs.next()) {
      date = rs.getDate(1);
    }
    return date;
  }

  public void changeVirtualFacilityTypeId(String facilityCode, int facilityTypeId) throws SQLException {
    update("UPDATE facilities SET typeid=" + facilityTypeId + "WHERE code='" + facilityCode + "';");
  }

  public String getGeographicZoneId(String geographicZone) throws SQLException {
    String res = null;
    ResultSet rs = query("select id  from geographic_zones WHERE code ='" + geographicZone + "';");

    if (rs.next()) {
      res = rs.getString(1);
    }
    return res;
  }

  public String getFacilityTypeId(String facilityType) throws SQLException {
    String res = null;
    ResultSet rs = query("select id  from facility_types WHERE code ='" + facilityType + "';");

    if (rs.next()) {
      res = rs.getString(1);
    }
    return res;
  }

  public void deleteCurrentPeriod() throws SQLException {
    update("delete from processing_periods where endDate>=NOW()");
  }

  public void updateProgramsSupportedByField(String field, String newValue, String facilityCode) throws SQLException {
    update("Update programs_supported set " + field + "='" + newValue + "' where facilityId=(Select id from facilities where code ='"
      + facilityCode + "');");
  }

  public void deleteSupervisoryRoleFromRoleAssignment() throws SQLException {
    update("delete from role_assignments where supervisoryNodeId is not null;");
  }

  public void deleteRnrTemplate() throws SQLException {
    update("delete from program_rnr_columns");
  }

  public void deleteProductAvailableAtFacility(String productCode, String programCode, String facilityCode) throws SQLException {
    update("delete from facility_approved_products where facilityTypeId=(select typeId from facilities where code='" + facilityCode + "') " +
      "and programproductid=(select id from program_products where programId=(select id from programs where code='" + programCode + "')" +
      "and productid=(select id from products where code='" + productCode + "'));");
  }

  public void UpdateProductFullSupplyStatus(String productCode, boolean fullSupply) throws SQLException {
    update("UPDATE products SET fullSupply=" + fullSupply + " WHERE code='" + productCode + "';");
  }

  public float getRequisitionFullSupplyItemsSubmittedCost(int requisitionId) throws SQLException {
    float fullSupplyItemsSubmittedCost = 0f;
    ResultSet rs = query("SELECT fullSupplyItemsSubmittedCost FROM requisitions WHERE id =" + requisitionId + ";");
    if (rs.next()) {
      fullSupplyItemsSubmittedCost = rs.getFloat(1);
    }
    return fullSupplyItemsSubmittedCost;
  }

  public void updateConfigureTemplateValidationFlag(String programCode, String flag) throws SQLException {
    update("UPDATE program_rnr_columns set formulaValidationRequired ='" + flag + "' WHERE programId=" +
      "(SELECT id from programs where code='" + programCode + "');");
  }

  public void updateConfigureTemplate(String programCode, String fieldName, String fieldValue, String visibilityFlag, String rnrColumnName) throws SQLException {
    update("UPDATE program_rnr_columns SET visible ='" + visibilityFlag + "', " + fieldName + "='" + fieldValue + "' WHERE programId=" +
      "(SELECT id from programs where code='" + programCode + "')" +
      "AND masterColumnId =(SELECT id from master_rnr_columns WHERE name = '" + rnrColumnName + "') ;");

  }

  public void deleteConfigureTemplate(String program) throws SQLException {
    update("DELETE FROM program_rnr_columns where programId=(select id from programs where code = '" + program + "');");
  }

  public String getApproverName(long requisitionId) throws IOException, SQLException {
    String name = null;
    ResultSet rs = query("SELECT name FROM requisition_status_changes WHERE rnrId =" + requisitionId + " and status='APPROVED';");

    if (rs.next()) {
      name = rs.getString("name");
    }
    return name;

  }

  public String getRequisitionLineItemFieldValue(Long requisitionId, String field, String productCode) throws SQLException {
    String value = null;
    ResultSet rs = query("SELECT %s FROM requisition_line_items WHERE rnrId = %d AND productCode = '%s'",
      field, requisitionId, productCode);
    if (rs.next()) {
      value = rs.getString(field);
    }
    return value;
  }

  public void updateProductFullSupplyFlag(boolean isFullSupply, String productCode) throws SQLException {
    update("UPDATE products SET fullSupply = %b WHERE code = '%s'", isFullSupply, productCode);
  }

  public void insertRoleAssignmentForSupervisoryNode(String userID, String roleName, String supervisoryNode, String programCode) throws SQLException {
    update(" INSERT INTO role_assignments\n" +
      "            (userId, roleId, programId, supervisoryNodeId) VALUES \n" +
      "    ('" + userID + "', (SELECT id FROM roles WHERE name = '" + roleName + "')," +
      " (SELECT id from programs WHERE code='" + programCode + "'), " +
      "(SELECT id from supervisory_nodes WHERE code = '" + supervisoryNode + "'));");
  }

  public void insertRequisitionGroupProgramScheduleForProgram(String requisitionGroupCode, String programCode, String scheduleCode) throws SQLException {
    update("delete from requisition_group_program_schedules where programId=(select id from programs where code='" + programCode + "');");
    update("insert into requisition_group_program_schedules ( requisitionGroupId , programId , scheduleId , directDelivery ) values\n" +
      "((select id from requisition_groups where code='" + requisitionGroupCode + "'),(select id from programs where code='" + programCode + "')," +
      "(select id from processing_schedules where code='" + scheduleCode + "'),TRUE);");
  }

  public void updateCreatedDateInRequisitionStatusChanges(String newDate, Long rnrId) throws SQLException {
    update("update requisition_status_changes SET createdDate= '" + newDate + "' WHERE rnrId=" + rnrId + ";");
  }

  public void updateCreatedDateInPODLineItems(String newDate, Long rnrId) throws SQLException {
    update("update pod SET createdDate= '" + newDate + "' WHERE orderId=" + rnrId + ";");
  }

  public void updatePeriodIdInRequisitions(Long rnrId) throws SQLException {
    Integer reqId = 0;
    ResultSet rs = query("select " + "periodId" + " from requisitions where id= " + rnrId + ";");

    if (rs.next()) {
      reqId = Integer.parseInt(rs.getString(1));
    }
    reqId = reqId - 1;
    update("update requisitions SET periodId= '" + reqId + "' WHERE id=" + rnrId + ";");
  }

  public void updateProductsByField(String field, String fieldValue, String productCode) throws SQLException {
    update("update products set " + field + "=" + fieldValue + " where code='" + productCode + "';");
  }

  public void updateCreatedDateAfterRequisitionIsInitiated(String createdDate) throws SQLException {
    update("update requisitions set createdDate ='" + createdDate + "';");
    update("update requisition_line_items set createdDate ='" + createdDate + "';");
    update("update requisition_status_changes set createdDate='" + createdDate + "';");
  }

  public void updateSupervisoryNodeForRequisitionGroup(String requisitionGroup, String supervisoryNodeCode) throws SQLException {
    update("update requisition_groups set supervisoryNodeId=(select id from supervisory_nodes where code='" + supervisoryNodeCode +
      "') where code='" + requisitionGroup + "';");
  }

  public void updateOrderStatus(String status, String username, String program) throws IOException, SQLException {
    update("update orders set status='RECEIVED'");
  }

  public void updateRequisitionToEmergency() throws IOException, SQLException {
    update("update requisitions set Emergency=true");
  }

  public void deleteSupplyLine() throws IOException, SQLException {
    update("delete from supply_lines where description='supplying node for MALARIA'");
  }

  public String getOrderFtpComment(Long orderId) throws IOException, SQLException {
    String orderFtpcomment = null;
    ResultSet rs = query("SELECT ftpComment from orders where id=" + orderId);

    if (rs.next()) {
      orderFtpcomment = rs.getString("ftpComment");
    }
    return orderFtpcomment;

  }

  public Map<String, String> getEpiUseDetails(String productGroupCode, String facilityCode) throws SQLException {
    return select("SELECT * FROM epi_use_line_items WHERE productGroupName = " +
      "(SELECT name FROM product_groups where code = '%s') AND epiUseId=(Select id from epi_use where facilityId=" +
      "(Select id from facilities where code ='%s'));", productGroupCode, facilityCode).get(0);
  }

  public void updateBudgetFlag(String ProgramName, Boolean Flag) throws IOException, SQLException {
    update("update programs set budgetingapplies ='" + Flag + "' where name ='" + ProgramName + "';");
  }

  public void insertBudgetData() throws IOException, SQLException {
    update("INSERT INTO budget_file_info VALUES (1,'abc.csv','f',200,'12/12/13',200,'12/12/13');");
    update("INSERT INTO budget_line_items VALUES (1,(select id from processing_periods where name='current Period'),1,'01/01/2013',200,'hjhj',200,'12/12/2013',200,'12/12/2013',(select id from facilities where code='F10'),1);");

  }

  public void updateBudgetLineItemsByField(String field, String newValue) throws SQLException {
    update("UPDATE budget_line_items SET " + field + " ='" + newValue + "';");
  }

  public void addRefrigeratorToFacility(String brand, String model, String serialNumber, String facilityCode) throws SQLException {
    update("INSERT INTO refrigerators(brand, model, serialNumber, facilityId, createdBy , modifiedBy) VALUES" +
      "('" + brand + "','" + model + "','" + serialNumber + "',(SELECT id FROM facilities WHERE code = '" + facilityCode + "'),1,1);");
  }

  public ResultSet getRefrigeratorReadings(String refrigeratorSerialNumber) throws SQLException {
    ResultSet resultSet = query("SELECT * FROM refrigerator_readings WHERE refrigeratorId = " +
      "(SELECT id FROM refrigerators WHERE serialNumber = '%s');", refrigeratorSerialNumber);
    resultSet.next();
    return resultSet;
  }

  public ResultSet getRefrigeratorProblems(Long readingId) throws SQLException {
    ResultSet resultSet = query("SELECT * FROM refrigerator_problems WHERE readingId = %d", readingId);
    return resultSet;
  }
}

