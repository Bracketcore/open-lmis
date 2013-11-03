/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.extension.functional.reports;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.extension.pageobjects.StockImbalanceByFacilityPage;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class StockImbalanceByFacilityReport extends ReportTestHelper {

    public static final String STORE_IN_CHARGE = "store in-charge";
    public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
    public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
    public static final String SUBMITTED = "SUBMITTED";
    public static final String AUTHORIZED = "AUTHORIZED";
    public static final String IN_APPROVAL = "IN_APPROVAL";
    public static final String APPROVED = "APPROVED";
    public static final String RELEASED = "RELEASED";
    public static final String TABLE_CELL_XPATH_TEMPLATE = "//div[@id='wrap']/div/div/div/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span";
    public static final String TABLE_SORT_BUTTON_XPATH_TEMPLATE = "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div";

    private static final Integer SUPPLYING_FACILITY = 1;
    private static final Integer FACILITY = 2;
    private static final Integer PRODUCT = 3;
    private static final Integer PHYSICAL_COUNT = 4;
    private static final Integer AMC = 5;
    private static final Integer MOS = 6;
    private static final Integer ORDER_QUANITY = 7;

    private ReportHomePage homePage;
    private ReportLoginPage loginPage;
    private StockImbalanceByFacilityPage stockImbalanceByFacilityPage;

    @BeforeMethod(groups = {"report"})
    public void setUp() throws Exception {
        super.setup();
    }


    private void navigateToStockImbalanceByFacilityPage(String userName, String passWord) throws IOException {
        login(userName, passWord);
        stockImbalanceByFacilityPage = homePage.navigateViewStockImbalanceByFacilityPage();
    }

    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportFiltersRendered(String[] credentials) throws Exception {
        navigateToStockImbalanceByFacilityPage(credentials[0], credentials[1]);

        System.out.println();
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityCodeIsDisplayed());
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityNameIsDisplayed());
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityTypeIsDisplayed());

        navigateToStockImbalanceByFacilityPage(credentials[0], credentials[1]);
        enterFilterValues();

    }

    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPDFOUtput(String[] credentials) throws Exception {
        navigateToStockImbalanceByFacilityPage(credentials[0], credentials[1]);
        verifyPdfReportOutput("pdf-button");
    }


    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyXLSOUtput(String[] credentials) throws Exception {
        navigateToStockImbalanceByFacilityPage(credentials[0], credentials[1]);
        verifyXlsReportOutput("xls-button");
    }

    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifySorting(String[] credentials) throws IOException {
        navigateToStockImbalanceByFacilityPage(credentials[0], credentials[1]);

        Map<String, String> templates = new HashMap<String, String>() {{
            put(SORT_BUTTON_ASC_TEMPLATE, "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(SORT_BUTTON_DESC_TEMPLATE, "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");

            put(TABLE_CELL_TEMPLATE, "test");
        }};

        verifySort("ASC", SUPPLYING_FACILITY, templates);
        verifySort("ASC", FACILITY, templates);
        verifySort("ASC", PRODUCT, templates);
        verifySort("ASC", PHYSICAL_COUNT, templates);
        verifySort("ASC", AMC, templates);
        verifySort("ASC", MOS, templates);
        verifySort("ASC", ORDER_QUANITY, templates);

    }


    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPagination(String[] credentials) throws Exception {
        navigateToStockImbalanceByFacilityPage(credentials[0], credentials[1]);


        Map<String, String> templates = new HashMap<String, String>() {{
            put(PAGINATION_BUTTON_PREV_TEMPLATE, "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_NEXT_TEMPLATE, "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_FIRST_TEMPLATE, "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_LAST_TEMPLATE, "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(TABLE_CELL_TEMPLATE, "//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span");
        }};
        verifyPagination(templates);
    }

    public void enterFilterValues() {

        testWebDriver.findElement(By.cssSelector("b")).click();
        new Select(testWebDriver.findElement(By.id("startYear"))).selectByVisibleText("2011");
        new Select(testWebDriver.findElement(By.id("startMonth"))).selectByVisibleText("Jan");
        new Select(testWebDriver.findElement(By.name("endYear"))).selectByVisibleText("2011");
        new Select(testWebDriver.findElement(By.name("endMonth"))).selectByVisibleText("Jan");
        testWebDriver.findElement(By.name("productCategoryElement")).click();
        testWebDriver.findElement(By.id("product")).click();
        testWebDriver.findElement(By.name("facilityTypeElement")).click();
        testWebDriver.findElement(By.name("requisitionGroup")).click();
    }


    @AfterMethod(groups = {"report"})
    public void tearDown() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        homePage.logout(baseUrlGlobal);
        //dbWrapper.deleteData();
        dbWrapper.closeConnection();
    }

    @DataProvider(name = "Data-Provider-Function-Positive")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {new String[]{"Admin123", "Admin123", "storeincharge", "Admin123"}}
        };
    }

}
