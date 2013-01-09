package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;


public class HomePage extends Page {

    @FindBy(how = How.XPATH, using = "//a[@id=\"username\"]")
    private static WebElement usernameDisplay;

    @FindBy(how = How.LINK_TEXT, using = "Logout")
    private static WebElement logoutLink;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'Manage')]")
    private static WebElement manageFacilityMenuItem;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'Facilities')]")
    private static WebElement facilityMenuItem;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'Roles')]")
    private static WebElement manageRoleAssignmentLink;

    @FindBy(how = How.LINK_TEXT, using = "Add new")
    private static WebElement createFacility;

    @FindBy(how = How.XPATH, using = "//div[@class='ng-scope']/div[@ng-hide='facility.id']/h2")
    private static WebElement facilityHeader;

    @FindBy(how = How.LINK_TEXT, using = "Configure")
    private static WebElement TemplateConfigTab;

    @FindBy(how = How.LINK_TEXT, using = "R & R Template")
    private static WebElement RnRTemplateConfigTab;

    @FindBy(how = How.LINK_TEXT, using = "R & R")
    private static WebElement ConfigureTemplateSelectProgramPage;

    @FindBy(how = How.ID, using = "selectProgram")
    private static WebElement ProgramDropDown;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'Next')]")
    private static WebElement NextButton;

    @FindBy(how = How.LINK_TEXT, using = "Requisitions")
    private static WebElement requisitionsLink;

    @FindBy(how = How.LINK_TEXT, using = "Create")
    private static WebElement createLink;

    @FindBy(how = How.LINK_TEXT, using = "My Facility")
    private static WebElement myFacilityLink;

    @FindBy(how = How.XPATH, using = "//a[contains(@href,'/public/pages/logistics/rnr/create.html')]")
    private static WebElement createRnRLink;

    @FindBy(how = How.XPATH, using = "//select[@ng-change='loadPrograms()']")
    private static WebElement facilityDropDown;

    @FindBy(how = How.XPATH, using = "//option[@value='0']")
    private static WebElement programDropDown;

    @FindBy(how = How.XPATH, using = "//select[2]")
    private static WebElement programDropDownSelect;


    @FindBy(how = How.XPATH, using = "//input[@value='Next']")
    private static WebElement nextButton;

    @FindBy(how = How.LINK_TEXT, using = "Manage")
    private static WebElement manageLink;

    @FindBy(how = How.LINK_TEXT, using = "Schedules")
    private static WebElement schedulesLink;

    @FindBy(how = How.LINK_TEXT, using = "Search")
    private static WebElement searchLink;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'Upload')]")
    private static WebElement uploadLink;

    public HomePage(TestWebDriver driver) throws  IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
        SeleneseTestNgHelper.assertTrue(usernameDisplay.isDisplayed());
    }

    public LoginPage logout() throws IOException{
        testWebDriver.waitForElementToAppear(usernameDisplay);
        testWebDriver.mouseOver(usernameDisplay);
        usernameDisplay.click();
        testWebDriver.waitForElementToAppear(logoutLink);
        logoutLink.click();
        return new LoginPage(testWebDriver);
    }

    public boolean verifyWelcomeMessage(String user) {
        testWebDriver.waitForTextToAppear("Welcome " + user);
        return testWebDriver.getPageSource().contains("Welcome " + user);
    }

    public CreateFacilityPage navigateCreateFacility() throws IOException {
        SeleneseTestNgHelper.assertTrue(manageFacilityMenuItem.isDisplayed());
        testWebDriver.waitForElementToAppear(manageFacilityMenuItem);
        testWebDriver.click(manageFacilityMenuItem);
        manageFacilityMenuItem.click();
        testWebDriver.waitForElementToAppear(facilityMenuItem);
        facilityMenuItem.click();
        testWebDriver.waitForElementToAppear(createFacility);
        createFacility.click();
        testWebDriver.waitForElementToAppear(facilityHeader);
        SeleneseTestNgHelper.assertEquals(facilityHeader.getText().trim(), "Add new facility");
        return new CreateFacilityPage(testWebDriver);
    }

    public TemplateConfigPage selectProgramToConfigTemplate(String programme) {
        testWebDriver.waitForElementToAppear(TemplateConfigTab);
        testWebDriver.mouseOver(TemplateConfigTab);
        TemplateConfigTab.click();
        testWebDriver.waitForElementToAppear(RnRTemplateConfigTab);
        RnRTemplateConfigTab.click();
        testWebDriver.selectByVisibleText(ProgramDropDown, programme);
        NextButton.click();
        return new TemplateConfigPage(testWebDriver);
    }

    public InitiateRnRPage navigateAndInitiateRnr(String FCode, String FName, String FCstring, String program) throws IOException {
        testWebDriver.waitForElementToAppear(requisitionsLink);
        requisitionsLink.click();
        testWebDriver.waitForElementToAppear(createLink);
        createLink.click();
        testWebDriver.waitForElementToAppear(myFacilityLink);
        myFacilityLink.click();
        testWebDriver.waitForElementToAppear(facilityDropDown);
        testWebDriver.selectByVisibleText(facilityDropDown, FCode + FCstring + "-" + FName + FCstring);
        testWebDriver.waitForElementToAppear(programDropDown);
        programDropDown.click();
        testWebDriver.selectByVisibleText(programDropDownSelect, program);
        nextButton.click();
        return new InitiateRnRPage(testWebDriver);
    }

    public DeleteFacilityPage navigateSearchFacility() throws IOException {
        testWebDriver.waitForElementToAppear(manageLink);
        manageLink.click();
        testWebDriver.waitForElementToAppear(facilityMenuItem);
        facilityMenuItem.click();
        return new DeleteFacilityPage(testWebDriver);
    }

    public RolesPage navigateRoleAssignments() throws IOException {
        testWebDriver.waitForElementToAppear(manageLink);
        manageLink.click();
        testWebDriver.waitForElementToAppear(manageRoleAssignmentLink);
        manageRoleAssignmentLink.click();
        return new RolesPage(testWebDriver);
    }

    public UploadPage navigateUploads() throws IOException {
        testWebDriver.waitForElementToAppear(uploadLink);
        uploadLink.click();
        return new UploadPage(testWebDriver);
    }

    public ManageSchedulePage navigateToSchedule() throws IOException{
        testWebDriver.waitForElementToAppear(manageLink);
        manageLink.click();
        testWebDriver.waitForElementToAppear(schedulesLink);
        schedulesLink.click();
        return new ManageSchedulePage(testWebDriver);

    }

}