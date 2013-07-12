/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.repository.mapper.*;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;
import static org.openlmis.rnr.domain.RnrStatus.IN_APPROVAL;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(RequisitionStatusChange.class)
public class RequisitionRepositoryTest {

  public static final Long FACILITY_ID = 1L;
  public static final Long PROGRAM_ID = 1L;
  public static final Long PERIOD_ID = 1L;
  public static final Long HIV = 1L;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  RequisitionMapper requisitionMapper;
  @Mock
  private RnrLineItemMapper rnrLineItemMapper;
  @Mock
  private LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;
  @Mock
  private CommentMapper commentMapper;
  @Mock
  private SupervisoryNodeRepository supervisoryNodeRepository;
  @Mock
  private CommaSeparator separator;

  @Mock
  private RequisitionStatusChangeMapper requisitionStatusChangeMapper;
  @Mock
  private RegimenLineItemMapper regimenLineItemMapper;

  @InjectMocks
  private RequisitionRepository requisitionRepository;

  private LossesAndAdjustments lossAndAdjustmentForLineItem = new LossesAndAdjustments();
  private RnrLineItem rnrLineItem1;
  private RnrLineItem rnrLineItem2;
  private Rnr rnr;
  private RegimenLineItem regimenLineItem;

  @Before
  public void setUp() throws Exception {
    rnr = new Rnr();
    rnrLineItem1 = new RnrLineItem();
    rnrLineItem1.setId(1L);
    rnrLineItem1.setBeginningBalance(10);
    rnrLineItem2 = new RnrLineItem();
    rnrLineItem2.setId(2L);
    rnrLineItem2.setBeginningBalance(5);
    rnr.add(rnrLineItem1, true);
    rnr.add(rnrLineItem2, true);
    rnrLineItem1.addLossesAndAdjustments(lossAndAdjustmentForLineItem);
    rnrLineItem2.addLossesAndAdjustments(lossAndAdjustmentForLineItem);
    regimenLineItem = new RegimenLineItem();
    regimenLineItem.setId(1L);
    List<RegimenLineItem> regimenLineItems = new ArrayList<>();
    regimenLineItems.add(regimenLineItem);
    rnr.setRegimenLineItems(regimenLineItems);

    rnr.setFacility(new Facility(FACILITY_ID));
    rnr.setProgram(new Program(PROGRAM_ID));
    rnr.setPeriod(new ProcessingPeriod(PERIOD_ID));
    rnr.setStatus(INITIATED);
  }

  @Test
  public void shouldInsertRnrAndItsLineItems() throws Exception {
    rnr.setId(1L);
    requisitionRepository.insert(rnr);
    assertThat(rnr.getStatus(), is(INITIATED));
    verify(requisitionMapper).insert(rnr);
    verify(rnrLineItemMapper, times(2)).insert(any(RnrLineItem.class));
    verify(lossesAndAdjustmentsMapper, never()).insert(any(RnrLineItem.class), any(LossesAndAdjustments.class));
    verify(regimenLineItemMapper, times(1)).insert(any(RegimenLineItem.class));
    RnrLineItem rnrLineItem = rnr.getFullSupplyLineItems().get(0);
    assertThat(rnrLineItem.getRnrId(), is(1L));
    assertThat(regimenLineItem.getRnrId(), is(1L));
  }

  @Test
  public void shouldUpdateRnrAndItsLineItemsAlongWithLossesAndAdjustments() throws Exception {
    requisitionRepository.update(rnr);
    verify(requisitionMapper).update(rnr);
    verify(lossesAndAdjustmentsMapper).deleteByLineItemId(rnrLineItem1.getId());
    verify(lossesAndAdjustmentsMapper).deleteByLineItemId(rnrLineItem2.getId());
    verify(lossesAndAdjustmentsMapper).insert(rnrLineItem1, lossAndAdjustmentForLineItem);
    verify(lossesAndAdjustmentsMapper).insert(rnrLineItem2, lossAndAdjustmentForLineItem);
    verify(rnrLineItemMapper).update(rnrLineItem1);
    verify(rnrLineItemMapper).update(rnrLineItem2);
  }

  @Test
  public void shouldApproveRnrAndItsLineItems() throws Exception {
    rnr.setStatus(RnrStatus.IN_APPROVAL);
    ArrayList<RnrLineItem> nonFullSupplyLineItems = new ArrayList<>();
    RnrLineItem nonFullSupplyLineItem = new RnrLineItem();
    nonFullSupplyLineItems.add(nonFullSupplyLineItem);
    rnr.setNonFullSupplyLineItems(nonFullSupplyLineItems);
    requisitionRepository.approve(rnr);
    verify(requisitionMapper).update(rnr);
    verify(lossesAndAdjustmentsMapper, never()).deleteByLineItemId(rnrLineItem1.getId());
    verify(lossesAndAdjustmentsMapper, never()).insert(rnrLineItem1, lossAndAdjustmentForLineItem);
    verify(rnrLineItemMapper).updateOnApproval(rnrLineItem1);
    verify(rnrLineItemMapper).updateOnApproval(rnrLineItem2);
    verify(rnrLineItemMapper).updateOnApproval(nonFullSupplyLineItem);
  }

  @Test
  public void shouldReturnNullIfRnrNotDefined() {
    Rnr expectedRnr = null;
    Facility facility = new Facility(FACILITY_ID);
    Program program = new Program(HIV);
    when(requisitionMapper.getRequisitionWithLineItems(facility, program, null)).thenReturn(expectedRnr);
    Rnr rnr = requisitionRepository.getRequisitionWithLineItems(facility, program, null);
    assertThat(rnr, is(expectedRnr));
  }

  @Test
  public void shouldGetRnrById() throws Exception {
    Rnr expectedRnr = new Rnr();
    Long rnrId = 1L;
    when(requisitionMapper.getById(rnrId)).thenReturn(expectedRnr);
    Rnr returnedRnr = requisitionRepository.getById(rnrId);
    assertThat(returnedRnr, is(expectedRnr));
  }

  @Test
  public void shouldThrowExceptionIfRnrNotFound() throws Exception {
    Long rnrId = 1L;
    when(requisitionMapper.getById(rnrId)).thenReturn(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.rnr.not.found");
    requisitionRepository.getById(rnrId);
  }

  @Test
  public void shouldGetRequisitionByRoleAssignment() throws Exception {
    List<Rnr> requisitions = new ArrayList<>();
    RoleAssignment roleAssignment = new RoleAssignment();
    when(requisitionMapper.getAuthorizedRequisitions(roleAssignment)).thenReturn(requisitions);

    List<Rnr> actualRequisitions = requisitionRepository.getAuthorizedRequisitions(roleAssignment);

    assertThat(actualRequisitions, is(requisitions));
  }

  @Test
  public void shouldGetTheLastRequisitionToEnterThePostSubmitFlow() throws Exception {
    Rnr rnr = new Rnr();
    when(requisitionMapper.getLastRequisitionToEnterThePostSubmitFlow(FACILITY_ID, PROGRAM_ID)).thenReturn(rnr);

    assertThat(requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(FACILITY_ID, PROGRAM_ID), is(rnr));
  }

  @Test
  public void shouldInsertAllNonFullSupplyLineItems() throws Exception {
    RnrLineItem rnrLineItem = new RnrLineItem();
    rnrLineItem.setBeginningBalance(2);
    RnrLineItem rnrLineItem2 = new RnrLineItem();
    rnr.add(rnrLineItem, false);
    rnr.add(rnrLineItem2, false);
    RnrLineItem fullSupply = new RnrLineItem();
    fullSupply.setFullSupply(true);

    requisitionRepository.update(rnr);

    verify(rnrLineItemMapper).insertNonFullSupply(rnrLineItem);
    verify(rnrLineItemMapper).insertNonFullSupply(rnrLineItem2);
    verify(rnrLineItemMapper, never()).insertNonFullSupply(fullSupply);
  }

  @Test
  public void shouldUpdateOnApprovalExistingNonFullSupplyLineItems() throws Exception {
    long rnrId = 1L;
    rnr.setId(rnrId);
    RnrLineItem rnrLineItem = make(a(RnrLineItemBuilder.defaultRnrLineItem));
    rnrLineItem.setProductCode("P1");
    rnrLineItem.setRnrId(rnrId);
    RnrLineItem rnrLineItem2 = make(a(RnrLineItemBuilder.defaultRnrLineItem));
    rnrLineItem2.setProductCode("P2");
    rnrLineItem2.setRnrId(rnrId);
    rnr.add(rnrLineItem, false);
    rnr.add(rnrLineItem2, false);
    when(rnrLineItemMapper.getExistingNonFullSupplyItemByRnrIdAndProductCode(rnrId, "P1")).thenReturn(rnrLineItem);
    when(rnrLineItemMapper.getExistingNonFullSupplyItemByRnrIdAndProductCode(rnrId, "P2")).thenReturn(rnrLineItem2);

    rnrLineItem.setQuantityApproved(5);
    rnrLineItem2.setQuantityRequested(3);

    rnr.setStatus(IN_APPROVAL);
    requisitionRepository.update(rnr);

    verify(rnrLineItemMapper).updateOnApproval(rnr.getFullSupplyLineItems().get(0));
    verify(rnrLineItemMapper).updateOnApproval(rnr.getFullSupplyLineItems().get(1));
    verify(rnrLineItemMapper).updateOnApproval(rnr.getNonFullSupplyLineItems().get(0));
    verify(rnrLineItemMapper).updateOnApproval(rnr.getNonFullSupplyLineItems().get(1));
  }

  @Test
  public void shouldUpdateExistingNonFullSupplyLineItems() throws Exception {
    long rnrId = 1L;
    rnr.setId(rnrId);
    RnrLineItem rnrLineItem = make(a(RnrLineItemBuilder.defaultRnrLineItem));
    rnrLineItem.setProductCode("P1");
    rnrLineItem.setRnrId(rnrId);
    rnr.add(rnrLineItem, false);
    when(rnrLineItemMapper.getExistingNonFullSupplyItemByRnrIdAndProductCode(rnrId, "P1")).thenReturn(rnrLineItem);

    rnrLineItem.setQuantityApproved(5);

    rnr.setStatus(INITIATED);
    requisitionRepository.update(rnr);

    verify(rnrLineItemMapper).update(rnr.getFullSupplyLineItems().get(0));
    verify(rnrLineItemMapper).update(rnr.getFullSupplyLineItems().get(1));
    verify(rnrLineItemMapper).update(rnr.getNonFullSupplyLineItems().get(0));
  }

  @Test
  public void shouldGetRequisitionsForFacilityProgramAndPeriods() throws Exception {
    Facility facility = new Facility(1L);
    Program program = new Program(1L);
    List<ProcessingPeriod> periods = asList(new ProcessingPeriod(1L), new ProcessingPeriod(2L));
    when(separator.commaSeparateIds(periods)).thenReturn("{1, 2}");
    List<Rnr> expected = new ArrayList<>();
    when(requisitionMapper.getPostSubmitRequisitions(facility, program, "{1, 2}")).thenReturn(expected);

    List<Rnr> actual = requisitionRepository.getPostSubmitRequisitions(facility, program, periods);

    assertThat(actual, is(expected));
    verify(requisitionMapper).getPostSubmitRequisitions(facility, program, "{1, 2}");
  }


  @Test
  public void shouldGetCategoryCount() {

    boolean fullSupply = true;
    when(rnrLineItemMapper.getCategoryCount(rnr, fullSupply)).thenReturn(10);

    Integer categoryCount = requisitionRepository.getCategoryCount(rnr, fullSupply);

    assertThat(categoryCount, is(10));
    verify(rnrLineItemMapper).getCategoryCount(rnr, fullSupply);

  }

  @Test
  public void shouldGetCommentsForARnR() throws Exception {
    List<Comment> comments = new ArrayList<>();
    when(commentMapper.getByRnrId(1L)).thenReturn(comments);
    List<Comment> returnedComments = requisitionRepository.getCommentsByRnrID(1L);
    verify(commentMapper).getByRnrId(1L);
    assertThat(returnedComments, is(comments));
  }

  @Test
  public void shouldInsertAComment() throws Exception {
    Comment comment = new Comment();
    requisitionRepository.insertComment(comment);

    verify(commentMapper).insert(comment);
  }

  @Test
  public void shouldLogRequisitionStatusChanges() throws Exception {
    RequisitionStatusChange requisitionStatusChange = new RequisitionStatusChange();
    Rnr requisition = new Rnr();
    whenNew(RequisitionStatusChange.class).withArguments(requisition).thenReturn(requisitionStatusChange);
    requisitionRepository.logStatusChange(requisition);
    verify(requisitionStatusChangeMapper).insert(requisitionStatusChange);
  }

  @Test
  public void itShouldUseMapperToReturnRequisitionWithoutLineItems() throws Exception {

    long periodId = 8L;
    long programId = 5L;
    long facilityId = 3L;

    Rnr requisition = new Rnr();

    when(requisitionMapper.getRequisitionWithoutLineItems(facilityId, programId, periodId)).thenReturn(requisition);
    Rnr receivedRnr = requisitionRepository.getRequisitionWithoutLineItems(facilityId, programId, periodId);
    assertThat(receivedRnr, is(requisition));
  }
}
