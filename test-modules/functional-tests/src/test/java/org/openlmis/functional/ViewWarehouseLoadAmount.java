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
import org.openlmis.pageobjects.DistributionPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.WarehouseLoadAmountPage;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ViewWarehouseLoadAmount extends TestCaseHelper {

  public static final String periodDisplayedByDefault = "Period14";
  public static final String district1 = "District1";
  public static final String district2 = "District2";
  public static final String parentGeoZone = "Dodoma";
  public static final String parentGeoZone1 = "Arusha";
  public String userSIC = "fieldcoordinator";
  public String deliveryZoneCodeFirst = "DZ1";
  public String deliveryZoneCodeSecond = "DZ2";
  public String deliveryZoneNameFirst = "Delivery Zone First";
  public String deliveryZoneNameSecond = "Delivery Zone Second";
  public String facilityCodeFirst = "F10";
  public String facilityCodeSecond = "F11";
  public String programFirst = "VACCINES";
  public String programSecond = "TB";
  public String schedule = "M";
  public String product = "P10";
  public String product2 = "P11";

  @BeforeMethod(groups = "distribution")
  @Before
  public void setUp() throws Exception {
    super.setup();
  }

  @Given("^I have data available for distribution load amount$")
  public void setupDataForDistributionLoadAmount() throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true,
      programFirst, userSIC, "200", rightsList, programSecond, district1, district1, parentGeoZone);

  }

  @And("^I have data available for \"([^\"]*)\" (facility|facilities) attached to delivery zones$")
  public void setupDataForMultipleDeliveryZones(String facilityInstances) throws Exception {
    if (facilityInstances.equalsIgnoreCase("Multiple")) {
      setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
        deliveryZoneNameFirst, deliveryZoneNameSecond,
        facilityCodeFirst, facilityCodeSecond,
        programFirst, programSecond, schedule);
    } else if (facilityInstances.equalsIgnoreCase("Single")) {
      setupDataForDeliveryZone(false, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
        deliveryZoneNameFirst, deliveryZoneNameSecond,
        facilityCodeFirst, facilityCodeSecond,
        programFirst, programSecond, schedule);
    }
  }

  @And("^I update population of facility \"([^\"]*)\" as \"([^\"]*)\"$")
  public void updatePopulationOfFacility(String facilityCode, String population) throws IOException, SQLException {
    dbWrapper.updatePopulationOfFacility(facilityCode, population);
  }

  @And("^I have role assigned to delivery zones$")
  public void setupRoleAssignmentForMultipleDeliveryZones() throws Exception {
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
  }

  @And("^I have following ISA values:$")
  public void setProgramProductISA(DataTable tableData) throws Exception {
    for (Map<String, String> map : tableData.asMaps()) {
      dbWrapper.insertProgramProductISA(map.get("Program"), map.get("Product"), map.get("whoRatio"),
        map.get("dosesPerYear"), map.get("wastageFactor"), map.get("bufferPercentage"), map.get("minimumValue"),
        map.get("maximumValue"), map.get("adjustmentValue"));
    }
  }

  @And("^I have following override ISA values:$")
  public void setOverrideISA(DataTable tableData) throws Exception {
    for (Map<String, String> map : tableData.asMaps()) {
      dbWrapper.InsertOverriddenIsa(map.get("Facility Code"), map.get("Program"),
        map.get("Product"), Integer.parseInt(map.get("ISA")));
    }
  }

  @When("^I click load amount$")
  public void clickDistributionLoadAmount() throws Exception {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.clickViewLoadAmount();
  }

  @Then("^I should see aggregate ISA values as per multiple facilities in one delivery zone$")
  public void verifyISAAndOverrideISAValuesAggregatedForMultipleFacilities() throws Exception {
    WarehouseLoadAmountPage warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 1))), warehouseLoadAmountPage.getFacilityPopulation(3, 1));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct1Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct1Isa(2, 1))), warehouseLoadAmountPage.getProduct1Isa(3, 1));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 1))), warehouseLoadAmountPage.getProduct2Isa(3, 1));

    assertEquals(warehouseLoadAmountPage.getAggregatePopulation(1), String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 1))));
    assertEquals(warehouseLoadAmountPage.getAggregateProduct1Isa(1), String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct1Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct1Isa(2, 1))));
    assertEquals(warehouseLoadAmountPage.getAggregateProduct2Isa(1), String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 1))));

    assertEquals(warehouseLoadAmountPage.getAggregateProduct2Isa(1), warehouseLoadAmountPage.getAggregateProduct2Isa(2));
    assertEquals(warehouseLoadAmountPage.getAggregateProduct1Isa(1), warehouseLoadAmountPage.getAggregateProduct1Isa(2));
    assertEquals(warehouseLoadAmountPage.getAggregatePopulation(1), warehouseLoadAmountPage.getAggregatePopulation(2));

    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 1))), warehouseLoadAmountPage.getAggregatePopulation(2));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct1Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct1Isa(2, 1))), warehouseLoadAmountPage.getAggregateProduct1Isa(2));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 1))), warehouseLoadAmountPage.getAggregateProduct2Isa(2));


  }

  @Then("^I should see ISA values as per delivery zone facilities$")
  public void verifyISAAndOverrideISA() throws Exception {
    WarehouseLoadAmountPage warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    assertEquals(facilityCodeSecond, warehouseLoadAmountPage.getFacilityCode(1, 1));
    assertEquals(dbWrapper.getFacilityName(facilityCodeSecond), warehouseLoadAmountPage.getFacilityName(1, 1));
    assertEquals(dbWrapper.getFacilityPopulation(facilityCodeSecond), warehouseLoadAmountPage.getFacilityPopulation(1, 1));

    assertEquals(getISAForProgramProduct(programFirst, product, warehouseLoadAmountPage.getFacilityPopulation(1, 1)), warehouseLoadAmountPage.getProduct1Isa(1, 1));
    assertEquals(dbWrapper.getOverriddenIsa(facilityCodeSecond, programFirst, product2, periodDisplayedByDefault), warehouseLoadAmountPage.getProduct2Isa(1, 1));

  }

  @And("^I verify ISA values for Product1 as:$")
  public void verifyISAForProduct1(DataTable dataTable) throws IOException {
    WarehouseLoadAmountPage warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    List<Map<String, String>> facilityProductISAMaps = dataTable.asMaps();
    for (Map<String, String> facilityProductISAMap : facilityProductISAMaps) {
      assertEquals(facilityProductISAMap.get("Facility1"), warehouseLoadAmountPage.getProduct1Isa(1, 1));
      assertEquals(facilityProductISAMap.get("Facility2"), warehouseLoadAmountPage.getProduct1Isa(2, 1));
    }
  }

  @And("^I verify ISA values for Product2 as:$")
  public void verifyISAForProduct2(DataTable dataTable) throws IOException {
    WarehouseLoadAmountPage warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    List<Map<String, String>> facilityProductISAMaps = dataTable.asMaps();
    for (Map<String, String> facilityProductISAMap : facilityProductISAMaps) {
      assertEquals(facilityProductISAMap.get("Facility1"), warehouseLoadAmountPage.getProduct2Isa(1, 1));
      assertEquals(facilityProductISAMap.get("Facility2"), warehouseLoadAmountPage.getProduct2Isa(2, 1));
    }
  }

  @Then("^I should see message \"([^\"]*)\"$")
  public void verifyNoRecordFoundMessage(String message) throws Exception {
    WarehouseLoadAmountPage warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    assertEquals(message, warehouseLoadAmountPage.getNoRecordFoundMessage());
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function-Multiple-Facilities")
  public void testShouldVerifyISAForDeliveryZoneNegativeScenarios(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                                  String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                                  String facilityCodeFirst, String facilityCodeSecond, String facilityCodeThird, String facilityCodeFourth,
                                                                  String programFirst, String programSecond, String schedule, String product1, String product2) throws Exception {

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst,
      userSIC, "200", rightsList, programSecond, district1, district1, parentGeoZone1);

    setupDataForDeliveryZone(false, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    addOnDataSetupForDeliveryZoneForMultipleFacilitiesAttachedWithSingleDeliveryZone(deliveryZoneCodeFirst,
      facilityCodeThird, facilityCodeFourth, district2, district2, parentGeoZone);

    dbWrapper.updateProductsByField("packSize", "4", product1);
    dbWrapper.updateProductsByField("packSize", "5", product2);
    dbWrapper.updateProcessingPeriodByField("numberOfMonths", "2", periodDisplayedByDefault, schedule);


    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.InsertOverriddenIsa(facilityCodeFirst, programFirst, product1, 1000);
    dbWrapper.InsertOverriddenIsa(facilityCodeFirst, programFirst, product2, 9999999);
    dbWrapper.InsertOverriddenIsa(facilityCodeSecond, programFirst, product1, 3000);
    dbWrapper.InsertOverriddenIsa(facilityCodeSecond, programFirst, product2, 888888);
    dbWrapper.InsertOverriddenIsa(facilityCodeThird, programFirst, product1, 51);
    dbWrapper.InsertOverriddenIsa(facilityCodeThird, programFirst, product2, 51);
    dbWrapper.InsertOverriddenIsa(facilityCodeFourth, programFirst, product2, 57);
    dbWrapper.updatePopulationOfFacility(facilityCodeFirst, null);
    dbWrapper.updateOverriddenIsa(facilityCodeFirst, programFirst, product1, null);
    dbWrapper.updateOverriddenIsa(facilityCodeSecond, programFirst, product1, null);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();

    WarehouseLoadAmountPage warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    assertEquals(warehouseLoadAmountPage.getFacilityPopulation(1, 1), warehouseLoadAmountPage.getFacilityPopulation(3, 1));
    assertEquals("--", warehouseLoadAmountPage.getProduct1Isa(3, 1));
    assertEquals("--", warehouseLoadAmountPage.getProduct1Isa(2, 1));
    assertEquals("--", warehouseLoadAmountPage.getProduct1Isa(1, 1));
    assertEquals("4355555", warehouseLoadAmountPage.getProduct2Isa(3, 1));
    assertEquals("4000000", warehouseLoadAmountPage.getProduct2Isa(2, 1));
    assertEquals("355555", warehouseLoadAmountPage.getProduct2Isa(1, 1));
    assertEquals("--", warehouseLoadAmountPage.getFacilityPopulation(2, 1));
    assertEquals("333", warehouseLoadAmountPage.getFacilityPopulation(1, 1));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 1))), warehouseLoadAmountPage.getProduct2Isa(3, 1));

    assertEquals(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 2)) + Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 2)), warehouseLoadAmountPage.getFacilityPopulation(3, 2));
    assertEquals("--", warehouseLoadAmountPage.getProduct1Isa(1, 2));
    assertEquals("23", warehouseLoadAmountPage.getProduct2Isa(1, 2));
    assertEquals("20", warehouseLoadAmountPage.getProduct2Isa(2, 2));
    assertEquals("26", warehouseLoadAmountPage.getProduct1Isa(2, 2));
    assertEquals(warehouseLoadAmountPage.getProduct1Isa(2, 2), warehouseLoadAmountPage.getProduct1Isa(3, 2));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(1, 2)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 2))), warehouseLoadAmountPage.getProduct2Isa(3, 2));

    assertEquals(warehouseLoadAmountPage.getAggregatePopulation(3), String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 2)) + Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 2))));
    assertEquals(warehouseLoadAmountPage.getAggregateProduct2Isa(3), String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 2)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(1, 2))));
    assertEquals(warehouseLoadAmountPage.getAggregateProduct1Isa(3), warehouseLoadAmountPage.getProduct1Isa(2, 2));
    assertEquals("--", warehouseLoadAmountPage.getAggregateProduct1Isa(1));

    verifyCaptionsAndLabels(deliveryZoneNameFirst, warehouseLoadAmountPage);
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function-Multiple-GeoZones")
  public void testShouldVerifyISAForGeographicZones(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                    String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                    String facilityCodeFirst, String facilityCodeSecond,
                                                    String programFirst, String programSecond, String schedule, String product,
                                                    String product2, String geoZone1, String geoZone2) throws Exception {

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, "200", rightsList, programSecond, district1, parentGeoZone, parentGeoZone);
    setupDataForDeliveryZone(false, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.InsertOverriddenIsa(facilityCodeFirst, programFirst, product, 1000);
    dbWrapper.InsertOverriddenIsa(facilityCodeFirst, programFirst, product2, 2000);
    dbWrapper.InsertOverriddenIsa(facilityCodeSecond, programFirst, product, 3000);
    dbWrapper.InsertOverriddenIsa(facilityCodeSecond, programFirst, product2, 0);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();

    WarehouseLoadAmountPage warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);

    verifyWarehouseLoadAmountHeader(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
    assertEquals(facilityCodeSecond, warehouseLoadAmountPage.getFacilityCode(1, 1));
    assertEquals("Central Hospital", warehouseLoadAmountPage.getFacilityName(1, 1));
    assertEquals("333", warehouseLoadAmountPage.getFacilityPopulation(1, 1));
    assertEquals("300", warehouseLoadAmountPage.getProduct1Isa(1, 1));
    assertEquals("0", warehouseLoadAmountPage.getProduct2Isa(1, 1));

    assertEquals(facilityCodeFirst, warehouseLoadAmountPage.getFacilityCode(1, 2));
    assertEquals("Village Dispensary", warehouseLoadAmountPage.getFacilityName(1, 2));
    assertEquals("333", warehouseLoadAmountPage.getFacilityPopulation(1, 2));
    assertEquals("100", warehouseLoadAmountPage.getProduct1Isa(1, 2));
    assertEquals("200", warehouseLoadAmountPage.getProduct2Isa(1, 2));

    dbWrapper.updatePopulationOfFacility(facilityCodeFirst, null);
    dbWrapper.updateOverriddenIsa(facilityCodeFirst, programFirst, product, null);
    homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();
    assertEquals("--", warehouseLoadAmountPage.getFacilityPopulation(1, 2));
    assertEquals("--", warehouseLoadAmountPage.getProduct1Isa(1, 2));

  }

  private void verifyWarehouseLoadAmountHeader(String deliverZone, String program, String period) {
    WebElement deliverZoneElement = testWebDriver.getElementByXpath("(//div[2]/div/div/div/span)[1]");
    WebElement programElement = testWebDriver.getElementByXpath("(//div[2]/div/div/div/span)[2]");
    WebElement periodElement = testWebDriver.getElementByXpath("(//div[2]/div/div/div/span)[3]");
    testWebDriver.waitForElementToAppear(deliverZoneElement);
    SeleneseTestNgHelper.assertEquals(deliverZoneElement.getText(), deliverZone);
    SeleneseTestNgHelper.assertEquals(programElement.getText(), program);
    SeleneseTestNgHelper.assertEquals(periodElement.getText(), period);
  }

  private void verifyCaptionsAndLabels(String deliveryZoneNameFirst, WarehouseLoadAmountPage warehouseLoadAmountPage) {
    assertEquals(warehouseLoadAmountPage.getGeoZoneNameTitle(1), testWebDriver.getElementByXpath("//h3/span[contains(text(),'" + district1 + "')]").getText());
    assertEquals(warehouseLoadAmountPage.getGeoZoneNameTitle(2), testWebDriver.getElementByXpath("//h3/span[contains(text(),'" + district2 + "')]").getText());
    assertEquals(deliveryZoneNameFirst, testWebDriver.getElementByXpath("//h3/span[contains(text(),'" + deliveryZoneNameFirst + "')]").getText());
    assertEquals("Zone Total", warehouseLoadAmountPage.getAggregateTableCaption());
    assertEquals(district1, warehouseLoadAmountPage.getGeoZoneTotalCaption(1));
    assertEquals(district2, warehouseLoadAmountPage.getGeoZoneTotalCaption(2));
    assertEquals(district1, warehouseLoadAmountPage.getCitiesFromAggregatedTable(1));
    assertEquals(district2, warehouseLoadAmountPage.getCitiesFromAggregatedTable(2));

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
      ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
    }

  }

  @DataProvider(name = "Data-Provider-Function-Multiple-Facilities")
  public Object[][] parameterIntTestProvider() {
    return new Object[][]{
      {"fieldCoordinator", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "F12", "F13", "VACCINES", "TB", "M", "P10", "P11"}
    };

  }

  @DataProvider(name = "Data-Provider-Function-Multiple-GeoZones")
  public Object[][] parameterIntTestProviderMultipleGeoZones() {
    return new Object[][]{
      {"fieldCoordinator", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M", "P10", "P11", "District", "Total"}
    };

  }

}

