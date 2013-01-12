package org.openlmis.web.controller;

import org.openlmis.web.logger.ApplicationLogger;
import org.openlmis.web.response.OpenLmisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

public class BaseController {
  private static Logger logger = LoggerFactory.getLogger(ApplicationLogger.class);
  public static final String UNEXPECTED_EXCEPTION = "unexpected.exception";

  protected String loggedInUser(HttpServletRequest request) {
    return (String) request.getSession().getAttribute(USER);
  }

  protected Integer loggedInUserId(HttpServletRequest request) {
    return (Integer) request.getSession().getAttribute(USER_ID);
  }

  protected String homePageUrl() {
    return "redirect:/public/pages/index.html";
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<OpenLmisResponse> handleException(Exception ex) {
    logger.error("something broke with following exception... ",ex);
    return OpenLmisResponse.error(UNEXPECTED_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
