/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;
import static org.joda.time.DateTime.now;

public class ProgramSupportedBuilder {
  public static Property<ProgramSupported, String> supportedFacilityCode = newProperty();
  public static Property<ProgramSupported, Long> supportedFacilityId = newProperty();
  public static Property<ProgramSupported, Program> supportedProgram = newProperty();
  public static Property<ProgramSupported, Boolean> isActive = newProperty();
  public static Property<ProgramSupported, Date> startDate = newProperty();

  public static final Long FACILITY_ID = 101L;
  public static final String FACILITY_CODE = "F_CD";
  public static final Long PROGRAM_ID = 101L;
  public static final String PROGRAM_CODE = "P_CD";
  private static final String PROGRAM_NAME = "P_NAME";
  public static final Boolean IS_ACTIVE = true;
  public static final Date START_DATE = now().minusYears(5).toDate();

  public static final Instantiator<ProgramSupported> defaultProgramSupported = new Instantiator<ProgramSupported>() {
    @Override
    public ProgramSupported instantiate(PropertyLookup<ProgramSupported> lookup) {
      ProgramSupported programSupported = new ProgramSupported();
      programSupported.setFacilityCode(lookup.valueOf(supportedFacilityCode, FACILITY_CODE));
      programSupported.setFacilityId(lookup.valueOf(supportedFacilityId, FACILITY_ID));
      programSupported.setProgram(lookup.valueOf(supportedProgram, new Program(PROGRAM_ID, PROGRAM_CODE, PROGRAM_NAME, null, true, false)));
      programSupported.setStartDate(lookup.valueOf(startDate, START_DATE));
      programSupported.setActive(lookup.valueOf(isActive, IS_ACTIVE));
      programSupported.setModifiedBy(1L);
      programSupported.setModifiedDate(now().toDate());
      return programSupported;
    }
  };
}
