/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.report.controller;

import org.openlmis.core.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;


public class BaseController {

  private static Logger logger = LoggerFactory.getLogger(BaseController.class);

  public static final String ACCEPT_JSON = "Accept=application/json";
  public static final String FORBIDDEN_EXCEPTION = "forbidden.exception";
  public static final String UNEXPECTED_EXCEPTION = "unexpected.exception";


  @Autowired
  MessageService messageService;

  Long loggedInUserId(HttpServletRequest request) {
    return (Long) request.getSession().getAttribute("USER_ID");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(Exception ex) {
    logger.error("something broke with following exception... ", ex);
    if (ex instanceof AccessDeniedException) {
        //response(ERROR, errorMessage, statusCode, contentType);
      return null;//error(messageService.message(FORBIDDEN_EXCEPTION), HttpStatus.FORBIDDEN);
    }
    return null;//error(messageService.message(UNEXPECTED_EXCEPTION), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}