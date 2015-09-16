package org.openlmis.web.controller.vaccine.demographic;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.dto.DemographicEstimateForm;
import org.openlmis.vaccine.dto.DemographicEstimateLineItem;
import org.openlmis.vaccine.service.demographics.DistrictDemographicEstimateService;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistrictDemographicEstimateControllerTest {

  private static final Long USER_ID = 1L;
  private static final String USER = "user";
  @Mock
  DistrictDemographicEstimateService service;
  @InjectMocks
  DistrictDemographicEstimateController controller;
  private MockHttpServletRequest request;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);
  }

  @Test
  public void shouldGet() throws Exception {
    DemographicEstimateForm form = new DemographicEstimateForm();
    form.setEstimateLineItems(new ArrayList<DemographicEstimateLineItem>());
    when(service.getEstimateForm(2005, 1L, 1L)).thenReturn(form);

    ResponseEntity<OpenLmisResponse> response = controller.get(2005, 1L, request);

    assertThat(form, is(response.getBody().getData().get("estimates")));
    verify(service).getEstimateForm(2005, 1L, 1L);
  }

  @Test
  public void shouldSave() throws Exception {
    DemographicEstimateForm form = new DemographicEstimateForm();
    form.setEstimateLineItems(new ArrayList<DemographicEstimateLineItem>());
    doNothing().when(service).save(form, 1L);

    ResponseEntity<OpenLmisResponse> response = controller.save(form, request);

    assertThat(form, is(response.getBody().getData().get("estimates")));
    verify(service).save(form, 1L);
  }
}