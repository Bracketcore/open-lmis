package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

@TransactionConfiguration(defaultRollback=true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EUpload extends TestCaseHelper {

    @BeforeClass
    public void setUp() throws Exception
    {
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteData();
        dbWrapper.deleteFacilities();
    }

    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void uploadCSVFiles(String user, String[] credentials) throws Exception {

        LoginPage loginPage=new LoginPage(testWebDriver);

        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
        TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate("HIV");
        templateConfigPage.configureTemplate();

        RolesPage rolesPage = homePage.navigateRoleAssignments();
        List<String> userRoleList = new ArrayList<>();
        userRoleList.add("Create Requisition");

        rolesPage.createRole("User", "User", userRoleList);

        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.insertUser("200", "User123", "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==");

        dbWrapper.insertRoleAssignment("User");

        UploadPage uploadPage = homePage.navigateUploads();

        uploadPage.uploadProducts();
        testWebDriver.setImplicitWait(2500);

        uploadPage.uploadProgramProductMapping();
        testWebDriver.setImplicitWait(2500);

        uploadPage.uploadFacilities();
        testWebDriver.setImplicitWait(2500);

        dbWrapper.insertFacilityApprovedProducts();
        dbWrapper.allocateFacilityToUser();

        uploadPage.uploadProgramSupportedByFacilities();
        testWebDriver.setImplicitWait(2500);

        uploadPage.uploadSupervisoryNodes();
        testWebDriver.setImplicitWait(2500);

        uploadPage.uploadRequisitionGroup();
        testWebDriver.setImplicitWait(2500);

        uploadPage.uploadRequisitionGroupProgramSchedule();
        testWebDriver.setImplicitWait(2500);

        uploadPage.uploadRequisitionGroupMembers();
        testWebDriver.setImplicitWait(2500);

        uploadPage.uploadSupplyLines();
        testWebDriver.setImplicitWait(2500);



        LoginPage loginPageSecond=homePage.logout();
        HomePage homePageUser = loginPageSecond.loginAs("User123", "User123");

        InitiateRnRPage initiateRnRPage = homePageUser.navigateAndInitiateRnr("F11","F11 Village Dispensary", "", "HIV");
        initiateRnRPage.verifyRnRHeader("F11","F11 Village Dispensary", "", "HIV");

    }
    @AfterClass
    public void tearDown() throws Exception
    {
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteData();

    }

    @DataProvider(name = "Data-Provider-Function-Positive")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {"User123", new String[]{"Admin123", "Admin123"}}
        };
    }
}
