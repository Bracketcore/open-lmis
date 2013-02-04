package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.User;
import org.openlmis.core.domain.UserRoleAssignment;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.exception.EmailException;
import org.openlmis.email.service.EmailService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;
import static org.openlmis.core.service.UserService.PASSWORD_RESET_TOKEN_INVALID;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  public static final String FORGET_PASSWORD_LINK = "http://openLMIS.org";
  @Rule
  public ExpectedException expectedException = none();

  @Mock
  @SuppressWarnings("unused")
  private UserRepository userRepository;

  @Mock
  @SuppressWarnings("unused")
  private EmailService emailService;

  @Mock
  @SuppressWarnings("unused")
  private RoleAssignmentService roleAssignmentService;

  private UserService userService;


  @Before
  public void setUp() throws Exception {
    userService = new UserService(userRepository, roleAssignmentService, emailService);
  }

  @Test
  public void shouldValidateUserBeforeInsert() throws Exception {
    User user = mock(User.class);
    doThrow(new DataException("user.email.invalid")).when(user).validate();
    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.email.invalid");
    userService.create(user, "http://openLMIS.org");
    verify(userRepository, never()).create(user);
  }

  @Test
  public void shouldGiveErrorIfUserDoesNotExist() throws Exception {
    User user = new User();
    String email = "some email";
    user.setEmail(email);
    when(userRepository.getByEmail(email)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(UserService.USER_EMAIL_INCORRECT);

    userService.sendForgotPasswordEmail(user, FORGET_PASSWORD_LINK);

  }

  @Test
  public void shouldSendForgotPasswordEmailIfUserEmailExists() throws Exception {
    User user = new User();
    user.setEmail("shibhama@thoughtworks.com");

    User userToBeReturned = new User();
    userToBeReturned.setUserName("Admin");
    userToBeReturned.setEmail("shibhama@thoughtworks.com");
    userToBeReturned.setId(1111);
    when(userRepository.getByEmail(user.getEmail())).thenReturn(userToBeReturned);

    userService.sendForgotPasswordEmail(user, FORGET_PASSWORD_LINK);

    verify(emailService).send(any(EmailMessage.class));
    verify(userRepository).getByEmail(user.getEmail());
    verify(userRepository).insertPasswordResetToken(eq(userToBeReturned), anyString());
  }

  @Test
  public void shouldGiveErrorIfUserEmailDoesNotExist() throws Exception {
    User userWithoutEmail = new User();
    User user = new User();
    user.setEmail("some email");
    when(userRepository.getByEmail(user.getEmail())).thenReturn(userWithoutEmail);
    doThrow(new EmailException("")).when(emailService).send(any(EmailMessage.class));

    expectedException.expect(DataException.class);
    expectedException.expectMessage(UserService.USER_EMAIL_NOT_FOUND);

    userService.sendForgotPasswordEmail(user, FORGET_PASSWORD_LINK);
  }

  @Test
  public void shouldReturnSearchResultsWhenUserExists() throws Exception {
    User user = new User();
    String userSearchParam = "abc";
    List<User> listOfUsers = Arrays.asList(new User());

    when(userRepository.searchUser(userSearchParam)).thenReturn(listOfUsers);

    List<User> listOfReturnedUsers = userService.searchUser(userSearchParam);

    assertTrue(listOfReturnedUsers.contains(user));
  }

  @Test
  public void shouldReturnUserIfIdExists() throws Exception {
    User user = new User();
    List<UserRoleAssignment> userRoleAssignments = Arrays.asList(new UserRoleAssignment());

    when(userRepository.getById(1)).thenReturn(user);
    when(roleAssignmentService.getRoleAssignments(1)).thenReturn(userRoleAssignments);

    User returnedUser = userService.getById(1);

    assertThat(returnedUser, is(user));
    assertThat(returnedUser.getRoleAssignments(), is(userRoleAssignments));
  }

  @Test
  public void shouldSendPasswordEmailWhenUserCreated() throws Exception {
    User user = new User();

    userService.create(user, FORGET_PASSWORD_LINK);

    verify(emailService).send(any(EmailMessage.class));
  }

  @Test
  public void shouldSaveUserWithProgramRoleMapping() throws Exception {
    User user = new User();
    UserRoleAssignment userRoleAssignment = new UserRoleAssignment(1, Arrays.asList(2, 3));
    List<UserRoleAssignment> userRoleAssignments = new ArrayList<>();
    userRoleAssignments.add(userRoleAssignment);
    user.setRoleAssignments(userRoleAssignments);

    userService.create(user, FORGET_PASSWORD_LINK);

    verify(userRepository).create(user);
    verify(roleAssignmentService).insertUserProgramRoleMapping(user);

    user.setId(1);
    userService.create(user, FORGET_PASSWORD_LINK);
  }

  @Test
  public void shouldThrowErrorIfPasswordResetTokenIsInValidWhileGettingUserId() throws Exception {

    String invalidToken = "invalidToken";
    when(userRepository.getUserIdForPasswordResetToken(invalidToken)).thenReturn(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(PASSWORD_RESET_TOKEN_INVALID);

    userService.getUserIdByPasswordResetToken(invalidToken);
  }

  @Test
  public void shouldReturnUserIdIfPasswordResetTokenIsValid() throws Exception {
    String validToken = "validToken";
    Integer expectedUserId = 1;
    when(userRepository.getUserIdForPasswordResetToken(validToken)).thenReturn(expectedUserId);
    Integer userId = userService.getUserIdByPasswordResetToken(validToken);

    verify(userRepository).getUserIdForPasswordResetToken(validToken);
    assertThat(userId, is(expectedUserId));
  }
}
