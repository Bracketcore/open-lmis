package org.openlmis.admin.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.repository.mapper.ProgramRnrColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.ResultActions;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationContext-admin-web.xml")
public class RnrTemplateControllerIT {

    @Autowired
    RnrTemplateController controller;

    @Autowired
    ProgramRnrColumnMapper programRnrColumnMapper;

    @Before
    public void setUp() throws Exception {
        programRnrColumnMapper.deleteAll();
    }

    @Test
    public void shouldGetAllMasterRnRColumns() throws Exception {
        String existingProgramCode = "HIV";
        ResultActions resultActions = standaloneSetup(controller).setViewResolvers(contentNegotiatingViewResolver()).build()
                .perform(get("/admin/rnr/" + existingProgramCode + "/columns.json"));
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        String msdProductColumn = "{\"rnrColumnList\":[{\"id\":1,\"name\":\"product_code\",\"description\":\"Unique identifier for each commodity\",\"position\":1,\"label\":\"Product Code\",\"defaultValue\":\"\",\"dataSource\":\"Reference Value (Product Table)\",\"availableColumnTypes\":[\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"O\",\"used\":true,\"visible\":true,\"mandatory\":true},{\"id\":2,\"name\":\"product\",\"description\":\"Primary name of the product\",\"position\":2,\"label\":\"Product\",\"defaultValue\":\"\",\"dataSource\":\"Reference Value (Product Table)\",\"availableColumnTypes\":[\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"R\",\"used\":true,\"visible\":true,\"mandatory\":true},{\"id\":3,\"name\":\"unit_of_issue\",\"description\":\"Dispensing unit for this product\",\"position\":3,\"label\":\"Unit/Unit of Issue\",\"defaultValue\":\"\",\"dataSource\":\"Reference Value (Product Table)\",\"availableColumnTypes\":[\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"U\",\"used\":true,\"visible\":true,\"mandatory\":true},{\"id\":4,\"name\":\"beginning_balance\",\"description\":\"Stock in hand of previous period.This is quantified in dispensing units\",\"position\":4,\"label\":\"Beginning Balance\",\"defaultValue\":\"0\",\"dataSource\":\"User Input\",\"availableColumnTypes\":[\"UserInput\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"A\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":5,\"name\":\"quantity_received\",\"description\":\"Total quantity received in last period.This is quantified in dispensing units\",\"position\":5,\"label\":\" Total Received Quantity\",\"defaultValue\":\"0\",\"dataSource\":\"User Input\",\"availableColumnTypes\":[\"UserInput\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"B\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":6,\"name\":\"quantity_dispensed\",\"description\":\"Quantity dispensed/consumed in last reporting  period. This is quantified in dispensing units\",\"position\":6,\"label\":\"Total Consumed Quantity\",\"defaultValue\":\"0\",\"dataSource\":\"User Input/Derived\",\"availableColumnTypes\":[\"UserInput\",\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"A + B (+/-) D - E\",\"indicator\":\"C\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":7,\"name\":\"losses_and_adjustments\",\"description\":\"All kind of looses/adjustments made at the facility\",\"position\":7,\"label\":\"Total Losses / Adjustments\",\"defaultValue\":\"0\",\"dataSource\":\"User Input\",\"availableColumnTypes\":[\"UserInput\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"D1 + D2+D3...DN\",\"indicator\":\"D\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":8,\"name\":\"reason_for_losses_and_adjustments\",\"description\":\"Type of Losses/adjustments\",\"position\":8,\"label\":\"Reason for Losses and Adjustments\",\"defaultValue\":null,\"dataSource\":\"Reference Value ( Table)\",\"availableColumnTypes\":[\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"S\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":9,\"name\":\"stock_in_hand\",\"description\":\"Current physical count of stock on hand. This is quantified in dispensing units\",\"position\":9,\"label\":\"Stock on Hand\",\"defaultValue\":\"0\",\"dataSource\":\"User Input/Derived\",\"availableColumnTypes\":[\"UserInput\",\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"A+B(+/-)D-C\",\"indicator\":\"E\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":10,\"name\":\"new_patient_count\",\"description\":\"Total of new patients introduced\",\"position\":10,\"label\":\"Total number of new patients added to service on the program\",\"defaultValue\":\"0\",\"dataSource\":\"User Input\",\"availableColumnTypes\":[\"UserInput\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"F\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":11,\"name\":\"stock_out_days\",\"description\":\"Total number of days facility was out of stock\",\"position\":11,\"label\":\"Total Stockout days\",\"defaultValue\":\"0\",\"dataSource\":\"User Input\",\"availableColumnTypes\":[\"UserInput\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"X\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":12,\"name\":\"normalized_consumption\",\"description\":\"Total quantity consumed after adjusting for stockout days. This is quantified in dispensing units\",\"position\":12,\"label\":\"Adjusted Total Consumption\",\"defaultValue\":null,\"dataSource\":\"Derived\",\"availableColumnTypes\":[\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"C * (M*30)/((M*30)-X) + ( F* No of tabs per month * 1)\",\"indicator\":\"N\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":13,\"name\":\"amc\",\"description\":\"Average Monthly consumption, for last three months. This is quantified in dispensing units\",\"position\":13,\"label\":\"Average Monthly Consumption(AMC)\",\"defaultValue\":\"Default = N\",\"dataSource\":\"Derived\",\"availableColumnTypes\":[\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"(N/M + Ng-1/M + ...Ng-(g-1)/M)/G\",\"indicator\":\"P\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":14,\"name\":\"max_stock_quantity\",\"description\":\"Maximum Stock calculated based on consumption and max months of stock.This is quantified in dispensing units\",\"position\":14,\"label\":\"Maximum Stock Quantity\",\"defaultValue\":\"0\",\"dataSource\":\"Derived\",\"availableColumnTypes\":[\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"P * MaxMonthsStock\",\"indicator\":\"H\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":15,\"name\":\"calculated_order_quantity\",\"description\":\"Actual Quantity needed after deducting stock in hand. This is quantified in dispensing units\",\"position\":15,\"label\":\"Calculated Order Quantity\",\"defaultValue\":\"0\",\"dataSource\":\"Derived\",\"availableColumnTypes\":[\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"H - E\",\"indicator\":\"I\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":16,\"name\":\"quantity_requested\",\"description\":\"Requested override of calculated quantity.This is quantified in dispensing units\",\"position\":16,\"label\":\"Requested Quantity\",\"defaultValue\":null,\"dataSource\":\"User Input\",\"availableColumnTypes\":[\"UserInput\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"J\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":17,\"name\":\"reason_for_requested_quantity\",\"description\":\"Explanation of request for a quatity other than calculated order quantity.\",\"position\":17,\"label\":\"Requested Quantity Explanation\",\"defaultValue\":null,\"dataSource\":\"User Input\",\"availableColumnTypes\":[\"UserInput\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"W\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":18,\"name\":\"quantity_approved\",\"description\":\"Final approved quantity. This is quantified in dispensing units\",\"position\":18,\"label\":\"Approved Quantity\",\"defaultValue\":\"Default = I\",\"dataSource\":\"User Input\",\"availableColumnTypes\":[\"UserInput\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"K\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":19,\"name\":\"packs_to_ship\",\"description\":\"Total packs to be shipped based on pack size and applying rounding rules \",\"position\":19,\"label\":\"Packs to Ship\",\"defaultValue\":\"0\",\"dataSource\":\"Derived\",\"availableColumnTypes\":[\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"K / U + Rounding rules\",\"indicator\":\"V\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":20,\"name\":\"Price\",\"description\":\"Price per Pack. It defaults to zero if not specified.\",\"position\":20,\"label\":\"Price per pack\",\"defaultValue\":null,\"dataSource\":\"Reference value (CostHistory Table)\",\"availableColumnTypes\":[\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"T\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":21,\"name\":\"cost\",\"description\":\"Total cost of the product. This will be zero if price is not defined\",\"position\":21,\"label\":\"Total cost\",\"defaultValue\":null,\"dataSource\":\"Derived\",\"availableColumnTypes\":[\"Derived\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"V * T\",\"indicator\":\"Q\",\"used\":true,\"visible\":true,\"mandatory\":false},{\"id\":22,\"name\":\"remarks\",\"description\":\"Any additional remarks\",\"position\":22,\"label\":\"Remarks\",\"defaultValue\":null,\"dataSource\":\"User Input\",\"availableColumnTypes\":[\"UserInput\"],\"selectedColumnType\":\"UserInput\",\"formula\":\"\",\"indicator\":\"L\",\"used\":true,\"visible\":true,\"mandatory\":false}]}";
        assertEquals(contentAsString, msdProductColumn);
    }

    private ContentNegotiatingViewResolver contentNegotiatingViewResolver() {
        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        viewResolver.setMediaTypes(new HashMap<String, String>() {{
            put("json", "application/json");
        }});
        viewResolver.setDefaultViews(new ArrayList<View>() {{
            add(new MappingJacksonJsonView());
        }});
        return viewResolver;
    }
}
