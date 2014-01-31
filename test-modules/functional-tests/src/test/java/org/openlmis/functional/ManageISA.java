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


import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ManageFacilityPage;
import org.testng.annotations.*;

import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;
import static org.openlmis.pageobjects.ManageFacilityPage.saveButton;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageISA extends TestCaseHelper {

  private static String geoZone = "Ngorongoro";
  private static String facilityType = "Lvl3 Hospital";
  private static String operatedBy = "MoH";
  private static String facilityCodePrefix = "FCcode";
  private static String facilityNamePrefix = "FCname";
  public String user, program, product, productName, category, whoRatio, dosesPerYear, wastageFactor, bufferPercentage, minimumValue,
    maximumValue, adjustmentValue, date_time;
  static ManageFacilityPage manageFacilityPage;

  @BeforeMethod(groups = "admin")
  public void setUp() throws Exception {
    super.setup();
  }

  @Given("^I have the following data for override ISA:$")
  public void theFollowingDataExist(DataTable tableData) throws Exception {
    List<Map<String, String>> data = tableData.asMaps();
    for (Map map : data) {
      user = map.get("user").toString();
      program = map.get("program").toString();
      product = map.get("product").toString();
      productName = map.get("productName").toString();
      category = map.get("category").toString();
      whoRatio = map.get("whoRatio").toString();
      dosesPerYear = map.get("dosesPerYear").toString();
      wastageFactor = map.get("wastageFactor").toString();
      bufferPercentage = map.get("bufferPercentage").toString();
      minimumValue = map.get("minimumValue").toString();
      maximumValue = map.get("maximumValue").toString();
      adjustmentValue = map.get("adjustmentValue").toString();

      setupProgramProductTestDataWithCategories(product, productName, category, program);
      setupProgramProductISA(program, product, whoRatio, dosesPerYear, wastageFactor, bufferPercentage, minimumValue, maximumValue, adjustmentValue);
    }
  }

  @When("^I create facility$")
  public void createFacility() throws Exception {
    manageFacilityPage = PageFactory.getInstanceOfManageFacilityPage(testWebDriver);
    date_time = ManageFacilityPage.getInstance(testWebDriver).enterValuesInFacility(facilityCodePrefix, facilityNamePrefix,
      program, geoZone, facilityType, operatedBy, valueOf(333), true);
  }

  @And("^I override ISA \"([^\"]*)\"$")
  public void overrideISA(String isaValue) throws Exception {
    PageFactory.getInstanceOfManageFacilityPage(testWebDriver).overrideIsa(isaValue, 1);
  }

  @Then("^I should see calculated ISA \"([^\"]*)\"$")
  public void verifyCalculatedISA(String isaValue) throws Exception {
    PageFactory.getInstanceOfManageFacilityPage(testWebDriver).verifyCalculatedIsa(Integer.parseInt(isaValue));
  }

  @When("^I click ISA done$")
  public void clickISADone() throws Exception {
    PageFactory.getInstanceOfManageFacilityPage(testWebDriver).clickIsaDoneButton();
  }

  @When("^I save facility$")
  public void clickSave() throws Exception {
    saveButton.click();
  }

  @Then("^I should see save successfully$")
  public void verifySaveSuccessfully() throws Exception {
    ManageFacilityPage.getInstance(testWebDriver).verifySuccessMessage();
  }

  @When("^I search facility$")
  public void searchFacility() throws Exception {
    manageFacilityPage = ManageFacilityPage.getInstance(testWebDriver);
    manageFacilityPage = PageFactory.getInstanceOfManageFacilityPage(testWebDriver);
    manageFacilityPage.searchFacility(date_time);
    manageFacilityPage.clickFacilityList(date_time);
  }

  @Then("^I should see overridden ISA \"([^\"]*)\"$")
  public void verifyOverriddenISA(String isa) throws Exception {
    manageFacilityPage = PageFactory.getInstanceOfManageFacilityPage(testWebDriver);
    manageFacilityPage.verifyOverriddenIsa(isa);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void shouldOverrideIsaExistingFacility(String userSIC, String password, String program) throws Exception {
    setupProgramProductTestDataWithCategories("P1", "antibiotic1", "C1", "VACCINES");
    setupProgramProductISA(program, "P1", "1", "2", "3", "100", "100", "1000", "5");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    loginPage.loginAs(userSIC, password);
    HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
    manageFacilityPage = homePage.navigateManageFacility();
    homePage.clickCreateFacilityButton();

    String date_time = manageFacilityPage.enterValuesInFacility(facilityCodePrefix, facilityNamePrefix, program, geoZone,
      facilityType, operatedBy, valueOf(333), true);
    saveButton.click();
    manageFacilityPage.searchFacility(date_time);
    manageFacilityPage.clickFacilityList(date_time);

    manageFacilityPage.overrideIsa("24", 1);
    manageFacilityPage.verifyCalculatedIsa(100);
    manageFacilityPage.clickIsaDoneButton();
    manageFacilityPage.verifyOverriddenIsa("24");

    manageFacilityPage.overrideIsa("30", 1);
    manageFacilityPage.clickIsaCancelButton();
    manageFacilityPage.verifyOverriddenIsa("24");

    manageFacilityPage.overrideIsa("30", 1);
    manageFacilityPage.clickUseCalculatedIsaButton();
    manageFacilityPage.clickIsaDoneButton();
    manageFacilityPage.verifyOverriddenIsa("");

    manageFacilityPage.editPopulation(valueOf("30"));
    manageFacilityPage.overrideIsa("24", 1);
    manageFacilityPage.verifyCalculatedIsa(100);
    manageFacilityPage.clickIsaCancelButton();

    manageFacilityPage.editPopulation(valueOf(3000000));
    manageFacilityPage.overrideIsa("124", 1);
    manageFacilityPage.verifyCalculatedIsa(1000);
    manageFacilityPage.clickIsaCancelButton();
    manageFacilityPage.verifyOverriddenIsa("");

    manageFacilityPage.overrideIsa("24", 1);
    manageFacilityPage.clickIsaDoneButton();
    saveButton.click();
    manageFacilityPage.verifySuccessMessage();
    manageFacilityPage.clickFacilityList(date_time);
    manageFacilityPage.verifyOverriddenIsa("24");
  }


  @AfterMethod(groups = "admin")
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
    }
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123", "Admin123", "VACCINES"}
    };

  }

}

