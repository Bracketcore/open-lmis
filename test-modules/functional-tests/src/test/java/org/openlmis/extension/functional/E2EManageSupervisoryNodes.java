/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.extension.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.extension.pageobjects.ManageSupervisoryNodesPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;

@TransactionConfiguration(defaultRollback = true)
@Transactional
@Listeners
public class E2EManageSupervisoryNodes extends TestCaseHelper {

    @BeforeMethod(groups = {"functional2"})
    public void setup() throws Exception {
        super.setup();
    }

    @Test(enabled=true, groups = {"functional2"},dataProvider = "Data-Provider-Function-Positive")
    public void testE2EManageSupervisoryNodesAddNewSupGroup (String [] credentials ) throws IOException{
        LoginPage loginPage = new LoginPage(testWebDriver,baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0],credentials[1]);
//        ManageSupervisoryNodesPage manageSupervisoryNodesPage = homePage.navigateToSupervisoryNode();
//        manageSupervisoryNodesPage.EnterAndVerifyNewSupervisoryNode("Code123","Test123","1","Test Description","2");
    }

    @Test(enabled=true, groups = {"functional2"},dataProvider = "Data-Provider-Function-Positive")
    public void testE2EManageSupervisoryNodesAddNewSupGroupWithEmptyFacility (String [] credentials ) throws IOException{
        LoginPage loginPage = new LoginPage(testWebDriver,baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0],credentials[1]);
//        ManageSupervisoryNodesPage manageSupervisoryNodesPage = homePage.navigateToSupervisoryNode();
//        manageSupervisoryNodesPage.EnterAndVerifyNewSupervisoryNodeWOFacility("Code1234","Test1234","1","Test Description");
    }

    @Test(enabled=true, groups = {"functional2"},dataProvider = "Data-Provider-Function-Positive")
    public void testE2EManageSupervisoryNodesAddNewSupGroupWithEmptyNameAndCode (String [] credentials ) throws IOException{
        LoginPage loginPage = new LoginPage(testWebDriver,baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0],credentials[1]);
//        ManageSupervisoryNodesPage manageSupervisoryNodesPage = homePage.navigateToSupervisoryNode();
//        manageSupervisoryNodesPage.EnterAndVerifyNewSupervisoryNodeWONameAndCode("1","Test Description","5");
    }

    @AfterMethod
    public void tearDown() throws IOException{
        HomePage homePage= new HomePage(testWebDriver);
        homePage.logout(baseUrlGlobal);
    }

    @DataProvider(name="Data-Provider-Function-Positive")
    public Object[] parameterForTheTest(){
        return new Object[]{
                new String[]{"Admin123","Admin123"}
        };
    }



}
