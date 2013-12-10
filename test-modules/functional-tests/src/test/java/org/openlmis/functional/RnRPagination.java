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
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.InitiateRnRPage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ViewRequisitionPage;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class RnRPagination extends TestCaseHelper {

  private static final String FULL_SUPPLY_BASE_LOCATOR = "//table[@id='fullSupplyTable']";
  private static final String NON_FULL_SUPPLY_BASE_LOCATOR = "//table[@id='nonFullSupplyTable']";

  @BeforeMethod(groups = {"requisition"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive", enabled = false)
  public void testRnRPaginationAndSpecificDisplayOrder(String program, String userSIC, String userMO, String password, String[] credentials) throws Exception {
    dbWrapper.setupMultipleProducts(program, "Lvl3 Hospital", 11, false);
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestUserRoleRightsData("200", userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    testWebDriver.sleep(2000);
    verifyNumberOfPageLinks(11, 10);
    verifyNextAndLastLinksEnabled();
    verifyPreviousAndFirstLinksDisabled();
    verifyDisplayOrderFullSupply(10);

    initiateRnRPage.PopulateMandatoryFullSupplyDetails(11, 10);

    testWebDriver.getElementByXpath("//a[contains(text(), '2') and @class='ng-binding']").click();
    assertEquals(testWebDriver.getElementByXpath(FULL_SUPPLY_BASE_LOCATOR + "/tbody[1]/tr[1]/td").getText(), "Antibiotics");
    verifyPageLinksFromLastPage();

    initiateRnRPage.addMultipleNonFullSupplyLineItems(11, false);
    verifyDisplayOrderNonFullSupply(10);
    verifyNumberOfPageLinks(11, 10);
    verifyPreviousAndFirstLinksDisabled();
    verifyNextAndLastLinksEnabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '2') and @class='ng-binding']").click();
    assertEquals(testWebDriver.getElementByXpath(NON_FULL_SUPPLY_BASE_LOCATOR + "/tbody[1]/tr[1]/td").getText(), "Antibiotics");
    verifyPageLinksFromLastPage();

    initiateRnRPage.submitRnR();

    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "HIV");
    ViewRequisitionPage viewRequisitionPage = homePage.navigateViewRequisition();
    viewRequisitionPage.enterViewSearchCriteria();
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.clickRnRList();

    verifyNumberOfPageLinks(11, 10);
    verifyNextAndLastLinksEnabled();
    verifyPreviousAndFirstLinksDisabled();
    verifyDisplayOrderFullSupplyOnViewRequisition(10);

    testWebDriver.getElementByXpath("//a[contains(text(), '2') and @class='ng-binding']").click();
    assertEquals(testWebDriver.getElementByXpath(FULL_SUPPLY_BASE_LOCATOR + "/tbody[1]/tr[1]/td").getText(), "Antibiotics");
    verifyPageLinksFromLastPage();

    viewRequisitionPage.clickNonFullSupplyTab();

    verifyDisplayOrderNonFullSupplyOnViewRequisition(10);

    verifyNumberOfPageLinks(11, 10);
    verifyPreviousAndFirstLinksDisabled();
    verifyNextAndLastLinksEnabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '2') and @class='ng-binding']").click();
    assertEquals(testWebDriver.getElementByXpath(NON_FULL_SUPPLY_BASE_LOCATOR + "/tbody/tr[1]/td").getText(), "Antibiotics");


    verifyPageLinksFromLastPage();

  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testProductDefaultDisplayOrder(String program, String userSIC, String userMO, String password, String[] credentials) throws Exception {
    dbWrapper.setupMultipleProducts(program, "Lvl3 Hospital", 11, true);
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestUserRoleRightsData("200", userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    String periodDetails = homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    verifyDefaultDisplayOrderFullSupply(initiateRnRPage);

    initiateRnRPage.addMultipleNonFullSupplyLineItems(11, false);
    verifyDefaultDisplayOrderNonFullSupply(initiateRnRPage);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testCategoryDisplayOrder(String program, String userSIC, String userMO, String password, String[] credentials) throws Exception {
    dbWrapper.setupMultipleCategoryProducts(program, "Lvl3 Hospital", 11, false);
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestUserRoleRightsData("200", userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    String periodDetails = homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    verifyCategoryDisplayOrder(initiateRnRPage, 10);


    initiateRnRPage.addMultipleNonFullSupplyLineItems(11, true);
    verifyCategoryDisplayOrder(initiateRnRPage, 10);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testCategoryDefaultDisplayOrder(String program, String userSIC, String userMO, String password, String[] credentials) throws Exception {
    dbWrapper.setupMultipleCategoryProducts(program, "Lvl3 Hospital", 11, true);
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestUserRoleRightsData("200", userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    verifyCategoryDefaultDisplayOrderFullSupply(initiateRnRPage);

    initiateRnRPage.addMultipleNonFullSupplyLineItems(11, true);
    verifyCategoryDefaultDisplayOrderNonFullSupply(initiateRnRPage);
  }

  
  public void verifyDisplayOrderFullSupply(int numberOfLineItemsPerPage) throws Exception {
    for (int i = 0; i < numberOfLineItemsPerPage; i++) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(FULL_SUPPLY_BASE_LOCATOR + "/tbody[" + (i + 1) + "]/tr[2]/td[1]/ng-switch/span/ng-switch/span"));
      assertEquals(testWebDriver.getElementByXpath(FULL_SUPPLY_BASE_LOCATOR + "/tbody[" + (i + 1) + "]/tr[2]/td[1]/ng-switch/span/ng-switch/span").getText(), "F" + i);
    }
  }

  public void verifyDisplayOrderFullSupplyOnViewRequisition(int numberOfLineItemsPerPage) throws Exception {
    for (int i = 0; i < numberOfLineItemsPerPage; i++) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//span[@id='productCode_" + i + "']"));
      assertEquals(testWebDriver.getElementByXpath("//span[@id='productCode_" + i + "']").getText(), "F" + i);
    }
  }

  public void verifyDisplayOrderNonFullSupply(int numberOfLineItemsPerPage) throws Exception {
    for (int i = 0; i < numberOfLineItemsPerPage; i++) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(NON_FULL_SUPPLY_BASE_LOCATOR + "/tbody[" + (i + 1) + "]/tr[2]/td[1]/ng-switch/span"));
      assertEquals(testWebDriver.getElementByXpath(NON_FULL_SUPPLY_BASE_LOCATOR + "/tbody[" + (i + 1) + "]/tr[2]/td[1]/ng-switch/span").getText(), "NF" + i);
    }
  }

  public void verifyDisplayOrderNonFullSupplyOnViewRequisition(int numberOfLineItemsPerPage) throws Exception {
    for (int i = 0; i < numberOfLineItemsPerPage; i++) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//span[@id='productCode_" + i + "']"));
      assertEquals(testWebDriver.getElementByXpath("//span[@id='productCode_" + i + "']").getText(), "NF" + i);
    }
  }

  public void verifyDefaultDisplayOrderFullSupply(InitiateRnRPage initiateRnRPage) throws Exception {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById("productCode_0"));
    assertEquals(initiateRnRPage.getProductCode(0), "F0");
    assertEquals(initiateRnRPage.getProductCode(1), "F1");
    assertEquals(initiateRnRPage.getProductCode(2), "F10");
  }

  public void verifyDefaultDisplayOrderNonFullSupply(InitiateRnRPage initiateRnRPage) throws Exception {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById("productCode_0"));
    assertEquals(initiateRnRPage.getProductCode(0), "NF0");
    assertEquals(initiateRnRPage.getProductCode(1), "NF1");
    assertEquals(initiateRnRPage.getProductCode(2), "NF10");
  }

  public void verifyCategoryDisplayOrder(InitiateRnRPage initiateRnRPage, int numberOfLineItems) throws Exception {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById("category_0"));
    for (int i = 0; i < numberOfLineItems; i++) {
      assertEquals(initiateRnRPage.getCategoryText(i), "Antibiotics" + i);
    }
  }

  public void verifyCategoryDefaultDisplayOrderFullSupply(InitiateRnRPage initiateRnRPage) throws Exception {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById("category_0"));
    assertEquals(initiateRnRPage.getCategoryText(0), "Antibiotics0");
    assertEquals(initiateRnRPage.getCategoryText(1), "Antibiotics1");
    assertEquals(initiateRnRPage.getCategoryText(2), "Antibiotics10");
  }

  public void verifyCategoryDefaultDisplayOrderNonFullSupply(InitiateRnRPage initiateRnRPage) throws Exception {
    assertEquals(initiateRnRPage.getCategoryText(0), "Antibiotics0");
    assertEquals(initiateRnRPage.getCategoryText(1), "Antibiotics1");
    assertEquals(initiateRnRPage.getCategoryText(2), "Antibiotics10");

  }

  @AfterMethod(groups = {"requisition"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeIncharge", "medicalofficer", "Admin123", new String[]{"Admin123", "Admin123"}}
    };

  }
}

