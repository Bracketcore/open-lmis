package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UploadPage extends Page {

    String uploadFilePath=null;

    @FindBy(how = How.XPATH, using = "//input[@value='Upload']")
    private static WebElement uploadButton;

    @FindBy(how = How.XPATH, using = "//input[@value='Choose CSV File to upload']")
    private static WebElement setCsvPath;

    @FindBy(how = How.XPATH, using = "//select[@id='model']")
    private static WebElement uploadDropDown;

    @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
    private static WebElement saveSuccessMsgDiv;

    @FindBy(how = How.ID, using = "saveErrorMsgDiv")
    private static WebElement saveErrorMsgDiv;



    public UploadPage(TestWebDriver driver) throws IOException {
        super(driver);

        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
        SeleneseTestNgHelper.assertTrue(uploadButton.isDisplayed());
        SeleneseTestNgHelper.assertTrue(uploadDropDown.isDisplayed());
    }

    public void selectUploadType(String uploadType){
        testWebDriver.waitForElementToAppear(uploadDropDown);
        testWebDriver.selectByVisibleText(uploadDropDown,uploadType);
    }

    public void uploadFile(String fileName){
        uploadFilePath = System.getProperty("user.dir") + "/src/main/resources/"+fileName;
        if(uploadFilePath.contains("functional-tests"))
            setCsvPath.sendKeys(uploadFilePath);
        else
        {
            uploadFilePath = System.getProperty("user.dir") + "/test-modules/functional-tests/src/main/resources/"+fileName;
            setCsvPath.sendKeys(uploadFilePath);
        }
        uploadButton.click();
    }

    public void uploadFacilities() throws FileNotFoundException {
        selectUploadType("Facilities");
        uploadFile("facilities.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    }

    public void uploadProducts() throws FileNotFoundException {
        selectUploadType("Products");
        uploadFile("products.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    }

    public void uploadUsers() throws FileNotFoundException {
        selectUploadType("Users");
        uploadFile("Users.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    }

    public void uploadProgramProductMapping() throws FileNotFoundException {
        selectUploadType("Program Product");
        uploadFile("program_product.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    }

    public void uploadProgramProductPrice() throws FileNotFoundException {
        selectUploadType("Program Product Price");
        uploadFile("Product_Cost.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    }

    public void uploadProgramSupportedByFacilities() throws FileNotFoundException {
        selectUploadType("Programs supported by facilities");
        uploadFile("program_supported.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    }

    public void uploadFacilityTypeToProductMapping() throws FileNotFoundException {
        selectUploadType("Facility Approved Products");
        uploadFile("Facility_Type_To_Product_Mapping.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    }


    public void uploadSupervisoryNodes() throws FileNotFoundException {
        selectUploadType("Supervisory Nodes");
        uploadFile("Supervisory_Nodes.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    }

    public void uploadRequisitionGroup() throws FileNotFoundException {
        selectUploadType("Requisition Groups");
        uploadFile("Requisition_Groups.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    }

    public void uploadRequisitionGroupMembers() throws FileNotFoundException {
        selectUploadType("Requisition Group Members");
        uploadFile("Requisition_Group_Members.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    }

    public void uploadRequisitionGroupProgramSchedule() throws FileNotFoundException {
        selectUploadType("Map Requisition Groups to Programs + Schedule");
        uploadFile("Requisition_Group_Program_Schedule.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    }

    public void uploadSupplyLines() throws FileNotFoundException {
        selectUploadType("Supply Lines");
        uploadFile("Supply_Lines.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    }



}
