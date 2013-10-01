/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.rnr.repository.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.*;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.*;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.SupplyLineBuilder.defaultProgram;
import static org.openlmis.core.builder.SupplyLineBuilder.defaultSupplyLine;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.RnrStatus.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class RequisitionMapperIT {
  public static final Long MODIFIED_BY = 1L;
  public static final Long USER_ID = 2L;

  private Facility facility;
  private ProcessingSchedule processingSchedule;
  private ProcessingPeriod processingPeriod1;
  private ProcessingPeriod processingPeriod2;
  private ProcessingPeriod processingPeriod3;
  private Program program;

  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private RequisitionMapper mapper;
  @Autowired
  RnrLineItemMapper lineItemMapper;
  @Autowired
  LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;
  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;
  @Autowired
  private ProductMapper productMapper;
  @Autowired
  private ProgramProductMapper programProductMapper;
  @Autowired
  private FacilityApprovedProductMapper facilityApprovedProductMapper;
  @Autowired
  private ProgramMapper programMapper;
  @Autowired
  private CommentMapper commentMapper;
  @Autowired
  SupplyLineMapper supplyLineMapper;
  @Autowired
  RequisitionStatusChangeMapper requisitionStatusChangeMapper;

  private SupervisoryNode supervisoryNode;
  private SupplyLine supplyLine;


  @Before
  public void setUp() {
    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    insertProgram();

    processingPeriod1 = insertPeriod("Period 1");
    processingPeriod2 = insertPeriod("Period 2");
    processingPeriod3 = insertPeriod("Period 3");
    supervisoryNode = insertSupervisoryNode();
    supplyLine = make(a(defaultSupplyLine, with(SupplyLineBuilder.facility, facility),
      with(SupplyLineBuilder.supervisoryNode, supervisoryNode), with(defaultProgram, program)));
    supplyLineMapper.insert(supplyLine);
  }

  @Test
  public void shouldSetRequisitionId() {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false);
    assertThat(requisition.getId(), is(notNullValue()));
  }

  @Test
  public void shouldGetRequisitionById() {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false);
    Product product = insertProduct(true, "P1");
    RnrLineItem fullSupplyLineItem = make(a(defaultRnrLineItem, with(fullSupply, true), with(productCode, product.getCode())));
    RnrLineItem nonFullSupplyLineItem = make(a(defaultRnrLineItem, with(fullSupply, false), with(productCode, product.getCode())));
    fullSupplyLineItem.setRnrId(requisition.getId());
    nonFullSupplyLineItem.setRnrId(requisition.getId());
    lineItemMapper.insert(fullSupplyLineItem);
    lineItemMapper.insert(nonFullSupplyLineItem);

    User author = new User();
    author.setId(1L);
    Comment comment = new Comment(requisition.getId(), author, "A comment", null);
    commentMapper.insert(comment);
    updateSupplyingDepotForRequisition(requisition);

    Rnr fetchedRequisition = mapper.getById(requisition.getId());

    assertThat(fetchedRequisition.getId(), is(requisition.getId()));
    assertThat(fetchedRequisition.getProgram().getId(), is(equalTo(program.getId())));
    assertThat(fetchedRequisition.getFacility().getId(), is(equalTo(facility.getId())));
    assertThat(fetchedRequisition.getPeriod().getId(), is(equalTo(processingPeriod1.getId())));
    assertThat(fetchedRequisition.getModifiedBy(), is(equalTo(MODIFIED_BY)));
    assertThat(fetchedRequisition.getStatus(), is(equalTo(INITIATED)));
    assertThat(fetchedRequisition.getFullSupplyLineItems().size(), is(1));
    assertThat(fetchedRequisition.getNonFullSupplyLineItems().size(), is(1));
  }

  private void updateSupplyingDepotForRequisition(Rnr requisition) {
    requisition.setSupplyingDepot(facility);
    mapper.update(requisition);
  }

  @Test
  public void shouldUpdateRequisition() {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false);
    requisition.setModifiedBy(USER_ID);
    Date submittedDate = new Date();
    requisition.setSubmittedDate(submittedDate);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());
    requisition.setSupplyingDepot(facility);

    mapper.update(requisition);

    Rnr updatedRequisition = mapper.getById(requisition.getId());

    assertThat(updatedRequisition.getId(), is(requisition.getId()));
    assertThat(updatedRequisition.getSupervisoryNodeId(), is(requisition.getSupervisoryNodeId()));
    assertThat(updatedRequisition.getModifiedBy(), is(equalTo(USER_ID)));
  }

  @Test
  public void shouldReturnRequisitionWithoutLineItemsByFacilityProgramAndPeriod() {

    Product fullSupplyProduct = insertProduct(true, "P1");
    Product nonFullSupplyProduct = insertProduct(false, "P2");

    ProgramProduct fullSupplyProgramProduct = insertProgramProduct(fullSupplyProduct, program);
    ProgramProduct nonFullSupplyProgramProduct = insertProgramProduct(nonFullSupplyProduct, program);

    FacilityTypeApprovedProduct fullSupplyFacilityTypeApprovedProduct = insertFacilityApprovedProduct(fullSupplyProgramProduct);
    FacilityTypeApprovedProduct nonFullSupplyFacilityTypeApprovedProduct = insertFacilityApprovedProduct(nonFullSupplyProgramProduct);

    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false);
    insertRequisition(processingPeriod2, program, INITIATED, false);

    insertRnrLineItem(requisition, fullSupplyFacilityTypeApprovedProduct);
    insertRnrLineItem(requisition, nonFullSupplyFacilityTypeApprovedProduct);

    Rnr returnedRequisition = mapper.getRequisitionWithLineItems(facility, new Program(program.getId()), processingPeriod1);

    assertThat(returnedRequisition.getId(), is(requisition.getId()));
    assertThat(returnedRequisition.getFacility().getId(), is(facility.getId()));
    assertThat(returnedRequisition.getProgram().getId(), is(program.getId()));
    assertThat(returnedRequisition.getPeriod().getId(), is(processingPeriod1.getId()));
    assertThat(returnedRequisition.getFullSupplyLineItems().size(), is(1));
    assertThat(returnedRequisition.getNonFullSupplyLineItems().size(), is(1));
  }

  @Test
  public void shouldGetOnlyRegularRequisitions() throws Exception {
    Rnr regularRnr = insertRequisition(processingPeriod1, program, INITIATED, false);
    insertRequisition(processingPeriod1, program, INITIATED, true);

    Rnr regularRequisition = mapper.getRegularRequisitionWithLineItems(facility, program, processingPeriod1);

    assertThat(regularRequisition.getId(), is(regularRnr.getId()));
  }

  @Test
  public void shouldPopulateLineItemsWhenGettingRnrById() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false);
    Product product = insertProduct(true, "P1");
    ProgramProduct programProduct = insertProgramProduct(product, program);
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = insertFacilityApprovedProduct(programProduct);

    RnrLineItem item1 = insertRnrLineItem(requisition, facilityTypeApprovedProduct);
    lossesAndAdjustmentsMapper.insert(item1, RnrLineItemBuilder.ONE_LOSS);
    Rnr returnedRequisition = mapper.getById(requisition.getId());

    assertThat(returnedRequisition.getFullSupplyLineItems().size(), is(1));
    final RnrLineItem item = returnedRequisition.getFullSupplyLineItems().get(0);
    assertThat(item.getLossesAndAdjustments().size(), is(1));
    assertThat(returnedRequisition.getFacility().getId(), is(requisition.getFacility().getId()));
    assertThat(returnedRequisition.getStatus(), is(requisition.getStatus()));
    assertThat(returnedRequisition.getId(), is(requisition.getId()));
  }

  @Test
  public void shouldNotGetInitiatedRequisitionsForFacilitiesAndPrograms() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());
    mapper.update(requisition);

    List<Rnr> requisitions = mapper.getAuthorizedRequisitions(null);

    assertThat(requisitions.size(), is(0));
  }

  @Test
  public void shouldGetRequisitionsInSubmittedStateForRoleAssignment() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, program, AUTHORIZED, true);
    requisition.setSupervisoryNodeId(supervisoryNode.getId());
    mapper.update(requisition);
    RoleAssignment roleAssignment = new RoleAssignment(USER_ID, 1L, program.getId(), supervisoryNode);

    List<Rnr> requisitions = mapper.getAuthorizedRequisitions(roleAssignment);

    Rnr rnr = requisitions.get(0);
    assertThat(requisitions.size(), is(1));
    assertThat(rnr.getFacility().getId(), is(facility.getId()));
    assertThat(rnr.getProgram().getId(), is(program.getId()));
    assertThat(rnr.getPeriod().getId(), is(processingPeriod1.getId()));
    assertThat(rnr.getId(), is(requisition.getId()));
    assertThat(rnr.getModifiedDate(), is(notNullValue()));
    assertTrue(rnr.isEmergency());
  }

  @Test
  public void shouldGetTheLastRegularRequisitionToEnterThePostSubmitFlow() throws Exception {
    DateTime date1 = now();
    DateTime date2 = date1.plusMonths(1);

    ProcessingPeriod processingPeriod4 = make(a(defaultProcessingPeriod,
      with(scheduleId, processingSchedule.getId()),
      with(ProcessingPeriodBuilder.name, "Period4")));
    processingPeriod4.setStartDate(new Date());

    processingPeriodMapper.insert(processingPeriod4);

    Rnr rnr1 = insertRequisition(processingPeriod1, program, AUTHORIZED, false);
    rnr1.setSubmittedDate(date1.toDate());
    mapper.update(rnr1);

    Rnr rnr2 = insertRequisition(processingPeriod4, program, APPROVED, false);
    rnr2.setSubmittedDate(date2.toDate());
    mapper.update(rnr2);

    insertRequisition(processingPeriod3, program, INITIATED, false);

    Rnr lastRequisitionToEnterThePostSubmitFlow = mapper.getLastRegularRequisitionToEnterThePostSubmitFlow(facility.getId(), program.getId());

    assertThat(lastRequisitionToEnterThePostSubmitFlow.getId(), is(rnr2.getId()));
  }

  @Test
  public void shouldNotGetEmergencyRequisitionsForPostSubmitFlow() throws Exception {
    insertRequisition(processingPeriod1, program, INITIATED, false);
    insertRequisition(processingPeriod1, program, AUTHORIZED, true);

    Rnr lastRequisition = mapper.getLastRegularRequisitionToEnterThePostSubmitFlow(facility.getId(), program.getId());

    assertThat(lastRequisition, is(nullValue()));
  }

  @Test
  public void shouldGetApprovedRequisitionsForCriteriaAndPageNumberWhenSearchingByFacilityCode() {
    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, true);
    Rnr requisition2 = insertRequisition(processingPeriod2, program, SUBMITTED, false);
    Rnr requisition3 = insertRequisition(processingPeriod3, program, SUBMITTED, false);
    approve(requisition1, requisition2, requisition3);

    String searchType = RequisitionService.SEARCH_FACILITY_CODE;
    Integer pageNumber = 1;
    Integer pageSize = 2;

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, "F10", pageNumber, pageSize);

    assertThat(requisitions.size(), is(2));
    populateProgramValuesForComparison(requisition1, 0, requisitions);
    populateProgramValuesForComparison(requisition2, 1, requisitions);
    assertThat(requisitions.get(0), is(requisition1));
    assertThat(requisitions.get(1), is(requisition2));
  }

  private void approve(Rnr... requisitions) {
    for (Rnr requisition : requisitions) {
      requisition.setStatus(APPROVED);
      mapper.update(requisition);
    }
  }

  @Test
  public void shouldGetApprovedRequisitionsForCriteriaAndPageNumberWhenSearchingByFacilityName() {
    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, false);
    Rnr requisition2 = insertRequisition(processingPeriod2, program, SUBMITTED, false);
    Rnr requisition3 = insertRequisition(processingPeriod3, program, SUBMITTED, false);

    approve(requisition1, requisition2, requisition3);

    String searchType = RequisitionService.SEARCH_FACILITY_NAME;
    Integer pageNumber = 1;
    Integer pageSize = 2;

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, "Apollo", pageNumber, pageSize);

    assertThat(requisitions.size(), is(2));
    populateProgramValuesForComparison(requisition1, 0, requisitions);
    populateProgramValuesForComparison(requisition2, 1, requisitions);
    assertThat(requisitions.get(0), is(requisition1));
    assertThat(requisitions.get(1), is(requisition2));
  }

  @Test
  public void shouldGetApprovedRequisitionsForCriteriaAndPageNumberWhenSearchingBySupplyDepotName() {
    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, false);
    Rnr requisition2 = insertRequisition(processingPeriod2, program, SUBMITTED, false);
    Rnr requisition3 = insertRequisition(processingPeriod3, program, SUBMITTED, false);

    approve(requisition1, requisition2, requisition3);

    String searchType = RequisitionService.SEARCH_SUPPLYING_DEPOT_NAME;
    Integer pageNumber = 1;
    Integer pageSize = 2;

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, "apollo", pageNumber, pageSize);

    assertThat(requisitions.size(), is(2));
    populateProgramValuesForComparison(requisition1, 0, requisitions);
    populateProgramValuesForComparison(requisition2, 1, requisitions);
    assertThat(requisitions.get(0), is(requisition1));
    assertThat(requisitions.get(1), is(requisition2));
  }

  @Test
  public void shouldGetApprovedRequisitionsForCriteriaAndPageNumberWhenSearchingByProgramName() {

    Rnr requisition1 = insertRequisition(processingPeriod1, program, SUBMITTED, false);
    Rnr requisition2 = insertRequisition(processingPeriod2, program, SUBMITTED, false);
    Rnr requisition3 = insertRequisition(processingPeriod3, program, SUBMITTED, false);

    approve(requisition1, requisition2, requisition3);

    String searchType = RequisitionService.SEARCH_PROGRAM_NAME;
    Integer pageNumber = 1;
    Integer pageSize = 2;

    List<Rnr> requisitions = mapper.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, "Yellow", pageNumber, pageSize);

    assertThat(requisitions.size(), is(2));
    populateProgramValuesForComparison(requisition1, 0, requisitions);
    populateProgramValuesForComparison(requisition2, 1, requisitions);
    assertThat(requisitions.get(0), is(requisition1));
    assertThat(requisitions.get(1), is(requisition2));
  }

  private void populateProgramValuesForComparison(Rnr requisition, int index, List<Rnr> requisitions) {
    requisition.setSubmittedDate(requisitions.get(index).getSubmittedDate());
    requisition.setProgram(requisitions.get(index).getProgram());
    requisition.setSupplyingDepot(requisitions.get(index).getSupplyingDepot());
    requisition.setSupervisoryNodeId(requisitions.get(index).getSupervisoryNodeId());
  }

  @Test
  public void shouldGetRequisitionsForViewByFacilityProgramAndPeriodIds() throws Exception {

    String commaSeparatedPeriodIds = "{" + processingPeriod1.getId() + "," + processingPeriod2.getId() + "," + processingPeriod3.getId() + "}";
    insertRequisition(processingPeriod1, program, AUTHORIZED, false);
    insertRequisition(processingPeriod2, program, APPROVED, false);
    insertRequisition(processingPeriod3, program, SUBMITTED, false);
    List<Rnr> result = mapper.getPostSubmitRequisitions(facility, program, commaSeparatedPeriodIds);
    assertThat(result.size(), is(2));
  }

  @Test
  public void shouldOnlyLoadEmergencyRequisitionDataForGivenQuery() throws Exception {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, true);

    List<Rnr> fetchedRnr = mapper.getInitiatedEmergencyRequisition(facility.getId(), program.getId());

    assertThat(fetchedRnr.get(0).getId(), is(requisition.getId()));
    assertThat(fetchedRnr.get(0).getPeriod(), is(processingPeriod1));
    assertThat(fetchedRnr.get(0).getStatus(), is(INITIATED));
    assertThat(fetchedRnr.get(0).getFullSupplyLineItems().size(), is(0));
    assertThat(fetchedRnr.get(0).getNonFullSupplyLineItems().size(), is(0));
  }

  private Rnr insertRequisition(ProcessingPeriod period, Program program, RnrStatus status, Boolean emergency) {
    Rnr rnr = new Rnr(facility.getId(), program.getId(), period.getId(), emergency, MODIFIED_BY, 1L);
    rnr.setStatus(status);
    rnr.setEmergency(emergency);
    rnr.setModifiedDate(new Date());
    rnr.setSubmittedDate(new Date(111111L));
    rnr.setProgram(program);
    rnr.setSupplyingDepot(facility);
    mapper.insert(rnr);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr));

    rnr.setSupervisoryNodeId(supervisoryNode.getId());
    mapper.update(rnr);

    return rnr;
  }

  private RnrLineItem insertRnrLineItem(Rnr rnr, FacilityTypeApprovedProduct facilityTypeApprovedProduct) {
    RnrLineItem item = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, 1L, 1L);
    lineItemMapper.insert(item);
    return item;
  }

  private FacilityTypeApprovedProduct insertFacilityApprovedProduct(ProgramProduct programProduct) {
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = make(a(FacilityApprovedProductBuilder.defaultFacilityApprovedProduct));
    facilityTypeApprovedProduct.setProgramProduct(programProduct);
    facilityApprovedProductMapper.insert(facilityTypeApprovedProduct);
    return facilityTypeApprovedProduct;
  }

  private ProgramProduct insertProgramProduct(Product product, Program program) {
    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, new Money("12.5000"));
    programProductMapper.insert(programProduct);
    return programProduct;
  }

  private void insertProgram() {
    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);
  }

  private Product insertProduct(boolean isFullSupply, String productCode) {
    Product product = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, productCode), with(ProductBuilder.fullSupply, isFullSupply)));
    productMapper.insert(product);
    return product;
  }

  private ProcessingPeriod insertPeriod(String name) {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
      with(scheduleId, processingSchedule.getId()),
      with(ProcessingPeriodBuilder.name, name)));

    processingPeriodMapper.insert(processingPeriod);

    return processingPeriod;
  }

  private SupervisoryNode insertSupervisoryNode() {
    supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);

    supervisoryNodeMapper.insert(supervisoryNode);
    return supervisoryNode;
  }

  @Test
  public void shouldGetLWRequisitionById() {
    Rnr requisition = insertRequisition(processingPeriod1, program, INITIATED, false);
    Product product = insertProduct(true, "P1");
    RnrLineItem fullSupplyLineItem = make(a(defaultRnrLineItem, with(fullSupply, true), with(productCode, product.getCode())));
    RnrLineItem nonFullSupplyLineItem = make(a(defaultRnrLineItem, with(fullSupply, false), with(productCode, product.getCode())));
    fullSupplyLineItem.setRnrId(requisition.getId());
    nonFullSupplyLineItem.setRnrId(requisition.getId());
    lineItemMapper.insert(fullSupplyLineItem);
    lineItemMapper.insert(nonFullSupplyLineItem);

    User author = new User();
    author.setId(1L);
    Comment comment = new Comment(requisition.getId(), author, "A comment", null);
    commentMapper.insert(comment);

    Rnr fetchedRequisition = mapper.getLWById(requisition.getId());

    assertThat(fetchedRequisition.getId(), is(requisition.getId()));
    assertThat(fetchedRequisition.getProgram().getId(), is(equalTo(program.getId())));
    assertThat(fetchedRequisition.getFacility().getId(), is(equalTo(facility.getId())));
    assertThat(fetchedRequisition.getPeriod().getId(), is(equalTo(processingPeriod1.getId())));
    assertThat(fetchedRequisition.getModifiedBy(), is(equalTo(MODIFIED_BY)));
    assertThat(fetchedRequisition.getStatus(), is(equalTo(INITIATED)));
    assertThat(fetchedRequisition.getFullSupplyLineItems().size(), is(0));
    assertThat(fetchedRequisition.getNonFullSupplyLineItems().size(), is(0));
    assertThat(fetchedRequisition.getRegimenLineItems().size(), is(0));
  }
}
