/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.reporting.model.ReportTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.hamcrest.CoreMatchers.is;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-reporting.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ReportTemplateMapperIT {

  @Autowired
  ReportTemplateMapper reportTemplateMapper;

  @Test
  public void shouldGetById() throws Exception {
    ReportTemplate reportTemplate = createReportTemplate("Sample Report");

    ReportTemplate returnedTemplate = reportTemplateMapper.getById(reportTemplate.getId());

    Assert.assertThat(returnedTemplate.getName(), is(reportTemplate.getName()));
    Assert.assertThat(returnedTemplate.getData(), is(reportTemplate.getData()));
  }

  private ReportTemplate createReportTemplate(String name) {
    ReportTemplate reportTemplate = new ReportTemplate();
    reportTemplate.setName(name);
    reportTemplate.setData(new byte[1]);
    List<String> parameterList = new ArrayList<>();
    parameterList.add("rnrId");
    reportTemplate.setParameters(parameterList);
    reportTemplate.setModifiedBy(1L);
    Date currentTimeStamp = new Date();
    reportTemplate.setModifiedDate(currentTimeStamp);
    reportTemplateMapper.insert(reportTemplate);
    return reportTemplate;
  }

  @Test
  public void shouldInsertReportForXmlTemplateFile() throws Exception {

    ReportTemplate reportTemplate = new ReportTemplate();
    reportTemplate.setName("Requisition reportTemplate");
    List<String> parameters = new ArrayList<>();
    parameters.add("rnrId");
    reportTemplate.setParameters(parameters);
    File file = new ClassPathResource("report1.jrxml").getFile();

    reportTemplate.setData(readFileToByteArray(file));
    reportTemplate.setModifiedDate(new Date());
    reportTemplate.setModifiedBy(1L);

    reportTemplateMapper.insert(reportTemplate);
  }

  @Test
  public void shouldGetAllReportTemplatesAccordingToCreatedDate() throws Exception {
    ReportTemplate reportTemplate1 = createReportTemplate("report1");
    createReportTemplate("report2");

    List<ReportTemplate> result = reportTemplateMapper.getAll();

    Assert.assertThat(result.size(), CoreMatchers.is(9));
    Assert.assertThat(result.get(0).getName(), CoreMatchers.is("Facilities Missing Supporting Requisition Group"));
    Assert.assertThat(result.get(7).getName(), CoreMatchers.is("report1"));
    Assert.assertThat(result.get(7).getId(), is(reportTemplate1.getId()));
  }
}
