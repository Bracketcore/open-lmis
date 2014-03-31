/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.service.UserService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.web.response.OpenLmisResponse.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller handles endpoints to related to user management like resetting password, disabling a user, etc.
 */

@Controller
@NoArgsConstructor
public class UserController extends BaseController {

  static final String MSG_USER_DISABLE_SUCCESS = "msg.user.disable.success";
  static final String USER_CREATED_SUCCESS_MSG = "message.user.created.success.email.sent";

  @Autowired
  private RoleRightsService roleRightService;

  @Autowired
  private UserService userService;

  @Autowired
  private SessionRegistry sessionRegistry;

  @Value("${mail.base.url}")
  private String baseUrl;

  public static final String TOKEN_VALID = "TOKEN_VALID";

  private static final String RESET_PASSWORD_PATH = "/public/pages/reset-password.html#/token/";

  @RequestMapping(value = "/user-context", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> user(HttpServletRequest httpServletRequest) {
    Long userId = (Long) httpServletRequest.getSession().getAttribute(USER_ID);
    if (userId != null) {
      String userName = (String) httpServletRequest.getSession().getAttribute(USER);

      OpenLmisResponse openLmisResponse = new OpenLmisResponse("name", userName);
      openLmisResponse.addData("authenticated", TRUE);
      openLmisResponse.addData("rights", roleRightService.getRights(userId));
      return openLmisResponse.response(OK);
    } else {
      return authenticationError();
    }
  }

  @RequestMapping(value = "/authentication-error", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> authenticationError() {
    return error(messageService.message("user.login.error"), UNAUTHORIZED);
  }

  @RequestMapping(value = "/forgot-password", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> sendPasswordTokenEmail(@RequestBody User user) {
    try {
      String resetPasswordLink = baseUrl + RESET_PASSWORD_PATH;
      userService.sendForgotPasswordEmail(user, resetPasswordLink);
      return success(messageService.message("email.sent"));
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/users", method = POST, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> create(@RequestBody User user, HttpServletRequest request) {
    user.setCreatedBy(loggedInUserId(request));
    user.setModifiedBy(loggedInUserId(request));
    try {
      String resetPasswordBaseLink = baseUrl + RESET_PASSWORD_PATH;
      userService.create(user, resetPasswordBaseLink);
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
    ResponseEntity<OpenLmisResponse> success = success(USER_CREATED_SUCCESS_MSG);
    success.getBody().addData("user", user);
    return success;
  }

  @RequestMapping(value = "/users/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody User user,
                                                 @PathVariable("id") Long id,
                                                 HttpServletRequest request) {
    user.setModifiedBy(loggedInUserId(request));
    user.setId(id);
    try {
      userService.update(user);
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
    return new OpenLmisResponse().response(OK);
  }

  @RequestMapping(value = "/users", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public List<User> searchUser(@RequestParam(required = true) String param) {
    return userService.searchUser(param);
  }

  @RequestMapping(value = "/users/{id}", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public User get(@PathVariable(value = "id") Long id) {
    return userService.getById(id);
  }

  @RequestMapping(value = "/users/{id}", method = DELETE, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> disable(@PathVariable("id") Long id,
                                                  HttpServletRequest request) {
    userService.disable(id, loggedInUserId(request));
    deactivateUserSessions(id);

    return success(MSG_USER_DISABLE_SUCCESS);
  }

  private void deactivateUserSessions(Long id) {
    List<Object> principals = sessionRegistry.getAllPrincipals();
    List<SessionInformation> disabledUserSessions = new ArrayList<>();

    if (principals.contains(id)) {
      Object disabledUserPrincipal = getDisabledUserPrincipal(principals, id);
      disabledUserSessions = sessionRegistry.getAllSessions(disabledUserPrincipal, false);
    }

    for (SessionInformation disabledUserSession : disabledUserSessions) {
      sessionRegistry.getSessionInformation(disabledUserSession.getSessionId()).expireNow();
    }
  }

  private Object getDisabledUserPrincipal(List<Object> principals, Long id) {
    Object disabledUserPrincipal = null;
    for (Object principal : principals) {
      if (principal.equals(id)) {
        disabledUserPrincipal = principal;
      }
    }
    return disabledUserPrincipal;
  }

  @RequestMapping(value = "/user/validatePasswordResetToken/{token}", method = GET)
  public ResponseEntity<OpenLmisResponse> validatePasswordResetToken(@PathVariable(value = "token") String token) throws IOException, ServletException {
    try {
      userService.getUserIdByPasswordResetToken(token);
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
    return response(TOKEN_VALID, true);
  }

  @RequestMapping(value = "/user/resetPassword/{token}", method = PUT)
  public ResponseEntity<OpenLmisResponse> resetPassword(@PathVariable(value = "token") String token, @RequestBody String password) {
    try {
      userService.updateUserPassword(token, password);
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
    return success(messageService.message("password.reset"));
  }

  @RequestMapping(value = "/admin/resetPassword/{userId}", method = PUT)
  public ResponseEntity<OpenLmisResponse> updateUserPassword(@PathVariable(value = "userId") Long userId, @RequestBody String password) {
    try {
      userService.updateUserPassword(userId, password);
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
    return success(messageService.message("password.reset.success"));
  }


  @RequestMapping(value = "/user/preferences", method = GET)
  public ResponseEntity<OpenLmisResponse> getUserPreferences(HttpServletRequest request){
    return response("preferences", userService.getPreferences(this.loggedInUserId(request)));
  }

}
