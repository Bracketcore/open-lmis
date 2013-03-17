package org.openlmis.web.controller;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.rnr.service.RnrTemplateService;
import org.openlmis.web.form.RnrList;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.openlmis.core.matchers.Matchers.*;
import static org.openlmis.web.controller.RequisitionController.*;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RnrDTO.class)
public class RequisitionControllerTest {
  public static final String FACILITY_CODE = "F14";
  public static final String FACILITY_NAME = "Facility";
  public static final String PROGRAM_NAME = "HIV";
  private static final String USER = "user";
  private static final Integer USER_ID = 1;

  private RequisitionService requisitionService;
  private RnrTemplateService rnrTemplateService;
  private MockHttpServletRequest request;
  private RequisitionController controller;
  private Rnr rnr;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);

    requisitionService = mock(RequisitionService.class);
    rnrTemplateService = mock(RnrTemplateService.class);
    controller = new RequisitionController(requisitionService, rnrTemplateService);
    rnr = new Rnr();
  }

  @Test
  public void shouldInitiateRnr() throws Exception {
    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1, 2, 3, request);

    verify(requisitionService).initiate(1, 2, 3, USER_ID);
    assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
  }

  @Test
  public void shouldGetRnrByFacilityProgramAndPeriodIfExists() throws Exception {
    ResponseEntity<OpenLmisResponse> response = controller.get(1, 2, 3);

    verify(requisitionService).get(argThat(facilityMatcher(1)), argThat(programMatcher(2)), argThat(periodMatcher(3)));
    assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
  }


  @Test
  public void shouldGetRequisitionById() throws Exception {
    Rnr expectedRequisition = new Rnr();
    Mockito.when(requisitionService.getFullRequisitionById(1)).thenReturn(expectedRequisition);
    ResponseEntity<OpenLmisResponse> response = controller.getById(1);

    assertThat((Rnr)response.getBody().getData().get(RequisitionController.RNR), is(expectedRequisition));
    verify(requisitionService).getFullRequisitionById(1);
  }

  @Test
  public void shouldGetRnrByIdIfExists() throws Exception {
    ResponseEntity<OpenLmisResponse> response = controller.getRnrForApprovalById(1, request);

    verify(requisitionService).getRnrForApprovalById(1, USER_ID);
    assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
  }

  @Test
  public void shouldReturnErrorResponseIfServiceThrowsException() throws Exception {
    String errorMessage = "error-message";
    doThrow(new DataException(errorMessage)).when(requisitionService).getRnrForApprovalById(1, USER_ID);
    ResponseEntity<OpenLmisResponse> response = controller.getRnrForApprovalById(1, request);
    assertThat(response.getBody().getErrorMsg(), is(equalTo(errorMessage)));
  }

  @Test
  public void shouldSaveWIPRnr() throws Exception {
    controller.saveRnr(rnr, rnr.getId(), request);

    verify(requisitionService).save(rnr);
    assertThat(rnr.getModifiedBy(), is(equalTo(USER_ID)));
  }

  @Test
  public void shouldGiveErrorIfInitiatingFails() throws Exception {
    String errorMessage = "error-message";
    doThrow(new DataException(errorMessage)).when(requisitionService).initiate(1, 2, null, USER_ID);
    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1, 2, null, request);
    assertThat(response.getBody().getErrorMsg(), is(equalTo(errorMessage)));
  }

  @Test
  public void shouldReturnNullIfGettingRequisitionFails() throws Exception {
    Rnr expectedRnr = null;
    Facility facility = new Facility(1);
    whenNew(Facility.class).withArguments(1).thenReturn(facility);
    Program program = new Program(2);
    whenNew(Program.class).withArguments(2).thenReturn(program);

    when(requisitionService.get(facility, program, null)).thenReturn(expectedRnr);
    ResponseEntity<OpenLmisResponse> response = controller.get(1, 2, null);
    assertThat((Rnr) response.getBody().getData().get(RNR), is(expectedRnr));
  }

  @Test
  public void shouldAllowSubmittingOfRnrAndTagWithModifiedBy() throws Exception {
    when(requisitionService.submit(rnr)).thenReturn(new OpenLmisMessage("test.msg.key"));
    ResponseEntity<OpenLmisResponse> response = controller.submit(rnr, rnr.getId(), request);
    assertThat(response.getBody().getSuccessMsg(), is("test.msg.key"));
    verify(requisitionService).submit(rnr);
    assertThat(rnr.getModifiedBy(), is(USER_ID));
  }

  @Test
  public void shouldReturnErrorMessageIfRnrNotValid() throws Exception {
    doThrow(new DataException(new OpenLmisMessage("some error"))).when(requisitionService).submit(rnr);

    ResponseEntity<OpenLmisResponse> response = controller.submit(rnr, rnr.getId(), request);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some error"));
  }

  @Test
  public void shouldGiveMessageAndAuthorizeRnr() throws Exception {
    String code = RequisitionService.RNR_AUTHORIZED_SUCCESSFULLY;
    String message = "R&R authorized successfully!";

    when(requisitionService.authorize(rnr)).thenReturn(new OpenLmisMessage(code));

    ResponseEntity<OpenLmisResponse> response = controller.authorize(rnr, rnr.getId(), request);

    verify(requisitionService).authorize(rnr);
    assertThat(response.getBody().getSuccessMsg(), is(message));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  public void shouldNotAuthorizeRnrAndGiveErrorMessage() throws Exception {
    String errorMessage = "some error";
    doThrow(new DataException(new OpenLmisMessage(errorMessage))).when(requisitionService).authorize(rnr);
    ResponseEntity<OpenLmisResponse> response = controller.authorize(rnr, rnr.getId(), request);

    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldGiveErrorResponseIfThereIsAnyExceptionWhileSavingRnr() throws Exception {
    String errorMessage = "some error";
    doThrow(new DataException(new OpenLmisMessage(errorMessage))).when(requisitionService).save(rnr);
    ResponseEntity<OpenLmisResponse> response = controller.saveRnr(rnr, rnr.getId(), request);

    verify(requisitionService).save(rnr);
    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldGiveSuccessResponseIfRnrSavedSuccessfully() throws Exception {
    ResponseEntity<OpenLmisResponse> response = controller.saveRnr(rnr, rnr.getId(), request);
    verify(requisitionService).save(rnr);
    assertThat(response.getBody().getSuccessMsg(), is("R&R saved successfully!"));
  }

  @Test
  public void shouldReturnListOfUserSupervisedRnrForApproval() {
    final Rnr requisition = createRequisition();
    final List<Rnr> requisitions = new ArrayList<Rnr>() {{
      add(requisition);
    }};
    when(requisitionService.listForApproval(USER_ID)).thenReturn(requisitions);
    final ResponseEntity<OpenLmisResponse> response = controller.listForApproval(request);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    final List<RnrDTO> requisitionsList = (List<RnrDTO>) response.getBody().getData().get(RNR_LIST);
    assertThat(requisitionsList.get(0).getFacilityName(), is(FACILITY_NAME));
    assertThat(requisitionsList.get(0).getFacilityCode(), is(FACILITY_CODE));
    assertThat(requisitionsList.get(0).getProgramName(), is(PROGRAM_NAME));
    verify(requisitionService).listForApproval(USER_ID);
  }

  @Test
  public void shouldApproveRequisitionAndTagWithModifiedBy() throws Exception {
    when(requisitionService.approve(rnr)).thenReturn(new OpenLmisMessage("some message"));
    final ResponseEntity<OpenLmisResponse> response = controller.approve(rnr, rnr.getId(), request);
    verify(requisitionService).approve(rnr);
    assertThat(rnr.getModifiedBy(), CoreMatchers.is(USER_ID));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("some message"));
  }

  @Test
  public void shouldGiveErrorMessageWhenServiceThrowsSomeExceptionWhileApprovingAnRnr() throws Exception {
    doThrow(new DataException("some-error")).when(requisitionService).approve(rnr);

    ResponseEntity<OpenLmisResponse> response = controller.approve(rnr, rnr.getId(), request);

    verify(requisitionService).approve(rnr);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some-error"));
  }

  @Test
  public void shouldReturnAllPeriodsForInitiatingRequisition() throws Exception {
    ProcessingPeriod processingPeriod = new ProcessingPeriod(6);
    List<ProcessingPeriod> periodList = Arrays.asList(processingPeriod);
    Rnr rnr = new Rnr();

    Facility facility = new Facility(1);
    whenNew(Facility.class).withArguments(1).thenReturn(facility);
    Program program = new Program(2);
    whenNew(Program.class).withArguments(2).thenReturn(program);
    when(requisitionService.get(facility, program, processingPeriod)).thenReturn(rnr);

    when(requisitionService.getAllPeriodsForInitiatingRequisition(1, 2)).thenReturn(periodList);

    ResponseEntity<OpenLmisResponse> response = controller.getAllPeriodsForInitiatingRequisitionWithRequisitionStatus(1, 2);

    verify(requisitionService).getAllPeriodsForInitiatingRequisition(1, 2);
    assertThat((List<ProcessingPeriod>) response.getBody().getData().get(PERIODS), is(periodList));
    assertThat((Rnr) response.getBody().getData().get(RNR), is(rnr));
  }

  @Test
  public void shouldReturnErrorResponseIfNoPeriodsFoundForInitiatingRequisition() throws Exception {
    String errorMessage = "some error";
    doThrow(new DataException(errorMessage)).when(requisitionService).getAllPeriodsForInitiatingRequisition(1, 2);

    ResponseEntity<OpenLmisResponse> response = controller.getAllPeriodsForInitiatingRequisitionWithRequisitionStatus(1, 2);

    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldReturnListOfApprovedRequisitionsForConvertingToOrder(){
    ArrayList<Rnr> expectedRequisitions = new ArrayList<>();
    mockStatic(RnrDTO.class);
    when(requisitionService.getApprovedRequisitions()).thenReturn(expectedRequisitions);
    List<RnrDTO> expectedRnrList = new ArrayList<>();
    when(RnrDTO.prepareForListApproval(expectedRequisitions)).thenReturn(expectedRnrList);
  
    ResponseEntity<OpenLmisResponse> responseEntity = controller.listForConvertToOrder();

    verify(requisitionService).getApprovedRequisitions();
    assertThat((List<RnrDTO>) responseEntity.getBody().getData().get(RNR_LIST), is(expectedRnrList));
  }

  @Test
  public void shouldGetRequisitionsForViewWithGivenFacilityIdProgramIdAndPeriodRangeAndSetUserIdInSearchCriteria() throws Exception {
    Date dateRangeStart = new Date();
    Date dateRangeEnd = new Date();
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(1, 1, dateRangeStart, dateRangeEnd);
    List<Rnr> requisitionsReturnedByService = new ArrayList<>();
    when(requisitionService.get(criteria)).thenReturn(requisitionsReturnedByService);
    mockStatic(RnrDTO.class);
    List<RnrDTO> expectedRnrList = new ArrayList<>();
    when(RnrDTO.prepareForView(requisitionsReturnedByService)).thenReturn(expectedRnrList);

    ResponseEntity<OpenLmisResponse> response = controller.getRequisitionsForView(criteria, request);

    verify(requisitionService).get(criteria);
    assertThat(criteria.getUserId(), is(USER_ID));
    List<RnrDTO> actual = (List<RnrDTO>) response.getBody().getData().get(RNR_LIST);
    assertThat(actual, is(expectedRnrList));
  }

  @Test
  public void shouldReleaseRequisitionsAsOrder() throws Exception {
    RnrList rnrList = new RnrList();
    rnrList.setRnrList(Arrays.asList(new Rnr()));

    controller.releaseAsOrder(rnrList, request);

    verify(requisitionService).releaseRequisitionsAsOrder(rnrList.getRnrList(), USER_ID);
  }

  @Test
  public void shouldReturnModelAndViewForPrintingRequisitionAsPdf(){
    int rnrId = 1;
    int programId = 2;
    Program program = new Program();
    program.setId(programId);
    rnr.setProgram(program);
    ArrayList<RnrColumn> rnrTemplate = new ArrayList<>();
    when(requisitionService.getFullRequisitionById(rnrId)).thenReturn(rnr);
    when(requisitionService.getCategoryCount(rnr, true)).thenReturn(10);
    when(requisitionService.getCategoryCount(rnr, false)).thenReturn(5);
    when(rnrTemplateService.fetchColumnsForRequisition(programId)).thenReturn(rnrTemplate);

    ModelAndView modelAndView = controller.printRequisition(rnrId);

    assertThat((Rnr) modelAndView.getModel().get(RNR), is(rnr));
    assertThat((ArrayList<RnrColumn>) modelAndView.getModel().get(RNR_TEMPLATE), is(rnrTemplate));

  }

  @Test
  public void shouldReturnAllFilledOrders() throws Exception {
    ArrayList<Rnr> orderedRequisitions = new ArrayList<>();
    mockStatic(RnrDTO.class);
    when(requisitionService.getOrders()).thenReturn(orderedRequisitions);
    List<RnrDTO> expectedRequisitionList = new ArrayList<>();
    when(RnrDTO.prepareForOrderView(orderedRequisitions)).thenReturn(expectedRequisitionList);

    ResponseEntity<OpenLmisResponse> fetchedOrders = controller.getOrders();

    verify(requisitionService).getOrders();
    assertThat((List<RnrDTO>) fetchedOrders.getBody().getData().get(ORDERS), is(expectedRequisitionList));
  }

  private Rnr createRequisition() {
    Rnr requisition = new Rnr();
    final Facility facility = new Facility();
    facility.setCode(FACILITY_CODE);
    facility.setName(FACILITY_NAME);
    final Program program = new Program();
    program.setName(PROGRAM_NAME);
    final ProcessingPeriod period = new ProcessingPeriod();
    period.setStartDate(new Date());
    period.setEndDate(new Date(1111232323L));
    requisition.setFacility(facility);
    requisition.setProgram(program);
    requisition.setPeriod(period);
    return requisition;
  }
}

