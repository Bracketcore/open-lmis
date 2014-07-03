/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.service.JasperReportsViewFactory;
import org.openlmis.reporting.service.TemplateService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.sql.DataSource;
import java.util.HashMap;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.reporting.controller.ReportController.USER_ID;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@Category(UnitTests.class)
@PrepareForTest(ReportController.class)
public class ReportControllerTest {

  @Mock
  TemplateService templateService;

  @Mock
  private JasperReportsViewFactory viewFactory;

  @Mock
  private DataSource dataSource;

  @InjectMocks
  ReportController controller;

  private MockHttpServletRequest request;
  private Long userId = 1L;

  @Before
  public void setUp() {
    request = new MockHttpServletRequest();
    request.getSession().setAttribute(USER_ID, userId);
  }

  @Test
  public void shouldGenerateReportInRequestedFormat() throws Exception {
    Template template = new Template();
    when(templateService.getById(1L)).thenReturn(template);
    JasperReportsMultiFormatView mockView = mock(JasperReportsMultiFormatView.class);
    HashMap<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("createdBy", userId);
    when(viewFactory.getJasperReportsView(template)).thenReturn(mockView);
    whenNew(HashMap.class).withNoArguments().thenReturn(parameterMap);

    ModelAndView modelAndView = controller.generateReport(request, 1L, "pdf");

    assertThat((JasperReportsMultiFormatView) modelAndView.getView(), is(mockView));
    verify(viewFactory).getJasperReportsView(template);
    verify(templateService).getById(1L);
  }

  @Test
  public void shouldGetReportParameters() throws Exception {
    Long id = 2L;
    Template template = new Template();
    when(templateService.getLWById(id)).thenReturn(template);

    Template reportWithParameters = controller.getReportWithParameters(id);

    assertThat(reportWithParameters, is(template));
  }
}
