/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

public class DistributionEpiInventoryTest extends TestCaseHelper {

  public static final String USER = "user";
  public static final String PASSWORD = "password";
  public static final String FIRST_DELIVERY_ZONE_CODE = "firstDeliveryZoneCode";
  public static final String SECOND_DELIVERY_ZONE_CODE = "secondDeliveryZoneCode";
  public static final String FIRST_DELIVERY_ZONE_NAME = "firstDeliveryZoneName";
  public static final String SECOND_DELIVERY_ZONE_NAME = "secondDeliveryZoneName";
  public static final String FIRST_FACILITY_CODE = "firstFacilityCode";
  public static final String SECOND_FACILITY_CODE = "secondFacilityCode";
  public static final String VACCINES_PROGRAM = "vaccinesProgram";
  public static final String TB_PROGRAM = "secondProgram";
  public static final String SCHEDULE = "schedule";
  public static final String PRODUCT_GROUP_CODE = "productGroupName";

  public Map<String, String> epiUseData = new HashMap<String, String>() {{
    put(USER, "fieldCoordinator");
    put(PASSWORD, "Admin123");
    put(FIRST_DELIVERY_ZONE_CODE, "DZ1");
    put(SECOND_DELIVERY_ZONE_CODE, "DZ2");
    put(FIRST_DELIVERY_ZONE_NAME, "Delivery Zone First");
    put(SECOND_DELIVERY_ZONE_NAME, "Delivery Zone Second");
    put(FIRST_FACILITY_CODE, "F10");
    put(SECOND_FACILITY_CODE, "F11");
    put(VACCINES_PROGRAM, "VACCINES");
    put(TB_PROGRAM, "TB");
    put(SCHEDULE, "M");
    put(PRODUCT_GROUP_CODE, "PG1");
  }};

  @BeforeMethod(groups = {"distribution"})
  public void setUp() throws Exception {
    super.setup();

    Map<String, String> dataMap = epiUseData;

    setupDataForDistributionTest(dataMap.get(USER), dataMap.get(FIRST_DELIVERY_ZONE_CODE), dataMap.get(SECOND_DELIVERY_ZONE_CODE),
      dataMap.get(FIRST_DELIVERY_ZONE_NAME), dataMap.get(SECOND_DELIVERY_ZONE_NAME), dataMap.get(FIRST_FACILITY_CODE),
      dataMap.get(SECOND_FACILITY_CODE), dataMap.get(VACCINES_PROGRAM), dataMap.get(TB_PROGRAM), dataMap.get(SCHEDULE),
      dataMap.get(PRODUCT_GROUP_CODE));
  }

  public void setupDataForDistributionTest(String userSIC, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                           String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                           String facilityCodeFirst, String facilityCodeSecond,
                                           String programFirst, String programSecond, String schedule, String productGroupCode) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, "200", rightsList, programSecond,
      "District1", "Ngorongoro", "Ngorongoro");
    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProductGroup(productGroupCode);
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", productGroupCode, true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", productGroupCode, true);
    dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
    dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");
  }

  @Test(groups = {"distribution"})
  public void testDisplayAllActiveProductsWithIdealQuantityOnEpiInventoryPage() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");

    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.selectProgram("VACCINES");
    programProductISAPage.editFormula();
    programProductISAPage.fillProgramProductISA("100", "1", "50", "30", "0", "100", "2000");
    String expectedISAValue = String.valueOf(Math.round(Integer.parseInt(programProductISAPage.calculateISA("333")) / 10));
    programProductISAPage.saveISA();

    loginPage = programProductISAPage.logout();

    homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.initiate(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    EpiInventoryPage epiInventoryPage = facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE)).navigateToEpiInventory();

    assertEquals(epiInventoryPage.getIsaValue(1), expectedISAValue);
    assertEquals(epiInventoryPage.getProductCode(1),"antibiotic");

    assertEquals(epiInventoryPage.getIsaValue(2), "--");
    assertEquals(epiInventoryPage.getProductCode(2),"ProductName6");

    assertEquals(epiInventoryPage.getIsaValue(3), "--");
    assertEquals(epiInventoryPage.getProductCode(1),"antibiotic");

  }

  // @Test(groups = {"distribution"})
  public void testEpiInventoryPageSyncWhenAllProductsInactiveAfterCaching() throws Exception {

  }

  //@Test(groups = {"distribution"})
  public void testEpiInventoryPageSyncWhenFacilityInactiveAfterCaching() throws Exception {


  }

  //  @Test(groups = {"distribution"})
  public void testEpiInventoryPageSyncWhenFacilityDisabledAfterCaching() throws Exception {

  }

  //@Test(groups = {"distribution"})
  public void testEpiInventoryPageSyncWhenAllProgramInactiveAfterCaching() throws Exception {

  }

  // @Test(groups = {"distribution"})
  public void testEpiInventoryPageSyncWhenSomeFieldsEmpty() throws Exception {

  }

  // @Test(groups = {"distribution"})
  public void testEpiInventoryPageSyncWhenNrAppliedToAllFields() throws Exception {

  }

  @Test(groups = {"distribution"})
  public void testEpiInventoryPageSyncWhenNRAppliedToFewFields() throws Exception {

  }

  @AfterMethod(groups = "distribution")
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
    ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
  }

}
