package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;


public class OrderPage extends Page {

  @FindBy(how = How.ID, using = "NoRequisitionsPendingMessage")
  private static WebElement NoRequisitionsPendingMessage;

  @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[@class='ngCell col1 ']/div/span")
  private static WebElement programOnOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[@class='ngCell col2 ']/div/span")
  private static WebElement facilityCodeOnOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[@class='ngCell col3 ']/div/span")
  private static WebElement facilityNameOnOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[@class='ngCell col4 ']/div/span")
  private static WebElement periodStartDateOnOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[@class='ngCell col5 ']/div/span")
  private static WebElement periodEndDateOnOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[@class='ngCell col8 ']/div/span")
  private static WebElement supplyDepotOnOrderScreen;

  @FindBy(how = How.XPATH, using = "//input[@class='ngSelectionCheckbox']")
  private static WebElement checkboxOnOrderScreen;

  @FindBy(how = How.XPATH, using = "//input[@value='Convert To Order']")
  private static WebElement convertToOrderButton;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']")
  private static WebElement successMessageDiv;

  @FindBy(how = How.XPATH, using = "//div[@id='NoRequisitionsPendingMessage']")
  private static WebElement noRequisitionPendingMessage;

  public OrderPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }


  public void verifyOrderListElements(String program, String facilityCode, String facilityName, String periodStartDate, String periodEndDate, String supplyFacilityName) throws IOException {
    testWebDriver.waitForElementToAppear(programOnOrderScreen);
    SeleneseTestNgHelper.assertEquals(programOnOrderScreen.getText().trim(), program);
    SeleneseTestNgHelper.assertEquals(facilityCodeOnOrderScreen.getText().trim(), facilityCode);
    SeleneseTestNgHelper.assertEquals(facilityNameOnOrderScreen.getText().trim(), facilityName);
    SeleneseTestNgHelper.assertEquals(periodStartDateOnOrderScreen.getText().trim(), periodStartDate);
    SeleneseTestNgHelper.assertEquals(periodEndDateOnOrderScreen.getText().trim(), periodEndDate);
    SeleneseTestNgHelper.assertEquals(supplyDepotOnOrderScreen.getText().trim(), supplyFacilityName);

  }

  public void convertToOrder() throws IOException {
    testWebDriver.waitForElementToAppear(checkboxOnOrderScreen);
    testWebDriver.waitForElementToAppear(convertToOrderButton);
    convertToOrderButton.click();
    testWebDriver.sleep(500);
    SeleneseTestNgHelper.assertTrue("Message 'Please select atleast one Requisition for Converting to Order.' is not displayed", successMessageDiv.isDisplayed());
    checkboxOnOrderScreen.click();
    convertToOrderButton.click();
    testWebDriver.sleep(1000);
    SeleneseTestNgHelper.assertTrue("Message 'No R&Rs are pending to be Converted as Orders' is not displayed", noRequisitionPendingMessage.isDisplayed());
    SeleneseTestNgHelper.assertTrue("Message 'The requisition(s) have been successfully converted to Orders' is not displayed", successMessageDiv.isDisplayed());
  }


}