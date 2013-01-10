package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class LoginPage extends Page {

    @FindBy(how = How.ID, using = "username")
    private static WebElement userNameField;

    @FindBy(how = How.ID, using = "password")
    private static WebElement passwordField;

    private String BASE_URL;

    private String ERROR_MESSAGE_LOGIN = "The username or password you entered is incorrect. Please try again.";

    private String baseUrl;

    /*
    baseUrl = http://localhost:9090/
dbUrl = jdbc:postgresql://localhost:5432/open_lmis
dbUser = postgres
dbPassword = p@ssw0rd
     */

    public LoginPage(TestWebDriver driver) throws  IOException{
        super(driver);

        baseUrl = "http://localhost:9091/";
        BASE_URL=baseUrl;
        testWebDriver.setBaseURL(BASE_URL);
        testWebDriver.setErrorMessage(ERROR_MESSAGE_LOGIN);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
        SeleneseTestNgHelper.assertTrue(userNameField.isDisplayed());
    }


    public HomePage loginAs(String username, String password) throws IOException {
        testWebDriver.waitForElementToAppear(userNameField);
        testWebDriver.waitForElementToAppear(passwordField);
        userNameField.sendKeys(username);
        passwordField.sendKeys(password);
        userNameField.submit();
        return new HomePage(testWebDriver);
    }

}