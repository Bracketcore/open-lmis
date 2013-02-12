package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.hash.Encoder;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.service.UserService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.web.controller.UserController.TOKEN_VALID;

public class UserControllerTest {

  public static final Integer userId = 1;

  private MockHttpSession session;

  private MockHttpServletRequest httpServletRequest;

  private UserController userController;

  @Mock
  @SuppressWarnings("unused")
  private RoleRightsService roleRightService;

  @Mock
  @SuppressWarnings("unused")
  private UserService userService;

  @Before
  public void setUp() {
    initMocks(this);
    httpServletRequest = new MockHttpServletRequest();
    session = new MockHttpSession();
    httpServletRequest.setSession(session);

    userController = new UserController(roleRightService, userService);
  }

  @Test
  public void shouldReturnUserInfoOfLoggedInUser() {
    String username = "Foo";
    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);
    HashMap<String, Object> params = userController.user(httpServletRequest, null);
    assertThat(params.get("name").toString(), is("Foo"));
    assertThat((Boolean) params.get("authenticated"), is(true));
  }


  @Test
  public void shouldNotReturnUserInfoWhenNotLoggedIn() {
    session.setAttribute(UserAuthenticationSuccessHandler.USER, null);
    HashMap<String, Object> params = userController.user(httpServletRequest, "true");
    assertThat(params.get("error").toString(), is("true"));
    assertThat((Boolean) params.get("authenticated"), is(false));
  }

  @Test
  public void shouldGetAllPrivilegesForTheLoggedInUser() throws Exception {
    String username = "Foo";
    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);
    Set<Right> rights = new HashSet<>();
    when(roleRightService.getRights(username)).thenReturn(rights);
    HashMap<String, Object> params = userController.user(httpServletRequest, "true");
    verify(roleRightService).getRights(username);
    assertThat((Set<Right>) params.get("rights"), is(rights));
  }

  @Test
  public void shouldEmailPasswordTokenForUser() throws Exception {
    User user = new User();
    user.setUserName("Manan");
    user.setEmail("manan@thoughtworks.com");
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("referer")).thenReturn("http://referrer/");
    userController.sendPasswordTokenEmail(user, request);
    verify(userService).sendForgotPasswordEmail(eq(user), anyString());
  }

  @Test
  public void shouldReturnErrorIfSendingForgotPasswordEmailFails() throws Exception {
    User user = new User();
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("referer")).thenReturn("http://referrer/");
    doThrow(new DataException("some error")).when(userService).sendForgotPasswordEmail(eq(user), anyString());

    ResponseEntity<OpenLmisResponse> response = userController.sendPasswordTokenEmail(user, request);

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some error"));
  }

  @Test
  public void shouldSaveUser() throws Exception {
    User user = new User();

    httpServletRequest.getSession().setAttribute(USER_ID, userId);
    httpServletRequest.getSession().setAttribute(USER, USER);
    ResponseEntity<OpenLmisResponse> response = userController.create(user, httpServletRequest);

    verify(userService).create(eq(user), anyString());

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("User " + user.getFirstName() + " " + user.getLastName() + " has been successfully created, password link has been sent on registered Email address"));
    assertThat(user.getPassword(), is(Encoder.hash("openLmis123")));
    assertThat(user.getModifiedBy(), is(USER));
  }

  @Test
  public void shouldUpdateUser() throws Exception {
    User user = new User();
    user.setId(1);
    user.setPassword("password");
    httpServletRequest.getSession().setAttribute(USER_ID, userId);
    httpServletRequest.getSession().setAttribute(USER, USER);

    ResponseEntity<OpenLmisResponse> response = userController.update(user, 1, httpServletRequest);

    verify(userService).update(user);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("User " + user.getFirstName() + " " + user.getLastName() + " has been successfully updated"));
    assertThat(user.getPassword(), is(Encoder.hash("password")));
    assertThat(user.getModifiedBy(), is(USER));
  }

  @Test
  public void shouldReturnErrorIfSaveUserFails() throws Exception {
    User user = new User();
    doThrow(new DataException("Save user failed")).when(userService).create(eq(user), anyString());

    ResponseEntity<OpenLmisResponse> response = userController.create(user, httpServletRequest);

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("Save user failed"));
  }

  @Test
  public void shouldReturnUserDetailsIfUserExists() throws Exception {
    String userSearchParam = "Admin";
    List<User> listOfUsers = Arrays.asList(new User());
    User userReturned = new User();

    when(userService.searchUser(userSearchParam)).thenReturn(listOfUsers);

    List<User> userList = userController.searchUser(userSearchParam);

    assertTrue(userList.contains(userReturned));
  }

  @Test
  public void shouldReturnUserIfIdExists() throws Exception {
    User user = new User();
    when(userService.getById(1)).thenReturn(user);

    User returnedUser = userController.get(1);

    assertThat(returnedUser, is(user));

  }

  @Test
  public void shouldReturnErrorResponseIfTokenIsNotValid() throws IOException, ServletException {
    String invalidToken = "invalidToken";
    String errorMessage = "some error";
    doThrow(new DataException(errorMessage)).when(userService).getUserIdByPasswordResetToken(invalidToken);
    ResponseEntity<OpenLmisResponse> response = userController.validatePasswordResetToken(invalidToken);

    verify(userService).getUserIdByPasswordResetToken(invalidToken);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldReturnSuccessResponseIfTokenIsValid() throws IOException, ServletException {
    String validToken = "validToken";
    ResponseEntity<OpenLmisResponse> response = userController.validatePasswordResetToken(validToken);

    verify(userService).getUserIdByPasswordResetToken(validToken);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat((Boolean)response.getBody().getData().get(TOKEN_VALID), is(true));
  }

}
