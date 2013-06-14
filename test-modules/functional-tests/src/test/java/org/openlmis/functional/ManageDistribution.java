/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestBase.fail;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageDistribution extends TestCaseHelper {

  public static final String NONE_ASSIGNED = "--None Assigned--";
  public static final String SELECT_DELIVERY_ZONE = "--Select Delivery Zone--";
  public static final String periodDisplayedByDefault = "Period14";
  public static final String periodNotToBeDisplayedInDropDown = "Period1";

  @BeforeMethod(groups = {"functional2"})
  public void setUp() throws Exception {
    super.setup();
  }


  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testManageDistribution(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                     String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                     String facilityCodeFirst, String facilityCodeSecond,
                                     String program, String schedule, String period, Integer totalNumberOfPeriods) throws Exception {

    List<String> rightsList = new ArrayList<String>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnR(true, program, userSIC, "200", "openLmis", rightsList);
    setupDataForDeliveryZone(deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      program, schedule);


    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    verifyElementsPresent(distributionPage);

    String defaultDistributionZoneValuesToBeVerified = NONE_ASSIGNED;
    verifySelectedOptionFromDeliveryZoneSelectField(distributionPage, defaultDistributionZoneValuesToBeVerified);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    homePage.navigateHomePage();
    homePage.navigatePlanDistribution();

    List<String> distributionZoneValuesToBeVerified = new ArrayList<String>();
    distributionZoneValuesToBeVerified.add(deliveryZoneNameFirst);
    verifyDeliveryZoneSelectFieldValues(distributionPage, distributionZoneValuesToBeVerified);

    String defaultProgramValuesToBeVerified = NONE_ASSIGNED;
    verifySelectedOptionFromProgramSelectField(distributionPage, defaultProgramValuesToBeVerified);

    String defaultPeriodValuesToBeVerified = NONE_ASSIGNED;
    verifySelectedOptionFromPeriodSelectField(distributionPage, defaultPeriodValuesToBeVerified);

    selectValueFromDeliveryZone(distributionPage, deliveryZoneNameFirst);

    List<String> programValuesToBeVerified = new ArrayList<String>();
    programValuesToBeVerified.add(program);
    verifyProgramSelectFieldValues(distributionPage, programValuesToBeVerified);
    verifySelectedOptionFromPeriodSelectField(distributionPage, defaultPeriodValuesToBeVerified);

    selectValueFromProgram(distributionPage, program);

    List<String> periodValuesToBeVerified = new ArrayList<String>();

    verifySelectedOptionFromPeriodSelectField(distributionPage, periodDisplayedByDefault);
    for (int counter = 2; counter <= totalNumberOfPeriods; counter++) {
      String periodWithCounter = period + counter;
      periodValuesToBeVerified.add(periodWithCounter);
    }
    verifyPeriodSelectFieldValuesPresent(distributionPage, periodValuesToBeVerified);

    verifyPeriodSelectFieldValuesNotPresent(distributionPage, periodNotToBeDisplayedInDropDown);

    selectValueFromPeriod(distributionPage, periodDisplayedByDefault);

    verifySelectedOptionFromDeliveryZoneSelectField(distributionPage, deliveryZoneNameFirst);

    verifySelectedOptionFromProgramSelectField(distributionPage, program);

    verifySelectedOptionFromPeriodSelectField(distributionPage, periodDisplayedByDefault);

    selectValueFromDeliveryZone(distributionPage, SELECT_DELIVERY_ZONE);

    verifySelectedOptionFromProgramSelectField(distributionPage, defaultProgramValuesToBeVerified);

    verifySelectedOptionFromPeriodSelectField(distributionPage, defaultPeriodValuesToBeVerified);

    clickProceed(distributionPage);
    verifySubOptionsOfProceedButton(distributionPage);

  }

  private void verifyElementsPresent(DistributionPage distributionPage) {
    assertTrue("selectDeliveryZoneSelectBox should be present", distributionPage.getSelectDeliveryZoneSelectBox().isDisplayed());
    assertTrue("selectProgramSelectBox should be present", distributionPage.getSelectProgramSelectBox().isDisplayed());
    assertTrue("selectPeriodSelectBox should be present", distributionPage.getSelectPeriodSelectBox().isDisplayed());
    assertTrue("proceedButton should be present", distributionPage.getProceedButton().isDisplayed());
  }

  private void selectValueFromDeliveryZone(DistributionPage distributionPage, String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(distributionPage.getSelectDeliveryZoneSelectBox());
    testWebDriver.selectByVisibleText(distributionPage.getSelectDeliveryZoneSelectBox(), valueToBeSelected);
  }

  private void selectValueFromProgram(DistributionPage distributionPage, String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(distributionPage.getSelectProgramSelectBox());
    testWebDriver.selectByVisibleText(distributionPage.getSelectProgramSelectBox(), valueToBeSelected);
  }

  private void selectValueFromPeriod(DistributionPage distributionPage, String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(distributionPage.getSelectPeriodSelectBox());
    testWebDriver.selectByVisibleText(distributionPage.getSelectPeriodSelectBox(), valueToBeSelected);
  }

  private void verifyDeliveryZoneSelectFieldValues(DistributionPage distributionPage, List<String> valuesToBeVerified) {
    List<WebElement> selectFieldValues = testWebDriver.getOptions(distributionPage.getSelectDeliveryZoneSelectBox());
    int valuesInSelectFieldCounter = 0;
    int valuesToBeVerifiedCounter = 0;
    int finalCounter = 0;
    int counterOfFirstSelectValueToBeSkipped = 0;
    for (String values : valuesToBeVerified)
      valuesToBeVerifiedCounter++;
    for (WebElement webElement : selectFieldValues)
      valuesInSelectFieldCounter++;

    if (valuesToBeVerifiedCounter == valuesInSelectFieldCounter - 1) {
      for (WebElement webElement : selectFieldValues) {
        counterOfFirstSelectValueToBeSkipped++;
        if (counterOfFirstSelectValueToBeSkipped != 1) {
          for (String values : valuesToBeVerified) {
            if (values.equalsIgnoreCase(webElement.getText().trim())) {
              finalCounter++;
            }
          }

        }
      }
    } else {
      fail("Values in select field are not same in number as values to be verified");
    }
    assertEquals(valuesToBeVerifiedCounter, finalCounter);
    assertEquals(valuesInSelectFieldCounter - 1, finalCounter);
  }

  private void verifyProgramSelectFieldValues(DistributionPage distributionPage, List<String> valuesToBeVerified) {
    List<WebElement> selectFieldValues = testWebDriver.getOptions(distributionPage.getSelectProgramSelectBox());
    int valuesInSelectFieldCounter = 0;
    int valuesToBeVerifiedCounter = 0;
    int finalCounter = 0;
    int counterOfFirstSelectValueToBeSkipped = 0;
    for (String values : valuesToBeVerified)
      valuesToBeVerifiedCounter++;
    for (WebElement webElement : selectFieldValues)
      valuesInSelectFieldCounter++;

    if (valuesToBeVerifiedCounter == valuesInSelectFieldCounter - 1) {
      for (WebElement webElement : selectFieldValues) {
        counterOfFirstSelectValueToBeSkipped++;
        if (counterOfFirstSelectValueToBeSkipped != 1) {
          for (String values : valuesToBeVerified) {
            if (values.equalsIgnoreCase(webElement.getText().trim())) {
              finalCounter++;
            }
          }

        }
      }
    } else {
      fail("Values in select field are not same in number as values to be verified");
    }
    assertEquals(valuesToBeVerifiedCounter, finalCounter);
    assertEquals(valuesInSelectFieldCounter - 1, finalCounter);
  }

  private void verifyPeriodSelectFieldValuesPresent(DistributionPage distributionPage, List<String> valuesToBeVerified) {
    List<WebElement> selectFieldValues = testWebDriver.getOptions(distributionPage.getSelectPeriodSelectBox());
    int valuesInSelectFieldCounter = 0;
    int valuesToBeVerifiedCounter = 0;
    int finalCounter = 0;
    int counterOfFirstSelectValueToBeSkipped = 0;
    for (String values : valuesToBeVerified)
      valuesToBeVerifiedCounter++;
    for (WebElement webElement : selectFieldValues)
      valuesInSelectFieldCounter++;

    if (valuesToBeVerifiedCounter == valuesInSelectFieldCounter - 1) {
      for (WebElement webElement : selectFieldValues) {
        counterOfFirstSelectValueToBeSkipped++;
        if (counterOfFirstSelectValueToBeSkipped != 1) {
          for (String values : valuesToBeVerified) {
            if (values.equalsIgnoreCase(webElement.getText().trim())) {
              finalCounter++;
            }
          }

        }
      }
    } else {
      fail("Values in select field are not same in number as values to be verified");
    }
    assertEquals(valuesToBeVerifiedCounter, finalCounter);
    assertEquals(valuesInSelectFieldCounter - 1, finalCounter);
  }

  private void verifyPeriodSelectFieldValuesNotPresent(DistributionPage distributionPage, String valueToBeVerified) {
    List<WebElement> selectFieldValues = testWebDriver.getOptions(distributionPage.getSelectPeriodSelectBox());
    boolean flag = false;


    for (WebElement webElement : selectFieldValues) {
      if (valueToBeVerified.equalsIgnoreCase(webElement.getText().trim())) {
        flag = true;
        break;
      }

    }
    assertTrue(valueToBeVerified + " should not exist in period drop down", flag == false);
  }


  private void verifySelectedOptionFromDeliveryZoneSelectField(DistributionPage distributionPage, String valuesToBeVerified) {
    WebElement selectFieldValue = testWebDriver.getFirstSelectedOption(distributionPage.getSelectDeliveryZoneSelectBox());
    assertEquals(valuesToBeVerified, selectFieldValue.getText());
  }

  private void verifySelectedOptionFromProgramSelectField(DistributionPage distributionPage, String valuesToBeVerified) {
    WebElement selectFieldValue = testWebDriver.getFirstSelectedOption(distributionPage.getSelectProgramSelectBox());
    assertEquals(valuesToBeVerified, selectFieldValue.getText());
  }

  private void verifySelectedOptionFromPeriodSelectField(DistributionPage distributionPage, String valuesToBeVerified) {
    WebElement selectFieldValue = testWebDriver.getFirstSelectedOption(distributionPage.getSelectPeriodSelectBox());
    assertEquals(valuesToBeVerified, selectFieldValue.getText());
  }

  private void clickProceed(DistributionPage distributionPage) {
    testWebDriver.waitForElementToAppear(distributionPage.getProceedButton());
    distributionPage.getProceedButton().click();
    testWebDriver.waitForElementToAppear(distributionPage.getViewWarehouseLoadAmountLink());
  }

  private void verifySubOptionsOfProceedButton(DistributionPage distributionPage) {
    assertTrue("getViewWarehouseLoadAmount Link should be present", distributionPage.getViewWarehouseLoadAmountLink().isDisplayed());
    assertTrue("getInputFacilityData Link should be present", distributionPage.getInputFacilityDataLink().isDisplayed());
  }


  @AfterMethod(groups = {"functional2"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"storeincharge", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "M", "Period", 14}
    };

  }
}

