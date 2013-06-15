/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.distribution.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.distribution.builder.DeliveryZoneBuilder;
import org.openlmis.distribution.builder.DeliveryZoneProgramScheduleBuilder;
import org.openlmis.distribution.domain.DeliveryZone;
import org.openlmis.distribution.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.distribution.builder.DeliveryZoneProgramScheduleBuilder.*;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.code;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-distribution.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class DeliveryZoneProgramScheduleMapperIT {

  @Autowired
  DeliveryZoneProgramScheduleMapper mapper;
  @Autowired
  DeliveryZoneMapper deliveryZoneMapper;
  @Autowired
  ProcessingScheduleMapper scheduleMapper;
  @Autowired
  ProgramMapper programMapper;
  @Autowired
  QueryExecutor queryExecutor;

  private ProcessingSchedule processingSchedule;
  private DeliveryZone deliveryZone;
  private Program program;
  private DeliveryZoneProgramSchedule deliveryZoneProgramSchedule;

  @Before
  public void setUp() throws Exception {
    processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));

    deliveryZone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));
    program = make(a(defaultProgram));

    programMapper.insert(program);
    scheduleMapper.insert(processingSchedule);
    deliveryZoneMapper.insert(deliveryZone);

    deliveryZoneProgramSchedule = make(a(defaultDZProgramSchedule,
      with(DeliveryZoneProgramScheduleBuilder.program, program),
      with(schedule, processingSchedule), with(zone, deliveryZone)));
  }

  @Test
  public void shouldInsertDZProgramSchedule() throws Exception {
    mapper.insert(deliveryZoneProgramSchedule);

    DeliveryZoneProgramSchedule returnedValue = mapper.getByDeliveryZoneCodeAndProgramCode(deliveryZone.getCode(), program.getCode());

    assertDZPSEquals(deliveryZoneProgramSchedule, returnedValue);
  }

  private void assertDZPSEquals(DeliveryZoneProgramSchedule deliveryZoneProgramSchedule, DeliveryZoneProgramSchedule actual) {
    DeliveryZoneProgramSchedule expectedDZPS = new DeliveryZoneProgramSchedule(deliveryZone.getId(), program.getId(), processingSchedule.getId());
    expectedDZPS.setCreatedBy(deliveryZoneProgramSchedule.getCreatedBy());
    assertThat(actual.getCreatedDate(), is(notNullValue()));
    actual.setCreatedDate(null);

    assertThat(actual.getProgram().getId(), is(expectedDZPS.getProgram().getId()));
    assertThat(actual.getDeliveryZone().getId(), is(expectedDZPS.getDeliveryZone().getId()));
    assertThat(actual.getSchedule().getId(), is(expectedDZPS.getSchedule().getId()));
  }

  @Test
  public void shouldUpdateDZProgramSchedule() throws Exception {
    mapper.insert(deliveryZoneProgramSchedule);
    ProcessingSchedule processingSchedule1 = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule, with(code, "HalfYearly")));
    scheduleMapper.insert(processingSchedule1);
    deliveryZoneProgramSchedule.setSchedule(processingSchedule);

    mapper.update(deliveryZoneProgramSchedule);

    DeliveryZoneProgramSchedule returned = mapper.getByDeliveryZoneCodeAndProgramCode(deliveryZone.getCode(), program.getCode());

    assertDZPSEquals(deliveryZoneProgramSchedule, returned);
  }

  @Test
  public void shouldGetProgramIdsForDeliveryZone() throws Exception {
    mapper.insert(deliveryZoneProgramSchedule);
    List<Long> programIds = mapper.getProgramsIdsForDeliveryZones(deliveryZoneProgramSchedule.getDeliveryZone().getId());

    assertThat(programIds.get(0), is(deliveryZoneProgramSchedule.getProgram().getId()));
    assertThat(programIds.size(), is(1));
  }

  @Test
  public void shouldGetProcessingScheduleForProgramInDeliveryZone() throws Exception {
    long deliveryZoneId = insertDeliveryZone("deliveryZoneCode", "DZ name");
    Long scheduleId = insertSchedule();
    insertDeliveryZoneProgramSchedule(deliveryZoneId, scheduleId);

    ProcessingSchedule processingSchedule = mapper.getProcessingScheduleByZoneAndProgram(deliveryZoneId, getProgramId());

    assertThat(processingSchedule.getId(), is(scheduleId));
  }

  private Long getProgramId() throws SQLException {
    try (ResultSet resultSet = queryExecutor.execute("SELECT id FROM programs WHERE code = 'VACCINES'")) {
      resultSet.next();
      return resultSet.getLong(1);
    }
  }

  private void insertDeliveryZoneProgramSchedule(long id, Long scheduleId) throws SQLException {
    queryExecutor.executeUpdate("INSERT INTO delivery_zone_program_schedules(deliveryZoneId, programId, scheduleId ) " +
      "VALUES(?,(SELECT id FROM programs WHERE code='VACCINES'), ?)", asList(id, scheduleId));
  }

  private long insertSchedule() throws SQLException {
    return queryExecutor.executeUpdate("INSERT INTO processing_schedules(code, name, description) values(?, ?, ?)",
      asList("M", "scheduleName", "desc"));
  }

  private long insertDeliveryZone(String deliveryZoneCode, String deliveryZoneName) throws SQLException {
    return queryExecutor.executeUpdate("INSERT INTO delivery_zones (code, name) values (?,?)", asList(deliveryZoneCode, deliveryZoneName));
  }
}
