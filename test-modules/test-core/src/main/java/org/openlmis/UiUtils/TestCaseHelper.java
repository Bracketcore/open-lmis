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


import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestBase.fail;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.lang.System.getProperty;

public class TestCaseHelper {

  public static DBWrapper dbWrapper;
  protected static String baseUrlGlobal, dbUrlGlobal;
  protected static String DOWNLOAD_FILE_PATH;
  protected static TestWebDriver testWebDriver;
  protected static boolean isSeleniumStarted = false;
  protected static DriverFactory driverFactory = new DriverFactory();
  public static final String DEFAULT_BROWSER = "firefox";
  public static final String DEFAULT_BASE_URL = "http://localhost:9091/";
  public static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/open_lmis";

  public void setup() throws Exception {
    String browser = getProperty("browser", DEFAULT_BROWSER);
    baseUrlGlobal = getProperty("baseurl", DEFAULT_BASE_URL);
    dbUrlGlobal = getProperty("dbUrl", DEFAULT_DB_URL);

    dbWrapper = new DBWrapper(baseUrlGlobal, dbUrlGlobal);
    dbWrapper.deleteData();

    if (!isSeleniumStarted) {
      loadDriver(browser);
      addTearDownShutDownHook();
      isSeleniumStarted = true;
    }
    if (getProperty("os.name").startsWith("Windows"))
      DOWNLOAD_FILE_PATH = "C:\\Users\\openlmis\\Downloads";
    else
      DOWNLOAD_FILE_PATH = new File(System.getProperty("user.dir")).getParent();
  }

  public void tearDownSuite() {
    try {
      if (getProperty("os.name").startsWith("Windows") && driverFactory.driverType().contains("IEDriverServer")) {
        Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
        Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
      } else {
        try {
          testWebDriver.quitDriver();
        } catch (UnreachableBrowserException e) {
        }
      }
      driverFactory.deleteExe();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  protected void addTearDownShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        if (testWebDriver != null) {
          tearDownSuite();
        }
      }
    });
  }


  protected void loadDriver(String browser) throws InterruptedException, IOException {

    testWebDriver = new TestWebDriver(driverFactory.loadDriver(browser));
  }

  public void setupTestDataToInitiateRnR(boolean configureTemplate, String program, String user, String userId, List<String> rightsList) throws IOException, SQLException {
    setupProductTestData("P10", "P11", program, "Lvl3 Hospital");
    dbWrapper.insertFacilities("F10", "F11");
    if (configureTemplate)
      dbWrapper.configureTemplate(program);

    setupTestUserRoleRightsData(userId, user, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment(userId, "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2012-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10");
  }

  public void setupTestUserRoleRightsData(String userId, String userSIC, List<String> rightsList) throws IOException, SQLException {
    dbWrapper.insertRole("store in-charge", "");
    dbWrapper.insertRole("district pharmacist", "");
    for (String rights : rightsList)
      dbWrapper.assignRight("store in-charge", rights);
    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    dbWrapper.insertUser(userId, userSIC, passwordUsers, "F10", "Fatima_Doe@openlmis.com");
  }

  public void createUserAndAssignRoleRights(String userId, String user, String email, String homeFacility, String role, List<String> rightsList) throws IOException, SQLException {
    dbWrapper.insertRole(role, "");
    for (String rights : rightsList)
      dbWrapper.assignRight(role, rights);
    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    dbWrapper.insertUser(userId, user, passwordUsers, homeFacility, email);
    dbWrapper.insertRoleAssignment(userId, role);
  }

  public void setupRnRTestDataRnRForCommTrack(boolean configureGenericTemplate, String program, String user, String userId, List<String> rightsList) throws IOException, SQLException {
    setupProductTestData("P10", "P11", program, "Lvl3 Hospital");
    dbWrapper.insertFacilities("F10", "F11");

    setupTestUserRoleRightsData(userId, user, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment(userId, "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10");
    if (configureGenericTemplate) {
      dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
      dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
      dbWrapper.configureTemplate(program);
    } else {
      dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "M");
      dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
      dbWrapper.configureTemplateForCommTrack(program);
      dbWrapper.insertPastPeriodRequisitionAndLineItems("F10", "HIV", "Period1", "P10");
    }
  }

  public void setupTestDataToApproveRnR(String user, String userId, List<String> rightsList) throws IOException, SQLException {
    for (String rights : rightsList)
      dbWrapper.assignRight("store in-charge", rights);
    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    dbWrapper.insertUser(userId, user, passwordUsers, "F10", "");
    dbWrapper.insertSupervisoryNodeSecond("F10", "N2", "Node 2", "N1");
    dbWrapper.insertRoleAssignmentForSupervisoryNode(userId, "store in-charge", "N1");
  }

  public void setupProductTestData(String product1, String product2, String program, String facilityType) throws IOException, SQLException {
    dbWrapper.insertProducts(product1, product2);
    dbWrapper.insertProgramProducts(product1, product2, program);
    dbWrapper.insertFacilityApprovedProducts(product1, product2, program, facilityType);
  }

  public void setupProgramProductTestData(String product1, String product2, String program) throws IOException, SQLException {
    dbWrapper.insertProducts(product1, product2);
    dbWrapper.insertProgramProducts(product1, product2, program);
  }

  public void setupProgramProductTestDataWithCategories(String product, String productName, String category, String program) throws IOException, SQLException {
    dbWrapper.insertProductWithCategory(product, productName, category);
    dbWrapper.insertProgramProductsWithCategory(product, program);
  }

  public void setupProgramProductISA(String program, String product, String whoratio, String dosesperyear, String wastageFactor, String bufferpercentage, String minimumvalue, String maximumvalue, String adjustmentvalue) throws IOException, SQLException {
    dbWrapper.insertProgramProductISA(program, product, whoratio, dosesperyear, wastageFactor, bufferpercentage, minimumvalue, maximumvalue, adjustmentvalue);
  }

  public void setupRequisitionGroupData(String RGCode1, String RGCode2, String SupervisoryNodeCode1, String SupervisoryNodeCode2, String Facility1, String Facility2) throws IOException, SQLException {
    dbWrapper.insertRequisitionGroups(RGCode1, RGCode2, SupervisoryNodeCode1, SupervisoryNodeCode2);
    dbWrapper.insertRequisitionGroupMembers(Facility1, Facility2);
    dbWrapper.insertRequisitionGroupProgramSchedule();
  }

  public void setupTestRoleRightsData(String roleName, String roleRight) throws IOException, SQLException {
    dbWrapper.insertRole(roleName, "");
    String right[] = roleRight.split(",");
    for (int i = 0; i < right.length; i++)
      dbWrapper.assignRight(roleName, right[i]);
  }

  public void setupDataExternalVendor(boolean isPreviousPeriodRnRRequired) throws IOException, SQLException {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    rightsList.add("AUTHORIZE_REQUISITION");
    if (isPreviousPeriodRnRRequired)
      setupRnRTestDataRnRForCommTrack(false, "HIV", "commTrack", "700", rightsList);
    else
      setupRnRTestDataRnRForCommTrack(true, "HIV", "commTrack", "700", rightsList);

  }

  public void setupDataApproverExternalVendor() throws IOException, SQLException {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("APPROVE_REQUISITION");
    rightsList.add("CONVERT_TO_ORDER");
    setupTestDataToApproveRnR("commTrack1", "701", rightsList);
  }

  public void setupDataForDeliveryZone(boolean multipleFacilityInstances, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                       String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                       String facilityCodeFirst, String facilityCodeSecond,
                                       String programFirst, String programSecond, String schedule) throws IOException, SQLException {
    dbWrapper.insertDeliveryZone(deliveryZoneCodeFirst, deliveryZoneNameFirst);
    if (multipleFacilityInstances)
      dbWrapper.insertDeliveryZone(deliveryZoneCodeSecond, deliveryZoneNameSecond);
    dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeFirst, facilityCodeFirst);
    dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeFirst, facilityCodeSecond);
    if (multipleFacilityInstances)
      dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeSecond, facilityCodeSecond);
    dbWrapper.insertProcessingPeriodForDistribution(14, schedule);
    dbWrapper.insertDeliveryZoneProgramSchedule(deliveryZoneCodeFirst, programFirst, schedule);
    dbWrapper.insertDeliveryZoneProgramSchedule(deliveryZoneCodeFirst, programSecond, schedule);
    if (multipleFacilityInstances)
      dbWrapper.insertDeliveryZoneProgramSchedule(deliveryZoneCodeSecond, programSecond, schedule);
  }


  public void addOnDataSetupForDeliveryZoneForMultipleFacilitiesAttachedWithSingleDeliveryZone(String deliveryZoneCodeFirst,
                                                                                               String facilityCodeThird,
                                                                                               String facilityCodeFourth, String geoZone1, String geoZone2, String parentGeoZone) throws IOException, SQLException {
    dbWrapper.insertGeographicZone(geoZone1, geoZone2, parentGeoZone);
    dbWrapper.insertFacilitiesWithDifferentGeoZones(facilityCodeThird, facilityCodeFourth, geoZone1, geoZone2);
    dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeFirst, facilityCodeThird);
    dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeFirst, facilityCodeFourth);
  }

  public void setupTestDataToInitiateRnRAndDistribution(String facilityCode1, String facilityCode2, boolean configureTemplate, String program, String user, String userId,
                                                        List<String> rightsList, String programCode,
                                                        String geoLevel1, String geoLevel2, String parentGeoLevel) throws IOException, SQLException {
    setupProductTestData("P10", "P11", program, "Lvl3 Hospital");
    dbWrapper.insertGeographicZone(geoLevel1, geoLevel1, parentGeoLevel);
    dbWrapper.insertFacilitiesWithDifferentGeoZones(facilityCode1, facilityCode2, geoLevel2, geoLevel1);
    if (configureTemplate)
      dbWrapper.configureTemplate(program);

    setupTestUserRoleRightsData(userId, user, rightsList);
    dbWrapper.insertSupervisoryNode(facilityCode1, "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment(userId, "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", facilityCode1, facilityCode2);
    dbWrapper.insertSupplyLines("N1", program, facilityCode1);
    dbWrapper.updateActiveStatusOfProgram(programCode,true);
  }

  public void updateProductWithGroup(String product, String productGroup) throws IOException, SQLException {
    dbWrapper.insertProductGroup(productGroup);
    dbWrapper.updateProductToHaveGroup(product, productGroup);
  }


  public void sendKeys(String locator, String value) {
    int length = testWebDriver.getAttribute(testWebDriver.getElementByXpath(locator), "value").length();
    for (int i = 0; i < length; i++)
      testWebDriver.getElementByXpath(locator).sendKeys("\u0008");
    testWebDriver.getElementByXpath(locator).sendKeys(value);
  }

  public void sendKeys(WebElement locator, String value) {
    int length = testWebDriver.getAttribute(locator, "value").length();
    for (int i = 0; i < length; i++)
      locator.sendKeys("\u0008");
    locator.sendKeys(value);
  }

  public String IsaProgramProduct(String program, String product, String population) throws IOException, SQLException {
    String[] isaParams = dbWrapper.getProgramProductISA(program, product);
    return calculateISA(isaParams[0], isaParams[1], isaParams[2], isaParams[3], isaParams[4], isaParams[5], isaParams[6], population);
  }

  public String calculateISA(String ratioValue, String dosesPerYearValue, String wastageValue, String bufferPercentageValue, String adjustmentValue,
                             String minimumValue, String maximumValue, String populationValue) {
    Float calculatedISA;
    Float minimum = 0.0F;
    Float maximum = 0.0F;

    Integer population = Integer.parseInt(populationValue);
    Float ratio = Float.parseFloat(ratioValue) / 100;
    Integer dosesPerYear = Integer.parseInt(dosesPerYearValue);
    Float wastage = Float.parseFloat(wastageValue);
    Float bufferPercentage = (Float.parseFloat(bufferPercentageValue) / 100) + 1;

    if (minimumValue != null) {
      minimum = Float.parseFloat(minimumValue);
    }
    if (maximumValue != null) {
      maximum = Float.parseFloat(maximumValue);
    }

    Integer adjustment = Integer.parseInt(adjustmentValue);

    calculatedISA = (((population * ratio * dosesPerYear * wastage) / 12) * bufferPercentage) + adjustment;

    if (calculatedISA <= minimum && minimum != 0.0)
      return (minimumValue);
    else if (calculatedISA >= maximum && maximum != 0.0)
      return (maximumValue);
    return (new BigDecimal(calculatedISA).setScale(0, BigDecimal.ROUND_CEILING)).toString();
  }

  public void setupDeliveryZoneRolesAndRights(String deliveryZoneCodeFirst, String deliveryZoneCodeSecond, String deliveryZoneNameFirst,
                                              String deliveryZoneNameSecond, String facilityCodeFirst, String facilityCodeSecond,
                                              String programFirst, String programSecond, String schedule, String roleNmae) throws IOException, SQLException {
    dbWrapper.insertFacilities(facilityCodeFirst, facilityCodeSecond);
    dbWrapper.insertSchedule(schedule, "Monthly", "Month");
    setupTestRoleRightsData(roleNmae, "MANAGE_DISTRIBUTION");
    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond, facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);
  }

    public void setupDeliveryZoneRolesAndRightsAfterWarehouse(String deliveryZoneCodeFirst, String deliveryZoneCodeSecond, String deliveryZoneNameFirst,
                                                String deliveryZoneNameSecond, String facilityCodeFirst, String facilityCodeSecond,
                                                String programFirst, String programSecond, String schedule, String roleNmae) throws IOException, SQLException {
        //dbWrapper.insertFacilities(facilityCodeFirst, facilityCodeSecond);
        //dbWrapper.insertSchedule(schedule, "Monthly", "Month");
        setupTestRoleRightsData(roleNmae, "MANAGE_DISTRIBUTION");
        setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond, facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);
    }

    public void setupWarehouseRolesAndRights(String facilityCodeFirst, String facilityCodeSecond,
                                             String programName,
                                             String schedule, String roleName) throws IOException, SQLException {
        dbWrapper.insertFacilities(facilityCodeFirst, facilityCodeSecond);
        dbWrapper.insertSchedule(schedule, "Monthly", "Month");
        setupTestRoleRightsData(roleName, "FACILITY_FILL_SHIPMENT");
        setupDataForWarehouse(facilityCodeFirst, programName, "N1");
    }

    public void setupDataForWarehouse(String facilityCode,String programName, String supervisoryNode)throws IOException, SQLException{
        dbWrapper.insertWarehouseIntoSupplyLinesTable(facilityCode, programName,"N1",false);
    }

  public String[] readCSVFile(String file) throws IOException, SQLException {
    BufferedReader br = null;
    String line = "";
    String[] array = new String[50];
    String filePath = DOWNLOAD_FILE_PATH + getProperty("file.separator") + file;
    try {
      int i = 0;
      br = new BufferedReader(new FileReader(filePath));
      while ((line = br.readLine()) != null) {
        array[i] = line;
        i++;
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return array;
  }

  public void deleteFile(String file) {
    String filePath = DOWNLOAD_FILE_PATH + getProperty("file.separator") + file;
    File f = new File(filePath);

    if (!f.exists())
      throw new IllegalArgumentException(
        "Delete: no such file or directory: " + filePath);

    if (!f.canWrite())
      throw new IllegalArgumentException("Delete: write protected: "
        + filePath);

    if (f.isDirectory()) {
      String[] files = f.list();
      if (files.length > 0)
        throw new IllegalArgumentException(
          "Delete: directory not empty: " + filePath);
    }

    boolean success = f.delete();

    if (!success)
      throw new IllegalArgumentException("Delete: deletion failed");
  }

  public void switchOffNetwork() throws IOException {
    testWebDriver.sleep(2000);
    Runtime.getRuntime().exec("sudo ifconfig en1 down");
    testWebDriver.sleep(2000);
  }

  public void switchOnNetwork() throws IOException {
    testWebDriver.sleep(2000);
    Runtime.getRuntime().exec("sudo ifconfig en1 up");
    testWebDriver.sleep(2000);
  }

  public void waitForAppCacheComplete() {
    int count = 0;
    JavascriptExecutor driver = (JavascriptExecutor) TestWebDriver.getDriver();

    driver.executeScript("if(!window.localStorage[\"appCached\"]) window.localStorage.setItem(\"appCached\",\"false\");");
    driver.executeScript("window.applicationCache.oncached = function (e) {window.localStorage.setItem(\"appCached\",\"true\");};");
    while ((driver.executeScript("return window.localStorage.getItem(\"appCached\");")).toString().equals("false")) {
      testWebDriver.sleep(2000);
      driver.executeScript("window.applicationCache.oncached = function (e) {window.localStorage.setItem(\"appCached\",\"true\");};");
      count++;
      if (count > 10) {
        fail("Appcache not working in 20 sec.");
        break;
      }
    }
  }

  public void verifyPageLinksFromLastPage() throws Exception {
    verifyNextAndLastLinksDisabled();
    verifyPreviousAndFirstLinksEnabled();

    testWebDriver.getElementById("firstPageLink").click();
    verifyNextAndLastLinksEnabled();
    verifyPreviousAndFirstLinksDisabled();

    testWebDriver.getElementById("nextPageLink").click();
    verifyNextAndLastLinksDisabled();
    verifyPreviousAndFirstLinksEnabled();

    testWebDriver.getElementById("previousPageLink").click();
    verifyNextAndLastLinksEnabled();
    verifyPreviousAndFirstLinksDisabled();

    testWebDriver.getElementById("lastPageLink").click();
    verifyNextAndLastLinksDisabled();
    verifyPreviousAndFirstLinksEnabled();
  }

  public void verifyNumberOfPageLinks(int numberOfProducts, int numberOfLineItemsPerPage) throws Exception {
    testWebDriver.waitForPageToLoad();
    int numberOfPages = numberOfProducts / numberOfLineItemsPerPage;
    if (numberOfProducts % numberOfLineItemsPerPage != 0) {
      numberOfPages = numberOfPages + 1;
    }
    for (int i = 1; i <= numberOfPages; i++) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[contains(text(), '" + i + "') and @class='ng-binding']"));
      assertTrue(testWebDriver.getElementByXpath("//a[contains(text(), '" + i + "') and @class='ng-binding']").isDisplayed());
    }
  }

  public void verifyNextAndLastLinksEnabled() throws Exception {
    testWebDriver.waitForPageToLoad();
    WebElement nextPageLink = testWebDriver.getElementById("nextPageLink");

    testWebDriver.waitForElementToAppear(nextPageLink);

    assertEquals(nextPageLink.getCssValue("color"), "rgba(119, 119, 119, 1)");
    assertEquals(testWebDriver.getElementById("lastPageLink").getCssValue("color"), "rgba(119, 119, 119, 1)");
  }

  public void verifyPreviousAndFirstLinksEnabled() throws Exception {
    testWebDriver.waitForPageToLoad();
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById("previousPageLink"));
    assertEquals(testWebDriver.getElementById("previousPageLink").getCssValue("color"), "rgba(119, 119, 119, 1)");
    assertEquals(testWebDriver.getElementById("firstPageLink").getCssValue("color"), "rgba(119, 119, 119, 1)");
  }

  public void verifyNextAndLastLinksDisabled() throws Exception {
    testWebDriver.waitForPageToLoad();
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById("nextPageLink"));
    assertEquals(testWebDriver.getElementById("nextPageLink").getCssValue("color"), "rgba(204, 204, 204, 1)");
    assertEquals(testWebDriver.getElementById("lastPageLink").getCssValue("color"), "rgba(204, 204, 204, 1)");
  }

  public void verifyPreviousAndFirstLinksDisabled() throws Exception {
    testWebDriver.waitForPageToLoad();
    WebElement firstPageLink = testWebDriver.getElementById("firstPageLink");

    testWebDriver.waitForElementToAppear(firstPageLink);

    assertEquals(firstPageLink.getCssValue("color"), "rgba(204, 204, 204, 1)");
    assertEquals(testWebDriver.getElementById("previousPageLink").getCssValue("color"), "rgba(204, 204, 204, 1)");
  }
}


