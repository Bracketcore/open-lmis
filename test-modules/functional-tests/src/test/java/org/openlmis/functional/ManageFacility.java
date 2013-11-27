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
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ManageFacilityPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageFacility extends TestCaseHelper {


  @BeforeMethod(groups = {"admin"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void testE2EManageFacility(String user, String program, String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    ManageFacilityPage manageFacilityPage = homePage.navigateCreateFacility();
    homePage.verifyAdminTabs();
    homePage.clickCreateFacilityButton();
    homePage.verifyHeader("Add new facility");

    String geoZone = "Ngorongoro";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "FCcode";
    String facilityNamePrefix = "FCname";
    String catchmentPopulationValue = "600000";
    String latitudeValue = "955.5555";
    String longitudeValue = "644.4444";
    String altitudeValue = "6545.4545";

    String date_time = manageFacilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program,
      geoZone, facilityType, operatedBy, "500000");
    manageFacilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");
    assertEquals("f", dbWrapper.getVirtualPropertyOfFacility(facilityCodePrefix+date_time));

    homePage.navigateSearchFacility();
    manageFacilityPage.searchFacility(date_time);
    manageFacilityPage.clickFacilityList(date_time);
    manageFacilityPage.disableFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    manageFacilityPage.verifyDisabledFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    HomePage homePageRestore = manageFacilityPage.enableFacility();

    ManageFacilityPage manageFacilityPageRestore = homePageRestore.navigateSearchFacility();
    manageFacilityPageRestore.searchFacility(date_time);
    manageFacilityPageRestore.clickFacilityList(date_time);
    manageFacilityPageRestore.verifyEditFacilityHeader("Edit facility") ;
    HomePage homePageEdit = manageFacilityPageRestore.editFacility("ESSENTIAL MEDICINES",catchmentPopulationValue, latitudeValue,longitudeValue, altitudeValue);

    manageFacilityPageRestore.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "updated");
    homePage.navigateSearchFacility();
    manageFacilityPage.searchFacility(date_time);
    manageFacilityPage.clickFacilityList(date_time);
    manageFacilityPageRestore.verifyEditedFacility(catchmentPopulationValue, latitudeValue,longitudeValue, altitudeValue);

    ManageFacilityPage manageFacilityPageEdit = homePageEdit.navigateSearchFacility();
    manageFacilityPageEdit.searchFacility(date_time);
    manageFacilityPageEdit.clickFacilityList(date_time);
    ArrayList<String> programsSupported = new ArrayList<String>();
    programsSupported.add("HIV");
    programsSupported.add("ESSENTIAL MEDICINES");
    manageFacilityPageEdit.verifyProgramSupported(programsSupported, date_time);


  }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
    public void testFacilityTypeAndGeoZonePropagationFromParentFacility(String user, String program, String[] credentials) throws Exception {
        String geoZone = "District 1";
        String facilityType = "Lvl2 Hospital";

        setupProductTestData("P10", "P11", program, "Lvl3 Hospital");
        dbWrapper.insertFacilities("F10", "F11");
        dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
        dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
        dbWrapper.insertSchedule("M", "Monthly", "Month");
        setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");

        dbWrapper.insertVirtualFacility("V10", "F10");
        dbWrapper.insertGeographicZone("District 1","District 1","Dodoma");
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

        ManageFacilityPage manageFacilityPage = homePage.navigateSearchFacility();
        manageFacilityPage.searchFacility("F10");
        manageFacilityPage.clickFacilityList("F10");
        manageFacilityPage.editFacilityType(facilityType);
        manageFacilityPage.editGeographicZone(geoZone) ;
        manageFacilityPage.saveFacility();

        manageFacilityPage.searchFacility("V10");
        manageFacilityPage.clickFacilityList("V10");

        assertEquals(facilityType,manageFacilityPage.getFacilityType());
        assertEquals(geoZone,manageFacilityPage.getGeographicZone());

    }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
    public void testProgramSupportedPropagationFromParentFacility(String user, String program, String[] credentials) throws Exception {
        setupProductTestData("P10", "P11", program, "Lvl3 Hospital");
        dbWrapper.insertFacilities("F10", "F11");
        dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
        dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
        dbWrapper.insertSchedule("M", "Monthly", "Month");
        setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
        dbWrapper.insertVirtualFacility("V10","F10");
        dbWrapper.insertGeographicZone("District 1","District 1","Dodoma");
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

        ManageFacilityPage manageFacilityPage = homePage.navigateSearchFacility();
        manageFacilityPage.searchFacility("F10");
        manageFacilityPage.clickFacilityList("F10");
        manageFacilityPage.removeFirstProgram();
        manageFacilityPage.saveFacility();

        manageFacilityPage.searchFacility("V10");
        manageFacilityPage.clickFacilityList("V10");

        assertEquals("ESSENTIAL MEDICINES",manageFacilityPage.getProgramSupported(1));
        assertEquals("VACCINES",manageFacilityPage.getProgramSupported(2));

        homePage.navigateSearchFacility();
        manageFacilityPage.searchFacility("F10");
        manageFacilityPage.clickFacilityList("F10");
        manageFacilityPage.removeFirstProgram();
        manageFacilityPage.activeInactiveFirstProgram();
        manageFacilityPage.saveFacility();

        manageFacilityPage.searchFacility("V10");
        manageFacilityPage.clickFacilityList("V10");

        assertEquals("VACCINES",manageFacilityPage.getProgramSupported(1));
        assertFalse("Program supported flag incorrect",manageFacilityPage.getProgramSupportedActive(1));

        manageFacilityPage.activeInactiveFirstProgram();
        manageFacilityPage.saveFacility();
        manageFacilityPage.clickFacilityList("V10");
        assertTrue("Program supported flag incorrect", manageFacilityPage.getProgramSupportedActive(1));

        homePage.navigateSearchFacility();
        manageFacilityPage.searchFacility("F10");
        manageFacilityPage.clickFacilityList("F10");
        manageFacilityPage.saveFacility();

        manageFacilityPage.searchFacility("V10");
        manageFacilityPage.clickFacilityList("V10");

        assertTrue("Program supported flag incorrect", manageFacilityPage.getProgramSupportedActive(1));

        homePage.navigateSearchFacility();
        manageFacilityPage.searchFacility("F10");
        manageFacilityPage.clickFacilityList("F10");
        manageFacilityPage.addProgram("HIV",false);
        manageFacilityPage.saveFacility();

        manageFacilityPage.searchFacility("V10");
        manageFacilityPage.clickFacilityList("V10");

        assertEquals("HIV",manageFacilityPage.getProgramSupported(1));
        assertTrue("Program supported flag incorrect", manageFacilityPage.getProgramSupportedActive(1));
        assertEquals("VACCINES",manageFacilityPage.getProgramSupported(2));
        assertFalse("Program supported flag incorrect", manageFacilityPage.getProgramSupportedActive(2));
        assertEquals(dbWrapper.getRequisitionGroupId("F10") , dbWrapper.getRequisitionGroupId("V10"));

    }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}
