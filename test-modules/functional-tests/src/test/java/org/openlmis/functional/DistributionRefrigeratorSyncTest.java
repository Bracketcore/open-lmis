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


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.lang.String.valueOf;

public class DistributionRefrigeratorSyncTest extends TestCaseHelper {

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

  @Test(groups = {"distribution"})
  public void testRefrigeratorPageSync() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.onRefrigeratorScreen();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.enterValueInBrandModal("LG");
    refrigeratorPage.enterValueInModelModal("800 LITRES");
    refrigeratorPage.enterValueInManufacturingSerialNumberModal("GR-J287PGHV");
    refrigeratorPage.clickDoneOnModal();

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "RED");
    refrigeratorPage.clickShow();
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "RED");

    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "AMBER");

    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("1");
    refrigeratorPage.enterValueInHighAlarmEvents("0");
    refrigeratorPage.clickProblemSinceLastVisitDontKnowRadio();

    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "GREEN");

    refrigeratorPage.enterValueInNotesTextArea("miscellaneous");
    refrigeratorPage.clickDone();

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011",1);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorDataInDatabase();
  }

  @Test(groups = {"distribution"})
  public void testRefrigeratorSyncWhenRefrigeratorHasProblemAndDefaultValueOfNotes() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.onRefrigeratorScreen();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.enterValueInBrandModal("LG");
    refrigeratorPage.enterValueInModelModal("800 LITRES");
    refrigeratorPage.enterValueInManufacturingSerialNumberModal("GR-J287PGHV");
    refrigeratorPage.clickDoneOnModal();

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "RED");
    refrigeratorPage.clickShow();
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "RED");

    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "AMBER");

    refrigeratorPage.clickFunctioningCorrectlyNoRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("1");
    refrigeratorPage.enterValueInHighAlarmEvents("0");
    refrigeratorPage.clickProblemSinceLastVisitYesRadio();
    refrigeratorPage.verifyIndividualRefrigeratorColor("overall","AMBER");
    refrigeratorPage.selectOtherProblem();

    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011",1);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorDataInDatabase();
  }

  @Test(groups = {"distribution"})
  public void testRefrigeratorSyncWhenProblemIsSelectedAndAppliedNRBeforeSync() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.onRefrigeratorScreen();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.enterValueInBrandModal("LG");
    refrigeratorPage.enterValueInModelModal("800 LITRES");
    refrigeratorPage.enterValueInManufacturingSerialNumberModal("GR-J287PGHV");
    refrigeratorPage.clickDoneOnModal();

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "RED");
    refrigeratorPage.clickShow();
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "RED");

    refrigeratorPage.applyNRToRefrigeratorTemperature();
    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "AMBER");

    refrigeratorPage.clickFunctioningCorrectlyNR();
    refrigeratorPage.applyNRToLowAlarmEvent();
    refrigeratorPage.applyNRToHighAlarmEvent();
    refrigeratorPage.verifyIndividualRefrigeratorColor("overall","AMBER");
    refrigeratorPage.clickProblemSinceLastVisitNR();

    refrigeratorPage.verifyFieldsDisabledWhenAllNRSelected();

    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011",1);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorDataInDatabase();
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

  public void initiateDistribution(String deliveryZoneNameFirst, String programFirst) throws IOException {

    HomePage homePage = new HomePage(testWebDriver);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    distributionPage.clickRecordData();
  }

  public void enterDataInGeneralObservationsPage(String observation, String confirmName, String confirmTitle, String verifierName,
                                                 String verifierTitle) {
    GeneralObservationPage generalObservationPage = new GeneralObservationPage(testWebDriver);
    generalObservationPage.navigate();
    generalObservationPage.setObservations(observation);
    generalObservationPage.setConfirmedByName(confirmName);
    generalObservationPage.setConfirmedByTitle(confirmTitle);
    generalObservationPage.setVerifiedByName(verifierName);
    generalObservationPage.setVerifiedByTitle(verifierTitle);
  }

  public void enterDataInEpiUsePage(Integer stockAtFirstOfMonth, Integer receivedValue, Integer distributedValue,
                                    Integer loss, Integer stockAtEndOfMonth, String expirationDate, int rowNumber) {
    EPIUse epiUse = new EPIUse(testWebDriver);
    epiUse.navigate();

    epiUse.verifyProductGroup("PG1-Name", 1);

    epiUse.enterValueInStockAtFirstOfMonth(stockAtFirstOfMonth.toString(), rowNumber);
    epiUse.verifyIndicator("AMBER");
    epiUse.enterValueInReceived(receivedValue.toString(), rowNumber);
    epiUse.enterValueInDistributed(distributedValue.toString(), rowNumber);
    epiUse.enterValueInLoss(valueOf(loss), rowNumber);
    epiUse.enterValueInStockAtEndOfMonth(stockAtEndOfMonth.toString(), rowNumber);
    epiUse.enterValueInExpirationDate(expirationDate, rowNumber);
    epiUse.verifyIndicator("GREEN");
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
