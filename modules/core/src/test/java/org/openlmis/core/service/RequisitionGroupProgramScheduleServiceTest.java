package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.repository.RequisitionGroupProgramScheduleRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequisitionGroupProgramScheduleServiceTest {

    @Mock
    RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveRequisitionGroupProgramSchedule() throws Exception {

        RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();

        new RequisitionGroupProgramScheduleService(requisitionGroupProgramScheduleRepository).save(requisitionGroupProgramSchedule);

        verify(requisitionGroupProgramScheduleRepository).insert(requisitionGroupProgramSchedule);
    }
}
