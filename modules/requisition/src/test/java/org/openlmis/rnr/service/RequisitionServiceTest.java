package org.openlmis.rnr.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.*;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.*;
import static org.openlmis.core.builder.ProductBuilder.code;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.openlmis.rnr.builder.RequisitionBuilder.status;
import static org.openlmis.rnr.builder.RnrColumnBuilder.*;
import static org.openlmis.rnr.domain.RnrStatus.*;
import static org.openlmis.rnr.service.RequisitionService.*;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequisitionService.class)
public class RequisitionServiceTest {

  private static final Integer HIV = 1;
  private static final Facility FACILITY = new Facility(1);
  private static final Program PROGRAM = new Program(2);
  private static final ProcessingPeriod PERIOD = make(a(defaultProcessingPeriod, with(id, 10), with(numberOfMonths, 1)));
  private static final Integer USER_ID = 1;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  @Autowired
  private RequisitionService requisitionService;
  @Mock
  private FacilityApprovedProductService facilityApprovedProductService;
  @Mock
  private RequisitionRepository requisitionRepository;
  @Mock
  private RnrTemplateRepository rnrTemplateRepository;
  @Mock
  private SupervisoryNodeService supervisoryNodeService;
  @Mock
  private RoleRightsService roleRightService;
  @Mock
  private ProgramService programService;
  @Mock
  private ProcessingScheduleService processingScheduleService;
  @Mock
  private FacilityService facilityService;

  private Rnr submittedRnr;
  private Rnr initiatedRnr;
  private Rnr authorizedRnr;
  ArrayList<RnrColumn> rnrColumns;


  @Before
  public void setup() {
    requisitionService = new RequisitionService(requisitionRepository, rnrTemplateRepository, facilityApprovedProductService,
      supervisoryNodeService, roleRightService, programService, processingScheduleService, facilityService);
    submittedRnr = make(a(RequisitionBuilder.defaultRnr, with(status, SUBMITTED)));
    initiatedRnr = make(a(RequisitionBuilder.defaultRnr, with(status, INITIATED)));
    authorizedRnr = make(a(RequisitionBuilder.defaultRnr, with(status, AUTHORIZED)));
    rnrColumns = new ArrayList<RnrColumn>() {{
      add(new RnrColumn());
    }};
  }

  @Test
  public void shouldInitRequisition() throws Exception {
    Date date = new Date();
    Rnr requisition = createRequisition(PERIOD.getId(), null);
    setupForInitRnr(date, requisition, PERIOD);
    Rnr returnedRnr = new Rnr(1, 2, 3, 4);

    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityApprovedProducts);

    RequisitionService spyRequisitionService = spy(requisitionService);

    doReturn(returnedRnr).when(spyRequisitionService).get(new Facility(FACILITY.getId()), new Program(PROGRAM.getId()), new ProcessingPeriod(PERIOD.getId()));
    Rnr rnr = spyRequisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), 1);

    verify(facilityApprovedProductService).getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId());
    verify(requisitionRepository).insert(any(Rnr.class));

    assertThat(rnr, is(returnedRnr));
  }

  @Test
  public void shouldGetRequisition() throws Exception {
    Rnr expectedRequisition = new Rnr();
    when(requisitionRepository.getRequisition(FACILITY, PROGRAM, PERIOD)).thenReturn(expectedRequisition);

    Facility facility = new Facility();
    facility.setId(FACILITY.getId());
    facility.setName("test Facility"); facility.setOperatedBy(new FacilityOperator()); facility.setFacilityType(new FacilityType());
    facility.setCode("test code"); facility.setGeographicZone(new GeographicZone());

    ProcessingPeriod period = new ProcessingPeriod();
    Date startDate = new Date();
    Date endDate = new Date(123456789L);
    period.setId(PERIOD.getId());
    period.setStartDate(startDate);
    period.setEndDate(endDate);
    period.setNumberOfMonths(3);

    Program program = new Program();
    program.setId(PROGRAM.getId());
    program.setName("test name");


    when(programService.getById(PROGRAM.getId())).thenReturn(program);
    when(facilityService.getById(FACILITY.getId())).thenReturn(facility);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(period);

    expectedRequisition.setFacility(facility); expectedRequisition.setPeriod(period); expectedRequisition.setProgram(program);

    Rnr actualRequisition = requisitionService.get(FACILITY, PROGRAM, PERIOD);
    assertThat(actualRequisition, is(expectedRequisition));
  }

  @Test
  public void shouldGetPreviousTwoRequisitionsNormalizedConsumptionsWhileGettingRequisition() throws Exception {

    final Integer lastPeriodId = 2;
    final int secondLastPeriodsId = 3;
    ProcessingPeriod lastPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(id, lastPeriodId)));

    Rnr rnr = new Rnr(FACILITY, PROGRAM, PERIOD);
    final Rnr spyRnr = Mockito.spy(rnr);

    when(requisitionRepository.getRequisition(FACILITY, PROGRAM, PERIOD)).thenReturn(spyRnr);
    ProcessingPeriod period = new ProcessingPeriod(PERIOD.getId(), PERIOD.getStartDate(), PERIOD.getEndDate(), PERIOD.getNumberOfMonths());
    when(processingScheduleService.getPeriodById(10)).thenReturn(period);

    when(processingScheduleService.getImmediatePreviousPeriod(period)).thenReturn(lastPeriod);

    ProcessingPeriod secondLastPeriod = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(id, secondLastPeriodsId)));
    when(processingScheduleService.getImmediatePreviousPeriod(lastPeriod)).thenReturn(secondLastPeriod);

    Rnr lastPeriodsRrn = new Rnr(FACILITY, PROGRAM, lastPeriod);
    when(requisitionRepository.getRequisition(FACILITY, PROGRAM, lastPeriod)).thenReturn(lastPeriodsRrn);

    Rnr secondLastPeriodsRrn = new Rnr(FACILITY, PROGRAM, secondLastPeriod);
    when(requisitionRepository.getRequisition(FACILITY, PROGRAM, secondLastPeriod)).thenReturn(secondLastPeriodsRrn);

    when(programService.getById(PROGRAM.getId())).thenReturn(PROGRAM);
    when(facilityService.getById(FACILITY.getId())).thenReturn(FACILITY);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);

    final Rnr actual = requisitionService.get(FACILITY, PROGRAM, PERIOD);
    assertThat(actual, is(spyRnr));
    verify(spyRnr).fillLastTwoPeriodsNormalizedConsumptions(lastPeriodsRrn, secondLastPeriodsRrn);
  }

  @Test
  public void shouldGetAllPeriodsForInitiatingRequisitionWhenThereIsAtLeastOneExistingRequisitionInThePostSubmitFlow() throws Exception {
    DateTime date1 = new DateTime();
    DateTime date2 = date1.minusMonths(1);
    DateTime date3 = date1.minusMonths(2);
    DateTime date4 = date1.minusMonths(3);

    ProcessingPeriod processingPeriod1 = createProcessingPeriod(10, date1);
    ProcessingPeriod processingPeriod2 = createProcessingPeriod(20, date2);
    ProcessingPeriod processingPeriod3 = createProcessingPeriod(30, date3);
    ProcessingPeriod processingPeriod4 = createProcessingPeriod(40, date4);

    createRequisition(processingPeriod1.getId(), AUTHORIZED);
    Rnr rnr2 = createRequisition(processingPeriod2.getId(), APPROVED);
    createRequisition(processingPeriod3.getId(), INITIATED);

    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(date1.toDate());
    when(requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(FACILITY.getId(), PROGRAM.getId())).thenReturn(rnr2);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date1.toDate(), processingPeriod2.getId())).
      thenReturn(Arrays.asList(processingPeriod3, processingPeriod4));

    List<ProcessingPeriod> periods = requisitionService.getAllPeriodsForInitiatingRequisition(FACILITY.getId(), PROGRAM.getId());

    assertThat(periods.size(), is(2));
    assertThat(periods.get(0), is(processingPeriod3));
    assertThat(periods.get(1), is(processingPeriod4));
  }

  @Test
  public void shouldGetAllPeriodsForInitiatingRequisitionWhenThereIsNoRequisitionInThePostSubmitFlow() throws Exception {
    DateTime date1 = new DateTime();
    DateTime date2 = date1.minusMonths(1);

    ProcessingPeriod processingPeriod1 = createProcessingPeriod(10, date1);
    ProcessingPeriod processingPeriod2 = createProcessingPeriod(20, date2);

    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(date1.toDate());
    when(requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(FACILITY.getId(), PROGRAM.getId())).thenReturn(null);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date1.toDate(), null)).
      thenReturn(Arrays.asList(processingPeriod1, processingPeriod2));

    List<ProcessingPeriod> periods = requisitionService.getAllPeriodsForInitiatingRequisition(FACILITY.getId(), PROGRAM.getId());

    assertThat(periods.size(), is(2));
    assertThat(periods.get(0), is(processingPeriod1));
    assertThat(periods.get(1), is(processingPeriod2));
  }

  private Rnr createRequisition(int periodId, RnrStatus status) {
    return make(a(RequisitionBuilder.defaultRnr,
      with(RequisitionBuilder.periodId, periodId),
      with(RequisitionBuilder.status, status)));
  }

  private ProcessingPeriod createProcessingPeriod(int id, DateTime startDate) {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
      with(ProcessingPeriodBuilder.startDate, startDate.toDate())));
    processingPeriod.setId(id);
    return processingPeriod;
  }

  @Test
  public void shouldNotInitRequisitionIfTemplateNotDefined() {
    when(rnrTemplateRepository.fetchRnrTemplateColumns(PROGRAM.getId())).thenReturn(new ArrayList<RnrColumn>());
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_TEMPLATE_NOT_INITIATED_ERROR);
    Rnr rnr = requisitionService.initiate(FACILITY.getId(), HIV, null, 1);
    verify(facilityApprovedProductService, never()).getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), HIV);
    verify(requisitionRepository, never()).insert(rnr);
  }

  @Test
  public void shouldNotInitRequisitionIfPeriodDoesNotAllowInitiation() throws Exception {
    Date date = new Date();
    Rnr requisition = createRequisition(PERIOD.getId(), null);
    ProcessingPeriod validPeriod = new ProcessingPeriod(1);
    setupForInitRnr(date, requisition, validPeriod);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_PREVIOUS_NOT_FILLED_ERROR);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), USER_ID);

    verify(programService).getProgramStartDate(FACILITY.getId(), PROGRAM.getId());
    verify(requisitionRepository).getLastRequisitionToEnterThePostSubmitFlow(FACILITY.getId(), PROGRAM.getId());
    verify(processingScheduleService).getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date, validPeriod.getId());
  }

  private void setupForInitRnr(Date date, Rnr requisition, ProcessingPeriod validPeriod) {
    when(rnrTemplateRepository.fetchRnrTemplateColumns(PROGRAM.getId())).thenReturn(Arrays.asList(make(a(defaultRnrColumn))));
    when(programService.getProgramStartDate(FACILITY.getId(), PROGRAM.getId())).thenReturn(date);
    when(requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(FACILITY.getId(), PROGRAM.getId())).thenReturn(requisition);
    when(processingScheduleService.getAllPeriodsAfterDateAndPeriod(FACILITY.getId(), PROGRAM.getId(), date, PERIOD.getId())).
      thenReturn(Arrays.asList(validPeriod));
  }

  @Test
  public void shouldReturnMessageWhileSubmittingRnrIfSupervisingNodeNotPresent() {
    Rnr rnr = Mockito.spy(make(a(defaultRnr)));

    when(requisitionRepository.getById(rnr.getId())).thenReturn(initiatedRnr);
    when(rnrTemplateRepository.fetchRnrTemplateColumns(rnr.getProgram().getId())).thenReturn(rnrColumns);
    doReturn(true).when(rnr).validate(rnrColumns);
    when(supervisoryNodeService.getFor(rnr.getFacility(), rnr.getProgram())).thenReturn(null);

    OpenLmisMessage message = requisitionService.submit(rnr);
    verify(requisitionRepository).update(rnr);
    assertThat(rnr.getStatus(), is(SUBMITTED));
    assertThat(message.getCode(), is("rnr.submitted.without.supervisor"));
  }

  @Test
  public void shouldSubmitValidRnrWithSubmittedDateAndSetMessage() {
    Rnr rnr = Mockito.spy(make(a(defaultRnr)));
    when(requisitionRepository.getById(rnr.getId())).thenReturn(initiatedRnr);
    doReturn(true).when(rnr).validate(rnrColumns);
    when(supervisoryNodeService.getFor(rnr.getFacility(), rnr.getProgram())).thenReturn(new SupervisoryNode());
    when(rnrTemplateRepository.fetchRnrTemplateColumns(rnr.getProgram().getId())).thenReturn(rnrColumns);

    OpenLmisMessage message = requisitionService.submit(rnr);

    verify(requisitionRepository).update(rnr);
    verify(rnr).validate(rnrColumns);
    assertThat(rnr.getSubmittedDate(), is(notNullValue()));
    assertThat(rnr.getStatus(), is(SUBMITTED));
    assertThat(message.getCode(), is("rnr.submitted.success"));
  }

  @Test
  public void shouldAuthorizeAValidRnrAndTagWithSupervisoryNode() throws Exception {
    Rnr rnr = Mockito.spy(make(a(defaultRnr)));
    when(requisitionRepository.getById(rnr.getId())).thenReturn(submittedRnr);
    doReturn(true).when(rnr).validate(rnrColumns);
    when(rnrTemplateRepository.fetchRnrTemplateColumns(rnr.getProgram().getId())).thenReturn(rnrColumns);
    when(supervisoryNodeService.getApproverFor(rnr.getFacility(), rnr.getProgram())).thenReturn(new User());
    SupervisoryNode approverNode = new SupervisoryNode();
    when(supervisoryNodeService.getFor(rnr.getFacility(), rnr.getProgram())).thenReturn(approverNode);

    OpenLmisMessage authorize = requisitionService.authorize(rnr);

    verify(rnr).validate(rnrColumns);
    verify(rnrTemplateRepository).fetchRnrTemplateColumns(rnr.getProgram().getId());
    verify(requisitionRepository).update(rnr);
    assertThat(rnr.getStatus(), is(AUTHORIZED));
    assertThat(rnr.getSupervisoryNodeId(), is(approverNode.getId()));
    assertThat(authorize.getCode(), is(RNR_AUTHORIZED_SUCCESSFULLY));

  }

  @Test
  public void shouldNotOverwriteSubmittedDateWhenAuthorizing() {
    Date submittedDate = new Date(1465555522222L);
    ArrayList<RnrColumn> columns = new ArrayList<>();
    Rnr rnrForAuthorizing = Mockito.spy(make(a(defaultRnr)));

    submittedRnr.setSubmittedDate(submittedDate);
    doReturn(true).when(rnrForAuthorizing).validate(columns);
    when(requisitionRepository.getById(rnrForAuthorizing.getId())).thenReturn(submittedRnr);
    SupervisoryNode node = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    when(supervisoryNodeService.getFor(rnrForAuthorizing.getFacility(), rnrForAuthorizing.getProgram())).thenReturn(node);
    when(rnrTemplateRepository.fetchRnrTemplateColumns(rnrForAuthorizing.getProgram().getId())).thenReturn(columns);

    requisitionService.authorize(rnrForAuthorizing);

    verify(rnrTemplateRepository).fetchRnrTemplateColumns(rnrForAuthorizing.getProgram().getId());
    verify(requisitionRepository).update(rnrForAuthorizing);
    assertThat(rnrForAuthorizing.getSubmittedDate(), is(submittedDate));
  }

  @Test
  public void shouldAuthorizeAValidRnrAndAdviseUserIfRnrDoesNotHaveApprover() throws Exception {
    Rnr rnr = Mockito.spy(make(a(defaultRnr)));
    when(requisitionRepository.getById(rnr.getId())).thenReturn(submittedRnr);
    when(rnrTemplateRepository.fetchRnrTemplateColumns(rnr.getProgram().getId())).thenReturn(rnrColumns);
    when(supervisoryNodeService.getApproverFor(rnr.getFacility(), rnr.getProgram())).thenReturn(null);
    SupervisoryNode node = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    when(supervisoryNodeService.getFor(rnr.getFacility(), rnr.getProgram())).thenReturn(node);
    doReturn(true).when(rnr).validate(rnrColumns);

    OpenLmisMessage openLmisMessage = requisitionService.authorize(rnr);

    verify(rnrTemplateRepository).fetchRnrTemplateColumns(rnr.getProgram().getId());
    verify(rnr).validate(rnrColumns);
    verify(requisitionRepository).update(rnr);
    assertThat(rnr.getStatus(), is(AUTHORIZED));
    assertThat(openLmisMessage.getCode(), is(RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR));
  }

  @Test
  public void shouldNotAuthorizeInvalidRnr() throws Exception {
    Rnr rnr = Mockito.spy(make(a(defaultRnr)));
    when(requisitionRepository.getById(rnr.getId())).thenReturn(submittedRnr);
    when(rnrTemplateRepository.fetchRnrTemplateColumns(rnr.getProgram().getId())).thenReturn(rnrColumns);
    doThrow(new DataException("error-message")).when(rnr).validate(rnrColumns);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error-message");

    requisitionService.authorize(rnr);
  }

  @Test
  public void shouldNotAuthorizeRnrIfNotSubmitted() throws Exception {
    Rnr rnr = Mockito.spy(make(a(defaultRnr)));
    when(requisitionRepository.getById(rnr.getId())).thenReturn(initiatedRnr);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_AUTHORIZATION_ERROR);

    requisitionService.authorize(rnr);
  }

  @Test
  public void shouldSaveRnrIfStatusIsSubmittedAndUserHasAuthorizeRight() {
    Rnr rnr = Mockito.spy(make(a(defaultRnr)));
    Integer userId = 1;
    rnr.setModifiedBy(userId);
    rnr.setStatus(SUBMITTED);
    List<Right> listUserRights = Arrays.asList(AUTHORIZE_REQUISITION);
    when(roleRightService.getRights(userId)).thenReturn(listUserRights);
    when(requisitionRepository.getRequisition(rnr.getFacility(), rnr.getProgram(), rnr.getPeriod())).thenReturn(make(a(defaultRnr)));

    requisitionService.save(rnr);

    verify(requisitionRepository).update(rnr);
  }

  @Test
  public void shouldSaveRnrIfStatusIsAuthorizedAndUserHasApproveRight() {
    Rnr rnr = Mockito.spy(make(a(defaultRnr)));
    Integer userId = 1;
    rnr.setModifiedBy(userId);
    rnr.setStatus(AUTHORIZED);
    List<Right> listUserRights = Arrays.asList(APPROVE_REQUISITION);
    when(roleRightService.getRights(userId)).thenReturn(listUserRights);
    when(requisitionRepository.getRequisition(rnr.getFacility(), rnr.getProgram(), rnr.getPeriod())).thenReturn(make(a(defaultRnr)));

    requisitionService.save(rnr);

    verify(requisitionRepository).update(rnr);
  }

  @Test
  public void shouldSaveRnrIfStatusIsInitiatedAndUserHasCreateRight() {
    Rnr rnr = Mockito.spy(make(a(defaultRnr)));
    Integer userId = 1;
    rnr.setModifiedBy(userId);
    rnr.setStatus(INITIATED);
    List<Right> listUserRights = Arrays.asList(CREATE_REQUISITION);
    when(roleRightService.getRights(userId)).thenReturn(listUserRights);
    when(requisitionRepository.getRequisition(rnr.getFacility(), rnr.getProgram(), rnr.getPeriod())).thenReturn(make(a(defaultRnr)));

    requisitionService.save(rnr);

    verify(requisitionRepository).update(rnr);
  }

  @Test
  public void shouldNotSaveRnrWithStatusInitiatedIfUserHasOnlyAuthorizeRight() {
    Rnr rnr = Mockito.spy(make(a(defaultRnr)));
    Integer userId = 1;
    rnr.setModifiedBy(userId);
    rnr.setStatus(INITIATED);
    List<Right> listUserRights = Arrays.asList(AUTHORIZE_REQUISITION);
    when(roleRightService.getRights(userId)).thenReturn(listUserRights);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);

    requisitionService.save(rnr);
  }

  @Test
  public void shouldNotSaveAlreadySubmittedRnrIfUserHasOnlyCreateRequisitionRight() {
    Rnr rnr = Mockito.spy(make(a(defaultRnr)));
    Integer userId = 1;
    rnr.setModifiedBy(userId);
    rnr.setStatus(SUBMITTED);
    List<Right> listUserRights = Arrays.asList(CREATE_REQUISITION);
    when(roleRightService.getRights(userId)).thenReturn(listUserRights);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);

    requisitionService.save(rnr);
  }

  @Test
  public void shouldFetchAllRequisitionsForFacilitiesAndProgramSupervisedByUserForApproval() throws Exception {
    final RoleAssignment firstAssignment = new RoleAssignment(1, 1, 1, new SupervisoryNode());
    final RoleAssignment secondAssignment = new RoleAssignment(2, 2, 2, new SupervisoryNode());
    final Rnr requisition = make(a(RequisitionBuilder.defaultRnr));
    final List<Rnr> requisitionsForFirstAssignment = new ArrayList<Rnr>() {{
      add(requisition);
    }};
    final List<Rnr> requisitionsForSecondAssignment = new ArrayList<>();
    List<RoleAssignment> roleAssignments = new ArrayList<RoleAssignment>() {{
      add(firstAssignment);
      add(secondAssignment);
    }};
    when(roleRightService.getRoleAssignments(APPROVE_REQUISITION, USER_ID)).thenReturn(roleAssignments);
    when(requisitionRepository.getAuthorizedRequisitions(firstAssignment)).thenReturn(requisitionsForFirstAssignment);
    when(requisitionRepository.getAuthorizedRequisitions(secondAssignment)).thenReturn(requisitionsForSecondAssignment);
    Program expectedProgram = new Program();
    Facility expectedFacility = new Facility();
    ProcessingPeriod expectedPeriod = new ProcessingPeriod();
    when(programService.getById(3)).thenReturn(expectedProgram);
    when(facilityService.getById(3)).thenReturn(expectedFacility);
    when(processingScheduleService.getPeriodById(3)).thenReturn(expectedPeriod);

    List<Rnr> requisitions = requisitionService.listForApproval(USER_ID);

    List<Rnr> expectedRequisitions = new ArrayList<Rnr>() {{
      addAll(requisitionsForFirstAssignment);
      addAll(requisitionsForSecondAssignment);
    }};

    assertThat(requisitions, is(expectedRequisitions));
    assertThat(requisition.getProgram(), is(expectedProgram));
    assertThat(requisition.getFacility(), is(expectedFacility));
    assertThat(requisition.getPeriod(), is(expectedPeriod));
    verify(requisitionRepository, times(1)).getAuthorizedRequisitions(firstAssignment);
    verify(requisitionRepository, times(1)).getAuthorizedRequisitions(secondAssignment);
  }

  @Test
  public void shouldNotApproveAnRnrIfStatusIsNotAuthorized() throws Exception {
    when(requisitionRepository.getById(submittedRnr.getId())).thenReturn(submittedRnr);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);

    requisitionService.approve(submittedRnr);
  }

  @Test
  public void shouldApproveAnRnrAndChangeStatusToApprovedIfThereIsNoFurtherApprovalNeeded() throws Exception {
    authorizedRnr.setSupervisoryNodeId(1);
    when(requisitionRepository.getById(authorizedRnr.getId())).thenReturn(authorizedRnr);

    OpenLmisMessage message = requisitionService.approve(authorizedRnr);
    verify(requisitionRepository).update(authorizedRnr);
    assertThat(authorizedRnr.getStatus(), is(APPROVED));
    assertThat(authorizedRnr.getSupervisoryNodeId(), is(nullValue()));
    assertThat(message.getCode(), is(RNR_APPROVED_SUCCESSFULLY));
  }

  @Test
  public void shouldApproveAnRnrAndKeepStatusInApprovalIfFurtherApprovalNeeded() throws Exception {
    authorizedRnr.setSupervisoryNodeId(1);
    when(requisitionRepository.getById(authorizedRnr.getId())).thenReturn(authorizedRnr);

    SupervisoryNode parentNode = new SupervisoryNode() {{
      setId(2);
    }};
    when(supervisoryNodeService.getParent(1)).thenReturn(parentNode);
    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parentNode, authorizedRnr.getProgram())).thenReturn(new User());

    OpenLmisMessage message = requisitionService.approve(authorizedRnr);

    verify(requisitionRepository).update(authorizedRnr);
    assertThat(authorizedRnr.getStatus(), is(IN_APPROVAL));
    assertThat(authorizedRnr.getSupervisoryNodeId(), is(2));
    assertThat(message.getCode(), is(RNR_APPROVED_SUCCESSFULLY));
  }

  @Test
  public void shouldApproveAnRnrAndKeepStatusInApprovalIfFurtherApprovalNeededAndShouldGiveMessageIfThereIsNoSupervisorAssigned() throws Exception {
    authorizedRnr.setSupervisoryNodeId(1);
    when(requisitionRepository.getById(authorizedRnr.getId())).thenReturn(authorizedRnr);

    SupervisoryNode parentNode = new SupervisoryNode() {{
      setId(2);
    }};
    when(supervisoryNodeService.getParent(1)).thenReturn(parentNode);

    when(supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parentNode, authorizedRnr.getProgram())).thenReturn(null);
    OpenLmisMessage message = requisitionService.approve(authorizedRnr);

    verify(requisitionRepository).update(authorizedRnr);
    assertThat(authorizedRnr.getStatus(), is(IN_APPROVAL));
    assertThat(authorizedRnr.getSupervisoryNodeId(), is(2));
    assertThat(message.getCode(), is(RNR_APPROVED_SUCCESSFULLY_WITHOUT_SUPERVISOR));
  }

  @Test
  public void shouldGetRequisitionForApprovalById() throws Exception {
    Rnr expected = new Rnr();
    final int supervisoryNodeId = 1;
    expected.setSupervisoryNodeId(supervisoryNodeId);
    final int rnrId = 1;
    when(requisitionRepository.getById(rnrId)).thenReturn(expected);

    final RoleAssignment assignment = roleAssignmentWithSupervisoryNodeId(supervisoryNodeId);
    List<RoleAssignment> roleAssignments = new ArrayList<RoleAssignment>() {{
      add(assignment);
    }};

    final int userId = 1;
    when(roleRightService.getRoleAssignments(APPROVE_REQUISITION, userId)).thenReturn(roleAssignments);
    Rnr actual = requisitionService.getRnrForApprovalById(rnrId, userId);

    assertThat(actual, is(expected));
  }

  @Test
  public void shouldThrowExceptionIfUserDoesNotHaveAccessToRequestedRequisition() throws Exception {
    Rnr expected = new Rnr();
    final int supervisoryNodeId = 1;
    expected.setSupervisoryNodeId(supervisoryNodeId);
    final int rnrId = 1;
    when(requisitionRepository.getById(rnrId)).thenReturn(expected);

    List<RoleAssignment> roleAssignments = new ArrayList<RoleAssignment>() {{
    }};

    final int userId = 1;
    when(roleRightService.getRoleAssignments(APPROVE_REQUISITION, userId)).thenReturn(roleAssignments);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);
    requisitionService.getRnrForApprovalById(rnrId, userId);

  }

  private RoleAssignment roleAssignmentWithSupervisoryNodeId(int supervisoryNodeId) {
    final RoleAssignment assignment = new RoleAssignment();
    final SupervisoryNode node = new SupervisoryNode();
    node.setId(supervisoryNodeId);
    assignment.setSupervisoryNode(node);
    return assignment;
  }

  @Test
  public void shouldFillBeginningBalanceOfLineItemsFromPreviousRequisitionIfAvailableDuringInitialize() throws Exception {
    Date date = new Date();
    ProcessingPeriod period = new ProcessingPeriod(10);
    Rnr someRequisition = createRequisition(period.getId(), null);
    Rnr previousRnr = make(a(defaultRnr));
    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod, with(id, period.getId() - 1)));
    setupForInitRnr(date, someRequisition, period);

    Rnr spyRequisition = spy(someRequisition);

    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    ProgramProduct programProduct2 = new ProgramProduct(null, make(a(defaultProduct, with(code, "testCode"))), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct2, 30));

    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityApprovedProducts);
    when(processingScheduleService.getImmediatePreviousPeriod(spyRequisition.getPeriod())).thenReturn(previousPeriod);
    when(requisitionRepository.getRequisition(spyRequisition.getFacility(), spyRequisition.getProgram(), previousPeriod)).thenReturn(previousRnr);

    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), period.getId(), facilityApprovedProducts, USER_ID).thenReturn(spyRequisition);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), period.getId(), USER_ID);

    verify(spyRequisition).setBeginningBalanceForEachLineItem(previousRnr);
  }

  @Test
  public void shouldNotFillBeginningBalanceIfPreviousRnrNotDefineDuringInitiate() throws Exception {
    Date date = new Date();
    Rnr someRequisition = createRequisition(PERIOD.getId(), null);
    setupForInitRnr(date, someRequisition, PERIOD);

    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityApprovedProducts);

    Rnr spyRequisition = Mockito.spy(someRequisition);
    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), facilityApprovedProducts, USER_ID).thenReturn(spyRequisition);

    int previousPeriodId = PERIOD.getId() - 1;
    ProcessingPeriod previousPeriod = make(a(defaultProcessingPeriod, with(id, previousPeriodId)));
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);
    when(processingScheduleService.getImmediatePreviousPeriod(PERIOD)).thenReturn(previousPeriod);
    when(requisitionRepository.getRequisition(FACILITY, PROGRAM, previousPeriod)).thenReturn(null);
    when(processingScheduleService.getPeriodById(PERIOD.getId())).thenReturn(PERIOD);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), USER_ID);

    verify(spyRequisition).setBeginningBalanceForEachLineItem(null);
  }

  @Test
  public void shouldFillNullInBeginningBalanceIfStockInHandIsNotDisplayed() throws Exception {
    Date date = new Date();
    Rnr someRequisition = createRequisition(PERIOD.getId(), null);
    setupForInitRnr(date, someRequisition, PERIOD);
    someRequisition.setFacility(FACILITY);
    someRequisition.setProgram(PROGRAM);

    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(FACILITY.getId(), PROGRAM.getId())).thenReturn(facilityApprovedProducts);
    when(rnrTemplateRepository.fetchRnrTemplateColumns(PROGRAM.getId())).thenReturn(Arrays.asList(make(a(defaultRnrColumn, with(columnName, "stockInHand"), with(visible, false)))));

    Rnr spyRequisition = spy(someRequisition);
    whenNew(Rnr.class).withArguments(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), facilityApprovedProducts, USER_ID).thenReturn(spyRequisition);

    requisitionService.initiate(FACILITY.getId(), PROGRAM.getId(), PERIOD.getId(), USER_ID);

    verify(spyRequisition, never()).setBeginningBalanceForEachLineItem(null);
  }

  @Test
  public void shouldNotOverwriteBeginningBalanceIfPreviousStockInHandAvailableFlagIsSet() throws Exception {
    Rnr savedRequisition = make(a(defaultRnr));
    Rnr requisition = createRequisition(PERIOD.getId(), SUBMITTED);
    requisition.setModifiedBy(USER_ID);
    requisition.getLineItems().get(0).setBeginningBalance(3);
    savedRequisition.getLineItems().get(0).setPreviousStockInHandAvailable(true);
    List<Right> listUserRights = Arrays.asList(AUTHORIZE_REQUISITION);
    when(roleRightService.getRights(USER_ID)).thenReturn(listUserRights);
    when(requisitionRepository.getRequisition(requisition.getFacility(), requisition.getProgram(), requisition.getPeriod()))
      .thenReturn(savedRequisition);

    requisitionService.save(requisition);

    verify(requisitionRepository).getRequisition(requisition.getFacility(), requisition.getProgram(), requisition.getPeriod());
    verify(requisitionRepository).update(requisition);
    assertThat(requisition.getLineItems().get(0).getBeginningBalance(), is(RnrLineItemBuilder.STOCK_IN_HAND));
  }
}
