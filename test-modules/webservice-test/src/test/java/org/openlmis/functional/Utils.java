/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.UiUtils.TestCaseHelper;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static java.lang.System.getProperty;


public class Utils extends TestCaseHelper {

  public String submitReport() throws Exception {
    baseUrlGlobal = getProperty("baseurl", DEFAULT_BASE_URL);
    dburlGlobal = getProperty("dburl", DEFAULT_DB_URL);
    dbWrapper = new DBWrapper(baseUrlGlobal, dburlGlobal);

    HttpClient client = new HttpClient();
    client.createContext();

    String json = readJSON("FullJson.txt");
    json = updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
    json = updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
    json = updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));

    ResponseEntity responseEntity = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST",
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

    return responseEntity.getResponse();
  }

  public String readJSON(String fileName) throws IOException {
    String json = "";
    String classPathFile = this.getClass().getClassLoader().getResource(fileName).getFile();
    File file = new File(classPathFile);
    Scanner scanner = new Scanner(file);
    while (scanner.hasNextLine()) {
      json = json + scanner.nextLine();
    }
    scanner.close();
    return json;
  }

  public String updateJSON(String json, String field, String value) throws IOException {
    String updateValue = "\"" + field + "\" : \"" + value + "\"";
    String originalValue = "\"" + field + "\" : \"\"";

    System.out.println(json.contains(originalValue));
    json = json.replace(originalValue, updateValue);

    return json;
  }

  public String getRequisitionIdFromResponse(String response) {
    return response.substring(response.lastIndexOf(":") + 1, response.lastIndexOf("}"));
  }

}

