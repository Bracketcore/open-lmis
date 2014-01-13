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


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestBase.fail;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.util.Collections.addAll;
import static org.testng.AssertJUnit.assertFalse;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageDistribution extends TestCaseHelper {

  public static final String NONE_ASSIGNED = "--None Assigned--";
  public static final String SELECT_DELIVERY_ZONE = "--Select Delivery Zone--";
  public static final String periodDisplayedByDefault = "Period14";
  public static final String periodNotToBeDisplayedInDropDown = "Period1";
  public String userSIC, password, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
    deliveryZoneNameFirst, deliveryZoneNameSecond,
    facilityCodeFirst, facilityCodeSecond,
    programFirst, programSecond, schedule;
  private HashMap<String, DistributionTab> tabMap;
  String productGroupCode="PG1" ;

  @BeforeMethod(groups = "distribution")
  @Before
  public void setUp() throws Exception {
    super.setup();
    tabMap = new HashMap<String, DistributionTab>() {{
      put("epi use", new EPIUsePage(testWebDriver));
      put("general observation", new GeneralObservationPage(testWebDriver));
      put("coverage", new CoveragePage(testWebDriver));
      put("epi inventory", new EpiInventoryPage(testWebDriver));
    }};
  }

  @Given("^I have the following data for distribution:$")
  public void theFollowingDataExist(DataTable tableData) throws Exception {
    List<Map<String, String>> data = tableData.asMaps();
    for (Map map : data) {
      userSIC = map.get("userSIC").toString();
      deliveryZoneCodeFirst = map.get("deliveryZoneCodeFirst").toString();
      deliveryZoneCodeSecond = map.get("deliveryZoneCodeSecond").toString();
      deliveryZoneNameFirst = map.get("deliveryZoneNameFirst").toString();
      deliveryZoneNameSecond = map.get("deliveryZoneNameSecond").toString();
      facilityCodeFirst = map.get("facilityCodeFirst").toString();
      facilityCodeSecond = map.get("facilityCodeSecond").toString();
      programFirst = map.get("programFirst").toString();
      programSecond = map.get("programSecond").toString();
      schedule = map.get("schedule").toString();
    }

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true,
      programFirst, userSIC, "200", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
  }

  @And("^I update product \"([^\"]*)\" to have product group \"([^\"]*)\"$")
  public void setupProductAndProductGroup(String product, String productGroup) throws Exception {
    updateProductWithGroup(product, productGroup);
  }

  @And("^I disassociate \"([^\"]*)\" from delivery zone$")
  public void disassociateFacility(String facility) throws Exception {
    dbWrapper.deleteDeliveryZoneMembers(facility);
  }

  @When("^I Enter \"([^\"]*)\" values:$")
  public void enterValuesInForm(String tabName, DataTable tableData) {
    List<Map<String, String>> data = tableData.asMaps();
    tabMap.get(tabName).enterValues(data);
  }

  @When("^I verify saved \"([^\"]*)\" values:$")
  public void verifySavedEPIValues(String tabName, DataTable tableData) {
    testWebDriver.sleep(1000);
    new RefrigeratorPage(testWebDriver).navigateToRefrigeratorTab();
    DistributionTab tab = tabMap.get(tabName);
    tab.navigate();
    Map<String, String> data = tableData.asMaps().get(0);

    tab.verifyData(data);
  }

  @Then("^I should see program \"([^\"]*)\"$")
  public void verifyProgram(String programs) throws IOException, SQLException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    List<String> firstProgramValuesToBeVerified = new ArrayList<>();

    addAll(firstProgramValuesToBeVerified, programs.split(","));

    List<WebElement> valuesPresentInDropDown = distributionPage.getAllSelectOptionsFromProgram();
    verifyAllSelectFieldValues(firstProgramValuesToBeVerified, valuesPresentInDropDown);
  }

  @Then("^I verify fields$")
  public void verifyFieldsOnScreen() throws IOException, SQLException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    verifyElementsPresent(distributionPage);
  }

  @Then("^I should see period \"([^\"]*)\"$")
  public void verifyPeriod(String period) throws IOException, SQLException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    WebElement actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();
    testWebDriver.sleep(100);
    verifySelectedOptionFromSelectField(period, actualSelectFieldElement);
  }

  @Then("^I should see deliveryZone \"([^\"]*)\"$")
  public void verifyDeliveryZone(String deliveryZone) throws IOException, SQLException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    WebElement actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromDeliveryZone();
    verifySelectedOptionFromSelectField(deliveryZone, actualSelectFieldElement);
  }

  @Given("^I login as user \"([^\"]*)\" having password \"([^\"]*)\"$")
  public void login(String user, String password) throws IOException, SQLException {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    loginPage.loginAs(user, password);
  }

  @And("^I access plan my distribution page$")
  public void accessDistributionPage() throws IOException, SQLException {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateHomePage();
    homePage.navigateToDistributionWhenOnline();
  }

  @When("^I assign delivery zone \"([^\"]*)\" to user \"([^\"]*)\" having role \"([^\"]*)\"$")
  public void assignDeliveryZone(String deliveryZone, String user, String role) throws IOException, SQLException {
    dbWrapper.insertRoleAssignmentForDistribution(user, role, deliveryZone);
  }

  @When("^I select delivery zone \"([^\"]*)\"$")
  public void selectDeliveryZone(String deliveryZone) throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.selectValueFromDeliveryZone(deliveryZone);
  }

  @And("^I select program \"([^\"]*)\"$")
  public void selectProgram(String program) throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.selectValueFromProgram(program);
  }

  @And("^I select period \"([^\"]*)\"$")
  public void selectPeriod(String period) throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.selectValueFromPeriod(period);
  }

  @And("^I verify Distributions data is not synchronised$")
  public void verifyDistributionsInDB() throws IOException, SQLException {
    assertEquals(dbWrapper.getRowsCountFromDB("Distributions"), 1);
  }

  @And("^I initiate distribution$")
  public void initiateDistribution() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.clickInitiateDistribution();
  }

  @Then("^I see \"([^\"]*)\" facility icon as \"([^\"]*)\"$")
  public void verifyOverAllFacilityIndicator(String whichIcon, String color) throws IOException {
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.verifyFacilityIndicatorColor(whichIcon, color);
  }

  @When("^I record data for distribution \"([^\"]*)\"$")
  public void clickRecordDataForGivenRow(String rowNumber) throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.clickRecordData(Integer.parseInt(rowNumber));
  }

  @Then("^I should see No facility selected$")
  public void shouldSeeNoFacilitySelected() throws IOException {
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.verifyNoFacilitySelected();
  }

  @Then("^I navigate to general observations tab$")
  public void navigateToGeneralObservationsTab() throws IOException {
    GeneralObservationPage observation = new GeneralObservationPage(testWebDriver);
    observation.navigate();
  }

  @Then("^I navigate to refrigerator tab$")
  public void navigateToRefrigeratorTab() throws IOException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.navigateToRefrigeratorTab();
  }

  @Then("^I access show$")
  public void accessShow() throws IOException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickShowForRefrigerator1();
  }

  @Then("^I see general observations fields disabled$")
  public void verifyObservationFieldsDisabled() throws IOException {
    GeneralObservationPage observation = new GeneralObservationPage(testWebDriver);
    observation.verifyAllFieldsDisabled();
  }

  @Then("^I see refrigerator fields disabled$")
  public void verifyRefrigeratorFieldsDisabled() throws IOException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.verifyAllFieldsDisabled();
  }

  @Then("^I see epi fields disabled$")
  public void verifyEpiFieldsDisabled() throws IOException {
    EPIUsePage epiUse = new EPIUsePage(testWebDriver);
    epiUse.verifyAllFieldsDisabled();
  }

  @Then("^Verify \"([^\"]*)\" indicator should be \"([^\"]*)\"$")
  public void shouldVerifyIndicatorColor(String tabName, String color) throws IOException, SQLException {
    tabMap.get(tabName).verifyIndicator(color);
  }

  @And("^I should see \"([^\"]*)\" facilities that support the program \"([^\"]*)\" and delivery zone \"([^\"]*)\"$")
  public void shouldSeeNoFacilitySelected(String active, String program, String deliveryZone) throws IOException, SQLException {
    boolean activeFlag = false;
    if (active.equalsIgnoreCase("active"))
      activeFlag = true;
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    List<String> valuesToBeVerified = dbWrapper.getFacilityCodeNameForDeliveryZoneAndProgram(deliveryZone, program, activeFlag);
    List<WebElement> facilityList = facilityListPage.getAllFacilitiesFromDropDown();
    verifyAllSelectFieldValues(valuesToBeVerified, facilityList);
  }

  @When("^I choose facility \"([^\"]*)\"$")
  public void selectFacility(String facilityCode) throws IOException {
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(facilityCode);
  }

  @And("^I should see Delivery Zone \"([^\"]*)\", Program \"([^\"]*)\" and Period \"([^\"]*)\" in the header$")
  public void shouldVerifyHeaderElements(String deliveryZone, String program, String period) throws IOException, SQLException {
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.verifyHeaderElements(deliveryZone, program, period);
  }

  @And("^I click view load amount$")
  public void clickViewLoadAmount() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.clickViewLoadAmount();
  }

  @When("^I sync recorded data$")
  public void syncDistribution() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.syncDistribution(1);
  }

  @When("^I try to sync recorded data$")
  public void clickSyncLink() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.clickSyncDistribution(1);
  }

  @Then("^I verify sync message as \"([^\"]*)\"$")
  public void verifySyncMessage(String message) throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    assertEquals(message, distributionPage.getSyncAlertMessage());
  }

  @Then("^I check confirm sync message as \"([^\"]*)\"$")
  public void verifyConfirmSyncMessage(String message) throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    assertTrue("Incorrect Sync Facility", distributionPage.getSyncMessage().contains(message));
  }

  @When("^I done sync message$")
  public void doneSyncMessage() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.syncDistributionMessageDone();
  }

  @Then("^I view observations data in DB for facility \"([^\"]*)\":$")
  public void verifyObservationsDataInDB(String facility, DataTable tableData) throws SQLException {
    List<Map<String, String>> data = tableData.asMaps();
    for (Map map : data) {
      Map<String, String> facilityVisitDetails = dbWrapper.getFacilityVisitDetails(facility);
      assertEquals(facilityVisitDetails.get("observations"), map.get("observations"));
      assertEquals(facilityVisitDetails.get("confirmedByName"), map.get("confirmedByName"));
      assertEquals(facilityVisitDetails.get("confirmedByTitle"), map.get("confirmedByTitle"));
      assertEquals(facilityVisitDetails.get("verifiedByName"), map.get("verifiedByName"));
      assertEquals(facilityVisitDetails.get("verifiedByTitle"), map.get("verifiedByTitle"));
    }
  }

  @Then("^I view epi use data in DB for facility \"([^\"]*)\" and product group \"([^\"]*)\":$")
  public void verifyEpiUseDataInDB(String facilityCode, String productGroupCode, DataTable tableData) throws SQLException {
    List<Map<String, String>> data = tableData.asMaps();
    Map<String, String> epiDetails = dbWrapper.getEpiUseDetails(productGroupCode, facilityCode);
    for (Map map : data) {
      assertEquals(map.get("firstOfMonth").toString(), epiDetails.get("stockatfirstofmonth"));
      assertEquals(map.get("received").toString(), epiDetails.get("received"));
      assertEquals(map.get("distributed").toString(), epiDetails.get("distributed"));
      assertEquals(map.get("loss").toString(), epiDetails.get("loss"));
      assertEquals(map.get("endOfMonth").toString(), epiDetails.get("stockatendofmonth"));
      assertEquals(map.get("expirationDate").toString(), epiDetails.get("expirationdate"));
    }
  }

  @And("^I view refrigerator readings in DB for refrigerator serial number \"([^\"]*)\" and facility \"([^\"]*)\":$")
  public void verifyRefrigeratorReadingDataInDB(String refrigeratorSerialNumber, String facilityCode, DataTable tableData) throws SQLException {
    List<Map<String, String>> data = tableData.asMaps();
    ResultSet resultSet = dbWrapper.getRefrigeratorReadings(refrigeratorSerialNumber, facilityCode);
    for (Map map : data) {
      assertEquals(map.get("temperature"), resultSet.getString("temperature"));
      assertEquals(map.get("functioningCorrectly"), resultSet.getString("functioningCorrectly"));
      assertEquals(map.get("lowAlarmEvents"), resultSet.getString("lowAlarmEvents"));
      assertEquals(map.get("highAlarmEvents"), resultSet.getString("highAlarmEvents"));
      assertEquals(map.get("problemSinceLastTime"), resultSet.getString("problemSinceLastTime"));
      String notes = (String) map.get("notes");
      if (notes.equals("null")) {
        notes = null;
      }
      assertEquals(notes, resultSet.getString("notes"));
    }
  }

  @Then("^I verify no record present in refrigerator problem table for refrigerator serial number \"([^\"]*)\" and facility \"([^\"]*)\"$")
  public void verifyNoRecordAddedToRefrigeratorProblemsTable(String refrigeratorSerialNumber, String facilityCode) throws SQLException {
    verifyRefrigeratorProblemDataNullInDatabase(refrigeratorSerialNumber, facilityCode);
  }

  @Then("^I should see data download successfully$")
  public void seeDownloadSuccessfully() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById("cachedDistributions"));
    distributionPage.verifyDownloadSuccessFullMessage(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
  }

  @Then("^I should verify facility name \"([^\"]*)\" in the header$")
  public void verifyFacilityNameInHeader(String facilityName) throws IOException {
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.verifyFacilityNameInHeader(facilityName);
  }

  @Then("^I should verify facility zone \"([^\"]*)\" in the header$")
  public void verifyFacilityZoneInHeader(String facilityZone) throws IOException {
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.verifyFacilityZoneInHeader(facilityZone);
  }

  @Then("^I verify legends$")
  public void verifyFacilityZoneInHeader() throws IOException {
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.verifyLegend();
  }

  @And("^I should see delivery zone \"([^\"]*)\" program \"([^\"]*)\" period \"([^\"]*)\" in table$")
  public void verifyTableValue(String deliveryZoneNameFirst, String programFirst, String periodDisplayedByDefault) throws IOException {
    verifyElementsInTable(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
  }

  @And("^I remove cached distribution$")
  public void deleteDistribution() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.deleteDistribution();
  }

  @And("^I observe confirm delete distribution dialog$")
  public void verifyDeleteDistributionConfirmation() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.verifyDeleteConfirmMessageAndHeader();
  }

  @And("I cancel delete distribution$")
  public void cancelDeleteDistributionConfirmation() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.CancelDeleteDistribution();
  }

  @And("I confirm delete distribution$")
  public void confirmDeleteDistributionConfirmation() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.clickOk();
  }

  @Then("I see no distribution in cache$")
  public void noDistributionInCache() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.verifyNoDistributionCachedMessage();
  }

  @When("I enter EPI Inventory deliveredQuantity of Row \"([^\"]*)\" as \"([^\"]*)\"$")
  public void enterDeliveredQuantity(Integer rowNumber, String deliveredQuantity) throws IOException {
   EpiInventoryPage epiInventoryPage=new EpiInventoryPage(testWebDriver);
   epiInventoryPage.fillDeliveredQuantity(rowNumber,deliveredQuantity);
  }

  @When("I enter coverage maleMobileBrigade as \"([^\"]*)\"$")
  public void enterCoverageMaleMobileBrigade(String maleMobileBrigade) throws IOException {
   CoveragePage coveragePage=new CoveragePage(testWebDriver);
   coveragePage.enterMaleMobileBrigade(maleMobileBrigade);
  }
  private void verifyElementsInTable(String deliveryZoneNameFirst, String programFirst, String periodDisplayedByDefault) {
    SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]/" +
      "div[1]/div[1]/div").getText(), deliveryZoneNameFirst);

    SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[2]").getText(), programFirst);

    SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[3]").getText(), periodDisplayedByDefault);

    SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[4]").getText(), "INITIATED");

    SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[5]/a").getText(), "Record Data");

    SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[6]/a").getText(), "Sync");
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testVerifyAlreadyCachedDistribution(String userSIC, String password, String deliveryZoneCodeFirst,
                                                  String deliveryZoneCodeSecond,
                                                  String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                  String facilityCodeFirst, String facilityCodeSecond,
                                                  String programFirst, String programSecond, String schedule,
                                                  String period, Integer totalNumberOfPeriods) throws Exception {

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", rightsList,
      programSecond, "District1", "Ngorongoro", "Ngorongoro");

    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    distributionPage.verifyDownloadSuccessFullMessage(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
    distributionPage.clickInitiateDistribution();
    distributionPage.verifyDataAlreadyCachedMessage(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testManageDistribution(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                     String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                     String facilityCodeFirst, String facilityCodeSecond,
                                     String programFirst, String programSecond, String schedule, String period,
                                     Integer totalNumberOfPeriods) throws Exception {

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200",
      rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");

    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    verifyElementsPresent(distributionPage);

    String defaultDistributionZoneValuesToBeVerified = NONE_ASSIGNED;
    WebElement actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromDeliveryZone();
    verifySelectedOptionFromSelectField(defaultDistributionZoneValuesToBeVerified, actualSelectFieldElement);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);

    homePage.navigateHomePage();
    homePage.navigateToDistributionWhenOnline();

    List<String> distributionZoneValuesToBeVerified = new ArrayList<>();
    distributionZoneValuesToBeVerified.add(deliveryZoneNameFirst);
    distributionZoneValuesToBeVerified.add(deliveryZoneNameSecond);
    List<WebElement> valuesPresentInDropDown = distributionPage.getAllSelectOptionsFromDeliveryZone();
    verifyAllSelectFieldValues(distributionZoneValuesToBeVerified, valuesPresentInDropDown);

    String defaultProgramValuesToBeVerified = NONE_ASSIGNED;
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromProgram();
    verifySelectedOptionFromSelectField(defaultProgramValuesToBeVerified, actualSelectFieldElement);

    String defaultPeriodValuesToBeVerified = NONE_ASSIGNED;
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();
    verifySelectedOptionFromSelectField(defaultPeriodValuesToBeVerified, actualSelectFieldElement);


    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    List<String> firstProgramValuesToBeVerified = new ArrayList<>();
    firstProgramValuesToBeVerified.add(programFirst);
    firstProgramValuesToBeVerified.add(programSecond);
    valuesPresentInDropDown = distributionPage.getAllSelectOptionsFromProgram();
    verifyAllSelectFieldValues(firstProgramValuesToBeVerified, valuesPresentInDropDown);
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();
    verifySelectedOptionFromSelectField(defaultPeriodValuesToBeVerified, actualSelectFieldElement);


    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameSecond);
    List<String> secondProgramValuesToBeVerified = new ArrayList<>();
    secondProgramValuesToBeVerified.add(programSecond);
    valuesPresentInDropDown = distributionPage.getAllSelectOptionsFromProgram();
    verifyAllSelectFieldValues(secondProgramValuesToBeVerified, valuesPresentInDropDown);
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();
    verifySelectedOptionFromSelectField(defaultPeriodValuesToBeVerified, actualSelectFieldElement);


    distributionPage.selectValueFromProgram(programSecond);
    List<String> periodValuesToBeVerified = new ArrayList<>();
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();

    verifySelectedOptionFromSelectField(periodDisplayedByDefault, actualSelectFieldElement);
    for (int counter = 2; counter <= totalNumberOfPeriods; counter++) {
      String periodWithCounter = period + counter;
      periodValuesToBeVerified.add(periodWithCounter);
    }
    valuesPresentInDropDown = distributionPage.getAllSelectOptionsFromPeriod();
    verifyAllSelectFieldValues(periodValuesToBeVerified, valuesPresentInDropDown);
    verifySelectFieldValueNotPresent(periodNotToBeDisplayedInDropDown, valuesPresentInDropDown);

    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);

    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromDeliveryZone();
    verifySelectedOptionFromSelectField(deliveryZoneNameSecond, actualSelectFieldElement);
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromProgram();
    verifySelectedOptionFromSelectField(programSecond, actualSelectFieldElement);
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();
    verifySelectedOptionFromSelectField(periodDisplayedByDefault, actualSelectFieldElement);

    distributionPage.selectValueFromDeliveryZone(SELECT_DELIVERY_ZONE);
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromProgram();
    verifySelectedOptionFromSelectField(defaultProgramValuesToBeVerified, actualSelectFieldElement);
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();
    verifySelectedOptionFromSelectField(defaultPeriodValuesToBeVerified, actualSelectFieldElement);
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testVerifyNoFacilityToBeShownIfNotMappedWithDeliveryZone(String userSIC, String password,
                                                                       String deliveryZoneCodeFirst,
                                                                       String deliveryZoneCodeSecond,
                                                                       String deliveryZoneNameFirst,
                                                                       String deliveryZoneNameSecond,
                                                                       String facilityCodeFirst,
                                                                       String facilityCodeSecond,
                                                                       String programFirst, String programSecond,
                                                                       String schedule, String period,
                                                                       Integer totalNumberOfPeriods) throws Exception {

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", rightsList,
      programSecond, "District1", "Ngorongoro", "Ngorongoro");

    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.deleteDeliveryZoneToFacilityMapping(deliveryZoneNameFirst);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(period + totalNumberOfPeriods);
    distributionPage.clickInitiateDistribution();
    distributionPage.verifyFacilityNotSupportedMessage(programFirst, deliveryZoneNameFirst);
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testVerifyNoFacilityToBeShownIfNotMappedWithPrograms(String userSIC, String password,
                                                                   String deliveryZoneCodeFirst,
                                                                   String deliveryZoneCodeSecond,
                                                                   String deliveryZoneNameFirst,
                                                                   String deliveryZoneNameSecond,
                                                                   String facilityCodeFirst, String facilityCodeSecond,
                                                                   String programFirst, String programSecond,
                                                                   String schedule, String period,
                                                                   Integer totalNumberOfPeriods) throws Exception {

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", rightsList,
      programSecond, "District1", "Ngorongoro", "Ngorongoro");

    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.deleteProgramToFacilityMapping(programFirst);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(period + totalNumberOfPeriods);
    distributionPage.clickInitiateDistribution();
    distributionPage.verifyFacilityNotSupportedMessage(programFirst, deliveryZoneNameFirst);
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testVerifyNoFacilityToBeShownIfInactive(String userSIC, String password, String deliveryZoneCodeFirst,
                                                      String deliveryZoneCodeSecond,
                                                      String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                      String facilityCodeFirst, String facilityCodeSecond,
                                                      String programFirst, String programSecond, String schedule,
                                                      String period, Integer totalNumberOfPeriods) throws Exception {

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", rightsList,
      programSecond, "District1", "Ngorongoro", "Ngorongoro");

    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.updateActiveStatusOfFacility(facilityCodeFirst, "false");
    dbWrapper.updateActiveStatusOfFacility(facilityCodeSecond, "false");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(period + totalNumberOfPeriods);
    distributionPage.clickInitiateDistribution();
    distributionPage.verifyFacilityNotSupportedMessage(programFirst, deliveryZoneNameFirst);
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testVerifyGeoZonesOrderOnFacilityListPage(String userSIC, String password, String deliveryZoneCodeFirst,
                                                        String deliveryZoneCodeSecond,
                                                        String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                        String facilityCodeFirst, String facilityCodeSecond,
                                                        String programFirst, String programSecond, String schedule,
                                                        String period, Integer totalNumberOfPeriods) throws Exception {

    String geoZoneFirst = "District1";
    String geoZoneSecond = "Ngorongoro";
    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", rightsList,
      programSecond, geoZoneFirst, geoZoneSecond, geoZoneSecond);

    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(period + totalNumberOfPeriods);
    distributionPage.clickInitiateDistribution();
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    facilityListPage.clickFacilityListDropDown();
    facilityListPage.verifyGeographicZoneOrder(geoZoneFirst, geoZoneSecond);


  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testVerifyOnlyActiveProductsDisplayedOnViewLoadAmountScreenDistribution(String userSIC, String password, String deliveryZoneCodeFirst,
                                                  String deliveryZoneCodeSecond,
                                                  String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                  String facilityCodeFirst, String facilityCodeSecond,
                                                  String programFirst, String programSecond, String schedule,
                                                  String period, Integer totalNumberOfPeriods) throws Exception {

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", rightsList,
      programSecond, "District1", "Ngorongoro", "Ngorongoro");

    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProductGroup(productGroupCode);
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", productGroupCode, true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", productGroupCode, true);
    dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
    dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");
    dbWrapper.updateActiveStatusOfProduct("Product6","false");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickViewLoadAmount();

    verifyInactiveProductsNotDisplayedOnViewLoadAmount();


  }

  private void verifyElementsPresent(DistributionPage distributionPage) {
    assertTrue("selectDeliveryZoneSelectBox should be present", distributionPage.IsDisplayedSelectDeliveryZoneSelectBox());
    assertTrue("selectProgramSelectBox should be present", distributionPage.IsDisplayedSelectProgramSelectBox());
    assertTrue("selectPeriodSelectBox should be present", distributionPage.IsDisplayedSelectPeriodSelectBox());
    assertTrue("proceedButton should be present", distributionPage.IsDisplayedViewLoadAmountButton());
  }

  private void verifyAllSelectFieldValues(List<String> valuesToBeVerified, List<WebElement> valuesPresentInDropDown) {
    String collectionOfValuesPresentINDropDown = "";
    int valuesToBeVerifiedCounter = valuesToBeVerified.size();
    int valuesInSelectFieldCounter = valuesPresentInDropDown.size();

    if (valuesToBeVerifiedCounter == valuesInSelectFieldCounter - 1) {
      for (WebElement webElement : valuesPresentInDropDown) {
        collectionOfValuesPresentINDropDown = collectionOfValuesPresentINDropDown + webElement.getText().trim();
      }
      for (String values : valuesToBeVerified) {
        assertTrue(collectionOfValuesPresentINDropDown.contains(values));
      }
    } else {
      fail("Values in select field are not same in number as values to be verified");
    }

  }

  private void verifySelectFieldValueNotPresent(String valueToBeVerified, List<WebElement> valuesPresentInDropDown) {
    boolean flag = false;
    for (WebElement webElement : valuesPresentInDropDown) {
      if (valueToBeVerified.equalsIgnoreCase(webElement.getText().trim())) {
        flag = true;
        break;
      }
    }
    assertTrue(valueToBeVerified + " should not exist in period drop down", !flag);
  }

  private void verifySelectedOptionFromSelectField(String valuesToBeVerified, WebElement actualSelectFieldElement) {
    testWebDriver.sleep(200);
    testWebDriver.waitForElementToAppear(actualSelectFieldElement);
    assertEquals(valuesToBeVerified, actualSelectFieldElement.getText());
  }

  public void verifyInactiveProductsNotDisplayedOnViewLoadAmount() throws IOException {
    WarehouseLoadAmountPage warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    assertFalse(warehouseLoadAmountPage.getAggregateTableData().contains("ProductName6"));
    assertFalse(warehouseLoadAmountPage.getTable1Data().contains("ProductName6"));

    assertFalse(warehouseLoadAmountPage.getAggregateTableData().contains("ProductName5"));
    assertFalse(warehouseLoadAmountPage.getTable1Data().contains("ProductName5"));

    assertFalse(warehouseLoadAmountPage.getAggregateTableData().contains("PG1-Name"));
    assertFalse(warehouseLoadAmountPage.getTable1Data().contains("PG1-Name"));
  }

  @And("^Navigate to Coverage tab$")
  public void navigateToCoverageTab() throws Throwable {
    new CoveragePage(testWebDriver).navigateToCoverage();
  }

  @And("^I navigate to epi inventory tab$")
  public void navigateToEpiInventoryTab() {
    new EpiInventoryPage(testWebDriver).navigateToEpiInventory();
  }

  @AfterMethod(groups = "distribution")
  @After
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
    ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis')");
  }

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"storeIncharge", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M", "Period", 14}
    };
  }
}

