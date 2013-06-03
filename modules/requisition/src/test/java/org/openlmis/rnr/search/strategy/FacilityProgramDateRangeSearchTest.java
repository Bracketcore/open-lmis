/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.search.strategy;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityProgramDateRangeSearchTest {

  @Mock
  ProcessingScheduleService processingScheduleService;

  @Mock
  RequisitionRepository requisitionRepository;

  @Test
  public void testSearch() throws Exception {
    //Arrange
    Date dateRangeStart = new Date(), dateRangeEnd = new Date();
    Long facilityId = 1L, programId = 1L;
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facilityId, programId, dateRangeStart, dateRangeEnd);
    FacilityProgramDateRangeSearch facilityProgramDateRangeSearch = new FacilityProgramDateRangeSearch(criteria, processingScheduleService, requisitionRepository);
    List<ProcessingPeriod> periods = new ArrayList<>();
    Facility facility = new Facility(1L);
    Program program = new Program(1L);
    List<Rnr> requisitions = new ArrayList<>();
    when(processingScheduleService.getAllPeriodsForDateRange(facility, program, dateRangeStart, dateRangeEnd)).thenReturn(periods);
    when(requisitionRepository.get(facility, program, periods)).thenReturn(requisitions);


    //Act
    List<Rnr> actualRequisitions = facilityProgramDateRangeSearch.search();

    //Assert
    assertThat(actualRequisitions, is(requisitions));
    verify(processingScheduleService).getAllPeriodsForDateRange(facility, program, dateRangeStart, dateRangeEnd);
    verify(requisitionRepository).get(facility, program, periods);

  }
}
