/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.extension.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.SeleniumFileDownloadUtil;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.File;
import java.io.IOException;

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.NAME;


public class AverageConsumptionReportPage extends Page {

  @FindBy(how = NAME, using = "periodType")
  private static WebElement periodType;

  @FindBy(how = NAME, using = "startYear")
  private static WebElement startYear;

  @FindBy(how = NAME, using = "startMonth")
  private static WebElement startMonth;

  @FindBy(how = NAME, using = "endYear")
  private static WebElement endYear;

  @FindBy(how = NAME, using = "endMonth")
  private static WebElement endMonth;

  @FindBy(how = NAME, using = "zone")
  private static WebElement zone;

  @FindBy(how = NAME, using = "productCategory")
  private static WebElement productCategory;

  @FindBy(how = NAME, using = "facilityType")
  private static WebElement facilityType;

  @FindBy(how = NAME, using = "requisitionGroup")
  private static WebElement  requisitionGroup;

  @FindBy(how = NAME, using = "product")
  private static WebElement product;

  @FindBy(how = NAME, using = "startQuarter")
  private static WebElement startQuarter;

  @FindBy(how = NAME, using = "endQuarter")
  private static WebElement endQuarter;


  @FindBy(how = NAME, using = "startHalf")
  private static WebElement startHalf;

  @FindBy(how = NAME, using = "endHalf")
   private static WebElement endHalf;

  @FindBy(how = NAME, using = "program")
  private static WebElement program;


  @FindBy(how = How.XPATH, using = "//div[@ng-grid='gridOptions']")
  private static WebElement averageConsumptionReportListGrid;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col0 colt0']/span")
  private static WebElement firstRowProductColumn;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col1 colt1']/span")
  private static WebElement firstRowProductDescriptionColumn;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
  private static WebElement  firstRowFacilityTypeColumn;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col3 colt3']/span")
  private static WebElement  firstRowFacilityColumn;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col4 colt4']/span")
  private static WebElement firstRowAvgConsumptionColumn;


  @FindBy(how = ID, using = "pdf-button")
  private static WebElement PdfButton;

  @FindBy(how = ID, using = "xls-button")
  private static WebElement XLSButton;

  public AverageConsumptionReportPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public void enterFilterValues(String periodTypeValue, String startYearValue, String startMonthValue,
                                String endYearValue, String endMonthValue, String zoneValue, String productCategoryValue,
                                String facilityTypeValue, String requesitionGroupValue, String productValue, String programValue){

      testWebDriver.waitForElementToAppear(periodType);
      testWebDriver.selectByVisibleText(periodType, periodTypeValue);
      testWebDriver.selectByVisibleText(startYear, startYearValue);
      testWebDriver.selectByVisibleText(startMonth, startMonthValue);
      testWebDriver.selectByVisibleText(endYear, endYearValue);
      testWebDriver.selectByVisibleText(endMonth, endMonthValue);
      testWebDriver.selectByVisibleText(zone, zoneValue);
      testWebDriver.selectByVisibleText(productCategory, productCategoryValue);
      testWebDriver.selectByVisibleText(facilityType, facilityTypeValue);
      testWebDriver.selectByVisibleText(requisitionGroup, requesitionGroupValue);
      testWebDriver.selectByVisibleText(product, productValue);
      testWebDriver.selectByVisibleText(program, programValue);

      testWebDriver.sleep(500);
  }

  public void verifyHTMLReportOutput(){

      testWebDriver.waitForElementToAppear(averageConsumptionReportListGrid);
      testWebDriver.waitForElementToAppear(firstRowProductColumn);
      SeleneseTestNgHelper.assertEquals(firstRowProductColumn.getText().trim(), "Antibiotics");
      SeleneseTestNgHelper.assertEquals(firstRowProductDescriptionColumn.getText().trim(),"antibiotic Capsule 300/200/600 mg");
      SeleneseTestNgHelper.assertEquals(firstRowFacilityTypeColumn.getText().trim(),"Lvl3 Hospital");
      SeleneseTestNgHelper.assertEquals(firstRowFacilityColumn.getText().trim(),"Village Dispensary");
      SeleneseTestNgHelper.assertEquals(firstRowAvgConsumptionColumn.getText().trim(),"1");

      testWebDriver.sleep(500);

  }

  public void verifyPdfReportOutput() throws Exception {
      testWebDriver.waitForElementToAppear(PdfButton);
      PdfButton.click();
      testWebDriver.sleep(500);

      SeleniumFileDownloadUtil downloadHandler = new SeleniumFileDownloadUtil(TestWebDriver.getDriver());
      downloadHandler.setURI(testWebDriver.getCurrentUrl());
      File downloadedFile = downloadHandler.downloadFile(this.getClass().getSimpleName(), ".pdf");
      SeleneseTestNgHelper.assertEquals(downloadHandler.getLinkHTTPStatus(),200);
      SeleneseTestNgHelper.assertEquals(downloadedFile.exists(), true);

      testWebDriver.sleep(500);
    }



    public void verifyXlsReportOutput() throws Exception {
        testWebDriver.waitForElementToAppear(PdfButton);
        XLSButton.click();
        testWebDriver.sleep(500);

        SeleniumFileDownloadUtil downloadHandler = new SeleniumFileDownloadUtil(TestWebDriver.getDriver());
        downloadHandler.setURI(testWebDriver.getCurrentUrl());
        File downloadedFile = downloadHandler.downloadFile(this.getClass().getSimpleName(), ".xls");
        SeleneseTestNgHelper.assertEquals(downloadHandler.getLinkHTTPStatus(), 200);
        SeleneseTestNgHelper.assertEquals(downloadedFile.exists(), true);
        SeleneseTestNgHelper.assertTrue(downloadedFile.length() > 0);

        testWebDriver.sleep(500);
    }

    public void verifyPagination() throws IOException {

        WebElement btnNext = testWebDriver.findElement(By.xpath("//div[@id='wrap']/div/div/div/div/div[3]/div[3]/div/div[2]/div[2]/button[3]"));
        WebElement btnPrev = testWebDriver.findElement(By.xpath("//div[@id='wrap']/div/div/div/div/div[3]/div[3]/div/div[2]/div[2]/button[2]"));


        for (int i = 0; i < 10; i++)
            btnNext.click();
        for (int i = 0; i < 10; i++)
            btnPrev.click();

    }


}