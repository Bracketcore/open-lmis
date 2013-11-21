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

import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.pageobjects.*;
import org.openlmis.restapi.domain.Agent;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertNotEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.testng.Assert.assertNull;


public class CreateUpdateCHW extends JsonUtility {
  public static final String POST = "POST";
  public static final String PUT = "PUT";
  public static final String FULL_JSON_TXT_FILE_NAME = "AgentValid.txt";
  public static final String userEmail = "Fatim_Doe@openlmis.com";
  public static final String CREATE_URL = "http://localhost:9091/rest-api/agents.json";
  public static final String UPDATE_URL = "http://localhost:9091/rest-api/agents/";
  public static final String commTrackUser = "commTrack";
  public static final String PHONE_NUMBER = "0099887766";
  public static final String DEFAULT_AGENT_NAME = "AgentVinod";
  public static final String DEFAULT_PARENT_FACILITY_CODE = "F10";
  public static final String ACTIVE_STATUS = "true";
  public static final String DEFAULT_AGENT_CODE = "A2";
  public static final String FALSE_FLAG = "f";
  public static final String TRUE_FLAG = "t";
  public static final String JSON_EXTENSION = ".json";


  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    super.setup();
    super.setupTestData(true);
    dbWrapper.updateRestrictLogin("commTrack",true);
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws Exception {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();

  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldNotShowVirtualFacilityOnManageUserScreen(String user, String program, String[] credentials) throws Exception {
    dbWrapper.updateVirtualPropertyOfFacility(DEFAULT_PARENT_FACILITY_CODE, ACTIVE_STATUS);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UserPage userPage = homePage.navigateToUser();
    userPage.enterUserDetails("storeIncharge", userEmail, "Fatim", "Doe");
    userPage.clickViewHere();
    userPage.enterUserHomeFacility(DEFAULT_PARENT_FACILITY_CODE);
    userPage.verifyNoMatchedFoundMessage();
    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyFacilityUpload(String user, String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadAndVerifyGeographicZone("QA_Geographic_Data_WebService.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadFacilities("QA_facilities_WebService.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(FALSE_FLAG, dbWrapper.getVirtualPropertyOfFacility("facilityf10"));
    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldManageFacility(String user, String program, String[] credentials) throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    String agentCode = "ABC";

    agentJson.setAgentCode(agentCode);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ManageFacilityPage manageFacilityPage = homePage.navigateSearchFacility();
    manageFacilityPage.searchFacility(agentCode);
    manageFacilityPage.clickFacilityList(agentCode);
    manageFacilityPage.disableFacility(agentCode, DEFAULT_AGENT_NAME);

    manageFacilityPage.verifyDisabledFacility(agentCode, DEFAULT_AGENT_NAME);
    HomePage homePageRestore = manageFacilityPage.enableFacility();

    ManageFacilityPage manageFacilityPageRestore = homePageRestore.navigateSearchFacility();
    manageFacilityPageRestore.searchFacility(agentCode);
    manageFacilityPageRestore.clickFacilityList(agentCode);
    manageFacilityPage.saveFacility();
    manageFacilityPage.verifyMessageOnFacilityScreen(DEFAULT_AGENT_NAME, "updated");
    assertEquals(TRUE_FLAG, dbWrapper.getVirtualPropertyOfFacility(agentCode));
    homePage.logout(baseUrlGlobal);

  }


  @Test(groups = {"webservice"})
  public void testChwFeedWithValidParentFacilityCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    assertEquals(dbWrapper.getRequisitionGroupId(DEFAULT_PARENT_FACILITY_CODE), dbWrapper.getRequisitionGroupId(DEFAULT_AGENT_CODE));
    List<Integer> listOfProgramsSupportedByParentFacility = new ArrayList();
    listOfProgramsSupportedByParentFacility = dbWrapper.getAllProgramsOfFacility(DEFAULT_PARENT_FACILITY_CODE);
    List<Integer> listOfProgramsSupportedByVirtualFacility = new ArrayList();
    listOfProgramsSupportedByVirtualFacility = dbWrapper.getAllProgramsOfFacility(DEFAULT_AGENT_CODE);
    Set<Integer> setOfProgramsSupportedByParentFacility = new HashSet<>();
    setOfProgramsSupportedByParentFacility.addAll(listOfProgramsSupportedByParentFacility);
    Set<Integer> setOfProgramsSupportedByVirtualFacility = new HashSet<>();
    setOfProgramsSupportedByVirtualFacility.addAll(listOfProgramsSupportedByVirtualFacility);
    assertTrue(setOfProgramsSupportedByParentFacility.equals(setOfProgramsSupportedByVirtualFacility));
    assertEquals(listOfProgramsSupportedByParentFacility.size(),listOfProgramsSupportedByVirtualFacility.size());
    for(Integer programId : listOfProgramsSupportedByParentFacility){
      assertEquals(dbWrapper.getProgramFieldForProgramIdAndFacilityCode(programId, DEFAULT_PARENT_FACILITY_CODE, "active"), dbWrapper.getProgramFieldForProgramIdAndFacilityCode(programId, DEFAULT_AGENT_CODE, "active"));
      assertEquals(dbWrapper.getProgramStartDateForProgramIdAndFacilityCode(programId,DEFAULT_PARENT_FACILITY_CODE),dbWrapper.getProgramStartDateForProgramIdAndFacilityCode(programId,DEFAULT_AGENT_CODE));
    }
  }



  @Test(groups = {"webservice"})
  public void testUpdateChwFeedForEnableScenarios() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");

    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    agentJson.setActive("false");
    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));

    dbWrapper.updateFacilityFieldBYCode("enabled", "false", DEFAULT_AGENT_CODE);

    ResponseEntity responseEntityEnabledFalse = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");

    assertTrue("Showing response as : " + responseEntityEnabledFalse.getResponse(),
        responseEntityEnabledFalse.getResponse().contains("{\"error\":\"CHW cannot be updated as it has been deleted\"}"));

  }

  @Test(groups = {"webservice"})
  public void testChwFeedCreateWithInvalidDataLength() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber("0099887766759785759859757757887");
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"error\":\"Incorrect data length\"}"));
  }

  @Test(groups = {"webservice"})
  public void testChwFeedUpdateWithInvalidDataLength() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber("0099887");
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");

    agentJson.setPhoneNumber("0099887766759785759859757757887");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
        responseEntityUpdated.getResponse().contains("{\"error\":\"Incorrect data length\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateStatusOfAgentCode() throws Exception {
    String AGENT_CODE = "ABCD";
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    agentJson.setActive("false");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
        responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));

    assertEquals(FALSE_FLAG, dbWrapper.getActivePropertyOfFacility(AGENT_CODE));
  }

  @Test(groups = {"webservice"})
  public void testVerifyFieldsAfterChangeInParentFacilityCode() throws Exception {
    String typeId = "typeId";
    String geographicZoneId = "geographicZoneId";
    String parentFacilityId = "parentFacilityId";
    String agentCode = "ABCDE";
    String firstParentFacility = DEFAULT_PARENT_FACILITY_CODE;
    String updateParentFacility = "F11";
    String id = "id";


    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(agentCode);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(firstParentFacility);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    assertEquals(dbWrapper.getFacilityFieldBYCode(typeId, firstParentFacility), dbWrapper.getFacilityFieldBYCode(typeId, agentCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(geographicZoneId, firstParentFacility), dbWrapper.getFacilityFieldBYCode(geographicZoneId, agentCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(id, firstParentFacility), dbWrapper.getFacilityFieldBYCode(parentFacilityId, agentCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode("name", agentCode), DEFAULT_AGENT_NAME);
    assertNotEquals(dbWrapper.getFacilityFieldBYCode("id", agentCode), dbWrapper.getFacilityFieldBYCode("id", firstParentFacility));
    assertEquals(dbWrapper.getFacilityFieldBYCode("code", agentCode), agentCode);
    assertNull(dbWrapper.getFacilityFieldBYCode("description", agentCode));
    assertNull(dbWrapper.getFacilityFieldBYCode("gln", agentCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode("mainPhone", agentCode), PHONE_NUMBER);
    assertNull(dbWrapper.getFacilityFieldBYCode("fax", agentCode));
    assertNull(dbWrapper.getFacilityFieldBYCode("address1", agentCode));
    assertNull(dbWrapper.getFacilityFieldBYCode("address2", agentCode));
    assertNull(dbWrapper.getFacilityFieldBYCode("catchmentPopulation", agentCode));
    assertNull(dbWrapper.getFacilityFieldBYCode("operatedById", agentCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode("active", agentCode), "t");
    assertEquals(dbWrapper.getFacilityFieldBYCode("enabled", agentCode), TRUE_FLAG);
    assertEquals(dbWrapper.getFacilityFieldBYCode("virtualFacility", agentCode), TRUE_FLAG);
    assertEquals(dbWrapper.getRequisitionGroupId(firstParentFacility) , dbWrapper.getRequisitionGroupId(agentCode));

    agentJson.setParentFacilityCode(updateParentFacility);

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + agentCode + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
    responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));
    assertEquals(dbWrapper.getFacilityFieldBYCode(typeId, updateParentFacility), dbWrapper.getFacilityFieldBYCode(typeId, agentCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(geographicZoneId, updateParentFacility), dbWrapper.getFacilityFieldBYCode(geographicZoneId, agentCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(id, updateParentFacility), dbWrapper.getFacilityFieldBYCode(parentFacilityId, agentCode));
    assertEquals(dbWrapper.getRequisitionGroupId(updateParentFacility) , dbWrapper.getRequisitionGroupId(agentCode));

    List<Integer> listOfProgramsSupportedByParentFacility = new ArrayList();
    listOfProgramsSupportedByParentFacility = dbWrapper.getAllProgramsOfFacility(updateParentFacility);
    List<Integer> listOfProgramsSupportedByVirtualFacility = new ArrayList();
    listOfProgramsSupportedByVirtualFacility = dbWrapper.getAllProgramsOfFacility(agentCode);
    Set<Integer> setOfProgramsSupportedByParentFacility = new HashSet<>();
    setOfProgramsSupportedByParentFacility.addAll(listOfProgramsSupportedByParentFacility);
    Set<Integer> setOfProgramsSupportedByVirtualFacility = new HashSet<>();
    setOfProgramsSupportedByVirtualFacility.addAll(listOfProgramsSupportedByVirtualFacility);
    assertTrue(setOfProgramsSupportedByParentFacility.equals(setOfProgramsSupportedByVirtualFacility));
    assertEquals(listOfProgramsSupportedByParentFacility.size(),listOfProgramsSupportedByVirtualFacility.size());
    for(Integer programId : listOfProgramsSupportedByParentFacility){
       assertEquals(dbWrapper.getProgramFieldForProgramIdAndFacilityCode(programId, updateParentFacility, "active"), dbWrapper.getProgramFieldForProgramIdAndFacilityCode(programId, agentCode, "active"));
       assertEquals(dbWrapper.getProgramStartDateForProgramIdAndFacilityCode(programId,updateParentFacility),dbWrapper.getProgramStartDateForProgramIdAndFacilityCode(programId,agentCode));
    }
  }

  @Test(groups = {"webservice"})
  public void testVerifyFieldsAfterCHWCreation() throws Exception {
    String typeId = "typeid";
    String geographicZoneId = "geographiczoneid";
    String parentFacilityId = "parentfacilityid";
    String agentCode = "commtrk";
    String agentName = DEFAULT_AGENT_NAME;
    String agentNameUpdated = "AgentJyot";
    String firstParentFacility = DEFAULT_PARENT_FACILITY_CODE;
    String firstParentFacilityUpdated = "F11";
    String code = "code";
    String name = "name";
    String id = "id";
    String mainPhone = "mainphone";
    String phoneNumber = PHONE_NUMBER;
    String phoneNumberUpdated = "12345678";
    String active = "active";
    String virtualFacility = "virtualfacility";
    String sdp = "sdp";
    String enabled = "enabled";


    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(agentCode);
    agentJson.setAgentName(agentName);
    agentJson.setParentFacilityCode(firstParentFacility);
    agentJson.setPhoneNumber(phoneNumber);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    assertEquals(dbWrapper.getFacilityFieldBYCode(typeId, firstParentFacility), dbWrapper.getFacilityFieldBYCode(typeId, agentCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(geographicZoneId, firstParentFacility), dbWrapper.getFacilityFieldBYCode(geographicZoneId, agentCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(id, firstParentFacility), dbWrapper.getFacilityFieldBYCode(parentFacilityId, agentCode));
    assertEquals(agentCode, dbWrapper.getFacilityFieldBYCode(code, agentCode));
    assertEquals(agentName, dbWrapper.getFacilityFieldBYCode(name, agentCode));
    assertEquals(phoneNumber, dbWrapper.getFacilityFieldBYCode(mainPhone, agentCode));

    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(active, agentCode));
    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(virtualFacility, agentCode));
    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(sdp, agentCode));
    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(enabled, agentCode));

    agentJson.setAgentName(agentNameUpdated);
    agentJson.setParentFacilityCode(firstParentFacilityUpdated);
    agentJson.setPhoneNumber(phoneNumberUpdated);
    agentJson.setActive("false");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + agentCode + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
        responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));
    assertEquals(dbWrapper.getFacilityFieldBYCode(typeId, firstParentFacilityUpdated), dbWrapper.getFacilityFieldBYCode(typeId, agentCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(geographicZoneId, firstParentFacilityUpdated), dbWrapper.getFacilityFieldBYCode(geographicZoneId, agentCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(id, firstParentFacilityUpdated), dbWrapper.getFacilityFieldBYCode(parentFacilityId, agentCode));
    assertEquals(agentCode, dbWrapper.getFacilityFieldBYCode(code, agentCode));
    assertEquals(agentNameUpdated, dbWrapper.getFacilityFieldBYCode(name, agentCode));
    assertEquals(phoneNumberUpdated, dbWrapper.getFacilityFieldBYCode(mainPhone, agentCode));
    assertEquals(FALSE_FLAG, dbWrapper.getFacilityFieldBYCode(active, agentCode));
    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(virtualFacility, agentCode));
    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(sdp, agentCode));
    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(enabled, agentCode));
  }

  @Test(groups = {"webservice"})
  public void testCreateChwFeedWithParentFacilityCodeAsVirtualFacility() throws Exception {
    dbWrapper.updateVirtualPropertyOfFacility(DEFAULT_PARENT_FACILITY_CODE, ACTIVE_STATUS);
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"error\":\"Parent facility can not be virtual facility\"}"));
  }

  @Test(groups = {"webservice"})
  public void testUpdateChwFeedWithParentFacilityCodeAsVirtualFacility() throws Exception {
    String facilityCode = DEFAULT_PARENT_FACILITY_CODE;

    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(facilityCode);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");

    dbWrapper.updateVirtualPropertyOfFacility(facilityCode, ACTIVE_STATUS);

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
        responseEntityUpdated.getResponse().contains("{\"error\":\"Parent facility can not be virtual facility\"}"));
  }


  @Test(groups = {"webservice"})
  public void testChwFeedWithAgentCodeAlreadyRegistered() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"error\":\"Agent already registered\"}"));
  }


  @Test(groups = {"webservice"})
  public void testUpdateShouldVerifyAgentIsNotAVirtualFacility() throws Exception {
    String Agent_code = "F11";
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + Agent_code + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"error\":\"Agent is not a virtual facility\"}"));
  }

  @Test(groups = {"webservice"})
  public void testCreateChwFeedWithInvalidParentFacilityCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode("A10");
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"error\":\"Invalid Facility code\"}"));
  }

  @Test(groups = {"webservice"})
  public void testUpdateChwFeedWithInvalidParentFacilityCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");

    agentJson.setParentFacilityCode("A10");
    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
        responseEntityUpdated.getResponse().contains("{\"error\":\"Invalid Facility code\"}"));
  }

  @Test(groups = {"webservice"})
  public void testMalformedJson() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedJson = getJsonStringFor(agentJson).replace(':', ';');

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedJson,
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");

    assertEquals(responseEntityUpdated.getStatus(), SC_BAD_REQUEST);

  }


  @Test(groups = {"webservice"})
  public void testBlankJson() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    ResponseEntity responseEntity = client.SendJSON("{}",
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldsWhenFieldIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst("\"agentName\":\"AgentVinod\",", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
        responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenFieldIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    String modifiedString = getJsonStringFor(agentJson).replaceFirst("\"agentName\":\"AgentVinod\",", " ");

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " modifiedString : " + modifiedString,
        responseEntityUpdated.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldValueWhenFieldIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst("\"agentName\":\"AgentVinod\",", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
        responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldValueWhenFieldIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);


    client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");

    String modifiedString = getJsonStringFor(agentJson).replaceFirst("\"agentName\":\"AgentVinod\",", " ");

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");


    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " modifiedString : " + modifiedString,
        responseEntityUpdated.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldsWhenActiveFieldIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst(", \"active\":\"true\"", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
        responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateAgentCodeNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);


    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " +
        getJsonStringFor(agentJson), responseEntity.getResponse().contains("{\"error\":\"Invalid agent code\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenActiveFieldIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");


    String modifiedString = getJsonStringFor(agentJson).replaceFirst(",\"active\":\"true\"", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
        responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldsWhenFieldValueIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst(DEFAULT_AGENT_NAME, "");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
        responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenFieldValueIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");

    String modifiedString = getJsonStringFor(agentJson).replaceFirst(DEFAULT_AGENT_NAME, "");

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " modifiedString : " + modifiedString,
        responseEntityUpdated.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldsWhenActiveFieldValueIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst(ACTIVE_STATUS, "");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
        responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenActiveFieldValueIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");


    String modifiedString = getJsonStringFor(agentJson).replaceFirst(ACTIVE_STATUS, "");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
        responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenActiveFieldValueIsNotCorrect() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");


    String modifiedString = getJsonStringFor(agentJson).replaceFirst(ACTIVE_STATUS, " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
        responseEntity.getResponse().contains("Active should be True/False"));

  }

  @Test(groups = {"webservice"})
  public void testUpdatedSuccessfully() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUnrecognizedField() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst("phoneNumber", "phonenumber");

    ResponseEntity responseEntity = client.SendJSON(modifiedString,
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");


    assertEquals(responseEntity.getStatus(), SC_BAD_REQUEST);
  }

  @Test(groups = {"webservice"})
  public void testCaseSensitiveCheckForCreateCHW() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode("casesensitive");
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    agentJson.setAgentCode("CASESENSITIVE");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " updated json : " +
        getJsonStringFor(agentJson), responseEntityUpdated.getResponse().contains("{\"error\":\"Agent already registered\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCaseSensitiveCheckForUpdateCHW() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    String agent_code = "casesensitive";
    agentJson.setAgentCode(agent_code);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    String agent_code_updated = "CASESENSITIVE";
    agentJson.setAgentCode(agent_code_updated);


    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + agent_code_updated + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " updated json : " +
        getJsonStringFor(agentJson), responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));

  }

  @Test(groups = {"webservice"})
  public void testInvalidActiveFieldOption() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst(ACTIVE_STATUS, "truefalse");

    ResponseEntity responseEntity = client.SendJSON(modifiedString,
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
        responseEntity.getResponse().contains("Active should be True/False"));

  }

  @Test(groups = {"webservice"})
  public void testCreateInvalidAuthenticationToken() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        commTrackUser,
        "Testing");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("Authentication Failed"));


  }

  @Test(groups = {"webservice"})
  public void testUpdateInvalidAuthenticationToken() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        commTrackUser,
        "Testing");
    assertEquals(responseEntity.getStatus(), SC_UNAUTHORIZED);
  }

  @Test(groups = {"webservice"})
  public void testCreateInvalidUserName() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        CREATE_URL,
        POST,
        "Testing",
        "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("Authentication Failed"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateInvalidUserName() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
        UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
        PUT,
        "Testing",
        "Admin123");
    assertEquals(responseEntity.getStatus(), SC_UNAUTHORIZED);
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
        {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}

