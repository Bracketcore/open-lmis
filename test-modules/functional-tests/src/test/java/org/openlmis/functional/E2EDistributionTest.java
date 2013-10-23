/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EDistributionTest extends TestCaseHelper {

  public String userSIC, password;


  @BeforeMethod(groups = {"offline"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"offline"}, dataProvider = "Data-Provider-Function")
  public void testE2EManageDistribution(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                        String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                        String facilityCodeFirst, String facilityCodeSecond,
                                        String programFirst, String programSecond, String schedule, String period, Integer totalNumberOfPeriods) throws Exception {

    List<String> rightsList = new ArrayList<String>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProductGroup("PG1");
    dbWrapper.insertProductWithGroup("Product5", "ProdutName5", "PG1", true);
    dbWrapper.insertProductWithGroup("Product6", "ProdutName6", "PG1", true);
    dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
    dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");
    dbWrapper.deleteDeliveryZoneMembers("F11");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();

    waitForAppCacheComplete();
    switchOffNetwork();
    testWebDriver.sleep(2000);
    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();
    assertFalse("Delivery Zone selectbox displayed.", distributionPage.verifyDeliveryZoneSelectBoxNotPresent());
    assertFalse("Period selectbox displayed.", distributionPage.verifyPeriodSelectBoxNotPresent());
    assertFalse("Program selectbox displayed.", distributionPage.verifyProgramSelectBoxNotPresent());


    distributionPage.clickRecordData();
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility("F10");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.onRefrigeratorScreen();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.enterValueInBrandModal("LG");
    refrigeratorPage.enterValueInModelModal("800 LITRES");
    refrigeratorPage.enterValueInManufacturingSerialNumberModal("GR-J287PGHV");
    refrigeratorPage.clickDoneOnModal();


    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();


    distributionPage.clickRecordData();
    facilityListPage.selectFacility("F10");

    String[] refrigeratorDetails = "LG;800 LITRES;GR-J287PGHV".split(";");
    for (int i = 0; i < refrigeratorDetails.length; i++) {
      assertEquals(testWebDriver.getElementByXpath("//div[@class='list-row ng-scope']/ng-include/form/div[1]/div[" + (i + 2) + "]").getText(), refrigeratorDetails[i]);
    }

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

    EPIUse epiUse = new EPIUse(testWebDriver);
    epiUse.navigate();
    epiUse.verifyProductGroup("PG1-Name", 1);
    epiUse.verifyIndicator("RED");

    epiUse.enterValueInStockAtFirstOfMonth("10", 1);
    epiUse.verifyIndicator("AMBER");
    epiUse.enterValueInReceived("20", 1);
    epiUse.enterValueInDistributed("30", 1);
    epiUse.enterValueInLoss("40", 1);
    epiUse.enterValueInStockAtEndOfMonth("50", 1);
    epiUse.enterValueInExpirationDate("10/2011", 1);
    epiUse.verifyIndicator("GREEN");


    GeneralObservationPage generalObservationPage = new GeneralObservationPage(testWebDriver);
    generalObservationPage.navigate();
    generalObservationPage.setObservations("Some observations");
    generalObservationPage.setConfirmedByName("samuel");
    generalObservationPage.setConfirmedByTitle("Doe");
    generalObservationPage.setVerifiedByName("Mai ka");
    generalObservationPage.setVerifiedByTitle("Laal");

    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();
    distributionPage.clickRecordData();
    facilityListPage.selectFacility("F10");

    refrigeratorPage.clickShow();
    assertEquals(refrigeratorPage.getRefrigeratorTemperateTextFieldValue(), "3");
    assertEquals(refrigeratorPage.getLowAlarmEventsTextFieldValue(), "1");
    assertEquals(refrigeratorPage.getHighAlarmEventsTextFieldValue(), "0");
    assertEquals(refrigeratorPage.getNotesTextAreaValue(), "miscellaneous");
    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "GREEN");

    epiUse.navigate();
    epiUse.verifyIndicator("GREEN");

    epiUse.verifyTotal("30", 1);
    epiUse.verifyStockAtFirstOfMonth("10", 1);
    epiUse.verifyReceived("20", 1);
    epiUse.verifyDistributed("30", 1);
    epiUse.verifyLoss("40", 1);
    epiUse.verifyStockAtEndOfMonth("50", 1);
    epiUse.verifyExpirationDate("10/2011", 1);

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();

    switchOnNetwork();
    testWebDriver.sleep(5000);

    distributionPage.clickSyncDistribution();
    assertEquals(distributionPage.getSyncMessage(), "F10 - Village Dispensary synced successfully");

    dbWrapper.verifyFacilityVisits("Some observations", "samuel", "Doe", "Mai ka", "Laal");
    distributionPage.clickRecordData();
    facilityListPage.selectFacility("F10");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");
    //facilityListPage.verifyFacilityIndicatorColor("individual", "BLUE");
    generalObservationPage.navigate();
    generalObservationPage.verifyAllFieldsDisabled();

    epiUse.navigate();
    epiUse.verifyAllFieldsDisabled();

    refrigeratorPage.navigateToRefrigeratorTab();
    refrigeratorPage.clickShow();
    refrigeratorPage.verifyAllFieldsDisabled();


  }

  @AfterMethod(groups = {"offline"})
  public void tearDownNew() throws Exception {
    switchOnNetwork();
    testWebDriver.sleep(5000);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
    ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
  }

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"storeIncharge", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M", "Period", 14}
    };

  }
}