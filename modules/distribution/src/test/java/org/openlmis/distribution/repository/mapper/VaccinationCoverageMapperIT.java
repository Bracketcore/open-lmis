/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.distribution.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.distribution.builder.DistributionBuilder.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-distribution.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class VaccinationCoverageMapperIT {

  @Autowired
  DeliveryZoneMapper deliveryZoneMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  ProcessingPeriodMapper periodMapper;

  @Autowired
  DistributionMapper distributionMapper;

  @Autowired
  private ProcessingScheduleMapper scheduleMapper;

  @Autowired
  private QueryExecutor queryExecutor;

  @Autowired
  private FacilityVisitMapper facilityVisitMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  @Autowired
  VaccinationCoverageMapper mapper;

  @Autowired
  private ProductMapper productMapper;

  Distribution distribution;
  DeliveryZone zone;
  Program program1;
  ProcessingPeriod processingPeriod;

  Facility facility;
  FacilityVisit facilityVisit;

  @Before
  public void setUp() throws Exception {
    zone = make(a(defaultDeliveryZone));
    program1 = make(a(defaultProgram));
    facility = make(a(defaultFacility));
    ProcessingSchedule schedule = make(a(defaultProcessingSchedule));
    scheduleMapper.insert(schedule);

    processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId())));

    deliveryZoneMapper.insert(zone);
    programMapper.insert(program1);
    periodMapper.insert(processingPeriod);

    distribution = make(a(initiatedDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(program, program1)));

    distributionMapper.insert(distribution);

    facilityMapper.insert(facility);

    facilityVisit = new FacilityVisit(facility, distribution);
    facilityVisitMapper.insert(facilityVisit);
  }

  @Test
  public void shouldSaveVaccinationFullCoverage() throws Exception {
    VaccinationFullCoverage vaccinationFullCoverage = new VaccinationFullCoverage(34, 78, 11, 666);
    vaccinationFullCoverage.setFacilityVisitId(facilityVisit.getId());
    vaccinationFullCoverage.setCreatedBy(1L);
    mapper.insertFullVaccinationCoverage(vaccinationFullCoverage);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM full_coverages WHERE id = " + vaccinationFullCoverage.getId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getLong("facilityVisitId"), is(facilityVisit.getId()));
    assertThat(resultSet.getInt("femaleHealthCenter"), is(34));
    assertThat(resultSet.getInt("femaleOutreach"), is(78));
    assertThat(resultSet.getInt("maleHealthCenter"), is(11));
    assertThat(resultSet.getInt("maleOutreach"), is(666));
    assertThat(resultSet.getLong("createdBy"), is(1L));
  }

  @Test
  public void shouldGetFullCoverageByFacilityVisitId() {
    VaccinationFullCoverage vaccinationFullCoverage = new VaccinationFullCoverage(34, 78, 11, 666);
    vaccinationFullCoverage.setFacilityVisitId(facilityVisit.getId());
    mapper.insertFullVaccinationCoverage(vaccinationFullCoverage);

    VaccinationFullCoverage savedVaccinationFullCoverage = mapper.getFullCoverageBy(facilityVisit.getId());

    assertThat(savedVaccinationFullCoverage, is(vaccinationFullCoverage));
  }

  @Test
  public void shouldReturnVaccinationCoverageProductMappings() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));

    productMapper.insert(product);
    VaccinationProduct vaccinationProduct = new VaccinationProduct("BCG", product.getCode(), true);

    queryExecutor.executeUpdate("INSERT INTO coverage_vaccination_products (vaccination, productCode, childCoverage) VALUES (?, ?, ?)",
      vaccinationProduct.getVaccination(), vaccinationProduct.getProductCode(), vaccinationProduct.getChildCoverage());

    List<VaccinationProduct> vaccinationProducts = mapper.getVaccinationProducts(true);

    assertThat(vaccinationProducts.size(), is(1));
    VaccinationProduct vaccinationProductFromDB = vaccinationProducts.get(0);
    assertThat(vaccinationProductFromDB.getVaccination(), is(vaccinationProduct.getVaccination()));
    assertThat(vaccinationProductFromDB.getProductCode(), is(vaccinationProduct.getProductCode()));
    assertThat(vaccinationProductFromDB.getChildCoverage(), is(vaccinationProduct.getChildCoverage()));
  }

  @Test
  public void shouldInsertChildVaccinationCoverage() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));

    productMapper.insert(product);
    VaccinationProduct vaccinationProduct = new VaccinationProduct("BCG", product.getCode(), true);

    queryExecutor.executeUpdate("INSERT INTO coverage_vaccination_products (vaccination, productCode, childCoverage) VALUES (?, ?, ?)",
      vaccinationProduct.getVaccination(), vaccinationProduct.getProductCode(), vaccinationProduct.getChildCoverage());

    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem(facilityVisit.getId(), "BCG", 56);
    mapper.insertChildVaccinationCoverageLineItem(childCoverageLineItem);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM vaccination_child_coverage_line_items WHERE facilityVisitId = " + childCoverageLineItem.getFacilityVisitId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getLong("facilityVisitId"), is(facilityVisit.getId()));
    assertThat(resultSet.getInt("targetGroup"), is(56));
    assertThat(resultSet.getString("vaccination"), is("BCG"));
  }

  @Test
  public void shouldGetChildCoverageLineItemByFacilityVisitId() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));

    productMapper.insert(product);
    VaccinationProduct vaccinationProduct = new VaccinationProduct("BCG", product.getCode(), true);

    queryExecutor.executeUpdate("INSERT INTO coverage_vaccination_products (vaccination, productCode, childCoverage) VALUES (?, ?, ?)",
      vaccinationProduct.getVaccination(), vaccinationProduct.getProductCode(), vaccinationProduct.getChildCoverage());

    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem(facilityVisit.getId(), "BCG", 56);
    mapper.insertChildVaccinationCoverageLineItem(childCoverageLineItem);

    List<ChildCoverageLineItem> fetchedChildCoverageLineItems = mapper.getChildCoverageLineItemsBy(facilityVisit.getId());

    assertThat(fetchedChildCoverageLineItems.size(), is(1));
    assertThat(fetchedChildCoverageLineItems.get(0).getFacilityVisitId(), is(facilityVisit.getId()));
  }
}
