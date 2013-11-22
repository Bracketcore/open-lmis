/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.rnr.domain;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.calculation.DefaultStrategy;
import org.openlmis.rnr.calculation.RnrCalculationStrategy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProductBuilder.*;
import static org.openlmis.core.builder.ProductBuilder.productCategoryDisplayOrder;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.rnr.builder.RnrColumnBuilder.*;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.BEGINNING_BALANCE;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.LOSSES_AND_ADJUSTMENTS;
import static org.openlmis.rnr.domain.RnRColumnSource.CALCULATED;
import static org.openlmis.rnr.domain.RnRColumnSource.USER_INPUT;
import static org.openlmis.rnr.domain.RnrLineItem.RNR_VALIDATION_ERROR;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(RnrLineItem.class)
public class RnrLineItemTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private RnrLineItem lineItem;
  private List<RnrColumn> templateColumns;
  private ProcessingPeriod period;
  private List<LossesAndAdjustmentsType> lossesAndAdjustmentsList;

  @Mock
  ProgramRnrTemplate template;

  @Mock
  RnrColumn column;

  private RnrCalculationStrategy calcStrategy;

  @Before
  public void setUp() throws Exception {
    period = new ProcessingPeriod() {{
      setNumberOfMonths(1);
    }};
    templateColumns = new ArrayList<>();
    addVisibleColumns(templateColumns);
    lineItem = make(a(defaultRnrLineItem));
    template = new ProgramRnrTemplate(getRnrColumns());
    LossesAndAdjustmentsType additive1 = new LossesAndAdjustmentsType("TRANSFER_IN", "TRANSFER IN", true, 1);
    LossesAndAdjustmentsType additive2 = new LossesAndAdjustmentsType("additive2", "Additive 2", true, 2);
    LossesAndAdjustmentsType subtractive1 = new LossesAndAdjustmentsType("subtractive1", "Subtractive 1", false, 3);
    LossesAndAdjustmentsType subtractive2 = new LossesAndAdjustmentsType("subtractive2", "Subtractive 2", false, 4);
    lossesAndAdjustmentsList = asList(
      new LossesAndAdjustmentsType[]{additive1, additive2, subtractive1, subtractive2});
    calcStrategy = mock(DefaultStrategy.class);
  }

  private void addVisibleColumns(List<RnrColumn> templateColumns) {
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.BEGINNING_BALANCE, true, true);
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.QUANTITY_DISPENSED, true, null);
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.QUANTITY_RECEIVED, true, null);
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.NEW_PATIENT_COUNT, true, null);
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.STOCK_OUT_DAYS, true, null);
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.QUANTITY_REQUESTED, true, null);
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.REASON_FOR_REQUESTED_QUANTITY, true, null);
  }

  private void addColumnToTemplate(List<RnrColumn> templateColumns, String columnName, Boolean visible, Boolean formulaValidation) {
    RnrColumn rnrColumn = new RnrColumn();
    rnrColumn.setName(columnName);
    rnrColumn.setVisible(visible);
    if (formulaValidation != null) rnrColumn.setFormulaValidationRequired(formulaValidation);
    templateColumns.add(rnrColumn);
  }

  @Test
  public void shouldCalculateAMC() throws Exception {
    lineItem.calculateAmc(calcStrategy, period);

    verify(calcStrategy).calculateAmc(period, lineItem.getNormalizedConsumption(), lineItem.getPreviousNormalizedConsumptions());
  }

  @Test
  public void shouldCalculatePacksToShip() throws Exception {
    lineItem.calculatePacksToShip(calcStrategy);

    verify(calcStrategy).calculatePacksToShip(lineItem.getQuantityApproved(), lineItem.getPackSize(),
      lineItem.getPackRoundingThreshold(), lineItem.getRoundToZero());
  }

  @Test
  public void shouldCalculateMaxStockQuantity() throws Exception {
    lineItem.calculateMaxStockQuantity(calcStrategy,template);

    verify(calcStrategy).calculateMaxStockQuantity(lineItem.getMaxMonthsOfStock(), lineItem.getAmc());
  }

  @Test
  public void shouldCalculateOrderQuantity() throws Exception {
    lineItem.calculateOrderQuantity(calcStrategy);

    verify(calcStrategy).calculateOrderQuantity(lineItem.getMaxStockQuantity(), lineItem.getStockInHand());
  }

  @Test
  public void shouldCalculateNormalizedConsumption() throws Exception {
    lineItem.calculateNormalizedConsumption(calcStrategy,template);

    verify(calcStrategy).calculateNormalizedConsumption(lineItem.getStockOutDays(), lineItem.getQuantityDispensed(),
      lineItem.getNewPatientCount(), lineItem.getDosesPerMonth(), lineItem.getDosesPerDispensingUnit(), null);
  }

  @Test
  public void shouldCalculateLossesAndAdjustments() throws Exception {
    lineItem.calculateTotalLossesAndAdjustments(calcStrategy, lossesAndAdjustmentsList);

    verify(calcStrategy).calculateTotalLossesAndAdjustments(lineItem.getLossesAndAdjustments(), lossesAndAdjustmentsList);
  }

  @Test
  public void shouldCalculateQuantityDispensed() throws Exception {
    lineItem.calculateQuantityDispensed(calcStrategy);

    verify(calcStrategy).calculateQuantityDispensed(lineItem.getBeginningBalance(), lineItem.getQuantityReceived(),
      lineItem.getTotalLossesAndAdjustments(), lineItem.getStockInHand());
  }

  @Test
  public void shouldCalculateStockInHand() throws Exception {
    lineItem.calculateStockInHand(calcStrategy);

    verify(calcStrategy).calculateStockInHand(lineItem.getBeginningBalance(), lineItem.getQuantityReceived(),
      lineItem.getTotalLossesAndAdjustments(), lineItem.getQuantityDispensed());
  }

  @Test
  public void shouldCalculateDefaultApprovedQuantity() {
    lineItem.setFieldsForApproval(calcStrategy);

    verify(calcStrategy).calculateDefaultApprovedQuantity(lineItem.getFullSupply(), lineItem.getCalculatedOrderQuantity(),
      lineItem.getQuantityRequested());
  }

  @Test
  public void shouldSetFieldValuesToNullIfSkipped() {
    lineItem.setCalculatedOrderQuantity(null);
    lineItem.setSkipped(true);
    lineItem.setExpirationDate("some date");
    when(calcStrategy.calculateDefaultApprovedQuantity(lineItem.getFullSupply(), null, null)).thenReturn(null);


    lineItem.setFieldsForApproval(calcStrategy);

    assertThat(lineItem.getLossesAndAdjustments().size(), is(0));
    assertThat(lineItem.getTotalLossesAndAdjustments(), is(0));
    assertThat(lineItem.getQuantityDispensed(), is(nullValue()));
    assertThat(lineItem.getBeginningBalance(), is(nullValue()));
    assertThat(lineItem.getReasonForRequestedQuantity(), is(nullValue()));
    assertThat(lineItem.getStockInHand(), is(nullValue()));
    assertThat(lineItem.getStockOutDays(), is(nullValue()));
    assertThat(lineItem.getNewPatientCount(), is(nullValue()));
    assertThat(lineItem.getQuantityRequested(), is(nullValue()));
    assertThat(lineItem.getQuantityApproved(), is(nullValue()));
    assertThat(lineItem.getNormalizedConsumption(), is(nullValue()));
    assertThat(lineItem.getPacksToShip(), is(nullValue()));
    assertThat(lineItem.getRemarks(), is(nullValue()));
    assertThat(lineItem.getExpirationDate(), is(nullValue()));
  }

  @Test
  public void shouldConstructRnrLineItem() {

    Program program = make(a(defaultProgram));
    Product product = make(
      a(defaultProduct, with(code, "ASPIRIN"), with(productCategoryDisplayOrder, 3), with(displayOrder, 9)));
    product.setDispensingUnit("Strip");

    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true);
    RnrLineItem rnrLineItem = new RnrLineItem(1L, new FacilityTypeApprovedProduct("warehouse", programProduct, 3), 1L, 1L);

    assertThat(rnrLineItem.getFullSupply(), is(product.getFullSupply()));
    assertThat(rnrLineItem.getMaxMonthsOfStock(), is(3));
    assertThat(rnrLineItem.getRnrId(), is(1L));
    assertThat(rnrLineItem.getDispensingUnit(), is("Strip"));
    assertThat(rnrLineItem.getProductCode(), is("ASPIRIN"));
    assertThat(rnrLineItem.getDosesPerMonth(), is(30));
    assertThat(rnrLineItem.getModifiedBy(), is(1L));
    assertThat(rnrLineItem.getDosesPerDispensingUnit(), is(10));
    assertThat(rnrLineItem.getProductCategoryDisplayOrder(), is(3));
    assertThat(rnrLineItem.getProductDisplayOrder(), is(9));
  }

  @Test
  public void shouldThrowErrorIfBeginningBalanceNotPresent() throws Exception {
    lineItem.setBeginningBalance(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowErrorIfQuantityReceivedNotPresent() throws Exception {
    lineItem.setQuantityReceived(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowErrorIfQuantityConsumedNotPresent() throws Exception {
    lineItem.setQuantityDispensed(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowErrorIfNewPatientsNotPresent() throws Exception {
    lineItem.setNewPatientCount(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowErrorIfStockOutDaysNotPresent() throws Exception {
    lineItem.setStockOutDays(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowErrorIfStockInHandNotPresentAndIsUserInput() throws Exception {
    lineItem.setStockInHand(null);
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.STOCK_IN_HAND, true, false);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldNotThrowErrorIfRequestedQuantityAndItsExplanationAreNull() throws Exception {
    lineItem.setQuantityRequested(null);
    lineItem.setReasonForRequestedQuantity(null);

    lineItem.validateMandatoryFields(template);
  }


  @Test
  public void shouldNotThrowErrorIfRequestedQuantityAndItsExplanationArePresent() {
    lineItem.setQuantityRequested(123);
    lineItem.setReasonForRequestedQuantity("something");

    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowErrorIfRequestedQuantityIsPresentAndExplanationIsNotPresent() throws Exception {
    lineItem.setQuantityRequested(70);
    lineItem.setReasonForRequestedQuantity(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);

    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldNotThrowErrorIfRequestedQuantityIsNullAndExplanationIsPresent() {
    lineItem.setQuantityRequested(null);
    lineItem.setReasonForRequestedQuantity("something");

    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowExceptionIfCalculationForQuantityDispensedAndStockInHandNotValidAndFormulaValidatedTrue() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);

    List<LossesAndAdjustments> list = Arrays.asList(createLossAndAdjustment("CLINIC_RETURN", true, 1));

    lineItem.setLossesAndAdjustments(list);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(9);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    lineItem.validateCalculatedFields(template);
  }

  private LossesAndAdjustments createLossAndAdjustment(String typeName, boolean additive, int quantity) {
    LossesAndAdjustments lossAndAdjustment = new LossesAndAdjustments();
    LossesAndAdjustmentsType lossesAndAdjustmentsType = new LossesAndAdjustmentsType();
    lossesAndAdjustmentsType.setName(typeName);
    lossesAndAdjustmentsType.setAdditive(additive);
    lossAndAdjustment.setType(lossesAndAdjustmentsType);
    lossAndAdjustment.setQuantity(quantity);
    return lossAndAdjustment;
  }

  @Test
  public void shouldNotThrowExceptionIfCalculationForQuantityDispensedAndStockInHandValidAndFormulaValidatedTrue() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setLossesAndAdjustments(asList(createLossAndAdjustment("", true, 1)));
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(10);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldNotThrowExceptionIfCalculationForQuantityDispensedAndStockInHandNotValidAndFormulaValidatedFalse() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setTotalLossesAndAdjustments(1);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(9);
    templateColumns.get(0).setFormulaValidationRequired(false);

    lineItem.validateMandatoryFields(template);
  }


  @Test
  public void shouldNotThrowExceptionIfCalculationForQuantityDispensedAndStockInHandValidAndFormulaValidatedFalse() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setTotalLossesAndAdjustments(1);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(10);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldCalculateAMCAndMaxStockQuantityAndOrderedQuantityOnlyWhenAuthorized() throws Exception {
    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption", calcStrategy, template);

    spyLineItem.calculateForFullSupply(calcStrategy, period, template, AUTHORIZED, lossesAndAdjustmentsList);

    verify(spyLineItem).calculateAmc(calcStrategy, period);
    verify(spyLineItem).calculateMaxStockQuantity(calcStrategy, template);
    verify(spyLineItem).calculateOrderQuantity(calcStrategy);
  }

  @Test
  public void shouldNotThrowErrorIfAllMandatoryFieldsPresent() throws Exception {
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldCopyApproverEditableFields() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem));
    editedLineItem.setQuantityApproved(1872);
    editedLineItem.setRemarks("Approved");
    editedLineItem.setStockInHand(1946);
    ProgramRnrTemplate template = new ProgramRnrTemplate(getRnrColumns());
    lineItem.copyApproverEditableFields(editedLineItem, template);

    assertThat(lineItem.getQuantityApproved(), is(1872));
    assertThat(lineItem.getRemarks(), is("Approved"));
    assertThat(lineItem.getStockInHand(), is(RnrLineItemBuilder.STOCK_IN_HAND));
  }

  @Test
  public void shouldCopyTotalLossesAndAdjustments() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem));
    editedLineItem.setTotalLossesAndAdjustments(10);

    lineItem.copyCreatorEditableFieldsForFullSupply(editedLineItem, new ProgramRnrTemplate(getRnrColumns()));

    assertThat(lineItem.getTotalLossesAndAdjustments(), is(10));
  }

  @Test
  public void shouldCopyEditableFieldsForNonFullSupplyBasedOnTemplate() throws Exception {
    lineItem.copyCreatorEditableFieldsForNonFullSupply(make(a(defaultRnrLineItem, with(quantityRequested, 9),
      with(reasonForRequestedQuantity, "no reason"), with(remarks, "no remarks"))), new ProgramRnrTemplate(getRnrColumnsForNonFullSupply()));

    assertThat(lineItem.getReasonForRequestedQuantity(), is("no reason"));
    assertThat(lineItem.getRemarks(), is("no remarks"));
    assertThat(lineItem.getQuantityRequested(), is(9));

  }

  private ArrayList<RnrColumn> getRnrColumns() {
    return new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.QUANTITY_RECEIVED), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.QUANTITY_DISPENSED), with(visible, false),
        with(source, CALCULATED))));
      add(make(a(defaultRnrColumn, with(columnName, LOSSES_AND_ADJUSTMENTS), with(visible, true))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.NEW_PATIENT_COUNT), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.STOCK_OUT_DAYS), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.STOCK_IN_HAND), with(visible, false),
        with(source, CALCULATED))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.BEGINNING_BALANCE), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.REMARKS), with(visible, true), with(source, USER_INPUT))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.QUANTITY_APPROVED), with(visible, true),
        with(source, USER_INPUT))));
    }};
  }

  private ArrayList<RnrColumn> getRnrColumnsForNonFullSupply() {
    return new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.QUANTITY_REQUESTED), with(visible, true), with(source, USER_INPUT))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.REMARKS), with(visible, true), with(source, USER_INPUT))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.REASON_FOR_REQUESTED_QUANTITY), with(visible, true),
        with(source, USER_INPUT))));
    }};
  }


  @Test
  public void shouldCopyUserEditableFieldsOnlyIfVisible() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem));
    editedLineItem.setRemarks("Submitted");
    editedLineItem.setBeginningBalance(12);
    editedLineItem.setQuantityReceived(23);
    editedLineItem.setQuantityDispensed(32);
    editedLineItem.setStockInHand(1946);
    editedLineItem.setNewPatientCount(1);
    editedLineItem.setStockOutDays(7);
    editedLineItem.setQuantityRequested(43);
    editedLineItem.setReasonForRequestedQuantity("Reason");
    List<LossesAndAdjustments> lossesAndAdjustments = new ArrayList<>();
    editedLineItem.setLossesAndAdjustments(lossesAndAdjustments);

    lineItem.copyCreatorEditableFieldsForFullSupply(editedLineItem, new ProgramRnrTemplate(getRnrColumns()));

    assertThat(lineItem.getBeginningBalance(), is(RnrLineItemBuilder.BEGINNING_BALANCE));
    assertThat(lineItem.getStockOutDays(), is(RnrLineItemBuilder.STOCK_OUT_DAYS));
  }

  @Test
  public void shouldThrowExceptionIfNonFullSupplyLineItemHasRequestedQuantityAsNull() {
    Integer nullInteger = null;
    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(quantityRequested, nullInteger)));
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    rnrLineItem.validateNonFullSupply();
  }

  @Test
  public void shouldThrowExceptionIfNonFullSupplyLineItemHasRequestedQuantityIsNegative() {
    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(quantityRequested, -10)));
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    rnrLineItem.validateNonFullSupply();
  }

  @Test
  public void shouldThrowExceptionIfNonFullSupplyLineItemHasReasonForRequestedQuantityNull() {
    String nullString = null;
    RnrLineItem rnrLineItem = make(
      a(defaultRnrLineItem, with(RnrLineItemBuilder.reasonForRequestedQuantity, nullString)));
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    rnrLineItem.validateNonFullSupply();
  }

  @Test
  public void shouldCalculateCostAsZeroIfPacksToShipIsNull() {
    Integer nullInteger = null;
    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(packsToShip, nullInteger)));
    Money money = rnrLineItem.calculateCost();
    assertThat(money.getValue().intValue(), Is.is(0));
  }

  @Test
  public void shouldCalculateCostIfPacksToShipIsNotNull() {
    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(packsToShip, 5)));
    Money money = rnrLineItem.calculateCost();
    assertThat(money.getValue().intValue(), Is.is(20));
  }

  @Test
  public void shouldCalculateStockInHandIfCalculated() throws Exception {
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, CALCULATED), with(columnName, ProgramRnrTemplate.STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, ProgramRnrTemplate.QUANTITY_DISPENSED))));
    }};
    lineItem.setStockInHand(99);
    lineItem.calculateForFullSupply(calcStrategy, period, new ProgramRnrTemplate(columns), SUBMITTED, lossesAndAdjustmentsList);

    verify(calcStrategy).calculateStockInHand(lineItem.getBeginningBalance(), lineItem.getQuantityReceived(),
      lineItem.getTotalLossesAndAdjustments(), lineItem.getQuantityDispensed());
  }

  @Test
  public void shouldNotCalculateStockInHandIfUserInput() throws Exception {
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, ProgramRnrTemplate.STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, ProgramRnrTemplate.QUANTITY_DISPENSED))));
    }};
    ProgramRnrTemplate template = new ProgramRnrTemplate(columns);
    lineItem.setStockInHand(66);
    lineItem.calculateForFullSupply(calcStrategy, period, template, SUBMITTED, lossesAndAdjustmentsList);

    verify(calcStrategy, never()).calculateStockInHand(lineItem.getBeginningBalance(), lineItem.getQuantityReceived(),
      lineItem.getTotalLossesAndAdjustments(), lineItem.getQuantityDispensed());
  }

  @Test
  public void shouldCalculateQuantityDispensedIfCalculated() throws Exception {
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, ProgramRnrTemplate.STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, CALCULATED), with(columnName, ProgramRnrTemplate.QUANTITY_DISPENSED))));
    }};
    ProgramRnrTemplate template = new ProgramRnrTemplate(columns);
    lineItem.setQuantityDispensed(4);
    lineItem.calculateForFullSupply(calcStrategy, period, template, SUBMITTED, lossesAndAdjustmentsList);

    verify(calcStrategy).calculateQuantityDispensed(lineItem.getBeginningBalance(), lineItem.getQuantityReceived(),
      lineItem.getTotalLossesAndAdjustments(), lineItem.getStockInHand());
  }

  @Test
  public void shouldNotCalculateQuantityDispensedIfUserInput() throws Exception {
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, ProgramRnrTemplate.STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, ProgramRnrTemplate.QUANTITY_DISPENSED))));
    }};

    lineItem.setQuantityDispensed(0);
    lineItem.calculateForFullSupply(calcStrategy, period, new ProgramRnrTemplate(columns), SUBMITTED, lossesAndAdjustmentsList);

    verify(calcStrategy, never()).calculateQuantityDispensed(lineItem.getBeginningBalance(), lineItem.getQuantityReceived(),
      lineItem.getTotalLossesAndAdjustments(), lineItem.getStockInHand());
  }

  @Test
  public void shouldCopyBeginningBalanceIfItIsVisible() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem));
    editedLineItem.setBeginningBalance(44);
    template.getRnrColumnsMap().get(BEGINNING_BALANCE).setVisible(true);
    lineItem.copyCreatorEditableFieldsForFullSupply(editedLineItem, template);

    assertThat(lineItem.getBeginningBalance(), is(editedLineItem.getBeginningBalance()));
  }

  @Test
  public void shouldNotCopyQuantityApprovedWhileCopyingNonApproverEditableFields() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem, with(quantityApproved, 89)));
    lineItem.copyCreatorEditableFieldsForFullSupply(editedLineItem, new ProgramRnrTemplate(getRnrColumns()));

    assertThat(lineItem.getQuantityApproved(), is(RnrLineItemBuilder.QUANTITY_APPROVED));
  }


  @Test
  public void shouldValidateLineItemForApproval() throws Exception {
    lineItem.setQuantityApproved(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    lineItem.validateForApproval();

  }
}
