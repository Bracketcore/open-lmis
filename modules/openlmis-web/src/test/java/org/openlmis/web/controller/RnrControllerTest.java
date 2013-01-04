package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RnrService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.web.controller.RnrController.RNR;

public class RnrControllerTest {

  MockHttpServletRequest request;
  private static final String USER = "user";
  private static final Integer USER_ID = 1;

  RnrService rnrService;

  RnrController controller;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);

    rnrService = mock(RnrService.class);
    controller = new RnrController(rnrService);
  }

  @Test
  public void shouldSaveWIPRnr() throws Exception {
    Rnr rnr = new Rnr();

    controller.saveRnr(rnr, request);

    verify(rnrService).save(rnr);
    assertThat(rnr.getModifiedBy(), is(equalTo(USER_ID)));
  }

  @Test
  public void shouldGiveErrorIfInitiatingFails() throws Exception {
    String errorMessage = "error-message";
    doThrow(new DataException(errorMessage)).when(rnrService).initRnr(1, 2, USER_ID);
    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1, 2, request);
    assertThat(response.getBody().getErrorMsg(), is(equalTo(errorMessage)));
  }

  @Test
  public void shouldReturnNullIfGettingRequisitionFails() throws Exception {
    Rnr expectedRnr = null;
    when(rnrService.get(1, 2)).thenReturn(expectedRnr);
    ResponseEntity<OpenLmisResponse> response = controller.get(1, 2);
    assertThat((Rnr)response.getBody().getData().get(RNR), is(expectedRnr));
  }

  @Test
  public void shouldAllowSubmittingOfRnrAndTagWithModifiedBy() throws Exception {
    Rnr rnr = new Rnr();
    controller.submit(rnr, request);
    verify(rnrService).submit(rnr);
    assertThat(rnr.getModifiedBy(), is(USER_ID));
  }
}
