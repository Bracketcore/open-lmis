package org.openlmis.core.repository.mapper;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.RequisitionGroupBuilder.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class RequisitionGroupMemberMapperIT {

    RequisitionGroupMember requisitionGroupMember;
    RequisitionGroup requisitionGroup;

    @Autowired
    RequisitionGroupMemberMapper requisitionGroupMemberMapper;

    @Autowired
    RequisitionGroupMapper requisitionGroupMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Autowired
    RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;

    @Autowired
    ProgramMapper programMapper;

    @Autowired
    ScheduleMapper scheduleMapper;

    @Before
    public void setUp() throws Exception {
        requisitionGroupMember = new RequisitionGroupMember();

        Facility facility = make(a(FacilityBuilder.defaultFacility));
        requisitionGroup = make(a(defaultRequisitionGroup));

        facilityMapper.insert(facility);
        requisitionGroupMapper.insert(requisitionGroup);

        requisitionGroupMember.setFacility(facility);
        requisitionGroupMember.setRequisitionGroup(requisitionGroup);
        requisitionGroupMember.setModifiedBy("User");
    }

    @Test
    public void shouldInsertRequisitionGroupToFacilityMapping() throws Exception {
        int status = requisitionGroupMemberMapper.insert(requisitionGroupMember);
        assertThat(status, is(1));
    }

    @Test
    public void shouldGetProgramsMappedToRequisitionGroupByFacilityId() throws Exception {
        RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();
        requisitionGroupProgramSchedule.setProgram(make(a(defaultProgram)));
        requisitionGroupProgramSchedule.setRequisitionGroup(requisitionGroup);
        ProcessingSchedule schedule = new ProcessingSchedule();
        schedule.setCode("Q1stY");
        schedule.setName("QuarterYearly");
        requisitionGroupProgramSchedule.setSchedule(schedule);
        programMapper.insert(requisitionGroupProgramSchedule.getProgram());
        requisitionGroupProgramSchedule.getSchedule().setId(scheduleMapper.insert(requisitionGroupProgramSchedule.getSchedule()));

        requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);
        requisitionGroupMemberMapper.insert(requisitionGroupMember);

        List<Integer> programIds = requisitionGroupMemberMapper.getRequisitionGroupProgramIdsForId(requisitionGroupMember.getFacility().getId());

        assertThat(programIds.size(), is(1));
        assertThat(programIds.get(0), is(requisitionGroupProgramSchedule.getProgram().getId()));
    }

    @Test
    public void shouldReturnOneIfMappingAlreadyExists() throws Exception {
        RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();
        requisitionGroupProgramSchedule.setProgram(make(a(defaultProgram)));
        requisitionGroupProgramSchedule.setRequisitionGroup(requisitionGroup);
        ProcessingSchedule schedule = new ProcessingSchedule();
        schedule.setCode("Q1stY");
        schedule.setName("QuarterYearly");
        requisitionGroupProgramSchedule.setSchedule(schedule);
        programMapper.insert(requisitionGroupProgramSchedule.getProgram());
        requisitionGroupProgramSchedule.getSchedule().setId(scheduleMapper.insert(requisitionGroupProgramSchedule.getSchedule()));

        requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);
        requisitionGroupMemberMapper.insert(requisitionGroupMember);

        assertThat(requisitionGroupMemberMapper.doesMappingExist(requisitionGroupMember.getRequisitionGroup().getId(), requisitionGroupMember.getFacility().getId()), is(1));

    }

    @Test
    public void shouldGetRGCodeByProgramIdAndFacilityId() throws Exception {
        RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();
        requisitionGroupProgramSchedule.setProgram(make(a(defaultProgram)));
        requisitionGroupProgramSchedule.setRequisitionGroup(requisitionGroup);
        ProcessingSchedule schedule = new ProcessingSchedule();
        schedule.setCode("Q1stY");
        schedule.setName("QuarterYearly");
        requisitionGroupProgramSchedule.setSchedule(schedule);
        programMapper.insert(requisitionGroupProgramSchedule.getProgram());
        requisitionGroupProgramSchedule.getSchedule().setId(scheduleMapper.insert(requisitionGroupProgramSchedule.getSchedule()));

        requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);
        requisitionGroupMemberMapper.insert(requisitionGroupMember);
        assertThat(requisitionGroupMemberMapper.getRequisitionGroupCodeForProgramAndFacility(requisitionGroupProgramSchedule.getProgram().getId(),
                requisitionGroupMember.getFacility().getId()), is(REQUISITION_GROUP_CODE));
    }
}
