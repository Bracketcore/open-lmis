package org.openlmis.rnr.strategy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FacilityProgramDateRangeSearchTest {

  @Mock
  ProcessingScheduleService processingScheduleService;

  @Mock
  RequisitionRepository requisitionRepository;

  @Test
  public void testSearch() throws Exception {
    //Arrange
    FacilityProgramDateRangeSearch facilityProgramDateRangeSearch = new FacilityProgramDateRangeSearch(processingScheduleService, requisitionRepository);
    Date dateRangeStart = new Date();
    Date dateRangeEnd = new Date();
    List<ProcessingPeriod> periods = new ArrayList<>();
    Facility facility = new Facility(1);
    Program program = new Program(1);
    List<Rnr> requisitions = new ArrayList<>();
    when(processingScheduleService.getAllPeriodsForDateRange(facility, program, dateRangeStart, dateRangeEnd)).thenReturn(periods);
    when(requisitionRepository.get(facility, program, periods)).thenReturn(requisitions);

    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(1, 1, dateRangeStart, dateRangeEnd);

    //Act
    List<Rnr> actualRequisitions = facilityProgramDateRangeSearch.search(criteria);

    //Assert
    assertThat(actualRequisitions, is(requisitions));
    verify(processingScheduleService).getAllPeriodsForDateRange(facility, program, dateRangeStart, dateRangeEnd);
    verify(requisitionRepository).get(facility, program, periods);

  }
}
