/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.authentication;

import org.openlmis.core.domain.Vendor;
import org.openlmis.core.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


public class RestApiAuthenticationProvider implements AuthenticationProvider {

  private VendorService vendorService;


  @Autowired
  public RestApiAuthenticationProvider(VendorService vendorService) {
    this.vendorService = vendorService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Vendor vendor = new Vendor();

    vendor.setName((String) authentication.getPrincipal());
    vendor.setAuthToken((String) authentication.getCredentials());

    if (!vendor.isValid()) return null;

    if (vendorService.authenticate(vendor)) return authentication;

    return null;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }
}
