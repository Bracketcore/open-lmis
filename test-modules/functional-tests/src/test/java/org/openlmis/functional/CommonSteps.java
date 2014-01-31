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
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class CommonSteps extends TestCaseHelper {

  @And("^I logout$")
  public void logout() throws IOException {
    HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
  }

  @And("^I am logged in as \"([^\"]*)\"$")
  public void login(String username) throws IOException {
    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    loginPage.loginAs(username, "Admin123");
  }

  @Given("^I am logged in as Admin$")
  public void adminLogin() throws IOException {
    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    loginPage.loginAs("Admin123", "Admin123");
  }

  @Given("^I have \"([^\"]*)\" user with \"([^\"]*)\" rights$")
  public void setupUserWithRights(String user, String rights) throws SQLException {
    String[] rightList = rights.split(",");
    List<String> rightsList = new ArrayList<>();
    Collections.addAll(rightsList, rightList);
    setupTestUserRoleRightsData("200", user, rightsList);
  }

  @When("^I have \"([^\"]*)\" role having \"([^\"]*)\" based \"([^\"]*)\" rights$")
  public void createRoleWithRights(String roleName, String roleType, String right) throws SQLException {
    setupTestRoleRightsData(roleName, right);
  }

  @And("^I have users:$")
  public void createUser(DataTable userTable) throws SQLException {
    String password = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    List<Map<String, String>> data = userTable.asMaps();
    for (Map map : data) {
      dbWrapper.insertUser(map.get("UserId").toString(), map.get("UserName").toString(), password, map.get("FacilityCode").toString(), map.get("Email").toString());
      dbWrapper.insertRoleAssignment(map.get("UserId").toString(), map.get("Role").toString());
    }
  }

  @And("^I have fulfillment data for user \"([^\"]*)\" role \"([^\"]*)\" and facility \"([^\"]*)\"$")
  public void insertFulfillmentRoleAssignment(String user, String role, String facility) throws SQLException {
    dbWrapper.insertFulfilmentRoleAssignment(user, role, facility);
  }

  @And("^I have approved quantity \"([^\"]*)\"$")
  public void insertApprovedQuantity(int approvedQuantity) throws IOException, SQLException {
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", approvedQuantity);
  }

  @After
  public void embedScreenshot(Scenario scenario) {
    if (scenario.isFailed()) {
      byte[] screenshot = testWebDriver.getScreenshot();
      scenario.embed(screenshot, "image/png");
    }
  }

}
