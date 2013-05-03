/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.ReportTemplate;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ReportService;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ReportTemplateController.class)
public class ReportTemplateControllerTest {

  @Mock
  private ReportService reportService;

  @InjectMocks
  private ReportTemplateController controller;

  private MockHttpServletRequest request;
  private static final Integer USER = 1;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER);
    request.setSession(session);
  }

  @Test
  public void shouldUploadJasperTemplateFileIfValid() throws Exception {
    MockMultipartFile reportTemplateFile = new MockMultipartFile("template.jrxml","template.jrxml","", new byte[1]);
    ReportTemplate report = new ReportTemplate("reportName",reportTemplateFile, USER);
    whenNew(ReportTemplate.class).withArguments("reportName", reportTemplateFile, USER).thenReturn(report);
    ResponseEntity<OpenLmisResponse> response = controller.uploadJasperTemplate(request, reportTemplateFile, "reportName");

    assertThat(response.getBody().getSuccessMsg(), is("Report created successfully"));
    verify(reportService).insert(report);
  }

  @Test
  public void shouldGiveErrorForInvalidReport() throws Exception {
    whenNew(ReportTemplate.class).withAnyArguments().thenThrow(new DataException("Error message"));

    ResponseEntity<OpenLmisResponse> response = controller.uploadJasperTemplate(request, null, "template");

    assertThat(response.getBody().getErrorMsg(), is("Error message"));
  }
}
