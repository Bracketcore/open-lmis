package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.Map;

import static org.openqa.selenium.support.How.ID;

public class EpiInventoryPage extends DistributionTab {


  @FindBy(how = ID, using = "applyNRAll")
  private static WebElement applyNRToAllButton = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okButton = null;

  @FindBy(how = ID, using = "noLineItems")
  private static WebElement noLineItems = null;

  public EpiInventoryPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }


  @Override
  public void verifyIndicator(String color) {
  }

  @Override
  public void enterValues(Map<String, String> map) {
  }

  @Override
  public void verifyData(Map<String, String> map) {
  }

  @Override
  public void navigate() {
  }

  public void fillDeliveredQuantity(int rowNumber, String deliveredQuantity) {
    testWebDriver.findElement(By.id("deliveredQuantity" + (rowNumber - 1))).sendKeys(deliveredQuantity);
  }

  public String getProductCode(int rowNumber) {
    return testWebDriver.findElement(By.id("productName" + (rowNumber - 1))).getText();
  }

  public String getIsaValue(int rowNumber) {
    return testWebDriver.findElement(By.id("idealQuantity" + (rowNumber - 1))).getText();
  }

  public String getMessage() {
    return noLineItems.getText();
  }

  public void applyNRToAll() {
    applyNRToAllButton.click();
    okButton.click();
  }

  public void fillEpiInventoryWithOnlyDeliveredQuantity(String deliveredQuantity1, String deliveredQuantity2, String deliveredQuantity3) {
    this.applyNRToAll();
    this.fillDeliveredQuantity(1, deliveredQuantity1);
    this.fillDeliveredQuantity(2, deliveredQuantity2);
    this.fillDeliveredQuantity(3, deliveredQuantity3);
  }
}
