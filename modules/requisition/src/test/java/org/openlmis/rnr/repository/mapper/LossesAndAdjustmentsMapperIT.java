/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class LossesAndAdjustmentsMapperIT {
  public static final Long MODIFIED_BY = 1L;
  public static final Long HIV = 1L;

  @Autowired
  private LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;
  @Autowired
  private RequisitionMapper requisitionMapper;
  @Autowired
  private RnrLineItemMapper rnrLineItemMapper;
  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private ProgramMapper programMapper;
  @Autowired
  private ProductMapper productMapper;
  @Autowired
  private ProgramProductMapper programProductMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;

  private RnrLineItem rnrLineItem;
  private LossesAndAdjustments lossAndAdjustment;

  @Before
  public void setUp() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);

    Program program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, new Money("12.5"));
    programProductMapper.insert(programProduct);

    FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct("warehouse", programProduct, 3);
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, processingSchedule.getId())));
    processingPeriodMapper.insert(processingPeriod);

    Rnr requisition = new Rnr(facility.getId(), HIV, processingPeriod.getId(), MODIFIED_BY);
    requisition.setStatus(INITIATED);
    requisitionMapper.insert(requisition);

    rnrLineItem = new RnrLineItem(requisition.getId(), facilityApprovedProduct, MODIFIED_BY);
    rnrLineItemMapper.insert(rnrLineItem);
    lossAndAdjustment = new LossesAndAdjustments();
    LossesAndAdjustmentsType lossesAndAdjustmentsType = new LossesAndAdjustmentsType();
    lossesAndAdjustmentsType.setName("CLINIC_RETURN");
    lossAndAdjustment.setType(lossesAndAdjustmentsType);
    lossAndAdjustment.setQuantity(20);
  }

  @Test
  public void shouldInsertLossesAndAdjustments() {
    lossesAndAdjustmentsMapper.insert(rnrLineItem, lossAndAdjustment);

    List<LossesAndAdjustments> lossesAndAdjustmentsList = lossesAndAdjustmentsMapper.getByRnrLineItem(rnrLineItem.getId());
    LossesAndAdjustments lineItemLossAndAdjustment = lossesAndAdjustmentsList.get(0);

    assertThat(lossesAndAdjustmentsList.size(), is(1));
    assertThat(lineItemLossAndAdjustment.getQuantity(), is(lossAndAdjustment.getQuantity()));
    assertThat(lineItemLossAndAdjustment.getType().getName(), is(lossAndAdjustment.getType().getName()));
    assertThat(lineItemLossAndAdjustment.getModifiedBy(), is(MODIFIED_BY));
    assertThat(lineItemLossAndAdjustment.getModifiedDate(), is(notNullValue()));
  }

  @Test
  public void shouldDeleteLossesAndAdjustmentForLineItem() throws Exception {
    lossesAndAdjustmentsMapper.insert(rnrLineItem, lossAndAdjustment);
    lossesAndAdjustmentsMapper.deleteByLineItemId(rnrLineItem.getId());
    assertThat(lossesAndAdjustmentsMapper.getByRnrLineItem(rnrLineItem.getId()).size(), is(0));
  }

  @Test
  public void shouldReturnAllLossesAndAdjustmentsTypesAccordingToDisplayOrder() {
    List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes = lossesAndAdjustmentsMapper.getLossesAndAdjustmentsTypes();
    assertThat(lossesAndAdjustmentsTypes.size(), is(9));
    assertThat(lossesAndAdjustmentsTypes.get(0).getDisplayOrder(), is(1));
    assertThat(lossesAndAdjustmentsTypes.get(1).getDisplayOrder(), is(2));
    assertThat(lossesAndAdjustmentsTypes.get(2).getDisplayOrder(), is(3));
  }

}
