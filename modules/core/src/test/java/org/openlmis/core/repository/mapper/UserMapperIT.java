/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.utils.mapper.TestVendorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.lang.Boolean.FALSE;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.UserBuilder.*;
import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class UserMapperIT {

  @Autowired
  UserMapper userMapper;
  @Autowired
  private ProgramMapper programMapper;
  @Autowired
  RoleAssignmentMapper roleAssignmentMapper;
  @Autowired
  private RoleRightsMapper roleRightsMapper;
  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private SupervisoryNodeMapper supervisoryNodeMapper;
  @Autowired
  TestVendorMapper vendorMapper;

  private Facility facility;
  private SupervisoryNode supervisoryNode;

  @Before
  public void setUp() throws Exception {
    supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));

    facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    supervisoryNode.setFacility(facility);

    supervisoryNodeMapper.insert(supervisoryNode);
  }

  @Test
  public void shouldGetUserByUserNameAndPassword() throws Exception {
    User someUser = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(someUser);
    userMapper.updateUserPassword(someUser.getId(), "random");

    User user = userMapper.selectUserByUserNameAndPassword(defaultUserName, "random");
    assertThat(user, is(notNullValue()));
    assertThat(user.getUserName(), is(defaultUserName));
    assertThat(user.getId(), is(someUser.getId()));
    User user1 = userMapper.selectUserByUserNameAndPassword(defaultUserName, "wrongPassword");
    assertThat(user1, is(nullValue()));
    User user2 = userMapper.selectUserByUserNameAndPassword("wrongUserName", defaultPassword);
    assertThat(user2, is(nullValue()));
  }

  @Test
  public void shouldInsertUserWithDbDefaultDateWhenSuppliedModifiedDateNull() throws Exception {
    User someUser = make(a(defaultUser, with(facilityId, facility.getId())));
    someUser.setModifiedDate(null);

    userMapper.insert(someUser);

    User fetchedUser = userMapper.getByUsernameAndVendorId(someUser);

    assertThat(fetchedUser, is(notNullValue()));
    assertThat(fetchedUser.getId(), is(someUser.getId()));
    assertThat(fetchedUser.getModifiedDate(), is(notNullValue()));
  }

  @Test
  public void shouldInsertUserWithSuppliedModifiedDate() throws Exception {
    User someUser = make(a(defaultUser, with(facilityId, facility.getId())));
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, Calendar.JANUARY);
    someUser.setModifiedDate(calendar.getTime());

    userMapper.insert(someUser);
    User fetchedUser = userMapper.getByUsernameAndVendorId(someUser);

    assertThat(fetchedUser, is(notNullValue()));
    assertThat(fetchedUser.getId(), is(someUser.getId()));
    assertThat(fetchedUser.getModifiedDate(), is(calendar.getTime()));
  }

  @Test
  public void shouldGetUsersWithGivenRightInNodeForProgram() {
    String nullString = null;
    User someUser = make(a(defaultUser, with(facilityId, facility.getId()), with(supervisorUserName, nullString)));
    userMapper.insert(someUser);

    Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));

    Role role = insertRole();
    roleRightsMapper.createRoleRight(role.getId(), APPROVE_REQUISITION);

    roleAssignmentMapper.insertRoleAssignment(someUser.getId(), program.getId(), supervisoryNode.getId(), role.getId());

    final List<User> users = userMapper.getUsersWithRightInNodeForProgram(program, supervisoryNode, Right.APPROVE_REQUISITION);
    someUser.setPassword(null);
    someUser.getSupervisor().setModifiedDate(null);
    someUser.setModifiedDate(null);
    someUser.setVendorId(null);
    assertThat(users, hasItem(someUser));
  }

  @Test
  public void shouldInsertAUser() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    Integer userCount = userMapper.insert(user);
    assertThat(userCount, is(1));
    assertThat(user.getId(), is(notNullValue()));
  }

  @Test
  public void shouldGetUserWithUserNameAndVendorIdWhenVendorIdIsNotNull() throws Exception {
    String nullString = null;
    Vendor vendor = new Vendor();
    vendor.setName("newVendor");
    vendorMapper.insert(vendor);

    User user = make(a(defaultUser, with(facilityId, facility.getId()), with(supervisorUserName, nullString), with(vendorId, vendor.getId())));
    user.setModifiedDate(Calendar.getInstance().getTime());
    userMapper.insert(user);

    User result = userMapper.getByUsernameAndVendorId(user);
    user.setPassword(null);
    assertThat(result, is(user));
  }

  @Test
  public void shouldGetUserWithUserNameAndVendorIdWhenVendorIdIsNull() throws Exception {
    String nullString = null;

    User user = make(a(defaultUser, with(facilityId, facility.getId()), with(supervisorUserName, nullString)));
    user.setModifiedDate(Calendar.getInstance().getTime());
    userMapper.insert(user);

    User result = userMapper.getByUsernameAndVendorId(user);
    user.setPassword(null);
    assertThat(result, is(user));
  }

  @Test
  public void shouldGetUsersWithValidSearchedFirstNameValue() throws Exception {
    String userSearchValue = "Sha";
    User user = make(a(defaultUser, with(firstName, "Shan"), with(lastName, "Frank"), with(userName, "JFrank"), with(facilityId, facility.getId())));
    userMapper.insert(user);
    List<User> listOfUsers = userMapper.getUserWithSearchedName(userSearchValue);

    User userResult = listOfUsers.get(0);

    assertThat(userResult.getFirstName(), is(user.getFirstName()));
    assertThat(listOfUsers.size(), is(1));
  }

  @Test
  public void shouldGetUsersWithValidSearchedLastNameValue() throws Exception {
    String userSearchValue = "Frank";
    User user = make(a(defaultUser, with(firstName, "Tom"), with(lastName, "franks"), with(userName, "JFrancis"), with(facilityId, facility.getId())));

    userMapper.insert(user);

    List<User> listOfUsers = userMapper.getUserWithSearchedName(userSearchValue);
    User userResult = listOfUsers.get(0);

    assertThat(userResult.getLastName(), is(user.getLastName()));
    assertThat(listOfUsers.size(), is(1));
  }

  @Test
  public void shouldNotGetUsersWithInvalidSearchedNameValue() throws Exception {
    String userSearchValue = "Tom";
    User user = make(a(defaultUser, with(firstName, "Harry"), with(lastName, "Bett"), with(userName, "HBett"), with(facilityId, facility.getId())));

    userMapper.insert(user);

    List<User> listOfUsers = userMapper.getUserWithSearchedName(userSearchValue);

    assertFalse(listOfUsers.contains(user));
    assertThat(listOfUsers.size(), is(0));
  }

  @Test
  public void shouldGetUsersWithValidSearchedEmail() throws Exception {
    String userSearchValue = "abc@";
    User user = make(a(defaultUser, with(email, "abc@gmail.com"), with(facilityId, facility.getId())));

    userMapper.insert(user);

    List<User> listOfUsers = userMapper.getUserWithSearchedName(userSearchValue);
    User userResult = listOfUsers.get(0);

    assertThat(userResult.getEmail(), is(user.getEmail()));
    assertThat(listOfUsers.size(), is(1));
  }

  @Test
  public void shouldUpdateUserIfUserExist() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);

    user.setUserName("New Name");

    userMapper.update(user);

    User fetchedUser = userMapper.getByUsernameAndVendorId(user);

    assertThat(fetchedUser.getFirstName(), is(user.getFirstName()));
    assertThat(fetchedUser.getLastName(), is(user.getLastName()));
  }

  @Test
  public void shouldGetUserIfUserIdExists() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));

    userMapper.insert(user);

    User returnedUser = userMapper.getById(user.getId());

    assertThat(user.getUserName(), is(returnedUser.getUserName()));
  }


  @Test
  public void shouldInsertPasswordResetToken() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);
    String passwordResetToken = "passwordResetToken";
    userMapper.insertPasswordResetToken(user, passwordResetToken);

    assertThat(userMapper.getUserIdForPasswordResetToken(passwordResetToken), is(user.getId()));
  }

  @Test
  public void shouldDeletePasswordResetToken() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);
    String passwordResetToken = "passwordResetToken";
    userMapper.insertPasswordResetToken(user, passwordResetToken);

    userMapper.deletePasswordResetTokenForUser(user.getId());

    assertThat(userMapper.getUserIdForPasswordResetToken(passwordResetToken), is(nullValue()));
  }

  @Test
  public void shouldGetUserIdForPasswordResetToken() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);
    String passwordResetToken = "passwordResetToken";
    userMapper.insertPasswordResetToken(user, passwordResetToken);
    assertThat(userMapper.getUserIdForPasswordResetToken(passwordResetToken), is(user.getId()));
  }

  @Test
  public void shouldUpdateUserPassword() throws Exception {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);
    String newPassword = "newPassword";
    userMapper.updateUserPassword(user.getId(), newPassword);
    User returnedUser = userMapper.selectUserByUserNameAndPassword(user.getUserName(), newPassword);
    assertThat(returnedUser, is(notNullValue()));
  }

  @Test
  public void shouldSelectOnlyUsersWithExternalSystemNullWhileAuthentication() throws Exception {
    Vendor commTrac = new Vendor("commTrac", true);
    vendorMapper.insert(commTrac);

    User externalUser = make(a(defaultUser, with(facilityId, facility.getId()), with(vendorId, commTrac.getId())));

    userMapper.insert(externalUser);
    userMapper.updateUserPassword(externalUser.getId(), "random");

    User user = userMapper.selectUserByUserNameAndPassword(defaultUserName, "random");
    assertThat(user, is(nullValue()));
  }

  private Program insertProgram(Program program) {
    programMapper.insert(program);
    return program;
  }

  private Role insertRole() {
    Role r1 = new Role("r1", FALSE, "random description");
    roleRightsMapper.insertRole(r1);
    return r1;
  }

}
