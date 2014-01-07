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


import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static java.lang.Float.parseFloat;
import static org.openqa.selenium.support.How.*;


public class InitiateRnRPage extends RequisitionPage {

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/h2")
  private static WebElement requisitionHeader = null;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/div/div[2]/div[1]/div[1]/span")
  private static WebElement facilityLabel = null;

  @FindBy(how = XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton = null;

  @FindBy(how = XPATH, using = "//input[@value='Submit']")
  private static WebElement submitButton = null;

  @FindBy(how = XPATH, using = "//input[@value='Authorize']")
  private static WebElement authorizeButton = null;

  @FindBy(how = XPATH, using = "//input[@value='Approve']")
  private static WebElement approveButton = null;

  @FindBy(how = XPATH, using = "//div[@id='submitSuccessMsgDiv' and @openlmis-message='submitMessage']")
  private static WebElement submitSuccessMessage = null;

  @FindBy(how = XPATH, using = "//div[@id='submitFailMessage' and @openlmis-message='submitError']")
  private static WebElement submitErrorMessage = null;

  @FindBy(how = ID, using = "beginningBalance_0")
  private static WebElement beginningBalanceFirstProduct = null;

  @FindBy(how = ID, using = "beginningBalance_1")
  private static WebElement beginningBalanceSecondProduct = null;

  @FindBy(how = ID, using = "quantityReceived_0")
  private static WebElement quantityReceivedFirstProduct = null;

  @FindBy(how = ID, using = "quantityReceived_1")
  private static WebElement quantityReceivedSecondProduct = null;

  @FindBy(how = ID, using = "quantityDispensed_0")
  private static WebElement quantityDispensedFirstProduct = null;

  @FindBy(how = ID, using = "quantityDispensed_1")
  private static WebElement quantityDispensedSecondProduct = null;

  @FindBy(how = ID, using = "stockInHand_0")
  private static WebElement stockInHandFirstProduct = null;

  @FindBy(how = ID, using = "stockInHand_1")
  private static WebElement stockInHandSecondProduct = null;

  @FindBy(how = ID, using = "newPatientCount_0")
  private static WebElement newPatientFirstProduct = null;

  @FindBy(how = ID, using = "newPatientCount_1")
  private static WebElement newPatientSecondProduct = null;

  @FindBy(how = ID, using = "maxStockQuantity_0")
  private static WebElement maximumStockQuantity = null;

  @FindBy(how = ID, using = "calculatedOrderQuantity_0")
  private static WebElement calculatedOrderQuantity = null;

  @FindBy(how = ID, using = "quantityRequested_0")
  private static WebElement requestedQuantityFirstProduct = null;

  @FindBy(how = ID, using = "quantityRequested_1")
  private static WebElement requestedQuantitySecondProduct = null;

  @FindBy(how = ID, using = "normalizedConsumption_0")
  private static WebElement adjustedTotalConsumptionFirstProduct = null;

  @FindBy(how = ID, using = "normalizedConsumption_1")
  private static WebElement adjustedTotalConsumptionSecondProduct = null;

  @FindBy(how = ID, using = "amc_0")
  private static WebElement amcFirstProduct = null;

  @FindBy(how = ID, using = "amc_1")
  private static WebElement amcSecondProduct = null;

  @FindBy(how = ID, using = "cost_0")
  private static WebElement totalCost = null;

  @FindBy(how = ID, using = "price_0")
  private static WebElement pricePerPack = null;

  @FindBy(how = ID, using = "packsToShip_0")
  private static WebElement packsToShip = null;

  @FindBy(how = ID, using = "price_0")
  private static WebElement pricePerPackNonFullSupply = null;

  @FindBy(how = ID, using = "fullSupplyItemsCost")
  private static WebElement totalCostFullSupplyFooter = null;

  @FindBy(how = ID, using = "nonFullSupplyItemsCost")
  private static WebElement totalCostNonFullSupplyFooter = null;

  @FindBy(how = XPATH, using = "//span[@id='totalCost']")
  private static WebElement totalCostFooter = null;

  @FindBy(how = ID, using = "reasonForRequestedQuantity_0")
  private static WebElement requestedQuantityExplanation = null;

  @FindBy(how = ID, using = "expirationDate_0")
  private static WebElement expirationDate = null;

  @FindBy(how = ID, using = "remarks_0")
  private static WebElement remarks = null;

  @FindBy(how = ID, using = "stockOutDays_0")
  private static WebElement totalStockOutDaysFirstProduct = null;

  @FindBy(how = ID, using = "stockOutDays_1")
  private static WebElement totalStockOutDaysSecondProduct = null;

  @FindBy(how = ID, using = "dividedCost")
  private static WebElement showRnrCostDetailsIcon = null;

  @FindBy(how = ID, using = "totalCostPopupClose")
  private static WebElement closeRnrCostDetailsIcon = null;

  @FindBy(how = XPATH, using = "//a[@class='rnr-adjustment']")
  private static WebElement addDescription = null;

  @FindBy(how = XPATH, using = "//div[@class='adjustment-field']/div[@class='row-fluid']/div[@class='span5']/select")
  private static WebElement lossesAndAdjustmentSelect = null;


  @FindBy(how = XPATH, using = "//input[@ng-model='lossAndAdjustment.quantity']")
  private static WebElement quantityAdj = null;

  @FindBy(how = ID, using = "addNonFullSupply")
  private static WebElement addButtonNonFullSupply = null;

  @FindBy(how = XPATH, using = "//table[@id='nonFullSupplyFrozenTable']/tbody/tr/td[2]/span")
  private static WebElement productDescriptionNonFullSupply = null;

  @FindBy(how = XPATH, using = "//table[@id='nonFullSupplyFrozenTable']/tbody/tr/td[1]/span")
  private static WebElement productCodeNonFullSupply = null;

  @FindBy(how = XPATH, using = "//div[@class='adjustment-list']/ul/li/span[@class='tpl-adjustment-type ng-binding']")
  private static WebElement adjList = null;

  @FindBy(how = ID, using = "lossesAndAdjustmentsDone")
  private static WebElement lossesAndAdjustmentsDone = null;

  @FindBy(how = ID, using = "reasonForRequestedQuantity_0_warning")
  private static WebElement requestedQtyWarningMessage = null;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/div/div[2]/div[2]/div[3]/span")
  private static WebElement reportingPeriodInitRnRScreen = null;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.geographicZone.name']")
  private static WebElement geoZoneInitRnRScreen = null;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.geographicZone.parent.name']")
  private static WebElement parentGeoZoneInitRnRScreen = null;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.operatedBy.text']")
  private static WebElement operatedByInitRnRScreen = null;

  @FindBy(how = ID, using = "addNonFullSupply")
  private static WebElement addNonFullSupplyItemButton = null;

  @FindBy(how = XPATH, using = "//input[@value='Add']")
  private static WebElement addNonFullSupplyButtonScreen = null;

  @FindBy(how = ID, using = "fullSupplyTab")
  private static WebElement fullSupplyTab = null;

  @FindBy(how = XPATH, using = "//select[@id='nonFullSupplyProductsCategory']")
  private static WebElement categoryDropDown = null;

  @FindBy(how = XPATH, using = "//select[@id='nonFullSupplyProductsCodeAndName']")
  private static WebElement productDropDown = null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_nonFullSupplyProductsCategory']/a/span")
  private static WebElement categoryDropDownLink = null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private static WebElement productDropDownTextField = null;

  @FindBy(how = XPATH, using = "//div[@class='select2-result-label']")
  private static WebElement productDropDownValue = null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_nonFullSupplyProductsCodeAndName']/a/span")
  private static WebElement productDropDownLink = null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private static WebElement categoryDropDownTextField = null;

  @FindBy(how = XPATH, using = "//div[@class='select2-result-label']")
  private static WebElement categoryDropDownValue = null;

  @FindBy(how = XPATH, using = "//input[@name='nonFullSupplyProductQuantityRequested0']")
  private static WebElement nonFullSupplyProductQuantityRequested = null;

  @FindBy(how = XPATH, using = "//div[@id='nonFullSupplyProductCodeAndName']/label")
  private static WebElement nonFullSupplyProductCodeAndName = null;

  @FindBy(how = XPATH, using = "//div[@id='nonFullSupplyProductReasonForRequestedQuantity']/input")
  private static WebElement nonFullSupplyProductReasonForRequestedQuantity = null;

  @FindBy(how = NAME, using = "newNonFullSupply.quantityRequested")
  private static WebElement requestedQuantityField = null;

  @FindBy(how = ID, using = "reasonForRequestedQuantity")
  private static WebElement requestedQuantityExplanationField = null;

  @FindBy(how = ID, using = "showNonFullSupplyModal")
  private static WebElement addButtonOnNonFullSupplyTab = null;

  @FindBy(how = ID, using = "addLossesAndAdjustment")
  private static WebElement addLossesAndAdjustmentButton = null;

  @FindBy(how = XPATH, using = "//input[@value='Cancel']")
  private static WebElement cancelButton = null;

  @FindBy(how = XPATH, using = "//input[@id='doneNonFullSupply']")
  private static WebElement doneButtonNonFullSupply = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Home')]")
  private static WebElement homeMenuItem = null;

  @FindBy(how = XPATH, using = "//div[@openlmis-message='error']")
  private static WebElement configureTemplateErrorDiv = null;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/div/div[1]/div[@class='Emergency']/span")
  private static WebElement rnrEmergencyLabel = null;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/div/div[1]/div[@class='Regular']/span")
  private static WebElement rnrRegularLabel = null;

  @FindBy(how = ID, using = "beginningBalance_0")
  private static WebElement beginningBalanceLabel = null;

  @FindBy(how = ID, using = "selectAll")
  private static WebElement skipAllLink = null;

  @FindBy(how = ID, using = "selectNone")
  private static WebElement skipNoneLink = null;

  @FindBy(how = ID, using = "allocatedBudgetAmount")
  private static WebElement allocatedBudgetAmount = null;

  @FindBy(how = XPATH, using = "//span[@openlmis-message='label.allocated.budget']")
  private static WebElement allocatedBudgetLabel = null;

  @FindBy(how = ID, using = "allocatedBudgetNotApplicable")
  private static WebElement budgetNotAllocated = null;

  @FindBy(how = ID, using = "budgetWarningIcon")
  private static WebElement budgetWarningIcon = null;

  @FindBy(how = ID, using = "budgetWarningMessage")
  private static WebElement budgetWarningMessage = null;

  @FindBy(how = ID, using = "budgetWarningExtra")
  private static WebElement budgetWarningMessageOnFooter = null;

  @FindBy(how = XPATH, using = "//*[@id=\"action_buttons\"]/ng-include/div/div/span[1]/strong/span[1]")
  private static WebElement budgetWarningIconOnFooter = null;

  Float actualTotalCostFullSupply = 0.0f;
  Float actualTotalCostNonFullSupply = 0.0f;
  private Map<String, WebElement> elementsMap = new HashMap<String, WebElement>() {{
    put("beginningBalanceFirstProduct", beginningBalanceFirstProduct);
    put("stockInHandFirstProduct", stockInHandFirstProduct);
    put("newPatientFirstProduct", newPatientFirstProduct);
    put("quantityReceivedFirstProduct", quantityReceivedFirstProduct);
    put("quantityDispensedFirstProduct", quantityDispensedFirstProduct);
    put("totalStockOutDaysFirstProduct", totalStockOutDaysFirstProduct);
    put("requestedQuantityFirstProduct", requestedQuantityFirstProduct);

    put("beginningBalanceSecondProduct", beginningBalanceSecondProduct);
    put("stockInHandSecondProduct", stockInHandSecondProduct);
    put("newPatientSecondProduct", newPatientSecondProduct);
    put("quantityReceivedSecondProduct", quantityReceivedSecondProduct);
    put("quantityDispensedSecondProduct", quantityDispensedSecondProduct);
    put("totalStockOutDaysSecondProduct", totalStockOutDaysSecondProduct);
    put("requestedQuantitySecondProduct", requestedQuantitySecondProduct);
    put("allocatedBudgetAmount", allocatedBudgetAmount);
  }};

  public InitiateRnRPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(2);
  }

  public void verifyRnRHeader(String FCode, String FName, String FcString, String program, String periodDetails, String geoZone, String parentGeoZone, String operatedBy, String facilityType) {
    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(requisitionHeader);
    String headerText = testWebDriver.getText(requisitionHeader);
    assertTrue(headerText.contains("Report and Requisition for " + program + " (" + facilityType + ")"));
    String facilityText = testWebDriver.getText(facilityLabel);
    assertTrue(facilityText.contains(FCode + FcString + " - " + FName + FcString));

    assertEquals(periodDetails.trim(), reportingPeriodInitRnRScreen.getText().trim());
    assertEquals(geoZone, geoZoneInitRnRScreen.getText().trim());
    assertEquals(parentGeoZone, parentGeoZoneInitRnRScreen.getText().trim());
    assertEquals(operatedBy, operatedByInitRnRScreen.getText().trim());
  }

  public void skipSingleProduct(int rowNumber) {
    WebElement skipCheckBox = testWebDriver.getElementById("skip_" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(skipCheckBox);
    skipCheckBox.click();
  }

  public void skipAllProduct() {
    testWebDriver.waitForElementToAppear(skipAllLink);
    skipAllLink.click();
  }

  public void unSkipAllProduct() {
    testWebDriver.waitForElementToAppear(skipNoneLink);
    skipNoneLink.click();
  }

  public HomePage clickHome() throws IOException {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(homeMenuItem);
    testWebDriver.keyPress(homeMenuItem);
    return new HomePage(testWebDriver);
  }

  public void enterValue(Integer value, String elementName) {
    if (value == null) {
      return;
    }
    testWebDriver.sleep(1000);
    WebElement element = elementsMap.get(elementName);
    testWebDriver.waitForElementToAppear(element);
    element.clear();
    element.sendKeys(value.toString());
  }

  public void verifyBeginningBalanceForFirstProduct(Integer beginningBalanceValue) {
    verifyFieldValue(testWebDriver.getAttribute(beginningBalanceFirstProduct, "value"), beginningBalanceValue.toString());
  }

  public void verifyStockOnHandForFirstProduct(String stockOnHandValue) {
    testWebDriver.sleep(1000);
    verifyFieldValue(stockOnHandValue, testWebDriver.getAttribute(stockInHandFirstProduct, "value"));
  }

  public boolean isEnableBeginningBalanceForFirstProduct() {
    testWebDriver.waitForElementToAppear(beginningBalanceFirstProduct);
    return beginningBalanceFirstProduct.isEnabled();
  }

  public void verifyFieldValue(String Expected, String Actual) {
    assertEquals(Expected, Actual);
  }

  public void verifyTemplateNotConfiguredMessage() {
    testWebDriver.waitForElementToAppear(configureTemplateErrorDiv);
    assertTrue("'Please contact admin to define R&R template for this program' div should show up", configureTemplateErrorDiv.isDisplayed());
    assertTrue("Please contact admin to define R&R template for this program should show up", configureTemplateErrorDiv.getText().equals("Please contact admin to define R&R template for this program"));

  }

  public void verifyQuantityReceivedForFirstProduct(Integer quantityReceivedValue) {
    verifyFieldValue(testWebDriver.getAttribute(quantityReceivedFirstProduct, "value"), quantityReceivedValue.toString());
  }

  public void verifyQuantityDispensedForFirstProduct(Integer quantityDispensedValue) {
    verifyFieldValue(testWebDriver.getAttribute(quantityDispensedFirstProduct, "value"), quantityDispensedValue.toString());
  }

  public void enterLossesAndAdjustments(String adj) {
    testWebDriver.waitForElementToAppear(addDescription);
    addDescription.click();
    testWebDriver.waitForElementToAppear(lossesAndAdjustmentSelect);
    testWebDriver.selectByVisibleText(lossesAndAdjustmentSelect, "Transfer In");

    testWebDriver.waitForElementToAppear(quantityAdj);
    quantityAdj.clear();
    quantityAdj.sendKeys(adj);

    testWebDriver.waitForElementToBeEnabled(addLossesAndAdjustmentButton);
    addLossesAndAdjustmentButton.click();

    testWebDriver.waitForElementToAppear(adjList);
    String labelAdj = testWebDriver.getText(adjList);

    assertEquals("Transfer In", labelAdj.trim());

    lossesAndAdjustmentsDone.click();
  }


  public void calculateAndVerifyStockOnHand(Integer beginningBalance, Integer quantityReceived, Integer quantityDispensed, Integer lossesAndAdjustments) {
    Integer StockOnHand = beginningBalance + quantityReceived - quantityDispensed + lossesAndAdjustments;
    String stockOnHandActualValue = StockOnHand.toString();
    String stockOnHandExpectedValue = calculateStockOnHand(beginningBalance, quantityReceived, quantityDispensed, lossesAndAdjustments);
    verifyFieldValue(stockOnHandExpectedValue, stockOnHandActualValue);
  }

  public String calculateStockOnHand(Integer beginningBalance, Integer quantityReceived,
                                     Integer quantityDispensed, Integer lossesAndAdjustment) {
    enterValue(beginningBalance, "beginningBalanceFirstProduct");
    enterValue(quantityReceived, "quantityReceivedFirstProduct");
    enterValue(quantityDispensed, "quantityDispensedFirstProduct");
    enterLossesAndAdjustments(lossesAndAdjustment.toString());

    testWebDriver.waitForElementToAppear(stockInHandFirstProduct);
    testWebDriver.sleep(2000);
    return stockInHandFirstProduct.getText();
  }

  public void PopulateMandatoryFullSupplyDetails(int numberOfLineItems, int numberOfLineItemsPerPage) {
    int numberOfPages = numberOfLineItems / numberOfLineItemsPerPage;
    if (numberOfLineItems % numberOfLineItemsPerPage != 0) {
      numberOfPages = numberOfPages + 1;
    }

    for (int j = 1; j <= numberOfPages; j++) {
      testWebDriver.getElementByXpath("//a[contains(text(), '" + j + "') and @class='ng-binding']").click();
      if (j == numberOfPages && (numberOfLineItems % numberOfLineItemsPerPage) != 0) {
        numberOfLineItemsPerPage = numberOfLineItems % numberOfLineItemsPerPage;
      }
      for (int i = 0; i < numberOfLineItemsPerPage; i++) {
        testWebDriver.getElementById("beginningBalance_" + i).sendKeys("10");
        testWebDriver.getElementById("quantityReceived_" + i).sendKeys("10");
        testWebDriver.getElementById("quantityDispensed_" + i).sendKeys("10");
      }
    }
  }

  public void verifyRequestedQuantityExplanation() {
    testWebDriver.waitForElementToAppear(requestedQtyWarningMessage);
    verifyFieldValue(testWebDriver.getText(requestedQtyWarningMessage).trim(), "Please enter a reason");
  }

  public void enterExplanationReason() {
    testWebDriver.sleep(1000);
    requestedQuantityExplanation.sendKeys("Due to bad climate");
    testWebDriver.sleep(1000);
  }

  public void verifyAmcAndCalculatedOrderQuantity(int expectedAdjustedTotalConsumption,
                                                  int expectedAmc, int maxMonth, int stockOnHand) {
    String actualAdjustedTotalConsumption = testWebDriver.getText(adjustedTotalConsumptionFirstProduct);
    verifyFieldValue(String.valueOf(expectedAdjustedTotalConsumption), actualAdjustedTotalConsumption);
    String actualAmc = testWebDriver.getText(amcFirstProduct);
    verifyFieldValue(String.valueOf(expectedAmc), actualAmc.trim());
    String actualMaximumStockQuantity = testWebDriver.getText(maximumStockQuantity);
    int expectedMaxStockQuantity = expectedAmc * maxMonth;
    verifyFieldValue(String.valueOf(expectedMaxStockQuantity), actualMaximumStockQuantity.trim());
    String actualCalculatedOrderQuantity = testWebDriver.getText(calculatedOrderQuantity);
    verifyFieldValue(String.valueOf(expectedMaxStockQuantity - stockOnHand), actualCalculatedOrderQuantity.trim());
  }

  public void verifyPacksToShip(Integer packSize) throws IOException, SQLException {
    testWebDriver.waitForElementToAppear(packsToShip);
    String actualPacksToShip = testWebDriver.getText(packsToShip);

    Integer expectedPacksToShip;
    if (requestedQuantityFirstProduct.getAttribute("value").isEmpty()) {
      float actualCalculatedOrderQuantity = Float.parseFloat(testWebDriver.getText(calculatedOrderQuantity));
      expectedPacksToShip = Math.round(actualCalculatedOrderQuantity / packSize);
    } else {
      float actualRequestedQuantity = Float.parseFloat(requestedQuantityFirstProduct.getAttribute("value"));
      expectedPacksToShip = Math.round(actualRequestedQuantity / packSize);
    }
    verifyFieldValue(expectedPacksToShip.toString(), actualPacksToShip.trim());
    testWebDriver.sleep(500);
  }

  public void calculateAndVerifyTotalCost() {
    actualTotalCostFullSupply = calculateTotalCost();
    assertEquals(actualTotalCostFullSupply.toString() + "0", totalCost.getText().substring(1));
    testWebDriver.sleep(500);
  }

  public float calculateTotalCost() {
    testWebDriver.waitForElementToAppear(packsToShip);
    String actualPacksToShip = testWebDriver.getText(packsToShip);
    testWebDriver.waitForElementToAppear(pricePerPack);
    String actualPricePerPack = testWebDriver.getText(pricePerPack).substring(1);
    if (actualPacksToShip.trim().equals(""))
      return parseFloat("0");
    else
      return parseFloat(actualPacksToShip) * parseFloat(actualPricePerPack);
  }

  public void calculateAndVerifyTotalCostNonFullSupply() {
    actualTotalCostNonFullSupply = calculateTotalCostNonFullSupply();
    assertEquals(actualTotalCostNonFullSupply.toString() + "0", totalCost.getText().trim().substring(1));
    testWebDriver.sleep(500);
  }

  public float calculateTotalCostNonFullSupply() {
    testWebDriver.waitForElementToAppear(packsToShip);
    String actualPacksToShip = testWebDriver.getText(packsToShip);
    testWebDriver.waitForElementToAppear(pricePerPackNonFullSupply);
    String actualPricePerPack = testWebDriver.getText(pricePerPackNonFullSupply).substring(1);
    return parseFloat(actualPacksToShip.trim()) * parseFloat(actualPricePerPack.trim());
  }

  public void verifyCostOnFooter() {
    testWebDriver.waitForElementToAppear(showRnrCostDetailsIcon);
    showRnrCostDetailsIcon.click();
    String totalCostFullSupplyFooterValue = testWebDriver.getText(totalCostFullSupplyFooter);
    String totalCostNonFullSupplyFooterValue = testWebDriver.getText(totalCostNonFullSupplyFooter);
    BigDecimal actualTotalCost = new BigDecimal(parseFloat(totalCostFullSupplyFooterValue.trim().substring(1)) + parseFloat(totalCostNonFullSupplyFooterValue.trim().substring(1))).setScale(2, BigDecimal.ROUND_HALF_UP);
    assertEquals(actualTotalCost.toString(), totalCostFooter.getText().trim().substring(1));
    fullSupplyTab.click();
    testWebDriver.sleep(500);
    actualTotalCostFullSupply = calculateTotalCost();
    assertEquals(totalCostFooter.getText().trim().substring(1),
      new BigDecimal(actualTotalCostFullSupply + actualTotalCostNonFullSupply).setScale(2,
        BigDecimal.ROUND_HALF_UP).toString());
    showRnrCostDetailsIcon.click();
    testWebDriver.sleep(500);
  }

  public String getTotalCostFooter() {
    testWebDriver.waitForElementToAppear(totalCostFooter);
    return totalCostFooter.getText().trim().substring(1);
  }

  public String getFullySupplyCostFooter() {
    testWebDriver.waitForElementToAppear(showRnrCostDetailsIcon);
    showRnrCostDetailsIcon.click();
    testWebDriver.waitForElementToAppear(totalCostFullSupplyFooter);
    String fullSupplyTotalCost = totalCostFullSupplyFooter.getText().trim().substring(1);
    showRnrCostDetailsIcon.click();
    showRnrCostDetailsIcon.click();
    closeRnrCostDetailsIcon.click();
    assertFalse(totalCostFullSupplyFooter.isDisplayed());
    return fullSupplyTotalCost;
  }

  public void addNonFullSupplyLineItems(String requestedQuantityValue, String requestedQuantityExplanationValue,
                                        String productPrimaryName, String productCode, String category)
    throws IOException, SQLException {
    DBWrapper dbWrapper = new DBWrapper();
    String nonFullSupplyItems = dbWrapper.fetchNonFullSupplyData(productCode, "2", "1");
    clickNonFullSupplyTab();
    testWebDriver.sleep(1000);

    addButtonOnNonFullSupplyTab.click();
    testWebDriver.sleep(1000);

    assertFalse("Add button not enabled", addButtonNonFullSupply.isEnabled());
    assertTrue("Close button not displayed", cancelButton.isDisplayed());
    testWebDriver.waitForElementToAppear(categoryDropDownLink);

    categoryDropDownLink.click();
    testWebDriver.waitForElementToAppear(categoryDropDownTextField);
    categoryDropDownTextField.sendKeys(category);
    testWebDriver.waitForElementToAppear(categoryDropDownValue);
    categoryDropDownValue.click();

    productDropDownLink.click();
    testWebDriver.waitForElementToAppear(productDropDownTextField);
    productDropDownTextField.sendKeys(productCode);
    testWebDriver.waitForElementToAppear(productDropDownValue);
    productDropDownValue.click();

    requestedQuantityField.clear();
    requestedQuantityField.sendKeys(requestedQuantityValue);
    requestedQuantityExplanationField.clear();
    requestedQuantityExplanationField.sendKeys(requestedQuantityExplanationValue);
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
    testWebDriver.waitForElementToAppear(addNonFullSupplyButtonScreen);
    addNonFullSupplyButtonScreen.click();

    testWebDriver.waitForElementToAppear(categoryDropDownLink);
    assertEquals("", testWebDriver.getSelectedOptionDefault(categoryDropDown).trim());
    assertEquals("", testWebDriver.getSelectedOptionDefault(productDropDown).trim());
    assertEquals("", requestedQuantityField.getAttribute("value").trim());
    assertEquals("", requestedQuantityExplanationField.getAttribute("value").trim());

    testWebDriver.waitForElementToAppear(categoryDropDownLink);

    categoryDropDownLink.click();
    testWebDriver.sleep(200);
    testWebDriver.waitForElementToAppear(categoryDropDownTextField);
    categoryDropDownTextField.sendKeys(category);
    testWebDriver.waitForElementToAppear(categoryDropDownValue);
    categoryDropDownValue.click();

    productDropDownLink.click();
    testWebDriver.sleep(200);
    testWebDriver.waitForElementToAppear(productDropDownTextField);
    productDropDownTextField.sendKeys(productCode);
    testWebDriver.waitForElementToAppear(productDropDownValue);
    productDropDownValue.click();

    requestedQuantityField.clear();
    requestedQuantityField.sendKeys(requestedQuantityValue);
    requestedQuantityExplanationField.clear();
    requestedQuantityExplanationField.sendKeys(requestedQuantityExplanationValue);

    testWebDriver.waitForElementToBeEnabled(addNonFullSupplyItemButton);
    addNonFullSupplyItemButton.click();

    testWebDriver.waitForElementToAppear(nonFullSupplyProductCodeAndName);
    assertEquals(productCode + " | " + productPrimaryName, nonFullSupplyProductCodeAndName.getText().trim());
    assertEquals(requestedQuantityValue, nonFullSupplyProductQuantityRequested.getAttribute("value").trim());
    assertEquals(requestedQuantityExplanationValue, nonFullSupplyProductReasonForRequestedQuantity.getAttribute("value").trim());
    doneButtonNonFullSupply.click();
    testWebDriver.sleep(1000);

    assertEquals(nonFullSupplyItems, productDescriptionNonFullSupply.getText().trim());
    assertEquals(productCode, productCodeNonFullSupply.getText().trim());
    testWebDriver.waitForElementToAppear(requestedQuantityFirstProduct);
    assertEquals(requestedQuantityValue, testWebDriver.getAttribute(requestedQuantityFirstProduct, "value").trim());
    assertEquals(requestedQuantityExplanationValue, testWebDriver.getAttribute(requestedQuantityExplanation, "value").trim());
  }

  public int getSizeOfElements(String xpath) {
    return testWebDriver.getElementsSizeByXpath(xpath);
  }

  public void verifyColumnsHeadingPresent(String xpathTillTrTag, String heading, int noOfColumns) {
    assertTrue(isColumnHeadingPresent(xpathTillTrTag, heading, noOfColumns));
  }

  public void verifyColumnHeadingNotPresent(String xpathTillTrTag, String heading, int noOfColumns) {
    assertFalse(isColumnHeadingPresent(xpathTillTrTag, heading, noOfColumns));
  }

  public boolean isColumnHeadingPresent(String xpathTillTrTag, String heading, int noOfColumns) {
    boolean flag = false;
    String actualColumnHeading;
    for (int i = 0; i < noOfColumns; i++) {
      try {
        WebElement columnElement = testWebDriver.getElementByXpath(xpathTillTrTag + "/th[" + (i + 1) + "]");
        columnElement.click();
        actualColumnHeading = columnElement.getText();
      } catch (ElementNotVisibleException e) {
        continue;
      } catch (NoSuchElementException e) {
        continue;
      }
      if (actualColumnHeading.trim().toUpperCase().equals(heading.toUpperCase())) {
        flag = true;
        break;
      }
    }
    return flag;
  }

  public void addMultipleNonFullSupplyLineItems(int numberOfLineItems, boolean isMultipleCategories) throws IOException, SQLException {
    clickNonFullSupplyTab();
    testWebDriver.sleep(1000);
    addButtonOnNonFullSupplyTab.click();
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(categoryDropDownLink);

    for (int i = 0; i < numberOfLineItems; i++) {
      categoryDropDownLink.click();
      testWebDriver.waitForElementToAppear(categoryDropDownTextField);
      if (isMultipleCategories) {
        categoryDropDownTextField.sendKeys("Antibiotics" + i);
      } else {
        categoryDropDownTextField.sendKeys("Antibiotics");
      }
      testWebDriver.waitForElementToAppear(categoryDropDownValue);
      categoryDropDownValue.click();

      productDropDownLink.click();
      testWebDriver.waitForElementToAppear(productDropDownTextField);
      productDropDownTextField.sendKeys("NF" + i);
      testWebDriver.waitForElementToAppear(productDropDownValue);
      productDropDownValue.click();

      requestedQuantityField.clear();
      requestedQuantityField.sendKeys("10");
      requestedQuantityExplanationField.clear();
      requestedQuantityExplanationField.sendKeys("Due to certain reasons: " + i);

      testWebDriver.waitForElementToBeEnabled(addNonFullSupplyItemButton);
      addNonFullSupplyItemButton.click();

      testWebDriver.sleep(500);
      testWebDriver.waitForElementToAppear(nonFullSupplyProductCodeAndName);
    }
    doneButtonNonFullSupply.click();
    testWebDriver.sleep(500);
  }

  public void saveRnR() {
    saveButton.click();
    testWebDriver.sleep(1500);
  }

  public void submitRnR() {
    submitButton.click();
    testWebDriver.sleep(250);
  }

  public void authorizeRnR() {
    authorizeButton.click();
    testWebDriver.sleep(250);
  }

  public void verifyAllFieldsDisabled() {

    assertFalse("beginningBalanceFirstProduct should be disabled", beginningBalanceFirstProduct.isEnabled());
    assertFalse("quantityReceivedFirstProduct should be disabled", quantityReceivedFirstProduct.isEnabled());
    assertFalse("quantityDispensedFirstProduct should be disabled", quantityDispensedFirstProduct.isEnabled());
    assertFalse("newPatientFirstProduct should be disabled", newPatientFirstProduct.isEnabled());
    assertFalse("totalStockOutDaysFirstProduct should be disabled", totalStockOutDaysFirstProduct.isEnabled());
    assertFalse("requestedQuantityFirstProduct should be disabled", requestedQuantityFirstProduct.isEnabled());
    assertFalse("requestedQuantityExplanation should be disabled", requestedQuantityExplanation.isEnabled());
    assertFalse("expirationDate should be disabled", expirationDate.isEnabled());
    assertFalse("remarks should be disabled", remarks.isEnabled());
  }

  public void verifySaveButtonDisabled() {
    assertFalse("saveButton should be disabled", saveButton.isEnabled());
  }

  public void verifySubmitRnrSuccessMsg() {
    assertTrue("RnR Submit Success message not displayed", submitSuccessMessage.isDisplayed());
  }

  public void verifyAuthorizeRnrSuccessMsg() {
    assertTrue("RnR authorize Success message not displayed", submitSuccessMessage.isDisplayed());
  }

  public void verifySubmitRnrErrorMsg() {
    testWebDriver.sleep(1000);
    assertTrue("RnR Fail message not displayed", submitErrorMessage.isDisplayed());
  }

  public void verifyAuthorizeButtonNotPresent() {
    boolean authorizeButtonPresent;
    try {
      authorizeButton.click();
      authorizeButtonPresent = true;
    } catch (ElementNotVisibleException e) {
      authorizeButtonPresent = false;
    }
    assertFalse(authorizeButtonPresent);
  }

  public void verifyApproveButtonNotPresent() {
    boolean approveButtonPresent = false;
    try {
      approveButton.click();
      approveButtonPresent = true;
    } catch (ElementNotVisibleException e) {
      approveButtonPresent = false;
    } catch (NoSuchElementException e) {
      approveButtonPresent = false;
    } finally {
      assertFalse(approveButtonPresent);
    }
  }

  public String getEmergencyLabelText() {
    testWebDriver.waitForElementToAppear(rnrEmergencyLabel);
    return rnrEmergencyLabel.getText();
  }

  public String getRegularLabelText() {
    testWebDriver.waitForElementToAppear(rnrRegularLabel);
    return rnrRegularLabel.getText();
  }

  public String getBeginningBalance() {
    testWebDriver.waitForElementToAppear(beginningBalanceLabel);
    return beginningBalanceLabel.getAttribute("value");
  }


  public String getCategoryText(Integer rowNumber) {
    return testWebDriver.getElementById("category_" + rowNumber).getText();
  }

  public String getProductCode(Integer rowNumber) {
    return testWebDriver.getElementById("productCode_" + rowNumber).getText();
  }

  public void verifyNormalizedConsumptionForFirstProduct(Integer expectedValue) {
    testWebDriver.sleep(1000);
    verifyFieldValue(expectedValue.toString(), adjustedTotalConsumptionFirstProduct.getText());
  }

  public void verifyNormalizedConsumptionForSecondProduct(Integer expectedValue) {
    testWebDriver.sleep(1000);
    verifyFieldValue(expectedValue.toString(), adjustedTotalConsumptionSecondProduct.getText());
  }


  public void verifyAmcForFirstProduct(Integer expectedValue) {
    testWebDriver.sleep(1000);
    verifyFieldValue(expectedValue.toString(), amcFirstProduct.getText());
  }

  public void verifyAmcForSecondProduct(Integer expectedValue) {
    testWebDriver.sleep(1000);
    verifyFieldValue(expectedValue.toString(), amcSecondProduct.getText());
  }

  public boolean isAllocatedBudgetLabelDisplayed() {
    return allocatedBudgetLabel.isDisplayed();
  }

  public String getAllocatedBudgetLabel() {
    return allocatedBudgetLabel.getText();
  }

  public boolean isAllocatedBudgetAmountDisplayed() {
    return allocatedBudgetAmount.isDisplayed();
  }

  public String getAllocatedBudgetAmount() {
    return allocatedBudgetAmount.getText();
  }

  public boolean isBudgetNotAllocatedDisplayed() {
    return budgetNotAllocated.isDisplayed();
  }

  public String getBudgetNotAllocatedText() {
    return budgetNotAllocated.getText();
  }

  public boolean isBudgetWarningIconDisplayed() {
    return budgetWarningIcon.isDisplayed();
  }

  public boolean isBudgetWarningMessageDisplayed() {
    if (isBudgetWarningIconDisplayed()) {
      testWebDriver.moveToElement(budgetWarningIcon);
      return budgetWarningMessage.isDisplayed();
    } else
      return budgetWarningMessage.isDisplayed();
  }

  public boolean isBudgetWarningIconOnFooterDisplayed() {
    return budgetWarningIconOnFooter.isDisplayed();
  }

  public boolean isBudgetWarningMessageOnFooterDisplayed() {
    return budgetWarningMessageOnFooter.isDisplayed();
  }

  public String getBudgetWarningMessage() {
    testWebDriver.waitForElementToAppear(budgetWarningIcon);
    testWebDriver.moveToElement(budgetWarningIcon);
    return budgetWarningMessage.getText();
  }

  public String getBudgetWarningMessageOnFooter() {
    testWebDriver.waitForElementToAppear(budgetWarningMessageOnFooter);
    return budgetWarningMessageOnFooter.getText();
  }
}