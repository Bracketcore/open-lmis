/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openqa.selenium.support.How.XPATH;


public class ViewOrdersPage extends RequisitionPage {

  @FindBy(how = How.ID, using = "NoRequisitionsPendingMessage")
  private static WebElement NoRequisitionsPendingMessage;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col0 colt0']/span")
  private static WebElement orderNumberOnViewOrdersScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
  private static WebElement programOnViewOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ng-scope ngRow even']/div[2]/div[2]/div/span")
  private static WebElement facilityCodeNameOnOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ng-scope ngRow even']/div[4]/div[2]/div/span")
  private static WebElement periodDetailsOnViewOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ng-scope ngRow even']/div[5]/div[2]/div/span")
  private static WebElement supplyDepotOnViewOrderScreen;

  @FindBy(how = How.XPATH, using = "(//div[@id='orderStatus'])[1]")
  private static WebElement orderStatusOnViewOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']")
  private static WebElement successMessageDiv;

  @FindBy(how = How.XPATH, using = "//div[@id='NoRequisitionsPendingMessage']")
  private static WebElement noRequisitionPendingMessage;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Download CSV')]")
  private static WebElement downloadCSVLink;

  @FindBy(how = How.XPATH, using = "//span[contains(text(),'No products in this order')]")
  private static WebElement noOrderMessage;

  @FindBy(how = XPATH, using = "//i[@class='icon-ok']")
  private static WebElement emergencyIcon;

  @FindBy(how = XPATH, using = "//span[@openlmis-message='message.no.order']")
  private static WebElement noRequisitionReleasedAsOrderYet;

  public ViewOrdersPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void verifyNoRequisitionReleasedAsOrderMessage()
  {
    testWebDriver.waitForPageToLoad();
    noRequisitionReleasedAsOrderYet.isDisplayed();
  }

  public void isFirstRowPresent()
  {
    testWebDriver.waitForPageToLoad();
    assertTrue("First row should show up",programOnViewOrderScreen.isDisplayed());
  }

  public void verifyOrderListElements(String program, String orderNumber, String facilityCodeName, String periodDetails, String supplyFacilityName, String orderStatus, boolean downloadLinkPresent) throws IOException {
    testWebDriver.refresh();
    testWebDriver.waitForElementToAppear(programOnViewOrderScreen);
    SeleneseTestNgHelper.assertEquals(programOnViewOrderScreen.getText().trim(), program);
    SeleneseTestNgHelper.assertEquals(orderNumberOnViewOrdersScreen.getText().trim(), orderNumber);
    SeleneseTestNgHelper.assertEquals(facilityCodeNameOnOrderScreen.getText().trim(), facilityCodeName);
    SeleneseTestNgHelper.assertEquals(periodDetailsOnViewOrderScreen.getText().trim(), periodDetails);
    SeleneseTestNgHelper.assertEquals(supplyDepotOnViewOrderScreen.getText().trim(), supplyFacilityName);
    SeleneseTestNgHelper.assertEquals(orderStatusOnViewOrderScreen.getText().trim(), orderStatus);
    if (downloadLinkPresent)
      SeleneseTestNgHelper.assertTrue("'Download CSV' link should show up", downloadCSVLink.isDisplayed());
    else
      SeleneseTestNgHelper.assertTrue("'No products in this order' message should show up", noOrderMessage.isDisplayed());
  }

  public void downloadCSV() throws IOException {
    testWebDriver.waitForElementToAppear(programOnViewOrderScreen);
    downloadFileWhileSaveDialogOPen(downloadCSVLink);
  }

  public void verifyEmergencyStatus() throws IOException {
    testWebDriver.waitForElementToAppear(emergencyIcon);
    assertTrue("Emergency icon should show up", emergencyIcon.isDisplayed());
  }

  public int getNumberOfLineItems() throws IOException {
    int number = 0;
    number = testWebDriver.getElementsSizeByXpath("html/body/div[1]/div/div/div/div[3]/div/div[2]/div/div");
    return number;
  }

  public void verifyProgram(int row, String program) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("(//div[@class='ngCellText ng-scope col2 colt2']/span)[" + row + "]"));
    String actualProgram = testWebDriver.getElementByXpath("(//div[@class='ngCellText ng-scope col2 colt2']/span)[" + row + "]").getText();
    SeleneseTestNgHelper.assertEquals(actualProgram, program);
  }


}