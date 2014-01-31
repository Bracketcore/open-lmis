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


import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.edi.ConfigureBudgetPage;
import org.openlmis.pageobjects.edi.ConfigureEDIPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureBudgetTemplate extends TestCaseHelper {

  final private static String user = "Admin123";
  final private static String password = "Admin123";
  public static final String CONFIGURE_EDI_INDEX_PAGE = "public/pages/admin/edi/index.html#/configure-edi-file";

  @BeforeMethod(groups = "admin")
  public void setUp() throws Exception {
    super.setup();
  }

  @And("^I access configure budget page$")
  public void accessOrderScreen() throws Exception {
    HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
    ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
    configureEDIPage.navigateConfigureBudgetPage();
  }

  @And("^I should see include column headers option unchecked$")
  public void verifyIncludeColumnHeader() throws Exception {
    ConfigureBudgetPage configureBudgetPage = PageFactory.getInstanceOfConfigureBudgetPage(testWebDriver);
    assertFalse(configureBudgetPage.isHeaderIncluded());
  }

  @And("^I verify default checkbox for all data fields$")
  public void verifyDefaultDataFieldsCheckBox() throws Exception {
    ConfigureBudgetPage configureBudgetPage = PageFactory.getInstanceOfConfigureBudgetPage(testWebDriver);
    configureBudgetPage.verifyDefaultIncludeCheckboxForAllDataFields();
  }

  @And("^I verify default value of positions$")
  public void verifyDefaultPositionValues() throws Exception {
    ConfigureBudgetPage configureBudgetPage = PageFactory.getInstanceOfConfigureBudgetPage(testWebDriver);
    configureBudgetPage.verifyDefaultPositionValues();
  }

  @When("^I save budget file format$")
  public void clickSave() throws Exception {
    ConfigureBudgetPage configureBudgetPage = PageFactory.getInstanceOfConfigureBudgetPage(testWebDriver);
    configureBudgetPage.clickSaveButton();
  }

  @Then("^I should see budget successful saved message as \"([^\"]*)\"$")
  public void verifySaveSuccessfullyMessage(String message) throws Exception {
    ConfigureBudgetPage configureBudgetPage = PageFactory.getInstanceOfConfigureBudgetPage(testWebDriver);
    configureBudgetPage.verifyMessage(message);
  }

  @Test(groups = {"admin"})
  public void testVerifyIncludeColumnHeaderONWithAllPositionsAltered() throws Exception {
    ConfigureBudgetPage configureBudgetPage = gotToConfigureBudgetPage();
    configureBudgetPage.checkIncludeHeader();
    configureBudgetPage.selectValueFromPeriodStartDateDropDown("MM-dd-yyyy");
    configureBudgetPage.setAllocatedBudgetPosition("101");
    configureBudgetPage.setFacilityCodePosition("102");
    configureBudgetPage.checkNotesCheckBox();
    configureBudgetPage.setNotesPosition("103");
    configureBudgetPage.setProgramCodePosition("104");
    configureBudgetPage.setPeriodStartDatePosition("105");

    configureBudgetPage.clickSaveButton();

    configureBudgetPage.verifyMessage("Budget file configuration saved successfully!");
    assertTrue(configureBudgetPage.isHeaderIncluded());
    assertEquals(configureBudgetPage.getAllocatedBudgetPosition(), "101");
    assertEquals(configureBudgetPage.getFacilityCodePosition(), "102");
    assertEquals(configureBudgetPage.getNotesPosition(), "103");
    assertEquals(configureBudgetPage.getProgramCodePosition(), "104");
    assertEquals(configureBudgetPage.getPeriodStartDatePosition(), "105");
    assertEquals(configureBudgetPage.getPeriodStartDateFormat(), "MM-dd-yyyy");
    assertTrue(configureBudgetPage.isNotesChecked());
  }

  @Test(groups = {"admin"})
  public void clickingCancelShouldTakeUserToConfigureEDIPage() throws Exception {
    ConfigureBudgetPage configureBudgetPage = gotToConfigureBudgetPage();
    configureBudgetPage.clickCancelButton();
    assertTrue("User should be redirected to EDI Config page", testWebDriver.getCurrentUrl().contains(CONFIGURE_EDI_INDEX_PAGE));
  }

  @Test(groups = {"admin"})
  public void testInputValidations() throws Exception {
    gotToConfigureBudgetPage();
    verifyDuplicatePositionError();
    verifyZeroPositionError();
    verifyBlankPositionError();
    verifyPositionMoreThan3Digits();
  }

  private ConfigureBudgetPage gotToConfigureBudgetPage() throws IOException {
    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
    return configureEDIPage.navigateConfigureBudgetPage();
  }

  private void verifyDuplicatePositionError() {
    ConfigureBudgetPage configureBudgetPage = PageFactory.getInstanceOfConfigureBudgetPage(testWebDriver);
    configureBudgetPage.setAllocatedBudgetPosition("101");
    configureBudgetPage.setFacilityCodePosition("101");
    configureBudgetPage.clickSaveButton();
    configureBudgetPage.verifyErrorMessage("Position numbers cannot have duplicate values");
  }

  private void verifyZeroPositionError() {
    ConfigureBudgetPage configureBudgetPage = PageFactory.getInstanceOfConfigureBudgetPage(testWebDriver);
    configureBudgetPage.setAllocatedBudgetPosition("0");
    configureBudgetPage.clickSaveButton();
    configureBudgetPage.verifyErrorMessage("Position number cannot be blank or zero for an included field");
  }

  private void verifyBlankPositionError() {
    ConfigureBudgetPage configureBudgetPage = PageFactory.getInstanceOfConfigureBudgetPage(testWebDriver);
    configureBudgetPage.setFacilityCodePosition("");
    configureBudgetPage.clickSaveButton();
    configureBudgetPage.verifyErrorMessage("Position number cannot be blank or zero for an included field");
  }

  private void verifyPositionMoreThan3Digits() {
    ConfigureBudgetPage configureBudgetPage = PageFactory.getInstanceOfConfigureBudgetPage(testWebDriver);

    configureBudgetPage.setFacilityCodePosition("12345");
    assertEquals(configureBudgetPage.getFacilityCodePosition(), "123");

    configureBudgetPage.setAllocatedBudgetPosition("22345");
    assertEquals(configureBudgetPage.getAllocatedBudgetPosition(), "223");

    configureBudgetPage.setNotesPosition("32345");
    assertEquals(configureBudgetPage.getNotesPosition(), "323");

    configureBudgetPage.setProgramCodePosition("42345");
    assertEquals(configureBudgetPage.getProgramCodePosition(), "423");

    configureBudgetPage.setPeriodStartDatePosition("52345");
    assertEquals(configureBudgetPage.getPeriodStartDatePosition(), "523");

    configureBudgetPage.clickSaveButton();
    assertEquals(configureBudgetPage.getFacilityCodePosition(), "123");
    assertEquals(configureBudgetPage.getAllocatedBudgetPosition(), "223");
    assertEquals(configureBudgetPage.getNotesPosition(), "323");
    assertEquals(configureBudgetPage.getProgramCodePosition(), "423");
    assertEquals(configureBudgetPage.getPeriodStartDatePosition(), "523");
  }

  @AfterMethod(groups = "admin")
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }

  }
}

