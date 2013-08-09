/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;

import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class RefrigeratorPage extends Page {

  @FindBy(how = XPATH, using = "//a[contains(text(),'Add New')]")
  private static WebElement addNewButton;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Edit')]")
  private static WebElement editButton;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Delete')]")
  private static WebElement deleteButton;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Done')]")
  private static WebElement doneButton;

  @FindBy(how = XPATH, using = "//a/span[contains(text(),'Refrigerators')]")
  private static WebElement refrigeratorTab;

  @FindBy(how = XPATH, using = "//input[@ng-model='refrigeratorReading.temperature']")
  private static WebElement refrigeratorTemperatureTextField;

  @FindBy(how = XPATH, using = "//input[@ng-model='refrigeratorReading.lowAlarmEvents'")
  private static WebElement lowAlarmEventsTextField;

  @FindBy(how = XPATH, using = "//input[@ng-model='refrigeratorReading.highAlarmEvents'")
  private static WebElement highAlarmEventsTextField;

  @FindBy(how = ID, using = "temperatureNR")
  private static WebElement refrigeratorTemperatureNR;

  @FindBy(how = ID, using = "functioningCorrectlyYes")
  private static WebElement functioningCorrectlyYesRadio;

  @FindBy(how = ID, using = "functioningCorrectlyNo")
  private static WebElement functioningCorrectlyNoRadio;

  @FindBy(how = ID, using = "functioningCorrectlyDontKnow")
  private static WebElement functioningCorrectlyDontKnowRadio;

  @FindBy(how = ID, using = "functioningCorrectlyNR")
  private static WebElement functioningCorrectlyNR;

  @FindBy(how = ID, using = "lowAlarmEventNR")
  private static WebElement lowAlarmEventNR;

  @FindBy(how = ID, using = "highAlarmEventNR")
  private static WebElement highAlarmEventNR;

  @FindBy(how = ID, using = "problemSinceLastVisitYes")
  private static WebElement problemSinceLastVisitYesRadio;

  @FindBy(how = ID, using = "problemSinceLastVisitNo")
  private static WebElement problemSinceLastVisitNoRadio;

  @FindBy(how = ID, using = "problemSinceLastVisitDontKnow")
  private static WebElement problemSinceLastVisitDontKnowRadio;

  @FindBy(how = ID, using = "problemSinceLastVisitNR")
  private static WebElement problemSinceLastVisitNR;

  @FindBy(how = ID, using = "operatorError")
  private static WebElement operatorError;

  @FindBy(how = ID, using = "burnerProblem")
  private static WebElement burnerProblem;

  @FindBy(how = ID, using = "gasLeakage")
  private static WebElement gasLeakage;

  @FindBy(how = ID, using = "gasFault")
  private static WebElement gasFault;

  @FindBy(how = ID, using = "other")
  private static WebElement other;

  @FindBy(how = ID, using = "otherTextbox")
  private static WebElement otherTextBox;

  @FindBy(how = XPATH, using = "//textarea[@ng-model='refrigeratorReading.notes']")
  private static WebElement notesTextArea;

  @FindBy(how = XPATH, using = "//h3[contains(text(),'Refrigerators')]")
  private static WebElement refrigeratorsHeader;


  protected RefrigeratorPage(TestWebDriver driver) {
    super(driver);
  }

  public void enterValueInRefrigeratorTemperature(String value) {
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureTextField);
    sendKeys(refrigeratorTemperatureTextField, value);
  }

  public void clickProblemSinceLastVisitYesRadio() {
    testWebDriver.waitForElementToAppear(problemSinceLastVisitYesRadio);
    problemSinceLastVisitYesRadio.click();
  }

  public void clickProblemSinceLastVisitNoRadio() {
    testWebDriver.waitForElementToAppear(problemSinceLastVisitNoRadio);
    problemSinceLastVisitNoRadio.click();
  }

  public void clickProblemSinceLastVisitDontKnowRadio() {
    testWebDriver.waitForElementToAppear(problemSinceLastVisitDontKnowRadio);
    problemSinceLastVisitDontKnowRadio.click();
  }

  public void clickProblemSinceLastVisitNR() {
    testWebDriver.waitForElementToAppear(problemSinceLastVisitNR);
    problemSinceLastVisitNR.click();
  }

  public void clickOperatorError() {
    testWebDriver.waitForElementToAppear(operatorError);
    operatorError.click();
  }

  public void clickBurnerProblem() {
    testWebDriver.waitForElementToAppear(burnerProblem);
    burnerProblem.click();
  }

  public void clickGasLeakage() {
    testWebDriver.waitForElementToAppear(gasLeakage);
    gasLeakage.click();
  }

  public void clickGasFault() {
    testWebDriver.waitForElementToAppear(gasFault);
    gasFault.click();
  }

  public void clickOther() {
    testWebDriver.waitForElementToAppear(other);
    other.click();
  }

  public void enterValueInLowAlarmEvents(String value) {
    testWebDriver.waitForElementToAppear(lowAlarmEventsTextField);
    sendKeys(lowAlarmEventsTextField, value);
  }

  public void enterValueInHighAlarmEvents(String value) {
    testWebDriver.waitForElementToAppear(highAlarmEventsTextField);
    sendKeys(highAlarmEventsTextField, value);
  }

  public void enterValueInOtherTextBox(String value) {
    testWebDriver.waitForElementToAppear(otherTextBox);
    sendKeys(otherTextBox, value);
  }

  public void enterValueInNotesTextArea(String value) {
    testWebDriver.waitForElementToAppear(notesTextArea);
    sendKeys(notesTextArea, value);
  }

  public void clickRefrigeratorTemperatureNR() {
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureNR);
    refrigeratorTemperatureNR.click();
  }

  public void clickLowAlarmEventNR() {
    testWebDriver.waitForElementToAppear(lowAlarmEventNR);
    lowAlarmEventNR.click();
  }

  public void clickHighAlarmEventNR() {
    testWebDriver.waitForElementToAppear(highAlarmEventNR);
    highAlarmEventNR.click();
  }

  public void clickFunctioningCorrectlyYesRadio() {
    testWebDriver.waitForElementToAppear(functioningCorrectlyYesRadio);
    functioningCorrectlyYesRadio.click();
  }

  public void clickFunctioningCorrectlyNoRadio() {
    testWebDriver.waitForElementToAppear(functioningCorrectlyNoRadio);
    functioningCorrectlyNoRadio.click();
  }

  public void clickFunctioningCorrectlyDontKnowRadio() {
    testWebDriver.waitForElementToAppear(functioningCorrectlyDontKnowRadio);
    functioningCorrectlyDontKnowRadio.click();
  }

  public void clickFunctioningCorrectlyNR() {
    testWebDriver.waitForElementToAppear(functioningCorrectlyNR);
    functioningCorrectlyNR.click();
  }

  public void clickAddNew() {
    testWebDriver.waitForElementToAppear(addNewButton);
    addNewButton.click();
  }

  public void clickEdit() {
    testWebDriver.waitForElementToAppear(editButton);
    editButton.click();
  }

  public void clickDelete() {
    testWebDriver.waitForElementToAppear(deleteButton);
    deleteButton.click();
  }

  public void clickRefrigeratorTab() {
    testWebDriver.waitForElementToAppear(refrigeratorTab);
    refrigeratorTab.click();
    testWebDriver.waitForElementToAppear(refrigeratorsHeader);
  }

  public void clickDone() {
    testWebDriver.waitForElementToAppear(doneButton);
    doneButton.click();
  }

}