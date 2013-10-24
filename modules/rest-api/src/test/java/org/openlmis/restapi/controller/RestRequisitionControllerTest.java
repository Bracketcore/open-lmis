/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.Report;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestRequisitionService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrDTO;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.controller.RestRequisitionController.RNR;
import static org.openlmis.restapi.controller.RestRequisitionController.UNEXPECTED_EXCEPTION;
import static org.openlmis.restapi.response.RestResponse.ERROR;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({RestResponse.class, RnrDTO.class})
public class RestRequisitionControllerTest {

  @Mock
  RestRequisitionService service;

  @InjectMocks
  RestRequisitionController controller;

  @Mock
  MessageService messageService;

  Principal principal;

  @Before
  public void setUp() throws Exception {
    principal = mock(Principal.class);
    when(principal.getName()).thenReturn("vendor name");
    mockStatic(RestResponse.class);
  }

  @Test
  public void shouldSubmitRequisitionForACommTrackUser() throws Exception {
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1L);
    when(service.submitReport(report)).thenReturn(requisition);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(RNR, requisition.getId()), OK);
    when(RestResponse.response(RNR, requisition.getId(), HttpStatus.CREATED)).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.submitRequisition(report);

    assertThat((Long) response.getBody().getData().get(RNR), is(1L));
  }

  @Test
  public void shouldGiveErrorMessageIfReportInvalid() throws Exception {
    String errorMessage = "some error";
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1L);
    DataException dataException = new DataException(errorMessage);
    doThrow(dataException).when(service).submitReport(report);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(ERROR, errorMessage), HttpStatus.BAD_REQUEST);
    when(RestResponse.error(dataException.getOpenLmisMessage(), HttpStatus.BAD_REQUEST)).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.submitRequisition(report);

    assertThat((String) response.getBody().getData().get(ERROR), is(errorMessage));
  }

  @Test
  public void shouldApproveReport() throws Exception {
    Report report = new Report();
    Long id = 1L;
    Rnr expectedRnr = new Rnr();
    when(service.approve(report)).thenReturn(expectedRnr);

    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(RNR, expectedRnr.getId()), OK);
    when(RestResponse.response(RNR, expectedRnr.getId())).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.approve(id, report);

    assertThat((Long) response.getBody().getData().get(RNR), is(expectedRnr.getId()));
    assertThat(report.getRequisitionId(), is(1L));
    verify(service).approve(report);
  }

  @Test
  public void shouldGiveErrorMessageIfSomeErrorOccursWhileApproving() throws Exception {
    String errorMessage = "some error";
    Long requisitionId = 1L;
    Report report = new Report();

    DataException dataException = new DataException(errorMessage);
    doThrow(dataException).when(service).approve(report);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(ERROR, errorMessage), HttpStatus.BAD_REQUEST);
    when(RestResponse.error(dataException.getOpenLmisMessage(), HttpStatus.BAD_REQUEST)).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.approve(requisitionId, report);

    assertThat((String) response.getBody().getData().get(ERROR), is(errorMessage));
  }

  @Test
  public void shouldResolveUnhandledException() throws Exception {
    String errorMessage = "Oops, something has gone wrong. Please try again later";
    when(messageService.message(UNEXPECTED_EXCEPTION)).thenReturn(errorMessage);

    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(ERROR, errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);

    when(RestResponse.error(UNEXPECTED_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR)).thenReturn(expectedResponse);

    final ResponseEntity<RestResponse> response = controller.handleException(new Exception());

    final RestResponse body = response.getBody();
    assertThat((String) body.getData().get(ERROR), is(errorMessage));
  }

  @Test
  public void shouldGetRequisitionById() throws Exception {
    mockStatic(RnrDTO.class);
    Long rnrId = 3L;
    Rnr rnr = new Rnr(rnrId);
    RnrDTO rnrDTO = new RnrDTO();
    when(RnrDTO.prepareForREST(rnr)).thenReturn(rnrDTO);
    when(service.getRequisition(rnrId)).thenReturn(rnr);
    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse("rnr", rnr), OK);
    when(RestResponse.response("rnr", rnrDTO)).thenReturn(expectedResponse);

    ResponseEntity<RestResponse> response = controller.getRequisition(rnrId);

    assertThat(response, is(expectedResponse));
    verify(service).getRequisition(rnrId);
  }

  @Test
  public void shouldThrowErrorIfGetServiceThrowsError() throws Exception {
    Long rnrId = 3L;
    Rnr rnr = new Rnr(rnrId);
    DataException exception = new DataException("some error");
    doThrow(exception).when(service).getRequisition(rnrId);

    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse("rnr", rnr), BAD_REQUEST);
    when(RestResponse.error(exception.getOpenLmisMessage(), BAD_REQUEST)).thenReturn(expectedResponse);

    ResponseEntity<RestResponse> response = controller.getRequisition(rnrId);

    assertThat(response, is(expectedResponse));
  }
}
