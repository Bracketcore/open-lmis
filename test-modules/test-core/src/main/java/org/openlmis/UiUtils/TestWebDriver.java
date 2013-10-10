/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.UiUtils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;


public class TestWebDriver {

  private static WebDriver driver;
  private static String BASE_URL;
  private String ERROR_MESSAGE_LOGIN;
  private int DEFAULT_WAIT_TIME = 30;

  Date dObjnew = new Date();
  SimpleDateFormat formatternew = new SimpleDateFormat("yyyyMMdd");
  String dateFolder = formatternew.format(dObjnew);
  String screenShotsFolder = null;


  public TestWebDriver(WebDriver driver) {
    this.driver = driver;
    maximizeBrowser();
  }

  public void setBaseURL(String BASE_URL) {
    driver.manage().deleteAllCookies();
    this.BASE_URL = BASE_URL;
    get();
  }

  public WebElement findElement(By by) {
    return driver.findElement(by);
  }

  public void setErrorMessage(String ERROR_MESSAGE_LOGIN) {
    this.ERROR_MESSAGE_LOGIN = ERROR_MESSAGE_LOGIN;
  }

  public void verifyUrl(String identifier) {
    sleep(2000);
    String url = getCurrentUrl();
    if (identifier.equalsIgnoreCase("Admin"))
      assertTrue(url.contains(BASE_URL + "public/pages/admin/index.html"));
    else
      assertTrue(url.contains(BASE_URL + "public/pages/logistics/rnr/create.html#/init-rnr"));
  }


  public void verifyUrlInvalid() {
    String url = getCurrentUrl();
    assertTrue(url.contains(BASE_URL + "public/pages/loginAs.html?error=true"));
  }


  public void get() {
    driver.get(BASE_URL);
  }

  public void getUrl(String url) {
    driver.get(url);
  }

  public static WebDriver getDriver() {
    return driver;
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }


  public String getPageSource() {
    return driver.getPageSource();
  }

  public void setImplicitWait(int defaultTimeToWait) {
    driver.manage().timeouts().implicitlyWait(defaultTimeToWait, TimeUnit.SECONDS);
  }


  public void quitDriver() {
    driver.quit();
  }

  public void maximizeBrowser() {
    driver.manage().window().maximize();
  }


  public void close() {
    for (String window : driver.getWindowHandles()) {
      driver.switchTo().window(window);
      driver.close();
    }
  }

  public void refresh()
  {
    driver.navigate().refresh();
  }

  public void waitForPageToLoad()
  {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return (((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
      }
    });
  }


  public void handleScroll() {
    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("scroll(0,1000);");
  }

  public void handleScrollByPixels(int x, int y) {
    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("scroll(" + x + "," + y + ");");
  }

  public void waitForElementToAppear(final WebElement element) {
    (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return (element.isDisplayed());
      }
    });
  }

  public void waitForElementsToAppear(final WebElement element, final WebElement elementSecond) {
    (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return (element.isDisplayed() || elementSecond.isDisplayed());
      }
    });
  }

  public void waitForElementsToAppear(final WebElement element, final WebElement elementSecond, final WebElement elementThird) {
    (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return (element.isDisplayed() || elementSecond.isDisplayed() || elementThird.isDisplayed());
      }
    });
  }


  public void waitForTextToAppear(final String textToWaitFor) {
    (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return (getPageSource().contains(textToWaitFor));
      }
    });
  }

  public boolean verifyErrorMessage() {
    waitForTextToAppear(ERROR_MESSAGE_LOGIN);
    return getPageSource().contains(ERROR_MESSAGE_LOGIN);
  }

  public void selectByVisibleText(WebElement element, String visibleText) {
    new Select(element).selectByVisibleText(visibleText);
  }

  public void selectByValue(WebElement element, String value) {
    new Select(element).selectByValue(value);
  }

  public void selectByIndex(WebElement element, int index) {
    new Select(element).selectByIndex(index);
  }

  public boolean isSelected(WebElement element) {
    return element.isSelected();
  }

  public WebElement getFirstSelectedOption(WebElement element) {
    return new Select(element).getFirstSelectedOption();
  }

  public WebElement getElementById(String Id) {
    return driver.findElement(By.id(Id));
  }

  public void selectFrame(String frameName) {
    driver.switchTo().frame(frameName);
  }

  public WebElement getElementByName(String Name) {
    return driver.findElement(By.name(Name));
  }

  public WebElement getElementByXpath(String Xpath) {
    return driver.findElement(By.xpath(Xpath));
  }

  public WebElement getElementByLink(String Link) {
    return driver.findElement(By.linkText(Link));
  }

  public List<WebElement> getElementsByXpath(String Xpath) {
    return driver.findElements(By.xpath(Xpath));
  }

  public int getElementsSizeByXpath(String Xpath) {
    return driver.findElements(By.xpath(Xpath)).size();
  }

  public String getSelectedOptionDefault(WebElement element) {
    return element.getAttribute("value");
  }

  public List<WebElement> getAllSelectedOptions(WebElement element) {
    return new Select(element).getAllSelectedOptions();
  }

  public List<WebElement> getOptions(WebElement element) {

    return new Select(element).getOptions();
  }

  public void deselectByIndex(WebElement element, int index) {
    new Select(element).deselectByIndex(index);
  }

  public void deselectByValue(WebElement element, String value) {
    new Select(element).deselectByValue(value);
  }

  public void deselectByVisibleText(WebElement element, String value) {
    new Select(element).deselectByVisibleText(value);
  }

  public void deselectAll(WebElement element) {
    new Select(element).deselectAll();
  }

  public String getText(WebElement element) {
    return element.getText();
  }

  public String getAttribute(WebElement element, String value) {
    return element.getAttribute(value);
  }


  public void takeScreenShotMethod() {
    try {
      Thread.sleep(1500);
      Date dObj = new Date();
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-hhmmss");
      String time = formatter.format(dObj);
      BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
      ImageIO.write(image, "png", new File(System.getProperty("user.dir") + "/src/main/resources/" + time + "-screenshot.png"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void sleep(long timeToSleep) {
    try {
      Thread.sleep(timeToSleep);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void getSize(String xPath) {
    WebElement element = getElementByXpath(xPath);

  }

  public void click(final WebElement element) {
    Actions action = new Actions(driver);
    action.click(element).perform();
  }

  public void clickForRadio(final WebElement element) {
    element.click();
    if (!element.isSelected()) {
      Actions action = new Actions(driver);
      action.click(element).perform();
    }

  }


  public boolean mouseOver(final WebElement element) {
    boolean flag = false;
    waitForElementToAppear(element);
    sleep(1500);
    if (element != null) {
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
      builder.moveByOffset(1000, 200);

      flag = true;
      return flag;
    } else
      flag = false;
    return flag;
  }

  public void keyPress(final WebElement element) {
    waitForElementToAppear(element);
    if (element != null) {
      for (int i = 0; i < 15; i++) {
        element.sendKeys(Keys.TAB);
        if (driver.switchTo().activeElement().getText().equalsIgnoreCase(element.getText())) ;
        break;
      }
      element.sendKeys(Keys.RETURN);
    }
  }


  private void createDirectory() {
    String Separator = System.getProperty("file.separator");
    File parentDir = new File(System.getProperty("user.dir"));
    screenShotsFolder = parentDir.getAbsolutePath() + Separator + "src" + Separator + "main" + Separator + "resources" + Separator + dateFolder + Separator;
    if (!new File(screenShotsFolder).exists()) {
      (new File(screenShotsFolder)).mkdir();
    }
  }

  public void captureScreenShotForCucumberRun() {
    WebDriver driver = TestWebDriver.getDriver();
    createDirectory();
    Date dObj = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-hhmmss");
    String time = formatter.format(dObj);
    String name = "failure-";
    String filename = screenShotsFolder
      + name + "-"
      + time + "-screenshot"
      + ".png";

    File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

    try {
      FileUtils.copyFile(scrFile, new File(filename));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
