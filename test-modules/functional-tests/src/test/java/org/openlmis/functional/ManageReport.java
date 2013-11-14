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
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageReport extends TestCaseHelper {

  String reportName, fileName;

  public ManageReport() {
    reportName = "Test-Report" + getCurrentDateAndTime();
    fileName = "activefacility.jrxml";
  }


  @BeforeMethod(groups = {"admin"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void invalidScenariosReports(String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    reportPage.verifyItemsOnReportUploadScreen();

    fileName = "invalidActivefacility.jrxml";

    reportPage.clickSaveButton();
    reportPage.verifyErrorMessageDivReportName();
    reportPage.verifyErrorMessageDivUploadFile();

    reportPage.enterReportName(reportName);
    reportPage.clickSaveButton();
    reportPage.verifyErrorMessageDivUploadFile();

    reportPage.enterReportName("");
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    reportPage.verifyErrorMessageDivReportName();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadWrongReport(String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();
    reportPage.clickAddNewButton();
    reportPage.verifyItemsOnReportUploadScreen();

    testWebDriver.sleep(1000);
    fileName = "invalidActivefacility.jrxml";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    testWebDriver.sleep(1000);
    //reportPage.verifyErrorMessageInvalidFile(); --- Flaky
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive", dependsOnMethods = "invalidScenariosReports")
  public void uploadManageReport(String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    reportPage.verifyItemsOnReportUploadScreen();

    fileName = "activefacility.jrxml";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    reportPage.verifySuccessMessageDiv();
    reportPage.verifyReportNameInList(reportName, 8);
    reportPage.verifyItemsOnReportListScreen();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive", dependsOnMethods = "uploadManageReport")
  public void verifyDuplicateReport(String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    reportPage.verifyItemsOnReportUploadScreen();

    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();

    reportPage.clickAddNewButton();
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();

    reportPage.verifyErrorMessageDivFooter();

    reportPage.clickCancelButton();
    reportPage.verifyReportNameInList(reportName, 8);


  }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyDefaultReports(String[] credentials) throws Exception {

        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
        ReportPage reportPage = homePage.navigateReportScreen();

        reportPage.verifyReportNameInList("Facilities Missing Supporting Requisition Group", 1);
        reportPage.verifyReportNameInList("Facilities Missing Create Requisition Role", 2);
        reportPage.verifyReportNameInList("Facilities Missing Authorize Requisition Role", 3);
        reportPage.verifyReportNameInList("Supervisory Nodes Missing Approve Requisition Role", 4);
        reportPage.verifyReportNameInList("Requisition Groups Missing Supply Line", 5);
        reportPage.verifyReportNameInList("Order Routing Inconsistencies", 6);
        reportPage.verifyReportNameInList("Delivery Zones Missing Manage Distribution Role", 7);

    }

  private String getCurrentDateAndTime() {
    Date dObj = new Date();
    SimpleDateFormat formatter_date_time = new SimpleDateFormat(
      "yyyyMMdd-hhmmss");
    return formatter_date_time.format(dObj);
  }


  @AfterMethod(groups = {"admin"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.deleteReport(reportName);
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}
