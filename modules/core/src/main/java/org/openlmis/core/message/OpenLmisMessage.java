/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.message;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Data
public class OpenLmisMessage {

  private String code;
  private String[] params = new String[0];

  public OpenLmisMessage(String code) {
    this.code = code;
  }

  public OpenLmisMessage(String code, String... params) {
    this.code = code;
    this.params = params;
  }

  public String resolve(ResourceBundle bundle) {
    String message = getDisplayText(code, bundle);
    List<String> paramList = resolveParams(bundle);
    return String.format(message, paramList.toArray());
  }

  @Override
  public String toString(){
    if(params.length == 0) return code;

    StringBuilder messageBuilder = new StringBuilder("code: "+code+ ", params: { ");
    for(String param : params){
      messageBuilder.append("; ").append(param);
    }
    messageBuilder.append(" }");
    return messageBuilder.toString().replaceFirst("; ","");
  }

  private List<String> resolveParams(ResourceBundle bundle) {
    List<String> paramList = new ArrayList<>();
    for (String code : params) {
      paramList.add(getDisplayText(code, bundle));
    }
    return paramList;
  }

  private String getDisplayText(String code, ResourceBundle bundle) {
    try {
      return bundle.getString(code);
    } catch (MissingResourceException e) {
      return code;
    }
  }
}
