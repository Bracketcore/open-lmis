package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.FacilityBuilder.code;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.SCHEDULE_CODE;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class BudgetLineItemMapperIT {

  @Autowired
  private BudgetFileMapper budgetFileMapper;

  @Autowired
  private BudgetLineItemMapper mapper;

  @Autowired
  private QueryExecutor queryExecutor;

  @Autowired
  private ProcessingScheduleMapper scheduleMapper;

  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  private BudgetLineItem budgetLineItem;

  private BudgetFileInfo budgetFileInfo;

  private String facilityCode;

  private ProcessingPeriod period;

  @Before
  public void setUp() throws Exception {

    ProcessingSchedule schedule = make(a(defaultProcessingSchedule, with(ProcessingScheduleBuilder.code, "Q1")));
    scheduleMapper.insert(schedule);

    period = make(a(defaultProcessingPeriod,
      with(ProcessingPeriodBuilder.startDate, new Date()),
      with(scheduleId, schedule.getId())));
    processingPeriodMapper.insert(period);

    budgetFileInfo = new BudgetFileInfo("Budget File", false);
    budgetFileMapper.insert(budgetFileInfo);

    facilityCode = "F1011";
    Facility facility = make(a(defaultFacility, with(code, facilityCode)));
    facilityMapper.insert(facility);

    budgetLineItem = new BudgetLineItem(facilityCode,
      "HIV",
      period.getId(),
      budgetFileInfo.getId(),
      new Date(),
      BigDecimal.valueOf(345.45),
      "My good notes");


  }

  @Test
  public void shouldInsertBudgetLineItem() throws Exception {
    mapper.insert(budgetLineItem);

    assertThat(budgetLineItem.getId(), is(notNullValue()));
    ResultSet resultSet = queryExecutor.execute("SELECT * FROM budget_line_items WHERE budgetFileId = " + budgetFileInfo.getId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getLong("id"), is(budgetLineItem.getId()));
  }

  @Test
  public void shouldUpdateBudgetLineItem() throws Exception {
    mapper.insert(budgetLineItem);

    BudgetLineItem duplicatedBudgetLineItem = new BudgetLineItem(facilityCode,
      "HIV",
      period.getId(),
      budgetFileInfo.getId(),
      new Date(),
      BigDecimal.valueOf(145.45),
      "My updated good notes");

    mapper.update(duplicatedBudgetLineItem);
    ResultSet resultSet = queryExecutor.execute("SELECT * FROM budget_line_items WHERE budgetFileId = " + budgetFileInfo.getId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getString("notes"), is("My updated good notes"));
  }
}
