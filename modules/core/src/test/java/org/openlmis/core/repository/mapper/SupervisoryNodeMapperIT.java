package org.openlmis.core.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.RequisitionGroupBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.SUPERVISORY_NODE_CODE;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.code;
import static org.openlmis.core.domain.Right.CONFIGURE_RNR;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class SupervisoryNodeMapperIT {

  SupervisoryNode supervisoryNode;
  Facility facility;

  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;
  @Autowired
  RoleAssignmentMapper roleAssignmentMapper;
  @Autowired
  FacilityMapper facilityMapper;
  @Autowired
  RoleRightsMapper roleRightsMapper;
  @Autowired
  ProgramMapper programMapper;
  @Autowired
  UserMapper userMapper;
  @Autowired
  RequisitionGroupMapper requisitionGroupMapper;

  @Before
  public void setUp() throws Exception {
    supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));

    facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    supervisoryNode.setFacility(facility);
  }

  @Test
  public void shouldInsertSupervisoryNode() throws Exception {
    supervisoryNodeMapper.insert(supervisoryNode);

    SupervisoryNode resultSupervisoryNode = supervisoryNodeMapper.getSupervisoryNode(supervisoryNode.getId());

    assertThat(resultSupervisoryNode, is(notNullValue()));
    assertThat(resultSupervisoryNode.getCode(), CoreMatchers.is(SUPERVISORY_NODE_CODE));
    assertThat(resultSupervisoryNode.getName(), CoreMatchers.is(SupervisoryNodeBuilder.SUPERVISORY_NODE_NAME));
    assertThat(resultSupervisoryNode.getModifiedDate(), CoreMatchers.is(SupervisoryNodeBuilder.SUPERVISORY_NODE_DATE));
    assertThat(resultSupervisoryNode.getFacility().getId(), is(facility.getId()));
  }

  @Test
  public void shouldGetSupervisoryNodeIdByCode() throws Exception {
    supervisoryNodeMapper.insert(supervisoryNode);

    Integer fetchedId = supervisoryNodeMapper.getIdForCode(supervisoryNode.getCode());

    assertThat(fetchedId, is(supervisoryNode.getId()));
  }

  @Test
  public void shouldGetAllSupervisoryNodesInTheHierarchyForAUserAndProgramWithAppropriateRight() {
    Program program1 = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Program program2 = insertProgram(make(a(defaultProgram, with(programCode, "p2"))));

    User user = insertUser();

    Role createRole = new Role("create role", "random description");
    roleRightsMapper.insertRole(createRole);

    Role configureRnrRole = new Role("configure rnr", "random description");
    roleRightsMapper.insertRole(configureRnrRole);

    roleRightsMapper.createRoleRight(createRole.getId(), CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(configureRnrRole.getId(), CONFIGURE_RNR);

    supervisoryNodeMapper.insert(supervisoryNode);

    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1")));
    supervisoryNode1.setFacility(facility);
    supervisoryNode1 = insertSupervisoryNode(supervisoryNode1);

    SupervisoryNode childNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "CN1")));
    childNode.setFacility(facility);
    childNode.setParent(supervisoryNode);
    childNode = insertSupervisoryNode(childNode);

    insertRoleAssignments(program1, user, createRole, supervisoryNode);
    insertRoleAssignments(program1, user, configureRnrRole, supervisoryNode1);
    insertRoleAssignments(program2, user, createRole, supervisoryNode);
    insertRoleAssignments(program1, user, createRole, null);

    List<SupervisoryNode> userSupervisoryNodes = supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyBy(user.getId(), program1.getId(), CREATE_REQUISITION);

    assertThat(userSupervisoryNodes.size(), is(2));
    assertTrue(userSupervisoryNodes.contains(supervisoryNode));
    assertTrue(userSupervisoryNodes.contains(childNode));
  }

  private SupervisoryNode insertSupervisoryNode(SupervisoryNode supervisoryNode) {
    supervisoryNodeMapper.insert(supervisoryNode);
    return supervisoryNode;
  }

  private Program insertProgram(Program program) {
    programMapper.insert(program);
    return program;
  }

  private Role insertRoleAssignments(Program program, User user, Role role, SupervisoryNode supervisoryNode) {
    roleAssignmentMapper.createRoleAssignment(user, role, program, supervisoryNode);
    return role;
  }

  private User insertUser() {
    User user = new User("random123123", "pwd");
    userMapper.insert(user);
    return user;
  }

  @Test
  public void shouldGetSupervisoryNodeForRG() {
    supervisoryNodeMapper.insert(supervisoryNode);
    RequisitionGroup requisitionGroup = make(a(defaultRequisitionGroup));
    requisitionGroup.setSupervisoryNode(supervisoryNode);
    requisitionGroupMapper.insert(requisitionGroup);

    SupervisoryNode actualSupervisoryNode = supervisoryNodeMapper.getFor(requisitionGroup.getCode());

    assertThat(actualSupervisoryNode, is(supervisoryNode));
  }
}
