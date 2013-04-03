/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.builder.RequisitionGroupBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.RequisitionGroupMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequisitionGroupRepositoryTest {

  RequisitionGroupRepository repository;
  RequisitionGroup requisitionGroup;

  @Mock
  private RequisitionGroupMapper mapper;
  @Mock
  private CommaSeparator commaSeparator;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    repository = new RequisitionGroupRepository(mapper, commaSeparator);
    requisitionGroup = make(a(RequisitionGroupBuilder.defaultRequisitionGroup));
    requisitionGroup.setSupervisoryNode(new SupervisoryNode());
  }

  @Test
  public void shouldSaveRequisitionGroup() throws Exception {
    repository.insert(requisitionGroup);
    verify(mapper).insert(requisitionGroup);
  }

  @Test
  public void shouldGetRequisitionGroupForSupervisoryNodes() throws Exception {
    List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
    when(commaSeparator.commaSeparateIds(supervisoryNodes)).thenReturn("{1, 2}");
    List<RequisitionGroup> requisitionGroups = new ArrayList<>();
    when(mapper.getRequisitionGroupBySupervisoryNodes("{1, 2}")).thenReturn(requisitionGroups);
    List<RequisitionGroup> result = repository.getRequisitionGroups(supervisoryNodes);
    verify(mapper).getRequisitionGroupBySupervisoryNodes("{1, 2}");
    assertThat(result, is(requisitionGroups));
  }

  @Test
  public void shouldGetRequisitionGroupForFacilityAndProgram() throws Exception {
    Facility facility = new Facility(1);
    Program program = new Program(1);
    repository.getRequisitionGroupForProgramAndFacility(program, facility);
    verify(mapper).getRequisitionGroupForProgramAndFacility(program, facility);
  }
}
