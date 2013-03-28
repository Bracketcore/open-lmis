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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.UserMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.UserBuilder.*;
import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;
import static org.openlmis.core.repository.UserRepository.*;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

  @Rule
  public ExpectedException exException = none();

  @Mock
  UserMapper userMapper;

  UserRepository userRepository;

  @Before
  public void setUp() throws Exception {
    userRepository = new UserRepository(userMapper);
  }

  @Test
  public void shouldGetUsersWithRightInNodeForProgram() throws Exception {
    List<User> users = new ArrayList<>();
    SupervisoryNode node = new SupervisoryNode(1);
    Program program = new Program(1);
    when(userMapper.getUsersWithRightInNodeForProgram(program, node, APPROVE_REQUISITION)).thenReturn(users);

    List<User> result = userRepository.getUsersWithRightInNodeForProgram(program, node, APPROVE_REQUISITION);

    verify(userMapper).getUsersWithRightInNodeForProgram(program, node, APPROVE_REQUISITION);
    assertThat(result, is(users));
  }

  @Test
  public void shouldInsertAUser() throws Exception {
    User user = new User();
    userRepository.create(user);
    verify(userMapper).insert(user);
  }

  @Test
  public void shouldThrowExceptionAndNotInsertUserIfSupervisorIdDoesNotExist() throws Exception {
    User user = make(a(defaultUser));
    when(userMapper.get(defaultSupervisorUserName)).thenReturn(null);
    exException.expect(DataException.class);
    exException.expectMessage(SUPERVISOR_USER_NOT_FOUND);
    userRepository.create(user);
  }

  @Test
  public void shouldThrowExceptionAndNotInsertUserOnDuplicateEmployeeId() throws Exception {
    User user = make(a(defaultUser));
    when(userMapper.get(user.getSupervisor().getUserName())).thenReturn(mock(User.class));
    doThrow(new DuplicateKeyException("duplicate key value violates unique constraint \"uc_users_employeeId\"")).when(userMapper).insert(user);

    exException.expect(DataException.class);
    exException.expectMessage(DUPLICATE_EMPLOYEE_ID_FOUND);
    userMapper.get(user.getSupervisor().getUserName());
    userRepository.create(user);
  }

  @Test
  public void shouldThrowExceptionAndNotInsertUserOnDuplicateEmail() throws Exception {
    User user = make(a(defaultUser));
    when(userMapper.get(user.getSupervisor().getUserName())).thenReturn(mock(User.class));
    doThrow(new DuplicateKeyException("duplicate key value violates unique constraint \"uc_users_email\"")).when(userMapper).insert(user);

    exException.expect(DataException.class);
    exException.expectMessage(DUPLICATE_EMAIL_FOUND);
    userMapper.get(user.getSupervisor().getUserName());
    userRepository.create(user);
  }

  @Test
  public void shouldThrowExceptionAndNotInsertUserOnDuplicateUserName() throws Exception {
    User user = make(a(defaultUser));
    when(userMapper.get(user.getSupervisor().getUserName())).thenReturn(mock(User.class));
    doThrow(new DuplicateKeyException("duplicate key value violates unique constraint \"uc_users_userName\"")).when(userMapper).insert(user);

    exException.expect(DataException.class);
    exException.expectMessage(DUPLICATE_USER_NAME_FOUND);
    userMapper.get(user.getSupervisor().getUserName());
    userRepository.create(user);
  }

  @Test
  public void shouldReturnUserWithValidUsername() {
    String username = "Admin";
    User user = make(a(defaultUser, with(email, "John_Doe@openlmis.com")));
    when(userMapper.get(username)).thenReturn(user);

    User returnedUser = userRepository.getByUsername(username);

    assertThat(returnedUser, is(user));
  }

  @Test
  public void shouldReturnUserIdWithValidUserEmail() throws Exception {

    String email = "abc@openlmis.org";
    User expectedUser = new User();
    when(userMapper.getByEmail(email)).thenReturn(expectedUser);

    User returnedUser = userRepository.getByEmail(email);

    assertThat(expectedUser, is(returnedUser));

  }

  @Test
  public void shouldReturnUserIfUserExistsWithSearchCriteria() throws Exception {
    String userSearchParam = "abc";
    User user = new User();
    List<User> listOfUsers = new ArrayList<User>();
    listOfUsers.add(user);

    when(userMapper.getUserWithSearchedName(userSearchParam)).thenReturn(listOfUsers);

    List<User> listOfUsersReturned = userRepository.searchUser(userSearchParam);

    assertTrue(listOfUsersReturned.contains(user));
  }

  @Test
  public void shouldReturnMessageIfNoUserExistsWithTheSearchCriteria() throws Exception {
    String userSearchParam = "xyz";
    when(userMapper.getUserWithSearchedName(userSearchParam)).thenReturn(null);

    List<User> userList = userRepository.searchUser(userSearchParam);

    assertThat(userList, is(nullValue()));
  }

  @Test
  public void shouldUpdateUserIfUserDataIsValid() throws Exception {
    User user = new User();
    user.setId(1);
    userRepository.update(user);
    verify(userMapper).update(user);
  }

  @Test
  public void shouldReturnUserIfIdIsValid() throws Exception {
    User user = new User();

    when(userMapper.getById(1)).thenReturn(user);

    User userReturned = userRepository.getById(1);

    assertThat(userReturned, is(user));
  }

  @Test
  public void shouldReturnUserIdForPasswordResetTokens() throws Exception {
    String passwordResetToken = "test";
    when(userMapper.getUserIdForPasswordResetToken(passwordResetToken)).thenReturn(1);

    assertThat(userRepository.getUserIdForPasswordResetToken(passwordResetToken), is(1));
  }

  @Test
  public void shouldThrowErrorIfUserWithSameTimeStampExist() throws Exception {
    User user = make(a(defaultUser));
    user.setUserName("userBeingUploaded");
    Date todayDate = new Date();
    user.setModifiedDate(todayDate);

    User savedUser = new User();
    savedUser.setModifiedDate(todayDate);

    User supervisorUser = new User();

    when(userMapper.get("userBeingUploaded")).thenReturn(savedUser);
    when(userMapper.get("supervisorUserName")).thenReturn(supervisorUser);

    exException.expect(DataException.class);
    exException.expectMessage("duplicate.user.name.found");

    userRepository.create(user);
  }

  @Test
  public void shouldUpdateUserIfUserWithUserNameAlreadyExist() throws Exception {
    User user = make(a(defaultUser));
    user.setUserName("userBeingUploaded");
    Calendar today = Calendar.getInstance();
    user.setModifiedDate(today.getTime());

    User savedUser = new User();
    today.add(Calendar.DATE,-1);
    savedUser.setModifiedDate(today.getTime());

    User supervisorUser = new User();

    when(userMapper.get("userBeingUploaded")).thenReturn(savedUser);
    when(userMapper.get("supervisorUserName")).thenReturn(supervisorUser);

    userRepository.create(user);

    verify(userMapper).update(user);
  }
}
