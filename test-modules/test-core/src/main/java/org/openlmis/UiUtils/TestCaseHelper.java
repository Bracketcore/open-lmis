/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.UiUtils;


import java.io.IOException;
import java.sql.SQLException;

import static java.lang.System.getProperty;

public class TestCaseHelper {

  protected DBWrapper dbWrapper;
  protected String baseUrlGlobal, dburlGlobal;

  protected static TestWebDriver testWebDriver;
  protected static boolean isSeleniumStarted = false;
  protected static DriverFactory driverFactory = new DriverFactory();
  public static final String DEFAULT_BROWSER = "firefox";
  public static final String DEFAULT_BASE_URL = "http://localhost:9091/";
  public static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/open_lmis";


  public void setup() throws Exception {
    String browser = getProperty("browser", DEFAULT_BROWSER);
    baseUrlGlobal = getProperty("baseurl", DEFAULT_BASE_URL);
    dburlGlobal = getProperty("dburl", DEFAULT_DB_URL);


    dbWrapper = new DBWrapper(baseUrlGlobal, dburlGlobal);
    dbWrapper.deleteData();

    if (!isSeleniumStarted) {
      loadDriver(browser);
      addTearDownShutDownHook();
      isSeleniumStarted = true;
    }
  }

  public void tearDownSuite() {
    try {
      if (getProperty("os.name").startsWith("Windows") && driverFactory.driverType().contains("IEDriverServer")) {
        Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
        Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
      } else {
        testWebDriver.quitDriver();
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


  protected void loadDriver(String browser) throws InterruptedException {
    testWebDriver = new TestWebDriver(driverFactory.loadDriver(browser));
  }

    public void setupTestDataToInitiateRnR(String program, String userSIC) throws IOException, SQLException{
        setupProductTestData("P10", "P11", program, "Lvl3 Hospital");
        dbWrapper.insertFacilities("F10", "F11");
        dbWrapper.configureTemplate(program);
        setupTestUserRoleRightsData(userSIC);
        dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
        dbWrapper.insertRoleAssignment("200", "store in-charge");
        dbWrapper.insertSchedules();
        dbWrapper.insertProcessingPeriods();
        setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
        dbWrapper.insertSupplyLines("N1", program, "F10");
    }

    public void setupProductTestData(String product1, String product2, String program, String facilityType) throws IOException, SQLException {
        dbWrapper.insertProducts(product1, product2);
        dbWrapper.insertProgramProducts(product1, product2, program);
        dbWrapper.insertFacilityApprovedProducts(product1, product2, program, facilityType);
    }

    public void setupRequisitionGroupData(String RGCode1, String RGCode2, String SupervisoryNodeCode1, String SupervisoryNodeCode2, String Facility1, String Facility2) throws IOException, SQLException {
        dbWrapper.insertRequisitionGroups(RGCode1, RGCode2, SupervisoryNodeCode1, SupervisoryNodeCode2);
        dbWrapper.insertRequisitionGroupMembers(Facility1, Facility2);
        dbWrapper.insertRequisitionGroupProgramSchedule();
    }

    public void setupTestUserRoleRightsData(String userSIC) throws IOException, SQLException {
        dbWrapper.insertRole("store in-charge", "false", "");
        dbWrapper.insertRole("district pharmacist", "false", "");
        dbWrapper.assignRight("store in-charge", "CREATE_REQUISITION");
        dbWrapper.assignRight("store in-charge", "VIEW_REQUISITION");
        String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
        dbWrapper.insertUser("200", userSIC, passwordUsers, "F10", "Fatima_Doe@openlmis.com");
    }

}
