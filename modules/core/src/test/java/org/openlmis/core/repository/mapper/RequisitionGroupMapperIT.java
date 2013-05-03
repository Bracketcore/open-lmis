/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.RequisitionGroupBuilder.*;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.defaultSupervisoryNode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class RequisitionGroupMapperIT {
  @Autowired
  RequisitionGroupMapper requisitionGroupMapper;

  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;

  @Autowired
  RequisitionGroupMemberMapper requisitionGroupMemberMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;

  private RequisitionGroup requisitionGroup;
  private SupervisoryNode supervisoryNode;

  private Facility facility;

  @Before
  public void setUp() throws Exception {
    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    supervisoryNode = make(a(defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);
    requisitionGroup = make(a(defaultRequisitionGroup));
  }

  @Test
  public void shouldInsertRequisitionGroup() throws Exception {
    requisitionGroup.setSupervisoryNode(supervisoryNode);

    requisitionGroupMapper.insert(requisitionGroup);

    RequisitionGroup resultRequisitionGroup = requisitionGroupMapper.getRequisitionGroupById(requisitionGroup.getId());

    assertThat(resultRequisitionGroup.getCode(), is(REQUISITION_GROUP_CODE));
    assertThat(requisitionGroup.getId(), is(notNullValue()));
    assertThat(resultRequisitionGroup.getModifiedDate(), is(requisitionGroup.getModifiedDate()));
    assertThat(resultRequisitionGroup.getName(), is(REQUISITION_GROUP_NAME));
    assertThat(resultRequisitionGroup.getSupervisoryNode().getId(), is(supervisoryNode.getId()));
  }

  @Test
  public void shouldUpdateRequisitionGroup() throws Exception {
    requisitionGroup.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup);

    requisitionGroup.setName("updated name");
    requisitionGroup.setDescription("updated description");
    supervisoryNode.setId(2L);

    requisitionGroupMapper.update(requisitionGroup);

    RequisitionGroup resultRequisitionGroup = requisitionGroupMapper.getRequisitionGroupById(requisitionGroup.getId());

    assertThat(resultRequisitionGroup.getCode(), is(REQUISITION_GROUP_CODE));
    assertThat(resultRequisitionGroup.getModifiedDate(), is(requisitionGroup.getModifiedDate()));
    assertThat(resultRequisitionGroup.getName(), is("updated name"));
    assertThat(resultRequisitionGroup.getDescription(), is("updated description"));
    assertThat(resultRequisitionGroup.getSupervisoryNode().getId(), is(2L));
  }

  @Test
  public void shouldGetRequisitionGroupsForSupervisoryNodes() {
    requisitionGroup.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup);

    List<RequisitionGroup> requisitionGroups = requisitionGroupMapper.getRequisitionGroupBySupervisoryNodes("{" + supervisoryNode.getId() + "}");

    assertThat(requisitionGroups.size(), is(1));
  }

  @Test
  public void shouldGetRequisitionGroupByProgramIdAndFacilityId() throws Exception {
    requisitionGroupMapper.insert(requisitionGroup);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();
    requisitionGroupProgramSchedule.setProgram(make(a(defaultProgram)));
    requisitionGroupProgramSchedule.setRequisitionGroup(requisitionGroup);
    requisitionGroupProgramSchedule.setProcessingSchedule(processingSchedule);
    programMapper.insert(requisitionGroupProgramSchedule.getProgram());

    requisitionGroupProgramScheduleMapper.insert(requisitionGroupProgramSchedule);

    RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setFacility(facility);
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMember.setModifiedBy(1L);
    requisitionGroupMemberMapper.insert(requisitionGroupMember);

    assertThat(requisitionGroupMapper.getRequisitionGroupForProgramAndFacility(requisitionGroupProgramSchedule.getProgram(),
      requisitionGroupMember.getFacility()), is(requisitionGroup));
  }

  @Test
  public void shouldGetRequisitionByCode() {
    requisitionGroup.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup);
    assertThat(requisitionGroupMapper.getByCode(requisitionGroup.getCode()), is(requisitionGroup));
  }

}
