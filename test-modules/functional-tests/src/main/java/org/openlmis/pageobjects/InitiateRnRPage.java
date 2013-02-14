package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.sql.SQLException;


public class InitiateRnRPage extends Page {

  @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/h2")
  private static WebElement requisitionHeader;

  @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/div[@class='info-box']/div[@class='row-fluid'][1]/div[1]")
  private static WebElement facilityLabel;

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton;

  @FindBy(how = How.XPATH, using = "//input[@value='Submit']")
  private static WebElement submitButton;

  @FindBy(how = How.XPATH, using = "//input[@value='Authorize']")
  private static WebElement authorizeButton;


  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-bind='message']")
  private static WebElement successMessage;

  @FindBy(how = How.XPATH, using = "//div[@id='submitSuccessMsgDiv' and @ng-bind='submitMessage']")
  private static WebElement submitSuccessMessage;

  @FindBy(how = How.XPATH, using = "//div[@id='submitFailMessage' and @ng-bind='submitError']")
  private static WebElement submitErrorMessage;

  @FindBy(how = How.ID, using = "A_0")
  private static WebElement beginningBalance;

  @FindBy(how = How.ID, using = "B_0")
  private static WebElement quantityReceived;

  @FindBy(how = How.ID, using = "C_0")
  private static WebElement quantityDispensed;

  @FindBy(how = How.ID, using = "D_0")
  private static WebElement lossesAndAdjustments;

  @FindBy(how = How.ID, using = "E_7")
  private static WebElement stockOnHand;

  @FindBy(how = How.ID, using = "F_0")
  private static WebElement newPatient;

  @FindBy(how = How.XPATH, using = "//table[@id='fullSupplyTable']/tbody/tr[1]/td[13]/ng-switch/span/ng-switch/span")
  private static WebElement maximumStockQuantity;

  @FindBy(how = How.XPATH, using = "//table[@id='fullSupplyTable']/tbody/tr[1]/td[14]/ng-switch/span/ng-switch/span")
  private static WebElement caculatedOrderQuantity;

  @FindBy(how = How.ID, using = "J_0")
  private static WebElement requestedQuantity;

  @FindBy(how = How.XPATH, using = "//table[@id='fullSupplyTable']/tbody/tr[1]/td[11]/ng-switch/span/ng-switch/span")
  private static WebElement adjustedTotalConsumption;

  @FindBy(how = How.XPATH, using = "//table[@id='fullSupplyTable']/tbody/tr[1]/td[12]/ng-switch/span/ng-switch/span")
  private static WebElement amc;

  @FindBy(how = How.XPATH, using = "//table[@id='fullSupplyTable']/tbody/tr[1]/td[20]/ng-switch/span/ng-switch/span")
  private static WebElement totalCost;

  @FindBy(how = How.XPATH, using = "//table[@id='fullSupplyTable']/tbody/tr[1]/td[19]/ng-switch/span/ng-switch/span")
  private static WebElement pricePerPack;

  @FindBy(how = How.XPATH, using = "//table[@id='fullSupplyTable']/tbody/tr[1]/td[18]/ng-switch/span/ng-switch/span")
  private static WebElement packsToShip;

  @FindBy(how = How.XPATH, using = "//table[@id='nonFullSupplyTable']/tbody/tr/td[18]/ng-switch/span")
  private static WebElement packsToShipNonFullSupply;

  @FindBy(how = How.XPATH, using = "//table[@id='nonFullSupplyTable']/tbody/tr/td[19]/ng-switch/span/span[2]")
  private static WebElement pricePerPackNonFullSupply;

  @FindBy(how = How.XPATH, using = "//table[@id='nonFullSupplyTable']/tbody/tr/td[20]/ng-switch/span/span[2]")
  private static WebElement totalCostNonFullSupply;

  @FindBy(how = How.XPATH, using = "//span[@id='fullSupplyItemsCost']")
  private static WebElement totalCostFullSupplyFooter;

  @FindBy(how = How.XPATH, using = "//span[@id='nonFullSupplyItemsCost']")
  private static WebElement totalCostNonFullSupplyFooter;

  @FindBy(how = How.XPATH, using = "//span[@id='totalCost']")
  private static WebElement totalCostFooter;

  @FindBy(how = How.ID, using = "W_0")
  private static WebElement requestedQuantityExplanation;

  @FindBy(how = How.ID, using = "X_0")
  private static WebElement totalStockOutDays;


  @FindBy(how = How.XPATH, using = "//a[@class='rnr-adjustment']")
  private static WebElement addDescription;

  @FindBy(how = How.XPATH, using = "//div[@class='adjustment-field']/div[@class='row-fluid']/div[@class='span5']/select")
  private static WebElement lossesAndAdjustmentSelect;


  @FindBy(how = How.XPATH, using = "//input[@ng-model='lossAndAdjustment.quantity']")
  private static WebElement quantityAdj;

  @FindBy(how = How.XPATH, using = "//input[@value='Add']")
  private static WebElement addButtonNonFullSupply;

  @FindBy(how = How.XPATH, using = "//form[@id='rnr-form']/div[1]/ng-include[2]/table/tbody/tr/td[2]/ng-switch/span")
  private static WebElement productDescriptionNonFullSupply;

  @FindBy(how = How.XPATH, using = "//form[@id='rnr-form']/div[1]/ng-include[2]/table/tbody/tr/td[1]/ng-switch/span")
  private static WebElement productCodeNonFullSupply;

  @FindBy(how = How.XPATH, using = "//div[@class='adjustment-list']/ul/li/span[@class='tpl-adjustment-type ng-binding']")
  private static WebElement adjList;

  @FindBy(how = How.XPATH, using = "//input[@id='D_6_0']")
  private static WebElement adjListValue;

  @FindBy(how = How.XPATH, using = "//div[@class='adjustment-total clearfix alert alert-warning']")
  private static WebElement totalAdj;

  @FindBy(how = How.XPATH, using = "//input[@value='Done']")
  private static WebElement doneButton;

  @FindBy(how = How.XPATH, using = "//span[@class='alert alert-warning reason-request']")
  private static WebElement requestedQtyWarningMessage;

  @FindBy(how = How.XPATH, using = "//div[@class='info-box']/div[2]/div[3]")
  private static WebElement reportingPeriodInitRnRScreen;

  @FindBy(how = How.XPATH, using = "//input[@value='Add Non-Full Supply']")
  private static WebElement addNonFullSupplyButton;

  @FindBy(how = How.XPATH, using = "//input[@id='J_undefined']")
  private static WebElement requestedQuantityNonFullSupply;

  @FindBy(how = How.XPATH, using = "//input[@id='W_undefined']")
  private static WebElement requestedQuantityExplanationNonFullSupply;

  @FindBy(how = How.XPATH, using = "//select[@id='nonFullSupplyProductsName']")
  private static WebElement productDropDown;


  @FindBy(how = How.XPATH, using = "//select[@id='nonFullSupplyProductsCode']")
  private static WebElement productCodeDropDown;

  @FindBy(how = How.NAME, using = "newNonFullSupply.quantityRequested")
  private static WebElement requestedQuantityField;

  @FindBy(how = How.ID, using = "reasonForRequestedQuantity")
  private static WebElement requestedQuantityExplanationField;

  @FindBy(how = How.XPATH, using = "//input[@value='Add']")
  private static WebElement addButton;

  @FindBy(how = How.XPATH, using = "//input[@ng-click='addNonFullSupplyLineItem()']")
  private static WebElement addButtonEnabled;

  @FindBy(how = How.XPATH, using = "//input[@value='Close']")
  private static WebElement closeButton;


  String successText = "R&R saved successfully!";


  public InitiateRnRPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(25);
  }

  public void verifyRnRHeader(String FCode, String FName, String FCstring, String program, String periodDetails) {

    testWebDriver.waitForElementToAppear(requisitionHeader);
    String headerText = testWebDriver.getText(requisitionHeader);
    SeleneseTestNgHelper.assertTrue(headerText.contains("Report and Requisition for " + program));
    String facilityText = testWebDriver.getText(facilityLabel);
    SeleneseTestNgHelper.assertTrue(facilityText.contains(FCode + FCstring + " - " + FName + FCstring));

    SeleneseTestNgHelper.assertEquals(reportingPeriodInitRnRScreen.getText().trim().substring("Reporting Period: ".length()), periodDetails.trim());

  }

  public void enterBeginningBalance(String A) {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(beginningBalance);
    beginningBalance.sendKeys(A);
    String beginningBalanceValue = testWebDriver.getAttribute(beginningBalance, "value");
    SeleneseTestNgHelper.assertEquals(beginningBalanceValue, A);
  }

  public void enterQuantityReceived(String B) {
    testWebDriver.waitForElementToAppear(quantityReceived);
    quantityReceived.sendKeys(B);
    String quantityReceivedValue = testWebDriver.getAttribute(quantityReceived, "value");
    SeleneseTestNgHelper.assertEquals(quantityReceivedValue, B);
  }

  public void enterQuantityDispensed(String C) {
    testWebDriver.waitForElementToAppear(quantityDispensed);
    quantityDispensed.sendKeys(C);
    String quantityDispensedValue = testWebDriver.getAttribute(quantityDispensed, "value");
    SeleneseTestNgHelper.assertEquals(quantityDispensedValue, C);
  }

  public void enterLossesAndAdjustments(String adj) {
    testWebDriver.waitForElementToAppear(addDescription);
    addDescription.click();
    testWebDriver.waitForElementToAppear(lossesAndAdjustmentSelect);
    testWebDriver.selectByVisibleText(lossesAndAdjustmentSelect, "Transfer In");
    testWebDriver.waitForElementToAppear(quantityAdj);
    quantityAdj.clear();
    quantityAdj.sendKeys(adj);
    addButton.click();
    testWebDriver.waitForElementToAppear(adjList);
    String labelAdj = testWebDriver.getText(adjList);
    SeleneseTestNgHelper.assertEquals(labelAdj.trim(), "Transfer In");
    String adjValue = testWebDriver.getAttribute(adjListValue, "value");
    SeleneseTestNgHelper.assertEquals(adjValue, adj);
    testWebDriver.waitForElementToAppear(totalAdj);
    String totalAdjValue = testWebDriver.getText(totalAdj);
    SeleneseTestNgHelper.assertEquals(totalAdjValue.substring("Total ".length()), adj);
    doneButton.click();
    testWebDriver.sleep(1000);


  }


  public void calculateAndVerifyStockOnHand(Integer A, Integer B, Integer C, Integer D) {
    enterBeginningBalance(A.toString());
    enterQuantityReceived(B.toString());
    enterQuantityDispensed(C.toString());
    enterLossesAndAdjustments(D.toString());
    beginningBalance.click();
    testWebDriver.waitForElementToAppear(stockOnHand);
    Integer StockOnHand = A + B - C + D;
    testWebDriver.sleep(1000);
    String stockOnHandValue = stockOnHand.getText();
    String StockOnHandValue = StockOnHand.toString();
    SeleneseTestNgHelper.assertEquals(stockOnHandValue, StockOnHandValue);
  }

  public void enterAndVerifyRequestedQuantityExplanation(Integer A) {
    String expectedWarningMessage = "Please enter a reason";
    testWebDriver.waitForElementToAppear(requestedQuantity);
    requestedQuantity.sendKeys(A.toString());
    testWebDriver.waitForElementToAppear(requestedQtyWarningMessage);
    String warningMessage = testWebDriver.getText(requestedQtyWarningMessage);
    SeleneseTestNgHelper.assertEquals(warningMessage.trim(), expectedWarningMessage);
    requestedQuantityExplanation.sendKeys("Due to bad climate");
    testWebDriver.sleep(1000);
  }

  public void enterValuesAndVerifyCalculatedOrderQuantity(Integer F, Integer X, Integer N, Integer P, Integer H, Integer I) {
    testWebDriver.waitForElementToAppear(newPatient);
    newPatient.sendKeys(F.toString());
    testWebDriver.waitForElementToAppear(totalStockOutDays);
    totalStockOutDays.sendKeys(X.toString());
    testWebDriver.waitForElementToAppear(adjustedTotalConsumption);
    String actualAdjustedTotalConsumption = testWebDriver.getText(adjustedTotalConsumption);
    SeleneseTestNgHelper.assertEquals(actualAdjustedTotalConsumption, N.toString());
    String actualAmc = testWebDriver.getText(amc);
    SeleneseTestNgHelper.assertEquals(actualAmc.trim(), P.toString());
    String actualMaximumStockQuantity = testWebDriver.getText(maximumStockQuantity);
    SeleneseTestNgHelper.assertEquals(actualMaximumStockQuantity.trim(), H.toString());
    String actualCalculatedOrderQuantity = testWebDriver.getText(caculatedOrderQuantity);
    SeleneseTestNgHelper.assertEquals(actualCalculatedOrderQuantity.trim(), I.toString());
    testWebDriver.sleep(1000);


  }

  public void verifyPacksToShip(Integer V) {
    testWebDriver.waitForElementToAppear(packsToShip);
    String actualPacksToShip = testWebDriver.getText(packsToShip);
    SeleneseTestNgHelper.assertEquals(actualPacksToShip.trim(), V.toString());
    testWebDriver.sleep(500);

  }

  public void calculateAndVerifyTotalCost() {
    testWebDriver.waitForElementToAppear(packsToShip);
    String actualPacksToShip = testWebDriver.getText(packsToShip);
    testWebDriver.waitForElementToAppear(pricePerPack);
    String actualPricePerPack = testWebDriver.getText(pricePerPack).substring(1);
    Float actualTotalCost = Float.parseFloat(actualPacksToShip) * Float.parseFloat(actualPricePerPack);
    SeleneseTestNgHelper.assertEquals(actualTotalCost.toString() + "0", totalCost.getText().substring(1));
    testWebDriver.sleep(500);
  }

  public void calculateAndVerifyTotalCostNonFullSupply() {
    testWebDriver.waitForElementToAppear(packsToShipNonFullSupply);
    String actualPacksToShip = testWebDriver.getText(packsToShipNonFullSupply);
    testWebDriver.waitForElementToAppear(pricePerPackNonFullSupply);
    String actualPricePerPack = testWebDriver.getText(pricePerPackNonFullSupply);
    Float actualTotalCost = Float.parseFloat(actualPacksToShip.trim()) * Float.parseFloat(actualPricePerPack.trim());
    SeleneseTestNgHelper.assertEquals(actualTotalCost.toString() + "0", totalCostNonFullSupply.getText().trim());
    testWebDriver.sleep(500);
  }


  public void verifyCostOnFooter() {
    testWebDriver.waitForElementToAppear(totalCostFullSupplyFooter);
    String totalCostFullSupplyFooterValue = testWebDriver.getText(totalCostFullSupplyFooter);
    testWebDriver.waitForElementToAppear(totalCostNonFullSupplyFooter);
    String totalCostNonFullSupplyFooterValue = testWebDriver.getText(totalCostNonFullSupplyFooter);
    Float actualTotalCost = Float.parseFloat(totalCostFullSupplyFooterValue.trim()) + Float.parseFloat(totalCostNonFullSupplyFooterValue.trim());
    SeleneseTestNgHelper.assertEquals(actualTotalCost.toString() + "0", totalCostFooter.getText().trim());
    testWebDriver.sleep(500);
  }

  public void addNonFullSupplyLineItems(String requestedQuantityValue, String requestedQuantityExplanationValue, String productPrimaryName, String productCode) throws IOException, SQLException {
    DBWrapper dbWrapper = new DBWrapper();
    String nonFullSupplyItems = dbWrapper.fetchNonFullSupplyData(productCode, "2", "1");
    testWebDriver.waitForElementToAppear(addNonFullSupplyButton);
    addNonFullSupplyButton.click();
    testWebDriver.waitForElementToAppear(productDropDown);
    SeleneseTestNgHelper.assertFalse("Add button not enabled", addButtonNonFullSupply.isEnabled());
    SeleneseTestNgHelper.assertTrue("Close button not displayed", closeButton.isDisplayed());
    testWebDriver.waitForElementToAppear(productDropDown);
    testWebDriver.selectByVisibleText(productDropDown, productPrimaryName);
    testWebDriver.waitForElementToAppear(productCodeDropDown);
    testWebDriver.selectByVisibleText(productCodeDropDown, productCode);
    requestedQuantityField.clear();
    requestedQuantityField.sendKeys(requestedQuantityValue);
    requestedQuantityExplanationField.clear();
    requestedQuantityExplanationField.sendKeys(requestedQuantityExplanationValue);
    testWebDriver.waitForElementToAppear(closeButton);
    testWebDriver.sleep(1000);
    addButtonEnabled.click();
    closeButton.click();

    SeleneseTestNgHelper.assertEquals(productDescriptionNonFullSupply.getText().trim(), nonFullSupplyItems);
    SeleneseTestNgHelper.assertEquals(productCodeNonFullSupply.getText().trim(), productCode);
    SeleneseTestNgHelper.assertEquals(testWebDriver.getAttribute(requestedQuantityNonFullSupply, "value").trim(), requestedQuantityValue);
    SeleneseTestNgHelper.assertEquals(testWebDriver.getAttribute(requestedQuantityExplanationNonFullSupply, "value").trim(), requestedQuantityExplanationValue);

  }

  public void saveRnR() {
    saveButton.click();
    testWebDriver.sleep(1500);
    SeleneseTestNgHelper.assertTrue("R&R saved successfully! message not displayed", successMessage.isDisplayed());
  }

  public void submitRnR() {
    submitButton.click();
    testWebDriver.sleep(1500);
  }

  public void authorizeRnR() {
    authorizeButton.click();
    testWebDriver.sleep(1500);
  }


  public void verifySubmitRnrSuccessMsg() {
    SeleneseTestNgHelper.assertTrue("RnR Submit Success message not displayed", submitSuccessMessage.isDisplayed());
  }

  public void verifyAuthorizeRnrSuccessMsg() {
    SeleneseTestNgHelper.assertTrue("RnR authorize Success message not displayed", submitSuccessMessage.isDisplayed());
  }

  public void verifySubmitRnrErrorMsg() {
    SeleneseTestNgHelper.assertTrue("RnR Fail message not displayed", submitErrorMessage.isDisplayed());
  }

  public void verifyBeginningBalanceDisabled() {
    SeleneseTestNgHelper.assertFalse("BB Not disabled", beginningBalance.isEnabled());
  }
}