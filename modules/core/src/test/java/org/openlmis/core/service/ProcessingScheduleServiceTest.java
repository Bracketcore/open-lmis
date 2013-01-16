package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.builder.RequisitionGroupBuilder;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.repository.ProcessingScheduleRepository;
import org.openlmis.core.repository.RequisitionGroupProgramScheduleRepository;
import org.openlmis.core.repository.RequisitionGroupRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;

public class ProcessingScheduleServiceTest {
  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Mock
  private ProcessingScheduleRepository repository;

  @Mock
  private ProcessingPeriodRepository periodRepository;

  @Mock
  private RequisitionGroupRepository requisitionGroupRepository;

  @Mock
  private RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;

  private ProcessingScheduleService service;
  private final int PROCESSING_PERIOD_ID = 1;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    service = new ProcessingScheduleService(repository, periodRepository, requisitionGroupRepository);
  }

  @Test
  public void shouldGetAllSchedules() throws Exception {
    List<ProcessingSchedule> processingScheduleList = new ArrayList<>();
    processingScheduleList.add(new ProcessingSchedule());
    when(repository.getAll()).thenReturn(processingScheduleList);

    List<ProcessingSchedule> processingSchedules = service.getAll();

    assertThat(processingSchedules, is(processingScheduleList));
  }

  @Test
  public void shouldGetASchedule() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    when(repository.get(1)).thenReturn(processingSchedule);

    ProcessingSchedule expectedProcessingSchedule = service.get(1);

    assertThat(processingSchedule, is(expectedProcessingSchedule));
  }

  @Test
  public void shouldThrowExceptionIfScheduleNotFound() throws Exception {
    doThrow(new DataException("Schedule not found")).when(repository).get(1);
    exException.expect(DataException.class);
    exException.expectMessage("Schedule not found");
    service.get(1);
  }

  @Test
  public void shouldInsertAndReturnInsertedSchedule() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule("testCode", "testName");
    ProcessingSchedule mockedSchedule = mock(ProcessingSchedule.class);
    when(repository.get(processingSchedule.getId())).thenReturn(mockedSchedule);

    ProcessingSchedule returnedSchedule = service.save(processingSchedule);

    verify(repository).create(processingSchedule);
    assertThat(returnedSchedule, is(mockedSchedule));
  }

  @Test
  public void shouldUpdateAndReturnUpdatedSchedule() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setId(1);
    ProcessingSchedule mockedSchedule = mock(ProcessingSchedule.class);
    when(repository.get(processingSchedule.getId())).thenReturn(mockedSchedule);

    ProcessingSchedule returnedSchedule = service.save(processingSchedule);

    verify(repository).update(processingSchedule);
    assertThat(returnedSchedule, is(mockedSchedule));
  }

  @Test
  public void shouldThrowErrorWhenTryingToSaveAScheduleWithNoCode() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    doThrow(new DataException("Schedule can not be saved without its code.")).when(repository).create(processingSchedule);

    exException.expect(DataException.class);
    exException.expectMessage("Schedule can not be saved without its code.");

    service.save(processingSchedule);
    verify(repository).create(processingSchedule);
  }

  @Test
  public void shouldThrowErrorWhenTryingToSaveAScheduleWithNoName() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setCode("testCode");
    doThrow(new DataException("Schedule can not be saved without its name.")).when(repository).create(processingSchedule);

    exException.expect(DataException.class);
    exException.expectMessage("Schedule can not be saved without its name.");

    service.save(processingSchedule);
    verify(repository).create(processingSchedule);
  }

  @Test
  public void shouldGetAllPeriodsForGivenSchedule() throws Exception {
    List<ProcessingPeriod> periodList = new ArrayList<>();
    when(periodRepository.getAll(123)).thenReturn(periodList);

    assertThat(service.getAllPeriods(123), is(periodList));
    verify(periodRepository).getAll(123);
  }

  @Test
  public void shouldInsertAPeriod() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod));
    service.savePeriod(processingPeriod);
    verify(periodRepository).insert(processingPeriod);
  }

  @Test
  public void shouldDeletePeriodIfStartDateGreaterThanCurrentDate(){
    ProcessingPeriod processingPeriod = new ProcessingPeriod();
    processingPeriod.setId(PROCESSING_PERIOD_ID);
    service.deletePeriod(processingPeriod.getId());
    verify(periodRepository).delete(processingPeriod.getId());
  }

  @Test
  public void shouldThrowExceptionIfStartDateLessThanOrEqualToCurrentDateWhenDeletingPeriod(){
    ProcessingPeriod processingPeriod = new ProcessingPeriod();
    processingPeriod.setId(PROCESSING_PERIOD_ID);
    String errorMessage = "some error";
    doThrow(new DataException( errorMessage)).when(periodRepository).delete(processingPeriod.getId());

    exException.expect(DataException.class);
    exException.expectMessage( errorMessage);

    service.deletePeriod(processingPeriod.getId());
  }

  @Test
  public void shouldGetAllPeriodsForFacilityAndProgram() throws Exception {
    Integer requisitionGroupId = 1;
    Integer facilityId = 10;
    Integer programId = 2;

    List<ProcessingPeriod> periodList = Arrays.asList(make(a(defaultProcessingPeriod)));
    RequisitionGroup requisitionGroup = make(a(RequisitionGroupBuilder.defaultRequisitionGroup));
    requisitionGroup.setId(requisitionGroupId);

    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(programId, facilityId)).thenReturn(requisitionGroup);
    when(periodRepository.getAllPeriodsForARequisitionGroupAndAProgram(requisitionGroupId, programId)).thenReturn(periodList);

    List<ProcessingPeriod> periods = service.getAllPeriodsForFacilityAndProgram(facilityId, programId);

    assertThat(periods, is(periodList));
  }
}
